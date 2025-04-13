<template>
	<view>
	   <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack>
			<block slot="backText">返回</block>
			<block slot="content">我的面试</block>
		</cu-custom>
		<!--滚动加载列表-->
		<mescroll-body ref="mescrollRef" bottom="88"  @init="mescrollInit" :up="upOption" :down="downOption" @down="downCallback" @up="upCallback">
		    <view class="cu-list menu">
				<view class="cu-item arrow" v-for="(item,index) in list" :key="index" @click="goHome(item)">
					<view class="content" style="width:100%">
                        <text class="text-lg" style="color: #000;">
                             {{ item.positionName}}
                        </text>
					</view>
					<view class="action">
						<text class="text-grey text-sm">{{ item.interviewDate }}</text>
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
		name: '面试管理',
		mixins: [MescrollMixin, Mixin],
		data() {
			return {
				CustomBar:this.CustomBar,
				NavBarColor:this.NavBarColor,
				url: "/xgsInviteToInterview/xgsInviteToInterview/myInterviewList",
			};
		},
		methods: {
			goHome(item){
                this.$Router.push({name: "XgsInterviewForm", params: { dataId: item.id }})
			}
		}
	}
</script>

