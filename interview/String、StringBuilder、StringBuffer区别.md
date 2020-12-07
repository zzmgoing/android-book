# String、StringBuilder、StringBuffer区别

1、String、StringBuffer是线程安全的，StringBuilder是非线程安全。

2、StringBuffer与StringBuilder的对象存储在堆中，String对象存储在Constant String Pool(字符串常量池)。

3、StringBuffer和StringBuilder是可变对象，String一旦创建无法修改，并且String是线程安全的。  
<span>安全和性能考虑(字符串常量池)是String类不可变的主要原因。</span>

**为什么String不可变**  

主要是为了安全。由于String广泛用于java类中的参数，所以安全是非常重要的考虑点。包括线程安全，打开文件，存储数据密码等等。

因为java字符串是不可变的，可以在java运行时节省大量java堆空间。因为**不同的字符串变量可以引用池中的相同的字符串**。如果字符串是可变得话，任何一个变量的值改变，就会反射到其他变量，那字符串池也就没有任何意义了。

String的不变性保证哈希码始终一，所以在用于HashMap等类的时候就不需要重新计算哈希码，提高效率。