<template>
  <div class='nav-bottom'>
    <div class="item" v-for="(item, index) in list" :key="item.path" @click="navTo(item.path, index)">
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
import pen from "@/static/icon/nav/pen.svg";
import penActive from "@/static/icon/nav/pen-active.svg";
import book from "@/static/icon/nav/book.svg";
import bookActive from "@/static/icon/nav/book-active.svg";
import my from "@/static/icon/nav/my.svg";
import myActive from "@/static/icon/nav/my-active.svg";
const list = ref([
  {
    path: "/pages/index",
    icon: pen,
    iconActive: penActive,
    label: "刷题"
  },
  {
    path: "/pageBank/bank",
    icon: book,
    iconActive: bookActive,
    label: "题库"
  },
  {
    path: "/pageMy/my",
    icon: my,
    iconActive: myActive,
    label: "我的"
  }
])
function navTo(url: string, index: number) {
  if (index === props.checkNav) return
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
        color: $uni-color-primary;
      }
    }
  }
}
</style>
