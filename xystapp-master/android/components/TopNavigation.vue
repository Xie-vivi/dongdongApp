<template>
	<view class="top-navigation-wrapper">
		<!-- 顶部状态栏占位 -->
		<view class="status-bar"></view>
		
		<!-- 顶部导航栏 -->
		<view class="top-nav">
			<view class="nav-left" @click="handleBack" v-if="showBack">
				<image src="/static/正文/back.png" class="back-icon" mode="aspectFit"></image>
			</view>
			<view class="nav-left" v-else style="width: 48rpx;"></view>
			
			<view class="nav-center">
				<text class="nav-title" :style="{ color: titleColor }">{{ title }}</text>
			</view>
			
			<view class="nav-right" @click="handleMore" v-if="showMore">
				<image src="/static/正文/点点.png" class="more-icon" mode="aspectFit"></image>
			</view>
			<view class="nav-right" v-else style="width: 48rpx;"></view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'TopNavigation',
	props: {
		// 标题文本
		title: {
			type: String,
			default: ''
		},
		// 标题颜色
		titleColor: {
			type: String,
			default: '#333333'
		},
		// 是否显示返回按钮
		showBack: {
			type: Boolean,
			default: true
		},
		// 是否显示更多按钮
		showMore: {
			type: Boolean,
			default: true
		},
		// 背景色
		backgroundColor: {
			type: String,
			default: '#F6F2FC'
		}
	},
	methods: {
		handleBack() {
			// 触发返回事件，父组件可以监听
			this.$emit('back')
			// 默认行为：返回上一页
			if (!this.$listeners.back) {
				uni.navigateBack()
			}
		},
		handleMore() {
			// 触发更多按钮点击事件
			this.$emit('more')
		}
	}
}
</script>

<style scoped>
.top-navigation-wrapper {
	background: #F6F2FC;
}

.status-bar {
	height: 88rpx; /* 44px * 2 状态栏高度 */
	background: #F6F2FC;
}

.top-nav {
	display: flex;
	align-items: center;
	justify-content: space-between;
	height: 88rpx;
	padding: 0 40rpx;
	background: #F6F2FC;
}

.nav-left, .nav-right {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

.back-icon, .more-icon {
	width: 48rpx;
	height: 48rpx;
}

.nav-center {
	flex: 1;
	display: flex;
	justify-content: center;
}

.nav-title {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 500;
	font-size: 32rpx;
	line-height: 44rpx;
	text-align: center;
}
</style>

