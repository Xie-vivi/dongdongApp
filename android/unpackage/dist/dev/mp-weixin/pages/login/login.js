"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      isLoading: false
    };
  },
  onLoad() {
    common_vendor.index.__f__("log", "at pages/login/login.vue:60", "登录页面加载完成");
  },
  methods: {
    // 本机号码一键登录
    async handlePhoneLogin() {
      this.isLoading = true;
      try {
        await this.simulateLogin("phone");
        common_vendor.index.setStorageSync("userToken", "phone_login_token");
        common_vendor.index.setStorageSync("loginType", "phone");
        common_vendor.index.showToast({
          title: "登录成功",
          icon: "success"
        });
        setTimeout(() => {
          common_vendor.index.reLaunch({
            url: "/pages/index/index"
          });
        }, 1500);
      } catch (error) {
        common_vendor.index.showToast({
          title: "登录失败，请重试",
          icon: "none"
        });
      } finally {
        this.isLoading = false;
      }
    },
    // 手机验证码登录
    handleSmsLogin() {
      common_vendor.index.navigateTo({
        url: "/pages/phone-login/phone-login"
      });
    },
    // 注册账号
    handleRegister() {
      common_vendor.index.navigateTo({
        url: "/pages/register/register"
      });
    },
    // 微信登录
    async loginWithWechat() {
      this.isLoading = true;
      try {
        await this.simulateLogin("wechat");
        common_vendor.index.setStorageSync("userToken", "wechat_login_token");
        common_vendor.index.setStorageSync("loginType", "wechat");
        common_vendor.index.showToast({
          title: "微信登录成功",
          icon: "success"
        });
        setTimeout(() => {
          common_vendor.index.reLaunch({
            url: "/pages/index/index"
          });
        }, 1500);
      } catch (error) {
        common_vendor.index.showToast({
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
        common_vendor.index.setStorageSync("userToken", "qq_login_token");
        common_vendor.index.setStorageSync("loginType", "qq");
        common_vendor.index.showToast({
          title: "QQ登录成功",
          icon: "success"
        });
        setTimeout(() => {
          common_vendor.index.reLaunch({
            url: "/pages/index/index"
          });
        }, 1500);
      } catch (error) {
        common_vendor.index.showToast({
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
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_assets._imports_0,
    b: common_vendor.o((...args) => $options.handlePhoneLogin && $options.handlePhoneLogin(...args)),
    c: common_vendor.o((...args) => $options.handleSmsLogin && $options.handleSmsLogin(...args)),
    d: common_vendor.o((...args) => $options.handleRegister && $options.handleRegister(...args)),
    e: common_assets._imports_1,
    f: common_vendor.o((...args) => $options.loginWithWechat && $options.loginWithWechat(...args)),
    g: common_assets._imports_3,
    h: common_vendor.o((...args) => $options.loginWithQQ && $options.loginWithQQ(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-e4e4508d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/login/login.js.map
