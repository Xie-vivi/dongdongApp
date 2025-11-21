"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
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
      common_vendor.index.__f__("log", "at pages/mbti-type/mbti-type.vue:111", "选择的MBTI类型:", group, type);
    },
    // 返回上一步
    goBack() {
      common_vendor.index.navigateBack();
    },
    // 进入下一步
    goNext() {
      if (!this.hasAnySelection) {
        common_vendor.index.showToast({
          title: "请至少选择一个MBTI类型",
          icon: "none"
        });
        return;
      }
      common_vendor.index.setStorageSync("userMbtiEITypes", this.selectedTypes);
      common_vendor.index.__f__("log", "at pages/mbti-type/mbti-type.vue:131", "MBTI EI类型已保存:", this.selectedTypes);
      common_vendor.index.reLaunch({
        url: "/pages/index/index"
      });
    }
  },
  // 页面加载时获取已保存的MBTI信息
  onLoad() {
    const savedMbtiTypes = common_vendor.index.getStorageSync("userMbtiEITypes");
    if (savedMbtiTypes) {
      this.selectedTypes = savedMbtiTypes;
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_vendor.f($data.eTypes, (type, k0, i0) => {
      return {
        a: common_vendor.t(type.label),
        b: type.value,
        c: $data.selectedTypes.e === type.value ? 1 : "",
        d: common_vendor.o(($event) => $options.selectMbti("e", type.value), type.value)
      };
    }),
    b: common_vendor.f($data.iTypes, (type, k0, i0) => {
      return {
        a: common_vendor.t(type.label),
        b: type.value,
        c: $data.selectedTypes.i === type.value ? 1 : "",
        d: common_vendor.o(($event) => $options.selectMbti("i", type.value), type.value)
      };
    }),
    c: common_vendor.o((...args) => $options.goBack && $options.goBack(...args)),
    d: $options.hasAnySelection ? 1 : "",
    e: common_vendor.o((...args) => $options.goNext && $options.goNext(...args)),
    f: !$options.hasAnySelection
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-5353da89"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/mbti-type/mbti-type.js.map
