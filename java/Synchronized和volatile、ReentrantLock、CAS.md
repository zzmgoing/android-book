# Synchronized和volatile、ReentrantLock、CAS 

**Synchronized**  
Synchronized提供了同步锁的概念，被Synchronized修饰的代码段可以防止被多个线程同时执行，必须一个线程把Synchronized修饰的代码段都执行完毕了，其他的线程才能开始执行这段代码。 因为Synchronized保证了在同一时刻，只能有一个线程执行同步代码块，所以执行同步代码块的时候相当于是单线程操作了，那么线程的可见性、原子性、有序性（线程之间的执行顺序）它都能保证了。

**volatile**  
其实volatile关键字的作用就是保证了可见性和有序性（不保证[原子性](/java/多线程三个特性.md)），如果一个共享变量被volatile关键字修饰，那么如果一个线程修改了这个共享变量后，其他线程是立马可知的。  
volatile能禁止指令重新排序，在指令重排序优化时，在volatile变量之前的指令不能在volatile之后执行，在volatile之后的指令也不能在volatile之前执行，所以它保证了有序性。  

**ReentrantLock**  
ReenTrantLock的实现是一种自旋锁，通过循环调用CAS操作来实现加锁。它的性能比较好也是因为避免了使线程进入内核态的阻塞状态。

**Synchronized和volatile的区别**  
volatile只能作用于变量，使用范围较小。synchronized可以用在方法、类、同步代码块等，使用范围比较广。  
volatile只能保证可见性和有序性，不能保证原子性。而可见性、有序性、原子性synchronized都可以保证。  
volatile不会造成线程阻塞。synchronized可能会造成线程阻塞。  

**Synchronized和ReentrantLock的区别**  
ReenTrantLock可以指定是公平锁还是非公平锁。而synchronized只能是非公平锁。所谓的公平锁就是先等待的线程先获得锁。  
ReenTrantLock提供了一个Condition（条件）类，用来实现分组唤醒需要唤醒的线程们，而不是像synchronized要么随机唤醒一个线程要么唤醒全部线程。  
ReenTrantLock提供了一种能够中断等待锁的线程的机制，通过lock.lockInterruptibly()来实现这个机制。  

**synchronized 修饰实例方法和修饰静态方法的区别**

synchronized修饰实例方法是对类的当前实例进行加锁，防止其他线程同时访问该类的该实例的所有synchronized块，注意这里是“类的当前实例”，类的两个不同实例就没有这种约束了。

synchronized修饰静态方法恰好就是要控制类的所有实例的访问了，static synchronized是限制线程同时访问jvm中该类的所有实例同时访问对应的代码块。

**实现原理**  
jvm基于进入和退出Monitor对象来实现方法同步和代码块同步。