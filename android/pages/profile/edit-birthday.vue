<template>
	<view class="container">
		<!-- 顶部导航 -->
		<TopNavigation 
			title="编辑生日" 
			:showBack="true" 
			:showMore="false"
			titleColor="#8A70C9"
			@back="goBack"
		/>

		<!-- 内容区域 -->
		<view class="content">
			<!-- 日期标签和开关 -->
			<view class="header-row">
				<text class="label">日期</text>
				<view class="switch-row">
					<text class="switch-label">展示年龄</text>
					<switch :checked="showAge" @change="onSwitchChange" color="#8A70C9" />
				</view>
			</view>
			
			<!-- 日期选择器 -->
			<picker-view 
				class="picker-view" 
				:value="pickerValue" 
				@change="onPickerChange"
				:indicator-style="indicatorStyle"
			>
				<picker-view-column>
					<view class="picker-item" v-for="(year, index) in years" :key="index">
						<text class="picker-text" :class="{ selected: index === pickerValue[0] }">{{ year }}年</text>
					</view>
				</picker-view-column>
				<picker-view-column>
					<view class="picker-item" v-for="(month, index) in months" :key="index">
						<text class="picker-text" :class="{ selected: index === pickerValue[1] }">{{ month }}月</text>
					</view>
				</picker-view-column>
				<picker-view-column>
					<view class="picker-item" v-for="(day, index) in days" :key="index">
						<text class="picker-text" :class="{ selected: index === pickerValue[2] }">{{ day }}日</text>
					</view>
				</picker-view-column>
			</picker-view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<view 
				class="submit-button active"
				@tap="confirmSubmit"
			>
				<text class="submit-text">提交修改</text>
			</view>
		</view>

		<!-- 确认弹窗 -->
		<view class="confirm-modal" v-if="showConfirm" @tap="closeConfirm">
			<view class="modal-content" @tap.stop>
				<text class="modal-title">确定修改为该生日吗？</text>
				<view class="modal-buttons">
					<view class="modal-button cancel" @tap="closeConfirm">
						<text class="button-text">取消</text>
					</view>
					<view class="modal-button confirm" @tap="submitBirthday">
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
		const currentYear = new Date().getFullYear()
		const years = []
		for (let i = currentYear - 80; i <= currentYear; i++) {
			years.push(i)
		}
		
		const months = []
		for (let i = 1; i <= 12; i++) {
			months.push(i)
		}
		
		const days = []
		for (let i = 1; i <= 31; i++) {
			days.push(i)
		}
		
		return {
			years,
			months,
			days,
			pickerValue: [years.length - 22, 3, 0], // 默认2002年4月1日
			showAge: true,
			showConfirm: false,
			indicatorStyle: 'height: 88rpx;'
		}
	},
	computed: {
		selectedBirthday() {
			const year = this.years[this.pickerValue[0]]
			const month = this.months[this.pickerValue[1]]
			const day = this.days[this.pickerValue[2]]
			return `${year}年${month}月${day}日`
		}
	},
	onLoad(options) {
		if (options.showAge !== undefined) {
			this.showAge = options.showAge === 'true'
		}
		// 可以从参数中解析已有的生日
		if (options.birthday) {
			// 解析生日字符串并设置 pickerValue
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		
		onPickerChange(e) {
			this.pickerValue = e.detail.value
		},
		
		onSwitchChange(e) {
			this.showAge = e.detail.value
		},
		
		confirmSubmit() {
			this.showConfirm = true
		},
		
		closeConfirm() {
			this.showConfirm = false
		},
		
		submitBirthday() {
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

.picker-view {
	width: 100%;
	height: 500rpx;
}

.picker-item {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 88rpx;
	line-height: 88rpx;
}

.picker-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #999999;
}

.picker-text.selected {
	color: #333333;
	font-weight: 500;
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
