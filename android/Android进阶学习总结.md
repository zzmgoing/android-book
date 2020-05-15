# Android进阶学习总结

## 一、JVM与DVM

### 1、JVM的内存分配

Java 虚拟机在执行 Java 程序的过程中，会把它所管理的内存划分为不同的数据区域。下面这张图描述了一个 HelloWorld.java 文件被 JVM 加载到内存中的过程：

a. HelloWorld.java 文件首先需要经过编译器编译，生成 HelloWorld.class 字节码文件。  
b. Java 程序中访问HelloWorld这个类时，需要通过 ClassLoader(类加载器)将HelloWorld.class 加载到 JVM 的内存中。  
c. JVM 中的内存可以划分为若干个不同的数据区域，主要分为：**程序计数器、虚拟机栈、本地方法栈、堆、方法区**。  

![jvm](https://img.upyun.zzming.cn/android/jvm_fp.png)

![jvm](https://img.upyun.zzming.cn/android/jvm_ing.png)

**总结来说：**  
JVM 的运行时内存结构中一共有两个“栈”和一个“堆”，分别是：**Java虚拟机栈**和**本地方法栈**，以及“**GC堆**”和**方法区**。
除此之外还有一个**程序计数器**，但是我们开发者几乎不会用到这一部分，所以并不是重点学习内容。 
JVM内存中只有**堆**和**方法区**是线程共享的数据区域，其它区域都是线程私有的。
并且程序计数器是唯一一个在 Java虚拟机规范中没有规定任何OutOfMemoryError情况的区域。

**注意：**  
对于 JVM 运行时内存布局，我们需要始终记住一点：上面介绍的这 5 块内容都是在 Java 虚拟机规范中定义的规则，这些规则只是描述了各个区域是负责做什么事情、存储什么样的数据、如何处理异常、是否允许线程间共享等。千万不要将它们理解为虚拟机的“具体实现”，虚拟机的具体实现有很多，比如 Sun 公司的 HotSpot、JRocket、IBM J9、以及我们非常熟悉的 Android Dalvik 和 ART 等。这些具体实现在符合上面 5 种运行时数据区的前提下，又各自有不同的实现方式。

### 2、GC回收机制和分代回收策略

垃圾回收指的是JVM回收内存中已经没有用的对象（垃圾）。

不同的虚拟机实现有着不同的 GC 实现机制，但是一般情况下每一种 GC 实现都会在以下两种情况下触发垃圾回收。  
1、Allocation Failure：在堆内存中分配时，如果因为可用剩余空间不足导致对象内存分配失败，这时系统会触发一次 GC。  
2、System.gc()：在应用层，Java 开发工程师可以主动调用此 API 来请求一次 GC。

垃圾收集算法：**标记清除算法（Mark and Sweep GC）**、**复制算法（Copying）**、**标记-压缩算法 (Mark-Compact)**。

**JVM分代回收策略**：  

Java 虚拟机根据对象存活的周期不同，把堆内存划分为几块，一般分为**新生代**、**老年代**，这就是 JVM 的内存分代策略。注意: 在**HotSpot**中除了新生代和老年代，还有**永久代**。

分代回收的中心思想就是：对于新创建的对象会在新生代中分配内存，此区域的对象生命周期一般较短。如果经过多次回收仍然存活下来，则将它们转移到老年代中。

**引用**

判断对象是否存活我们是通过GC Roots的引用可达性来判断的。但是JVM中的引用关系并不止一种，而是有四种，根据引用强度的由强到弱，
他们分别是:**强引用(Strong Reference)、软引用(Soft Reference)、弱引用(Weak Reference)、虚引用(Phantom Reference)**。

![yy](https://img.upyun.zzming.cn/android/gc_yy.png)

Android中软引用使用较多，但是不当的使用也会导致异常，比如软引用被强引用持有而频繁回收。

### 3、class类文件结构

![class](https://img.upyun.zzming.cn/android/class_jg.png)

String字符串的长度：  
我们在java代码中声明的String字符串最终在class文件中的存储格式就 CONSTANT_utf8_info。因此一个字符串最大长度也就是u2所能代表的最大值65536个，但是需要使用2个字节来保存 null 值，因此一个字符串的最大长度为 65536 - 2 = 65534。

### 4、编译插桩

顾名思义，所谓编译插桩就是在代码编译期间修改已有的代码或者生成新代码。实际上，我们项目中经常用到的 Dagger、ButterKnife 甚至是 Kotlin 语言，它们都用到了编译插桩的技术。

![bycz](https://img.upyun.zzming.cn/android/class_bycz.png)

1.在 .java 文件编译成 .class 文件时，APT、AndroidAnnotation 等就是在此处触发代码生成。  
2.在 .class 文件进一步优化成 .dex 文件时，也就是直接操作字节码文件，也是本课时主要介绍的内容。这种方式功能更加强大，应用场景也更多。但是门槛比较高，需要对字节码有一定的理解。

一般情况下，我们经常会使用编译插桩实现如下几种功能：  
日志埋点；性能监控；动态权限控制；业务逻辑跳转时，校验是否已经登录；甚至是代码调试等。

插桩工具：

**AspectJ**  
AspectJ 是老牌 AOP（Aspect-Oriented Programming）框架，如果你做过 J2EE 开发可能对这个框架更加熟悉，经常会拿这个框架跟 Spring AOP 进行比较。其主要优势是成熟稳定，使用者也不需要对字节码文件有深入的理解。

**ASM**  
目前另一种编译插桩的方式 ASM 越来越受到广大工程师的喜爱。通过 ASM 可以修改现有的字节码文件，也可以动态生成字节码文件，并且它是一款完全以字节码层面来操纵字节码并分析字节码的框架（此处可以联想一下写汇编代码时的酸爽）。

如何实现在Activity的onCreate()方法中插入代码？

① 编写自定义的Gradle插件  
② 编写自定义的Transform，遍历.class文件，找到activity文件  
- Transform 可以被看作是 Gradle 在编译项目时的一个 task，在 .class 文件转换成 .dex 的流程中会执行这些 task，对所有的 .class 文件（可包括第三方库的 .class）进行转换，转换的逻辑定义在 Transform 的 transform 方法中。实际上平时我们在 build.gradle 中常用的功能都是通过 Transform 实现的，比如混淆（proguard）、分包（multi-dex）、jar 包合并（jarMerge）。  

③ 使用ASM框架，插入字节码到Activity文件中  
④ 将自定义Transform注册到Gradle插件中，部署插件并运行项目

### 5、类加载器ClassLoader

在 Java 程序启动的时候，并不会一次性加载程序中所有的 .class 文件，而是在程序的运行过程中，动态地加载相应的类到内存中。

通常情况下,Java 程序中的 .class 文件会在以下 2 种情况下被 ClassLoader 主动加载到内存中：  
①调用类构造器  
②调用类中的静态（static）变量或者静态方法

JVM 中自带 3 个类加载器：  
①启动类加载器 **BootstrapClassLoader**  
②扩展类加载器 **ExtClassLoader**（JDK 1.9 之后，改名为 PlatformClassLoader）  
③系统加载器 **APPClassLoader**  

**双亲委派模式（Parents Delegation Model）**

既然 JVM 中已经有了这 3 种 ClassLoader，那么 JVM 又是如何知道该使用哪一个类加载器去加载相应的类呢？答案就是：**双亲委派模式**。

所谓双亲委派模式就是，当类加载器收到加载类或资源的请求时，通常都是先委托给父类加载器加载，也就是说，只有当父类加载器找不到指定类或资源时，自身才会执行实际的类加载过程。

**举例说明**  

```Test test = new Test();```  

默认情况下，JVM 首先使用 AppClassLoader 去加载 Test 类。  
①AppClassLoader 将加载的任务委派给它的父类加载器（parent）—ExtClassLoader。  
②ExtClassLoader 的 parent 为 null，所以直接将加载任务委派给 BootstrapClassLoader。  
③BootstrapClassLoader 在 jdk/lib 目录下无法找到 Test 类，因此返回的 Class 为 null。  
④因为 parent 和 BootstrapClassLoader 都没有成功加载 Test 类，所以AppClassLoader会调用自身的 findClass 方法来加载 Test。

**Android 中的 ClassLoader**  

本质上，Android 和传统的 JVM 是一样的，也需要通过 ClassLoader 将目标类加载到内存，类加载器之间也符合双亲委派模型。但是在 Android 中， ClassLoader 的加载细节有略微的差别。

在 Android 虚拟机里是无法直接运行 .class 文件的，Android 会将所有的 .class 文件转换成一个 .dex 文件，并且 Android 将加载 .dex 文件的实现封装在 BaseDexClassLoader 中，而我们一般只使用它的两个子类：**PathClassLoader** 和 **DexClassLoader**。

**PathClassLoader**  

PathClassLoader 用来加载系统 apk 和被安装到手机中的 apk 内的 dex 文件。它的 2 个构造函数如下：

```java
public PathClassLoader(String dexPath,ClassLoader parent){
  super((String)null,(File)null,(String)null,(ClassLoader)null);
}
public PathClassLoader(String dexPath,String librarySearchPath,ClassLoader parent){
  super((String)null,(File)null,(String)null,(ClassLoader)null);
}
```

- dexPath：dex 文件路径，或者包含 dex 文件的 jar 包路径；
- librarySearchPath：C/C++ native 库的路径。

PathClassLoader 里面除了这 2 个构造方法以外就没有其他的代码了，具体的实现都是在 BaseDexClassLoader 里面，其 dexPath 比较受限制，一般是已经安装应用的 apk 文件路径。
当一个 App 被安装到手机后，apk 里面的 class.dex 中的 class 均是通过 PathClassLoader 来加载的。

**DexClassLoader**

很明显，对比 PathClassLoader 只能加载已经安装应用的 dex 或 apk 文件，DexClassLoader 则没有此限制，可以从 SD 卡上加载包含 class.dex 的 .jar 和 .apk 文件，这也是插件化和热修复的基础，在不需要安装应用的情况下，完成需要使用的 dex 的加载。

DexClassLoader 的源码里面只有一个构造方法，代码如下：

```java
public DexClassLoader(String dexPath,String optimizedDirectory,String libraryPath,ClassLoader parent){
  super(dexPath,new File(optimizedDirectory),libraryPath,parent);
}
```

- dexPath：包含 class.dex 的 apk、jar 文件路径 ，多个路径用文件分隔符（默认是“:”）分隔。
- optimizedDirectory：用来缓存优化的 dex 文件的路径，即从 apk 或 jar 文件中提取出来的 dex 文件。该路径不可以为空，且应该是应用私有的，有读写权限的路径。

**使用DexClassLoader实现热修复**

①创建 HotFix patch 包，打成jar包，使用dx工具将jar包中的class文件优化成dex文件  
②将 HotFix patch 拷贝到SD卡中，原项目中使用DexClassLoader来加载需要被修复的类

**总结**

①ClassLoader 就是用来加载 class 文件的，不管是 jar 中还是 dex 中的 class。  
②Java 中的 ClassLoader 通过双亲委托来加载各自指定路径下的 class 文件。  
③可以自定义 ClassLoader，一般覆盖 findClass() 方法，不建议重写 loadClass 方法。  
④Android 中常用的两种 ClassLoader 分别为：PathClassLoader 和 DexClassLoader。  

### 6、class类的加载过程

 .class 文件被加载到内存中所经过的详细过程，主要分 3 大步：**装载**、**链接**、**初始化**。其中链接中又包含验证、准备、解析 3 小步。
 
 ① 装载：指查找字节流，并根据此字节流创建类的过程。装载过程成功的标志就是在方法区中成功创建了类所对应的 Class 对象。
 
 ② 链接：指验证创建的类，并将其解析到 JVM 中使之能够被 JVM 执行。
 
 - 验证：文件格式检验、元数据检验、字节码检验、符号引用检验。
 - 准备：为类中的静态变量分配内存，并为其设置“0值”。基本类型的默认值为 0；引用类型默认值是 null。
 - 解析：把常量池中的符号引用转换为直接引用，也就是具体的内存地址。在这一阶段，JVM 会将常量池中的类、接口名、字段名、方法名等转换为具体的内存地址。
 
 
 ③ 初始化：则是将标记为 static 的字段进行赋值，并且执行 static 标记的代码语句。没有 static 修饰的语句块在实例化对象的时候才会执行。
 
 
 **对象的初始化顺序：**
 
 静态变量/静态代码块 -> 普通代码块 -> 构造函数
 
 ① 父类静态变量和静态代码块；  
 ② 子类静态变量和静态代码块；  
 ③ 父类普通成员变量和普通代码块；  
 ④ 父类的构造函数；  
 ⑤ 子类普通成员变量和普通代码块；  
 ⑥ 子类的构造函数。  
 
 ### 7、Java内存模型与线程
 
 Java 内存模型的来源：主要是因为 CPU 缓存和指令重排等优化会造成多线程程序结果不可控。
 
 Java 内存模型是什么：本质上它就是一套规范，在这套规范中有一条最重要的 **happens-before** 原则。
 
 最后介绍了 Java 内存模型的使用，其中简单介绍了两种方式：**volatile** 和 **synchronized**。其实除了这两种方式，Java 还提供了很多关键字来实现 happens-before 原则，后续课时中将会详细介绍。 
 
 ### 8、Synchronized 和 ReentrantLock
 
 **synchronized**
 
synchronized可以用来修饰以下3个层面：**修饰实例方法**；**修饰静态类方法**；**修饰代码块**。

修饰实例方法:  
锁是当前对象，只有同一个实例对象调用方法才会产生互斥效果，不同实例对象之间不会产生互斥效果。

修饰静态类方法：  
锁是当前类的Class对象，即使在不同线程中调用不同实例对象，也会有互斥效果。

修饰代码块：  
synchronized 作用于代码块时，锁对象就是跟在后面括号中的对象。任何Object对象都可以当作锁对象。

**ReentrantLock**

ReentrantLock 的使用同 synchronized 有点不同，它的加锁和解锁操作都需要手动完成。 lock() 和 unlock()。

默认情况下，synchronized 和 ReentrantLock 都是非公平锁。但是 ReentrantLock 可以通过传入 true 来创建一个公平锁。所谓公平锁就是通过同步队列来实现多个线程按照申请锁的顺序获取锁。

**总结**

这课时我们主要学习了 Java 中两个实现同步的方式 synchronized 和 ReentrantLock。其中 synchronized 使用更简单，加锁和释放锁都是由虚拟机自动完成，而 ReentrantLock 需要开发者手动去完成。但是很显然 ReentrantLock 的使用场景更多，公平锁（**ReentrantLock(true)**）还有读写锁（**ReentrantReadWriteLock**）都可以在复杂场景中发挥重要作用。


