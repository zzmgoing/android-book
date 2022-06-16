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

MessageQueue内部持有一个Message对象，采用<span class="font-red">单项链表</span>的形式来维护消息列队。并且提供了入队，出队的基础操作。

### MessageQueue是什么时候创建的

MessageQueue是在Looper的构造函数里面创建的，所以一个线程对应一个Looper，一个Looper对应一个MessageQueue。

### Looper，Handler，MessageQueue的引用关系

一个Handler对象持有一个MessageQueue和它构造时所属线程的Looper引用。也就是说一个Handler必须持有它对应的消息队列和Looper。
<span class="font-red">一个线程可能有多个Handler，但是至多有只能有一个Looper和一个消息队列。</span>

<span class="font-gray">在主线程中new了一个Handler对象后，这个Handler对象自动和主线程生成的Looper以及消息队列关联上了。子线程中拿到主线程中Handler的引用，发送消息后，消息对象就会发送到target属性对应的那个Handler对应的消息队列中去，由对应Looper来处理(子线程msg->主线程handler->主线程messageQueue->主线程Looper->主线程Handler的handlerMessage)。而消息发送到主线程Handler，那么也就是发送到主线程的消息队列，用主线程的Looper轮询。</span>

### 为什么使用Handler

因为Android是单线程模型，只允许在UI线程更新UI，而我们在工作线程中想要更新UI，就需要用到Handler将消息传递到主线程，从而避免线程操作不安全的问题。

### Handler总结

- Handler有且只能绑定一个线程的Looper。
- Handler的消息是发送给Looper的消息队列MessageQueue，需要等待Looper轮询处理。
- 如果在子线程中声明了一个Handler，是不能直接更新UI的，需要调用Handler相关的构造方法，传入主线程的Looper才能进行UI的更新操作。
- 子线程默认是没有开启专属的Looper，所以在子线程中创建Handler之前，必须先调用Looper.prepare()开启Looper然后调用Looper.loop()开启轮询，否则就会异常。
- 主线程中声明的Handler不需要调用Looper.prepare()和Looper.loop()方法是因为在主线程创建的时候ActivityThread的main方法调用了Looper.prepareMainLooper()方法，并启动了轮询。

## Handler异步消息和同步屏障

Handler有一个mAsynchronous（非标准读音：A森磕儿那思）的属性，在构造函数中接收一个async参数来表示是否是异步，我们一般用的默认构造方法都是false，所以通过send方法发送的都是同步消息，发出后都会在消息队列里面排队等待处理。
Android系统每隔16ms会刷新一次屏幕，如果主线程的消息过多在16ms内没有执行完就会造成卡顿或者掉帧，这个时候我们就需要使用异步消息。

MessageQueue通过<span class="font-red">消息屏障</span>让异步消息不用通过排队等候处理，这个消息屏障可以理解为一堵墙，把同步消息队列拦住，
先处理异步消息，异步消息处理完了就会取消这堵墙，然后继续处理同步消息。

消息屏障体现在MessageQueue里面的postSyncBarrier方法，创建了一个没有target属性的message，MessageQueue的next方法在处理异步消息时有一个判断，如果<span class="font-red">message的target为空</span>就是屏障消息，然后while循环直到取出异步消息终止，接下来的处理就跟同步消息一样。

**怎么发送异步消息？**

- 在Handler的构造方法里面async参数设置为true，发送的就都是异步消息。
- Message有一个setAsynchronous(true)方法可以设置为异步消息。

## Handler何如处理延迟消息

Handler 延时消息机制不是延时发送消息，而是<span class="font-red">延时去处理消息</span>，比如将消息插入消息队列后等3秒后再去处理。

<details><summary>消息入列：enqueueMessage(Message msg, long when)</summary>

```java
boolean enqueueMessage(Message msg, long when) {
    if (msg.target == null) {
        throw new IllegalArgumentException("Message must have a target.");
    }
    if (msg.isInUse()) {
        throw new IllegalStateException(msg + " This message is already in use.");
    }
    synchronized (this) {
        // 判断发送消息的进程是否还活着
        if (mQuitting) {
            IllegalStateException e = new IllegalStateException(
                    msg.target + " sending message to a Handler on a dead thread");
            Log.w(TAG, e.getMessage(), e);
            msg.recycle(); // 回收消息到消息池
            return false;
        }
        msg.markInUse(); // 标记消息正在使用
        msg.when = when; 
        Message p = mMessages; // 获取表头消息
        boolean needWake;
        // 如果队列中没有消息 或者 消息为即时消息 或者 表头消息时间大于当前消息的延时时间
        if (p == null || when == 0 || when < p.when) {
            // New head, wake up the event queue if blocked.
            msg.next = p;
            mMessages = msg;
            // 表示要唤醒 Hander 对应的线程，这个后面解释
            needWake = mBlocked;
        } else {
            needWake = mBlocked && p.target == null && msg.isAsynchronous();
            Message prev;
            // 如下都是单链表尾插法，很简单，不赘述
            for (;;) {
                prev = p;
                p = p.next;
                if (p == null || when < p.when) {
                    break;
                }
                if (needWake && p.isAsynchronous()) {
                    needWake = false;
                }
            }
            msg.next = p; // invariant: p == prev.next
            prev.next = msg;
        }
        // 唤醒Handler对应的线程
        if (needWake) {
            nativeWake(mPtr);
        }
    }
    return true;
}
```

</details>


如果消息队列是空的，就会把延迟消息放到队列当中。

<details><summary>Message.next()</summary>

```java
 Message next() {
     // Return here if the message loop has already quit and been disposed.
     // This can happen if the application tries to restart a looper after quit
     // which is not supported.
     final long ptr = mPtr;
     if (ptr == 0) {
         return null;
     }
     int pendingIdleHandlerCount = -1; // -1 only during first iteration
     int nextPollTimeoutMillis = 0;
     for (;;) {
         if (nextPollTimeoutMillis != 0) {
             Binder.flushPendingCommands();
         }
         // 表示要休眠多长时间，功能类似于wait(time)
         // -1表示一直休眠,
         // 等于0时，不堵塞
         // 当有新的消息来时，如果handler对应的线程是阻塞的，那么会唤醒
         nativePollOnce(ptr, nextPollTimeoutMillis);
         synchronized (this) {
             // Try to retrieve the next message.  Return if found.
             final long now = SystemClock.uptimeMillis();
             Message prevMsg = null;
             Message msg = mMessages;
             if (msg != null && msg.target == null) {
                 // Stalled by a barrier.  Find the next asynchronous message in the queue.
                 do {
                     prevMsg = msg;
                     msg = msg.next;
                 } while (msg != null && !msg.isAsynchronous());
             }
             if (msg != null) {
                 if (now < msg.when) {
                     // 计算延时消息的剩余时间
                     nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
                 } else {
                     // Got a message.
                     mBlocked = false;
                     if (prevMsg != null) {
                         prevMsg.next = msg.next;
                     } else {
                         mMessages = msg.next;
                     }
                     msg.next = null;
                     if (DEBUG) Log.v(TAG, "Returning message: " + msg);
                     msg.markInUse();
                     return msg;
                 }
             } else {
                 // No more messages.
                 nextPollTimeoutMillis = -1;
             }
           
             .......
             // 判断是否有 idle 任务，即主线程空闲时需要执行的任务，这个下面说
     
             if (pendingIdleHandlerCount <= 0) {
                 // 这里表示所有到时间的消息都执行完了，剩下的如果有消息一定是延时且时间还没到的消息； 
                 // 刚上面的 enqueueMessage 就是根据这个变量来判断是否要唤醒handler对应的线程
                 mBlocked = true; 
                 continue;
             }       
         
        ......
     }
 }
```

</details>

从消息队列中获取消息是通过 Looper.loop() 来调用 MessageQueue 的 next()方法。
- 首次进入next方法，nativePollOnce(long ptr, int timeoutMillis)从MessageQueue中获取表头Message；
- 获取Message的执行时间与当前时间进行判断，计算表头Message是否需要延迟，延迟时间为nextPollTimeoutMillis；
- 若当前时间小于Message执行时间，即now < msg.when，那么立即返回当前Message交由Handler处理；
- 需要延迟，则判断是否存在IdelHandler，不存在则进入下一个循环，继续执行nativePollOnce方法；
- 此时nativePollOnce(long ptr, int timeoutMillis)的入参nextPollTimeoutMillis即为需要延迟的时间，等待延迟时间后在触发获取Message；

**总结**：next()中如果当前链表头部的Message是延迟消息，则根据延迟时间进行消息队列阻塞，不返回给Looper Message，并设置定时唤醒，唤醒后，返回Message给Looper处理。

**nativePollOnce获取的是链表表头信息，如何保证获取的消息按照顺序执行？**

当调用enqueueMessage(Message msg, long when)方法时，MessageQueue会根据Message的执行时间msg.when进行排序，链表头的延迟时间小，尾部延迟时间最大。

**如果在延时唤醒的过程中，又来了一个立即执行的message又该如何呢？**

立即执行的消息同样也会先入链表，然后唤醒线程获取表头message，看是否到了执行时间。由于立即执行的消息其实是一个延时为0的message，在一个延迟的链表中，必然会放入表头，而且是无延迟的，所以会立即取出返回给loop去执行了，loop处理完消息，继续来拿表头的message。

**当整个链表都是延迟执行的message时，如果此时插入的message也是延时执行的，是否一定要唤醒呢?**

如果插入的message并非插入表头，说明拿的下一个message也不是自己，完全可以让线程继续休眠，没有必要唤醒，因为此时的定时器到期唤醒后拿到的正是待返回和执行的表头message。

**总结**：只有当表头来了新消息，才会唤醒Loop来获取，Message要么立即执行，要么Loop刷新自我唤醒的定时器继续睡眠。

## Looper怎么保证在线程里唯一

Looper的构造函数是私有的，我们在线程里面创建Looper需要调用Looper.prepare()方法，如果Looper已经创建了，再次调用会直接抛出异常。
```java
public static void prepare() {
        prepare(true);
    }

private static void prepare(boolean quitAllowed) {
    if (sThreadLocal.get() != null) {
        throw new RuntimeException("Only one Looper may be created per thread");
    }
    sThreadLocal.set(new Looper(quitAllowed));
}
```
1、Looper对象创建完成后会存放在ThreadLocal中。

2、ThreadLocal 提供了针对于单独的线程的局部变量，并能够使用 set()、get() 方法对这些变量进行设置和获取，并且能够保证这些变量与其他线程相隔离。

3、通过get()方法获取到一个ThreadLocalMap对象，如果没有则进行初始化，以ThreadLocal自身为key，存入的对象是value的ThreadLocalMap对象，然后把ThreadLocalMap存到线程Thread中。

4、回到prepare()方法中，如果用过get()方法能直接拿到对象，说明已经存入过了，Looper已经存在，所以直接抛出异常。

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


> [https://blog.csdn.net/wsq_tomato/article/details/80301851](https://blog.csdn.net/wsq_tomato/article/details/80301851)
> [https://blog.csdn.net/ly502541243/article/details/109091386](https://blog.csdn.net/ly502541243/article/details/109091386)
