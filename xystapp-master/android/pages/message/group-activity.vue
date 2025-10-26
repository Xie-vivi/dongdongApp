<template>
	<view class="activity-container">
		<!-- 顶部导航栏 -->
		<TopNavigation 
			title="活动" 
			:showBack="true" 
			:showMore="false"
		/>
		
		<!-- 活动卡片 -->
		<view class="activity-card">
			<image :src="activityInfo.image" class="activity-image" mode="aspectFill"></image>
			<view class="activity-info">
				<view class="activity-header">
					<text class="activity-title">{{ activityInfo.title }}</text>
					<view class="like-section">
						<image src="/static/关注页/5.png" class="like-icon" mode="aspectFit"></image>
						<text class="like-count">{{ activityInfo.likeCount }}</text>
					</view>
				</view>
				<view class="activity-meta">
					<text class="activity-time">{{ activityInfo.date }}   {{ activityInfo.weekday }}   {{ activityInfo.time }}</text>
					<text class="activity-location">{{ activityInfo.location }}</text>
				</view>
				<view class="activity-footer">
					<view class="activity-tag">
						<text class="tag-text">{{ activityInfo.tag }}</text>
					</view>
					<view class="activity-status">
						<text class="status-text">已报名：{{ activityInfo.signupCount }}人</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import TopNavigation from '@/components/TopNavigation.vue'

export default {
	name: 'GroupActivity',
	components: {
		TopNavigation
	},
	data() {
		return {
			activityInfo: {
				title: 'lol线下比赛',
				image: '/static/关注页/Rectangle 109.png',
				date: '2025.8.25',
				weekday: '周一',
				time: '17:00-18:00',
				location: '体育馆1楼',
				tag: 'lol场',
				signupCount: 30,
				likeCount: 125
			}
		}
	},
	onLoad(options) {
		if (options.title) {
			this.activityInfo.title = decodeURIComponent(options.title)
		}
		if (options.date) {
			this.activityInfo.date = decodeURIComponent(options.date)
		}
		if (options.time) {
			this.activityInfo.time = decodeURIComponent(options.time)
		}
		if (options.location) {
			this.activityInfo.location = decodeURIComponent(options.location)
		}
		if (options.tag) {
			this.activityInfo.tag = decodeURIComponent(options.tag)
		}
	}
}
</script>

<style scoped>
.activity-container {
	min-height: 100vh;
	background: #F6F2FC;
}

.activity-card {
	margin: 20rpx;
	background: #FFFFFF;
	border-radius: 20rpx;
	overflow: hidden;
}

.activity-image {
	width: 100%;
	height: 350rpx;
}

.activity-info {
	padding: 40rpx;
}

.activity-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.activity-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 500;
	font-size: 36rpx;
	line-height: 50rpx;
	color: #333333;
}

.like-section {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.like-icon {
	width: 48rpx;
	height: 48rpx;
	margin-bottom: 5rpx;
}

.like-count {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #333333;
}

.activity-meta {
	margin-bottom: 20rpx;
}

.activity-time,
.activity-location {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	line-height: 40rpx;
	color: #333333;
	display: block;
}

.activity-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-top: 20rpx;
	border-top: 1rpx solid #E6D9F9;
}

.activity-tag {
	background: #EAEEFD;
	border-radius: 8rpx;
	padding: 8rpx 20rpx;
}

.tag-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #333333;
}

.activity-status {
	flex-shrink: 0;
}

.status-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	line-height: 38rpx;
	color: #757575;
}
</style>

