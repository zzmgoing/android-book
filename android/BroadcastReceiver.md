# BroadcastReceiver  
> 广播接收器用于响应来自其他应用程序或者系统的广播消息。这些消息有时被称为事件或者意图，用于处理Android操作系统和应用程序之间的通信。

例如，应用程序可以发送广播来让其他的应用程序知道一些数据已经被下载到设备，并可以为他们所用，这样广播接收器可以定义适当的动作来拦截这些通信。

## 广播注册方式

### 静态注册

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

### 动态注册

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

## 广播类型

**普通广播（Normal Broadcast）**：开发者自定义 intent 广播。  
**系统广播（System Broadcast）**：系统内置广播，如开机、网络状态变化。  
**有序广播（Ordered Broadcast）**：发送出去的广播被广播接收者按照先后顺序接收，先接收到广播的接受者可对广播进行修改或者截断，使用sendOrderedBroadcast(intent)。  
**粘性广播（Sticky Broadcast）**：由于在 Android5.0 & API 21 中已经失效，所以不建议使用。  
**App应用内广播（Local Broadcast）**：App 应用内广播可理解为一种局部广播，广播的发送者和接收者都同属于一个 App。（将 exported 属性设置为false）  

**BroadcastReceiver中onReceive()方法在10S内没有执行完毕就会被Android系统认为应用程序无响应并弹出ANR对话框，因此BroadcastReceiver里不能做一些比较耗时的操作。**
