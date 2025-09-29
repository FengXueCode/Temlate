<!--
 * @Author: FengXue
 * @Date: 2024-03-12 22:04:52
 * @LastEditors: FengXue
 * @LastEditTime: 2024-06-11 11:29:05
 * @filePath: Do not edit
-->
<template>
  <div class="main">

    <up-navbar title="个人信息" :autoBack="true"></up-navbar>
    <div class="content">
      <div class="head">
        <button class="avatar" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
          <img class="img" :src="avatarUrl == null ? head : avatarUrl" alt="" />
        </button>
        <div class="tip">点击更换头像</div>
      </div>
      <up-cell title="昵称" class="cell">
        <template #value>
          <input type="nickname" v-model="nickName" placeholder="请输入昵称" clearable class="input" @input="getNickName"
            @blur="getNickName" />
        </template>

      </up-cell>
      <div class="tip">{{ tip }}</div>
    </div>
    <div class="btn-full" @click="save">保存</div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";

import head from "@/static/icon/user.png"
//获取用户id
const user = ref(uni.getStorageSync("user"))
onMounted(() => {
  nickName.value = user.value.nickName;
  avatarUrl.value = user.value.portrait;
});

//头像地址
const avatarUrl = ref<string>();
const isChangeAvatar = ref<boolean>(false);
// 上传头像
const onChooseAvatar = (e) => {
  avatarUrl.value = e.detail.avatarUrl;
  isChangeAvatar.value = true;
};
//昵称
const nickName = ref<string>();
const getNickName = (e) => {
  nickName.value = e.detail.value;
};
//提示
const tip = ref<string>();
const save = () => {
  if (nickName.value == null || nickName.value == "") {
    tip.value = "请输入昵称";
    return;
  }
  uni.showLoading({ title: "修改中..." })

  if (isChangeAvatar.value) {
    uni.uploadFile({
      url: import.meta.env.VITE_BASE_URL + "/user/update",
      filePath: avatarUrl.value,
      name: "file",
      header: {
        Authorization: uni.getStorageSync("user").token,
      },
      formData: {
        nickName: nickName.value,
      },
      success: (res) => {
        let data = JSON.parse(res.data);
        if (data.code == 200) {
          util.toast("保存成功")
          uni.hideLoading()
          user.nickname = data.data.nickname;
          user.portrait = data.data.portrait;
          uni.setStorageSync("user", user)
        }
        else {
          util.toast(data.msg)
        }
      },
      fail: (res) => {
        console.log("fail", res);
      },
    });
  } else {
    request('/user/update',
      {
        nickName: nickName.value,
      }, 'POST',
      {
        "Content-Type": "application/x-www-form-urlencoded",
      }).then(res => {
        console.log('res', res)
        user.value.nickName = nickName.value;
        uni.setStorageSync('userInfo', user.value)
        uni.showToast({
          title: "保存成功",
          icon: "none",
        });
        isChangeAvatar.value = false;
        tip.value = "";
        uni.hideLoading()
        setTimeout(() => {
          uni.navigateBack({
            success: () => {
              let pages = getCurrentPages();
              let beforePage = pages[pages.length - 1];
              if (beforePage.route == "pages/user/user") {
                beforePage.onLoad();
              }
            },
          });
        }, 500);
      }).catch(err => {
        console.log('err', err)
        util.toast(err.msg)
      })


  }
};
</script>
<style lang="scss" scoped>
.head {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;

  .avatar {
    width: 100px;
    height: 100px;
    padding: 0;

    .img {
      width: 100%;
      height: 100%;
    }
  }

  .tip {
    margin-top: 10px;
    color: $uni-text-color-grey !important;
  }
}

.content {
  width: 100%;
  box-sizing: border-box;
  // box-shadow: $uni-border-shadow;
  border-radius: 15px;
  height: 70vh;
  margin-top: 10px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

:deep(.u-cell) {
  width: 100%;
}
</style>
