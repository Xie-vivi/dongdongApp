<template>
	<view class="container">
		<!-- 顶部状态栏占位 -->
		<view class="status-bar"></view>
		
		<!-- 顶部导航栏 -->
		<view class="top-nav">
			<view class="nav-left">
				<image src="/static/关注页/menu-01.png" class="menu-image" mode="aspectFit"></image>
			</view>
			<view class="nav-center">
				<view class="nav-tabs">
					<view class="nav-tab" :class="{ 'active': activeTab === 'follow' }" @click="switchTab('follow')">
						<text class="nav-tab-text">关注</text>
					</view>
					<view class="nav-tab" :class="{ 'active': activeTab === 'discover' }" @click="switchTab('discover')">
						<text class="nav-tab-text">发现</text>
					</view>
					<view class="nav-tab" :class="{ 'active': activeTab === 'activity' }" @click="switchTab('activity')">
						<text class="nav-tab-text">活动</text>
					</view>
				</view>
			</view>
			<view class="nav-right" @click="goToSearch">
				<image src="/static/关注页/发现.png" class="search-image" mode="aspectFit"></image>
			</view>
		</view>
		
		<!-- 内容区域 - 根据activeTab显示不同内容 -->
		
		<!-- 关注tab内容 -->
		<view v-if="activeTab === 'follow'">
			<!-- 关注用户头像区域 -->
			<view class="follow-users-section">
				<swiper class="follow-users-swiper" 
						@change="onUserSwiperChange" 
						:display-multiple-items="7"
						previous-margin="10rpx"
						next-margin="10rpx"
						:circular="true">
				<swiper-item v-for="(user, index) in users" :key="user.id" :style="getUserItemStyle(index)">
					<view class="user-item" :class="getUserItemClass(index)">
						<view class="user-avatar-container">
							<image class="user-avatar-img" :src="user.avatar" mode="aspectFill" @error="onImageError" @load="onImageLoad" />
						</view>
						<text class="user-name">{{ user.name }}</text>
					</view>
				</swiper-item>
				</swiper>
			</view>
			
			<!-- 轮播图区域 -->
			<view class="banner-section">
				<swiper class="banner-swiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="500" indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#FFFFFF">
					<swiper-item v-for="(banner, index) in banners" :key="index">
						<view class="banner-item">
							<image class="banner-bg" :src="banner.image" mode="aspectFill" />
							<view class="banner-content">
								<view class="title-section">
									<text class="main-title">羽毛球场</text>
								</view>
								<text class="banner-desc">{{ banner.description }}</text>
							</view>
						</view>
					</swiper-item>
				</swiper>
			</view>
		</view>
		
		<!-- 发现tab内容 -->
		<view v-else-if="activeTab === 'discover'">
			<!-- 分类标签 -->
			<view class="discover-categories">
				<scroll-view class="categories-scroll-horizontal" scroll-x="true" show-scrollbar="false">
					<view class="categories-wrapper">
						<view class="category-tag" :class="{ active: selectedDiscoverCategory === 'recommend' }" @tap="selectDiscoverCategory('recommend')">
							<text class="category-tag-text">推荐</text>
						</view>
						<view class="category-tag" @tap="selectDiscoverCategory('study')">
							<text class="category-tag-text">学习</text>
						</view>
						<view class="category-tag" @tap="selectDiscoverCategory('sports')">
							<text class="category-tag-text">体育</text>
						</view>
						<view class="category-tag" @tap="selectDiscoverCategory('entertainment')">
							<text class="category-tag-text">娱乐</text>
						</view>
						<view class="category-tag" @tap="selectDiscoverCategory('life')">
							<text class="category-tag-text">生活</text>
						</view>
						<view class="category-tag" @tap="selectDiscoverCategory('tech')">
							<text class="category-tag-text">科技</text>
						</view>
						<view class="category-tag" @tap="selectDiscoverCategory('food')">
							<text class="category-tag-text">美食</text>
						</view>
						<view class="category-tag" @tap="selectDiscoverCategory('travel')">
							<text class="category-tag-text">旅行</text>
						</view>
						<view class="category-tag" @tap="selectDiscoverCategory('photography')">
							<text class="category-tag-text">摄影</text>
						</view>
						<view class="category-tag" @tap="selectDiscoverCategory('music')">
							<text class="category-tag-text">音乐</text>
						</view>
						<view class="category-tag" @tap="selectDiscoverCategory('reading')">
							<text class="category-tag-text">阅读</text>
						</view>
						<view class="category-tag" @tap="selectDiscoverCategory('gaming')">
							<text class="category-tag-text">游戏</text>
						</view>
					</view>
				</scroll-view>
			</view>
			
			<!-- 关注用户区域 -->
			<view class="discover-follow-users">
				<view class="discover-view-all-fixed">
					<text class="discover-view-all-text">全部</text><text class="discover-chevron">›</text>
				</view>
				<scroll-view class="discover-users-scroll" scroll-x="true" show-scrollbar="false">
					<view class="discover-users-container">
						<view class="discover-user-item" v-for="(user, index) in discoverUsers" :key="index">
							<view class="discover-user-avatar-wrapper">
								<image class="discover-user-avatar" :src="user.avatar" mode="aspectFill" />
								<view class="discover-user-plus-btn">
									<text class="discover-plus-icon">+</text>
								</view>
							</view>
							<text class="discover-user-label">{{ user.label }}</text>
						</view>
					</view>
				</scroll-view>
			</view>
			
			<!-- 活动卡片轮播 -->
			<swiper class="discover-activity-swiper" 
					:indicator-dots="false" 
					:autoplay="false" 
					:interval="5000" 
					:duration="500"
					@change="onActivitySwiperChange">
				<swiper-item v-for="(activity, index) in discoverActivities" :key="index">
					<view class="discover-activity-card">
						<!-- 左侧图片 -->
						<image class="discover-activity-image" :src="activity.image" mode="aspectFill" />
						
						<!-- 右侧内容区域 -->
						<view class="discover-activity-content">
							<!-- 标题和爱心 -->
							<view class="discover-activity-header">
								<text class="discover-activity-title">{{ activity.title }}</text>
								<view class="discover-activity-like">
									<image class="discover-heart-icon" src="/static/发现页/1.png" mode="aspectFit" />
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
						
						<!-- 底部指示点 -->
						<view class="discover-activity-dots">
							<view class="discover-dot" 
								  v-for="(item, dotIndex) in discoverActivities" 
								  :key="dotIndex"
								  :class="{ active: dotIndex === currentActivityIndex }"></view>
						</view>
					</view>
				</swiper-item>
			</swiper>
			
			<!-- 帖子内容 -->
			<view class="discover-post">
				<view class="discover-post-header">
					<image class="discover-post-avatar" src="/static/关注页/1.png" mode="aspectFill" />
					<view class="discover-post-user-info">
						<text class="discover-post-username">用户名字</text>
						<text class="discover-post-time">昨天 20:15</text>
					</view>
					<view class="discover-follow-btn">
						<text class="discover-follow-plus">+</text>
						<text class="discover-follow-text">关注</text>
					</view>
				</view>
				<view class="discover-post-content">
					<text class="discover-post-title">帖子大标题我也写长一点</text>
					<text class="discover-post-desc">短一点</text>
					<view class="discover-post-tag">
						<text class="discover-post-tag-text">某某场</text>
					</view>
				</view>
			<view class="discover-post-actions">
				<view class="discover-post-action">
					<image class="discover-action-icon" src="/static/关注页/5.png"></image>
					<text class="discover-action-count">21</text>
				</view>
				<view class="discover-post-action">
					<image class="discover-action-icon" src="/static/关注页/6.png"></image>
					<text class="discover-action-count">2</text>
				</view>
				<view class="discover-post-action">
					<image class="discover-action-icon" src="/static/关注页/7.png"></image>
					<text class="discover-action-count">1</text>
				</view>
			</view>
			</view>
			
			<!-- 第二个帖子 -->
			<view class="discover-post">
				<view class="discover-post-header">
					<image class="discover-post-avatar" src="/static/关注页/1.png" mode="aspectFill" />
					<view class="discover-post-user-info">
						<text class="discover-post-username">用户名称我起长一点</text>
						<text class="discover-post-time">昨天 20:11</text>
					</view>
					<view class="discover-followed-btn">
						<text class="discover-followed-text">已关注</text>
					</view>
				</view>
				<view class="discover-post-content">
					<text class="discover-post-title">帖子大标题我也写长一点</text>
					<text class="discover-post-desc">测试一下最长的长度在这里我们可以轻松自定义主题背景与装扮，打造专属兴趣空间，轻松找到同好社群，感受如 "家园" 般的沉浸互动氛围；实时获取全校各类活动动态，支持一键报名与专属提</text>
					<text class="discover-post-expand">... 全文</text>
					<view class="discover-post-images">
						<image class="discover-post-img" src="/static/关注页/2.png" mode="aspectFill" />
						<image class="discover-post-img" src="/static/关注页/3.png" mode="aspectFill" />
						<image class="discover-post-img" src="/static/关注页/4.png" mode="aspectFill" />
					</view>
					<view class="discover-post-tag">
						<text class="discover-post-tag-text">某某场</text>
					</view>
				</view>
			<view class="discover-post-actions">
				<view class="discover-post-action">
					<image class="discover-action-icon" src="/static/关注页/5.png"></image>
					<text class="discover-action-count">125</text>
				</view>
				<view class="discover-post-action">
					<image class="discover-action-icon" src="/static/关注页/6.png"></image>
					<text class="discover-action-count">16</text>
				</view>
				<view class="discover-post-action">
					<image class="discover-action-icon" src="/static/关注页/7.png"></image>
					<text class="discover-action-count">20</text>
				</view>
			</view>
			</view>
		</view>
		
		<!-- 活动tab内容 -->
		<view v-else-if="activeTab === 'activity'">
			<!-- 分类标签 - 使用发现页面的样式 -->
			<view class="discover-categories">
				<scroll-view class="categories-scroll-horizontal" scroll-x="true" show-scrollbar="false">
					<view class="categories-wrapper">
						<view class="category-tag" :class="{ active: selectedActivityCategory === 'recommend' }" @tap="selectActivityCategory('recommend')">
							<text class="category-tag-text">推荐</text>
						</view>
						<view class="category-tag" :class="{ active: selectedActivityCategory === 'study' }" @tap="selectActivityCategory('study')">
							<text class="category-tag-text">学习</text>
						</view>
						<view class="category-tag" :class="{ active: selectedActivityCategory === 'sports' }" @tap="selectActivityCategory('sports')">
							<text class="category-tag-text">体育</text>
						</view>
						<view class="category-tag" :class="{ active: selectedActivityCategory === 'entertainment' }" @tap="selectActivityCategory('entertainment')">
							<text class="category-tag-text">娱乐</text>
						</view>
						<view class="category-tag" :class="{ active: selectedActivityCategory === 'life' }" @tap="selectActivityCategory('life')">
							<text class="category-tag-text">生活</text>
						</view>
					</view>
				</scroll-view>
			</view>
			
			<!-- 活动卡片列表 -->
			<view class="activity-cards-container">
				<view class="activity-card-item" v-for="(activity, index) in filteredActivityCards" :key="index" @click="goToEventDetail(activity)">
					<!-- 左侧图片 -->
					<image class="activity-card-image" :src="activity.image" mode="aspectFill" />
					
					<!-- 右侧内容区域 -->
					<view class="activity-card-content">
						<!-- 标题和爱心 -->
						<view class="activity-card-header">
							<text class="activity-card-title">{{ activity.title }}</text>
							<view class="activity-card-like">
								<image class="activity-heart-icon" src="/static/发现页/1.png" mode="aspectFit" />
								<text class="activity-like-count">{{ activity.likes }}</text>
							</view>
						</view>
						
						<!-- 时间信息 -->
						<view class="activity-card-datetime">
							<text class="activity-card-date">{{ activity.date }}</text>
							<text class="activity-card-day">{{ activity.day }}</text>
							<text class="activity-card-time">{{ activity.time }}</text>
						</view>
						
						<!-- 地点和标签 -->
						<view class="activity-card-location-tag">
							<view class="activity-card-location-wrapper">
								<text class="activity-card-location">{{ activity.location }}</text>
								<view class="activity-card-tag">
									<text class="activity-card-tag-text">{{ activity.tag }}</text>
								</view>
							</view>
							<text class="activity-card-participants">已报名：{{ activity.participants }}人</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 帖子列表 - 只在关注tab显示 -->
		<view class="posts-section" v-if="activeTab === 'follow'">
			<view class="post-card" v-for="(post, index) in posts" :key="index" @click="goToPostDetail(post, index)">
				<!-- 用户信息 -->
				<view class="post-header">
					<image class="user-avatar-small" :src="post.userAvatar" mode="aspectFill" />
					<view class="user-info">
						<text class="username">{{ post.username }}</text>
						<text class="post-time">{{ post.time }}</text>
					</view>
					<view class="follow-btn" :class="{ 'followed': post.isFollowed }" @click.stop="toggleFollow(index)">
						<view class="follow-icon" v-if="!post.isFollowed">+</view>
						<text class="follow-text">{{ post.isFollowed ? '已关注' : '关注' }}</text>
					</view>
				</view>
				
				<!-- 帖子内容 -->
				<view class="post-content">
					<text class="post-title">{{ post.title }}</text>
					<text class="post-text" :class="{ 'expanded': post.expanded }">{{ post.content }}</text>
					<text class="expand-btn" v-if="post.content.length > 100 && !post.expanded" @click.stop="expandPost(index)">... 全文</text>
				</view>
				
				<!-- 帖子图片 -->
				<view class="post-images" v-if="post.images && post.images.length > 0">
					<image 
						v-for="(img, imgIndex) in post.images" 
						:key="imgIndex" 
						class="post-image" 
						:src="img" 
						mode="aspectFill"
						@click.stop="previewImage(img, post.images)"
					/>
				</view>
				
				<!-- 标签 -->
				<view class="post-tags" v-if="post.tags && post.tags.length > 0">
					<view class="tag" v-for="(tag, tagIndex) in post.tags" :key="tagIndex">
						<text class="tag-text">{{ tag }}</text>
					</view>
				</view>
				
				<!-- 互动区域 -->
				<view class="post-actions">
					<view class="action-item" @click.stop="toggleLike(index)">
						<image class="action-icon" src="/static/关注页/5.png" :class="{ 'liked': post.isLiked }"></image>
						<text class="action-count">{{ post.likes }}</text>
					</view>
					<view class="action-item" @click.stop="toggleStar(index)">
						<image class="action-icon" src="/static/关注页/6.png" :class="{ 'starred': post.isStarred }"></image>
						<text class="action-count">{{ post.stars }}</text>
					</view>
					<view class="action-item" @click.stop="showComments(index)">
						<image class="action-icon" src="/static/关注页/7.png"></image>
						<text class="action-count">{{ post.comments }}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 底部导航栏 -->
		<BottomNavigation :currentPage="'index'" :messageCount="messageCount" />
	</view>
</template>

<script>
import BottomNavigation from '@/components/BottomNavigation.vue'

export default {
	components: {
		BottomNavigation
	},
	data() {
		return {
			activeTab: 'follow',
			messageCount: 1,
			currentUserIndex: 0, // swiper显示的第一个item的索引
			userDescriptions: ['羽毛球场', '网球场', '篮球场', '足球场', '游泳馆', '健身房', '乒乓球室', '台球厅'],
			selectedCategory: 'all', // 活动分类选择
			selectedDiscoverCategory: 'recommend', // 发现页分类选择
			selectedActivityCategory: 'recommend', // 活动页分类选择
			// 发现页用户数据
			discoverUsers: [
				{ avatar: '/static/关注页/follow-users-section/Ellipse 2.png', label: '无名场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 9.png', label: '乒乓球场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 11.png', label: '羽毛球场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 13.png', label: '动漫场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 14.png', label: 'xx场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 2.png', label: '篮球场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 9.png', label: '音乐场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 11.png', label: '摄影场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 13.png', label: '游戏场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 14.png', label: '读书场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 2.png', label: '美食场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 9.png', label: '旅行场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 11.png', label: '健身场' },
				{ avatar: '/static/关注页/follow-users-section/Ellipse 13.png', label: '学习场' }
			],
			// 发现页活动卡片数据
			discoverActivities: [
				{
					title: '羽毛球初级训练找人：1v1或2v2都可',
					date: '2025.8.16',
					day: '周六',
					time: '17:00 - 18:00',
					location: '体育馆2楼',
					tag: '羽毛球场',
					participants: 5,
					likes: 21,
					image: '/static/发现页/2.png'
				},
				{
					title: '篮球约战：3v3半场比赛',
					date: '2025.8.17',
					day: '周日',
					time: '15:00 - 17:00',
					location: '室外篮球场',
					tag: '篮球场',
					participants: 8,
					likes: 35,
					image: '/static/发现页/2.png'
				},
				{
					title: '乒乓球技术交流会',
					date: '2025.8.18',
					day: '周一',
					time: '19:00 - 21:00',
					location: '学生活动中心',
					tag: '乒乓球场',
					participants: 12,
					likes: 18,
					image: '/static/发现页/2.png'
				},
				{
					title: '网球双打友谊赛',
					date: '2025.8.19',
					day: '周二',
					time: '16:00 - 18:00',
					location: '网球场A区',
					tag: '网球场',
					participants: 4,
					likes: 28,
					image: '/static/发现页/2.png'
				},
				{
					title: '足球训练营：基础技能提升',
					date: '2025.8.20',
					day: '周三',
					time: '18:30 - 20:30',
					location: '足球场',
					tag: '足球场',
					participants: 15,
					likes: 42,
					image: '/static/发现页/2.png'
				},
				{
					title: '游泳健身约起来',
					date: '2025.8.21',
					day: '周四',
					time: '20:00 - 21:30',
					location: '游泳馆',
					tag: '游泳场',
					participants: 6,
					likes: 15,
					image: '/static/发现页/2.png'
				},
				{
					title: '瑜伽课程体验：初学者友好',
					date: '2025.8.22',
					day: '周五',
					time: '19:00 - 20:00',
					location: '健身房B区',
					tag: '瑜伽馆',
					participants: 10,
					likes: 32,
					image: '/static/发现页/2.png'
				},
				{
					title: '跑步团：晨跑打卡活动',
					date: '2025.8.23',
					day: '周六',
					time: '07:00 - 08:00',
					location: '学校操场',
					tag: '跑步场',
					participants: 20,
					likes: 56,
					image: '/static/发现页/2.png'
				}
			],
			currentActivityIndex: 0,
			// 活动页面卡片数据
			activityCards: [
				{
					title: '羽毛球初级训练找人：1v1或2v2都可',
					date: '2025.8.16',
					day: '周六',
					time: '17:00 - 18:00',
					location: '体育馆2楼',
					tag: '羽毛球场',
					participants: 5,
					likes: 21,
					image: '/static/发现页/2.png',
					category: 'sports'
				},
				{
					title: '明日方舟线下集会',
					date: '2025.8.26',
					day: '周六',
					time: '17:00 - 18:00',
					location: '体育馆1楼',
					tag: '动漫场',
					participants: 30,
					likes: 125,
					image: '/static/发现页/2.png',
					category: 'entertainment'
				},
				{
					title: '乒乓球',
					date: '2025.8.26',
					day: '周六',
					time: '17:00 - 18:00',
					location: '体育馆1楼',
					tag: '乒乓球场',
					participants: 7,
					likes: 21,
					image: '/static/发现页/2.png',
					category: 'sports'
				},
				{
					title: '羽毛球初级训练找人：1v1或2v2都可',
					date: '2025.8.16',
					day: '周六',
					time: '12:00 - 13:00',
					location: '体育馆2楼',
					tag: '足球场',
					participants: 11,
					likes: 22,
					image: '/static/发现页/2.png',
					category: 'sports'
				},
				{
					title: '编程学习交流会',
					date: '2025.8.20',
					day: '周三',
					time: '19:00 - 21:00',
					location: '图书馆3楼',
					tag: '学习场',
					participants: 15,
					likes: 45,
					image: '/static/发现页/2.png',
					category: 'study'
				},
				{
					title: '摄影技巧分享',
					date: '2025.8.18',
					day: '周一',
					time: '14:00 - 16:00',
					location: '艺术楼1楼',
					tag: '摄影场',
					participants: 12,
					likes: 33,
					image: '/static/发现页/2.png',
					category: 'life'
				}
			],
			// 发现页面数据
			hotClubs: [
				{ name: '羽毛球社', avatar: '/static/关注页/follow-users-section/Ellipse 2.png', members: 128 },
				{ name: '摄影社', avatar: '/static/关注页/follow-users-section/Ellipse 2 (1).png', members: 96 },
				{ name: '篮球社', avatar: '/static/关注页/follow-users-section/Ellipse 9.png', members: 156 },
				{ name: '音乐社', avatar: '/static/关注页/follow-users-section/Ellipse 11.png', members: 89 },
				{ name: '书法社', avatar: '/static/关注页/follow-users-section/Ellipse 13.png', members: 67 }
			],
			recommendActivities: [
				{
					title: '周末羽毛球友谊赛',
					description: '欢迎各水平球友参加，一起切磋球技',
					time: '本周六 14:00',
					location: '体育馆',
					image: '/static/关注页/banner/banner1.png'
				},
				{
					title: '摄影作品分享会',
					description: '分享摄影技巧，展示优秀作品',
					time: '下周日 10:00',
					location: '图书馆',
					image: '/static/关注页/banner/banner2.png'
				}
			],
			// 活动页面数据
			activityCategories: [
				{ id: 'all', name: '全部' },
				{ id: 'sports', name: '运动' },
				{ id: 'culture', name: '文化' },
				{ id: 'social', name: '社交' },
				{ id: 'study', name: '学习' }
			],
			activities: [
				{
					id: 1,
					title: '羽毛球友谊赛',
					description: '欢迎各水平球友参加，一起切磋球技，结识新朋友',
					time: '2024-01-15 14:00',
					location: '体育馆A场',
					participants: 12,
					category: 'sports',
					image: '/static/关注页/banner/banner1.png'
				},
				{
					id: 2,
					title: '书法交流会',
					description: '书法爱好者聚集，互相学习交流书法心得',
					time: '2024-01-16 10:00',
					location: '艺术楼201',
					participants: 8,
					category: 'culture',
					image: '/static/关注页/banner/banner2.png'
				},
				{
					id: 3,
					title: '编程技术分享',
					description: '前端开发技术分享，Vue3最新特性解析',
					time: '2024-01-17 19:00',
					location: '计算机楼305',
					participants: 25,
					category: 'study',
					image: '/static/关注页/banner/banner1.png'
				}
			],
			users: [
				{ id: 1, name: '小明', avatar: '/static/关注页/follow-users-section/Ellipse 2.png' },
				{ id: 2, name: '小红', avatar: '/static/关注页/follow-users-section/Ellipse 2 (1).png' },
				{ id: 3, name: '小李', avatar: '/static/关注页/follow-users-section/Ellipse 9.png' },
				{ id: 4, name: '小张', avatar: '/static/关注页/follow-users-section/Ellipse 11.png' },
				{ id: 5, name: '小王', avatar: '/static/关注页/follow-users-section/Ellipse 13.png' },
				{ id: 6, name: '小赵', avatar: '/static/关注页/follow-users-section/Ellipse 15.png' },
				{ id: 7, name: '小陈', avatar: '/static/关注页/follow-users-section/Ellipse 2.png' },
				{ id: 8, name: '小刘', avatar: '/static/关注页/follow-users-section/Ellipse 9.png' }
			],
			banners: [
				{
					image: '/static/关注页/banner.png',
					description: '这是一个羽毛球爱好者的聚集地这是一个羽毛球测一下长度我们测测到底最长能写到哪里所以我们的最多字数就是这么多了我觉得够了'
				}
			],
			posts: [
				{
					userAvatar: '/static/关注页/1.png',
					username: '用户名称我起长一点',
					time: '昨天 20:15',
					title: '帖子大标题我也写长一点',
					content: '测试一下最长的长度在这里我们可以轻松自定义主题背景与装扮，打造专属兴趣空间，轻松找到同好社群，感受如 "家园" 般的沉浸互动氛围；实时获取全校各类活动动态，支持一键报名与专属提',
					images: ['/static/关注页/2.png', '/static/关注页/3.png', '/static/关注页/4.png'],
					tags: ['某某场'],
					likes: 125,
					stars: 16,
					comments: 20,
					isLiked: false,
					isStarred: false,
					isFollowed: false,
					expanded: false
				},
				{
					userAvatar: '/static/关注页/2.png',
					username: '用户名字',
					time: '昨天 20:15',
					title: '帖子大标题我也写长一点',
					content: '短一点',
					images: [],
					tags: ['某某场'],
					likes: 125,
					stars: 16,
					comments: 20,
					isLiked: false,
					isStarred: false,
					isFollowed: true,
					expanded: false
				},
				{
					userAvatar: '/static/关注页/3.png',
					username: '第三个用户',
					time: '2小时前',
					title: '分享一个有趣的活动',
					content: '今天参加了学校的社团活动，认识了很多志同道合的朋友，大家一起讨论学习和生活，感觉特别充实和有意义。',
					images: ['/static/images/post1.jpg'],
					tags: ['社团活动'],
					likes: 89,
					stars: 12,
					comments: 15,
					isLiked: true,
					isStarred: false,
					isFollowed: false,
					expanded: false
				},
				{
					userAvatar: '/static/关注页/4.png',
					username: '第四个用户',
					time: '5小时前',
					title: '学习心得分享',
					content: '最近在学习新的技术栈，虽然过程中遇到了不少困难，但是通过不断的练习和实践，终于有了一些收获和体会。',
					images: [],
					tags: ['学习分享'],
					likes: 67,
					stars: 8,
					comments: 12,
					isLiked: false,
					isStarred: true,
					isFollowed: true,
					expanded: false
				}
			]
		}
	},
	computed: {
		// 计算中间位置的索引（显示5个时，中间是第2个，索引为2）
		centerIndex() {
			return Math.floor(7 / 2) // 显示7个图片时，中间索引为3
		},
		// 获取当前中间显示的用户
		currentUser() {
			return this.users[this.currentUserIndex] || this.users[0]
		},
		// 根据分类过滤活动
		filteredActivities() {
			if (this.selectedCategory === 'all') {
				return this.activities
			}
			return this.activities.filter(activity => activity.category === this.selectedCategory)
		},
		// 根据分类过滤活动卡片
		filteredActivityCards() {
			if (this.selectedActivityCategory === 'recommend') {
				return this.activityCards
			}
			return this.activityCards.filter(activity => activity.category === this.selectedActivityCategory)
		}
	},
	onLoad() {
		// 检查登录状态
		this.checkLoginStatus()
	},
	methods: {
		// 活动轮播切换事件
		onActivitySwiperChange(e) {
			this.currentActivityIndex = e.detail.current;
		},
		getUserItemClass(index) {
			// 在显示7个item的swiper中，中间位置是第4个（index=3）
			// 我们需要计算每个item相对于显示区域中心的距离
			const displayItems = 7;
			const centerPosition = Math.floor(displayItems / 2); // 3 (第4个位置)
			
			// 计算当前item在显示区域中的相对位置
			const totalUsers = this.users.length;
			const currentSwiperIndex = this.currentUserIndex; // swiper当前显示的第一个item的索引
			
			// 计算item在当前显示窗口中的位置
			let positionInWindow = index - currentSwiperIndex;
			
			// 处理循环情况
			if (positionInWindow < 0) {
				positionInWindow += totalUsers;
			}
			if (positionInWindow >= displayItems) {
				positionInWindow = positionInWindow % totalUsers;
				if (positionInWindow >= displayItems) {
					positionInWindow = displayItems - 1;
				}
			}
			
			// 计算相对于中心位置的距离
			const distanceFromCenter = Math.abs(positionInWindow - centerPosition);
			
			// 调试信息
			console.log(`用户${index}: swiperIndex=${currentSwiperIndex}, positionInWindow=${positionInWindow}, centerPosition=${centerPosition}, distance=${distanceFromCenter}`);
			
			// 基于距离返回不同的类名
			if (distanceFromCenter === 0) {
				console.log(`用户${index}: 设置为center (最清晰)`);
				return 'center'; // 最中间，最清晰
			} else if (distanceFromCenter === 1) {
				console.log(`用户${index}: 设置为near`);
				return 'near'; // 靠近中间，稍微模糊
			} else if (distanceFromCenter === 2) {
				console.log(`用户${index}: 设置为far`);
				return 'far'; // 较远，更模糊
			} else {
				console.log(`用户${index}: 设置为farthest`);
				return 'farthest'; // 最远，最模糊
			}
		},
		getUserItemStyle(index) {
			// 计算基于位置的z-index
			const displayItems = 7;
			const centerPosition = Math.floor(displayItems / 2); // 3 (第4个位置，0-based索引)
			
			const totalUsers = this.users.length;
			const currentSwiperIndex = this.currentUserIndex;
			
			// 计算item在当前显示窗口中的位置
			let positionInWindow = index - currentSwiperIndex;
			
			// 处理循环情况
			if (positionInWindow < 0) {
				positionInWindow += totalUsers;
			}
			if (positionInWindow >= displayItems) {
				positionInWindow = positionInWindow % totalUsers;
				if (positionInWindow >= displayItems) {
					positionInWindow = displayItems - 1;
				}
			}
			
			// 修正positionInWindow计算，确保在0-6范围内
			positionInWindow = positionInWindow % displayItems;
			if (positionInWindow < 0) {
				positionInWindow += displayItems;
			}
			
			// 计算z-index：中心位置(3)最高，向两边递减
			// 位置3: z-index = 200 (最高层级)
			// 位置2,4: z-index = 180
			// 位置1,5: z-index = 160
			// 位置0,6: z-index = 140
			const distanceFromCenter = Math.abs(positionInWindow - centerPosition);
			const zIndex = 200 - (distanceFromCenter * 20);
			
			console.log(`用户${index}: positionInWindow=${positionInWindow}, distanceFromCenter=${distanceFromCenter}, zIndex=${zIndex}`);
			
			return {
				zIndex: zIndex
			};
		},
		onImageError(e) {
			console.error('图片加载失败:', e.detail.errMsg)
			console.log('失败的图片路径:', e.target.src)
		},
		onImageLoad(e) {
			console.log('图片加载成功:', e.target.src)
		},
		checkLoginStatus() {
			const token = uni.getStorageSync('userToken')
			if (!token) {
				// 如果没有登录，跳转到登录页面
				uni.reLaunch({
					url: '/pages/login/login'
				})
			}
		},
		onUserSwiperChange(e) {
			this.currentUserIndex = e.detail.current
		},
		switchTab(tab) {
			this.activeTab = tab;
		},
		toggleFollow(index) {
			this.posts[index].isFollowed = !this.posts[index].isFollowed
		},
		expandPost(index) {
			this.posts[index].expanded = true
		},
		toggleLike(index) {
			this.posts[index].isLiked = !this.posts[index].isLiked
			if (this.posts[index].isLiked) {
				this.posts[index].likes++
			} else {
				this.posts[index].likes--
			}
		},
		toggleStar(index) {
			this.posts[index].isStarred = !this.posts[index].isStarred
			if (this.posts[index].isStarred) {
				this.posts[index].stars++
			} else {
				this.posts[index].stars--
			}
		},
		showComments(index) {
			// 跳转到帖子详情页
			this.goToPostDetail(this.posts[index], index)
		},
		goToPostDetail(post, index) {
			// 跳转到帖子详情页，传递帖子数据
			uni.navigateTo({
				url: `/pages/post-detail/post-detail?postId=${post.id || index}`
			})
		},
		previewImage(current, urls) {
			uni.previewImage({
				current,
				urls
			})
		},
		goToSearch() {
			uni.showToast({
				title: '搜索功能',
				icon: 'none'
			})
		},
		// 活动分类选择
		selectCategory(categoryId) {
			this.selectedCategory = categoryId
		},
		// 参与活动
		joinActivity(index) {
			const activity = this.filteredActivities[index]
			uni.showToast({
				title: `已报名参加：${activity.title}`,
				icon: 'success'
			})
			// 这里可以添加实际的报名逻辑
		},
		// 发现页分类选择
		selectDiscoverCategory(categoryId) {
			this.selectedDiscoverCategory = categoryId
		},
		// 活动页分类选择
		selectActivityCategory(categoryId) {
			this.selectedActivityCategory = categoryId
		},
		// 跳转到活动详情页
		goToEventDetail(activity) {
			uni.navigateTo({
				url: '/pages/event-detail/event-detail'
			})
		}
	}
}
</script>

<style scoped>
.container {
	position: relative;
	width: 100%;
	min-height: 100vh;
	background: #F6F2FC;
	padding-bottom: 160rpx; /* 为底部导航栏留出空间 */
}

/* 顶部状态栏占位 */
.status-bar {
	height: 88rpx; /* 44px * 2 状态栏高度 */
	background: #F6F2FC;
}

/* 顶部导航栏 */
.top-nav {
	display: flex;
	align-items: center;
	justify-content: space-between;
	height: 88rpx;
	padding: 0 40rpx;
	background: #F6F2FC;
}

.nav-left, .nav-right {
	width: 60rpx;
	height: 60rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.menu-image, .search-image {
	width: 40rpx;
	height: 40rpx;
}


.nav-center {
	flex: 1;
	display: flex;
	justify-content: center;
}

.nav-tabs {
	display: flex;
	align-items: center;
	gap: 60rpx;
}

.nav-tab {
	position: relative;
	padding: 20rpx 0;
}

.nav-tab-text {
	font-size: 32rpx;
	color: #666666;
	font-weight: 500;
}

.nav-tab.active .nav-tab-text {
	color: #8B5CF6;
	font-weight: 600;
}

.nav-tab.active::after {
	content: '';
	position: absolute;
	bottom: 8rpx;
	left: 50%;
	transform: translateX(-50%);
	width: 40rpx;
	height: 6rpx;
	background: #8B5CF6;
	border-radius: 3rpx;
}


/* 关注用户区域 */
.follow-users-section {
	padding: 0rpx;
	background: #FFFFFF;
	border-radius: 20rpx;
	margin: 20rpx;
}

.follow-users-swiper {
	height: 300rpx;
	width: 100%;
	overflow: visible;
}

.follow-users-swiper swiper-item {
	overflow: visible;
	position: relative;
}

.user-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	height: 100%;
	position: relative;
	transition: all 0.3s ease;
}

/* 最中间的图片 - 最清晰 */
.user-item.center {
	transform: scale(1.2);
	opacity: 1;
	filter: blur(0px);
}

/* 靠近中间的图片 - 稍微模糊 */
.user-item.near {
	transform: scale(1.0);
	opacity: 0.8;
	filter: blur(1px);
}

/* 较远的图片 - 更模糊 */
.user-item.far {
	transform: scale(0.8);
	opacity: 0.6;
	filter: blur(2px);
}

/* 最远的图片 - 最模糊 */
.user-item.farthest {
	transform: scale(0.6);
	opacity: 0.3;
	filter: blur(4px);
}

.user-avatar-container {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	overflow: hidden;
	margin-bottom: 20rpx;
	transition: all 0.3s ease;
	position: relative;
}

.user-avatar-img {
	width: 100%;
	height: 100%;
}

.user-name {
	font-size: 28rpx;
	color: #333333;
	font-weight: 500;
	text-align: center;
}

.section-title-old {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx; /* 14px * 2 */
	line-height: 38rpx; /* 19px * 2 */
	color: #333333;
	text-align: center;
	margin-top: 20rpx;
}

/* 轮播图区域 */
.banner-section {
	padding: 0 20rpx 40rpx 20rpx;
}

.title-section {
	margin-bottom: 10rpx;
}

.main-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 48rpx;
	font-weight: bold;
	line-height: 64rpx;
	color: #000000;
	text-align: left;
}

.banner-swiper {
	width: 100%;
	height: 286rpx; /* 143px * 2 */
	border-radius: 20rpx; /* 10px * 2 */
	overflow: hidden;
}

.banner-item {
	position: relative;
	width: 100%;
	height: 100%;
}

.banner-bg {
	width: 100%;
	height: 100%;
}

.banner-content {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 20rpx;
	padding: 40rpx;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.banner-desc {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 40rpx; /* 22px * 2 */
	color: #000000;
	margin-bottom: 20rpx;
}

/* 帖子列表 */
.posts-section {
	padding: 0 20rpx;
}

.post-card {
	background: #FFFFFF;
	border-radius: 20rpx; /* 10px * 2 */
	margin-bottom: 20rpx;
	overflow: hidden;
}

/* 帖子头部 */
.post-header {
	display: flex;
	align-items: center;
	padding: 20rpx;
	gap: 20rpx;
}

.user-avatar-small {
	width: 80rpx; /* 40px * 2 */
	height: 80rpx;
	border-radius: 50%;
}

.user-info {
	flex: 1;
}

.username {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx; /* 14px * 2 */
	line-height: 38rpx; /* 19px * 2 */
	color: #333333;
	font-weight: 500;
	display: block;
	margin-bottom: 6rpx;
}

.post-time {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx; /* 12px * 2 */
	line-height: 32rpx; /* 16px * 2 */
	color: #333333;
	display: block;
}

.follow-btn {
	width: 112rpx; /* 56px * 2 */
	height: 50rpx; /* 25px * 2 */
	border-radius: 10rpx; /* 5px * 2 */
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 8rpx;
}

.follow-btn:not(.followed) {
	background: #C1B8E8;
}

.follow-btn.followed {
	background: #EAEEFD;
}

.follow-icon {
	font-size: 24rpx; /* 12px * 2 */
	color: #333333;
	font-weight: bold;
}

.follow-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx; /* 12px * 2 */
	line-height: 50rpx; /* 25px * 2 */
	color: #333333;
}

/* 帖子内容 */
.post-content {
	padding: 0 20rpx 20rpx;
}

.post-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 44rpx; /* 22px * 2 */
	color: #8A70C9;
	font-weight: 500;
	display: block;
	margin-bottom: 12rpx;
}

.post-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 50rpx; /* 25px * 2 */
	color: #333333;
	display: block;
	margin-bottom: 20rpx;
}

.post-text:not(.expanded) {
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 4;
	line-clamp: 4;
	-webkit-box-orient: vertical;
}

.expand-btn {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 44rpx; /* 22px * 2 */
	color: #8A70C9;
	margin-left: 10rpx;
}

/* 帖子图片 */
.post-images {
	display: flex;
	gap: 20rpx; /* 10px * 2 */
	padding: 0 20rpx 20rpx;
}

.post-image {
	width: 220rpx; /* 110px * 2 */
	height: 220rpx;
	border-radius: 16rpx; /* 8px * 2 */
}

/* 标签 */
.post-tags {
	padding: 0 20rpx 20rpx;
	display: flex;
	gap: 20rpx;
}

.tag {
	background: #EDE7F9;
	border: 2rpx solid #BCA8F0; /* 1px * 2 */
	border-radius: 10rpx; /* 5px * 2 */
	padding: 12rpx 24rpx;
	box-sizing: border-box;
}

.tag-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx; /* 12px * 2 */
	color: #333333;
}

/* 互动区域 */
.post-actions {
	border-top: 2rpx solid #E6D9F9; /* 1px * 2 */
	height: 100rpx; /* 50px * 2 */
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
	width: 48rpx; /* 24px * 2 */
	height: 48rpx;
}

.action-icon.liked {
	opacity: 0.8;
	transform: scale(1.1);
}

.action-icon.starred {
	opacity: 0.8;
	transform: scale(1.1);
}

.action-count {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx; /* 14px * 2 */
	line-height: 38rpx; /* 19px * 2 */
	color: #333333;
}

/* 发现页面样式 */
.discover-categories {
	background: #F6F2FC;
	padding: 20rpx 0;
}

.categories-scroll-horizontal {
	white-space: nowrap;
	padding: 0 20rpx;
}

.categories-wrapper {
	display: flex;
	gap: 30rpx;
	min-width: max-content;
}

.category-tag {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 10rpx 20rpx;
	background: #FFFFFF;
	border-radius: 10rpx;
	min-width: 130rpx;
	height: 50rpx;
	flex-shrink: 0;
}

.category-tag.active {
	background: #EDE7F9;
	border: 2rpx solid #BCA8F0;
}

.category-tag-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	color: #000000;
	line-height: 30rpx;
	white-space: nowrap;
	max-width: 8em;
	overflow: hidden;
	text-overflow: ellipsis;
}

/* 关注用户区域 */
.discover-follow-users {
	margin: 20rpx;
	background: #FFFFFF;
	border-radius: 20rpx;
	padding: 28rpx;
	position: relative;
}

.discover-view-all-fixed {
	position: absolute;
	top: 20rpx;
	right: 20rpx;
	display: flex;
	flex-direction: row;
	align-items: center;
	z-index: 10;
}

.discover-users-scroll {
	white-space: nowrap;
}

.discover-users-container {
	display: flex;
	align-items: flex-end;
	justify-content: flex-start;
	gap: 30rpx;
	min-width: max-content;
}

.discover-user-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	width: 100rpx;
	flex-shrink: 0;
}

.discover-user-avatar-wrapper {
	position: relative;
	margin-bottom: 16rpx;
}

.discover-user-avatar {
	width: 100rpx;
	height: 100rpx;
	border-radius: 50rpx;
}

.discover-user-plus-btn {
	position: absolute;
	right: -4rpx;
	bottom: 4rpx;
	width: 32rpx;
	height: 32rpx;
	background: #8A70C9;
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.discover-plus-icon {
	color: #FFFFFF;
	font-size: 20rpx;
	font-weight: bold;
}

.discover-user-label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #333333;
	text-align: center;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	max-width: 140rpx;
}

.discover-view-all-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	color: #333333;
	line-height: 32rpx;
}

.discover-chevron {
	font-size: 32rpx;
	color: #333333;
	line-height: 32rpx;
}

/* 活动卡片轮播 */
.discover-activity-swiper {
	height: 300rpx;
	margin: 20rpx;
}

.discover-activity-card {
	position: relative;
	/* width: 740rpx; */
	height: 300rpx;
	background: #FFFFFF;
	border-radius: 20rpx;
	overflow: hidden;
}


/* 底部指示点 */
.discover-activity-dots {
	position: absolute;
	left: 50%;
	bottom: 16rpx;
	transform: translateX(-50%);
	display: flex;
	gap: 12rpx;
}

.discover-dot {
	width: 12rpx;
	height: 12rpx;
	background: #EDE7F9;
	border-radius: 50%;
	transition: all 0.3s ease;
}

.discover-dot.active {
	background: #8A70C9;
	width: 24rpx;
	border-radius: 6rpx;
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
	/* margin-bottom: 16rpx; */
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
	/* margin-bottom: 8rpx; */
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

.discover-activity-participants {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 20rpx;
	line-height: 30rpx;
	color: #999999;
	flex-shrink: 0;
}

/* 帖子样式 */
.discover-post {
	margin: 20rpx;
	background: #FFFFFF;
	border-radius: 20rpx;
	padding: 30rpx;
}

.discover-post-header {
	display: flex;
	align-items: center;
	margin-bottom: 20rpx;
}

.discover-post-avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 40rpx;
	margin-right: 20rpx;
}

.discover-post-user-info {
	flex: 1;
}

.discover-post-username {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #333333;
	margin-bottom: 8rpx;
	display: block;
}

.discover-post-time {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	color: #333333;
}

.discover-follow-btn {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 10rpx 20rpx;
	background: #C1B8E8;
	border-radius: 10rpx;
	gap: 8rpx;
}

.discover-follow-plus {
	font-size: 20rpx;
	color: #333333;
	font-weight: bold;
}

.discover-follow-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	color: #333333;
	line-height: 20rpx;
}

.discover-followed-btn {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 10rpx 20rpx;
	background: #EAEEFD;
	border-radius: 10rpx;
}

.discover-followed-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	color: #333333;
	line-height: 32rpx;
}

.discover-post-content {
	margin-bottom: 20rpx;
}

.discover-post-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 16rpx;
	line-height: 44rpx;
	display: block;
}

.discover-post-desc {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	color: #333333;
	line-height: 50rpx;
	margin-bottom: 12rpx;
	display: block;
}

.discover-post-expand {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	color: #8A70C9;
	line-height: 44rpx;
	margin-bottom: 20rpx;
	display: block;
}

.discover-post-images {
	display: flex;
	gap: 20rpx;
	margin-bottom: 20rpx;
}

.discover-post-img {
	width: 220rpx;
	height: 220rpx;
	border-radius: 16rpx;
}

.discover-post-tag {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	padding: 10rpx 20rpx;
	background: #EDE7F9;
	border: 2rpx solid #BCA8F0;
	border-radius: 10rpx;
	margin-bottom: 20rpx;
}

.discover-post-tag-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	color: #333333;
	line-height: 20rpx;
}

.discover-post-actions {
	border-top: 2rpx solid #E6D9F9;
	height: 100rpx;
	display: flex;
	align-items: center;
	justify-content: space-around;
}

.discover-post-action {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.discover-action-icon {
	width: 48rpx;
	height: 48rpx;
}

.discover-action-count {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	color: #333333;
}

/* 活动页面样式 */
.activity-content {
	padding: 0 40rpx;
}

.activity-header {
	padding: 40rpx 0;
	text-align: center;
}

.activity-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 48rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 16rpx;
}

.activity-subtitle {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #666666;
}

.activity-categories {
	margin-bottom: 40rpx;
}

.categories-scroll {
	white-space: nowrap;
	padding: 20rpx 0;
}

.category-item {
	display: inline-block;
	padding: 16rpx 32rpx;
	margin-right: 20rpx;
	background: #F0F0F0;
	border-radius: 50rpx;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #666666;
	transition: all 0.3s ease;
}

.category-item.active {
	background: #8A70C9;
	color: #FFFFFF;
}

.category-text {
	white-space: nowrap;
}

.activities-list {
	display: flex;
	flex-direction: column;
	gap: 30rpx;
}

.activity-card {
	background: #FFFFFF;
	border-radius: 20rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 20rpx rgba(138, 112, 201, 0.1);
}

.activity-card-image {
	width: 100%;
	height: 300rpx;
}


.activity-card-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 36rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 16rpx;
}

.activity-card-desc {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #666666;
	margin-bottom: 20rpx;
	line-height: 1.5;
}

.activity-card-info {
	display: flex;
	flex-direction: column;
	gap: 12rpx;
	margin-bottom: 30rpx;
}

.activity-card-time, .activity-card-location, .activity-card-participants {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 26rpx;
	color: #8A70C9;
}

.activity-card-actions {
	display: flex;
	justify-content: flex-end;
}

.join-btn {
	background: #8A70C9;
	color: #FFFFFF;
	padding: 16rpx 40rpx;
	border-radius: 50rpx;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	transition: all 0.3s ease;
}

.join-btn:active {
	background: #7A60B9;
	transform: scale(0.95);
}

.join-btn-text {
	color: #FFFFFF;
}

/* 活动页面新样式 - 基于发现页面的卡片样式 */
.activity-cards-container {
	margin: 20rpx;
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.activity-card-item {
	position: relative;
	height: 300rpx;
	background: #FFFFFF;
	border-radius: 20rpx;
	overflow: hidden;
}

/* 活动卡片左侧图片 */
.activity-card-image {
	position: absolute;
	width: 220rpx;
	height: 220rpx;
	left: 40rpx;
	top: 40rpx;
	border-radius: 16rpx;
}

/* 活动卡片右侧内容区域 */
.activity-card-content {
	position: absolute;
	left: 300rpx;
	top: 32rpx;
	right: 32rpx;
	bottom: 32rpx;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

/* 活动卡片标题和爱心区域 */
.activity-card-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
}

/* 活动卡片标题 */
.activity-card-title {
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

.activity-card-like {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 4rpx;
	flex-shrink: 0;
}

.activity-heart-icon {
	width: 32rpx;
	height: 32rpx;
}

.activity-like-count {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #333333;
}

/* 活动卡片时间信息 */
.activity-card-datetime {
	display: flex;
	gap: 8rpx;
}

.activity-card-date {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #333333;
}

.activity-card-day {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #333333;
}

.activity-card-time {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #333333;
}

/* 活动卡片地点标签和已报名区域 */
.activity-card-location-tag {
	display: flex;
	justify-content: space-between;
	align-items: flex-end;
	margin-top: 8rpx;
}

.activity-card-location-wrapper {
	display: flex;
	flex-direction: column;
	align-items: flex-start;
	gap: 8rpx;
	min-width: 0;
}

/* 活动卡片地点 */
.activity-card-location {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #333333;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	max-width: 120rpx;
}

/* 活动卡片标签 */
.activity-card-tag {
	display: inline-block;
	padding: 8rpx 16rpx;
	background: #EDE7F9;
	border: 2rpx solid #BCA8F0;
	border-radius: 16rpx;
}

.activity-card-tag-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 20rpx;
	line-height: 24rpx;
	color: #333333;
}

.activity-card-participants {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #333333;
	flex-shrink: 0;
}
</style>
