# Lifecycle

在 ComponentActivity 创建的时候，同时创建了一个ReportFragment，这个Fragment的作用就是用来分发生命周期状态的。  
(目的是为了兼顾不是继承自AppCompactActivity的场景)

监听过程就是Activity/Fragment继承LifecycleOwner，并在子类ComponentActivity中创建Lifecycle的子类LifecycleRegistry。在复写getLifecycle()的方法中将子类LifecycleRegistry返回。

在onCreate()中注入ReportFragment，在生命周期回调后，通过getLifecycle()的方法得到LifecycleRegistry对象中的handleLifecycleEvent(event)方法给每个观察者派发生命周期事件。

[Lifecycle，看完这次就真的懂了](https://mp.weixin.qq.com/s/ffeN9LrxcZObLr01gtw3HA)