<template>
  <div class="main">
    <up-navbar v-if="isScrolled" :background="{ background: { color: '$uni-color-zt' } }">
      <template #left>
        <div class="search">
          <up-search :showAction="false" placeholder="搜索商品"></up-search>
        </div>
      </template>

    </up-navbar>
    <!-- 头部 -->
    <div class="bg-head">
      <img src="@/static/logo.png" alt="" class="logo">
      <div class="search">
        <up-search :showAction="false"></up-search>
      </div>
    </div>
    <!-- banner -->
    <div class="swiper">
      <up-swiper :height="160"></up-swiper>
    </div>
    <!-- 分类导航 -->
    <div class="class">
      <div class="item" v-for="index in 10">
        <img src="" alt="" class="icon">
        <div class="label">分类</div>
      </div>
    </div>
    <!-- 推广 -->

    <!-- 列表 -->
    <div class="row-list" :class="{ fixed: isRowListFixed }">
      <div class="item" :class="{ active: index == nowClass }" v-for="(item, index) in classList" :key="item.id">
        <div class="title">{{ item.title }}</div>
        <div class="label">{{ item.label }}</div>
      </div>
    </div>
    <div class="list">
      <div class="item" v-for="item in list" :key="item.productId">
        <img :src="item.mainImage" alt="" class="item-img">
        <div class="item-info">
          <div class="name">{{ item.name }}</div>
          <div class="price">￥{{ item.price }}</div>
        </div>
      </div>
    </div>

    <nav-bottom :checkNav="0"></nav-bottom>
  </div>
</template>
<script setup lang="ts">
// 添加滚动状态响应式变量
const isScrolled = ref(false)
// 添加rowList固定状态响应式变量
const isRowListFixed = ref(false)

onLoad((val: any) => { });
onShow(() => {
})
onMounted(() => {

});

// 监听页面滚动
onPageScroll((e) => {
  // 如果滚动高度超过50px，设置isScrolled为true，否则为false
  isScrolled.value = e.scrollTop > 50;
  // 如果滚动高度超过88px，设置isRowListFixed为true，否则为false
  isRowListFixed.value = e.scrollTop > 88;
});

//--------------------<list-列表>-----------------------
//分类列表
const classList = ref([])
classList.value = [
  { id: "1", title: "精选", label: "商品推荐" },
  { id: "2", title: "精选", label: "商品推荐" },
  { id: "3", title: "精选", label: "商品推荐" },
  { id: "4", title: "精选", label: "商品推荐" },
  { id: "5", title: "精选", label: "商品推荐" },
  { id: "6", title: "精选", label: "商品推荐" },
  { id: "7", title: "精选", label: "商品推荐" },
]
const nowClass = ref(0)
const list = ref([])
list.value = [
  { productId: "1", name: "小米6A Type-C快充数据线 原装同款", mainImage: "", description: "", price: 27.0, stock: 100, status: 1, createTime: "2021-09-01 00:00:00", updateTime: "2021-09-01 00:00:00" },
  { productId: "1", name: "小米6A Type-C快充数据线 原装同款", mainImage: "", description: "", price: 27.0, stock: 100, status: 1, createTime: "2021-09-01 00:00:00", updateTime: "2021-09-01 00:00:00" },
  { productId: "1", name: "小米6A Type-C快充数据线 原装同款", mainImage: "", description: "", price: 27.0, stock: 100, status: 1, createTime: "2021-09-01 00:00:00", updateTime: "2021-09-01 00:00:00" },
  { productId: "1", name: "小米6A Type-C快充数据线 原装同款", mainImage: "", description: "", price: 27.0, stock: 100, status: 1, createTime: "2021-09-01 00:00:00", updateTime: "2021-09-01 00:00:00" },
  { productId: "1", name: "小米6A Type-C快充数据线 原装同款", mainImage: "", description: "", price: 27.0, stock: 100, status: 1, createTime: "2021-09-01 00:00:00", updateTime: "2021-09-01 00:00:00" },
  { productId: "1", name: "小米6A Type-C快充数据线 原装同款", mainImage: "", description: "", price: 27.0, stock: 100, status: 1, createTime: "2021-09-01 00:00:00", updateTime: "2021-09-01 00:00:00" },
  { productId: "1", name: "小米6A Type-C快充数据线 原装同款", mainImage: "", description: "", price: 27.0, stock: 100, status: 1, createTime: "2021-09-01 00:00:00", updateTime: "2021-09-01 00:00:00" },
]

</script>
<style scoped lang="scss">
:deep(.u-navbar) {
  background: $uni-color-zt !important;
}

.main {
  margin-top: unset;
  padding-top: unset;
  background: $uni-bg-color-grey;
  height: auto;

  .bg-head {
    height: 20vh;
    background: $uni-color-zt;
    position: relative;
    border-radius: 0 0 20px 20px;

    .logo {
      width: 32px;
      height: 32px;
      position: absolute;
      top: 50px;
      left: 16px;
    }

    .search {
      width: 55%;
      height: 32px;
      position: absolute;
      top: 50px;
      left: 58px;

    }
  }

  .swiper {
    margin-top: -75px;
    padding: 0 16px;
    z-index: 2;
    width: 100%;
    box-sizing: border-box;
  }

  .class {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    background-color: white;
    margin: 16px;
    box-sizing: border-box;
    border-radius: 6px;
    padding: 16px;
    grid-gap: 10px;

    .item {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;

      .icon {
        width: 32px;
        height: 32px;
        border: 1px solid;
      }

      .label {
        margin-top: 6px;
        font-size: 12px;
        color: #333;
      }
    }
  }

  .row-list {
    display: flex;
    overflow-x: auto;
    width: 100vw;
    margin: 16px 0;
    // 当isRowListFixed为true时固定在顶部
    background-color: white;
    z-index: 999;

    &.fixed {
      position: sticky;
      top: 88px;
      padding: 10px 0;
    }

    .item {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 16px;
      box-sizing: border-box;
      border-radius: 6px;
      background-color: white;
      margin-right: 10px;
      flex-wrap: nowrap;

      &.active {
        background-color: $uni-color-zt;
        color: white;

      }

      .title {
        font-size: 16px;
      }

      .label {
        //禁止换行
        white-space: nowrap;
        font-size: 12px;
        margin-top: 5px;
      }

    }
  }

  .list {
    width: 100vw;
    padding: 0 16px;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    grid-gap: 16px;
    box-sizing: border-box;
    padding-bottom: 100px;

    .item {
      border-radius: 10px;
      width: 100%;
      background: #FCFCFC;

      .item-img {
        width: 100%;
        height: auto;
        aspect-ratio: 1/1;

      }

      .item-info {
        background-color: white;
        padding: 10px;
        border-radius: 0 0 10px 10px;

        .price {
          margin-top: 10px;
          color: $uni-color-zt;
        }
      }
    }


  }
}
</style>