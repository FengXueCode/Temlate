//--------------------<初始化登录>-----------------------
function initLogin(code) {
    return request("/user/init", { code: code }, "GET", {})
}
//--------------------<获取用户信息>-----------------------
function getUserInfo() {
    return request("/user/getUser", {}, "GET", {})
}

export default {
    initLogin,
    getUserInfo
}