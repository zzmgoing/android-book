# Hook技术原理


Hook 的选择点：  
静态变量和单例，因为一旦创建对象，它们不容易变化，非常容易定位。

Hook 过程：  
1、寻找 Hook 点，原则是静态变量或者单例对象，尽量 Hook public 的对象和方法。  
2、选择合适的代理方式，如果是接口可以用动态代理。  
3、偷梁换柱——用代理对象替换原始对象。

## 代理模式

代理模式定义：为其他对象提供一种代理以控制这个对象的访问

### 静态代理

定义一个代理类，构造函数接收被代理的类，然后替换。

### 动态代理

- 定义代理对象和真实对象的公共接口；（与静态代理步骤相同）
- 真实对象实现公共接口中的方法；（与静态代理步骤相同）
- 定义一个实现了InvocationHandler接口的动态代理类；
- 通过Proxy类的newProxyInstance方法创建代理对象，调用代理对象的方法。

使用Proxy这个类的 newProxyInstance 这个方法

```java
public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
```

JDK动态代理需要借助接口来实现，如果我们要代理的对象功能没有抽成任何接口，那么我们就无法通过JDK动态代理的方式来实现。

- loader：一个ClassLoader对象，定义了由哪个ClassLoader对象来对生成的代理对象进行加载
- interfaces：一个Interface对象的数组，表示的是我将要给我需要代理的对象提供一组什么接口，如果我提供了一组接口给它，那么这个代理对象就宣称实现了该接口(多态)，这样我就能调用这组接口中的方法了
- 一个InvocationHandler对象，表示的是当我这个动态代理对象在调用方法的时候，会关联到哪一个InvocationHandler对象上。

与静态代理相比，动态代理具有如下的优点：

- 代理转发的过程自动化了，实现自动化搬砖；
- 代理类的代码逻辑和具体业务逻辑解耦，与业务无关；