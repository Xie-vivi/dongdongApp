<template>
	<view class="profile-setup-container">
		<!-- 顶部进度条区域 -->
		<view class="header-section">
			<view class="progress-container">
				<view class="progress-bar">
					<view class="progress-fill"></view>
				</view>
			</view>
			
			<!-- 页面标题 -->
			<text class="page-title">基本信息</text>
		</view>
		
		<!-- 头像区域 -->
		<view class="avatar-section">
			<view class="avatar-container" @tap="selectAvatar">
				<image class="avatar-image" :src="userInfo.avatar" mode="aspectFill"></image>
				<view class="edit-icon-container">
					<image class="edit-icon" src="/static/type18/pencil.png" mode="aspectFit"></image>
				</view>
			</view>
		</view>
		
		<!-- 表单区域 -->
		<view class="form-section">
			<!-- 用户名输入 -->
			<view class="form-group">
				<text class="form-label">用户名 *</text>
				<view class="input-container">
					<input 
						class="form-input" 
						type="text" 
						placeholder="请输入昵称" 
						v-model="userInfo.username"
						maxlength="20"
						@input="onUsernameInput"
					/>
					<view class="dice-icon" @tap="generateRandomName">
						<image class="dice-image" src="/static/type18/s.png" mode="aspectFit"></image>
					</view>
				</view>
			</view>
			
			<!-- 性别选择 -->
			<view class="form-group">
				<text class="form-label">性别</text>
				<view class="gender-options">
					<view 
						class="gender-option" 
						:class="{ 'active': userInfo.gender === 'male' }"
						@tap="selectGender('male')"
					>
						<text class="gender-text">男</text>
					</view>
					<view 
						class="gender-option" 
						:class="{ 'active': userInfo.gender === 'female' }"
						@tap="selectGender('female')"
					>
						<text class="gender-text">女</text>
					</view>
					<view 
						class="gender-option" 
						:class="{ 'active': userInfo.gender === 'other' }"
						@tap="selectGender('other')"
					>
						<text class="gender-text">其他</text>
					</view>
				</view>
			</view>
			
			<!-- 生日选择 -->
			<view class="form-group">
				<text class="form-label">日期</text>
				<view class="date-picker-container">
					<view class="date-selectors">
						<!-- 年份选择 -->
						<picker 
							mode="selector" 
							:range="yearOptions" 
							:value="selectedYearIndex"
							@change="onYearChange"
							class="date-picker"
						>
							<view class="picker-text">{{ userInfo.birthYear }}年</view>
						</picker>
						
						<!-- 月份选择 -->
						<picker 
							mode="selector" 
							:range="monthOptions" 
							:value="selectedMonthIndex"
							@change="onMonthChange"
							class="date-picker"
						>
							<view class="picker-text">{{ userInfo.birthMonth }}月</view>
						</picker>
						
						<!-- 日期选择 -->
						<picker 
							mode="selector" 
							:range="dayOptions" 
							:value="selectedDayIndex"
							@change="onDayChange"
							class="date-picker"
						>
							<view class="picker-text">{{ userInfo.birthDay }}日</view>
						</picker>
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
				:disabled="!canProceed"
				:class="{ 'active': canProceed }"
				@tap="handleNext"
			>
				下一步
			</button>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			userInfo: {
				avatar: '/static/type18/t.png',
				username: '',
				gender: '',
				birthYear: '2002',
				birthMonth: '3',
				birthDay: '1'
			},
			yearOptions: [],
			monthOptions: [],
			dayOptions: [],
			selectedYearIndex: 0,
			selectedMonthIndex: 2, // 默认3月
			selectedDayIndex: 0, // 默认1日
			randomNames: [
				'小咚', '咚咚', '阳光少年', '快乐小鸟', '梦想家', 
				'星空漫步', '微笑天使', '自由飞翔', '彩虹之约', '温暖阳光',
				'清风徐来', '花开半夏', '时光荏苒', '岁月如歌', '青春无悔'
			]
		}
	},
	computed: {
		// 是否可以进入下一步
		canProceed() {
			return this.userInfo.username.trim().length >= 2 && this.userInfo.gender !== ''
		}
	},
	onLoad() {
		console.log('个人信息设置页面加载完成')
		this.initDateOptions()
	},
	methods: {
		// 初始化日期选项
		initDateOptions() {
			// 生成年份选项 (1950-2010)
			this.yearOptions = []
			for (let year = 1950; year <= 2010; year++) {
				this.yearOptions.push(year.toString())
			}
			
			// 生成月份选项
			this.monthOptions = []
			for (let month = 1; month <= 12; month++) {
				this.monthOptions.push(month.toString())
			}
			
			// 设置默认选中索引
			this.selectedYearIndex = this.yearOptions.indexOf('2002')
			this.selectedMonthIndex = 2 // 3月索引
			this.selectedDayIndex = 0 // 1日索引
			
			// 生成日期选项
			this.updateDayOptions()
		},
		
		// 更新日期选项
		updateDayOptions() {
			const year = parseInt(this.userInfo.birthYear)
			const month = parseInt(this.userInfo.birthMonth)
			const daysInMonth = new Date(year, month, 0).getDate()
			
			// 保存当前选中的日期
			const currentDay = parseInt(this.userInfo.birthDay)
			
			this.dayOptions = []
			for (let day = 1; day <= daysInMonth; day++) {
				this.dayOptions.push(day.toString())
			}
			
			// 如果当前选中的日期超出了新月份的天数，则调整到最后一天
			if (currentDay > daysInMonth) {
				this.selectedDayIndex = daysInMonth - 1
				this.userInfo.birthDay = daysInMonth.toString()
			} else {
				// 保持当前选中的日期
				this.selectedDayIndex = currentDay - 1
			}
		},
		
		// 用户名输入处理
		onUsernameInput(e) {
			this.userInfo.username = e.detail.value
		},
		
		// 生成随机用户名
		generateRandomName() {
			const randomIndex = Math.floor(Math.random() * this.randomNames.length)
			this.userInfo.username = this.randomNames[randomIndex]
			
			uni.showToast({
				title: '已生成随机昵称',
				icon: 'none',
				duration: 1000
			})
		},
		
		// 选择性别
		selectGender(gender) {
			this.userInfo.gender = gender
		},
		
		// 年份改变
		onYearChange(e) {
			this.selectedYearIndex = e.detail.value
			this.userInfo.birthYear = this.yearOptions[e.detail.value]
			this.updateDayOptions()
		},
		
		// 月份改变
		onMonthChange(e) {
			this.selectedMonthIndex = e.detail.value
			this.userInfo.birthMonth = this.monthOptions[e.detail.value]
			this.updateDayOptions()
		},
		
		// 日期改变
		onDayChange(e) {
			this.selectedDayIndex = e.detail.value
			this.userInfo.birthDay = this.dayOptions[e.detail.value]
		},
		
		// 选择头像
		selectAvatar() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.userInfo.avatar = res.tempFilePaths[0]
					uni.showToast({
						title: '头像已更新',
						icon: 'success',
						duration: 1000
					})
				},
				fail: () => {
					uni.showToast({
						title: '取消选择头像',
						icon: 'none',
						duration: 1000
					})
				}
			})
		},
		
		// 返回上一步
		goBack() {
			uni.navigateBack()
		},
		
		// 下一步处理
		async handleNext() {
			if (!this.canProceed) {
				uni.showToast({
					title: '请完善基本信息',
					icon: 'none'
				})
				return
			}
			
			try {
				// 模拟保存用户信息
				await this.saveUserProfile()
				
				// 保存到本地存储
				uni.setStorageSync('userProfile', this.userInfo)
				
				// 直接跳转到MBTI设置页面
				uni.navigateTo({
					url: '/pages/mbti-setup/mbti-setup'
				})
				
			} catch (error) {
				uni.hideLoading()
				uni.showToast({
					title: '保存失败，请重试',
					icon: 'none'
				})
			}
		},
		
		// 模拟保存用户资料
		saveUserProfile() {
			return new Promise((resolve) => {
				setTimeout(() => {
					console.log('用户信息保存成功:', this.userInfo)
					resolve({ success: true })
				}, 1000)
			})
		}
	}
}
</script>

<style scoped>
.profile-setup-container {
	position: relative;
	width: 100vw;
	height: 100vh;
	background: #FFFFFF;
	display: flex;
	flex-direction: column;
	overflow: hidden;
	padding-bottom: 120rpx; /* 为底部按钮留出空间 */
}

/* 顶部区域 */
.header-section {
	position: relative;
	width: 100%;
	height: 140rpx; /* 减少高度 */
	background: rgba(246, 242, 252, 0.988);
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

/* 进度条容器 */
.progress-container {
	position: absolute;
	top: 150rpx; /* 减少顶部距离 */
	left: 40rpx;
	right: 40rpx;
}

.progress-bar {
	position: relative;
	width: 100%;
	height: 20rpx; /* 10px * 2 */
	background: #FFFFFF;
	border: 2rpx solid #8A70C9;
	border-radius: 10rpx; /* 5px * 2 */
}

.progress-fill {
	position: absolute;
	left: 0;
	top: 0;
	width: 50%; /* 50%进度 */
	height: 100%;
	background: #8A70C9;
	border-radius: 10rpx; /* 5px * 2 */
}

/* 页面标题 */
.page-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 500;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #8A70C9;
	margin-top: 60rpx; /* 减少顶部间距 */
}

/* 头像区域 */
.avatar-section {
	position: relative;
	width: 100%;
	display: flex;
	justify-content: center;
	align-items: center;
	margin-top: 80rpx; /* 减少顶部间距 */
	margin-bottom: 30rpx; /* 减少底部间距 */
}

.avatar-container {
	position: relative;
	width: 180rpx; /* 减少头像尺寸 */
	height: 180rpx;
	border-radius: 50%;
	overflow: hidden;
	cursor: pointer;
}

.avatar-image {
	width: 100%;
	height: 100%;
	border-radius: 50%;
}

.edit-icon-container {
	position: absolute;
	right: -10rpx;
	bottom: 40rpx;
	width: 76rpx; /* 38px * 2 */
	height: 76rpx; /* 38px * 2 */
	background: #FFFFFF;
	border: 4rpx solid #C1B8E8;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.edit-icon {
	width: 48rpx; /* 24px * 2 */
	height: 48rpx; /* 24px * 2 */
}

/* 表单区域 */
.form-section {
	flex: 1;
	padding: 0 40rpx;
	margin-top: 40rpx; /* 减少顶部间距 */
}

/* 表单组 */
.form-group {
	margin-bottom: 30rpx; /* 减少底部间距 */
}

.form-label {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
	display: block;
	margin-bottom: 20rpx; /* 减少标签间距 */
}

/* 输入框容器 */
.input-container {
	position: relative;
	width: 100%;
	height: 80rpx; /* 40px * 2 */
	background: #F5F5F5;
	border-radius: 20rpx; /* 10px * 2 */
	display: flex;
	align-items: center;
}

.form-input {
	flex: 1;
	height: 100%;
	border: none;
	background: transparent;
	padding: 0 40rpx; /* 20px * 2 */
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 44rpx; /* 22px * 2 */
	color: #333333;
	box-sizing: border-box;
}

.form-input::placeholder {
	color: #757575;
}

.dice-icon {
	position: absolute;
	right: 20rpx; /* 10px * 2 */
	width: 60rpx; /* 30px * 2 */
	height: 60rpx; /* 30px * 2 */
	background: #8A70C9;
	border-radius: 10rpx; /* 5px * 2 */
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
}

.dice-image {
	width: 40rpx; /* 20px * 2 */
	height: 36rpx; /* 18px * 2 */
}

/* 性别选择 */
.gender-options {
	display: flex;
	width: 100%;
	height: 80rpx; /* 40px * 2 */
	background: #F5F5F5;
	border-radius: 20rpx; /* 10px * 2 */
	overflow: hidden;
}

.gender-option {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
	transition: all 0.3s ease;
}

.gender-option.active {
	background: #8A70C9;
}

.gender-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 44rpx; /* 22px * 2 */
	color: #757575;
	transition: color 0.3s ease;
}

.gender-option.active .gender-text {
	color: #FFFFFF;
}

/* 日期选择器 */
.date-picker-container {
	width: 100%;
}

.date-selectors {
	display: flex;
	width: 100%;
	height: 80rpx; /* 40px * 2 */
	background: #F5F5F5;
	border-radius: 20rpx; /* 10px * 2 */
	overflow: hidden;
}

.date-picker {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
}

.picker-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-style: normal;
	font-weight: 400;
	font-size: 32rpx; /* 16px * 2 */
	line-height: 44rpx; /* 22px * 2 */
	color: #333333;
	text-align: center;
}

/* 底部按钮区域 */
.bottom-buttons {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	width: 100%;
	height: 120rpx;
	padding: 20rpx 60rpx; /* 增加左右内边距确保按钮不贴边 */
	display: flex;
	justify-content: center; /* 水平居中 */
	align-items: center; /* 垂直居中 */
	gap: 30rpx; /* 增加按钮间距 */
	background: #FFFFFF;
	border-top: 1rpx solid #F0F0F0;
	z-index: 100;
	box-sizing: border-box; /* 确保padding计算在宽度内 */
}

/* 次要按钮 */
.btn-secondary {
	flex: 1; /* 使用flex: 1 让按钮自适应宽度 */
	max-width: 280rpx; /* 设置最大宽度 */
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
	flex: 1; /* 使用flex: 1 让按钮自适应宽度 */
	max-width: 280rpx; /* 设置最大宽度 */
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

.btn-primary:active.active {
	transform: scale(0.98);
}
</style>
