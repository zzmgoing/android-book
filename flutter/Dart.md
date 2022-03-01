# Dart

[Flutter实战·第二版-Dart语言简介](https://book.flutterchina.club/chapter1/dart.html)

**Dart是单线程模型，所以也就没有了所谓的主线程/子线程之分。**

Dart也是**Event-Looper**以及**Event-Queue**的模型，所有的事件都是通过**EventLooper**依次执行。  
而Dart的EventLoop就是： 从EventQueue中获取Event， 处理Event， 直到EventQueue为空。

Dart在异步调用中有三个关键词，**async、await、Future**，其中async和await需要一起使用。

在Dart中可以通过async和await进行异步操作，async表示开启一个异步操作，也可以返回一个Future结果。
如果没有返回值，则默认返回一个返回值为null的Future。

async、await本质上就是Dart对异步操作的一个语法糖，可以减少异步调用的嵌套调用，并且由async修饰后返回一个Future，外界可以以链式调用的方式调用。这个语法是JS的ES7标准中推出的，Dart的设计和JS相同。

**Future就是延时操作的一个封装，可以将异步任务封装为Future对象**。获取到Future对象后，最简单的方法就是用await修饰，并等待返回结果继续向下执行。正如上面async、await中讲到的，使用await修饰时需要配合async一起使用。

**isolate是Dart平台对线程的实现方案**，但和普通Thread不同的是，isolate拥有独立的内存，**isolate由线程和独立内存构成**。正是由于isolate线程之间的内存不共享，所以isolate线程之间并不存在资源抢夺的问题，所以也不需要锁。