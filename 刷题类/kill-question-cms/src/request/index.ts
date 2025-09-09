/*
 * @Author: FengXue
 * @Date: 2024-04-08 17:41:43
 * @LastEditors: FengXue
 * @LastEditTime: 2024-04-09 16:24:11
 * @filePath: Do not edit
 */
import axios from 'axios';
import { useMainStore } from "@/stores/index";

const Axios = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL,
});

Axios.interceptors.request.use((config) => {
  const store = useMainStore();
  if (store.user && store.user.token) {
    config.headers['authorization'] = store.user.token;
  }
  return config;
});

export default Axios;
