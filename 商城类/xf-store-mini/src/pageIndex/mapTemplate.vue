<template>
  <div class="main">
<up-navbar
      title="地图"
      :autoBack="true"
    ></up-navbar>
    <map
      name="map"
      class="map"
      :markers="list"
      :longitude="contre.longitude"
      :latitude="contre.latitude"
    >
    </map>
  </div>
</template>

<script setup lang="ts">
const webViewStyles = ref({
  height: "90vh",
});
onMounted(() => {
});
//----------------------<zxzb-中心坐标>----------------------
// 26.065236,119.168623
const contre = ref({ longitude: "119.168623", latitude: "26.065236" });

</script>
<style lang="scss" scoped>
.main {
  padding: 0;
  position: relative;
}
.map {
  width: 100%;
  height: 100vh;
}
</style>
