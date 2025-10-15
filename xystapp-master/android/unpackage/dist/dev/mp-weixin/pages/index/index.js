"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      activeTab: "follow",
      messageCount: 1,
      currentUserIndex: 0,
      // swiper显示的第一个item的索引
      userDescriptions: ["羽毛球场", "网球场", "篮球场", "足球场", "游泳馆", "健身房", "乒乓球室", "台球厅"],
      users: [
        { id: 1, name: "小明", avatar: "/static/关注页/follow-users-section/Ellipse 2.png" },
        { id: 2, name: "小红", avatar: "/static/关注页/follow-users-section/Ellipse 2 (1).png" },
        { id: 3, name: "小李", avatar: "/static/关注页/follow-users-section/Ellipse 9.png" },
        { id: 4, name: "小张", avatar: "/static/关注页/follow-users-section/Ellipse 11.png" },
        { id: 5, name: "小王", avatar: "/static/关注页/follow-users-section/Ellipse 13.png" },
        { id: 6, name: "小赵", avatar: "/static/关注页/follow-users-section/Ellipse 15.png" },
        { id: 7, name: "小陈", avatar: "/static/关注页/follow-users-section/Ellipse 2.png" },
        { id: 8, name: "小刘", avatar: "/static/关注页/follow-users-section/Ellipse 9.png" }
      ],
      banners: [
        {
          image: "/static/关注页/banner.png",
          description: "这是一个羽毛球爱好者的聚集地这是一个羽毛球测一下长度我们测测到底最长能写到哪里所以我们的最多字数就是这么多了我觉得够了"
        }
      ],
      posts: [
        {
          userAvatar: "/static/images/user1.jpg",
          username: "用户名称我起长一点",
          time: "昨天 20:15",
          title: "帖子大标题我也写长一点",
          content: '测试一下最长的长度在这里我们可以轻松自定义主题背景与装扮，打造专属兴趣空间，轻松找到同好社群，感受如 "家园" 般的沉浸互动氛围；实时获取全校各类活动动态，支持一键报名与专属提',
          images: ["/static/images/post1.jpg", "/static/images/post2.jpg", "/static/images/post3.jpg"],
          tags: ["某某场"],
          likes: 125,
          stars: 16,
          comments: 20,
          isLiked: false,
          isStarred: false,
          isFollowed: false,
          expanded: false
        },
        {
          userAvatar: "/static/images/user2.jpg",
          username: "用户名字",
          time: "昨天 20:15",
          title: "帖子大标题我也写长一点",
          content: "短一点",
          images: [],
          tags: ["某某场"],
          likes: 125,
          stars: 16,
          comments: 20,
          isLiked: false,
          isStarred: false,
          isFollowed: true,
          expanded: false
        }
      ]
    };
  },
  computed: {
    // 计算中间位置的索引（显示5个时，中间是第2个，索引为2）
    centerIndex() {
      return Math.floor(7 / 2);
    },
    // 获取当前中间显示的用户
    currentUser() {
      return this.users[this.currentUserIndex] || this.users[0];
    }
  },
  onLoad() {
    this.checkLoginStatus();
  },
  methods: {
    getUserItemClass(index) {
      const displayItems = 7;
      const centerPosition = Math.floor(displayItems / 2);
      const totalUsers = this.users.length;
      const currentSwiperIndex = this.currentUserIndex;
      let positionInWindow = index - currentSwiperIndex;
      if (positionInWindow < 0) {
        positionInWindow += totalUsers;
      }
      if (positionInWindow >= displayItems) {
        positionInWindow = positionInWindow % totalUsers;
        if (positionInWindow >= displayItems) {
          positionInWindow = displayItems - 1;
        }
      }
      const distanceFromCenter = Math.abs(positionInWindow - centerPosition);
      common_vendor.index.__f__("log", "at pages/index/index.vue:260", `用户${index}: swiperIndex=${currentSwiperIndex}, positionInWindow=${positionInWindow}, centerPosition=${centerPosition}, distance=${distanceFromCenter}`);
      if (distanceFromCenter === 0) {
        common_vendor.index.__f__("log", "at pages/index/index.vue:264", `用户${index}: 设置为center (最清晰)`);
        return "center";
      } else if (distanceFromCenter === 1) {
        common_vendor.index.__f__("log", "at pages/index/index.vue:267", `用户${index}: 设置为near`);
        return "near";
      } else if (distanceFromCenter === 2) {
        common_vendor.index.__f__("log", "at pages/index/index.vue:270", `用户${index}: 设置为far`);
        return "far";
      } else {
        common_vendor.index.__f__("log", "at pages/index/index.vue:273", `用户${index}: 设置为farthest`);
        return "farthest";
      }
    },
    getUserItemStyle(index) {
      const displayItems = 7;
      const centerPosition = Math.floor(displayItems / 2);
      const totalUsers = this.users.length;
      const currentSwiperIndex = this.currentUserIndex;
      let positionInWindow = index - currentSwiperIndex;
      if (positionInWindow < 0) {
        positionInWindow += totalUsers;
      }
      if (positionInWindow >= displayItems) {
        positionInWindow = positionInWindow % totalUsers;
        if (positionInWindow >= displayItems) {
          positionInWindow = displayItems - 1;
        }
      }
      const distanceFromCenter = Math.abs(positionInWindow - centerPosition);
      const zIndex = 100 - distanceFromCenter * 10;
      common_vendor.index.__f__("log", "at pages/index/index.vue:307", `用户${index}: positionInWindow=${positionInWindow}, zIndex=${zIndex}`);
      return {
        zIndex
      };
    },
    onImageError(e) {
      common_vendor.index.__f__("error", "at pages/index/index.vue:314", "图片加载失败:", e.detail.errMsg);
      common_vendor.index.__f__("log", "at pages/index/index.vue:315", "失败的图片路径:", e.target.src);
    },
    onImageLoad(e) {
      common_vendor.index.__f__("log", "at pages/index/index.vue:318", "图片加载成功:", e.target.src);
    },
    checkLoginStatus() {
      const token = common_vendor.index.getStorageSync("userToken");
      if (!token) {
        common_vendor.index.reLaunch({
          url: "/pages/login/login"
        });
      }
    },
    onUserSwiperChange(e) {
      this.currentUserIndex = e.detail.current;
    },
    switchTab(tab) {
      this.activeTab = tab;
    },
    toggleFollow(index) {
      this.posts[index].isFollowed = !this.posts[index].isFollowed;
    },
    expandPost(index) {
      this.posts[index].expanded = true;
    },
    toggleLike(index) {
      this.posts[index].isLiked = !this.posts[index].isLiked;
      if (this.posts[index].isLiked) {
        this.posts[index].likes++;
      } else {
        this.posts[index].likes--;
      }
    },
    toggleStar(index) {
      this.posts[index].isStarred = !this.posts[index].isStarred;
      if (this.posts[index].isStarred) {
        this.posts[index].stars++;
      } else {
        this.posts[index].stars--;
      }
    },
    showComments(index) {
      common_vendor.index.showToast({
        title: "查看评论",
        icon: "none"
      });
    },
    previewImage(current, urls) {
      common_vendor.index.previewImage({
        current,
        urls
      });
    },
    goToPage(page) {
      if (page === "index") {
        return;
      }
      const routes = {
        club: "/pages/club/club",
        message: "/pages/message/message",
        publish: "/pages/publish/publish",
        profile: "/pages/profile/profile"
      };
      if (routes[page]) {
        common_vendor.index.navigateTo({
          url: routes[page]
        });
      }
    },
    goToSearch() {
      common_vendor.index.showToast({
        title: "搜索功能",
        icon: "none"
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_assets._imports_0$1,
    b: $data.activeTab === "follow" ? 1 : "",
    c: common_vendor.o(($event) => $options.switchTab("follow")),
    d: $data.activeTab === "discover" ? 1 : "",
    e: common_vendor.o(($event) => $options.switchTab("discover")),
    f: $data.activeTab === "activity" ? 1 : "",
    g: common_vendor.o(($event) => $options.switchTab("activity")),
    h: common_assets._imports_1$1,
    i: common_vendor.o((...args) => $options.goToSearch && $options.goToSearch(...args)),
    j: common_vendor.f($data.users, (user, index, i0) => {
      return {
        a: user.avatar,
        b: common_vendor.o((...args) => $options.onImageError && $options.onImageError(...args), user.id),
        c: common_vendor.o((...args) => $options.onImageLoad && $options.onImageLoad(...args), user.id),
        d: common_vendor.t(user.name),
        e: common_vendor.n($options.getUserItemClass(index)),
        f: common_vendor.s($options.getUserItemStyle(index)),
        g: user.id
      };
    }),
    k: common_vendor.o((...args) => $options.onUserSwiperChange && $options.onUserSwiperChange(...args)),
    l: common_vendor.f($data.banners, (banner, index, i0) => {
      return {
        a: banner.image,
        b: common_vendor.t(banner.description),
        c: index
      };
    }),
    m: common_vendor.f($data.posts, (post, index, i0) => {
      return common_vendor.e({
        a: post.userAvatar,
        b: common_vendor.t(post.username),
        c: common_vendor.t(post.time),
        d: !post.isFollowed
      }, !post.isFollowed ? {} : {}, {
        e: common_vendor.t(post.isFollowed ? "已关注" : "关注"),
        f: post.isFollowed ? 1 : "",
        g: common_vendor.o(($event) => $options.toggleFollow(index), index),
        h: common_vendor.t(post.title),
        i: common_vendor.t(post.content),
        j: post.expanded ? 1 : "",
        k: post.content.length > 100 && !post.expanded
      }, post.content.length > 100 && !post.expanded ? {
        l: common_vendor.o(($event) => $options.expandPost(index), index)
      } : {}, {
        m: post.images && post.images.length > 0
      }, post.images && post.images.length > 0 ? {
        n: common_vendor.f(post.images, (img, imgIndex, i1) => {
          return {
            a: imgIndex,
            b: img,
            c: common_vendor.o(($event) => $options.previewImage(img, post.images), imgIndex)
          };
        })
      } : {}, {
        o: post.tags && post.tags.length > 0
      }, post.tags && post.tags.length > 0 ? {
        p: common_vendor.f(post.tags, (tag, tagIndex, i1) => {
          return {
            a: common_vendor.t(tag),
            b: tagIndex
          };
        })
      } : {}, {
        q: post.isLiked ? 1 : "",
        r: common_vendor.t(post.likes),
        s: common_vendor.o(($event) => $options.toggleLike(index), index),
        t: post.isStarred ? 1 : "",
        v: common_vendor.t(post.stars),
        w: common_vendor.o(($event) => $options.toggleStar(index), index),
        x: common_vendor.t(post.comments),
        y: common_vendor.o(($event) => $options.showComments(index), index),
        z: index
      });
    }),
    n: common_vendor.o(($event) => $options.goToPage("index")),
    o: common_vendor.o(($event) => $options.goToPage("club")),
    p: common_vendor.o(($event) => $options.goToPage("publish")),
    q: $data.messageCount > 0
  }, $data.messageCount > 0 ? {
    r: common_vendor.t($data.messageCount)
  } : {}, {
    s: common_vendor.o(($event) => $options.goToPage("message")),
    t: common_vendor.o(($event) => $options.goToPage("profile"))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-1cf27b2a"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
