"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
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
        common_vendor.index.showToast({
          title: "请输入手机号",
          icon: "none"
        });
        return;
      }
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (!phoneRegex.test(this.registerForm.phone)) {
        common_vendor.index.showToast({
          title: "请输入正确的手机号",
          icon: "none"
        });
        return;
      }
      if (this.countdown > 0)
        return;
      common_vendor.index.showToast({
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
        common_vendor.index.showToast({
          title: "请输入手机号",
          icon: "none"
        });
        return;
      }
      if (!this.registerForm.password) {
        common_vendor.index.showToast({
          title: "请输入密码",
          icon: "none"
        });
        return;
      }
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        common_vendor.index.showToast({
          title: "两次密码输入不一致",
          icon: "none"
        });
        return;
      }
      if (!this.registerForm.verificationCode) {
        common_vendor.index.showToast({
          title: "请输入验证码",
          icon: "none"
        });
        return;
      }
      common_vendor.index.showLoading({
        title: "注册中..."
      });
      setTimeout(() => {
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: "注册成功",
          icon: "success"
        });
        setTimeout(() => {
          common_vendor.index.navigateTo({
            url: "/pages/profile-setup/profile-setup"
          });
        }, 1500);
      }, 2e3);
    },
    // 微信登录
    loginWithWechat() {
      common_vendor.index.showToast({
        title: "微信登录",
        icon: "none"
      });
    },
    // 电话登录
    loginWithPhone() {
      common_vendor.index.showToast({
        title: "电话登录",
        icon: "none"
      });
    },
    // QQ登录
    loginWithQQ() {
      common_vendor.index.showToast({
        title: "QQ登录",
        icon: "none"
      });
    },
    // 返回登录页面
    goToLogin() {
      common_vendor.index.navigateBack();
    }
  },
  onUnload() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_assets._imports_0,
    b: $data.registerForm.phone,
    c: common_vendor.o(($event) => $data.registerForm.phone = $event.detail.value),
    d: $data.registerForm.password,
    e: common_vendor.o(($event) => $data.registerForm.password = $event.detail.value),
    f: $data.registerForm.confirmPassword,
    g: common_vendor.o(($event) => $data.registerForm.confirmPassword = $event.detail.value),
    h: $data.registerForm.verificationCode,
    i: common_vendor.o(($event) => $data.registerForm.verificationCode = $event.detail.value),
    j: common_vendor.t($data.codeButtonText),
    k: common_vendor.o((...args) => $options.sendVerificationCode && $options.sendVerificationCode(...args)),
    l: common_vendor.o((...args) => $options.handleRegister && $options.handleRegister(...args)),
    m: common_assets._imports_1,
    n: common_vendor.o((...args) => $options.loginWithWechat && $options.loginWithWechat(...args)),
    o: common_assets._imports_2,
    p: common_vendor.o((...args) => $options.loginWithPhone && $options.loginWithPhone(...args)),
    q: common_assets._imports_3,
    r: common_vendor.o((...args) => $options.loginWithQQ && $options.loginWithQQ(...args)),
    s: common_vendor.o((...args) => $options.goToLogin && $options.goToLogin(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-bac4a35d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/register/register.js.map
