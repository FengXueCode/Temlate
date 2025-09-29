<template>
    <div class='main'>
        <up-navbar title="麻友记账" :autoBack="true"></up-navbar>
        <div class="content">
            <div class="item">
                <div class="title">· 麻友记账简介</div>
                <div class="text">
                    谢谢您的支持，麻友记账本是一个牌局记账工具。
                    辅助您记录牌局中各位玩家的分数变化，仅作为辅助记账的工具。
                    不涉及任何金钱操作，请合理使用小程序功能。
                    <span class="red">禁止用于违法行为</span> 。
                    使用过程中遇到问题，请在点击页面中的【联系客服】按钮给我们留言，我们会尽快处理您的反馈。
                </div>
            </div>
            <div class="item">
                <div class="title">· 隐私说明</div>
                <div class="text">小程序对您信息的访问都经由微信官方审核，在明确告知用户并获取授权后我们方可访问，呢称及头像数据仅作为小程序中展示用户信息使用。</div>
            </div>
            <div class="item">
                <div class="title">· 操作说明</div>
                <div class="text">
                    <div>

                        <span class="weight">修改昵称，头像</span>：修改昵称，头像:在主页中点击头像展示区域，您会进入呢称，头像设置界面，您可以修改昵称及头像。
                    </div>
                    <div>
                        <span class="weight"> 创建房间</span>：点击【创建房间】按钮，您会创建一个虚拟房间，其他用户可以通过您房间的二维码扫码进入。
                    </div>
                    <div>

                        <span class="weight">加入房间</span> ：点击【扫码加入房间】按钮，您可以扫描其他用户房间的二维码进入房间(同一时间您只能处于一个房间内)。
                    </div>
                    <div>

                        <span class="weight">意见反馈</span> :点击【意见反馈】按钮，您将进入客服系统，可以在该界面中与客服实时沟通或给客服留言，我们会在一个工作日内尽快回复您的反馈。
                    </div>
                    <div>

                        <span class="weight">对战记录</span>:点击主页【对战记录】按钮，可以查看以往所有对战记录，对战记录中点击【详情】按钮，可以查看该房间详细记账明细，若您需要删除该条对战记录，在对战记录详情页面，下拉滚动到页面最下方，点击【删除记录】按钮即可。
                    </div>
                </div>
            </div>
        </div>

    </div>
</template>
<script setup lang='ts'>
onLoad((val: any) => {

});
onMounted(() => {
})
</script>
<style scoped lang='scss'>
.content {
    background: $uni-bg-color-grey;

    .item {
        margin-bottom: 10px;

        .title {
            font-weight: bold;
            font-size: 16px;
        }

        .text {
            white-space: pre-line;
            background: white;
            padding: 10px;
            border-radius: 10px;
            margin-top: 10px;

            .red {
                color: red;
            }
            .weight {
                font-weight: bold;
            }
        }
    }
}
</style>