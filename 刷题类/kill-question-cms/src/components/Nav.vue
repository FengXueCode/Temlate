<!--
 * @Author: FengXue
 * @Date: 2023-08-15 10:42:23
 * @LastEditors: FengXue
 * @LastEditTime: 2024-05-15 14:08:17
 * @filePath: Do not edit
-->
<template>
  <el-menu class="head" :router="true" mode="horizontal" :ellipsis="false">
    <el-menu-item index="/">
      <div class="title">小闽助手Mate</div>
    </el-menu-item>
    <div class="flex-grow" />
    <div>
      <el-sub-menu index="/login">
        <template #title>{{ store.user.nickName }}</template>
        <el-menu-item index="/login"> 退出 </el-menu-item>
      </el-sub-menu>
    </div>
  </el-menu>
</template>

<script lang="ts" setup>
import { useMainStore } from "@/stores/index";
const store = useMainStore();
</script>

<style lang="scss">
.head {
  width: 100%;
  height: 60px;
  box-sizing: border-box;
  padding: 0 10px 0 10px;
  border-bottom: 1px solid var(--el-border-color);
  .title {
    font-size: 24px;
  }

  .flex-grow {
    flex-grow: 1;
  }
}
</style>
