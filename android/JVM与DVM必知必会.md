# JVM与DVM必知必会

注：本篇《Android进阶》来自拉勾教育[Android 工程师进阶 34 讲](https://kaiwu.lagou.com/course/courseInfo.htm?courseId=67#/detail/pc?id=1855)  
知识来自书中部分总结，如有想细读的同学可以购买该书。

## 1、JVM的内存分配

Java 虚拟机在执行 Java 程序的过程中，会把它所管理的内存划分为不同的数据区域。下面这张图描述了一个 HelloWorld.java 文件被 JVM 加载到内存中的过程：  
a. HelloWorld.java 文件首先需要经过编译器编译，生成 HelloWorld.class 字节码文件。  
b. Java 程序中访问HelloWorld这个类时，需要通过 ClassLoader(类加载器)将HelloWorld.class 加载到 JVM 的内存中。  
c. JVM 中的内存可以划分为若干个不同的数据区域，主要分为：**程序计数器、虚拟机栈、本地方法栈、堆、方法区**。  

![jvm](../image/jvm_fp.webp)

**1、程序计数器**  
>程序计数器是虚拟机中一块较小的内存空间，主要用于记录当前线程执行的位置。

Java 程序是多线程的，CPU 可以在多个线程中分配执行时间片段。当某一个线程被 CPU 挂起时，需要记录代码已经执行到的位置，方便 CPU 重新执行此线程时，知道从哪行指令开始执行。这就是程序计数器的作用（分支操作、循环操作、跳转、异常处理）。

**注意：**  
a、在Java虚拟机规范中，对程序计数器这一区域没有规定任何OutOfMemoryError情况。  
b、线程私有的，每条线程内部都有一个私有程序计数器。它的生命周期随着线程的创建而创建，随着线程的结束而死亡。  
c、当一个线程正在执行一个Java方法的时候，这个计数器记录的是正在执行的虚拟机字节码指令的地址。如果正在执行的是Native方法，这个计数器值则为空（Undefined）。  

**2、虚拟机栈**
>虚拟机栈也是线程私有的，与线程的生命周期同步。

虚拟机栈的初衷是用来描述Java方法执行的内存模型，每个方法被执行的时候，JVM都会在虚拟机栈中创建一个栈帧。  
**栈帧（Stack Frame）是用于支持虚拟机进行方法调用和方法执行的数据结构，每一个线程在执行某个方法时，都会为这个方法创建一个栈帧。**  
一个线程包含多个栈帧，而每个栈帧内部包含**局部变量表、操作数栈、动态连接、返回地址**等。  

**局部变量表**是变量值的存储空间，我们调用方法时传递的参数，以及在方法内部创建的局部变量都保存在局部变量表中。  
**操作数栈**（Operand Stack）也常称为操作栈，它是一个后入先出栈（LIFO）。当一个方法刚刚开始执行的时候，这个方法的操作数栈是空的。在方法执行的过程中，会有各种字节码指令被压入和弹出操作数栈。  
**动态链接**的主要目的是为了支持方法调用过程中的动态连接（Dynamic Linking）。在一个 class 文件中，一个方法要调用其他方法，需要将这些方法的符号引用转化为其所在内存地址中的直接引用，而符号引用存在于方法区中。  
**返回地址**作用是无论当前方法采用何种方式退出（正常退出/异常退出），在方法退出后都需要返回到方法被调用的位置，程序才能继续执行。而虚拟机栈中的“返回地址”就是用来帮助当前方法恢复它的上层方法执行状态。

**3、本地方法栈**  

本地方法栈和上面介绍的虚拟栈基本相同，只不过是针对本地（native）方法。在开发中如果涉及 JNI 可能接触本地方法栈多一些，在有些虚拟机的实现中已经将两个合二为一了（比如HotSpot）。

**4、堆**  
>用于存放对象实例。

Java 堆（Heap）是 JVM 所管理的内存中最大的一块，该区域唯一目的就是存放对象实例，几乎所有对象的实例都在堆里面分配，因此它也是 Java 垃圾收集器（GC）管理的主要区域，有时候也叫作“GC 堆”（关于堆的 GC 回收机制将会在后续课时中做详细介绍）。同时它也是**所有线程共享的内存区域**，因此被分配在此区域的对象如果被多个线程访问的话，需要考虑**线程安全**问题。

按照**对象存储时间**的不同，堆中的内存可以划分为**新生代**（Young）和**老年代**（Old），其中新生代又被划分为 Eden 和 Survivor 区。不同的区域存放具有不同生命周期的对象。这样可以根据不同的区域使用不同的垃圾回收算法，从而更具有针对性，进而提高垃圾回收效率。

**5、方法区**

方法区（Method Area）也是 JVM 规范里规定的一块**运行时数据区**。方法区主要是存储已经被**JVM 加载的类信息（版本、字段、方法、接口）、常量、静态变量、即时编译器编译后的代码和数据**。该区域同堆一样，也是被各个线程共享的内存区域。

![jvm](../image/jvm_ing.webp)

**异常**  
**StackOverflowError** 栈溢出异常  
递归调用是造成StackOverflowError的一个常见场景。原因就是每调用一次method方法时，都会在虚拟机栈中创建出一个栈帧。因为是递归调用，method方法并不会退出，也不会将栈帧销毁，所以必然会导致StackOverflowError。因此当需要使用递归时，需要格外谨慎。

**OutOfMemoryError** 内存溢出异常  
理论上，虚拟机栈、堆、方法区都有发生OutOfMemoryError的可能。但是实际项目中，大多发生于堆当中。比如在一个无限循环中，动态的向ArrayList中添加新的HeapError对象。这会不断的占用堆中的内存，当堆内存不够时，必然会产生OutOfMemoryError，也就是内存溢出异常。

**总结**来说，JVM 的运行时内存结构中一共有**两个栈和一个堆，分别是：Java 虚拟机栈和本地方法栈，以及GC堆和方法区**。除此之外还有一个**程序计数器**，但是我们开发者几乎不会用到这一部分，所以并不是重点学习内容。 JVM 内存中只有堆和方法区是线程共享的数据区域，其它区域都是线程私有的。并且程序计数器是唯一一个在 Java 虚拟机规范中没有规定任何 OutOfMemoryError 情况的区域。

## 2、GC回收机制和分代回收策略

垃圾回收指的是JVM回收内存中已经没有用的对象（垃圾）。

不同的虚拟机实现有着不同的 GC 实现机制，但是一般情况下每一种 GC 实现都会在以下两种情况下触发垃圾回收。  
1、Allocation Failure：在堆内存中分配时，如果因为可用剩余空间不足导致对象内存分配失败，这时系统会触发一次 GC。  
2、System.gc()：在应用层，Java 开发工程师可以主动调用此 API 来请求一次 GC。

**如何识别垃圾：**  

JVM通过**可达性分析算法**来标识垃圾，首先通过GC Root作为起始点，然后向下进行搜索，搜索所走过的路径称为引用链，最后通过判断对象的引用链是否可达来决定对象是否可以被回收。

**可以作为GC Root的对象：**  

1、Java 虚拟机栈（局部变量表）中的引用的对象。  
2、方法区中静态引用指向的对象。  
3、仍处于存活状态中的线程对象。  
4、Native 方法中 JNI 引用的对象。  

**如何回收垃圾** （通过垃圾回收算法）

1、标记清除算法（Mark and Sweep GC）  
<span>从”GC Roots”集合开始，将内存整个遍历一次，保留所有可以被GC Roots直接或间接引用到的对象，而剩下的对象都当作垃圾对待并回收。</span>

2、复制算法（Copying）  
<span>将现有的内存空间分为两快，每次只使用其中一块，在垃圾回收时将正在使用的内存中的存活对象复制到未被使用的内存块中。之后，清除正在使用的内存块中的所有对象，交换两个内存的角色，完成垃圾回收。</span>

3、标记-压缩算法 (Mark-Compact)  
<span>需要先从根节点开始对所有可达对象做一次标记，之后，它并不简单地清理未标记的对象，而是将所有的存活对象压缩到内存的一端。最后，清理边界外所有的空间。</span>

**JVM分代回收策略**：  

Java 虚拟机根据对象存活的周期不同，把堆内存划分为几块，一般分为**新生代**、**老年代**，这就是 JVM 的内存分代策略。注意: 在**HotSpot**中除了新生代和老年代，还有**永久代**。

分代回收的中心思想就是：对于新创建的对象会在新生代中分配内存，此区域的对象生命周期一般较短。如果经过多次回收仍然存活下来，则将它们转移到老年代中。

**引用**

判断对象是否存活我们是通过GC Roots的引用可达性来判断的。但是JVM中的引用关系并不止一种，而是有四种，根据引用强度的由强到弱，
他们分别是:**强引用(Strong Reference)、软引用(Soft Reference)、弱引用(Weak Reference)、虚引用(Phantom Reference)**。

![yy](../image/gc_yy.webp)

Android中软引用使用较多，但是不当的使用也会导致异常，比如软引用被强引用持有而频繁回收。

## 3、class类文件结构

![class](../image/class_jg.webp)

String字符串的长度：  
我们在java代码中声明的String字符串最终在class文件中的存储格式就 CONSTANT_utf8_info。因此一个字符串最大长度也就是u2所能代表的最大值65536个，但是需要使用2个字节来保存 null 值，因此一个字符串的最大长度为 65536 - 2 = 65534。

## 4、编译插桩操作字节码

顾名思义，所谓编译插桩就是在代码编译期间修改已有的代码或者生成新代码。实际上，我们项目中经常用到的 Dagger、ButterKnife 甚至是 Kotlin 语言，它们都用到了编译插桩的技术。

![bycz](../image/class_bycz.webp)

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

## 5、类加载器ClassLoader加载机制

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

```Test code = new Test();```  

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

## 6、class类的加载过程

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
 
 ## 7、Java内存模型与线程
 
 Java 内存模型的来源：主要是因为 CPU 缓存和指令重排等优化会造成多线程程序结果不可控。
 
 Java 内存模型是什么：本质上它就是一套规范，在这套规范中有一条最重要的 **happens-before** 原则。
 
 最后介绍了 Java 内存模型的使用，其中简单介绍了两种方式：**volatile** 和 **synchronized**。其实除了这两种方式，Java 还提供了很多关键字来实现 happens-before 原则，后续课时中将会详细介绍。 
 
 ## 8、Synchronized 和 ReentrantLock
 
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


## 9、Java 线程优化 偏向锁，轻量级锁、重量级锁

```java
private Object lock = new Object();

public void syncMethod(){
    synchronized(lock){
    }
}
```
锁对象是 lock 对象，在 JVM 中会有一个 ObjectMonitor 对象（通过对象头生成）与之对应。

多个线程进入ObjectMonitor的EntrySet中；  
线程获得锁，ObjectMonitor的Owner指向该线程，count++；  
线程被挂起，进入ObjectMonitor的WaitSet等待中，count --；    

**Java 虚拟机对 synchronized 的优化**：  

从 Java 6 开始，虚拟机对 synchronized 关键字做了多方面的优化，主要目的就是，避免 ObjectMonitor 的访问，减少“重量级锁”的使用次数，并最终减少线程上下文切换的频率 。其中主要做了以下几个优化： **锁自旋、轻量级锁、偏向锁**。  

**锁自旋**

所谓自旋，就是让该线程等待一段时间，不会被立即挂起，看当前持有锁的线程是否会很快释放锁。而所谓的等待就是执行一段无意义的循环即可（自旋）。  
<span>自旋锁也存在一定的缺陷：自旋锁要占用 CPU，如果锁竞争的时间比较长，那么自旋通常不能获得锁，白白浪费了自旋占用的 CPU 时间。这通常发生在锁持有时间长，且竞争激烈的场景中，此时应主动禁用自旋锁。</span>

**轻量级锁**

1、当线程执行某同步代码时，Java 虚拟机会在当前线程的栈帧中开辟一块空间（Lock Record）作为该锁的记录,  
2、然后 Java 虚拟机会尝试使用 CAS（Compare And Swap）操作，将锁对象的 Mark Word 拷贝到这块空间中，并且将锁记录中的 owner 指向 Mark Word。  
3、当线程再次执行此同步代码块时，判断当前对象的 Mark Word 是否指向当前线程的栈帧，如果是则表示当前线程已经持有当前对象的锁，则直接执行同步代码块；否则只能说明该锁对象已经被其他线程抢占了，这时轻量级锁需要膨胀为重量级锁。  
<span>轻量级锁所适应的场景是线程交替执行同步块的场合，如果存在同一时间访问同一锁的场合，就会导致轻量级锁膨胀为重量级锁。</span>

**偏向锁**

偏向锁的意思是如果一个线程获得了一个偏向锁，如果在接下来的一段时间中没有其他线程来竞争锁，那么持有偏向锁的线程再次进入或者退出同一个同步代码块，不需要再次进行抢占锁和释放锁的操作。  
<span>偏向锁的具体实现就是在锁对象的对象头中有个 ThreadId 字段，默认情况下这个字段是空的，当第一次获取锁的时候，就将自身的 ThreadId 写入锁对象的 Mark Word 中的 ThreadId 字段内，将是否偏向锁的状态置为 01。这样下次获取锁的时候，直接检查 ThreadId 是否和自身线程 Id 一致，如果一致，则认为当前线程已经获取了锁，因此不需再次获取锁，略过了轻量级锁和重量级锁的加锁阶段。提高了效率。</span>

**总结**：

本课时主要介绍了 Java 中锁的几种状态，其中偏向锁和轻量级锁都是通过自旋等技术避免真正的加锁，而重量级锁才是获取锁和释放锁，重量级锁通过对象内部的监视器（ObjectMonitor）实现，其本质是依赖于底层操作系统的 Mutex Lock 实现，操作系统实现线程之间的切换需要从用户态到内核态的切换，成本非常高。实际上Java对锁的优化还有”锁消除“，但是”锁消除“是基于Java对象逃逸分析的，如果对此感兴趣可以查阅 Java 逃逸分析 这篇文章。

## 10、深入理解 AQS 和 CAS 原理

**总结**

总体来说，AQS 是一套框架，在框架内部已经封装好了大部分同步需要的逻辑，在 AQS 内部维护了一个状态指示器 state 和一个等待队列 Node，而通过 state 的操作又分为两种：独占式和共享式，这就导致 AQS 有两种不同的实现：独占锁（ReentrantLock 等）和分享锁（CountDownLatch、读写锁等）。本课时主要从独占锁的角度深入分析了 AQS 的加锁和释放锁的流程。

理解 AQS 的原理对理解 JUC 包中其他组件实现的基础有帮助，并且理解其原理才能更好的扩展其功能。上层开发人员可以基于此框架基础上进行扩展实现适合不同场景、不同功能的锁。其中几个有可能需要子类同步器实现的方法如下。

1、lock()。  
2、tryAcquire(int)：独占方式。尝试获取资源，成功则返回 true，失败则返回 false。  
3、tryRelease(int)：独占方式。尝试释放资源，成功则返回 true，失败则返回 false。  
4、tryAcquireShared(int)：共享方式。尝试获取资源。负数表示失败；0 表示成功，但没有剩余可用资源；正数表示成功，且有剩余资源。  
5、tryReleaseShared(int)：共享方式。尝试释放资源，如果释放后允许唤醒后续等待结点返回 true，否则返回 false。  

**CAS 全称是 Compare And Swap，译为比较和替换，是一种通过硬件实现并发安全的常用技术**，底层通过利用 CPU 的 CAS 指令对缓存加锁或总线加锁的方式来实现多处理器之间的原子操作。

它的实现过程主要有 3 个操作数：内存值 V，旧的预期值 E，要修改的新值 U，当且仅当预期值 E和内存值 V 相同时，才将内存值 V 修改为 U，否则什么都不做。

CAS 底层会根据操作系统和处理器的不同来选择对应的调用代码，以 Windows 和 X86 处理器为例，如果是多处理器，通过带 lock 前缀的 cmpxchg 指令对缓存加锁或总线加锁的方式来实现多处理器之间的原子操作；如果是单处理器，通过 cmpxchg 指令完成原子操作。

## 11、线程池之刨根问底

线程池主要解决两个问题：  
一、 当执行大量异步任务时线程池能够提供很好的性能。  
二、 线程池提供了一种资源限制和管理的手段，比如可以限制线程的个数，动态新增线程等。  

**线程池体系**  

![thread](../image/thread_pool.webp)

1、Executor 是线程池最顶层的接口，在 Executor 中只有一个 execute 方法，用于执行任务。至于线程的创建、调度等细节由子类实现。  
2、ExecutorService 继承并拓展了 Executor，在 ExecutorService 内部提供了更全面的任务提交机制以及线程池关闭方法。  
3、ThreadPoolExecutor 是 ExecutorService 的默认实现，所谓的线程池机制也大多封装在此类当中，因此它是本课时分析的重点。  
4、ScheduledExecutorService 继承自 ExecutorService，增加了定时任务相关方法。  
5、ScheduledThreadPoolExecutor 继承自 ThreadPoolExecutor，并实现了 ScheduledExecutorService 接口。  
6、ForkJoinPool 是一种支持任务分解的线程池，一般要配合可分解任务接口 ForkJoinTask 来使用。  


## 12、DVM以及ART是如何对JVM进行优化的？

**什么是Dalvik?**  
Dalvik是Google公司自己设计用于Android平台的Java虚拟机，Android工程师编写的Java或者Kotlin代码最终都是在这台虚拟机中被执行的。在 Android5.0之前叫作DVM，5.0之后改为ART（Android Runtime）。

>DVM 大多数实现与传统的 JVM 相同，但是因为 Android 最初是被设计用于手机端的，对内存空间要求较高，并且起初Dalvik目标是只运行在ARM架构的CPU上。针对这几种情况，Android DVM 有了自己独有的优化措施。

**Dex文件优化**  
传统Class文件是由一个Java源码文件生成的.Class文件，而Android是把所有Class文件进行合并优化，然后生成一个最终的class.dex文件。dex文件去除了class文件中的冗余信息（比如重复字符常量），并且结构更加紧凑，因此在dex解析阶段，可以减少I/O操作，提高了类的查找速度。

注意：这一优化过程也会伴随着一些副作用，最经典的就是 Android 65535 问题。出现这个问题的根本原因是在 DVM 源码中的 MemberIdsSection.java 类中，如果items个数超过DexFormat.MAX_MEMBER_IDX则会报错。（items 代表 dex 文件中的方法个数、属性个数、以及类的个数）
也就是说理论上不止方法数，我们在 java 文件中声明的变量，或者创建的类个数如果也超过 65535 个，同样会编译失败，Android 提供了 MultiDex 来解决这个问题。

**架构基于寄存器&基于栈堆结构**  
JVM的指令集是基于栈结构来执行的，而Android却是基于寄存器的，不过这里不是直接操作硬件的寄存器，而是在内存中模拟一组寄存器。基于寄存器的指令会比基于栈的指令少，虽然增加了指令长度但却缩减了指令的数量，执行也更为快速。

**内存管理与回收**  
DVM与JVM另一个比较显著的不同就是内存结构的区别，主要体现在对”堆”内存的的管理。Dalvik虚拟机中的堆被划分为了2部分：**Active Heap**和**Zygote Heap**。  
Android系统的第一个Dalvik虚拟机是由Zygote进程创建的，而应用程序进程是由Zygote进程fork出来的。

Zygote进程是在系统启动时产生的，它会完成虚拟机的初始化，库的加载，预置类库的加载和初始化等操作，而在系统需要一个新的虚拟机实例时，Zygote 通过复制自身，最快速的提供一个进程；另外，对于一些只读的系统库，所有虚拟机实例都和Zygote共享一块内存区域，大大节省了内存开销。

**Dalvik虚拟机堆**  
在 Dalvik 虚拟机中，堆实际上就是一块匿名共享内存。Dalvik虚拟机并不是直接管理这块匿名共享内存，而是将它封装成一个mspace，交给C库来管理，可以很好地解决内存碎片问题。


