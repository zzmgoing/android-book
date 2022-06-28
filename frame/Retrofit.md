# Retrofit

> Retrofit是基于OkHttp封装的一套网络请求框架。

1、通过注解简化代码  
2、支持自己更换解析的方式，搭配各式的converter来实现  
3、restful的api设计风格  
4、支持RxJava

## 动态代理

```java
public <T> T create(final Class<T> service) {
  // 省略非关键代码
  return (T) Proxy.newProxyInstance(service.getClassLoader(),
      new Class<?>[] { service },
      new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object... args)
            throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }
            if (platform.isDefaultMethod(method)) {
                return platform.invokeDefaultMethod(method, service, proxy, args);
            }
            ServiceMethod serviceMethod = loadServiceMethod(method);
            OkHttpCall okHttpCall = new OkHttpCall<>(serviceMethod, args);
            return serviceMethod.callAdapter.adapt(okHttpCall);
        }
      });
}
```

静态代理：定义代理类，实现代理的接口，构造函数接收被代理的类，然后用代理类进行替换。

在Retrofit中使用到了动态代理，使用动态代理，可以无侵入式的扩展代码，在不改动原有代码的情况下，增强一些方法或功能，它的定义是：在程序运行时创建的代理方式。
