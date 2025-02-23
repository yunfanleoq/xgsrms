<template>
	<view>
	   <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor">
			<block slot="content">我的简历列表</block>
		</cu-custom>
		<!--滚动加载列表-->
		<mescroll-body ref="mescrollRef" bottom="88"  @init="mescrollInit" :up="upOption" :down="downOption" @down="downCallback" @up="upCallback">
		    <view class="cu-list menu">
				<view class="cu-item" v-for="(item,index) in list" :key="index" @click="goForm(item.id)">
					<view class="flex" style="width:100%">
                        <text class="text-lg" style="color: #000;">
                             {{ item.name}} - 创建时间：{{ formatDate(item.createTime) }}
                        </text>
					</view>
				</view>
				<view class="cu-item" @click="uploadFile">
					<view class="text-center" style="width:100%">
						<button class="cu-btn block bg-blue">
							<text class="cuIcon-upload"></text> 上传PDF简历</button>
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
		name: '我的简历列表',
		mixins: [MescrollMixin, Mixin],
		data() {
			return {
				CustomBar:this.CustomBar,
				NavBarColor:this.NavBarColor,
				url: "/xgsMyresume/xgsMyresume/list",
				uploadUrl: "/xgsMyresume/xgsMyresume/uploadPdf"
			};
		},
		methods: {
			goForm(id){
                // this.$Router.push({name: "XgsMyresumeForm"})
				 uni.navigateTo({
				        url: '/pages/xgs/myresume/XgsMyresumeForm?id=' + id
				      });
			},
			formatDate (value, formatString = 'YYYY-MM-DD') {
			  if (!value) return '';
			  const date = new Date(value);
			  const options = {
				year: 'numeric', month: '2-digit', day: '2-digit',
				hour: '2-digit', minute: '2-digit', second: '2-digit',
				hour12: false,
				timeZoneName: 'short'
			  };

			  formatString = formatString.replace(/YYYY/g, date.getFullYear())
										.replace(/MM/g, ('0' + (date.getMonth() + 1)).slice(-2))
										.replace(/DD/g, ('0' + date.getDate()).slice(-2));

			  return formatString;
			},
			uploadFile() {
				var that=this;
				uni.chooseImage({
					count: 1, //默认9
					extension: ['.pdf'],
					success: (res) => {
						that.$http.upload(that.$config.apiUrl+that.uploadUrl, {
								filePath: res.tempFilePaths[0],
								name: 'file'
							})
							.then(res => {
								that.loadList()
							})
							.catch(err => {
								that.$tip.error(err.data.message)
							});
						this.imgList = res.tempFilePaths
					}
				});
			}
		}
	}
</script>

