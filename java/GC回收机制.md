# GC回收机制

>垃圾回收指的是JVM回收内存中已经没有用的对象（垃圾）。

不同的虚拟机实现有着不同的 GC 实现机制，但是一般情况下每一种 GC 实现都会在以下两种情况下触发垃圾回收。  
- Allocation Failure：在堆内存中分配时，如果因为可用剩余空间不足导致对象内存分配失败，这时系统会触发一次 GC。  
- System.gc()：在应用层，Java 开发工程师可以主动调用此 API 来请求一次 GC。

## 如何识别垃圾

JVM通过<span class="font-red">可达性分析算法</span>来标识垃圾，首先通过GC Root作为起始点，然后向下进行搜索，搜索所走过的路径称为引用链，最后通过判断对象的引用链是否可达来决定对象是否可以被回收。

可以作为GC Root的对象：

1、Java 虚拟机栈（局部变量表）中引用的对象。  
2、方法区中静态引用指向的对象。  
3、仍处于存活状态中的线程对象。  
4、Native 方法中 JNI 引用的对象。  

## 如何回收垃圾

通过垃圾回收算法:

- <span class="font-red">标记清除算法（Mark and Sweep GC）</span>

从”GC Roots”集合开始，将内存整个遍历一次，保留所有可以被GC Roots直接或间接引用到的对象，而剩下的对象都当作垃圾对待并回收。

- <span class="font-red">标记-压缩算法 (Mark-Compact)</span>

需要先从根节点开始对所有可达对象做一次标记，之后，它并不简单地清理未标记的对象，而是将所有的存活对象压缩到内存的一端。最后，清理边界外所有的空间。

- <span class="font-red">复制算法（Copying）</span>

将现有的内存空间分为两快，每次只使用其中一块，在垃圾回收时将正在使用的内存中的存活对象复制到未被使用的内存块中。之后，清除正在使用的内存块中的所有对象，交换两个内存的角色，完成垃圾回收。


## JVM分代回收策略

Java 虚拟机根据对象存活的周期不同，把堆内存划分为几块，一般分为<span class="font-red">新生代、老年代</span>，这就是 JVM 的内存分代策略。注意: 在**HotSpot**中除了新生代和老年代，还有<span class="font-blue">永久代</span>。

<span class="font-red">分代回收的中心思想就是：</span>对于新创建的对象会在新生代中分配内存，此区域的对象生命周期一般较短。如果经过多次回收仍然存活下来，则将它们转移到老年代中。

### 引用

判断对象是否存活我们是通过GC Roots的引用可达性来判断的。但是JVM中的引用关系并不止一种，而是有[四大引用类型](/java/四大引用类型.md)，根据引用强度的由强到弱，
他们分别是:**强引用(Strong Reference)、软引用(Soft Reference)、弱引用(Weak Reference)、虚引用(Phantom Reference)。**

Android中软引用使用较多，但是不当的使用也会导致异常，比如软引用被强引用持有而频繁回收。