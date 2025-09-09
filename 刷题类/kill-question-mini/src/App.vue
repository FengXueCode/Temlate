<script setup lang="ts">
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";
onLaunch(() => {
  console.log("App Launch");
  initUser()
});
onShow(() => {
  console.log("App Show");
});
onHide(() => {
  console.log("App Hide");
});
//--------------------<initUser-初始化用户>-----------------------
function initUser() {
  uni.login({
    provider: "weixin",
    success: function (res: any) {
      questionApi.initLogin(res.code).then((res) => {
        uni.setStorageSync("user", res);
      })
    }
  })
}
</script>
<style lang="scss">
@import "uview-plus/index.scss";
@import './static/css/style.scss';
@import "./static/css/unit.scss";
@import "./static/css/uview.scss";
</style>
