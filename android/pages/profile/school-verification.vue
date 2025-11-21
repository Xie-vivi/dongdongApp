<template>
	<view class="container">
		<!-- 顶部标题和进度 -->
		<view class="header">
			<text class="title">校园认证（{{ verificationStatus }}）</text>
			<view class="progress-bar">
				<view class="progress-fill" :style="{ width: progressWidth }"></view>
			</view>
			<text class="progress-text">可稍后认证</text>
		</view>

		<!-- 内容区域 -->
		<scroll-view scroll-y class="content">
			<!-- 方法一：自动认证 -->
			<view class="method-section">
				<text class="method-title">方法一：自动认证</text>
				
				<view class="auto-verify-box">
					<!-- 未连接状态 -->
					<view v-if="autoVerifyStatus === 'not_connected'" class="status-row">
						<text class="status-text">未连接校园网</text>
						<image class="status-icon error" src="/static/verification/error.png" mode="aspectFit" />
					</view>
					
					<!-- 验证中状态 -->
					<view v-else-if="autoVerifyStatus === 'verifying'" class="status-row">
						<text class="status-text">正在验证，请稍等</text>
						<image class="status-icon loading" src="/static/verification/loading.png" mode="aspectFit" />
					</view>
					
					<!-- 已连接状态 -->
					<view v-else-if="autoVerifyStatus === 'connected'" class="status-row">
						<text class="status-text">已连接校园网</text>
						<image class="status-icon success" src="/static/verification/success.png" mode="aspectFit" />
					</view>
					
					<!-- 已连接后显示学校信息 -->
					<view v-if="autoVerifyStatus === 'connected' && selectedSchool" class="school-info">
						<text class="school-name">已连接至：{{ selectedSchool }}</text>
						<view class="reverify-button" @tap="reverify">
							<text class="reverify-text">重新验证</text>
						</view>
					</view>
				</view>
				
				<view class="verify-button-wrapper">
					<view 
						class="verify-button" 
						:class="{ active: autoVerifyStatus === 'not_connected', verifying: autoVerifyStatus === 'verifying' }"
						@tap="startAutoVerify"
					>
						<text class="verify-button-text">{{ autoVerifyButtonText }}</text>
					</view>
				</view>
			</view>

			<!-- 分割线 -->
			<view class="divider"></view>

			<!-- 方法二：手动认证 -->
			<view class="method-section">
				<text class="method-title">方法二：手动认证</text>
				
				<view class="upload-box" @tap="chooseImage">
					<image v-if="!uploadedImage" class="upload-icon" src="/static/verification/upload.png" mode="aspectFit" />
					<image v-else class="uploaded-image" :src="uploadedImage" mode="aspectFill" />
					<text v-if="!uploadedImage" class="upload-text">点击上传校园卡照片</text>
				</view>
				
				<view class="verify-button-wrapper">
					<view 
						class="verify-button" 
						:class="{ active: uploadedImage }"
						@tap="submitManualVerify"
					>
						<text class="verify-button-text">验证</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部按钮 -->
		<view class="footer-buttons">
			<view class="footer-button outline" @tap="goBack">
				<text class="footer-button-text">上一步</text>
			</view>
			<view class="footer-button primary" @tap="nextStep">
				<text class="footer-button-text white">下一步</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			verificationStatus: '未认证',
			progressWidth: '60%',
			autoVerifyStatus: 'not_connected', // not_connected, verifying, connected
			uploadedImage: '',
			selectedSchool: ''
		}
	},
	computed: {
		autoVerifyButtonText() {
			if (this.autoVerifyStatus === 'verifying') {
				return '验证中'
			} else if (this.autoVerifyStatus === 'connected') {
				return '已验证'
			}
			return '验证'
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		
		startAutoVerify() {
			if (this.autoVerifyStatus === 'verifying' || this.autoVerifyStatus === 'connected') {
				return
			}
			
			// 开始验证
			this.autoVerifyStatus = 'verifying'
			
			// 模拟验证过程
			setTimeout(() => {
				// 这里应该调用实际的校园网验证API
				this.autoVerifyStatus = 'connected'
				this.selectedSchool = '中央民族大学'
				this.verificationStatus = '已认证'
				
				uni.showToast({
					title: '认证成功',
					icon: 'success'
				})
			}, 2000)
		},
		
		reverify() {
			this.autoVerifyStatus = 'not_connected'
			this.selectedSchool = ''
			this.verificationStatus = '未认证'
		},
		
		chooseImage() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.uploadedImage = res.tempFilePaths[0]
				}
			})
		},
		
		submitManualVerify() {
			if (!this.uploadedImage) {
				uni.showToast({
					title: '请先上传校园卡照片',
					icon: 'none'
				})
				return
			}
			
			uni.showLoading({
				title: '验证中...'
			})
			
			// 模拟上传和验证
			setTimeout(() => {
				uni.hideLoading()
				uni.showToast({
					title: '提交成功，等待审核',
					icon: 'success'
				})
			}, 1500)
		},
		
		nextStep() {
			// 跳转到下一步或返回
			uni.navigateBack()
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
	width: 100%;
	max-width: 100%;
	overflow-x: hidden;
}

.header {
	padding: 60rpx 40rpx 40rpx;
	background: #F6F2FC;
}

.title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 36rpx;
	line-height: 48rpx;
	color: #8A70C9;
	font-weight: 500;
	text-align: center;
	display: block;
	margin-bottom: 30rpx;
}

.progress-bar {
	width: 100%;
	height: 12rpx;
	background: #E0E0E0;
	border-radius: 6rpx;
	overflow: hidden;
	margin-bottom: 20rpx;
}

.progress-fill {
	height: 100%;
	background: linear-gradient(90deg, #8A70C9 0%, #B8A5E3 100%);
	border-radius: 6rpx;
	transition: width 0.3s;
}

.progress-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #999999;
	text-align: right;
	display: block;
}

.content {
	flex: 1;
	padding: 0 40rpx;
	box-sizing: border-box;
	width: 100%;
	max-width: 100%;
}

.method-section {
	margin-bottom: 60rpx;
}

.method-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	font-weight: 500;
	display: block;
	margin-bottom: 30rpx;
}

.auto-verify-box {
	background: #F0F0F0;
	border-radius: 16rpx;
	padding: 40rpx;
	margin-bottom: 30rpx;
}

.status-row {
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.status-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 40rpx;
	color: #333333;
}

.status-icon {
	width: 48rpx;
	height: 48rpx;
}

.status-icon.loading {
	animation: rotate 1s linear infinite;
}

@keyframes rotate {
	from {
		transform: rotate(0deg);
	}
	to {
		transform: rotate(360deg);
	}
}

.school-info {
	margin-top: 20rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.school-name {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #666666;
}

.reverify-button {
	padding: 8rpx 20rpx;
	background: #8A70C9;
	border-radius: 16rpx;
}

.reverify-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #FFFFFF;
}

.verify-button-wrapper {
	display: flex;
	justify-content: flex-end;
	width: 100%;
}

.verify-button {
	padding: 16rpx 30rpx;
	background: #D8D8D8;
	border-radius: 20rpx;
	min-width: 100rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.verify-button.active {
	background: #8A70C9;
}

.verify-button.verifying {
	background: #B8A5E3;
}

.verify-button-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 40rpx;
	color: #FFFFFF;
	text-align: center;
}

.divider {
	height: 2rpx;
	background: #E0E0E0;
	margin: 40rpx 0;
}

.upload-box {
	width: 100%;
	height: 400rpx;
	border: 4rpx dashed #CCCCCC;
	border-radius: 16rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	margin-bottom: 30rpx;
	position: relative;
	overflow: hidden;
	box-sizing: border-box;
	max-width: 100%;
}

.upload-icon {
	width: 80rpx;
	height: 80rpx;
	margin-bottom: 20rpx;
}

.upload-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 40rpx;
	color: #999999;
}

.uploaded-image {
	width: 100%;
	height: 100%;
}

.footer-buttons {
	display: flex;
	gap: 20rpx;
	padding: 40rpx;
	background: #F6F2FC;
}

.footer-button {
	flex: 1;
	height: 88rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.footer-button.outline {
	background: #FFFFFF;
	border: 2rpx solid #8A70C9;
}

.footer-button.primary {
	background: #8A70C9;
}

.footer-button-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #8A70C9;
	font-weight: 500;
}

.footer-button-text.white {
	color: #FFFFFF;
}
</style>
