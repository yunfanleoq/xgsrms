<template>
	<view>
	   <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor">
			<!-- <block slot="backText">返回</block> -->
			<block slot="content">岗位列表</block>
		</cu-custom>
		<!--滚动加载列表-->
		<mescroll-body ref="mescrollRef" bottom="88"  @init="mescrollInit" :up="positionsList" :down="downOption" @down="downCallback" @up="upCallback">
		    <view class="cu-list menu">
				<view class="cu-item arrow" v-for="(item,index) in list" :key="index" @click="goHome">
					<view class="content" style="width:100%">
                        <text class="text-lg" style="color: #000;">
                             {{ item.positionName}}
                        </text>
					</view>
					<view class="action">
						<text class="text-grey text-sm">{{ item.dept_dictText }}</text>
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
		name: 'positions_list',
		mixins: [MescrollMixin,Mixin],
		data() {
			return {
				CustomBar:this.CustomBar,
				NavBarColor:this.NavBarColor,
				url: "/positions/xgsPositions/list",
				list:[],
			};
		},
		methods: {
			goHome(){
                this.$Router.push({name: "index"})
			},
			goToDetail(item){
				let params = {...item}
				params.page = 'positionsList'
				params.htmlType = '招聘'
				// this.$Router.push({
				// 	name: "positionsDetail", // 新页面的路由名称
				// 	params:parmas, // 通过 parmas 传递 id
				// });
				console.log("---------", params)
				sessionStorage.setItem("positionId", params.id)
				// sessionStorage.setItem("positionName", params.positionName)
				// sessionStorage.setItem("positionDept", params.positionDept)
				// sessionStorage.setItem("positionKtz", params.positionKtz)
				// sessionStorage.setItem("positionCount", params.positionCount)
				sessionStorage.setItem("dept_dictText", params.dept_dictText)
				sessionStorage.setItem("ktz_dictText", params.ktz_dictText)
				sessionStorage.setItem("category", params.category)
				sessionStorage.setItem("htmlType", params.htmlType)
				sessionStorage.setItem("page", params.page)
				uni.$globalParams = params;
				uni.navigateTo({
					url:"/pages/positions/xgsPositionsForm"
				})
			}
		}
	}
</script>

