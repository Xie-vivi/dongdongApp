<template>
	<view class="group-settings-container">
		<!-- 顶部导航栏 -->
		<TopNavigation 
			title="消息设置" 
			:showBack="true" 
			:showMore="false"
		/>
		
		<!-- 群信息卡片 -->
		<view class="group-card">
			<view class="group-info">
				<image :src="groupInfo.avatar" class="group-avatar"></image>
				<view class="group-details">
					<text class="group-name">{{ groupInfo.name }}（{{ groupInfo.groupField }}）</text>
					<text class="group-desc">{{ groupInfo.description }}</text>
				</view>
			</view>
			<view class="join-btn" @click="joinGroup">
				<text class="join-text">进场</text>
			</view>
		</view>
		
		<!-- 群成员区域 -->
		<view class="members-section">
			<view class="members-grid">
				<view class="member-item" v-for="(member, index) in displayMembers" :key="index">
					<image :src="member.avatar" class="member-avatar"></image>
					<text class="member-name">{{ member.name }}</text>
				</view>
				<!-- 邀请按钮 -->
				<view class="member-item" @click="inviteMember">
					<view class="add-member-btn">
						<text class="add-icon">+</text>
					</view>
					<text class="member-name">邀请</text>
				</view>
			</view>
			<view class="view-all" @click="viewAllMembers">
				<text class="view-all-text">查看全部成员</text>
			</view>
		</view>
		
		<!-- 设置选项 -->
		<view class="settings-section">
			<!-- 群公告 -->
			<view class="setting-item" @click="viewNotice">
				<text class="setting-label">群公告</text>
				<view class="arrow-right">
					<image src="/static/正文/back.png" class="arrow-icon" mode="aspectFit"></image>
				</view>
			</view>
			
			<!-- 活动 -->
			<view class="setting-item" @click="viewActivity">
				<text class="setting-label">活动</text>
				<view class="arrow-right">
					<image src="/static/正文/back.png" class="arrow-icon" mode="aspectFit"></image>
				</view>
			</view>
			
			<!-- 置顶群聊 -->
			<view class="setting-item">
				<text class="setting-label">置顶群聊</text>
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
	name: 'GroupSettings',
	components: {
		TopNavigation
	},
	data() {
		return {
			groupInfo: {
				id: '',
				name: 'lol群',
				groupField: 'lol场',
				description: 'lol爱好者聚集地',
				avatar: '/static/关注页/follow-users-section/Ellipse 9.png',
				notice: '',
				activity: ''
			},
			members: [
				{
					id: 1,
					name: '路人1',
					avatar: '/static/关注页/follow-users-section/Ellipse 11.png'
				},
				{
					id: 2,
					name: '好友1',
					avatar: '/static/关注页/follow-users-section/Ellipse 14.png'
				},
				{
					id: 3,
					name: '名字长...',
					avatar: '/static/关注页/follow-users-section/Ellipse 2.png'
				},
				{
					id: 4,
					name: '四个字的',
					avatar: '/static/关注页/follow-users-section/Ellipse 15.png'
				},
				{
					id: 5,
					name: '好友2',
					avatar: '/static/关注页/follow-users-section/Ellipse 16.png'
				},
				{
					id: 6,
					name: '好友3',
					avatar: '/static/关注页/follow-users-section/Ellipse 13.png'
				},
				{
					id: 7,
					name: '路人1',
					avatar: '/static/关注页/follow-users-section/Ellipse 11.png'
				},
				{
					id: 8,
					name: '好友1',
					avatar: '/static/关注页/follow-users-section/Ellipse 14.png'
				},
				{
					id: 9,
					name: '名字长...',
					avatar: '/static/关注页/follow-users-section/Ellipse 2.png'
				},
				{
					id: 10,
					name: '四个字的',
					avatar: '/static/关注页/follow-users-section/Ellipse 15.png'
				},
				{
					id: 11,
					name: '好友2',
					avatar: '/static/关注页/follow-users-section/Ellipse 16.png'
				}
			],
			settings: {
				pinTop: true,
				muteNotification: false
			}
		}
	},
	computed: {
		displayMembers() {
			// 显示前10个成员（第一行5个，第二行5个+1个邀请按钮）
			return this.members.slice(0, 10)
		}
	},
	onLoad(options) {
		// 接收传递过来的群信息
		if (options.name) {
			this.groupInfo.name = decodeURIComponent(options.name)
		}
		if (options.avatar) {
			this.groupInfo.avatar = decodeURIComponent(options.avatar)
		}
		if (options.id) {
			this.groupInfo.id = options.id
		}
		if (options.notice) {
			this.groupInfo.notice = decodeURIComponent(options.notice)
		}
		if (options.activity) {
			this.groupInfo.activity = decodeURIComponent(options.activity)
		}
		if (options.groupField) {
			this.groupInfo.groupField = decodeURIComponent(options.groupField)
		}
		if (options.description) {
			this.groupInfo.description = decodeURIComponent(options.description)
		}
		
		// 加载设置
		this.loadSettings()
	},
	methods: {
		joinGroup() {
			// 进场功能
			uni.showToast({
				title: '进入群场',
				icon: 'success'
			})
		},
		inviteMember() {
			// 邀请成员
			uni.showToast({
				title: '邀请成员',
				icon: 'none'
			})
		},
		viewAllMembers() {
			// 查看全部成员
			uni.navigateTo({
				url: `/pages/message/group-members?groupId=${this.groupInfo.id}`
			})
		},
		viewNotice() {
			// 查看群公告
			uni.navigateTo({
				url: '/pages/message/group-notice'
			})
		},
		viewActivity() {
			// 查看活动
			uni.navigateTo({
				url: '/pages/message/group-activity'
			})
		},
		togglePinTop(e) {
			this.settings.pinTop = e.detail.value
			this.saveSettings()
			uni.showToast({
				title: this.settings.pinTop ? '已置顶' : '已取消置顶',
				icon: 'success'
			})
		},
		toggleMute(e) {
			this.settings.muteNotification = e.detail.value
			this.saveSettings()
			uni.showToast({
				title: this.settings.muteNotification ? '已开启免打扰' : '已关闭免打扰',
				icon: 'success'
			})
		},
		loadSettings() {
			// 从本地存储加载设置
			const key = `group_settings_${this.groupInfo.id}`
			const savedSettings = uni.getStorageSync(key)
			if (savedSettings) {
				this.settings = savedSettings
			}
		},
		saveSettings() {
			// 保存设置到本地存储
			const key = `group_settings_${this.groupInfo.id}`
			uni.setStorageSync(key, this.settings)
		}
	}
}
</script>

<style scoped>
.group-settings-container {
	min-height: 100vh;
	background: #F6F2FC;
}

/* 群信息卡片 */
.group-card {
	margin-bottom: 20rpx;
	background: #FFFFFF;
	border-radius: 20rpx;
	padding: 30rpx 40rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.group-info {
	display: flex;
	align-items: center;
	flex: 1;
}

.group-avatar {
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	margin-right: 30rpx;
}

.group-details {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.group-name {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	margin-bottom: 10rpx;
}

.group-desc {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	line-height: 38rpx;
	color: #757575;
}

.join-btn {
	width: 120rpx;
	height: 60rpx;
	background: #C1B8E8;
	border-radius: 10rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.join-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	color: #333333;
}

/* 群成员区域 */
.members-section {
	background: #FFFFFF;
	padding: 40rpx 40rpx 30rpx 40rpx;
	margin-bottom: 20rpx;
}

.members-grid {
	display: grid;
	grid-template-columns: repeat(5, 100rpx);
	justify-content: space-between;
	row-gap: 30rpx;
	margin-bottom: 30rpx;
}

.member-item {
	width: 100rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.member-avatar {
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	margin-bottom: 10rpx;
}

.member-name {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #333333;
	text-align: center;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	width: 100%;
}

.add-member-btn {
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	background: #F5F5F5;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 10rpx;
}

.add-icon {
	font-size: 60rpx;
	color: #999999;
	line-height: 60rpx;
}

.view-all {
	text-align: center;
}

.view-all-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	line-height: 38rpx;
	color: #8A70C9;
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

.setting-switch {
	transform: scale(0.8);
}
</style>

