# Flutter简介

> [Flutter实战·第二版](https://book.flutterchina.club/)

Flutter 是 Google 发布的一个用于创建**跨平台、高性能**移动应用的框架。

从 2017 年 Google I/O 大会上，Google 首次发布 Flutter 到 2021年8月底，已经有 127K 的 Star，Star 数量 Github 上排名前 20 。经历了4年多的时间，Flutter 生态系统得以快速增长，国内外有非常多基于 Flutter 的成功案例，国内的互联网公司基本都有专门的 Flutter 团队。总之，历时 4 年，Flutter 发展飞快，已在业界得到了广泛的关注和认可，在开发者中受到了热烈的欢迎，成为了移动跨端开发中最受欢迎的框架之一。

*Flutter 没有使用原生控件，实现了一个自绘引擎，使用自身的布局、绘制系统。*

**Flutter的优势：**

- 生态：Flutter 生态系统发展迅速，社区非常活跃，无论是开发者数量还是第三方组件都已经非常可观。
- 技术支持：现在 Google 正在大力推广Flutter，Flutter 的作者中很多人都是来自Chromium团队，并且 Github上活跃度很高。另一个角度，从 Flutter 诞生到现在，频繁的版本发布也可以看出 Google 对 Flutter的投入的资源不小，所以在官方技术支持这方面，大可不必担心。
- 开发效率：一套代码，多端运行；并且在开发过程中 Flutter 的热重载可帮助开发者快速地进行测试、构建UI、添加功能并更快地修复错误。在 iOS 和 Android 模拟器或真机上可以实现毫秒级热重载，并且不会丢失状态。这真的很棒，相信我，如果你是一名原生开发者，体验了Flutter开发流后，很可能就不想重新回去做原生了，毕竟很少有人不吐槽原生开发的编译速度。

**跨平台技术对比：**

|技术类型|UI渲染方式|性能|开发效率|动态化|框架代表|
|--|--|--|--|--|--|
|H5 + 原生|WebView渲染|一般|高|支持|Cordova、Ionic|
|JavaScript + 原生渲染|原生控件渲染|好|中|支持|RN、Weex|
|自绘UI + 原生|调用系统API渲染|好|Flutter高, Qt低|默认不支持|Qt、Flutter|

**Flutter为什么不支持动态化？**

Flutter 的 Release 包默认是使用 Dart AOT 模式编译的，所以不支持动态化，但 Dart 还有 JIT 或 snapshot 运行方式，这些模式都是支持动态化的。

> [美团外卖Flutter动态化实践](https://tech.meituan.com/2020/06/23/meituan-flutter-flap.html)