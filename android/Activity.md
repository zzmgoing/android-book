# Activity
> 描述UI，并且处理用户与机器屏幕的交互。

## Activity生命周期

![activity](https://img.upyun.zzming.cn/android/activity.png)

|  回调   |  描述   |
| --- | --- |
|  onCreate()   |  这是第一个回调，在活动第一次创建时调用。   |
|  onStart()   |  这个回调在活动为用户可见时被调用。   |
|  onResume()   |   这个回调在应用程序与用户开始可交互的时候调用。  |
|  onPause()   |  被暂停的活动无法接受用户输入，不能执行任何代码。当前活动将要被暂停，上一个活动将要被恢复时调用。   |
|  onStop()   |  当活动不在可见时调用。   |
|  onDestroy()   |  当活动被系统销毁之前调用。  |
|  onRestart()   |  当活动被停止以后重新打开时调用。  |

## Activity启动模式

1、默认启动模式：**Standard**  
2、栈顶复用模式：**SingleTop**  
3、栈内复用模式：**SingleTask**  
4、全局唯一模式：**SingleInstance**  

## Activity间通信方式

1、Intent、Bundle  
2、类的静态变量  
3、全局变量，如在Application中定义的  
4、外部存储，如SharedPreference、SQLite、文件等  
5、Service  

## Activity启动流程

1、首先startActivity()最终都会调用startActivityForResult()  
2、然后通过ActivityManagerProxy 调用 system_server 进程中的 ActivityManagerServier中的startActivity()

* 如果应用进程未启动  
* 2.1、 调用Zygote 孵化应用进程  
* 2.2、 进程创建后调用 ActivityThread#main 方法  
* 2.3、 main方法 调用 attach 方法将应用进程绑定到ActivityManagerService 中（保存应用的 ApplicationThread 的代理对象）  
* 2.4、 开启loop循环接收消息  

3、ActivityManagerService 通过 ApplicationThread 的代理发送 Message 通知启动 Activity  
4、Activity 内部 Handler 处理 handleLaunchActivity，依次调用 performLaunchActivity，handleResumeActivity（即onCreate, onStart, onResume）