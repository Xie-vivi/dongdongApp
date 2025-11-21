<template>
	<view class="container">
		<!-- 顶部导航 -->
		<TopNavigation 
			title="编辑资料" 
			:showBack="true" 
			:showMore="false"
			titleColor="#8A70C9"
			@back="goBack"
		/>

		<!-- 内容区域 -->
		<scroll-view scroll-y class="content">
			<!-- 头像和认证区域 -->
			<view class="avatar-section">
				<view class="avatar-container" @tap="selectAvatar">
					<image class="avatar-image" :src="userInfo.avatar" mode="aspectFill" />
					<view class="edit-icon">
						<image class="edit-icon-image" src="/static/edit-profile/edit.png" mode="aspectFit" />
					</view>
				</view>
				
				<!-- 认证状态 -->
				<view class="certification-status" :class="{ certified: userInfo.isCertified }">
					<text class="certification-text">{{ userInfo.isCertified ? '已通过校园认证' : '未通过校园认证' }}</text>
				</view>
			</view>

			<!-- 列表项区域 -->
			<view class="list-section">
				<!-- 用户名 -->
				<view class="list-item" @tap="editUsername">
					<text class="list-label">用户名</text>
					<view class="list-value-container">
						<text class="list-value">{{ userInfo.username }}</text>
						<image class="arrow-icon" src="/static/content/back.png" mode="aspectFit" />
					</view>
				</view>

				<!-- 简介 -->
				<view class="list-item" @tap="editBio">
					<view class="list-label-section">
						<text class="list-label">简介</text>
						<text class="list-desc" v-if="!userInfo.bio">{{ userInfo.bioPlaceholder }}</text>
						<text class="list-desc" v-else>{{ userInfo.bio }}</text>
					</view>
					<image class="arrow-icon" src="/static/content/back.png" mode="aspectFit" />
				</view>

				<!-- 背景图 -->
				<view class="list-item" @tap="selectBackground">
					<text class="list-label">背景图</text>
					<view class="list-value-container">
						<image class="background-preview" :src="userInfo.background" mode="aspectFill" />
						<image class="arrow-icon" src="/static/content/back.png" mode="aspectFit" />
					</view>
				</view>

				<!-- 性别 -->
				<view class="list-item" @tap="selectGender">
					<text class="list-label">性别</text>
					<view class="list-value-container">
						<text class="list-value">{{ userInfo.gender || '选择性别' }}</text>
						<image class="arrow-icon" src="/static/content/back.png" mode="aspectFit" />
					</view>
				</view>

				<!-- 生日 -->
				<view class="list-item" @tap="selectBirthday">
					<text class="list-label">生日</text>
					<view class="list-value-container">
						<text class="list-value">{{ userInfo.birthday || '选择生日' }}</text>
						<image class="arrow-icon" src="/static/content/back.png" mode="aspectFit" />
					</view>
				</view>

				<!-- 学校 -->
				<view class="list-item" @tap="selectSchool">
					<text class="list-label">学校 {{ userInfo.schoolCertified ? '(已认证)' : '(未认证)' }}</text>
					<view class="list-value-container">
						<text class="list-value" :class="{ 'uncertified-text': !userInfo.schoolCertified }">
							{{ userInfo.school || '选择学校' }}
						</text>
						<image class="arrow-icon" src="/static/content/back.png" mode="aspectFit" />
					</view>
				</view>

				<!-- MBTI -->
				<view class="list-item" @tap="selectMBTI">
					<text class="list-label">MBTI</text>
					<view class="list-value-container">
						<text class="list-value">{{ userInfo.mbti || '选择MBTI' }}</text>
						<image class="arrow-icon" src="/static/content/back.png" mode="aspectFit" />
					</view>
				</view>
			</view>

		</scroll-view>

		<!-- 背景图预览弹窗 -->
		<view class="background-preview-modal" v-if="showBackgroundPreview" @tap="closeBackgroundPreview">
			<!-- 关闭按钮 -->
			<view class="close-btn" @tap.stop="closeBackgroundPreview">
				<text class="close-icon">×</text>
			</view>
			
			<!-- 背景图片 -->
			<image class="preview-image" :src="userInfo.background" mode="aspectFit" @tap.stop />
			
			<!-- 修改背景图按钮 -->
			<view class="preview-edit-button" @tap.stop="editBackground">
				<text class="preview-edit-text">修改背景图</text>
			</view>
		</view>
	</view>
</template>

<script>
import TopNavigation from '@/components/TopNavigation.vue'

export default {
	components: {
		TopNavigation
	},
	data() {
		return {
			userInfo: {
				avatar: '/static/follow/follow-users-section/Ellipse 11.png',
				username: '会吃西瓜的小鸭纸',
				bio: '',
				bioPlaceholder: '这个人什么简介都没有',
				background: '/static/profile/Rectangle 279.png',
				gender: '',
				birthday: '',
				school: '',
				schoolCertified: false,
				mbti: '',
				isCertified: false
			},
			showBackgroundPreview: false
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		
		selectAvatar() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.userInfo.avatar = res.tempFilePaths[0]
				}
			})
		},
		
		editUsername() {
			uni.navigateTo({
				url: `/pages/profile/edit-username?username=${this.userInfo.username}`
			})
		},
		
		editBio() {
			const bio = this.userInfo.bio || ''
			uni.navigateTo({
				url: `/pages/profile/edit-bio?bio=${encodeURIComponent(bio)}`
			})
		},
		
		selectBackground() {
			// 点击背景图列表项，显示预览
			this.showBackgroundPreview = true
		},
		
		closeBackgroundPreview() {
			this.showBackgroundPreview = false
		},
		
		editBackground() {
			// 关闭预览
			this.showBackgroundPreview = false
			// 选择新的背景图
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album'],
				success: (res) => {
					this.userInfo.background = res.tempFilePaths[0]
				}
			})
		},
		
		selectGender() {
			uni.navigateTo({
				url: `/pages/profile/edit-gender?gender=${this.userInfo.gender || ''}`
			})
		},
		
		selectBirthday() {
			uni.navigateTo({
				url: `/pages/profile/edit-birthday?birthday=${encodeURIComponent(this.userInfo.birthday || '')}`
			})
		},
		
		selectSchool() {
			uni.navigateTo({
				url: '/pages/profile/school-verification'
			})
		},
		
		selectMBTI() {
			uni.navigateTo({
				url: `/pages/profile/edit-mbti?mbti=${this.userInfo.mbti || ''}`
			})
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: #F6F2FC;
	display: flex;
	flex-direction: column;
}

.content {
	flex: 1;
	padding: 0;
}

/* 头像和认证区域 */
.avatar-section {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 60rpx 0 40rpx;
	background: #F6F2FC;
}

.avatar-container {
	position: relative;
	width: 160rpx;
	height: 160rpx;
	margin-bottom: 20rpx;
}

.avatar-image {
	width: 100%;
	height: 100%;
	border-radius: 50%;
}

.edit-icon {
	position: absolute;
	bottom: 0;
	right: 0;
	width: 56rpx;
	height: 56rpx;
	background: #8A70C9;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	border: 4rpx solid #FFFFFF;
}

.edit-icon-image {
	width: 28rpx;
	height: 28rpx;
}

/* 认证状态 */
.certification-status {
	border: 2rpx solid #8A70C9;
	border-radius: 10rpx;
	padding: 12rpx 24rpx;
	background: #FFFFFF;
}

.certification-status.certified {
	background: #EDE7F9;
	border-color: #8A70C9;
}

.certification-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #333333;
}

/* 列表项区域 */
.list-section {
	background: #FFFFFF;
	margin: 0 20rpx;
	border-radius: 20rpx;
	overflow: hidden;
}

.list-item {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	padding: 32rpx 24rpx;
	border-bottom: 2rpx solid #F0F0F0;
}

.list-item:last-child {
	border-bottom: none;
}

.list-label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	font-weight: 400;
}

.list-label-section {
	display: flex;
	flex-direction: column;
	gap: 12rpx;
}

.list-desc {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #999999;
	max-width: 300rpx;
	word-break: break-all;
}

.list-value-container {
	display: flex;
	align-items: center;
	gap: 16rpx;
}

.list-value {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 40rpx;
	color: #333333;
	text-align: right;
}

.list-value.uncertified-text {
	color: #FF6B6B;
}

.arrow-icon {
	width: 24rpx;
	height: 24rpx;
	flex-shrink: 0;
	transform: rotate(180deg);
}

/* 背景图预览 */
.background-preview {
	width: 80rpx;
	height: 80rpx;
	border-radius: 12rpx;
}

/* 背景图预览弹窗 */
.background-preview-modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: #000000;
	z-index: 9999;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.close-btn {
	position: absolute;
	top: 88rpx;
	left: 40rpx;
	width: 64rpx;
	height: 64rpx;
	z-index: 10000;
	display: flex;
	align-items: center;
	justify-content: center;
}

.close-icon {
	font-size: 64rpx;
	color: #FFFFFF;
	line-height: 64rpx;
}

.preview-image {
	width: 100%;
	height: 100%;
}

.preview-edit-button {
	position: absolute;
	bottom: 80rpx;
	left: 50%;
	transform: translateX(-50%);
	width: 400rpx;
	height: 88rpx;
	background: #8A70C9;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.preview-edit-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #FFFFFF;
	font-weight: 500;
}
</style>
