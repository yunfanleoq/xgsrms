<template>
	<view>
	   <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack>
			<block slot="backText">返回</block>
			<block slot="content">岗位列表</block>
		</cu-custom>
		<!--滚动加载列表-->
		<mescroll-body ref="mescrollRef" bottom="88"  @init="mescrollInit" :up="positionsList" :down="downOption" @down="downCallback" @up="upCallback">
		    <view class="cu-list menu">
				<view class="cu-item" v-for="(item,index) in list" :key="index" @click="goHome">
					<view class="flex" style="width:100%">
                        <text class="text-lg" style="color: #000;">
                             {{ item.positionName}}
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
		name: '岗位列表',
		mixins: [MescrollMixin,Mixin],
		data() {
			return {
				CustomBar:this.CustomBar,
				NavBarColor:this.NavBarColor,
				url: "/positions/xgsPositions/list",
				list:[]
			};
		},
		methods: {
			goHome(){
                this.$Router.push({name: "index"})
				
			},
			// 请求后端接口的方法
			fetchData() {
				this.$http.get(this.url).then(res => {
					this.list = res.data; // 将返回的数据赋值给 list
				}).catch(err => {
					console.error("请求失败：", err); // 打印错误信息
				});
			},
		}
	}
</script>

