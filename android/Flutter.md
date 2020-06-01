# Flutter

Dart是单线程模型，也就没有了所谓的主线程/子线程之分。

Dart也是Event-Looper以及Event-Queue的模型，所有的事件都是通过EventLooper依次执行。  
而Dart的Event Loop就是： 从EventQueue中获取Event， 处理Event， 直到EventQueue为空。

在异步调用中有三个关键词，**async、await、Future**，其中async和await需要一起使用。**在Dart中可以通过async和await进行异步操作，async表示开启一个异步操作，也可以返回一个Future结果**。如果没有返回值，则默认返回一个返回值为null的Future。  
async、await本质上就是Dart对异步操作的一个语法糖，可以减少异步调用的嵌套调用，并且由async修饰后返回一个Future，外界可以以链式调用的方式调用。这个语法是JS的ES7标准中推出的，Dart的设计和JS相同。  
**Future就是延时操作的一个封装，可以将异步任务封装为Future对象**。获取到Future对象后，最简单的方法就是用await修饰，并等待返回结果继续向下执行。正如上面async、await中讲到的，使用await修饰时需要配合async一起使用。  

**isolate是Dart平台对线程的实现方案**，但和普通Thread不同的是，isolate拥有独立的内存，**isolate由线程和独立内存构成**。正是由于isolate线程之间的内存不共享，所以isolate线程之间并不存在资源抢夺的问题，所以也不需要锁。

### Flutter和Activity之间通信  {docsify-ignore}

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

上述代码中我们创建了MethodChannel实例，并调用 setMethodCallHandler 注册监听回调。从源码中，可以看到MethodChannel构造函数接收两个参数
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

> [Android 集成 Flutter 及通信交互详解](https://blog.csdn.net/u013718120/article/details/86679147)