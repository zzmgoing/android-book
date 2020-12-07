# ARouter

ARouter会在项目的编译期通过注解处理器扫描所有添加@Route注解的Activity类，然后将Route注解中的path地址和Activity.class文件映射关系保存到它自己生成的java文件中。  

首先，我们了解下ARouter是干嘛的？ARouter是阿里巴巴研发的一个用于解决组件间，模块间界面跳转问题的框架。所以简单的说，就是用来跳转界面的，不同于平时用到的显式或隐式跳转，只需要在对应的界面上添加注解，就可以实现跳转，看个案例：

```java
@Route(path = "/test/activity")
public class YourActivity extend Activity {
    ...
}

//跳转
ARouter.getInstance().build("/test/activity").navigation();
```

使用很方便，通过一个path就可以进行跳转了，那么原理是什么呢？

其实仔细思考下，就可以联想到，既然关键跳转过程是通过path跳转到具体的activity，那么原理无非就是把path和Activity一一对应起来就行了。没错，其实就是通过注释，通过apt技术，也就是注解处理工具，把path和activity关联起来了。主要有以下几个步骤：

1、代码里加入的@Route注解，会在编译时期通过apt生成一些存储path和activity.class映射关系的类文件  
2、app进程启动的时候会加载这些类文件，把保存这些映射关系的数据读到内存里(保存在map里)  
3、进行路由跳转的时候，通过build()方法传入要到达页面的路由地址，ARouter会通过它自己存储的路由表找到路由地址对应的Activity.class  
4、然后new Intent方法，如果有调用ARouter的withString()方法，就会调用intent.putExtra(String name, String value)方法添加参数  
5、最后调用navigation()方法，它的内部会调用startActivity(intent)进行跳转  

**ARouter怎么实现页面拦截**

先说一个拦截器的案例，用作页面跳转时候检验是否登录，然后判断跳转到登录页面还是目标页面：

```java
@Interceptor(name = "login", priority = 6)
public class LoginInterceptorImpl implements IInterceptor {
    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        String path = postcard.getPath();
        boolean isLogin = SPUtils.getInstance().getBoolean(ConfigConstants.SP_IS_LOGIN, false);

        if (isLogin) { 
            // 如果已经登录不拦截
            callback.onContinue(postcard);
        } else {  
            // 如果没有登录，进行拦截
            callback.onInterrupt(postcard);
        }

    }

    @Override
    public void init(Context context) {
        LogUtils.v("初始化成功"); 
    }

}

//使用
ARouter.getInstance().build(ConfigConstants.SECOND_PATH)
                         .withString("msg", "123")
                          .navigation(this,new LoginNavigationCallbackImpl()); 
                          // 第二个参数是路由跳转的回调


// 拦截的回调
public class LoginNavigationCallbackImpl  implements NavigationCallback{
    @Override 
    public void onFound(Postcard postcard) {

    }

    @Override 
    public void onLost(Postcard postcard) {

    }

    @Override   
    public void onArrival(Postcard postcard) {

    }

    @Override
    public void onInterrupt(Postcard postcard) {
     //拦截并跳转到登录页
        String path = postcard.getPath();
        Bundle bundle = postcard.getExtras();
        ARouter.getInstance().build(ConfigConstants.LOGIN_PATH)
                .with(bundle)
                .withString(ConfigConstants.PATH, path)
                .navigation();
    }
}
```

拦截器实现IInterceptor接口，使用注解@Interceptor，这个拦截器就会自动被注册了，同样是使用APT技术自动生成映射关系类。这里还有一个优先级参数priority，数值越小，就会越先执行。
