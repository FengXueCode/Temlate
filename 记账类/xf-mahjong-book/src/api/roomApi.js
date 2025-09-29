//--------------------<创建房间>-----------------------
function createRoom(mode,ratio) {
    return request("/room/create", {mode:mode,ratio:ratio}, 'GET', {})
}
//--------------------<获取房间>-----------------------
function getRoom() {
    return request("/room/getRoom", {}, 'GET', {})
}
//--------------------<根据房间id获取房间信息>-----------------------
function getRoomById(roomId) {
    return request("/room/getRoomById", { roomId: roomId }, 'GET', {})
}
//--------------------<获取房间用户>-----------------------
function getRoomUser(roomId) {
    return request("/room/getRoomUser", { roomId: roomId }, 'GET', {})
}
//--------------------<获取房间分享二维码>-----------------------
function getRoomQrCode(roomId) {
    return request("/room/getRoomQrCode", { roomId: roomId }, 'GET', {})
}
//--------------------<转账>-----------------------
function transfer(roomId, userId, amount) {
    return request("/room/transfer", { roomId: roomId, userId: userId, money: amount }, 'GET', {})
}
//--------------------<检查房间好友>-----------------------
function checkFriend(roomId) {
    return request("/room/checkFriend", { roomId: roomId }, 'GET', {})
}

//--------------------<修改茶水>-----------------------
function updateTea(room) {
    return request("/room/updateTea", room, 'POST', {})
}
//--------------------<修改好友备注>-----------------------
function updateFriendRemark(to, remark) {
    return request("/room/updateRemark", { to: to, remark: remark }, 'GET', {})
}
//--------------------<获取历史记录>-----------------------
function getHistory(roomId) {
    return request("/room/getHistory", { roomId: roomId }, 'GET', {})
}
//--------------------<获取房间茶水>-----------------------
function getRoomTea(roomId) {
    return request("/room/getTea",{roomId:roomId},'GET',{})
}
//--------------------<获取结算信息>-----------------------
function getSettlement(roomId) {
    return request("/room/getSettlement", { roomId: roomId }, 'GET', {})
}
export default {
    createRoom,
    getRoom,
    getRoomById,
    getRoomUser,
    getRoomQrCode,
    transfer,
    checkFriend,
    updateTea,
    updateFriendRemark,
    getHistory,
    getRoomTea,
    getSettlement
}