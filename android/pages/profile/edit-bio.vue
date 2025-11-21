<template>
	<view class="container">
		<!-- 顶部导航 -->
		<TopNavigation 
			title="编辑简介" 
			:showBack="true" 
			:showMore="false"
			titleColor="#8A70C9"
			@back="goBack"
		/>

		<!-- 内容区域 -->
		<view class="content">
			<!-- 文本域 -->
			<textarea 
				class="textarea-field" 
				placeholder="请输入简介（0-100个字符）" 
				v-model="bio"
				maxlength="100"
				:auto-height="true"
			></textarea>
			
			<!-- 提示和字数统计 -->
			<view class="hint-row">
				<text class="hint-text">一周仅可修改一次简介</text>
				<text class="char-count">{{ bio.length }}/100</text>
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<view 
				class="submit-button" 
				:class="{ active: bio.length > 0 && bio.length <= 100 }"
				@tap="confirmSubmit"
			>
				<text class="submit-text">提交修改</text>
			</view>
		</view>

		<!-- 确认弹窗 -->
		<view class="confirm-modal" v-if="showConfirm" @tap="closeConfirm">
			<view class="modal-content" @tap.stop>
				<text class="modal-title">确定修改为该简介吗？</text>
				<view class="modal-buttons">
					<view class="modal-button cancel" @tap="closeConfirm">
						<text class="button-text">取消</text>
					</view>
					<view class="modal-button confirm" @tap="submitBio">
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
			bio: '',
			showConfirm: false
		}
	},
	onLoad(options) {
		// 从上一页传入的当前简介
		if (options.bio) {
			this.bio = decodeURIComponent(options.bio)
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		
		confirmSubmit() {
			if (this.bio.length === 0 || this.bio.length > 100) {
				uni.showToast({
					title: '简介长度为0-100个字符',
					icon: 'none'
				})
				return
			}
			this.showConfirm = true
		},
		
		closeConfirm() {
			this.showConfirm = false
		},
		
		submitBio() {
			this.showConfirm = false
			// 这里应该调用API保存简介
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

.textarea-field {
	width: 100%;
	min-height: 300rpx;
	background: #F0F0F0;
	border-radius: 16rpx;
	padding: 24rpx;
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
