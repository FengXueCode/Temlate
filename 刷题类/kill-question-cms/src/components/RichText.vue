<!--
 * @Author: FengXue
 * @Date: 2023-08-25 15:36:48
 * @LastEditors: FengXue
 * @LastEditTime: 2023-08-25 20:14:03
 * @filePath: Do not edit
-->
<template>
  <div style="border: 1px solid #ccc">
    <Toolbar
      style="border-bottom: 1px solid #ccc"
      :editor="editorRef"
      :defaultConfig="toolbarConfig"
    />
    <Editor
      style="height: 500px; overflow-y: hidden"
      v-model="content"
      :defaultConfig="editorConfig"
      @onCreated="handleCreated"
    />
  </div>
</template>

<script setup lang="ts">
import { useStore } from "vuex";
const store = useStore();
import Axios from "@/request/index";
const content = ref();
const props = defineProps({
  content: String,
});
let prop = toRefs(props);
watch(prop.content, (val) => {
  content.value = val;
});

let emits = defineEmits(["getHtml"]);
watch(content, (newv) => {
  emits("getHtml", newv);
});
// /**富文本 */
import "@wangeditor/editor/dist/css/style.css"; // 引入 css
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
const editorRef = shallowRef(); //编辑器实例
const toolbarConfig = {}; //工具栏配置
const editorConfig = { placeholder: "请输入内容...", MENU_CONF: {} }; //编辑器配置
editorConfig.MENU_CONF["uploadImage"] = {
  async customUpload(file: File, insertFn: InsertFnType) {
    let data = new FormData();
    data.append("file", file);
    Axios({
      method: "post",
      url: "/Editor/UploadImg",
      headers: {
        token: store.state.token,
      },
      data: data,
    })
      .then((res) => {
        console.log(res.data.data[0]);
        let src = store.state.baseUrl + res.data.data[0];
        insertFn(src, "", "");
      })
      .catch((err) => {});
  },
};
/**销毁实例 */
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});
/**初始化实例 */
const handleCreated = (editor: any) => {
  editorRef.value = editor; // 记录 editor 实例，重要！
};
</script>
<style lang="scss" scoped></style>
