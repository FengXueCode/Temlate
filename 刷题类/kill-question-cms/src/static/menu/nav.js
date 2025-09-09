/*
 * @Author: FengXue
 * @Date: 2024-05-15 08:57:12
 * @LastEditors: FengXue
 * @LastEditTime: 2024-05-15 10:54:14
 * @filePath: Do not edit
 */

import { createPinia } from "pinia";
import { useMainStore } from "@/stores/index";
const pinia = createPinia();
const store = useMainStore(pinia);
const nav = {
  logo: {
    img: "",
    path: "/"
  },
  url: [
    {
      title: user.value.nickName,
      path: '/',
      icon: '',
      child: [
        {
          title: '退出',
          path: '/login',
          icon: '',
          child: []
        },
      ]
    },
  ]
}
export default nav