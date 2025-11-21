<template>
	<view class="like-collect-container">
		<!-- 顶部状态栏占位 -->
		<view class="status-bar"></view>
		
		<!-- 顶部导航栏 -->
		<view class="top-nav">
			<view class="nav-left" @click="goBack">
				<image src="/static/content/back.png" class="back-icon"></image>
			</view>
			<view class="nav-center">
				<text class="nav-title">点赞收藏</text>
			</view>
		</view>
		
		<!-- 消息列表 -->
		<view class="message-list">
			<view class="message-item" v-for="(message, index) in messageList" :key="index" @click="openMessage(message)">
				<view class="message-avatar">
					<image :src="message.avatar" class="avatar-img"></image>
				</view>
				
				<view class="message-content">
					<view class="message-header">
						<text class="message-name">{{ message.name }}</text>
						<text class="message-tag">{{ message.tag }}</text>
					</view>
					<text class="message-action">{{ message.action }}</text>
					<text class="message-time">{{ message.time }}</text>
					
					<!-- 引用内容 -->
					<view class="quote-content" v-if="message.quote">
						<text class="quote-text">{{ message.quote }}</text>
					</view>
				</view>
				
				<!-- 相关图片 -->
				<view class="message-image" v-if="message.image">
					<image :src="message.image" class="post-img"></image>
				</view>
			</view>
		</view>
		
		<!-- 历史记录入口 -->
		<view class="history-section">
			<text class="history-link">历史点赞收藏</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'LikeCollectPage',
	data() {
		return {
			messageList: [
				{
					id: 1,
					name: '会吃西瓜的小鸭纸',
					tag: '你的好友',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					action: '赞了你的评论',
					time: '1小时前',
					quote: '钻石段位真的易如反掌美，反正我黑铁...',
					image: '/static/follow/Rectangle 107.png'
				},
				{
					id: 2,
					name: '会吃西瓜的小鸭纸',
					tag: '你的好友',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					action: '赞了你的评论',
					time: '2小时前',
					quote: '别骂了别骂了',
					image: '/static/follow/Rectangle 108.png'
				},
				{
					id: 3,
					name: '不知名用户',
					tag: '你的关注',
					avatar: '/static/follow/follow-users-section/Ellipse 9.png',
					action: '赞了你的评论',
					time: '2小时前',
					quote: '别骂了别骂了',
					image: '/static/follow/Rectangle 109.png'
				},
				{
					id: 4,
					name: '你叫什么名字',
					tag: '你的粉丝',
					avatar: '/static/follow/follow-users-section/Ellipse 11.png',
					action: '赞了你的帖子',
					time: '1天前',
					quote: '',
					image: '/static/follow/Rectangle 107.png'
				},
				{
					id: 5,
					name: '点赞人机',
					tag: '',
					avatar: '/static/follow/follow-users-section/Ellipse 13.png',
					action: '赞了你的评论',
					time: '1天前',
					quote: '别骂了别骂了',
					image: '/static/follow/Rectangle 108.png'
				},
				{
					id: 6,
					name: '点赞人机',
					tag: '',
					avatar: '/static/follow/follow-users-section/Ellipse 14.png',
					action: '收藏了你的帖子',
					time: '1天前',
					quote: '',
					image: '/static/follow/Rectangle 109.png'
				}
			]
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		openMessage(message) {
			// 这里可以跳转到相关帖子详情
			console.log('查看详情:', message)
		}
	}
}
</script>

<style scoped>
.like-collect-container {
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
	align-items: flex-start;
	cursor: pointer;
	transition: transform 0.2s;
}

.message-item:active {
	transform: scale(0.98);
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
	margin-right: 20rpx;
}

.message-header {
	display: flex;
	align-items: center;
	margin-bottom: 8rpx;
	flex-wrap: wrap;
}

.message-name {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	font-weight: 600;
	color: #333333;
	margin-right: 16rpx;
}

.message-tag {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	color: #8A70C9;
	background: rgba(138, 112, 201, 0.1);
	padding: 4rpx 12rpx;
	border-radius: 12rpx;
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
	margin-bottom: 16rpx;
}

.quote-content {
	background: #F8F8F8;
	border-left: 6rpx solid #8A70C9;
	padding: 16rpx 20rpx;
	border-radius: 0 12rpx 12rpx 0;
	margin-top: 16rpx;
}

.quote-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 26rpx;
	color: #666666;
	line-height: 1.4;
}

.message-image {
	width: 120rpx;
	height: 120rpx;
	border-radius: 12rpx;
	overflow: hidden;
	flex-shrink: 0;
}

.post-img {
	width: 100%;
	height: 100%;
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
