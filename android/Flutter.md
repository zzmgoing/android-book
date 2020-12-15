# Flutter

**Dart是单线程模型，所以也就没有了所谓的主线程/子线程之分。**

Dart也是**Event-Looper**以及**Event-Queue**的模型，所有的事件都是通过**EventLooper**依次执行。  
而Dart的Event Loop就是： 从EventQueue中获取Event， 处理Event， 直到EventQueue为空。

在异步调用中有三个关键词，**async、await、Future**，其中async和await需要一起使用。  
**在Dart中可以通过async和await进行异步操作，async表示开启一个异步操作，也可以返回一个Future结果。**  
如果没有返回值，则默认返回一个返回值为null的Future。  

async、await本质上就是Dart对异步操作的一个语法糖，可以减少异步调用的嵌套调用，并且由async修饰后返回一个Future，外界可以以链式调用的方式调用。这个语法是JS的ES7标准中推出的，Dart的设计和JS相同。  

**Future就是延时操作的一个封装，可以将异步任务封装为Future对象**。获取到Future对象后，最简单的方法就是用await修饰，并等待返回结果继续向下执行。正如上面async、await中讲到的，使用await修饰时需要配合async一起使用。  

**isolate是Dart平台对线程的实现方案**，但和普通Thread不同的是，isolate拥有独立的内存，**isolate由线程和独立内存构成**。正是由于isolate线程之间的内存不共享，所以isolate线程之间并不存在资源抢夺的问题，所以也不需要锁。

### Flutter和Activity之间通信  {docsify-ignore}

由于在初始化flutter页面时会传递一个字符串——route，因此我们就可以拿route来做文章，传递自己想要传递的数据。该种方式仅支持单向数据传递且数据类型只能为字符串，无返回值。  
通过EventChannel来实现，EventChannel仅支持数据单向传递，无返回值，用于数据流（event streams）的通信。  
通过MethodChannel来实现，MethodChannel支持数据双向传递，有返回值，用于传递方法调用（method invocation）。  
通过BasicMessageChannel来实现，BasicMessageChannel支持数据双向传递，有返回值，可用于传递字符串和半结构化的信息。  
通过dart：ffi库调用原生C API。


**1、MethodChannel**

Flutter端向Native端发送通知：

Native端：  
```java
new MethodChannel(getFlutterView(), "com.xxx").setMethodCallHandler(new MethodChannel.MethodCallHandler() {
    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        // methodCall.method 对应 Flutter端invokeMethod方法的第一个参数
        if(methodCall.method.equals("123")) {
            // 获取Flutter传递的参数
            String msg = methodCall.<String>argument("msg");
            // 回传给Flutter
            result.success(msg);
        }
    }
});
```

上述代码中我们创建了**MethodChannel**实例，并调用**setMethodCallHandler**注册监听回调。从源码中，可以看到MethodChannel构造函数接收两个参数
```java
public MethodChannel(BinaryMessenger messenger, String name) {
     this(messenger, name, StandardMethodCodec.INSTANCE);
}
 
public MethodChannel(BinaryMessenger messenger, String name, MethodCodec codec) {
 ......
}
```
name 就是双发通信的唯一标识，我们可以简单理解为钥匙即可。

MethodCodec有两种实现：

1、JSONMethodCodec  
JSONMethodCodec的编解码依赖于JSONMessageCodec，当其在编码MethodCall时，会先将MethodCall转化为字典{"method":method,"args":args}。其在编码调用结果时，会将其转化为一个数组，调用成功为[result]，调用失败为[code,message,detail]。再使用JSONMessageCodec将字典或数组转化为二进制数据。

2、StandardMethodCodec  
MethodCodec的默认实现，StandardMethodCodec的编解码依赖于StandardMessageCodec，当其编码MethodCall时，会将method和args依次使用StandardMessageCodec编码，写入二进制数据容器。其在编码方法的调用结果时，若调用成功，会先向二进制数据容器写入数值0（代表调用成功），再写入StandardMessageCodec编码后的result。而调用失败，则先向容器写入数据1（代表调用失败），再依次写入StandardMessageCodec编码后的code，message和detail。

Flutter端：  
```dart
import 'package:flutter/services.dart';
 
static const methodPlugin = const MethodChannel('com.xxx');
String callbackResult = await methodPlugin.invokeMethod('123', { "msg": "456" });
```
在Flutter同样需要创建MethodChannel实例，并将通信钥匙作为参数传入，要与原生端保持一致。然后调用invokeMethod方法向原生端发送通信请求。第一个参数表示要调用原生端的哪个方法，第二个参数为可选参数，即传递给Native端的数据参数。

**2、EventChannel**

Native端向Flutter端发送通知：

Native端：
```java
new EventChannel(getFlutterView(), "com.xxx").setStreamHandler(new EventChannel.StreamHandler() {
 
    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        eventSink.success("msg");
    }
 
    @Override
    public void onCancel(Object o) {
        // 做一些注销操作
    }
});
```
和 MethodChannel 类似，EventChannel 也是通过 new 创建对象实例，并设置 StreamHandler 类型的监听回调。其中 onCancel 代表对面不再接收，这里我们可以做注销的逻辑操作。onListen 代表通信已经建立完毕，Native可以向Flutter发送数据。onListen 方法中携带了 EventSink 参数，后续Native发送数据都是经过 EventSink 的 success、error 方法。

Flutter端:
```dart
import 'package:flutter/services.dart';
 
static const eventPlugin = const EventChannel('com.xxx');
 
@override
void initState() {
  super.initState();
  _streamSubscription = eventPlugin.receiveBroadcastStream()
      .listen(_onData, onError: _onError, onDone: _onDone, cancelOnError: true);
}
 
void _onData(Object event) {
  // 接收数据
  setState(() {
    eventVal = event;
  });
}
 
void _onError(Object error) {
  // 发生错误时被回调
  setState((){
    eventVal = "错误";
  });
}
 
void _onDone() {
  //结束时调用
}
 
@override
void dispose() {
  super.dispose();
  if(_streamSubscription != null) {
    _streamSubscription.cancel();
  }
}
```
同样与 MethodChannel 类似，首先是创建 EventChannel 实例，然后在 initState 生命周期中调用 receiveBroadcastStream方法的listen。listen 返回的是 StreamSubscription 对象。此处有点类似Android中的BroadcastReceiver广播。listen方法源码如下：

可以看到，onData 为必需参数，onError、onDone、cancelOnError 为可选。顾名思义，onData 即为收到原生端发送数据的回调，onError为接收数据失败，onDone为接收数据结束，cancelOnError是一个bool类型参数，标识在发生错误时，时候自动取消通信。以上即可实现Native端向Flutter发送通知。

**Flutter如何创建Activity**

创建一个Activity继承FlutterFragmentActivity，然后通过Flutter.createView()创建一个view，将它通过addContentView()添加到Activity中。

```kotlin
package com.ywy.androidwithflutter

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import io.flutter.app.FlutterFragmentActivity
import io.flutter.facade.Flutter

class MainFlutterActivity : FlutterFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_flutter)
        val mFlutterView: View = Flutter.createView(this, lifecycle, "main_flutter")
        val mParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT)
        addContentView(mFlutterView, mParams)
    }
}
```

**Flutter实现原理**

Flutter的Framework层是使用Dart语言实现的SDK，它实现了一套基础库， 用于处理动画、绘图和手势。并且基于绘图封装了一套 UI 组件库，然后根据 Material 和 Cupertino 两种视觉风格区分开来。这个纯 Dart 实现的 SDK 被封装为了一个叫作 dart:ui 的 Dart 库。我们在使用 Flutter 写 App 的时候，直接导入这个库即可使用组件等功能。

Flutter的引擎Engine层使用C++语言编写。其中囊括了 Skia 引擎、Dart 运行时、文字排版引擎等。

Flutter开辟了新的设计理念，实现了真正的跨平台的方案，自研 UI 框架，它的渲染引擎是 Skia 图形库来实现的，而开发语言选择了同时支持 JIT 和 AOT 的 Dart。不仅保证了开发效率，同时也提升了执行效率。由于 Flutter 自绘 UI 的实现方式，因此也尽可能的减少了不同平台之间的差异。也保持和原生应用一样的高性能。因此，Flutter 也是跨平台开发方案中最灵活和彻底的那个，它重写了底层渲染逻辑和上层开发语言的一整套完整解决方案。

**Flutter和React Native的区别**

**实现原理**  
RN：利用JS来做桥接，将JS调用转为本地代码调用，依赖于客户端渲染，底层代码会调用不同平台原生代码，会存在一套代码在不同平台展示出不同的样式问题，差异比较大。一些API、属性只支持某个特定的平台，经常在代码判断平台使用不用的API、属性等，兼容性比较差。  
Flutter：自己实现了一套UI框架，在底层基于 Skia 自己绘制的图形界面，兼容性高，一个页面是一个整体，效率也高。而Dart是AOT编译的，可编译成快速、可预测的本地代码。 

**代码调试**  
RN：因为RN代码是JS语言，所以需要在浏览器中调试。  
Flutter：可以在编辑器中直接下断点调试，相对于RN，Flutter这种调试方式比较方便，更符合原生调试习惯。

**热更新**  
RN：支持。  
Flutter：暂不支持。

**Widget、Element、RederObject的区别**

Widget 是 Flutter 里对视图的一种结构化描述，你可以把它看作是前端中的“控件”或“组件”。Widget 是控件实现的基本逻辑单位，里面存储的是有关视图渲染的配置信息，包括布局、渲染属性、事件响应信息等。

Element 是 Widget 的一个实例化对象，它承载了视图构建的上下文数据，是连接结构化的配置信息到完成最终渲染的桥梁。  
<span>Flutter 中真正代表屏幕上显示元素的类是Element，Widget 只是描述 Element 的配置数据，并且一个Widget 可以对应多个Element。</span>

RenderObject为应用程序提供真正的渲染。它的主要职责是绘制和布局，是一个真正的渲染对象。

首先，通过 Widget 树生成对应的 Element 树；  
然后，创建相应的 RenderObject 并关联到 Element.renderObject 属性上；  
最后，构建成 RenderObject 树来完成布局的排列和绘制，以完成最终的渲染。  

> [Android 集成 Flutter 及通信交互详解](https://blog.csdn.net/u013718120/article/details/86679147)