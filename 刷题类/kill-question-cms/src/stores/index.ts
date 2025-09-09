/*
 * @Author: FengXue
 * @Date: 2024-04-09 09:26:49
 * @LastEditors: FengXue
 * @LastEditTime: 2024-05-15 10:44:42
 * @filePath: Do not edit
 */
import { defineStore } from "pinia";

export const useMainStore = defineStore("main", {
  state: () => {
    return {
      user: {},
      path: "/",
    }
  },
  actions: {
    setUser(val: any) {
      this.user = val
    },
    setPath(val: string) {
      this.path = val
    }
  },
  persist: {
    enabled: true
  },

})