<template>
	<view id="app">
		<!-- 这里是应用的根组件 -->
	</view>
</template>

<script>
	export default {
		onLaunch: function() {
			console.log('=== APP 启动 ===')
		},
		onShow: function() {
			console.log('=== APP 显示 ===')
		},
		onHide: function() {
			console.log('=== APP 隐藏 ===')
		}
	}
</script>

<style>
	/*每个页面公共css */
	@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;700&display=swap');
	
	/* 全局样式 */
	page {
		background-color: #f5f5f5;
		font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
	}
	
	/* 通用按钮样式 */
	.btn {
		padding: 24rpx 48rpx;
		border-radius: 12rpx;
		font-size: 32rpx;
		font-weight: 500;
		text-align: center;
		transition: all 0.3s ease;
	}
	
	.btn-primary {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		color: white;
		border: none;
	}
	
	.btn-primary:active {
		opacity: 0.8;
		transform: scale(0.98);
	}
	
	/* 输入框样式 */
	.input {
		padding: 24rpx;
		border: 2rpx solid #e1e5e9;
		border-radius: 12rpx;
		font-size: 32rpx;
		background-color: white;
		transition: border-color 0.3s ease;
	}
	
	.input:focus {
		border-color: #667eea;
		outline: none;
	}
	
	/* 卡片样式 */
	.card {
		background: white;
		border-radius: 16rpx;
		padding: 48rpx;
		margin: 24rpx;
		box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
	}
</style>
