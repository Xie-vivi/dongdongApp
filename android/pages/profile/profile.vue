<template>
	<view class="container">
		<!-- 顶部导航栏 - 透明 -->
		<view class="top-nav">
			<view class="nav-left">
				<image src="/static/follow/menu-01.png" class="menu-image" mode="aspectFit"></image>
			</view>
			<view class="nav-center"></view>
			<view class="nav-right">
				<text class="nav-edit-btn" @click="goEditProfile">编辑资料</text>
				<image src="/static/profile/settings-3-line.png" class="setting-image" mode="aspectFit"></image>
			</view>
		</view>

		<!-- 顶部个人信息 -->
		<view class="header">
			<!-- 背景图 -->
			<image class="bg-image" src="/static/profile/Rectangle 279.png" mode="aspectFill"></image>
			<view class="header-main">
				<image class="avatar-big" src="/static/follow/follow-users-section/Ellipse 11.png" mode="aspectFill"></image>
				<view class="header-info">
					<text class="nick">会吃西瓜的小鸭纸</text>
					<text class="uid">ID:542312132 · 美国</text>
					<view class="tag-row">
						<text class="tag">中央民族大学</text>
						<text class="tag">INFP</text>
						<text class="tag">21岁</text>
					</view>
				</view>
			</view>
			<view class="desc">我也不知道简介写什么好，什么时候才能放假啊啊啊啊啊啊啊啊啊哈哈就是说日子一天也过不下去了，快放我回家！简介只许写五行。</view>
			<view class="stat-row">
				<view class="stat">
					<text class="stat-num">20</text>
					<text class="stat-label">关注的场</text>
				</view>
				<view class="stat">
					<text class="stat-num">155</text>
					<text class="stat-label">粉丝</text>
				</view>
				<view class="stat">
					<text class="stat-num">33</text>
					<text class="stat-label">关注的人</text>
				</view>
			</view>
		</view>

		<!-- 四个入口 -->
		<view class="quick">
			<view class="quick-item" v-for="q in quickEntries" :key="q.key" @click="handleQuickEntry(q.key)">
				<image class="quick-icon" :src="q.icon" mode="aspectFit"></image>
				<text class="quick-text">{{ q.label }}</text>
			</view>
		</view>

		<view class="content">
			<!-- 主 Tab + 搜索按钮 -->
			<view class="tab-main" v-if="mode === 'normal'">
				<view class="tab-row">
					<text :class="['tab-main-item', mainTab === 'post' && 'active']" @click="mainTab = 'post'">帖子</text>
					<text :class="['tab-main-item', mainTab === 'activity' && 'active']" @click="mainTab = 'activity'">活动</text>
					<view class="tab-search" @click="mode = 'search'">
						<image class="search-icon" src="/static/profile/search-01.png" mode="aspectFit"></image>
					</view>
				</view>
				<scroll-view scroll-x class="tab-sub-scroll">
					<view class="tab-sub">
						<text
							v-for="p in (mainTab === 'post' ? postSubTabs : activitySubTabs)"
							:key="p.key"
							:class="['tab-sub-item', getSubActive(p.key) && 'active']"
							@click="setSub(p.key)"
						>
							{{ p.label }}
						</text>
					</view>
				</scroll-view>
				<scroll-view scroll-y class="list">
					<view v-if="mainTab === 'post'">
						<view class="post-card" v-for="item in currentPostList" :key="item.id">
							<!-- 用户信息 -->
							<view class="post-header">
								<image class="user-avatar-small" src="/static/follow/follow-users-section/Ellipse 11.png" mode="aspectFill" />
								<view class="user-info">
									<text class="username">会吃西瓜的小鸭纸</text>
									<text class="post-time">昨天 20:15</text>
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
									<text class="tag-text">某某场</text>
								</view>
							</view>
							
							<!-- 互动区域 -->
							<view class="post-actions">
								<view class="action-item">
									<image class="action-icon" src="/static/follow/5.png"></image>
									<text class="action-count">21</text>
								</view>
								<view class="action-item">
									<image class="action-icon" src="/static/follow/6.png"></image>
									<text class="action-count">2</text>
								</view>
								<view class="action-item">
									<image class="action-icon" src="/static/follow/7.png"></image>
									<text class="action-count">1</text>
								</view>
							</view>
						</view>
					</view>
					<view v-else>
						<!-- 活动卡片列表 -->
						<view class="activity-cards-container">
							<view class="activity-card-item" v-for="a in currentActivityList" :key="a.id">
								<!-- 左侧图片 -->
								<image class="activity-card-image" :src="a.image" mode="aspectFill" />
								
								<!-- 右侧内容区域 -->
								<view class="activity-card-content">
									<!-- 标题和爱心 -->
									<view class="activity-card-header">
										<text class="activity-card-title">{{ a.title }}</text>
										<view class="activity-card-like">
											<image class="activity-heart-icon" src="/static/discover/1.png" mode="aspectFit" />
											<text class="activity-like-count">{{ a.likes }}</text>
										</view>
									</view>
									
									<!-- 日期信息 -->
									<view class="activity-card-datetime">
										<text class="activity-card-date">{{ a.date }}</text>
										<text class="activity-card-day">{{ a.day }}</text>
									</view>
									
									<!-- 地点和标签 -->
									<view class="activity-card-location-tag">
										<view class="activity-card-location-wrapper">
											<text class="activity-card-location">{{ a.location }}</text>
											<view class="activity-card-tag">
												<text class="activity-card-tag-text">{{ a.tag }}</text>
											</view>
										</view>
										<view class="activity-card-right">
											<text class="activity-card-time">{{ a.time }}</text>
											<text class="activity-card-participants">已报名：{{ a.participants }}人</text>
										</view>
									</view>
								</view>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>

			<!-- 搜索页 -->
			<view v-else-if="mode === 'search'" class="search-page">
				<!-- 搜索页面容器 -->
				<view class="search-container">
					<!-- 搜索标题栏 -->
					<view class="search-header">
						<image class="back-icon" src="/static/profile/contract-left-right-line.png" mode="aspectFit" @click="mode = 'normal'"></image>
						<text class="search-page-title">搜索我的</text>
					</view>
					
					<!-- 搜索输入区域 -->
					<view class="search-input-area">
						<view class="search-input-box">
							<input v-model="searchText" class="search-input-field" placeholder="搜索我的帖子/活动/点赞/收藏" />
						</view>
						<view class="search-btn" @click="doSearch">
							<text class="search-btn-text">搜索</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 搜索结果页 -->
			<view v-else class="search-result">
				<!-- 搜索标题栏 -->
				<view class="search-header">
					<image class="back-icon" src="/static/profile/contract-left-right-line.png" mode="aspectFit" @click="mode = 'search'"></image>
					<text class="search-page-title">搜索我的</text>
				</view>
				
				<!-- 搜索结果列表 -->
				<scroll-view scroll-y class="search-results">
					<view class="result-item" v-for="item in searchResultPosts" :key="item.id">
						<view class="post-header">
							<image class="user-avatar" src="/static/follow/follow-users-section/Ellipse 11.png" mode="aspectFill"></image>
							<view class="user-info">
								<text class="username">会吃西瓜的小鸭纸</text>
								<text class="post-time">昨天 20:15</text>
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
								<text class="tag-text">某某场</text>
							</view>
						</view>
						
						<!-- 互动区域 -->
						<view class="post-actions">
							<view class="action-item">
								<image class="action-icon" src="/static/follow/5.png"></image>
								<text class="action-count">21</text>
							</view>
							<view class="action-item">
								<image class="action-icon" src="/static/follow/6.png"></image>
								<text class="action-count">2</text>
							</view>
							<view class="action-item">
								<image class="action-icon" src="/static/follow/7.png"></image>
								<text class="action-count">1</text>
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
			postSub: 'mine',
			activitySub: 'mine',
			mode: 'normal',
			messageCount: 55,
			searchText: '',
			searchResultPosts: [],
			quickEntries: [
				{ key: 'draft', label: '草稿箱', icon: '/static/profile/archive-2-line.png' },
				{ key: 'signup', label: '报名的活动', icon: '/static/profile/calendar-check-line.png' },
				{ key: 'history', label: '浏览记录', icon: '/static/profile/clock-forward.png' },
				{ key: 'mineField', label: '我建的场', icon: '/static/profile/team-line.png' }
			],
			postSubTabs: [
				{ key: 'mine', label: '我发布的帖子' },
				{ key: 'like', label: '我点赞的帖子' },
				{ key: 'star', label: '我收藏的帖子' },
				{ key: 'view', label: '我浏览的帖子' }
			],
			activitySubTabs: [
				{ key: 'mine', label: '我发布的活动' },
				{ key: 'like', label: '我点赞的活动' }
			],
			postsMine: [
				{ id: 1, title: '帖子大标题我也写长一点', subtitle: '短一点' }
			],
			postsLike: [
				{ id: 2, title: '我点赞的帖子标题示例', subtitle: '短一点' }
			],
			postsStar: [
				{ id: 3, title: '我收藏的帖子标题示例', subtitle: '短一点' }
			],
			postsView: [
				{ id: 4, title: '我浏览的帖子标题示例', subtitle: '短一点' }
			],
			activitiesMine: [
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
					image: '/static/discover/2.png'
				}
			],
			activitiesLike: [
				{
					id: 2,
					title: '羽毛球周末友谊赛',
					date: '2025.9.1',
					day: '周日',
					time: '15:00 - 17:00',
					location: '体育馆1楼',
					tag: '羽毛球场',
					participants: 8,
					likes: 10,
					image: '/static/discover/2.png'
				}
			],
			searchChips: [
				{ key: 'post', label: '帖子 1' },
				{ key: 'activity', label: '活动 2' },
				{ key: 'likePost', label: '点赞帖子 13' },
				{ key: 'starPost', label: '收藏帖子 7' },
				{ key: 'history', label: '浏览记录 9' }
			],
			searchResultPosts: []
		}
	},
	computed: {
		currentPostList() {
			if (this.postSub === 'mine') return this.postsMine
			if (this.postSub === 'like') return this.postsLike
			if (this.postSub === 'star') return this.postsStar
			if (this.postSub === 'view') return this.postsView
			return []
		},
		currentActivityList() {
			if (this.activitySub === 'mine') return this.activitiesMine
			if (this.activitySub === 'like') return this.activitiesLike
			return []
		}
	},
	methods: {
		getSubActive(key) {
			return this.mainTab === 'post' ? this.postSub === key : this.activitySub === key
		},
		setSub(key) {
			if (this.mainTab === 'post') this.postSub = key
			else this.activitySub = key
		},
		goEditProfile() {
			uni.navigateTo({
				url: '/pages/profile/edit-profile'
			})
		},
		doSearch() {
			const kw = this.searchText.toLowerCase()
			this.searchResultPosts = this.postsMine.filter(p => !kw || p.title.toLowerCase().includes(kw))
			this.mode = 'searchResult'
		},
		handleQuickEntry(key) {
			switch(key) {
				case 'draft':
					uni.navigateTo({
						url: '/pages/profile/draft'
					})
					break
				case 'signup':
					uni.navigateTo({
						url: '/pages/profile/signed-activities'
					})
					break
				case 'history':
					uni.navigateTo({
						url: '/pages/profile/browse-history'
					})
					break
				case 'mineField':
					// 我建的场页面
					uni.showToast({
						title: '功能开发中',
						icon: 'none'
					})
					break
			}
		}
	}
}
</script>

<style scoped>
.container{min-height:100vh;background:#f9f6ff;display:flex;flex-direction:column;}
.top-nav{position:absolute;top:44px;left:0;right:0;height:100rpx;display:flex;flex-direction:row;justify-content:space-between;align-items:center;padding:0 40rpx;background:transparent;z-index:10;}
.nav-left{width:50rpx;height:50rpx;}
.menu-image{width:100%;height:100%;}
.nav-center{flex:1;}
.nav-right{display:flex;flex-direction:row;align-items:center;gap:20rpx;}
.nav-edit-btn{font-size:24rpx;color:#333;background:#fff;border:2rpx solid #333;border-radius:30rpx;padding:12rpx 24rpx;}
.setting-image{width:48rpx;height:48rpx;}
.header{position:relative;padding-top:200rpx;padding-bottom:30rpx;}
.bg-image{position:absolute;top:0;left:0;right:0;bottom:0;width:100%;height:100%;z-index:1;}
.header-main{position:relative;z-index:2;flex-direction:row;display:flex;padding:0 40rpx;}
.avatar-big{width:120rpx;height:120rpx;border-radius:60rpx;margin-right:24rpx;border:4rpx solid rgba(255,255,255,0.3);}
.header-info{flex:1;}
.nick{font-size:32rpx;font-weight:700;color:#333;}
.uid{display:block;margin-top:6rpx;font-size:22rpx;color:#666;}
.tag-row{margin-top:10rpx;flex-wrap:wrap;display:flex;}
.tag{display:inline-block;font-size:22rpx;color:#8b5cf6;background:#f4ecff;border-radius:999rpx;padding:4rpx 14rpx;margin-right:8rpx;margin-bottom:6rpx;}
.desc{position:relative;z-index:2;margin-top:16rpx;margin-left:40rpx;margin-right:40rpx;font-size:22rpx;color:#333;line-height:34rpx;text-align:left;text-indent:0;white-space:normal;}
.stat-row{position:relative;z-index:2;margin-top:18rpx;margin-left:40rpx;margin-right:40rpx;flex-direction:row;display:flex;justify-content:space-between;}
.stat{text-align:center;flex:1;}
.stat-num{font-size:30rpx;color:#333;font-weight:700;}
.stat-label{display:block;margin-top:4rpx;font-size:22rpx;color:#666;}
.quick{margin:20rpx 40rpx 10rpx;background:#fff;border-radius:24rpx;padding:24rpx 0;flex-direction:row;display:flex;justify-content:space-around;box-shadow:0 4rpx 12rpx rgba(0,0,0,0.08);}
.quick-item{text-align:center;flex:1;display:flex;flex-direction:column;align-items:center;}
.quick-icon{width:48rpx;height:48rpx;margin-bottom:12rpx;}
.quick-text{font-size:24rpx;color:#555;}
.content{flex:1;padding:0 40rpx 120rpx;}
.tab-row{flex-direction:row;display:flex;align-items:center;margin-top:16rpx;}
.tab-main-item{margin-right:40rpx;font-size:28rpx;color:#888;}
.tab-main-item.active{color:#7445ff;border-bottom:4rpx solid #7445ff;}
.tab-search{margin-left:auto;width:48rpx;height:48rpx;display:flex;align-items:center;justify-content:center;}
.search-icon{width:32rpx;height:32rpx;}
.tab-sub-scroll{margin-top:16rpx;white-space:nowrap;}
.tab-sub{flex-direction:row;display:flex;white-space:nowrap;}
.tab-sub-item{margin-right:26rpx;font-size:24rpx;color:#888;white-space:nowrap;flex-shrink:0;}
.tab-sub-item.active{color:#7445ff;border-bottom:3rpx solid #7445ff;}
.list{margin-top:16rpx;max-height:680rpx;}
.post-card{background:#fff;border-radius:24rpx;padding:20rpx;margin-bottom:16rpx;}
.post-header{flex-direction:row;display:flex;align-items:center;}
.user-avatar-small{width:72rpx;height:72rpx;border-radius:36rpx;margin-right:16rpx;}
.user-info{flex:1;}
.username{font-size:26rpx;color:#333;font-weight:600;}
.post-time{display:block;font-size:22rpx;color:#aaa;margin-top:4rpx;}
.post-content{margin-top:12rpx;}
.post-title{display:block;font-size:28rpx;color:#333;font-weight:600;}
.post-text{display:block;margin-top:4rpx;font-size:24rpx;color:#666;}
.post-tags{margin-top:10rpx;}
.tag{font-size:22rpx;color:#7445ff;background:#f4ecff;border-radius:999rpx;padding:4rpx 14rpx;}
.post-actions{border-top:2rpx solid #E6D9F9;height:100rpx;display:flex;align-items:center;justify-content:space-around;}
.action-item{display:flex;align-items:center;gap:12rpx;}
.action-icon{width:48rpx;height:48rpx;}
.action-count{font-family:'Alibaba PuHuiTi',sans-serif;font-size:28rpx;line-height:38rpx;color:#333333;}
.activity-cards-container{margin:20rpx;display:flex;flex-direction:column;gap:20rpx;}
.activity-card-item{position:relative;height:300rpx;background:#FFFFFF;border-radius:20rpx;overflow:hidden;box-shadow:0 4rpx 20rpx rgba(138,112,201,0.1);}
.activity-card-image{position:absolute;width:220rpx;height:220rpx;left:40rpx;top:40rpx;border-radius:20rpx;}
.activity-card-content{position:absolute;left:300rpx;top:32rpx;right:32rpx;bottom:32rpx;display:flex;flex-direction:column;justify-content:space-between;}
.activity-card-header{display:flex;justify-content:space-between;align-items:flex-start;}
.activity-card-title{font-family:'Alibaba PuHuiTi',sans-serif;font-weight:500;font-size:32rpx;line-height:40rpx;color:#333333;flex:1;margin-right:20rpx;display:-webkit-box;-webkit-box-orient:vertical;-webkit-line-clamp:2;overflow:hidden;}
.activity-card-like{display:flex;flex-direction:column;align-items:center;gap:8rpx;}
.activity-heart-icon{width:32rpx;height:32rpx;}
.activity-like-count{font-family:'Alibaba PuHuiTi',sans-serif;font-size:20rpx;line-height:24rpx;color:#333333;}
.activity-card-datetime{display:flex;gap:8rpx;}
.activity-card-date{font-family:'Alibaba PuHuiTi',sans-serif;font-size:24rpx;line-height:30rpx;color:#333333;}
.activity-card-day{font-family:'Alibaba PuHuiTi',sans-serif;font-size:24rpx;line-height:30rpx;color:#333333;white-space:nowrap;}
.activity-card-location-tag{display:flex;justify-content:space-between;align-items:flex-end;margin-top:8rpx;}
.activity-card-location-wrapper{display:flex;flex-direction:column;align-items:flex-start;gap:8rpx;}
.activity-card-location{font-family:'Alibaba PuHuiTi',sans-serif;font-size:24rpx;line-height:30rpx;color:#333333;margin-bottom:8rpx;}
.activity-card-tag{display:inline-block;padding:8rpx 16rpx;background:#EDE7F9;border-radius:16rpx;}
.activity-card-tag-text{font-family:'Alibaba PuHuiTi',sans-serif;font-size:20rpx;line-height:24rpx;color:#333333;}
.activity-card-right{display:flex;flex-direction:column;align-items:flex-end;gap:4rpx;}
.activity-card-time{font-family:'Alibaba PuHuiTi',sans-serif;font-size:24rpx;line-height:30rpx;color:#8A70C9;}
.activity-card-participants{font-family:'Alibaba PuHuiTi',sans-serif;font-size:24rpx;line-height:30rpx;color:#8A70C9;}
.search-page{position:absolute;top:0;left:0;right:0;bottom:0;background:#F6F2FC;}
.search-container{position:absolute;width:100%;height:681rpx;top:180rpx;left:0;background:#FFFFFF;border-radius:20rpx 20rpx 0 0;}
.search-header{display:flex;align-items:center;padding:40rpx 40rpx 20rpx;}
.back-icon{width:48rpx;height:48rpx;margin-right:20rpx;}
.search-page-title{font-family:'Alibaba PuHuiTi',sans-serif;font-weight:500;font-size:32rpx;line-height:44rpx;color:#333333;flex:1;text-align:center;margin-right:68rpx;}
.search-input-area{display:flex;padding:0 20rpx;gap:20rpx;}
.search-input-box{flex:1;height:80rpx;background:#FFFFFF;border:2rpx solid #8A70C9;border-radius:20rpx;display:flex;align-items:center;padding:0 20rpx;}
.search-input-field{flex:1;font-family:'Alibaba PuHuiTi',sans-serif;font-size:32rpx;line-height:44rpx;color:#757575;border:none;outline:none;}
.search-btn{width:140rpx;height:80rpx;background:#8A70C9;border-radius:20rpx;display:flex;align-items:center;justify-content:center;}
.search-btn-text{font-family:'Alibaba PuHuiTi',sans-serif;font-size:28rpx;line-height:38rpx;color:#FFFFFF;}
.search-result{position:absolute;top:0;left:0;right:0;bottom:0;background:#F6F2FC;display:flex;flex-direction:column;}
.search-results{flex:1;padding:20rpx 40rpx;}
.result-item{background:#FFFFFF;border-radius:20rpx;padding:40rpx;margin-bottom:20rpx;}
.post-header{display:flex;align-items:center;margin-bottom:20rpx;}
.user-avatar{width:72rpx;height:72rpx;border-radius:36rpx;margin-right:20rpx;}
.user-info{flex:1;}
.username{font-family:'Alibaba PuHuiTi',sans-serif;font-size:28rpx;line-height:36rpx;color:#333333;font-weight:600;}
.post-time{font-family:'Alibaba PuHuiTi',sans-serif;font-size:24rpx;line-height:32rpx;color:#999999;margin-top:4rpx;}
.post-content{margin-bottom:20rpx;}
.post-title{font-family:'Alibaba PuHuiTi',sans-serif;font-size:32rpx;line-height:44rpx;color:#333333;font-weight:600;display:block;margin-bottom:8rpx;}
.post-text{font-family:'Alibaba PuHuiTi',sans-serif;font-size:28rpx;line-height:38rpx;color:#666666;}
.post-tags{margin-bottom:20rpx;}
.tag{display:inline-block;background:#EDE7F9;border-radius:16rpx;padding:8rpx 16rpx;}
.tag-text{font-family:'Alibaba PuHuiTi',sans-serif;font-size:24rpx;line-height:32rpx;color:#8A70C9;}
.post-actions{display:flex;gap:40rpx;}
.action-item{display:flex;align-items:center;gap:8rpx;}
.action-icon{width:32rpx;height:32rpx;}
.action-count{font-family:'Alibaba PuHuiTi',sans-serif;font-size:24rpx;line-height:32rpx;color:#8A70C9;}
.search,.search-result{padding-top:20rpx;}
.search-bar{flex-direction:row;display:flex;align-items:center;margin-bottom:20rpx;}
.back{font-size:32rpx;color:#333;margin-right:10rpx;}
.search-title{font-size:28rpx;color:#333;font-weight:600;}
.search-input-row{flex-direction:row;display:flex;align-items:center;background:#fff;border-radius:999rpx;padding:8rpx 12rpx;}
.search-input{flex:1;font-size:24rpx;padding:0 10rpx;}
.btn-search{margin-left:10rpx;background:#7445ff;color:#fff;font-size:24rpx;border-radius:999rpx;padding:10rpx 24rpx;}
.chip-row{margin:16rpx 0;flex-wrap:wrap;display:flex;}
.chip{font-size:22rpx;color:#7445ff;background:#f4ecff;border-radius:999rpx;padding:4rpx 14rpx;margin-right:8rpx;margin-bottom:8rpx;}
</style>
