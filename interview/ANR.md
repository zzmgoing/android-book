# ANR

> 在 Android 上，如果你的应用程序有一段时间响应不够灵敏，系统会向用户显示一个对话框，这个对话框称作应用程序无响应（ANR：Application NotResponding）对话框。 用户可以选择让程序继续运行，但是，他们在使用你的应用程序时，并不希望每次都要处理这个对话框。因此 ，在程序里对响应性能的设计很重要这样，这样系统就不会显示 ANR 给用户。

不同的组件发生 ANR 的时间不一样，Activity 是 5 秒，BroadCastReceiver 是 10 秒，Service 是 20 秒（均为前台）。

如果开发机器上出现问题，我们可以通过查看`/data/anr/traces.txt`即可，最新的 ANR 信息在最开始部分。

**ANR出现的原因**

- 主线程被 IO 操作（从 4.0 之后网络 IO 不允许在主线程中）阻塞。 
- 主线程中存在耗时的计算 
- 主线程中错误的操作，比如 Thread.wait 或者 Thread.sleep 等 Android 系 统会监控程序的响应状况，一旦出现下面两种情况，则弹出 ANR 对话框
- 应用在 5 秒内未响应用户的输入事件（如按键或者触摸） 
- BroadcastReceiver 未在 10 秒内完成相关的处理 
- Service 在特定的时间内无法处理完成 20 秒

**解决方案**

- 使用 AsyncTask 处理耗时 IO 操作。 
- 使用 Thread 或者 HandlerThread 时，调用 Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)设置优先级，否则仍然会降低程序响应，因为默认 Thread 的优先级和主线程相同。 
- 使用 Handler 处理工作线程结果，而不是使用 Thread.wait()或者 Thread.sleep() 来阻塞主线程。 
- Activity 的 onCreate 和 onResume 回调中尽量避免耗时的代码。 BroadcastReceiver 中 onReceive 代码也要尽量减少耗时，建议使用 IntentService 处理。

## 如何避免ANR

将所有耗时操作，比如访问网络，Socket 通信，查询大量 SQL 语句，复杂逻辑计算等都放在子线程中去，然后通过 handler.sendMessage、runonUIThread、 AsyncTask、RxJava 等方式更新 UI。无论如何都要确保用户界面的流畅度。如果耗时操作需要让用户等待，那么可以在界面上显示度条。


