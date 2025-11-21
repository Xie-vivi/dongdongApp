"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
if (!Math) {
  "./pages/login/login.js";
  "./pages/index/index.js";
  "./pages/profile/profile.js";
  "./pages/phone-login/phone-login.js";
  "./pages/register/register.js";
  "./pages/password-login/password-login.js";
  "./pages/profile-setup/profile-setup.js";
  "./pages/mbti-setup/mbti-setup.js";
  "./pages/mbti-type/mbti-type.js";
}
const _sfc_main = {
  onLaunch: function() {
    common_vendor.index.__f__("log", "at App.vue:10", "=== APP 启动 ===");
  },
  onShow: function() {
    common_vendor.index.__f__("log", "at App.vue:13", "=== APP 显示 ===");
  },
  onHide: function() {
    common_vendor.index.__f__("log", "at App.vue:16", "=== APP 隐藏 ===");
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {};
}
const App = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
function createApp() {
  const app = common_vendor.createSSRApp(App);
  return {
    app
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
//# sourceMappingURL=../.sourcemap/mp-weixin/app.js.map
