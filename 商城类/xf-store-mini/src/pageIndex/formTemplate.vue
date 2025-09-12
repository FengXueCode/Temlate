<template>
  <div class="main">
    <up-navbar
      title="表单"
      :autoBack="true"
    ></up-navbar>
    <div class="content">
      <div class="ship-info">
        <up-form :model="formData" labelAlign="right" ref="form" :rules="rules">
          <up-form-item label="下拉" required prop="" labelWidth="80" @click="showShip = true">
            <up-input
              placeholder="请选择"
              disabled
              border="none"
              disabledColor="#ffffff"
            ></up-input>
            <template #right>
              <up-icon
                name="arrow-right"
              ></up-icon>
            </template>

            <up-picker
              @cancel="showShip = false"
              confirmColor="#07C160"
              :show="showShip"
              :columns="[shipList]"
              :defaultIndex="[checkShip]"
              keyName="shipName"
              @confirm="sureShip"
            ></up-picker>
          </up-form-item>

          <up-form-item
            label="时间"
            prop="plannedDate"
            labelWidth="80"
            required
            @click="showDate = true"
          >
            <up-input
              placeholder="请选择"
              disabled
              border="none"
              disabledColor="#ffffff"
            ></up-input>
            <template #right>
              <up-icon
                name="arrow-right"
              ></up-icon>
            </template>
            <up-datetime-picker
              :show="showDate"
              :minDate="Date.now()"
              mode="datetime"
              v-model="formData.plannedDate"
              @cancel="showDate = false"
              @confirm="sureDate"
            ></up-datetime-picker>
          </up-form-item>

          <up-form-item 
            required
            label="输入框" prop="proposerName" labelWidth="80">
            <up-input
              placeholder="请输入"
              type="text"
              maxlength="10"
              v-model="formData.proposerName"
            ></up-input>
          </up-form-item>
          <up-form-item
            label="文本域"
            required
            prop="proposerContact"
            labelWidth="80"
          >
            <up-textarea  placeholder="请输入内容" count autoHeight  ></up-textarea>
          </up-form-item>
        <up-form-item
            label="上传"
            prop="photos"
            labelWidth="80"
            required
          >
            <up-upload
              :fileList="fileList"
              @afterRead="afterRead"
              @delete="deletePic"
              name="1"
              multiple
              :maxCount="3"
            ></up-upload>
          </up-form-item>
        </up-form>
      </div>

    </div>

    <div class="bottom-btn">
      <div class="btn-group" v-if="!isEdit">
        <div class="local item" @click="saveLocal">暂存本地</div>
        <div class="submit item" @click="submit">提交申报</div>
      </div>
      <div
        class="btn-full"
        v-if="formData.sailingHarbourAuditState == 2"
        @click="submit"
      >
        <div class="submit item">修改申请</div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
onLoad((val: any) => {

 });

onMounted(() => {
});
//----------------------<zy-资源>----------------------
//----------------------<tysj-通用数据>----------------------
const user = uni.getStorageSync("user");
const form = ref();
const formData = ref({
  cbShipId: "",
  harbourNo: "",
  harbourName: "",
  plannedDate: "",
  proposerName: user.userName,
  proposerContact: "",
});
const rules = reactive({
  // 普通
  cbShipId: [
    {
      type: "string",
      required: true,
      message: "请选择船舶",
      trigger: ["blur", "change"],
    },
  ],
  //自定义
  cbCrews: [
    {
      validator: (rule, value, callback) => {
        return value.length != 0;
      },
      message: "请选择船员",
    },
  ],
});

const initNull = util.initNull;

//----------------------<tjsb-提交申报>----------------------
function submit() {
  uni.showModal({
    title: "确认提交？",
    success(res) {
      if (res.confirm) {
        if (cbCrews.value.length == 0) {
          util.toast("请选择船员");
          return;
        }
        formData.value.sailingHarbourApplyType = inOut.value ? 0 : 1;
        formData.value.sailingHarbourPersonNum = cbCrews.value.length;
        form.value.validate().then((valid) => {
          if (valid) {
            request(
              isEdit.value
                ? "/fisherApi/wechatMiniProgram/editShipEnterOuterHarbour"
                : "/fisherApi/wechatMiniProgram/saveShipEnterOuterHarbour",
              {
                cbShipEnterOuterHarbour: formData.value,
                cbCrews: cbCrews.value,
              },
              "POST",
            )
              .then((res) => {
                uni.removeStorageSync(inOut.value ? "out" : "in");
                util.toast("提交成功");
                util.back();
              })
              .catch((err) => {});
          }
        });
      }
    },
  });
}
//----------------------<zcbd-暂存本地>----------------------
function saveLocal() {
  uni.showModal({
    title: "确认暂存？",
    success(res) {
      if (res.confirm) {
        uni.setStorageSync(
          inOut.value ? "out" : "in",
          JSON.stringify(formData.value),
        );
        util.back();
      }
    },
  });
}
//----------------------<jhsj-计划时间>----------------------
const plannedDate = ref("");
const showDate = ref(false);
function sureDate(e) {
  // plannedDate.value = util.formaDate(e.value);
  formData.value.plannedDate = util.formaDate(e.value);
  showDate.value = false;
}
//----------------------<cb-船舶>----------------------
const showShip = ref(false);
const shipList = ref([]);
const ship = ref({ shipName: "", shipOwner: "", shipOwnerContact: "" });
function getShipList() {
  request("/fisherApi/wechatMiniProgram/findShipByUserId", {}, "POST")
    .then((res) => {
      shipList.value = res;
    })
    .catch((err) => {});
}
const checkShip = ref(0);
function sureShip(e) {
  checkShip.value = e.indexs[0];
  showShip.value = false;
  ship.value = e.value[0];
  formData.value.cbShipId = e.value[0].cbShipId;
  formData.value.shipName = e.value[0].shipName;
}
//----------------------<sc-上传>----------------------
const fileList = ref([]);
const photos = ref([]);
// 删除图片
const deletePic = (event) => {
  fileList.value.splice(event.index, 1);
  photos.value.splice(event.index, 1);
};

// 新增图片
const afterRead = async (event) => {
  // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
  let lists = [].concat(event.file);
  let fileListLen = fileList.value.length;
  lists.map((item) => {
    fileList.value.push({
      ...item,
      status: "uploading",
      message: "上传中",
    });
  });
  for (let i = 0; i < lists.length; i++) {
    const result = await uploadFilePromise("图片路径");
    let item = fileList.value[fileListLen];
    fileList.value.splice(fileListLen, 1, {
      ...item,
      status: "success",
      message: "",
      url: result,
    });
    fileListLen++;
  }
  console.log("fileList", fileList.value);
};
const uploadFilePromise = (url:string) => {
  return new Promise((resolve, reject) => {
    let a = uni.uploadFile({
      url: "接口路径", // 仅为示例，非真实的接口地址
      filePath: url,
      name: "file",
      success: (res) => {
        let data = JSON.parse(res.data);
 
        setTimeout(() => {
          resolve(res.data.data);
        }, 1000);
      },
    });
  });
};
</script>
<style scoped lang="scss">
.main {
  background: $uni-bg-color-grey;

  .content {
    width: 100%;
    height: calc(100vh - 80px - 20px);
    overflow-y: auto;
  }

  .ship-info {
    background: white;
    margin-top: 10px;
    box-sizing: border-box;

    .reject {
      margin-bottom: 10px;

      .text {
        margin-top: 10px;
        padding: 10px;
        background: #f7f7f7;
        color: #f92800;
        box-sizing: border-box;
      }
    }

    .declare-state {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 40px;
      font-size: 16px;

      .title {
        font-weight: bolder;
      }

      .state {
        color: white;
        position: relative;
        height: 30px;
        width: 100px;
        background: #0086fd;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: -16px;
        padding-left: 10px;

        &.reject {
          background: red;
        }
      }

      .state::before {
        content: "";
        position: absolute;
        left: -20px;
        top: 0px;
        width: 30px;
        height: 30px;
        background-color: white;
        transform: rotate(45deg);
        transform-origin: center;
      }
    }

    .user-info {
      background: #f7f7f7;
      padding: 10px;

      .item {
        display: flex;

        .label {
          width: 80px;
          text-align: end;
          box-sizing: border-box;
          padding-right: 15px;
          font-size: 15px;
          color: #303133;
          box-sizing: border-box;
        }

        .text {
          margin-left: 0px;
          color: #333333;
        }
      }

      .item:nth-child(1) {
        margin-bottom: 10px;
      }
    }
  }

  .user-list {
    background: white;
    margin-top: 10px;

    .title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 10px;
      box-sizing: border-box;
      border-bottom: 1px solid $uni-border-color;
      height: 44px;

      .text {
      }

      .btn {
        display: flex;
        align-items: center;
        justify-content: center;
        background: #0086fd;
        color: white;
        width: 108px;
        height: 28px;
        border-radius: 30px;
        font-size: 14px;

        .text {
          margin-left: 10px;
        }
      }
    }

    .list {
      height: calc(100% - 44px);
      box-sizing: border-box;
      padding: 0 10px;
      overflow-y: auto;

      .item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 10px 10px;
        border-bottom: 1px solid $uni-border-color;

        .item-state {
          padding: 5px 10px;
          color: white;
          height: 22;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
          background: $uni-color-primary;

          &.is-owner {
            background: $uni-color-zt-2;
          }
        }

        .item-info {
          flex-grow: 1;
          margin-left: 10px;
        }
      }
    }
  }
}
</style>
