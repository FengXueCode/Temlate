<template>
    <div class='main'>
        <up-navbar title="麻友记账" leftIcon="home" @leftClick="goBack"></up-navbar>
        <div class="title">我的战绩</div>
        <div class="record">
            <div class="img-info">
                <img src="@/static/icon/winner.svg" alt="" class="icon" v-if="sum > 0">
                <img src="@/static/icon/loser.svg" alt="" class="icon" v-else>
                <div class="text" :class="{ win: sum >= 0 }">{{ sum >= 0 ? '大赢家' : '惜败' }}</div>
            </div>
            <div class="info">
                <div class="sum">总分: <span class="text" :class="{ win: sum > 0 }">{{ sum }}</span> 分</div>
                <div class="tip">{{ sum > 0 ? '恭喜发财~' : '再接再厉~' }}</div>
            </div>
        </div>
        <div class="title">结算记录<span class="tip">(分钱比:{{ room.roomRatio }}/分)</span> </div>
        <div class="history">
            <div class="history-title" v-if="loserSettlementList.length != 0">转出</div>

            <div class="item" v-for="item in loserSettlementList" :key="item.roomRecordId">
                <div class="info">
                    <div class=" head me" v-if="item.loser == currentUser.userId">我</div>
                    <img :src="getUserPortrait(item.loser) == null ? head : getUserPortrait(item.loser)" alt=""
                        class="head" v-else>
                    <div class="name">{{ getUserNickname(item.loser) }}</div>
                </div>
                <div class="line">
                    <div class="text">
                        {{ Math.abs(item.money) * room.roomRatio }}元 <span class="tip">({{ Math.abs(item.money)
                            }}分)</span>
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
                    <img :src="getUserPortrait(item.winner) == null ? head : getUserPortrait(item.winner)" alt=""
                        class="head" v-else>
                    <div class="name">{{ getUserNickname(item.winner) }}</div>

                </div>
            </div>

            <div class="history-title" v-if="winSettlementList.length != 0">转入</div>
            <div class="item" v-for="item in winSettlementList" :key="item.roomRecordId">
                <div class="info">
                    <div class=" head me" v-if="item.loser == currentUser.userId">我</div>
                    <img :src="getUserPortrait(item.loser) == null ? head : getUserPortrait(item.loser)" alt=""
                        class="head" v-else>
                    <div class="name">{{ getUserNickname(item.loser) }}</div>
                </div>
                <div class="line">
                    <div class="text">
                        {{ Math.abs(item.money) * room.roomRatio }}元 <span class="tip">({{ Math.abs(item.money)
                            }}分)</span>
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
                    <img :src="getUserPortrait(item.winner) == null ? head : getUserPortrait(item.winner)" alt=""
                        class="head" v-else>
                    <div class="name">{{ getUserNickname(item.winner) }}</div>

                </div>
            </div>
        </div>
        <div class="title" v-if="isRoom">房间排名</div>
        <div class="ranking" v-if="isRoom">
            <div class="item" v-for="(item, index) in roomEndingList" :key="item.userId">
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
    isRoom.value = JSON.parse(val.isRoom)
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
        getRoomEnding()
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
function getUserPortrait(userId: string) {
    let user = fullUserList.value.find(item => item.userId == userId)
    if (user == undefined) return null
    return user.portrait;
}




//--------------------<结算信息>-----------------------
const isRoom = ref(false)// 是否房间结算
const list = ref([])//转账记录
const winSettlementList = ref([])
const loserSettlementList = ref([])
const sum = ref(0)
function getSettlement() {
    roomApi.getSettlement(room.value.roomId).then(res => {
        list.value = res;
        winSettlementList.value = res.filter(item => item.winner == currentUser.userId)
        loserSettlementList.value = res.filter(item => item.loser == currentUser.userId)
    })
}
const roomEndingList = ref([])
function getRoomEnding() {
    roomApi.getRoomEnding(room.value.roomId).then(res => {
        roomEndingList.value = res
        sum.value = res.find(item=>item.userId==currentUser.userId).money
    })
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

        .history-title {
            margin: 10px 0;

            border-left: 3px solid $uni-color-primary;
            padding-left: 10px;
        }

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
                        background: $uni-color-zt;
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