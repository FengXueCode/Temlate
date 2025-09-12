<template>
  <div class="main">
    <up-navbar title="账号申请" :autoBack="true"></up-navbar>

    <div class="content">
      <div class="user-info">
        <div class="explain" v-if="formShipData.shipAuditState==90">
          账号说明
          <div class="text">
            当前账号申请通过，账号：122222222，初始密码：2222222
            注意及时修改密码 保障账号安全
          </div>
        </div>
        <div class="reject" v-if="formShipData.shipAuditState==2">
          驳回意见
          <div class="text">{{formShipData.shipAuditFeedback}}</div>
        </div>

        <div class="title">
          船舶所有人信息
          <div class="state" v-if="formShipData.shipAuditState==90">申请通过</div>
        </div>

        <up-form
          :model="formShipData"
          labelAlign="right"
          ref="formShip"
          :rules="rules"
        >
          <up-form-item label="姓名" prop="shipOwner" labelWidth="80">
            <up-input
              placeholder="请输入姓名"
              type="text"
              maxlength="10"
              border="bottom"
              v-model="formShipData.shipOwner"
            ></up-input>
          </up-form-item>

          <up-form-item
            label="联系方式"
            prop="shipOwnerContact"
            labelWidth="80"
          >
            <up-input
              placeholder="请输入联系方式"
              type="text"
              maxlength="11"
              border="bottom"
              v-model="formShipData.shipOwnerContact"
            ></up-input>
          </up-form-item>
          <up-form-item
            label="身份证号"
            prop="shipOwnershipIdentificationNo"
            labelWidth="80"
          >
            <up-input
              placeholder="请输入身份证号"
              type="idcard"
              maxlength="18"
              border="bottom"
              v-model="formShipData.shipOwnershipIdentificationNo"
            ></up-input>
          </up-form-item>

          <div class="title">船舶信息登记</div>
          <up-form-item label="船舶名称" prop="shipName" labelWidth="80">
            <up-input
              placeholder="请输入船舶名称"
              type="text"
              maxlength="10"
              border="bottom"
              v-model="formShipData.shipName"
            ></up-input>
          </up-form-item>
          <up-form-item label="AIS船名" prop="ais" labelWidth="80">
            <up-input
              placeholder="请输入AIS船名"
              type="text"
              maxlength="10"
              border="bottom"
              v-model="formShipData.ais"
            ></up-input>
          </up-form-item>
          <up-form-item label="船牌" prop="shipPlate" labelWidth="80">
            <up-input
              placeholder="请输入船牌"
              type="text"
              maxlength="10"
              border="bottom"
              v-model="formShipData.shipPlate"
            ></up-input>
          </up-form-item>
          <up-form-item label="船籍港" prop="harbourNo" labelWidth="80">
            <div
              class="select"
              :class="{ 'no-input': formShipData.harbourNo == '' }"
              @click="showHarbour = true"
            >
              <div class="text">
                {{
                  formShipData.harbourNo == "" ? "请选择船籍港" : harbour.desc
                }}
              </div>
              <up-icon name="arrow-right"></up-icon>
            </div>

            <up-picker
              @cancel="showHarbour = false"
              confirmColor="#07C160"
              :show="showHarbour"
              :columns="[harhourList]"
              keyName="desc"
              @confirm="sureHarbour"
            ></up-picker>
          </up-form-item>
          <up-form-item label="船舶类型" prop="shipType" labelWidth="80">
            <div
              class="select"
              :class="{ 'no-input': formShipData.shipType == '' }"
              @click="showShipType = true"
            >
              <div class="text">
                {{
                  formShipData.shipType == ""
                    ? "请选择船舶类型"
                    : shipType.label
                }}
              </div>
              <up-icon name="arrow-right"></up-icon>
            </div>

            <up-picker
              @cancel="showShipType = false"
              confirmColor="#07C160"
              :show="showShipType"
              :columns="[shipTypeList]"
              keyName="label"
              @confirm="sureShipType"
            ></up-picker>
          </up-form-item>
          <up-form-item label="归属行政区" prop="region" labelWidth="80">
            <picker
              class="pciker"
              :value="checkRegion"
              mode="region"
              @change="changeRegion"
            >
              <div
                class="select"
                :class="{ 'no-input': formShipData.region === '' }"
                @click="showRegion = true"
              >
                <div class="text">
                  {{ formShipData.region == "" ? "请选择归属行政区" : region }}
                </div>
                <up-icon name="arrow-right"></up-icon>
              </div>
            </picker>
          </up-form-item>
          <up-form-item label="航线类型" prop="routeType" labelWidth="80">
            <div
              class="select"
              :class="{ 'no-input': formShipData.routeType === '' }"
              @click="showRouteType = true"
            >
              <div class="text">
                {{
                  formShipData.routeType === ""
                    ? "请选择航线类型"
                    : routeType.label
                }}
              </div>
              <up-icon name="arrow-right"></up-icon>
            </div>

            <up-picker
              @cancel="showRouteType = false"
              confirmColor="#07C160"
              :show="showRouteType"
              :columns="[routeTypeList]"
              keyName="label"
              @confirm="sureRouteType"
            ></up-picker>
          </up-form-item>
          <up-form-item label="船舶照片" prop="shipPhotos" labelWidth="80">
            <up-upload
              :fileList="fileList1"
              @afterRead="afterRead"
              @delete="deletePic"
              name="1"
              multiple
              :maxCount="1"
            ></up-upload>
          </up-form-item>
          <up-form-item label="船舶证件照" prop="shipAuditData" labelWidth="80">
            <up-upload
              :fileList="fileList2"
              @afterRead="afterRead1"
              @delete="deletePic1"
              name="1"
              multiple
              :maxCount="1"
            ></up-upload>
          </up-form-item>
        </up-form>
      </div>
    </div>
    <div class="bottom-btn" v-if="formShipData.shipAuditState != 90">
      <div class="btn-full await " v-if="formShipData.shipAuditState==0">
        <div class="item submit" >申请中</div>
      </div>
      <div class="btn-full await " v-if="formShipData.shipAuditState==2" @click="debounce(false)">
        <div class="item" >修改信息</div>
      </div>
      <div class="btn-group" v-if="formShipData.shipAuditState==null">
        <div class="item local" @click="saveLocal">暂存草稿</div>
        <div class="item submit" @click="debounce(true)">发起</div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
onLoad((val: any) => {
  if (uni.getStorageSync("formShipData") != "") {
    formShipData.value = uni.getStorageSync("formShipData");
    shipType.value = formShipData.value.shipType;
    harbour.value = formShipData.value.harbour;
    routeType.value = formShipData.value.routeType;
    region.value = formShipData.value.regionText;
    let shipPhotos = JSON.parse(formShipData.value.shipPhotos);
    fileList1.value.push({
      message: "",
      size: 0,
      status: "success",
      type: "image",
      thumb: "",
      url:
        import.meta.env.VITE_BASE_URL +
        "/fisherApi/storage/preview?filePath=" +
        shipPhotos.filePath,
    });
    let shipAuditData = JSON.parse(formShipData.value.shipPhotos);
    fileList2.value.push({
      message: "",
      size: 0,
      status: "success",
      type: "image",
      thumb: "",
      url:
        import.meta.env.VITE_BASE_URL +
        "/fisherApi/storage/preview?filePath=" +
        shipAuditData.filePath,
    });
  }
});
onMounted(() => {
  getHarbour();
  setTimeout(()=>{

  getStatus();
  },100)
});
const openId = uni.getStorageSync("openId");
function getStatus() {
  request(
    "/fisherApi/wechatMiniProgram/queryAccountAddShip",
    { openId: openId },
    "POST",
  )
    .then((res) => {
      console.log("res", res.rows[0]);
      if (res.rows.length > 0) {
        formShipData.value = res.rows[0];
        harbour.value = harhourList.value.find(
          (element) => element.value == formShipData.value.harbourNo,
        );
        shipType.value = shipTypeList.find(
          (element) => element.value == formShipData.value.shipType,
        );
        formShipData.value.shipType = formShipData.value.shipType+""
        let regionText = JSON.parse(formShipData.value.region);
        region.value = regionText.province + regionText.city + regionText.area;
        routeType.value = routeTypeList.find(
          (element) => element.value == formShipData.value.routeType,
        );
        formShipData.value.routeType = formShipData.value.routeType+""
        let shipPhotos = JSON.parse(formShipData.value.shipPhotos);
        fileList1.value.push({
          message: "",
          size: 0,
          status: "success",
          type: "image",
          thumb: "",
          url:
            import.meta.env.VITE_BASE_URL +
            "/fisherApi/storage/preview?filePath=" +
            shipPhotos.filePath,
        });
        let shipAuditData = JSON.parse(formShipData.value.shipPhotos);
        fileList2.value.push({
          message: "",
          size: 0,
          status: "success",
          type: "image",
          thumb: "",
          url:
            import.meta.env.VITE_BASE_URL +
            "/fisherApi/storage/preview?filePath=" +
            shipAuditData.filePath,
        });
      }
    })
    .catch((err) => {
      console.log("err", err);
    });
}
const formShip = ref();
const formShipData = ref({
  shipOwner: "",
  shipOwnerContact: "",
  shipOwnershipIdentificationNo: "",
  shipName: "",
  ais: "",
  harbourName: "",
  harbourNo: "",
  shipType: "",
  shipPlate: "",
  region: "",
  routeType: "",
  shipPhotos: "",
  shipAuditData: "",
});
//----------------------<cbzp-船舶照片>----------------------
const fileList1 = ref([]);

// 删除图片
const deletePic = (event) => {
  fileList1.value.splice(event.index, 1);
  formShipData.value.shipPhotos = "";
};

// 新增图片
const afterRead = async (event) => {
  // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
  let lists = [].concat(event.file);
  let fileListLen = fileList1.value.length;
  lists.map((item) => {
    fileList1.value.push({
      ...item,
      status: "uploading",
      message: "上传中",
    });
  });
  for (let i = 0; i < lists.length; i++) {
    const result = await uploadFilePromise(lists[i].url, false);
    let item = fileList1.value[fileListLen];
    fileList1.value.splice(fileListLen, 1, {
      ...item,
      status: "success",
      message: "",
      url: result,
    });
    fileListLen++;
  }
  console.log("fileList1", fileList1.value);
};
//----------------------<cbzjz-船舶证件照>----------------------
const fileList2 = ref([]);

// 删除图片
const deletePic1 = (event) => {
  fileList2.value.splice(event.index, 1);
  formShipData.value.shipAuditData = "";
};

// 新增图片
const afterRead1 = async (event) => {
  // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
  let lists = [].concat(event.file);
  let fileListLen = fileList2.value.length;
  lists.map((item) => {
    fileList2.value.push({
      ...item,
      status: "uploading",
      message: "上传中",
    });
  });
  for (let i = 0; i < lists.length; i++) {
    const result = await uploadFilePromise(lists[i].url, true);
    let item = fileList2.value[fileListLen];
    fileList2.value.splice(fileListLen, 1, {
      ...item,
      status: "success",
      message: "",
      url: result,
    });
    fileListLen++;
  }
};
const uploadFilePromise = (url, flag = false) => {
  return new Promise((resolve, reject) => {
    let a = uni.uploadFile({
      url: "http://10.8.131.221:9999/fisherApi/storage/uploadFile", // 仅为示例，非真实的接口地址
      filePath: url,
      name: "file",
      success: (res) => {
        let data = JSON.parse(res.data);
        if (flag == false) {
          formShipData.value.shipPhotos = JSON.stringify(data.data);
        } else {
          formShipData.value.shipAuditData = JSON.stringify(data.data);
        }
        setTimeout(() => {
          resolve(res.data.data);
        }, 1000);
      },
    });
  });
};
//----------------------<xzq-行政区>----------------------
const region = ref("");
const checkRegion = ref([])
function changeRegion(e) {
  formShipData.value.region = "";
  let data = e.detail.value;
  let code = e.detail.code
  checkRegion.value = data
  let item = {
    province: {
      name: data[0],
      code: code[0]
    },
    city: {
      name: data[1],
      code: code[1]
    },
    area: {
      name: data[2],
      code: code[2]
    },
  };
  region.value = data[0] + data[1] + data[2];
  formShipData.value.region = item;
}

//----------------------<hxlx-航线类型>----------------------
let routeTypeList =[];
const routeType = ref({ label: "", value: "" });
const showRouteType = ref(false);
function sureRouteType(e) {
  showRouteType.value = false;
  routeType.value = e.value[0];
  formShipData.value.routeType = routeType.value.value + "";
}
//----------------------<cjg-船籍港>----------------------
const harhourList = ref([]);
const harbour = ref({ desc: "", value: "" });
const showHarbour = ref(false);

function getHarbour() {
  request("/fisherApi/wechatMiniProgram/pullDownValueHarbourNo", {}, "POST")
    .then((res) => {
      console.log("res", res);
      harhourList.value = res;
    })
    .catch((err) => {
      console.log("err", err);
    });
}
function sureHarbour(e) {
  showHarbour.value = false;
  harbour.value = e.value[0];
  formShipData.value.harbourNo = harbour.value.value;
  formShipData.value.harbourName = harbour.value.desc;
}
//----------------------<cblx-船舶类型>----------------------
let shipTypeList = [];
const showShipType = ref(false);
const shipType = ref({ label: "", value: "" });
function sureShipType(e) {
  showShipType.value = false;
  shipType.value = e.value[0];
  formShipData.value.shipType = shipType.value.value + "";
}
const rules = reactive({
  shipOwner: [
    {
      type: "string",
      required: true,
      message: "请输入姓名",
      trigger: ["blur", "change"],
    },
    {
      pattern: /^\S+$/,
      message: "不能存在空格",
    },
  ],
  shipOwnerContact: [
    {
      required: true,
      message: "请输入手机号",
      trigger: ["blur", "change"],
    },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
    },
  ],
  shipOwnershipIdentificationNo: [
    {
      required: true,
      message: "请输入身份证号",
      trigger: ["blur", "change"],
    },
    {
      pattern:
        /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/,
      message: "请输入正确的身份证",
    },
  ],
  shipName: [
    {
      required: true,
      message: "请输入船舶名称",
      trigger: ["blur", "change"],
    },
  ],
  ais: [
    {
      required: true,
      message: "请输入AIS船名",
      trigger: ["blur", "change"],
    },
  ],
  shipPlate: [
    {
      required: true,
      message: "请输入船牌",
      trigger: ["blur", "change"],
    },
  ],
  harbourNo: [
    {
      required: true,
      message: "请选择船籍港",
      trigger: ["blur", "change"],
    },
  ],
  shipType: [
    {
      required: true,
      message: "请选择船舶类型",
      trigger: ["blur", "change"],
    },
  ],
  region: [
    {
      required: true,
      message: "请选择归属行政区",
      trigger: ["blur", "change"],
    },
  ],
  routeType: [
    {
      required: true,
      message: "请选择航线类型",
      trigger: ["blur", "change"],
    },
  ],
  shipPhotos: [
    {
      required: true,
      message: "请上传船舶照片",
      trigger: ["blur", "change"],
    },
  ],
  shipAuditData: [
    {
      required: true,
      message: "请上传船舶证件照",
      trigger: ["blur", "change"],
    },
  ],
});
function saveLocal() {
  uni.showModal({
    title: "确认暂存？",
    success(res) {
      if (res.confirm) {
        formShipData.value.shipType = shipType.value;
        formShipData.value.routeType = routeType.value;
        formShipData.value.harbour = harbour.value;
        formShipData.value.regionText = region.value;
        uni.setStorageSync("formShipData", formShipData.value);
        util.back();
      }
    },
  });
}
function debounce(flag:boolean) {
  console.log("formShipData", formShipData.value);
  formShip.value.validate().then((valid) => {
    if (valid) {
      request(
        flag?
        "/fisherApi/wechatMiniProgram/applyAccountAddShip":

        "/fisherApi/wechatMiniProgram/modifyAccountAddShip",
        { openId: openId, cbShips: [formShipData.value] },
        "POST",
      )
        .then((res) => {
          console.log("res", res);
          util.toast("发起成功，等待审核！");
          formShipData.value.shipAuditState=0
        })
        .catch((err) => {
          console.log("err", err);
        });
    }
  });
}
</script>
<style scoped lang="scss">
.main {
  .content {
    background: $uni-bg-color-grey;
    padding: unset;
    height: calc(100vh - 80px - 88px);
    overflow-y: auto;
  }
  .user-info {
    box-sizing: border-box;
    margin-bottom: 10px;
    background: white;
    padding: 16px;
    .explain {
      margin-bottom: 10px;
      .text {
        margin-top: 10px;
        padding: 10px;
        background: #f7f7f7;
        color: black;
        font-size: 14px;
        box-sizing: border-box;
      }
    }
    .reject {
      margin-bottom: 10px;
      .text {
        margin-top: 10px;
        padding: 10px;
        background: #f7f7f7;
        color: #f92800;
        font-size: 14px;
        box-sizing: border-box;
      }
    }
    .title {
      font-weight: bolder;
      display: flex;
      justify-content: space-between;
      .state {
        color: white;
        position: relative;
        height: 30px;
        width: 100px;
        background: #0086fd;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: -18px;
        padding-left: 10px;
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
  }
  .ship-info {
    box-sizing: border-box;
    background: white;
    padding: 16px;
    .title {
      font-weight: bolder;
    }
  }
  .select {
    padding: 5px;
    border-bottom: 1px solid #dadbde;
    &.no-input {
      color: #9095b2 !important;
    }
  }
  .picker {
    width: 80vw;
    border: 1px solid;
  }
  .btn-full {
    &.await {
    background:  rgba(247, 247, 247, 1);
      color:rgba(0, 134, 253, 1) ;
    }
  }
}
</style>
