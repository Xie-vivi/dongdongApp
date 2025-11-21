<template>
	<view class="chat-container">
		<!-- 顶部导航栏 -->
		<TopNavigation 
			:title="chatType === 'group' ? groupInfo.name : chatUser.name" 
			:showBack="true" 
			:showMore="true"
			@back="goBack"
			@more="showMore"
		/>
		
		<!-- 群公告（仅群聊显示） -->
		<view class="notice-bar" v-if="chatType === 'group' && groupInfo.notice && showNotice">
			<text class="notice-label">群公告：</text>
			<text class="notice-text">{{ groupInfo.notice }}</text>
			<view class="close-btn" @click="closeNotice">
				<text class="close-icon">×</text>
			</view>
		</view>
		
		<!-- 活动信息（仅群聊显示） -->
		<view class="activity-bar" v-if="chatType === 'group' && groupInfo.activity && showActivity">
			<text class="activity-label">活动：</text>
			<text class="activity-text">{{ groupInfo.activity }}</text>
			<view class="close-btn" @click="closeActivity">
				<text class="close-icon">×</text>
			</view>
		</view>
		
		<!-- 聊天消息列表 -->
		<scroll-view class="message-list" scroll-y :scroll-top="scrollTop" scroll-with-animation>
			<view class="message-wrapper" v-for="(msg, index) in messages" :key="index">
				<!-- 左侧消息（对方发送的） -->
				<view v-if="msg.type === 'received'" class="message-item message-left">
					<image :src="msg.avatar" class="message-avatar" @click="viewUserProfile(msg.senderId)"></image>
					<view class="message-bubble-wrapper">
						<!-- 群聊显示发言人昵称 -->
						<text v-if="chatType === 'group'" class="sender-name">{{ msg.senderName }}</text>
						<view class="message-triangle left-triangle"></view>
						<view class="message-bubble left-bubble">
							<!-- 文本消息 -->
							<text v-if="msg.contentType === 'text'" class="message-text">{{ msg.content }}</text>
							<!-- 图片消息 -->
							<image v-if="msg.contentType === 'image'" :src="msg.content" class="message-image" mode="widthFix"></image>
						</view>
					</view>
				</view>
				
				<!-- 右侧消息（我发送的） -->
				<view v-if="msg.type === 'sent'" class="message-item message-right">
					<view class="message-bubble-wrapper">
						<!-- 群聊显示发言人昵称 -->
						<text v-if="chatType === 'group'" class="sender-name sender-name-right">{{ msg.senderName }}</text>
						<view class="message-triangle right-triangle"></view>
						<view class="message-bubble right-bubble">
							<text class="message-text">{{ msg.content }}</text>
						</view>
					</view>
					<image :src="msg.avatar" class="message-avatar"></image>
				</view>
			</view>
			
			<!-- 时间戳 -->
			<view class="message-time">
				<text class="time-text">星期六 19:20</text>
			</view>
		</scroll-view>
		
		<!-- 底部输入栏 -->
		<view class="bottom-input">
			<view class="input-wrapper">
				<!-- 语音按钮 -->
				<view class="input-icon voice-icon">
					<image src="/static/message/1.png" class="bottom-icon-img"></image>
				</view>
				
				<!-- 输入框 -->
				<view class="input-box">
					<input 
						v-model="inputText" 
						class="text-input" 
						placeholder="加入讨论？" 
						placeholder-style="color: #757575"
						@confirm="sendMessage"
					/>
				</view>
				
				<!-- 表情按钮 -->
				<view class="input-icon emoji-icon">
					<image src="/static/message/2.png" class="bottom-icon-img"></image>
				</view>
				
				<!-- 图片按钮 -->
				<view class="input-icon image-icon" @click="chooseImage">
					<image src="/static/message/3.png" class="bottom-icon-img"></image>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import TopNavigation from '@/components/TopNavigation.vue'

export default {
	name: 'ChatDetail',
	components: {
		TopNavigation
	},
	data() {
		return {
			chatType: 'private', // 'private' 或 'group'
			chatUser: {
				id: '',
				name: '会吃西瓜的小鸭纸',
				avatar: '/static/follow/follow-users-section/Ellipse 2.png'
			},
			groupInfo: {
				id: '',
				name: 'lol群',
				avatar: '/static/follow/follow-users-section/Ellipse 9.png',
				notice: '入群须知: 大家可以随意组队打lol   不要把群...',
				activity: 'lol线下比赛   2025.8.25   17:00-18:00'
			},
			showNotice: true,
			showActivity: true,
			inputText: '',
			scrollTop: 0,
			messages: []
		}
	},
	onLoad(options) {
		// 确定聊天类型
		this.chatType = options.type || 'private'
		
		if (this.chatType === 'group') {
			// 群聊
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
			
			// 加载群聊消息
			this.loadGroupMessages()
		} else {
			// 私聊
			if (options.name) {
				this.chatUser.name = decodeURIComponent(options.name)
			}
			if (options.avatar) {
				this.chatUser.avatar = decodeURIComponent(options.avatar)
			}
			if (options.id) {
				this.chatUser.id = options.id
			}
			
			// 加载私聊消息
			this.loadPrivateMessages()
		}
		
		// 滚动到底部
		this.$nextTick(() => {
			this.scrollToBottom()
		})
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		showMore() {
			if (this.chatType === 'group') {
				// 群聊：跳转到群设置页面
				let url = `/pages/message/group-settings?id=${this.groupInfo.id || ''}&name=${encodeURIComponent(this.groupInfo.name)}&avatar=${encodeURIComponent(this.groupInfo.avatar)}`
				
				if (this.groupInfo.notice) {
					url += `&notice=${encodeURIComponent(this.groupInfo.notice)}`
				}
				if (this.groupInfo.activity) {
					url += `&activity=${encodeURIComponent(this.groupInfo.activity)}`
				}
				// 添加群场和描述信息
				url += `&groupField=${encodeURIComponent('lol场')}`
				url += `&description=${encodeURIComponent('lol爱好者聚集地')}`
				
				uni.navigateTo({
					url: url
				})
			} else {
				// 私聊：跳转到消息设置页面
				uni.navigateTo({
					url: `/pages/message/chat-settings?id=${this.chatUser.id || ''}&name=${encodeURIComponent(this.chatUser.name)}&avatar=${encodeURIComponent(this.chatUser.avatar)}`
				})
			}
		},
		closeNotice() {
			this.showNotice = false
		},
		closeActivity() {
			this.showActivity = false
		},
		viewUserProfile(userId) {
			// 跳转到用户主页
			uni.navigateTo({
				url: `/pages/profile/user-profile?userId=${userId || ''}`
			})
		},
		sendMessage() {
			if (!this.inputText.trim()) {
				return
			}
			
			// 添加新消息
			this.messages.push({
				type: 'sent',
				contentType: 'text',
				avatar: '/static/follow/follow-users-section/Ellipse 13.png',
				content: this.inputText
			})
			
			// 清空输入框
			this.inputText = ''
			
			// 滚动到底部
			this.$nextTick(() => {
				this.scrollToBottom()
			})
		},
		chooseImage() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					const tempFilePath = res.tempFilePaths[0]
					// 添加图片消息
					this.messages.push({
						type: 'sent',
						contentType: 'image',
						avatar: '/static/follow/follow-users-section/Ellipse 13.png',
						content: tempFilePath
					})
					
					// 滚动到底部
					this.$nextTick(() => {
						this.scrollToBottom()
					})
				}
			})
		},
		scrollToBottom() {
			// 滚动到底部
			this.scrollTop = 99999
		},
		loadPrivateMessages() {
			// 加载私聊消息（模拟数据）
			this.messages = [
				{
					type: 'received',
					contentType: 'text',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					content: '我真得跟你说一下你玩塞拉斯的问题了。你为什么总是把他当成纯刺客在玩，塞拉斯真正的强点在于时机感和选择。这英雄不是看谁就冲谁，而是要先观察战局，挑最关键的时刻切进去。'
				},
				{
					type: 'received',
					contentType: 'text',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					content: '对线的时候别太心急。塞拉斯的 Q 是清线和消耗，E 是位移与控制，W 是反打的核心技能：回血、爆发两不误。最常见的问题就是你W太早用了，结果关键时刻没血没反打。塞拉斯的强势在于能苟着拼、拼着反，打的是节奏和耐心。'
				},
				{
					type: 'sent',
					contentType: 'text',
					avatar: '/static/follow/follow-users-section/Ellipse 13.png',
					content: '但我真的学不会怎么办'
				},
				{
					type: 'received',
					contentType: 'text',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					content: '？'
				},
				{
					type: 'received',
					contentType: 'text',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					content: '不是哥们你......算了看看我的五杀'
				},
				{
					type: 'received',
					contentType: 'image',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					content: '/static/follow/Rectangle 109.png'
				}
			]
		},
		loadGroupMessages() {
			// 加载群聊消息（模拟数据）
			this.messages = [
				{
					type: 'received',
					contentType: 'text',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					senderName: '国服塞拉斯',
					content: '我真得跟你说一下你玩塞拉斯的问题了。你为什么总是把他当成纯刺客在玩，塞拉斯真正的强点在于时机感和选择。这英雄不是看谁就冲谁，而是要先观察战局，挑最关键的时刻切进去。'
				},
				{
					type: 'received',
					contentType: 'text',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					senderName: '国服塞拉斯',
					content: '哦发错地方了'
				},
				{
					type: 'sent',
					contentType: 'text',
					avatar: '/static/follow/follow-users-section/Ellipse 13.png',
					senderName: 'abandon',
					content: '嘿嘿。'
				},
				{
					type: 'received',
					contentType: 'text',
					avatar: '/static/follow/follow-users-section/Ellipse 14.png',
					senderName: '剑圣绝活哥',
					content: '？'
				},
				{
					type: 'received',
					contentType: 'text',
					avatar: '/static/follow/follow-users-section/Ellipse 14.png',
					senderName: '剑圣绝活哥',
					content: '你说得对但看看我的五杀'
				},
				{
					type: 'received',
					contentType: 'image',
					avatar: '/static/follow/follow-users-section/Ellipse 14.png',
					senderName: '剑圣绝活哥',
					content: '/static/follow/Rectangle 109.png'
				},
				{
					type: 'received',
					contentType: 'text',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					senderName: '国服塞拉斯',
					content: '你又欠打了？偷我图干嘛'
				}
			]
		}
	}
}
</script>

<style scoped>
.chat-container {
	width: 100%;
	height: 100vh;
	background: #F6F2FC;
	display: flex;
	flex-direction: column;
}

/* 群公告栏 */
.notice-bar {
	background: #FFFFFF;
	padding: 20rpx 40rpx;
	display: flex;
	align-items: center;
	border-bottom: 1rpx solid #E6D9F9;
}

.notice-label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	color: #333333;
	flex-shrink: 0;
}

.notice-text {
	flex: 1;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	color: #333333;
	margin-left: 10rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.close-btn {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
	margin-left: 20rpx;
}

.close-icon {
	font-size: 48rpx;
	color: #8A70C9;
	line-height: 48rpx;
}

/* 活动栏 */
.activity-bar {
	background: #FFFFFF;
	padding: 20rpx 40rpx;
	display: flex;
	align-items: center;
	border-bottom: 1rpx solid #E6D9F9;
}

.activity-label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	color: #333333;
	flex-shrink: 0;
}

.activity-text {
	flex: 1;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	color: #333333;
	margin-left: 10rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

/* 消息列表 */
.message-list {
	flex: 1;
	background: #FFFFFF;
	padding: 20rpx;
	overflow-y: auto;
	box-sizing: border-box;
}

.message-wrapper {
	margin-bottom: 20rpx;
}

.message-item {
	display: flex;
	margin-bottom: 30rpx;
}

.message-left {
	justify-content: flex-start;
}

.message-right {
	justify-content: flex-end;
}

.message-avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	flex-shrink: 0;
}

.message-bubble-wrapper {
	position: relative;
	max-width: 516rpx;
	box-sizing: border-box;
}

.sender-name {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 24rpx;
	line-height: 30rpx;
	color: #757575;
	display: block;
	margin-bottom: 10rpx;
	margin-left: 20rpx;
}

.sender-name-right {
	text-align: right;
	margin-left: 0;
	margin-right: 20rpx;
}

.message-bubble {
	padding: 20rpx;
	border-radius: 20rpx;
	position: relative;
	box-sizing: border-box;
	word-break: break-all;
}

.left-bubble {
	background: #F5F5F5;
	margin-left: 20rpx;
}

.right-bubble {
	background: #EAEEFD;
	margin-right: 20rpx;
}

.message-triangle {
	position: absolute;
	width: 0;
	height: 0;
	border-style: solid;
}

.left-triangle {
	left: 10rpx;
	top: 20rpx;
	border-width: 10rpx 20rpx 10rpx 0;
	border-color: transparent #F5F5F5 transparent transparent;
}

.right-triangle {
	right: 10rpx;
	top: 20rpx;
	border-width: 10rpx 0 10rpx 20rpx;
	border-color: transparent transparent transparent #EAEEFD;
}

.message-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	word-wrap: break-word;
}

.message-image {
	max-width: 352rpx;
	max-height: 220rpx;
	border-radius: 16rpx;
	display: block;
}

.message-time {
	text-align: center;
	margin: 40rpx 0;
}

.time-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #757575;
}

/* 底部输入栏 */
.bottom-input {
	width: 100%;
	height: 160rpx;
	background: #F9F6FF;
	padding: 20rpx;
	box-sizing: border-box;
}

.input-wrapper {
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.input-icon {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

.bottom-icon-img {
	width: 48rpx;
	height: 48rpx;
}

.input-box {
	flex: 1;
	margin: 0 20rpx;
	background: #FFFFFF;
	border-radius: 20rpx;
	padding: 0 30rpx;
	height: 80rpx;
}

.text-input {
	width: 100%;
	height: 100%;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #333333;
}

.emoji-icon {
	margin-right: 15rpx;
}
</style>


