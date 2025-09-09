<template>
  <div class="init">
    <view class="login">
      <div class="title">***题库管理平台</div>
      <el-form class="input" :model="login" :rules="rules" ref="form">
        <el-form-item label="账号" prop="account">
          <el-input v-model="login.account" placeholder="编号"></el-input>
        </el-form-item>
      </el-form>
      <div class="tip">在小程序我的中查看编号</div>
      <el-button type="primary" size="default" @click="onSubmit(form)">登录</el-button>
    </view>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, FormInstance, FormRules } from "element-plus";
import { useRouter } from "vue-router";
import { reactive } from "vue";
import { useMainStore } from "@/stores/index";
import Axios from "@/request";
const store = useMainStore();
const router = useRouter();
interface Login {
  account: string;
  password: string;
}
const login = reactive({
  account: "",
  password: "",
});
const form = ref<FormInstance>();
const rules = reactive<FormRules<Login>>({
  account: [{ required: true, message: "账号不能为空", trigger: "blur" } ],
});
const onSubmit = (formEl: FormInstance | undefined) => {
  if (!formEl) return;

  formEl.validate((valid) => {
    if (valid) {
      // login.password = Base64.encode(login.password);
      Axios({
        url: "/questionBank/login",
        params: login,
      })
        .then((res) => {
          if (res.data.code == 200) {
            store.setUser(res.data.data);
            //存入本地
            localStorage.setItem("user", JSON.stringify(res.data.data));
            router.push("/question/questionBank");
            ElMessage.success("登陆成功~");
          } else {
            ElMessage.error(res.data.msg);
            login.account = "";
            login.password = "";
          }
        })
        .catch(() => { });
    }
  });
};
</script>
<style lang="scss" scoped>
.init {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url("@/assets/bg.jpg");
  background-size: 100% 100%;

  .title {
    font-size: 20px;
  }

  .login {
    background: white;
    width: 300px;
    height: 300px;
    border-radius: 15px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-around;

    .logo {
      width: 100px;
      height: 100px;
      margin: 0;
    }
  }
}
</style>
