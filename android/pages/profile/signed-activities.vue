<template>
	<view class="container">
		<!-- 顶部导航 -->
		<TopNavigation 
			title="报名的活动" 
			:showBack="true" 
			:showMore="false"
			titleColor="#8A70C9"
			@back="goBack"
		/>

		<!-- 日历选择器 -->
		<view class="calendar-section">
			<view class="calendar-header">
				<text class="calendar-year-month">{{ currentYear }}年{{ currentMonth }}月</text>
				<view class="calendar-nav">
					<image class="nav-btn" src="/static/profile/arrow-left.png" mode="aspectFit" @click="prevMonth"></image>
					<image class="nav-btn" src="/static/profile/arrow-right.png" mode="aspectFit" @click="nextMonth"></image>
				</view>
			</view>
			
			<view class="calendar-weekdays">
				<text class="weekday" v-for="day in weekdays" :key="day">{{ day }}</text>
			</view>
			
			<view class="calendar-days">
				<view 
					class="calendar-day" 
					v-for="day in calendarDays" 
					:key="day.date"
					:class="{
						'other-month': day.isOtherMonth,
						'selected': day.isSelected,
						'has-activity': day.hasActivity
					}"
					@click="selectDate(day)"
				>
					<text class="day-number">{{ day.day }}</text>
				</view>
			</view>
		</view>

		<!-- 活动列表 -->
		<view class="activities-section" v-if="selectedDateActivities.length > 0">
			<view class="activity-item" v-for="activity in selectedDateActivities" :key="activity.id">
				<image class="activity-image" :src="activity.image" mode="aspectFill"></image>
				<view class="activity-content">
					<view class="activity-header">
						<text class="activity-title">{{ activity.title }}</text>
						<view class="activity-like">
							<image class="heart-icon" src="/static/discover/1.png" mode="aspectFit"></image>
							<text class="like-count">{{ activity.likes }}</text>
						</view>
					</view>
					<text class="activity-datetime">{{ activity.date }} {{ activity.day }} {{ activity.time }}</text>
					<text class="activity-location">{{ activity.location }}</text>
					<view class="activity-footer">
						<view class="activity-tag">
							<text class="tag-text">{{ activity.tag }}</text>
						</view>
						<text class="participants">已报名：{{ activity.participants }}人</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import TopNavigation from '@/components/TopNavigation.vue'

export default {
	components: { TopNavigation },
	data() {
		return {
			currentYear: 2025,
			currentMonth: 10,
			selectedDate: null,
			weekdays: ['一', '二', '三', '四', '五', '六', '日'],
			activities: [
				{
					id: 1,
					title: '羽毛球初级训练找人：1v1或2v2都可',
					date: '2025.10.16',
					day: '周五',
					time: '17:00 - 18:00',
					location: '体育馆2楼',
					tag: '羽毛球场',
					participants: 5,
					likes: 21,
					image: '/static/discover/2.png',
					dateKey: '2025-10-16'
				}
			]
		}
	},
	computed: {
		calendarDays() {
			const year = this.currentYear
			const month = this.currentMonth
			const firstDay = new Date(year, month - 1, 1)
			const lastDay = new Date(year, month, 0)
			const firstDayWeek = firstDay.getDay() || 7 // 周日为0，转换为7
			const daysInMonth = lastDay.getDate()
			
			const days = []
			
			// 上个月的日期
			const prevMonth = month === 1 ? 12 : month - 1
			const prevYear = month === 1 ? year - 1 : year
			const prevMonthLastDay = new Date(prevYear, prevMonth, 0).getDate()
			
			for (let i = firstDayWeek - 1; i > 0; i--) {
				days.push({
					day: prevMonthLastDay - i + 1,
					date: `${prevYear}-${prevMonth.toString().padStart(2, '0')}-${(prevMonthLastDay - i + 1).toString().padStart(2, '0')}`,
					isOtherMonth: true,
					isSelected: false,
					hasActivity: false
				})
			}
			
			// 当前月的日期
			for (let day = 1; day <= daysInMonth; day++) {
				const dateKey = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`
				const hasActivity = this.activities.some(activity => activity.dateKey === dateKey)
				
				days.push({
					day,
					date: dateKey,
					isOtherMonth: false,
					isSelected: this.selectedDate === dateKey,
					hasActivity
				})
			}
			
			// 下个月的日期（补齐42天）
			const nextMonth = month === 12 ? 1 : month + 1
			const nextYear = month === 12 ? year + 1 : year
			const remainingDays = 42 - days.length
			
			for (let day = 1; day <= remainingDays; day++) {
				days.push({
					day,
					date: `${nextYear}-${nextMonth.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`,
					isOtherMonth: true,
					isSelected: false,
					hasActivity: false
				})
			}
			
			return days
		},
		selectedDateActivities() {
			if (!this.selectedDate) return []
			return this.activities.filter(activity => activity.dateKey === this.selectedDate)
		}
	},
	mounted() {
		// 默认选择有活动的日期
		if (this.activities.length > 0) {
			this.selectedDate = this.activities[0].dateKey
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		prevMonth() {
			if (this.currentMonth === 1) {
				this.currentMonth = 12
				this.currentYear--
			} else {
				this.currentMonth--
			}
		},
		nextMonth() {
			if (this.currentMonth === 12) {
				this.currentMonth = 1
				this.currentYear++
			} else {
				this.currentMonth++
			}
		},
		selectDate(day) {
			if (!day.isOtherMonth) {
				this.selectedDate = day.date
			}
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: #F6F2FC;
}

.calendar-section {
	background: #FFFFFF;
	margin: 0 40rpx 20rpx;
	border-radius: 20rpx;
	padding: 40rpx;
}

.calendar-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 40rpx;
}

.calendar-year-month {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 36rpx;
	line-height: 48rpx;
	color: #333333;
	font-weight: 600;
}

.calendar-nav {
	display: flex;
	gap: 20rpx;
}

.nav-btn {
	width: 48rpx;
	height: 48rpx;
}

.calendar-weekdays {
	display: flex;
	margin-bottom: 20rpx;
}

.weekday {
	flex: 1;
	text-align: center;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 36rpx;
	color: #666666;
}

.calendar-days {
	display: flex;
	flex-wrap: wrap;
}

.calendar-day {
	width: 14.28%;
	height: 80rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 10rpx;
}

.calendar-day.other-month .day-number {
	color: #CCCCCC;
}

.calendar-day.selected {
	background: #EDE7F9;
	border-radius: 12rpx;
}

.calendar-day.has-activity {
	background: #8A70C9;
	border-radius: 12rpx;
}

.calendar-day.has-activity .day-number {
	color: #FFFFFF;
}

.day-number {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 36rpx;
	color: #333333;
}

.activities-section {
	padding: 0 40rpx;
}

.activity-item {
	background: #FFFFFF;
	border-radius: 20rpx;
	padding: 40rpx;
	margin-bottom: 20rpx;
	display: flex;
}

.activity-image {
	width: 200rpx;
	height: 140rpx;
	border-radius: 16rpx;
	margin-right: 20rpx;
}

.activity-content {
	flex: 1;
}

.activity-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 12rpx;
}

.activity-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	line-height: 36rpx;
	color: #333333;
	font-weight: 600;
	flex: 1;
	margin-right: 20rpx;
}

.activity-like {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 4rpx;
}

.heart-icon {
	width: 32rpx;
	height: 32rpx;
}

.like-count {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 20rpx;
	line-height: 24rpx;
	color: #333333;
}

.activity-datetime {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #8A70C9;
	margin-bottom: 4rpx;
}

.activity-location {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #666666;
	margin-bottom: 12rpx;
}

.activity-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.activity-tag {
	background: #EDE7F9;
	border-radius: 16rpx;
	padding: 8rpx 16rpx;
}

.tag-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 20rpx;
	line-height: 24rpx;
	color: #8A70C9;
}

.participants {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx;
	line-height: 32rpx;
	color: #8A70C9;
}
</style>
