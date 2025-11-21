<template>
	<view class="container">
		<!-- 顶部导航 -->
		<TopNavigation 
			title="浏览记录" 
			:showBack="true" 
			:showMore="false"
			titleColor="#8A70C9"
			@back="goBack"
		/>
		
		<!-- 清空按钮 -->
		<view class="clear-btn-container">
			<view class="clear-btn" @click="clearHistory">
				<text class="clear-text">清空</text>
			</view>
		</view>

		<!-- Tab 切换 -->
		<view class="tab-section">
			<view class="tab-item" :class="{ active: currentTab === 'post' }" @click="switchTab('post')">
				<text class="tab-text">帖子</text>
			</view>
			<view class="tab-item" :class="{ active: currentTab === 'activity' }" @click="switchTab('activity')">
				<text class="tab-text">活动</text>
			</view>
		</view>

		<!-- 内容区域 -->
		<scroll-view scroll-y class="content">
			<!-- 帖子列表 -->
			<view v-if="currentTab === 'post'">
				<!-- 时间分组 -->
				<view v-for="group in groupedPosts" :key="group.date" class="time-group">
					<text class="time-label">{{ group.label }}</text>
					
					<view class="post-card" v-for="post in group.items" :key="post.id">
						<!-- 用户信息 -->
						<view class="post-header">
							<image class="user-avatar-small" :src="post.avatar" mode="aspectFill" />
							<view class="user-info">
								<text class="username">{{ post.username }}</text>
								<text class="post-time">{{ post.time || '昨天 20:15' }}</text>
							</view>
							<view class="follow-btn followed">
								<text class="follow-text">{{ post.followStatus }}</text>
							</view>
						</view>
						
						<!-- 帖子内容 -->
						<view class="post-content">
							<text class="post-title">{{ post.title }}</text>
							<text class="post-text">{{ post.content }}</text>
						</view>
						
						<!-- 标签 -->
						<view class="post-tags" v-if="post.tag">
							<view class="tag">
								<text class="tag-text">{{ post.tag }}</text>
							</view>
						</view>
						
						<!-- 互动区域 -->
						<view class="post-actions">
							<view class="action-item">
								<image class="action-icon" src="/static/follow/5.png"></image>
								<text class="action-count">{{ post.likes }}</text>
							</view>
							<view class="action-item">
								<image class="action-icon" src="/static/follow/6.png"></image>
								<text class="action-count">{{ post.stars }}</text>
							</view>
							<view class="action-item">
								<image class="action-icon" src="/static/follow/7.png"></image>
								<text class="action-count">{{ post.comments }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 活动列表 -->
			<view v-else>
				<!-- 时间分组 -->
				<view v-for="group in groupedActivities" :key="group.date" class="time-group">
					<text class="time-label">{{ group.label }}</text>
					
					<view class="discover-activity-card" v-for="activity in group.items" :key="activity.id">
						<!-- 左侧图片 -->
						<image class="discover-activity-image" :src="activity.image" mode="aspectFill" />
						
						<!-- 右侧内容区域 -->
						<view class="discover-activity-content">
							<!-- 标题和爱心 -->
							<view class="discover-activity-header">
								<text class="discover-activity-title">{{ activity.title }}</text>
								<view class="discover-activity-like">
									<image class="discover-heart-icon" src="/static/discover/1.png" mode="aspectFit" />
									<text class="discover-like-count">{{ activity.likes }}</text>
								</view>
							</view>
							
							<!-- 时间信息 -->
							<view class="discover-activity-datetime">
								<text class="discover-activity-date">{{ activity.date }}</text>
								<text class="discover-activity-day">{{ activity.day }}</text>
								<text class="discover-activity-time">{{ activity.time }}</text>
							</view>
							
							<!-- 地点和标签 -->
							<view class="discover-activity-location-tag">
								<view class="discover-activity-location-wrapper">
									<text class="discover-activity-location">{{ activity.location }}</text>
									<view class="discover-activity-tag">
										<text class="discover-activity-tag-text">{{ activity.tag }}</text>
									</view>
								</view>
								<text class="discover-activity-participants">已报名：{{ activity.participants }}人</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import TopNavigation from '@/components/TopNavigation.vue'

export default {
	components: { TopNavigation },
	data() {
		return {
			currentTab: 'post',
			posts: [
				{
					id: 1,
					username: '这个不是自己的帖子',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					followStatus: '已关注',
					title: '帖子大标题我也写长一点',
					content: '短一点',
					tag: '某某场',
					likes: 21,
					stars: 2,
					comments: 1,
					time: '昨天 20:15',
					date: 'today'
				},
				{
					id: 2,
					username: '假装这个不是自己',
					avatar: '/static/follow/follow-users-section/Ellipse 9.png',
					followStatus: '已关注',
					title: '帖子大标题我也写长一点',
					content: '短一点',
					tag: '某某场',
					likes: 21,
					stars: 2,
					comments: 1,
					time: '昨天 20:15',
					date: 'today'
				},
				{
					id: 3,
					username: '会吃西瓜的小鸭纸',
					avatar: '/static/follow/follow-users-section/Ellipse 11.png',
					followStatus: '已关注',
					title: '这个也不是自己的帖子',
					content: '短一点',
					tag: '某某场',
					likes: 21,
					stars: 2,
					comments: 1,
					time: '昨天 20:15',
					date: 'today'
				},
				{
					id: 4,
					username: '不是自己的',
					avatar: '/static/follow/follow-users-section/Ellipse 13.png',
					followStatus: '已关注',
					title: '',
					content: '',
					tag: '',
					likes: 0,
					stars: 0,
					comments: 0,
					time: '昨天 20:15',
					date: 'today'
				}
			],
			activities: [
				{
					id: 1,
					title: '羽毛球初级训练找人：1v1或2v2都可',
					date: '2025.8.16',
					day: '周六',
					time: '17:00 - 18:00',
					location: '体育馆2楼',
					tag: '羽毛球场',
					participants: 5,
					likes: 21,
					image: '/static/discover/2.png',
					dateGroup: 'today'
				},
				{
					id: 2,
					title: '假装这个活动不一样',
					date: '2025.8.16',
					day: '周六',
					time: '17:00 - 18:00',
					location: '体育馆2楼',
					tag: '羽毛球场',
					participants: 5,
					likes: 21,
					image: '/static/discover/2.png',
					dateGroup: 'yesterday'
				},
				{
					id: 3,
					title: '假装这个活动不一样',
					date: '2025.8.16',
					day: '周六',
					time: '17:00 - 18:00',
					location: '体育馆2楼',
					tag: '羽毛球场',
					participants: 5,
					likes: 21,
					image: '/static/discover/2.png',
					dateGroup: 'week'
				}
			]
		}
	},
	computed: {
		groupedPosts() {
			const groups = {
				today: { label: '今天', items: [] },
				yesterday: { label: '昨天', items: [] },
				week: { label: '一周内', items: [] },
				month: { label: '一月前', items: [] }
			}
			
			this.posts.forEach(post => {
				const group = post.date || 'today'
				if (groups[group]) {
					groups[group].items.push(post)
				}
			})
			
			return Object.values(groups).filter(group => group.items.length > 0)
		},
		groupedActivities() {
			const groups = {
				today: { label: '今天', items: [] },
				yesterday: { label: '昨天', items: [] },
				week: { label: '一周内', items: [] },
				month: { label: '一月前', items: [] }
			}
			
			this.activities.forEach(activity => {
				const group = activity.dateGroup || 'today'
				if (groups[group]) {
					groups[group].items.push(activity)
				}
			})
			
			return Object.values(groups).filter(group => group.items.length > 0)
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		switchTab(tab) {
			this.currentTab = tab
		},
		clearHistory() {
			uni.showModal({
				title: '提示',
				content: '确定要清空浏览记录吗？',
				success: (res) => {
					if (res.confirm) {
						if (this.currentTab === 'post') {
							this.posts = []
						} else {
							this.activities = []
						}
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
	background: #F6F2FC;
	width: 100%;
	box-sizing: border-box;
	display: flex;
	flex-direction: column;
	overflow: hidden;
}

.clear-btn-container {
	position: absolute;
	top: 44px;
	right: 20rpx;
	z-index: 10;
	padding-top: 20rpx;
}

.clear-btn {
	background: #8A70C9;
	border-radius: 16rpx;
	padding: 12rpx 24rpx;
}

.clear-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #FFFFFF;
}

.tab-section {
	display: flex;
	padding: 0 20rpx;
	margin-bottom: 20rpx;
}

.tab-item {
	margin-right: 60rpx;
	padding-bottom: 16rpx;
	position: relative;
}

.tab-item.active::after {
	content: '';
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	height: 4rpx;
	background: #8A70C9;
	border-radius: 2rpx;
}

.tab-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 36rpx;
	color: #666666;
}

.tab-item.active .tab-text {
	color: #8A70C9;
	font-weight: 600;
}

.content {
	flex: 1;
	padding: 0 20rpx;
	width: 100%;
	box-sizing: border-box;
	overflow-x: hidden;
}

.time-group {
	margin-bottom: 40rpx;
}

.time-label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #999999;
	margin-bottom: 20rpx;
	display: block;
}

/* 首页帖子卡片样式 */
.post-card {
	background: #FFFFFF;
	border-radius: 20rpx;
	margin-bottom: 20rpx;
	overflow: hidden;
	width: 100%;
	box-sizing: border-box;
}

/* 帖子头部 */
.post-header {
	display: flex;
	align-items: center;
	padding: 20rpx;
	gap: 15rpx;
}

.user-avatar-small {
	width: 70rpx;
	height: 70rpx;
	border-radius: 50%;
}

.user-info {
	flex: 1;
}

.username {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 26rpx;
	line-height: 34rpx;
	color: #333333;
	font-weight: 500;
	display: block;
	margin-bottom: 4rpx;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	max-width: 120rpx;
}

.post-time {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 22rpx;
	line-height: 28rpx;
	color: #333333;
	display: block;
}

.follow-btn {
	width: 90rpx;
	height: 50rpx;
	border-radius: 10rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 8rpx;
	flex-shrink: 0;
}

.follow-btn.followed {
	background: #EAEEFD;
}

.follow-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 22rpx;
	line-height: 50rpx;
	color: #333333;
}

/* 帖子内容 */
.post-content {
	padding: 0 20rpx 20rpx;
}

.post-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 30rpx;
	line-height: 40rpx;
	color: #8A70C9;
	font-weight: 500;
	display: block;
	margin-bottom: 12rpx;
}

.post-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 40rpx;
	color: #333333;
	display: block;
	margin-bottom: 20rpx;
}

/* 标签 */
.post-tags {
	padding: 0 20rpx 20rpx;
	display: flex;
	gap: 20rpx;
}

.tag {
	background: #EDE7F9;
	border: 2rpx solid #BCA8F0;
	border-radius: 10rpx;
	padding: 12rpx 24rpx;
	box-sizing: border-box;
}

.tag-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	color: #333333;
}

/* 互动区域 */
.post-actions {
	border-top: 2rpx solid #E6D9F9;
	height: 100rpx;
	display: flex;
	align-items: center;
	justify-content: space-around;
}

.action-item {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.action-icon {
	width: 48rpx;
	height: 48rpx;
}

.action-count {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 38rpx;
	color: #333333;
}

/* 首页活动卡片样式 */
.discover-activity-card {
	background: #FFFFFF;
	border-radius: 20rpx;
	overflow: hidden;
	margin-bottom: 20rpx;
	display: flex;
	padding: 40rpx 20rpx;
	box-sizing: border-box;
	width: 100%;
}

.discover-activity-like {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 4rpx;
	flex-shrink: 0;
}

.discover-heart-icon {
	width: 32rpx;
	height: 32rpx;
}

.discover-like-count {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #333333;
}

/* 左侧图片 */
.discover-activity-image {
	width: 200rpx;
	height: 200rpx;
	border-radius: 16rpx;
	flex-shrink: 0;
	margin-right: 20rpx;
}

/* 右侧内容区域 */
.discover-activity-content {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	min-width: 0;
}

/* 标题和爱心区域 */
.discover-activity-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
}

/* 活动标题 */
.discover-activity-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 500;
	font-size: 28rpx;
	line-height: 36rpx;
	color: #333333;
	flex: 1;
	margin-right: 12rpx;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	line-clamp: 2;
	overflow: hidden;
}

/* 时间信息 */
.discover-activity-datetime {
	display: flex;
	gap: 8rpx;
}

.discover-activity-date {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #333333;
}

.discover-activity-day {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #333333;
}

.discover-activity-time {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #333333;
}

/* 地点标签和已报名区域 */
.discover-activity-location-tag {
	display: flex;
	justify-content: space-between;
	align-items: flex-end;
	margin-top: 8rpx;
}

.discover-activity-location-wrapper {
	display: flex;
	flex-direction: column;
	align-items: flex-start;
	gap: 8rpx;
	min-width: 0;
}

/* 地点 */
.discover-activity-location {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #333333;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	max-width: 100rpx;
}

/* 标签 */
.discover-activity-tag {
	display: inline-block;
	padding: 8rpx 16rpx;
	background: #EDE7F9;
	border: 2rpx solid #BCA8F0;
	border-radius: 16rpx;
}

.discover-activity-tag-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 20rpx;
	line-height: 24rpx;
	color: #333333;
}

.discover-activity-participants {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 20rpx;
	line-height: 30rpx;
	color: #999999;
	flex-shrink: 0;
}
</style>
