<template>
	<view class='mescroll-body'>
	   <!--标题和返回-->
<!-- 		<cu-custom :bgColor="NavBarColor" isBack>
			<block slot="backText">返回</block>
			<block slot="content">头条新闻</block>
		</cu-custom> -->
		<!--滚动加载列表-->
		<mescroll-body class="mescroll-body" ref="mescrollRef" bottom="0"  @init="mescrollInit" :up="upOption" :down="downOption" @down="downCallback" @up="upCallback">
		    <view class="cu-list menu">
				<view class="cu-item" v-for="(item,index) in list" :key="index" @click="goNewsDetail">
					
					<view class="flex" style="width:100%">
                        <text class="text-lg" style="color: #000;">
                             {{ item.journalismHead}}
                        </text>
					</view>
				</view>
			</view>
		</mescroll-body>
	</view>
</template>

<script>
import MescrollMixin from "@/components/mescroll-uni/mescroll-mixins.js";
import Mixin from "@/common/mixin/Mixin.js";

export default {
    // name: '头条新闻',
    mixins: [MescrollMixin, Mixin],
    data() {
        return {
            // 这里可以先初始化为 null 或默认值
            CustomBar: null,
            NavBarColor: null,
            url: "/xgsJournalism/xgsJournalism/list",
            // list: []
        };
    },
    methods: {
        goNewsDetail() {
            // 使用 this.$router 进行路由跳转
            this.$router.push({ name: "newsDetail" });
        },
   //      GetList() {
   //          this.list = [{'createBy': 'b'},{'createBy': 'c'}];
			// console.log('>>>>>>>>>',this.list)
   //      }
    },
    mounted() {
        // 调用 GetList 方法
        // this.GetList();
		console.log('>>>>xxx>>>>>',this.list)
    }
}
</script>

<style scoped>
/* 去除可能的默认外边距和内边距 */
.mescroll-body {
    margin: 0;
    padding: 0;
}
.cu-list.menu {
    margin: 0;
    padding: 0;
}
.parent-container {
    margin: 0;
    padding: 0;
}
</style>