<template>
	<view class="message-container">
		<!-- 顶部状态栏占位 -->
		<view class="status-bar"></view>
		
		<!-- 顶部导航栏 -->
		<view class="top-nav">
			<view class="nav-center">
				<text class="nav-title">消息</text>
			</view>
		</view>
		
		<!-- 消息统计卡片 -->
		<view class="message-stats-card">
			<view class="stats-item" @click="goToPage('like-collect')">
				<view class="stats-icon like-icon">
					<image src="/static/heart-fill.png" class="icon-img"></image>
				</view>
				<view class="stats-badge" v-if="stats.likeCount > 0">
					<text class="badge-text">{{ stats.likeCount }}</text>
				</view>
				<text class="stats-text">点赞收藏</text>
			</view>
			
			<view class="stats-item" @click="goToPage('follow-subscribe')">
				<view class="stats-icon follow-icon">
					<image src="/static/user-add-fill.png" class="icon-img"></image>
				</view>
				<view class="stats-badge" v-if="stats.followCount > 0">
					<text class="badge-text">{{ stats.followCount }}</text>
				</view>
				<text class="stats-text">关注订阅</text>
			</view>
			
			<view class="stats-item" @click="goToPage('comment-at')">
				<view class="stats-icon comment-icon">
					<image src="/static/chat-forward-fill.png" class="icon-img"></image>
				</view>
				<view class="stats-badge" v-if="stats.commentCount > 0">
					<text class="badge-text">{{ stats.commentCount }}</text>
				</view>
				<text class="stats-text">评论@转发</text>
			</view>
		</view>
		
		<!-- 消息列表 -->
		<view class="message-list">
			<view class="message-item" v-for="(message, index) in messageList" :key="index" @click="openMessage(message)">
				<view class="message-avatar">
					<image :src="message.avatar" class="avatar-img"></image>
					<view class="message-count-badge" v-if="message.unreadCount > 0">
						<text class="count-text">{{ message.unreadCount }}</text>
					</view>
				</view>
				
				<view class="message-content">
					<view class="message-header">
						<text class="message-name">{{ message.name }}</text>
						<text class="message-time">{{ message.time }}</text>
					</view>
					<text class="message-preview">{{ message.preview }}</text>
				</view>
			</view>
		</view>
		
		<!-- 底部导航栏 -->
		<BottomNavigation :currentPage="'message'" :messageCount="totalUnreadCount" />
	</view>
</template>

<script>
import BottomNavigation from '@/components/BottomNavigation.vue'

export default {
	name: 'MessagePage',
	components: {
		BottomNavigation
	},
	data() {
		return {
			stats: {
				likeCount: 5,
				followCount: 55,
				commentCount: 294
			},
			messageList: [
				{
					id: 1,
					type: 'private',
					name: '会吃西瓜的小鸭纸',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					preview: '今天出去打球吗？明个个？',
					time: '19:03',
					unreadCount: 99
				},
				{
					id: 2,
					type: 'group',
					name: 'lol群',
					avatar: '/static/follow/follow-users-section/Ellipse 9.png',
					preview: '国服塞拉斯：你又欠打了？偷我图干嘛',
					time: '19:02',
					unreadCount: 15,
					notice: '入群须知: 大家可以随意组队打lol   不要把群...',
					activity: 'lol线下比赛   2025.8.25   17:00-18:00'
				},
				{
					id: 3,
					type: 'private',
					name: '你叫什么名字',
					avatar: '/static/follow/follow-users-section/Ellipse 11.png',
					preview: '我们聚餐喝酒[合情]',
					time: '13:14',
					unreadCount: 299
				},
				{
					id: 4,
					type: 'group',
					name: '羽毛球群',
					avatar: '/static/follow/follow-users-section/Ellipse 13.png',
					preview: '不知名用户：这是老人新手警了',
					time: '13:00',
					unreadCount: 0
				},
				{
					id: 5,
					type: 'group',
					name: '原神集会',
					avatar: '/static/follow/follow-users-section/Ellipse 14.png',
					preview: '群主：发支付宝账号码吧',
					time: '10:21',
					unreadCount: 901
				},
				{
					id: 6,
					type: 'group',
					name: '鸣潮集会',
					avatar: '/static/follow/follow-users-section/Ellipse 15.png',
					preview: '群主：有没有人陪每日刷在不来点个赞...',
					time: '09:05',
					unreadCount: 99
				},
				{
					id: 7,
					type: 'group',
					name: '锋芒值YDs',
					avatar: '/static/follow/follow-users-section/Ellipse 16.png',
					preview: '请奇大人的朋：暴雪游戏大人的朋友[图...',
					time: '08:04',
					unreadCount: 0
				}
			]
		}
	},
	computed: {
		totalUnreadCount() {
			return this.messageList.reduce((total, message) => total + message.unreadCount, 0)
		}
	},
	methods: {
		goToPage(page) {
			const routes = {
				'like-collect': '/pages/message/like-collect',
				'follow-subscribe': '/pages/message/follow-subscribe',
				'comment-at': '/pages/message/comment-at'
			}
			
			if (routes[page]) {
				uni.navigateTo({
					url: routes[page]
				})
			}
		},
		openMessage(message) {
			// 跳转到聊天页面（私聊或群聊）
			let url = `/pages/message/chat-detail?type=${message.type || 'private'}&id=${message.id}&name=${encodeURIComponent(message.name)}&avatar=${encodeURIComponent(message.avatar)}`
			
			// 如果是群聊，传递群公告和活动信息
			if (message.type === 'group') {
				if (message.notice) {
					url += `&notice=${encodeURIComponent(message.notice)}`
				}
				if (message.activity) {
					url += `&activity=${encodeURIComponent(message.activity)}`
				}
			}
			
			uni.navigateTo({
				url: url
			})
		}
	}
}
</script>

<style scoped>
.message-container {
	min-height: 100vh;
	background: #F5F3FF;
	padding-bottom: 160rpx; /* 为底部导航栏留空间 */
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
	justify-content: center;
	position: relative;
}

.nav-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 36rpx;
	font-weight: 600;
	color: #333333;
}

.message-stats-card {
	margin: 40rpx 30rpx;
	background: #FFFFFF;
	border-radius: 24rpx;
	padding: 50rpx 20rpx;
	display: flex;
	justify-content: space-around;
	align-items: center;
	box-shadow: 0 4rpx 20rpx rgba(138, 112, 201, 0.1);
}

.stats-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	position: relative;
	cursor: pointer;
}

.stats-icon {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 16rpx;
	position: relative;
}

.like-icon {
	background: rgba(138, 112, 201, 0.1);
}

.follow-icon {
	background: rgba(138, 112, 201, 0.1);
}

.comment-icon {
	background: rgba(138, 112, 201, 0.1);
}

.icon-img {
	width: 44rpx;
	height: 44rpx;
}

.stats-badge {
	position: absolute;
	top: -8rpx;
	right: -8rpx;
	min-width: 36rpx;
	height: 36rpx;
	background: #8A70C9;
	border: 2rpx solid #FFFFFF;
	border-radius: 18rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0 8rpx;
}

.badge-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 20rpx;
	color: #FFFFFF;
	font-weight: 600;
}

.stats-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #666666;
	font-weight: 500;
}

.message-list {
	margin: 0 30rpx;
}

.message-item {
	background: #FFFFFF;
	border-radius: 20rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
	display: flex;
	align-items: center;
	cursor: pointer;
	transition: transform 0.2s;
}

.message-item:active {
	transform: scale(0.98);
}

.message-avatar {
	position: relative;
	margin-right: 24rpx;
}

.avatar-img {
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
}

.message-count-badge {
	position: absolute;
	top: -8rpx;
	right: -8rpx;
	min-width: 36rpx;
	height: 36rpx;
	background: #FF6B6B;
	border: 2rpx solid #FFFFFF;
	border-radius: 18rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0 8rpx;
}

.count-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 20rpx;
	color: #FFFFFF;
	font-weight: 600;
}

.message-content {
	flex: 1;
}

.message-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12rpx;
}

.message-name {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	font-weight: 600;
	color: #333333;
}

.message-time {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	color: #999999;
}

.message-preview {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #666666;
	line-height: 1.4;
}
</style>
