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
onShow(() => {
})
import homeActive from "@/static/icon/nav/home-active.svg";
import home from "@/static/icon/nav/home.svg";
import statisticsActive from "@/static/icon/nav/statistics-active.svg";
import statistics from "@/static/icon/nav/statistics.svg";
const props = defineProps({
  checkNav: Number
})

const list = ref([
  {
    path: "/pages/index",
    icon: home,
    iconActive: homeActive,
    label: "首页"
  },
  {
    path: "/pageMy/my",
    icon: statistics,
    iconActive: statisticsActive,
    label: "统计"
  }
])
function navTo(url: string) {
  util.relaunch(url)
}
</script>
<style scoped lang='scss'>
.nav-bottom {
  background: white;
  position: fixed;
  bottom: 0;
  width: 100vw;
  height: 88px;
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 10px;
  box-sizing: border-box;
  box-shadow: 0 0 6px $uni-bg-color-grey;

  .item {
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-between;

    .icon {
      width: 36px;
      height: 36px;
    }

    .label {
      font-size: 16px;
      height: 24px;

      &.active {
        color: $uni-color-zt;
      }
    }
  }
}
</style>
