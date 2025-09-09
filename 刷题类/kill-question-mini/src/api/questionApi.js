//--------------------<初始化登录>-----------------------
function initLogin(code) {
    return request("/user/init", { code: code }, 'GET', {})
}
//--------------------<获取刷题记录>-----------------------
function getRecord() {
    return request("/questionRecord/findAll", {}, 'GET', {})
}

export default {
    initLogin,
    getRecord
}