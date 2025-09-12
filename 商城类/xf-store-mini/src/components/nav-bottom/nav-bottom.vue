<template>
  <div class='nav-bottom'>
    <div class="item" v-for="(item, index) in list" :key="item.path" @click="navTo(item.path)">
      <img :src="item.iconActive" alt="" class="icon" v-if="index === props.checkNav">
      <img :src="item.icon" alt="" class="icon" v-else>
      <div class="label" :class="{ active: index === props.checkNav }">{{ item.label }}</div>
    </div>

  </div>
</template>
<script setup lang='ts'>
onLoad((val: any) => {

});
onMounted(() => {

});
const props = defineProps({
  checkNav: Number
})

const list = ref([
  {
    path: "/pages/index",
    icon: '',
    iconActive: "",
    label: "首页"
  },
  {
    path: "/pages/index",
    icon: "",
    iconActive: "",
    label: "分类"
  },

  {
    path: "/pages/index",
    icon: "",
    iconActive: "",
    label: "购物车"
  },
  {
    path: "/pageMy/my",
    icon: "",
    iconActive: "",
    label: "我的"
  }
])
function navTo(url: string) {
  util.relaunch(url)
}
</script>
<style scoped lang='scss'>
.nav-bottom {
  position: fixed;
  bottom: 0;
  width: 100vw;
  height: 88px;
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 10px;
  box-sizing: border-box;
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.1);
  z-index: 9999;
  background-color: white;

  .item {
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-between;

    .icon {
      width: 36px;
      height: 36px;
      border: 1px solid;
    }

    .label {
      font-size: 16px;
      height: 24px;

      &.active {
        color: $uni-color-primary;
      }
    }
  }
}
</style>
