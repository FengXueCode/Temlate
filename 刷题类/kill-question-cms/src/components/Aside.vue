<!--
 * @Author: FengXue
 * @Date: 2023-08-22 02:20:24
 * @LastEditors: FengXue
 * @LastEditTime: 2024-05-15 10:08:52
 * @filePath: Do not edit
-->

<template>
  <div class="aside">
    <el-menu
      :default-active="path"
      class="el-menu-vertical-demo"
      :collapse="isCollapse"
      :router="true"
      @select="handleSelect"
    >
      <div v-for="item in aside" :key="item.path">
        <el-menu-item :index="item.path" v-if="item.child.length == 0">
          <el-icon size="24px">
            <component :is="item.icon"></component>
          </el-icon>

          {{ item.title }}
        </el-menu-item>
        <el-sub-menu :index="item.path" v-else>
          <template #title>
            <el-icon size="24px">
              <component :is="item.icon"></component>
            </el-icon>
            {{ item.title }}</template
          >
          <el-menu-item :index="e.path" v-for="e in item.child">
            <el-icon size="24px">
              <component :is="e.icon"></component>
            </el-icon>
            {{ e.title }}
          </el-menu-item>
        </el-sub-menu>
      </div>
    </el-menu>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import aside from "@/static/menu/aside.js";
import { useMainStore } from "@/stores/index";
import { storeToRefs } from "pinia";
const store = useMainStore();
const { path } = storeToRefs(store);
const isCollapse = ref(false);
const handleSelect = (index: any) => {
  store.setPath(index);
};
</script>

<style scoped lang="scss">
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  height: 100vh;
  overflow-y: auto;
}
.aside {
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
