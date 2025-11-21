<template>
	<view class="register-container">
		<!-- Logo -->
		<view class="logo-section">
			<image class="logo" src="/static/type10/logo.png" mode="aspectFit"></image>
		</view>
		
		<!-- 品牌标题 -->
		<view class="brand-title">咚 咚</view>
		<view class="brand-subtitle">一个社团交流场地</view>
		
		<!-- 注册卡片 -->
		<view class="register-card">
			<!-- 手机号 -->
			<view class="form-group">
				<text class="form-label">手机号</text>
				<view class="input-container">
					<input 
						class="form-input" 
						type="number" 
						placeholder="请输入手机号"
						v-model="registerForm.phone"
						maxlength="11"
					/>
				</view>
			</view>
			
			<!-- 密码 -->
			<view class="form-group">
				<text class="form-label">密码</text>
				<view class="input-container">
					<input 
						class="form-input" 
						type="password" 
						placeholder="请输入密码"
						v-model="registerForm.password"
					/>
				</view>
			</view>
			
			<!-- 密码确认 -->
			<view class="form-group">
				<text class="form-label">密码确认</text>
				<view class="input-container">
					<input 
						class="form-input" 
						type="password" 
						placeholder="请再次输入密码"
						v-model="registerForm.confirmPassword"
					/>
				</view>
			</view>
			
			<!-- 验证码 -->
			<view class="form-group">
				<text class="form-label">验证码</text>
				<view class="input-container verification-container">
					<input 
						class="form-input verification-input" 
						type="text" 
						placeholder="请输入验证码"
						v-model="registerForm.verificationCode"
						maxlength="6"
					/>
					<button class="send-code-btn" @click="sendVerificationCode">
						{{ codeButtonText }}
					</button>
				</view>
			</view>
		</view>
		
		<!-- 注册按钮 -->
		<button class="register-submit-btn" @click="handleRegister">
			立即注册
		</button>
		
		<!-- 第三方登录 -->
		<view class="divider-container">
			<view class="divider-line"></view>
			<text class="divider-text">第三方登录</text>
			<view class="divider-line"></view>
		</view>
		
		<view class="social-login">
			<view class="social-btn" @click="loginWithWechat">
				<image class="social-icon" src="/static/type10/weixin.png" mode="aspectFit"></image>
			</view>
			<view class="social-btn" @click="loginWithPhone">
				<image class="social-icon" src="/static/type10/phone.png" mode="aspectFit"></image>
			</view>
			<view class="social-btn" @click="loginWithQQ">
				<image class="social-icon" src="/static/type10/qq.png" mode="aspectFit"></image>
			</view>
		</view>
		
		<!-- 返回登录 -->
		<view class="back-to-login">
			<text class="login-link" @click="goToLogin">已有账号？立即登录</text>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			registerForm: {
				phone: '',
				password: '',
				confirmPassword: '',
				verificationCode: ''
			},
			codeButtonText: '发送验证码',
			countdown: 0,
			countdownTimer: null
		}
	},
	methods: {
		// 发送验证码
		sendVerificationCode() {
			if (!this.registerForm.phone) {
				uni.showToast({
					title: '请输入手机号',
					icon: 'none'
				});
				return;
			}
			
			// 手机号格式验证
			const phoneRegex = /^1[3-9]\d{9}$/;
			if (!phoneRegex.test(this.registerForm.phone)) {
				uni.showToast({
					title: '请输入正确的手机号',
					icon: 'none'
				});
				return;
			}
			
			if (this.countdown > 0) return;
			
			// 模拟发送验证码
			uni.showToast({
				title: '验证码已发送',
				icon: 'success'
			});
			
			// 开始倒计时
			this.countdown = 60;
			this.codeButtonText = `${this.countdown}s`;
			this.countdownTimer = setInterval(() => {
				this.countdown--;
				if (this.countdown <= 0) {
					clearInterval(this.countdownTimer);
					this.codeButtonText = '发送验证码';
				} else {
					this.codeButtonText = `${this.countdown}s`;
				}
			}, 1000);
		},
		
		// 注册
		handleRegister() {
			// 表单验证
			if (!this.registerForm.phone) {
				uni.showToast({
					title: '请输入手机号',
					icon: 'none'
				});
				return;
			}
			
			if (!this.registerForm.password) {
				uni.showToast({
					title: '请输入密码',
					icon: 'none'
				});
				return;
			}
			
			if (this.registerForm.password !== this.registerForm.confirmPassword) {
				uni.showToast({
					title: '两次密码输入不一致',
					icon: 'none'
				});
				return;
			}
			
			if (!this.registerForm.verificationCode) {
				uni.showToast({
					title: '请输入验证码',
					icon: 'none'
				});
				return;
			}
			
			// 模拟注册请求
			uni.showLoading({
				title: '注册中...'
			});
			
			setTimeout(() => {
				uni.hideLoading();
				uni.showToast({
					title: '注册成功',
					icon: 'success'
				});
				
				// 注册成功后跳转到个人信息完善页面
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/profile-setup/profile-setup'
					});
				}, 1500);
			}, 2000);
		},
		
		// 微信登录
		loginWithWechat() {
			uni.showToast({
				title: '微信登录',
				icon: 'none'
			});
		},
		
		// 电话登录
		loginWithPhone() {
			uni.showToast({
				title: '电话登录',
				icon: 'none'
			});
		},
		
		// QQ登录
		loginWithQQ() {
			uni.showToast({
				title: 'QQ登录',
				icon: 'none'
			});
		},
		
		// 返回登录页面
		goToLogin() {
			uni.navigateBack();
		}
	},
	
	onUnload() {
		// 清除定时器
		if (this.countdownTimer) {
			clearInterval(this.countdownTimer);
		}
	}
}
</script>

<style scoped>
/* 容器样式 */
.register-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	min-height: 100vh;
	width: 100vw;
	background: #FFFFFF;
	padding: 40rpx 20rpx;
	box-sizing: border-box;
}

/* Logo区域 */
.logo-section {
	margin-bottom: 20rpx;
}

.logo {
	width: 250rpx;
	height: 226rpx;
}

/* 品牌标题 */
.brand-title {
	font-family: 'Inter', sans-serif;
	font-style: normal;
	font-weight: 700;
	font-size: 64rpx;
	line-height: 78rpx;
	color: #000000;
	margin-bottom: 10rpx;
}

.brand-subtitle {
	font-family: 'Inter', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx;
	line-height: 38rpx;
	color: #333333;
	margin-bottom: 40rpx;
}

/* 注册卡片 */
.register-card {
	width: 700rpx;
	background: #FFFFFF;
	box-shadow: 0px 4rpx 20rpx rgba(193, 184, 232, 0.5);
	border-radius: 20rpx;
	padding: 40rpx;
	margin-bottom: 60rpx;
}

/* 表单组 */
.form-group {
	margin-bottom: 40rpx;
}

.form-group:last-child {
	margin-bottom: 0;
}

.form-label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	display: block;
	margin-bottom: 20rpx;
}

/* 输入框容器 */
.input-container {
	background: #F5F5F5;
	border-radius: 20rpx;
	padding: 0 30rpx;
	height: 80rpx;
	display: flex;
	align-items: center;
}

.form-input {
	flex: 1;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	background: transparent;
	border: none;
	outline: none;
}

.form-input::placeholder {
	color: #757575;
}

/* 验证码容器 */
.verification-container {
	padding-right: 20rpx;
}

.verification-input {
	margin-right: 20rpx;
	flex: 1;
}

.send-code-btn {
	width: 200rpx;
	height: 60rpx;
	background: #8A70C9;
	border-radius: 10rpx;
	border: none;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #FFFFFF;
	display: flex;
	align-items: center;
	justify-content: center;
	white-space: nowrap;
}

.send-code-btn:active {
	transform: scale(0.95);
}

/* 注册按钮 */
.register-submit-btn {
	width: 700rpx;
	height: 100rpx;
	background: #8A70C9;
	border-radius: 20rpx;
	border: none;
	font-family: 'Inter', sans-serif;
	font-style: normal;
	font-weight: 600;
	font-size: 36rpx;
	line-height: 44rpx;
	color: #FFFFFF;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 60rpx;
}

.register-submit-btn:active {
	transform: scale(0.98);
	background: #7A5FB8;
}

/* 分割线 */
.divider-container {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 700rpx;
	margin-bottom: 40rpx;
	position: relative;
}

.divider-line {
	width: 210rpx;
	height: 2rpx;
	background: #757575;
}

.divider-text {
	font-family: 'Inter', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #757575;
	background: #FFFFFF;
	padding: 0 20rpx;
	text-align: center;
}

/* 社交登录 */
.social-login {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 60rpx;
	margin-bottom: 40rpx;
}

.social-btn {
	width: 70rpx;
	height: 70rpx;
	background: #FFFFFF;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0px 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.social-btn:active {
	transform: scale(0.95);
}

.social-icon {
	width: 70rpx;
	height: 70rpx;
}

/* 返回登录 */
.back-to-login {
	text-align: center;
}

.login-link {
	font-family: 'Inter', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 28rpx;
	line-height: 34rpx;
	color: #8A70C9;
	text-decoration: underline;
}

.login-link:active {
	opacity: 0.7;
}
</style>
