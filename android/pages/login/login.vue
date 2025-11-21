<template>
	<view class="login-container">
		<!-- Logo区域 -->
		<view class="logo-section">
			<image class="logo" src="/static/type10/logo.png" mode="aspectFit"></image>
		</view>
		
		<!-- 品牌标题 -->
		<view class="brand-section">
			<text class="brand-title">咚 咚</text>
			<text class="brand-desc">一个社团交流场地</text>
		</view>
		
		<!-- 登录卡片 -->
		<view class="login-card">
			<!-- 本机号码一键登录 -->
			<button class="login-btn primary-btn" @tap="handlePhoneLogin">
				本机号码一键登录
			</button>
			
			<!-- 手机验证码登录 -->
			<button class="login-btn secondary-btn" @tap="handleSmsLogin">
				手机验证码登录
			</button>
			
			<!-- 注册账号 -->
			<button class="login-btn secondary-btn" @tap="handleRegister">
				注册账号
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
			isLoading: false
		}
	},
	onLoad() {
		console.log('登录页面加载完成')
	},
	methods: {
		// 本机号码一键登录
		async handlePhoneLogin() {
			this.isLoading = true
			
			try {
				// 模拟一键登录
				await this.simulateLogin('phone')
				
				// 保存登录状态
				uni.setStorageSync('userToken', 'phone_login_token')
				uni.setStorageSync('loginType', 'phone')
				
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
				uni.showToast({
					title: '登录失败，请重试',
					icon: 'none'
				})
			} finally {
				this.isLoading = false
			}
		},
		
		// 手机验证码登录
		handleSmsLogin() {
			uni.navigateTo({
				url: '/pages/phone-login/phone-login'
			})
		},
		
		// 注册账号
		handleRegister() {
			uni.navigateTo({
				url: '/pages/register/register'
			})
		},
		
		// 微信登录
		async loginWithWechat() {
			this.isLoading = true
			
			try {
				// 模拟微信登录
				await this.simulateLogin('wechat')
				
				// 保存登录状态
				uni.setStorageSync('userToken', 'wechat_login_token')
				uni.setStorageSync('loginType', 'wechat')
				
				uni.showToast({
					title: '微信登录成功',
					icon: 'success'
				})
				
				// 跳转到首页
				setTimeout(() => {
					uni.reLaunch({
						url: '/pages/index/index'
					})
				}, 1500)
				
			} catch (error) {
				uni.showToast({
					title: '微信登录失败',
					icon: 'none'
				})
			} finally {
				this.isLoading = false
			}
		},
		
		// QQ登录
		async loginWithQQ() {
			this.isLoading = true
			
			try {
				// 模拟QQ登录
				await this.simulateLogin('qq')
				
				// 保存登录状态
				uni.setStorageSync('userToken', 'qq_login_token')
				uni.setStorageSync('loginType', 'qq')
				
				uni.showToast({
					title: 'QQ登录成功',
					icon: 'success'
				})
				
				// 跳转到首页
				setTimeout(() => {
					uni.reLaunch({
						url: '/pages/index/index'
					})
				}, 1500)
				
			} catch (error) {
				uni.showToast({
					title: 'QQ登录失败',
					icon: 'none'
				})
			} finally {
				this.isLoading = false
			}
		},
		
		// 模拟登录请求
		simulateLogin(type) {
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					// 模拟登录成功
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
.login-container {
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
	margin-top: 80rpx;
	margin-bottom: 60rpx;
	width: 250rpx;
	height: 226rpx;
}

.logo {
	width: 100%;
	height: 100%;
}

/* 品牌标题区域 */
.brand-section {
	margin-bottom: 80rpx;
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
	color: #757575;
	display: block;
}

/* 登录卡片 */
.login-card {
	position: relative;
	width: 700rpx;
	height: 440rpx;
	background: #FFFFFF;
	box-shadow: 0px 4rpx 20rpx rgba(193, 184, 232, 0.5);
	border-radius: 20rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 0;
	margin-bottom: 60rpx;
}

/* 登录按钮 */
.login-btn {
	width: 620rpx;
	height: 80rpx;
	border-radius: 20rpx;
	border: none;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx;
	line-height: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
	margin: 30rpx 0;
}

/* 本机号码一键登录按钮 */
.primary-btn {
	background: #8A70C9;
	color: #FFFFFF;
}

/* 手机验证码登录按钮和注册账号按钮 */
.secondary-btn {
	background: #FFFFFF;
	border: 2rpx solid #BCA8F0;
	color: #333333;
}

.login-btn:active {
	transform: scale(0.98);
	opacity: 0.8;
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
	gap: 100rpx;
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

/* 加载状态 */
.login-btn.loading {
	opacity: 0.6;
	pointer-events: none;
}

.login-btn.loading::after {
	content: '';
	width: 20rpx;
	height: 20rpx;
	border: 2rpx solid transparent;
	border-top: 2rpx solid currentColor;
	border-radius: 50%;
	animation: spin 1s linear infinite;
	margin-left: 10rpx;
}

@keyframes spin {
	0% { transform: rotate(0deg); }
	100% { transform: rotate(360deg); }
}
</style>