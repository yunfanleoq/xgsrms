<template name="home">
	<view>
		<scroll-view>
			<!-- 轮播 -->
			<swiper class="screen-swiper square-dot"  :indicator-dots="true" :circular="true"
			 :autoplay="true" interval="5000" duration="500" :style="[{animation: 'show 0.2s 1'}]">
				<swiper-item v-for="(item,index) in swiperList" :key="index">
					<image :src="item.url" mode="aspectFill" v-if="item.type=='image'"></image>
					<video :src="item.url" autoplay loop muted :show-play-btn="false" :controls="false" objectFit="cover" v-if="item.type=='video'"></video>
				</swiper-item>
			</swiper>
			
			<view class="cu-bar bg-white solid-bottom" :style="[{animation: 'show 0.5s 1'}]">
				<view class="action">
					<text class='cuIcon-title text-blue'>新闻头条</text>
				</view>
			</view>
			<div class='clearfix'>
				<XgsJournalismList  ></XgsJournalismList >
			</div>
			<!-- <view class="cu-bar bg-white solid-bottom margin-top"  :style="[{animation: 'show 0.6s 1'}]">
				<view class="action">
					 <text class='cuIcon-title text-yellow'>招聘公告</text>
				</view>
			</view>

			<Xgszpgg :style="[{animation: 'show 0.5s 1'}]" ></Xgszpgg > -->
		</scroll-view>
		<view class="cu-tabbar-height margin-top"></view>
	</view>
</template>

<script>
	import { us,os } from '@/common/util/work.js'
	import socket from '@/common/js-sdk/socket/socket.js'
	import XgsJournalismList from '@/pages/home/XgsJournalismList.vue'
	import Xgszpgg from '@/pages/home/Xgszpgg.vue'
	
	export default {
		name: 'home',
		components:{
			XgsJournalismList,
			Xgszpgg
		},
		props:{
			cur:String,
		},
		watch: {
			cur: {
				immediate: true,
				handler:function(val,oldVal){
					console.log('cur',val,oldVal)
				    this.initMenu()
				},
			},
		},
		data() {
			return {
			 swiperList: [
				  {id:1,type: 'image',url: '/static/banner/banner1.png', link: ''},
				  {id:2,type: 'image',url: '/static/banner/banner2.jpg', link: ''},
				  {id:3,type: 'image',url: '/static/banner/banner3.jpg', link: ''},
				  {id:4,type: 'image',url: '/static/banner/banner4.jpg', link: ''},
				],
				middleApps: [
				  {icon: 'line2_icon1.png', title: '审批', 'text': '个人审批'},
				  {icon: 'line2_icon2.png', title: '审批稿', 'text': '审批草稿箱'},
				],
				usList:us.data,
				osList:os.data,
				
				msgCount:0,
				dot:{
				  mailHome:false
				}
			}
		},
		methods: {
			initMenu(){
				console.log("-----------home------------")
			    this.onSocketOpen()
			    this.onSocketReceive()
			    this.loadCount(0);
			},
			goPage(page){
				console.log("page...",page)
				if(!page){
					return false;
				}
				if(page==='annotationList'){
				  this.msgCount = 0
				}
				this.dot[page]=false
				this.$Router.push({name: page})
			},
			// 启动webSocket
			onSocketOpen() {
				socket.init('websocket');
			},
			onSocketReceive() {
				var _this=this
				socket.acceptMessage = function(res){
					// console.log("页面收到的消息", res);
					if(res.cmd == "topic"){
					  //系统通知
					  _this.loadCount('1')
					}else if(res.cmd == "user"){
					  //用户消息
					  _this.loadCount('2')
					} else if(res.cmd == 'email'){
					 //邮件消息
					  _this.loadEmailCount()
					}
				}
			},
			loadCount(flag){
				console.log("loadCount::flag",flag)
				let url = '/sys/annountCement/listByUser';
				this.$http.get(url).then(res=>{
					console.log("res::",res)
					console.log("loadCount res::",res)
				  if(res.data.success){
					let msg1Count = res.data.result.anntMsgTotal;
					let msg2Count = res.data.result.sysMsgTotal;
					this.msgCount = msg1Count + msg2Count
					console.log("this.msgCount",this.msgCount)
				  }
				})
			},
			loadEmailCount(){
				this.dot.mailHome = true
			},
			getTtemDotInfo(item){
				if(item.page==='annotationList' && this.msgCount>0){
				  return this.msgCount
				}
				return '';
			},
			loadswiperList(page = 1, size = 5){
				// 获取轮播图数据的函数					
				console.log("处理前的 carouselImages",this.swiperList)
				console.log("loadswiperList::begin")
				let url = '/xgsHome/xgsHome/list';
				this.$http.get( url).then(res=>{
					console.log("loadswiperList res::",res)
					if(res.data.success){
						this.swiperList = res.data.result.records.map((item,index) => {
						const imgTag = item.photograph;  // 获取 HTML 字符串
						let imgUrl = "";  // 用于存储图片链接
						// 如果是 HTML 格式的 <img> 标签，提取 src 属性
						if (imgTag && imgTag.includes('<img')) {
							const match = imgTag.match(/src="(.*?)"/);  // 提取 <img> 标签中的 src 链接
							imgUrl = match ? match[1] : "";
						}
						  // 如果是纯图片链接，直接使用该链接
						else if (imgTag && !imgTag.includes('<img')) {
							imgUrl = imgTag;
						}
						return {
							id: index,
							link: imgUrl,  // 将图片链接放入 image 字段
							type: 'image',
							url: imgUrl,
						  };
						})
						.sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime()) // 按时间降序排序
						.slice(0, 5); // 截取前5张轮播图
					  console.log("处理后的 carouselImages",this.swiperList)
				  }
				})
			}
				// try {
				// 	// const response = [];
				// 	this.$http.get({
				// 	  url: "/xgsHome/xgsHome/list", // 替换为你的轮播图数据接口
				// 	  params: { pageNo: 1, pageSize: 1000 },
				// 	}).then(
				// 	res=>{
				// 		console.log("loadswiperList res::",res)
				// 		}
				// 		)
					// 	console.log('loadswiperList', response)
					
					// 	if (response && response.records) {
					// 	  this.swiperList = response.records.map((item) => {
					// 		  const imgTag = item.photograph;  // 获取 HTML 字符串
					// 		  let imgUrl = "";  // 用于存储图片链接
					// 		  // 如果是 HTML 格式的 <img> 标签，提取 src 属性
					// 		  if (imgTag && imgTag.includes('<img')) {
					// 			const match = imgTag.match(/src="(.*?)"/);  // 提取 <img> 标签中的 src 链接
					// 			imgUrl = match ? match[1] : "";
					// 		  }
					// 		  // 如果是纯图片链接，直接使用该链接
					// 		  else if (imgTag && !imgTag.includes('<img')) {
					// 			imgUrl = imgTag;
					// 		  }
					// 		  return {
					// 			image: imgUrl,  // 将图片链接放入 image 字段
					// 			createTime: item.createTime || "",
					// 		  };
					// 		})
					// 		.sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime()) // 按时间降序排序
					// 		.slice(0, 5); // 截取前5张轮播图
					// 	  console.log("处理后的 carouselImages",carouselImages.value)
				
					
				// } catch (error) {
				// 	console.error("请求轮播图数据失败:", error);
				// }
			// }

		},
		mounted(){
			this.loadswiperList()
		}
	}
</script>

<style scoped>
	.cu-list.grid>.cu-item {
	  padding: 0px 0px; 
	}
    .line2-icon {
	  width: 60px;
	  height: 60px;
    }

	.clearfix::after {
	  content: "";
	  display: table;
	  clear: both;
	}

</style>
