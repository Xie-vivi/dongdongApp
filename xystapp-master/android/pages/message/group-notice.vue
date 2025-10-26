<template>
	<view class="notice-container">
		<!-- 顶部导航栏 -->
		<TopNavigation 
			title="群公告" 
			:showBack="true" 
			:showMore="false"
		/>
		
		<!-- 公告内容卡片 -->
		<view class="notice-card">
			<view class="notice-content">
				<text class="notice-text">{{ noticeInfo.content }}</text>
			</view>
			<view class="notice-meta">
				<text class="notice-author">会吃西...</text>
				<text class="notice-time">{{ noticeInfo.time }}</text>
			</view>
			<!-- 公告图片 -->
			<image v-if="noticeInfo.image" :src="noticeInfo.image" class="notice-image" mode="aspectFill"></image>
		</view>
	</view>
</template>

<script>
import TopNavigation from '@/components/TopNavigation.vue'

export default {
	name: 'GroupNotice',
	components: {
		TopNavigation
	},
	data() {
		return {
			noticeInfo: {
				content: '入群须知:\n大家可以随意组队打lol\n不要把群聊到了谢谢大家哦\n新进群的都看看我的五杀',
				author: '会吃西瓜的小鸭纸',
				time: '2025年9月12日  17:00',
				image: '/static/关注页/Rectangle 109.png'
			}
		}
	},
	onLoad(options) {
		if (options.content) {
			this.noticeInfo.content = decodeURIComponent(options.content)
		}
		if (options.author) {
			this.noticeInfo.author = decodeURIComponent(options.author)
		}
		if (options.time) {
			this.noticeInfo.time = decodeURIComponent(options.time)
		}
		if (options.image) {
			this.noticeInfo.image = decodeURIComponent(options.image)
		}
	}
}
</script>

<style scoped>
.notice-container {
	min-height: 100vh;
	background: #F6F2FC;
}

.notice-card {
	margin: 20rpx;
	background: #FFFFFF;
	border-radius: 20rpx;
	padding: 40rpx;
}

.notice-content {
	margin-bottom: 30rpx;
}

.notice-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 32rpx;
	line-height: 50rpx;
	color: #333333;
	white-space: pre-wrap;
}

.notice-meta {
	display: flex;
	align-items: center;
	margin-bottom: 30rpx;
}

.notice-author {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	line-height: 38rpx;
	color: #757575;
	margin-right: 20rpx;
}

.notice-time {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	line-height: 38rpx;
	color: #757575;
}

.notice-image {
	width: 100%;
	height: 400rpx;
	border-radius: 16rpx;
}
</style>

