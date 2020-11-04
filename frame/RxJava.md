# RxJava

RxJava是一个基于事件流、实现异步操作的库。

RxJava原理：基于一种扩展的观察者模式，整个模式中有4个角色：  

|角色|作用|
|---|---|
|被观察者（Observable）|产生事件|
|观察者（Observer）|接收事件，并给出响应动作|	
|订阅（Subscribe）|连接 被观察者 & 观察者|
|事件（Event）|被观察者 & 观察者 沟通的载体|

被观察者 （Observable） 通过 订阅（Subscribe） 按顺序发送事件 给观察者 （Observer）  
观察者（Observer） 按顺序接收事件 & 作出对应的响应动作

**线程切换原理**：

```
...
.subscribeOn(Schedulers.newThread())
.subscribeOn(Schedulers.io())
.observeOn(AndroidSchedulers.mainThread())
```

subscribeOn()决定最原始数据源发射数据代码运行的线程，而接下来的发射数据代码是由observeOn决定的。  
observeOn()决定下游代码运行的线程，不定义的话是默认使用当前运行的线程。  

1、如果设置了observeOn(指定线程)，那么Observer（观察者）中的onNext()、onComplete()等方法将会运行在这个指定线程中去。subscribeOn()设置的线程不会影响到observeOn()。

2、如果设置了subscribeOn(指定线程)，那么Observable（被观察者）中subscribe()方法将会运行在这个指定线程中去。

RxJava分配线程时是通过调用链自下而上处理，所以最上面的subscribeOn()会覆盖下面的定义，也就是说subscribeOn()在调用链中最多定义一次就可以，位置任意。

**底层线程实现原理**：  

在IoScheduler中创建和缓存一组线程池并在可以重用时使用它们，主要是通过CachedWorkerPool将ThreadWorker用队列缓存起来，ThreadWorker继承NewThreadWorker，NewThreadWorker通过Executors来创建线程池。
