<template>
    <view>
        <!--标题和返回-->
		<!-- <cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">家庭状况测试</block>
		</cu-custom> -->
		 <!--表单区域-->
		<view>
			<form>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">姓名：</text></view>
                  <input  placeholder="请输入姓名" v-model="model.name"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">关系：</text></view>
                  <input  placeholder="请输入关系" v-model="model.relation"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">性别：</text></view>
                  <input  placeholder="请输入性别" v-model="model.sex"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">国籍：</text></view>
                  <input  placeholder="请输入国籍" v-model="model.nationality"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">出生地：</text></view>
                  <input  placeholder="请输入出生地" v-model="model.nativePlace"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">出生日期：</text></view>
                  <input  placeholder="请输入出生日期" v-model="model.birthday"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">民族：</text></view>
                  <input  placeholder="请输入民族" v-model="model.nation"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">身份证号码：</text></view>
                  <input  placeholder="请输入身份证号码" v-model="model.idNumber"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">政治背景：</text></view>
                  <input  placeholder="请输入政治背景" v-model="model.politicBackground"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">户口：</text></view>
                  <input  placeholder="请输入户口" v-model="model.hukou"/>
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
                  <view class="title"><text space="ensp">教育背景：</text></view>
                  <input  placeholder="请输入教育背景" v-model="model.education"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">学历：</text></view>
                  <input  placeholder="请输入学历" v-model="model.degree"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">专业：</text></view>
                  <input  placeholder="请输入专业" v-model="model.profession"/>
                </view>
              </view>
              <!-- <my-date label="毕业日期：" v-model="model.graduteDate" placeholder="请输入毕业日期"></my-date> -->
              <!-- <my-date label="工作日期：" v-model="model.workDate" placeholder="请输入工作日期"></my-date> -->
			  <view class="cu-form-group">
			    <view class="flex align-center">
			  	<view class="title"><text space="ensp">毕业时间：</text></view>
			  	<picker mode="date" :value="model.graduteDate" :start="startDate" :end="endDate" @change="(e) => bindDateChange(e,'graduteDate')">
			  		<view class="uni-input" v-if="graduteDate">{{graduteDate}}</view>
			  		<view class="uni-input" v-else>
			  			<input  placeholder="请输入开始时间" style="pointer-events: none;"/>
			  		</view>
			  	</picker>
			    </view>
			  </view>
			  <view class="cu-form-group">
			    <view class="flex align-center">
			  	<view class="title"><text space="ensp">参加工作时间：</text></view>
			  	<picker mode="date" :value="model.workDate" :start="endStartDate" :end="endDate" @change="(e) => bindDateChange(e,'workDate')">
			  		<view class="uni-input" v-if="workDate">{{workDate}}</view>
			  		<view class="uni-input" v-else>
			  			<input  placeholder="请输入开始时间" style="pointer-events: none;"/>
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
        name: "HomeTestForm",
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
				graduteDate: "",
				workDate: "",
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
					this.graduteDate = this.model.graduteDate
					this.endStartDate = this.model.graduteDate
					this.workEndDate = this.model.workDate
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
					case "graduteDate":
						this.model.graduteDate = e.detail.value
						this.graduteDate = this.model.graduteDate
						this.endStartDate = this.model.graduteDate
						break;
					case "workDate":
						this.model.workDate = e.detail.value
						this.workDate = this.model.workDate
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
