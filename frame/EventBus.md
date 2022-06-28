# EventBus

> EventBus 是一个基于观察者模式的事件订阅/发布框架，利用 EventBus 可以在不同模块之间，实现低耦合的消息通信。

## 流程

- 通过apt在编译期将所有被 @Subscribe注解的函数添加到MyEventBusIndex对象中。
- 在register过程中生成subscriptionsByEventType的数据。
- 在post过程中通过subscriptionsByEventType数据查找对应的函数，然后再通过反射的方式调用。

## 线程切换

```java
@Subscribe(threadMode = ThreadMode.MAIN)
fun onEventTest(event:TestEvent){
    // 处理事件
}
```

- <span class="font-red">POSTING</span>，默认值，那个线程发就是那个线程收。
- <span class="font-red">MAIN</span>，切换至主线程接收事件。
- <span class="font-red">MAIN_ORDERED</span>，v3.1.1 中新增的属性，也是切换至主线程接收事件，但是和 MAIN 有些许区别，后面详细讲。
- <span class="font-red">BACKGROUND</span>，确保在子线程中接收事件。细节就是，如果是主线程发送的消息，会切换到子线程接收，而如果事件本身就是由子线程发出，会直接使用发送事件消息的线程处理消息。
- <span class="font-red">ASYNC</span>，确保在子线程中接收事件，但是和 BACKGROUND 的区别在于，它不会区分发送线程是否是子线程，而是每次都在不同的线程中接收事件。

EventBus 的线程切换，主要涉及的方法就是 EventBus 的 `postToSubscription()` 方法。

- 切换主线程：判断是主线程就直接执行，不是就使用`mainThreadPoster`来处理，其实就是使用主线程looper的Handler来发送消息处理。

1. 为了避免频繁的向主线程 sendMessage()，EventBus 的做法是在一个消息里尽可能多的处理更多的消息事件，所以使用了 while 循环，持续从消息队列 queue 中获取消息。  
2. 同时为了避免长期占有主线程，间隔 10ms （maxMillisInsideHandleMessage = 10ms）会重新发送 sendMessage()，用于让出主线程的执行权，避免造成 UI 卡顿和 ANR。

- 切换子线程：threadMode为<span class="font-red">BACKGROUND</span> 或 <span class="font-red">ASYNC</span>。

1. BACKGROUND，通过 postToSubscription() 中的逻辑可以看到，BACKGROUND 会区分当前发生事件的线程，是否是主线程，非主线程这直接分发事件，如果是主线程，则 backgroundPoster 来分发事件。  
2. BackgroundPoster 也实现了 Poster 接口，其中也维护了一个用链表实现的消息队列 PendingPostQueue。  
3. 在 BackgroundPoster 中，处理主线程抛出的事件时，同一时刻只会存在一个线程，去循环从队列中，获取事件处理事件。  
4. 通过 synchronized 同步锁来保证队列数据的线程安全，同时利用 volatile 标识的 executorRunning 来保证不同线程下看到的执行状态是可见的。

ASYNC 对应的 Poster 是 AsyncPoster，其中并没有做任何特殊的处理，所有的事件，都是无脑的抛给 EventBus 的 executorService 这个线程池去处理，这也就保证了，无论如何发生事件的线程，和接收事件的线程，必然是不同的，也保证了一定会在子线程中处理事件。

BACKGROUND 同一时间，只会利用一个子线程，来循环从事件队列中获取事件并进行处理，也就是前面的事件的执行效率，会影响后续事件的执行。例如你分发了一个事件，使用的是 BACKGROUND 但是队列前面还有一个耗时操作，那你分发的这个事件，也必须等待队列前面的事件都处理完成才可以继续执行。所以如果你追求执行的效率，立刻马上就要执行的事件，可以使用 ASYNC。

### 小结

1. EventBus 可以通过 threadMode 来配置接收事件的线程。  
2. MAIN 和 MAIN_ORDERED 都会在主线程接收事件，区别在于是否区分，发生事件的线程是否是主线程。  
3. BACKGROUND 确保在子线程中接收线程，它会通过线程池，使用一个线程循环处理所有的事件。所以事件的执行时机，会受到事件队列前面的事件处理效率的影响。  
4. ASYNC 确保在子线程中接收事件，区别于 BACKGROUND，ASYNC 会每次向线程池中发送任务，通过线程池的调度去执行。但是因为线程池采用的是无界队列，会导致 ASYNC 待处理的事件太多时，会导致 OOM。

## 粘性事件

普通事件是先注册，后发送。而粘性事件相反，是先发送，后注册。

我们只需要调换一下顺序即可。在发送的时候将事件存储下来，然后在register的时候去检查有没有合适的事件。




> [Android 面试题：EventBus 发送的消息，如何做到线程切换](https://juejin.cn/post/6844903944561377293)