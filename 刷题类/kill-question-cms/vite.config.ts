/*
 * @Author: FengXue
 * @Date: 2024-04-08 17:41:43
 * @LastEditors: FengXue
 * @LastEditTime: 2024-05-15 14:05:26
 * @filePath: Do not edit
 */
import { defineConfig } from 'vite'
import path from 'path'
import vue from '@vitejs/plugin-vue'
// import vueRouter from 'vite-plugin-vue-router'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import Icons from 'unplugin-icons/vite'
import IconsResolver from 'unplugin-icons/resolver'
// https://vitejs.dev/config/
// 设置别名指向src
const pathSrc = path.resolve(__dirname, 'src')
export default defineConfig({
  resolve: {
    alias: {
      '@': pathSrc,
    },
  },
  plugins: [
    vue(),
    AutoImport({
      imports: ["vue", "vue-router"],
      resolvers: [
        //自动导入ElementPlus 相关函数
        ElementPlusResolver(),
        //自动导入图标组件
        // IconsResolver({
        //   prefix: "Icon",
        // })
      ],
    }),
    Components({
      resolvers: [
        // 自动导入ElementPlus组件
        ElementPlusResolver(),
        // 自动注册图标组件
        // IconsResolver({
        //   enabledCollections: ['ep'],
        // })
      ],

    }),
    // Icons({
    //   autoInstall: true,
    // })
  ],
})
