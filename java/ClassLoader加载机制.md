# ClassLoader加载机制

>在 Java 程序启动的时候，并不会一次性加载程序中所有的 .class 文件，而是在程序的运行过程中，动态地加载相应的类到内存中。

通常情况下，Java 程序中的 .class 文件会在以下 2 种情况下被 ClassLoader 主动加载到内存中：

- 调用类构造器  
- 调用类中的静态（static）变量或者静态方法

JVM 中自带 3 个类加载器：
- 启动类加载器：**BootstrapClassLoader**
- 扩展类加载器：**ExtClassLoader**（JDK 1.9 之后，改名为 PlatformClassLoader）
- 系统加载器：**APPClassLoader**

## 双亲委派模式

既然 JVM 中已经有了这 3 种 ClassLoader，那么 JVM 又是如何知道该使用哪一个类加载器去加载相应的类呢？  
答案就是：双亲委派模式（Parents Delegation Model）。

所谓双亲委派模式就是，当类加载器收到加载类或资源的请求时，通常都是先委托给父类加载器加载，也就是说，只有当父类加载器找不到指定类或资源时，自身才会执行实际的类加载过程。

这么设计的原因是为了防止危险代码的植入，比如String类，如果在AppClassLoader就直接被加载，就相当于会被篡改了，所以都要经过老大，也就是BootstrapClassLoader进行检查，已经加载过的类就不需要再去加载了。

**举例说明：** `TestClass test = new TestClass();`

默认情况下，JVM 首先使用 AppClassLoader 去加载 Test 类。  
① AppClassLoader 将加载的任务委派给它的父类加载器（parent）—ExtClassLoader。  
② ExtClassLoader 的 parent 为 null，所以直接将加载任务委派给 BootstrapClassLoader。  
③ BootstrapClassLoader 在 jdk/lib 目录下无法找到 TestClass 类，因此返回的 Class 为 null。  
④ 因为 parent 和 BootstrapClassLoader 都没有成功加载 TestClass 类，所以AppClassLoader会调用自身的 findClass 方法来加载 TestClass。

## Android 中的 ClassLoader

本质上，Android 和传统的 JVM 是一样的，也需要通过 ClassLoader 将目标类加载到内存，类加载器之间也符合双亲委派模型。但是在 Android 中， ClassLoader 的加载细节有略微的差别。

在 Android 虚拟机里是无法直接运行 .class 文件的，Android 会将所有的 .class 文件转换成一个 .dex 文件，并且 Android 将加载 .dex 文件的实现封装在 BaseDexClassLoader 中，而我们一般只使用它的两个子类：**PathClassLoader** 和 **DexClassLoader**。

### PathClassLoader

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

### DexClassLoader

很明显，对比 PathClassLoader 只能加载已经安装应用的 dex 或 apk 文件，DexClassLoader 则没有此限制，可以从 SD 卡上加载包含 class.dex 的 .jar 和 .apk 文件，这也是插件化和热修复的基础，在不需要安装应用的情况下，完成需要使用的 dex 的加载。

DexClassLoader 的源码里面只有一个构造方法，代码如下：

```java
public DexClassLoader(String dexPath,String optimizedDirectory,String libraryPath,ClassLoader parent){
  super(dexPath,new File(optimizedDirectory),libraryPath,parent);
}
```

- dexPath：包含 class.dex 的 apk、jar 文件路径 ，多个路径用文件分隔符（默认是" : "）分隔。
- optimizedDirectory：用来缓存优化的 dex 文件的路径，即从 apk 或 jar 文件中提取出来的 dex 文件。该路径不可以为空，且应该是应用私有的，有读写权限的路径。

**使用DexClassLoader实现热修复**

①创建 HotFix patch 包，打成jar包，使用dex工具将jar包中的class文件优化成dex文件  
②将 HotFix patch 拷贝到SD卡中，原项目中使用DexClassLoader来加载需要被修复的类

### 总结

- ClassLoader 就是用来加载 class 文件的，不管是 jar 中还是 dex 中的 class。  
- Java 中的 ClassLoader 通过双亲委派来加载各自指定路径下的 class 文件。  
- 可以自定义 ClassLoader，一般覆盖 findClass() 方法，不建议重写 loadClass 方法。  
- Android 中常用的两种 ClassLoader 分别为：PathClassLoader 和 DexClassLoader。  
