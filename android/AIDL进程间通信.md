# AIDL进程间通信

>AIDL（Android Interface Definition Language）是Android系统自定义的接口描述语言，可以用来实现进程间的通讯。

**AIDL支持的数据类型**  
1、八种基本数据类型：byte、char、short、int、long、float、double、boolean  
2、String，CharSequence  
3、实现了Parcelable接口的数据类型  
4、ArrayList和HashMap类型(承载的数据必须是AIDL支持的类型，或者是其它声明的AIDL对象)  

**服务端**  
1、创建.aidl文件（定义编程接口和方法）。  
2、实现接口，Android SDK工具基于.aidl文件生成接口文件。这个接口有一个名叫Stub的内部抽象类，Stub扩展了Binder并实现了AIDL接口中声明的方法。  
3、暴露接口给客户端，实现一个Service重写onBind()，onBind()返回实现了Stub的类。

```java
package com.demo.aidl;

interface IRemoteService{

    void sendMessage(String str);

    void registerCallBack(CallBack callback);

}
```

```java
public class AIDLService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IRemoteService.Stub mBinder = new IRemoteService.Stub(){
		public void sendMessage(String str){
			Message msg = new Message();
			msg.what = MSG_RECEIVE_MESSAGE;
			msg.obj = str;
			mHandler.sendMessage(msg);
		}
	    
	    public void registerCallBack(CallBack callback){
	    	mService.registerCallBack(callback);
	    }
	};
}
```

**客户端**  
1、将服务端aidl文件和相关实体类拷贝过来（文件目录应该与服务端相同，包名应该一样）。  
2、编译后通过bindService使用ServiceConnection来获取binder对象，即可调用服务端方法。  
```java
public class Activity{
    public void onCreate(){
        Intent intent = new Intent(this, AIDLService.class);
        intent.setComponent(new ComponentName("com.demo.aidl", "com.demo.aidl.AIDLService"));
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtil.d("onServiceConnected");
                remoteService = IRemoteService.Stub.asInterface(service);
            }
        
            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogUtil.d("onServiceDisconnected");
                remoteService = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }
}
```