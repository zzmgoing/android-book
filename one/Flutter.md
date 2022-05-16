# Flutter

> Flutter 是 Google 发布的一个用于创建**跨平台、高性能**移动应用的框架。  
> Flutter 没有使用原生控件，实现了一个自绘引擎，使用自身的布局、绘制系统。

<details><summary>Flutter简介</summary>

从 2017 年 Google I/O 大会上，Google 首次发布 Flutter 到 2021年8月底，已经有 127K 的 Star，Star 数量 Github 上排名前 20 。经历了4年多的时间，Flutter 生态系统得以快速增长，国内外有非常多基于 Flutter 的成功案例，国内的互联网公司基本都有专门的 Flutter 团队。总之，历时 4 年，Flutter 发展飞快，已在业界得到了广泛的关注和认可，在开发者中受到了热烈的欢迎，成为了移动跨端开发中最受欢迎的框架之一。

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

</details>
<br>

<details><summary>Dart简介</summary>

**Dart是单线程模型，所以也就没有了所谓的主线程/子线程之分。**

Dart也是**Event-Looper**以及**Event-Queue**的模型，所有的事件都是通过**EventLooper**依次执行。  
而Dart的EventLoop就是： 从EventQueue中获取Event， 处理Event， 直到EventQueue为空。

Dart在异步调用中有三个关键词，**async、await、Future**，其中async和await需要一起使用。

在Dart中可以通过async和await进行异步操作，async表示开启一个异步操作，也可以返回一个Future结果。
如果没有返回值，则默认返回一个返回值为null的Future。

async、await本质上就是Dart对异步操作的一个语法糖，可以减少异步调用的嵌套调用，并且由async修饰后返回一个Future，外界可以以链式调用的方式调用。这个语法是JS的ES7标准中推出的，Dart的设计和JS相同。

**Future就是延时操作的一个封装，可以将异步任务封装为Future对象**。获取到Future对象后，最简单的方法就是用await修饰，并等待返回结果继续向下执行。正如上面async、await中讲到的，使用await修饰时需要配合async一起使用。

**isolate是Dart平台对线程的实现方案**，但和普通Thread不同的是，isolate拥有独立的内存，**isolate由线程和独立内存构成**。正是由于isolate线程之间的内存不共享，所以isolate线程之间并不存在资源抢夺的问题，所以也不需要锁。

</details>

## Flutter与Activity通信

- 通过EventChannel来实现，EventChannel仅支持数据单向传递，无返回值，用于数据流（event streams）的通信。  
- 通过MethodChannel来实现，MethodChannel支持数据双向传递，有返回值，用于传递方法调用（method invocation）。  
- 通过BasicMessageChannel来实现，BasicMessageChannel支持数据双向传递，有返回值，可用于传递字符串和半结构化的信息。  
- 通过dart：ffi库调用原生C API。

## Widget、Element、RederObject

Widget 是 Flutter 里对视图的一种结构化描述，你可以把它看作是前端中的“控件”或“组件”。Widget 是控件实现的基本逻辑单位，里面存储的是有关视图渲染的配置信息，包括布局、渲染属性、事件响应信息等。

Element 是 Widget 的一个实例化对象，它承载了视图构建的上下文数据，是连接结构化的配置信息到完成最终渲染的桥梁。  
Flutter 中真正代表屏幕上显示元素的类是Element，Widget 只是描述 Element 的配置数据，并且一个Widget 可以对应多个Element。

RenderObject为应用程序提供真正的渲染。它的主要职责是绘制和布局，是一个真正的渲染对象。

首先，通过 Widget 树生成对应的 Element 树；  
然后，创建相应的 RenderObject 并关联到 Element.renderObject 属性上；  
最后，构建成的 RenderObject 树来完成布局的排列和绘制，以完成最终的渲染。  

## setState刷新原理

调用一个Widget的setState()方法，它会将当前Widget及其所有子Widget都标记要重新刷新的状态，等待下一个Vsync刷新信号到来时重新刷新所有这些标记为刷新状态的控件，也就是调用他们的build方法。

局部刷新的话可以给子部件Widget添加一个GlobalKey，完成跟Element的绑定，拿到子部件Widget的State来进行刷新。


> [Flutter中关于setState的理解(三)](https://www.jianshu.com/p/24018d234210)
> [Flutter中Widget 、Element、RenderObject角色深入分析](https://zhuanlan.zhihu.com/p/183645816)  
> [Flutter渲染之Widget、Element 和 RenderObject](https://www.jianshu.com/p/71bb118517b1)