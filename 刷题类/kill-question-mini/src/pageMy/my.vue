<template>
  <div class="main">
    <div class="head">
      <img :src="user.portrait == null ? userImg : user.portrait" alt="" class="head-img">
      <div class="info">
        <div class="name">昵称：{{ user.nickName }}</div>
        <div class="code">编号：{{ user.userId }} <img src="@/static/icon/my/copy.svg" class="copy" @click="copyId"></img>  </div>
      </div>
    </div>
    <up-cell-group>
      <up-cell v-for="(item, index) in navList" :key="index" :title="item.label" :icon="item.icon" isLink
        @click="navTo(item.url)">
      </up-cell>
    </up-cell-group>
    <nav-bottom :checkNav="2"></nav-bottom>
  </div>
</template>
<script setup lang="ts">
import userImg from "@/static/icon/my/user.png";
onLoad((val: any) => { });
onMounted(() => { });

//----------------------<yhm-用户名>----------------------
const user = uni.getStorageSync("user")

//----------------------<gndh-功能导航>----------------------
const navList = ref([
  {
    label: "列表1",
    icon: '',
    url: "/pageMy/information"
  },
  {
    label: "列表2",
    icon: '',
    url: "/pageMy/harbour"
  },
])

function navTo(url: string) {
  util.go(url)
}

//--------------------<copyId-复制用户id>-----------------------
function copyId() {
  uni.setClipboardData({
    data: user.userId
  })
}
</script>
<style scoped lang="scss">
.main {
  height: 100vh;
  padding: unset;
  margin: unset;

  .head {
    width: 100vw;
    height: calc(30vh);
    background: $uni-bg-color-grey;
    display: flex;
    align-items: center;
    padding: 0 $uni-spacing-row-lg;
    padding-top: 80px;
    box-sizing: border-box;
    background: #ECF2FFFF;

    .head-img {
      width: 60px;
      height: 60px;
      border-radius: 40px;
    }

    .info {
      margin-left: $uni-spacing-row-lg;

      .name {
        border-bottom: 1px solid $uni-border-color;
        padding-bottom: 5px;
      }

      .code {
        margin-top: 5px;
        color: $uni-text-color-grey;
        font-size: 12px;
        display: flex;
        align-items: center;
      }
      .copy {
        width: 16px;
        height: 16px;
        margin-left: 5px;
      }
    }
  }

  .cell-group {
    box-sizing: border-box;
    padding: 0 $uni-spacing-row-lg;

    .cell {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 $uni-spacing-row-lg;
      height: 60px;
      border-bottom: 1px solid $uni-border-color;

      .cell-info {
        display: flex;
        align-items: center;

        .icon {
          width: 24px;
          height: 24px;
        }

        .title {
          margin-left: $uni-spacing-row-base;
        }
      }

      .right {
        .state {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 70px;
          height: 30px;
          border-radius: 15px;
          background: $uni-bg-color-grey;
          font-size: 14px;
        }
      }
    }
  }
}
</style>
