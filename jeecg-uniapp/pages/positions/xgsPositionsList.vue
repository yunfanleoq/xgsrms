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
				<view class="cu-item" v-for="(item,index) in list" :key="index" @tap="goToDetail(item)">
					<view class="flex" style="width:100%">
                        <text class="text-lg" style="color: #000;">
                             {{ item.positionName}}
                        </text>
						<text class="text-lg" style="color: #999; font-size: 12px;">
						     {{ item.dept_dictText}}
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
		// name: '岗位列表',
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
				let parmas = {...item}
				parmas.page = 'peoplelis'
				parmas.htmlType = '招聘'
				this.$Router.push({
								name: "positionsThis", // 新页面的路由名称
								params:parmas, // 通过 parmas 传递 id
							});
			}
		}
	}
</script>

