# APK安装流程

- 复制APK到/data/app目录下，解压并扫描安装包
- 资源管理器解析APK里的资源文件
- 解析AndroidManifest文件，并在/data/data/目录下创建对应的应用数据目录
- 然后对dex文件进行优化，并保存在dalvik-cache目录下
- 将AndroidManifest文件解析出的四大组件信息注册到PackageManagerService中
- 安装完成后，发送广播通知安装完成

## Activity启动流程

1、首先startActivity()调用startActivityForResult()  
2、然后通过ActivityManagerProxy 调用 system_server 进程中的 ActivityManagerService中的startActivity()

* 如果应用进程未启动
* 2.1、 调用Zygote 孵化应用进程
* 2.2、 进程创建后调用 ActivityThread#main 方法
* 2.3、 main方法调用 attach 方法将应用进程绑定到ActivityManagerService 中（保存应用的 ApplicationThread 的代理对象）
* 2.4、 开启loop循环接收消息

3、ActivityManagerService 通过 ApplicationThread 的代理发送 Message 通知启动 Activity  
4、Activity 内部 Handler 处理 handleLaunchActivity，依次调用 performLaunchActivity，handleResumeActivity（即onCreate, onStart, onResume）

## 点击应用图标

> 点击应用图标后会去启动应用的LauncherActivity，如果LancerActivity所在的进程没有创建，还会创建新进程，整体的流程就是一个Activity的启动流程。

- 点击桌面应用图标，Launcher进程将启动Activity（LancerActivity）的请求以Binder的方式发送给了AMS
- AMS接收到启动请求后，交付ActivityStarter处理Intent和Flag等信息，然后再交给ActivityStackSupervisior/ActivityStack处理Activity进栈相关流程，同时以Socket方式请求Zygote进程fork新进程
- Zygote接收到新进程创建请求后fork出新进程
- 在新进程里创建ActivityThread对象，新创建的进程就是应用的主线程，在主线程里开启Looper消息循环，开始处理创建Activity
- ActivityThread利用ClassLoader去加载Activity、创建Activity实例，并回调Activity的onCreate()方法，这样便完成了Activity的启动