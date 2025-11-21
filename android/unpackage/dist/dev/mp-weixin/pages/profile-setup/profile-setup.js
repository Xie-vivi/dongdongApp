"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      userInfo: {
        avatar: "/static/type18/t.png",
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
    common_vendor.index.__f__("log", "at pages/profile-setup/profile-setup.vue:164", "个人信息设置页面加载完成");
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
      common_vendor.index.showToast({
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
      common_vendor.index.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: (res) => {
          this.userInfo.avatar = res.tempFilePaths[0];
          common_vendor.index.showToast({
            title: "头像已更新",
            icon: "success",
            duration: 1e3
          });
        },
        fail: () => {
          common_vendor.index.showToast({
            title: "取消选择头像",
            icon: "none",
            duration: 1e3
          });
        }
      });
    },
    // 返回上一步
    goBack() {
      common_vendor.index.navigateBack();
    },
    // 下一步处理
    async handleNext() {
      if (!this.canProceed) {
        common_vendor.index.showToast({
          title: "请完善基本信息",
          icon: "none"
        });
        return;
      }
      try {
        await this.saveUserProfile();
        common_vendor.index.setStorageSync("userProfile", this.userInfo);
        common_vendor.index.navigateTo({
          url: "/pages/mbti-setup/mbti-setup"
        });
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: "保存失败，请重试",
          icon: "none"
        });
      }
    },
    // 模拟保存用户资料
    saveUserProfile() {
      return new Promise((resolve) => {
        setTimeout(() => {
          common_vendor.index.__f__("log", "at pages/profile-setup/profile-setup.vue:321", "用户信息保存成功:", this.userInfo);
          resolve({ success: true });
        }, 1e3);
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: $data.userInfo.avatar,
    b: common_assets._imports_0$3,
    c: common_vendor.o((...args) => $options.selectAvatar && $options.selectAvatar(...args)),
    d: common_vendor.o([($event) => $data.userInfo.username = $event.detail.value, (...args) => $options.onUsernameInput && $options.onUsernameInput(...args)]),
    e: $data.userInfo.username,
    f: common_assets._imports_1$2,
    g: common_vendor.o((...args) => $options.generateRandomName && $options.generateRandomName(...args)),
    h: $data.userInfo.gender === "male" ? 1 : "",
    i: common_vendor.o(($event) => $options.selectGender("male")),
    j: $data.userInfo.gender === "female" ? 1 : "",
    k: common_vendor.o(($event) => $options.selectGender("female")),
    l: $data.userInfo.gender === "other" ? 1 : "",
    m: common_vendor.o(($event) => $options.selectGender("other")),
    n: common_vendor.t($data.userInfo.birthYear),
    o: $data.yearOptions,
    p: $data.selectedYearIndex,
    q: common_vendor.o((...args) => $options.onYearChange && $options.onYearChange(...args)),
    r: common_vendor.t($data.userInfo.birthMonth),
    s: $data.monthOptions,
    t: $data.selectedMonthIndex,
    v: common_vendor.o((...args) => $options.onMonthChange && $options.onMonthChange(...args)),
    w: common_vendor.t($data.userInfo.birthDay),
    x: $data.dayOptions,
    y: $data.selectedDayIndex,
    z: common_vendor.o((...args) => $options.onDayChange && $options.onDayChange(...args)),
    A: common_vendor.o((...args) => $options.goBack && $options.goBack(...args)),
    B: !$options.canProceed,
    C: $options.canProceed ? 1 : "",
    D: common_vendor.o((...args) => $options.handleNext && $options.handleNext(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-27de631f"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/profile-setup/profile-setup.js.map
