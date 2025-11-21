<template>
  <view class="event-detail">
    <!-- 顶部背景图片 -->
    <view class="header-bg" :class="{ 'hidden': showTopNav }">
      <image class="bg-image" src="/static/follow/Rectangle 230.png" mode="aspectFill" />
      <!-- <view class="bg-overlay"></view> -->
      
      <!-- 返回按钮 -->
      <view class="back-btn" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      
      <!-- 更多按钮 -->
      <view class="more-btn">
        <text class="more-icon">⋯</text>
      </view>
    </view>

    <!-- 顶部导航栏（滚动时显示活动标题） -->
    <view class="top-nav" :class="{ 'show': showTopNav }">
      <view class="nav-content">
        <view class="nav-back" @click="goBack">
          <text class="nav-back-icon">‹</text>
        </view>
        
        <!-- 活动标题 -->
        <view class="nav-title">
          <text class="nav-title-text">阴阳师线下交流会：阴阳师校园集结令...</text>
        </view>
        
        <view class="nav-more">
          <text class="nav-more-icon">⋯</text>
        </view>
      </view>
    </view>

    <!-- 主要内容 -->
    <scroll-view 
      class="content-scroll" 
      scroll-y 
      @scroll="onScroll"
      :scroll-top="scrollTop"
    >
      <!-- 基本信息卡片 -->
      <view class="info-card">
        <view class="event-title">
          阴阳师线下交流会：阴阳师校园集结令！这场平安京邀约，等你来赴
        </view>
        
         <!-- 时间和感兴趣信息 -->
         <view class="time-interest-row">
           <view class="time-item">
             <image class="time-icon" src="/static/activity-detail/1.png" />
             <text class="time-text">2025.8.16</text>
             <text class="time-text">周六</text>
             <text class="time-text">17:00 - 18:00</text>
           </view>
           <view class="interest-info">
             <image class="heart-icon" src="/static/heart-fill.png" />
             <text class="interest-text">23人感兴趣</text>
           </view>
         </view>
         
         <!-- 地点信息 -->
         <view class="location-info">
           <image class="location-icon" src="/static/activity-detail/2.png" />
           <text class="location-text">中央民族大学 | 体育馆2楼</text>
         </view>
      </view>
      
      <!-- 选项卡切换（滑动时或切换到非简介tab时显示） -->
      <view class="content-tabs" :class="{ 'show': showTopNav || activeTab !== 'intro' }">
        <view 
          class="content-tab" 
          :class="{ 'active': activeTab === 'intro' }"
          @click="switchTab('intro')"
        >
          简介
        </view>
        <view 
          class="content-tab" 
          :class="{ 'active': activeTab === 'signup' }"
          @click="switchTab('signup')"
        >
          报名
        </view>
        <view 
          class="content-tab" 
          :class="{ 'active': activeTab === 'comment' }"
          @click="switchTab('comment')"
        >
          评论
        </view>
      </view>

      <!-- 活动简介区域 -->
      <view class="intro-section" v-if="activeTab === 'intro'">
        <view class="section-title">活动简介</view>
        
        <!-- 发起人信息 -->
        <view class="organizer-card">
          <image class="organizer-avatar" src="/static/follow/follow-users-section/Ellipse 9.png" />
          <view class="organizer-info">
            <text class="organizer-name">阴阳师场</text>
            <view class="organizer-stats">
              <text class="stat-text">100关注</text>
              <text class="stat-text">15帖子</text>
            </view>
          </view>
          <view class="enter-btn">
            <text class="enter-text">进场</text>
          </view>
        </view>
        
        <!-- 活动发起人 -->
        <view class="creator-info">
          <image class="creator-avatar" src="/static/follow/follow-users-section/Ellipse 2.png" />
          <text class="creator-name">我名字叫活动发起人</text>
        </view>
        
        <!-- 活动详细描述 -->
        <view class="event-description">
          <text class="description-text">
            平安京的风，吹进你的校园啦！阴阳师校园专属活动即将开启，四大趣味玩法带你沉浸式玩转式神世界，还有限定好礼拿到手软！
          </text>
          <text class="description-text">
            想证明自己是"资深痒痒鼠"？阴阳师知识赛等你来战！从式神技能到剧情伏笔，答对就能赢取式神主题周边；想化身心仪式神？cosplay 打卡开启，穿式神服装拍校园美照，集赞兑换专属纪念礼，还原度高还能拿额外奖励；热衷指尖对决？式神技能赛上线，操控本命式神同台竞技，勾玉、金币等游戏道具轻松赢；爱创作的你别错过同人创作赛，漫画、小说、绘画均可投稿，优秀作品不仅校内展览，还可能被制成周边！
          </text>
          <text class="description-text">
            快约上同校寮友，一起在校园里解锁平安京的专属快乐，这场属于阴阳师玩家的校园狂欢，就等你加入！
          </text>
          <text class="description-text">
            快约上同校寮友，一起在校园里解锁平安京的专属快乐，这场属于阴阳师玩家的校园狂欢，就等你加入！
          </text>
          <text class="description-text">
            快约上同校寮友，一起在校园里解锁平安京的专属快乐，这场属于阴阳师玩家的校园狂欢，就等你加入！
          </text>
          <text class="description-text">
            快约上同校寮友，一起在校园里解锁平安京的专属快乐，这场属于阴阳师玩家的校园狂欢，就等你加入！
          </text>
          <text class="description-text">
            快约上同校寮友，一起在校园里解锁平安京的专属快乐，这场属于阴阳师玩家的校园狂欢，就等你加入！
          </text>
          <text class="description-text">
            快约上同校寮友，一起在校园里解锁平安京的专属快乐，这场属于阴阳师玩家的校园狂欢，就等你加入！
          </text>
        </view>
      </view>

      <!-- 报名区域 -->
      <view class="signup-section" v-if="activeTab === 'signup'" :style="{ paddingTop: (showTopNav || activeTab !== 'intro') ? '70px' : '20px' }">
        <view class="signup-header">
          <text class="signup-title">已报名7人</text>
          <view class="view-all">
            <text class="view-all-text">全部</text>
            <text class="view-all-arrow">></text>
          </view>
        </view>
        
        <view class="signup-users">
          <view class="user-item" v-for="(user, index) in signupUsers" :key="index">
            <image class="user-avatar" :src="user.avatar" />
            <view class="user-plus" v-if="user.showPlus">+</view>
            <text class="user-name">{{ user.name }}</text>
          </view>
        </view>
      </view>

      <!-- 评论区域 -->
      <view class="comment-section" v-if="activeTab === 'comment'" :style="{ paddingTop: (showTopNav || activeTab !== 'intro') ? '70px' : '20px' }">
        <view class="comment-header">
          <text class="comment-title">评论15</text>
          <view class="sort-options">
            <text class="sort-text">热度最高</text>
            <view class="sort-icon">⋯</view>
            <text class="sort-by-heat">按热度</text>
          </view>
        </view>
        
        <view class="comment-list">
          <view class="comment-item" v-for="(comment, index) in comments" :key="index">
            <image class="comment-avatar" :src="comment.avatar" />
            <view class="comment-content">
              <view class="comment-user">
                <text class="comment-username">{{ comment.username }}</text>
                <view class="comment-likes">
                  <image class="like-icon" src="/static/heart-fill.png" />
                  <text class="like-count">{{ comment.likes }}</text>
                </view>
              </view>
              <text class="comment-text">{{ comment.content }}</text>
              <text class="comment-mention" v-if="comment.mention">{{ comment.mention }}</text>
              <view class="comment-meta">
                <text class="comment-time">{{ comment.time }}</text>
                <text class="comment-location">{{ comment.location }}</text>
                <text class="comment-reply">回复</text>
              </view>
              
              <!-- 回复列表 -->
              <view class="replies" v-if="comment.replies && comment.replies.length > 0">
                <view class="reply-item" v-for="(reply, replyIndex) in comment.replies" :key="replyIndex">
                  <text class="reply-text">{{ reply.content }}</text>
                </view>
                <text class="view-replies" v-if="comment.totalReplies > comment.replies.length">
                  查看共{{ comment.totalReplies }}条回复
                </text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="action-left">
        <view class="action-item">
          <image class="action-icon-img" src="/static/activity-detail/4.png" />
          <text class="action-text">群聊</text>
        </view>
        <view class="action-item" @click="switchToComment">
          <image class="action-icon-img" src="/static/activity-detail/5.png" />
          <text class="action-text">评论</text>
        </view>
        <view class="action-item" @click="switchToSignup">
          <image class="action-icon-img" src="/static/activity-detail/3.png" />
          <text class="signup-count">已报名7人</text>
        </view>
      </view>
      
      <view class="signup-btn" @click="signupEvent">
        <text class="signup-btn-text">报名活动</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'EventDetail',
  data() {
    return {
      showTopNav: false,
      activeTab: 'intro',
      scrollTop: 0,
      signupUsers: [
        { avatar: '/static/follow/follow-users-section/Ellipse 9.png', name: '路人1', showPlus: true },
        { avatar: '/static/follow/follow-users-section/Ellipse 2.png', name: '好友1', showPlus: false },
        { avatar: '/static/follow/follow-users-section/Ellipse 9.png', name: '名字长...', showPlus: true },
        { avatar: '/static/follow/follow-users-section/Ellipse 2.png', name: '四个字的', showPlus: true },
        { avatar: '/static/follow/follow-users-section/Ellipse 9.png', name: '好友2', showPlus: false },
        { avatar: '/static/follow/follow-users-section/Ellipse 2.png', name: '好友3', showPlus: false },
        { avatar: '/static/follow/follow-users-section/Ellipse 9.png', name: '更多...', showPlus: false }
      ],
      comments: [
        {
          avatar: '/static/follow/follow-users-section/Ellipse 9.png',
          username: '我是评论人机',
          likes: 125,
          content: '试试看评论是什么样的呢感觉还有意思的可以多写点看看效果怎么样。^^',
          mention: '@会吃西瓜的小鸭纸',
          time: '1小时前',
          location: '北京',
          replies: [
            { content: '我是评论人机的评论人机：没错我就是来测试一下评论的评论怎么样的' },
            { content: '我不是人机：怎么全是测试评论的人机？' }
          ],
          totalReplies: 8
        },
        {
          avatar: '/static/follow/follow-users-section/Ellipse 2.png',
          username: '我是评论人机2号',
          likes: 15,
          content: '我也试试看评论是什么样的呢感觉还挺有意思的可以多写点看看效果怎么样',
          mention: '@不会吃西瓜的小鸭纸',
          time: '2小时前',
          location: '北京',
          replies: [
            { content: '我是评论人机的评论人机2：没错我就是来测试一下评论的评论怎么样的' },
            { content: '我不是人机2：怎么全是测试评论的人机？' }
          ],
          totalReplies: 3
        }
      ]
    }
  },
  methods: {
    onScroll(e) {
      const scrollTop = e.detail.scrollTop;
      this.showTopNav = scrollTop > 150;
    },
    
    switchTab(tab) {
      this.activeTab = tab;
    },
    
    goBack() {
      uni.navigateBack();
    },
    
    signupEvent() {
      uni.showToast({
        title: '报名成功',
        icon: 'success'
      });
    },
    
    switchToComment() {
      this.activeTab = 'comment';
    },
    
    switchToSignup() {
      this.activeTab = 'signup';
    }
  }
}
</script>

<style scoped>
.event-detail {
  position: relative;
  width: 100%;
  min-height: 100vh;
  background: #F6F2FC;
}

/* 顶部背景图片 */
.header-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 200px;
  z-index: 1;
  transition: all 0.3s ease;
}

.header-bg.hidden {
  transform: translateY(-200px);
  opacity: 0;
}

.bg-image {
  width: 100%;
  height: 100%;
}

.bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(246, 242, 252, 0.3), rgba(246, 242, 252, 0.8));
}

.back-btn {
  position: absolute;
  top: 53px;
  left: 20px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
}

.back-icon {
  color: #FFFFFF;
  font-size: 24px;
  font-weight: bold;
}

.more-btn {
  position: absolute;
  top: 53px;
  right: 20px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
}

.more-icon {
  color: #FFFFFF;
  font-size: 18px;
  font-weight: bold;
}

/* 顶部导航栏 */
.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 90px;
  background: rgba(246, 242, 252, 0.95);
  backdrop-filter: blur(10px);
  z-index: 10;
  transform: translateY(-90px);
  transition: all 0.3s ease;
}

.top-nav.show {
  transform: translateY(0);
}

.nav-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
  padding-top: 30px;
}

.nav-back {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-back-icon {
  color: #333333;
  font-size: 24px;
  font-weight: bold;
}

.nav-title {
  flex: 1;
  text-align: center;
  padding: 0 20px;
}

.nav-title-text {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 16px;
  font-weight: bold;
  color: #333333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.nav-more {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-more-icon {
  color: #333333;
  font-size: 18px;
  font-weight: bold;
}

 /* 内容滚动区域 */
 .content-scroll {
   position: fixed;
   top: 140px; /* 图片底部往上一点的位置 */
   left: 0;
   right: 0;
   bottom: 0;
   width: 100%;
   z-index: 2; /* 在图片上方 */
   background-color: #f5f5f5;
   border-radius: 20px 20px 0 0; /* 顶部圆角 */
   box-sizing: border-box;
   padding-top: 0;
 }

/* 基本信息卡片 */
.info-card {
  width: 100%;
  background: #FFFFFF;
  border-radius: 20px 20px 0 0;
  padding: 20px 20px 10px 20px;
  margin: 0;
  box-sizing: border-box;
}

 .event-title {
   font-family: 'Alibaba PuHuiTi', sans-serif;
   font-size: 16px;
   font-weight: bold;
   line-height: 25px;
   color: #333333;
   margin-bottom: 20px;
 }

 .time-interest-row {
   display: flex;
   align-items: center;
   justify-content: space-between;
   margin-bottom: 15px;
 }
 
 .time-item {
   display: flex;
   align-items: center;
   gap: 8px;
 }
 
 .time-icon {
   width: 16px;
   height: 16px;
 }
 
 .time-text {
   font-family: 'Alibaba PuHuiTi', sans-serif;
   font-size: 12px;
   color: #333333;
 }
 
 .location-info {
   display: flex;
   align-items: center;
   gap: 8px;
   margin-bottom: 15px;
 }
 
 .location-icon {
   width: 16px;
   height: 16px;
 }
 
 .location-text {
   font-family: 'Alibaba PuHuiTi', sans-serif;
   font-size: 12px;
   color: #333333;
 }
 
 .interest-info {
   display: flex;
   flex-direction: column;
   align-items: center;
   gap: 4px;
 }
 
 .interest-text {
   font-family: 'Alibaba PuHuiTi', sans-serif;
   font-size: 12px;
   color: #757575;
 }
 
 .heart-icon {
   width: 20px;
   height: 20px;
 }
 
 /* 内容区域选项卡样式 */
 .content-tabs {
   display: flex;
   background-color: #ffffff;
   padding: 0 20px;
   border-bottom: 1px solid #f0f0f0;
   opacity: 0;
   transform: translateY(-20px);
   transition: all 0.3s ease;
   pointer-events: none;
   position: fixed;
   top: 90px; /* 在top-nav下方 */
   left: 0;
   right: 0;
   z-index: 9; /* 低于top-nav的z-index */
   box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
 }
 
 .content-tabs.show {
   opacity: 1;
   transform: translateY(0);
   pointer-events: auto;
 }
 
 .content-tab {
   flex: 1;
   text-align: center;
   padding: 15px 0;
   font-family: 'Alibaba PuHuiTi', sans-serif;
   font-size: 14px;
   color: #999999;
   position: relative;
   cursor: pointer;
 }
 
 .content-tab.active {
   color: #333333;
   font-weight: 500;
 }
 
 .content-tab.active::after {
   content: '';
   position: absolute;
   bottom: 0;
   left: 50%;
   transform: translateX(-50%);
   width: 20px;
   height: 2px;
   background-color: #007AFF;
   border-radius: 1px;
 }

/* 活动简介区域 */
.intro-section {
  width: 100%;
  background: #FFFFFF;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  margin-top: 10px; /* 为top-nav和content-tabs留出空间 */
  box-sizing: border-box;
}

.section-title {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 16px;
  font-weight: 500;
  color: #333333;
  margin-bottom: 20px;
}

.organizer-card {
  display: flex;
  align-items: center;
  background: #F5F5F5;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 20px;
}

.organizer-avatar {
  width: 50px;
  height: 50px;
  border-radius: 25px;
  margin-right: 10px;
}

.organizer-info {
  flex: 1;
}

.organizer-name {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 16px;
  font-weight: 500;
  color: #333333;
  display: block;
  margin-bottom: 5px;
}

.organizer-stats {
  display: flex;
  gap: 10px;
}

.stat-text {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 14px;
  color: #B0B0B0;
}

.enter-btn {
  background: #C1B8E8;
  border-radius: 5px;
  padding: 5px 16px;
}

.enter-text {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 12px;
  color: #333333;
}

.creator-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.creator-avatar {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  margin-right: 10px;
}

.creator-name {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: #333333;
}

.event-description {
  line-height: 25px;
}

.description-text {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 14px;
  color: #333333;
  line-height: 25px;
  display: block;
  margin-bottom: 15px;
}

/* 报名和评论区域 */
.signup-section,
.comment-section {
  width: 100%;
  background: #FFFFFF;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 100px; /* 为底部操作栏留出空间 */
  margin-top: 10px; /* 调整顶部间距 */
  box-sizing: border-box;
}

/* 报名区域样式 */
.signup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.signup-title {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 16px;
  font-weight: 500;
  color: #333333;
}

.view-all {
  display: flex;
  align-items: center;
  gap: 5px;
}

.view-all-text {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 14px;
  color: #666666;
}

.view-all-arrow {
  font-size: 14px;
  color: #666666;
}

.signup-users {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.user-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  position: relative;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 25px;
}

.user-plus {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 16px;
  height: 16px;
  background: #007AFF;
  color: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

.user-name {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 12px;
  color: #333333;
  max-width: 60px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

/* 评论区域样式 */
.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-title {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 16px;
  font-weight: 500;
  color: #333333;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-text {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 14px;
  color: #666666;
}

.sort-icon {
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #666666;
}

.sort-by-heat {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 14px;
  color: #666666;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f5f5f5;
}

.comment-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-user {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-username {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: #333333;
}

.comment-likes {
  display: flex;
  align-items: center;
  gap: 4px;
}

.like-icon {
  width: 16px;
  height: 16px;
}

.like-count {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 14px;
  color: #666666;
}

.comment-text {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 14px;
  color: #333333;
  line-height: 20px;
  margin-bottom: 8px;
  display: block;
}

.comment-mention {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 14px;
  color: #007AFF;
  margin-bottom: 8px;
  display: block;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.comment-time,
.comment-location {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 12px;
  color: #999999;
}

.comment-reply {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 12px;
  color: #007AFF;
  cursor: pointer;
}

.replies {
  background: #f8f8f8;
  border-radius: 8px;
  padding: 12px;
  margin-top: 10px;
}

.reply-item {
  margin-bottom: 8px;
}

.reply-item:last-child {
  margin-bottom: 0;
}

.reply-text {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 13px;
  color: #333333;
  line-height: 18px;
  display: block;
}

.view-replies {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 13px;
  color: #007AFF;
  margin-top: 8px;
  cursor: pointer;
  display: block;
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 80px;
  background: #F9F6FF;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-sizing: border-box;
  z-index: 5;
}

.action-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.action-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.action-icon-img {
  width: 24px;
  height: 24px;
}

.action-text {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 12px;
  color: #333333;
}

.signup-count {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 12px;
  color: #333333;
}

.signup-btn {
  background: #8A70C9;
  border-radius: 10px;
  padding: 10px 30px;
}

.signup-btn-text {
  font-family: 'Alibaba PuHuiTi', sans-serif;
  font-size: 14px;
  color: #FFFFFF;
}
</style>
