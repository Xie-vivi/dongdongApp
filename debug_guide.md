# uniapp 调试指南 - 解决界面空白问题

## 🔍 查看完整日志的方法

### 1. HBuilderX 控制台优化
- **运行时**：菜单 → 运行 → 运行到手机或模拟器 → Android
- **控制台位置**：HBuilderX底部"控制台"面板
- **日志级别**：点击控制台右上角的设置图标，选择"显示所有日志"
- **清空日志**：点击控制台的清空按钮，重新运行获取干净的日志

### 2. Chrome DevTools 调试（推荐）
1. 在Android设备上打开应用
2. 在电脑Chrome浏览器中访问：`chrome://inspect`
3. 找到您的设备和应用，点击"inspect"
4. 在DevTools的Console中查看完整的JavaScript错误和日志

### 3. Android Studio Logcat
如果安装了Android Studio：
1. 打开Android Studio
2. 菜单：View → Tool Windows → Logcat
3. 选择您的设备和应用包名
4. 查看完整的系统级日志

### 4. ADB 命令行（需要Android SDK）
```bash
# 查看所有日志
adb logcat

# 只查看应用相关日志（替换为您的包名）
adb logcat | grep "io.dcloud"

# 清空日志后重新开始
adb logcat -c && adb logcat
```

## 🐛 界面空白常见原因

### 1. 路径配置问题
检查 pages.json 中的页面路径是否正确

### 2. 页面组件错误
检查登录页面的Vue语法是否正确

### 3. 依赖问题
检查是否缺少必要的依赖或插件

### 4. 编译缓存问题
清除编译缓存重新运行

## 🔧 调试步骤

1. **清除缓存**：HBuilderX → 运行 → 清除缓存
2. **重新编译**：完全重新编译运行
3. **检查控制台**：查看是否有错误信息
4. **逐步排查**：从简单页面开始测试
