
export function go(url) {
  uni.navigateTo({
    url: url
  })
}
export function relaunch(url) {
  uni.reLaunch({
    url: url
  });
}
export function toast(value) {
  uni.showToast({
    title: value,
    icon: "none"
  })
}
export function back(delta = 1) {
  uni.navigateBack({
    delta: delta
  })
}
export function initNull(str, type = "string") {
  if (str == null || str == '' || str == undefined) {
    return type == 'string' ? "--" : type == 'number' ? 0 : "----/--/-- --:--:--"
  } else {
    return str
  }

}

export function formaDate(timer) {
  if(timer=="")return "----/--/-- --:--:--"
  timer = timer instanceof Date?timer:new Date(timer);
  const year = timer.getFullYear()
  const month = timer.getMonth() + 1 // 由于月份从0开始，因此需加1
  const day = timer.getDate()
  const hour = timer.getHours()
  const minute = timer.getMinutes()
  const second = timer.getSeconds()
  return `${pad(year, 4)}-${pad(month)}-${pad(day)} ${pad(hour)}:${pad(minute)}:${pad(second)}`
}
// 定义具体处理标准
// timeEl 传递过来具体的数值：年月日时分秒
// total 字符串总长度 默认值为2
// str 补充元素 默认值为"0"
function pad(timeEl, total = 2, str = '0') {
  return timeEl.toString().padStart(total, str)
}
export default {
  go,
  relaunch,
  toast,
  back,
  initNull,
  formaDate
};
