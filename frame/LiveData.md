# LiveData

> LiveData是一个可被观察的数据容器类，它将数据包装起来，使数据成为被观察者。当数据发生改变时，观察者能够及时得到通知。又是一个典型的观察者模式！

通过observe方法进行订阅绑定，只有在主线程、且非DESTROYED状态下，才会进行绑定。将观察者、被观察者统一封装到LifecycleBoundObserver对象中，并将Observer对象、LifecycleBoundObserver对象封装到ObserverWrapper对象中。最后调用LifeCycle的addObserver方法，走的还是LifeCycle的绑定流程！


```java
@MainThread
public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
  	//判断是否主线程，否则就抛出异常
    assertMainThread("observe");
  	//判断状态，如果DESTORYED就return不管。因为此时Activity没用，根本没必要管
  	//Activity能用，才会继续往下走
    if (owner.getLifecycle().getCurrentState() == DESTROYED) {
        // ignore
        return;
    }
  	//将观察者、被观察者统一封装到LifecycleBoundObserver中
    LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
  	//将Observer、LifecycleBoundObserver对象包装放到Entry<Observer,LifecycleBoundObserver对象>中
  	//先get(key)查，有就return返回。没有再put
    ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
    if (existing != null && !existing.isAttachedTo(owner)) {
        throw new IllegalArgumentException("Cannot add the same observer"
                + " with different lifecycles");
    }
    if (existing != null) {
        return;
    }
  	//调Lifecycle的addObserver方法，用的还是Lifecycle的绑定流程
    owner.getLifecycle().addObserver(wrapper);
}
```

调用LifeData的setValue或postValue方法进行数据更新。

实际上LiveData传递数据的方法也是通过Handler。在postValue()方法中，LiveData调用ArchTaskExecutor.postToMainThread()，将一个Runnable对象传递给主线程。这个Runnable对象调用setValue()对数据进行更新。

更新数据的时候，LiveData首先将版本号加1，然后遍历观察者。如果观察者处于活跃状态（Lifecycle.State是STARTED或RESUMED），并且观察者的版本号小于LiveData，LiveData会调用观察者的onChanged()方法进行通知。

## 粘性事件

因为在通过observe注册的时候，Lifecycle.State进行了更新，使得能和宿主LifeCycleOwner生命周期一致，然后会调用activeStateChanged方法，
最终还是会调用dispatchingValue方法，但是此时传入的参数并不是null，而是ObserverWrapper自己本身，observer.mLastVersion是初始值为-1, 但是mVersion由于刚才设置了一次，所以mVersion现在的值为0，会进入数据的回调阶段。
