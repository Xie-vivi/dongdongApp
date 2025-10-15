<template>
	<view class="mbti-setup-container">
		<!-- 顶部进度条区域 -->
		<view class="header-section">
			<view class="progress-container">
				<view class="progress-bar">
					<view class="progress-fill progress-step3"></view>
				</view>
			</view>
			
			<!-- 页面标题 -->
			<text class="page-title">设置MBTI</text>
		</view>
		
		<!-- MBTI选择区域 -->
		<view class="mbti-section">
			<!-- 外倾型E人 -->
			<view class="mbti-group">
				<text class="group-title">外倾型E人</text>
				<view class="mbti-options">
					<view 
						v-for="type in eTypes" 
						:key="type.value"
						class="mbti-option"
						:class="{ 'selected': selectedTypes.e === type.value }"
						@tap="selectMbti('e', type.value)"
					>
						<text class="mbti-text">{{ type.label }}</text>
					</view>
				</view>
			</view>
			
			<!-- 内倾型I人 -->
			<view class="mbti-group">
				<text class="group-title">内倾型I人</text>
				<view class="mbti-options">
					<view 
						v-for="type in iTypes" 
						:key="type.value"
						class="mbti-option"
						:class="{ 'selected': selectedTypes.i === type.value }"
						@tap="selectMbti('i', type.value)"
					>
						<text class="mbti-text">{{ type.label }}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 底部按钮区域 -->
		<view class="bottom-buttons">
			<button class="btn-secondary" @tap="goBack">
				上一步
			</button>
			<button 
				class="btn-primary" 
				:class="{ 'active': hasAnySelection }"
				@tap="goNext"
				:disabled="!hasAnySelection"
			>
				下一步
			</button>
		</view>
	</view>
</template>

<script>
export default {
	name: 'MbtiType',
	data() {
		return {
			// 每个组的选中状态
			selectedTypes: {
				e: '',
				i: ''
			},
			// 外倾型E人
			eTypes: [
				{ label: 'ENTJ', value: 'ENTJ' },
				{ label: 'ENTP', value: 'ENTP' },
				{ label: 'ESTJ', value: 'ESTJ' },
				{ label: 'ESFJ', value: 'ESFJ' },
				{ label: 'ENFJ', value: 'ENFJ' },
				{ label: 'ENFP', value: 'ENFP' },
				{ label: 'ESTP', value: 'ESTP' },
				{ label: 'ESFP', value: 'ESFP' }
			],
			// 内倾型I人
			iTypes: [
				{ label: 'INTJ', value: 'INTJ' },
				{ label: 'INTP', value: 'INTP' },
				{ label: 'ISTJ', value: 'ISTJ' },
				{ label: 'ISFJ', value: 'ISFJ' },
				{ label: 'INFJ', value: 'INFJ' },
				{ label: 'INFP', value: 'INFP' },
				{ label: 'ISTP', value: 'ISTP' },
				{ label: 'ISFP', value: 'ISFP' }
			]
		}
	},
	computed: {
		// 是否有任何选择
		hasAnySelection() {
			return this.selectedTypes.e || this.selectedTypes.i;
		}
	},
	methods: {
		// 选择MBTI类型
		selectMbti(group, type) {
			this.selectedTypes[group] = type;
			console.log('选择的MBTI类型:', group, type);
		},
		
		// 返回上一步
		goBack() {
			uni.navigateBack();
		},
		
		// 进入下一步
		goNext() {
			if (!this.hasAnySelection) {
				uni.showToast({
					title: '请至少选择一个MBTI类型',
					icon: 'none'
				});
				return;
			}
			
			// 保存MBTI信息到本地存储
			uni.setStorageSync('userMbtiEITypes', this.selectedTypes);
			console.log('MBTI EI类型已保存:', this.selectedTypes);
			
			// 跳转到首页
			uni.reLaunch({
				url: '/pages/index/index'
			});
		}
	},
	
	// 页面加载时获取已保存的MBTI信息
	onLoad() {
		const savedMbtiTypes = uni.getStorageSync('userMbtiEITypes');
		if (savedMbtiTypes) {
			this.selectedTypes = savedMbtiTypes;
		}
	}
}
</script>

<style scoped>
/* 容器样式 */
.mbti-setup-container {
	min-height: 100vh;
	background: #FFFFFF;
	padding-bottom: 140rpx; /* 为底部按钮留出空间 */
}

/* 顶部区域样式 */
.header-section {
	padding: 40rpx 40rpx 60rpx 40rpx;
	background: #FFFFFF;
}

/* 进度条容器 */
.progress-container {
	margin-bottom: 40rpx;
}

/* 进度条背景 */
.progress-bar {
	width: 100%;
	height: 8rpx; /* 4px * 2 */
	background: #F0F0F0;
	border-radius: 4rpx; /* 2px * 2 */
	overflow: hidden;
}

/* 进度条填充 - 第三步 */
.progress-fill.progress-step3 {
	width: 100%; /* 3步中的第3步 */
	height: 100%;
	background: linear-gradient(90deg, #8A70C9 0%, #A688D4 100%);
	border-radius: 4rpx; /* 2px * 2 */
	transition: width 0.3s ease;
}

/* 页面标题 */
.page-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 500;
	font-size: 44rpx; /* 22px * 2 */
	line-height: 44rpx; /* 22px * 2 */
	color: #8A70C9;
	text-align: center;
}

/* MBTI选择区域 */
.mbti-section {
	padding: 0 40rpx;
}

/* MBTI分组 */
.mbti-group {
	margin-bottom: 60rpx;
}

/* 分组标题 */
.group-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 500;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 44rpx; /* 22px * 2 */
	color: #333333;
	margin-bottom: 30rpx;
	display: block;
}

/* MBTI选项容器 */
.mbti-options {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
	justify-content: flex-start;
}

/* MBTI选项 */
.mbti-option {
	width: calc(25% - 15rpx);
	height: 80rpx; /* 40px * 2 */
	background: #FFFFFF;
	border: 2rpx solid #E0E0E0;
	border-radius: 20rpx; /* 10px * 2 */
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
	cursor: pointer;
	box-sizing: border-box;
}

/* 选中状态 */
.mbti-option.selected {
	background: #8A70C9;
	border-color: #8A70C9;
}

/* MBTI文字 */
.mbti-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 28rpx; /* 14px * 2 */
	line-height: 38rpx; /* 19px * 2 */
	color: #333333;
	transition: color 0.3s ease;
}

/* 选中状态文字 */
.mbti-option.selected .mbti-text {
	color: #FFFFFF;
	font-weight: 500;
}

/* 底部按钮区域 */
.bottom-buttons {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	width: 100%;
	height: 120rpx;
	padding: 20rpx 60rpx;
	display: flex;
	justify-content: center;
	align-items: center;
	gap: 30rpx;
	background: #FFFFFF;
	border-top: 1rpx solid #F0F0F0;
	z-index: 100;
	box-sizing: border-box;
}

/* 次要按钮 */
.btn-secondary {
	flex: 1;
	max-width: 280rpx;
	height: 80rpx; /* 40px * 2 */
	background: #FFFFFF;
	border: 2rpx solid #C1B8E8;
	border-radius: 20rpx; /* 10px * 2 */
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 28rpx; /* 14px * 2 */
	line-height: 38rpx; /* 19px * 2 */
	color: #757575;
	display: flex;
	align-items: center;
	justify-content: center;
}

.btn-secondary:active {
	transform: scale(0.98);
	background: #F5F5F5;
}

/* 主要按钮 */
.btn-primary {
	flex: 1;
	max-width: 280rpx;
	height: 80rpx; /* 40px * 2 */
	background: #EAEEFD;
	border-radius: 20rpx; /* 10px * 2 */
	border: none;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 28rpx; /* 14px * 2 */
	line-height: 38rpx; /* 19px * 2 */
	color: #757575;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
}

.btn-primary.active {
	background: #8A70C9;
	color: #FFFFFF;
}

.btn-primary:active {
	transform: scale(0.98);
}

.btn-primary:disabled {
	background: #F5F5F5;
	color: #CCCCCC;
	cursor: not-allowed;
}

/* 响应式适配 */
@media screen and (max-width: 750rpx) {
	.mbti-options {
		gap: 15rpx;
	}
	
	.mbti-option {
		width: calc(25% - 12rpx);
	}
}

@media screen and (max-width: 600rpx) {
	.mbti-options {
		gap: 10rpx;
	}
	
	.mbti-option {
		width: calc(25% - 8rpx);
		height: 70rpx;
	}
	
	.mbti-text {
		font-size: 24rpx;
	}
}
</style>
