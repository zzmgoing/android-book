#  String、StringBuilder、StringBuffer

StringBuffer是线程安全的。  
StringBuilder是非线程安全。  
StringBuilder与StringBuffer的对象存储在堆中。String对象存储在Constant String Pool(字符串常量池)。  
StringBuffer和StringBuilder是可变对象。String一旦创建无法修改，并且String是线程安全的。
<span>安全和性能考虑(字符串常量池)是String类不可变的主要原因。</span>