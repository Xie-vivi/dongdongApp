<template>
	<view class="container">
		<!-- 用户信息卡片 -->
		<view class="user-card">
			<view class="avatar">
				<image src="/static/images/avatar.png" mode="aspectFill"></image>
			</view>
			<view class="user-info">
				<text class="username">用户名</text>
				<text class="user-desc">欢迎使用东东社团</text>
			</view>
		</view>
		
		<!-- 功能菜单 -->
		<view class="menu-section">
			<view class="menu-item" @click="handleMenuClick('settings')">
				<view class="menu-icon">⚙️</view>
				<text class="menu-text">设置</text>
				<text class="menu-arrow">></text>
			</view>
			
			<view class="menu-item" @click="handleMenuClick('about')">
				<view class="menu-icon">ℹ️</view>
				<text class="menu-text">关于我们</text>
				<text class="menu-arrow">></text>
			</view>
			
			<view class="menu-item" @click="handleMenuClick('feedback')">
				<view class="menu-icon">💬</view>
				<text class="menu-text">意见反馈</text>
				<text class="menu-arrow">></text>
			</view>
		</view>
		
		<!-- 退出登录按钮 -->
		<view class="logout-section">
			<button class="logout-btn" @click="logout">退出登录</button>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			
		}
	},
	methods: {
		handleMenuClick(type) {
			uni.showToast({
				title: `点击了${type}`,
				icon: 'none'
			})
		},
		logout() {
			uni.showModal({
				title: '确认退出',
				content: '确定要退出登录吗？',
				success: (res) => {
					if (res.confirm) {
						// 清除登录状态
						uni.removeStorageSync('userToken')
						
						// 跳转到登录页面
						uni.reLaunch({
							url: '/pages/login/login'
						})
						
						uni.showToast({
							title: '已退出登录',
							icon: 'success'
						})
					}
				}
			})
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding: 20rpx;
}

.user-card {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border-radius: 20rpx;
	padding: 40rpx;
	display: flex;
	align-items: center;
	margin-bottom: 30rpx;
	box-shadow: 0 10rpx 30rpx rgba(102, 126, 234, 0.3);
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 60rpx;
	overflow: hidden;
	margin-right: 30rpx;
	border: 4rpx solid rgba(255, 255, 255, 0.3);
}

.avatar image {
	width: 100%;
	height: 100%;
}

.user-info {
	flex: 1;
}

.username {
	font-size: 36rpx;
	font-weight: bold;
	color: white;
	display: block;
	margin-bottom: 10rpx;
}

.user-desc {
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.8);
	display: block;
}

.menu-section {
	background: white;
	border-radius: 20rpx;
	margin-bottom: 30rpx;
	overflow: hidden;
	box-shadow: 0 5rpx 15rpx rgba(0, 0, 0, 0.1);
}

.menu-item {
	display: flex;
	align-items: center;
	padding: 30rpx 40rpx;
	border-bottom: 1rpx solid #f0f0f0;
	transition: all 0.3s;
}

.menu-item:last-child {
	border-bottom: none;
}

.menu-item:active {
	background-color: #f8f8f8;
}

.menu-icon {
	font-size: 36rpx;
	margin-right: 30rpx;
	width: 50rpx;
	text-align: center;
}

.menu-text {
	flex: 1;
	font-size: 32rpx;
	color: #333;
}

.menu-arrow {
	font-size: 32rpx;
	color: #ccc;
}

.logout-section {
	padding: 20rpx 0;
}

.logout-btn {
	width: 100%;
	height: 100rpx;
	background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
	border: none;
	border-radius: 20rpx;
	color: white;
	font-size: 32rpx;
	font-weight: bold;
	box-shadow: 0 10rpx 25rpx rgba(255, 107, 107, 0.3);
	transition: all 0.3s;
}

.logout-btn:active {
	transform: translateY(2rpx);
	box-shadow: 0 5rpx 15rpx rgba(255, 107, 107, 0.3);
}
</style>
