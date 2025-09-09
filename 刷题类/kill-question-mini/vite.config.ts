import { defineConfig } from "vite";
import uni from "@dcloudio/vite-plugin-uni";
import AutoImport from 'unplugin-auto-import/vite'
import Components from "unplugin-vue-components/vite";
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [uni(),
  AutoImport({
    imports: [
      "vue",
      "uni-app",
      {
        //自定义的自动引入库
      }
    ],
    //本地目录 
    dirs: [
        "src/util",
        "src/api"
    ]
  }),
  Components({
    dirs: [
      "src/pages"
    ],
    dts: "src/components.d.ts",
    resolvers: [
      //自动导入的组件库

    ]
  })],
});
