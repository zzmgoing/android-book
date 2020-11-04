# OKHttp

>OkHttp是一个高性能的http请求库，它通过okio来进行流的读取和写入，定义了一些基础的io操作方法。然后将请求和回调封装成request和response，通过拦截器来处理各种请求，并返回结果。  

首先通过OkhttpClient创建Call发起同步或异步请求,okhttp会通过dispatcher对我们所有的realcall进行统一管理，并通过execute()或enqueue()方法对请求进行处理，
这两个方法最终会调用RealCall中getResponseWithInterceptorChain()方法，从拦截器中获取返回结果。

**拦截器的作用：**  
RetryAndFollowUpInterceptor：重试拦截器，处理重定向  
BridgeInterceptor：基础的拦截器（设置请求头Cookie、Connection、Content-Type等，做一些返回的处理，保存Cookie等）  
CacheInterceptor：缓存拦截器（在缓存可用的情况下，读取本地的缓存的数据）  
ConnectInterceptor：连接的拦截器（调用findHealthyConnection()：找到一个连接，首先判断有没有健康的，没有就创建（建立socket，握手连接），连接缓存）  
CallServerInterceptor：（给服务器写数据和读取数据；写头部信息，写body表单信息等等）  

Keep-Alive保活机制：当一个http请求完成后，tcp连接不会立即释放，如果有新的http请求，并且host和上次一样，那么可以复用tcp连接，省去重新连接的过程。  
连接3个核心类（连接复用）  
ConnectionPool：保存了连接，管理连接；  
RealConnection：建立连接的一个对象，维护allocations列表；  
StreamAllocation：连接的一些封装；