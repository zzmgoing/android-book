# Handler机制

> Handler是Android Framework架构中的一个基础组件，它实现了一种非阻塞的消息传递机制，在消息转换的过程中，消息的生产者和消费者都不会阻塞。

在多线程的应用场景中，将工作线程中需更新UI的操作信息传递到UI主线程，从而实现工作线程对UI的更新处理，最终实现异步消息的处理。

|角色|作用|
|--|--|
|Message|消息，分为硬件产生的消息（例如：按钮、触摸）和软件产生的消息。|
|MessageQueue|消息队列，主要用来向消息池添加消息和取走消息。|
|Looper|消息循环器，主要用来把消息分发给相应的处理者。|
|Handler|消息处理器，主要向消息队列发送各种消息以及处理各种消息。|

## Handler机制简述

- Handler通过sendMessage()将消息Message发送到消息队列MessageQueue中。  
- Looper通过loop()不断地从MessageQueue中循环读取Message，然后调用Message的target，即附属Handler的dispatchMessage()方法来分发消息。  
- 最终Handler将消息回调到自身的handleMessage()中来处理Message，完成UI操作。

### MessageQueue数据结构

MessageQueue内部持有一个Message对象，采用**单项链表**的形式来维护消息列队。并且提供了入队，出队的基础操作。

### MessageQueue是什么时候创建的

MessageQueue是在Looper的构造函数里面创建的，所以一个线程对应一个Looper，一个Looper对应一个MessageQueue。

### Looper，Handler，MessageQueue的引用关系

一个Handler对象持有一个MessageQueue和它构造时所属线程的Looper引用。也就是说一个Handler必须持有它对应的消息队列和Looper。一个线程可能有多个Handler，但是至多有只能有一个Looper和一个消息队列。

在主线程中new了一个Handler对象后，这个Handler对象自动和主线程生成的Looper以及消息队列关联上了。子线程中拿到主线程中Handler的引用，发送消息后，消息对象就会发送到target属性对应的那个Handler对应的消息队列中去，由对应Looper来处理(子线程msg->主线程handler->主线程messageQueue->主线程Looper->主线程Handler的handlerMessage)。而消息发送到主线程Handler，那么也就是发送到主线程的消息队列，用主线程的Looper轮询。

### 为什么使用Handler

因为Android是单线程模型，只允许在UI线程更新UI，而我们在工作线程中想要更新UI，就需要用到Handler将消息传递到主线程，从而避免线程操作不安全的问题。

### Handler总结

- Handler有且只能绑定一个线程的Looper。  
- Handler的消息是发送给Looper的消息队列MessageQueue，需要等待Looper轮询处理。  
- 如果在子线程中声明了一个Handler，是不能直接更新UI的，需要调用Handler相关的构造方法，传入主线程的Looper才能进行UI的更新操作。  
- 子线程默认是没有开启专属的Looper，所以在子线程中创建Handler之前，必须先调用Looper.prepare()开启Looper然后调用Looper.loop()开启轮询，否则就会异常。  
- 主线程中声明的Handler不需要调用Looper.prepare()和Looper.loop()方法是因为在主线程创建的时候ActivityThread的main方法调用了Looper.prepareMainLooper()方法，并启动了轮询。

## HandlerThread

> HandlerThread可以创建一个带有looper的线程。Looper对象可以用于创建Handler类来进行调度。

- HandlerThread将loop转到子线程中去处理，说白了就是分担MainLooper的工作量，降低了主线程压力，使主界面更流畅。  
- 开启一个线程起到多个线程的作用。处理任务是串行执行，按消息发送顺序进行处理。HandlerThread本质是一个线程，在线程内部，代码是串行处理的。但是由于每一个任务都将以队列的方式逐个被执行到，一旦队列中某个任务执行时间过长，那么就会导致后续的任务都会被延迟处理。HandlerThread拥有自己的消息队列，它不会干扰或阻塞UI线程。  
- 对于网络I/O操作，HandlerThread并不合适，因为它只有一个线程，还得排队一个一个等着。  

### Handler和HandlerThread

- Handler：在Android中负责发送和处理消息，通过它可以实现其他支线线程与主线程之间的消息通讯。  
- Thread：线程，可以看作是进程的一个实体，是CPU调度和分派的基本单位，他是比进程更小的独立运行的基本单位。  
- HandlerThread：封装了Handler和Thread，HandlerThread适合在有需要一个工作线程（非UI线程）+任务的等待队列的形式，优点是不会有堵塞，减少了对性能的消耗，缺点是不能同时进行多个任务的处理，需要等待进行处理。处理效率低，可以当成一个轻量级的线程池来用。

**一个线程只能有一个Looper!**  
Handler在哪个线程创建的，就跟哪个线程的Looper关联，也可以在Handler的构造方法中传入指定的Looper。  

Handler是怎么跟Looper关联上的？  
在Handler中有两个全局变量mLooper和mQueue代表当前Handler关联的Looper和消息队列，并在**构造函数中进行了初始化，重要的就是调用了：Looper.myLooper()方法**。


>[https://blog.csdn.net/wsq_tomato/article/details/80301851](https://blog.csdn.net/wsq_tomato/article/details/80301851)

