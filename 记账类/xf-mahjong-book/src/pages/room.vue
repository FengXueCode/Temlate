<template>
    <div class='main'>
        <up-navbar :title="`麻友记账(${room?.roomRatio}元局)`" leftIcon="">

        </up-navbar>
        <!-- 用户列表  -->

        <div class="mahjing" v-if="room.roomType === 0">
            <div class="container">
                <div class="table">
                    <div class="border">
                        <div class="icon">
                            雀
                        </div>
                        <div class="orientation east" :class="{isMe:currentRoomUser?.location == 0}">东</div>
                        <div class="orientation south" :class="{isMe:currentRoomUser?.location == 1}">南</div>
                        <div class="orientation west" :class="{isMe:currentRoomUser?.location == 2}">西</div>
                        <div class="orientation north" :class="{isMe:currentRoomUser?.location == 3}">北</div>
                    </div>
                    <div class="item" :class="'location' + item.location" v-for="item in userList" :key="item.userId"
                        @click="selectUserForTransfer(item)"
                        @longpress="room.roomType === 0 ? showSwitchSeatMenuModal(item) : showEditRemarkModal(item)">
                        <img :src="(item.portrait == null || item.portrait == '') ? head : item.portrait" alt=""
                            class="head">
                        <div class="info">
                            <div class="name">{{ item.nickname }}</div>
                            <div class="money" :class="{ minus: item.money < 0 }">{{ item.money }}分</div>
                        </div>
                    </div>


                    <!-- 添加空座位占位符 -->
                    <div class="item empty-seat" :class="'location' + (index - 1)"
                        :style="{ display: isSeatOccupied(index - 1) ? 'none' : 'flex' }" v-for="index in 4"
                        :key="'empty-' + index" @click="showSwitchSeatSelectionForEmpty(index - 1)">
                        +
                    </div>
                </div>
                <div class="tea" @click="showTeaSettings">
                    <div class="item">
                        <img src="@/static/icon/tea.svg" alt="" class="head">
                        <div class="info">
                            <div class="name">茶水</div>
                            <div class="money">{{ tea }}分</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="user-list" v-else>
            <div class="item-group">
                <div class="item" v-for="item in userList" :key="item.userId" @click="selectUserForTransfer(item)"
                    @longpress="room.roomType === 0 ? showSwitchSeatMenuModal(item) : showEditRemarkModal(item)">
                    <img :src="(item.portrait == null || item.portrait == '') ? head : item.portrait" alt=""
                        class="head">
                    <div class="info">
                        <div class="name">{{ item.nickname }}</div>
                        <div class="money" :class="{ minus: item.money < 0 }">{{ item.money }} 分</div>
                    </div>
                </div>

            </div>
            <div class="tea" @click="showTeaSettings">
                <div class="item">
                    <img src="@/static/icon/tea.svg" alt="" class="head">
                    <div class="info">
                        <div class="name">茶水</div>
                        <div class="money">{{ tea }} 分</div>
                    </div>
                </div>
            </div>
        </div>
        <!-- 历史记录 -->
        <div class="history" :class="{ isMahing: room.roomType === 0 }">
            <div class="item" v-for="(item, index) in history" :key="index">

                <div class="join" v-if="item.type === 0">{{ item.message }}</div>
                <div class="record" v-else>
                    <img v-if="item.recordType === 1"
                        :src="getUserPortrait(item.settlement) == null ? head : getUserPortrait(item.settlement)" alt="" class="head">

                    <img v-if="item.loser !== currentUser.userId && item.recordType === 0"
                        :src="getUserPortrait(item.loser) == null ? head : getUserPortrait(item.loser)" alt="" class="head">

                    <div class="info" :class="{ me: item.loser === currentUser.userId && item.recordType === 0 }">
                        <div class="time">{{ formaDate(item.createTime) }}</div>
                        <div class="message" v-if="item.recordType === 0">

                            <span class="red">{{
                                getUserNickname(item.loser)
                            }}</span>
                            向
                            <span class="red">{{ getUserNickname(item.winner) }}</span>
                            转账
                            <span class="red">{{ item.money }}</span>
                            分
                            <span v-if="item.tea != null && item.tea != 0">
                                ,支付茶水
                                <span class="red"> {{ item.tea }}</span>
                                分
                            </span>

                        </div>
                        <div class="message" v-else>
                            <span class="red">{{ getUserNickname(item.settlement) }}</span>
                            已结算,
                            <span class="red">{{ getUserNickname(item.loser) }}</span>
                            需要向
                            <span class="red">{{ getUserNickname(item.winner) }}</span>
                            支付
                            <span class="red">{{ Math.abs(item.money) * room.roomRatio }}</span>
                            元
                        </div>
                    </div>

                    <img v-if="item.loser === currentUser.userId && item.recordType === 0"
                        :src="getUserPortrait(item.loser) == null ? head : getUserPortrait(item.loser)" alt="" class="head">
                </div>

            </div>
        </div>
        <!-- 功能栏 -->
        <div class="tools">
            <div class="item" @click="showSettlementOptions">结算</div>
            <div class="item" @click="showGroupTransfer">群发</div>
            <div class="item" @click="getInvite">邀请</div>
            <div class="item">说明</div>
        </div>
        <!-- 邀请弹窗 -->
        <up-popup :show="showInvite" closeable :round="10" mode="center" @close="showInvite = false">
            <div class="invite">
                <div class="title">邀请</div>
                <div class="tip">可邀请好友微信扫码加入房间</div>
                <img :src="inviteImg" alt="" class="img">
                <up-button type="success" shape="circle">转发给好友</up-button>
            </div>

        </up-popup>

        <!-- 转账弹窗 -->
        <up-popup :show="showTransfer" closeable :round="10" mode="center" @close="showTransfer = false">
            <div class="transfer-modal">
                <div class="title">转账</div>
                <div class="transfer-info" v-if="transferToUser">
                    转账给: {{ transferToUser.nickname }}
                </div>
                <up-input v-model="transferAmount" type="digit" placeholder="请输入转账金额" />
                <up-button type="success" shape="circle" @click="sendTransfer">确认转账</up-button>
            </div>
        </up-popup>

        <!-- 结算选项弹窗 -->
        <up-popup :show="showSettlement" closeable :round="10" mode="center" @close="showSettlement = false">
            <div class="settlement-modal">
                <div class="title">结算方式</div>
                <div class="option personal" @click="confirmPersonalSettlement">
                    <div class="option-title">个人结算</div>
                    <div class="option-desc">仅结算自己的账单，房间其他人可以继续正常使用</div>
                </div>
                <div class="option room" @click="confirmRoomSettlement">
                    <div class="option-title">房间结算</div>
                    <div class="option-desc">完全结算房间，确认之后房间其它成员将无法使用房间</div>
                </div>
            </div>
        </up-popup>

        <!-- 个人结算确认弹窗 -->
        <up-modal :show="showPersonalConfirm" title="个人结算确认" content="个人结算仅结算自己的账单，房间其他人可以继续正常使用，是否确认结算？"
            @confirm="performPersonalSettlement" @cancel="showPersonalConfirm = false"
            :showCancelButton="true"></up-modal>

        <!-- 房间结算确认弹窗 -->
        <up-modal :show="showRoomConfirm" title="房间结算确认" content="房间结算将完全结算房间，确认之后房间其它成员将无法使用房间，请和他人确认后再结算喔"
            @confirm="performRoomSettlement" @cancel="showRoomConfirm = false" :showCancelButton="true"></up-modal>

        <!-- 茶水设置弹窗 -->
        <up-popup :show="showTeaSetting" closeable :round="10" mode="center" @close="showTeaSetting = false">
            <div class="tea-setting-modal">
                <div class="title">茶水设置</div>

                <!-- 茶水模式选择 -->
                <div class="mode-selection">
                    <div class="label">茶水模式</div>
                    <div class="options">
                        <div class="option" :class="{ active: teaMode === 1 }" @click="teaMode = 1">
                            按比例抽成
                        </div>
                        <div class="option" :class="{ active: teaMode === 2 }" @click="teaMode = 2">
                            满减模式
                        </div>
                    </div>
                </div>

                <!-- 模式1: 按比例抽成 -->
                <div v-if="teaMode === 1" class="mode-content">
                    <div class="slider-container">
                        <div class="label">抽成比例: {{ teaRate }}%</div>
                        <slider :value="teaRate" :min="0" :max="100" :step="1" @changing="handleRate" :block-size="16"
                            activeColor="#007aff"></slider>
                    </div>
                </div>

                <!-- 模式2: 满减模式 -->
                <div v-if="teaMode === 2" class="mode-content">
                    <div class="input-group">
                        <div class="label">满</div>
                        <up-input v-model="teaFullAmount" type="digit" placeholder="请输入满减金额" />
                    </div>
                    <div class="input-group">
                        <div class="label">减</div>
                        <up-input v-model="teaDiscountAmount" type="digit" placeholder="请输入减免金额" />
                    </div>
                </div>

                <!-- 茶水上限设置 -->
                <div class="input-group">
                    <div class="label">茶水上限 (分)</div>
                    <up-input v-model="teaMax" type="digit" placeholder="请输入茶水上限，0为无上限" />
                </div>

                <!-- 确认按钮 -->
                <up-button type="success" shape="circle" @click="saveTeaSettings">保存设置</up-button>
            </div>
        </up-popup>

        <!-- 群发转账弹窗 -->
        <up-popup :show="showGroupTransferModal" closeable :round="10" mode="center"
            @close="showGroupTransferModal = false">
            <div class="group-transfer-modal">
                <div class="title">群发转账</div>

                <!-- 用户列表 -->
                <div class="user-list">
                    <div class="user-item" v-for="(user, index) in otherUsers" :key="user.userId">
                        <div class="user-info">
                            <img :src="user.portrait || head" alt="" class="head">
                            <div class="name">{{ user.nickname }}</div>
                        </div>

                        <up-input v-model="groupTransferAmounts[index]" type="digit" placeholder="请输入金额"
                            class="amount-input" />
                    </div>
                </div>

                <!-- 功能按钮 -->
                <div class="group-buttons">
                    <up-button type="primary" shape="circle" @click="syncAmounts">同步金额</up-button>
                    <up-button type="success" shape="circle" @click="confirmGroupTransfer">确认转账</up-button>
                </div>
            </div>
        </up-popup>

        <!-- 修改备注弹窗 -->
        <up-popup :show="showEditRemark" closeable :round="10" mode="center" @close="showEditRemark = false">
            <div class="edit-remark-modal">
                <div class="title">修改备注</div>
                <div class="user-info">
                    <img :src="selectedUserForRemark.portrait || head" alt="" class="head">
                    <div class="nickname">{{ selectedUserForRemark.nickname }}</div>
                </div>
                <up-input v-model="remark" placeholder="请输入备注名称" />
                <up-button type="success" shape="circle" @click="updateRemark">修改</up-button>
            </div>
        </up-popup>

        <!-- 切换座位菜单弹窗 -->
        <up-popup :show="showSwitchSeatMenu" closeable :round="10" mode="center" @close="showSwitchSeatMenu = false">
            <div class="switch-seat-menu-modal">
                <div class="title">操作选项</div>
                <div class="menu-options">
                    <div class="menu-item" @click="confirmSeatChange">
                        <div class="menu-text">切换座位</div>
                    </div>
                    <div class="menu-item" @click="showEditRemarkModalFromMenu">
                        <div class="menu-text">修改备注</div>
                    </div>
                </div>
                <up-button type="primary" shape="circle" @click="showSwitchSeatMenu = false">取消</up-button>
            </div>
        </up-popup>

    </div>
</template>
<script setup lang='ts'>
import head from "@/static/icon/user.png"
onLoad((val: any) => {
    roomId.value = val.scene ? val.scene : val.roomId
    if (val.isNew) {
        getInvite()
    }
});
onShow(() => {
    connectRoom()
    getRoomById()
    getHistory()
})
onMounted(() => {
})
onUnload(() => {
    socketTask.value.close();
})
onHide(() => {
    socketTask.value.close();
})
const roomId = ref("")
//--------------------<获取房间信息>-----------------------
const room = ref({
    roomType: 0
})
function getRoomById() {
    roomApi.getRoomById(roomId.value).then(res => {
        room.value = res
        teaMode.value = res.teaType === 0 ? 1 : res.teaType;
        teaRate.value = res.teaType === 0 ? 0 : res.ratio;
        teaFullAmount.value = res.teaFull;
        teaDiscountAmount.value = res.teaMinus;
        teaMax.value = res.teaLimit;
    })
}

//--------------------<连接房间>-----------------------
const socketTask = ref(null)
const user = uni.getStorageSync("user")
function connectRoom() {
    socketTask.value = uni.connectSocket({
        url: import.meta.env.VITE_SOCKET_URL + `/websocket/${roomId.value}/${user.userId}`,
        complete: () => { }
    })

    socketTask.value.onMessage((res: any) => {
        if (res.data.includes('加入')) {
            let item = {
                type: 0,///0:加入，1:转账记录
                message: res.data
            }
            //从头插入
            history.value.unshift(item)
            checkFriend()
        }
        if (res.data.includes('转账记录') || res.data.includes('个人结算')) {

            let record = JSON.parse(res.data.split('=')[1])
            history.value.unshift(record)
        }

        switch (res.data) {
            case '刷新用户数据':
                getRoomUser()
                getRoomTea()
                break;
            case '房间结算':
                util.relaunch("/pages/settlement?isRoom=true&roomId=" + roomId.value)
                break;
            case '房间已满':
                util.toast('房间已满,请让其先结算')
                setTimeout(() => {
                    util.relaunch('/pages/index')
                }, 1500);
                break;

            default:
                break;
        }
    })
}
//检查好友
function checkFriend() {
    roomApi.checkFriend(roomId.value).then((res) => {
    })
}
//--------------------<获取用户数据>-----------------------
const userList = ref([])
const fullUserList = ref([])
const currentRoomUser = ref()
function getRoomUser() {
    roomApi.getRoomUser(roomId.value).then((res) => {
        fullUserList.value = res;
        currentRoomUser.value = res.find(item => item.userId == currentUser.userId)
        userList.value = res.filter(item => item.status === 1)
    })
}
function getUserNickname(userId: string) {
    let user = fullUserList.value.find(item => item.userId == userId)
    if (user == undefined) return null
    return user.nickname
}
function getUserPortrait(userId: string) {
    let user = fullUserList.value.find(item => item.userId == userId)
    if (user == undefined) return null
    return user.portrait;
}
//--------------------<操作记录>-----------------------
const history = ref([])
function getHistory() {
    roomApi.getHistory(roomId.value).then((res) => {
        history.value = res
    })
}
//--------------------<邀请用户>-----------------------
const showInvite = ref(false)
const inviteImg = ref("")
function getInvite() {
    if (inviteImg.value != '') {
        showInvite.value = true
        return
    }
    uni.showLoading({
        title: "加载图片中..."
    })
    uni.request({
        url: import.meta.env.VITE_BASE_URL + '/room/getRoomQrCode',
        method: 'GET',
        data: {
            roomId: roomId.value
        },
        header: {
            Authorization: uni.getStorageSync("user").token,
        },
        responseType: 'arraybuffer',
        success: (res) => {
            let base64 = uni.arrayBufferToBase64(res.data);
            inviteImg.value = 'data:image/png;base64,' + base64;
            showInvite.value = true;
        },
        fail(err) {
            console.log("🚀 ~ fail ~ err:", err)
            inviteImg.value = ""
        },
        complete() {
            uni.hideLoading()
        }
    })
}

//--------------------<转账功能>-----------------------
const showTransfer = ref(false)
const transferToUser = ref(null)
const transferAmount = ref('')
const formaDate = util.formaDate;


// 选择转账用户（点击用户头像时调用）
function selectUserForTransfer(user) {
    if (user.userId === uni.getStorageSync("user").userId) {
        uni.showToast({
            title: '不能转账给自己',
            icon: 'none'
        })
        return
    }
    transferToUser.value = user
    showTransfer.value = true
}

// 发送转账请求

const currentUser = uni.getStorageSync("user");
function sendTransfer() {
    if (!transferAmount.value || parseFloat(transferAmount.value) <= 0) {
        uni.showToast({
            title: '请输入正确的金额',
            icon: 'none'
        })
        return
    }

    if (!transferToUser.value) {
        uni.showToast({
            title: '请选择转账用户',
            icon: 'none'
        })
        return
    }

    // 通过WebSocket发送转账消息
    const transferData = {
        from: currentUser.userId,
        to: transferToUser.value.userId,
        amount: parseFloat(transferAmount.value),
    };



    socketTask.value.send({
        data: '转账:' + JSON.stringify(transferData),
        success: () => {
            console.log('转账消息发送成功');
            uni.showToast({
                title: '转账成功',
                icon: 'success'
            });
            // 关闭转账窗口并重置数据
            showTransfer.value = false;
            transferAmount.value = '';
            transferToUser.value = null;
        },
        fail: (err) => {
            console.error('转账消息发送失败:', err);
            uni.showToast({
                title: '转账失败,请重试~',
                icon: 'none'
            });
            if (err.errMsg == 'SocketTask.send:fail SocketTask.readyState is not OPEN') {
                connectRoom()
            }
        }
    });
}

//--------------------<结算功能>-----------------------
const showSettlement = ref(false)
const showPersonalConfirm = ref(false)
const showRoomConfirm = ref(false)

// 显示结算选项弹窗
function showSettlementOptions() {
    showSettlement.value = true
}

// 确认个人结算
function confirmPersonalSettlement() {
    showSettlement.value = false
    showPersonalConfirm.value = true
}

// 确认房间结算
function confirmRoomSettlement() {
    showSettlement.value = false
    showRoomConfirm.value = true
}

// 执行个人结算
function performPersonalSettlement() {
    showPersonalConfirm.value = false
    uni.showLoading({
        title: "结算中..."
    })
    socketTask.value.send({
        data: "个人结算:userId=" + currentUser.userId,
        success: function (res) {
            console.log("个人结算成功")
            uni.hideLoading()
            util.relaunch("/pages/settlement?isRoom=false&roomId=" + roomId.value)

        },
        fail: function (err) {
            if (err.errMsg == 'SocketTask.send:fail SocketTask.readyState is not OPEN') {
                connectRoom()
                setTimeout(() => {
                    performPersonalSettlement()
                }, 1000);
            }
        }
    })
}

// 执行房间结算
function performRoomSettlement() {
    showRoomConfirm.value = false
    uni.showLoading({
        title: "结算中..."
    })
    socketTask.value.send({
        data: "房间结算",
        success: function (res) {
            uni.hideLoading()
        },
        fail: function (err) {
            if (err.errMsg == 'SocketTask.send:fail SocketTask.readyState is not OPEN') {
                connectRoom()
                setTimeout(() => {
                    performPersonalSettlement()
                }, 1000);
            }
        }
    })
}

//--------------------<茶水设置功能>-----------------------
const tea = ref(0)
const showTeaSetting = ref(false)
const teaMode = ref(1) // 1: 按比例抽成, 2: 满减模式
const teaRate = ref(0) // 抽成比例 (百分比)
const teaFullAmount = ref('100') // 满减的满金额
const teaDiscountAmount = ref('10') // 满减的减金额
const teaMax = ref('0') // 茶水上限，0表示无上限
// 获取房间茶水
function getRoomTea() {
    roomApi.getRoomTea(roomId.value).then(res => {
        tea.value = res
    })
}

// 显示茶水设置弹窗
function showTeaSettings() {
    showTeaSetting.value = true
}
//设置比例
function handleRate(value: any) {
    teaRate.value = value.detail.value
}

// 保存茶水设置
function saveTeaSettings() {
    // 验证输入
    if (teaMode.value === 1) {
        // 按比例抽成模式
        if (teaRate.value < 0 || teaRate.value > 100) {
            uni.showToast({
                title: '抽成比例应在0-100之间',
                icon: 'none'
            })
            return
        }
    } else if (teaMode.value === 2) {
        // 满减模式
        if (!teaFullAmount.value || parseFloat(teaFullAmount.value) <= 0) {
            uni.showToast({
                title: '请输入正确的满金额',
                icon: 'none'
            })
            return
        }
        if (!teaDiscountAmount.value || parseFloat(teaDiscountAmount.value) <= 0) {
            uni.showToast({
                title: '请输入正确的减金额',
                icon: 'none'
            })
            return
        }
        if (parseFloat(teaDiscountAmount.value) >= parseFloat(teaFullAmount.value)) {
            uni.showToast({
                title: '减免金额应小于满金额',
                icon: 'none'
            })
            return
        }
    }

    // 验证茶水上限
    if (teaMax.value && parseFloat(teaMax.value) < 0) {
        uni.showToast({
            title: '茶水上限不能为负数',
            icon: 'none'
        })
        return
    }
    room.value.teaType = teaMode.value;
    room.value.ratio = teaRate.value;
    room.value.teaFull = teaFullAmount.value;
    room.value.teaMinus = teaDiscountAmount.value;
    room.value.teaLimit = teaMax.value;
    roomApi.updateTea(room.value).then((res) => {
        util.toast('设置成功')
        // 关闭弹窗
        showTeaSetting.value = false
    })
}

//--------------------<群发转账功能>-----------------------
const showGroupTransferModal = ref(false)
const groupTransferAmounts = ref([])

// 显示群发转账弹窗
function showGroupTransfer() {
    showGroupTransferModal.value = true
    // 初始化每个用户的转账金额为空
    groupTransferAmounts.value = Array(otherUsers.value.length).fill('')
}

// 获取除自己外的其他用户
const otherUsers = computed(() => {
    return userList.value.filter(user => user.userId !== currentUser.userId);
})

// 同步金额（将第一个用户的金额同步给所有用户）
function syncAmounts() {
    if (groupTransferAmounts.value.length > 0 && groupTransferAmounts.value[0] !== '') {
        const firstAmount = groupTransferAmounts.value[0];
        groupTransferAmounts.value = Array(otherUsers.value.length).fill(firstAmount);

    } else {
        uni.showToast({
            title: '请先输入第一个用户的金额',
            icon: 'none'
        })
    }
}

// 确认群发转账
function confirmGroupTransfer() {
    // 检查是否有输入金额
    const hasAmount = groupTransferAmounts.value.some(amount => amount && parseFloat(amount) > 0);
    if (!hasAmount) {
        uni.showToast({
            title: '请至少为一个用户输入金额',
            icon: 'none'
        })
        return;
    }

    // 检查是否所有输入都是有效数字
    for (let i = 0; i < groupTransferAmounts.value.length; i++) {
        const amount = groupTransferAmounts.value[i];
        if (amount && (isNaN(parseFloat(amount)) || parseFloat(amount) <= 0)) {
            uni.showToast({
                title: `用户${otherUsers.value[i].nickname}的金额输入不正确`,
                icon: 'none'
            })
            return;
        }
    }


    // 发送转账消息
    const transferRequests = [];
    for (let i = 0; i < groupTransferAmounts.value.length; i++) {
        const amount = groupTransferAmounts.value[i];
        if (amount && parseFloat(amount) > 0) {
            const user = otherUsers.value[i];
            const transferData = {
                from: currentUser.userId,
                to: user.userId,
                amount: parseFloat(amount),
            };

            transferRequests.push(new Promise((resolve, reject) => {
                socketTask.value.send({
                    data: '转账:' + JSON.stringify(transferData),
                    success: resolve,
                    fail: reject
                });
            }));
        }
    }

    // 等待所有转账完成
    Promise.all(transferRequests).then(() => {
        uni.showToast({
            title: '群发转账成功',
            icon: 'success'
        });
        showGroupTransferModal.value = false;
        groupTransferAmounts.value = [];
    }).catch((err) => {
        console.error('群发转账失败:', err);
        uni.showToast({
            title: '转账失败，请重试',
            icon: 'none'
        });
        if (err.errMsg == 'SocketTask.send:fail SocketTask.readyState is not OPEN') {
            connectRoom()
        }
    });
}

//--------------------<修改备注功能>-----------------------
const showEditRemark = ref(false)
const selectedUserForRemark = ref({})
const remark = ref('')

// 显示修改备注弹窗
function showEditRemarkModal(user) {
    selectedUserForRemark.value = user
    remark.value = user.remark || user.nickname
    showEditRemark.value = true
}

// 更新备注
function updateRemark() {
    if (!remark.value) {
        uni.showToast({
            title: '请输入备注名称',
            icon: 'none'
        })
        return
    }

    roomApi.updateFriendRemark(selectedUserForRemark.value.userId, remark.value).then(res => {
        getRoomUser()
    })

    showEditRemark.value = false
    remark.value = ''
}

//--------------------<切换座位功能>-----------------------
const showSwitchSeatMenu = ref(false) // 显示切换座位菜单
const selectedUserForSeat = ref({}) // 要切换座位的用户
const selectedSeat = ref(-1) // 选中的座位

// 显示切换座位菜单
function showSwitchSeatMenuModal(user) {
    if (user.userId == currentUser.userId) return
    selectedUserForSeat.value = user
    selectedSeat.value = user.location
    showSwitchSeatMenu.value = true
}



// 从菜单显示修改备注弹窗
function showEditRemarkModalFromMenu() {
    showSwitchSeatMenu.value = false
    showEditRemarkModal(selectedUserForSeat.value)
}

// 检查座位是否被占用
function isSeatOccupied(location: number) {
    return userList.value.some(user => user.location === location)
}

// 点击空座位时直接显示座位选择弹窗
function showSwitchSeatSelectionForEmpty(location: number) {
    const seatData = {
        userId: currentUser.userId,
        toUserId: "",
        newLocation: location
    }
    uni.showModal({
        title: "换位确认",
        content: `是否切换到${location === 0 ? '东' : location === 1 ? '南' : location === 2 ? '西' : '北'}方位`,
        success(res) {
            if (res.confirm) {
                socketTask.value.send({
                    data: '切换座位=' + JSON.stringify(seatData),
                    success: () => {
                        console.log('切换座位消息发送成功')
                        uni.showToast({
                            title: '座位切换成功',
                            icon: 'success'
                        })
                        showSwitchSeatMenu.value = false
                    },
                    fail: (err) => {
                        console.error('切换座位消息发送失败:', err)
                        uni.showToast({
                            title: '切换座位失败,请重试~',
                            icon: 'none'
                        })
                        if (err.errMsg == 'SocketTask.send:fail SocketTask.readyState is not OPEN') {
                            connectRoom()

                        }
                    }
                })
            }
        }
    })
}

// 确认切换座位
function confirmSeatChange() {

    // 发送切换座位请求
    const seatData = {
        userId: currentUser.userId,
        toUserId: selectedUserForSeat.value.userId,
        newLocation: selectedSeat.value
    }
    uni.showModal({
        title: "换位确认",
        content: `当前座位已被【${selectedUserForSeat.value.nickname}】占用，是否和Ta交互座位？`,
        success(res) {
            if (res.confirm) {
                socketTask.value.send({
                    data: '切换座位=' + JSON.stringify(seatData),
                    success: () => {
                        console.log('切换座位消息发送成功')
                        uni.showToast({
                            title: '座位切换成功',
                            icon: 'success'
                        })
                        showSwitchSeatMenu.value = false
                    },
                    fail: (err) => {
                        console.error('切换座位消息发送失败:', err)
                        uni.showToast({
                            title: '切换座位失败,请重试~',
                            icon: 'none'
                        })
                        if (err.errMsg == 'SocketTask.send:fail SocketTask.readyState is not OPEN') {
                            connectRoom()
                        }
                    }
                })
            }
        }
    })


}
</script>
<style scoped lang='scss'>
.main {
    padding-left: 16px;
    padding-right: 16px;
    box-sizing: border-box;
    height: 100vh;
}

.mahjing {
    box-shadow: $uni-border-shadow;
    //长宽比1:1
    width: 100%;
    height: 28vh;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;

    .container {
        width: 95%;
        height: 95%;
        border-radius: 10px;
        background: #E6F3EF;
        display: flex;
        align-items: center;
        justify-content: center;
        position: relative;

        .table {
            width: 120px;
            height: 120px;
            background: #018A6F;
            border: 10px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;

            .border {
                width: 80%;
                height: 80%;
                border: 1px solid $uni-border-color;
                border-radius: 6px;
                display: flex;
                align-items: center;
                justify-content: center;
                position: relative;

                .orientation {
                    font-size: 11px;
                    padding: 3px;
                    background: white;
                    border-radius: 3px;
                    position: absolute;

                    &.east {
                        right: 6px;
                    }

                    &.south {
                        bottom: 6px;
                    }

                    &.west {
                        left: 6px;
                    }

                    &.north {
                        top: 6px;
                    }
                    &.isMe {
                        color: white;
                        background: $uni-color-zt;
                    }

                }

                .icon {
                    font-weight: bold;
                    width: 28px;
                    height: 28px;
                    font-size: 14px;
                    background: white;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    border-radius: 50%;
                    color: #018D71;
                }
            }
        }

        .tea {
            top: 10px;
            right: 100px;
            position: absolute;
        }
    }

    .item {
        position: absolute;
        background: white;
        opacity: 0.9;
        padding: 10px;
        display: flex;
        align-items: center;
        border-radius: 10px;

        &.location0 {
            right: -83px;
        }

        &.location1 {
            bottom: -45px;
        }

        &.location2 {
            left: -83px;
        }

        &.location3 {
            top: -45px;
        }

        .head {
            width: 30px;
            height: 30px;
            border-radius: 50%;
        }

        .info {
            display: flex;
            flex-direction: column;
            align-items: center;
            font-size: 12px;

            .name {
                width: 40px;
                //单行省略号
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                text-align: center;
            }

            .money {
                color: $uni-color-error;

                &.minus {
                    color: $uni-color-warning;
                }

            }
        }

    }

    .empty-seat {
        width: 30px;
        height: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        font-weight: bold;
        color: #018A6F;

        &.location2 {
            left: -43px;
        }

        &:hover {
            opacity: 1;
        }
    }


}

.user-list {
    width: 100%;
    height: 128px;
    box-sizing: border-box;
    box-shadow: $uni-border-shadow;
    display: flex;
    border-radius: 10px;
    padding: 10px;
    justify-content: space-between;

    .item-group {
        flex-grow: 1;
        display: flex;
        justify-content: flex-start;
        overflow-x: auto;
    }

    .item {
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        font-size: 14px;
        margin: 0 6px;
        cursor: pointer;

        .head {
            width: 55px;
            height: 55px;

            border-radius: 50%;
        }

        .info {
            text-align: center;

            .name {
                font-size: 12px;
                width: 55px;
                margin: 10px 0;
                //单行省略号
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }

            .money {
                color: red;

                &.minus {
                    color: $uni-color-warning;
                }

            }
        }
    }
}

.history {
    width: 100%;
    height: calc(100vh - 88px - 128px - 20px - 68px);
    box-shadow: $uni-border-shadow;
    margin: 10px 0;
    border-radius: 10px;
    overflow-y: auto;
    padding: 10px;
    box-sizing: border-box;

    &.isMahing {
        height: calc(100vh - 88px - 20px - 68px - 28vh);
    }

    .item {
        margin-bottom: 10px;
    }

    .join {
        width: 60%;
        border-radius: 10px;
        padding: 10px;
        text-align: center;
        margin: 0 auto;
        font-size: 14px;
        background: $uni-bg-color-grey;
    }

    .record {
        display: flex;

        .head {
            width: 50px;
            height: 50px;
            min-width: 50px;
            min-height: 50px;
            border-radius: 50%;
        }

        .info {

            display: flex;
            flex-grow: 1;
            flex-direction: column;
            justify-content: space-around;
            margin-left: 10px;
            font-size: 14px;

            &.me {
                text-align: end;
                margin-left: unset;
                margin-right: 10px;
            }

            .time {
                font-size: 12px;
                color: $uni-text-color-grey;
            }

            .red {
                color: red;
            }
        }
    }
}

.tools {
    width: 100vw;
    height: 68px;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    align-items: center;
    box-shadow: 0 0 2px #A3A6AD;
    background: white;
    position: absolute;
    bottom: 0;
    left: 0;
    grid-gap: 10px;
    box-sizing: border-box;
    padding: 0 16px;

    .item {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 10px;
        box-shadow: $uni-border-shadow;
        border-radius: 6px;
        cursor: pointer;
    }
}

.invite {
    width: 90vw;
    height: 60vh;
    background: white;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-around;
    border-radius: 10px;

    .title {
        font-size: 20px;
        font-weight: bold;
    }

    .img {
        width: 300px;
        height: 300px;
    }

    .tip {
        color: $uni-text-color-grey;
    }

    :deep(.u-button) {
        width: 80%;
        height: 40px;
    }
}

.transfer-modal {
    width: 80vw;
    padding: 20px;
    background: white;
    border-radius: 10px;
    display: flex;
    flex-direction: column;
    gap: 15px;

    .title {
        font-size: 20px;
        font-weight: bold;
        text-align: center;
    }

    .transfer-info {
        text-align: center;
        font-size: 16px;
        color: #333;
    }

    :deep(.u-input) {
        border: 1px solid #e0e0e0;
        border-radius: 6px;
        padding: 5px 10px;
    }

    :deep(.u-button) {
        margin-top: 10px;
    }
}

.settlement-modal {
    width: 80vw;
    padding: 20px;
    background: white;
    border-radius: 10px;

    .title {
        font-size: 20px;
        font-weight: bold;
        text-align: center;
        margin-bottom: 20px;
    }

    .option {
        padding: 15px;
        border-radius: 8px;
        margin-bottom: 15px;
        cursor: pointer;
        transition: background-color 0.3s;

        &.personal {
            background-color: #e3f2fd;

            .option-title {
                color: #1976d2;
                font-weight: bold;
                font-size: 18px;
                margin-bottom: 5px;
            }
        }

        &.room {
            background-color: #ffebee;

            .option-title {
                color: #d32f2f;
                font-weight: bold;
                font-size: 18px;
                margin-bottom: 5px;
            }
        }

        .option-desc {
            font-size: 14px;
            color: #666;
        }

        &:last-child {
            margin-bottom: 0;
        }

        &:hover {
            opacity: 0.8;
        }
    }
}

.tea-setting-modal {
    width: 80vw;
    padding: 20px;
    background: white;
    border-radius: 10px;

    .title {
        font-size: 20px;
        font-weight: bold;
        text-align: center;
        margin-bottom: 20px;
    }

    .mode-selection {
        margin-bottom: 20px;

        .label {
            font-weight: bold;
            margin-bottom: 10px;
        }

        .options {
            display: flex;
            gap: 10px;

            .option {
                flex: 1;
                text-align: center;
                padding: 10px;
                border: 1px solid #e0e0e0;
                border-radius: 6px;
                cursor: pointer;

                &.active {
                    border-color: #007AFF;
                    background-color: #e3f2fd;
                    color: #007AFF;
                }
            }
        }
    }

    .mode-content {
        margin-bottom: 20px;
        padding: 15px;
        background-color: #f5f5f5;
        border-radius: 6px;

        .slider-container {
            .label {
                margin-bottom: 10px;
                font-weight: bold;
            }

            :deep(.u-slider) {
                margin-top: 10px;
            }
        }

        .input-group {
            margin-bottom: 15px;

            .label {
                margin-bottom: 5px;
                font-weight: bold;
            }

            :deep(.u-input) {
                border: 1px solid #e0e0e0;
                border-radius: 6px;
                padding: 5px 10px;
            }
        }
    }

    .input-group {
        margin-bottom: 20px;

        .label {
            margin-bottom: 5px;
            font-weight: bold;
        }

        :deep(.u-input) {
            border: 1px solid #e0e0e0;
            border-radius: 6px;
            padding: 5px 10px;
        }
    }

    :deep(.u-button) {
        width: 100%;
    }
}

.group-transfer-modal {
    width: 90vw;
    max-height: 80vh;
    background: white;
    border-radius: 10px;
    padding: 20px;
    display: flex;
    flex-direction: column;
    box-sizing: border-box;

    .title {
        font-size: 20px;
        font-weight: bold;
        text-align: center;
        margin-bottom: 20px;
    }

    .user-list {
        display: flex;
        flex-direction: column;
        flex: 1;
        overflow-y: auto;
        margin-bottom: 20px;

        .user-item {
            width: 100%;
            display: flex;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #f0f0f0;

            .user-info {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: space-around;
                margin-right: 10px;


                .head {
                    width: 40px;
                    height: 40px;
                    border-radius: 50%;
                }

                .name {
                    font-size: 12px;
                }
            }

            :deep(.amount-input) {
                width: 100%;
                border: 1px solid #e0e0e0;
                border-radius: 6px;
                padding: 5px;
            }

            &:last-child {
                border-bottom: none;
            }
        }
    }

    .group-buttons {
        display: flex;
        justify-content: space-between;
        gap: 15px;

        :deep(.u-button) {
            flex: 1;
        }
    }
}

.edit-remark-modal {
    width: 80vw;
    padding: 20px;
    background: white;
    border-radius: 10px;
    display: flex;
    flex-direction: column;
    gap: 15px;

    .title {
        font-size: 20px;
        font-weight: bold;
        text-align: center;
    }

    .user-info {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 10px;

        .head {
            width: 60px;
            height: 60px;
            border-radius: 50%;
        }

        .nickname {
            font-size: 16px;
            color: #666;
        }
    }

    :deep(.u-input) {
        border: 1px solid #e0e0e0;
        border-radius: 6px;
        padding: 5px 10px;
    }

    :deep(.u-button) {
        margin-top: 10px;
    }
}

.switch-seat-menu-modal {
    width: 80vw;
    padding: 20px;
    background: white;
    border-radius: 10px;
    display: flex;
    flex-direction: column;
    gap: 15px;

    .title {
        font-size: 20px;
        font-weight: bold;
        text-align: center;
    }

    .menu-options {
        display: flex;
        flex-direction: column;
        gap: 10px;

        .menu-item {
            padding: 15px;
            text-align: center;
            background-color: #f5f5f5;
            border-radius: 8px;
            cursor: pointer;
            transition: background-color 0.3s;

            &:hover {
                background-color: #e0e0e0;
            }

            .menu-text {
                font-size: 16px;
                color: #333;
            }
        }
    }

    :deep(.u-button) {
        margin-top: 10px;
    }
}

.seat-selection-modal {
    width: 80vw;
    padding: 20px;
    background: white;
    border-radius: 10px;
    display: flex;
    flex-direction: column;
    gap: 15px;

    .title {
        font-size: 20px;
        font-weight: bold;
        text-align: center;
    }

    .seat-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 20px;
        margin: 20px 0;

        .seat-item {
            padding: 20px 10px;
            text-align: center;
            background-color: #e6f3ef;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
            border: 2px solid transparent;

            &.occupied {
                background-color: #ffcccc;
                cursor: not-allowed;
            }

            &.selected {
                border-color: #018A6F;
                background-color: #018A6F;
                color: white;
            }

            &:not(.occupied):hover {
                background-color: #018A6F;
                color: white;
            }

            &.disabled {
                background: $uni-bg-color-grey;
            }

            .seat-label {
                font-size: 18px;
                font-weight: bold;
            }
        }
    }

    .current-user {
        text-align: center;
        font-size: 14px;
        color: #666;
        margin: 10px 0;
    }

    :deep(.u-button) {
        margin-top: 10px;
    }
}
</style>