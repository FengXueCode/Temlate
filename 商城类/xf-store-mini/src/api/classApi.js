//--------------------<获取所有分类>-----------------------
function queryAll() {
    return request("/class/queryAll", {}, "GET", {})
}
//--------------------<获取顶层分类>-----------------------
function queryTop() {
    return request("/class/queryTop", {}, "GET", {})
}
export default {
    queryAll,
    queryTop
}