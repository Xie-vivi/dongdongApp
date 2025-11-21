<template>
	<view class="post-detail-container">
		<!-- 顶部导航栏 -->
		<TopNavigation 
			title="帖子正文" 
			titleColor="#8A70C9"
			:showBack="true" 
			:showMore="true"
			@back="goBack"
			@more="showMore"
		/>
		
		<!-- 帖子内容区域 -->
		<view class="post-content">
			<!-- 用户信息 -->
			<view class="user-info">
				<view class="user-avatar">
					<image :src="postData.userAvatar" class="avatar-img" mode="aspectFill"></image>
				</view>
				<view class="user-details">
					<text class="username">{{ postData.username }}</text>
					<view class="post-meta">
						<text class="post-time">{{ postData.postTime }}</text>
						<text class="post-location">{{ postData.location }}</text>
					</view>
				</view>
				<view class="follow-btn" :class="{ 'followed': postData.isFollowed }" @click="toggleFollow">
					<text class="follow-text">{{ postData.isFollowed ? '已关注' : '关注' }}</text>
				</view>
			</view>
			
			<!-- 帖子标题 -->
			<view class="post-title">
				<text class="title-text">{{ postData.title }}</text>
			</view>
			
			<!-- 帖子正文 -->
			<view class="post-body">
				<text class="body-text">{{ postData.content }}</text>
			</view>
			
			<!-- 帖子图片 -->
			<view class="post-images" v-if="postData.images && postData.images.length > 0">
				<view class="image-item" v-for="(image, index) in postData.images" :key="index" @click="previewImage(index)">
					<image :src="image" class="post-image" mode="aspectFill"></image>
				</view>
			</view>
			
			<!-- 相关场 -->
			<view class="related-section">
				<text class="related-title">相关场</text>
				<view class="related-card">
					<view class="related-avatar">
						<image :src="postData.relatedGroup.avatar" class="related-avatar-img" mode="aspectFill"></image>
					</view>
					<view class="related-info">
						<text class="related-name">{{ postData.relatedGroup.name }}</text>
						<view class="related-stats">
							<text class="related-stat">{{ postData.relatedGroup.followers }}关注</text>
							<text class="related-stat">{{ postData.relatedGroup.posts }}帖子</text>
						</view>
					</view>
					<view class="join-btn" @click="joinGroup">
						<text class="join-text">进场</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 底部互动栏 -->
		<view class="bottom-actions">
			<view class="comment-input" @click="showCommentInput">
				<text class="input-placeholder">加入讨论？</text>
			</view>
			<view class="action-buttons">
				<view class="action-btn" @click="toggleLike">
					<image src="/static/follow/5.png" class="action-icon" mode="aspectFit" :class="{ 'liked': postData.isLiked }"></image>
					<text class="action-count">{{ postData.likeCount }}</text>
				</view>
				<view class="action-btn" @click="toggleStar">
					<image src="/static/follow/6.png" class="action-icon" mode="aspectFit" :class="{ 'starred': postData.isStared }"></image>
					<text class="action-count">{{ postData.starCount }}</text>
				</view>
				<view class="action-btn" @click="scrollToComments">
					<image src="/static/follow/7.png" class="action-icon" mode="aspectFit"></image>
					<text class="action-count">{{ postData.commentCount }}</text>
				</view>
			</view>
		</view>
		
		<!-- 评论区域 -->
		<view class="comments-section">
			<!-- 评论头部 -->
			<view class="comments-header">
				<text class="comments-title">评论{{ postData.commentCount }}</text>
				<view class="sort-options">
					<text class="sort-option" :class="{ 'active': sortType === 'hot' }" @click="changeSortType('hot')">热度最高</text>
					<text class="sort-option" :class="{ 'active': sortType === 'time' }" @click="changeSortType('time')">按热度</text>
					<image src="/static/follow/menu-01.png" class="sort-menu" mode="aspectFit"></image>
				</view>
			</view>
			
			<!-- 分割线 -->
			<view class="divider"></view>
			
			<!-- 评论列表 -->
			<view class="comments-list">
				<view class="comment-item" v-for="(comment, index) in comments" :key="comment.id">
					<view class="comment-avatar">
						<image :src="comment.userAvatar" class="comment-avatar-img" mode="aspectFill"></image>
					</view>
					<view class="comment-content">
						<text class="comment-username">{{ comment.username }}</text>
						<text class="comment-text">{{ comment.content }}</text>
						<view class="comment-meta">
							<text class="comment-time">{{ comment.time }}</text>
							<text class="comment-location">{{ comment.location }}</text>
							<text class="reply-btn" @click="replyToComment(comment.id)">回复</text>
						</view>
						
						<!-- 评论的回复 -->
						<view class="replies" v-if="comment.replies && comment.replies.length > 0">
							<view class="reply-item" v-for="reply in comment.replies" :key="reply.id">
								<text class="reply-text">{{ reply.username }}：{{ reply.content }}</text>
							</view>
							<text class="view-more-replies" v-if="comment.totalReplies > comment.replies.length" @click="viewMoreReplies(comment.id)">
								查看共{{ comment.totalReplies }}条回复
							</text>
						</view>
					</view>
					<view class="comment-actions">
						<view class="comment-like" @click="toggleCommentLike(comment.id)">
							<image src="/static/follow/5.png" class="comment-like-icon" mode="aspectFit" :class="{ 'liked': comment.isLiked }"></image>
							<text class="comment-like-count">{{ comment.likeCount }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 评论输入弹窗 -->
		<view class="comment-modal" v-if="showCommentModal" @click="hideCommentModal">
			<view class="comment-modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">发表评论</text>
					<text class="modal-close" @click="hideCommentModal">×</text>
				</view>
				<textarea class="comment-textarea" v-model="commentText" placeholder="说点什么吧..." maxlength="500"></textarea>
				<view class="modal-footer">
					<text class="char-count">{{ commentText.length }}/500</text>
					<view class="send-btn" :class="{ 'active': commentText.trim().length > 0 }" @click="sendComment">
						<text class="send-text">发送</text>
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
			postData: {
				userAvatar: '/static/follow/follow-users-section/Ellipse 2.png',
				username: '我是lol场的一个小用户',
				postTime: '昨天 20:15',
				location: '北京',
				isFollowed: true,
				title: '这是我的帖子的一个标题',
				content: '测试一下最长的长度在这里我们可以轻松自定义主题背景与装扮，打造专属兴趣空间，轻松找到同好社群，感受如 "家园" 般的沉浸互动氛围；实时获取全校各类活动动态后面我再凑一点字数然后写的更长一点\n当灿烂的太阳跳出东海的碧波。\n#吐槽 #2026新生\n@会吃西瓜的小鸭纸',
				images: [
					'/static/follow/Rectangle 107.png',
					'/static/follow/Rectangle 108.png',
					'/static/follow/Rectangle 109.png'
				],
				relatedGroup: {
					avatar: '/static/follow/follow-users-section/Ellipse 9.png',
					name: 'lol场',
					followers: '261',
					posts: '54'
				},
				isLiked: true,
				likeCount: 125,
				isStared: false,
				starCount: 21,
				commentCount: 15
			},
			comments: [
				{
					id: 1,
					userAvatar: '/static/follow/follow-users-section/Ellipse 11.png',
					username: '我是评论人机',
					content: '试试看评论是什么样的呗感觉还挺有意思的可以多写点看看效果怎么样。^ ^ @会吃西瓜的小鸭纸',
					time: '1小时前',
					location: '北京',
					isLiked: false,
					likeCount: 125,
					replies: [
						{
							id: 11,
							username: '我是评论人机的评论人机',
							content: '没错我就是来测试一下评论的评论怎么样的'
						},
						{
							id: 12,
							username: '我不是人机',
							content: '怎么全是测试评论的人机？'
						}
					],
					totalReplies: 8
				},
				{
					id: 2,
					userAvatar: '/static/follow/follow-users-section/Ellipse 13.png',
					username: '我是评论人机2号',
					content: '我也试试看评论是什么样的呗感觉还挺有意思的可以多写点看看效果怎么样 @不会吃西瓜的小鸭纸',
					time: '2小时前',
					location: '北京',
					isLiked: true,
					likeCount: 15,
					replies: [
						{
							id: 21,
							username: '我是评论人机的评论人机2',
							content: '没错我就是来测试一下评论的评论怎么样的'
						},
						{
							id: 22,
							username: '我不是人机2',
							content: '怎么全是测试评论的人机？'
						}
					],
					totalReplies: 3
				}
			],
			sortType: 'hot', // hot: 热度最高, time: 按时间
			showCommentModal: false,
			commentText: ''
		}
	},
	onLoad(options) {
		// 可以通过options获取传递的帖子ID等参数
		console.log('帖子详情页参数：', options)
		// 这里可以根据帖子ID加载具体的帖子数据
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		showMore() {
			uni.showActionSheet({
				itemList: ['举报', '分享', '收藏'],
				success: (res) => {
					console.log('选中了第' + (res.tapIndex + 1) + '个按钮')
				}
			})
		},
		toggleFollow() {
			this.postData.isFollowed = !this.postData.isFollowed
		},
		previewImage(index) {
			uni.previewImage({
				current: index,
				urls: this.postData.images
			})
		},
		joinGroup() {
			uni.showToast({
				title: '加入成功',
				icon: 'success'
			})
		},
		toggleLike() {
			this.postData.isLiked = !this.postData.isLiked
			if (this.postData.isLiked) {
				this.postData.likeCount++
			} else {
				this.postData.likeCount--
			}
		},
		toggleStar() {
			this.postData.isStared = !this.postData.isStared
			if (this.postData.isStared) {
				this.postData.starCount++
			} else {
				this.postData.starCount--
			}
		},
		scrollToComments() {
			// 滚动到评论区域
			uni.pageScrollTo({
				selector: '.comments-section',
				duration: 300
			})
		},
		showCommentInput() {
			this.showCommentModal = true
		},
		hideCommentModal() {
			this.showCommentModal = false
			this.commentText = ''
		},
		sendComment() {
			if (this.commentText.trim().length === 0) return
			
			// 添加新评论到列表
			const newComment = {
				id: Date.now(),
				userAvatar: '/static/images/avatar.png',
				username: '当前用户',
				content: this.commentText.trim(),
				time: '刚刚',
				location: '北京',
				isLiked: false,
				likeCount: 0,
				replies: [],
				totalReplies: 0
			}
			
			this.comments.unshift(newComment)
			this.postData.commentCount++
			this.hideCommentModal()
			
			uni.showToast({
				title: '评论成功',
				icon: 'success'
			})
		},
		changeSortType(type) {
			this.sortType = type
			// 这里可以重新排序评论列表
		},
		toggleCommentLike(commentId) {
			const comment = this.comments.find(c => c.id === commentId)
			if (comment) {
				comment.isLiked = !comment.isLiked
				if (comment.isLiked) {
					comment.likeCount++
				} else {
					comment.likeCount--
				}
			}
		},
		replyToComment(commentId) {
			// 回复评论功能
			this.showCommentInput()
		},
		viewMoreReplies(commentId) {
			// 查看更多回复功能
			console.log('查看更多回复：', commentId)
		}
	}
}
</script>

<style lang="scss" scoped>
.post-detail-container {
	background: #F6F2FC;
	min-height: 100vh;
}

.post-content {
	background: #FFFFFF;
	padding: 40rpx;
	margin-bottom: 20rpx;
	
	.user-info {
		display: flex;
		align-items: flex-start;
		margin-bottom: 40rpx;
		
		.user-avatar {
			width: 80rpx;
			height: 80rpx;
			border-radius: 50%;
			overflow: hidden;
			margin-right: 20rpx;
			
			.avatar-img {
				width: 100%;
				height: 100%;
			}
		}
		
		.user-details {
			flex: 1;
			
			.username {
				font-family: 'Alibaba PuHuiTi';
				font-weight: 500;
				font-size: 32rpx;
				line-height: 44rpx;
				color: #333333;
				display: block;
				margin-bottom: 10rpx;
			}
			
			.post-meta {
				display: flex;
				align-items: center;
				
				.post-time, .post-location {
					font-family: 'Alibaba PuHuiTi';
					font-size: 24rpx;
					line-height: 32rpx;
					color: #757575;
					margin-right: 20rpx;
				}
			}
		}
		
		.follow-btn {
			width: 112rpx;
			height: 50rpx;
			background: #EAEEFD;
			border-radius: 10rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			
			&.followed {
				background: #EAEEFD;
			}
			
			.follow-text {
				font-family: 'Alibaba PuHuiTi';
				font-size: 24rpx;
				line-height: 50rpx;
				color: #333333;
			}
		}
	}
	
	.post-title {
		margin-bottom: 20rpx;
		
		.title-text {
			font-family: 'Alibaba PuHuiTi';
			font-weight: 500;
			font-size: 32rpx;
			line-height: 44rpx;
			color: #333333;
		}
	}
	
	.post-body {
		margin-bottom: 40rpx;
		
		.body-text {
			font-family: 'Alibaba PuHuiTi';
			font-size: 32rpx;
			line-height: 50rpx;
			color: #333333;
		}
	}
	
	.post-images {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
		margin-bottom: 40rpx;
		
		.image-item {
			width: 220rpx;
			height: 220rpx;
			border-radius: 16rpx;
			overflow: hidden;
			
			.post-image {
				width: 100%;
				height: 100%;
			}
		}
	}
	
	.related-section {
		.related-title {
			font-family: 'Alibaba PuHuiTi';
			font-size: 28rpx;
			line-height: 38rpx;
			color: #757575;
			margin-bottom: 20rpx;
			display: block;
		}
		
		.related-card {
			background: #F5F5F5;
			border-radius: 20rpx;
			padding: 30rpx;
			display: flex;
			align-items: center;
			
			.related-avatar {
				width: 100rpx;
				height: 100rpx;
				border-radius: 50%;
				overflow: hidden;
				margin-right: 20rpx;
				
				.related-avatar-img {
					width: 100%;
					height: 100%;
				}
			}
			
			.related-info {
				flex: 1;
				
				.related-name {
					font-family: 'Alibaba PuHuiTi';
					font-weight: 500;
					font-size: 32rpx;
					line-height: 44rpx;
					color: #333333;
					display: block;
					margin-bottom: 10rpx;
				}
				
				.related-stats {
					display: flex;
					
					.related-stat {
						font-family: 'Alibaba PuHuiTi';
						font-size: 28rpx;
						line-height: 38rpx;
						color: #B0B0B0;
						margin-right: 20rpx;
					}
				}
			}
			
			.join-btn {
				width: 112rpx;
				height: 50rpx;
				background: #C1B8E8;
				border-radius: 10rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				
				.join-text {
					font-family: 'Alibaba PuHuiTi';
					font-size: 24rpx;
					line-height: 32rpx;
					color: #333333;
				}
			}
		}
	}
}

.bottom-actions {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	height: 160rpx;
	background: #F6F2FC;
	display: flex;
	align-items: center;
	padding: 0 40rpx;
	z-index: 100;
	
	.comment-input {
		flex: 1;
		height: 60rpx;
		background: #FFFFFF;
		border-radius: 20rpx;
		display: flex;
		align-items: center;
		padding: 0 30rpx;
		margin-right: 20rpx;
		
		.input-placeholder {
			font-family: 'Alibaba PuHuiTi';
			font-size: 28rpx;
			line-height: 38rpx;
			color: #757575;
		}
	}
	
	.action-buttons {
		display: flex;
		align-items: center;
		gap: 40rpx;
		
		.action-btn {
			display: flex;
			flex-direction: column;
			align-items: center;
			
			.action-icon {
				width: 48rpx;
				height: 48rpx;
				margin-bottom: 5rpx;
			}
			
			.action-count {
				font-family: 'Alibaba PuHuiTi';
				font-size: 28rpx;
				line-height: 38rpx;
				color: #333333;
			}
		}
	}
}

.comments-section {
	background: #FFFFFF;
	margin-top: 160rpx; // 为底部固定栏留出空间
	
	.comments-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 40rpx;
		
		.comments-title {
			font-family: 'Alibaba PuHuiTi';
			font-size: 28rpx;
			line-height: 38rpx;
			color: #8A70C9;
		}
		
		.sort-options {
			display: flex;
			align-items: center;
			gap: 20rpx;
			
			.sort-option {
				font-family: 'Alibaba PuHuiTi';
				font-size: 24rpx;
				line-height: 50rpx;
				color: #757575;
				
				&.active {
					color: #8A70C9;
				}
			}
			
			.sort-menu {
				width: 48rpx;
				height: 48rpx;
			}
		}
	}
	
	.divider {
		height: 2rpx;
		background: #E6D9F9;
		margin: 0 40rpx;
	}
	
	.comments-list {
		padding: 0 40rpx 40rpx;
		
		.comment-item {
			display: flex;
			padding: 40rpx 0;
			border-bottom: 2rpx solid #E6D9F9;
			
			&:last-child {
				border-bottom: none;
			}
			
			.comment-avatar {
				width: 80rpx;
				height: 80rpx;
				border-radius: 50%;
				overflow: hidden;
				margin-right: 20rpx;
				flex-shrink: 0;
				
				.comment-avatar-img {
					width: 100%;
					height: 100%;
				}
			}
			
			.comment-content {
				flex: 1;
				
				.comment-username {
					font-family: 'Alibaba PuHuiTi';
					font-weight: 500;
					font-size: 28rpx;
					line-height: 38rpx;
					color: #757575;
					display: block;
					margin-bottom: 10rpx;
				}
				
				.comment-text {
					font-family: 'Alibaba PuHuiTi';
					font-size: 28rpx;
					line-height: 50rpx;
					color: #333333;
					display: block;
					margin-bottom: 20rpx;
				}
				
				.comment-meta {
					display: flex;
					align-items: center;
					margin-bottom: 20rpx;
					
					.comment-time, .comment-location, .reply-btn {
						font-family: 'Alibaba PuHuiTi';
						font-size: 24rpx;
						line-height: 32rpx;
						color: #757575;
						margin-right: 20rpx;
					}
					
					.reply-btn {
						color: #333333;
					}
				}
				
				.replies {
					background: #F5F5F5;
					border-radius: 20rpx;
					padding: 20rpx;
					
					.reply-item {
						margin-bottom: 10rpx;
						
						&:last-child {
							margin-bottom: 0;
						}
						
						.reply-text {
							font-family: 'Alibaba PuHuiTi';
							font-size: 28rpx;
							line-height: 38rpx;
							color: #757575;
						}
					}
					
					.view-more-replies {
						font-family: 'Alibaba PuHuiTi';
						font-size: 28rpx;
						line-height: 38rpx;
						color: #8A70C9;
						margin-top: 10rpx;
						display: block;
					}
				}
			}
			
			.comment-actions {
				display: flex;
				flex-direction: column;
				align-items: center;
				margin-left: 20rpx;
				
				.comment-like {
					display: flex;
					flex-direction: column;
					align-items: center;
					
					.comment-like-icon {
						width: 48rpx;
						height: 48rpx;
						margin-bottom: 5rpx;
					}
					
					.comment-like-count {
						font-family: 'Alibaba PuHuiTi';
						font-size: 28rpx;
						line-height: 38rpx;
						color: #333333;
					}
				}
			}
		}
	}
}

.comment-modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	z-index: 1000;
	display: flex;
	align-items: flex-end;
	
	.comment-modal-content {
		width: 100%;
		background: #FFFFFF;
		border-radius: 20rpx 20rpx 0 0;
		padding: 40rpx;
		
		.modal-header {
			display: flex;
			align-items: center;
			justify-content: space-between;
			margin-bottom: 30rpx;
			
			.modal-title {
				font-family: 'Alibaba PuHuiTi';
				font-weight: 500;
				font-size: 32rpx;
				line-height: 44rpx;
				color: #333333;
			}
			
			.modal-close {
				font-size: 48rpx;
				color: #757575;
			}
		}
		
		.comment-textarea {
			width: 100%;
			min-height: 200rpx;
			background: #F5F5F5;
			border-radius: 10rpx;
			padding: 20rpx;
			font-family: 'Alibaba PuHuiTi';
			font-size: 28rpx;
			line-height: 40rpx;
			color: #333333;
			border: none;
			outline: none;
			resize: none;
			margin-bottom: 30rpx;
		}
		
		.modal-footer {
			display: flex;
			align-items: center;
			justify-content: space-between;
			
			.char-count {
				font-family: 'Alibaba PuHuiTi';
				font-size: 24rpx;
				line-height: 32rpx;
				color: #757575;
			}
			
			.send-btn {
				width: 120rpx;
				height: 60rpx;
				background: #E0E0E0;
				border-radius: 30rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				
				&.active {
					background: #8A70C9;
					
					.send-text {
						color: #FFFFFF;
					}
				}
				
				.send-text {
					font-family: 'Alibaba PuHuiTi';
					font-size: 28rpx;
					line-height: 38rpx;
					color: #AAAAAA;
				}
			}
		}
	}
}
</style>
