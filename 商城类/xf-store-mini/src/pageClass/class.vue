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
                <div class="item" :class="{ active: checkMainClass.classId === item.classId }" v-for="item in classList"
                    :key="item.classId" @click="handleClassCheck(item)">{{
                        item.name
                    }}
                </div>
            </div>
            <div class="right">
                <div class="banner"></div>
                <div class="right-top-list">
                    <div class="item" :class="{ active: item.classId === checkChildClass.classId }"
                        v-for="item in checkMainClass.children" :key="item.classId">{{ item.name }}</div>
                </div>
                <div class="class-list">
                    <div class="class" v-for="item in checkMainClass.children">
                        <div class="title">{{ item.name }}</div>

                        <!-- 子分类 -->
                        <div class="children-list" v-if="item.children != undefined">
                            <div class="item" v-for="itm in item.children" :key="item.classId">
                                <img :src="itm.image" alt="" class="item-img">
                                <div class="label">{{ itm.name }}</div>
                            </div>
                        </div>

                        <!-- 商品列表 -->
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
})

const classList = ref([
    {
        classId: "1", name: "xiaomi", parent: "", image: "", children: [
            { classId: "1-1", name: "XiaoMi 数字旗舰", image: "", parent: "1" },
            { classId: "1-2", name: "XiaoMi MIX系列", image: "", parent: "1" },
            { classId: "1-3", name: "XiaoMi Civi", image: "", parnet: "1" },
            { classId: "1-4", name: "XiaoMi 手机套餐", image: "", parent: "1" },
        ],

    },
    {
        classId: "2", name: "手机配件", parent: "", image: "", children: [
            {
                classId: "2-1", name: "XiaoMi 配件", image: "", parent: "2", children: [
                    { classId: "2-1-1", name: "充电器", image: "", parent: "2-1" },
                    { classId: "2-1-2", name: "车充", image: "", parent: "2-1" },
                    { classId: "2-1-3", name: "游戏配件", image: "", parent: "2-1" },
                    { classId: "2-1-4", name: "数据线", image: "", parent: "2-1" },
                    { classId: "2-1-5", name: "无线充", image: "", parent: "2-1" },
                    { classId: "2-1-6", name: "手机保护壳", image: "", parent: "2-1" },
                    { classId: "2-1-7", name: "手机贴膜", image: "", parent: "2-1" },
                    { classId: "2-1-8", name: "充电宝", image: "", parent: "2-1" },
                    { classId: "2-1-9", name: "自拍杆", image: "", parent: "2-1" },
                    { classId: "2-1-10", name: "自拍杆", image: "", parent: "2-1" },
                ]
            },
            {
                classId: "2-2", name: "Redmi 配件", image: "", parent: "2", children: [
                    { classId: "2-2-1", name: "充电器", image: "", parent: "2-2" },
                ]
            },
        ]
    },

])
const checkMainClass = ref()
checkMainClass.value = classList.value[0]
//切换分类
function handleClassCheck(item: any) {
    checkMainClass.value = item
    checkChildClass.value = item.children[0]
}
const checkChildClass = ref()
checkChildClass.value = checkMainClass.value.children[0]

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