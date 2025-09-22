/*
 * @Author: FengXue
 * @Date: 2024-07-17 09:05:20
 * @LastEditors: FengXue
 * @LastEditTime: 2024-07-17 10:08:07
 * @filePath: Do not edit
 */


function request(url, data = {}, method = "POST", header = {}) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: import.meta.env.VITE_BASE_URL + url,
      data: data,
      header: {
        token: uni.getStorageSync("user").token,
        ...header
      },
      method: method,
    }).then(res => {
      if (res.statusCode === 200) {
        if (res.data.data == undefined) {
          resolve(res.data)
          return
        }
        resolve(res.data);
      } else {
        reject(res.data)
      }
    }).catch(err => {
      console.log('响应失败结果', err)
      uni.showToast({
        title: '请求失败',
        icon: 'error',
      })
    })
  })
}
export default request;
