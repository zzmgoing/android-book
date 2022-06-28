# ViewModel

> ViewModel旨在以生命周期意识的方式存储和管理用户界面相关的数据，它可以用来管理Activity和Fragment中的数据，还可以拿来处理Fragment与Fragment之间的通信等，并且让数据可在发生屏幕旋转等配置更改后继续留存。


ViewModel在UI整个生命周期范围内都是同一个状态内的，当UI销毁的时候才会执行onCleard()操作，清除数据。

实例化ViewModelProvider，通过ViewModelProvider::get 方法来获取ViewModel 的实例对象，通过这种创建形式，不难看出，ViewModel 的创建是通过反射来创建的。

```java
    @MainThread
    public open operator fun <T : ViewModel> get(key: String, modelClass: Class<T>): T {
        var viewModel = store[key]
        if (modelClass.isInstance(viewModel)) {
            (factory as? OnRequeryFactory)?.onRequery(viewModel)
            return viewModel as T
        } else {
           ........
        }
        viewModel = if (factory is KeyedFactory) {
            factory.create(key, modelClass)
        } else {
            factory.create(modelClass)
        }
        store.put(key, viewModel)
        return viewModel
    }
```

根据key值从store中获取ViewModel 对象,如果 类型正确 ,当即返回当前对象, 如果不正确的话,通过工厂创建新的ViewModel对象，存储到store中并返回。

- 我们的Activity 的父类 ComponentActivity 实现了 ViewModelStoreOwner 接口，通过 ViewModelProvider 使用默认工厂 创建了 viewModel ，并通过唯一Key值 进行标识，存储到了 ViewModelStore 中。等下次需要的时候即可通过唯一Key值进行获取。

- 由于ComponentActivity 实现了ViewModelStoreOwner 接口，实现了 getViewModelStore 方法，当屏幕旋转的时候，会先调用
onRetainNonConfigurationInstance() 方法将 viewModelStore 保存起来，然后再调用 getLastNonConfigurationInstance 方法将数据恢复，如果为空的话，会重新创建 viewModelStore ，并存储在全局中，以便以下次发生变化的时候，能够通过onRetainNonConfigurationInstance 保存起来。

- 最后当页面销毁并且没有配置更改的时候，会将viewModelStore 中的数据 进行清除操作。
