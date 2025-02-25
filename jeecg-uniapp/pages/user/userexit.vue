<template name="user">
	<view>
		<view class="cu-custom ">
			<view class="cu-bar fixed animation-slide-top" :style="[{height: CustomBar + 'px', animationDelay: '0.1s'}]">
				<view class="action" @tap="goBack">
					<text class="cuIcon-back text-white"></text>
					<text class="text-white text-bold text-shadow">返回</text>
				</view>
			</view>
		</view>
		<scroll-view scroll-y class="page">
			<image src="/static/blue.png" mode="widthFix" class="response" style="margin-top: 0;"></image>
			<view class="nav-list margin-top-xl">
				<navigator hover-class="none" :url="'/pages/common/' + item.name" class="nav-li" navigateTo :class="'bg-'+item.color"
				 :style="[{animation: 'show ' + ((index+1)*0.2+1) + 's 1'}]" v-for="(item,index) in elements" :key="index">
					<view class="nav-title">{{item.title}}</view>
					<view class="nav-name">{{item.name}}</view>
					<text :class="'cuIcon-' + item.cuIcon"></text>
				</navigator>
			</view>
			<view class="cu-tabbar-height"></view>
		</scroll-view>
	</view>
</template>

<script>
	export default {
		name: 'user',
		data() {
			return {
				CustomBar: this.CustomBar,
				elements: [{
						title: '退出',
						name: 'exit',
						color: 'cyan',
						cuIcon: 'newsfill',
						auth: 'ac'
					}
				],
			}
		},
		methods: {
			goBack() {
				uni.navigateBack({
					delta: 1
				});
			}
		}
	}
</script>

<style>

</style>
