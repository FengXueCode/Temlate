<template>
    <div class='main'>
        <up-navbar title="麻友记账" leftIcon="home" @leftClick="goBack"></up-navbar>
        <div class="title">我的战绩</div>
        <div class="record">
            <div class="img-info">
                <img src="@/static/icon/winner.svg" alt="" class="icon" v-if="sum > 0">
                <img src="@/static/icon/loser.svg" alt="" class="icon" v-else>
                <div class="text" :class="{ win: sum > 0 }">{{ sum > 0 ? '大赢家' : '惜败' }}</div>
            </div>
            <div class="info">
                <div class="sum">总分: <span class="text" :class="{ win: sum > 0 }">{{ sum }}</span> 分</div>
                <div class="tip">{{ sum > 0 ? '恭喜发财~' : '再接再厉~' }}</div>
            </div>
        </div>
        <div class="title">结算记录<span class="tip">(分钱比:{{ room.roomRatio }}/分)</span> </div>
        <div class="history">
            <div class="item" v-for="item in lastSettlementList" :key="item.roomRecordId">
                <div class="info">
                    <div class=" head me" v-if="item.loser == currentUser.userId">我</div>
                    <img :src="item.loserPortrait == null ? head : item.loserPortrait" alt="" class="head" v-else>
                    <div class="name">{{ getUserNickname(item.loser) }}</div>
                </div>
                <div class="line">
                    <div class="text">
                        {{ item.money * room.roomRatio }}元 <span class="tip">({{ item.money }}分)</span>
                    </div>
                    <div class="text-info">

                        ------------------
                        <div class="arrow">
                            <up-icon name="arrow-right" color="#999" size="16"></up-icon>
                        </div>
                    </div>

                </div>
                <div class="info">
                    <div class=" head me" v-if="item.winner == currentUser.userId">我</div>
                    <img :src="item.winnerPortrait == null ? head : item.winnerPortrait" alt="" class="head" v-else>
                    <div class="name">{{ getUserNickname(item.winner) }}</div>

                </div>
            </div>

        </div>
        <div class="title" v-if="isRoom">房间排名</div>
        <div class="ranking">
            <div class="item" v-for="(item, index) in fullUserList" :key="item.userId">
                <div class="info">

                    <img :src="index === 0 ? ranking0 : index === 1 ? ranking1 : ranking2" alt="" class="icon"
                        v-if="index < 3">
                    <div class="icon" v-else>{{ index + 1 }}</div>
                    <div class="name">{{ getUserNickname(item.userId) }}</div>
                </div>
                <div class="money " :class="{ minus: item.money < 0 }">{{ item.money }} 分</div>

            </div>
        </div>
        <div class="btn-full" @click="goBack">退出房间</div>
    </div>
</template>
<script setup lang='ts'>
onLoad((val: any) => {
    let roomId = val.roomId;
    isRoom.value = val.isRoom
    getRoom(roomId)
});
onMounted(() => {
})

import head from "@/static/icon/user.png"
import ranking0 from "@/static/icon/gold-medal.svg"
import ranking1 from "@/static/icon/silver-medal.svg"
import ranking2 from "@/static/icon/bronze-medal.svg"
const currentUser = uni.getStorageSync("user")
//--------------------<房间信息>-----------------------
const room = ref({
    roomRatio: 1
})

function getRoom(roomId: string) {
    roomApi.getRoomById(roomId).then(res => {
        room.value = res
        getSettlement()
        getRoomUser()
    }
    )
}

const fullUserList = ref([])
function getRoomUser() {
    roomApi.getRoomUser(room.value.roomId).then((res) => {
        res.sort((a, b) => b.money - a.money)
        fullUserList.value = res;
    })
}
function getUserNickname(userId: string) {
    let user = fullUserList.value.find(item => item.userId == userId)
    if (user == undefined) return ''
    return user.nickname
}
//--------------------<结算信息>-----------------------
const isRoom = ref(false)// 是否房间结算
const list = ref([])//转账记录
const lastSettlementList = ref([])
const sum = ref(0)
function getSettlement() {
    roomApi.getSettlement(room.value.roomId).then(res => {
        list.value = res;
        // 将创建时间在5秒内的结算记录归为一次结算
        groupSettlementsByTime(list.value)
    })
}

// 将创建时间在5秒内的结算记录归为一次结算
function groupSettlementsByTime(settlements: any[]) {
    if (!settlements || settlements.length === 0) {
        sum.value = 0
        return
    }

    // 分组：将5秒内创建的记录分为一组
    const groupedSettlements = []
    let currentGroup = [settlements[0]]
    groupedSettlements.push(currentGroup)

    for (let i = 1; i < settlements.length; i++) {
        const currentTime = new Date(settlements[i].createTime).getTime()
        const lastGroupTime = new Date(currentGroup[0].createTime).getTime()

        // 如果与当前组第一个记录的时间差在5秒内，则归为同一组
        if (lastGroupTime - currentTime <= 5000) {
            currentGroup.push(settlements[i])
        } else {
            // 否则新建一个组
            currentGroup = [settlements[i]]
            groupedSettlements.push(currentGroup)
        }
    }

    // 计算最近一次结算的总和（第一组）
    if (groupedSettlements.length > 0) {
        lastSettlementList.value = groupedSettlements[0]

        sum.value = lastSettlementList.value.reduce((total, item) => total + item.money, 0)
    } else {
        sum.value = 0
    }
}


function goBack() {
    util.relaunch("/pages/index")
}
</script>
<style scoped lang='scss'>
.main {
    padding-left: 10px;
    padding-right: 10px;
    box-sizing: border-box;
    height: auto;
    min-height: 100vh;
    padding-bottom: 68px;

    .title {
        border-left: 3px solid $uni-color-primary;
        padding-left: 10px;
        margin: 10px 0;
        height: 30px;
        display: flex;
        align-items: center;

        .tip {
            font-size: 14px;
            margin-left: 10px;
            color: $uni-text-color-grey;
        }
    }

    .record {
        width: 100%;
        height: 16vh;
        box-sizing: border-box;
        display: flex;
        align-items: center;
        justify-content: space-around;
        box-shadow: $uni-border-shadow;
        padding: 10px;
        border-radius: 10px;

        .text {
            color: $uni-color-error;

            &.win {
                color: $uni-color-success;
            }
        }

        .img-info {
            display: flex;
            flex-direction: column;
            align-items: center;

            .icon {
                width: 100px;
                height: 100px;
            }


        }

        .info {
            .sum {
                font-size: 24px;
            }

            .tip {
                margin-top: 10px;
                color: $uni-text-color-grey;
            }
        }

    }

    .history {
        box-shadow: $uni-border-shadow;
        border-radius: 10px;
        padding: 10px;
        box-sizing: border-box;
        overflow-y: auto;

        .item {
            display: flex;
            align-items: center;
            justify-content: space-around;
            border-bottom: 1px solid $uni-border-color;
            padding: 10px 0;
            box-sizing: border-box;

            .info {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;

                .head {
                    width: 50px;
                    height: 50px;
                    border-radius: 50%;

                    &.me {
                        background: $uni-color-primary;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        color: white;
                    }
                }

                .name {
                    margin-top: 10px;
                }
            }

            .line {
                display: flex;
                flex-direction: column;
                align-items: center;

                .tip {
                    font-size: 14px;
                    color: $uni-text-color-grey;
                }

                .text-info {
                    display: flex;
                    align-items: center;
                    color: #999;
                }
            }


        }
    }

    .ranking {
        width: 100%;
        padding: 10px;
        box-sizing: border-box;
        box-shadow: $uni-border-shadow;
        border-radius: 10px;

        .item {

            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 10px;
            border-bottom: 1px solid $uni-border-color;

            .info {
                display: flex;
                align-items: center;

                .icon {
                    width: 30px;
                    height: 30px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }

                .name {
                    margin-left: 10px;
                }

            }

            .money {
                color: red;

                &.minus {
                    color: $uni-color-warning;
                }

            }
        }
    }

    .btn-full {
        position: absolute;
        margin-top: 10px;
        bottom: 10px;
        left: 20%;
    }
}
</style>