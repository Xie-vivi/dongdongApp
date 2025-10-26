<template>
	<view class="members-container">
		<!-- 顶部导航栏 -->
		<TopNavigation 
			title="群聊成员" 
			:showBack="true" 
			:showMore="false"
		/>
		
		<!-- 搜索框 -->
		<view class="search-section">
			<view class="search-box">
				<input 
					v-model="searchKeyword" 
					class="search-input" 
					placeholder="搜索成员" 
					placeholder-style="color: #999999"
					@input="onSearch"
				/>
			</view>
			<view class="search-btn" @click="handleSearch">
				<text class="search-btn-text">搜索</text>
			</view>
		</view>
		
		<!-- 成员列表 -->
		<view class="members-list">
			<!-- 场主、场管理分组 -->
			<view class="member-group">
				<text class="group-title">场主、场管理（3人）</text>
				<view class="member-item" v-for="admin in admins" :key="admin.id" @click="viewMember(admin)">
					<image :src="admin.avatar" class="member-avatar"></image>
					<text class="member-name admin-name">{{ admin.name }}</text>
				</view>
			</view>
			
			<!-- 按字母分组 -->
			<view class="member-group" v-for="(group, letter) in groupedMembers" :key="letter">
				<text class="group-title">{{ letter }}（{{ group.length }}人）</text>
				<view class="member-item" v-for="member in group" :key="member.id" @click="viewMember(member)">
					<image :src="member.avatar" class="member-avatar"></image>
					<text class="member-name">{{ member.name }}</text>
				</view>
			</view>
		</view>
		
		<!-- 字母索引 -->
		<view class="alphabet-index">
			<text class="index-item" v-for="letter in alphabet" :key="letter" @click="scrollToLetter(letter)">
				{{ letter }}
			</text>
		</view>
	</view>
</template>

<script>
import TopNavigation from '@/components/TopNavigation.vue'

export default {
	name: 'GroupMembers',
	components: {
		TopNavigation
	},
	data() {
		return {
			searchKeyword: '',
			admins: [
				{
					id: 1,
					name: '会吃西瓜的小鸭纸',
					avatar: '/static/关注页/follow-users-section/Ellipse 2.png',
					role: 'owner'
				},
				{
					id: 2,
					name: '伊苏尔德的狗',
					avatar: '/static/关注页/follow-users-section/Ellipse 11.png',
					role: 'admin'
				},
				{
					id: 3,
					name: '剑圣绝活哥',
					avatar: '/static/关注页/follow-users-section/Ellipse 14.png',
					role: 'admin'
				}
			],
			members: [
				{
					id: 4,
					name: 'AAA猪饲料批发',
					avatar: '/static/关注页/follow-users-section/Ellipse 15.png',
					initial: 'A'
				},
				{
					id: 5,
					name: 'cc果粒真好喝',
					avatar: '/static/关注页/follow-users-section/Ellipse 16.png',
					initial: 'C'
				},
				{
					id: 6,
					name: 'CAO是氧化钙',
					avatar: '/static/关注页/follow-users-section/Ellipse 13.png',
					initial: 'C'
				}
			],
			alphabet: 'ABCDEFGHIJKLMNOPQRSTUVWXYZ#'.split('')
		}
	},
	computed: {
		groupedMembers() {
			const grouped = {}
			this.members.forEach(member => {
				const initial = member.initial || '#'
				if (!grouped[initial]) {
					grouped[initial] = []
				}
				grouped[initial].push(member)
			})
			return grouped
		}
	},
	methods: {
		onSearch(e) {
			this.searchKeyword = e.detail.value
		},
		handleSearch() {
			if (this.searchKeyword.trim()) {
				uni.showToast({
					title: '搜索：' + this.searchKeyword,
					icon: 'none'
				})
			}
		},
		viewMember(member) {
			uni.showToast({
				title: '查看成员：' + member.name,
				icon: 'none'
			})
		},
		scrollToLetter(letter) {
			// 滚动到对应字母的位置
			console.log('滚动到:', letter)
		}
	}
}
</script>

<style scoped>
.members-container {
	min-height: 100vh;
	background: #F6F2FC;
	position: relative;
}

/* 搜索区域 */
.search-section {
	display: flex;
	align-items: center;
	padding: 20rpx 30rpx;
	background: #F6F2FC;
}

.search-box {
	flex: 1;
	height: 70rpx;
	background: #FFFFFF;
	border-radius: 35rpx;
	padding: 0 30rpx;
	margin-right: 20rpx;
}

.search-input {
	width: 100%;
	height: 100%;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-size: 28rpx;
	color: #333333;
}

.search-btn {
	width: 120rpx;
	height: 70rpx;
	background: #8A70C9;
	border-radius: 35rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.search-btn-text {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	color: #FFFFFF;
}

/* 成员列表 */
.members-list {
	background: #FFFFFF;
	padding-bottom: 40rpx;
}

.member-group {
	margin-bottom: 40rpx;
}

.group-title {
	display: block;
	padding: 20rpx 40rpx;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 28rpx;
	line-height: 38rpx;
	color: #757575;
	background: #F6F2FC;
}

.member-item {
	display: flex;
	align-items: center;
	padding: 20rpx 40rpx;
	border-bottom: 1rpx solid #F5F5F5;
}

.member-avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	margin-right: 24rpx;
}

.member-name {
	flex: 1;
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 32rpx;
	line-height: 44rpx;
	color: #333333;
}

.admin-name {
	color: #8A70C9;
}

/* 字母索引 */
.alphabet-index {
	position: fixed;
	right: 10rpx;
	top: 50%;
	transform: translateY(-50%);
	display: flex;
	flex-direction: column;
	align-items: center;
	z-index: 100;
}

.index-item {
	font-family: 'Alibaba PuHuiTi', sans-serif;
	font-weight: 400;
	font-size: 20rpx;
	line-height: 28rpx;
	color: #8A70C9;
	padding: 4rpx 8rpx;
}
</style>

