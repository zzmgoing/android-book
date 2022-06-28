# OKHttp

> OkHttp是一个高性能的http请求库，它通过okio来进行流的读取和写入，定义了一些基础的io操作方法。然后将请求和回调封装成request和response，通过拦截器来处理各种请求，并返回结果。

首先通过OkhttpClient创建Call发起同步或异步请求，OkHttp会通过dispatcher对我们所有的RealCall进行统一管理，并通过execute()或enqueue()方法对请求进行处理，这两个方法最终会调用`RealCall`中`getResponseWithInterceptorChain()`方法，从拦截器中获取返回结果。

## 拦截器

OkHttp的拦截器是把所有的拦截器放到一个list里，然后每次依次执行拦截器，并且每个拦截器分为三部分：

- 预处理拦截器内容
- 通过`proceed`方法把请求交给下一个拦截器
- 下一个拦截器处理完成并返回，后续处理工作

这样依次下去就形成了一个链式调用，看看源码，具体有哪些拦截器：

```java
Response getResponseWithInterceptorChain() throws IOException {
    // Build a full stack of interceptors.
    List<Interceptor> interceptors = new ArrayList<>();
    interceptors.addAll(client.interceptors());
    interceptors.add(retryAndFollowUpInterceptor);
    interceptors.add(new BridgeInterceptor(client.cookieJar()));
    interceptors.add(new CacheInterceptor(client.internalCache()));
    interceptors.add(new ConnectInterceptor(client));
    if (!forWebSocket) {
      interceptors.addAll(client.networkInterceptors());
    }
    interceptors.add(new CallServerInterceptor(forWebSocket));

    Interceptor.Chain chain = new RealInterceptorChain(
        interceptors, null, null, null, 0, originalRequest);
    return chain.proceed(originalRequest);
  }
```

根据源码可知，一共**七**个拦截器：

|拦截器|描述|作用|
|--|--|--|
|<span class="font-red">addInterceptor</span>|这是由开发者设置的，会按照开发者的要求，在所有的拦截器处理之前进行最早的拦截处理，比如一些公共参数，Header都可以在这里添加|开发者自定义|
|<span class="font-red">RetryAndFollowUpInterceptor</span>|这里会对连接做一些初始化工作，以及请求失败的充实工作，重定向的后续请求工作，跟它的名字一样，就是做重试工作还有一些连接跟踪工作|重试拦截器，处理重定向|
|<span class="font-red">BridgeInterceptor</span>|这里会为用户构建一个能够进行网络访问的请求，同时后续工作将网络请求回来的响应Response转化为用户可用的Response，比如添加文件类型，content-length计算添加，gzip解包等|基础的拦截器（设置请求头Cookie、Connection、Content-Type等，做一些返回的处理，保存Cookie等）|
|<span class="font-red">CacheInterceptor</span>|这里主要是处理cache相关，会根据OkHttpClient对象的配置以及缓存策略对请求值进行缓存，而且如果本地有了可⽤的Cache，就可以在没有网络交互的情况下就返回缓存结果|缓存拦截器（在缓存可用的情况下，读取本地的缓存的数据）|
|<span class="font-red">ConnectInterceptor</span>|这里主要就是负责建立连接了，会建立TCP连接或者TLS连接，以及负责编码解码的HttpCodec|连接的拦截器（调用`findHealthyConnection()`找到一个连接，首先判断有没有健康的，没有就创建（建立socket，握手连接），连接缓存）|
|<span class="font-red">networkInterceptors</span>|这里也是开发者自己设置的，所以本质上和第一个拦截器差不多，但是由于位置不同，所以用处也不同。这个位置添加的拦截器可以看到请求和响应的数据了，所以可以做一些网络调试|开发者自定义|
|<span class="font-red">CallServerInterceptor</span>|这里就是进行网络数据的请求和响应了，也就是实际的网络I/O操作，通过socket读写数据|给服务器写数据和读取数据；写头部信息，写body表单信息等等|

## 线程池

```java
 public synchronized ExecutorService executorService() {
    if (executorService == null) {
      executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
          new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
    }
    return executorService;
 }
```

根据源码可知，Okhttp使用<span class="font-red">SynchronousQueue</span>，当前线程池没有核心线程，使用同步队列，先来先执行，线程空闲后，不会保留，并且适用于短时间的任务操作。

SynchronousQueue每个插入操作必须等待另一个线程的移除操作，同样任何一个移除操作都等待另一个线程的插入操作。因此队列内部其实没有任何一个元素，或者说容量为0，严格说并不是一种容器，由于队列没有容量，因此不能调用peek等操作，因此只有移除元素才有新增的元素，显然这是一种快速传递元素的方式，也就是说在这种情况下元素总是以最快的方式从插入者(生产者)传递给移除者(消费者),这在多任务队列中最快的处理任务方式。对于高频请求场景，无疑是最合适的。

在OKHttp中，创建了一个阀值是Integer.MAX_VALUE的线程池，它不保留任何最小线程，随时创建更多的线程数，而且如果线程空闲后，只能多活60秒。所以也就说如果收到20个并发请求，线程池会创建20个线程，当完成后的60秒后会自动关闭所有20个线程。他这样设计成不设上限的线程，以保证I/O任务中高阻塞低占用的过程，不会长时间卡在阻塞上。


## 设计模式

- <span class="font-red">责任链模式</span>：这个不要太明显，可以说是okhttp的精髓所在了，主要体现就是拦截器的使用，具体代码可以看看上述的拦截器介绍。
- <span class="font-red">建造者模式</span>：在Okhttp中，建造者模式也是用的挺多的，主要用处是将对象的创建与表示相分离，用Builder组装各项配置，比如Request。
- <span class="font-red">工厂模式 </span>：工厂模式和建造者模式类似，区别就在于工厂模式侧重点在于对象的生成过程，而建造者模式主要是侧重对象的各个参数配置。例子有CacheInterceptor拦截器中有个CacheStrategy对象。
- <span class="font-red">单例模式</span>

## Keep-Alive保活机制

> 当一个http请求完成后，tcp连接不会立即释放，如果有新的http请求，并且host和上次一样，那么可以复用tcp连接，省去重新连接的过程。

连接复用的3个核心类：
- **ConnectionPool**：保存了连接，管理连接；
- **RealConnection**：建立连接的一个对象，维护allocations列表；
- **StreamAllocation**：连接的一些封装。

