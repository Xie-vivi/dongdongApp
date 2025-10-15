<template>
	<view class="footer">
		<view class="footer-item" @click="goToPage('index')">
			<image class="footer-icon" src="/static/关注页/daohang/1.png"></image>
			<text class="footer-text">首页</text>
		</view>
		<view class="footer-item" @click="goToPage('club')">
			<image class="footer-icon" src="/static/关注页/daohang/2.png"></image>
			<text class="footer-text">社团</text>
		</view>
		<view class="footer-item publish-btn" @click="goToPage('publish')">
			<view class="publish-icon">
				<view class="plus-h"></view>
				<view class="plus-v"></view>
			</view>
		</view>
		<view class="footer-item" @click="goToPage('message')">
			<view class="footer-icon message-icon-wrapper">
				<image class="footer-icon" src="/static/关注页/daohang/2.png"></image>
				<view class="message-badge" v-if="messageCount > 0">
					<text class="badge-text">{{ messageCount }}</text>
				</view>
			</view>
			<text class="footer-text">消息</text>
		</view>
		<view class="footer-item" @click="goToPage('profile')">
			<image class="footer-icon" src="/static/关注页/daohang/3.png"></image>
			<text class="footer-text">我的</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'BottomNavigation',
	props: {
		currentPage: {
			type: String,
			default: 'index'
		},
		messageCount: {
			type: Number,
			default: 0
		}
	},
	methods: {
		goToPage(page) {
			// 如果是当前页面，不进行跳转
			if (page === this.currentPage) {
				return
			}
			
			const routes = {
				index: '/pages/index/index',
				club: '/pages/club/club',
				message: '/pages/message/message',
				publish: '/pages/publish/publish',
				profile: '/pages/profile/profile'
			}
			
			if (routes[page]) {
				if (page === 'index') {
					// 首页使用 reLaunch 确保是根页面
					uni.reLaunch({
						url: routes[page]
					})
				} else {
					uni.navigateTo({
						url: routes[page]
					})
				}
			}
		}
	}
}
</script>

<style scoped>
.footer {
	position: fixed;
	bottom: 0;
	left: 0;
	width: 100%;
	height: 160rpx; /* 80px * 2 */
	background: #F9F6FF;
	display: flex;
	align-items: center;
	justify-content: space-around;
	z-index: 100;
}

.footer-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 8rpx;
}

.publish-btn {
	position: relative;
}

.footer-icon {
	width: 44rpx; /* 22px * 2 */
	height: 44rpx;
}

.message-icon-wrapper {
	position: relative;
	width: 44rpx;
	height: 44rpx;
}

.message-badge {
	position: absolute;
	top: -8rpx;
	right: -8rpx;
	width: 34rpx; /* 17px * 2 */
	height: 32rpx; /* 16px * 2 */
	background: #8A70C9;
	border: 2rpx solid #F9F6FF; /* 1px * 2 */
	border-radius: 30rpx; /* 15px * 2 */
	display: flex;
	align-items: center;
	justify-content: center;
}

.badge-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 20rpx; /* 10px * 2 */
	line-height: 28rpx; /* 14px * 2 */
	color: #FFFFFF;
}

.publish-icon {
	width: 96rpx; /* 48px * 2 */
	height: 80rpx; /* 40px * 2 */
	background: #8A70C9;
	border-radius: 30rpx; /* 15px * 2 */
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
}

.plus-h, .plus-v {
	position: absolute;
	background: #FFFFFF;
}

.plus-h {
	width: 32rpx; /* 16px * 2 */
	height: 6rpx; /* 3px * 2 */
}

.plus-v {
	width: 6rpx; /* 3px * 2 */
	height: 32rpx; /* 16px * 2 */
}

.footer-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 24rpx; /* 12px * 2 */
	line-height: 32rpx; /* 16px * 2 */
	color: #333333;
}
</style>
