# IO流
> 它是一种数据的流从源头流到目的地。  
> 比如文件拷贝，输入流和输出流都包括了。输入流从文件中读取数据存储到进程(process)中，输出流从进程中读取数据然后写入到目标文件。

1、按照数据流的方向不同可以分为：**输入流** 和 **输出流。**  

2、按照处理数据单位不同可以分为：**字节流** 和 **字符流。**  
字节流：一次读入或读出是8位二进制，后缀是Stream。  
字符流：一次读入或读出是16位二进制，后缀是Reader，Writer。  
**字符流能实现的功能字节流都能实现，反之不一定。如：图片，视频等二进制文件，只能使用字节流读写。**  

3、按照实现功能不同可以分为：**节点流** 和 **处理流。**  
节点流：直接与数据源相连，读入或读出。  
处理流：与节点流一块使用，在节点流的基础上，再套接一层，套接在节点流上的就是处理流。  

## 流的超类

**OutputStream**：字节输出流的超类。  
操作的数据都是字节。  
定义了输出字节流的基本共性功能。  
输出流中定义都是写write方法。操作字节数组write(byte\[\])，操作单个字节write(byte)。  
**InputStream**：字节输入流的超类。  
int read():读取一个字节并返回，没有字节返回-1。  
int read(byte\[\]): 读取一定量的字节数，并存储到字节数组中，返回读取到的字节数。  
**Reader**：字符输入流的超类。  
**Writer**：字符输出流的超类。  

![IO流](https://img.upyun.zzming.cn/android/io.png)

**处理IO流异常**

```
public class FileOutputStreamDemo {
    public static void main(String[] args) {
        File file = new File("c:\\file.txt");
        //定义FileOutputStream的引用
        FileOutputStream fos = null;
        try {
            //创建FileOutputStream对象
            fos = new FileOutputStream(file);
            //写出数据
            fos.write("abcde".getBytes());
        } catch (IOException e) {
            System.out.println(e.toString() + "----");
        } finally {
            //一定要判断fos是否为null，只有不为null时，才可以关闭资源
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException("");
                }
            }
        }
    }
}
```
> [java IO流学习总结](https://www.cnblogs.com/hopeyes/p/9736642.html)