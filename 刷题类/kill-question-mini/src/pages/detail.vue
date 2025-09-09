<!--
 * @Author: FengXue
 * @Date: 2024-04-26 16:20:03
 * @LastEditors: FengXue
 * @LastEditTime: 2024-06-03 13:42:04
 * @filePath: Do not edit
-->
<template>
  <up-navbar title="题库详情" :autoBack="true"></up-navbar>
  <div class="main">
    <div class="practice">
      <div class="content">
        <div class="text">
          <div class="title">
            {{ questionBank.questionBank.questionBankName }}
          </div>
          <div class="author">
            共{{ questionBank.total }}题/作者：{{
              questionBank.questionBank.author
            }}
          </div>
          <div class="infomation">
            做{{ questionBank.questionRecord.accomplishArray.length }}题/错{{
              questionBank.questionRecord.errorArray.length
            }}题/收藏{{ questionBank.questionRecord.collectArray.length }}题
          </div>
        </div>
        <div class="hear">
            <img class="img" :src="questionBank.portrait == null ? userImg : questionBank.portrait" alt="" />
          </div>
      </div>
      <div class="line">
        <div class="success" :style="{
          width:
            ((questionBank.questionRecord.accomplishArray.length -
              questionBank.questionRecord.errorArray.length) /
              questionBank.total) *
            100 +
            '%',
        }"></div>
        <div class="error" :style="{
          width:
            (questionBank.questionRecord.errorArray.length /
              questionBank.total) *
            100 +
            '%',
        }"></div>
        <div class="none"></div>
      </div>
    </div>
    <div class="function">
      <div class="item" @click="show = true">
        <img class="icon" :src="practice" />
        <div class="text">开始刷题</div>
      </div>
      <div class="item" @click="resetBank">
        <img class="icon" :src="reset" />
        <div class="text">重新刷题</div>
      </div>
      <div class="item" @click="shareBank" v-if="questionBank.questionBank.type == 0">
        <img class="icon" :src="share" />
        <div class="text">共享题库</div>
      </div>
      <div class="item" @click="searchQuestion">
        <img class="icon" :src="search" />
        <div class="text">搜索题目</div>
      </div>
      <div class="item" @click="peak">
        <img class="icon" :src="peakedness" />
        <div class="text">巅峰赛</div>
      </div>

      <button open-type="share" class="item">
        <img class="icon" :src="share1" />
        <div class="text">分享</div>
      </button>

      <div class="item" @click="remove" v-if="questionBank.questionBank.type == 0">
        <img class="icon" :src="del" />
        <div class="text">删除</div>
      </div>
    </div>

    <!-- 弹出层 -->
    <up-popup :show="show" mode="right" @close="show = false" safe-area-inset-top>
      <div class="popup">
        <div class="title">
          <div>刷题设置</div>
          <div class="close" @click="show = false">关闭</div>
        </div>
        <up-form labelPosition="left" :model="setting" labelWidth="80px" ref="form">
          <up-form-item label="刷题范围:" prop="num">
            <div class="popup-content">
              <div class="item" :class="{ active: item.value == setting.scope }" v-for="item in scopeList"
                @click="changescope(item)">
                {{ item.label }}
              </div>
            </div>
          </up-form-item>
          <up-form-item label="筛选题型:" prop="num">
            <div class="popup-content">
              <div class="item" :class="{ active: item.value == setting.type }" v-for="item in typeList"
                @click="changeType(item)">
                {{ item.label }}
              </div>
            </div>
          </up-form-item>
          <up-form-item label="刷题模式:" prop="num">
            <div class="popup-content">
              <div class="item" :class="{ active: item.value == setting.mode }" v-for="item in modeList"
                @click="changeMode(item)">
                {{ item.label }}
              </div>
            </div>
          </up-form-item>
          <up-form-item label="其他设置:" prop="num">
            <div class="popup-content">
              <div class="item" @click="changeTitleChaos" :class="{ active: setting.titleChaos == 1 }">
                题目乱序
              </div>
              <div class="item" @click="changeOptionChaos" :class="{ active: setting.optionChaos == 1 }">
                选项乱序
              </div>
              <div class="item" @click="changeAutoSwitch" :class="{ active: setting.autoSwitch == 1 }">
                自动切题
              </div>
              <div class="item" @click="changeAutoCollect" :class="{ active: setting.autoCollect == 1 }">
                错题收藏
              </div>
            </div>
          </up-form-item>
        </up-form>
        <div class="btn" @click="startExams">开始刷题</div>
      </div>
    </up-popup>
  </div>
</template>

<script setup lang="ts">
import peakedness from "@/static/icon/practiceExams/peakedness.svg";
import practice from "@/static/icon/practiceExams/practice.svg";
import reset from "@/static/icon/practiceExams/reset.svg";
import search from "@/static/icon/practiceExams/search.svg";
import share from "@/static/icon/practiceExams/share.svg";
import share1 from "@/static/icon/practiceExams/share1.svg";
import del from "@/static/icon/practiceExams/delete.svg";
import userImg from "@/static/icon/my/user.png";
import { onLoad, onShow } from "@dcloudio/uni-app";
const shareData = computed(() => {
  return {
    title: "小闽题库：" + questionBank.value.questionBank.questionBankName,
    path:
      "pageIndex/index/tools/practiceExams/detail?questionBankId=" +
      questionBank.value.questionBank.questionBankId,
  }
})
onLoad((e) => {
  if (e.item != undefined) {
    questionBank.value = JSON.parse(decodeURIComponent(e.item));
    questionBankId.value = questionBank.value.questionBank.questionBankId;
  }
  uni.$mpShare = shareData.value
  if (e.questionBankId != undefined) {
    questionBankId.value = e.questionBankId;
    request(
      "/questionShare/add",
      { questionBankId: questionBankId.value, userId: user.userId },
      "POST",
    )
      .then((res) => {
      })
      .catch((err) => {
        console.log("err", err);
      });
  }
});
onShow(() => {
  initData();
  initSetting();
  uni.$mpShare = shareData.value
});
const questionBankId = ref("");
function remove() {
  uni.showModal({
    title: "确认删除?",
    success: function (res) {
      if (res.confirm) {
        request(
          "/questionBank/remove",
          { questionBankId: questionBankId.value, userId: user.userId },
          "GET",
        )
          .then((res) => {
            util.back();
          })
          .catch((err) => {
            console.log("err", err);
          });
      }
    },
  });
}
const initData = () => {
  request("/questionBank/findById", {
    questionBankId: questionBankId.value,
  }, 'GET', {})
    .then(res => {
      let data = res;
      data.questionRecord.accomplishArray = JSON.parse(
        data.questionRecord.accomplishArray,
      );
      data.questionRecord.errorArray = JSON.parse(
        data.questionRecord.errorArray,
      );
      data.questionRecord.collectArray = JSON.parse(
        data.questionRecord.collectArray,
      );
      questionBank.value = data;
    })
    .catch(err => {
      console.log(err);
    })

};
const questionBank = ref<any>({
  questionBank: {},
  questionRecord: {
    accomplishArray: [],
    errorArray: [],
    collectArray: [],
  },
  total: 0,
  portrait: "",
});

const user = uni.getStorageSync("userInfo");
//巅峰赛
const peak = () => {
  uni.navigateTo({
    url: `/pages/peak?item=${encodeURIComponent(
      JSON.stringify(questionBank.value),
    )}`,
  });
};
//分享题库
const shareBank = () => {
  uni.showModal({
    title: "提示",
    content: "确认共享题库嘛？",
    success: function (res) {
      if (res.confirm) {
        request("/questionBank/shareBank", {
          questionBankId: questionBank.value.questionBank.questionBankId,
        }, 'GET', {})
          .then(res => {
            util.toast("分享成功")
          })
          .catch(err => {
            console.log(err);
          })

      }
    },
  });
};
//
// //分享致微信
// onShareAppMessage((res: any) => {
//   console.log("res", res);
//   return {
//     title: "小闽题库：" + questionBank.value.questionBank.questionBankName,
//     path:
//       "pageIndex/index/tools/practiceExams/detail?questionBankId=" +
//       questionBank.value.questionBank.questionBankId,
//   };
// });
//
// 搜索题目
const searchQuestion = () => {
  uni.showModal({
    title: "搜索题目",
    editable: true,
    placholderText: "关键词",
    success: (val) => {
      if (val.confirm) {
        if (val.content == "") {
          return;
        }
        questionBank.value.key = val.content;
        uni.navigateTo({
          url:
            "/pages/examsIng?questionBank=" +
            encodeURIComponent(JSON.stringify(questionBank.value)),
        });
      }
    },
  });
};
//弹窗管理
const show = ref(false);
const setting = ref({
  questionSettingId: "",
  userId: user.userId,
  scope: 0, //刷题范围 0 全部 1 收藏 2 错误 3 未作
  type: 0, //筛选体系 0 全部 1 单选 2多选 3 判断 4 填空
  mode: 0, //刷题模式 0 刷题模式 1 背题模式
  titleChaos: 0, //题目乱序 0关闭 1开启
  optionChaos: 0, //选项乱序 0 关闭 1开启
  autoSwitch: 0, //自动切题 0关闭 1开启
  autoCollect: 0, //做错自动收藏 0关闭 1开启
});
const initSetting = () => {
  request("/questionSetting/findByUserId", {}, 'GET', {})
    .then(res => {
      setting.value = res
    })
    .catch(err => {
      console.log(err);
    })

};
const scopeList = [
  {
    label: "全部题目",
    value: 0,
  },
  {
    label: "收藏题目",
    value: 1,
  },

  {
    label: "错误题目",
    value: 2,
  },

  {
    label: "未作题目",
    value: 3,
  },
];
const changescope = (row: any) => {
  setting.value.scope = row.value;
};
const typeList = [
  {
    label: "全部",
    value: 0,
  },
  {
    label: "单选",
    value: 1,
  },
  {
    label: "多选",
    value: 2,
  },
  {
    label: "判断",
    value: 3,
  },
  {
    label: "填空",
    value: 4,
  },
];
const changeType = (row: any) => {
  setting.value.type = row.value;
};
const modeList = [
  {
    label: "刷题模式",
    value: 0,
  },
  {
    label: "背题模式",
    value: 1,
  },
];
const changeMode = (row: any) => {
  setting.value.mode = row.value;
};
const changeTitleChaos = () => {
  setting.value.titleChaos = setting.value.titleChaos == 0 ? 1 : 0;
};
const changeOptionChaos = () => {
  setting.value.optionChaos = setting.value.optionChaos == 0 ? 1 : 0;
};
const changeAutoSwitch = () => {
  setting.value.autoSwitch = setting.value.autoSwitch == 0 ? 1 : 0;
};
const changeAutoCollect = () => {
  setting.value.autoCollect = setting.value.autoCollect == 0 ? 1 : 0;
};
// 开始刷题
const startExams = () => {
  request("/questionSetting/update", setting.value, 'POST', {})
    .then(res => {
      uni.navigateTo({
        url:
          "/pages/examsIng?questionBank=" +
          encodeURIComponent(JSON.stringify(questionBank.value)),
        success: () => {
          show.value = false;
        },
      });
    })
    .catch(err => {
      console.log(err);
    })

};

// 重新刷题
const resetBank = () => {
  uni.showModal({
    title: "确认重置刷题记录？",
    success: (res) => {
      if (res.confirm) {
        request("/questionRecord/reset", {

          questionBankId: questionBank.value.questionBank.questionBankId,
        }, 'GET', {})
          .then(res => {
            initData()
          })
          .catch(err => {
            console.log(err);
          })


      }
    },
  });
};
</script>
<style lang="scss" scoped>
.main {
  .practice {
    width: 100%;
    height: 100px;
    box-shadow: $uni-border-shadow;
    margin-bottom: 20px;
    box-sizing: border-box;
    border-radius: 5px 5px 0 0;

    .content {
      padding: 10px;
      box-sizing: border-box;
      display: flex;
      width: 100%;
      height: 100%;
      color: $uni-text-color-grey;

      .text {
        width: 80%;
        height: 100%;
        font-size: 18px;

        .title {
          font-size: 24px;
          color: black;
        }
      }

      .hear {
        width: 20%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;

        .img {
          width: 50px;
          height: 50px;
          border-radius: 25px;
          border: 1px solid;
        }
      }
    }

    .line {
      width: 100%;
      height: 5px;
      display: flex;
      border-radius: 0 0 5px 5px;

      .success {
        border-radius: 0 0 0 5px;
        background: #a5d63f;
      }

      .error {
        background: #d43030;
      }

      .none {
        flex-grow: 1;
        background: #cccccc;
        border-radius: 0 0 5px 0;
      }
    }
  }

  .function {
    width: 100%;
    display: grid;
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
    grid-gap: 10px;

    .item {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      box-sizing: border-box;
      box-shadow: $uni-border-shadow;
      border-radius: 7px;
      padding: 10px 3px 10px 3px;
      background: white !important;

      .icon {
        width: 32px;
        height: 32px;
      }

      .text {
        font-size: 12px;
        margin-top: 5px;
      }
    }
  }

  $active: #025e7d;

  .popup {
    width: 85vw;
    padding: 44px 5px 0 5px;

    .title {
      height: 30px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid $uni-border-color;

      .close {
        color: $active;
      }
    }

    .popup-content {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      grid-gap: 5px;

      .item {
        color: $uni-text-color-grey;
      }
    }

    .active {
      color: $active !important;
    }

    .btn {
      width: 96%;
      height: 30px;
      color: white;
      background: $active;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 10px;
      margin-top: 10px;
    }
  }
}
</style>
