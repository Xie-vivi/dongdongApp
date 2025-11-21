<template>
	<view class="container">
		<!-- 顶部导航 -->
		<TopNavigation 
			title="修改用户名" 
			:showBack="true" 
			:showMore="false"
			titleColor="#8A70C9"
			@back="goBack"
		/>

		<!-- 内容区域 -->
		<view class="content">
			<!-- 用户名标签 -->
			<text class="label">用户名</text>
			
			<!-- 输入框 -->
			<input 
				class="input-field" 
				type="text" 
				placeholder="请输入用户名（6-24个字符）" 
				v-model="username"
				maxlength="24"
			/>
			
			<!-- 提示和字数统计 -->
			<view class="hint-row">
				<text class="hint-text">一个月仅可修改一次用户名</text>
				<text class="char-count">{{ username.length }}/24</text>
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<view 
				class="submit-button" 
				:class="{ active: username.length >= 6 && username.length <= 24 }"
				@tap="confirmSubmit"
			>
				<text class="submit-text">提交修改</text>
			</view>
		</view>

		<!-- 确认弹窗 -->
		<view class="confirm-modal" v-if="showConfirm" @tap="closeConfirm">
			<view class="modal-content" @tap.stop>
				<text class="modal-title">确定修改为该用户名吗？</text>
				<view class="modal-buttons">
					<view class="modal-button cancel" @tap="closeConfirm">
						<text class="button-text">取消</text>
					</view>
					<view class="modal-button confirm" @tap="submitUsername">
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
			username: '',
			showConfirm: false
		}
	},
	onLoad(options) {
		// 从上一页传入的当前用户名
		if (options.username) {
			this.username = options.username
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		
		confirmSubmit() {
			if (this.username.length < 6 || this.username.length > 24) {
				uni.showToast({
					title: '用户名长度为6-24个字符',
					icon: 'none'
				})
				return
			}
			this.showConfirm = true
		},
		
		closeConfirm() {
			this.showConfirm = false
		},
		
		submitUsername() {
			this.showConfirm = false
			// 这里应该调用API保存用户名
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

.label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	font-weight: 500;
	display: block;
	margin-bottom: 20rpx;
}

.input-field {
	width: 100%;
	height: 88rpx;
	background: #F0F0F0;
	border-radius: 16rpx;
	padding: 0 24rpx;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 40rpx;
	color: #333333;
	box-sizing: border-box;
	border: none;
}

.hint-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 20rpx;
}

.hint-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #999999;
}

.char-count {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #999999;
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
