<template>
	<view class="container">
		<!-- 顶部导航 -->
		<TopNavigation 
			title="草稿箱" 
			:showBack="true" 
			:showMore="false"
			titleColor="#8A70C9"
			@back="goBack"
		/>

		<!-- 内容区域 -->
		<view class="content">
			<!-- 帖子草稿 -->
			<view class="draft-item" v-for="item in draftPosts" :key="item.id">
				<view class="draft-header">
					<image class="user-avatar" src="/static/follow/follow-users-section/Ellipse 11.png" mode="aspectFill"></image>
					<view class="user-info">
						<text class="username">用户名字</text>
						<text class="time">昨天 20:15</text>
					</view>
				</view>
				
				<view class="draft-content">
					<text class="draft-title">{{ item.title }}</text>
					<text class="draft-text">{{ item.content }}</text>
				</view>
				
				<view class="draft-footer">
					<view class="tag">
						<text class="tag-text">某某场</text>
					</view>
					<view class="draft-actions">
						<image class="delete-btn-small" src="/static/profile/delete-bin-6-line.png" mode="aspectFit" @click="showDeleteDialog(item)"></image>
						<view class="edit-btn" @click="editDraft(item)">
							<text class="edit-text">编辑</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 活动草稿 -->
			<view class="discover-activity-card" v-for="activity in draftActivities" :key="activity.id">
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
						<view class="draft-activity-actions">
							<image class="delete-btn-small" src="/static/profile/delete-bin-6-line.png" mode="aspectFit" @click="showDeleteDialog(activity)"></image>
							<view class="edit-btn" @click="editDraft(activity)">
								<text class="edit-text">编辑</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 删除确认弹窗 -->
		<view v-if="showDeleteConfirm" class="delete-modal">
			<view class="modal-overlay" @click="hideDeleteDialog"></view>
			<view class="modal-content">
				<text class="modal-title">确定删除该草稿吗？</text>
				<view class="modal-actions">
					<view class="modal-btn cancel" @click="hideDeleteDialog">
						<text class="modal-btn-text">取消</text>
					</view>
					<view class="modal-btn confirm" @click="confirmDelete">
						<text class="modal-btn-text confirm-text">确定</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import TopNavigation from '@/components/TopNavigation.vue'

export default {
	components: { TopNavigation },
	data() {
		return {
			showDeleteConfirm: false,
			currentDeleteItem: null,
			draftPosts: [
				{
					id: 1,
					title: '帖子大标题我也写长一点',
					content: '短一点'
				}
			],
			draftActivities: [
				{
					id: 1,
					title: '羽毛球初级训练找人：1v1或2v2都可',
					date: '2025.8.16',
					day: '周六',
					time: '17:00 - 18:00',
					location: '体育馆2楼',
					tag: '羽毛球场',
					image: '/static/discover/2.png'
				}
			]
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		showDeleteDialog(item) {
			this.currentDeleteItem = item
			this.showDeleteConfirm = true
		},
		hideDeleteDialog() {
			this.showDeleteConfirm = false
			this.currentDeleteItem = null
		},
		confirmDelete() {
			// 删除逻辑
			this.hideDeleteDialog()
		},
		editDraft(item) {
			// 编辑草稿逻辑
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: #F6F2FC;
}

.content {
	padding: 0 20rpx;
}

.draft-item {
	background: #FFFFFF;
	border-radius: 20rpx;
	padding: 20rpx;
	margin-bottom: 10rpx;
	position: relative;
}

.draft-header {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.user-avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 40rpx;
	margin-right: 20rpx;
}

.user-info {
	flex: 1;
}

.username {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 26rpx;
	color: #333333;
	font-weight: 400;
	margin-bottom: 8rpx;
}

.time {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 20rpx;
	color: #333333;
}

.delete-btn {
	width: 40rpx;
	height: 40rpx;
}

.draft-content {
	margin-bottom: 20rpx;
	padding-left: 0;
}

.draft-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 30rpx;
	color: #333333;
	font-weight: 500;
	display: block;
	margin-bottom: 12rpx;
}

.draft-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 20rpx;
	color: #333333;
	font-weight: 400;
}

.draft-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.draft-actions {
	display: flex;
	align-items: center;
	gap: 16rpx;
}

.delete-btn-small {
	width: 32rpx;
	height: 32rpx;
}

.tag {
	display: inline-block;
	background: #EDE7F9;
	border: 2rpx solid #BCA8F0;
	border-radius: 10rpx;
	padding: 12rpx 20rpx;
}

.tag-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 20rpx;
	color: #333333;
}

.edit-btn {
	background: #C1B8E8;
	border-radius: 10rpx;
	padding: 12rpx 20rpx;
}

.edit-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 20rpx;
	color: #333333;
}

/* 首页活动卡片样式 */
.discover-activity-card {
	position: relative;
	height: 300rpx;
	background: #FFFFFF;
	border-radius: 20rpx;
	overflow: hidden;
	margin-bottom: 10rpx;
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
	position: absolute;
	width: 220rpx;
	height: 220rpx;
	left: 40rpx;
	top: 40rpx;
	border-radius: 16rpx;
}

/* 右侧内容区域 */
.discover-activity-content {
	position: absolute;
	left: 300rpx;
	top: 32rpx;
	right: 32rpx;
	bottom: 32rpx;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
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
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	flex: 1;
	margin-right: 16rpx;
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
	max-width: 120rpx;
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

.draft-activity-actions {
	display: flex;
	align-items: center;
	gap: 16rpx;
	flex-shrink: 0;
}

/* 删除确认弹窗 */
.delete-modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 1000;
}

.modal-overlay {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
}

.modal-content {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	width: 600rpx;
	background: #FFFFFF;
	border-radius: 20rpx;
	padding: 60rpx 40rpx 40rpx;
}

.modal-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	text-align: center;
	margin-bottom: 60rpx;
}

.modal-actions {
	display: flex;
	gap: 20rpx;
}

.modal-btn {
	flex: 1;
	height: 88rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.modal-btn.cancel {
	background: #F5F5F5;
}

.modal-btn.confirm {
	background: #FF4757;
}

.modal-btn-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #999999;
	font-weight: 500;
}

.confirm-text {
	color: #FFFFFF;
}
</style>
