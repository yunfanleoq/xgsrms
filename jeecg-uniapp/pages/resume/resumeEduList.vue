<template>
	<view>	
		<view class="uni-padding-wrap uni-common-mt" v-if="!showForm">
			<button type="primary" @click="showForm = true">新增教育经历</button>
		</view>
		
		<!-- 弹出表单 -->
		<resumeEduForm 
			v-if="showForm" 
			@confirm="handleConfirm" 
			@updateConfirm="updateConfirm"
			@delConfirm="delConfirm"
			@cancel="showForm = false"
			:formData = "workForm"
		/>
		
		<!--滚动加载列表-->
		<view class="cu-list menu" v-if="!showForm">
			<view class="cu-item" v-for="(item,index) in list" :key="index" @click="showForm = true, workForm = item, listIndex = index">
				<view class="flex" style="width:100%">
					<text class="text-lg" style="color: #000;">
						 {{ item.graduateCollege}}
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
	
	import resumeEduForm from '@/pages/resume/resumeEduForm.vue'

	export default {
		name: 'ResumeEdulList',
		mixins: [MescrollMixin,Mixin],
		data() {
			return {
				CustomBar:this.CustomBar,
				NavBarColor:this.NavBarColor,
				url: "/xgsUserResumes/eduTest/list",
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

