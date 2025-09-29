<template>
  <div class="main">
    <up-navbar title="麻友记账" leftIcon=""></up-navbar>
    <div class="content">
      <!-- 用户信息 -->
      <div class="user-info">
        <div class="container" @click="goInfo">
          <img :src="user?.portrait != null ? user?.portrait : head" alt="" class="head">
          <div class="info">
            <div class="name">{{ user?.nickName }}</div>
            <div class="uid">uid:{{ user?.userId }}</div>
          </div>
        </div>
        <div class="data-show">
          <div class="item">
            <div class="number">{{ 0 }}</div>
            <div class="label">胜场</div>
          </div>
          <div class="item">
            <div class="number">{{ 0 }}%</div>
            <div class="label">胜率</div>
          </div>
        </div>

      </div>
      <!-- 创建房间 -->
      <div class="btn-group">
        <div class="item" @click="createRoom"> 创建新的房间</div>
        <div class="item" @click="scanJoin">扫码加入房间</div>
      </div>

      <!-- 功能 -->
      <div class="function">
        <button class="item" open-type="contact" bindcontact="handleContact" session-from="sessionFrom">
          联系客服
        </button>
        <button class="item" @click="goInfo">修改头像昵称</button>
        <button class="item" @click="goDescribe">操作说明</button>
      </div>
    </div>

    <!-- 创建房间时选择模式的弹窗 -->
    <up-popup :show="showCreateRoomModal" mode="center" :round="10" closeable @close="showCreateRoomModal = false">
      <div class="create-room-modal">
        <div class="modal-title">选择房间模式</div>
        <div class="mode-options">
          <div class="mode-option" :class="{ active: selectedMode === 0 }" @click="selectMode(0)">
            <div class="mode-name">麻将模式</div>
          </div>
          <div class="mode-option" :class="{ active: selectedMode === 1 }" @click="selectMode(1)">
            <div class="mode-name">牌类模式</div>
          </div>
        </div>
        <div class="money-ratio">
          <div class="ratio-label">分钱比(元/分)</div>
          <div class="ratio-input">
            <up-input v-model="moneyRatio" type="digit" placeholder="请输入分钱比，如：10元/分" />
          </div>
        </div>
        <up-button type="primary" shape="circle" @click="confirmCreateRoom">确定创建</up-button>
      </div>
    </up-popup>

    <nav-bottom :checkNav="0"></nav-bottom>
  </div>
</template>
<script setup lang="ts">
onLoad((val: any) => {

});
onShow(() => {
  if (user.value.userId === undefined) {
    initUser();
  } else {
    getRoom()
  }
})
onMounted(() => {

});
import head from "@/static/icon/user.png"
//--------------------<user-info,跳转用户信息>-----------------------
function goInfo() {
  util.go("/pageMy/info")
}
//--------------------<describe,跳转描述>-----------------------
function goDescribe() {
  util.go("/pageMy/describe")
}
//--------------------<初始化用户>-----------------------
const user = ref(uni.getStorageSync("user"))

function initUser() {
  uni.login({
    provider: "weixin",
    success: function (res: any) {
      userApi.initLogin(res.code).then((res) => {
        user.value = res
        uni.setStorageSync("user", res);
        getRoom()
      })
    }
  })

}
//--------------------<创建房间>-----------------------
const showCreateRoomModal = ref(false);
const selectedMode = ref(0);
const moneyRatio = ref('');

function createRoom() {
  showCreateRoomModal.value = true;
}

function selectMode(mode: number) {
  selectedMode.value = mode;
}

function confirmCreateRoom() {

  if (!moneyRatio.value) {
    util.toast("请设置分钱比");
    return;
  }
  if (moneyRatio.value <= 0) {
    util.toast("请输入正确的分钱比")
    return
  }

  roomApi.createRoom(selectedMode.value, moneyRatio.value).then((res: any) => {
    if (res.roomId != undefined) {
      util.relaunch("/pages/room?roomId=" + res.roomId + "&isNew=new")
    }
  })
}

//--------------------<获取房间>-----------------------
function getRoom() {
  roomApi.getRoom().then((res: any) => {
    if (res.roomId != undefined) {
      util.relaunch("/pages/room?roomId=" + res.roomId)
    }
  }).catch((err: any) => {
    if (err.sttatusCode == 401) {
      initUser()
    }
  })
}
//--------------------<扫码加入房间>-----------------------
function scanJoin() {
  uni.scanCode({
    success: function (res: any) {
      if (res.scanType == 'WX_CODE') {
        let roomId = res.path.split("=")[1]
        util.relaunch("/pages/room?roomId=" + roomId)
      } else {
        util.toast("扫码错误")
      }
    },
  })
}
</script>
<style scoped lang="scss">
.main {
  .content {
    height: calc(100% - 88px);

    .user-info {
      width: 100%;
      height: 20vh;
      border-radius: 10px;
      padding: 16px;
      box-sizing: border-box;
      background-color: $uni-color-zt;
      color: white;
      display: flex;
      flex-direction: column;
      justify-content: space-between;


      .container {
        display: flex;
        align-items: center;
        padding-bottom: 10px;
        border-bottom: 1px solid $uni-border-color;

        .head {
          width: 60px;
          height: 60px;
          border-radius: 50%;
          margin-right: 16px;
        }

        .info {
          display: flex;
          flex-direction: column;
          justify-content: space-around;

          .name {
            font-size: 16px;
            font-weight: 600;
          }

          .uid {
            font-size: 12px;
          }
        }
      }

      .data-show {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        align-items: center;

        .item {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          font-weight: 600;

          .number {
            font-size: 20px;
            color: #FDDE32;
          }
        }

        .item:nth-child(1) {
          border-right: 1px solid $uni-border-color;
        }
      }

    }

    .btn-group {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      grid-gap: 10px;
      margin: 40px 0;

      .item {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 10px;
        border: 1px solid $uni-color-zt;
        border-radius: 6px;
        color: $uni-color-zt;
      }

      .item:nth-child(1) {
        background-color: $uni-color-zt;
        color: white;
      }
    }

    .function {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .item {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 10px;
        border: 1px solid $uni-color-zt;
        border-radius: 6px;
        color: $uni-color-zt;
        background: white;
        height: 44px;
        margin: unset !important;
        font-size: 16px;
      }
    }
  }

  .create-room-modal {
    width: 300px;
    padding: 20px;

    .modal-title {
      text-align: center;
      font-size: 18px;
      font-weight: bold;
      margin-bottom: 20px;
    }

    .mode-options {
      display: flex;
      justify-content: space-between;
      margin-bottom: 20px;

      .mode-option {
        flex: 1;
        text-align: center;
        padding: 15px;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        margin: 0 5px;
        cursor: pointer;

        &.active {
          border-color: $uni-color-zt;
          background-color: rgba($uni-color-zt, 0.1);
        }

        .mode-name {
          font-size: 16px;
        }
      }
    }

    .money-ratio {
      margin-bottom: 20px;

      .ratio-label {
        margin-bottom: 10px;
        font-size: 14px;
      }
    }
  }
}
</style>