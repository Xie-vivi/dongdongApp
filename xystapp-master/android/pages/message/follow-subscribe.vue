<template>
	<view class="follow-subscribe-container">
		<!-- 顶部状态栏占位 -->
		<view class="status-bar"></view>
		
		<!-- 顶部导航栏 -->
		<view class="top-nav">
			<view class="nav-left" @click="goBack">
				<image src="/static/正文/back.png" class="back-icon"></image>
			</view>
			<view class="nav-center">
				<text class="nav-title">关注订阅</text>
			</view>
		</view>
		
		<!-- 消息列表 -->
		<view class="message-list">
			<view class="message-item" v-for="(message, index) in messageList" :key="index">
				<view class="message-avatar">
					<image :src="message.avatar" class="avatar-img"></image>
				</view>
				
				<view class="message-content">
					<view class="message-header">
						<text class="message-name">{{ message.name }}</text>
						<view class="action-btn" :class="{ 'followed': message.isFollowed }" @click="toggleFollow(message)">
							<text class="action-text">{{ message.isFollowed ? '互相关注' : '回关' }}</text>
						</view>
					</view>
					<text class="message-action">{{ message.action }}</text>
					<text class="message-time">{{ message.time }}</text>
				</view>
			</view>
		</view>
		
		<!-- 历史记录入口 -->
		<view class="history-section">
			<text class="history-link">历史关注订阅</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'FollowSubscribePage',
	data() {
		return {
			messageList: [
				{
					id: 1,
					name: '会吃西瓜的小鸭纸',
					avatar: '/static/关注页/follow-users-section/Ellipse 2.png',
					action: '关注了你',
					time: '1小时前',
					isFollowed: true
				},
				{
					id: 2,
					name: '你叫什么名字',
					avatar: '/static/关注页/follow-users-section/Ellipse 11.png',
					action: '关注了你',
					time: '1天前',
					isFollowed: false
				}
			]
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		toggleFollow(message) {
			message.isFollowed = !message.isFollowed
		}
	}
}
</script>

<style scoped>
.follow-subscribe-container {
	min-height: 100vh;
	background: #F5F3FF;
}

.status-bar {
	height: var(--status-bar-height, 44px);
	background: #F5F3FF;
}

.top-nav {
	height: 88rpx;
	background: #F6F2FC;
	display: flex;
	align-items: center;
	position: relative;
	padding: 0 40rpx;
}

.nav-left {
	width: 60rpx;
	height: 60rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
}

.back-icon {
	width: 32rpx;
	height: 32rpx;
}

.nav-center {
	flex: 1;
	display: flex;
	justify-content: center;
}

.nav-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 36rpx;
	font-weight: 600;
	color: #333333;
}

.message-list {
	padding: 0 30rpx;
}

.message-item {
	background: #FFFFFF;
	border-radius: 20rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
	display: flex;
	align-items: center;
}

.message-avatar {
	margin-right: 24rpx;
}

.avatar-img {
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
}

.message-content {
	flex: 1;
}

.message-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 8rpx;
}

.message-name {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	font-weight: 600;
	color: #333333;
}

.action-btn {
	padding: 8rpx 24rpx;
	border-radius: 20rpx;
	border: 2rpx solid #8A70C9;
	background: transparent;
	cursor: pointer;
	transition: all 0.3s;
}

.action-btn.followed {
	background: rgba(138, 112, 201, 0.1);
	border-color: rgba(138, 112, 201, 0.3);
}

.action-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	color: #8A70C9;
	font-weight: 500;
}

.message-action {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #666666;
	margin-bottom: 8rpx;
}

.message-time {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	color: #999999;
}

.history-section {
	padding: 60rpx 0;
	display: flex;
	justify-content: center;
}

.history-link {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	color: #8A70C9;
	font-weight: 600;
	cursor: pointer;
}
</style>
