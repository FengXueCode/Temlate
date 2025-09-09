/*
 * @Author: FengXue
 * @Date: 2024-04-08 17:41:43
 * @LastEditors: FengXue
 * @LastEditTime: 2024-04-10 10:50:07
 * @filePath: Do not edit
 */
import { createApp } from 'vue'
import '@/static/css/style.scss'
import '@/static/css/element.scss'
import 'element-plus/theme-chalk/src/index.scss'
import App from './App.vue'
import router from "@/router/index"
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(ElementPlus, { locale: zhCn })
app.use(router)
app.use(pinia)
app.mount('#app')
