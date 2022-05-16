# SharedPreferences存储

> SharedPreferences的本质是用键值对的方式保存数据到xml文件，然后对文件进行读写操作。

## SharedPreferences的缺点

- SP第一次加载数据时需要全量加载，当数据量大时可能会阻塞UI线程造成卡顿
- SP读写文件不是类型安全的，且没有发出错误信号的机制，缺少事务性API
- commit() / apply()操作可能会造成ANR问题：  
  commit()是同步提交，会在UI主线程中直接执行IO操作，当写入操作耗时比较长时就会导致UI线程被阻塞，进而产生ANR。  
  apply()虽然是异步提交，但异步写入磁盘时，如果执行了Activity / Service中的onStop()方法，那么一样会同步等待SP写入完毕，等待时间过长时也会引起ANR问题。

## SharedPreferences进程安全吗

SharedPreferences是**进程不安全**的，因为没有使用跨进程的锁。既然是进程不安全，那么就有可能在多进程操作的时候发生数据异常。

我们有两个办法能保证进程安全：  
- 使用跨进程组件，也就是ContentProvider，这也是官方推荐的做法。通过ContentProvider对多进程进行了处理，使得不同进程都是通过ContentProvider访问SharedPreferences。  
- 加文件锁，由于SharedPreferences的本质是读写文件，所以我们对文件加锁，就能保证进程安全了。

## SharedPreferences 操作有文件备份吗？是怎么完成备份的？

SharedPreferences 的写入操作，首先是**将源文件备份**，  
再写入所有数据，只有写入成功，并且通过 sync 完成落盘后，才会将 Backup（.bak） 文件删除。  
如果写入过程中进程被杀，或者关机等非正常情况发生。进程再次启动后如果发现该 SharedPreferences 存在 Backup 文件，就将 Backup 文件重名为源文件，原本未完成写入的文件就直接丢弃，这样就能保证之前数据的正确。

## commit和apply的区别

commit是直接写入磁盘，apply是先写入内存，然后异步写入磁盘，频繁写入操作的话建议使用apply。

## JetPack DataStore

DataStore的优势：

- DataStore基于[事务方式](/java/Java基础.md?id=数据库)（原子性、一致性、隔离性、持久性）处理数据更新。
- DataStore基于Kotlin Flow存取数据，默认在Dispatchers.IO里异步操作，避免阻塞UI线程，且在读取数据时能对发生的Exception进行处理。
- 不提供apply()、commit()存留数据的方法。
- 支持SP一次性自动迁移至DataStore中。