<template>
	<view class="phone-login-container">
		<!-- Logo区域 -->
		<view class="logo-section">
			<image class="logo" src="/static/type10/logo.png" mode="aspectFit"></image>
		</view>
		
		<!-- 品牌标题 -->
		<view class="brand-section">
			<text class="brand-title">咚 咚</text>
			<text class="brand-desc">一个社团交流场地</text>
		</view>
		
		<!-- 登录表单卡片 -->
		<view class="login-form-card">
			<!-- 手机号输入 -->
			<view class="form-group">
				<text class="form-label">手机号</text>
				<view class="input-container">
					<input 
						class="form-input" 
						type="number" 
						placeholder="请输入手机号" 
						v-model="phoneNumber"
						maxlength="11"
						@input="onPhoneInput"
					/>
				</view>
			</view>
			
			<!-- 验证码输入 -->
			<view class="form-group">
				<text class="form-label">验证码</text>
				<view class="verification-container">
					<input 
						class="form-input verification-input" 
						type="number" 
						placeholder="请输入验证码" 
						v-model="verificationCode"
						maxlength="6"
					/>
					<button 
						class="send-code-btn" 
						:disabled="!canSendCode || countdown > 0"
						@tap="sendVerificationCode"
					>
						{{ countdown > 0 ? `重新发送(${countdown}s)` : '发送验证码' }}
					</button>
				</view>
			</view>
			
			<!-- 登录按钮 -->
			<button 
				class="login-submit-btn" 
				:disabled="!canLogin"
				:class="{ 'active': canLogin }"
				@tap="handleLogin"
			>
				登录
			</button>
			
			<!-- 注册账号按钮 -->
			<button class="register-btn" @tap="handleRegister">
				注册账号
			</button>
			
			<!-- 切换密码登录按钮 -->
			<button class="switch-login-btn" @tap="handleSwitchLogin">
				切换密码登录
			</button>
		</view>
		
		<!-- 第三方登录 -->
		<view class="third-party-section">
			<view class="divider-section">
				<view class="divider-line"></view>
				<text class="divider-text">第三方登录</text>
				<view class="divider-line"></view>
			</view>
			
			<view class="social-login">
				<view class="social-btn wechat" @tap="loginWithWechat">
					<image class="social-icon" src="/static/type10/weixin.png" mode="aspectFit"></image>
				</view>
				<view class="social-btn phone" @tap="loginWithPhone">
					<image class="social-icon" src="/static/type10/phone.png" mode="aspectFit"></image>
				</view>
				<view class="social-btn qq" @tap="loginWithQQ">
					<image class="social-icon" src="/static/type10/qq.png" mode="aspectFit"></image>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			phoneNumber: '',
			verificationCode: '',
			countdown: 0,
			timer: null,
			isLoading: false
		}
	},
	computed: {
		// 是否可以发送验证码
		canSendCode() {
			return this.phoneNumber.length === 11 && /^1[3-9]\d{9}$/.test(this.phoneNumber)
		},
		// 是否可以登录
		canLogin() {
			return this.canSendCode && this.verificationCode.length === 6
		}
	},
	onLoad() {
		console.log('手机验证码登录页面加载完成')
	},
	onUnload() {
		// 清理定时器
		if (this.timer) {
			clearInterval(this.timer)
		}
	},
	methods: {
		// 手机号输入处理
		onPhoneInput(e) {
			this.phoneNumber = e.detail.value
		},
		
		// 发送验证码
		async sendVerificationCode() {
			if (!this.canSendCode) {
				uni.showToast({
					title: '请输入正确的手机号',
					icon: 'none'
				})
				return
			}
			
			try {
				// 模拟发送验证码请求
				uni.showLoading({
					title: '发送中...'
				})
				
				await this.simulateSendCode()
				
				uni.hideLoading()
				uni.showToast({
					title: '验证码已发送',
					icon: 'success'
				})
				
				// 开始倒计时
				this.startCountdown()
				
			} catch (error) {
				uni.hideLoading()
				uni.showToast({
					title: '发送失败，请重试',
					icon: 'none'
				})
			}
		},
		
		// 开始倒计时
		startCountdown() {
			this.countdown = 60
			this.timer = setInterval(() => {
				this.countdown--
				if (this.countdown <= 0) {
					clearInterval(this.timer)
					this.timer = null
				}
			}, 1000)
		},
		
		// 登录处理
		async handleLogin() {
			if (!this.canLogin) {
				uni.showToast({
					title: '请填写完整信息',
					icon: 'none'
				})
				return
			}
			
			this.isLoading = true
			
			try {
				uni.showLoading({
					title: '登录中...'
				})
				
				// 模拟登录验证
				await this.simulateLogin()
				
				// 保存登录状态
				uni.setStorageSync('userToken', 'phone_sms_login_token')
				uni.setStorageSync('loginType', 'phone_sms')
				uni.setStorageSync('userPhone', this.phoneNumber)
				
				uni.hideLoading()
				uni.showToast({
					title: '登录成功',
					icon: 'success'
				})
				
				// 跳转到首页
				setTimeout(() => {
					uni.reLaunch({
						url: '/pages/index/index'
					})
				}, 1500)
				
			} catch (error) {
				uni.hideLoading()
				uni.showToast({
					title: error.message || '登录失败，请重试',
					icon: 'none'
				})
			} finally {
				this.isLoading = false
			}
		},
		
		// 注册账号
		handleRegister() {
			uni.navigateTo({
				url: '/pages/register/register'
			})
		},
		
		// 切换密码登录
		handleSwitchLogin() {
			uni.navigateTo({
				url: '/pages/password-login/password-login'
			})
		},
		
		// 微信登录
		async loginWithWechat() {
			try {
				uni.showLoading({
					title: '微信登录中...'
				})
				
				await this.simulateThirdPartyLogin('wechat')
				
				uni.setStorageSync('userToken', 'wechat_login_token')
				uni.setStorageSync('loginType', 'wechat')
				
				uni.hideLoading()
				uni.showToast({
					title: '微信登录成功',
					icon: 'success'
				})
				
				setTimeout(() => {
					uni.reLaunch({
						url: '/pages/index/index'
					})
				}, 1500)
				
			} catch (error) {
				uni.hideLoading()
				uni.showToast({
					title: '微信登录失败',
					icon: 'none'
				})
			}
		},
		
		// 本机号码登录
		async loginWithPhone() {
			try {
				uni.showLoading({
					title: '本机号码登录中...'
				})
				
				await this.simulateThirdPartyLogin('phone')
				
				uni.setStorageSync('userToken', 'phone_auto_login_token')
				uni.setStorageSync('loginType', 'phone_auto')
				
				uni.hideLoading()
				uni.showToast({
					title: '登录成功',
					icon: 'success'
				})
				
				setTimeout(() => {
					uni.reLaunch({
						url: '/pages/index/index'
					})
				}, 1500)
				
			} catch (error) {
				uni.hideLoading()
				uni.showToast({
					title: '本机号码登录失败',
					icon: 'none'
				})
			}
		},
		
		// QQ登录
		async loginWithQQ() {
			try {
				uni.showLoading({
					title: 'QQ登录中...'
				})
				
				await this.simulateThirdPartyLogin('qq')
				
				uni.setStorageSync('userToken', 'qq_login_token')
				uni.setStorageSync('loginType', 'qq')
				
				uni.hideLoading()
				uni.showToast({
					title: 'QQ登录成功',
					icon: 'success'
				})
				
				setTimeout(() => {
					uni.reLaunch({
						url: '/pages/index/index'
					})
				}, 1500)
				
			} catch (error) {
				uni.hideLoading()
				uni.showToast({
					title: 'QQ登录失败',
					icon: 'none'
				})
			}
		},
		
		// 模拟发送验证码
		simulateSendCode() {
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					if (Math.random() > 0.1) { // 90%成功率
						resolve({ success: true })
					} else {
						reject(new Error('发送失败'))
					}
				}, 1000)
			})
		},
		
		// 模拟登录验证
		simulateLogin() {
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					if (this.verificationCode === '123456') { // 模拟验证码验证
						resolve({ success: true })
					} else {
						reject(new Error('验证码错误'))
					}
				}, 1500)
			})
		},
		
		// 模拟第三方登录
		simulateThirdPartyLogin(type) {
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					if (Math.random() > 0.1) { // 90%成功率
						resolve({ type, success: true })
					} else {
						reject(new Error('登录失败'))
					}
				}, 1500)
			})
		}
	}
}
</script>

<style scoped>
.phone-login-container {
	position: relative;
	width: 100vw;
	height: 100vh;
	background: #FFFFFF;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	overflow: hidden;
}

/* Logo区域 */
.logo-section {
	margin-top: 60rpx;
	margin-bottom: 40rpx;
	width: 250rpx;
	height: 226rpx;
}

.logo {
	width: 100%;
	height: 100%;
}

/* 品牌标题区域 */
.brand-section {
	margin-bottom: 40rpx;
	text-align: center;
}

.brand-title {
	font-family: 'Inter', sans-serif;
	font-style: normal;
	font-weight: 700;
	font-size: 64rpx; /* 32px * 2 */
	line-height: 78rpx; /* 39px * 2 */
	color: #000000;
	display: block;
	margin-bottom: 4rpx;
}

.brand-desc {
	font-family: 'Inter', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 38rpx; /* 19px * 2 */
	color: #333333;
	display: block;
}

/* 登录表单卡片 */
.login-form-card {
	position: relative;
	width: 700rpx;
	height: 848rpx;
	background: #FFFFFF;
	box-shadow: 0px 4rpx 20rpx rgba(193, 184, 232, 0.5);
	border-radius: 20rpx;
	display: flex;
	flex-direction: column;
	padding: 40rpx 40rpx;
	margin-bottom: 40rpx;
}

/* 表单组 */
.form-group {
	margin-bottom: 40rpx;
}

.form-label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 44rpx; /* 22px * 2 */
	color: #333333;
	display: block;
	margin-bottom: 20rpx;
}

/* 输入框容器 */
.input-container {
	position: relative;
}

.form-input {
	width: 100%;
	height: 80rpx; /* 40px * 2 */
	background: #F5F5F5;
	border-radius: 20rpx; /* 10px * 2 */
	border: none;
	padding: 0 30rpx;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 44rpx; /* 22px * 2 */
	color: #333333;
	box-sizing: border-box;
}

.form-input::placeholder {
	color: #757575;
}

/* 验证码容器 */
.verification-container {
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.verification-input {
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

.send-code-btn:disabled {
	background: #CCCCCC;
	color: #999999;
}

.send-code-btn:active:not(:disabled) {
	transform: scale(0.95);
}

/* 登录按钮 */
.login-submit-btn {
	width: 100%;
	height: 80rpx; /* 40px * 2 */
	background: #EAEEFD;
	border-radius: 20rpx; /* 10px * 2 */
	border: none;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 44rpx; /* 22px * 2 */
	color: #757575;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 40rpx;
	transition: all 0.3s ease;
}

.login-submit-btn.active {
	background: #8A70C9;
	color: #FFFFFF;
}

.login-submit-btn:active.active {
	transform: scale(0.98);
}

/* 注册账号按钮 */
.register-btn {
	width: 100%;
	height: 80rpx; /* 40px * 2 */
	background: #FFFFFF;
	border: 2rpx solid #C1B8E8;
	border-radius: 20rpx; /* 10px * 2 */
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 44rpx; /* 22px * 2 */
	color: #333333;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 40rpx;
}

.register-btn:active {
	transform: scale(0.98);
	background: #F5F5F5;
}

/* 切换密码登录按钮 */
.switch-login-btn {
	width: 100%;
	height: 80rpx; /* 40px * 2 */
	background: #FFFFFF;
	border: 2rpx solid #C1B8E8;
	border-radius: 20rpx; /* 10px * 2 */
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 44rpx; /* 22px * 2 */
	text-align: center;
	color: #333333;
	display: flex;
	align-items: center;
	justify-content: center;
}

.switch-login-btn:active {
	transform: scale(0.98);
	background: #F5F5F5;
}

/* 第三方登录区域 */
.third-party-section {
	position: relative;
	width: 700rpx;
}

/* 分割线区域 */
.divider-section {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 54rpx;
	width: 100%;
}

/* 分割线 */
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

/* 社交登录按钮 */
.social-login {
	position: relative;
	width: 100%;
	height: 70rpx;
	display: flex;
	justify-content: center;
	gap: 95rpx;
	align-items: center;
}

/* 社交登录按钮 */
.social-btn {
	width: 70rpx;
	height: 70rpx;
	border-radius: 50%;
	background: #FFFFFF;
	cursor: pointer;
	transition: all 0.3s ease;
}

.social-btn:active {
	transform: scale(0.9);
}

.social-icon {
	width: 70rpx;
	height: 70rpx;
}
</style>
