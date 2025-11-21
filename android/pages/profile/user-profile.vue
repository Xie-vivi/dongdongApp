<template>
	<view class="container">
		<!-- 顶部导航栏 - 透明 -->
		<view class="top-nav">
			<view class="nav-left" @click="goBack">
				<image src="/static/content/back.png" class="back-icon" mode="aspectFit"></image>
			</view>
			<view class="nav-center"></view>
			<view class="nav-right">
				<image src="/static/profile/more.png" class="more-icon" mode="aspectFit"></image>
			</view>
		</view>

		<!-- 顶部个人信息 -->
		<view class="header">
			<!-- 背景图 -->
			<image class="bg-image" src="/static/profile/Rectangle 279.png" mode="aspectFill"></image>
			<view class="header-main">
				<view class="avatar-wrapper">
					<image class="avatar-big" src="/static/follow/follow-users-section/Ellipse 11.png" mode="aspectFill"></image>
					<image v-if="userInfo.isCertified" class="cert-badge" src="/static/profile/cert-badge.png" mode="aspectFit"></image>
				</view>
				<view class="header-info">
					<text class="nick">{{ userInfo.nickname }}</text>
					<view class="id-location">
						<image v-if="userInfo.isCertified" class="cert-icon" src="/static/profile/cert-icon.png" mode="aspectFit"></image>
						<text class="cert-text" v-if="userInfo.isCertified">已认证</text>
						<text class="uid">ID:{{ userInfo.uid }}</text>
						<text class="location">{{ userInfo.location }}</text>
					</view>
					<view class="tag-row">
						<text class="tag" v-if="userInfo.school">{{ userInfo.school }}</text>
						<text class="tag" v-if="userInfo.mbti">{{ userInfo.mbti }}</text>
						<text class="tag" v-if="userInfo.age">{{ userInfo.age }}岁</text>
					</view>
				</view>
			</view>
			<view class="desc">{{ userInfo.bio }}</view>
			<view class="stat-row">
				<view class="stat">
					<text class="stat-num">{{ userInfo.fieldCount }}</text>
					<text class="stat-label">关注的场</text>
				</view>
				<view class="stat">
					<text class="stat-num">{{ userInfo.followers }}</text>
					<text class="stat-label">粉丝</text>
				</view>
				<view class="stat">
					<text class="stat-num">{{ userInfo.following }}</text>
					<text class="stat-label">关注的人</text>
				</view>
			</view>
			
			<!-- 关注和私信按钮 -->
			<view class="action-buttons">
				<view class="follow-button" :class="{ followed: isFollowed }" @click="toggleFollow">
					<text class="follow-text">{{ isFollowed ? '已关注' : '关注' }}</text>
				</view>
				<view class="message-button" @click="sendMessage">
					<text class="message-text">私信</text>
				</view>
			</view>
		</view>

		<view class="content">
			<!-- 主 Tab + 搜索按钮 -->
			<view class="tab-main">
				<view class="tab-row">
					<text :class="['tab-main-item', mainTab === 'post' && 'active']" @click="mainTab = 'post'">帖子</text>
					<text :class="['tab-main-item', mainTab === 'activity' && 'active']" @click="mainTab = 'activity'">活动</text>
					<view class="tab-search" @click="showSearch">
						<image class="search-icon" src="/static/profile/search-01.png" mode="aspectFit"></image>
					</view>
				</view>
				
				<scroll-view scroll-y class="list">
					<view v-if="mainTab === 'post'">
						<view class="post-card" v-for="item in posts" :key="item.id">
							<!-- 用户信息 -->
							<view class="post-header">
								<image class="user-avatar-small" src="/static/follow/follow-users-section/Ellipse 11.png" mode="aspectFill" />
								<view class="user-info">
									<text class="username">{{ userInfo.nickname }}</text>
									<text class="post-time">{{ item.time }}</text>
								</view>
								<view class="follow-status" v-if="isFollowed">
									<text class="follow-status-text">已关注</text>
								</view>
							</view>
							
							<!-- 帖子内容 -->
							<view class="post-content">
								<text class="post-title">{{ item.title }}</text>
								<text class="post-text">{{ item.subtitle }}</text>
							</view>
							
							<!-- 标签 -->
							<view class="post-tags">
								<view class="tag">
									<text class="tag-text">{{ item.tag }}</text>
								</view>
							</view>
							
							<!-- 互动区域 -->
							<view class="post-actions">
								<view class="action-item">
									<image class="action-icon" src="/static/follow/5.png"></image>
									<text class="action-count">{{ item.likes }}</text>
								</view>
								<view class="action-item">
									<image class="action-icon" src="/static/follow/6.png"></image>
									<text class="action-count">{{ item.stars }}</text>
								</view>
								<view class="action-item">
									<image class="action-icon" src="/static/follow/7.png"></image>
									<text class="action-count">{{ item.comments }}</text>
								</view>
							</view>
						</view>
					</view>
					<view v-else>
						<!-- 活动列表 -->
						<view class="activity-card" v-for="activity in activities" :key="activity.id">
							<image class="activity-image" :src="activity.image" mode="aspectFill" />
							<view class="activity-content">
								<view class="activity-header">
									<text class="activity-title">{{ activity.title }}</text>
									<view class="activity-like">
										<image class="heart-icon" src="/static/discover/1.png" mode="aspectFit" />
										<text class="like-count">{{ activity.likes }}</text>
									</view>
								</view>
								<view class="activity-datetime">
									<text class="activity-date">{{ activity.date }}</text>
									<text class="activity-day">{{ activity.day }}</text>
								</view>
								<view class="activity-location-tag">
									<view class="activity-location-wrapper">
										<text class="activity-location">{{ activity.location }}</text>
										<view class="activity-tag">
											<text class="activity-tag-text">{{ activity.tag }}</text>
										</view>
									</view>
									<view class="activity-right">
										<text class="activity-time">{{ activity.time }}</text>
										<text class="activity-participants">已报名：{{ activity.participants }}人</text>
									</view>
								</view>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
		</view>

		<BottomNavigation :currentPage="'profile'" :messageCount="messageCount" />
	</view>
</template>

<script>
import BottomNavigation from '@/components/BottomNavigation.vue'

export default {
	components: { BottomNavigation },
	data() {
		return {
			mainTab: 'post',
			messageCount: 55,
			isFollowed: true,
			userInfo: {
				nickname: '假如说名字很长的话十一二三四五六七八九十一二三四',
				uid: '542312132',
				location: '美国',
				isCertified: true,
				school: '中央民族大学',
				mbti: 'INFP',
				age: '21',
				bio: '我也不知道简介写什么好\n什么时候才能放假啊啊啊啊啊啊啊啊啊哈哈就是说日子一天也过不下去了\n快放我回家！\n简介只许写五行多了不许写了：）',
				fieldCount: 20,
				followers: 155,
				following: 33
			},
			posts: [
				{
					id: 1,
					title: '帖子大标题我也写长一点',
					subtitle: '短一点',
					tag: '某某场',
					time: '昨天 20:15',
					likes: 21,
					stars: 2,
					comments: 1
				}
			],
			activities: []
		}
	},
	onLoad(options) {
		// 从参数中获取用户ID
		if (options.userId) {
			// 加载用户信息
			this.loadUserInfo(options.userId)
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		
		loadUserInfo(userId) {
			// 这里应该调用API加载用户信息
		},
		
		toggleFollow() {
			this.isFollowed = !this.isFollowed
			uni.showToast({
				title: this.isFollowed ? '已关注' : '已取消关注',
				icon: 'none'
			})
		},
		
		sendMessage() {
			uni.navigateTo({
				url: '/pages/message/chat-detail'
			})
		},
		
		showSearch() {
			uni.showToast({
				title: '搜索功能开发中',
				icon: 'none'
			})
		}
	}
}
</script>

<style scoped>
.container{min-height:100vh;background:#f9f6ff;display:flex;flex-direction:column;}
.top-nav{position:absolute;top:44px;left:0;right:0;height:100rpx;display:flex;flex-direction:row;justify-content:space-between;align-items:center;padding:0 40rpx;background:transparent;z-index:10;}
.nav-left{width:50rpx;height:50rpx;}
.back-icon{width:48rpx;height:48rpx;}
.nav-center{flex:1;}
.nav-right{width:50rpx;height:50rpx;}
.more-icon{width:48rpx;height:48rpx;}
.header{position:relative;padding-top:200rpx;padding-bottom:30rpx;}
.bg-image{position:absolute;top:0;left:0;right:0;bottom:0;width:100%;height:100%;z-index:1;}
.header-main{position:relative;z-index:2;flex-direction:row;display:flex;padding:0 40rpx;}
.avatar-wrapper{position:relative;margin-right:24rpx;}
.avatar-big{width:120rpx;height:120rpx;border-radius:60rpx;border:4rpx solid rgba(255,255,255,0.3);}
.cert-badge{position:absolute;bottom:0;right:0;width:32rpx;height:32rpx;}
.header-info{flex:1;}
.nick{font-size:32rpx;font-weight:700;color:#333;display:block;line-height:44rpx;}
.id-location{display:flex;align-items:center;margin-top:6rpx;flex-wrap:wrap;}
.cert-icon{width:28rpx;height:28rpx;margin-right:8rpx;}
.cert-text{font-size:22rpx;color:#8A70C9;margin-right:16rpx;}
.uid{font-size:22rpx;color:#666;margin-right:16rpx;}
.location{font-size:22rpx;color:#666;}
.tag-row{margin-top:10rpx;flex-wrap:wrap;display:flex;}
.tag{display:inline-block;font-size:22rpx;color:#8b5cf6;background:#f4ecff;border-radius:999rpx;padding:4rpx 14rpx;margin-right:8rpx;margin-bottom:6rpx;}
.desc{position:relative;z-index:2;margin-top:16rpx;margin-left:40rpx;margin-right:40rpx;font-size:22rpx;color:#333;line-height:34rpx;text-align:left;white-space:pre-wrap;}
.stat-row{position:relative;z-index:2;margin-top:18rpx;margin-left:40rpx;margin-right:40rpx;flex-direction:row;display:flex;justify-content:space-between;}
.stat{text-align:center;flex:1;}
.stat-num{font-size:30rpx;color:#333;font-weight:700;}
.stat-label{display:block;margin-top:4rpx;font-size:22rpx;color:#666;}
.action-buttons{position:relative;z-index:2;display:flex;gap:20rpx;padding:20rpx 40rpx 0;}
.follow-button{flex:1;height:72rpx;border:2rpx solid #8A70C9;border-radius:36rpx;display:flex;align-items:center;justify-content:center;background:#FFFFFF;}
.follow-button.followed{background:#F0F0F0;border-color:#999999;}
.follow-text{font-family:'Alibaba PuHuiTi',sans-serif;font-size:28rpx;color:#8A70C9;}
.follow-button.followed .follow-text{color:#666666;}
.message-button{flex:1;height:72rpx;background:#8A70C9;border-radius:36rpx;display:flex;align-items:center;justify-content:center;}
.message-text{font-family:'Alibaba PuHuiTi',sans-serif;font-size:28rpx;color:#FFFFFF;}
.content{flex:1;padding:0 40rpx 120rpx;}
.tab-row{flex-direction:row;display:flex;align-items:center;margin-top:16rpx;}
.tab-main-item{margin-right:40rpx;font-size:28rpx;color:#888;}
.tab-main-item.active{color:#7445ff;border-bottom:4rpx solid #7445ff;}
.tab-search{margin-left:auto;width:48rpx;height:48rpx;display:flex;align-items:center;justify-content:center;}
.search-icon{width:32rpx;height:32rpx;}
.list{margin-top:16rpx;max-height:680rpx;}
.post-card{background:#fff;border-radius:24rpx;padding:20rpx;margin-bottom:16rpx;}
.post-header{flex-direction:row;display:flex;align-items:center;position:relative;}
.user-avatar-small{width:72rpx;height:72rpx;border-radius:36rpx;margin-right:16rpx;}
.user-info{flex:1;}
.username{font-size:26rpx;color:#333;font-weight:600;}
.post-time{display:block;font-size:22rpx;color:#aaa;margin-top:4rpx;}
.follow-status{position:absolute;right:0;top:0;padding:8rpx 16rpx;background:#F0F0F0;border-radius:16rpx;}
.follow-status-text{font-size:22rpx;color:#666666;}
.post-content{margin-top:12rpx;}
.post-title{display:block;font-size:28rpx;color:#333;font-weight:600;}
.post-text{display:block;margin-top:4rpx;font-size:24rpx;color:#666;}
.post-tags{margin-top:10rpx;}
.tag-text{font-size:22rpx;color:#7445ff;}
.post-actions{border-top:2rpx solid #E6D9F9;height:100rpx;display:flex;align-items:center;justify-content:space-around;}
.action-item{display:flex;align-items:center;gap:12rpx;}
.action-icon{width:48rpx;height:48rpx;}
.action-count{font-family:'Alibaba PuHuiTi',sans-serif;font-size:28rpx;line-height:38rpx;color:#333333;}
.activity-card{position:relative;height:300rpx;background:#FFFFFF;border-radius:20rpx;overflow:hidden;margin-bottom:20rpx;display:flex;padding:40rpx 20rpx;}
.activity-image{width:200rpx;height:200rpx;border-radius:16rpx;flex-shrink:0;margin-right:20rpx;}
.activity-content{flex:1;display:flex;flex-direction:column;justify-content:space-between;min-width:0;}
.activity-header{display:flex;justify-content:space-between;align-items:flex-start;}
.activity-title{font-family:'Alibaba PuHuiTi',sans-serif;font-weight:500;font-size:32rpx;line-height:40rpx;color:#333333;flex:1;margin-right:20rpx;}
.activity-like{display:flex;flex-direction:column;align-items:center;gap:8rpx;}
.heart-icon{width:32rpx;height:32rpx;}
.like-count{font-family:'Alibaba PuHuiTi',sans-serif;font-size:20rpx;color:#333333;}
.activity-datetime{display:flex;gap:8rpx;}
.activity-date,.activity-day{font-family:'Alibaba PuHuiTi',sans-serif;font-size:24rpx;color:#333333;}
.activity-location-tag{display:flex;justify-content:space-between;align-items:flex-end;}
.activity-location-wrapper{display:flex;flex-direction:column;gap:8rpx;}
.activity-location{font-family:'Alibaba PuHuiTi',sans-serif;font-size:24rpx;color:#333333;}
.activity-tag{padding:8rpx 16rpx;background:#EDE7F9;border-radius:16rpx;}
.activity-tag-text{font-family:'Alibaba PuHuiTi',sans-serif;font-size:20rpx;color:#333333;}
.activity-right{display:flex;flex-direction:column;align-items:flex-end;gap:4rpx;}
.activity-time,.activity-participants{font-family:'Alibaba PuHuiTi',sans-serif;font-size:24rpx;color:#8A70C9;}
</style>
