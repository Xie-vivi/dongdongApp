<template>
	<view class="mbti-setup-container">
		<!-- 顶部进度条区域 -->
		<view class="header-section">
			<view class="progress-container">
				<view class="progress-bar">
					<view class="progress-fill progress-step2"></view>
				</view>
			</view>
			
			<!-- 页面标题 -->
			<text class="page-title">设置MBTI</text>
		</view>
		
		<!-- MBTI选择区域 -->
		<view class="mbti-section">
			<!-- NT型紫人 -->
			<view class="mbti-group">
				<text class="group-title">NT型紫人</text>
				<view class="mbti-options">
					<view 
						v-for="type in ntTypes" 
						:key="type.value"
						class="mbti-option"
						:class="{ 'selected': selectedTypes.nt === type.value }"
						@tap="selectMbti('nt', type.value)"
					>
						<text class="mbti-text">{{ type.label }}</text>
					</view>
				</view>
			</view>
			
			<!-- NF型绿人 -->
			<view class="mbti-group">
				<text class="group-title">NF型绿人</text>
				<view class="mbti-options">
					<view 
						v-for="type in nfTypes" 
						:key="type.value"
						class="mbti-option"
						:class="{ 'selected': selectedTypes.nf === type.value }"
						@tap="selectMbti('nf', type.value)"
					>
						<text class="mbti-text">{{ type.label }}</text>
					</view>
				</view>
			</view>
			
			<!-- SJ型蓝人 -->
			<view class="mbti-group">
				<text class="group-title">SJ型蓝人</text>
				<view class="mbti-options">
					<view 
						v-for="type in sjTypes" 
						:key="type.value"
						class="mbti-option"
						:class="{ 'selected': selectedTypes.sj === type.value }"
						@tap="selectMbti('sj', type.value)"
					>
						<text class="mbti-text">{{ type.label }}</text>
					</view>
				</view>
			</view>
			
			<!-- SP型黄人 -->
			<view class="mbti-group">
				<text class="group-title">SP型黄人</text>
				<view class="mbti-options">
					<view 
						v-for="type in spTypes" 
						:key="type.value"
						class="mbti-option"
						:class="{ 'selected': selectedTypes.sp === type.value }"
						@tap="selectMbti('sp', type.value)"
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
	name: 'MbtiSetup',
	data() {
		return {
			// 每个组的选中状态
			selectedTypes: {
				nt: '',
				nf: '',
				sj: '',
				sp: ''
			},
			// NT型紫人
			ntTypes: [
				{ label: 'INTJ', value: 'INTJ' },
				{ label: 'INTP', value: 'INTP' },
				{ label: 'ENTJ', value: 'ENTJ' },
				{ label: 'ENTP', value: 'ENTP' }
			],
			// NF型绿人
			nfTypes: [
				{ label: 'INFJ', value: 'INFJ' },
				{ label: 'INFP', value: 'INFP' },
				{ label: 'ENFJ', value: 'ENFJ' },
				{ label: 'ENFP', value: 'ENFP' }
			],
			// SJ型蓝人
			sjTypes: [
				{ label: 'ISTJ', value: 'ISTJ' },
				{ label: 'ISFJ', value: 'ISFJ' },
				{ label: 'ESTJ', value: 'ESTJ' },
				{ label: 'ESFJ', value: 'ESFJ' }
			],
			// SP型黄人
			spTypes: [
				{ label: 'ISTP', value: 'ISTP' },
				{ label: 'ISFP', value: 'ISFP' },
				{ label: 'ESTP', value: 'ESTP' },
				{ label: 'ESFP', value: 'ESFP' }
			]
		}
	},
	computed: {
		// 是否有任何选择
		hasAnySelection() {
			return this.selectedTypes.nt || this.selectedTypes.nf || 
				   this.selectedTypes.sj || this.selectedTypes.sp;
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
			uni.setStorageSync('userMbtiTypes', this.selectedTypes);
			console.log('MBTI类型已保存:', this.selectedTypes);
			
			// 跳转到下一个MBTI类型选择页面
			uni.navigateTo({
				url: '/pages/mbti-type/mbti-type'
			});
		}
	},
	
	// 页面加载时获取已保存的MBTI信息
	onLoad() {
		const savedMbtiTypes = uni.getStorageSync('userMbtiTypes');
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

/* 进度条填充 - 第二步 */
.progress-fill.progress-step2 {
	width: 66.67%; /* 3步中的第2步 */
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
	justify-content: space-between;
}

/* MBTI选项 */
.mbti-option {
	flex: 1;
	min-width: 0;
	max-width: calc(25% - 15rpx);
	height: 80rpx; /* 40px * 2 */
	background: #FFFFFF;
	border: 2rpx solid #E0E0E0;
	border-radius: 20rpx; /* 10px * 2 */
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
	cursor: pointer;
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
		max-width: calc(25% - 12rpx);
	}
}

@media screen and (max-width: 600rpx) {
	.mbti-options {
		gap: 10rpx;
	}
	
	.mbti-option {
		max-width: calc(25% - 8rpx);
		height: 70rpx;
	}
	
	.mbti-text {
		font-size: 24rpx;
	}
}
</style>
