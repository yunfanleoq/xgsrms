<template>
	<view>
	   <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack>
			<block slot="backText">返回</block>
			<block slot="content">申请测试</block>
		</cu-custom>
		<!--滚动加载列表-->
		<mescroll-body ref="mescrollRef" bottom="88"  @init="mescrollInit" :up="upOption" :down="downOption" @down="downCallback" @up="loadList">
		    <view class="cu-list menu">
				<view class="cu-item" v-for="(item,index) in list" :key="index" @click="onApply(item)">
					<view class="flex" style="width:100%">
                        <text class="text-lg" style="color: #000;">
                             {{ item.positionDept}}
                        </text>
						<text class="text-lg" style="color: #999; font-size: 12px;">
						     {{ item.approvalStatus}}
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
		name: '申请测试',
		mixins: [MescrollMixin,Mixin],
		data() {
			return {
				CustomBar:this.CustomBar,
				NavBarColor:this.NavBarColor,
				url: "/positions/xgsPositionApply/listMine",
				byIdUrl:"/positions/xgsPositions/queryById"
			};
		},
		methods: {
			//岗位申请（对于‘招聘’）
			onApply(item){
				let params = {...item}
				params.htmlType = '查看'
				params.resumeAppleId = params.id
				this.$http.get(this.byIdUrl,{params: {id: params.positionId}}).then(res=>{
					if(res.data.success){
						params = {...params, ...res.data.result}
						console.log("==========", params)
						uni.$globalParams = params;
						uni.navigateTo({
							url:"/pages/resume/resumeForm"
						})
					}
				}).catch(err => {
					console.log(err);
				});
			},
		}
	}
</script>

