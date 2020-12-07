# LruCache缓存机制

关于Android的三级缓存，其中主要的就是内存缓存和硬盘缓存。这两种缓存机制的实现都应用到了LruCache算法。

>LRU(Least Recently Used)是近期最少使用的算法，它的核心思想是当缓存满时，会优先淘汰那些近期最少使用的缓存对象。采用LRU算法的缓存有两种：LrhCache和DisLruCache，分别用于实现内存缓存和硬盘缓存，其核心思想都是LRU缓存算法。

**LruCache的介绍**

LruCache是个泛型类，主要算法原理是把最近使用的对象用强引用（即我们平常使用的对象引用方式）存储在 LinkedHashMap 中。当缓存满时，把最近最少使用的对象从内存中移除，并提供了get和put方法来完成缓存的获取和添加操作。

**LruCache的使用**

```java
int maxMemory = (int) (Runtime.getRuntime().totalMemory()/1024);
int cacheSize = maxMemory/8;
LruCache mMemoryCache = new LruCache<String,Bitmap>(cacheSize){
    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes()*value.getHeight()/1024;
    }
};
```

1、设置LruCache缓存的大小，一般为当前进程可用容量的1/8。  
2、重写sizeOf方法，计算出要缓存的每张图片的大小。  
**注意：** 缓存的总容量和每个缓存对象的大小所用单位要一致。

**LruCache的实现原理**

LruCache的核心思想很好理解，就是要维护一个缓存对象列表，其中对象列表的排列方式是按照访问顺序实现的，即一直没访问的对象，将放在队尾，即将被淘汰。而最近访问的对象将放在队头，最后被淘汰。

这个队列由**LinkedHashMap**来维护。
而LinkedHashMap是由**数组+双向链表**的数据结构来实现的。其中双向链表的结构可以实现访问顺序和插入顺序，使得LinkedHashMap中的<key,value>对按照一定顺序排列起来。

总结：LruCache中维护了一个集合LinkedHashMap，该LinkedHashMap是以访问顺序排序的。当调用put()方法时，就会在结合中添加元素，并调用trimToSize()判断缓存是否已满，如果满了就用LinkedHashMap的迭代器删除队尾元素，即近期最少访问的元素。当调用get()方法访问缓存对象时，就会调用LinkedHashMap的get()方法获得对应集合元素，同时会更新该元素到队头。

> [https://www.jianshu.com/p/b49a111147ee](https://www.jianshu.com/p/b49a111147ee)