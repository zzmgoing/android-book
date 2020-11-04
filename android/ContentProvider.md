# ContentProvider
> 处理数据和数据库管理方面的问题。  

有时候在应用程序之间需要共享数据，这时内容提供者变得非常有用。   
内容提供者组件通过请求从一个应用程序向其他的应用程序提供数据，这些请求由类ContentResolver的方法来处理。内容提供者可以使用不同的方式来存储数据，数据可以被存放在数据库，文件，甚至是网络。

1、内容提供者可以让内容集中，必要时可以有多个不同的应用程序来访问。  
2、内容提供者的行为和数据库很像。你可以查询，编辑它的内容，使用insert()，update()，delete()和query()来添加或者删除内容。多数情况下数据被存储在SQLite数据库。  
3、内容提供者被实现为类**ContentProvider**类的子类。需要实现一系列标准的API，以便其他的应用程序来执行事务。  

![content](https://img.upyun.zzming.cn/android/content.jpg) 

#### 内容URI {docsify-ignore}
```
<prefix>://<authority>/<data_type>/<id>
```
|  部分   |  说明  |
| --- | --- |
|  prefix   |  前缀：一直被设置为content://   |
|  authority   |  授权：指定内容提供者的名称，例如联系人，浏览器等。第三方的内容提供者可以是全名，如：cn.programmer.statusprovider   |
|  data_type   |  数据类型：这个表明这个特殊的内容提供者中的数据的类型。例如：你要通过内容提供者Contacts来获取所有的通讯录，数据路径是people，那么URI将是下面这样：content://contacts/people   |
|  id   |  这个指定特定的请求记录。例如：你在内容提供者Contacts中查找联系人的ID号为5，那么URI看起来是这样：content://contacts/people/5   |

**创建内容提供者**

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