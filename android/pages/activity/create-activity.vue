<template>
	<view class="create-activity-container">
		<!-- 顶部导航栏 -->
		<TopNavigation 
			title="发布活动" 
			titleColor="#8A70C9"
			:showBack="true" 
			:showMore="false"
		/>
		
		<!-- 活动内容区域 -->
		<scroll-view class="content-area" scroll-y>
			<!-- 活动标题输入 -->
			<view class="input-section">
				<textarea 
					v-model="activityTitle" 
					class="title-input" 
					placeholder="请输入活动标题..."
					placeholder-style="color: #999999"
					maxlength="100"
					:auto-height="true"
				></textarea>
			</view>
			
			<!-- 分割线 -->
			<view class="divider"></view>
			
			<!-- 选择活动时间 -->
			<view class="select-item" @click="selectTime">
				<image src="/static/create-activity/1.png" class="item-icon time-icon"></image>
				<text class="item-text">{{ activityTime || '选择活动时间' }}</text>
				<image src="/static/content/back.png" class="arrow-icon" mode="aspectFit"></image>
			</view>
			
			<!-- 分割线 -->
			<view class="divider"></view>
			
			<!-- 选择活动地点 -->
			<view class="select-item" @click="selectLocation">
				<image src="/static/create-activity/2.png" class="item-icon location-icon"></image>
				<text class="item-text">{{ activityLocation || '选择活动地点' }}</text>
				<image src="/static/content/back.png" class="arrow-icon" mode="aspectFit"></image>
			</view>
			
			<!-- 分割线 -->
			<view class="divider"></view>
			
			<!-- 上传活动封面 -->
			<view class="cover-section">
				<view v-if="!coverImage" class="upload-cover" @click="uploadCover">
					<text class="upload-icon">+</text>
					<text class="upload-text">请上传活动封面</text>
				</view>
				<view v-else class="cover-preview">
					<image :src="coverImage" class="cover-image" mode="aspectFill"></image>
					<view class="delete-cover" @click="deleteCover">
						<text class="delete-icon">×</text>
					</view>
				</view>
			</view>
			
			<!-- 分割线 -->
			<view class="divider"></view>
			
			<!-- 活动详情输入 -->
			<view class="input-section">
				<textarea 
					v-model="activityContent" 
					class="content-input" 
					placeholder="请输入活动详情内容..."
					placeholder-style="color: #999999"
					maxlength="5000"
					:auto-height="true"
				></textarea>
				<!-- 已添加的话题和@用户 -->
				<view class="tags-section" v-if="selectedTopics.length > 0 || mentionedUsers.length > 0">
					<text v-for="(topic, index) in selectedTopics" :key="'topic-' + index" class="tag-item">#{{ topic }} </text>
					<text v-for="(user, index) in mentionedUsers" :key="'user-' + index" class="tag-item">@{{ user.name }} </text>
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
			
			<!-- 分割线 -->
			<view class="divider"></view>
			
			<!-- 选择的场 -->
			<view class="select-item" @click="showFieldSelector">
				<image src="/static/create-activity/5.png" class="item-icon field-icon"></image>
				<text class="item-text" :class="{ 'selected': selectedField }">{{ selectedField ? selectedField.name : '选择的场' }}</text>
				<image src="/static/content/back.png" class="arrow-icon" mode="aspectFit"></image>
			</view>
			
			<!-- 分割线 -->
			<view class="divider"></view>
			
			<!-- 设置活动人数上限 -->
			<view class="select-item" @click="setMaxPeople">
				<image src="/static/create-activity/3.png" class="item-icon people-icon"></image>
				<text class="item-text">{{ maxPeople ? `上限：${maxPeople}人` : '设置活动人数上限' }}</text>
				<image src="/static/content/back.png" class="arrow-icon" mode="aspectFit"></image>
			</view>
			
			<!-- 分割线 -->
			<view class="divider"></view>
			
			<!-- 设置报名模式 -->
			<view class="select-item" @click="setSignupMode">
				<image src="/static/create-activity/4.png" class="item-icon signup-icon"></image>
				<text class="item-text">{{ signupMode || '设置报名模式' }}</text>
				<image src="/static/content/back.png" class="arrow-icon" mode="aspectFit"></image>
			</view>
		</scroll-view>
		
		<!-- 底部发布按钮 -->
		<view class="bottom-actions">
			<view class="publish-btn" :class="{ 'active': canPublish }" @click="publishActivity">
				<text class="publish-text">发布活动</text>
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
		
		<!-- 选择活动时间弹窗 -->
		<view v-if="showTimePanel" class="time-panel">
			<view class="panel-mask" @click="hideTimePanel"></view>
			<view class="time-panel-content">
				<view class="time-panel-header">
					<view class="back-btn" @click="hideTimePanel">
						<image src="/static/content/back.png" class="back-icon" mode="aspectFit"></image>
					</view>
					<text class="panel-title">选择活动时间</text>
				</view>
				<view class="time-section">
					<view class="time-row">
						<text class="time-label">开始时间</text>
						<picker mode="date" :value="startDate" @change="onStartDateChange">
							<view class="time-picker">
								<text class="time-value">{{ startDate ? startDate.split('-')[1] + '月' + startDate.split('-')[2] + '日' : '10月17日' }}</text>
							</view>
						</picker>
						<picker mode="time" :value="startTime" @change="onStartTimeChange">
							<view class="time-picker">
								<text class="time-value">{{ startTime || '11:00' }}</text>
							</view>
						</picker>
					</view>
					<view class="time-row">
						<text class="time-label">结束时间</text>
						<picker mode="date" :value="endDate" @change="onEndDateChange">
							<view class="time-picker">
								<text class="time-value">{{ endDate ? endDate.split('-')[1] + '月' + endDate.split('-')[2] + '日' : '10月17日' }}</text>
							</view>
						</picker>
						<picker mode="time" :value="endTime" @change="onEndTimeChange">
							<view class="time-picker">
								<text class="time-value">{{ endTime || '12:00' }}</text>
							</view>
						</picker>
					</view>
				</view>
				<view class="time-save-btn" @click="saveTime">
					<text class="save-btn-text">保存</text>
				</view>
			</view>
		</view>
		
		<!-- 选择地点弹窗 -->
		<view v-if="showLocationPanel" class="location-panel">
			<view class="panel-mask" @click="hideLocationPanel"></view>
			<view class="location-panel-content">
				<view class="location-panel-header">
					<view class="back-btn" @click="hideLocationPanel">
						<image src="/static/content/back.png" class="back-icon" mode="aspectFit"></image>
					</view>
					<text class="panel-title">选择地点</text>
				</view>
				<view class="location-input-section">
					<view class="location-search-box">
						<input 
							v-model="locationName" 
							class="location-input" 
							placeholder="输入地点" 
							placeholder-style="color: #999999"
						/>
						<view class="location-search-btn" @click="searchLocation">
							<text class="search-btn-text">搜索</text>
						</view>
					</view>
					<textarea 
						v-model="locationDetail" 
						class="location-detail-input" 
						placeholder="输入详细地址..."
						placeholder-style="color: #999999"
						maxlength="200"
					></textarea>
				</view>
				<view class="location-save-btn" @click="saveLocation">
					<text class="save-btn-text">保存</text>
				</view>
			</view>
		</view>
		
		<!-- 设置活动人数上限弹窗 -->
		<view v-if="showPeoplePanel" class="people-panel">
			<view class="panel-mask" @click="hidePeoplePanel"></view>
			<view class="people-panel-content">
				<view class="people-panel-header">
					<view class="back-btn" @click="hidePeoplePanel">
						<image src="/static/content/back.png" class="back-icon" mode="aspectFit"></image>
					</view>
					<text class="panel-title">设置活动人数上限</text>
				</view>
				<scroll-view class="people-list" scroll-y>
					<view 
						class="people-item" 
						v-for="num in [8, 10, 12, 15, 20, 25, 30, 40, 50, 100]" 
						:key="num"
						@click="selectPeopleCount(num)"
					>
						<text class="people-text" :class="{ 'selected': maxPeople == num }">{{ num }}人</text>
					</view>
				</scroll-view>
				<view class="people-save-btn" @click="hidePeoplePanel">
					<text class="save-btn-text">保存</text>
				</view>
			</view>
		</view>
		
		<!-- 设置报名模式弹窗 -->
		<view v-if="showSignupModePanel" class="signup-mode-panel">
			<view class="panel-mask" @click="hideSignupModePanel"></view>
			<view class="signup-mode-content">
				<view class="signup-mode-header">
					<view class="back-btn" @click="hideSignupModePanel">
						<image src="/static/content/back.png" class="back-icon" mode="aspectFit"></image>
					</view>
					<text class="panel-title">设置报名模式</text>
				</view>
				<view class="signup-mode-list">
					<view 
						class="signup-mode-item" 
						@click="selectSignupMode('无需报名，可直接参加')"
						:class="{ 'selected': signupMode === '无需报名，可直接参加' }"
					>
						<view class="checkbox" :class="{ 'checked': signupMode === '无需报名，可直接参加' }">
							<text v-if="signupMode === '无需报名，可直接参加'" class="check-icon">✓</text>
						</view>
						<text class="mode-text">无需报名，可直接参加</text>
					</view>
					<view 
						class="signup-mode-item" 
						@click="selectSignupMode('需报名后参加')"
						:class="{ 'selected': signupMode === '需报名后参加' }"
					>
						<view class="checkbox" :class="{ 'checked': signupMode === '需报名后参加' }">
							<text v-if="signupMode === '需报名后参加'" class="check-icon">✓</text>
						</view>
						<text class="mode-text">需报名后参加</text>
					</view>
					<view 
						class="signup-mode-item" 
						@click="selectSignupMode('报名与否皆可参加')"
						:class="{ 'selected': signupMode === '报名与否皆可参加' }"
					>
						<view class="checkbox" :class="{ 'checked': signupMode === '报名与否皆可参加' }">
							<text v-if="signupMode === '报名与否皆可参加'" class="check-icon">✓</text>
						</view>
						<text class="mode-text">报名与否皆可参加</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 选择场弹窗 -->
		<view v-if="showFieldPanel" class="field-panel">
			<view class="panel-content">
				<view class="panel-header">
					<view class="back-btn" @click="hideFieldSelector">
						<image src="/static/content/back.png" class="back-icon" mode="aspectFit"></image>
					</view>
					<text class="panel-title">选择的场</text>
				</view>
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
	name: 'CreateActivity',
	components: {
		TopNavigation
	},
	data() {
		return {
			activityTitle: '',
			activityTime: '',
			activityLocation: '',
			coverImage: '',
			activityContent: '',
			images: [],
			selectedField: null,
			maxPeople: '',
			signupMode: '',
			showFieldPanel: false,
			showTimePanel: false,
			showLocationPanel: false,
			showPeoplePanel: false,
			showSignupModePanel: false,
			searchKeyword: '',
			startDate: '',
			startTime: '',
			endDate: '',
			endTime: '',
			locationName: '',
			locationDetail: '',
			selectingStartDate: true,
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
			return this.activityTitle.trim() && 
				   this.activityTime && 
				   this.activityLocation && 
				   this.coverImage && 
				   this.activityContent.trim() && 
				   this.selectedField
		}
	},
	methods: {
		selectTime() {
			this.showTimePanel = true
		},
		hideTimePanel() {
			this.showTimePanel = false
		},
		onStartDateChange(e) {
			this.startDate = e.detail.value
			this.endDate = e.detail.value
		},
		onStartTimeChange(e) {
			this.startTime = e.detail.value
		},
		onEndDateChange(e) {
			this.endDate = e.detail.value
		},
		onEndTimeChange(e) {
			this.endTime = e.detail.value
		},
		saveTime() {
			if (this.startDate && this.startTime && this.endDate && this.endTime) {
				const startDateObj = new Date(this.startDate)
				const weekDays = ['日', '一', '二', '三', '四', '五', '六']
				const weekDay = weekDays[startDateObj.getDay()]
				const startMonth = this.startDate.split('-')[1]
				const startDay = this.startDate.split('-')[2]
				this.activityTime = `${startMonth}月${startDay}日 周${weekDay} ${this.startTime}-${this.endTime}`
				this.hideTimePanel()
			} else {
				uni.showToast({
					title: '请完整选择时间',
					icon: 'none'
				})
			}
		},
		selectLocation() {
			this.showLocationPanel = true
		},
		hideLocationPanel() {
			this.showLocationPanel = false
		},
		saveLocation() {
			if (this.locationName.trim()) {
				this.activityLocation = this.locationName.trim()
				if (this.locationDetail.trim()) {
					this.activityLocation += ' - ' + this.locationDetail.trim()
				}
				this.hideLocationPanel()
			} else {
				uni.showToast({
					title: '请输入地点',
					icon: 'none'
				})
			}
		},
		searchLocation() {
			uni.showToast({
				title: '搜索地点：' + this.locationName,
				icon: 'none'
			})
		},
		uploadCover() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.coverImage = res.tempFilePaths[0]
				}
			})
		},
		deleteCover() {
			this.coverImage = ''
		},
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
		setMaxPeople() {
			this.showPeoplePanel = true
		},
		hidePeoplePanel() {
			this.showPeoplePanel = false
		},
		selectPeopleCount(count) {
			this.maxPeople = count
			this.hidePeoplePanel()
		},
		setSignupMode() {
			this.showSignupModePanel = true
		},
		hideSignupModePanel() {
			this.showSignupModePanel = false
		},
		selectSignupMode(mode) {
			this.signupMode = mode
			this.hideSignupModePanel()
		},
		publishActivity() {
			if (!this.canPublish) {
				uni.showToast({
					title: '请完善活动信息',
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
.create-activity-container {
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
	min-height: 200rpx;
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

.divider {
	height: 1rpx;
	background: #E6D9F9;
	margin: 0 40rpx;
}

.select-item {
	display: flex;
	align-items: center;
	padding: 30rpx 40rpx;
	min-height: 100rpx;
}

.item-icon {
	width: 48rpx;
	height: 48rpx;
	margin-right: 20rpx;
	flex-shrink: 0;
}

.item-text {
	flex: 1;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	color: #999999;
}

.item-text.selected {
	color: #8A70C9;
}

.arrow-icon {
	width: 48rpx;
	height: 48rpx;
	transform: rotate(180deg);
	flex-shrink: 0;
}

.cover-section {
	padding: 40rpx;
}

.upload-cover {
	width: 100%;
	height: 400rpx;
	border: 2rpx dashed #E6D9F9;
	border-radius: 20rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	background: #F9F6FF;
}

.upload-icon {
	font-size: 100rpx;
	color: #999999;
	line-height: 100rpx;
	margin-bottom: 20rpx;
}

.upload-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #999999;
}

.cover-preview {
	position: relative;
	width: 100%;
	height: 400rpx;
	border-radius: 20rpx;
	overflow: hidden;
}

.cover-image {
	width: 100%;
	height: 100%;
}

.delete-cover {
	position: absolute;
	top: 20rpx;
	right: 20rpx;
	width: 60rpx;
	height: 60rpx;
	background: rgba(0, 0, 0, 0.6);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
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

/* 选择活动时间弹窗 */
.time-panel,
.location-panel,
.people-panel,
.signup-mode-panel {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 2000;
}

.time-panel-content,
.location-panel-content,
.people-panel-content,
.signup-mode-content {
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	background: #FFFFFF;
	border-radius: 30rpx 30rpx 0 0;
	z-index: 2001;
	max-height: 90vh;
}

.time-panel-header,
.location-panel-header,
.people-panel-header,
.signup-mode-header {
	height: 176rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
	padding-top: 88rpx;
	border-bottom: 1rpx solid #E6D9F9;
}

.time-section {
	padding: 40rpx;
}

.time-row {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 40rpx;
}

.time-label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	color: #333333;
	width: 150rpx;
}

.time-picker {
	flex: 1;
	height: 80rpx;
	background: #FFFFFF;
	border: 2rpx solid #E6D9F9;
	border-radius: 10rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 0 10rpx;
}

.time-value {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #333333;
}

.time-save-btn,
.location-save-btn,
.people-save-btn {
	margin: 40rpx;
	height: 100rpx;
	background: #8A70C9;
	border-radius: 50rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.save-btn-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	color: #FFFFFF;
}

/* 选择地点弹窗 */
.location-input-section {
	padding: 40rpx;
}

.location-search-box {
	display: flex;
	align-items: center;
	margin-bottom: 30rpx;
}

.location-input {
	flex: 1;
	height: 80rpx;
	background: #FFFFFF;
	border: 2rpx solid #E6D9F9;
	border-radius: 10rpx;
	padding: 0 30rpx;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #333333;
	margin-right: 20rpx;
}

.location-search-btn {
	width: 120rpx;
	height: 80rpx;
	background: #8A70C9;
	border-radius: 10rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.location-detail-input {
	width: 100%;
	min-height: 200rpx;
	background: #FFFFFF;
	border: 2rpx solid #E6D9F9;
	border-radius: 10rpx;
	padding: 20rpx;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #333333;
}

/* 设置人数上限弹窗 */
.people-list {
	max-height: 500rpx;
	padding: 20rpx 0;
}

.people-item {
	height: 100rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	border-bottom: 1rpx solid #F5F5F5;
}

.people-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	color: #757575;
}

.people-text.selected {
	color: #333333;
	font-weight: 500;
}

/* 设置报名模式弹窗 */
.signup-mode-list {
	padding: 40rpx;
}

.signup-mode-item {
	display: flex;
	align-items: center;
	padding: 30rpx 0;
}

.checkbox {
	width: 44rpx;
	height: 44rpx;
	border: 2rpx solid #E6D9F9;
	border-radius: 50%;
	margin-right: 20rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.checkbox.checked {
	background: #8A70C9;
	border-color: #8A70C9;
}

.check-icon {
	font-size: 28rpx;
	color: #FFFFFF;
	line-height: 28rpx;
}

.mode-text {
	flex: 1;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	color: #333333;
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

