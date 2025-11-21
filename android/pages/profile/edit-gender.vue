<template>
	<view class="container">
		<!-- 顶部导航 -->
		<TopNavigation 
			title="编辑性别" 
			:showBack="true" 
			:showMore="false"
			titleColor="#8A70C9"
			@back="goBack"
		/>

		<!-- 内容区域 -->
		<view class="content">
			<!-- 性别标签和开关 -->
			<view class="header-row">
				<text class="label">性别</text>
				<view class="switch-row">
					<text class="switch-label">展示性别</text>
					<switch :checked="showGender" @change="onSwitchChange" color="#8A70C9" />
				</view>
			</view>
			
			<!-- 性别选项 -->
			<view class="gender-options">
				<view 
					class="gender-option" 
					:class="{ selected: selectedGender === '男' }"
					@tap="selectGender('男')"
				>
					<text class="gender-text">男</text>
				</view>
				<view 
					class="gender-option" 
					:class="{ selected: selectedGender === '女' }"
					@tap="selectGender('女')"
				>
					<text class="gender-text">女</text>
				</view>
				<view 
					class="gender-option" 
					:class="{ selected: selectedGender === '其他' }"
					@tap="selectGender('其他')"
				>
					<text class="gender-text">其他</text>
				</view>
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<view 
				class="submit-button" 
				:class="{ active: selectedGender }"
				@tap="confirmSubmit"
			>
				<text class="submit-text">提交修改</text>
			</view>
		</view>

		<!-- 确认弹窗 -->
		<view class="confirm-modal" v-if="showConfirm" @tap="closeConfirm">
			<view class="modal-content" @tap.stop>
				<text class="modal-title">确定修改为该性别吗？</text>
				<view class="modal-buttons">
					<view class="modal-button cancel" @tap="closeConfirm">
						<text class="button-text">取消</text>
					</view>
					<view class="modal-button confirm" @tap="submitGender">
						<text class="button-text confirm-text">确定</text>
					</view>
				</view>
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
			selectedGender: '',
			showGender: true,
			showConfirm: false
		}
	},
	onLoad(options) {
		if (options.gender) {
			this.selectedGender = options.gender
		}
		if (options.showGender !== undefined) {
			this.showGender = options.showGender === 'true'
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		
		selectGender(gender) {
			this.selectedGender = gender
		},
		
		onSwitchChange(e) {
			this.showGender = e.detail.value
		},
		
		confirmSubmit() {
			if (!this.selectedGender) {
				uni.showToast({
					title: '请选择性别',
					icon: 'none'
				})
				return
			}
			this.showConfirm = true
		},
		
		closeConfirm() {
			this.showConfirm = false
		},
		
		submitGender() {
			this.showConfirm = false
			uni.showToast({
				title: '修改成功',
				icon: 'success'
			})
			setTimeout(() => {
				uni.navigateBack()
			}, 1500)
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
	padding: 40rpx;
}

.header-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 40rpx;
}

.label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	font-weight: 500;
}

.switch-row {
	display: flex;
	align-items: center;
	gap: 16rpx;
}

.switch-label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 40rpx;
	color: #666666;
}

.gender-options {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.gender-option {
	width: 100%;
	height: 88rpx;
	background: #F0F0F0;
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.gender-option.selected {
	background: #EDE7F9;
	border: 2rpx solid #8A70C9;
}

.gender-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 40rpx;
	color: #333333;
}

/* 提交按钮 */
.submit-section {
	padding: 40rpx;
}

.submit-button {
	width: 100%;
	height: 88rpx;
	background: #D8D8D8;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.submit-button.active {
	background: #8A70C9;
}

.submit-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #FFFFFF;
	font-weight: 500;
}

/* 确认弹窗 */
.confirm-modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 9999;
}

.modal-content {
	width: 540rpx;
	background: #FFFFFF;
	border-radius: 32rpx;
	overflow: hidden;
}

.modal-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	text-align: center;
	padding: 60rpx 40rpx;
}

.modal-buttons {
	display: flex;
	border-top: 2rpx solid #F0F0F0;
}

.modal-button {
	flex: 1;
	height: 100rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.modal-button.cancel {
	border-right: 2rpx solid #F0F0F0;
}

.button-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
}

.confirm-text {
	color: #FF6B6B;
}
</style>
