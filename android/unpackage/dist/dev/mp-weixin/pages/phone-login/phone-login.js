"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
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
    common_vendor.index.__f__("log", "at pages/phone-login/phone-login.vue:118", "手机验证码登录页面加载完成");
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
        common_vendor.index.showToast({
          title: "请输入正确的手机号",
          icon: "none"
        });
        return;
      }
      try {
        common_vendor.index.showLoading({
          title: "发送中..."
        });
        await this.simulateSendCode();
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: "验证码已发送",
          icon: "success"
        });
        this.startCountdown();
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
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
        common_vendor.index.setStorageSync("userToken", "phone_sms_login_token");
        common_vendor.index.setStorageSync("loginType", "phone_sms");
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
    // 切换密码登录
    handleSwitchLogin() {
      common_vendor.index.navigateTo({
        url: "/pages/password-login/password-login"
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
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_assets._imports_0,
    b: common_vendor.o([($event) => $data.phoneNumber = $event.detail.value, (...args) => $options.onPhoneInput && $options.onPhoneInput(...args)]),
    c: $data.phoneNumber,
    d: $data.verificationCode,
    e: common_vendor.o(($event) => $data.verificationCode = $event.detail.value),
    f: common_vendor.t($data.countdown > 0 ? `重新发送(${$data.countdown}s)` : "发送验证码"),
    g: !$options.canSendCode || $data.countdown > 0,
    h: common_vendor.o((...args) => $options.sendVerificationCode && $options.sendVerificationCode(...args)),
    i: !$options.canLogin,
    j: $options.canLogin ? 1 : "",
    k: common_vendor.o((...args) => $options.handleLogin && $options.handleLogin(...args)),
    l: common_vendor.o((...args) => $options.handleRegister && $options.handleRegister(...args)),
    m: common_vendor.o((...args) => $options.handleSwitchLogin && $options.handleSwitchLogin(...args)),
    n: common_assets._imports_1,
    o: common_vendor.o((...args) => $options.loginWithWechat && $options.loginWithWechat(...args)),
    p: common_assets._imports_2,
    q: common_vendor.o((...args) => $options.loginWithPhone && $options.loginWithPhone(...args)),
    r: common_assets._imports_3,
    s: common_vendor.o((...args) => $options.loginWithQQ && $options.loginWithQQ(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-e0aec1e2"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/phone-login/phone-login.js.map
