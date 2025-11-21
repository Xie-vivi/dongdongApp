<template>
	<view class="create-post-container">
		<!-- 顶部导航栏 -->
		<TopNavigation 
			title="发帖" 
			titleColor="#8A70C9"
			:showBack="true" 
			:showMore="false"
		/>
		
		<!-- 帖子内容区域 -->
		<scroll-view class="content-area" scroll-y>
			<!-- 标题输入 -->
			<view class="input-section">
				<textarea 
					v-model="postTitle" 
					class="title-input" 
					placeholder="请输入帖子标题..."
					placeholder-style="color: #999999"
					maxlength="100"
					:auto-height="true"
				></textarea>
			</view>
			
			<!-- 分割线 -->
			<view class="divider"></view>
			
			<!-- 内容输入 -->
			<view class="input-section">
				<textarea 
					v-model="postContent" 
					class="content-input" 
					placeholder="请输入帖子内容..."
					placeholder-style="color: #999999"
					maxlength="5000"
					:auto-height="true"
				></textarea>
				<!-- 已添加的话题和@用户 -->
				<view class="tags-section" v-if="selectedTopics.length > 0 || mentionedUsers.length > 0">
					<text v-for="(topic, index) in selectedTopics" :key="'topic-' + index" class="tag-item topic-tag">#{{ topic }} </text>
					<text v-for="(user, index) in mentionedUsers" :key="'user-' + index" class="tag-item mention-tag">@{{ user.name }} </text>
				</view>
			</view>
			
			<!-- 话题和@用户 -->
			<view class="action-buttons">
				<view class="action-btn" @click="addTopic">
					<text class="action-text"># 话题</text>
				</view>
				<view class="action-btn" @click="mentionUser">
					<text class="action-text">@ 用户</text>
				</view>
			</view>
			
			<!-- 图片选择区域 -->
			<view class="image-section">
				<view class="image-item" v-for="(image, index) in images" :key="index">
					<image :src="image" class="preview-image" mode="aspectFill"></image>
					<view class="delete-btn" @click="deleteImage(index)">
						<text class="delete-icon">×</text>
					</view>
				</view>
				<view v-if="images.length < 9" class="add-image" @click="chooseImage">
					<text class="add-icon">+</text>
				</view>
			</view>
			
			<!-- 选择场 -->
			<view class="select-field" @click="showFieldSelector">
				<image src="/static/follow/daohang/2.png" class="field-icon"></image>
				<text class="field-text">{{ selectedField ? selectedField.name : '选择的场' }}</text>
				<image src="/static/content/back.png" class="arrow-icon" mode="aspectFit"></image>
			</view>
		</scroll-view>
		
		<!-- 底部发布按钮 -->
		<view class="bottom-actions">
			<view class="publish-btn" :class="{ 'active': canPublish }" @click="publishPost">
				<text class="publish-text">发布帖子</text>
			</view>
		</view>
		
		<!-- @用户弹窗 -->
		<view v-if="showUserPanel" class="user-panel">
			<view class="panel-mask" @click="hideUserPanel"></view>
			<view class="panel-box">
				<view class="panel-box-header">
					<text class="panel-box-title">选择用户</text>
					<text class="panel-close" @click="hideUserPanel">×</text>
				</view>
				<scroll-view class="user-list" scroll-y>
					<view class="user-item" v-for="user in userList" :key="user.id" @click="selectUser(user)">
						<image :src="user.avatar" class="user-avatar"></image>
						<text class="user-name">{{ user.name }}</text>
					</view>
				</scroll-view>
			</view>
		</view>
		
		<!-- #话题弹窗 -->
		<view v-if="showTopicPanel" class="topic-panel">
			<view class="panel-mask" @click="hideTopicPanel"></view>
			<view class="panel-box">
				<view class="panel-box-header">
					<text class="panel-box-title">添加话题</text>
					<text class="panel-close" @click="hideTopicPanel">×</text>
				</view>
				<!-- 输入自定义话题 -->
				<view class="topic-input-section">
					<input 
						v-model="customTopic" 
						class="topic-input" 
						placeholder="输入话题" 
						placeholder-style="color: #999999"
						@confirm="addCustomTopic"
					/>
					<view class="add-topic-btn" @click="addCustomTopic">
						<text class="add-topic-text">添加</text>
					</view>
				</view>
				<!-- 热门话题 -->
				<view class="hot-topics-section">
					<text class="section-title">热门话题</text>
					<scroll-view class="topic-list" scroll-y>
						<view class="topic-item" v-for="(topic, index) in hotTopics" :key="index" @click="selectTopic(topic)">
							<text class="topic-name">#{{ topic }}</text>
						</view>
					</scroll-view>
				</view>
			</view>
		</view>
		
		<!-- 选择场弹窗 -->
		<view v-if="showFieldPanel" class="field-panel">
			<view class="panel-content">
				<!-- 顶部导航 -->
				<view class="panel-header">
					<view class="back-btn" @click="hideFieldSelector">
						<image src="/static/content/back.png" class="back-icon" mode="aspectFit"></image>
					</view>
					<text class="panel-title">选择的场</text>
				</view>
				
				<!-- 搜索框 -->
				<view class="search-section">
					<view class="search-box">
						<input 
							v-model="searchKeyword" 
							class="search-input" 
							placeholder="输入场" 
							placeholder-style="color: #999999"
						/>
					</view>
					<view class="search-btn" @click="searchField">
						<text class="search-btn-text">搜索</text>
					</view>
				</view>
				
				<!-- 我关注的场 -->
				<scroll-view class="fields-list" scroll-y>
					<text class="list-title">我关注的场</text>
					<view class="field-item" v-for="field in myFields" :key="field.id" @click="selectField(field)">
						<image :src="field.avatar" class="field-avatar"></image>
						<view class="field-info">
							<text class="field-name">{{ field.name }}</text>
							<view class="field-stats">
								<text class="stat-text">{{ field.followers }}关注</text>
								<text class="stat-text">{{ field.posts }}帖子</text>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
		</view>
	</view>
</template>

<script>
import TopNavigation from '@/components/TopNavigation.vue'

export default {
	name: 'CreatePost',
	components: {
		TopNavigation
	},
	data() {
		return {
			postTitle: '',
			postContent: '',
			images: [],
			selectedField: null,
			showFieldPanel: false,
			searchKeyword: '',
			myFields: [
				{
					id: 1,
					name: 'lol场',
					avatar: '/static/follow/follow-users-section/Ellipse 11.png',
					followers: 261,
					posts: 54
				},
				{
					id: 2,
					name: '羽毛球场',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png',
					followers: 23,
					posts: 11
				}
			],
			showUserPanel: false,
			showTopicPanel: false,
			mentionedUsers: [],
			selectedTopics: [],
			customTopic: '',
			userList: [
				{
					id: 1,
					name: '会吃西瓜的小鸭纸',
					avatar: '/static/follow/follow-users-section/Ellipse 2.png'
				},
				{
					id: 2,
					name: '不知名用户',
					avatar: '/static/follow/follow-users-section/Ellipse 11.png'
				},
				{
					id: 3,
					name: '你叫什么名字',
					avatar: '/static/follow/follow-users-section/Ellipse 14.png'
				},
				{
					id: 4,
					name: '剑圣绝活哥',
					avatar: '/static/follow/follow-users-section/Ellipse 15.png'
				}
			],
			hotTopics: [
				'吐槽',
				'2026新生',
				'校园生活',
				'美食推荐',
				'学习打卡',
				'运动健身'
			]
		}
	},
	computed: {
		canPublish() {
			return this.postTitle.trim() && this.postContent.trim() && this.selectedField
		}
	},
	methods: {
		addTopic() {
			this.showTopicPanel = true
		},
		hideTopicPanel() {
			this.showTopicPanel = false
			this.customTopic = ''
		},
		selectTopic(topic) {
			if (!this.selectedTopics.includes(topic)) {
				this.selectedTopics.push(topic)
			}
			this.hideTopicPanel()
		},
		addCustomTopic() {
			if (this.customTopic.trim() && !this.selectedTopics.includes(this.customTopic.trim())) {
				this.selectedTopics.push(this.customTopic.trim())
				this.hideTopicPanel()
			}
		},
		mentionUser() {
			this.showUserPanel = true
		},
		hideUserPanel() {
			this.showUserPanel = false
		},
		selectUser(user) {
			// 避免重复添加同一用户
			const exists = this.mentionedUsers.find(u => u.id === user.id)
			if (!exists) {
				this.mentionedUsers.push(user)
			}
			this.hideUserPanel()
		},
		chooseImage() {
			uni.chooseImage({
				count: 9 - this.images.length,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.images = this.images.concat(res.tempFilePaths)
				}
			})
		},
		deleteImage(index) {
			this.images.splice(index, 1)
		},
		showFieldSelector() {
			this.showFieldPanel = true
		},
		hideFieldSelector() {
			this.showFieldPanel = false
		},
		searchField() {
			uni.showToast({
				title: '搜索场：' + this.searchKeyword,
				icon: 'none'
			})
		},
		selectField(field) {
			this.selectedField = field
			this.hideFieldSelector()
		},
		publishPost() {
			if (!this.canPublish) {
				uni.showToast({
					title: '请完善帖子内容',
					icon: 'none'
				})
				return
			}
			
			uni.showToast({
				title: '发布成功',
				icon: 'success',
				success: () => {
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				}
			})
		}
	}
}
</script>

<style scoped>
.create-post-container {
	min-height: 100vh;
	background: #F6F2FC;
	display: flex;
	flex-direction: column;
}

.content-area {
	flex: 1;
	background: #FFFFFF;
	padding-bottom: 200rpx;
}

.input-section {
	padding: 40rpx;
}

.title-input,
.content-input {
	width: 100%;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 50rpx;
	color: #333333;
	border: none;
	outline: none;
}

.title-input {
	min-height: 60rpx;
}

.content-input {
	min-height: 300rpx;
}

.tags-section {
	margin-top: 20rpx;
	padding-top: 20rpx;
	border-top: 1rpx solid #E6D9F9;
}

.tag-item {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 44rpx;
	color: #8A70C9;
	margin-right: 10rpx;
}

.topic-tag,
.mention-tag {
	color: #8A70C9;
}

.divider {
	height: 1rpx;
	background: #E6D9F9;
	margin: 0 40rpx;
}

.action-buttons {
	display: flex;
	gap: 20rpx;
	padding: 40rpx;
}

.action-btn {
	padding: 16rpx 32rpx;
	background: #FFFFFF;
	border: 1rpx solid #E6D9F9;
	border-radius: 40rpx;
}

.action-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #757575;
}

.image-section {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
	padding: 0 40rpx 40rpx;
}

.image-item {
	position: relative;
	width: 200rpx;
	height: 200rpx;
}

.preview-image {
	width: 100%;
	height: 100%;
	border-radius: 16rpx;
}

.delete-btn {
	position: absolute;
	top: -10rpx;
	right: -10rpx;
	width: 48rpx;
	height: 48rpx;
	background: rgba(0, 0, 0, 0.6);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.delete-icon {
	font-size: 40rpx;
	color: #FFFFFF;
	line-height: 40rpx;
}

.add-image {
	width: 200rpx;
	height: 200rpx;
	background: #F5F5F5;
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.add-icon {
	font-size: 80rpx;
	color: #999999;
	line-height: 80rpx;
}

.select-field {
	display: flex;
	align-items: center;
	padding: 30rpx 40rpx;
	border-top: 1rpx solid #E6D9F9;
}

.field-icon {
	width: 48rpx;
	height: 48rpx;
	margin-right: 20rpx;
}

.field-text {
	flex: 1;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	color: #8A70C9;
}

.arrow-icon {
	width: 48rpx;
	height: 48rpx;
	transform: rotate(180deg);
}

.bottom-actions {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	padding: 30rpx 40rpx;
	background: #FFFFFF;
	box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.publish-btn {
	width: 100%;
	height: 100rpx;
	background: #E0E0E0;
	border-radius: 50rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.publish-btn.active {
	background: #8A70C9;
}

.publish-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	color: #FFFFFF;
}

/* @用户弹窗 */
.user-panel,
.topic-panel {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 2000;
	display: flex;
	align-items: center;
	justify-content: center;
}

.panel-mask {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
}

.panel-box {
	position: relative;
	width: 600rpx;
	max-height: 800rpx;
	background: #FFFFFF;
	border-radius: 20rpx;
	overflow: hidden;
	z-index: 2001;
}

.panel-box-header {
	height: 100rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	border-bottom: 1rpx solid #E6D9F9;
	position: relative;
}

.panel-box-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 500;
	font-size: 32rpx;
	color: #333333;
}

.panel-close {
	position: absolute;
	right: 30rpx;
	font-size: 60rpx;
	color: #999999;
	line-height: 60rpx;
}

/* 用户列表 */
.user-list {
	max-height: 600rpx;
	padding: 20rpx 0;
}

.user-item {
	display: flex;
	align-items: center;
	padding: 20rpx 40rpx;
}

.user-item:active {
	background: #F5F5F5;
}

.user-avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	margin-right: 24rpx;
}

.user-name {
	flex: 1;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	color: #333333;
}

/* 话题弹窗 */
.topic-input-section {
	display: flex;
	align-items: center;
	padding: 30rpx 40rpx;
	border-bottom: 1rpx solid #E6D9F9;
}

.topic-input {
	flex: 1;
	height: 70rpx;
	background: #F5F5F5;
	border-radius: 35rpx;
	padding: 0 30rpx;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #333333;
	margin-right: 20rpx;
}

.add-topic-btn {
	width: 100rpx;
	height: 70rpx;
	background: #8A70C9;
	border-radius: 35rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.add-topic-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #FFFFFF;
}

.hot-topics-section {
	padding: 30rpx 40rpx;
}

.section-title {
	display: block;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 500;
	font-size: 28rpx;
	color: #757575;
	margin-bottom: 20rpx;
}

.topic-list {
	max-height: 400rpx;
}

.topic-item {
	padding: 20rpx 0;
	border-bottom: 1rpx solid #F5F5F5;
}

.topic-item:active {
	background: #F5F5F5;
}

.topic-name {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	color: #8A70C9;
}

/* 选择场弹窗 */
.field-panel {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: #F6F2FC;
	z-index: 1000;
}

.panel-content {
	height: 100%;
	display: flex;
	flex-direction: column;
}

.panel-header {
	height: 176rpx;
	background: #F6F2FC;
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
	padding-top: 88rpx;
}

.back-btn {
	position: absolute;
	left: 40rpx;
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.back-icon {
	width: 48rpx;
	height: 48rpx;
}

.panel-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 500;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
}

.search-section {
	display: flex;
	align-items: center;
	padding: 30rpx 40rpx;
	background: #F6F2FC;
}

.search-box {
	flex: 1;
	height: 80rpx;
	background: #FFFFFF;
	border-radius: 40rpx;
	padding: 0 30rpx;
	margin-right: 20rpx;
	border: 2rpx solid #8A70C9;
}

.search-input {
	width: 100%;
	height: 100%;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #333333;
}

.search-btn {
	width: 120rpx;
	height: 80rpx;
	background: #8A70C9;
	border-radius: 40rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.search-btn-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #FFFFFF;
}

.fields-list {
	flex: 1;
	background: #FFFFFF;
	padding: 40rpx;
}

.list-title {
	display: block;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 500;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	margin-bottom: 30rpx;
}

.field-item {
	display: flex;
	align-items: center;
	padding: 30rpx 0;
	border-bottom: 1rpx solid #F5F5F5;
}

.field-avatar {
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	margin-right: 30rpx;
}

.field-info {
	flex: 1;
}

.field-name {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 500;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	display: block;
	margin-bottom: 10rpx;
}

.field-stats {
	display: flex;
	gap: 30rpx;
}

.stat-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #999999;
}
</style>

