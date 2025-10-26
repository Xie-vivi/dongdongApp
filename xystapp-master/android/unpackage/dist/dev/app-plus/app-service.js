if (typeof Promise !== "undefined" && !Promise.prototype.finally) {
  Promise.prototype.finally = function(callback) {
    const promise = this.constructor;
    return this.then(
      (value) => promise.resolve(callback()).then(() => value),
      (reason) => promise.resolve(callback()).then(() => {
        throw reason;
      })
    );
  };
}
;
if (typeof uni !== "undefined" && uni && uni.requireGlobal) {
  const global = uni.requireGlobal();
  ArrayBuffer = global.ArrayBuffer;
  Int8Array = global.Int8Array;
  Uint8Array = global.Uint8Array;
  Uint8ClampedArray = global.Uint8ClampedArray;
  Int16Array = global.Int16Array;
  Uint16Array = global.Uint16Array;
  Int32Array = global.Int32Array;
  Uint32Array = global.Uint32Array;
  Float32Array = global.Float32Array;
  Float64Array = global.Float64Array;
  BigInt64Array = global.BigInt64Array;
  BigUint64Array = global.BigUint64Array;
}
;
if (uni.restoreGlobal) {
  uni.restoreGlobal(Vue, weex, plus, setTimeout, clearTimeout, setInterval, clearInterval);
}
(function(vue) {
  "use strict";
  function formatAppLog(type, filename, ...args) {
    if (uni.__log__) {
      uni.__log__(type, filename, ...args);
    } else {
      console[type].apply(console, [...args, filename]);
    }
  }
  const _imports_0$8 = "/static/关注页/daohang/1.png";
  const _imports_0$7 = "/static/关注页/daohang/2.png";
  const _imports_2$7 = "/static/关注页/daohang/3.png";
  const _export_sfc = (sfc, props) => {
    const target = sfc.__vccOpts || sfc;
    for (const [key, val] of props) {
      target[key] = val;
    }
    return target;
  };
  const _sfc_main$p = {
    name: "BottomNavigation",
    props: {
      currentPage: {
        type: String,
        default: "index"
      },
      messageCount: {
        type: Number,
        default: 0
      }
    },
    data() {
      return {
        showActionSheet: false
      };
    },
    methods: {
      showPublishOptions() {
        this.showActionSheet = true;
      },
      hideActionSheet() {
        this.showActionSheet = false;
      },
      handleAction(type) {
        this.hideActionSheet();
        if (type === "post") {
          uni.navigateTo({
            url: "/pages/post/create-post"
          });
        } else if (type === "activity") {
          uni.navigateTo({
            url: "/pages/activity/create-activity"
          });
        }
      },
      goToPage(page) {
        if (page === this.currentPage) {
          return;
        }
        const routes = {
          index: "/pages/index/index",
          club: "/pages/club/club",
          message: "/pages/message/message",
          publish: "/pages/publish/publish",
          profile: "/pages/profile/profile"
        };
        if (routes[page]) {
          if (page === "index") {
            uni.reLaunch({
              url: routes[page]
            });
          } else {
            uni.navigateTo({
              url: routes[page]
            });
          }
        }
      }
    }
  };
  function _sfc_render$p(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", null, [
      vue.createCommentVNode(" 遮罩层 "),
      $data.showActionSheet ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 0,
        class: "action-mask",
        onClick: _cache[0] || (_cache[0] = (...args) => $options.hideActionSheet && $options.hideActionSheet(...args))
      })) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" 底部导航 "),
      vue.createElementVNode("view", { class: "footer" }, [
        vue.createElementVNode("view", {
          class: "footer-item",
          onClick: _cache[1] || (_cache[1] = ($event) => $options.goToPage("index"))
        }, [
          vue.createElementVNode("image", {
            class: "footer-icon",
            src: _imports_0$8
          }),
          vue.createElementVNode("text", { class: "footer-text" }, "首页")
        ]),
        vue.createElementVNode("view", {
          class: "footer-item",
          onClick: _cache[2] || (_cache[2] = ($event) => $options.goToPage("club"))
        }, [
          vue.createElementVNode("image", {
            class: "footer-icon",
            src: _imports_0$7
          }),
          vue.createElementVNode("text", { class: "footer-text" }, "社团")
        ]),
        vue.createElementVNode("view", {
          class: "footer-item publish-btn",
          onClick: _cache[3] || (_cache[3] = (...args) => $options.showPublishOptions && $options.showPublishOptions(...args))
        }, [
          vue.createElementVNode("view", { class: "publish-icon" }, [
            vue.createElementVNode("view", { class: "plus-h" }),
            vue.createElementVNode("view", { class: "plus-v" })
          ])
        ]),
        vue.createElementVNode("view", {
          class: "footer-item",
          onClick: _cache[4] || (_cache[4] = ($event) => $options.goToPage("message"))
        }, [
          vue.createElementVNode("view", { class: "footer-icon message-icon-wrapper" }, [
            vue.createElementVNode("image", {
              class: "footer-icon",
              src: _imports_0$7
            }),
            $props.messageCount > 0 ? (vue.openBlock(), vue.createElementBlock("view", {
              key: 0,
              class: "message-badge"
            }, [
              vue.createElementVNode(
                "text",
                { class: "badge-text" },
                vue.toDisplayString($props.messageCount),
                1
                /* TEXT */
              )
            ])) : vue.createCommentVNode("v-if", true)
          ]),
          vue.createElementVNode("text", { class: "footer-text" }, "消息")
        ]),
        vue.createElementVNode("view", {
          class: "footer-item",
          onClick: _cache[5] || (_cache[5] = ($event) => $options.goToPage("profile"))
        }, [
          vue.createElementVNode("image", {
            class: "footer-icon",
            src: _imports_2$7
          }),
          vue.createElementVNode("text", { class: "footer-text" }, "我的")
        ])
      ]),
      vue.createCommentVNode(" 发布操作弹窗 "),
      $data.showActionSheet ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 1,
        class: "action-sheet"
      }, [
        vue.createElementVNode("view", {
          class: "action-item",
          onClick: _cache[6] || (_cache[6] = ($event) => $options.handleAction("post"))
        }, [
          vue.createElementVNode("text", { class: "action-text" }, "发帖")
        ]),
        vue.createElementVNode("view", {
          class: "action-item",
          onClick: _cache[7] || (_cache[7] = ($event) => $options.handleAction("activity"))
        }, [
          vue.createElementVNode("text", { class: "action-text" }, "创建活动")
        ])
      ])) : vue.createCommentVNode("v-if", true)
    ]);
  }
  const BottomNavigation = /* @__PURE__ */ _export_sfc(_sfc_main$p, [["render", _sfc_render$p], ["__scopeId", "data-v-c65ea04b"], ["__file", "D:/code space2/xystapp/android/components/BottomNavigation.vue"]]);
  const _imports_3$4 = "/static/关注页/menu-01.png";
  const _imports_1$8 = "/static/关注页/发现.png";
  const _imports_2$6 = "/static/发现页/1.png";
  const _imports_3$3 = "/static/关注页/1.png";
  const _imports_0$6 = "/static/关注页/5.png";
  const _imports_1$7 = "/static/关注页/6.png";
  const _imports_2$5 = "/static/关注页/7.png";
  const _imports_7$1 = "/static/关注页/2.png";
  const _imports_8$1 = "/static/关注页/3.png";
  const _imports_9 = "/static/关注页/4.png";
  const _sfc_main$o = {
    components: {
      BottomNavigation
    },
    data() {
      return {
        activeTab: "follow",
        messageCount: 1,
        currentUserIndex: 0,
        // swiper显示的第一个item的索引
        userDescriptions: ["羽毛球场", "网球场", "篮球场", "足球场", "游泳馆", "健身房", "乒乓球室", "台球厅"],
        selectedCategory: "all",
        // 活动分类选择
        selectedDiscoverCategory: "recommend",
        // 发现页分类选择
        selectedActivityCategory: "recommend",
        // 活动页分类选择
        // 发现页用户数据
        discoverUsers: [
          { avatar: "/static/关注页/follow-users-section/Ellipse 2.png", label: "无名场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 9.png", label: "乒乓球场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 11.png", label: "羽毛球场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 13.png", label: "动漫场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 14.png", label: "xx场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 2.png", label: "篮球场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 9.png", label: "音乐场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 11.png", label: "摄影场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 13.png", label: "游戏场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 14.png", label: "读书场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 2.png", label: "美食场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 9.png", label: "旅行场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 11.png", label: "健身场" },
          { avatar: "/static/关注页/follow-users-section/Ellipse 13.png", label: "学习场" }
        ],
        // 发现页活动卡片数据
        discoverActivities: [
          {
            title: "羽毛球初级训练找人：1v1或2v2都可",
            date: "2025.8.16",
            day: "周六",
            time: "17:00 - 18:00",
            location: "体育馆2楼",
            tag: "羽毛球场",
            participants: 5,
            likes: 21,
            image: "/static/发现页/2.png"
          },
          {
            title: "篮球约战：3v3半场比赛",
            date: "2025.8.17",
            day: "周日",
            time: "15:00 - 17:00",
            location: "室外篮球场",
            tag: "篮球场",
            participants: 8,
            likes: 35,
            image: "/static/发现页/2.png"
          },
          {
            title: "乒乓球技术交流会",
            date: "2025.8.18",
            day: "周一",
            time: "19:00 - 21:00",
            location: "学生活动中心",
            tag: "乒乓球场",
            participants: 12,
            likes: 18,
            image: "/static/发现页/2.png"
          },
          {
            title: "网球双打友谊赛",
            date: "2025.8.19",
            day: "周二",
            time: "16:00 - 18:00",
            location: "网球场A区",
            tag: "网球场",
            participants: 4,
            likes: 28,
            image: "/static/发现页/2.png"
          },
          {
            title: "足球训练营：基础技能提升",
            date: "2025.8.20",
            day: "周三",
            time: "18:30 - 20:30",
            location: "足球场",
            tag: "足球场",
            participants: 15,
            likes: 42,
            image: "/static/发现页/2.png"
          },
          {
            title: "游泳健身约起来",
            date: "2025.8.21",
            day: "周四",
            time: "20:00 - 21:30",
            location: "游泳馆",
            tag: "游泳场",
            participants: 6,
            likes: 15,
            image: "/static/发现页/2.png"
          },
          {
            title: "瑜伽课程体验：初学者友好",
            date: "2025.8.22",
            day: "周五",
            time: "19:00 - 20:00",
            location: "健身房B区",
            tag: "瑜伽馆",
            participants: 10,
            likes: 32,
            image: "/static/发现页/2.png"
          },
          {
            title: "跑步团：晨跑打卡活动",
            date: "2025.8.23",
            day: "周六",
            time: "07:00 - 08:00",
            location: "学校操场",
            tag: "跑步场",
            participants: 20,
            likes: 56,
            image: "/static/发现页/2.png"
          }
        ],
        currentActivityIndex: 0,
        // 活动页面卡片数据
        activityCards: [
          {
            title: "羽毛球初级训练找人：1v1或2v2都可",
            date: "2025.8.16",
            day: "周六",
            time: "17:00 - 18:00",
            location: "体育馆2楼",
            tag: "羽毛球场",
            participants: 5,
            likes: 21,
            image: "/static/发现页/2.png",
            category: "sports"
          },
          {
            title: "明日方舟线下集会",
            date: "2025.8.26",
            day: "周六",
            time: "17:00 - 18:00",
            location: "体育馆1楼",
            tag: "动漫场",
            participants: 30,
            likes: 125,
            image: "/static/发现页/2.png",
            category: "entertainment"
          },
          {
            title: "乒乓球",
            date: "2025.8.26",
            day: "周六",
            time: "17:00 - 18:00",
            location: "体育馆1楼",
            tag: "乒乓球场",
            participants: 7,
            likes: 21,
            image: "/static/发现页/2.png",
            category: "sports"
          },
          {
            title: "羽毛球初级训练找人：1v1或2v2都可",
            date: "2025.8.16",
            day: "周六",
            time: "12:00 - 13:00",
            location: "体育馆2楼",
            tag: "足球场",
            participants: 11,
            likes: 22,
            image: "/static/发现页/2.png",
            category: "sports"
          },
          {
            title: "编程学习交流会",
            date: "2025.8.20",
            day: "周三",
            time: "19:00 - 21:00",
            location: "图书馆3楼",
            tag: "学习场",
            participants: 15,
            likes: 45,
            image: "/static/发现页/2.png",
            category: "study"
          },
          {
            title: "摄影技巧分享",
            date: "2025.8.18",
            day: "周一",
            time: "14:00 - 16:00",
            location: "艺术楼1楼",
            tag: "摄影场",
            participants: 12,
            likes: 33,
            image: "/static/发现页/2.png",
            category: "life"
          }
        ],
        // 发现页面数据
        hotClubs: [
          { name: "羽毛球社", avatar: "/static/关注页/follow-users-section/Ellipse 2.png", members: 128 },
          { name: "摄影社", avatar: "/static/关注页/follow-users-section/Ellipse 2 (1).png", members: 96 },
          { name: "篮球社", avatar: "/static/关注页/follow-users-section/Ellipse 9.png", members: 156 },
          { name: "音乐社", avatar: "/static/关注页/follow-users-section/Ellipse 11.png", members: 89 },
          { name: "书法社", avatar: "/static/关注页/follow-users-section/Ellipse 13.png", members: 67 }
        ],
        recommendActivities: [
          {
            title: "周末羽毛球友谊赛",
            description: "欢迎各水平球友参加，一起切磋球技",
            time: "本周六 14:00",
            location: "体育馆",
            image: "/static/关注页/banner/banner1.png"
          },
          {
            title: "摄影作品分享会",
            description: "分享摄影技巧，展示优秀作品",
            time: "下周日 10:00",
            location: "图书馆",
            image: "/static/关注页/banner/banner2.png"
          }
        ],
        // 活动页面数据
        activityCategories: [
          { id: "all", name: "全部" },
          { id: "sports", name: "运动" },
          { id: "culture", name: "文化" },
          { id: "social", name: "社交" },
          { id: "study", name: "学习" }
        ],
        activities: [
          {
            id: 1,
            title: "羽毛球友谊赛",
            description: "欢迎各水平球友参加，一起切磋球技，结识新朋友",
            time: "2024-01-15 14:00",
            location: "体育馆A场",
            participants: 12,
            category: "sports",
            image: "/static/关注页/banner/banner1.png"
          },
          {
            id: 2,
            title: "书法交流会",
            description: "书法爱好者聚集，互相学习交流书法心得",
            time: "2024-01-16 10:00",
            location: "艺术楼201",
            participants: 8,
            category: "culture",
            image: "/static/关注页/banner/banner2.png"
          },
          {
            id: 3,
            title: "编程技术分享",
            description: "前端开发技术分享，Vue3最新特性解析",
            time: "2024-01-17 19:00",
            location: "计算机楼305",
            participants: 25,
            category: "study",
            image: "/static/关注页/banner/banner1.png"
          }
        ],
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
            userAvatar: "/static/关注页/1.png",
            username: "用户名称我起长一点",
            time: "昨天 20:15",
            title: "帖子大标题我也写长一点",
            content: '测试一下最长的长度在这里我们可以轻松自定义主题背景与装扮，打造专属兴趣空间，轻松找到同好社群，感受如 "家园" 般的沉浸互动氛围；实时获取全校各类活动动态，支持一键报名与专属提',
            images: ["/static/关注页/2.png", "/static/关注页/3.png", "/static/关注页/4.png"],
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
            userAvatar: "/static/关注页/2.png",
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
          },
          {
            userAvatar: "/static/关注页/3.png",
            username: "第三个用户",
            time: "2小时前",
            title: "分享一个有趣的活动",
            content: "今天参加了学校的社团活动，认识了很多志同道合的朋友，大家一起讨论学习和生活，感觉特别充实和有意义。",
            images: ["/static/images/post1.jpg"],
            tags: ["社团活动"],
            likes: 89,
            stars: 12,
            comments: 15,
            isLiked: true,
            isStarred: false,
            isFollowed: false,
            expanded: false
          },
          {
            userAvatar: "/static/关注页/4.png",
            username: "第四个用户",
            time: "5小时前",
            title: "学习心得分享",
            content: "最近在学习新的技术栈，虽然过程中遇到了不少困难，但是通过不断的练习和实践，终于有了一些收获和体会。",
            images: [],
            tags: ["学习分享"],
            likes: 67,
            stars: 8,
            comments: 12,
            isLiked: false,
            isStarred: true,
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
      },
      // 根据分类过滤活动
      filteredActivities() {
        if (this.selectedCategory === "all") {
          return this.activities;
        }
        return this.activities.filter((activity) => activity.category === this.selectedCategory);
      },
      // 根据分类过滤活动卡片
      filteredActivityCards() {
        if (this.selectedActivityCategory === "recommend") {
          return this.activityCards;
        }
        return this.activityCards.filter((activity) => activity.category === this.selectedActivityCategory);
      }
    },
    onLoad() {
      this.checkLoginStatus();
    },
    methods: {
      // 活动轮播切换事件
      onActivitySwiperChange(e) {
        this.currentActivityIndex = e.detail.current;
      },
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
        formatAppLog("log", "at pages/index/index.vue:805", `用户${index}: swiperIndex=${currentSwiperIndex}, positionInWindow=${positionInWindow}, centerPosition=${centerPosition}, distance=${distanceFromCenter}`);
        if (distanceFromCenter === 0) {
          formatAppLog("log", "at pages/index/index.vue:809", `用户${index}: 设置为center (最清晰)`);
          return "center";
        } else if (distanceFromCenter === 1) {
          formatAppLog("log", "at pages/index/index.vue:812", `用户${index}: 设置为near`);
          return "near";
        } else if (distanceFromCenter === 2) {
          formatAppLog("log", "at pages/index/index.vue:815", `用户${index}: 设置为far`);
          return "far";
        } else {
          formatAppLog("log", "at pages/index/index.vue:818", `用户${index}: 设置为farthest`);
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
        positionInWindow = positionInWindow % displayItems;
        if (positionInWindow < 0) {
          positionInWindow += displayItems;
        }
        const distanceFromCenter = Math.abs(positionInWindow - centerPosition);
        const zIndex = 200 - distanceFromCenter * 20;
        formatAppLog("log", "at pages/index/index.vue:858", `用户${index}: positionInWindow=${positionInWindow}, distanceFromCenter=${distanceFromCenter}, zIndex=${zIndex}`);
        return {
          zIndex
        };
      },
      onImageError(e) {
        formatAppLog("error", "at pages/index/index.vue:865", "图片加载失败:", e.detail.errMsg);
        formatAppLog("log", "at pages/index/index.vue:866", "失败的图片路径:", e.target.src);
      },
      onImageLoad(e) {
        formatAppLog("log", "at pages/index/index.vue:869", "图片加载成功:", e.target.src);
      },
      checkLoginStatus() {
        const token = uni.getStorageSync("userToken");
        if (!token) {
          uni.reLaunch({
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
        this.goToPostDetail(this.posts[index], index);
      },
      goToPostDetail(post, index) {
        uni.navigateTo({
          url: `/pages/post-detail/post-detail?postId=${post.id || index}`
        });
      },
      previewImage(current, urls) {
        uni.previewImage({
          current,
          urls
        });
      },
      goToSearch() {
        uni.showToast({
          title: "搜索功能",
          icon: "none"
        });
      },
      // 活动分类选择
      selectCategory(categoryId) {
        this.selectedCategory = categoryId;
      },
      // 参与活动
      joinActivity(index) {
        const activity = this.filteredActivities[index];
        uni.showToast({
          title: `已报名参加：${activity.title}`,
          icon: "success"
        });
      },
      // 发现页分类选择
      selectDiscoverCategory(categoryId) {
        this.selectedDiscoverCategory = categoryId;
      },
      // 活动页分类选择
      selectActivityCategory(categoryId) {
        this.selectedActivityCategory = categoryId;
      },
      // 跳转到活动详情页
      goToEventDetail(activity) {
        uni.navigateTo({
          url: "/pages/event-detail/event-detail"
        });
      }
    }
  };
  function _sfc_render$o(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_BottomNavigation = vue.resolveComponent("BottomNavigation");
    return vue.openBlock(), vue.createElementBlock("view", { class: "container" }, [
      vue.createCommentVNode(" 顶部状态栏占位 "),
      vue.createElementVNode("view", { class: "status-bar" }),
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createElementVNode("view", { class: "top-nav" }, [
        vue.createElementVNode("view", { class: "nav-left" }, [
          vue.createElementVNode("image", {
            src: _imports_3$4,
            class: "menu-image",
            mode: "aspectFit"
          })
        ]),
        vue.createElementVNode("view", { class: "nav-center" }, [
          vue.createElementVNode("view", { class: "nav-tabs" }, [
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["nav-tab", { "active": $data.activeTab === "follow" }]),
                onClick: _cache[0] || (_cache[0] = ($event) => $options.switchTab("follow"))
              },
              [
                vue.createElementVNode("text", { class: "nav-tab-text" }, "关注")
              ],
              2
              /* CLASS */
            ),
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["nav-tab", { "active": $data.activeTab === "discover" }]),
                onClick: _cache[1] || (_cache[1] = ($event) => $options.switchTab("discover"))
              },
              [
                vue.createElementVNode("text", { class: "nav-tab-text" }, "发现")
              ],
              2
              /* CLASS */
            ),
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["nav-tab", { "active": $data.activeTab === "activity" }]),
                onClick: _cache[2] || (_cache[2] = ($event) => $options.switchTab("activity"))
              },
              [
                vue.createElementVNode("text", { class: "nav-tab-text" }, "活动")
              ],
              2
              /* CLASS */
            )
          ])
        ]),
        vue.createElementVNode("view", {
          class: "nav-right",
          onClick: _cache[3] || (_cache[3] = (...args) => $options.goToSearch && $options.goToSearch(...args))
        }, [
          vue.createElementVNode("image", {
            src: _imports_1$8,
            class: "search-image",
            mode: "aspectFit"
          })
        ])
      ]),
      vue.createCommentVNode(" 内容区域 - 根据activeTab显示不同内容 "),
      vue.createCommentVNode(" 关注tab内容 "),
      $data.activeTab === "follow" ? (vue.openBlock(), vue.createElementBlock("view", { key: 0 }, [
        vue.createCommentVNode(" 关注用户头像区域 "),
        vue.createElementVNode("view", { class: "follow-users-section" }, [
          vue.createElementVNode(
            "swiper",
            {
              class: "follow-users-swiper",
              onChange: _cache[6] || (_cache[6] = (...args) => $options.onUserSwiperChange && $options.onUserSwiperChange(...args)),
              "display-multiple-items": 7,
              "previous-margin": "10rpx",
              "next-margin": "10rpx",
              circular: true
            },
            [
              (vue.openBlock(true), vue.createElementBlock(
                vue.Fragment,
                null,
                vue.renderList($data.users, (user, index) => {
                  return vue.openBlock(), vue.createElementBlock(
                    "swiper-item",
                    {
                      key: user.id,
                      style: vue.normalizeStyle($options.getUserItemStyle(index))
                    },
                    [
                      vue.createElementVNode(
                        "view",
                        {
                          class: vue.normalizeClass(["user-item", $options.getUserItemClass(index)])
                        },
                        [
                          vue.createElementVNode("view", { class: "user-avatar-container" }, [
                            vue.createElementVNode("image", {
                              class: "user-avatar-img",
                              src: user.avatar,
                              mode: "aspectFill",
                              onError: _cache[4] || (_cache[4] = (...args) => $options.onImageError && $options.onImageError(...args)),
                              onLoad: _cache[5] || (_cache[5] = (...args) => $options.onImageLoad && $options.onImageLoad(...args))
                            }, null, 40, ["src"])
                          ]),
                          vue.createElementVNode(
                            "text",
                            { class: "user-name" },
                            vue.toDisplayString(user.name),
                            1
                            /* TEXT */
                          )
                        ],
                        2
                        /* CLASS */
                      )
                    ],
                    4
                    /* STYLE */
                  );
                }),
                128
                /* KEYED_FRAGMENT */
              ))
            ],
            32
            /* NEED_HYDRATION */
          )
        ]),
        vue.createCommentVNode(" 轮播图区域 "),
        vue.createElementVNode("view", { class: "banner-section" }, [
          vue.createElementVNode("swiper", {
            class: "banner-swiper",
            "indicator-dots": true,
            autoplay: true,
            interval: 3e3,
            duration: 500,
            "indicator-color": "rgba(255,255,255,0.5)",
            "indicator-active-color": "#FFFFFF"
          }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.banners, (banner, index) => {
                return vue.openBlock(), vue.createElementBlock("swiper-item", { key: index }, [
                  vue.createElementVNode("view", { class: "banner-item" }, [
                    vue.createElementVNode("image", {
                      class: "banner-bg",
                      src: banner.image,
                      mode: "aspectFill"
                    }, null, 8, ["src"]),
                    vue.createElementVNode("view", { class: "banner-content" }, [
                      vue.createElementVNode("view", { class: "title-section" }, [
                        vue.createElementVNode("text", { class: "main-title" }, "羽毛球场")
                      ]),
                      vue.createElementVNode(
                        "text",
                        { class: "banner-desc" },
                        vue.toDisplayString(banner.description),
                        1
                        /* TEXT */
                      )
                    ])
                  ])
                ]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ])
      ])) : $data.activeTab === "discover" ? (vue.openBlock(), vue.createElementBlock(
        vue.Fragment,
        { key: 1 },
        [
          vue.createCommentVNode(" 发现tab内容 "),
          vue.createElementVNode("view", null, [
            vue.createCommentVNode(" 分类标签 "),
            vue.createElementVNode("view", { class: "discover-categories" }, [
              vue.createElementVNode("scroll-view", {
                class: "categories-scroll-horizontal",
                "scroll-x": "true",
                "show-scrollbar": "false"
              }, [
                vue.createElementVNode("view", { class: "categories-wrapper" }, [
                  vue.createElementVNode(
                    "view",
                    {
                      class: vue.normalizeClass(["category-tag", { active: $data.selectedDiscoverCategory === "recommend" }]),
                      onClick: _cache[7] || (_cache[7] = ($event) => $options.selectDiscoverCategory("recommend"))
                    },
                    [
                      vue.createElementVNode("text", { class: "category-tag-text" }, "推荐")
                    ],
                    2
                    /* CLASS */
                  ),
                  vue.createElementVNode("view", {
                    class: "category-tag",
                    onClick: _cache[8] || (_cache[8] = ($event) => $options.selectDiscoverCategory("study"))
                  }, [
                    vue.createElementVNode("text", { class: "category-tag-text" }, "学习")
                  ]),
                  vue.createElementVNode("view", {
                    class: "category-tag",
                    onClick: _cache[9] || (_cache[9] = ($event) => $options.selectDiscoverCategory("sports"))
                  }, [
                    vue.createElementVNode("text", { class: "category-tag-text" }, "体育")
                  ]),
                  vue.createElementVNode("view", {
                    class: "category-tag",
                    onClick: _cache[10] || (_cache[10] = ($event) => $options.selectDiscoverCategory("entertainment"))
                  }, [
                    vue.createElementVNode("text", { class: "category-tag-text" }, "娱乐")
                  ]),
                  vue.createElementVNode("view", {
                    class: "category-tag",
                    onClick: _cache[11] || (_cache[11] = ($event) => $options.selectDiscoverCategory("life"))
                  }, [
                    vue.createElementVNode("text", { class: "category-tag-text" }, "生活")
                  ]),
                  vue.createElementVNode("view", {
                    class: "category-tag",
                    onClick: _cache[12] || (_cache[12] = ($event) => $options.selectDiscoverCategory("tech"))
                  }, [
                    vue.createElementVNode("text", { class: "category-tag-text" }, "科技")
                  ]),
                  vue.createElementVNode("view", {
                    class: "category-tag",
                    onClick: _cache[13] || (_cache[13] = ($event) => $options.selectDiscoverCategory("food"))
                  }, [
                    vue.createElementVNode("text", { class: "category-tag-text" }, "美食")
                  ]),
                  vue.createElementVNode("view", {
                    class: "category-tag",
                    onClick: _cache[14] || (_cache[14] = ($event) => $options.selectDiscoverCategory("travel"))
                  }, [
                    vue.createElementVNode("text", { class: "category-tag-text" }, "旅行")
                  ]),
                  vue.createElementVNode("view", {
                    class: "category-tag",
                    onClick: _cache[15] || (_cache[15] = ($event) => $options.selectDiscoverCategory("photography"))
                  }, [
                    vue.createElementVNode("text", { class: "category-tag-text" }, "摄影")
                  ]),
                  vue.createElementVNode("view", {
                    class: "category-tag",
                    onClick: _cache[16] || (_cache[16] = ($event) => $options.selectDiscoverCategory("music"))
                  }, [
                    vue.createElementVNode("text", { class: "category-tag-text" }, "音乐")
                  ]),
                  vue.createElementVNode("view", {
                    class: "category-tag",
                    onClick: _cache[17] || (_cache[17] = ($event) => $options.selectDiscoverCategory("reading"))
                  }, [
                    vue.createElementVNode("text", { class: "category-tag-text" }, "阅读")
                  ]),
                  vue.createElementVNode("view", {
                    class: "category-tag",
                    onClick: _cache[18] || (_cache[18] = ($event) => $options.selectDiscoverCategory("gaming"))
                  }, [
                    vue.createElementVNode("text", { class: "category-tag-text" }, "游戏")
                  ])
                ])
              ])
            ]),
            vue.createCommentVNode(" 关注用户区域 "),
            vue.createElementVNode("view", { class: "discover-follow-users" }, [
              vue.createElementVNode("view", { class: "discover-view-all-fixed" }, [
                vue.createElementVNode("text", { class: "discover-view-all-text" }, "全部"),
                vue.createElementVNode("text", { class: "discover-chevron" }, "›")
              ]),
              vue.createElementVNode("scroll-view", {
                class: "discover-users-scroll",
                "scroll-x": "true",
                "show-scrollbar": "false"
              }, [
                vue.createElementVNode("view", { class: "discover-users-container" }, [
                  (vue.openBlock(true), vue.createElementBlock(
                    vue.Fragment,
                    null,
                    vue.renderList($data.discoverUsers, (user, index) => {
                      return vue.openBlock(), vue.createElementBlock("view", {
                        class: "discover-user-item",
                        key: index
                      }, [
                        vue.createElementVNode("view", { class: "discover-user-avatar-wrapper" }, [
                          vue.createElementVNode("image", {
                            class: "discover-user-avatar",
                            src: user.avatar,
                            mode: "aspectFill"
                          }, null, 8, ["src"]),
                          vue.createElementVNode("view", { class: "discover-user-plus-btn" }, [
                            vue.createElementVNode("text", { class: "discover-plus-icon" }, "+")
                          ])
                        ]),
                        vue.createElementVNode(
                          "text",
                          { class: "discover-user-label" },
                          vue.toDisplayString(user.label),
                          1
                          /* TEXT */
                        )
                      ]);
                    }),
                    128
                    /* KEYED_FRAGMENT */
                  ))
                ])
              ])
            ]),
            vue.createCommentVNode(" 活动卡片轮播 "),
            vue.createElementVNode(
              "swiper",
              {
                class: "discover-activity-swiper",
                "indicator-dots": false,
                autoplay: false,
                interval: 5e3,
                duration: 500,
                onChange: _cache[19] || (_cache[19] = (...args) => $options.onActivitySwiperChange && $options.onActivitySwiperChange(...args))
              },
              [
                (vue.openBlock(true), vue.createElementBlock(
                  vue.Fragment,
                  null,
                  vue.renderList($data.discoverActivities, (activity, index) => {
                    return vue.openBlock(), vue.createElementBlock("swiper-item", { key: index }, [
                      vue.createElementVNode("view", { class: "discover-activity-card" }, [
                        vue.createCommentVNode(" 左侧图片 "),
                        vue.createElementVNode("image", {
                          class: "discover-activity-image",
                          src: activity.image,
                          mode: "aspectFill"
                        }, null, 8, ["src"]),
                        vue.createCommentVNode(" 右侧内容区域 "),
                        vue.createElementVNode("view", { class: "discover-activity-content" }, [
                          vue.createCommentVNode(" 标题和爱心 "),
                          vue.createElementVNode("view", { class: "discover-activity-header" }, [
                            vue.createElementVNode(
                              "text",
                              { class: "discover-activity-title" },
                              vue.toDisplayString(activity.title),
                              1
                              /* TEXT */
                            ),
                            vue.createElementVNode("view", { class: "discover-activity-like" }, [
                              vue.createElementVNode("image", {
                                class: "discover-heart-icon",
                                src: _imports_2$6,
                                mode: "aspectFit"
                              }),
                              vue.createElementVNode(
                                "text",
                                { class: "discover-like-count" },
                                vue.toDisplayString(activity.likes),
                                1
                                /* TEXT */
                              )
                            ])
                          ]),
                          vue.createCommentVNode(" 时间信息 "),
                          vue.createElementVNode("view", { class: "discover-activity-datetime" }, [
                            vue.createElementVNode(
                              "text",
                              { class: "discover-activity-date" },
                              vue.toDisplayString(activity.date),
                              1
                              /* TEXT */
                            ),
                            vue.createElementVNode(
                              "text",
                              { class: "discover-activity-day" },
                              vue.toDisplayString(activity.day),
                              1
                              /* TEXT */
                            ),
                            vue.createElementVNode(
                              "text",
                              { class: "discover-activity-time" },
                              vue.toDisplayString(activity.time),
                              1
                              /* TEXT */
                            )
                          ]),
                          vue.createCommentVNode(" 地点和标签 "),
                          vue.createElementVNode("view", { class: "discover-activity-location-tag" }, [
                            vue.createElementVNode("view", { class: "discover-activity-location-wrapper" }, [
                              vue.createElementVNode(
                                "text",
                                { class: "discover-activity-location" },
                                vue.toDisplayString(activity.location),
                                1
                                /* TEXT */
                              ),
                              vue.createElementVNode("view", { class: "discover-activity-tag" }, [
                                vue.createElementVNode(
                                  "text",
                                  { class: "discover-activity-tag-text" },
                                  vue.toDisplayString(activity.tag),
                                  1
                                  /* TEXT */
                                )
                              ])
                            ]),
                            vue.createElementVNode(
                              "text",
                              { class: "discover-activity-participants" },
                              "已报名：" + vue.toDisplayString(activity.participants) + "人",
                              1
                              /* TEXT */
                            )
                          ])
                        ]),
                        vue.createCommentVNode(" 底部指示点 "),
                        vue.createElementVNode("view", { class: "discover-activity-dots" }, [
                          (vue.openBlock(true), vue.createElementBlock(
                            vue.Fragment,
                            null,
                            vue.renderList($data.discoverActivities, (item, dotIndex) => {
                              return vue.openBlock(), vue.createElementBlock(
                                "view",
                                {
                                  class: vue.normalizeClass(["discover-dot", { active: dotIndex === $data.currentActivityIndex }]),
                                  key: dotIndex
                                },
                                null,
                                2
                                /* CLASS */
                              );
                            }),
                            128
                            /* KEYED_FRAGMENT */
                          ))
                        ])
                      ])
                    ]);
                  }),
                  128
                  /* KEYED_FRAGMENT */
                ))
              ],
              32
              /* NEED_HYDRATION */
            ),
            vue.createCommentVNode(" 帖子内容 "),
            vue.createElementVNode("view", { class: "discover-post" }, [
              vue.createElementVNode("view", { class: "discover-post-header" }, [
                vue.createElementVNode("image", {
                  class: "discover-post-avatar",
                  src: _imports_3$3,
                  mode: "aspectFill"
                }),
                vue.createElementVNode("view", { class: "discover-post-user-info" }, [
                  vue.createElementVNode("text", { class: "discover-post-username" }, "用户名字"),
                  vue.createElementVNode("text", { class: "discover-post-time" }, "昨天 20:15")
                ]),
                vue.createElementVNode("view", { class: "discover-follow-btn" }, [
                  vue.createElementVNode("text", { class: "discover-follow-plus" }, "+"),
                  vue.createElementVNode("text", { class: "discover-follow-text" }, "关注")
                ])
              ]),
              vue.createElementVNode("view", { class: "discover-post-content" }, [
                vue.createElementVNode("text", { class: "discover-post-title" }, "帖子大标题我也写长一点"),
                vue.createElementVNode("text", { class: "discover-post-desc" }, "短一点"),
                vue.createElementVNode("view", { class: "discover-post-tag" }, [
                  vue.createElementVNode("text", { class: "discover-post-tag-text" }, "某某场")
                ])
              ]),
              vue.createElementVNode("view", { class: "discover-post-actions" }, [
                vue.createElementVNode("view", { class: "discover-post-action" }, [
                  vue.createElementVNode("image", {
                    class: "discover-action-icon",
                    src: _imports_0$6
                  }),
                  vue.createElementVNode("text", { class: "discover-action-count" }, "21")
                ]),
                vue.createElementVNode("view", { class: "discover-post-action" }, [
                  vue.createElementVNode("image", {
                    class: "discover-action-icon",
                    src: _imports_1$7
                  }),
                  vue.createElementVNode("text", { class: "discover-action-count" }, "2")
                ]),
                vue.createElementVNode("view", { class: "discover-post-action" }, [
                  vue.createElementVNode("image", {
                    class: "discover-action-icon",
                    src: _imports_2$5
                  }),
                  vue.createElementVNode("text", { class: "discover-action-count" }, "1")
                ])
              ])
            ]),
            vue.createCommentVNode(" 第二个帖子 "),
            vue.createElementVNode("view", { class: "discover-post" }, [
              vue.createElementVNode("view", { class: "discover-post-header" }, [
                vue.createElementVNode("image", {
                  class: "discover-post-avatar",
                  src: _imports_3$3,
                  mode: "aspectFill"
                }),
                vue.createElementVNode("view", { class: "discover-post-user-info" }, [
                  vue.createElementVNode("text", { class: "discover-post-username" }, "用户名称我起长一点"),
                  vue.createElementVNode("text", { class: "discover-post-time" }, "昨天 20:11")
                ]),
                vue.createElementVNode("view", { class: "discover-followed-btn" }, [
                  vue.createElementVNode("text", { class: "discover-followed-text" }, "已关注")
                ])
              ]),
              vue.createElementVNode("view", { class: "discover-post-content" }, [
                vue.createElementVNode("text", { class: "discover-post-title" }, "帖子大标题我也写长一点"),
                vue.createElementVNode("text", { class: "discover-post-desc" }, '测试一下最长的长度在这里我们可以轻松自定义主题背景与装扮，打造专属兴趣空间，轻松找到同好社群，感受如 "家园" 般的沉浸互动氛围；实时获取全校各类活动动态，支持一键报名与专属提'),
                vue.createElementVNode("text", { class: "discover-post-expand" }, "... 全文"),
                vue.createElementVNode("view", { class: "discover-post-images" }, [
                  vue.createElementVNode("image", {
                    class: "discover-post-img",
                    src: _imports_7$1,
                    mode: "aspectFill"
                  }),
                  vue.createElementVNode("image", {
                    class: "discover-post-img",
                    src: _imports_8$1,
                    mode: "aspectFill"
                  }),
                  vue.createElementVNode("image", {
                    class: "discover-post-img",
                    src: _imports_9,
                    mode: "aspectFill"
                  })
                ]),
                vue.createElementVNode("view", { class: "discover-post-tag" }, [
                  vue.createElementVNode("text", { class: "discover-post-tag-text" }, "某某场")
                ])
              ]),
              vue.createElementVNode("view", { class: "discover-post-actions" }, [
                vue.createElementVNode("view", { class: "discover-post-action" }, [
                  vue.createElementVNode("image", {
                    class: "discover-action-icon",
                    src: _imports_0$6
                  }),
                  vue.createElementVNode("text", { class: "discover-action-count" }, "125")
                ]),
                vue.createElementVNode("view", { class: "discover-post-action" }, [
                  vue.createElementVNode("image", {
                    class: "discover-action-icon",
                    src: _imports_1$7
                  }),
                  vue.createElementVNode("text", { class: "discover-action-count" }, "16")
                ]),
                vue.createElementVNode("view", { class: "discover-post-action" }, [
                  vue.createElementVNode("image", {
                    class: "discover-action-icon",
                    src: _imports_2$5
                  }),
                  vue.createElementVNode("text", { class: "discover-action-count" }, "20")
                ])
              ])
            ])
          ])
        ],
        2112
        /* STABLE_FRAGMENT, DEV_ROOT_FRAGMENT */
      )) : $data.activeTab === "activity" ? (vue.openBlock(), vue.createElementBlock(
        vue.Fragment,
        { key: 2 },
        [
          vue.createCommentVNode(" 活动tab内容 "),
          vue.createElementVNode("view", null, [
            vue.createCommentVNode(" 分类标签 - 使用发现页面的样式 "),
            vue.createElementVNode("view", { class: "discover-categories" }, [
              vue.createElementVNode("scroll-view", {
                class: "categories-scroll-horizontal",
                "scroll-x": "true",
                "show-scrollbar": "false"
              }, [
                vue.createElementVNode("view", { class: "categories-wrapper" }, [
                  vue.createElementVNode(
                    "view",
                    {
                      class: vue.normalizeClass(["category-tag", { active: $data.selectedActivityCategory === "recommend" }]),
                      onClick: _cache[20] || (_cache[20] = ($event) => $options.selectActivityCategory("recommend"))
                    },
                    [
                      vue.createElementVNode("text", { class: "category-tag-text" }, "推荐")
                    ],
                    2
                    /* CLASS */
                  ),
                  vue.createElementVNode(
                    "view",
                    {
                      class: vue.normalizeClass(["category-tag", { active: $data.selectedActivityCategory === "study" }]),
                      onClick: _cache[21] || (_cache[21] = ($event) => $options.selectActivityCategory("study"))
                    },
                    [
                      vue.createElementVNode("text", { class: "category-tag-text" }, "学习")
                    ],
                    2
                    /* CLASS */
                  ),
                  vue.createElementVNode(
                    "view",
                    {
                      class: vue.normalizeClass(["category-tag", { active: $data.selectedActivityCategory === "sports" }]),
                      onClick: _cache[22] || (_cache[22] = ($event) => $options.selectActivityCategory("sports"))
                    },
                    [
                      vue.createElementVNode("text", { class: "category-tag-text" }, "体育")
                    ],
                    2
                    /* CLASS */
                  ),
                  vue.createElementVNode(
                    "view",
                    {
                      class: vue.normalizeClass(["category-tag", { active: $data.selectedActivityCategory === "entertainment" }]),
                      onClick: _cache[23] || (_cache[23] = ($event) => $options.selectActivityCategory("entertainment"))
                    },
                    [
                      vue.createElementVNode("text", { class: "category-tag-text" }, "娱乐")
                    ],
                    2
                    /* CLASS */
                  ),
                  vue.createElementVNode(
                    "view",
                    {
                      class: vue.normalizeClass(["category-tag", { active: $data.selectedActivityCategory === "life" }]),
                      onClick: _cache[24] || (_cache[24] = ($event) => $options.selectActivityCategory("life"))
                    },
                    [
                      vue.createElementVNode("text", { class: "category-tag-text" }, "生活")
                    ],
                    2
                    /* CLASS */
                  )
                ])
              ])
            ]),
            vue.createCommentVNode(" 活动卡片列表 "),
            vue.createElementVNode("view", { class: "activity-cards-container" }, [
              (vue.openBlock(true), vue.createElementBlock(
                vue.Fragment,
                null,
                vue.renderList($options.filteredActivityCards, (activity, index) => {
                  return vue.openBlock(), vue.createElementBlock("view", {
                    class: "activity-card-item",
                    key: index,
                    onClick: ($event) => $options.goToEventDetail(activity)
                  }, [
                    vue.createCommentVNode(" 左侧图片 "),
                    vue.createElementVNode("image", {
                      class: "activity-card-image",
                      src: activity.image,
                      mode: "aspectFill"
                    }, null, 8, ["src"]),
                    vue.createCommentVNode(" 右侧内容区域 "),
                    vue.createElementVNode("view", { class: "activity-card-content" }, [
                      vue.createCommentVNode(" 标题和爱心 "),
                      vue.createElementVNode("view", { class: "activity-card-header" }, [
                        vue.createElementVNode(
                          "text",
                          { class: "activity-card-title" },
                          vue.toDisplayString(activity.title),
                          1
                          /* TEXT */
                        ),
                        vue.createElementVNode("view", { class: "activity-card-like" }, [
                          vue.createElementVNode("image", {
                            class: "activity-heart-icon",
                            src: _imports_2$6,
                            mode: "aspectFit"
                          }),
                          vue.createElementVNode(
                            "text",
                            { class: "activity-like-count" },
                            vue.toDisplayString(activity.likes),
                            1
                            /* TEXT */
                          )
                        ])
                      ]),
                      vue.createCommentVNode(" 时间信息 "),
                      vue.createElementVNode("view", { class: "activity-card-datetime" }, [
                        vue.createElementVNode(
                          "text",
                          { class: "activity-card-date" },
                          vue.toDisplayString(activity.date),
                          1
                          /* TEXT */
                        ),
                        vue.createElementVNode(
                          "text",
                          { class: "activity-card-day" },
                          vue.toDisplayString(activity.day),
                          1
                          /* TEXT */
                        ),
                        vue.createElementVNode(
                          "text",
                          { class: "activity-card-time" },
                          vue.toDisplayString(activity.time),
                          1
                          /* TEXT */
                        )
                      ]),
                      vue.createCommentVNode(" 地点和标签 "),
                      vue.createElementVNode("view", { class: "activity-card-location-tag" }, [
                        vue.createElementVNode("view", { class: "activity-card-location-wrapper" }, [
                          vue.createElementVNode(
                            "text",
                            { class: "activity-card-location" },
                            vue.toDisplayString(activity.location),
                            1
                            /* TEXT */
                          ),
                          vue.createElementVNode("view", { class: "activity-card-tag" }, [
                            vue.createElementVNode(
                              "text",
                              { class: "activity-card-tag-text" },
                              vue.toDisplayString(activity.tag),
                              1
                              /* TEXT */
                            )
                          ])
                        ]),
                        vue.createElementVNode(
                          "text",
                          { class: "activity-card-participants" },
                          "已报名：" + vue.toDisplayString(activity.participants) + "人",
                          1
                          /* TEXT */
                        )
                      ])
                    ])
                  ], 8, ["onClick"]);
                }),
                128
                /* KEYED_FRAGMENT */
              ))
            ])
          ])
        ],
        2112
        /* STABLE_FRAGMENT, DEV_ROOT_FRAGMENT */
      )) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" 帖子列表 - 只在关注tab显示 "),
      $data.activeTab === "follow" ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 3,
        class: "posts-section"
      }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($data.posts, (post, index) => {
            return vue.openBlock(), vue.createElementBlock("view", {
              class: "post-card",
              key: index,
              onClick: ($event) => $options.goToPostDetail(post, index)
            }, [
              vue.createCommentVNode(" 用户信息 "),
              vue.createElementVNode("view", { class: "post-header" }, [
                vue.createElementVNode("image", {
                  class: "user-avatar-small",
                  src: post.userAvatar,
                  mode: "aspectFill"
                }, null, 8, ["src"]),
                vue.createElementVNode("view", { class: "user-info" }, [
                  vue.createElementVNode(
                    "text",
                    { class: "username" },
                    vue.toDisplayString(post.username),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "text",
                    { class: "post-time" },
                    vue.toDisplayString(post.time),
                    1
                    /* TEXT */
                  )
                ]),
                vue.createElementVNode("view", {
                  class: vue.normalizeClass(["follow-btn", { "followed": post.isFollowed }]),
                  onClick: vue.withModifiers(($event) => $options.toggleFollow(index), ["stop"])
                }, [
                  !post.isFollowed ? (vue.openBlock(), vue.createElementBlock("view", {
                    key: 0,
                    class: "follow-icon"
                  }, "+")) : vue.createCommentVNode("v-if", true),
                  vue.createElementVNode(
                    "text",
                    { class: "follow-text" },
                    vue.toDisplayString(post.isFollowed ? "已关注" : "关注"),
                    1
                    /* TEXT */
                  )
                ], 10, ["onClick"])
              ]),
              vue.createCommentVNode(" 帖子内容 "),
              vue.createElementVNode("view", { class: "post-content" }, [
                vue.createElementVNode(
                  "text",
                  { class: "post-title" },
                  vue.toDisplayString(post.title),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "text",
                  {
                    class: vue.normalizeClass(["post-text", { "expanded": post.expanded }])
                  },
                  vue.toDisplayString(post.content),
                  3
                  /* TEXT, CLASS */
                ),
                post.content.length > 100 && !post.expanded ? (vue.openBlock(), vue.createElementBlock("text", {
                  key: 0,
                  class: "expand-btn",
                  onClick: vue.withModifiers(($event) => $options.expandPost(index), ["stop"])
                }, "... 全文", 8, ["onClick"])) : vue.createCommentVNode("v-if", true)
              ]),
              vue.createCommentVNode(" 帖子图片 "),
              post.images && post.images.length > 0 ? (vue.openBlock(), vue.createElementBlock("view", {
                key: 0,
                class: "post-images"
              }, [
                (vue.openBlock(true), vue.createElementBlock(
                  vue.Fragment,
                  null,
                  vue.renderList(post.images, (img, imgIndex) => {
                    return vue.openBlock(), vue.createElementBlock("image", {
                      key: imgIndex,
                      class: "post-image",
                      src: img,
                      mode: "aspectFill",
                      onClick: vue.withModifiers(($event) => $options.previewImage(img, post.images), ["stop"])
                    }, null, 8, ["src", "onClick"]);
                  }),
                  128
                  /* KEYED_FRAGMENT */
                ))
              ])) : vue.createCommentVNode("v-if", true),
              vue.createCommentVNode(" 标签 "),
              post.tags && post.tags.length > 0 ? (vue.openBlock(), vue.createElementBlock("view", {
                key: 1,
                class: "post-tags"
              }, [
                (vue.openBlock(true), vue.createElementBlock(
                  vue.Fragment,
                  null,
                  vue.renderList(post.tags, (tag, tagIndex) => {
                    return vue.openBlock(), vue.createElementBlock("view", {
                      class: "tag",
                      key: tagIndex
                    }, [
                      vue.createElementVNode(
                        "text",
                        { class: "tag-text" },
                        vue.toDisplayString(tag),
                        1
                        /* TEXT */
                      )
                    ]);
                  }),
                  128
                  /* KEYED_FRAGMENT */
                ))
              ])) : vue.createCommentVNode("v-if", true),
              vue.createCommentVNode(" 互动区域 "),
              vue.createElementVNode("view", { class: "post-actions" }, [
                vue.createElementVNode("view", {
                  class: "action-item",
                  onClick: vue.withModifiers(($event) => $options.toggleLike(index), ["stop"])
                }, [
                  vue.createElementVNode(
                    "image",
                    {
                      class: vue.normalizeClass(["action-icon", { "liked": post.isLiked }]),
                      src: _imports_0$6
                    },
                    null,
                    2
                    /* CLASS */
                  ),
                  vue.createElementVNode(
                    "text",
                    { class: "action-count" },
                    vue.toDisplayString(post.likes),
                    1
                    /* TEXT */
                  )
                ], 8, ["onClick"]),
                vue.createElementVNode("view", {
                  class: "action-item",
                  onClick: vue.withModifiers(($event) => $options.toggleStar(index), ["stop"])
                }, [
                  vue.createElementVNode(
                    "image",
                    {
                      class: vue.normalizeClass(["action-icon", { "starred": post.isStarred }]),
                      src: _imports_1$7
                    },
                    null,
                    2
                    /* CLASS */
                  ),
                  vue.createElementVNode(
                    "text",
                    { class: "action-count" },
                    vue.toDisplayString(post.stars),
                    1
                    /* TEXT */
                  )
                ], 8, ["onClick"]),
                vue.createElementVNode("view", {
                  class: "action-item",
                  onClick: vue.withModifiers(($event) => $options.showComments(index), ["stop"])
                }, [
                  vue.createElementVNode("image", {
                    class: "action-icon",
                    src: _imports_2$5
                  }),
                  vue.createElementVNode(
                    "text",
                    { class: "action-count" },
                    vue.toDisplayString(post.comments),
                    1
                    /* TEXT */
                  )
                ], 8, ["onClick"])
              ])
            ], 8, ["onClick"]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ])) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" 底部导航栏 "),
      vue.createVNode(_component_BottomNavigation, {
        currentPage: "index",
        messageCount: $data.messageCount
      }, null, 8, ["messageCount"])
    ]);
  }
  const PagesIndexIndex = /* @__PURE__ */ _export_sfc(_sfc_main$o, [["render", _sfc_render$o], ["__scopeId", "data-v-1cf27b2a"], ["__file", "D:/code space2/xystapp/android/pages/index/index.vue"]]);
  const _imports_0$5 = "/static/type10/logo.png";
  const _imports_1$6 = "/static/type10/weixin.png";
  const _imports_3$2 = "/static/type10/qq.png";
  const _sfc_main$n = {
    data() {
      return {
        isLoading: false
      };
    },
    onLoad() {
      formatAppLog("log", "at pages/login/login.vue:60", "登录页面加载完成");
    },
    methods: {
      // 本机号码一键登录
      async handlePhoneLogin() {
        this.isLoading = true;
        try {
          await this.simulateLogin("phone");
          uni.setStorageSync("userToken", "phone_login_token");
          uni.setStorageSync("loginType", "phone");
          uni.showToast({
            title: "登录成功",
            icon: "success"
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index"
            });
          }, 1500);
        } catch (error) {
          uni.showToast({
            title: "登录失败，请重试",
            icon: "none"
          });
        } finally {
          this.isLoading = false;
        }
      },
      // 手机验证码登录
      handleSmsLogin() {
        uni.navigateTo({
          url: "/pages/phone-login/phone-login"
        });
      },
      // 注册账号
      handleRegister() {
        uni.navigateTo({
          url: "/pages/register/register"
        });
      },
      // 微信登录
      async loginWithWechat() {
        this.isLoading = true;
        try {
          await this.simulateLogin("wechat");
          uni.setStorageSync("userToken", "wechat_login_token");
          uni.setStorageSync("loginType", "wechat");
          uni.showToast({
            title: "微信登录成功",
            icon: "success"
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index"
            });
          }, 1500);
        } catch (error) {
          uni.showToast({
            title: "微信登录失败",
            icon: "none"
          });
        } finally {
          this.isLoading = false;
        }
      },
      // QQ登录
      async loginWithQQ() {
        this.isLoading = true;
        try {
          await this.simulateLogin("qq");
          uni.setStorageSync("userToken", "qq_login_token");
          uni.setStorageSync("loginType", "qq");
          uni.showToast({
            title: "QQ登录成功",
            icon: "success"
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index"
            });
          }, 1500);
        } catch (error) {
          uni.showToast({
            title: "QQ登录失败",
            icon: "none"
          });
        } finally {
          this.isLoading = false;
        }
      },
      // 模拟登录请求
      simulateLogin(type) {
        return new Promise((resolve, reject) => {
          setTimeout(() => {
            if (Math.random() > 0.1) {
              resolve({ type, success: true });
            } else {
              reject(new Error("登录失败"));
            }
          }, 1500);
        });
      }
    }
  };
  function _sfc_render$n(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "login-container" }, [
      vue.createCommentVNode(" Logo区域 "),
      vue.createElementVNode("view", { class: "logo-section" }, [
        vue.createElementVNode("image", {
          class: "logo",
          src: _imports_0$5,
          mode: "aspectFit"
        })
      ]),
      vue.createCommentVNode(" 品牌标题 "),
      vue.createElementVNode("view", { class: "brand-section" }, [
        vue.createElementVNode("text", { class: "brand-title" }, "咚 咚"),
        vue.createElementVNode("text", { class: "brand-desc" }, "一个社团交流场地")
      ]),
      vue.createCommentVNode(" 登录卡片 "),
      vue.createElementVNode("view", { class: "login-card" }, [
        vue.createCommentVNode(" 本机号码一键登录 "),
        vue.createElementVNode("button", {
          class: "login-btn primary-btn",
          onClick: _cache[0] || (_cache[0] = (...args) => $options.handlePhoneLogin && $options.handlePhoneLogin(...args))
        }, " 本机号码一键登录 "),
        vue.createCommentVNode(" 手机验证码登录 "),
        vue.createElementVNode("button", {
          class: "login-btn secondary-btn",
          onClick: _cache[1] || (_cache[1] = (...args) => $options.handleSmsLogin && $options.handleSmsLogin(...args))
        }, " 手机验证码登录 "),
        vue.createCommentVNode(" 注册账号 "),
        vue.createElementVNode("button", {
          class: "login-btn secondary-btn",
          onClick: _cache[2] || (_cache[2] = (...args) => $options.handleRegister && $options.handleRegister(...args))
        }, " 注册账号 ")
      ]),
      vue.createCommentVNode(" 第三方登录 "),
      vue.createElementVNode("view", { class: "third-party-section" }, [
        vue.createElementVNode("view", { class: "divider-section" }, [
          vue.createElementVNode("view", { class: "divider-line" }),
          vue.createElementVNode("text", { class: "divider-text" }, "第三方登录"),
          vue.createElementVNode("view", { class: "divider-line" })
        ]),
        vue.createElementVNode("view", { class: "social-login" }, [
          vue.createElementVNode("view", {
            class: "social-btn wechat",
            onClick: _cache[3] || (_cache[3] = (...args) => $options.loginWithWechat && $options.loginWithWechat(...args))
          }, [
            vue.createElementVNode("image", {
              class: "social-icon",
              src: _imports_1$6,
              mode: "aspectFit"
            })
          ]),
          vue.createElementVNode("view", {
            class: "social-btn qq",
            onClick: _cache[4] || (_cache[4] = (...args) => $options.loginWithQQ && $options.loginWithQQ(...args))
          }, [
            vue.createElementVNode("image", {
              class: "social-icon",
              src: _imports_3$2,
              mode: "aspectFit"
            })
          ])
        ])
      ])
    ]);
  }
  const PagesLoginLogin = /* @__PURE__ */ _export_sfc(_sfc_main$n, [["render", _sfc_render$n], ["__scopeId", "data-v-e4e4508d"], ["__file", "D:/code space2/xystapp/android/pages/login/login.vue"]]);
  const _imports_0$4 = "/static/images/avatar.png";
  const _sfc_main$m = {
    data() {
      return {};
    },
    methods: {
      handleMenuClick(type) {
        uni.showToast({
          title: `点击了${type}`,
          icon: "none"
        });
      },
      logout() {
        uni.showModal({
          title: "确认退出",
          content: "确定要退出登录吗？",
          success: (res) => {
            if (res.confirm) {
              uni.removeStorageSync("userToken");
              uni.reLaunch({
                url: "/pages/login/login"
              });
              uni.showToast({
                title: "已退出登录",
                icon: "success"
              });
            }
          }
        });
      }
    }
  };
  function _sfc_render$m(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "container" }, [
      vue.createCommentVNode(" 用户信息卡片 "),
      vue.createElementVNode("view", { class: "user-card" }, [
        vue.createElementVNode("view", { class: "avatar" }, [
          vue.createElementVNode("image", {
            src: _imports_0$4,
            mode: "aspectFill"
          })
        ]),
        vue.createElementVNode("view", { class: "user-info" }, [
          vue.createElementVNode("text", { class: "username" }, "用户名"),
          vue.createElementVNode("text", { class: "user-desc" }, "欢迎使用东东社团")
        ])
      ]),
      vue.createCommentVNode(" 功能菜单 "),
      vue.createElementVNode("view", { class: "menu-section" }, [
        vue.createElementVNode("view", {
          class: "menu-item",
          onClick: _cache[0] || (_cache[0] = ($event) => $options.handleMenuClick("settings"))
        }, [
          vue.createElementVNode("view", { class: "menu-icon" }, "⚙️"),
          vue.createElementVNode("text", { class: "menu-text" }, "设置"),
          vue.createElementVNode("text", { class: "menu-arrow" }, ">")
        ]),
        vue.createElementVNode("view", {
          class: "menu-item",
          onClick: _cache[1] || (_cache[1] = ($event) => $options.handleMenuClick("about"))
        }, [
          vue.createElementVNode("view", { class: "menu-icon" }, "ℹ️"),
          vue.createElementVNode("text", { class: "menu-text" }, "关于我们"),
          vue.createElementVNode("text", { class: "menu-arrow" }, ">")
        ]),
        vue.createElementVNode("view", {
          class: "menu-item",
          onClick: _cache[2] || (_cache[2] = ($event) => $options.handleMenuClick("feedback"))
        }, [
          vue.createElementVNode("view", { class: "menu-icon" }, "💬"),
          vue.createElementVNode("text", { class: "menu-text" }, "意见反馈"),
          vue.createElementVNode("text", { class: "menu-arrow" }, ">")
        ])
      ]),
      vue.createCommentVNode(" 退出登录按钮 "),
      vue.createElementVNode("view", { class: "logout-section" }, [
        vue.createElementVNode("button", {
          class: "logout-btn",
          onClick: _cache[3] || (_cache[3] = (...args) => $options.logout && $options.logout(...args))
        }, "退出登录")
      ])
    ]);
  }
  const PagesProfileProfile = /* @__PURE__ */ _export_sfc(_sfc_main$m, [["render", _sfc_render$m], ["__scopeId", "data-v-dd383ca2"], ["__file", "D:/code space2/xystapp/android/pages/profile/profile.vue"]]);
  const _imports_2$4 = "/static/type10/phone.png";
  const _sfc_main$l = {
    data() {
      return {
        phoneNumber: "",
        verificationCode: "",
        countdown: 0,
        timer: null,
        isLoading: false
      };
    },
    computed: {
      // 是否可以发送验证码
      canSendCode() {
        return this.phoneNumber.length === 11 && /^1[3-9]\d{9}$/.test(this.phoneNumber);
      },
      // 是否可以登录
      canLogin() {
        return this.canSendCode && this.verificationCode.length === 6;
      }
    },
    onLoad() {
      formatAppLog("log", "at pages/phone-login/phone-login.vue:118", "手机验证码登录页面加载完成");
    },
    onUnload() {
      if (this.timer) {
        clearInterval(this.timer);
      }
    },
    methods: {
      // 手机号输入处理
      onPhoneInput(e) {
        this.phoneNumber = e.detail.value;
      },
      // 发送验证码
      async sendVerificationCode() {
        if (!this.canSendCode) {
          uni.showToast({
            title: "请输入正确的手机号",
            icon: "none"
          });
          return;
        }
        try {
          uni.showLoading({
            title: "发送中..."
          });
          await this.simulateSendCode();
          uni.hideLoading();
          uni.showToast({
            title: "验证码已发送",
            icon: "success"
          });
          this.startCountdown();
        } catch (error) {
          uni.hideLoading();
          uni.showToast({
            title: "发送失败，请重试",
            icon: "none"
          });
        }
      },
      // 开始倒计时
      startCountdown() {
        this.countdown = 60;
        this.timer = setInterval(() => {
          this.countdown--;
          if (this.countdown <= 0) {
            clearInterval(this.timer);
            this.timer = null;
          }
        }, 1e3);
      },
      // 登录处理
      async handleLogin() {
        if (!this.canLogin) {
          uni.showToast({
            title: "请填写完整信息",
            icon: "none"
          });
          return;
        }
        this.isLoading = true;
        try {
          uni.showLoading({
            title: "登录中..."
          });
          await this.simulateLogin();
          uni.setStorageSync("userToken", "phone_sms_login_token");
          uni.setStorageSync("loginType", "phone_sms");
          uni.setStorageSync("userPhone", this.phoneNumber);
          uni.hideLoading();
          uni.showToast({
            title: "登录成功",
            icon: "success"
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index"
            });
          }, 1500);
        } catch (error) {
          uni.hideLoading();
          uni.showToast({
            title: error.message || "登录失败，请重试",
            icon: "none"
          });
        } finally {
          this.isLoading = false;
        }
      },
      // 注册账号
      handleRegister() {
        uni.navigateTo({
          url: "/pages/register/register"
        });
      },
      // 切换密码登录
      handleSwitchLogin() {
        uni.navigateTo({
          url: "/pages/password-login/password-login"
        });
      },
      // 微信登录
      async loginWithWechat() {
        try {
          uni.showLoading({
            title: "微信登录中..."
          });
          await this.simulateThirdPartyLogin("wechat");
          uni.setStorageSync("userToken", "wechat_login_token");
          uni.setStorageSync("loginType", "wechat");
          uni.hideLoading();
          uni.showToast({
            title: "微信登录成功",
            icon: "success"
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index"
            });
          }, 1500);
        } catch (error) {
          uni.hideLoading();
          uni.showToast({
            title: "微信登录失败",
            icon: "none"
          });
        }
      },
      // 本机号码登录
      async loginWithPhone() {
        try {
          uni.showLoading({
            title: "本机号码登录中..."
          });
          await this.simulateThirdPartyLogin("phone");
          uni.setStorageSync("userToken", "phone_auto_login_token");
          uni.setStorageSync("loginType", "phone_auto");
          uni.hideLoading();
          uni.showToast({
            title: "登录成功",
            icon: "success"
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index"
            });
          }, 1500);
        } catch (error) {
          uni.hideLoading();
          uni.showToast({
            title: "本机号码登录失败",
            icon: "none"
          });
        }
      },
      // QQ登录
      async loginWithQQ() {
        try {
          uni.showLoading({
            title: "QQ登录中..."
          });
          await this.simulateThirdPartyLogin("qq");
          uni.setStorageSync("userToken", "qq_login_token");
          uni.setStorageSync("loginType", "qq");
          uni.hideLoading();
          uni.showToast({
            title: "QQ登录成功",
            icon: "success"
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index"
            });
          }, 1500);
        } catch (error) {
          uni.hideLoading();
          uni.showToast({
            title: "QQ登录失败",
            icon: "none"
          });
        }
      },
      // 模拟发送验证码
      simulateSendCode() {
        return new Promise((resolve, reject) => {
          setTimeout(() => {
            if (Math.random() > 0.1) {
              resolve({ success: true });
            } else {
              reject(new Error("发送失败"));
            }
          }, 1e3);
        });
      },
      // 模拟登录验证
      simulateLogin() {
        return new Promise((resolve, reject) => {
          setTimeout(() => {
            if (this.verificationCode === "123456") {
              resolve({ success: true });
            } else {
              reject(new Error("验证码错误"));
            }
          }, 1500);
        });
      },
      // 模拟第三方登录
      simulateThirdPartyLogin(type) {
        return new Promise((resolve, reject) => {
          setTimeout(() => {
            if (Math.random() > 0.1) {
              resolve({ type, success: true });
            } else {
              reject(new Error("登录失败"));
            }
          }, 1500);
        });
      }
    }
  };
  function _sfc_render$l(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "phone-login-container" }, [
      vue.createCommentVNode(" Logo区域 "),
      vue.createElementVNode("view", { class: "logo-section" }, [
        vue.createElementVNode("image", {
          class: "logo",
          src: _imports_0$5,
          mode: "aspectFit"
        })
      ]),
      vue.createCommentVNode(" 品牌标题 "),
      vue.createElementVNode("view", { class: "brand-section" }, [
        vue.createElementVNode("text", { class: "brand-title" }, "咚 咚"),
        vue.createElementVNode("text", { class: "brand-desc" }, "一个社团交流场地")
      ]),
      vue.createCommentVNode(" 登录表单卡片 "),
      vue.createElementVNode("view", { class: "login-form-card" }, [
        vue.createCommentVNode(" 手机号输入 "),
        vue.createElementVNode("view", { class: "form-group" }, [
          vue.createElementVNode("text", { class: "form-label" }, "手机号"),
          vue.createElementVNode("view", { class: "input-container" }, [
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                class: "form-input",
                type: "number",
                placeholder: "请输入手机号",
                "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $data.phoneNumber = $event),
                maxlength: "11",
                onInput: _cache[1] || (_cache[1] = (...args) => $options.onPhoneInput && $options.onPhoneInput(...args))
              },
              null,
              544
              /* NEED_HYDRATION, NEED_PATCH */
            ), [
              [vue.vModelText, $data.phoneNumber]
            ])
          ])
        ]),
        vue.createCommentVNode(" 验证码输入 "),
        vue.createElementVNode("view", { class: "form-group" }, [
          vue.createElementVNode("text", { class: "form-label" }, "验证码"),
          vue.createElementVNode("view", { class: "verification-container" }, [
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                class: "form-input verification-input",
                type: "number",
                placeholder: "请输入验证码",
                "onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => $data.verificationCode = $event),
                maxlength: "6"
              },
              null,
              512
              /* NEED_PATCH */
            ), [
              [vue.vModelText, $data.verificationCode]
            ]),
            vue.createElementVNode("button", {
              class: "send-code-btn",
              disabled: !$options.canSendCode || $data.countdown > 0,
              onClick: _cache[3] || (_cache[3] = (...args) => $options.sendVerificationCode && $options.sendVerificationCode(...args))
            }, vue.toDisplayString($data.countdown > 0 ? `重新发送(${$data.countdown}s)` : "发送验证码"), 9, ["disabled"])
          ])
        ]),
        vue.createCommentVNode(" 登录按钮 "),
        vue.createElementVNode("button", {
          class: vue.normalizeClass(["login-submit-btn", { "active": $options.canLogin }]),
          disabled: !$options.canLogin,
          onClick: _cache[4] || (_cache[4] = (...args) => $options.handleLogin && $options.handleLogin(...args))
        }, " 登录 ", 10, ["disabled"]),
        vue.createCommentVNode(" 注册账号按钮 "),
        vue.createElementVNode("button", {
          class: "register-btn",
          onClick: _cache[5] || (_cache[5] = (...args) => $options.handleRegister && $options.handleRegister(...args))
        }, " 注册账号 "),
        vue.createCommentVNode(" 切换密码登录按钮 "),
        vue.createElementVNode("button", {
          class: "switch-login-btn",
          onClick: _cache[6] || (_cache[6] = (...args) => $options.handleSwitchLogin && $options.handleSwitchLogin(...args))
        }, " 切换密码登录 ")
      ]),
      vue.createCommentVNode(" 第三方登录 "),
      vue.createElementVNode("view", { class: "third-party-section" }, [
        vue.createElementVNode("view", { class: "divider-section" }, [
          vue.createElementVNode("view", { class: "divider-line" }),
          vue.createElementVNode("text", { class: "divider-text" }, "第三方登录"),
          vue.createElementVNode("view", { class: "divider-line" })
        ]),
        vue.createElementVNode("view", { class: "social-login" }, [
          vue.createElementVNode("view", {
            class: "social-btn wechat",
            onClick: _cache[7] || (_cache[7] = (...args) => $options.loginWithWechat && $options.loginWithWechat(...args))
          }, [
            vue.createElementVNode("image", {
              class: "social-icon",
              src: _imports_1$6,
              mode: "aspectFit"
            })
          ]),
          vue.createElementVNode("view", {
            class: "social-btn phone",
            onClick: _cache[8] || (_cache[8] = (...args) => $options.loginWithPhone && $options.loginWithPhone(...args))
          }, [
            vue.createElementVNode("image", {
              class: "social-icon",
              src: _imports_2$4,
              mode: "aspectFit"
            })
          ]),
          vue.createElementVNode("view", {
            class: "social-btn qq",
            onClick: _cache[9] || (_cache[9] = (...args) => $options.loginWithQQ && $options.loginWithQQ(...args))
          }, [
            vue.createElementVNode("image", {
              class: "social-icon",
              src: _imports_3$2,
              mode: "aspectFit"
            })
          ])
        ])
      ])
    ]);
  }
  const PagesPhoneLoginPhoneLogin = /* @__PURE__ */ _export_sfc(_sfc_main$l, [["render", _sfc_render$l], ["__scopeId", "data-v-e0aec1e2"], ["__file", "D:/code space2/xystapp/android/pages/phone-login/phone-login.vue"]]);
  const _sfc_main$k = {
    data() {
      return {
        registerForm: {
          phone: "",
          password: "",
          confirmPassword: "",
          verificationCode: ""
        },
        codeButtonText: "发送验证码",
        countdown: 0,
        countdownTimer: null
      };
    },
    methods: {
      // 发送验证码
      sendVerificationCode() {
        if (!this.registerForm.phone) {
          uni.showToast({
            title: "请输入手机号",
            icon: "none"
          });
          return;
        }
        const phoneRegex = /^1[3-9]\d{9}$/;
        if (!phoneRegex.test(this.registerForm.phone)) {
          uni.showToast({
            title: "请输入正确的手机号",
            icon: "none"
          });
          return;
        }
        if (this.countdown > 0)
          return;
        uni.showToast({
          title: "验证码已发送",
          icon: "success"
        });
        this.countdown = 60;
        this.codeButtonText = `${this.countdown}s`;
        this.countdownTimer = setInterval(() => {
          this.countdown--;
          if (this.countdown <= 0) {
            clearInterval(this.countdownTimer);
            this.codeButtonText = "发送验证码";
          } else {
            this.codeButtonText = `${this.countdown}s`;
          }
        }, 1e3);
      },
      // 注册
      handleRegister() {
        if (!this.registerForm.phone) {
          uni.showToast({
            title: "请输入手机号",
            icon: "none"
          });
          return;
        }
        if (!this.registerForm.password) {
          uni.showToast({
            title: "请输入密码",
            icon: "none"
          });
          return;
        }
        if (this.registerForm.password !== this.registerForm.confirmPassword) {
          uni.showToast({
            title: "两次密码输入不一致",
            icon: "none"
          });
          return;
        }
        if (!this.registerForm.verificationCode) {
          uni.showToast({
            title: "请输入验证码",
            icon: "none"
          });
          return;
        }
        uni.showLoading({
          title: "注册中..."
        });
        setTimeout(() => {
          uni.hideLoading();
          uni.showToast({
            title: "注册成功",
            icon: "success"
          });
          setTimeout(() => {
            uni.navigateTo({
              url: "/pages/profile-setup/profile-setup"
            });
          }, 1500);
        }, 2e3);
      },
      // 微信登录
      loginWithWechat() {
        uni.showToast({
          title: "微信登录",
          icon: "none"
        });
      },
      // 电话登录
      loginWithPhone() {
        uni.showToast({
          title: "电话登录",
          icon: "none"
        });
      },
      // QQ登录
      loginWithQQ() {
        uni.showToast({
          title: "QQ登录",
          icon: "none"
        });
      },
      // 返回登录页面
      goToLogin() {
        uni.navigateBack();
      }
    },
    onUnload() {
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer);
      }
    }
  };
  function _sfc_render$k(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "register-container" }, [
      vue.createCommentVNode(" Logo "),
      vue.createElementVNode("view", { class: "logo-section" }, [
        vue.createElementVNode("image", {
          class: "logo",
          src: _imports_0$5,
          mode: "aspectFit"
        })
      ]),
      vue.createCommentVNode(" 品牌标题 "),
      vue.createElementVNode("view", { class: "brand-title" }, "咚 咚"),
      vue.createElementVNode("view", { class: "brand-subtitle" }, "一个社团交流场地"),
      vue.createCommentVNode(" 注册卡片 "),
      vue.createElementVNode("view", { class: "register-card" }, [
        vue.createCommentVNode(" 手机号 "),
        vue.createElementVNode("view", { class: "form-group" }, [
          vue.createElementVNode("text", { class: "form-label" }, "手机号"),
          vue.createElementVNode("view", { class: "input-container" }, [
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                class: "form-input",
                type: "number",
                placeholder: "请输入手机号",
                "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $data.registerForm.phone = $event),
                maxlength: "11"
              },
              null,
              512
              /* NEED_PATCH */
            ), [
              [vue.vModelText, $data.registerForm.phone]
            ])
          ])
        ]),
        vue.createCommentVNode(" 密码 "),
        vue.createElementVNode("view", { class: "form-group" }, [
          vue.createElementVNode("text", { class: "form-label" }, "密码"),
          vue.createElementVNode("view", { class: "input-container" }, [
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                class: "form-input",
                type: "password",
                placeholder: "请输入密码",
                "onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => $data.registerForm.password = $event)
              },
              null,
              512
              /* NEED_PATCH */
            ), [
              [vue.vModelText, $data.registerForm.password]
            ])
          ])
        ]),
        vue.createCommentVNode(" 密码确认 "),
        vue.createElementVNode("view", { class: "form-group" }, [
          vue.createElementVNode("text", { class: "form-label" }, "密码确认"),
          vue.createElementVNode("view", { class: "input-container" }, [
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                class: "form-input",
                type: "password",
                placeholder: "请再次输入密码",
                "onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => $data.registerForm.confirmPassword = $event)
              },
              null,
              512
              /* NEED_PATCH */
            ), [
              [vue.vModelText, $data.registerForm.confirmPassword]
            ])
          ])
        ]),
        vue.createCommentVNode(" 验证码 "),
        vue.createElementVNode("view", { class: "form-group" }, [
          vue.createElementVNode("text", { class: "form-label" }, "验证码"),
          vue.createElementVNode("view", { class: "input-container verification-container" }, [
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                class: "form-input verification-input",
                type: "text",
                placeholder: "请输入验证码",
                "onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => $data.registerForm.verificationCode = $event),
                maxlength: "6"
              },
              null,
              512
              /* NEED_PATCH */
            ), [
              [vue.vModelText, $data.registerForm.verificationCode]
            ]),
            vue.createElementVNode(
              "button",
              {
                class: "send-code-btn",
                onClick: _cache[4] || (_cache[4] = (...args) => $options.sendVerificationCode && $options.sendVerificationCode(...args))
              },
              vue.toDisplayString($data.codeButtonText),
              1
              /* TEXT */
            )
          ])
        ])
      ]),
      vue.createCommentVNode(" 注册按钮 "),
      vue.createElementVNode("button", {
        class: "register-submit-btn",
        onClick: _cache[5] || (_cache[5] = (...args) => $options.handleRegister && $options.handleRegister(...args))
      }, " 立即注册 "),
      vue.createCommentVNode(" 第三方登录 "),
      vue.createElementVNode("view", { class: "divider-container" }, [
        vue.createElementVNode("view", { class: "divider-line" }),
        vue.createElementVNode("text", { class: "divider-text" }, "第三方登录"),
        vue.createElementVNode("view", { class: "divider-line" })
      ]),
      vue.createElementVNode("view", { class: "social-login" }, [
        vue.createElementVNode("view", {
          class: "social-btn",
          onClick: _cache[6] || (_cache[6] = (...args) => $options.loginWithWechat && $options.loginWithWechat(...args))
        }, [
          vue.createElementVNode("image", {
            class: "social-icon",
            src: _imports_1$6,
            mode: "aspectFit"
          })
        ]),
        vue.createElementVNode("view", {
          class: "social-btn",
          onClick: _cache[7] || (_cache[7] = (...args) => $options.loginWithPhone && $options.loginWithPhone(...args))
        }, [
          vue.createElementVNode("image", {
            class: "social-icon",
            src: _imports_2$4,
            mode: "aspectFit"
          })
        ]),
        vue.createElementVNode("view", {
          class: "social-btn",
          onClick: _cache[8] || (_cache[8] = (...args) => $options.loginWithQQ && $options.loginWithQQ(...args))
        }, [
          vue.createElementVNode("image", {
            class: "social-icon",
            src: _imports_3$2,
            mode: "aspectFit"
          })
        ])
      ]),
      vue.createCommentVNode(" 返回登录 "),
      vue.createElementVNode("view", { class: "back-to-login" }, [
        vue.createElementVNode("text", {
          class: "login-link",
          onClick: _cache[9] || (_cache[9] = (...args) => $options.goToLogin && $options.goToLogin(...args))
        }, "已有账号？立即登录")
      ])
    ]);
  }
  const PagesRegisterRegister = /* @__PURE__ */ _export_sfc(_sfc_main$k, [["render", _sfc_render$k], ["__scopeId", "data-v-bac4a35d"], ["__file", "D:/code space2/xystapp/android/pages/register/register.vue"]]);
  const _sfc_main$j = {
    data() {
      return {
        phoneNumber: "",
        password: "",
        showPassword: false,
        isLoading: false
      };
    },
    computed: {
      // 是否可以登录
      canLogin() {
        return this.phoneNumber.length === 11 && /^1[3-9]\d{9}$/.test(this.phoneNumber) && this.password.length >= 6;
      }
    },
    onLoad() {
      formatAppLog("log", "at pages/password-login/password-login.vue:114", "账号密码登录页面加载完成");
    },
    methods: {
      // 手机号输入处理
      onPhoneInput(e) {
        this.phoneNumber = e.detail.value;
      },
      // 密码输入处理
      onPasswordInput(e) {
        this.password = e.detail.value;
      },
      // 切换密码显示/隐藏
      togglePassword() {
        this.showPassword = !this.showPassword;
      },
      // 忘记密码
      handleForgotPassword() {
        uni.showToast({
          title: "请联系客服重置密码",
          icon: "none"
        });
      },
      // 登录处理
      async handleLogin() {
        if (!this.canLogin) {
          uni.showToast({
            title: "请填写完整信息",
            icon: "none"
          });
          return;
        }
        this.isLoading = true;
        try {
          uni.showLoading({
            title: "登录中..."
          });
          await this.simulateLogin();
          uni.setStorageSync("userToken", "password_login_token");
          uni.setStorageSync("loginType", "password");
          uni.setStorageSync("userPhone", this.phoneNumber);
          uni.hideLoading();
          uni.showToast({
            title: "登录成功",
            icon: "success"
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index"
            });
          }, 1500);
        } catch (error) {
          uni.hideLoading();
          uni.showToast({
            title: error.message || "登录失败，请重试",
            icon: "none"
          });
        } finally {
          this.isLoading = false;
        }
      },
      // 注册账号
      handleRegister() {
        uni.navigateTo({
          url: "/pages/register/register"
        });
      },
      // 切换验证码登录
      handleSwitchLogin() {
        uni.navigateTo({
          url: "/pages/phone-login/phone-login"
        });
      },
      // 微信登录
      async loginWithWechat() {
        try {
          uni.showLoading({
            title: "微信登录中..."
          });
          await this.simulateThirdPartyLogin("wechat");
          uni.setStorageSync("userToken", "wechat_login_token");
          uni.setStorageSync("loginType", "wechat");
          uni.hideLoading();
          uni.showToast({
            title: "微信登录成功",
            icon: "success"
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index"
            });
          }, 1500);
        } catch (error) {
          uni.hideLoading();
          uni.showToast({
            title: "微信登录失败",
            icon: "none"
          });
        }
      },
      // 本机号码登录
      async loginWithPhone() {
        try {
          uni.showLoading({
            title: "本机号码登录中..."
          });
          await this.simulateThirdPartyLogin("phone");
          uni.setStorageSync("userToken", "phone_auto_login_token");
          uni.setStorageSync("loginType", "phone_auto");
          uni.hideLoading();
          uni.showToast({
            title: "登录成功",
            icon: "success"
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index"
            });
          }, 1500);
        } catch (error) {
          uni.hideLoading();
          uni.showToast({
            title: "本机号码登录失败",
            icon: "none"
          });
        }
      },
      // QQ登录
      async loginWithQQ() {
        try {
          uni.showLoading({
            title: "QQ登录中..."
          });
          await this.simulateThirdPartyLogin("qq");
          uni.setStorageSync("userToken", "qq_login_token");
          uni.setStorageSync("loginType", "qq");
          uni.hideLoading();
          uni.showToast({
            title: "QQ登录成功",
            icon: "success"
          });
          setTimeout(() => {
            uni.reLaunch({
              url: "/pages/index/index"
            });
          }, 1500);
        } catch (error) {
          uni.hideLoading();
          uni.showToast({
            title: "QQ登录失败",
            icon: "none"
          });
        }
      },
      // 模拟登录验证
      simulateLogin() {
        return new Promise((resolve, reject) => {
          setTimeout(() => {
            if (this.phoneNumber === "13800138000" && this.password === "123456") {
              resolve({ success: true });
            } else if (this.password === "123456") {
              resolve({ success: true });
            } else {
              reject(new Error("账号或密码错误"));
            }
          }, 1500);
        });
      },
      // 模拟第三方登录
      simulateThirdPartyLogin(type) {
        return new Promise((resolve, reject) => {
          setTimeout(() => {
            if (Math.random() > 0.1) {
              resolve({ type, success: true });
            } else {
              reject(new Error("登录失败"));
            }
          }, 1500);
        });
      }
    }
  };
  function _sfc_render$j(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "password-login-container" }, [
      vue.createCommentVNode(" Logo区域 "),
      vue.createElementVNode("view", { class: "logo-section" }, [
        vue.createElementVNode("image", {
          class: "logo",
          src: _imports_0$5,
          mode: "aspectFit"
        })
      ]),
      vue.createCommentVNode(" 品牌标题 "),
      vue.createElementVNode("view", { class: "brand-section" }, [
        vue.createElementVNode("text", { class: "brand-title" }, "咚 咚"),
        vue.createElementVNode("text", { class: "brand-desc" }, "一个社团交流场地")
      ]),
      vue.createCommentVNode(" 登录表单卡片 "),
      vue.createElementVNode("view", { class: "login-form-card" }, [
        vue.createCommentVNode(" 手机号输入 "),
        vue.createElementVNode("view", { class: "form-group" }, [
          vue.createElementVNode("text", { class: "form-label" }, "手机号"),
          vue.createElementVNode("view", { class: "input-container" }, [
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                class: "form-input",
                type: "number",
                placeholder: "请输入手机号",
                "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $data.phoneNumber = $event),
                maxlength: "11",
                onInput: _cache[1] || (_cache[1] = (...args) => $options.onPhoneInput && $options.onPhoneInput(...args))
              },
              null,
              544
              /* NEED_HYDRATION, NEED_PATCH */
            ), [
              [vue.vModelText, $data.phoneNumber]
            ])
          ])
        ]),
        vue.createCommentVNode(" 密码输入 "),
        vue.createElementVNode("view", { class: "form-group" }, [
          vue.createElementVNode("text", { class: "form-label" }, "密码"),
          vue.createElementVNode("view", { class: "password-container" }, [
            vue.withDirectives(vue.createElementVNode("input", {
              class: "form-input password-input",
              type: $data.showPassword ? "text" : "password",
              placeholder: "请输入密码",
              "onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => $data.password = $event),
              onInput: _cache[3] || (_cache[3] = (...args) => $options.onPasswordInput && $options.onPasswordInput(...args))
            }, null, 40, ["type"]), [
              [vue.vModelDynamic, $data.password]
            ]),
            vue.createElementVNode("view", {
              class: "password-toggle",
              onClick: _cache[4] || (_cache[4] = (...args) => $options.togglePassword && $options.togglePassword(...args))
            }, [
              vue.createElementVNode(
                "text",
                { class: "toggle-text" },
                vue.toDisplayString($data.showPassword ? "隐藏" : "显示"),
                1
                /* TEXT */
              )
            ])
          ]),
          vue.createElementVNode("view", {
            class: "forgot-password",
            onClick: _cache[5] || (_cache[5] = (...args) => $options.handleForgotPassword && $options.handleForgotPassword(...args))
          }, [
            vue.createElementVNode("text", { class: "forgot-text" }, "忘记密码")
          ])
        ]),
        vue.createCommentVNode(" 登录按钮 "),
        vue.createElementVNode("button", {
          class: vue.normalizeClass(["login-submit-btn", { "active": $options.canLogin }]),
          disabled: !$options.canLogin,
          onClick: _cache[6] || (_cache[6] = (...args) => $options.handleLogin && $options.handleLogin(...args))
        }, " 登录 ", 10, ["disabled"]),
        vue.createCommentVNode(" 注册账号按钮 "),
        vue.createElementVNode("button", {
          class: "register-btn",
          onClick: _cache[7] || (_cache[7] = (...args) => $options.handleRegister && $options.handleRegister(...args))
        }, " 注册账号 "),
        vue.createCommentVNode(" 切换验证码登录按钮 "),
        vue.createElementVNode("button", {
          class: "switch-login-btn",
          onClick: _cache[8] || (_cache[8] = (...args) => $options.handleSwitchLogin && $options.handleSwitchLogin(...args))
        }, " 切换验证码登录 ")
      ]),
      vue.createCommentVNode(" 第三方登录 "),
      vue.createElementVNode("view", { class: "third-party-section" }, [
        vue.createElementVNode("view", { class: "divider-section" }, [
          vue.createElementVNode("view", { class: "divider-line" }),
          vue.createElementVNode("text", { class: "divider-text" }, "第三方登录"),
          vue.createElementVNode("view", { class: "divider-line" })
        ]),
        vue.createElementVNode("view", { class: "social-login" }, [
          vue.createElementVNode("view", {
            class: "social-btn wechat",
            onClick: _cache[9] || (_cache[9] = (...args) => $options.loginWithWechat && $options.loginWithWechat(...args))
          }, [
            vue.createElementVNode("image", {
              class: "social-icon",
              src: _imports_1$6,
              mode: "aspectFit"
            })
          ]),
          vue.createElementVNode("view", {
            class: "social-btn phone",
            onClick: _cache[10] || (_cache[10] = (...args) => $options.loginWithPhone && $options.loginWithPhone(...args))
          }, [
            vue.createElementVNode("image", {
              class: "social-icon",
              src: _imports_2$4,
              mode: "aspectFit"
            })
          ]),
          vue.createElementVNode("view", {
            class: "social-btn qq",
            onClick: _cache[11] || (_cache[11] = (...args) => $options.loginWithQQ && $options.loginWithQQ(...args))
          }, [
            vue.createElementVNode("image", {
              class: "social-icon",
              src: _imports_3$2,
              mode: "aspectFit"
            })
          ])
        ])
      ])
    ]);
  }
  const PagesPasswordLoginPasswordLogin = /* @__PURE__ */ _export_sfc(_sfc_main$j, [["render", _sfc_render$j], ["__scopeId", "data-v-24579e20"], ["__file", "D:/code space2/xystapp/android/pages/password-login/password-login.vue"]]);
  const _imports_0$3 = "/static/type18/pencil.png";
  const _imports_1$5 = "/static/type18/筛子.png";
  const _sfc_main$i = {
    data() {
      return {
        userInfo: {
          avatar: "/static/type18/头像.png",
          username: "",
          gender: "",
          birthYear: "2002",
          birthMonth: "3",
          birthDay: "1"
        },
        yearOptions: [],
        monthOptions: [],
        dayOptions: [],
        selectedYearIndex: 0,
        selectedMonthIndex: 2,
        // 默认3月
        selectedDayIndex: 0,
        // 默认1日
        randomNames: [
          "小咚",
          "咚咚",
          "阳光少年",
          "快乐小鸟",
          "梦想家",
          "星空漫步",
          "微笑天使",
          "自由飞翔",
          "彩虹之约",
          "温暖阳光",
          "清风徐来",
          "花开半夏",
          "时光荏苒",
          "岁月如歌",
          "青春无悔"
        ]
      };
    },
    computed: {
      // 是否可以进入下一步
      canProceed() {
        return this.userInfo.username.trim().length >= 2 && this.userInfo.gender !== "";
      }
    },
    onLoad() {
      formatAppLog("log", "at pages/profile-setup/profile-setup.vue:164", "个人信息设置页面加载完成");
      this.initDateOptions();
    },
    methods: {
      // 初始化日期选项
      initDateOptions() {
        this.yearOptions = [];
        for (let year = 1950; year <= 2010; year++) {
          this.yearOptions.push(year.toString());
        }
        this.monthOptions = [];
        for (let month = 1; month <= 12; month++) {
          this.monthOptions.push(month.toString());
        }
        this.selectedYearIndex = this.yearOptions.indexOf("2002");
        this.selectedMonthIndex = 2;
        this.selectedDayIndex = 0;
        this.updateDayOptions();
      },
      // 更新日期选项
      updateDayOptions() {
        const year = parseInt(this.userInfo.birthYear);
        const month = parseInt(this.userInfo.birthMonth);
        const daysInMonth = new Date(year, month, 0).getDate();
        const currentDay = parseInt(this.userInfo.birthDay);
        this.dayOptions = [];
        for (let day = 1; day <= daysInMonth; day++) {
          this.dayOptions.push(day.toString());
        }
        if (currentDay > daysInMonth) {
          this.selectedDayIndex = daysInMonth - 1;
          this.userInfo.birthDay = daysInMonth.toString();
        } else {
          this.selectedDayIndex = currentDay - 1;
        }
      },
      // 用户名输入处理
      onUsernameInput(e) {
        this.userInfo.username = e.detail.value;
      },
      // 生成随机用户名
      generateRandomName() {
        const randomIndex = Math.floor(Math.random() * this.randomNames.length);
        this.userInfo.username = this.randomNames[randomIndex];
        uni.showToast({
          title: "已生成随机昵称",
          icon: "none",
          duration: 1e3
        });
      },
      // 选择性别
      selectGender(gender) {
        this.userInfo.gender = gender;
      },
      // 年份改变
      onYearChange(e) {
        this.selectedYearIndex = e.detail.value;
        this.userInfo.birthYear = this.yearOptions[e.detail.value];
        this.updateDayOptions();
      },
      // 月份改变
      onMonthChange(e) {
        this.selectedMonthIndex = e.detail.value;
        this.userInfo.birthMonth = this.monthOptions[e.detail.value];
        this.updateDayOptions();
      },
      // 日期改变
      onDayChange(e) {
        this.selectedDayIndex = e.detail.value;
        this.userInfo.birthDay = this.dayOptions[e.detail.value];
      },
      // 选择头像
      selectAvatar() {
        uni.chooseImage({
          count: 1,
          sizeType: ["compressed"],
          sourceType: ["album", "camera"],
          success: (res) => {
            this.userInfo.avatar = res.tempFilePaths[0];
            uni.showToast({
              title: "头像已更新",
              icon: "success",
              duration: 1e3
            });
          },
          fail: () => {
            uni.showToast({
              title: "取消选择头像",
              icon: "none",
              duration: 1e3
            });
          }
        });
      },
      // 返回上一步
      goBack() {
        uni.navigateBack();
      },
      // 下一步处理
      async handleNext() {
        if (!this.canProceed) {
          uni.showToast({
            title: "请完善基本信息",
            icon: "none"
          });
          return;
        }
        try {
          await this.saveUserProfile();
          uni.setStorageSync("userProfile", this.userInfo);
          uni.navigateTo({
            url: "/pages/mbti-setup/mbti-setup"
          });
        } catch (error) {
          uni.hideLoading();
          uni.showToast({
            title: "保存失败，请重试",
            icon: "none"
          });
        }
      },
      // 模拟保存用户资料
      saveUserProfile() {
        return new Promise((resolve) => {
          setTimeout(() => {
            formatAppLog("log", "at pages/profile-setup/profile-setup.vue:321", "用户信息保存成功:", this.userInfo);
            resolve({ success: true });
          }, 1e3);
        });
      }
    }
  };
  function _sfc_render$i(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "profile-setup-container" }, [
      vue.createCommentVNode(" 顶部进度条区域 "),
      vue.createElementVNode("view", { class: "header-section" }, [
        vue.createElementVNode("view", { class: "progress-container" }, [
          vue.createElementVNode("view", { class: "progress-bar" }, [
            vue.createElementVNode("view", { class: "progress-fill" })
          ])
        ]),
        vue.createCommentVNode(" 页面标题 "),
        vue.createElementVNode("text", { class: "page-title" }, "基本信息")
      ]),
      vue.createCommentVNode(" 头像区域 "),
      vue.createElementVNode("view", { class: "avatar-section" }, [
        vue.createElementVNode("view", {
          class: "avatar-container",
          onClick: _cache[0] || (_cache[0] = (...args) => $options.selectAvatar && $options.selectAvatar(...args))
        }, [
          vue.createElementVNode("image", {
            class: "avatar-image",
            src: $data.userInfo.avatar,
            mode: "aspectFill"
          }, null, 8, ["src"]),
          vue.createElementVNode("view", { class: "edit-icon-container" }, [
            vue.createElementVNode("image", {
              class: "edit-icon",
              src: _imports_0$3,
              mode: "aspectFit"
            })
          ])
        ])
      ]),
      vue.createCommentVNode(" 表单区域 "),
      vue.createElementVNode("view", { class: "form-section" }, [
        vue.createCommentVNode(" 用户名输入 "),
        vue.createElementVNode("view", { class: "form-group" }, [
          vue.createElementVNode("text", { class: "form-label" }, "用户名 *"),
          vue.createElementVNode("view", { class: "input-container" }, [
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                class: "form-input",
                type: "text",
                placeholder: "请输入昵称",
                "onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => $data.userInfo.username = $event),
                maxlength: "20",
                onInput: _cache[2] || (_cache[2] = (...args) => $options.onUsernameInput && $options.onUsernameInput(...args))
              },
              null,
              544
              /* NEED_HYDRATION, NEED_PATCH */
            ), [
              [vue.vModelText, $data.userInfo.username]
            ]),
            vue.createElementVNode("view", {
              class: "dice-icon",
              onClick: _cache[3] || (_cache[3] = (...args) => $options.generateRandomName && $options.generateRandomName(...args))
            }, [
              vue.createElementVNode("image", {
                class: "dice-image",
                src: _imports_1$5,
                mode: "aspectFit"
              })
            ])
          ])
        ]),
        vue.createCommentVNode(" 性别选择 "),
        vue.createElementVNode("view", { class: "form-group" }, [
          vue.createElementVNode("text", { class: "form-label" }, "性别"),
          vue.createElementVNode("view", { class: "gender-options" }, [
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["gender-option", { "active": $data.userInfo.gender === "male" }]),
                onClick: _cache[4] || (_cache[4] = ($event) => $options.selectGender("male"))
              },
              [
                vue.createElementVNode("text", { class: "gender-text" }, "男")
              ],
              2
              /* CLASS */
            ),
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["gender-option", { "active": $data.userInfo.gender === "female" }]),
                onClick: _cache[5] || (_cache[5] = ($event) => $options.selectGender("female"))
              },
              [
                vue.createElementVNode("text", { class: "gender-text" }, "女")
              ],
              2
              /* CLASS */
            ),
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["gender-option", { "active": $data.userInfo.gender === "other" }]),
                onClick: _cache[6] || (_cache[6] = ($event) => $options.selectGender("other"))
              },
              [
                vue.createElementVNode("text", { class: "gender-text" }, "其他")
              ],
              2
              /* CLASS */
            )
          ])
        ]),
        vue.createCommentVNode(" 生日选择 "),
        vue.createElementVNode("view", { class: "form-group" }, [
          vue.createElementVNode("text", { class: "form-label" }, "日期"),
          vue.createElementVNode("view", { class: "date-picker-container" }, [
            vue.createElementVNode("view", { class: "date-selectors" }, [
              vue.createCommentVNode(" 年份选择 "),
              vue.createElementVNode("picker", {
                mode: "selector",
                range: $data.yearOptions,
                value: $data.selectedYearIndex,
                onChange: _cache[7] || (_cache[7] = (...args) => $options.onYearChange && $options.onYearChange(...args)),
                class: "date-picker"
              }, [
                vue.createElementVNode(
                  "view",
                  { class: "picker-text" },
                  vue.toDisplayString($data.userInfo.birthYear) + "年",
                  1
                  /* TEXT */
                )
              ], 40, ["range", "value"]),
              vue.createCommentVNode(" 月份选择 "),
              vue.createElementVNode("picker", {
                mode: "selector",
                range: $data.monthOptions,
                value: $data.selectedMonthIndex,
                onChange: _cache[8] || (_cache[8] = (...args) => $options.onMonthChange && $options.onMonthChange(...args)),
                class: "date-picker"
              }, [
                vue.createElementVNode(
                  "view",
                  { class: "picker-text" },
                  vue.toDisplayString($data.userInfo.birthMonth) + "月",
                  1
                  /* TEXT */
                )
              ], 40, ["range", "value"]),
              vue.createCommentVNode(" 日期选择 "),
              vue.createElementVNode("picker", {
                mode: "selector",
                range: $data.dayOptions,
                value: $data.selectedDayIndex,
                onChange: _cache[9] || (_cache[9] = (...args) => $options.onDayChange && $options.onDayChange(...args)),
                class: "date-picker"
              }, [
                vue.createElementVNode(
                  "view",
                  { class: "picker-text" },
                  vue.toDisplayString($data.userInfo.birthDay) + "日",
                  1
                  /* TEXT */
                )
              ], 40, ["range", "value"])
            ])
          ])
        ])
      ]),
      vue.createCommentVNode(" 底部按钮区域 "),
      vue.createElementVNode("view", { class: "bottom-buttons" }, [
        vue.createElementVNode("button", {
          class: "btn-secondary",
          onClick: _cache[10] || (_cache[10] = (...args) => $options.goBack && $options.goBack(...args))
        }, " 上一步 "),
        vue.createElementVNode("button", {
          class: vue.normalizeClass(["btn-primary", { "active": $options.canProceed }]),
          disabled: !$options.canProceed,
          onClick: _cache[11] || (_cache[11] = (...args) => $options.handleNext && $options.handleNext(...args))
        }, " 下一步 ", 10, ["disabled"])
      ])
    ]);
  }
  const PagesProfileSetupProfileSetup = /* @__PURE__ */ _export_sfc(_sfc_main$i, [["render", _sfc_render$i], ["__scopeId", "data-v-27de631f"], ["__file", "D:/code space2/xystapp/android/pages/profile-setup/profile-setup.vue"]]);
  const _sfc_main$h = {
    name: "MbtiSetup",
    data() {
      return {
        // 每个组的选中状态
        selectedTypes: {
          nt: "",
          nf: "",
          sj: "",
          sp: ""
        },
        // NT型紫人
        ntTypes: [
          { label: "INTJ", value: "INTJ" },
          { label: "INTP", value: "INTP" },
          { label: "ENTJ", value: "ENTJ" },
          { label: "ENTP", value: "ENTP" }
        ],
        // NF型绿人
        nfTypes: [
          { label: "INFJ", value: "INFJ" },
          { label: "INFP", value: "INFP" },
          { label: "ENFJ", value: "ENFJ" },
          { label: "ENFP", value: "ENFP" }
        ],
        // SJ型蓝人
        sjTypes: [
          { label: "ISTJ", value: "ISTJ" },
          { label: "ISFJ", value: "ISFJ" },
          { label: "ESTJ", value: "ESTJ" },
          { label: "ESFJ", value: "ESFJ" }
        ],
        // SP型黄人
        spTypes: [
          { label: "ISTP", value: "ISTP" },
          { label: "ISFP", value: "ISFP" },
          { label: "ESTP", value: "ESTP" },
          { label: "ESFP", value: "ESFP" }
        ]
      };
    },
    computed: {
      // 是否有任何选择
      hasAnySelection() {
        return this.selectedTypes.nt || this.selectedTypes.nf || this.selectedTypes.sj || this.selectedTypes.sp;
      }
    },
    methods: {
      // 选择MBTI类型
      selectMbti(group, type) {
        this.selectedTypes[group] = type;
        formatAppLog("log", "at pages/mbti-setup/mbti-setup.vue:152", "选择的MBTI类型:", group, type);
      },
      // 返回上一步
      goBack() {
        uni.navigateBack();
      },
      // 进入下一步
      goNext() {
        if (!this.hasAnySelection) {
          uni.showToast({
            title: "请至少选择一个MBTI类型",
            icon: "none"
          });
          return;
        }
        uni.setStorageSync("userMbtiTypes", this.selectedTypes);
        formatAppLog("log", "at pages/mbti-setup/mbti-setup.vue:172", "MBTI类型已保存:", this.selectedTypes);
        uni.navigateTo({
          url: "/pages/mbti-type/mbti-type"
        });
      }
    },
    // 页面加载时获取已保存的MBTI信息
    onLoad() {
      const savedMbtiTypes = uni.getStorageSync("userMbtiTypes");
      if (savedMbtiTypes) {
        this.selectedTypes = savedMbtiTypes;
      }
    }
  };
  function _sfc_render$h(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "mbti-setup-container" }, [
      vue.createCommentVNode(" 顶部进度条区域 "),
      vue.createElementVNode("view", { class: "header-section" }, [
        vue.createElementVNode("view", { class: "progress-container" }, [
          vue.createElementVNode("view", { class: "progress-bar" }, [
            vue.createElementVNode("view", { class: "progress-fill progress-step2" })
          ])
        ]),
        vue.createCommentVNode(" 页面标题 "),
        vue.createElementVNode("text", { class: "page-title" }, "设置MBTI")
      ]),
      vue.createCommentVNode(" MBTI选择区域 "),
      vue.createElementVNode("view", { class: "mbti-section" }, [
        vue.createCommentVNode(" NT型紫人 "),
        vue.createElementVNode("view", { class: "mbti-group" }, [
          vue.createElementVNode("text", { class: "group-title" }, "NT型紫人"),
          vue.createElementVNode("view", { class: "mbti-options" }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.ntTypes, (type) => {
                return vue.openBlock(), vue.createElementBlock("view", {
                  key: type.value,
                  class: vue.normalizeClass(["mbti-option", { "selected": $data.selectedTypes.nt === type.value }]),
                  onClick: ($event) => $options.selectMbti("nt", type.value)
                }, [
                  vue.createElementVNode(
                    "text",
                    { class: "mbti-text" },
                    vue.toDisplayString(type.label),
                    1
                    /* TEXT */
                  )
                ], 10, ["onClick"]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ]),
        vue.createCommentVNode(" NF型绿人 "),
        vue.createElementVNode("view", { class: "mbti-group" }, [
          vue.createElementVNode("text", { class: "group-title" }, "NF型绿人"),
          vue.createElementVNode("view", { class: "mbti-options" }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.nfTypes, (type) => {
                return vue.openBlock(), vue.createElementBlock("view", {
                  key: type.value,
                  class: vue.normalizeClass(["mbti-option", { "selected": $data.selectedTypes.nf === type.value }]),
                  onClick: ($event) => $options.selectMbti("nf", type.value)
                }, [
                  vue.createElementVNode(
                    "text",
                    { class: "mbti-text" },
                    vue.toDisplayString(type.label),
                    1
                    /* TEXT */
                  )
                ], 10, ["onClick"]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ]),
        vue.createCommentVNode(" SJ型蓝人 "),
        vue.createElementVNode("view", { class: "mbti-group" }, [
          vue.createElementVNode("text", { class: "group-title" }, "SJ型蓝人"),
          vue.createElementVNode("view", { class: "mbti-options" }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.sjTypes, (type) => {
                return vue.openBlock(), vue.createElementBlock("view", {
                  key: type.value,
                  class: vue.normalizeClass(["mbti-option", { "selected": $data.selectedTypes.sj === type.value }]),
                  onClick: ($event) => $options.selectMbti("sj", type.value)
                }, [
                  vue.createElementVNode(
                    "text",
                    { class: "mbti-text" },
                    vue.toDisplayString(type.label),
                    1
                    /* TEXT */
                  )
                ], 10, ["onClick"]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ]),
        vue.createCommentVNode(" SP型黄人 "),
        vue.createElementVNode("view", { class: "mbti-group" }, [
          vue.createElementVNode("text", { class: "group-title" }, "SP型黄人"),
          vue.createElementVNode("view", { class: "mbti-options" }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.spTypes, (type) => {
                return vue.openBlock(), vue.createElementBlock("view", {
                  key: type.value,
                  class: vue.normalizeClass(["mbti-option", { "selected": $data.selectedTypes.sp === type.value }]),
                  onClick: ($event) => $options.selectMbti("sp", type.value)
                }, [
                  vue.createElementVNode(
                    "text",
                    { class: "mbti-text" },
                    vue.toDisplayString(type.label),
                    1
                    /* TEXT */
                  )
                ], 10, ["onClick"]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ])
      ]),
      vue.createCommentVNode(" 底部按钮区域 "),
      vue.createElementVNode("view", { class: "bottom-buttons" }, [
        vue.createElementVNode("button", {
          class: "btn-secondary",
          onClick: _cache[0] || (_cache[0] = (...args) => $options.goBack && $options.goBack(...args))
        }, " 上一步 "),
        vue.createElementVNode("button", {
          class: vue.normalizeClass(["btn-primary", { "active": $options.hasAnySelection }]),
          onClick: _cache[1] || (_cache[1] = (...args) => $options.goNext && $options.goNext(...args)),
          disabled: !$options.hasAnySelection
        }, " 下一步 ", 10, ["disabled"])
      ])
    ]);
  }
  const PagesMbtiSetupMbtiSetup = /* @__PURE__ */ _export_sfc(_sfc_main$h, [["render", _sfc_render$h], ["__scopeId", "data-v-a7f4e6a3"], ["__file", "D:/code space2/xystapp/android/pages/mbti-setup/mbti-setup.vue"]]);
  const _sfc_main$g = {
    name: "MbtiType",
    data() {
      return {
        // 每个组的选中状态
        selectedTypes: {
          e: "",
          i: ""
        },
        // 外倾型E人
        eTypes: [
          { label: "ENTJ", value: "ENTJ" },
          { label: "ENTP", value: "ENTP" },
          { label: "ESTJ", value: "ESTJ" },
          { label: "ESFJ", value: "ESFJ" },
          { label: "ENFJ", value: "ENFJ" },
          { label: "ENFP", value: "ENFP" },
          { label: "ESTP", value: "ESTP" },
          { label: "ESFP", value: "ESFP" }
        ],
        // 内倾型I人
        iTypes: [
          { label: "INTJ", value: "INTJ" },
          { label: "INTP", value: "INTP" },
          { label: "ISTJ", value: "ISTJ" },
          { label: "ISFJ", value: "ISFJ" },
          { label: "INFJ", value: "INFJ" },
          { label: "INFP", value: "INFP" },
          { label: "ISTP", value: "ISTP" },
          { label: "ISFP", value: "ISFP" }
        ]
      };
    },
    computed: {
      // 是否有任何选择
      hasAnySelection() {
        return this.selectedTypes.e || this.selectedTypes.i;
      }
    },
    methods: {
      // 选择MBTI类型
      selectMbti(group, type) {
        this.selectedTypes[group] = type;
        formatAppLog("log", "at pages/mbti-type/mbti-type.vue:111", "选择的MBTI类型:", group, type);
      },
      // 返回上一步
      goBack() {
        uni.navigateBack();
      },
      // 进入下一步
      goNext() {
        if (!this.hasAnySelection) {
          uni.showToast({
            title: "请至少选择一个MBTI类型",
            icon: "none"
          });
          return;
        }
        uni.setStorageSync("userMbtiEITypes", this.selectedTypes);
        formatAppLog("log", "at pages/mbti-type/mbti-type.vue:131", "MBTI EI类型已保存:", this.selectedTypes);
        uni.reLaunch({
          url: "/pages/index/index"
        });
      }
    },
    // 页面加载时获取已保存的MBTI信息
    onLoad() {
      const savedMbtiTypes = uni.getStorageSync("userMbtiEITypes");
      if (savedMbtiTypes) {
        this.selectedTypes = savedMbtiTypes;
      }
    }
  };
  function _sfc_render$g(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "mbti-setup-container" }, [
      vue.createCommentVNode(" 顶部进度条区域 "),
      vue.createElementVNode("view", { class: "header-section" }, [
        vue.createElementVNode("view", { class: "progress-container" }, [
          vue.createElementVNode("view", { class: "progress-bar" }, [
            vue.createElementVNode("view", { class: "progress-fill progress-step3" })
          ])
        ]),
        vue.createCommentVNode(" 页面标题 "),
        vue.createElementVNode("text", { class: "page-title" }, "设置MBTI")
      ]),
      vue.createCommentVNode(" MBTI选择区域 "),
      vue.createElementVNode("view", { class: "mbti-section" }, [
        vue.createCommentVNode(" 外倾型E人 "),
        vue.createElementVNode("view", { class: "mbti-group" }, [
          vue.createElementVNode("text", { class: "group-title" }, "外倾型E人"),
          vue.createElementVNode("view", { class: "mbti-options" }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.eTypes, (type) => {
                return vue.openBlock(), vue.createElementBlock("view", {
                  key: type.value,
                  class: vue.normalizeClass(["mbti-option", { "selected": $data.selectedTypes.e === type.value }]),
                  onClick: ($event) => $options.selectMbti("e", type.value)
                }, [
                  vue.createElementVNode(
                    "text",
                    { class: "mbti-text" },
                    vue.toDisplayString(type.label),
                    1
                    /* TEXT */
                  )
                ], 10, ["onClick"]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ]),
        vue.createCommentVNode(" 内倾型I人 "),
        vue.createElementVNode("view", { class: "mbti-group" }, [
          vue.createElementVNode("text", { class: "group-title" }, "内倾型I人"),
          vue.createElementVNode("view", { class: "mbti-options" }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.iTypes, (type) => {
                return vue.openBlock(), vue.createElementBlock("view", {
                  key: type.value,
                  class: vue.normalizeClass(["mbti-option", { "selected": $data.selectedTypes.i === type.value }]),
                  onClick: ($event) => $options.selectMbti("i", type.value)
                }, [
                  vue.createElementVNode(
                    "text",
                    { class: "mbti-text" },
                    vue.toDisplayString(type.label),
                    1
                    /* TEXT */
                  )
                ], 10, ["onClick"]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ])
      ]),
      vue.createCommentVNode(" 底部按钮区域 "),
      vue.createElementVNode("view", { class: "bottom-buttons" }, [
        vue.createElementVNode("button", {
          class: "btn-secondary",
          onClick: _cache[0] || (_cache[0] = (...args) => $options.goBack && $options.goBack(...args))
        }, " 上一步 "),
        vue.createElementVNode("button", {
          class: vue.normalizeClass(["btn-primary", { "active": $options.hasAnySelection }]),
          onClick: _cache[1] || (_cache[1] = (...args) => $options.goNext && $options.goNext(...args)),
          disabled: !$options.hasAnySelection
        }, " 下一步 ", 10, ["disabled"])
      ])
    ]);
  }
  const PagesMbtiTypeMbtiType = /* @__PURE__ */ _export_sfc(_sfc_main$g, [["render", _sfc_render$g], ["__scopeId", "data-v-5353da89"], ["__file", "D:/code space2/xystapp/android/pages/mbti-type/mbti-type.vue"]]);
  const _imports_1$4 = "/static/正文/back.png";
  const _imports_1$3 = "/static/正文/点点.png";
  const _sfc_main$f = {
    name: "TopNavigation",
    props: {
      // 标题文本
      title: {
        type: String,
        default: ""
      },
      // 标题颜色
      titleColor: {
        type: String,
        default: "#333333"
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
        default: "#F6F2FC"
      }
    },
    methods: {
      handleBack() {
        this.$emit("back");
        if (!this.$listeners.back) {
          uni.navigateBack();
        }
      },
      handleMore() {
        this.$emit("more");
      }
    }
  };
  function _sfc_render$f(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "top-navigation-wrapper" }, [
      vue.createCommentVNode(" 顶部状态栏占位 "),
      vue.createElementVNode("view", { class: "status-bar" }),
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createElementVNode("view", { class: "top-nav" }, [
        $props.showBack ? (vue.openBlock(), vue.createElementBlock("view", {
          key: 0,
          class: "nav-left",
          onClick: _cache[0] || (_cache[0] = (...args) => $options.handleBack && $options.handleBack(...args))
        }, [
          vue.createElementVNode("image", {
            src: _imports_1$4,
            class: "back-icon",
            mode: "aspectFit"
          })
        ])) : (vue.openBlock(), vue.createElementBlock("view", {
          key: 1,
          class: "nav-left",
          style: { "width": "48rpx" }
        })),
        vue.createElementVNode("view", { class: "nav-center" }, [
          vue.createElementVNode(
            "text",
            {
              class: "nav-title",
              style: vue.normalizeStyle({ color: $props.titleColor })
            },
            vue.toDisplayString($props.title),
            5
            /* TEXT, STYLE */
          )
        ]),
        $props.showMore ? (vue.openBlock(), vue.createElementBlock("view", {
          key: 2,
          class: "nav-right",
          onClick: _cache[1] || (_cache[1] = (...args) => $options.handleMore && $options.handleMore(...args))
        }, [
          vue.createElementVNode("image", {
            src: _imports_1$3,
            class: "more-icon",
            mode: "aspectFit"
          })
        ])) : (vue.openBlock(), vue.createElementBlock("view", {
          key: 3,
          class: "nav-right",
          style: { "width": "48rpx" }
        }))
      ])
    ]);
  }
  const TopNavigation = /* @__PURE__ */ _export_sfc(_sfc_main$f, [["render", _sfc_render$f], ["__scopeId", "data-v-11dc4142"], ["__file", "D:/code space2/xystapp/android/components/TopNavigation.vue"]]);
  const _sfc_main$e = {
    components: {
      TopNavigation
    },
    data() {
      return {
        postData: {
          userAvatar: "/static/关注页/follow-users-section/Ellipse 2.png",
          username: "我是lol场的一个小用户",
          postTime: "昨天 20:15",
          location: "北京",
          isFollowed: true,
          title: "这是我的帖子的一个标题",
          content: '测试一下最长的长度在这里我们可以轻松自定义主题背景与装扮，打造专属兴趣空间，轻松找到同好社群，感受如 "家园" 般的沉浸互动氛围；实时获取全校各类活动动态后面我再凑一点字数然后写的更长一点\n当灿烂的太阳跳出东海的碧波。\n#吐槽 #2026新生\n@会吃西瓜的小鸭纸',
          images: [
            "/static/关注页/Rectangle 107.png",
            "/static/关注页/Rectangle 108.png",
            "/static/关注页/Rectangle 109.png"
          ],
          relatedGroup: {
            avatar: "/static/关注页/follow-users-section/Ellipse 9.png",
            name: "lol场",
            followers: "261",
            posts: "54"
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
            userAvatar: "/static/关注页/follow-users-section/Ellipse 11.png",
            username: "我是评论人机",
            content: "试试看评论是什么样的呗感觉还挺有意思的可以多写点看看效果怎么样。^ ^ @会吃西瓜的小鸭纸",
            time: "1小时前",
            location: "北京",
            isLiked: false,
            likeCount: 125,
            replies: [
              {
                id: 11,
                username: "我是评论人机的评论人机",
                content: "没错我就是来测试一下评论的评论怎么样的"
              },
              {
                id: 12,
                username: "我不是人机",
                content: "怎么全是测试评论的人机？"
              }
            ],
            totalReplies: 8
          },
          {
            id: 2,
            userAvatar: "/static/关注页/follow-users-section/Ellipse 13.png",
            username: "我是评论人机2号",
            content: "我也试试看评论是什么样的呗感觉还挺有意思的可以多写点看看效果怎么样 @不会吃西瓜的小鸭纸",
            time: "2小时前",
            location: "北京",
            isLiked: true,
            likeCount: 15,
            replies: [
              {
                id: 21,
                username: "我是评论人机的评论人机2",
                content: "没错我就是来测试一下评论的评论怎么样的"
              },
              {
                id: 22,
                username: "我不是人机2",
                content: "怎么全是测试评论的人机？"
              }
            ],
            totalReplies: 3
          }
        ],
        sortType: "hot",
        // hot: 热度最高, time: 按时间
        showCommentModal: false,
        commentText: ""
      };
    },
    onLoad(options) {
      formatAppLog("log", "at pages/post-detail/post-detail.vue:249", "帖子详情页参数：", options);
    },
    methods: {
      goBack() {
        uni.navigateBack();
      },
      showMore() {
        uni.showActionSheet({
          itemList: ["举报", "分享", "收藏"],
          success: (res) => {
            formatAppLog("log", "at pages/post-detail/post-detail.vue:260", "选中了第" + (res.tapIndex + 1) + "个按钮");
          }
        });
      },
      toggleFollow() {
        this.postData.isFollowed = !this.postData.isFollowed;
      },
      previewImage(index) {
        uni.previewImage({
          current: index,
          urls: this.postData.images
        });
      },
      joinGroup() {
        uni.showToast({
          title: "加入成功",
          icon: "success"
        });
      },
      toggleLike() {
        this.postData.isLiked = !this.postData.isLiked;
        if (this.postData.isLiked) {
          this.postData.likeCount++;
        } else {
          this.postData.likeCount--;
        }
      },
      toggleStar() {
        this.postData.isStared = !this.postData.isStared;
        if (this.postData.isStared) {
          this.postData.starCount++;
        } else {
          this.postData.starCount--;
        }
      },
      scrollToComments() {
        uni.pageScrollTo({
          selector: ".comments-section",
          duration: 300
        });
      },
      showCommentInput() {
        this.showCommentModal = true;
      },
      hideCommentModal() {
        this.showCommentModal = false;
        this.commentText = "";
      },
      sendComment() {
        if (this.commentText.trim().length === 0)
          return;
        const newComment = {
          id: Date.now(),
          userAvatar: "/static/images/avatar.png",
          username: "当前用户",
          content: this.commentText.trim(),
          time: "刚刚",
          location: "北京",
          isLiked: false,
          likeCount: 0,
          replies: [],
          totalReplies: 0
        };
        this.comments.unshift(newComment);
        this.postData.commentCount++;
        this.hideCommentModal();
        uni.showToast({
          title: "评论成功",
          icon: "success"
        });
      },
      changeSortType(type) {
        this.sortType = type;
      },
      toggleCommentLike(commentId) {
        const comment = this.comments.find((c) => c.id === commentId);
        if (comment) {
          comment.isLiked = !comment.isLiked;
          if (comment.isLiked) {
            comment.likeCount++;
          } else {
            comment.likeCount--;
          }
        }
      },
      replyToComment(commentId) {
        this.showCommentInput();
      },
      viewMoreReplies(commentId) {
        formatAppLog("log", "at pages/post-detail/post-detail.vue:356", "查看更多回复：", commentId);
      }
    }
  };
  function _sfc_render$e(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_TopNavigation = vue.resolveComponent("TopNavigation");
    return vue.openBlock(), vue.createElementBlock("view", { class: "post-detail-container" }, [
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createVNode(_component_TopNavigation, {
        title: "帖子正文",
        titleColor: "#8A70C9",
        showBack: true,
        showMore: true,
        onBack: $options.goBack,
        onMore: $options.showMore
      }, null, 8, ["onBack", "onMore"]),
      vue.createCommentVNode(" 帖子内容区域 "),
      vue.createElementVNode("view", { class: "post-content" }, [
        vue.createCommentVNode(" 用户信息 "),
        vue.createElementVNode("view", { class: "user-info" }, [
          vue.createElementVNode("view", { class: "user-avatar" }, [
            vue.createElementVNode("image", {
              src: $data.postData.userAvatar,
              class: "avatar-img",
              mode: "aspectFill"
            }, null, 8, ["src"])
          ]),
          vue.createElementVNode("view", { class: "user-details" }, [
            vue.createElementVNode(
              "text",
              { class: "username" },
              vue.toDisplayString($data.postData.username),
              1
              /* TEXT */
            ),
            vue.createElementVNode("view", { class: "post-meta" }, [
              vue.createElementVNode(
                "text",
                { class: "post-time" },
                vue.toDisplayString($data.postData.postTime),
                1
                /* TEXT */
              ),
              vue.createElementVNode(
                "text",
                { class: "post-location" },
                vue.toDisplayString($data.postData.location),
                1
                /* TEXT */
              )
            ])
          ]),
          vue.createElementVNode(
            "view",
            {
              class: vue.normalizeClass(["follow-btn", { "followed": $data.postData.isFollowed }]),
              onClick: _cache[0] || (_cache[0] = (...args) => $options.toggleFollow && $options.toggleFollow(...args))
            },
            [
              vue.createElementVNode(
                "text",
                { class: "follow-text" },
                vue.toDisplayString($data.postData.isFollowed ? "已关注" : "关注"),
                1
                /* TEXT */
              )
            ],
            2
            /* CLASS */
          )
        ]),
        vue.createCommentVNode(" 帖子标题 "),
        vue.createElementVNode("view", { class: "post-title" }, [
          vue.createElementVNode(
            "text",
            { class: "title-text" },
            vue.toDisplayString($data.postData.title),
            1
            /* TEXT */
          )
        ]),
        vue.createCommentVNode(" 帖子正文 "),
        vue.createElementVNode("view", { class: "post-body" }, [
          vue.createElementVNode(
            "text",
            { class: "body-text" },
            vue.toDisplayString($data.postData.content),
            1
            /* TEXT */
          )
        ]),
        vue.createCommentVNode(" 帖子图片 "),
        $data.postData.images && $data.postData.images.length > 0 ? (vue.openBlock(), vue.createElementBlock("view", {
          key: 0,
          class: "post-images"
        }, [
          (vue.openBlock(true), vue.createElementBlock(
            vue.Fragment,
            null,
            vue.renderList($data.postData.images, (image, index) => {
              return vue.openBlock(), vue.createElementBlock("view", {
                class: "image-item",
                key: index,
                onClick: ($event) => $options.previewImage(index)
              }, [
                vue.createElementVNode("image", {
                  src: image,
                  class: "post-image",
                  mode: "aspectFill"
                }, null, 8, ["src"])
              ], 8, ["onClick"]);
            }),
            128
            /* KEYED_FRAGMENT */
          ))
        ])) : vue.createCommentVNode("v-if", true),
        vue.createCommentVNode(" 相关场 "),
        vue.createElementVNode("view", { class: "related-section" }, [
          vue.createElementVNode("text", { class: "related-title" }, "相关场"),
          vue.createElementVNode("view", { class: "related-card" }, [
            vue.createElementVNode("view", { class: "related-avatar" }, [
              vue.createElementVNode("image", {
                src: $data.postData.relatedGroup.avatar,
                class: "related-avatar-img",
                mode: "aspectFill"
              }, null, 8, ["src"])
            ]),
            vue.createElementVNode("view", { class: "related-info" }, [
              vue.createElementVNode(
                "text",
                { class: "related-name" },
                vue.toDisplayString($data.postData.relatedGroup.name),
                1
                /* TEXT */
              ),
              vue.createElementVNode("view", { class: "related-stats" }, [
                vue.createElementVNode(
                  "text",
                  { class: "related-stat" },
                  vue.toDisplayString($data.postData.relatedGroup.followers) + "关注",
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "text",
                  { class: "related-stat" },
                  vue.toDisplayString($data.postData.relatedGroup.posts) + "帖子",
                  1
                  /* TEXT */
                )
              ])
            ]),
            vue.createElementVNode("view", {
              class: "join-btn",
              onClick: _cache[1] || (_cache[1] = (...args) => $options.joinGroup && $options.joinGroup(...args))
            }, [
              vue.createElementVNode("text", { class: "join-text" }, "进场")
            ])
          ])
        ])
      ]),
      vue.createCommentVNode(" 底部互动栏 "),
      vue.createElementVNode("view", { class: "bottom-actions" }, [
        vue.createElementVNode("view", {
          class: "comment-input",
          onClick: _cache[2] || (_cache[2] = (...args) => $options.showCommentInput && $options.showCommentInput(...args))
        }, [
          vue.createElementVNode("text", { class: "input-placeholder" }, "加入讨论？")
        ]),
        vue.createElementVNode("view", { class: "action-buttons" }, [
          vue.createElementVNode("view", {
            class: "action-btn",
            onClick: _cache[3] || (_cache[3] = (...args) => $options.toggleLike && $options.toggleLike(...args))
          }, [
            vue.createElementVNode(
              "image",
              {
                src: _imports_0$6,
                class: vue.normalizeClass(["action-icon", { "liked": $data.postData.isLiked }]),
                mode: "aspectFit"
              },
              null,
              2
              /* CLASS */
            ),
            vue.createElementVNode(
              "text",
              { class: "action-count" },
              vue.toDisplayString($data.postData.likeCount),
              1
              /* TEXT */
            )
          ]),
          vue.createElementVNode("view", {
            class: "action-btn",
            onClick: _cache[4] || (_cache[4] = (...args) => $options.toggleStar && $options.toggleStar(...args))
          }, [
            vue.createElementVNode(
              "image",
              {
                src: _imports_1$7,
                class: vue.normalizeClass(["action-icon", { "starred": $data.postData.isStared }]),
                mode: "aspectFit"
              },
              null,
              2
              /* CLASS */
            ),
            vue.createElementVNode(
              "text",
              { class: "action-count" },
              vue.toDisplayString($data.postData.starCount),
              1
              /* TEXT */
            )
          ]),
          vue.createElementVNode("view", {
            class: "action-btn",
            onClick: _cache[5] || (_cache[5] = (...args) => $options.scrollToComments && $options.scrollToComments(...args))
          }, [
            vue.createElementVNode("image", {
              src: _imports_2$5,
              class: "action-icon",
              mode: "aspectFit"
            }),
            vue.createElementVNode(
              "text",
              { class: "action-count" },
              vue.toDisplayString($data.postData.commentCount),
              1
              /* TEXT */
            )
          ])
        ])
      ]),
      vue.createCommentVNode(" 评论区域 "),
      vue.createElementVNode("view", { class: "comments-section" }, [
        vue.createCommentVNode(" 评论头部 "),
        vue.createElementVNode("view", { class: "comments-header" }, [
          vue.createElementVNode(
            "text",
            { class: "comments-title" },
            "评论" + vue.toDisplayString($data.postData.commentCount),
            1
            /* TEXT */
          ),
          vue.createElementVNode("view", { class: "sort-options" }, [
            vue.createElementVNode(
              "text",
              {
                class: vue.normalizeClass(["sort-option", { "active": $data.sortType === "hot" }]),
                onClick: _cache[6] || (_cache[6] = ($event) => $options.changeSortType("hot"))
              },
              "热度最高",
              2
              /* CLASS */
            ),
            vue.createElementVNode(
              "text",
              {
                class: vue.normalizeClass(["sort-option", { "active": $data.sortType === "time" }]),
                onClick: _cache[7] || (_cache[7] = ($event) => $options.changeSortType("time"))
              },
              "按热度",
              2
              /* CLASS */
            ),
            vue.createElementVNode("image", {
              src: _imports_3$4,
              class: "sort-menu",
              mode: "aspectFit"
            })
          ])
        ]),
        vue.createCommentVNode(" 分割线 "),
        vue.createElementVNode("view", { class: "divider" }),
        vue.createCommentVNode(" 评论列表 "),
        vue.createElementVNode("view", { class: "comments-list" }, [
          (vue.openBlock(true), vue.createElementBlock(
            vue.Fragment,
            null,
            vue.renderList($data.comments, (comment, index) => {
              return vue.openBlock(), vue.createElementBlock("view", {
                class: "comment-item",
                key: comment.id
              }, [
                vue.createElementVNode("view", { class: "comment-avatar" }, [
                  vue.createElementVNode("image", {
                    src: comment.userAvatar,
                    class: "comment-avatar-img",
                    mode: "aspectFill"
                  }, null, 8, ["src"])
                ]),
                vue.createElementVNode("view", { class: "comment-content" }, [
                  vue.createElementVNode(
                    "text",
                    { class: "comment-username" },
                    vue.toDisplayString(comment.username),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "text",
                    { class: "comment-text" },
                    vue.toDisplayString(comment.content),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode("view", { class: "comment-meta" }, [
                    vue.createElementVNode(
                      "text",
                      { class: "comment-time" },
                      vue.toDisplayString(comment.time),
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode(
                      "text",
                      { class: "comment-location" },
                      vue.toDisplayString(comment.location),
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode("text", {
                      class: "reply-btn",
                      onClick: ($event) => $options.replyToComment(comment.id)
                    }, "回复", 8, ["onClick"])
                  ]),
                  vue.createCommentVNode(" 评论的回复 "),
                  comment.replies && comment.replies.length > 0 ? (vue.openBlock(), vue.createElementBlock("view", {
                    key: 0,
                    class: "replies"
                  }, [
                    (vue.openBlock(true), vue.createElementBlock(
                      vue.Fragment,
                      null,
                      vue.renderList(comment.replies, (reply) => {
                        return vue.openBlock(), vue.createElementBlock("view", {
                          class: "reply-item",
                          key: reply.id
                        }, [
                          vue.createElementVNode(
                            "text",
                            { class: "reply-text" },
                            vue.toDisplayString(reply.username) + "：" + vue.toDisplayString(reply.content),
                            1
                            /* TEXT */
                          )
                        ]);
                      }),
                      128
                      /* KEYED_FRAGMENT */
                    )),
                    comment.totalReplies > comment.replies.length ? (vue.openBlock(), vue.createElementBlock("text", {
                      key: 0,
                      class: "view-more-replies",
                      onClick: ($event) => $options.viewMoreReplies(comment.id)
                    }, " 查看共" + vue.toDisplayString(comment.totalReplies) + "条回复 ", 9, ["onClick"])) : vue.createCommentVNode("v-if", true)
                  ])) : vue.createCommentVNode("v-if", true)
                ]),
                vue.createElementVNode("view", { class: "comment-actions" }, [
                  vue.createElementVNode("view", {
                    class: "comment-like",
                    onClick: ($event) => $options.toggleCommentLike(comment.id)
                  }, [
                    vue.createElementVNode(
                      "image",
                      {
                        src: _imports_0$6,
                        class: vue.normalizeClass(["comment-like-icon", { "liked": comment.isLiked }]),
                        mode: "aspectFit"
                      },
                      null,
                      2
                      /* CLASS */
                    ),
                    vue.createElementVNode(
                      "text",
                      { class: "comment-like-count" },
                      vue.toDisplayString(comment.likeCount),
                      1
                      /* TEXT */
                    )
                  ], 8, ["onClick"])
                ])
              ]);
            }),
            128
            /* KEYED_FRAGMENT */
          ))
        ])
      ]),
      vue.createCommentVNode(" 评论输入弹窗 "),
      $data.showCommentModal ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 0,
        class: "comment-modal",
        onClick: _cache[12] || (_cache[12] = (...args) => $options.hideCommentModal && $options.hideCommentModal(...args))
      }, [
        vue.createElementVNode("view", {
          class: "comment-modal-content",
          onClick: _cache[11] || (_cache[11] = vue.withModifiers(() => {
          }, ["stop"]))
        }, [
          vue.createElementVNode("view", { class: "modal-header" }, [
            vue.createElementVNode("text", { class: "modal-title" }, "发表评论"),
            vue.createElementVNode("text", {
              class: "modal-close",
              onClick: _cache[8] || (_cache[8] = (...args) => $options.hideCommentModal && $options.hideCommentModal(...args))
            }, "×")
          ]),
          vue.withDirectives(vue.createElementVNode(
            "textarea",
            {
              class: "comment-textarea",
              "onUpdate:modelValue": _cache[9] || (_cache[9] = ($event) => $data.commentText = $event),
              placeholder: "说点什么吧...",
              maxlength: "500"
            },
            null,
            512
            /* NEED_PATCH */
          ), [
            [vue.vModelText, $data.commentText]
          ]),
          vue.createElementVNode("view", { class: "modal-footer" }, [
            vue.createElementVNode(
              "text",
              { class: "char-count" },
              vue.toDisplayString($data.commentText.length) + "/500",
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["send-btn", { "active": $data.commentText.trim().length > 0 }]),
                onClick: _cache[10] || (_cache[10] = (...args) => $options.sendComment && $options.sendComment(...args))
              },
              [
                vue.createElementVNode("text", { class: "send-text" }, "发送")
              ],
              2
              /* CLASS */
            )
          ])
        ])
      ])) : vue.createCommentVNode("v-if", true)
    ]);
  }
  const PagesPostDetailPostDetail = /* @__PURE__ */ _export_sfc(_sfc_main$e, [["render", _sfc_render$e], ["__scopeId", "data-v-13500661"], ["__file", "D:/code space2/xystapp/android/pages/post-detail/post-detail.vue"]]);
  const _imports_2$3 = "/static/heart-fill.png";
  const _imports_1$2 = "/static/user-add-fill.png";
  const _imports_2$2 = "/static/chat-forward-fill.png";
  const _sfc_main$d = {
    name: "MessagePage",
    components: {
      BottomNavigation
    },
    data() {
      return {
        stats: {
          likeCount: 5,
          followCount: 55,
          commentCount: 294
        },
        messageList: [
          {
            id: 1,
            type: "private",
            name: "会吃西瓜的小鸭纸",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            preview: "今天出去打球吗？明个个？",
            time: "19:03",
            unreadCount: 99
          },
          {
            id: 2,
            type: "group",
            name: "lol群",
            avatar: "/static/关注页/follow-users-section/Ellipse 9.png",
            preview: "国服塞拉斯：你又欠打了？偷我图干嘛",
            time: "19:02",
            unreadCount: 15,
            notice: "入群须知: 大家可以随意组队打lol   不要把群...",
            activity: "lol线下比赛   2025.8.25   17:00-18:00"
          },
          {
            id: 3,
            type: "private",
            name: "你叫什么名字",
            avatar: "/static/关注页/follow-users-section/Ellipse 11.png",
            preview: "我们聚餐喝酒[合情]",
            time: "13:14",
            unreadCount: 299
          },
          {
            id: 4,
            type: "group",
            name: "羽毛球群",
            avatar: "/static/关注页/follow-users-section/Ellipse 13.png",
            preview: "不知名用户：这是老人新手警了",
            time: "13:00",
            unreadCount: 0
          },
          {
            id: 5,
            type: "group",
            name: "原神集会",
            avatar: "/static/关注页/follow-users-section/Ellipse 14.png",
            preview: "群主：发支付宝账号码吧",
            time: "10:21",
            unreadCount: 901
          },
          {
            id: 6,
            type: "group",
            name: "鸣潮集会",
            avatar: "/static/关注页/follow-users-section/Ellipse 15.png",
            preview: "群主：有没有人陪每日刷在不来点个赞...",
            time: "09:05",
            unreadCount: 99
          },
          {
            id: 7,
            type: "group",
            name: "锋芒值YDs",
            avatar: "/static/关注页/follow-users-section/Ellipse 16.png",
            preview: "请奇大人的朋：暴雪游戏大人的朋友[图...",
            time: "08:04",
            unreadCount: 0
          }
        ]
      };
    },
    computed: {
      totalUnreadCount() {
        return this.messageList.reduce((total, message) => total + message.unreadCount, 0);
      }
    },
    methods: {
      goToPage(page) {
        const routes = {
          "like-collect": "/pages/message/like-collect",
          "follow-subscribe": "/pages/message/follow-subscribe",
          "comment-at": "/pages/message/comment-at"
        };
        if (routes[page]) {
          uni.navigateTo({
            url: routes[page]
          });
        }
      },
      openMessage(message) {
        let url = `/pages/message/chat-detail?type=${message.type || "private"}&id=${message.id}&name=${encodeURIComponent(message.name)}&avatar=${encodeURIComponent(message.avatar)}`;
        if (message.type === "group") {
          if (message.notice) {
            url += `&notice=${encodeURIComponent(message.notice)}`;
          }
          if (message.activity) {
            url += `&activity=${encodeURIComponent(message.activity)}`;
          }
        }
        uni.navigateTo({
          url
        });
      }
    }
  };
  function _sfc_render$d(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_BottomNavigation = vue.resolveComponent("BottomNavigation");
    return vue.openBlock(), vue.createElementBlock("view", { class: "message-container" }, [
      vue.createCommentVNode(" 顶部状态栏占位 "),
      vue.createElementVNode("view", { class: "status-bar" }),
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createElementVNode("view", { class: "top-nav" }, [
        vue.createElementVNode("view", { class: "nav-center" }, [
          vue.createElementVNode("text", { class: "nav-title" }, "消息")
        ])
      ]),
      vue.createCommentVNode(" 消息统计卡片 "),
      vue.createElementVNode("view", { class: "message-stats-card" }, [
        vue.createElementVNode("view", {
          class: "stats-item",
          onClick: _cache[0] || (_cache[0] = ($event) => $options.goToPage("like-collect"))
        }, [
          vue.createElementVNode("view", { class: "stats-icon like-icon" }, [
            vue.createElementVNode("image", {
              src: _imports_2$3,
              class: "icon-img"
            })
          ]),
          $data.stats.likeCount > 0 ? (vue.openBlock(), vue.createElementBlock("view", {
            key: 0,
            class: "stats-badge"
          }, [
            vue.createElementVNode(
              "text",
              { class: "badge-text" },
              vue.toDisplayString($data.stats.likeCount),
              1
              /* TEXT */
            )
          ])) : vue.createCommentVNode("v-if", true),
          vue.createElementVNode("text", { class: "stats-text" }, "点赞收藏")
        ]),
        vue.createElementVNode("view", {
          class: "stats-item",
          onClick: _cache[1] || (_cache[1] = ($event) => $options.goToPage("follow-subscribe"))
        }, [
          vue.createElementVNode("view", { class: "stats-icon follow-icon" }, [
            vue.createElementVNode("image", {
              src: _imports_1$2,
              class: "icon-img"
            })
          ]),
          $data.stats.followCount > 0 ? (vue.openBlock(), vue.createElementBlock("view", {
            key: 0,
            class: "stats-badge"
          }, [
            vue.createElementVNode(
              "text",
              { class: "badge-text" },
              vue.toDisplayString($data.stats.followCount),
              1
              /* TEXT */
            )
          ])) : vue.createCommentVNode("v-if", true),
          vue.createElementVNode("text", { class: "stats-text" }, "关注订阅")
        ]),
        vue.createElementVNode("view", {
          class: "stats-item",
          onClick: _cache[2] || (_cache[2] = ($event) => $options.goToPage("comment-at"))
        }, [
          vue.createElementVNode("view", { class: "stats-icon comment-icon" }, [
            vue.createElementVNode("image", {
              src: _imports_2$2,
              class: "icon-img"
            })
          ]),
          $data.stats.commentCount > 0 ? (vue.openBlock(), vue.createElementBlock("view", {
            key: 0,
            class: "stats-badge"
          }, [
            vue.createElementVNode(
              "text",
              { class: "badge-text" },
              vue.toDisplayString($data.stats.commentCount),
              1
              /* TEXT */
            )
          ])) : vue.createCommentVNode("v-if", true),
          vue.createElementVNode("text", { class: "stats-text" }, "评论@转发")
        ])
      ]),
      vue.createCommentVNode(" 消息列表 "),
      vue.createElementVNode("view", { class: "message-list" }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($data.messageList, (message, index) => {
            return vue.openBlock(), vue.createElementBlock("view", {
              class: "message-item",
              key: index,
              onClick: ($event) => $options.openMessage(message)
            }, [
              vue.createElementVNode("view", { class: "message-avatar" }, [
                vue.createElementVNode("image", {
                  src: message.avatar,
                  class: "avatar-img"
                }, null, 8, ["src"]),
                message.unreadCount > 0 ? (vue.openBlock(), vue.createElementBlock("view", {
                  key: 0,
                  class: "message-count-badge"
                }, [
                  vue.createElementVNode(
                    "text",
                    { class: "count-text" },
                    vue.toDisplayString(message.unreadCount),
                    1
                    /* TEXT */
                  )
                ])) : vue.createCommentVNode("v-if", true)
              ]),
              vue.createElementVNode("view", { class: "message-content" }, [
                vue.createElementVNode("view", { class: "message-header" }, [
                  vue.createElementVNode(
                    "text",
                    { class: "message-name" },
                    vue.toDisplayString(message.name),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "text",
                    { class: "message-time" },
                    vue.toDisplayString(message.time),
                    1
                    /* TEXT */
                  )
                ]),
                vue.createElementVNode(
                  "text",
                  { class: "message-preview" },
                  vue.toDisplayString(message.preview),
                  1
                  /* TEXT */
                )
              ])
            ], 8, ["onClick"]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ]),
      vue.createCommentVNode(" 底部导航栏 "),
      vue.createVNode(_component_BottomNavigation, {
        currentPage: "message",
        messageCount: $options.totalUnreadCount
      }, null, 8, ["messageCount"])
    ]);
  }
  const PagesMessageMessage = /* @__PURE__ */ _export_sfc(_sfc_main$d, [["render", _sfc_render$d], ["__scopeId", "data-v-4c1b26cf"], ["__file", "D:/code space2/xystapp/android/pages/message/message.vue"]]);
  const _sfc_main$c = {
    name: "LikeCollectPage",
    data() {
      return {
        messageList: [
          {
            id: 1,
            name: "会吃西瓜的小鸭纸",
            tag: "你的好友",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            action: "赞了你的评论",
            time: "1小时前",
            quote: "钻石段位真的易如反掌美，反正我黑铁...",
            image: "/static/关注页/Rectangle 107.png"
          },
          {
            id: 2,
            name: "会吃西瓜的小鸭纸",
            tag: "你的好友",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            action: "赞了你的评论",
            time: "2小时前",
            quote: "别骂了别骂了",
            image: "/static/关注页/Rectangle 108.png"
          },
          {
            id: 3,
            name: "不知名用户",
            tag: "你的关注",
            avatar: "/static/关注页/follow-users-section/Ellipse 9.png",
            action: "赞了你的评论",
            time: "2小时前",
            quote: "别骂了别骂了",
            image: "/static/关注页/Rectangle 109.png"
          },
          {
            id: 4,
            name: "你叫什么名字",
            tag: "你的粉丝",
            avatar: "/static/关注页/follow-users-section/Ellipse 11.png",
            action: "赞了你的帖子",
            time: "1天前",
            quote: "",
            image: "/static/关注页/Rectangle 107.png"
          },
          {
            id: 5,
            name: "点赞人机",
            tag: "",
            avatar: "/static/关注页/follow-users-section/Ellipse 13.png",
            action: "赞了你的评论",
            time: "1天前",
            quote: "别骂了别骂了",
            image: "/static/关注页/Rectangle 108.png"
          },
          {
            id: 6,
            name: "点赞人机",
            tag: "",
            avatar: "/static/关注页/follow-users-section/Ellipse 14.png",
            action: "收藏了你的帖子",
            time: "1天前",
            quote: "",
            image: "/static/关注页/Rectangle 109.png"
          }
        ]
      };
    },
    methods: {
      goBack() {
        uni.navigateBack();
      },
      openMessage(message) {
        formatAppLog("log", "at pages/message/like-collect.vue:126", "查看详情:", message);
      }
    }
  };
  function _sfc_render$c(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "like-collect-container" }, [
      vue.createCommentVNode(" 顶部状态栏占位 "),
      vue.createElementVNode("view", { class: "status-bar" }),
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createElementVNode("view", { class: "top-nav" }, [
        vue.createElementVNode("view", {
          class: "nav-left",
          onClick: _cache[0] || (_cache[0] = (...args) => $options.goBack && $options.goBack(...args))
        }, [
          vue.createElementVNode("image", {
            src: _imports_1$4,
            class: "back-icon"
          })
        ]),
        vue.createElementVNode("view", { class: "nav-center" }, [
          vue.createElementVNode("text", { class: "nav-title" }, "点赞收藏")
        ])
      ]),
      vue.createCommentVNode(" 消息列表 "),
      vue.createElementVNode("view", { class: "message-list" }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($data.messageList, (message, index) => {
            return vue.openBlock(), vue.createElementBlock("view", {
              class: "message-item",
              key: index,
              onClick: ($event) => $options.openMessage(message)
            }, [
              vue.createElementVNode("view", { class: "message-avatar" }, [
                vue.createElementVNode("image", {
                  src: message.avatar,
                  class: "avatar-img"
                }, null, 8, ["src"])
              ]),
              vue.createElementVNode("view", { class: "message-content" }, [
                vue.createElementVNode("view", { class: "message-header" }, [
                  vue.createElementVNode(
                    "text",
                    { class: "message-name" },
                    vue.toDisplayString(message.name),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode(
                    "text",
                    { class: "message-tag" },
                    vue.toDisplayString(message.tag),
                    1
                    /* TEXT */
                  )
                ]),
                vue.createElementVNode(
                  "text",
                  { class: "message-action" },
                  vue.toDisplayString(message.action),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "text",
                  { class: "message-time" },
                  vue.toDisplayString(message.time),
                  1
                  /* TEXT */
                ),
                vue.createCommentVNode(" 引用内容 "),
                message.quote ? (vue.openBlock(), vue.createElementBlock("view", {
                  key: 0,
                  class: "quote-content"
                }, [
                  vue.createElementVNode(
                    "text",
                    { class: "quote-text" },
                    vue.toDisplayString(message.quote),
                    1
                    /* TEXT */
                  )
                ])) : vue.createCommentVNode("v-if", true)
              ]),
              vue.createCommentVNode(" 相关图片 "),
              message.image ? (vue.openBlock(), vue.createElementBlock("view", {
                key: 0,
                class: "message-image"
              }, [
                vue.createElementVNode("image", {
                  src: message.image,
                  class: "post-img"
                }, null, 8, ["src"])
              ])) : vue.createCommentVNode("v-if", true)
            ], 8, ["onClick"]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ]),
      vue.createCommentVNode(" 历史记录入口 "),
      vue.createElementVNode("view", { class: "history-section" }, [
        vue.createElementVNode("text", { class: "history-link" }, "历史点赞收藏")
      ])
    ]);
  }
  const PagesMessageLikeCollect = /* @__PURE__ */ _export_sfc(_sfc_main$c, [["render", _sfc_render$c], ["__scopeId", "data-v-c410dafa"], ["__file", "D:/code space2/xystapp/android/pages/message/like-collect.vue"]]);
  const _sfc_main$b = {
    name: "FollowSubscribePage",
    data() {
      return {
        messageList: [
          {
            id: 1,
            name: "会吃西瓜的小鸭纸",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            action: "关注了你",
            time: "1小时前",
            isFollowed: true
          },
          {
            id: 2,
            name: "你叫什么名字",
            avatar: "/static/关注页/follow-users-section/Ellipse 11.png",
            action: "关注了你",
            time: "1天前",
            isFollowed: false
          }
        ]
      };
    },
    methods: {
      goBack() {
        uni.navigateBack();
      },
      toggleFollow(message) {
        message.isFollowed = !message.isFollowed;
      }
    }
  };
  function _sfc_render$b(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "follow-subscribe-container" }, [
      vue.createCommentVNode(" 顶部状态栏占位 "),
      vue.createElementVNode("view", { class: "status-bar" }),
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createElementVNode("view", { class: "top-nav" }, [
        vue.createElementVNode("view", {
          class: "nav-left",
          onClick: _cache[0] || (_cache[0] = (...args) => $options.goBack && $options.goBack(...args))
        }, [
          vue.createElementVNode("image", {
            src: _imports_1$4,
            class: "back-icon"
          })
        ]),
        vue.createElementVNode("view", { class: "nav-center" }, [
          vue.createElementVNode("text", { class: "nav-title" }, "关注订阅")
        ])
      ]),
      vue.createCommentVNode(" 消息列表 "),
      vue.createElementVNode("view", { class: "message-list" }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($data.messageList, (message, index) => {
            return vue.openBlock(), vue.createElementBlock("view", {
              class: "message-item",
              key: index
            }, [
              vue.createElementVNode("view", { class: "message-avatar" }, [
                vue.createElementVNode("image", {
                  src: message.avatar,
                  class: "avatar-img"
                }, null, 8, ["src"])
              ]),
              vue.createElementVNode("view", { class: "message-content" }, [
                vue.createElementVNode("view", { class: "message-header" }, [
                  vue.createElementVNode(
                    "text",
                    { class: "message-name" },
                    vue.toDisplayString(message.name),
                    1
                    /* TEXT */
                  ),
                  vue.createElementVNode("view", {
                    class: vue.normalizeClass(["action-btn", { "followed": message.isFollowed }]),
                    onClick: ($event) => $options.toggleFollow(message)
                  }, [
                    vue.createElementVNode(
                      "text",
                      { class: "action-text" },
                      vue.toDisplayString(message.isFollowed ? "互相关注" : "回关"),
                      1
                      /* TEXT */
                    )
                  ], 10, ["onClick"])
                ]),
                vue.createElementVNode(
                  "text",
                  { class: "message-action" },
                  vue.toDisplayString(message.action),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "text",
                  { class: "message-time" },
                  vue.toDisplayString(message.time),
                  1
                  /* TEXT */
                )
              ])
            ]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ]),
      vue.createCommentVNode(" 历史记录入口 "),
      vue.createElementVNode("view", { class: "history-section" }, [
        vue.createElementVNode("text", { class: "history-link" }, "历史关注订阅")
      ])
    ]);
  }
  const PagesMessageFollowSubscribe = /* @__PURE__ */ _export_sfc(_sfc_main$b, [["render", _sfc_render$b], ["__scopeId", "data-v-449ef549"], ["__file", "D:/code space2/xystapp/android/pages/message/follow-subscribe.vue"]]);
  const _sfc_main$a = {
    name: "CommentAtPage",
    data() {
      return {
        messageList: [
          {
            id: 1,
            name: "会吃西瓜的小鸭纸",
            tag: "你的好友",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            action: "评论了你的帖子",
            time: "1小时前",
            quote: "钻石段位真的易如反掌美，反正我黑铁...",
            comment: "哈哈哈，我也是黑铁选手",
            image: "/static/关注页/Rectangle 107.png"
          },
          {
            id: 2,
            name: "不知名用户",
            tag: "你的关注",
            avatar: "/static/关注页/follow-users-section/Ellipse 9.png",
            action: "@了你",
            time: "2小时前",
            quote: "今天天气不错，适合出去玩",
            comment: "@会吃西瓜的小鸭纸 一起去打球吗？",
            image: "/static/关注页/Rectangle 108.png"
          },
          {
            id: 3,
            name: "你叫什么名字",
            tag: "你的粉丝",
            avatar: "/static/关注页/follow-users-section/Ellipse 11.png",
            action: "转发了你的帖子",
            time: "3小时前",
            quote: "分享一个很棒的游戏攻略",
            comment: "这个攻略太实用了！",
            image: "/static/关注页/Rectangle 109.png"
          },
          {
            id: 4,
            name: "羽毛球群",
            tag: "",
            avatar: "/static/关注页/follow-users-section/Ellipse 13.png",
            action: "在群里@了你",
            time: "1天前",
            quote: "",
            comment: "@会吃西瓜的小鸭纸 明天有时间打球吗？",
            image: null
          },
          {
            id: 5,
            name: "原神集会",
            tag: "",
            avatar: "/static/关注页/follow-users-section/Ellipse 14.png",
            action: "回复了你的评论",
            time: "1天前",
            quote: "今天的副本难度有点高",
            comment: "是的，需要配合好队友",
            image: "/static/关注页/Rectangle 107.png"
          }
        ]
      };
    },
    methods: {
      goBack() {
        uni.navigateBack();
      },
      openMessage(message) {
        formatAppLog("log", "at pages/message/comment-at.vue:126", "查看详情:", message);
      }
    }
  };
  function _sfc_render$a(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "comment-at-container" }, [
      vue.createCommentVNode(" 顶部状态栏占位 "),
      vue.createElementVNode("view", { class: "status-bar" }),
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createElementVNode("view", { class: "top-nav" }, [
        vue.createElementVNode("view", {
          class: "nav-left",
          onClick: _cache[0] || (_cache[0] = (...args) => $options.goBack && $options.goBack(...args))
        }, [
          vue.createElementVNode("image", {
            src: _imports_1$4,
            class: "back-icon"
          })
        ]),
        vue.createElementVNode("view", { class: "nav-center" }, [
          vue.createElementVNode("text", { class: "nav-title" }, "评论@转发")
        ])
      ]),
      vue.createCommentVNode(" 消息列表 "),
      vue.createElementVNode("view", { class: "message-list" }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($data.messageList, (message, index) => {
            return vue.openBlock(), vue.createElementBlock("view", {
              class: "message-item",
              key: index,
              onClick: ($event) => $options.openMessage(message)
            }, [
              vue.createElementVNode("view", { class: "message-avatar" }, [
                vue.createElementVNode("image", {
                  src: message.avatar,
                  class: "avatar-img"
                }, null, 8, ["src"])
              ]),
              vue.createElementVNode("view", { class: "message-content" }, [
                vue.createElementVNode("view", { class: "message-header" }, [
                  vue.createElementVNode(
                    "text",
                    { class: "message-name" },
                    vue.toDisplayString(message.name),
                    1
                    /* TEXT */
                  ),
                  message.tag ? (vue.openBlock(), vue.createElementBlock(
                    "text",
                    {
                      key: 0,
                      class: "message-tag"
                    },
                    vue.toDisplayString(message.tag),
                    1
                    /* TEXT */
                  )) : vue.createCommentVNode("v-if", true)
                ]),
                vue.createElementVNode(
                  "text",
                  { class: "message-action" },
                  vue.toDisplayString(message.action),
                  1
                  /* TEXT */
                ),
                vue.createElementVNode(
                  "text",
                  { class: "message-time" },
                  vue.toDisplayString(message.time),
                  1
                  /* TEXT */
                ),
                vue.createCommentVNode(" 引用内容 "),
                message.quote ? (vue.openBlock(), vue.createElementBlock("view", {
                  key: 0,
                  class: "quote-content"
                }, [
                  vue.createElementVNode(
                    "text",
                    { class: "quote-text" },
                    vue.toDisplayString(message.quote),
                    1
                    /* TEXT */
                  )
                ])) : vue.createCommentVNode("v-if", true),
                vue.createCommentVNode(" 评论内容 "),
                message.comment ? (vue.openBlock(), vue.createElementBlock("view", {
                  key: 1,
                  class: "comment-content"
                }, [
                  vue.createElementVNode(
                    "text",
                    { class: "comment-text" },
                    vue.toDisplayString(message.comment),
                    1
                    /* TEXT */
                  )
                ])) : vue.createCommentVNode("v-if", true)
              ]),
              vue.createCommentVNode(" 相关图片 "),
              message.image ? (vue.openBlock(), vue.createElementBlock("view", {
                key: 0,
                class: "message-image"
              }, [
                vue.createElementVNode("image", {
                  src: message.image,
                  class: "post-img"
                }, null, 8, ["src"])
              ])) : vue.createCommentVNode("v-if", true)
            ], 8, ["onClick"]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ]),
      vue.createCommentVNode(" 历史记录入口 "),
      vue.createElementVNode("view", { class: "history-section" }, [
        vue.createElementVNode("text", { class: "history-link" }, "历史评论@转发")
      ])
    ]);
  }
  const PagesMessageCommentAt = /* @__PURE__ */ _export_sfc(_sfc_main$a, [["render", _sfc_render$a], ["__scopeId", "data-v-d69a6ce2"], ["__file", "D:/code space2/xystapp/android/pages/message/comment-at.vue"]]);
  const _imports_0$2 = "/static/关注页/Rectangle 230.png";
  const _imports_1$1 = "/static/活动详情/1.png";
  const _imports_3$1 = "/static/活动详情/2.png";
  const _imports_4$1 = "/static/关注页/follow-users-section/Ellipse 9.png";
  const _imports_5$1 = "/static/关注页/follow-users-section/Ellipse 2.png";
  const _imports_6 = "/static/活动详情/4.png";
  const _imports_7 = "/static/活动详情/5.png";
  const _imports_8 = "/static/活动详情/3.png";
  const _sfc_main$9 = {
    name: "EventDetail",
    data() {
      return {
        showTopNav: false,
        activeTab: "intro",
        scrollTop: 0,
        signupUsers: [
          { avatar: "/static/关注页/follow-users-section/Ellipse 9.png", name: "路人1", showPlus: true },
          { avatar: "/static/关注页/follow-users-section/Ellipse 2.png", name: "好友1", showPlus: false },
          { avatar: "/static/关注页/follow-users-section/Ellipse 9.png", name: "名字长...", showPlus: true },
          { avatar: "/static/关注页/follow-users-section/Ellipse 2.png", name: "四个字的", showPlus: true },
          { avatar: "/static/关注页/follow-users-section/Ellipse 9.png", name: "好友2", showPlus: false },
          { avatar: "/static/关注页/follow-users-section/Ellipse 2.png", name: "好友3", showPlus: false },
          { avatar: "/static/关注页/follow-users-section/Ellipse 9.png", name: "更多...", showPlus: false }
        ],
        comments: [
          {
            avatar: "/static/关注页/follow-users-section/Ellipse 9.png",
            username: "我是评论人机",
            likes: 125,
            content: "试试看评论是什么样的呢感觉还有意思的可以多写点看看效果怎么样。^^",
            mention: "@会吃西瓜的小鸭纸",
            time: "1小时前",
            location: "北京",
            replies: [
              { content: "我是评论人机的评论人机：没错我就是来测试一下评论的评论怎么样的" },
              { content: "我不是人机：怎么全是测试评论的人机？" }
            ],
            totalReplies: 8
          },
          {
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            username: "我是评论人机2号",
            likes: 15,
            content: "我也试试看评论是什么样的呢感觉还挺有意思的可以多写点看看效果怎么样",
            mention: "@不会吃西瓜的小鸭纸",
            time: "2小时前",
            location: "北京",
            replies: [
              { content: "我是评论人机的评论人机2：没错我就是来测试一下评论的评论怎么样的" },
              { content: "我不是人机2：怎么全是测试评论的人机？" }
            ],
            totalReplies: 3
          }
        ]
      };
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
          title: "报名成功",
          icon: "success"
        });
      },
      switchToComment() {
        this.activeTab = "comment";
      },
      switchToSignup() {
        this.activeTab = "signup";
      }
    }
  };
  function _sfc_render$9(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { class: "event-detail" }, [
      vue.createCommentVNode(" 顶部背景图片 "),
      vue.createElementVNode(
        "view",
        {
          class: vue.normalizeClass(["header-bg", { "hidden": $data.showTopNav }])
        },
        [
          vue.createElementVNode("image", {
            class: "bg-image",
            src: _imports_0$2,
            mode: "aspectFill"
          }),
          vue.createCommentVNode(' <view class="bg-overlay"></view> '),
          vue.createCommentVNode(" 返回按钮 "),
          vue.createElementVNode("view", {
            class: "back-btn",
            onClick: _cache[0] || (_cache[0] = (...args) => $options.goBack && $options.goBack(...args))
          }, [
            vue.createElementVNode("text", { class: "back-icon" }, "‹")
          ]),
          vue.createCommentVNode(" 更多按钮 "),
          vue.createElementVNode("view", { class: "more-btn" }, [
            vue.createElementVNode("text", { class: "more-icon" }, "⋯")
          ])
        ],
        2
        /* CLASS */
      ),
      vue.createCommentVNode(" 顶部导航栏（滚动时显示活动标题） "),
      vue.createElementVNode(
        "view",
        {
          class: vue.normalizeClass(["top-nav", { "show": $data.showTopNav }])
        },
        [
          vue.createElementVNode("view", { class: "nav-content" }, [
            vue.createElementVNode("view", {
              class: "nav-back",
              onClick: _cache[1] || (_cache[1] = (...args) => $options.goBack && $options.goBack(...args))
            }, [
              vue.createElementVNode("text", { class: "nav-back-icon" }, "‹")
            ]),
            vue.createCommentVNode(" 活动标题 "),
            vue.createElementVNode("view", { class: "nav-title" }, [
              vue.createElementVNode("text", { class: "nav-title-text" }, "阴阳师线下交流会：阴阳师校园集结令...")
            ]),
            vue.createElementVNode("view", { class: "nav-more" }, [
              vue.createElementVNode("text", { class: "nav-more-icon" }, "⋯")
            ])
          ])
        ],
        2
        /* CLASS */
      ),
      vue.createCommentVNode(" 主要内容 "),
      vue.createElementVNode("scroll-view", {
        class: "content-scroll",
        "scroll-y": "",
        onScroll: _cache[5] || (_cache[5] = (...args) => $options.onScroll && $options.onScroll(...args)),
        "scroll-top": $data.scrollTop
      }, [
        vue.createCommentVNode(" 基本信息卡片 "),
        vue.createElementVNode("view", { class: "info-card" }, [
          vue.createElementVNode("view", { class: "event-title" }, " 阴阳师线下交流会：阴阳师校园集结令！这场平安京邀约，等你来赴 "),
          vue.createCommentVNode(" 时间和感兴趣信息 "),
          vue.createElementVNode("view", { class: "time-interest-row" }, [
            vue.createElementVNode("view", { class: "time-item" }, [
              vue.createElementVNode("image", {
                class: "time-icon",
                src: _imports_1$1
              }),
              vue.createElementVNode("text", { class: "time-text" }, "2025.8.16"),
              vue.createElementVNode("text", { class: "time-text" }, "周六"),
              vue.createElementVNode("text", { class: "time-text" }, "17:00 - 18:00")
            ]),
            vue.createElementVNode("view", { class: "interest-info" }, [
              vue.createElementVNode("image", {
                class: "heart-icon",
                src: _imports_2$3
              }),
              vue.createElementVNode("text", { class: "interest-text" }, "23人感兴趣")
            ])
          ]),
          vue.createCommentVNode(" 地点信息 "),
          vue.createElementVNode("view", { class: "location-info" }, [
            vue.createElementVNode("image", {
              class: "location-icon",
              src: _imports_3$1
            }),
            vue.createElementVNode("text", { class: "location-text" }, "中央民族大学 | 体育馆2楼")
          ])
        ]),
        vue.createCommentVNode(" 选项卡切换（滑动时或切换到非简介tab时显示） "),
        vue.createElementVNode(
          "view",
          {
            class: vue.normalizeClass(["content-tabs", { "show": $data.showTopNav || $data.activeTab !== "intro" }])
          },
          [
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["content-tab", { "active": $data.activeTab === "intro" }]),
                onClick: _cache[2] || (_cache[2] = ($event) => $options.switchTab("intro"))
              },
              " 简介 ",
              2
              /* CLASS */
            ),
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["content-tab", { "active": $data.activeTab === "signup" }]),
                onClick: _cache[3] || (_cache[3] = ($event) => $options.switchTab("signup"))
              },
              " 报名 ",
              2
              /* CLASS */
            ),
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["content-tab", { "active": $data.activeTab === "comment" }]),
                onClick: _cache[4] || (_cache[4] = ($event) => $options.switchTab("comment"))
              },
              " 评论 ",
              2
              /* CLASS */
            )
          ],
          2
          /* CLASS */
        ),
        vue.createCommentVNode(" 活动简介区域 "),
        $data.activeTab === "intro" ? (vue.openBlock(), vue.createElementBlock("view", {
          key: 0,
          class: "intro-section"
        }, [
          vue.createElementVNode("view", { class: "section-title" }, "活动简介"),
          vue.createCommentVNode(" 发起人信息 "),
          vue.createElementVNode("view", { class: "organizer-card" }, [
            vue.createElementVNode("image", {
              class: "organizer-avatar",
              src: _imports_4$1
            }),
            vue.createElementVNode("view", { class: "organizer-info" }, [
              vue.createElementVNode("text", { class: "organizer-name" }, "阴阳师场"),
              vue.createElementVNode("view", { class: "organizer-stats" }, [
                vue.createElementVNode("text", { class: "stat-text" }, "100关注"),
                vue.createElementVNode("text", { class: "stat-text" }, "15帖子")
              ])
            ]),
            vue.createElementVNode("view", { class: "enter-btn" }, [
              vue.createElementVNode("text", { class: "enter-text" }, "进场")
            ])
          ]),
          vue.createCommentVNode(" 活动发起人 "),
          vue.createElementVNode("view", { class: "creator-info" }, [
            vue.createElementVNode("image", {
              class: "creator-avatar",
              src: _imports_5$1
            }),
            vue.createElementVNode("text", { class: "creator-name" }, "我名字叫活动发起人")
          ]),
          vue.createCommentVNode(" 活动详细描述 "),
          vue.createElementVNode("view", { class: "event-description" }, [
            vue.createElementVNode("text", { class: "description-text" }, " 平安京的风，吹进你的校园啦！阴阳师校园专属活动即将开启，四大趣味玩法带你沉浸式玩转式神世界，还有限定好礼拿到手软！ "),
            vue.createElementVNode("text", { class: "description-text" }, ' 想证明自己是"资深痒痒鼠"？阴阳师知识赛等你来战！从式神技能到剧情伏笔，答对就能赢取式神主题周边；想化身心仪式神？cosplay 打卡开启，穿式神服装拍校园美照，集赞兑换专属纪念礼，还原度高还能拿额外奖励；热衷指尖对决？式神技能赛上线，操控本命式神同台竞技，勾玉、金币等游戏道具轻松赢；爱创作的你别错过同人创作赛，漫画、小说、绘画均可投稿，优秀作品不仅校内展览，还可能被制成周边！ '),
            vue.createElementVNode("text", { class: "description-text" }, " 快约上同校寮友，一起在校园里解锁平安京的专属快乐，这场属于阴阳师玩家的校园狂欢，就等你加入！ "),
            vue.createElementVNode("text", { class: "description-text" }, " 快约上同校寮友，一起在校园里解锁平安京的专属快乐，这场属于阴阳师玩家的校园狂欢，就等你加入！ "),
            vue.createElementVNode("text", { class: "description-text" }, " 快约上同校寮友，一起在校园里解锁平安京的专属快乐，这场属于阴阳师玩家的校园狂欢，就等你加入！ "),
            vue.createElementVNode("text", { class: "description-text" }, " 快约上同校寮友，一起在校园里解锁平安京的专属快乐，这场属于阴阳师玩家的校园狂欢，就等你加入！ "),
            vue.createElementVNode("text", { class: "description-text" }, " 快约上同校寮友，一起在校园里解锁平安京的专属快乐，这场属于阴阳师玩家的校园狂欢，就等你加入！ "),
            vue.createElementVNode("text", { class: "description-text" }, " 快约上同校寮友，一起在校园里解锁平安京的专属快乐，这场属于阴阳师玩家的校园狂欢，就等你加入！ ")
          ])
        ])) : vue.createCommentVNode("v-if", true),
        vue.createCommentVNode(" 报名区域 "),
        $data.activeTab === "signup" ? (vue.openBlock(), vue.createElementBlock(
          "view",
          {
            key: 1,
            class: "signup-section",
            style: vue.normalizeStyle({ paddingTop: $data.showTopNav || $data.activeTab !== "intro" ? "70px" : "20px" })
          },
          [
            vue.createElementVNode("view", { class: "signup-header" }, [
              vue.createElementVNode("text", { class: "signup-title" }, "已报名7人"),
              vue.createElementVNode("view", { class: "view-all" }, [
                vue.createElementVNode("text", { class: "view-all-text" }, "全部"),
                vue.createElementVNode("text", { class: "view-all-arrow" }, ">")
              ])
            ]),
            vue.createElementVNode("view", { class: "signup-users" }, [
              (vue.openBlock(true), vue.createElementBlock(
                vue.Fragment,
                null,
                vue.renderList($data.signupUsers, (user, index) => {
                  return vue.openBlock(), vue.createElementBlock("view", {
                    class: "user-item",
                    key: index
                  }, [
                    vue.createElementVNode("image", {
                      class: "user-avatar",
                      src: user.avatar
                    }, null, 8, ["src"]),
                    user.showPlus ? (vue.openBlock(), vue.createElementBlock("view", {
                      key: 0,
                      class: "user-plus"
                    }, "+")) : vue.createCommentVNode("v-if", true),
                    vue.createElementVNode(
                      "text",
                      { class: "user-name" },
                      vue.toDisplayString(user.name),
                      1
                      /* TEXT */
                    )
                  ]);
                }),
                128
                /* KEYED_FRAGMENT */
              ))
            ])
          ],
          4
          /* STYLE */
        )) : vue.createCommentVNode("v-if", true),
        vue.createCommentVNode(" 评论区域 "),
        $data.activeTab === "comment" ? (vue.openBlock(), vue.createElementBlock(
          "view",
          {
            key: 2,
            class: "comment-section",
            style: vue.normalizeStyle({ paddingTop: $data.showTopNav || $data.activeTab !== "intro" ? "70px" : "20px" })
          },
          [
            vue.createElementVNode("view", { class: "comment-header" }, [
              vue.createElementVNode("text", { class: "comment-title" }, "评论15"),
              vue.createElementVNode("view", { class: "sort-options" }, [
                vue.createElementVNode("text", { class: "sort-text" }, "热度最高"),
                vue.createElementVNode("view", { class: "sort-icon" }, "⋯"),
                vue.createElementVNode("text", { class: "sort-by-heat" }, "按热度")
              ])
            ]),
            vue.createElementVNode("view", { class: "comment-list" }, [
              (vue.openBlock(true), vue.createElementBlock(
                vue.Fragment,
                null,
                vue.renderList($data.comments, (comment, index) => {
                  return vue.openBlock(), vue.createElementBlock("view", {
                    class: "comment-item",
                    key: index
                  }, [
                    vue.createElementVNode("image", {
                      class: "comment-avatar",
                      src: comment.avatar
                    }, null, 8, ["src"]),
                    vue.createElementVNode("view", { class: "comment-content" }, [
                      vue.createElementVNode("view", { class: "comment-user" }, [
                        vue.createElementVNode(
                          "text",
                          { class: "comment-username" },
                          vue.toDisplayString(comment.username),
                          1
                          /* TEXT */
                        ),
                        vue.createElementVNode("view", { class: "comment-likes" }, [
                          vue.createElementVNode("image", {
                            class: "like-icon",
                            src: _imports_2$3
                          }),
                          vue.createElementVNode(
                            "text",
                            { class: "like-count" },
                            vue.toDisplayString(comment.likes),
                            1
                            /* TEXT */
                          )
                        ])
                      ]),
                      vue.createElementVNode(
                        "text",
                        { class: "comment-text" },
                        vue.toDisplayString(comment.content),
                        1
                        /* TEXT */
                      ),
                      comment.mention ? (vue.openBlock(), vue.createElementBlock(
                        "text",
                        {
                          key: 0,
                          class: "comment-mention"
                        },
                        vue.toDisplayString(comment.mention),
                        1
                        /* TEXT */
                      )) : vue.createCommentVNode("v-if", true),
                      vue.createElementVNode("view", { class: "comment-meta" }, [
                        vue.createElementVNode(
                          "text",
                          { class: "comment-time" },
                          vue.toDisplayString(comment.time),
                          1
                          /* TEXT */
                        ),
                        vue.createElementVNode(
                          "text",
                          { class: "comment-location" },
                          vue.toDisplayString(comment.location),
                          1
                          /* TEXT */
                        ),
                        vue.createElementVNode("text", { class: "comment-reply" }, "回复")
                      ]),
                      vue.createCommentVNode(" 回复列表 "),
                      comment.replies && comment.replies.length > 0 ? (vue.openBlock(), vue.createElementBlock("view", {
                        key: 1,
                        class: "replies"
                      }, [
                        (vue.openBlock(true), vue.createElementBlock(
                          vue.Fragment,
                          null,
                          vue.renderList(comment.replies, (reply, replyIndex) => {
                            return vue.openBlock(), vue.createElementBlock("view", {
                              class: "reply-item",
                              key: replyIndex
                            }, [
                              vue.createElementVNode(
                                "text",
                                { class: "reply-text" },
                                vue.toDisplayString(reply.content),
                                1
                                /* TEXT */
                              )
                            ]);
                          }),
                          128
                          /* KEYED_FRAGMENT */
                        )),
                        comment.totalReplies > comment.replies.length ? (vue.openBlock(), vue.createElementBlock(
                          "text",
                          {
                            key: 0,
                            class: "view-replies"
                          },
                          " 查看共" + vue.toDisplayString(comment.totalReplies) + "条回复 ",
                          1
                          /* TEXT */
                        )) : vue.createCommentVNode("v-if", true)
                      ])) : vue.createCommentVNode("v-if", true)
                    ])
                  ]);
                }),
                128
                /* KEYED_FRAGMENT */
              ))
            ])
          ],
          4
          /* STYLE */
        )) : vue.createCommentVNode("v-if", true)
      ], 40, ["scroll-top"]),
      vue.createCommentVNode(" 底部操作栏 "),
      vue.createElementVNode("view", { class: "bottom-bar" }, [
        vue.createElementVNode("view", { class: "action-left" }, [
          vue.createElementVNode("view", { class: "action-item" }, [
            vue.createElementVNode("image", {
              class: "action-icon-img",
              src: _imports_6
            }),
            vue.createElementVNode("text", { class: "action-text" }, "群聊")
          ]),
          vue.createElementVNode("view", {
            class: "action-item",
            onClick: _cache[6] || (_cache[6] = (...args) => $options.switchToComment && $options.switchToComment(...args))
          }, [
            vue.createElementVNode("image", {
              class: "action-icon-img",
              src: _imports_7
            }),
            vue.createElementVNode("text", { class: "action-text" }, "评论")
          ]),
          vue.createElementVNode("view", {
            class: "action-item",
            onClick: _cache[7] || (_cache[7] = (...args) => $options.switchToSignup && $options.switchToSignup(...args))
          }, [
            vue.createElementVNode("image", {
              class: "action-icon-img",
              src: _imports_8
            }),
            vue.createElementVNode("text", { class: "signup-count" }, "已报名7人")
          ])
        ]),
        vue.createElementVNode("view", {
          class: "signup-btn",
          onClick: _cache[8] || (_cache[8] = (...args) => $options.signupEvent && $options.signupEvent(...args))
        }, [
          vue.createElementVNode("text", { class: "signup-btn-text" }, "报名活动")
        ])
      ])
    ]);
  }
  const PagesEventDetailEventDetail = /* @__PURE__ */ _export_sfc(_sfc_main$9, [["render", _sfc_render$9], ["__scopeId", "data-v-12ddee09"], ["__file", "D:/code space2/xystapp/android/pages/event-detail/event-detail.vue"]]);
  const _imports_0$1 = "/static/私信/1.png";
  const _imports_1 = "/static/私信/2.png";
  const _imports_2$1 = "/static/私信/3.png";
  const _sfc_main$8 = {
    name: "ChatDetail",
    components: {
      TopNavigation
    },
    data() {
      return {
        chatType: "private",
        // 'private' 或 'group'
        chatUser: {
          id: "",
          name: "会吃西瓜的小鸭纸",
          avatar: "/static/关注页/follow-users-section/Ellipse 2.png"
        },
        groupInfo: {
          id: "",
          name: "lol群",
          avatar: "/static/关注页/follow-users-section/Ellipse 9.png",
          notice: "入群须知: 大家可以随意组队打lol   不要把群...",
          activity: "lol线下比赛   2025.8.25   17:00-18:00"
        },
        showNotice: true,
        showActivity: true,
        inputText: "",
        scrollTop: 0,
        messages: []
      };
    },
    onLoad(options) {
      this.chatType = options.type || "private";
      if (this.chatType === "group") {
        if (options.name) {
          this.groupInfo.name = decodeURIComponent(options.name);
        }
        if (options.avatar) {
          this.groupInfo.avatar = decodeURIComponent(options.avatar);
        }
        if (options.id) {
          this.groupInfo.id = options.id;
        }
        if (options.notice) {
          this.groupInfo.notice = decodeURIComponent(options.notice);
        }
        if (options.activity) {
          this.groupInfo.activity = decodeURIComponent(options.activity);
        }
        this.loadGroupMessages();
      } else {
        if (options.name) {
          this.chatUser.name = decodeURIComponent(options.name);
        }
        if (options.avatar) {
          this.chatUser.avatar = decodeURIComponent(options.avatar);
        }
        if (options.id) {
          this.chatUser.id = options.id;
        }
        this.loadPrivateMessages();
      }
      this.$nextTick(() => {
        this.scrollToBottom();
      });
    },
    methods: {
      goBack() {
        uni.navigateBack();
      },
      showMore() {
        if (this.chatType === "group") {
          let url = `/pages/message/group-settings?id=${this.groupInfo.id || ""}&name=${encodeURIComponent(this.groupInfo.name)}&avatar=${encodeURIComponent(this.groupInfo.avatar)}`;
          if (this.groupInfo.notice) {
            url += `&notice=${encodeURIComponent(this.groupInfo.notice)}`;
          }
          if (this.groupInfo.activity) {
            url += `&activity=${encodeURIComponent(this.groupInfo.activity)}`;
          }
          url += `&groupField=${encodeURIComponent("lol场")}`;
          url += `&description=${encodeURIComponent("lol爱好者聚集地")}`;
          uni.navigateTo({
            url
          });
        } else {
          uni.navigateTo({
            url: `/pages/message/chat-settings?id=${this.chatUser.id || ""}&name=${encodeURIComponent(this.chatUser.name)}&avatar=${encodeURIComponent(this.chatUser.avatar)}`
          });
        }
      },
      closeNotice() {
        this.showNotice = false;
      },
      closeActivity() {
        this.showActivity = false;
      },
      sendMessage() {
        if (!this.inputText.trim()) {
          return;
        }
        this.messages.push({
          type: "sent",
          contentType: "text",
          avatar: "/static/关注页/follow-users-section/Ellipse 13.png",
          content: this.inputText
        });
        this.inputText = "";
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      },
      chooseImage() {
        uni.chooseImage({
          count: 1,
          sizeType: ["compressed"],
          sourceType: ["album", "camera"],
          success: (res) => {
            const tempFilePath = res.tempFilePaths[0];
            this.messages.push({
              type: "sent",
              contentType: "image",
              avatar: "/static/关注页/follow-users-section/Ellipse 13.png",
              content: tempFilePath
            });
            this.$nextTick(() => {
              this.scrollToBottom();
            });
          }
        });
      },
      scrollToBottom() {
        this.scrollTop = 99999;
      },
      loadPrivateMessages() {
        this.messages = [
          {
            type: "received",
            contentType: "text",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            content: "我真得跟你说一下你玩塞拉斯的问题了。你为什么总是把他当成纯刺客在玩，塞拉斯真正的强点在于时机感和选择。这英雄不是看谁就冲谁，而是要先观察战局，挑最关键的时刻切进去。"
          },
          {
            type: "received",
            contentType: "text",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            content: "对线的时候别太心急。塞拉斯的 Q 是清线和消耗，E 是位移与控制，W 是反打的核心技能：回血、爆发两不误。最常见的问题就是你W太早用了，结果关键时刻没血没反打。塞拉斯的强势在于能苟着拼、拼着反，打的是节奏和耐心。"
          },
          {
            type: "sent",
            contentType: "text",
            avatar: "/static/关注页/follow-users-section/Ellipse 13.png",
            content: "但我真的学不会怎么办"
          },
          {
            type: "received",
            contentType: "text",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            content: "？"
          },
          {
            type: "received",
            contentType: "text",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            content: "不是哥们你......算了看看我的五杀"
          },
          {
            type: "received",
            contentType: "image",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            content: "/static/关注页/Rectangle 109.png"
          }
        ];
      },
      loadGroupMessages() {
        this.messages = [
          {
            type: "received",
            contentType: "text",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            senderName: "国服塞拉斯",
            content: "我真得跟你说一下你玩塞拉斯的问题了。你为什么总是把他当成纯刺客在玩，塞拉斯真正的强点在于时机感和选择。这英雄不是看谁就冲谁，而是要先观察战局，挑最关键的时刻切进去。"
          },
          {
            type: "received",
            contentType: "text",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            senderName: "国服塞拉斯",
            content: "哦发错地方了"
          },
          {
            type: "sent",
            contentType: "text",
            avatar: "/static/关注页/follow-users-section/Ellipse 13.png",
            senderName: "abandon",
            content: "嘿嘿。"
          },
          {
            type: "received",
            contentType: "text",
            avatar: "/static/关注页/follow-users-section/Ellipse 14.png",
            senderName: "剑圣绝活哥",
            content: "？"
          },
          {
            type: "received",
            contentType: "text",
            avatar: "/static/关注页/follow-users-section/Ellipse 14.png",
            senderName: "剑圣绝活哥",
            content: "你说得对但看看我的五杀"
          },
          {
            type: "received",
            contentType: "image",
            avatar: "/static/关注页/follow-users-section/Ellipse 14.png",
            senderName: "剑圣绝活哥",
            content: "/static/关注页/Rectangle 109.png"
          },
          {
            type: "received",
            contentType: "text",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            senderName: "国服塞拉斯",
            content: "你又欠打了？偷我图干嘛"
          }
        ];
      }
    }
  };
  function _sfc_render$8(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_TopNavigation = vue.resolveComponent("TopNavigation");
    return vue.openBlock(), vue.createElementBlock("view", { class: "chat-container" }, [
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createVNode(_component_TopNavigation, {
        title: $data.chatType === "group" ? $data.groupInfo.name : $data.chatUser.name,
        showBack: true,
        showMore: true,
        onBack: $options.goBack,
        onMore: $options.showMore
      }, null, 8, ["title", "onBack", "onMore"]),
      vue.createCommentVNode(" 群公告（仅群聊显示） "),
      $data.chatType === "group" && $data.groupInfo.notice && $data.showNotice ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 0,
        class: "notice-bar"
      }, [
        vue.createElementVNode("text", { class: "notice-label" }, "群公告："),
        vue.createElementVNode(
          "text",
          { class: "notice-text" },
          vue.toDisplayString($data.groupInfo.notice),
          1
          /* TEXT */
        ),
        vue.createElementVNode("view", {
          class: "close-btn",
          onClick: _cache[0] || (_cache[0] = (...args) => $options.closeNotice && $options.closeNotice(...args))
        }, [
          vue.createElementVNode("text", { class: "close-icon" }, "×")
        ])
      ])) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" 活动信息（仅群聊显示） "),
      $data.chatType === "group" && $data.groupInfo.activity && $data.showActivity ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 1,
        class: "activity-bar"
      }, [
        vue.createElementVNode("text", { class: "activity-label" }, "活动："),
        vue.createElementVNode(
          "text",
          { class: "activity-text" },
          vue.toDisplayString($data.groupInfo.activity),
          1
          /* TEXT */
        ),
        vue.createElementVNode("view", {
          class: "close-btn",
          onClick: _cache[1] || (_cache[1] = (...args) => $options.closeActivity && $options.closeActivity(...args))
        }, [
          vue.createElementVNode("text", { class: "close-icon" }, "×")
        ])
      ])) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" 聊天消息列表 "),
      vue.createElementVNode("scroll-view", {
        class: "message-list",
        "scroll-y": "",
        "scroll-top": $data.scrollTop,
        "scroll-with-animation": ""
      }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($data.messages, (msg, index) => {
            return vue.openBlock(), vue.createElementBlock("view", {
              class: "message-wrapper",
              key: index
            }, [
              vue.createCommentVNode(" 左侧消息（对方发送的） "),
              msg.type === "received" ? (vue.openBlock(), vue.createElementBlock("view", {
                key: 0,
                class: "message-item message-left"
              }, [
                vue.createElementVNode("image", {
                  src: msg.avatar,
                  class: "message-avatar"
                }, null, 8, ["src"]),
                vue.createElementVNode("view", { class: "message-bubble-wrapper" }, [
                  vue.createCommentVNode(" 群聊显示发言人昵称 "),
                  $data.chatType === "group" ? (vue.openBlock(), vue.createElementBlock(
                    "text",
                    {
                      key: 0,
                      class: "sender-name"
                    },
                    vue.toDisplayString(msg.senderName),
                    1
                    /* TEXT */
                  )) : vue.createCommentVNode("v-if", true),
                  vue.createElementVNode("view", { class: "message-triangle left-triangle" }),
                  vue.createElementVNode("view", { class: "message-bubble left-bubble" }, [
                    vue.createCommentVNode(" 文本消息 "),
                    msg.contentType === "text" ? (vue.openBlock(), vue.createElementBlock(
                      "text",
                      {
                        key: 0,
                        class: "message-text"
                      },
                      vue.toDisplayString(msg.content),
                      1
                      /* TEXT */
                    )) : vue.createCommentVNode("v-if", true),
                    vue.createCommentVNode(" 图片消息 "),
                    msg.contentType === "image" ? (vue.openBlock(), vue.createElementBlock("image", {
                      key: 1,
                      src: msg.content,
                      class: "message-image",
                      mode: "widthFix"
                    }, null, 8, ["src"])) : vue.createCommentVNode("v-if", true)
                  ])
                ])
              ])) : vue.createCommentVNode("v-if", true),
              vue.createCommentVNode(" 右侧消息（我发送的） "),
              msg.type === "sent" ? (vue.openBlock(), vue.createElementBlock("view", {
                key: 1,
                class: "message-item message-right"
              }, [
                vue.createElementVNode("view", { class: "message-bubble-wrapper" }, [
                  vue.createCommentVNode(" 群聊显示发言人昵称 "),
                  $data.chatType === "group" ? (vue.openBlock(), vue.createElementBlock(
                    "text",
                    {
                      key: 0,
                      class: "sender-name sender-name-right"
                    },
                    vue.toDisplayString(msg.senderName),
                    1
                    /* TEXT */
                  )) : vue.createCommentVNode("v-if", true),
                  vue.createElementVNode("view", { class: "message-triangle right-triangle" }),
                  vue.createElementVNode("view", { class: "message-bubble right-bubble" }, [
                    vue.createElementVNode(
                      "text",
                      { class: "message-text" },
                      vue.toDisplayString(msg.content),
                      1
                      /* TEXT */
                    )
                  ])
                ]),
                vue.createElementVNode("image", {
                  src: msg.avatar,
                  class: "message-avatar"
                }, null, 8, ["src"])
              ])) : vue.createCommentVNode("v-if", true)
            ]);
          }),
          128
          /* KEYED_FRAGMENT */
        )),
        vue.createCommentVNode(" 时间戳 "),
        vue.createElementVNode("view", { class: "message-time" }, [
          vue.createElementVNode("text", { class: "time-text" }, "星期六 19:20")
        ])
      ], 8, ["scroll-top"]),
      vue.createCommentVNode(" 底部输入栏 "),
      vue.createElementVNode("view", { class: "bottom-input" }, [
        vue.createElementVNode("view", { class: "input-wrapper" }, [
          vue.createCommentVNode(" 语音按钮 "),
          vue.createElementVNode("view", { class: "input-icon voice-icon" }, [
            vue.createElementVNode("image", {
              src: _imports_0$1,
              class: "bottom-icon-img"
            })
          ]),
          vue.createCommentVNode(" 输入框 "),
          vue.createElementVNode("view", { class: "input-box" }, [
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                "onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => $data.inputText = $event),
                class: "text-input",
                placeholder: "加入讨论？",
                "placeholder-style": "color: #757575",
                onConfirm: _cache[3] || (_cache[3] = (...args) => $options.sendMessage && $options.sendMessage(...args))
              },
              null,
              544
              /* NEED_HYDRATION, NEED_PATCH */
            ), [
              [vue.vModelText, $data.inputText]
            ])
          ]),
          vue.createCommentVNode(" 表情按钮 "),
          vue.createElementVNode("view", { class: "input-icon emoji-icon" }, [
            vue.createElementVNode("image", {
              src: _imports_1,
              class: "bottom-icon-img"
            })
          ]),
          vue.createCommentVNode(" 图片按钮 "),
          vue.createElementVNode("view", {
            class: "input-icon image-icon",
            onClick: _cache[4] || (_cache[4] = (...args) => $options.chooseImage && $options.chooseImage(...args))
          }, [
            vue.createElementVNode("image", {
              src: _imports_2$1,
              class: "bottom-icon-img"
            })
          ])
        ])
      ])
    ]);
  }
  const PagesMessageChatDetail = /* @__PURE__ */ _export_sfc(_sfc_main$8, [["render", _sfc_render$8], ["__scopeId", "data-v-f66fca3b"], ["__file", "D:/code space2/xystapp/android/pages/message/chat-detail.vue"]]);
  const _sfc_main$7 = {
    name: "ChatSettings",
    components: {
      TopNavigation
    },
    data() {
      return {
        userInfo: {
          name: "会吃西瓜的小鸭纸",
          id: "542312132",
          avatar: "/static/关注页/follow-users-section/Ellipse 2.png"
        },
        settings: {
          pinTop: true,
          muteNotification: false
        }
      };
    },
    onLoad(options) {
      if (options.name) {
        this.userInfo.name = decodeURIComponent(options.name);
      }
      if (options.avatar) {
        this.userInfo.avatar = decodeURIComponent(options.avatar);
      }
      if (options.id) {
        this.userInfo.id = options.id;
      }
      this.loadSettings();
    },
    methods: {
      viewUserProfile() {
        uni.navigateTo({
          url: `/pages/profile/profile?userId=${this.userInfo.id}`
        });
      },
      togglePinTop(e) {
        this.settings.pinTop = e.detail.value;
        this.saveSettings();
        uni.showToast({
          title: this.settings.pinTop ? "已置顶" : "已取消置顶",
          icon: "success"
        });
      },
      toggleMute(e) {
        this.settings.muteNotification = e.detail.value;
        this.saveSettings();
        uni.showToast({
          title: this.settings.muteNotification ? "已开启免打扰" : "已关闭免打扰",
          icon: "success"
        });
      },
      loadSettings() {
        const key = `chat_settings_${this.userInfo.id}`;
        const savedSettings = uni.getStorageSync(key);
        if (savedSettings) {
          this.settings = savedSettings;
        }
      },
      saveSettings() {
        const key = `chat_settings_${this.userInfo.id}`;
        uni.setStorageSync(key, this.settings);
      }
    }
  };
  function _sfc_render$7(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_TopNavigation = vue.resolveComponent("TopNavigation");
    return vue.openBlock(), vue.createElementBlock("view", { class: "chat-settings-container" }, [
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createVNode(_component_TopNavigation, {
        title: "消息设置",
        showBack: true,
        showMore: false
      }),
      vue.createCommentVNode(" 用户信息卡片 "),
      vue.createElementVNode("view", {
        class: "user-card",
        onClick: _cache[0] || (_cache[0] = (...args) => $options.viewUserProfile && $options.viewUserProfile(...args))
      }, [
        vue.createElementVNode("view", { class: "user-info" }, [
          vue.createElementVNode("image", {
            src: $data.userInfo.avatar,
            class: "user-avatar"
          }, null, 8, ["src"]),
          vue.createElementVNode("view", { class: "user-details" }, [
            vue.createElementVNode(
              "text",
              { class: "user-name" },
              vue.toDisplayString($data.userInfo.name),
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "text",
              { class: "user-id" },
              "♂ ID:" + vue.toDisplayString($data.userInfo.id),
              1
              /* TEXT */
            )
          ])
        ]),
        vue.createElementVNode("view", { class: "arrow-right" }, [
          vue.createElementVNode("image", {
            src: _imports_1$4,
            class: "arrow-icon",
            mode: "aspectFit"
          })
        ])
      ]),
      vue.createCommentVNode(" 设置选项 "),
      vue.createElementVNode("view", { class: "settings-section" }, [
        vue.createCommentVNode(" 置顶聊天 "),
        vue.createElementVNode("view", { class: "setting-item" }, [
          vue.createElementVNode("text", { class: "setting-label" }, "置顶聊天"),
          vue.createElementVNode("switch", {
            checked: $data.settings.pinTop,
            onChange: _cache[1] || (_cache[1] = (...args) => $options.togglePinTop && $options.togglePinTop(...args)),
            color: "#8A70C9",
            class: "setting-switch"
          }, null, 40, ["checked"])
        ]),
        vue.createCommentVNode(" 消息免打扰 "),
        vue.createElementVNode("view", { class: "setting-item border-none" }, [
          vue.createElementVNode("text", { class: "setting-label" }, "消息免打扰"),
          vue.createElementVNode("switch", {
            checked: $data.settings.muteNotification,
            onChange: _cache[2] || (_cache[2] = (...args) => $options.toggleMute && $options.toggleMute(...args)),
            color: "#8A70C9",
            class: "setting-switch"
          }, null, 40, ["checked"])
        ])
      ])
    ]);
  }
  const PagesMessageChatSettings = /* @__PURE__ */ _export_sfc(_sfc_main$7, [["render", _sfc_render$7], ["__scopeId", "data-v-443a9e51"], ["__file", "D:/code space2/xystapp/android/pages/message/chat-settings.vue"]]);
  const _sfc_main$6 = {
    name: "GroupSettings",
    components: {
      TopNavigation
    },
    data() {
      return {
        groupInfo: {
          id: "",
          name: "lol群",
          groupField: "lol场",
          description: "lol爱好者聚集地",
          avatar: "/static/关注页/follow-users-section/Ellipse 9.png",
          notice: "",
          activity: ""
        },
        members: [
          {
            id: 1,
            name: "路人1",
            avatar: "/static/关注页/follow-users-section/Ellipse 11.png"
          },
          {
            id: 2,
            name: "好友1",
            avatar: "/static/关注页/follow-users-section/Ellipse 14.png"
          },
          {
            id: 3,
            name: "名字长...",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png"
          },
          {
            id: 4,
            name: "四个字的",
            avatar: "/static/关注页/follow-users-section/Ellipse 15.png"
          },
          {
            id: 5,
            name: "好友2",
            avatar: "/static/关注页/follow-users-section/Ellipse 16.png"
          },
          {
            id: 6,
            name: "好友3",
            avatar: "/static/关注页/follow-users-section/Ellipse 13.png"
          },
          {
            id: 7,
            name: "路人1",
            avatar: "/static/关注页/follow-users-section/Ellipse 11.png"
          },
          {
            id: 8,
            name: "好友1",
            avatar: "/static/关注页/follow-users-section/Ellipse 14.png"
          },
          {
            id: 9,
            name: "名字长...",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png"
          },
          {
            id: 10,
            name: "四个字的",
            avatar: "/static/关注页/follow-users-section/Ellipse 15.png"
          },
          {
            id: 11,
            name: "好友2",
            avatar: "/static/关注页/follow-users-section/Ellipse 16.png"
          }
        ],
        settings: {
          pinTop: true,
          muteNotification: false
        }
      };
    },
    computed: {
      displayMembers() {
        return this.members.slice(0, 10);
      }
    },
    onLoad(options) {
      if (options.name) {
        this.groupInfo.name = decodeURIComponent(options.name);
      }
      if (options.avatar) {
        this.groupInfo.avatar = decodeURIComponent(options.avatar);
      }
      if (options.id) {
        this.groupInfo.id = options.id;
      }
      if (options.notice) {
        this.groupInfo.notice = decodeURIComponent(options.notice);
      }
      if (options.activity) {
        this.groupInfo.activity = decodeURIComponent(options.activity);
      }
      if (options.groupField) {
        this.groupInfo.groupField = decodeURIComponent(options.groupField);
      }
      if (options.description) {
        this.groupInfo.description = decodeURIComponent(options.description);
      }
      this.loadSettings();
    },
    methods: {
      joinGroup() {
        uni.showToast({
          title: "进入群场",
          icon: "success"
        });
      },
      inviteMember() {
        uni.showToast({
          title: "邀请成员",
          icon: "none"
        });
      },
      viewAllMembers() {
        uni.navigateTo({
          url: `/pages/message/group-members?groupId=${this.groupInfo.id}`
        });
      },
      viewNotice() {
        uni.navigateTo({
          url: "/pages/message/group-notice"
        });
      },
      viewActivity() {
        uni.navigateTo({
          url: "/pages/message/group-activity"
        });
      },
      togglePinTop(e) {
        this.settings.pinTop = e.detail.value;
        this.saveSettings();
        uni.showToast({
          title: this.settings.pinTop ? "已置顶" : "已取消置顶",
          icon: "success"
        });
      },
      toggleMute(e) {
        this.settings.muteNotification = e.detail.value;
        this.saveSettings();
        uni.showToast({
          title: this.settings.muteNotification ? "已开启免打扰" : "已关闭免打扰",
          icon: "success"
        });
      },
      loadSettings() {
        const key = `group_settings_${this.groupInfo.id}`;
        const savedSettings = uni.getStorageSync(key);
        if (savedSettings) {
          this.settings = savedSettings;
        }
      },
      saveSettings() {
        const key = `group_settings_${this.groupInfo.id}`;
        uni.setStorageSync(key, this.settings);
      }
    }
  };
  function _sfc_render$6(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_TopNavigation = vue.resolveComponent("TopNavigation");
    return vue.openBlock(), vue.createElementBlock("view", { class: "group-settings-container" }, [
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createVNode(_component_TopNavigation, {
        title: "消息设置",
        showBack: true,
        showMore: false
      }),
      vue.createCommentVNode(" 群信息卡片 "),
      vue.createElementVNode("view", { class: "group-card" }, [
        vue.createElementVNode("view", { class: "group-info" }, [
          vue.createElementVNode("image", {
            src: $data.groupInfo.avatar,
            class: "group-avatar"
          }, null, 8, ["src"]),
          vue.createElementVNode("view", { class: "group-details" }, [
            vue.createElementVNode(
              "text",
              { class: "group-name" },
              vue.toDisplayString($data.groupInfo.name) + "（" + vue.toDisplayString($data.groupInfo.groupField) + "）",
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "text",
              { class: "group-desc" },
              vue.toDisplayString($data.groupInfo.description),
              1
              /* TEXT */
            )
          ])
        ]),
        vue.createElementVNode("view", {
          class: "join-btn",
          onClick: _cache[0] || (_cache[0] = (...args) => $options.joinGroup && $options.joinGroup(...args))
        }, [
          vue.createElementVNode("text", { class: "join-text" }, "进场")
        ])
      ]),
      vue.createCommentVNode(" 群成员区域 "),
      vue.createElementVNode("view", { class: "members-section" }, [
        vue.createElementVNode("view", { class: "members-grid" }, [
          (vue.openBlock(true), vue.createElementBlock(
            vue.Fragment,
            null,
            vue.renderList($options.displayMembers, (member, index) => {
              return vue.openBlock(), vue.createElementBlock("view", {
                class: "member-item",
                key: index
              }, [
                vue.createElementVNode("image", {
                  src: member.avatar,
                  class: "member-avatar"
                }, null, 8, ["src"]),
                vue.createElementVNode(
                  "text",
                  { class: "member-name" },
                  vue.toDisplayString(member.name),
                  1
                  /* TEXT */
                )
              ]);
            }),
            128
            /* KEYED_FRAGMENT */
          )),
          vue.createCommentVNode(" 邀请按钮 "),
          vue.createElementVNode("view", {
            class: "member-item",
            onClick: _cache[1] || (_cache[1] = (...args) => $options.inviteMember && $options.inviteMember(...args))
          }, [
            vue.createElementVNode("view", { class: "add-member-btn" }, [
              vue.createElementVNode("text", { class: "add-icon" }, "+")
            ]),
            vue.createElementVNode("text", { class: "member-name" }, "邀请")
          ])
        ]),
        vue.createElementVNode("view", {
          class: "view-all",
          onClick: _cache[2] || (_cache[2] = (...args) => $options.viewAllMembers && $options.viewAllMembers(...args))
        }, [
          vue.createElementVNode("text", { class: "view-all-text" }, "查看全部成员")
        ])
      ]),
      vue.createCommentVNode(" 设置选项 "),
      vue.createElementVNode("view", { class: "settings-section" }, [
        vue.createCommentVNode(" 群公告 "),
        vue.createElementVNode("view", {
          class: "setting-item",
          onClick: _cache[3] || (_cache[3] = (...args) => $options.viewNotice && $options.viewNotice(...args))
        }, [
          vue.createElementVNode("text", { class: "setting-label" }, "群公告"),
          vue.createElementVNode("view", { class: "arrow-right" }, [
            vue.createElementVNode("image", {
              src: _imports_1$4,
              class: "arrow-icon",
              mode: "aspectFit"
            })
          ])
        ]),
        vue.createCommentVNode(" 活动 "),
        vue.createElementVNode("view", {
          class: "setting-item",
          onClick: _cache[4] || (_cache[4] = (...args) => $options.viewActivity && $options.viewActivity(...args))
        }, [
          vue.createElementVNode("text", { class: "setting-label" }, "活动"),
          vue.createElementVNode("view", { class: "arrow-right" }, [
            vue.createElementVNode("image", {
              src: _imports_1$4,
              class: "arrow-icon",
              mode: "aspectFit"
            })
          ])
        ]),
        vue.createCommentVNode(" 置顶群聊 "),
        vue.createElementVNode("view", { class: "setting-item" }, [
          vue.createElementVNode("text", { class: "setting-label" }, "置顶群聊"),
          vue.createElementVNode("switch", {
            checked: $data.settings.pinTop,
            onChange: _cache[5] || (_cache[5] = (...args) => $options.togglePinTop && $options.togglePinTop(...args)),
            color: "#8A70C9",
            class: "setting-switch"
          }, null, 40, ["checked"])
        ]),
        vue.createCommentVNode(" 消息免打扰 "),
        vue.createElementVNode("view", { class: "setting-item border-none" }, [
          vue.createElementVNode("text", { class: "setting-label" }, "消息免打扰"),
          vue.createElementVNode("switch", {
            checked: $data.settings.muteNotification,
            onChange: _cache[6] || (_cache[6] = (...args) => $options.toggleMute && $options.toggleMute(...args)),
            color: "#8A70C9",
            class: "setting-switch"
          }, null, 40, ["checked"])
        ])
      ])
    ]);
  }
  const PagesMessageGroupSettings = /* @__PURE__ */ _export_sfc(_sfc_main$6, [["render", _sfc_render$6], ["__scopeId", "data-v-2f8a04c1"], ["__file", "D:/code space2/xystapp/android/pages/message/group-settings.vue"]]);
  const _sfc_main$5 = {
    name: "GroupNotice",
    components: {
      TopNavigation
    },
    data() {
      return {
        noticeInfo: {
          content: "入群须知:\n大家可以随意组队打lol\n不要把群聊到了谢谢大家哦\n新进群的都看看我的五杀",
          author: "会吃西瓜的小鸭纸",
          time: "2025年9月12日  17:00",
          image: "/static/关注页/Rectangle 109.png"
        }
      };
    },
    onLoad(options) {
      if (options.content) {
        this.noticeInfo.content = decodeURIComponent(options.content);
      }
      if (options.author) {
        this.noticeInfo.author = decodeURIComponent(options.author);
      }
      if (options.time) {
        this.noticeInfo.time = decodeURIComponent(options.time);
      }
      if (options.image) {
        this.noticeInfo.image = decodeURIComponent(options.image);
      }
    }
  };
  function _sfc_render$5(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_TopNavigation = vue.resolveComponent("TopNavigation");
    return vue.openBlock(), vue.createElementBlock("view", { class: "notice-container" }, [
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createVNode(_component_TopNavigation, {
        title: "群公告",
        showBack: true,
        showMore: false
      }),
      vue.createCommentVNode(" 公告内容卡片 "),
      vue.createElementVNode("view", { class: "notice-card" }, [
        vue.createElementVNode("view", { class: "notice-content" }, [
          vue.createElementVNode(
            "text",
            { class: "notice-text" },
            vue.toDisplayString($data.noticeInfo.content),
            1
            /* TEXT */
          )
        ]),
        vue.createElementVNode("view", { class: "notice-meta" }, [
          vue.createElementVNode("text", { class: "notice-author" }, "会吃西..."),
          vue.createElementVNode(
            "text",
            { class: "notice-time" },
            vue.toDisplayString($data.noticeInfo.time),
            1
            /* TEXT */
          )
        ]),
        vue.createCommentVNode(" 公告图片 "),
        $data.noticeInfo.image ? (vue.openBlock(), vue.createElementBlock("image", {
          key: 0,
          src: $data.noticeInfo.image,
          class: "notice-image",
          mode: "aspectFill"
        }, null, 8, ["src"])) : vue.createCommentVNode("v-if", true)
      ])
    ]);
  }
  const PagesMessageGroupNotice = /* @__PURE__ */ _export_sfc(_sfc_main$5, [["render", _sfc_render$5], ["__scopeId", "data-v-7833d0ca"], ["__file", "D:/code space2/xystapp/android/pages/message/group-notice.vue"]]);
  const _sfc_main$4 = {
    name: "GroupActivity",
    components: {
      TopNavigation
    },
    data() {
      return {
        activityInfo: {
          title: "lol线下比赛",
          image: "/static/关注页/Rectangle 109.png",
          date: "2025.8.25",
          weekday: "周一",
          time: "17:00-18:00",
          location: "体育馆1楼",
          tag: "lol场",
          signupCount: 30,
          likeCount: 125
        }
      };
    },
    onLoad(options) {
      if (options.title) {
        this.activityInfo.title = decodeURIComponent(options.title);
      }
      if (options.date) {
        this.activityInfo.date = decodeURIComponent(options.date);
      }
      if (options.time) {
        this.activityInfo.time = decodeURIComponent(options.time);
      }
      if (options.location) {
        this.activityInfo.location = decodeURIComponent(options.location);
      }
      if (options.tag) {
        this.activityInfo.tag = decodeURIComponent(options.tag);
      }
    }
  };
  function _sfc_render$4(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_TopNavigation = vue.resolveComponent("TopNavigation");
    return vue.openBlock(), vue.createElementBlock("view", { class: "activity-container" }, [
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createVNode(_component_TopNavigation, {
        title: "活动",
        showBack: true,
        showMore: false
      }),
      vue.createCommentVNode(" 活动卡片 "),
      vue.createElementVNode("view", { class: "activity-card" }, [
        vue.createElementVNode("image", {
          src: $data.activityInfo.image,
          class: "activity-image",
          mode: "aspectFill"
        }, null, 8, ["src"]),
        vue.createElementVNode("view", { class: "activity-info" }, [
          vue.createElementVNode("view", { class: "activity-header" }, [
            vue.createElementVNode(
              "text",
              { class: "activity-title" },
              vue.toDisplayString($data.activityInfo.title),
              1
              /* TEXT */
            ),
            vue.createElementVNode("view", { class: "like-section" }, [
              vue.createElementVNode("image", {
                src: _imports_0$6,
                class: "like-icon",
                mode: "aspectFit"
              }),
              vue.createElementVNode(
                "text",
                { class: "like-count" },
                vue.toDisplayString($data.activityInfo.likeCount),
                1
                /* TEXT */
              )
            ])
          ]),
          vue.createElementVNode("view", { class: "activity-meta" }, [
            vue.createElementVNode(
              "text",
              { class: "activity-time" },
              vue.toDisplayString($data.activityInfo.date) + " " + vue.toDisplayString($data.activityInfo.weekday) + " " + vue.toDisplayString($data.activityInfo.time),
              1
              /* TEXT */
            ),
            vue.createElementVNode(
              "text",
              { class: "activity-location" },
              vue.toDisplayString($data.activityInfo.location),
              1
              /* TEXT */
            )
          ]),
          vue.createElementVNode("view", { class: "activity-footer" }, [
            vue.createElementVNode("view", { class: "activity-tag" }, [
              vue.createElementVNode(
                "text",
                { class: "tag-text" },
                vue.toDisplayString($data.activityInfo.tag),
                1
                /* TEXT */
              )
            ]),
            vue.createElementVNode("view", { class: "activity-status" }, [
              vue.createElementVNode(
                "text",
                { class: "status-text" },
                "已报名：" + vue.toDisplayString($data.activityInfo.signupCount) + "人",
                1
                /* TEXT */
              )
            ])
          ])
        ])
      ])
    ]);
  }
  const PagesMessageGroupActivity = /* @__PURE__ */ _export_sfc(_sfc_main$4, [["render", _sfc_render$4], ["__scopeId", "data-v-89d799b0"], ["__file", "D:/code space2/xystapp/android/pages/message/group-activity.vue"]]);
  const _sfc_main$3 = {
    name: "GroupMembers",
    components: {
      TopNavigation
    },
    data() {
      return {
        searchKeyword: "",
        admins: [
          {
            id: 1,
            name: "会吃西瓜的小鸭纸",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            role: "owner"
          },
          {
            id: 2,
            name: "伊苏尔德的狗",
            avatar: "/static/关注页/follow-users-section/Ellipse 11.png",
            role: "admin"
          },
          {
            id: 3,
            name: "剑圣绝活哥",
            avatar: "/static/关注页/follow-users-section/Ellipse 14.png",
            role: "admin"
          }
        ],
        members: [
          {
            id: 4,
            name: "AAA猪饲料批发",
            avatar: "/static/关注页/follow-users-section/Ellipse 15.png",
            initial: "A"
          },
          {
            id: 5,
            name: "cc果粒真好喝",
            avatar: "/static/关注页/follow-users-section/Ellipse 16.png",
            initial: "C"
          },
          {
            id: 6,
            name: "CAO是氧化钙",
            avatar: "/static/关注页/follow-users-section/Ellipse 13.png",
            initial: "C"
          }
        ],
        alphabet: "ABCDEFGHIJKLMNOPQRSTUVWXYZ#".split("")
      };
    },
    computed: {
      groupedMembers() {
        const grouped = {};
        this.members.forEach((member) => {
          const initial = member.initial || "#";
          if (!grouped[initial]) {
            grouped[initial] = [];
          }
          grouped[initial].push(member);
        });
        return grouped;
      }
    },
    methods: {
      onSearch(e) {
        this.searchKeyword = e.detail.value;
      },
      handleSearch() {
        if (this.searchKeyword.trim()) {
          uni.showToast({
            title: "搜索：" + this.searchKeyword,
            icon: "none"
          });
        }
      },
      viewMember(member) {
        uni.showToast({
          title: "查看成员：" + member.name,
          icon: "none"
        });
      },
      scrollToLetter(letter) {
        formatAppLog("log", "at pages/message/group-members.vue:143", "滚动到:", letter);
      }
    }
  };
  function _sfc_render$3(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_TopNavigation = vue.resolveComponent("TopNavigation");
    return vue.openBlock(), vue.createElementBlock("view", { class: "members-container" }, [
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createVNode(_component_TopNavigation, {
        title: "群聊成员",
        showBack: true,
        showMore: false
      }),
      vue.createCommentVNode(" 搜索框 "),
      vue.createElementVNode("view", { class: "search-section" }, [
        vue.createElementVNode("view", { class: "search-box" }, [
          vue.withDirectives(vue.createElementVNode(
            "input",
            {
              "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $data.searchKeyword = $event),
              class: "search-input",
              placeholder: "搜索成员",
              "placeholder-style": "color: #999999",
              onInput: _cache[1] || (_cache[1] = (...args) => $options.onSearch && $options.onSearch(...args))
            },
            null,
            544
            /* NEED_HYDRATION, NEED_PATCH */
          ), [
            [vue.vModelText, $data.searchKeyword]
          ])
        ]),
        vue.createElementVNode("view", {
          class: "search-btn",
          onClick: _cache[2] || (_cache[2] = (...args) => $options.handleSearch && $options.handleSearch(...args))
        }, [
          vue.createElementVNode("text", { class: "search-btn-text" }, "搜索")
        ])
      ]),
      vue.createCommentVNode(" 成员列表 "),
      vue.createElementVNode("view", { class: "members-list" }, [
        vue.createCommentVNode(" 场主、场管理分组 "),
        vue.createElementVNode("view", { class: "member-group" }, [
          vue.createElementVNode("text", { class: "group-title" }, "场主、场管理（3人）"),
          (vue.openBlock(true), vue.createElementBlock(
            vue.Fragment,
            null,
            vue.renderList($data.admins, (admin) => {
              return vue.openBlock(), vue.createElementBlock("view", {
                class: "member-item",
                key: admin.id,
                onClick: ($event) => $options.viewMember(admin)
              }, [
                vue.createElementVNode("image", {
                  src: admin.avatar,
                  class: "member-avatar"
                }, null, 8, ["src"]),
                vue.createElementVNode(
                  "text",
                  { class: "member-name admin-name" },
                  vue.toDisplayString(admin.name),
                  1
                  /* TEXT */
                )
              ], 8, ["onClick"]);
            }),
            128
            /* KEYED_FRAGMENT */
          ))
        ]),
        vue.createCommentVNode(" 按字母分组 "),
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($options.groupedMembers, (group, letter) => {
            return vue.openBlock(), vue.createElementBlock("view", {
              class: "member-group",
              key: letter
            }, [
              vue.createElementVNode(
                "text",
                { class: "group-title" },
                vue.toDisplayString(letter) + "（" + vue.toDisplayString(group.length) + "人）",
                1
                /* TEXT */
              ),
              (vue.openBlock(true), vue.createElementBlock(
                vue.Fragment,
                null,
                vue.renderList(group, (member) => {
                  return vue.openBlock(), vue.createElementBlock("view", {
                    class: "member-item",
                    key: member.id,
                    onClick: ($event) => $options.viewMember(member)
                  }, [
                    vue.createElementVNode("image", {
                      src: member.avatar,
                      class: "member-avatar"
                    }, null, 8, ["src"]),
                    vue.createElementVNode(
                      "text",
                      { class: "member-name" },
                      vue.toDisplayString(member.name),
                      1
                      /* TEXT */
                    )
                  ], 8, ["onClick"]);
                }),
                128
                /* KEYED_FRAGMENT */
              ))
            ]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ]),
      vue.createCommentVNode(" 字母索引 "),
      vue.createElementVNode("view", { class: "alphabet-index" }, [
        (vue.openBlock(true), vue.createElementBlock(
          vue.Fragment,
          null,
          vue.renderList($data.alphabet, (letter) => {
            return vue.openBlock(), vue.createElementBlock("text", {
              class: "index-item",
              key: letter,
              onClick: ($event) => $options.scrollToLetter(letter)
            }, vue.toDisplayString(letter), 9, ["onClick"]);
          }),
          128
          /* KEYED_FRAGMENT */
        ))
      ])
    ]);
  }
  const PagesMessageGroupMembers = /* @__PURE__ */ _export_sfc(_sfc_main$3, [["render", _sfc_render$3], ["__scopeId", "data-v-e44f46fb"], ["__file", "D:/code space2/xystapp/android/pages/message/group-members.vue"]]);
  const _sfc_main$2 = {
    name: "CreatePost",
    components: {
      TopNavigation
    },
    data() {
      return {
        postTitle: "",
        postContent: "",
        images: [],
        selectedField: null,
        showFieldPanel: false,
        searchKeyword: "",
        myFields: [
          {
            id: 1,
            name: "lol场",
            avatar: "/static/关注页/follow-users-section/Ellipse 11.png",
            followers: 261,
            posts: 54
          },
          {
            id: 2,
            name: "羽毛球场",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            followers: 23,
            posts: 11
          }
        ],
        showUserPanel: false,
        showTopicPanel: false,
        mentionedUsers: [],
        selectedTopics: [],
        customTopic: "",
        userList: [
          {
            id: 1,
            name: "会吃西瓜的小鸭纸",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png"
          },
          {
            id: 2,
            name: "不知名用户",
            avatar: "/static/关注页/follow-users-section/Ellipse 11.png"
          },
          {
            id: 3,
            name: "你叫什么名字",
            avatar: "/static/关注页/follow-users-section/Ellipse 14.png"
          },
          {
            id: 4,
            name: "剑圣绝活哥",
            avatar: "/static/关注页/follow-users-section/Ellipse 15.png"
          }
        ],
        hotTopics: [
          "吐槽",
          "2026新生",
          "校园生活",
          "美食推荐",
          "学习打卡",
          "运动健身"
        ]
      };
    },
    computed: {
      canPublish() {
        return this.postTitle.trim() && this.postContent.trim() && this.selectedField;
      }
    },
    methods: {
      addTopic() {
        this.showTopicPanel = true;
      },
      hideTopicPanel() {
        this.showTopicPanel = false;
        this.customTopic = "";
      },
      selectTopic(topic) {
        if (!this.selectedTopics.includes(topic)) {
          this.selectedTopics.push(topic);
        }
        this.hideTopicPanel();
      },
      addCustomTopic() {
        if (this.customTopic.trim() && !this.selectedTopics.includes(this.customTopic.trim())) {
          this.selectedTopics.push(this.customTopic.trim());
          this.hideTopicPanel();
        }
      },
      mentionUser() {
        this.showUserPanel = true;
      },
      hideUserPanel() {
        this.showUserPanel = false;
      },
      selectUser(user) {
        const exists = this.mentionedUsers.find((u) => u.id === user.id);
        if (!exists) {
          this.mentionedUsers.push(user);
        }
        this.hideUserPanel();
      },
      chooseImage() {
        uni.chooseImage({
          count: 9 - this.images.length,
          sizeType: ["compressed"],
          sourceType: ["album", "camera"],
          success: (res) => {
            this.images = this.images.concat(res.tempFilePaths);
          }
        });
      },
      deleteImage(index) {
        this.images.splice(index, 1);
      },
      showFieldSelector() {
        this.showFieldPanel = true;
      },
      hideFieldSelector() {
        this.showFieldPanel = false;
      },
      searchField() {
        uni.showToast({
          title: "搜索场：" + this.searchKeyword,
          icon: "none"
        });
      },
      selectField(field) {
        this.selectedField = field;
        this.hideFieldSelector();
      },
      publishPost() {
        if (!this.canPublish) {
          uni.showToast({
            title: "请完善帖子内容",
            icon: "none"
          });
          return;
        }
        uni.showToast({
          title: "发布成功",
          icon: "success",
          success: () => {
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          }
        });
      }
    }
  };
  function _sfc_render$2(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_TopNavigation = vue.resolveComponent("TopNavigation");
    return vue.openBlock(), vue.createElementBlock("view", { class: "create-post-container" }, [
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createVNode(_component_TopNavigation, {
        title: "发帖",
        titleColor: "#8A70C9",
        showBack: true,
        showMore: false
      }),
      vue.createCommentVNode(" 帖子内容区域 "),
      vue.createElementVNode("scroll-view", {
        class: "content-area",
        "scroll-y": ""
      }, [
        vue.createCommentVNode(" 标题输入 "),
        vue.createElementVNode("view", { class: "input-section" }, [
          vue.withDirectives(vue.createElementVNode(
            "textarea",
            {
              "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $data.postTitle = $event),
              class: "title-input",
              placeholder: "请输入帖子标题...",
              "placeholder-style": "color: #999999",
              maxlength: "100",
              "auto-height": true
            },
            null,
            512
            /* NEED_PATCH */
          ), [
            [vue.vModelText, $data.postTitle]
          ])
        ]),
        vue.createCommentVNode(" 分割线 "),
        vue.createElementVNode("view", { class: "divider" }),
        vue.createCommentVNode(" 内容输入 "),
        vue.createElementVNode("view", { class: "input-section" }, [
          vue.withDirectives(vue.createElementVNode(
            "textarea",
            {
              "onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => $data.postContent = $event),
              class: "content-input",
              placeholder: "请输入帖子内容...",
              "placeholder-style": "color: #999999",
              maxlength: "5000",
              "auto-height": true
            },
            null,
            512
            /* NEED_PATCH */
          ), [
            [vue.vModelText, $data.postContent]
          ]),
          vue.createCommentVNode(" 已添加的话题和@用户 "),
          $data.selectedTopics.length > 0 || $data.mentionedUsers.length > 0 ? (vue.openBlock(), vue.createElementBlock("view", {
            key: 0,
            class: "tags-section"
          }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.selectedTopics, (topic, index) => {
                return vue.openBlock(), vue.createElementBlock(
                  "text",
                  {
                    key: "topic-" + index,
                    class: "tag-item topic-tag"
                  },
                  "#" + vue.toDisplayString(topic),
                  1
                  /* TEXT */
                );
              }),
              128
              /* KEYED_FRAGMENT */
            )),
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.mentionedUsers, (user, index) => {
                return vue.openBlock(), vue.createElementBlock(
                  "text",
                  {
                    key: "user-" + index,
                    class: "tag-item mention-tag"
                  },
                  "@" + vue.toDisplayString(user.name),
                  1
                  /* TEXT */
                );
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])) : vue.createCommentVNode("v-if", true)
        ]),
        vue.createCommentVNode(" 话题和@用户 "),
        vue.createElementVNode("view", { class: "action-buttons" }, [
          vue.createElementVNode("view", {
            class: "action-btn",
            onClick: _cache[2] || (_cache[2] = (...args) => $options.addTopic && $options.addTopic(...args))
          }, [
            vue.createElementVNode("text", { class: "action-text" }, "# 话题")
          ]),
          vue.createElementVNode("view", {
            class: "action-btn",
            onClick: _cache[3] || (_cache[3] = (...args) => $options.mentionUser && $options.mentionUser(...args))
          }, [
            vue.createElementVNode("text", { class: "action-text" }, "@ 用户")
          ])
        ]),
        vue.createCommentVNode(" 图片选择区域 "),
        vue.createElementVNode("view", { class: "image-section" }, [
          (vue.openBlock(true), vue.createElementBlock(
            vue.Fragment,
            null,
            vue.renderList($data.images, (image, index) => {
              return vue.openBlock(), vue.createElementBlock("view", {
                class: "image-item",
                key: index
              }, [
                vue.createElementVNode("image", {
                  src: image,
                  class: "preview-image",
                  mode: "aspectFill"
                }, null, 8, ["src"]),
                vue.createElementVNode("view", {
                  class: "delete-btn",
                  onClick: ($event) => $options.deleteImage(index)
                }, [
                  vue.createElementVNode("text", { class: "delete-icon" }, "×")
                ], 8, ["onClick"])
              ]);
            }),
            128
            /* KEYED_FRAGMENT */
          )),
          $data.images.length < 9 ? (vue.openBlock(), vue.createElementBlock("view", {
            key: 0,
            class: "add-image",
            onClick: _cache[4] || (_cache[4] = (...args) => $options.chooseImage && $options.chooseImage(...args))
          }, [
            vue.createElementVNode("text", { class: "add-icon" }, "+")
          ])) : vue.createCommentVNode("v-if", true)
        ]),
        vue.createCommentVNode(" 选择场 "),
        vue.createElementVNode("view", {
          class: "select-field",
          onClick: _cache[5] || (_cache[5] = (...args) => $options.showFieldSelector && $options.showFieldSelector(...args))
        }, [
          vue.createElementVNode("image", {
            src: _imports_0$7,
            class: "field-icon"
          }),
          vue.createElementVNode(
            "text",
            { class: "field-text" },
            vue.toDisplayString($data.selectedField ? $data.selectedField.name : "选择的场"),
            1
            /* TEXT */
          ),
          vue.createElementVNode("image", {
            src: _imports_1$4,
            class: "arrow-icon",
            mode: "aspectFit"
          })
        ])
      ]),
      vue.createCommentVNode(" 底部发布按钮 "),
      vue.createElementVNode("view", { class: "bottom-actions" }, [
        vue.createElementVNode(
          "view",
          {
            class: vue.normalizeClass(["publish-btn", { "active": $options.canPublish }]),
            onClick: _cache[6] || (_cache[6] = (...args) => $options.publishPost && $options.publishPost(...args))
          },
          [
            vue.createElementVNode("text", { class: "publish-text" }, "发布帖子")
          ],
          2
          /* CLASS */
        )
      ]),
      vue.createCommentVNode(" @用户弹窗 "),
      $data.showUserPanel ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 0,
        class: "user-panel"
      }, [
        vue.createElementVNode("view", {
          class: "panel-mask",
          onClick: _cache[7] || (_cache[7] = (...args) => $options.hideUserPanel && $options.hideUserPanel(...args))
        }),
        vue.createElementVNode("view", { class: "panel-box" }, [
          vue.createElementVNode("view", { class: "panel-box-header" }, [
            vue.createElementVNode("text", { class: "panel-box-title" }, "选择用户"),
            vue.createElementVNode("text", {
              class: "panel-close",
              onClick: _cache[8] || (_cache[8] = (...args) => $options.hideUserPanel && $options.hideUserPanel(...args))
            }, "×")
          ]),
          vue.createElementVNode("scroll-view", {
            class: "user-list",
            "scroll-y": ""
          }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.userList, (user) => {
                return vue.openBlock(), vue.createElementBlock("view", {
                  class: "user-item",
                  key: user.id,
                  onClick: ($event) => $options.selectUser(user)
                }, [
                  vue.createElementVNode("image", {
                    src: user.avatar,
                    class: "user-avatar"
                  }, null, 8, ["src"]),
                  vue.createElementVNode(
                    "text",
                    { class: "user-name" },
                    vue.toDisplayString(user.name),
                    1
                    /* TEXT */
                  )
                ], 8, ["onClick"]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ])
      ])) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" #话题弹窗 "),
      $data.showTopicPanel ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 1,
        class: "topic-panel"
      }, [
        vue.createElementVNode("view", {
          class: "panel-mask",
          onClick: _cache[9] || (_cache[9] = (...args) => $options.hideTopicPanel && $options.hideTopicPanel(...args))
        }),
        vue.createElementVNode("view", { class: "panel-box" }, [
          vue.createElementVNode("view", { class: "panel-box-header" }, [
            vue.createElementVNode("text", { class: "panel-box-title" }, "添加话题"),
            vue.createElementVNode("text", {
              class: "panel-close",
              onClick: _cache[10] || (_cache[10] = (...args) => $options.hideTopicPanel && $options.hideTopicPanel(...args))
            }, "×")
          ]),
          vue.createCommentVNode(" 输入自定义话题 "),
          vue.createElementVNode("view", { class: "topic-input-section" }, [
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                "onUpdate:modelValue": _cache[11] || (_cache[11] = ($event) => $data.customTopic = $event),
                class: "topic-input",
                placeholder: "输入话题",
                "placeholder-style": "color: #999999",
                onConfirm: _cache[12] || (_cache[12] = (...args) => $options.addCustomTopic && $options.addCustomTopic(...args))
              },
              null,
              544
              /* NEED_HYDRATION, NEED_PATCH */
            ), [
              [vue.vModelText, $data.customTopic]
            ]),
            vue.createElementVNode("view", {
              class: "add-topic-btn",
              onClick: _cache[13] || (_cache[13] = (...args) => $options.addCustomTopic && $options.addCustomTopic(...args))
            }, [
              vue.createElementVNode("text", { class: "add-topic-text" }, "添加")
            ])
          ]),
          vue.createCommentVNode(" 热门话题 "),
          vue.createElementVNode("view", { class: "hot-topics-section" }, [
            vue.createElementVNode("text", { class: "section-title" }, "热门话题"),
            vue.createElementVNode("scroll-view", {
              class: "topic-list",
              "scroll-y": ""
            }, [
              (vue.openBlock(true), vue.createElementBlock(
                vue.Fragment,
                null,
                vue.renderList($data.hotTopics, (topic, index) => {
                  return vue.openBlock(), vue.createElementBlock("view", {
                    class: "topic-item",
                    key: index,
                    onClick: ($event) => $options.selectTopic(topic)
                  }, [
                    vue.createElementVNode(
                      "text",
                      { class: "topic-name" },
                      "#" + vue.toDisplayString(topic),
                      1
                      /* TEXT */
                    )
                  ], 8, ["onClick"]);
                }),
                128
                /* KEYED_FRAGMENT */
              ))
            ])
          ])
        ])
      ])) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" 选择场弹窗 "),
      $data.showFieldPanel ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 2,
        class: "field-panel"
      }, [
        vue.createElementVNode("view", { class: "panel-content" }, [
          vue.createCommentVNode(" 顶部导航 "),
          vue.createElementVNode("view", { class: "panel-header" }, [
            vue.createElementVNode("view", {
              class: "back-btn",
              onClick: _cache[14] || (_cache[14] = (...args) => $options.hideFieldSelector && $options.hideFieldSelector(...args))
            }, [
              vue.createElementVNode("image", {
                src: _imports_1$4,
                class: "back-icon",
                mode: "aspectFit"
              })
            ]),
            vue.createElementVNode("text", { class: "panel-title" }, "选择的场")
          ]),
          vue.createCommentVNode(" 搜索框 "),
          vue.createElementVNode("view", { class: "search-section" }, [
            vue.createElementVNode("view", { class: "search-box" }, [
              vue.withDirectives(vue.createElementVNode(
                "input",
                {
                  "onUpdate:modelValue": _cache[15] || (_cache[15] = ($event) => $data.searchKeyword = $event),
                  class: "search-input",
                  placeholder: "输入场",
                  "placeholder-style": "color: #999999"
                },
                null,
                512
                /* NEED_PATCH */
              ), [
                [vue.vModelText, $data.searchKeyword]
              ])
            ]),
            vue.createElementVNode("view", {
              class: "search-btn",
              onClick: _cache[16] || (_cache[16] = (...args) => $options.searchField && $options.searchField(...args))
            }, [
              vue.createElementVNode("text", { class: "search-btn-text" }, "搜索")
            ])
          ]),
          vue.createCommentVNode(" 我关注的场 "),
          vue.createElementVNode("scroll-view", {
            class: "fields-list",
            "scroll-y": ""
          }, [
            vue.createElementVNode("text", { class: "list-title" }, "我关注的场"),
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.myFields, (field) => {
                return vue.openBlock(), vue.createElementBlock("view", {
                  class: "field-item",
                  key: field.id,
                  onClick: ($event) => $options.selectField(field)
                }, [
                  vue.createElementVNode("image", {
                    src: field.avatar,
                    class: "field-avatar"
                  }, null, 8, ["src"]),
                  vue.createElementVNode("view", { class: "field-info" }, [
                    vue.createElementVNode(
                      "text",
                      { class: "field-name" },
                      vue.toDisplayString(field.name),
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode("view", { class: "field-stats" }, [
                      vue.createElementVNode(
                        "text",
                        { class: "stat-text" },
                        vue.toDisplayString(field.followers) + "关注",
                        1
                        /* TEXT */
                      ),
                      vue.createElementVNode(
                        "text",
                        { class: "stat-text" },
                        vue.toDisplayString(field.posts) + "帖子",
                        1
                        /* TEXT */
                      )
                    ])
                  ])
                ], 8, ["onClick"]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ])
      ])) : vue.createCommentVNode("v-if", true)
    ]);
  }
  const PagesPostCreatePost = /* @__PURE__ */ _export_sfc(_sfc_main$2, [["render", _sfc_render$2], ["__scopeId", "data-v-77be352d"], ["__file", "D:/code space2/xystapp/android/pages/post/create-post.vue"]]);
  const _imports_0 = "/static/发布活动/1.png";
  const _imports_2 = "/static/发布活动/2.png";
  const _imports_3 = "/static/发布活动/5.png";
  const _imports_4 = "/static/发布活动/3.png";
  const _imports_5 = "/static/发布活动/4.png";
  const _sfc_main$1 = {
    name: "CreateActivity",
    components: {
      TopNavigation
    },
    data() {
      return {
        activityTitle: "",
        activityTime: "",
        activityLocation: "",
        coverImage: "",
        activityContent: "",
        images: [],
        selectedField: null,
        maxPeople: "",
        signupMode: "",
        showFieldPanel: false,
        showTimePanel: false,
        showLocationPanel: false,
        showPeoplePanel: false,
        showSignupModePanel: false,
        searchKeyword: "",
        startDate: "",
        startTime: "",
        endDate: "",
        endTime: "",
        locationName: "",
        locationDetail: "",
        selectingStartDate: true,
        myFields: [
          {
            id: 1,
            name: "lol场",
            avatar: "/static/关注页/follow-users-section/Ellipse 11.png",
            followers: 261,
            posts: 54
          },
          {
            id: 2,
            name: "羽毛球场",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png",
            followers: 23,
            posts: 11
          }
        ],
        showUserPanel: false,
        showTopicPanel: false,
        mentionedUsers: [],
        selectedTopics: [],
        customTopic: "",
        userList: [
          {
            id: 1,
            name: "会吃西瓜的小鸭纸",
            avatar: "/static/关注页/follow-users-section/Ellipse 2.png"
          },
          {
            id: 2,
            name: "不知名用户",
            avatar: "/static/关注页/follow-users-section/Ellipse 11.png"
          },
          {
            id: 3,
            name: "你叫什么名字",
            avatar: "/static/关注页/follow-users-section/Ellipse 14.png"
          },
          {
            id: 4,
            name: "剑圣绝活哥",
            avatar: "/static/关注页/follow-users-section/Ellipse 15.png"
          }
        ],
        hotTopics: [
          "吐槽",
          "2026新生",
          "校园生活",
          "美食推荐",
          "学习打卡",
          "运动健身"
        ]
      };
    },
    computed: {
      canPublish() {
        return this.activityTitle.trim() && this.activityTime && this.activityLocation && this.coverImage && this.activityContent.trim() && this.selectedField;
      }
    },
    methods: {
      selectTime() {
        this.showTimePanel = true;
      },
      hideTimePanel() {
        this.showTimePanel = false;
      },
      onStartDateChange(e) {
        this.startDate = e.detail.value;
        this.endDate = e.detail.value;
      },
      onStartTimeChange(e) {
        this.startTime = e.detail.value;
      },
      onEndDateChange(e) {
        this.endDate = e.detail.value;
      },
      onEndTimeChange(e) {
        this.endTime = e.detail.value;
      },
      saveTime() {
        if (this.startDate && this.startTime && this.endDate && this.endTime) {
          const startDateObj = new Date(this.startDate);
          const weekDays = ["日", "一", "二", "三", "四", "五", "六"];
          const weekDay = weekDays[startDateObj.getDay()];
          const startMonth = this.startDate.split("-")[1];
          const startDay = this.startDate.split("-")[2];
          this.activityTime = `${startMonth}月${startDay}日 周${weekDay} ${this.startTime}-${this.endTime}`;
          this.hideTimePanel();
        } else {
          uni.showToast({
            title: "请完整选择时间",
            icon: "none"
          });
        }
      },
      selectLocation() {
        this.showLocationPanel = true;
      },
      hideLocationPanel() {
        this.showLocationPanel = false;
      },
      saveLocation() {
        if (this.locationName.trim()) {
          this.activityLocation = this.locationName.trim();
          if (this.locationDetail.trim()) {
            this.activityLocation += " - " + this.locationDetail.trim();
          }
          this.hideLocationPanel();
        } else {
          uni.showToast({
            title: "请输入地点",
            icon: "none"
          });
        }
      },
      searchLocation() {
        uni.showToast({
          title: "搜索地点：" + this.locationName,
          icon: "none"
        });
      },
      uploadCover() {
        uni.chooseImage({
          count: 1,
          sizeType: ["compressed"],
          sourceType: ["album", "camera"],
          success: (res) => {
            this.coverImage = res.tempFilePaths[0];
          }
        });
      },
      deleteCover() {
        this.coverImage = "";
      },
      addTopic() {
        this.showTopicPanel = true;
      },
      hideTopicPanel() {
        this.showTopicPanel = false;
        this.customTopic = "";
      },
      selectTopic(topic) {
        if (!this.selectedTopics.includes(topic)) {
          this.selectedTopics.push(topic);
        }
        this.hideTopicPanel();
      },
      addCustomTopic() {
        if (this.customTopic.trim() && !this.selectedTopics.includes(this.customTopic.trim())) {
          this.selectedTopics.push(this.customTopic.trim());
          this.hideTopicPanel();
        }
      },
      mentionUser() {
        this.showUserPanel = true;
      },
      hideUserPanel() {
        this.showUserPanel = false;
      },
      selectUser(user) {
        const exists = this.mentionedUsers.find((u) => u.id === user.id);
        if (!exists) {
          this.mentionedUsers.push(user);
        }
        this.hideUserPanel();
      },
      chooseImage() {
        uni.chooseImage({
          count: 9 - this.images.length,
          sizeType: ["compressed"],
          sourceType: ["album", "camera"],
          success: (res) => {
            this.images = this.images.concat(res.tempFilePaths);
          }
        });
      },
      deleteImage(index) {
        this.images.splice(index, 1);
      },
      showFieldSelector() {
        this.showFieldPanel = true;
      },
      hideFieldSelector() {
        this.showFieldPanel = false;
      },
      searchField() {
        uni.showToast({
          title: "搜索场：" + this.searchKeyword,
          icon: "none"
        });
      },
      selectField(field) {
        this.selectedField = field;
        this.hideFieldSelector();
      },
      setMaxPeople() {
        this.showPeoplePanel = true;
      },
      hidePeoplePanel() {
        this.showPeoplePanel = false;
      },
      selectPeopleCount(count) {
        this.maxPeople = count;
        this.hidePeoplePanel();
      },
      setSignupMode() {
        this.showSignupModePanel = true;
      },
      hideSignupModePanel() {
        this.showSignupModePanel = false;
      },
      selectSignupMode(mode) {
        this.signupMode = mode;
        this.hideSignupModePanel();
      },
      publishActivity() {
        if (!this.canPublish) {
          uni.showToast({
            title: "请完善活动信息",
            icon: "none"
          });
          return;
        }
        uni.showToast({
          title: "发布成功",
          icon: "success",
          success: () => {
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          }
        });
      }
    }
  };
  function _sfc_render$1(_ctx, _cache, $props, $setup, $data, $options) {
    const _component_TopNavigation = vue.resolveComponent("TopNavigation");
    return vue.openBlock(), vue.createElementBlock("view", { class: "create-activity-container" }, [
      vue.createCommentVNode(" 顶部导航栏 "),
      vue.createVNode(_component_TopNavigation, {
        title: "发布活动",
        titleColor: "#8A70C9",
        showBack: true,
        showMore: false
      }),
      vue.createCommentVNode(" 活动内容区域 "),
      vue.createElementVNode("scroll-view", {
        class: "content-area",
        "scroll-y": ""
      }, [
        vue.createCommentVNode(" 活动标题输入 "),
        vue.createElementVNode("view", { class: "input-section" }, [
          vue.withDirectives(vue.createElementVNode(
            "textarea",
            {
              "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => $data.activityTitle = $event),
              class: "title-input",
              placeholder: "请输入活动标题...",
              "placeholder-style": "color: #999999",
              maxlength: "100",
              "auto-height": true
            },
            null,
            512
            /* NEED_PATCH */
          ), [
            [vue.vModelText, $data.activityTitle]
          ])
        ]),
        vue.createCommentVNode(" 分割线 "),
        vue.createElementVNode("view", { class: "divider" }),
        vue.createCommentVNode(" 选择活动时间 "),
        vue.createElementVNode("view", {
          class: "select-item",
          onClick: _cache[1] || (_cache[1] = (...args) => $options.selectTime && $options.selectTime(...args))
        }, [
          vue.createElementVNode("image", {
            src: _imports_0,
            class: "item-icon time-icon"
          }),
          vue.createElementVNode(
            "text",
            { class: "item-text" },
            vue.toDisplayString($data.activityTime || "选择活动时间"),
            1
            /* TEXT */
          ),
          vue.createElementVNode("image", {
            src: _imports_1$4,
            class: "arrow-icon",
            mode: "aspectFit"
          })
        ]),
        vue.createCommentVNode(" 分割线 "),
        vue.createElementVNode("view", { class: "divider" }),
        vue.createCommentVNode(" 选择活动地点 "),
        vue.createElementVNode("view", {
          class: "select-item",
          onClick: _cache[2] || (_cache[2] = (...args) => $options.selectLocation && $options.selectLocation(...args))
        }, [
          vue.createElementVNode("image", {
            src: _imports_2,
            class: "item-icon location-icon"
          }),
          vue.createElementVNode(
            "text",
            { class: "item-text" },
            vue.toDisplayString($data.activityLocation || "选择活动地点"),
            1
            /* TEXT */
          ),
          vue.createElementVNode("image", {
            src: _imports_1$4,
            class: "arrow-icon",
            mode: "aspectFit"
          })
        ]),
        vue.createCommentVNode(" 分割线 "),
        vue.createElementVNode("view", { class: "divider" }),
        vue.createCommentVNode(" 上传活动封面 "),
        vue.createElementVNode("view", { class: "cover-section" }, [
          !$data.coverImage ? (vue.openBlock(), vue.createElementBlock("view", {
            key: 0,
            class: "upload-cover",
            onClick: _cache[3] || (_cache[3] = (...args) => $options.uploadCover && $options.uploadCover(...args))
          }, [
            vue.createElementVNode("text", { class: "upload-icon" }, "+"),
            vue.createElementVNode("text", { class: "upload-text" }, "请上传活动封面")
          ])) : (vue.openBlock(), vue.createElementBlock("view", {
            key: 1,
            class: "cover-preview"
          }, [
            vue.createElementVNode("image", {
              src: $data.coverImage,
              class: "cover-image",
              mode: "aspectFill"
            }, null, 8, ["src"]),
            vue.createElementVNode("view", {
              class: "delete-cover",
              onClick: _cache[4] || (_cache[4] = (...args) => $options.deleteCover && $options.deleteCover(...args))
            }, [
              vue.createElementVNode("text", { class: "delete-icon" }, "×")
            ])
          ]))
        ]),
        vue.createCommentVNode(" 分割线 "),
        vue.createElementVNode("view", { class: "divider" }),
        vue.createCommentVNode(" 活动详情输入 "),
        vue.createElementVNode("view", { class: "input-section" }, [
          vue.withDirectives(vue.createElementVNode(
            "textarea",
            {
              "onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => $data.activityContent = $event),
              class: "content-input",
              placeholder: "请输入活动详情内容...",
              "placeholder-style": "color: #999999",
              maxlength: "5000",
              "auto-height": true
            },
            null,
            512
            /* NEED_PATCH */
          ), [
            [vue.vModelText, $data.activityContent]
          ]),
          vue.createCommentVNode(" 已添加的话题和@用户 "),
          $data.selectedTopics.length > 0 || $data.mentionedUsers.length > 0 ? (vue.openBlock(), vue.createElementBlock("view", {
            key: 0,
            class: "tags-section"
          }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.selectedTopics, (topic, index) => {
                return vue.openBlock(), vue.createElementBlock(
                  "text",
                  {
                    key: "topic-" + index,
                    class: "tag-item"
                  },
                  "#" + vue.toDisplayString(topic),
                  1
                  /* TEXT */
                );
              }),
              128
              /* KEYED_FRAGMENT */
            )),
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.mentionedUsers, (user, index) => {
                return vue.openBlock(), vue.createElementBlock(
                  "text",
                  {
                    key: "user-" + index,
                    class: "tag-item"
                  },
                  "@" + vue.toDisplayString(user.name),
                  1
                  /* TEXT */
                );
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])) : vue.createCommentVNode("v-if", true)
        ]),
        vue.createCommentVNode(" 话题和@用户 "),
        vue.createElementVNode("view", { class: "action-buttons" }, [
          vue.createElementVNode("view", {
            class: "action-btn",
            onClick: _cache[6] || (_cache[6] = (...args) => $options.addTopic && $options.addTopic(...args))
          }, [
            vue.createElementVNode("text", { class: "action-text" }, "# 话题")
          ]),
          vue.createElementVNode("view", {
            class: "action-btn",
            onClick: _cache[7] || (_cache[7] = (...args) => $options.mentionUser && $options.mentionUser(...args))
          }, [
            vue.createElementVNode("text", { class: "action-text" }, "@ 用户")
          ])
        ]),
        vue.createCommentVNode(" 图片选择区域 "),
        vue.createElementVNode("view", { class: "image-section" }, [
          (vue.openBlock(true), vue.createElementBlock(
            vue.Fragment,
            null,
            vue.renderList($data.images, (image, index) => {
              return vue.openBlock(), vue.createElementBlock("view", {
                class: "image-item",
                key: index
              }, [
                vue.createElementVNode("image", {
                  src: image,
                  class: "preview-image",
                  mode: "aspectFill"
                }, null, 8, ["src"]),
                vue.createElementVNode("view", {
                  class: "delete-btn",
                  onClick: ($event) => $options.deleteImage(index)
                }, [
                  vue.createElementVNode("text", { class: "delete-icon" }, "×")
                ], 8, ["onClick"])
              ]);
            }),
            128
            /* KEYED_FRAGMENT */
          )),
          $data.images.length < 9 ? (vue.openBlock(), vue.createElementBlock("view", {
            key: 0,
            class: "add-image",
            onClick: _cache[8] || (_cache[8] = (...args) => $options.chooseImage && $options.chooseImage(...args))
          }, [
            vue.createElementVNode("text", { class: "add-icon" }, "+")
          ])) : vue.createCommentVNode("v-if", true)
        ]),
        vue.createCommentVNode(" 分割线 "),
        vue.createElementVNode("view", { class: "divider" }),
        vue.createCommentVNode(" 选择的场 "),
        vue.createElementVNode("view", {
          class: "select-item",
          onClick: _cache[9] || (_cache[9] = (...args) => $options.showFieldSelector && $options.showFieldSelector(...args))
        }, [
          vue.createElementVNode("image", {
            src: _imports_3,
            class: "item-icon field-icon"
          }),
          vue.createElementVNode(
            "text",
            {
              class: vue.normalizeClass(["item-text", { "selected": $data.selectedField }])
            },
            vue.toDisplayString($data.selectedField ? $data.selectedField.name : "选择的场"),
            3
            /* TEXT, CLASS */
          ),
          vue.createElementVNode("image", {
            src: _imports_1$4,
            class: "arrow-icon",
            mode: "aspectFit"
          })
        ]),
        vue.createCommentVNode(" 分割线 "),
        vue.createElementVNode("view", { class: "divider" }),
        vue.createCommentVNode(" 设置活动人数上限 "),
        vue.createElementVNode("view", {
          class: "select-item",
          onClick: _cache[10] || (_cache[10] = (...args) => $options.setMaxPeople && $options.setMaxPeople(...args))
        }, [
          vue.createElementVNode("image", {
            src: _imports_4,
            class: "item-icon people-icon"
          }),
          vue.createElementVNode(
            "text",
            { class: "item-text" },
            vue.toDisplayString($data.maxPeople ? `上限：${$data.maxPeople}人` : "设置活动人数上限"),
            1
            /* TEXT */
          ),
          vue.createElementVNode("image", {
            src: _imports_1$4,
            class: "arrow-icon",
            mode: "aspectFit"
          })
        ]),
        vue.createCommentVNode(" 分割线 "),
        vue.createElementVNode("view", { class: "divider" }),
        vue.createCommentVNode(" 设置报名模式 "),
        vue.createElementVNode("view", {
          class: "select-item",
          onClick: _cache[11] || (_cache[11] = (...args) => $options.setSignupMode && $options.setSignupMode(...args))
        }, [
          vue.createElementVNode("image", {
            src: _imports_5,
            class: "item-icon signup-icon"
          }),
          vue.createElementVNode(
            "text",
            { class: "item-text" },
            vue.toDisplayString($data.signupMode || "设置报名模式"),
            1
            /* TEXT */
          ),
          vue.createElementVNode("image", {
            src: _imports_1$4,
            class: "arrow-icon",
            mode: "aspectFit"
          })
        ])
      ]),
      vue.createCommentVNode(" 底部发布按钮 "),
      vue.createElementVNode("view", { class: "bottom-actions" }, [
        vue.createElementVNode(
          "view",
          {
            class: vue.normalizeClass(["publish-btn", { "active": $options.canPublish }]),
            onClick: _cache[12] || (_cache[12] = (...args) => $options.publishActivity && $options.publishActivity(...args))
          },
          [
            vue.createElementVNode("text", { class: "publish-text" }, "发布活动")
          ],
          2
          /* CLASS */
        )
      ]),
      vue.createCommentVNode(" @用户弹窗 "),
      $data.showUserPanel ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 0,
        class: "user-panel"
      }, [
        vue.createElementVNode("view", {
          class: "panel-mask",
          onClick: _cache[13] || (_cache[13] = (...args) => $options.hideUserPanel && $options.hideUserPanel(...args))
        }),
        vue.createElementVNode("view", { class: "panel-box" }, [
          vue.createElementVNode("view", { class: "panel-box-header" }, [
            vue.createElementVNode("text", { class: "panel-box-title" }, "选择用户"),
            vue.createElementVNode("text", {
              class: "panel-close",
              onClick: _cache[14] || (_cache[14] = (...args) => $options.hideUserPanel && $options.hideUserPanel(...args))
            }, "×")
          ]),
          vue.createElementVNode("scroll-view", {
            class: "user-list",
            "scroll-y": ""
          }, [
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.userList, (user) => {
                return vue.openBlock(), vue.createElementBlock("view", {
                  class: "user-item",
                  key: user.id,
                  onClick: ($event) => $options.selectUser(user)
                }, [
                  vue.createElementVNode("image", {
                    src: user.avatar,
                    class: "user-avatar"
                  }, null, 8, ["src"]),
                  vue.createElementVNode(
                    "text",
                    { class: "user-name" },
                    vue.toDisplayString(user.name),
                    1
                    /* TEXT */
                  )
                ], 8, ["onClick"]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ])
      ])) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" #话题弹窗 "),
      $data.showTopicPanel ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 1,
        class: "topic-panel"
      }, [
        vue.createElementVNode("view", {
          class: "panel-mask",
          onClick: _cache[15] || (_cache[15] = (...args) => $options.hideTopicPanel && $options.hideTopicPanel(...args))
        }),
        vue.createElementVNode("view", { class: "panel-box" }, [
          vue.createElementVNode("view", { class: "panel-box-header" }, [
            vue.createElementVNode("text", { class: "panel-box-title" }, "添加话题"),
            vue.createElementVNode("text", {
              class: "panel-close",
              onClick: _cache[16] || (_cache[16] = (...args) => $options.hideTopicPanel && $options.hideTopicPanel(...args))
            }, "×")
          ]),
          vue.createElementVNode("view", { class: "topic-input-section" }, [
            vue.withDirectives(vue.createElementVNode(
              "input",
              {
                "onUpdate:modelValue": _cache[17] || (_cache[17] = ($event) => $data.customTopic = $event),
                class: "topic-input",
                placeholder: "输入话题",
                "placeholder-style": "color: #999999",
                onConfirm: _cache[18] || (_cache[18] = (...args) => $options.addCustomTopic && $options.addCustomTopic(...args))
              },
              null,
              544
              /* NEED_HYDRATION, NEED_PATCH */
            ), [
              [vue.vModelText, $data.customTopic]
            ]),
            vue.createElementVNode("view", {
              class: "add-topic-btn",
              onClick: _cache[19] || (_cache[19] = (...args) => $options.addCustomTopic && $options.addCustomTopic(...args))
            }, [
              vue.createElementVNode("text", { class: "add-topic-text" }, "添加")
            ])
          ]),
          vue.createElementVNode("view", { class: "hot-topics-section" }, [
            vue.createElementVNode("text", { class: "section-title" }, "热门话题"),
            vue.createElementVNode("scroll-view", {
              class: "topic-list",
              "scroll-y": ""
            }, [
              (vue.openBlock(true), vue.createElementBlock(
                vue.Fragment,
                null,
                vue.renderList($data.hotTopics, (topic, index) => {
                  return vue.openBlock(), vue.createElementBlock("view", {
                    class: "topic-item",
                    key: index,
                    onClick: ($event) => $options.selectTopic(topic)
                  }, [
                    vue.createElementVNode(
                      "text",
                      { class: "topic-name" },
                      "#" + vue.toDisplayString(topic),
                      1
                      /* TEXT */
                    )
                  ], 8, ["onClick"]);
                }),
                128
                /* KEYED_FRAGMENT */
              ))
            ])
          ])
        ])
      ])) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" 选择活动时间弹窗 "),
      $data.showTimePanel ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 2,
        class: "time-panel"
      }, [
        vue.createElementVNode("view", {
          class: "panel-mask",
          onClick: _cache[20] || (_cache[20] = (...args) => $options.hideTimePanel && $options.hideTimePanel(...args))
        }),
        vue.createElementVNode("view", { class: "time-panel-content" }, [
          vue.createElementVNode("view", { class: "time-panel-header" }, [
            vue.createElementVNode("view", {
              class: "back-btn",
              onClick: _cache[21] || (_cache[21] = (...args) => $options.hideTimePanel && $options.hideTimePanel(...args))
            }, [
              vue.createElementVNode("image", {
                src: _imports_1$4,
                class: "back-icon",
                mode: "aspectFit"
              })
            ]),
            vue.createElementVNode("text", { class: "panel-title" }, "选择活动时间")
          ]),
          vue.createElementVNode("view", { class: "time-section" }, [
            vue.createElementVNode("view", { class: "time-row" }, [
              vue.createElementVNode("text", { class: "time-label" }, "开始时间"),
              vue.createElementVNode("picker", {
                mode: "date",
                value: $data.startDate,
                onChange: _cache[22] || (_cache[22] = (...args) => $options.onStartDateChange && $options.onStartDateChange(...args))
              }, [
                vue.createElementVNode("view", { class: "time-picker" }, [
                  vue.createElementVNode(
                    "text",
                    { class: "time-value" },
                    vue.toDisplayString($data.startDate ? $data.startDate.split("-")[1] + "月" + $data.startDate.split("-")[2] + "日" : "10月17日"),
                    1
                    /* TEXT */
                  )
                ])
              ], 40, ["value"]),
              vue.createElementVNode("picker", {
                mode: "time",
                value: $data.startTime,
                onChange: _cache[23] || (_cache[23] = (...args) => $options.onStartTimeChange && $options.onStartTimeChange(...args))
              }, [
                vue.createElementVNode("view", { class: "time-picker" }, [
                  vue.createElementVNode(
                    "text",
                    { class: "time-value" },
                    vue.toDisplayString($data.startTime || "11:00"),
                    1
                    /* TEXT */
                  )
                ])
              ], 40, ["value"])
            ]),
            vue.createElementVNode("view", { class: "time-row" }, [
              vue.createElementVNode("text", { class: "time-label" }, "结束时间"),
              vue.createElementVNode("picker", {
                mode: "date",
                value: $data.endDate,
                onChange: _cache[24] || (_cache[24] = (...args) => $options.onEndDateChange && $options.onEndDateChange(...args))
              }, [
                vue.createElementVNode("view", { class: "time-picker" }, [
                  vue.createElementVNode(
                    "text",
                    { class: "time-value" },
                    vue.toDisplayString($data.endDate ? $data.endDate.split("-")[1] + "月" + $data.endDate.split("-")[2] + "日" : "10月17日"),
                    1
                    /* TEXT */
                  )
                ])
              ], 40, ["value"]),
              vue.createElementVNode("picker", {
                mode: "time",
                value: $data.endTime,
                onChange: _cache[25] || (_cache[25] = (...args) => $options.onEndTimeChange && $options.onEndTimeChange(...args))
              }, [
                vue.createElementVNode("view", { class: "time-picker" }, [
                  vue.createElementVNode(
                    "text",
                    { class: "time-value" },
                    vue.toDisplayString($data.endTime || "12:00"),
                    1
                    /* TEXT */
                  )
                ])
              ], 40, ["value"])
            ])
          ]),
          vue.createElementVNode("view", {
            class: "time-save-btn",
            onClick: _cache[26] || (_cache[26] = (...args) => $options.saveTime && $options.saveTime(...args))
          }, [
            vue.createElementVNode("text", { class: "save-btn-text" }, "保存")
          ])
        ])
      ])) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" 选择地点弹窗 "),
      $data.showLocationPanel ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 3,
        class: "location-panel"
      }, [
        vue.createElementVNode("view", {
          class: "panel-mask",
          onClick: _cache[27] || (_cache[27] = (...args) => $options.hideLocationPanel && $options.hideLocationPanel(...args))
        }),
        vue.createElementVNode("view", { class: "location-panel-content" }, [
          vue.createElementVNode("view", { class: "location-panel-header" }, [
            vue.createElementVNode("view", {
              class: "back-btn",
              onClick: _cache[28] || (_cache[28] = (...args) => $options.hideLocationPanel && $options.hideLocationPanel(...args))
            }, [
              vue.createElementVNode("image", {
                src: _imports_1$4,
                class: "back-icon",
                mode: "aspectFit"
              })
            ]),
            vue.createElementVNode("text", { class: "panel-title" }, "选择地点")
          ]),
          vue.createElementVNode("view", { class: "location-input-section" }, [
            vue.createElementVNode("view", { class: "location-search-box" }, [
              vue.withDirectives(vue.createElementVNode(
                "input",
                {
                  "onUpdate:modelValue": _cache[29] || (_cache[29] = ($event) => $data.locationName = $event),
                  class: "location-input",
                  placeholder: "输入地点",
                  "placeholder-style": "color: #999999"
                },
                null,
                512
                /* NEED_PATCH */
              ), [
                [vue.vModelText, $data.locationName]
              ]),
              vue.createElementVNode("view", {
                class: "location-search-btn",
                onClick: _cache[30] || (_cache[30] = (...args) => $options.searchLocation && $options.searchLocation(...args))
              }, [
                vue.createElementVNode("text", { class: "search-btn-text" }, "搜索")
              ])
            ]),
            vue.withDirectives(vue.createElementVNode(
              "textarea",
              {
                "onUpdate:modelValue": _cache[31] || (_cache[31] = ($event) => $data.locationDetail = $event),
                class: "location-detail-input",
                placeholder: "输入详细地址...",
                "placeholder-style": "color: #999999",
                maxlength: "200"
              },
              null,
              512
              /* NEED_PATCH */
            ), [
              [vue.vModelText, $data.locationDetail]
            ])
          ]),
          vue.createElementVNode("view", {
            class: "location-save-btn",
            onClick: _cache[32] || (_cache[32] = (...args) => $options.saveLocation && $options.saveLocation(...args))
          }, [
            vue.createElementVNode("text", { class: "save-btn-text" }, "保存")
          ])
        ])
      ])) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" 设置活动人数上限弹窗 "),
      $data.showPeoplePanel ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 4,
        class: "people-panel"
      }, [
        vue.createElementVNode("view", {
          class: "panel-mask",
          onClick: _cache[33] || (_cache[33] = (...args) => $options.hidePeoplePanel && $options.hidePeoplePanel(...args))
        }),
        vue.createElementVNode("view", { class: "people-panel-content" }, [
          vue.createElementVNode("view", { class: "people-panel-header" }, [
            vue.createElementVNode("view", {
              class: "back-btn",
              onClick: _cache[34] || (_cache[34] = (...args) => $options.hidePeoplePanel && $options.hidePeoplePanel(...args))
            }, [
              vue.createElementVNode("image", {
                src: _imports_1$4,
                class: "back-icon",
                mode: "aspectFit"
              })
            ]),
            vue.createElementVNode("text", { class: "panel-title" }, "设置活动人数上限")
          ]),
          vue.createElementVNode("scroll-view", {
            class: "people-list",
            "scroll-y": ""
          }, [
            (vue.openBlock(), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList([8, 10, 12, 15, 20, 25, 30, 40, 50, 100], (num) => {
                return vue.createElementVNode("view", {
                  class: "people-item",
                  key: num,
                  onClick: ($event) => $options.selectPeopleCount(num)
                }, [
                  vue.createElementVNode(
                    "text",
                    {
                      class: vue.normalizeClass(["people-text", { "selected": $data.maxPeople == num }])
                    },
                    vue.toDisplayString(num) + "人",
                    3
                    /* TEXT, CLASS */
                  )
                ], 8, ["onClick"]);
              }),
              64
              /* STABLE_FRAGMENT */
            ))
          ]),
          vue.createElementVNode("view", {
            class: "people-save-btn",
            onClick: _cache[35] || (_cache[35] = (...args) => $options.hidePeoplePanel && $options.hidePeoplePanel(...args))
          }, [
            vue.createElementVNode("text", { class: "save-btn-text" }, "保存")
          ])
        ])
      ])) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" 设置报名模式弹窗 "),
      $data.showSignupModePanel ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 5,
        class: "signup-mode-panel"
      }, [
        vue.createElementVNode("view", {
          class: "panel-mask",
          onClick: _cache[36] || (_cache[36] = (...args) => $options.hideSignupModePanel && $options.hideSignupModePanel(...args))
        }),
        vue.createElementVNode("view", { class: "signup-mode-content" }, [
          vue.createElementVNode("view", { class: "signup-mode-header" }, [
            vue.createElementVNode("view", {
              class: "back-btn",
              onClick: _cache[37] || (_cache[37] = (...args) => $options.hideSignupModePanel && $options.hideSignupModePanel(...args))
            }, [
              vue.createElementVNode("image", {
                src: _imports_1$4,
                class: "back-icon",
                mode: "aspectFit"
              })
            ]),
            vue.createElementVNode("text", { class: "panel-title" }, "设置报名模式")
          ]),
          vue.createElementVNode("view", { class: "signup-mode-list" }, [
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["signup-mode-item", { "selected": $data.signupMode === "无需报名，可直接参加" }]),
                onClick: _cache[38] || (_cache[38] = ($event) => $options.selectSignupMode("无需报名，可直接参加"))
              },
              [
                vue.createElementVNode(
                  "view",
                  {
                    class: vue.normalizeClass(["checkbox", { "checked": $data.signupMode === "无需报名，可直接参加" }])
                  },
                  [
                    $data.signupMode === "无需报名，可直接参加" ? (vue.openBlock(), vue.createElementBlock("text", {
                      key: 0,
                      class: "check-icon"
                    }, "✓")) : vue.createCommentVNode("v-if", true)
                  ],
                  2
                  /* CLASS */
                ),
                vue.createElementVNode("text", { class: "mode-text" }, "无需报名，可直接参加")
              ],
              2
              /* CLASS */
            ),
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["signup-mode-item", { "selected": $data.signupMode === "需报名后参加" }]),
                onClick: _cache[39] || (_cache[39] = ($event) => $options.selectSignupMode("需报名后参加"))
              },
              [
                vue.createElementVNode(
                  "view",
                  {
                    class: vue.normalizeClass(["checkbox", { "checked": $data.signupMode === "需报名后参加" }])
                  },
                  [
                    $data.signupMode === "需报名后参加" ? (vue.openBlock(), vue.createElementBlock("text", {
                      key: 0,
                      class: "check-icon"
                    }, "✓")) : vue.createCommentVNode("v-if", true)
                  ],
                  2
                  /* CLASS */
                ),
                vue.createElementVNode("text", { class: "mode-text" }, "需报名后参加")
              ],
              2
              /* CLASS */
            ),
            vue.createElementVNode(
              "view",
              {
                class: vue.normalizeClass(["signup-mode-item", { "selected": $data.signupMode === "报名与否皆可参加" }]),
                onClick: _cache[40] || (_cache[40] = ($event) => $options.selectSignupMode("报名与否皆可参加"))
              },
              [
                vue.createElementVNode(
                  "view",
                  {
                    class: vue.normalizeClass(["checkbox", { "checked": $data.signupMode === "报名与否皆可参加" }])
                  },
                  [
                    $data.signupMode === "报名与否皆可参加" ? (vue.openBlock(), vue.createElementBlock("text", {
                      key: 0,
                      class: "check-icon"
                    }, "✓")) : vue.createCommentVNode("v-if", true)
                  ],
                  2
                  /* CLASS */
                ),
                vue.createElementVNode("text", { class: "mode-text" }, "报名与否皆可参加")
              ],
              2
              /* CLASS */
            )
          ])
        ])
      ])) : vue.createCommentVNode("v-if", true),
      vue.createCommentVNode(" 选择场弹窗 "),
      $data.showFieldPanel ? (vue.openBlock(), vue.createElementBlock("view", {
        key: 6,
        class: "field-panel"
      }, [
        vue.createElementVNode("view", { class: "panel-content" }, [
          vue.createElementVNode("view", { class: "panel-header" }, [
            vue.createElementVNode("view", {
              class: "back-btn",
              onClick: _cache[41] || (_cache[41] = (...args) => $options.hideFieldSelector && $options.hideFieldSelector(...args))
            }, [
              vue.createElementVNode("image", {
                src: _imports_1$4,
                class: "back-icon",
                mode: "aspectFit"
              })
            ]),
            vue.createElementVNode("text", { class: "panel-title" }, "选择的场")
          ]),
          vue.createElementVNode("view", { class: "search-section" }, [
            vue.createElementVNode("view", { class: "search-box" }, [
              vue.withDirectives(vue.createElementVNode(
                "input",
                {
                  "onUpdate:modelValue": _cache[42] || (_cache[42] = ($event) => $data.searchKeyword = $event),
                  class: "search-input",
                  placeholder: "输入场",
                  "placeholder-style": "color: #999999"
                },
                null,
                512
                /* NEED_PATCH */
              ), [
                [vue.vModelText, $data.searchKeyword]
              ])
            ]),
            vue.createElementVNode("view", {
              class: "search-btn",
              onClick: _cache[43] || (_cache[43] = (...args) => $options.searchField && $options.searchField(...args))
            }, [
              vue.createElementVNode("text", { class: "search-btn-text" }, "搜索")
            ])
          ]),
          vue.createElementVNode("scroll-view", {
            class: "fields-list",
            "scroll-y": ""
          }, [
            vue.createElementVNode("text", { class: "list-title" }, "我关注的场"),
            (vue.openBlock(true), vue.createElementBlock(
              vue.Fragment,
              null,
              vue.renderList($data.myFields, (field) => {
                return vue.openBlock(), vue.createElementBlock("view", {
                  class: "field-item",
                  key: field.id,
                  onClick: ($event) => $options.selectField(field)
                }, [
                  vue.createElementVNode("image", {
                    src: field.avatar,
                    class: "field-avatar"
                  }, null, 8, ["src"]),
                  vue.createElementVNode("view", { class: "field-info" }, [
                    vue.createElementVNode(
                      "text",
                      { class: "field-name" },
                      vue.toDisplayString(field.name),
                      1
                      /* TEXT */
                    ),
                    vue.createElementVNode("view", { class: "field-stats" }, [
                      vue.createElementVNode(
                        "text",
                        { class: "stat-text" },
                        vue.toDisplayString(field.followers) + "关注",
                        1
                        /* TEXT */
                      ),
                      vue.createElementVNode(
                        "text",
                        { class: "stat-text" },
                        vue.toDisplayString(field.posts) + "帖子",
                        1
                        /* TEXT */
                      )
                    ])
                  ])
                ], 8, ["onClick"]);
              }),
              128
              /* KEYED_FRAGMENT */
            ))
          ])
        ])
      ])) : vue.createCommentVNode("v-if", true)
    ]);
  }
  const PagesActivityCreateActivity = /* @__PURE__ */ _export_sfc(_sfc_main$1, [["render", _sfc_render$1], ["__scopeId", "data-v-43900ae0"], ["__file", "D:/code space2/xystapp/android/pages/activity/create-activity.vue"]]);
  __definePage("pages/index/index", PagesIndexIndex);
  __definePage("pages/login/login", PagesLoginLogin);
  __definePage("pages/profile/profile", PagesProfileProfile);
  __definePage("pages/phone-login/phone-login", PagesPhoneLoginPhoneLogin);
  __definePage("pages/register/register", PagesRegisterRegister);
  __definePage("pages/password-login/password-login", PagesPasswordLoginPasswordLogin);
  __definePage("pages/profile-setup/profile-setup", PagesProfileSetupProfileSetup);
  __definePage("pages/mbti-setup/mbti-setup", PagesMbtiSetupMbtiSetup);
  __definePage("pages/mbti-type/mbti-type", PagesMbtiTypeMbtiType);
  __definePage("pages/post-detail/post-detail", PagesPostDetailPostDetail);
  __definePage("pages/message/message", PagesMessageMessage);
  __definePage("pages/message/like-collect", PagesMessageLikeCollect);
  __definePage("pages/message/follow-subscribe", PagesMessageFollowSubscribe);
  __definePage("pages/message/comment-at", PagesMessageCommentAt);
  __definePage("pages/event-detail/event-detail", PagesEventDetailEventDetail);
  __definePage("pages/message/chat-detail", PagesMessageChatDetail);
  __definePage("pages/message/chat-settings", PagesMessageChatSettings);
  __definePage("pages/message/group-settings", PagesMessageGroupSettings);
  __definePage("pages/message/group-notice", PagesMessageGroupNotice);
  __definePage("pages/message/group-activity", PagesMessageGroupActivity);
  __definePage("pages/message/group-members", PagesMessageGroupMembers);
  __definePage("pages/post/create-post", PagesPostCreatePost);
  __definePage("pages/activity/create-activity", PagesActivityCreateActivity);
  const _sfc_main = {
    onLaunch: function() {
      formatAppLog("log", "at App.vue:10", "=== APP 启动 ===");
    },
    onShow: function() {
      formatAppLog("log", "at App.vue:13", "=== APP 显示 ===");
    },
    onHide: function() {
      formatAppLog("log", "at App.vue:16", "=== APP 隐藏 ===");
    }
  };
  function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("view", { id: "app" }, [
      vue.createCommentVNode(" 这里是应用的根组件 ")
    ]);
  }
  const App = /* @__PURE__ */ _export_sfc(_sfc_main, [["render", _sfc_render], ["__file", "D:/code space2/xystapp/android/App.vue"]]);
  function createApp() {
    const app = vue.createVueApp(App);
    return {
      app
    };
  }
  const { app: __app__, Vuex: __Vuex__, Pinia: __Pinia__ } = createApp();
  uni.Vuex = __Vuex__;
  uni.Pinia = __Pinia__;
  __app__.provide("__globalStyles", __uniConfig.styles);
  __app__._component.mpType = "app";
  __app__._component.render = () => {
  };
  __app__.mount("#app");
})(Vue);
