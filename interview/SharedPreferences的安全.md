# SharedPreferences的安全

>SharedPreferences的本质是用键值对的方式保存数据到xml文件，然后对文件进行读写操作。

**SharedPreferences进程安全吗**

SharedPreferences是**进程不安全**的，因为没有使用跨进程的锁。既然是进程不安全，那么就有可能在多进程操作的时候发生数据异常。

我们有两个办法能保证进程安全：  
1、使用跨进程组件，也就是ContentProvider，这也是官方推荐的做法。通过ContentProvider对多进程进行了处理，使得不同进程都是通过ContentProvider访问SharedPreferences。  
2、加文件锁，由于SharedPreferences的本质是读写文件，所以我们对文件加锁，就能保证进程安全了。

**SharedPreferences 操作有文件备份吗？是怎么完成备份的？**

SharedPreferences 的写入操作，首先是将源文件备份，  
再写入所有数据，只有写入成功，并且通过 sync 完成落盘后，才会将 Backup（.bak） 文件删除。  
如果写入过程中进程被杀，或者关机等非正常情况发生。进程再次启动后如果发现该 SharedPreferences 存在 Backup 文件，就将 Backup 文件重名为源文件，原本未完成写入的文件就直接丢弃，这样就能保证之前数据的正确。