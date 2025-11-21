"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
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
    common_vendor.index.__f__("log", "at pages/password-login/password-login.vue:114", "账号密码登录页面加载完成");
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
      common_vendor.index.showToast({
        title: "请联系客服重置密码",
        icon: "none"
      });
    },
    // 登录处理
    async handleLogin() {
      if (!this.canLogin) {
        common_vendor.index.showToast({
          title: "请填写完整信息",
          icon: "none"
        });
        return;
      }
      this.isLoading = true;
      try {
        common_vendor.index.showLoading({
          title: "登录中..."
        });
        await this.simulateLogin();
        common_vendor.index.setStorageSync("userToken", "password_login_token");
        common_vendor.index.setStorageSync("loginType", "password");
        common_vendor.index.setStorageSync("userPhone", this.phoneNumber);
        common_vendor.index.hideLoading();
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
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: error.message || "登录失败，请重试",
          icon: "none"
        });
      } finally {
        this.isLoading = false;
      }
    },
    // 注册账号
    handleRegister() {
      common_vendor.index.navigateTo({
        url: "/pages/register/register"
      });
    },
    // 切换验证码登录
    handleSwitchLogin() {
      common_vendor.index.navigateTo({
        url: "/pages/phone-login/phone-login"
      });
    },
    // 微信登录
    async loginWithWechat() {
      try {
        common_vendor.index.showLoading({
          title: "微信登录中..."
        });
        await this.simulateThirdPartyLogin("wechat");
        common_vendor.index.setStorageSync("userToken", "wechat_login_token");
        common_vendor.index.setStorageSync("loginType", "wechat");
        common_vendor.index.hideLoading();
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
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: "微信登录失败",
          icon: "none"
        });
      }
    },
    // 本机号码登录
    async loginWithPhone() {
      try {
        common_vendor.index.showLoading({
          title: "本机号码登录中..."
        });
        await this.simulateThirdPartyLogin("phone");
        common_vendor.index.setStorageSync("userToken", "phone_auto_login_token");
        common_vendor.index.setStorageSync("loginType", "phone_auto");
        common_vendor.index.hideLoading();
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
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: "本机号码登录失败",
          icon: "none"
        });
      }
    },
    // QQ登录
    async loginWithQQ() {
      try {
        common_vendor.index.showLoading({
          title: "QQ登录中..."
        });
        await this.simulateThirdPartyLogin("qq");
        common_vendor.index.setStorageSync("userToken", "qq_login_token");
        common_vendor.index.setStorageSync("loginType", "qq");
        common_vendor.index.hideLoading();
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
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
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
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_assets._imports_0,
    b: common_vendor.o([($event) => $data.phoneNumber = $event.detail.value, (...args) => $options.onPhoneInput && $options.onPhoneInput(...args)]),
    c: $data.phoneNumber,
    d: $data.showPassword ? "text" : "password",
    e: common_vendor.o([($event) => $data.password = $event.detail.value, (...args) => $options.onPasswordInput && $options.onPasswordInput(...args)]),
    f: $data.password,
    g: common_vendor.t($data.showPassword ? "隐藏" : "显示"),
    h: common_vendor.o((...args) => $options.togglePassword && $options.togglePassword(...args)),
    i: common_vendor.o((...args) => $options.handleForgotPassword && $options.handleForgotPassword(...args)),
    j: !$options.canLogin,
    k: $options.canLogin ? 1 : "",
    l: common_vendor.o((...args) => $options.handleLogin && $options.handleLogin(...args)),
    m: common_vendor.o((...args) => $options.handleRegister && $options.handleRegister(...args)),
    n: common_vendor.o((...args) => $options.handleSwitchLogin && $options.handleSwitchLogin(...args)),
    o: common_assets._imports_1,
    p: common_vendor.o((...args) => $options.loginWithWechat && $options.loginWithWechat(...args)),
    q: common_assets._imports_2,
    r: common_vendor.o((...args) => $options.loginWithPhone && $options.loginWithPhone(...args)),
    s: common_assets._imports_3,
    t: common_vendor.o((...args) => $options.loginWithQQ && $options.loginWithQQ(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-24579e20"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/password-login/password-login.js.map
