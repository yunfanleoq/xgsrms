<template>
	<view>		
		
		<view class="uni-padding-wrap uni-common-mt" v-if="!showForm">
			<button type="primary" @click="showForm = true">新增工作经历</button>
		</view>
		
		<!-- 弹出表单 -->
		<resumeWorkForm 
			v-if="showForm" 
			@confirm="handleConfirm" 
			@updateConfirm="updateConfirm"
			@delConfirm="delConfirm"
			@cancel="showForm = false"
			:formData = "workForm"
		/>
		<!--滚动加载列表-->
		<view class="cu-list menu" v-if="!showForm">
			<view v-if="list.length === 0">
				请添加工作经历信息
			</view>
			<view class="cu-item" v-for="(item,index) in list" :key="index" @click="showForm = true, workForm = item, listIndex = index">
				<view class="flex" style="width:100%">
					<text class="text-lg" style="color: #000;">
						 {{ item.workUnit}}
					</text>
				</view>
				<view class="flex" style="width:100%">
					<text class="text-lg" style="color: #999;">
						 {{ item.beginDate}} - {{ item.endDate}}
					</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import MescrollMixin from "@/components/mescroll-uni/mescroll-mixins.js";
	import Mixin from "@/common/mixin/Mixin.js";
	
	import resumeWorkForm from '@/pages/resume/resumeWorkForm.vue'

	export default {
		name: 'ResumeWorkList',
		mixins: [MescrollMixin,Mixin],
		data() {
			return {
				CustomBar:this.CustomBar,
				NavBarColor:this.NavBarColor,
				url: "/xgsUserResumes/workTest/list",
				list: [],
				listIndex: -1,
				showForm: false, // 控制表单显示和隐藏
				workForm: null,
			};
		},
		methods: {
			// 处理表单确认事件
			handleConfirm(formData) {
				this.list.push(formData); // 将表单数据添加到列表
				this.finishProcessing ();
			},
			updateConfirm(formData){
				this.list[this.listIndex] = formData
				this.finishProcessing ();
			},
			delConfirm() {
				this.list.splice(this.index, 1);
				this.finishProcessing ();
			},
			finishProcessing (){
				this.showForm = false; // 关闭表单
				this.workForm = null;
				this.listIndex = -1;
			}
		}
	}
</script>

