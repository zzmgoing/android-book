# Exception和Error有什么区别

> 首先Exception和Error都是继承于Throwable 类，在 Java 中只有 Throwable 类型的实例才可以被抛出（throw）或者捕获（catch），它是异常处理机制的基本组成类型。
>Exception和Error体现了JAVA这门语言对于异常处理的两种方式。

1、**Exception**是java程序运行中**可预料**的异常情况，咱们可以获取到这种异常，并且对这种异常进行业务外的处理。

2、**Error**是java程序运行中**不可预料**的异常情况，这种异常发生以后，会直接导致JVM不可处理或者不可恢复的情况。所以这种异常不可能抓取到，比如OutOfMemoryError、NoClassDefFoundError等。

其中的Exception又分为检查性异常和非检查性异常。两个根本的区别在于，检查性异常必须在编写代码时，使用try catch捕获（比如：IOException异常）。非检查性异常在代码编写时，可以忽略捕获操作（比如：ArrayIndexOutOfBoundsException），这种异常是在代码编写或者使用过程中通过规范可以避免发生的。 切记，Error是Throw不是Exception 。

## NoClassDefFoundError 和 ClassNotFoundException 有什么区别?

区别一： NoClassDefFoundError它是Error，ClassNotFoundException是Exception。

区别二：还有一个区别在于NoClassDefFoundError是JVM运行时通过classpath加载类时，找不到对应的类而抛出的错误。ClassNotFoundException是在编译过程中如果可能出现此异常，在编译过程中必须将ClassNotFoundException异常抛出！

NoClassDefFoundError发生场景如下：  
1、类依赖的class或者jar不存在 （简单说就是maven生成运行包后被篡改）  
2、类文件存在，但是存在不同的域中 （简单说就是引入的类不在对应的包下)  
3、大小写问题，javac编译的时候是无视大小的，很有可能你编译出来的class文件就与想要的不一样！这个没有做验证  

ClassNotFoundException发生场景如下：  
1、调用class的forName方法时，找不到指定的类  
2、ClassLoader 中的 findSystemClass() 方法时，找不到指定的类  

## RunTimeException和其他Exception区分?

其他Exception，**受检查异常**。可以理解为错误，必须要开发者解决以后才能编译通过，解决的方法有两种，  
1：throw到上层，  
2：try-catch处理。  

RunTimeException：运行时异常，又称**不受检查异常**，不受检查！不受检查！！不受检查！！！   
重要的事情说三遍，因为不受检查，所以在代码中可能会有RunTimeException时Java编译检查时不会告诉你有这个异常，但是在实际运行代码时则会暴露出来，比如经典的**1/0，空指针**等。如果不处理也会被Java自己处理。

> [Exception和Error有什么区别](https://blog.csdn.net/weixin_42124070/article/details/80833629)