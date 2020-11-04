# Service
> 服务是一个后台运行的组件，执行长时间运行且不需要用户交互的任务，即使应用被销毁也依然可以工作。  

## Service生命周期

![service](https://img.upyun.zzming.cn/android/service.png)

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

## Service启动方式

1、startService()只是启动Service，启动它的组件（如Activity）和Service没有关联，只有当Service调用自身的stopSelf或者其他组件调用stopService()时，服务才会终止。  
2、bindService()方法启动Service，其他的组件可以通过回调获取Service的代理对象和Service进行绑定及交互，当启动的组件销毁时，Service也会自动进行unBind()操作，当所有的绑定组件都进行了unBind()时才会销毁Service。

## Service优先级

1、在AndroidManifest.xml文件中对于intent-filter可以通过android:priority = “1000”这个属性设置最高优先级，1000是最高值，如果数字越小则优先级越低，同时适用于广播。  
2、在onStartCommand()里面调用startForeground()方法把Service提升为前台进程级别，然后再onDestroy()里面要记得调用stopForeground()方法。  
3、onStartCommand方法，手动返回START_STICKY。  
4、在onDestroy()方法里发广播重启service。  
5、监听系统广播判断Service状态。通过系统的一些广播，比如：手机重启、界面唤醒、应用状态改变等等监听并捕获到，然后判断我们的Service是否还存活。  
6、Application加上Persistent属性。  

## onStartCommand返回值

**START_STICKY**：如果service进程被kill掉,保留service的状态为开始状态,但不保留传递的intent对象。随后系统会尝试重新创建service,由于服务状态为开始状态,所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。如果在此期间没有任何启动命令被传递到service,那么参数Intent将为null。   
**START_NOT_STICKY**：使用这个返回值时,如果在执行完onStartCommand后,服务被异常kill掉,系统不会自动重启该服务。  
**START_REDELIVER_INTENT**：重传Intent，使用这个返回值时,如果在执行完onStartCommand后,服务被异常kill掉,系统会自动重启该服务,并将Intent的值传入。  
**START_STICKY_COMPATIBILITY**：START_STICKY的兼容版本,但不保证服务被kill后一定能重启。  

## Service使用场景

Service就是不需要和用户交互，在后台默默执行的任务，比如音乐播放器的播放音乐，网盘的上传下载文件等。

**Service是在主线程（ActivityThread）中调用的，耗时操作会阻塞UI，因此做耗时操作需要开启子线程，或者使用IntentService、JobIntentService。**