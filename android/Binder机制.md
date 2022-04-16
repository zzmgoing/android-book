# Binder机制

**Binder是什么**  
1、Binder是基于开源的OpenBinder实现的一种进程间通信(IPC)框架。  
2、Binder的整个设计是C/S结构，客户端进程获取服务端进程的代理，然后通过这个代理的接口方法来读写数据，从而完成进程间的数据通信。  
比如在Android中，ActivityManagerService，LocationManagerService等系统服务在framework层都是在单独进程中的，它们使用binder和应用进行通信。
Android应用和系统services运行在不同进程中是为了安全，稳定，以及内存管理的原因，同时应用和系统服务需要通信和分享数据。

**Android使用Binder的原因(Binder的优势)**  
1、安全，每个进程都会被Android系统分配UID和PID，不像传统的在数据里加入UID，这就让那些恶意进程无法直接和其他进程通信，进程间通信的安全性得到提升。  
2、高效，像Socket之类的IPC每次数据拷贝都需要2次，而Binder只要1次，在手机这种资源紧张的情况下很重要。  
3、稳定，基于C/S架构，职责明确、架构清晰，因此稳定性好。

| 优势	 | 描述 |
| --- | --- |
| 性能 | 只需要一次数据拷贝，性能上仅次于共享内存| 
| 稳定性 | 基于 C/S 架构，职责明确、架构清晰，因此稳定性好| 
| 安全性 | 为每个 APP 分配 UID，进程的 UID 是鉴别进程身份的重要标志| 

**内存映射（mmap）**  
是一种内存映射文件的方法，即将一个文件或者其他对象映射到进程的地址空间，实现文件磁盘地址和应用程序进程虚拟地址空间中一段虚拟地址的一一映射关系。映射关系建立后，用户对这块内存区域的修改可以直接反应到内核空间，反之亦然。内存映射能减少数据拷贝次数，实现用户空间和内核空间的高效互动。

**Binder的通信原理**  
![binder的通信原理](https://img.upyun.zzming.cn/android/binder_yuanli.png)  
Binder是基于内存映射来实现的，在前面我们知道内存映射通常是用在有物理介质的文件系统上的，Binder没有物理介质，它使用内存映射是为了跨进程传递数据。Binder通信的步骤如下所示：  
1、Binder驱动在内核空间创建一个数据接收缓存区。  
2、在内核空间开辟一块内核缓存区，建立内核缓存区和数据接收缓存区之间的映射关系，以及数据接收缓存区和接收进程用户空间地址的映射关系。  
3、发送方进程通过copy_from_user()函数将数据拷贝 到内核中的内核缓存区，由于内核缓存区和接收进程的用户空间存在内存映射，因此也就相当于把数据发送到了接收进程的用户空间，这样便完成了一次进程间的通信。

**Binder的设计**  
Binder驱动程序实现在内核空间中，开发者在用户空间实现自己的client和server。  
Binder的整体设计总共有四层：  
1、Java层AIDL  
2、Framework层：Android.os.Binder  
3、Native层：libBinder.cpp  
4、内核层。内核层的通信都是通过ioctl来进行的，client打开一个ioctl,进入到轮询队列，一直阻塞直到时间到或者有消息。  
<span>Binder中使用的设计模式为代理模式。</span>

**Binder的运行机制**  
Binder基于Client-Server通信模式，一共有四个角色：  
1、Client进程：使用服务的进程。  
2、Server进程：提供服务的进程。  
3、ServiceManager进程：ServiceManager的作用是将字符形式的Binder名字转化成Client中对该Binder的引用，使得Client能够通过Binder名字获得对Server中Binder实体的引用。  
4、Binder驱动：驱动负责进程之间Binder通信的建立，Binder在进程之间的传递，Binder引用计数管理，数据包在进程之间的传递和交互等一系列底层支持。  

![binder的运行机制](https://img.upyun.zzming.cn/android/binder_yunxing.png)

**使用服务的具体执行过程**

![binder的执行过程](https://img.upyun.zzming.cn/android/binder_zhixing.png)
 
1、Client通过获得一个Server的代理接口，对Server进行调用。  
2、代理接口中定义的方法与Server中定义的方法是一一对应的。  
3、Client调用某个代理接口中的方法时，代理接口的方法会将Client传递的参数打包成Parcel对象。  
4、代理接口将Parcel发送给内核中的Binder Driver。  
5、Server会读取Binder Driver中的请求数据，如果是发送给自己的，解包Parcel对象，处理并将结果返回。  
6、整个的调用过程是一个同步过程，在Server处理的时候，Client会Block住。因此Client调用过程不应在主线程。  

**进程间通信方式**  
1、Files文件系统（包括内存映射）  
2、Sockets  
3、Pipes管道  
4、共享内存  
5、Intents, ContentProviders, Messenger  
6、Binder  