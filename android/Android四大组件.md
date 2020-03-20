# Android四大组件

## Activity
> 描述UI，并且处理用户与机器屏幕的交互。

Activity生命周期图

![activity](https://img.upyun.zzming.cn/android/activity.png)

|  回调   |  描述   |
| --- | --- |
|  onCreate()   |  这是第一个回调，在活动第一次创建时调用   |
|  onStart()   |  这个回调在活动为用户可见时被调用   |
|  onResume()   |   这个回调在应用程序与用户开始可交互的时候调用  |
|  onPause()   |  被暂停的活动无法接受用户输入，不能执行任何代码。当前活动将要被暂停，上一个活动将要被恢复时调用   |
|  onStop()   |  当活动不在可见时调用   |
|  onDestroy()   |  当活动被系统销毁之前调用  |
|  onRestart()   |  当活动被停止以后重新打开时调用  |

### Activity启动模式  

#### 默认启动模式**Standard**
![standard](https://img.upyun.zzming.cn/android/activity_standard)
#### 栈顶复用模式**SingleTop**
![singleTop](https://img.upyun.zzming.cn/android/activity_singleTop)
#### 栈内复用模式**SingleTask**
![singleTask](https://img.upyun.zzming.cn/android/activity_singleTask)
#### 全局唯一模式**SingleInstance**
![singleInstance](https://img.upyun.zzming.cn/android/activity_singleInstance)

## Service
> 处理与应用程序关联的后台操作。  
> 服务是一个后台运行的组件，执行长时间运行且不需要用户交互的任务，即使应用被销毁也依然可以工作。  
> 服务有两种运行状态。

|  状态   |  描述   |
| --- | --- |
|  Started   |  Android的应用程序组件，如活动，通过startService()启动了服务，则服务是Started状态。一旦启动，服务可以在后台无限期运行，即使启动它的组件已经被销毁。   |
|  Bound  |  当Android的应用程序组件通过bindService()绑定了服务，则服务是Bound状态。Bound状态的服务提供了一个客户服务器接口来允许组件与服务进行交互，如发送请求，获取结果，甚至通过IPC来进行跨进程通信。   |

Service生命周期图

![service](https://img.upyun.zzming.cn/android/service.png)

在Service的生命周期里，常用的有：

* 4个手动调用的方法

|  手动调用方法   |  作用   |
| --- | --- |
|  startService()   |  启动服务   |
|  stopService()   |  关闭服务   |
|  bindService()   |  绑定服务   |
|  unbindService()   |  解绑服务   |

* 5个自动调用的方法

|  内部自动调用的方法   |  作用   |
| --- | --- |
|  onCreat()   |  创建服务   |
|  onStartCommand()   |  开始服务   |
|  onDestroy()   |  销毁服务   |
|  onBind()   |  绑定服务   |
|  onUnbind()   |  解绑服务   |

* 回调描述

|  回调   |  描述   |
| --- | --- |
|  onCreate()   |  当服务通过onStartCommand()和onBind()被第一次创建的时候，系统调用该方法。该调用要求执行一次性安装。  |
|  onStartCommand()   |  其他组件(如活动)通过调用startService()来请求启动服务时，系统调用该方法。如果你实现该方法，你有责任在工作完成时通过stopSelf()或者stopService()方法来停止服务。  |
|  onBind()   |  当其他组件想要通过bindService()来绑定服务时，系统调用该方法。如果你实现该方法，你需要返回IBinder对象来提供一个接口，以便客户来与服务通信。你必须实现该方法，如果你不允许绑定，则直接返回null。  |
|  onUnbind()   |  当客户中断所有服务发布的特殊接口时，系统调用该方法。  |
|  onRebind()   |  当新的客户端与服务连接，且此前它已经通过onUnbind(Intent)通知断开连接时，系统调用该方法。  |
|  onDestroy()   |  当服务不再有用或者被销毁时，系统调用该方法。你的服务需要实现该方法来清理任何资源，如线程，已注册的监听器，接收器等。  |

## Broadcast Receiver
> 处理Android操作系统和应用程序之间的通信。  
> 广播接收器用于响应来自其他应用程序或者系统的广播消息。这些消息有时被称为事件或者意图。  
>例如，应用程序可以初始化广播来让其他的应用程序知道一些数据已经被下载到设备，并可以为他们所用。这样广播接收器可以定义适当的动作来拦截这些通信。

有以下两个重要的步骤来使系统的广播意图配合广播接收器工作。
* 创建广播接收器
* 注册广播接收器  
还有一个附加的步骤，要实现自定义的意图，你必须创建并广播这些意图。

### 注册广播接收器 {docsify-ignore}

![broadcast](https://img.upyun.zzming.cn/android/broadcast.jpg)

```xml
<application
   android:icon="@drawable/ic_launcher"
   android:label="@string/app_name"
   android:theme="@style/AppTheme" >
   <receiver android:name="cn.programmer.MyReceiver">
//监听系统启动意图，自定义意图
      <intent-filter>
         <action android:name="android.intent.action.BOOT_COMPLETED">
         </action>
         <action android:name="cn.programmer.CUSTOM_INTENT">
         </action>
      </intent-filter>
   </receiver>
</application>
```
### 广播自定义意图 {docsify-ignore}
```java
// 广播自定义意图
public void broadcastIntent(View view){
    Intent intent = new Intent();
    intent.setAction("cn.programmer.CUSTOM_INTENT");
    intent.setComponent(new ComponentName("cn.programmer","cn.programmer.MyReceiver"));
    sendBroadcast(intent);
}
```
### 创建广播接收器 {docsify-ignore}
```java
package cn.programmer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "检测到意图。", Toast.LENGTH_LONG).show();
    }
}
```

## Content Provider
> 处理数据和数据库管理方面的问题。  
> 内容提供者组件通过请求从一个应用程序向其他的应用程序提供数据。这些请求由类 ContentResolver 的方法来处理。内容提供者可以使用不同的方式来存储数据。数据可以被存放在数据库，文件，甚至是网络。

![content](https://img.upyun.zzming.cn/android/content.jpg)

有时候需要在应用程序之间共享数据。这时内容提供者变得非常有用。
* 内容提供者可以让内容集中，必要时可以有多个不同的应用程序来访问。
* 内容提供者的行为和数据库很像。你可以查询，编辑它的内容，使用 insert()， update()， delete() 和 query() 来添加或者删除内容。多数情况下数据被存储在 SQLite 数据库。
* 内容提供者被实现为类 **ContentProvider** 类的子类。需要实现一系列标准的 API，以便其他的应用程序来执行事务。

### 内容URI {docsify-ignore}
```
<prefix>://<authority>/<data_type>/<id>
```
|  部分   |  说明  |
| --- | --- |
|  prefix   |  前缀：一直被设置为content://   |
|  authority   |  授权：指定内容提供者的名称，例如联系人，浏览器等。第三方的内容提供者可以是全名，如：cn.programmer.statusprovider   |
|  data_type   |  数据类型：这个表明这个特殊的内容提供者中的数据的类型。例如：你要通过内容提供者Contacts来获取所有的通讯录，数据路径是people，那么URI将是下面这样：content://contacts/people   |
|  id   |  这个指定特定的请求记录。例如：你在内容提供者Contacts中查找联系人的ID号为5，那么URI看起来是这样：content://contacts/people/5   |

### 创建内容提供者 {docsify-ignore}
这里描述创建自己的内容提供者的简单步骤。

* 首先，你需要继承类 ContentProviderbase 来创建一个内容提供者类。
* 其次，你需要定义用于访问内容的你的内容提供者URI地址。
* 接下来，你需要创建数据库来保存内容。通常，Android 使用 SQLite 数据库，并在框架中重写 onCreate() 方法来使用 SQLiteOpenHelper 的方法创建或者打开提供者的数据库。当你的应用程序被启动，它的每个内容提供者的 onCreate() 方法将在应用程序主线程中被调用。
* 最后，使用标签在 AndroidManifest.xml 中注册内容提供者。

以下是让你的内容提供者正常工作，你需要在类 ContentProvider 中重写的一些方法：

![content1](https://img.upyun.zzming.cn/android/content1.jpg)

|  部分   |  说明  |
| --- | --- |
|  onCreate()   |  当提供者被启动时调用   |
|  query()   |  该方法从客户端接受请求，结果是返回指针(Cursor)对象   |
|  insert()   |  该方法向内容提供者插入新的记录   |
|  delete()   |  该方法从内容提供者中删除已存在的记录   |
|  update()   |  该方法更新内容提供者中已存在的记录   |
|  getType()   |  该方法为给定的URI返回元数据类型   |


> [细谈Activity四种启动模式](https://blog.csdn.net/zy_jibai/article/details/80587083)  
>[Android：Service生命周期 完全解析](https://www.jianshu.com/p/8d0cde35eb10)