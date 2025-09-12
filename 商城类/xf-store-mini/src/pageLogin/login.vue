<template>
  <div class="main">
    <div class="title">
      <div>欢迎使用</div>
      <div>****小程序！</div>
    </div>
    <div class="form">
      <up-form labelPosition="left" :model="user" :rules="rules" ref="form">
        <up-form-item prop="loginName">
          <up-input
            type="text"
            class="input"
            placeholder="账号"
            shape="circle"
            v-model="user.loginName"
            claerable
            :customStyle="{
              background: '#ECF2FF',
              padding: '10px 10px 10px 20px',
              width: '70%',
              margin: '0 auto',
            }"
            border="none"
          ></up-input>
        </up-form-item>
        <up-form-item prop="passWord">
          <up-input
            :type="isShowpassWord ? 'text' : 'password'"
            class="input"
            placeholder="密码"
            shape="circle"
            :focus="isShowpassWord"
            v-model="user.passWord"
            claerable
            :customStyle="{
              background: '#ECF2FF',
              padding: '10px 10px 10px 20px',
              width: '70%',
              margin: '0 auto',
            }"
            border="none"
          >
            <template #suffix>
              <up-icon
                v-if="!isShowpassWord"
                name="eye-off"
                @click.stop="changeShowpassWord"
                size="20"
              ></up-icon>
              <up-icon
                v-else
                name="eye-fill"
                @click.stop="changeShowpassWord"
                size="20"
              ></up-icon>
            </template>
          </up-input>
        </up-form-item>
      </up-form>
    </div>
    <div class="btn-full" @click="login">登录</div>
    <div class="btn-full nologin" @click="goRegister">无账号，前往申请 ></div>
  </div>
</template>
<script setup lang="ts">
onLoad((val: any) => {});
onMounted(() => {
  uni.login({
    "provider":"weixin",
    "onlyAuthorize":true,
    success(res){
      const {code} = res
      request('/fisherApi/wechatMiniProgram/getOpendIdAndUnionId',{jsCode:code},'POST').then(res=>{
      console.log('res',res)
        uni.setStorageSync("openId",res.openId)
      }).catch(err=>{
      console.log('err',err)
      })
      
    }
  })

});

const showNotification = ref(false);
const notification = ref("");

//----------------------<user-用户>----------------------
const user = ref({
  loginName: "",
  passWord: "",
  platformId: "",
});
//----------------------<jy-校验>----------------------
const rules = {
  loginName: {
    type: "string",
    required: true,
    message: "请填写账号",
    trigger: ["blur", "change"],
  },
  passWord: {
    type: "string",
    required: true,
    message: "请填写密码",
    trigger: ["blur", "change"],
  },
};
//----------------------<mmqh-密码切换>----------------------
const isShowpassWord = ref(false);
function changeShowpassWord() {
  isShowpassWord.value = !isShowpassWord.value;
}

//----------------------<ys-样式>----------------------
const style = ref({
  background: "#ECF2FF",
});
//----------------------<dl-登录>----------------------
const form = ref();
function login() {
  form.value.validate().then((valid) => {
    if (valid) {
      uni.showLoading({ title: "登录中" });
      uni
        .request({
          url: "http://10.8.131.221:50870/authPlatApi/authApi/user/auth/login",
          method: "POST",
          data: user.value,
        })
        .then((res) => {
          console.log("resl", res);
          uni.hideLoading();
          let data = res.data.data;
          if (res.data.rtnMsg == "成功") {
            util.toast("登录成功");
            uni.setStorageSync("user", data);
            util.relaunch("/pages/index/index")
          } else {
            util.toast(res.data.rtnMsg);
          }
        });
    }
  });
}

//----------------------<hqsf-获取身份>----------------------
function getIdentity() {

}
//----------------------<tzzc-跳转注册>----------------------
function goRegister(){
  util.go("/pageLogin/register")
}
</script>
<style scoped lang="scss">
.main {
  height: 100vh;
  background: rgba(250, 251, 255, 1);
  .title {
    /** 文本1 */
    font-size: 24px;
    font-weight: 400;
    letter-spacing: 0px;
    line-height: 43.44px;
    color: rgba(52, 57, 101, 1);
    margin: 10px 0 30px 13%;
  }

  .forget {
    width: 72%;
    margin: 0 auto;
    display: flex;
    justify-content: flex-end;

    .text {
      /** 文本1 */
      font-size: 12px;
      font-weight: 400;
      letter-spacing: -0.24px;
      line-height: 17.38px;
      text-decoration-line: underline;
      color: rgba(52, 57, 101, 0.5);
    }
  }

  .btn-full {
    width: 76%;
    height: 50px;
    border-radius: 50px;
    margin: 40px auto;
    box-sizing: border-box;
    background: #257cff;
    //box-shadow: 0px 7px 10px  rgba(0, 29, 176, 0.3);
    font-size: 16px;
    font-weight: 700;
    color: rgba(250, 251, 255, 1);
    display: flex;
    align-items: center;
    justify-content: center;
    &.nologin {
      color: #9096B2;
      background: #ECF2FF;
    }
  }


}
</style>
