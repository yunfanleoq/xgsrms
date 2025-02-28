<template>
    <view>
        <!--标题和返回-->
		<!-- <cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">工作经历测试</block>
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
                  <view class="title"><text space="ensp">工作单位：</text></view>
                  <input  placeholder="请输入工作单位" v-model="model.workUnit"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">部门：</text></view>
                  <input  placeholder="请输入部门" v-model="model.department"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">职位：</text></view>
                  <input  placeholder="请输入职位" v-model="model.position"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">职称：</text></view>
                  <input  placeholder="请输入职称" v-model="model.professionLevel"/>
                </view>
              </view>
              <view class="cu-form-group" style="display: none;">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">创建者：</text></view>
                  <input  placeholder="请输入创建者" v-model="model.createBy"/>
                </view>
              </view>
              <my-date label="创建时间：" v-model="model.createTime" placeholder="请输入创建时间" style="display: none;"></my-date>
              <view class="cu-form-group" style="display: none;">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">更新者：</text></view>
                  <input  placeholder="请输入更新者" v-model="model.updateBy"/>
                </view>
              </view>
              <my-date label="更新时间：" v-model="model.upateTime" placeholder="请输入更新时间" style="display: none;"></my-date>
              <view class="cu-form-group" style="display: none;">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">是否删除：</text></view>
                  <input  placeholder="请输入是否删除" v-model="model.deleted"/>
                </view>
              </view>
              <view class="cu-form-group" style="display: none;">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">租户ID：</text></view>
                  <input  placeholder="请输入租户ID" v-model="model.tenantId"/>
                </view>
              </view>
              <view class="cu-form-group" style="display: none;">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">编码：</text></view>
                  <input  placeholder="请输入编码" v-model="model.sysOrgCode"/>
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
        name: "ResumeWorkForm",
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
                  queryById: "/xgsUserResumes/workTest/queryById",
                  add: "/xgsUserResumes/workTest/add",
                  edit: "/xgsUserResumes/workTest/edit",
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
