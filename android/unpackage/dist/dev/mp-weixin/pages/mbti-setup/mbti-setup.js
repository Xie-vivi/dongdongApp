"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
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
      common_vendor.index.__f__("log", "at pages/mbti-setup/mbti-setup.vue:152", "选择的MBTI类型:", group, type);
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
      common_vendor.index.setStorageSync("userMbtiTypes", this.selectedTypes);
      common_vendor.index.__f__("log", "at pages/mbti-setup/mbti-setup.vue:172", "MBTI类型已保存:", this.selectedTypes);
      common_vendor.index.navigateTo({
        url: "/pages/mbti-type/mbti-type"
      });
    }
  },
  // 页面加载时获取已保存的MBTI信息
  onLoad() {
    const savedMbtiTypes = common_vendor.index.getStorageSync("userMbtiTypes");
    if (savedMbtiTypes) {
      this.selectedTypes = savedMbtiTypes;
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_vendor.f($data.ntTypes, (type, k0, i0) => {
      return {
        a: common_vendor.t(type.label),
        b: type.value,
        c: $data.selectedTypes.nt === type.value ? 1 : "",
        d: common_vendor.o(($event) => $options.selectMbti("nt", type.value), type.value)
      };
    }),
    b: common_vendor.f($data.nfTypes, (type, k0, i0) => {
      return {
        a: common_vendor.t(type.label),
        b: type.value,
        c: $data.selectedTypes.nf === type.value ? 1 : "",
        d: common_vendor.o(($event) => $options.selectMbti("nf", type.value), type.value)
      };
    }),
    c: common_vendor.f($data.sjTypes, (type, k0, i0) => {
      return {
        a: common_vendor.t(type.label),
        b: type.value,
        c: $data.selectedTypes.sj === type.value ? 1 : "",
        d: common_vendor.o(($event) => $options.selectMbti("sj", type.value), type.value)
      };
    }),
    d: common_vendor.f($data.spTypes, (type, k0, i0) => {
      return {
        a: common_vendor.t(type.label),
        b: type.value,
        c: $data.selectedTypes.sp === type.value ? 1 : "",
        d: common_vendor.o(($event) => $options.selectMbti("sp", type.value), type.value)
      };
    }),
    e: common_vendor.o((...args) => $options.goBack && $options.goBack(...args)),
    f: $options.hasAnySelection ? 1 : "",
    g: common_vendor.o((...args) => $options.goNext && $options.goNext(...args)),
    h: !$options.hasAnySelection
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-a7f4e6a3"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/mbti-setup/mbti-setup.js.map
