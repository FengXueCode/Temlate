<template>
    <div class='main'>
        <up-navbar>
            <template #left>
                <div class="search">
                    <up-search :showAction="false" placeholder="搜索商品"></up-search>
                </div>
            </template>

        </up-navbar>
        <div class="container">
            <div class="left">
                <div class="item">推荐</div>
                <div class="item" :class="{ active: checkMainClass.productClass.classId === item.productClass.classId }"
                    v-for="item in classList" :key="item.productClass.classId" @click="handleClassCheck(item)">{{
                        item.productClass.name
                    }}
                </div>
            </div>
            <div class="right">
                <div class="banner"></div>
                <div class="right-top-list">
                    <div class="item" :class="{ active: item.productClass.classId === checkChildClass.productClass.classId }"
                        v-for="item in checkMainClass?.children" :key="item.productClass.classId">{{
                            item.productClass.name }}
                    </div>
                </div>
                <div class="class-list">
                    <div class="class" v-for="item in checkMainClass?.children">
                        <div class="title">{{ item.productClass.name }}</div>

                        <div class="children-list" v-if="item.children.length != 0">
                            <div class="item" v-for="itm in item.children" :key="item.classId">
                                <img :src="itm.productClass.image" alt="" class="item-img">
                                <div class="label">{{ itm.productClass.name }}</div>
                            </div>
                        </div>

                        <div class="product-list" v-else>
                            <div class="item" v-for="itm in productList" :key="item.productId">
                                <img :src="itm.mainImage" alt="" class="item-img">
                                <div class="item-info">
                                    <div class="label">{{ itm.name }}</div>
                                    <div class="price">￥{{ itm.price }}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <nav-bottom :checkNav="1"></nav-bottom>
    </div>
</template>
<script setup lang='ts'>
onLoad((val: any) => {

});
onMounted(() => {
    getClassList()
})

const classList = ref([])
function getClassList() {
    classApi.queryAll().then(res => {
        console.log("🚀 ~ getClassList ~ res:", res)
        classList.value = res
        checkMainClass.value = res[0]
        checkChildClass.value = checkMainClass.value.children[0]
    })
}
const checkMainClass = ref()
//切换分类
function handleClassCheck(item: any) {
    checkMainClass.value = item
    checkChildClass.value = item.children[0]
}
const checkChildClass = ref()

//商品列表
const productList = ref([
    {
        productId: "1", name: "XiaoMi 11T", price: "6999", mainImage: "https://img.alicdn.com/imgextra/i4/O1CN01XmwjXe1HYQyj5y0Xw_!!6000000002635-0-tps-800-800.jpg", stock: 100, status: 1, description: "小米11T",
        createTime: "2022-03-01 10:00:00",
        updateTime: "2022-03-01 10:00:00",
    }
])


</script>
<style scoped lang='scss'>
.main {
    box-sizing: border-box;
    // height: 100vh;
    overflow: hidden;

    .container {
        display: flex;
        height: 100%;

    }

    .left {
        width: 30vw;
        height: 100%;
        overflow-y: auto;
        display: flex;
        flex-direction: column;
        padding-bottom: 88px;

        .item {
            padding: 20px 16px;
            font-size: 14px;

            &.active {
                border-left: 2px solid $uni-color-zt;
                font-weight: bold;
                box-sizing: border-box;
            }
        }
    }

    .right {
        width: 70vw;
        height: 100%;

        padding: 8px;
        box-sizing: border-box;
        overflow-y: auto;

        .banner {
            width: 100%;
            height: 100px;
            border: 1px solid;
            box-sizing: border-box;
        }

        .right-top-list {
            display: flex;
            overflow-x: auto;
            padding: 10px 0;
            border-bottom: 1px solid $uni-border-color;

            .item {
                white-space: nowrap;
                font-size: 14px;
                padding: 10px;
                border-radius: 5px;
                margin-right: 10px;

                &.active {
                    background: #EFEFEF;
                }
            }
        }

        .class-list {
            .class {
                .title {
                    font-weight: bold;
                    font-size: 16px;
                    margin: 10px 0;
                }

                .children-list {
                    display: grid;
                    grid-template-columns: repeat(3, 1fr);
                    grid-gap: 10px;


                    .item {
                        .item-img {
                            border: 1px solid;
                            width: 100%;
                            height: auto;
                            aspect-ratio: 1/1;

                        }

                        .label {
                            font-size: 12px;
                            text-align: center;
                            margin-top: 5px;
                        }
                    }
                }

                .product-list {
                    .item {
                        display: flex;
                        background: #F8F8F8;
                        box-sizing: border-box;
                        padding: 10px;
                        border-radius: 5px;

                        .item-img {
                            width: 80px;
                            height: 80px;
                            border: 1px solid;
                            margin-right: 10px;
                        }

                        .item-info {
                            display: flex;
                            flex-direction: column;
                            justify-content: space-around;
                        }
                    }
                }
            }
        }
    }
}
</style>