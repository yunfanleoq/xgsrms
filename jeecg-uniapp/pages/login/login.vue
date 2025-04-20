  <template>
	<view class="zai-box">
        <scroll-view scroll-y class="page">
            <view v-if="loginWay==1" class="text-center" :style="[{animation: 'show ' + 0.4+ 's 1'}]">
				<image src="https://www.iie.ac.cn/images/xgs_logo_.png" mode='aspectFit' class="zai-logo "></image>
				<view class="zai-title text-shadow ">招聘管理系统 </view>
			</view>
            <view class="box padding-lr-xl login-paddingtop" :style="[{animation: 'show ' + 0.6+ 's 1'}]">
				<block v-if="loginWay==1">
					<view class="cu-form-group margin-top  shadow-warp" :class="[shape=='round'?'round':'']">
						<view class="title"><text class="cuIcon-people margin-right-xs"></text>账号:</view>
						<input placeholder="请输入账号" name="input" v-model="userName"></input>
					</view>
					<view class="cu-form-group margin-top shadow-warp" :class="[shape=='round'?'round':'']">
						<view class="title"><text class="cuIcon-lock margin-right-xs"></text>密码:</view>
						<input class="uni-input" placeholder="请输入密码" :password="!showPassword" v-model="password" />
						<view class="action text-lg">
						    <text :class="[showPassword ? 'cuIcon-attention' : 'cuIcon-attentionforbid']" @click="changePassword"></text>
						</view>
					</view>
					<view class="cu-form-group margin-top shadow-warp" :class="[shape=='round'?'round':'']">
						<view class="title"><text class="cuIcon-safe margin-right-xs"></text>验证码:</view>
						<input class="uni-input" placeholder="请输入验证码" v-model="inputCode" />
						<view class="action">
							<image :src="randCodeData.randCodeImage" mode="widthFix" class="code-image" @tap="handleChangeCheckCode"></image>
						</view>
					</view>
					<view class="padding text-center margin-top">
						<button class="cu-btn bg-blue lg margin-right shadow" :loading="loading" :class="[shape=='round'?'round':'']"
							@tap="onLogin"><text space="emsp">{{loading ? "登录中...":" 登录 "}}</text>
						</button>
						<button class="cu-btn line-blue lg margin-left shadow" :loading="loading" :class="[shape=='round'?'round':'']"
							@tap="loginWay=3-loginWay">注册账号
						</button>
					</view>
				</block>
                <block v-else>
					<view class="text-center" :style="[{animation: 'show ' + 0.4+ 's 1'}]">
						<view class="zai-title text-shadow ">注册 </view>
					</view>
                	<view class="cu-form-group margin-top shadow-warp" :class="[shape=='round'?'round':'']">
                		<view class="title"><text class="cuIcon-people margin-right-xs"></text>姓名:</view>
                		<input placeholder="请输入真实姓名" type="text" v-model="realname"></input>
                	</view>
                	<view class="cu-form-group margin-top shadow-warp" :class="[shape=='round'?'round':'']">
                		<view class="title"><text class="cuIcon-people margin-right-xs"></text>账号:</view>
                		<input placeholder="请输入账号" type="text" v-model="username"></input>
                	</view>
                	<view class="cu-form-group margin-top shadow-warp" :class="[shape=='round'?'round':'']">
                		<view class="title"><text class="cuIcon-mobile margin-right-xs"></text>手机:</view>
                		<input placeholder="请输入手机号" type="number" maxlength="11" v-model="mobile"></input>
                	</view>
                	<view class="cu-form-group margin-top shadow-warp" :class="[shape=='round'?'round':'']">
                		<view class="title"><text class="cuIcon-mail margin-right-xs"></text>邮箱:</view>
                		<input placeholder="请输入邮箱" type="text" v-model="email"></input>
                	</view>
                	<view class="cu-form-group margin-top shadow-warp" :class="[shape=='round'?'round':'']">
                		<view class="title"><text class="cuIcon-lock margin-right-xs"></text>验证码:</view>
                		<input class="uni-input" placeholder="请输入邮箱验证码" v-model="smscode"/>
                		<view class="action">
                			<button class="cu-btn line-blue sm" :disabled="!isSendSMSEnable" @click="onSMSSend"> {{ getSendBtnText }}</button>
                		</view>
                	</view>
                	<view class="cu-form-group margin-top shadow-warp" :class="[shape=='round'?'round':'']">
                		<view class="title"><text class="cuIcon-lock margin-right-xs"></text>密码:</view>
                		<input class="uni-input" placeholder="请输入密码" :password="!showPassword" v-model="password" />
                		<view class="action text-lg">
                			<text :class="[showPassword ? 'cuIcon-attention' : 'cuIcon-attentionforbid']" @click="changePassword"></text>
                		</view>
                	</view>
                	<view class="cu-form-group margin-top shadow-warp" :class="[shape=='round'?'round':'']">
                		<view class="title"><text class="cuIcon-lock margin-right-xs"></text>确认密码:</view>
                		<input class="uni-input" placeholder="请再次输入密码" :password="!showPassword" v-model="confirmPassword" />
                	</view>
                	<view class="padding text-center margin-top">
                		<button class="cu-btn bg-blue lg margin-right shadow" :loading="loading" :class="[shape=='round'?'round':'']"
                			@tap="onRegister"><text space="emsp">{{loading ? "注册中...":" 注册 "}}</text>
                		</button>
                		<button class="cu-btn line-blue lg margin-left shadow" :loading="loading" :class="[shape=='round'?'round':'']"
                			@tap="loginWay=1">已有账号登录
                		</button>
                	</view>
                </block>
				
	
				<!-- #ifdef APP-PLUS -->
				<view class="padding flex flex-direction  text-center">
					当前版本:{{version}}
				</view>
				<!-- #endif -->
				
            </view>
        </scroll-view>
		<!-- 登录加载弹窗 -->
		<view class="cu-load load-modal" v-if="loading">
			<!-- <view class="cuIcon-emojifill text-orange"></view> -->
			<image src="https://www.iie.ac.cn/images/xgs_logo_.png" mode="aspectFit" class="round"></image>
			<view class="gray-text">登录中...</view>
		</view>
    </view>

</template>

<script>
	import { ACCESS_TOKEN,USER_NAME,USER_INFO } from "@/common/util/constants"
	import { mapActions } from "vuex"
    import configService from '@/common/service/config.service.js';
	
    export default {
        data() {
            return {
				shape:'',//round 圆形
				loading: false,
				userName: '',
				password: '',
				inputCode: '',
				// 注册相关字段
				realname: '',
				username: '',
				mobile: '',
				email: '',
				smscode: '',
				confirmPassword: '',
				showPassword: false, //是否显示明文
				loginWay: 1, //1: 账密，2：注册
				smsCountDown: 0,
				smsCountInterval: null,
				toggleDelay: false,
				version:'',
				//第三方登录相关信息
				thirdType:"",
				thirdLoginInfo:"",
				thirdLoginState:false,
				bindingPhoneModal:false,
				thirdUserUuid:'',
				randCodeData: {
					randCodeImage: '',
					requestCodeSuccess: false,
					checkKey: null,
				},
				url: {
					getInputCode: '/sys/randomImage',
					bindingThirdPhone: '/sys/thirdLogin/bindingThirdPhone'
				}
            };
        },
		onLoad:function(){
			// #ifdef APP-PLUS
			var that=this
			plus.runtime.getProperty( plus.runtime.appid, function ( wgtinfo ) {
				that.version=wgtinfo.version
			});
			// #endif
			// 初始化验证码
			this.handleChangeCheckCode()
		},
		computed: {
		      isSendSMSEnable() {
		        return this.smsCountDown <= 0 && this.email && this.email.length > 0;
		      },
		      getSendBtnText() {
		        if (this.smsCountDown > 0) {
		          return this.smsCountDown + '秒后发送';
		        } else {
		          return '发送验证码';
		        }
		      },
		      canSMSLogin() {
		        return this.userName.length > 4 && this.smsCode.length > 4;
		      },
		      canPwdLogin() {
		        return this.userName.length > 4 && this.password.length > 4;
		      },
		},
        methods: {
			 ...mapActions([ "mLogin","PhoneLogin","ThirdLogin" ]),
			/**
			 * 获取验证码
			 */
			handleChangeCheckCode() {
				this.inputCode = '';
				this.randCodeData.checkKey = new Date().getTime();
				this.getCodeInfo(this.randCodeData.checkKey).then((res) => {
					console.log("res",res)
					this.randCodeData.randCodeImage = res.data.result;
					this.randCodeData.requestCodeSuccess = true;
				});
			},
			getCodeInfo(currdatetime) {
				const url = this.url.getInputCode + `/${currdatetime}`;
				return this.$http.get(url);
			},
			onLogin: function (){
			        if(!this.userName || this.userName.length==0){
			          this.$tip.toast('请填写用户名');
			          return;
			        }
			        if(!this.password || this.password.length==0){
			           this.$tip.toast('请填写密码');
			          return;
			        }
			        if(!this.inputCode || this.inputCode.length==0){
			           this.$tip.toast('请填写验证码');
			          return;
			        }
			        let loginParams = {
			          username:this.userName,
			          password:this.password,
			          captcha: this.inputCode,
			          checkKey: this.randCodeData.checkKey
			        }
					this.loading=true;
			        this.mLogin(loginParams).then((res) => {
					  this.loading=false;
					  console.log("res+nm;lken-----",res)
			          if(res.data.success){
						 // #ifdef APP-PLUS
						  this.saveClientId()
						 // #endif
						 // #ifndef APP-PLUS
						  this.$tip.success('登录成功!')
						  this.$Router.replaceAll({name:'index'})
						 // #endif
						}else{
			              this.$tip.alert(res.data.message);
			            }
			        }).catch((err) => {
			          let msg = err.data.message || "请求出现错误，请稍后再试"
			          this.loading=false;
					  this.$tip.alert(msg);
			        }).finally(()=>{
					  this.loading=false;
				})
			},
			saveClientId(){
				var info = plus.push.getClientInfo();
				var cid = info.clientid;
				this.$http.get("/sys/user/saveClientId",{params:{clientId:cid}}).then(res=>{
					console.log("res::saveClientId>",res)
					this.$tip.success('登录成功!')
					this.$Router.replaceAll({name:'index'})
				})
			},
			changePassword() {
				this.showPassword = !this.showPassword;
			},
			onSMSSend() {
				let checkEmail = new RegExp(/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/);
				
				if(!this.email || this.email.length==0){
					this.$tip.toast('请输入邮箱');
					return false;
				}
				if(!checkEmail.test(this.email)){
					this.$tip.toast('请输入正确的邮箱格式');
					return false;
				}
				
				let smsParams = {
					mobile: this.mobile,
					email: this.email,
					smsmode: "1" // 1表示邮箱验证码
				};
				
				this.$http.post("/sys/ems", smsParams).then(res=>{
					if(res.data.success){
						this.smsCountDown = 60;
						this.startSMSTimer();
					}else{
						this.smsCountDown = 0;
						this.$tip.toast(res.data.message);
					}
				});
			},
			startSMSTimer() {
				this.smsCountInterval = setInterval(() => {
					this.smsCountDown--;
					if (this.smsCountDown <= 0) {
						clearInterval(this.smsCountInterval);
					}
				}, 1000);
			},
			onRegister() {
				let checkEmail = new RegExp(/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/);
				let checkMobile = new RegExp(/^[1]([3-9])[0-9]{9}$/);
				
				if(!this.realname || this.realname.length==0){
					this.$tip.toast('请填写姓名');
					return;
				}
				if(!this.username || this.username.length==0){
					this.$tip.toast('请填写账号');
					return;
				}
				if(!this.mobile || this.mobile.length==0){
					this.$tip.toast('请填写手机号');
					return;
				}
				if(!checkMobile.test(this.mobile)){
					this.$tip.toast('请输入正确的手机号');
					return;
				}
				if(!this.email || this.email.length==0){
					this.$tip.toast('请填写邮箱');
					return;
				}
				if(!checkEmail.test(this.email)){
					this.$tip.toast('请输入正确的邮箱格式');
					return;
				}
				if(!this.smscode || this.smscode.length==0){
					this.$tip.toast('请填写验证码');
					return;
				}
				if(!this.password || this.password.length==0){
					this.$tip.toast('请填写密码');
					return;
				}
				if(this.password.length < 6){
					this.$tip.toast('密码长度不能小于6位');
					return;
				}
				if(this.password !== this.confirmPassword){
					this.$tip.toast('两次输入的密码不一致');
					return;
				}
				
				let registerParams = {
					realname: this.realname,
					username: this.username,
					mobile: this.mobile,
					email: this.email,
					smscode: this.smscode,
					password: this.password
				};
				
				this.loading = true;
				this.$http.post("/sys/user/register", registerParams).then((res) => {
					if(res.data.success){
						this.$tip.success('注册成功!')
						// 注册成功后自动登录
						this.userName = this.username;
						this.password = this.password;
						this.loginWay = 1;
					}else{
						this.$tip.error(res.data.message);
					}
				}).catch((err) => {
					let msg = ((err.response || {}).data || {}).message || err.data.message || "请求出现错误，请稍后再试"
					this.$tip.error(msg);
				}).finally(() => {
					this.loading = false;
				});
			},
			loginSuccess() {
			  // 登陆成功，重定向到主页
			  this.$Router.replace({name:'index'})
			},
			requestFailed(err) {
			  this.$message.warning("登录失败")
			},
        },
		beforeDestroy() {
		    if (this.smsCountInterval) {
		        clearInterval(this.smsCountInterval);
		    }
		},
    }
</script>

<style>
    .login-paddingtop {
        padding-top: 100upx;
    }

    .zai-box {
        padding: 0 20upx;
        padding-top: 100upx;
        position: relative;
    }

    .zai-logo {
        width: 600upx;
        height: 150px;
    }

    .zai-title {
       font-size: 58upx;
       color: #000000;
       text-align: center;
    }

    .input-placeholder, .zai-input {
        color: #94afce;
    }

    .zai-label {
        padding: 60upx 0;
        text-align: center;
        font-size: 30upx;
        color: #a7b6d0;
    }

    .zai-btn {
        background: #ff65a3;
        color: #fff;
        border: 0;
        border-radius: 100upx;
        font-size: 36upx;
    }

    .zai-btn:after {
        border: 0;
    }

    /*按钮点击效果*/
    .zai-btn.button-hover {
        transform: translate(1upx, 1upx);
    }

    .code-image {
        width: 200rpx;
        height: 80rpx;
    }

</style>
