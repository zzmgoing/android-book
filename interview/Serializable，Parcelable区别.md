# Serializable，Parcelable区别

序列化，表示将一个对象转换成可存储或可传输的状态。序列化后的对象可以在网络上进行传输，也可以存储到本地。

Serializable是由Java提供的序列化接口，它是一个标识接口，只需对类实现Serializable接口而无需实现方法就可以进行序列化操作。  
这种序列化是**使用了反射机制，序列化过程慢，从而降低了性能**，这种机制**在序列化的时候会创建大量的临时对象从而会引起GC频繁回收**。

Parcelable是由Android提供的序列化接口，google做了大量的优化。  
Parcelable方式的实现原理是**将一个完整的对象进行分解，而分解后的每一部分都是Intent所支持的数据类型**，这样也就实现传递对象的功能了。

android上应该尽量采用Parcelable，效率高。  

Parcelable的三个过程：序列化、反序列化和描述
```java
public class Demo implements Parcelable {

    private String name;

    private int age;

    private boolean isTall;

    public Demo(String name, int age, boolean isTall) {
        this.name = name;
        this.age = age;
        this.isTall = isTall;
    }

    protected Demo(Parcel in) {
        name = in.readString();
        age = in.readInt();
        isTall = in.readByte() != 0;
    }

    public static final Creator<Demo> CREATOR = new Creator<Demo>() {
        @Override
        public Demo createFromParcel(Parcel in) {
            return new Demo(in);
        }

        @Override
        public Demo[] newArray(int size) {
            return new Demo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeByte((byte) (isTall ? 1 : 0));
    }
}
```
**序列化：** 使用Parcel的writeXXX  
**反序列化：**  使用Parcel的readXXX  
**描述：** describeContents()一般默认返回0