<template>
    <view>
        <!--标题和返回-->
		<!-- <cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">教育经历测试</block>
		</cu-custom> -->
		 <!--表单区域-->
		<view>
			<form>
				<view class="cu-form-group">
				  <view class="flex align-center">
					<view class="title"><text space="ensp">开始时间：</text></view>
					<picker mode="date" :value="model.beginDate" :start="startDate" :end="endDate" @change="(e) => bindDateChange(e,'workBeginDate')">
						<view class="uni-input" v-if="workBeginDate">{{workBeginDate}}</view>
						<view class="uni-input" v-else>
							<input  placeholder="请输入开始时间" style="pointer-events: none;"/>
						</view>
					</picker>
				  </view>
				</view>
				<view class="cu-form-group">
				  <view class="flex align-center">
					<view class="title"><text space="ensp">结束时间：</text></view>
					<picker mode="date" :value="model.endDate" :start="endStartDate" :end="endDate" @change="(e) => bindDateChange(e,'workEndDate')">
						<view class="uni-input" v-if="workEndDate">{{workEndDate}}</view>
						<view class="uni-input" v-else>
							<input  placeholder="请输入结束时间" style="pointer-events: none;"/>
						</view>
					</picker>
				  </view>
				</view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">毕业院校：</text></view>
                  <input  placeholder="请输入毕业院校" v-model="model.graduateCollege"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">专业：</text></view>
                  <input  placeholder="请输入专业" v-model="model.profession"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">学历：</text></view>
                  <input  placeholder="请输入学历" v-model="model.education"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">学位：</text></view>
                  <input  placeholder="请输入学位" v-model="model.degree"/>
                </view>
              </view>
			  
			  <view class="form-buttons" v-if="formType == '查看'">
				  <button @click="handleCancel">确认</button>
				  <button @click="handleCancel">取消</button>
			  </view>
			  <view class="form-buttons" v-else-if="formType == '修改'">
				  <button @click="updateConfirm">修改</button>
				  <button @click="delConfirm">删除</button>
				  <button @click="handleCancel">取消</button>
			  </view>
			  <view class="form-buttons" v-else>
				  <button @click="handleConfirm">确认</button>
				  <button @click="handleCancel">取消</button>
			  </view>
			</form>
		</view>
    </view>
</template>

<script>
    import myDate from '@/components/my-componets/my-date.vue'

    export default {
        name: "ResumeEdulForm",
        components:{ myDate },
        props:{
          formData:{
              type:Object,
              default:()=>{},
              required:false
          }
        },
        data(){
            return {
				CustomBar: this.CustomBar,
				NavBarColor: this.NavBarColor,
				loading:false,
                model: {},
                backRouteName:'index',
                url: {
                  queryById: "/xgsUserResumes/eduTest/queryById",
                  add: "/xgsUserResumes/eduTest/add",
                  edit: "/xgsUserResumes/eduTest/edit",
                },
				workBeginDate: "",
				workEndDate: "",
				startDate: "",
				endDate: "",
				endStartDate: "",
				formType: "新增"
            }
        },
        created(){
			this.getStartDate();
			this.getEndDate();
			this.initForm();
        },
        methods:{
			initForm(){
				console.log(this.formData)
				if(this.formData){
					this.model = this.formData
					this.workBeginDate = this.model.beginDate
					this.endStartDate = this.model.beginDate
					this.workEndDate = this.model.endDate
					this.formType = "修改"
				}
			},
           // 确认按钮点击事件
		   handleConfirm() {
			   this.$emit('confirm', this.model); // 触发 confirm 事件，传递表单数据
		   },
		   // 修改按钮点击事件
		   updateConfirm() {
			   this.$emit('updateConfirm', this.model); // 触发 confirm 事件，传递表单数据
		   },
		   // 删除按钮点击事件
		   delConfirm() {
			   this.$emit('delConfirm'); // 触发 confirm 事件，传递表单数据
		   },
		   // 取消按钮点击事件
		   handleCancel() {
			   this.$emit('cancel'); // 触发 cancel 事件
		   },
		   
			bindDateChange: function(e,value) {
				switch(value){
					case "workBeginDate":
						this.model.beginDate = e.detail.value
						this.workBeginDate = this.model.beginDate
						this.endStartDate = this.model.beginDate
						break;
					case "workEndDate":
						this.model.endDate = e.detail.value
						this.workEndDate = this.model.endDate
						break;
				}
			},
			getDate(type) {
				const date = new Date();
				let year = date.getFullYear();
				let month = date.getMonth() + 1;
				let day = date.getDate();
				
				if(type === 'start'){
					year = year  - 100;
				}
				
				month = month > 9 ? month : '0' + month;
				day = day > 9 ? day : '0' + day;
				return `${year}-${month}-${day}`;
			},
			getStartDate() {
				this.startDate = this.getDate('start');
			},
			getEndDate() {
				this.endDate = this.getDate('end');
			},
        }
    }
</script>
