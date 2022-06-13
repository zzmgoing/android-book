# Context理解

Context是维持Android程序中各组件能够正常工作的一个核心功能类，提供了关于应用环境全局信息的接口。

Context是一个抽象类，描述一个应用程序环境的信息（也就是上下文），Android提供了Context的具体实现类ContextImpl；通过它我们可以获取应用程序的资源和类。

主要功能有：

1. 四大组件的交互，包括启动 Activity、Broadcast、Service，获取 ContentResolver 等。
2. 获取系统/应用资源，包括 AssetManager、PackageManager、Resources、System Service 以及 color、string、drawable 等。
3. 文件，包括获取缓存文件夹、删除文件、SharedPreference 相关等。
4. 数据库（SQLite）相关，包括打开数据库、删除数据库、获取数据库路径等。

Application、Service都继承Context的代理类ContextWrapper，其中Activity继承的是ContextThemeWrapper。

## ContextWrapper
ContextWrapper继承Context抽象类，作为Context类的包装类，其内部维护了一个Context类型的成员变量mBase，mBase最终会指向一个ContextImpl对象，ContextWrapper的方法其内部依赖mBase，ContextWrapper是Context类的修饰类(装饰器模式)，真正的实现类是 ContextImpl，ContextWrapper 里面的方法调用也是调用 ContextImpl 里面的方法。

## ContextThemeWrapper
ContextThemeWrapper继承ContextWrapper，因此也拥有一个Context类型的成员变量mBase，mBase最终会指向一个ContextImpl对象，它的一个直接子类就是 Activity，所以Activity也就拥有了Context提供的所有功能。

相比 ContextWrapper，ContextThemeWrapper 有自己的另外 Resource 以及 Theme 成员，并且可以传入配置信息以初始化自己的 Resource 及 Theme。即 Resource 以及 Theme 相关的行为不再是直接调用 mBase 的方法了，也就说，ContextThemeWrapper 和它的 mBase 成员在 Resource 以及 Theme 相关的行为上是不同的。

## 应用中Context的数量

一般是Application + Activity + Service的数量

## ContextImpl实例什么时候生成

ContextImpl实例生成对应着mBase的赋值过程：

在启动Activity时，在ActivityThread内部通过handleLaunchActivity()方法一系列调用，在通过Instrucmentation创建完Activity后，会先调用Activity的attach()方法，会传入已创建好的ContextImpl对象，在Attach()方法内部会先调用attachBaseContext(context)方法，会将ContextImpl通过super.attachBaseContext(context)一步一步最后赋值给ContextWrapper的mBase，接下来再调用activity的onCreate()。

## ContentProvider里的Context初始化

ContentProvider本身不是Context ，但是它有一个成员变量 mContext ，是通过构造函数传入的。mContext初始化对应着ContentProvider创建时机。

应用创建Application是通过调用 ActivityThread.handleBindApplication方法，这个方法的相关流程有：
- 创建 Application
- 初始化 Application的Context
- 调用installContentProviders()并传入刚创建好的context来创建ContentProvider
- 调用Application.onCreate()
- ContentProvider的Context是在Application创建之后，调用onCreate方法调用之前初始化的。

## 四大组件Context的区别

- Activity跟Service都继承自Context，区别是Activity包含Theme信息，启动的Activity带有任务栈的信息。
- ContentProvider的Context就是Application。
- Broadcast Receiver的Context比较特殊，是传进来的，类型是ReceiverRestrictedContext，也就是说进行了一些限制，不能bindService,也不能registerReceiver。

## 非Activity的Context启动Activity报错

Intent需要添加FLAG_ACTIVITY_NEW_TASK这个Flag。

因为非Activity的Context启动的话需要找到当前的任务栈，添加了NEW_TASK这个标记的话，首先会查找是否存在和被启动的Activity具有相同的亲和性的任务栈（即taskAffinity，注意同一个应用程序中的activity的亲和性一样），如果有，则直接把这个栈整体移动到前台，并保持栈中的状态不变，即栈中的activity顺序不变，如果没有，则新建一个栈来存放被启动的activity。总结来说就是同一个应用中跳转不会创建新的Task，跳到另外一个应用中会启动新的Task。
