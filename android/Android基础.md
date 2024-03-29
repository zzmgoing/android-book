# Android基础

## Android架构

Android系统架构从下往上依次是：Linux内核层、硬件抽象层、C/C++库和Android Runtime、系统框架（Framework层）、系统应用层。

<details><summary>Android架构图及详解</summary>

![android架构图](../image/android-stack_2x.webp)

- Linux 内核  
Android 平台的基础是 Linux 内核。例如，Android Runtime (ART) 依靠 Linux 内核来执行底层功能，例如线程和低层内存管理。
使用 Linux 内核可让 Android 利用主要安全功能，并且允许设备制造商为著名的内核开发硬件驱动程序。

- 硬件抽象层 (HAL)  
硬件抽象层 (HAL) 提供标准界面，向更高级别的 Java API 框架显示设备硬件功能。HAL 包含多个库模块，其中每个模块都为特定类型的硬件组件实现一个界面，例如相机或蓝牙模块。当框架 API 要求访问设备硬件时，Android 系统将为该硬件组件加载库模块。

- Android Runtime  
对于运行 Android 5.0（API 级别 21）或更高版本的设备，每个应用都在其自己的进程中运行，并且有其自己的 Android Runtime (ART) 实例。ART 编写为通过执行 DEX 文件在低内存设备上运行多个虚拟机，DEX 文件是一种专为 Android 设计的字节码格式，经过优化，使用的内存很少。编译工具链（例如 Jack）将 Java 源代码编译为 DEX 字节码，使其可在 Android 平台上运行。   
ART 的部分主要功能包括：  
1、预先 (AOT) 和即时 (JIT) 编译  
2、优化的垃圾回收 (GC)  
3、在 Android 9（API 级别 28）及更高版本的系统中，支持将应用软件包中的 Dalvik Executable 格式 (DEX) 文件转换为更紧凑的机器代码。  
4、更好的调试支持，包括专用采样分析器、详细的诊断异常和崩溃报告，并且能够设置观察点以监控特定字段  
在 Android 版本 5.0（API 级别 21）之前，Dalvik 是 Android Runtime。如果您的应用在 ART 上运行效果很好，那么它应该也可在 Dalvik 上运行，但反过来不一定。
Android 还包含一套核心运行时库，可提供 Java API 框架所使用的 Java 编程语言中的大部分功能，包括一些 Java 8 语言功能。

- 原生 C/C++ 库  
许多核心 Android 系统组件和服务（例如 ART 和 HAL）构建自原生代码，需要以 C 和 C++ 编写的原生库。Android 平台提供 Java 框架 API 以向应用显示其中部分原生库的功能。例如，您可以通过 Android 框架的 Java OpenGL API 访问 OpenGL ES，以支持在应用中绘制和操作 2D 和 3D 图形。
如果开发的是需要 C 或 C++ 代码的应用，可以使用 Android NDK 直接从原生代码访问某些原生平台库。

- Java API 框架  
您可通过以 Java 语言编写的 API 使用 Android OS 的整个功能集。这些 API 形成创建 Android 应用所需的构建块，它们可简化核心模块化系统组件和服务的重复使用，包括以下组件和服务：  
1、丰富、可扩展的视图系统，可用以构建应用的 UI，包括列表、网格、文本框、按钮甚至可嵌入的网络浏览器  
2、资源管理器，用于访问非代码资源，例如本地化的字符串、图形和布局文件  
3、通知管理器，可让所有应用在状态栏中显示自定义提醒  
4、Activity 管理器，用于管理应用的生命周期，提供常见的导航返回栈  
5、内容提供程序，可让应用访问其他应用（例如“联系人”应用）中的数据或者共享其自己的数据  
开发者可以完全访问 Android 系统应用使用的框架 API。

- 系统应用  
Android 随附一套用于电子邮件、短信、日历、互联网浏览和联系人等的核心应用。平台随附的应用与用户可以选择安装的应用一样，没有特殊状态。因此第三方应用可成为用户的默认网络浏览器、短信 Messenger 甚至默认键盘（有一些例外，例如系统的“设置”应用）。
系统应用可用作用户的应用，以及提供开发者可从其自己的应用访问的主要功能。例如，如果您的应用要发短信，您无需自己构建该功能，可以改为调用已安装的短信应用向您指定的接收者发送消息。

**Framework层简介：**

Framework层为Android的应用框架层，主要为上层的应用开发提供服务和API接口，包含了三个主要部分，客户端，服务端和Linux驱动。  
客户端主要包括了Activity，ActivityThread，Window，PhoneWindow，WindowManager，DecorView，ViewRoot，W等类。  
服务端主要有WindowManagerService，ActivityManagerService，管理所有应用程序的窗口和页面。还有KeyQ和InputDispatcherThread类处理消息。  
Linux驱动包含了SurfaceFlingger和Binder，SF驱动的作用是把各个Surface显示在同一屏幕上，Binder驱动的作用是提供跨进程（IPC)的消息传递机制。

</details>


## Activity

> 描述UI，并且处理用户与机器屏幕的交互。

<details><summary>Activity生命周期、启动模式、通信方式</summary>

### Activity生命周期

![activity](../image/activity.webp)

|  回调   |  描述   |
| --- | --- |
|  onCreate()   |  这是第一个回调，在活动第一次创建时调用。   |
|  onStart()   |  这个回调在活动为用户可见时被调用。   |
|  onResume()   |   这个回调在应用程序与用户开始可交互的时候调用。  |
|  onPause()   |  被暂停的活动无法接受用户输入，不能执行任何代码。当前活动将要被暂停，上一个活动将要被恢复时调用。   |
|  onStop()   |  当活动不在可见时调用。   |
|  onDestroy()   |  当活动被系统销毁之前调用。  |
|  onRestart()   |  当活动被停止以后重新打开时调用。  |

### Activity启动模式

- 默认启动模式：<span class="font-red">Standard</span>
- 栈顶复用模式：<span class="font-red">SingleTop</span> 
- 栈内复用模式：<span class="font-red">SingleTask</span>
- 全局唯一模式：<span class="font-red">SingleInstance</span> 

### Activity间通信方式

- Intent、Bundle  
- 类的静态变量  
- 全局变量，如在Application中定义的  
- 外部存储，如SharedPreference、SQLite、文件等  
- Service  

</details>

### Activity启动流程

1、首先startActivity()调用startActivityForResult()  
2、然后通过ActivityManagerProxy 调用 system_server 进程中的 ActivityManagerService中的startActivity()

* 如果应用进程未启动  
* 2.1、 调用Zygote 孵化应用进程  
* 2.2、 进程创建后调用 ActivityThread#main 方法  
* 2.3、 main方法调用 attach 方法将应用进程绑定到ActivityManagerService 中（保存应用的 ApplicationThread 的代理对象）  
* 2.4、 开启loop循环接收消息  

3、ActivityManagerService 通过 ApplicationThread 的代理发送 Message 通知启动 Activity  
4、Activity 内部 Handler 处理 handleLaunchActivity，依次调用 performLaunchActivity，handleResumeActivity（即onCreate, onStart, onResume）

## Fragment

FragmentTransaction的提交方式：

- commit()
- commitAllowingStateLoss()
- commitNow()
- commitNowAllowingStateLoss()

<details>
<summary>四种commit提交的区别</summary>

- **commit()**

安排当前事务FragmentTransaction进行提交。但是提交后Fragment不会立即创建，而是由主线程异步来创建。也就是说使用commit()之后，你的Fragment不会被立即加入到Activity中。

本次提交，必须在Activity的onSaveInstanceState调用之前提交。否则会抛异常。

- **commitAllowingStateLoss()**

和commit类似。但是如果本次是在Activity的onSaveInstanceState调用之后，那么本次提交记录在Activity恢复的时候，可能不被保存。

- **commitNow()**

将事务立即提交。所有添加的Fragment会被立即初始化，并开始生命周期。所有被移除的Fragment将会被立即移除。

调用这个方法，相当于调用commit，然后调用FragmentManager的executePendingTransactions()。

- **commitNowAllowingStateLoss()**

和commitNow类似。但是如果在在Activity的onSaveInstanceState调用之后，那么本次提交记录在Activity恢复的时候，可能不被保存。

<span class="font-red">每个FragmentTransaction只能提交commit一次，再commit就会抛出异常。</span>

commit()和commitAllowingStateLoss()在实现上唯一的不同就是当你调用commit()的时候, FragmentManger会检查是否已经存储了它自己的状态, 如果已经存了, 就抛出IllegalStateException。

如果用commitAllowingStateLoss()在onSaveInstanceState()之后会丢失FragmentManager的状态, 即save之后任何被添加或被移除的Fragments。

</details>


## Service

> 服务是一个后台运行的组件，执行长时间运行且不需要用户交互的任务，即使应用被销毁也依然可以工作。 

<details><summary>Service生命周期、启动方式、优先级、使用场景</summary>

### Service生命周期

![service](../image/service.png)

|  回调   |  描述   |
| --- | --- |
|  onCreate()   |  当服务通过onStartCommand()和onBind()被第一次创建的时候，系统调用该方法。该调用要求执行一次性安装。  |
|  onStartCommand()   |  其他组件(如活动)通过调用startService()来请求启动服务时，系统调用该方法。如果你实现该方法，你有责任在工作完成时通过stopSelf()或者stopService()方法来停止服务。  |
|  onBind()   |  当其他组件想要通过bindService()来绑定服务时，系统调用该方法。如果你实现该方法，你需要返回IBinder对象来提供一个接口，以便客户来与服务通信。你必须实现该方法，如果你不允许绑定，则直接返回null。  |
|  onUnbind()   |  当客户中断所有服务发布的特殊接口时，系统调用该方法。  |
|  onRebind()   |  当新的客户端与服务连接，且此前它已经通过onUnbind(Intent)通知断开连接时，系统调用该方法。  |
|  onDestroy()   |  当服务不再有用或者被销毁时，系统调用该方法。你的服务需要实现该方法来清理任何资源，如线程，已注册的监听器，接收器等。  |

**服务有两种运行状态：**

|  状态   |  描述   |
| --- | --- |
|  Started   |  Android的应用程序组件，如活动，通过startService()启动了服务，则服务是Started状态。一旦启动，服务可以在后台无限期运行，即使启动它的组件已经被销毁。   |
|  Bound  |  当Android的应用程序组件通过bindService()绑定了服务，则服务是Bound状态。Bound状态的服务提供了一个客户服务器接口来允许组件与服务进行交互，如发送请求，获取结果，甚至通过IPC来进行跨进程通信。   |

### Service启动方式

1、startService()只是启动Service（运行在UI线程），启动它的组件（如Activity）和Service没有关联，只有当Service调用自身的stopSelf或者其他组件调用stopService()时，服务才会终止。  
2、bindService()方法启动Service（运行在binder线程），其他的组件可以通过回调获取Service的代理对象和Service进行绑定及交互，当启动的组件销毁时，Service也会自动进行unBind()操作，当所有的绑定组件都进行了unBind()时才会销毁Service。

### Service优先级

1、在AndroidManifest.xml文件中对于intent-filter可以通过android:priority = “1000”这个属性设置最高优先级，1000是最高值，如果数字越小则优先级越低，同时适用于广播。  
2、在onStartCommand()里面调用startForeground()方法把Service提升为前台进程级别，然后再onDestroy()里面要记得调用stopForeground()方法。  
3、onStartCommand方法，手动返回START_STICKY。  
4、在onDestroy()方法里发广播重启service。  
5、监听系统广播判断Service状态。通过系统的一些广播，比如：手机重启、界面唤醒、应用状态改变等等监听并捕获到，然后判断我们的Service是否还存活。  
6、Application加上Persistent属性。  

### onStartCommand返回值

<span class="font-red">START_STICKY：</span>如果service进程被kill掉,保留service的状态为开始状态,但不保留传递的intent对象。随后系统会尝试重新创建service,由于服务状态为开始状态,所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。如果在此期间没有任何启动命令被传递到service,那么参数Intent将为null。   
<span class="font-red">START_NOT_STICKY：</span>使用这个返回值时,如果在执行完onStartCommand后,服务被异常kill掉,系统不会自动重启该服务。  
<span class="font-red">START_REDELIVER_INTENT：</span>重传Intent，使用这个返回值时,如果在执行完onStartCommand后,服务被异常kill掉,系统会自动重启该服务,并将Intent的值传入。  
<span class="font-red">START_STICKY_COMPATIBILITY：</span>START_STICKY的兼容版本,但不保证服务被kill后一定能重启。  

### Service使用场景

Service就是不需要和用户交互，在后台默默执行的任务，比如音乐播放器的播放音乐，网盘的上传下载文件等。

</details>

Service是在主线程（ActivityThread）中调用的，耗时操作会阻塞UI，因此做耗时操作需要开启子线程，或者使用IntentService、JobIntentService。

## BroadcastReceiver

> 广播接收器用于响应来自其他应用程序或者系统的广播消息。这些消息有时被称为事件或者意图，用于处理Android操作系统和应用程序之间的通信。

例如，应用程序可以发送广播来让其他的应用程序知道一些数据已经被下载到设备，并可以为他们所用，这样广播接收器可以定义适当的动作来拦截这些通信。

<details><summary>广播注册方式、广播类型</summary>

### 广播注册方式

#### 静态注册

在清单文件AndroidManifest.xml中注册，只要APP在系统运行中则可以一直收到广播消息。

```xml
<application
   android:icon="@drawable/ic_launcher"
   android:label="@string/app_name"
   android:theme="@style/AppTheme" >
   <receiver android:name="cn.programmer.MyReceiver">
//监听系统启动意图、自定义意图
      <intent-filter>
         <action android:name="android.intent.action.BOOT_COMPLETED">
         </action>
         <action android:name="cn.programmer.CUSTOM_INTENT">
         </action>
      </intent-filter>
   </receiver>
</application>
```

#### 动态注册

在代码中注册，当注册的Activity或Service销毁了则收不到广播消息。  
动态注册的优先级高于静态注册。

```java
// 广播自定义意图
public class Test{
    public static void broadcastIntent(Context context){
        Intent intent = new Intent();
        intent.setAction("cn.programmer.CUSTOM_INTENT");
        intent.setComponent(new ComponentName("cn.programmer","cn.programmer.MyReceiver"));
        context.sendBroadcast(intent);
    }
}
```

**广播接收器**
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

### 广播类型

- 普通广播（Normal Broadcast）：开发者自定义 intent 广播。  
- 系统广播（System Broadcast）：系统内置广播，如开机、网络状态变化。  
- 有序广播（Ordered Broadcast）：发送出去的广播被广播接收者按照先后顺序接收，先接收到广播的接受者可对广播进行修改或者截断，使用sendOrderedBroadcast(intent)。  
- 粘性广播（Sticky Broadcast）：由于在 Android5.0 & API 21 中已经失效，所以不建议使用。  
- App应用内广播（Local Broadcast）：App 应用内广播可理解为一种局部广播，广播的发送者和接收者都同属于一个 App。（将 exported 属性设置为false）  

</details>

BroadcastReceiver中onReceive()方法在10S内没有执行完毕就会被Android系统认为应用程序无响应并弹出ANR对话框，因此BroadcastReceiver里不能做一些比较耗时的操作。

## ContentProvider

> 处理数据和数据库管理方面的问题。

有时候在应用程序之间需要共享数据，这时内容提供者变得非常有用。   
内容提供者组件通过请求从一个应用程序向其他的应用程序提供数据，这些请求由类ContentResolver的方法来处理。内容提供者可以使用不同的方式来存储数据，数据可以被存放在数据库，文件，甚至是网络。

1、内容提供者可以让内容集中，必要时可以有多个不同的应用程序来访问。  
2、内容提供者的行为和数据库很像。你可以查询，编辑它的内容，使用<span class="font-red">insert()，update()，delete()和query()</span>来添加或者删除内容。多数情况下数据被存储在SQLite数据库。  
3、内容提供者被实现为类ContentProvider类的子类。需要实现一系列标准的API，以便其他的应用程序来执行事务。  

<details><summary>ContentProvider内容URI、创建内容提供者</summary>
<br>

![content](../image/content.jpg) 

### 内容URI

```
<prefix>://<authority>/<data_type>/<id>
```

|  部分   |  说明  |
| --- | --- |
|  prefix   |  前缀：一直被设置为content://   |
|  authority   |  授权：指定内容提供者的名称，例如联系人，浏览器等。第三方的内容提供者可以是全名，如：cn.programmer.statusprovider   |
|  data_type   |  数据类型：这个表明这个特殊的内容提供者中的数据的类型。例如：你要通过内容提供者Contacts来获取所有的通讯录，数据路径是people，那么URI将是下面这样：content://contacts/people   |
|  id   |  这个指定特定的请求记录。例如：你在内容提供者Contacts中查找联系人的ID号为5，那么URI看起来是这样：content://contacts/people/5   |

### 创建内容提供者

这里描述创建自己的内容提供者的简单步骤。

1、首先，你需要继承类ContentProvider来创建一个内容提供者类。  
2、其次，你需要定义用于访问内容的你的内容提供者URI地址。  
3、接下来，你需要创建数据库来保存内容。通常Android使用SQLite数据库，并在框架中重写onCreate()方法来使用SQLiteOpenHelper的方法创建或者打开提供者的数据库。当你的应用程序被启动，它的每个内容提供者的onCreate()方法将在应用程序主线程中被调用。  
4、最后，在AndroidManifest.xml中注册内容提供者。  

以下是让你的内容提供者正常工作，你需要在类ContentProvider中重写的一些方法：

|  部分   |  说明  |
| --- | --- |
|  onCreate()   |  当提供者被启动时调用   |
|  query()   |  该方法从客户端接受请求，结果是返回指针(Cursor)对象   |
|  insert()   |  该方法向内容提供者插入新的记录   |
|  delete()   |  该方法从内容提供者中删除已存在的记录   |
|  update()   |  该方法更新内容提供者中已存在的记录   |
|  getType()   |  该方法为给定的URI返回元数据类型   |

</details>

## 四大组件和Context

<details>
<summary>Context继承关系图</summary>

![context](../image/android_context.png)

</details>

- Activity跟Service都继承自Context，都有生命周期，区别是Activity包含Theme信息，启动的Activity带有任务栈的信息，而BroadcastReceiver与ContentProvider采用监听机制，没有生命周期。
- ContentProvider的Context就是Application。
- BroadcastReceiver的Context比较特殊，是传进来的，类型是ReceiverRestrictedContext，也就是说进行了一些限制，不能bindService,也不能registerReceiver。

Context 就相当于 Application 的大管家，主要负责：
- 四大组件的交互，包括启动 Activity、Broadcast、Service，获取 ContentResolver 等。
- 获取系统/应用资源，包括 AssetManager、PackageManager、Resources、System Service 以及 color、string、drawable 等。
- 文件，包括获取缓存文件夹、删除文件、SharedPreference 相关等。
- 数据库（SQLite）相关，包括打开数据库、删除数据库、获取数据库路径等。
- 其它辅助功能，比如设置 ComponentCallbacks，即监听配置信息改变、内存不足等事件的发生。

<span class="font-red">ContextWrapper</span> 实际上就是 Context 的代理类而已，所有的操作都是通过内部成员 mBase 完成的，另外，Activity、Service 的 getBaseContext 返回的就是这个 mBase。

相比 ContextWrapper，<span class="font-red">ContextThemeWrapper</span> 有自己的另外 Resource 以及 Theme 成员，并且可以传入配置信息以初始化自己的 Resource 及 Theme。即 Resource 以及 Theme 相关的行为不再是直接调用 mBase 的方法了，也就说，ContextThemeWrapper 和它的 mBase 成员在 Resource 以及 Theme 相关的行为上是不同的。

<span class="font-red">ContextImpl</span> 和 ContextThemeWrapper 最大的区别就是没有一个 Configuration 而已，其它的行为大致是一样的。另外，ContextImpl 可以用于创建 Activity、Service 以及 Application 的 mBase 成员，这个 Base Context 除了参数不同，它们的 Resource 也不同。需要注意的是，createActivityContext 等方法中 setResource 是 mBase 自己调用的，Activity、Service 以及 Application 本身并没有执行 setResource。

**小结**

- ContextWrapper、ContextThemeWrapper 都是 Context 的代理类，二者的区别在于 ContextThemeWrapper 有自己的 Theme 以及 Resource，并且 Resource 可以传入自己的配置初始化
- ContextImpl 是 Context 的主要实现类，Activity、Service 和 Application 的 Base Context 都是由它创建的，即 ContextWrapper 代理的就是 ContextImpl 对象本身
- ContextImpl 和 ContextThemeWrapper 的主要区别是， ContextThemeWrapper 有 Configuration 对象，Resource 可以根据这个对象来初始化
- Service 和 Application 使用同一个 Resource，和 Activity 使用的 Resource 不同

**重点**

在Service中调用startActivity和在BroadcastReceiver（静态注册）中通过onReceive传递过来的context.startActivity时（该context类型为ReceiverRestrictedContext，和Service一样，都没有重写startActivity），如果不加FLAG_ACTIVITY_NEW_TASK的话会报错，提示没有添加FLAG_ACTIVITY_NEW_TASK这个flag。

在Activity中不加FLAG_ACTIVITY_NEW_TASK调用startActivity时不会报错是因为Activity重写了ContextWrapper中的startActivity方法，没有加FLAG_ACTIVITY_NEW_TASK判断。

<span class="font-red">NEW_TASK</span>这个属性它的原则是：设置此状态，首先会查找是否存在和被启动的Activity具有相同的亲和性的任务栈（即taskAffinity，注意同一个应用程序中的activity的亲和性一样），如果有，则直接把这个栈整体移动到前台，并保持栈中的状态不变，即栈中的activity顺序不变，如果没有，则新建一个栈来存放被启动的activity。总结来说就是<span class="font-blue">同一个应用中跳转不会创建新的Task，跳到另外一个应用中会启动新的Task。</span>

## Animation

### 帧动画

> 最容易实现的一种动画(AnimationDrawable)，将一张张图片连贯起来播放。

### 补间动画

- 平移动画（TranslateAnimation）
- 缩放动画（ScaleAnimation）
- 旋转动画（RotateAnimation）
- 透明度动画（AlphaAnimation）

### 属性动画

> 基于对象属性的动画，所有补间动画都可以用属性动画实现。

Android 3.0（API 11）后才提供的一种全新动画模式，出现原因是因为补间动画作用对象局限于View，没有改变View的属性，只是改变视觉效果，动画效果单一。

<span class="font-red">ValueAnimator、ObjectAnimator</span>是属性动画重要的两个类。  

<details><summary>ValueAnimator、ObjectAnimator详解</summary>

#### ValueAnimator

ValueAnimator可以设置开始值和结束值来动态改变view的移动位置，它有ofInt、ofFloat、ofObject三个重要的方法，方法的作用：
- 创建动画实例
- 将传入的多个参数进行平滑过渡: 此处传入0和1，表示将值从0平滑过渡到1，如果传入了3个Int参数a,b,c ,则是先从a平滑过渡到b,再从b平滑过渡到c，以此类推，其内置了估值器。

#### ObjectAnimator

ObjectAnimator功能更加强大，可以控制位移、透明度、旋转、缩放。

##### 插值器(Interpolator)

插值器决定值的变化模式，默认的种类有九个：

- AccelerateDecelerateInterpolator ：在动画开始与结束的地方速率改变比较慢，在中间的时候加速  
- AccelerateInterpolator：在动画开始的地方速率改变比较慢，然后开始速率变化加快  
- <span class="font-blue">LinearInterpolator：</span>以常量速率改变  
- AnticipateInterpolator：开始的时候向后然后向前甩  
- <span class="font-blue">CycleInterpolator：</span>动画循环播放特定的次数，速率改变沿着正弦曲线  
- <span class="font-blue">PathInterpolator：</span>动画执行的效果按贝塞尔曲线  
- anticipateOvershootInterpolator：开始的时候向后然后向前甩一定值后返回最后的值  
- OvershootInterpolator：向前甩一定值后再回到原来位置  
- BounceInterpolator：动画结束的时候有弹起效果  

**自定义插值器：**  
写一个类实现Interpolator接口，Interpolator是一个空的接口继承了TimeInterpolator接口，定义getInterpolation方法即可。

##### 估值器(TypeEvaluator)

估值器决定值的具体变化数值。

</details>

### 补间动画和属性动画的区别

- 补间动画只是绘制了一个不同的影子，view对象还在原来的位置。  
比如位移后点击原来的位置会响应点击事件，旋转后再次旋转会从头开始重新旋转。
- 而<span class="font-red">属性动画则是真正的视图移动</span>，例如点击移动后的视图会响应点击事件。

### Android属性动画原理

- Android属性动画通过监听帧刷新事件，在下一帧开始绘制前更新属性值；
- Android帧刷新统一由Choreographer指挥，在每一帧到来时，依次处理input、animation、traversal和commit事件，属性动画在animation阶段进行属性值更新；
- Android属性更新分为两阶段，第一阶段：将动画已播放时长百分比通过时间插值器转换为动画完成百分比（即属性变化的百分比）；第二阶段：将属性变化的百分比通过类型估值器映射为特定的属性值；
- Android界面绘制由两种模型：软件绘制和硬件加速绘制，软件绘制时，主线程负责执行绘制工作；硬件加速时，主线程将要绘制的内容交给渲染线程，由渲染线程执行绘制的工作。针对特定属性动画时，开启硬件加速可以提升动画流畅性。

<span class="font-red">内存泄漏：</span>

属性动画开始时，向AnimationHandler注册自身作为帧刷新回调接口，结束时才会解除注册，如果view销毁时，动画没有结束，AnimationHandler会一直持有该对象，造成内存泄漏问题。

## 数据存储

Android中数据存储：SharedPreferences、文件存储、SQLite数据库、ContentProvider、网络存储

> [https://www.shuzhiduo.com/A/Vx5MGGamzN/](https://www.shuzhiduo.com/A/Vx5MGGamzN/)