<template>
	<view class="container">
		<!-- 顶部导航 -->
		<TopNavigation 
			title="编辑MBTI" 
			:showBack="true" 
			:showMore="false"
			titleColor="#8A70C9"
			@back="goBack"
		/>

		<!-- 内容区域 -->
		<view class="content">
			<!-- 展示MBTI开关 -->
			<view class="switch-row">
				<text class="switch-label">展示MBTI</text>
				<switch :checked="showMBTI" @change="onSwitchChange" color="#8A70C9" />
			</view>
			
			<!-- MBTI类型显示 -->
			<view class="mbti-display" v-if="selectedMBTI">
				<text class="mbti-text">{{ selectedMBTI }}</text>
			</view>
			
			<!-- MBTI选择按钮 -->
			<view class="mbti-buttons">
				<!-- 第一行 -->
				<view class="button-row">
					<view 
						class="mbti-button" 
						:class="{ selected: selections[0] === 'E' }"
						@tap="selectLetter(0, 'E')"
					>
						<text class="button-text">E</text>
					</view>
					<view 
						class="mbti-button" 
						:class="{ selected: selections[1] === 'S' }"
						@tap="selectLetter(1, 'S')"
					>
						<text class="button-text">S</text>
					</view>
					<view 
						class="mbti-button" 
						:class="{ selected: selections[2] === 'T' }"
						@tap="selectLetter(2, 'T')"
					>
						<text class="button-text">T</text>
					</view>
					<view 
						class="mbti-button" 
						:class="{ selected: selections[3] === 'J' }"
						@tap="selectLetter(3, 'J')"
					>
						<text class="button-text">J</text>
					</view>
				</view>
				
				<!-- 第二行 -->
				<view class="button-row">
					<view 
						class="mbti-button" 
						:class="{ selected: selections[0] === 'I' }"
						@tap="selectLetter(0, 'I')"
					>
						<text class="button-text">I</text>
					</view>
					<view 
						class="mbti-button" 
						:class="{ selected: selections[1] === 'N' }"
						@tap="selectLetter(1, 'N')"
					>
						<text class="button-text">N</text>
					</view>
					<view 
						class="mbti-button" 
						:class="{ selected: selections[2] === 'F' }"
						@tap="selectLetter(2, 'F')"
					>
						<text class="button-text">F</text>
					</view>
					<view 
						class="mbti-button" 
						:class="{ selected: selections[3] === 'P' }"
						@tap="selectLetter(3, 'P')"
					>
						<text class="button-text">P</text>
					</view>
				</view>
			</view>
			
			<!-- MBTI人物插图 -->
			<view class="mbti-character" v-if="selectedMBTI">
				<image class="character-image" :src="characterImage" mode="aspectFit" />
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<view 
				class="submit-button" 
				:class="{ active: selectedMBTI }"
				@tap="confirmSubmit"
			>
				<text class="submit-text">提交修改</text>
			</view>
		</view>

		<!-- 确认弹窗 -->
		<view class="confirm-modal" v-if="showConfirm" @tap="closeConfirm">
			<view class="modal-content" @tap.stop>
				<text class="modal-title">确定修改为该MBTI吗？</text>
				<view class="modal-buttons">
					<view class="modal-button cancel" @tap="closeConfirm">
						<text class="button-text">取消</text>
					</view>
					<view class="modal-button confirm" @tap="submitMBTI">
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
			selections: ['', '', '', ''], // 四个位置的选择
			showMBTI: true,
			showConfirm: false
		}
	},
	computed: {
		selectedMBTI() {
			if (this.selections.every(s => s !== '')) {
				return this.selections.join('')
			}
			return ''
		},
		characterImage() {
			// 根据MBTI类型返回对应的人物图片
			// 这里使用一个默认图片，实际应该根据不同MBTI类型返回不同图片
			return '/static/mbti/character.png'
		}
	},
	onLoad(options) {
		if (options.mbti) {
			// 解析已有的MBTI类型
			const mbti = options.mbti
			if (mbti.length === 4) {
				this.selections = mbti.split('')
			}
		}
		if (options.showMBTI !== undefined) {
			this.showMBTI = options.showMBTI === 'true'
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		
		selectLetter(index, letter) {
			this.selections[index] = letter
			// 触发响应式更新
			this.selections = [...this.selections]
		},
		
		onSwitchChange(e) {
			this.showMBTI = e.detail.value
		},
		
		confirmSubmit() {
			if (!this.selectedMBTI) {
				uni.showToast({
					title: '请选择完整的MBTI类型',
					icon: 'none'
				})
				return
			}
			this.showConfirm = true
		},
		
		closeConfirm() {
			this.showConfirm = false
		},
		
		submitMBTI() {
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

.switch-row {
	display: flex;
	justify-content: flex-end;
	align-items: center;
	gap: 16rpx;
	margin-bottom: 40rpx;
}

.switch-label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 40rpx;
	color: #666666;
}

.mbti-display {
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 40rpx;
	padding: 40rpx 0;
}

.mbti-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 80rpx;
	line-height: 100rpx;
	color: #333333;
	font-weight: 700;
}

.mbti-buttons {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
	margin-bottom: 40rpx;
}

.button-row {
	display: flex;
	gap: 20rpx;
}

.mbti-button {
	flex: 1;
	height: 88rpx;
	background: #FFFFFF;
	border: 2rpx solid #E0E0E0;
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.mbti-button.selected {
	background: #B8A5E3;
	border-color: #8A70C9;
}

.button-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	font-weight: 500;
}

.mbti-character {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 40rpx 0;
}

.character-image {
	width: 400rpx;
	height: 400rpx;
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
