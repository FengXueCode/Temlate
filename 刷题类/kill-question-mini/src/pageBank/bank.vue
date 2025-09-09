<!--
 * @Author: FengXue
 * @Date: 2024-04-26 14:18:17
 * @LastEditors: FengXue
 * @LastEditTime: 2024-06-27 15:54:37
 * @filePath: Do not edit
-->
<template>
  <div class="main">
    <!-- 导航栏 -->
    <u-subsection mode="button" bg-color="#FFF" font-size="16px" active-color="#18b566" :list="states" :current="state"
      @change="changeState"></u-subsection>

    <!-- 列表 -->
    <div class="list">
      <div class="empty" v-if="list.length == 0">
        <u-empty mode="list"></u-empty>
      </div>
      <div class="item" v-for="(item, index) in list" @click="goDetail(item)" :key="index">
        <div class="content">
          <div class="text">
            <div class="title">{{ item.questionBank.questionBankName }}</div>
            <div class="author">
              共{{ item.total }}题/作者：{{ item.questionBank.author }}
            </div>
            <div class="infomation">
              做{{ item.questionRecord.accomplishArray.length }}题/错{{
                item.questionRecord.errorArray.length
              }}题/收藏{{ item.questionRecord.collectArray.length }}题
            </div>
          </div>
          <div class="hear">
            <img class="img" :src="item.portrait == null ? userImg : item.portrait" alt="" />
          </div>
        </div>
        <div class="line">
          <div class="success" :style="{
            width:
              ((item.questionRecord.accomplishArray.length -
                item.questionRecord.errorArray.length) /
                item.total) *
              100 +
              '%',
          }"></div>
          <div class="error" :style="{
            width:
              (item.questionRecord.errorArray.length / item.total) * 100 +
              '%',
          }"></div>
          <div class="none"></div>
        </div>
      </div>
    </div>
    <div class="btn-full" @click="addExams">添加词库</div>

  </div>
  <nav-bottom :checkNav="1"></nav-bottom>
</template>

<script setup lang="ts">
import userImg from "@/static/icon/my/user.png";
import { onShow } from "@dcloudio/uni-app";

const states = ref(["个人", "共享"]);
const state = ref(0); //0 个人 1共享
//切换状态
const changeState = (index) => {
  if (state.value == index) return;
  state.value = index;
  getList();
};

const list = ref([]);
const getList = () => {
  request("/questionBank/findByState", {
    state: state.value,
  }, 'GET', {})
    .then(res => {
      list.value = res;
      list.value.forEach((element) => {
        if (element.questionRecord == null) {
          element.questionRecord = {
            accomplishArray: [],
            errorArray: [],
            collectArray: [],
          };
        } else {
          element.questionRecord.accomplishArray = JSON.parse(
            element.questionRecord.accomplishArray,
          );

          element.questionRecord.collectArray = JSON.parse(
            element.questionRecord.collectArray,
          );
          element.questionRecord.errorArray = JSON.parse(
            element.questionRecord.errorArray,
          );
        }
      });
    })
    .catch(err => {
      console.log(err);
    })

};
const goDetail = (row: any) => {

  uni.navigateTo({
    url:
      "/pages/detail?item=" +
      encodeURIComponent(JSON.stringify(row)),
  });
};
const url = "http:你自己的后台url";
//添加词库
const addExams = () => {

  uni.showModal({
    title: "添加词库",
    content: "请到网页端添加：" + url + " ,确认后自动复制",
    success: (res) => {
      if (res.confirm) {
        uni.setClipboardData({
          data: url,
          success: () => {
            uni.showToast({
              title: "复制成功",
              icon: "none",
            });
          },
        });
      }
    },
  });
};
onShow(() => {
  getList();
});
</script>
<style lang="scss" scoped>
.main {
  .list {
    width: 100%;
    height: calc(100vh - 300px);
    overflow: auto;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;

    .item {
      width: 98%;
      height: 100px;
      box-shadow: $uni-border-shadow;
      margin-bottom: 20px;
      box-sizing: border-box;
      border-radius: 5px 5px 0 0;

      .content {
        padding: 5px;
        box-sizing: border-box;
        display: flex;
        width: 100%;
        height: 100%;
        color: $uni-text-color-grey;

        .text {
          width: 80%;
          height: 100%;
          font-size: 18px;
          display: flex;
          flex-direction: column;
          justify-content: center;

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
          // width: 50%;
          border-radius: 0 0 0 5px;
          background: #a5d63f;
        }

        .error {
          // width: 10%;
          background: #d43030;
        }

        .none {
          flex-grow: 1;
          background: #cccccc;
          border-radius: 0 0 5px 0;
        }
      }
    }
  }

  .btn-full {
    position: absolute;
    bottom: 100px;
  }
}
</style>
