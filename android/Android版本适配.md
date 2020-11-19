# Android版本适配

>https://developer.android.com/preview

## 6.0

**隐私权限申请**

```java
ActivityCompat.requestPermissions(this, permissions, 1000);

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    
}
```

Android 6.0中，还有两个特殊权限的处理：

1、设置悬浮窗： SYSTEM_ALERT_WINDOW

2、修改系统设置：WRITE_SETTINGS  
<span>修改系统设置权限的授权 WRITE_SETTINGS，与其他权限不同，而是使用startActivityForResult，启动系统设置的授权界面来申请。</span>

```java

if (!Settings.System.canWrite(this)) {
    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
    intent.setData(Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
}

```

## 7.0

**获取文件Uri需配置FileProvider**

**file_paths.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <!--context.getFileDir()  /data/data/包名/files-->
    <files-path
        name="files-path"
        path="" />
    <!--context.getCacheDir()  /data/data/包名/cache-->
    <cache-path
        name="cache-path"
        path="" />
    <!--Environment.getExternalStorageDirectory()  /storage/emulated/0 -->
    <external-path
        name="external-path"
        path="" />
    <!--context.getExternalFilesDirs() /storage/emulated/0/Android/data/包名/files-->
    <external-files-path
        name="external-files-path"
        path="" />
    <!--context.getExternalCacheDirs() /storage/emulated/0/Android/data/包名/cache-->
    <external-cache-path
        name="external-cache-path"
        path="" />
</paths>
```
**AndroidManifest.xml**
```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths"/>
</provider>
```
**使用：**
```java
Uri uri;
if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
    uri = Uri.fromFile(file);
} else {
    String authority = getPackageName() + ".provider";
    uri = FileProvider.getUriForFile(this, authority, file);
}
```

## 8.0

**一、通知栏**

Android 8.0 引入了通知渠道，其允许您为要显示的每种通知类型创建用户可自定义的渠道。用户界面将通知渠道称之为通知类别。  
针对 8.0 的应用，创建通知前需要创建渠道，创建通知时需要传入 channelId，否则通知将不会显示。

```java
// 创建通知渠道
private void initNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        CharSequence name = mContext.getString(R.string.app_name);
        NotificationChannel channel = new NotificationChannel(mChannelId, name, NotificationManager.IMPORTANCE_DEFAULT);
        mNotificationManager.createNotificationChannel(channel);
    }
}
// 创建通知传入channelId
NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationBarManager.getInstance().getChannelId());
```

**二、后台服务限制**

如果针对 Android 8.0 的应用尝试在不允许其创建后台服务的情况下使用 startService() 函数，则该函数将引发一个 IllegalStateException。

我们无法得知系统如何判断是否允许应用创建后台服务，所以我们目前只能简单 try-catch startService()，保证应用不会 crash。  
或者：  
系统不允许后台应用创建后台服务， Android 8.0 引入了一种全新的方法,即 Context.startForegroundService(),以在前台启动新服务。将原来 startService方式启动服务修改为 startForegroundService启动服务。

```java
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
     context.startForegroundService(checkIntent);
} else {
     context.startService(checkIntent);
}
```

程序有通知的情况下：

在系统创建服务后,应用有五秒的时间来调用该服务的startForeground()方法以显示新服务的用户可见通知(如果应用在此时间限制内未调用 startForeground(),则系统将停止服务并声明此应用为 ANR)，在服务的onCreate()方法中调用startForeground()即可。

```java
@Override
public void onCreate() {
　　super.onCreate();
　　if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
　　　　createNotificationChannel();
　　　　Notification notification = new Notification.Builder(getApplicationContext(), channelID).build();
　　　　startForeground(1, notification);
　　}
}
```

**三、允许安装未知来源应用**

针对 8.0 的应用需要在 AndroidManifest.xml 中声明 REQUEST_INSTALL_PACKAGES 权限，否则将无法进行应用内升级。

```xml
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
```

**四、 隐式广播**

>由于 Android 8.0 引入了新的广播接收器限制，因此您应该移除所有为隐式广播 Intent 注册的广播接收器。将它们留在原位并不会在构建时或运行时令应用失效，但当应用运行在 Android 8.0 上时它们不起任何作用。  
显式广播 Intent（只有您的应用可以响应的 Intent）在 Android 8.0 上仍以相同方式工作。
这个新增限制有一些例外情况。如需查看在以 Android 8.0 为目标平台的应用中仍然有效的隐式广播的列表，请参阅隐式广播例外。

需要检查应用静态注册的隐式广播，需要改为动态注册。

**五、桌面图标适配**

针对 8.0 的应用如果不适配桌面图标，则应用图标在 Launcher 中将会被添加白色背景。

## 9.0

**一、禁止Http请求**

在9.0中默认情况下启用网络传输层安全协议 (TLS)，默认情况下已停用明文支持。也就是不允许使用http请求，要求使用https。

**network_security_config.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```
**AndroidManifest.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest>
    <application 
        android:networkSecurityConfig="@xml/network_security_config">
    </application>
</manifest>
```

**二、Apache HTTP 客户端弃用**

在 Android 6.0 时，就已经取消了对 Apache HTTP 客户端的支持。 从 Android 9.0 开始，默认情况下该库已从 bootclasspath 中移除。

所以要想继续使用Apache HTTP，需要在应用的 AndroidManifest.xml 文件中添加：

```xml
<uses-library android:name="org.apache.http.legacy" android:required="false"/>
```

**三、前台服务需要权限**

9.0 要求创建一个前台服务需要请求 FOREGROUND_SERVICE 权限，否则系统会引发 SecurityException。

解决方法就是AndroidManifest.xml中添加FOREGROUND_SERVICE权限：

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

**四、启动Activity限制**

在9.0 中，不能直接非 Activity 环境中（比如Service，Application）启动 Activity，否则会崩溃报错。

解决方法就是 Intent 中添加标志FLAG_ACTIVITY_NEW_TASK。

```java
Intent intent = new Intent(this, TestActivity.class);
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
startActivity(intent);
```

**五、刘海屏、水滴屏适配**

在Android 9.0中官方提供了DisplayCutout 类，可以确定刘海区域的位置，国内的部分厂商在8.0就有了自己的适配方案。

**六、权限**

在9.0 中新增权限组CALL_LOG 并将 READ_CALL_LOG、WRITE_CALL_LOG 和 PROCESS_OUTGOING_CALLS 权限从PHONE中移入该组。

**限制访问通话记录**，如果应用需要访问通话记录或者需要处理去电，则您必须向 CALL_LOG权限组明确请求这些权限。 否则会发生 SecurityException。

**限制访问电话号码**，要通过 PHONE_STATE Intent 操作读取电话号码，同时需要 READ_CALL_LOG 权限和 READ_PHONE_STATE 权限。  
要从 PhoneStateListener的onCallStateChanged() 中读取电话号码，只需要 READ_CALL_LOG 权限。 不需要 READ_PHONE_STATE 权限。


## 10.0

**一、存储内容分区**

Android Q 在外部存储设备中为每个应用提供了一个“隔离存储沙盒”（例如 /sdcard）。任何其他应用都无法直接访问您应用的沙盒文件。由于文件是您应用的私有文件，因此您不再需要任何权限即可在外部存储设备中访问和保存自己的文件。此变更可让您更轻松地保证用户文件的隐私性，并有助于减少应用所需的权限数量。

1、特定目录（App-specific），使用getExternalFilesDir()或 getExternalCacheDir()方法访问。无需权限，且卸载应用时会自动删除。  
<span>应用在卸载后，会将App-specific目录下的数据删除，如果在AndroidManifest.xml中声明：android:hasFragileUserData="true"用户可以选择是否保留。</span>

2、照片、视频、音频这类媒体文件。使用MediaStore 访问，访问其他应用的媒体文件时需要READ_EXTERNAL_STORAGE权限。

3、其他目录，使用[存储访问框架SAF（Storage Access Framwork）](https://developer.android.google.cn/guide/topics/providers/document-provider?hl=zh_cn)

**二、用户的定位权限**

在后台运行时访问设备位置信息需要权限ACCESS_BACKGROUND_LOCATION。

```xml
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>
```
该权限允许应用程序在后台访问位置。如果请求此权限，则还必须请求ACCESS_FINE_LOCATION 或 ACCESS_COARSE_LOCATION权限。只请求此权限无效果。

**三、设备唯一标识符**

从 Android Q 开始，应用必须具有 READ_PRIVILEGED_PHONE_STATE 签名权限才能访问设备的不可重置标识符（包含 IMEI 和序列号）。


>https://www.cnblogs.com/candyzhmm/p/11242938.html  
>http://www.cocoachina.com/articles/29242  
>https://blog.csdn.net/u011174639/article/details/105658718