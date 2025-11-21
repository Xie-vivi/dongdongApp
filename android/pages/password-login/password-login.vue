<template>
	<view class="password-login-container">
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
			
			<!-- 密码输入 -->
			<view class="form-group">
				<text class="form-label">密码</text>
				<view class="password-container">
					<input 
						class="form-input password-input" 
						:type="showPassword ? 'text' : 'password'"
						placeholder="请输入密码" 
						v-model="password"
						@input="onPasswordInput"
					/>
					<view class="password-toggle" @tap="togglePassword">
						<text class="toggle-text">{{ showPassword ? '隐藏' : '显示' }}</text>
					</view>
				</view>
				<view class="forgot-password" @tap="handleForgotPassword">
					<text class="forgot-text">忘记密码</text>
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
			
			<!-- 切换验证码登录按钮 -->
			<button class="switch-login-btn" @tap="handleSwitchLogin">
				切换验证码登录
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
			password: '',
			showPassword: false,
			isLoading: false
		}
	},
	computed: {
		// 是否可以登录
		canLogin() {
			return this.phoneNumber.length === 11 && 
				   /^1[3-9]\d{9}$/.test(this.phoneNumber) && 
				   this.password.length >= 6
		}
	},
	onLoad() {
		console.log('账号密码登录页面加载完成')
	},
	methods: {
		// 手机号输入处理
		onPhoneInput(e) {
			this.phoneNumber = e.detail.value
		},
		
		// 密码输入处理
		onPasswordInput(e) {
			this.password = e.detail.value
		},
		
		// 切换密码显示/隐藏
		togglePassword() {
			this.showPassword = !this.showPassword
		},
		
		// 忘记密码
		handleForgotPassword() {
			uni.showToast({
				title: '请联系客服重置密码',
				icon: 'none'
			})
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
				uni.setStorageSync('userToken', 'password_login_token')
				uni.setStorageSync('loginType', 'password')
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
		
		// 切换验证码登录
		handleSwitchLogin() {
			uni.navigateTo({
				url: '/pages/phone-login/phone-login'
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
		
		// 模拟登录验证
		simulateLogin() {
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					// 模拟账号密码验证 (这里可以设置测试账号)
					if (this.phoneNumber === '13800138000' && this.password === '123456') {
						resolve({ success: true })
					} else if (this.password === '123456') { // 任何手机号，密码123456都能登录
						resolve({ success: true })
					} else {
						reject(new Error('账号或密码错误'))
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
.password-login-container {
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

/* 密码容器 */
.password-container {
	position: relative;
	display: flex;
	align-items: center;
}

.password-input {
	flex: 1;
	padding-right: 100rpx;
}

.password-toggle {
	position: absolute;
	right: 30rpx;
	top: 50%;
	transform: translateY(-50%);
	cursor: pointer;
}

.toggle-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 28rpx;
	line-height: 40rpx;
	color: #8A70C9;
}

.forgot-password {
	margin-top: 20rpx;
	text-align: right;
}

.forgot-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 28rpx;
	line-height: 40rpx;
	color: #8A70C9;
	cursor: pointer;
}

.forgot-text:active {
	opacity: 0.7;
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

/* 切换验证码登录按钮 */
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
