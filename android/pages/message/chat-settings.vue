<template>
	<view class="chat-settings-container">
		<!-- 顶部导航栏 -->
		<TopNavigation 
			title="消息设置" 
			:showBack="true" 
			:showMore="false"
		/>
		
		<!-- 用户信息卡片 -->
		<view class="user-card" @click="viewUserProfile">
			<view class="user-info">
				<image :src="userInfo.avatar" class="user-avatar"></image>
				<view class="user-details">
					<text class="user-name">{{ userInfo.name }}</text>
					<text class="user-id">♂ ID:{{ userInfo.id }}</text>
				</view>
			</view>
			<view class="arrow-right">
				<image src="/static/content/back.png" class="arrow-icon" mode="aspectFit"></image>
			</view>
		</view>
		
		<!-- 设置选项 -->
		<view class="settings-section">
			<!-- 置顶聊天 -->
			<view class="setting-item">
				<text class="setting-label">置顶聊天</text>
				<switch 
					:checked="settings.pinTop" 
					@change="togglePinTop" 
					color="#8A70C9"
					class="setting-switch"
				/>
			</view>
			
			<!-- 消息免打扰 -->
			<view class="setting-item border-none">
				<text class="setting-label">消息免打扰</text>
				<switch 
					:checked="settings.muteNotification" 
					@change="toggleMute" 
					color="#8A70C9"
					class="setting-switch"
				/>
			</view>
		</view>
	</view>
</template>

<script>
import TopNavigation from '@/components/TopNavigation.vue'

export default {
	name: 'ChatSettings',
	components: {
		TopNavigation
	},
	data() {
		return {
			userInfo: {
				name: '会吃西瓜的小鸭纸',
				id: '542312132',
				avatar: '/static/follow/follow-users-section/Ellipse 2.png'
			},
			settings: {
				pinTop: true,
				muteNotification: false
			}
		}
	},
	onLoad(options) {
		// 接收传递过来的用户信息
		if (options.name) {
			this.userInfo.name = decodeURIComponent(options.name)
		}
		if (options.avatar) {
			this.userInfo.avatar = decodeURIComponent(options.avatar)
		}
		if (options.id) {
			this.userInfo.id = options.id
		}
		
		// 可以从本地存储或服务器加载设置
		this.loadSettings()
	},
	methods: {
		viewUserProfile() {
			// 跳转到用户详情页
			uni.navigateTo({
				url: `/pages/profile/profile?userId=${this.userInfo.id}`
			})
		},
		togglePinTop(e) {
			this.settings.pinTop = e.detail.value
			// 保存设置到本地或服务器
			this.saveSettings()
			uni.showToast({
				title: this.settings.pinTop ? '已置顶' : '已取消置顶',
				icon: 'success'
			})
		},
		toggleMute(e) {
			this.settings.muteNotification = e.detail.value
			// 保存设置到本地或服务器
			this.saveSettings()
			uni.showToast({
				title: this.settings.muteNotification ? '已开启免打扰' : '已关闭免打扰',
				icon: 'success'
			})
		},
		loadSettings() {
			// 从本地存储加载设置
			const key = `chat_settings_${this.userInfo.id}`
			const savedSettings = uni.getStorageSync(key)
			if (savedSettings) {
				this.settings = savedSettings
			}
		},
		saveSettings() {
			// 保存设置到本地存储
			const key = `chat_settings_${this.userInfo.id}`
			uni.setStorageSync(key, this.settings)
		}
	}
}
</script>

<style scoped>
.chat-settings-container {
	min-height: 100vh;
	background: #F6F2FC;
}

/* 用户信息卡片 */
.user-card {
	margin: 0 0 20rpx 0;
	background: #FFFFFF;
	border-radius: 20rpx;
	padding: 30rpx 40rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.user-info {
	display: flex;
	align-items: center;
	flex: 1;
}

.user-avatar {
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	margin-right: 30rpx;
}

.user-details {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.user-name {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 32rpx;
	line-height: 30rpx;
	color: #333333;
	margin-bottom: 20rpx;
}

.user-id {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 32rpx;
	line-height: 30rpx;
	color: #333333;
}

.arrow-right {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.arrow-icon {
	width: 48rpx;
	height: 48rpx;
	transform: rotate(180deg);
}

/* 设置选项 */
.settings-section {
	background: #FFFFFF;
}

.setting-item {
	height: 120rpx;
	padding: 0 40rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
	background: #FFFFFF;
	border-bottom: 1rpx solid #E6D9F9;
}

.setting-item.border-none {
	border-bottom: none;
}

.setting-label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 32rpx;
	line-height: 30rpx;
	color: #333333;
}

.setting-switch {
	transform: scale(0.8);
}
</style>

