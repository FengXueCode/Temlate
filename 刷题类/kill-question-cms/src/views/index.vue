<!--
 * @Author: FengXue
 * @Date: 2024-04-09 15:04:48
 * @LastEditors: FengXue
 * @LastEditTime: 2024-05-14 16:34:50
 * @filePath: Do not edit
-->
<!--
 * @Author: FengXue
 * @Date: 2024-04-09 15:04:48
 * @LastEditors: FengXue
 * @LastEditTime: 2024-04-09 15:05:02
 * @filePath: Do not edit
-->
<template>
  <div class="main">
    <aside class="aside">
      <Aside></Aside>
    </aside>
    <section class="container">
      <header class="header">
        <Nav></Nav>
      </header>
      <section class="content">
        <div class="main-container">
          <router-view></router-view>
        </div>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts"></script>
<style lang="scss" scoped>
.main {
  width: 100vw;
  height: 100vh;
  display: flex;
  box-sizing: border-box;

  .container {
    display: flex;
    flex-direction: column;
    width: calc(100vw - 200px);
    height: 100vh;
    box-sizing: border-box;
    .tags {
      width: 100%;
      height: 40px;
      box-sizing: border-box;
      overflow-x: auto;
      border-bottom: 1px solid var(--el-border-color);
    }
    .content {
      width: 100%;
      height: calc(100vh - 60px);
      box-sizing: border-box;
      padding: 10px;
      background: #ecf5ff;
      .main-container {
        width: 100%;
        height: 100%;
        background: white;
      }
    }
  }
}
</style>
