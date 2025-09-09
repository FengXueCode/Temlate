<!--
 * @Author: FengXue
 * @Date: 2024-04-26 16:20:03
 * @LastEditors: FengXue
 * @LastEditTime: 2024-06-04 14:57:37
 * @filePath: Do not edit
-->
<template>
  <up-navbar title="巅峰榜单" :autoBack="true" ></up-navbar>
  <div class="main">
    <div class="titles">
      <div class="titles-item">排名</div>
      <div class="titles-item">用户名</div>
      <div class="titles-item">正确率</div>
      <div class="titles-item">用时</div>
    </div>
    <div class="list">
      <u-empty v-if="list.length == 0" mode="list"></u-empty>
      <div class="item" v-for="(item, index) in list" :key="item.peakednessId">
        <div class="item-text">{{ index + 1 }}</div>
        <div class="item-text">{{ item.user.nickName }}</div>
        <div class="item-text">
          {{ item.peak.correct }}/{{ item.peak.total }}
        </div>
        <div class="item-text">{{ item.peak.time }}</div>
      </div>
    </div>
    <div class="btn-full" @click="peak">昂也要赛！</div>
  </div>
</template>

<script setup lang="ts">
import { onLoad, onShow } from "@dcloudio/uni-app";

onLoad((e) => {
  questionBank.value = JSON.parse(decodeURIComponent(e.item));
});
onShow(() => {
  initData();
});
const peak = () => {
  uni.showModal({
    title: "参加巅峰赛",
    content: "用时越短、正确率也高、排名越高！",
    success: (res) => {
      if (res.confirm) {
        questionBank.value.isPeak = true;
        uni.navigateTo({
          url:
            "/pages/examsIng?questionBank=" +
            encodeURIComponent(JSON.stringify(questionBank.value)),
        });
      }
    },
  });
};
const list = ref([]);
const initData = () => {
  request("/questionBank/findPeak", {
    questionBankId: questionBank.value.questionBank.questionBankId,
  }, 'GET', {})
    .then(res => {
      list.value = res;
      list.value.forEach((element) => {
        element.peak.time = dateformat(element.peak.time);
      });
    })
    .catch(err => {
      console.log(err);
    })
  
};
const dateformat = (time) => {
  let h = Math.floor(time / 3600);
  let m = Math.floor((time - h * 3600) / 60);
  let s = time - h * 3600 - m * 60;
  let date = `${h < 10 ? "0" + h : h}:${m < 10 ? "0" + m : m}:${s < 10 ? "0" + s : s
    }`;
  return date;
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
</script>
<style lang="scss" scoped>
.main {
  .titles {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr 1fr;
    height: 20px;

    .titles-item {
      display: flex;
      justify-content: center;
      font-size: 14px;
      align-items: center;
    }
  }

  .list {
    width: 100%;
    height: 72vh;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    margin-top: 10px;
    border-radius: $uni-border-radius;
    box-shadow: $uni-border-shadow;
    padding: 10px;
    overflow: auto;

    .item {
      width: 100%;
      display: grid;
      grid-template-columns: 1fr 1fr 1fr 1fr;
      height: 44px;
      border-bottom: 1px solid $uni-border-color;

      .item-text {
        width: 100%;
        box-sizing: border-box;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        text-align: center;
        flex-wrap: wrap;
      }
    }
  }
}
</style>
