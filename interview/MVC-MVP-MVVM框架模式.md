# MVC-MVP-MVVM框架模式

## MVC

MVC (Model-View-Controller, 模型-视图-控制器)，标准的MVC是这个样子的：

模型层 (Model)：业务逻辑对应的数据模型，无View无关，而与业务相关；  
视图层 (View)：一般使用XML或者Java对界面进行描述；  
控制层 (Controllor)：在Android中通常指Activity和Fragment，或者由其控制的业务类。

Activity并非标准的Controller，它一方面用来控制了布局，另一方面还要在Activity中写业务代码，造成了Activity既像View又像Controller。

在Android开发中，就是指直接使用Activity并在其中写业务逻辑的开发方式。显然，一方面Activity本身就是一个视图，另一方面又要负责处理业务逻辑，因此逻辑会比较混乱。
(这种开发方式不太适合Android开发。)

## MVP

MVP (Model-View-Presenter) 是MVC的演化版本，几个主要部分如下：

模型层 (Model)：主要提供数据存取功能。  
视图层 (View)：处理用户事件和视图。在Android中，可能是指Activity、Fragment或者View。  
展示层 (Presenter)：负责通过Model存取书数据，连接View和Model，从Model中取出数据交给View。  

1、这里的Model是用来存取数据的，也就是用来从指定的数据源中获取数据，不要将其理解成MVC中的Model。在MVC中Model是数据模型，在MVP中，我们用Bean来表示数据模型。  
2、Model和View不会直接发生关系，它们需要通过Presenter来进行交互。在实际的开发中，我们可以用接口来定义一些规范，然后让我们的View和Model实现它们，并借助Presenter进行交互即可。

实际上，MVP的原理就是View通过Presenter获取数据，获取到数据之后再回调View的方法来展示数据。

**MVC和MVP的区别**

1、MVC中是允许Model和View进行交互的，而MVP中，Model与View之间的交互由Presenter完成；  
2、MVP模式就是将P定义成一个接口，然后在每个触发的事件中调用接口的方法来处理，也就是将逻辑放进了P中，需要执行某些操作的时候调用P的方法就行了。

**MVP的优缺点**

**优点**

①降低耦合度，实现了Model和View真正的完全分离，可以修改View而不影响Modle；  
②模块职责划分明显，层次清晰；  
③隐藏数据；  
④Presenter 可以复用，一个Presenter可以用于多个View，而不需要更改Presenter的逻辑；  
⑤利于测试驱动开发，以前的Android开发是难以进行单元测试的；  
⑥View可以进行组件化，在MVP当中，View不依赖Model。  

**缺点**

①Presenter中除了应用逻辑以外，还有大量的View->Model，Model->View的手动同步逻辑，造成Presenter比较笨重，维护起来会比较困难；  
②由于对视图的渲染放在了Presenter中，所以视图和Presenter的交互会过于频繁；  
③如果Presenter过多地渲染了视图，往往会使得它与特定的视图的联系过于紧密，一旦视图需要变更，那么Presenter也需要变更了。  

## MVVM

MVVM是Model-View-ViewModel的简写。它本质上就是MVC的改进版。MVVM就是将其中的View的状态和行为抽象化，让我们将视图UI和业务逻辑分开。

1、模型层 (Model)：负责从各种数据源中获取数据；  
2、视图层 (View)：在Android中对应于Activity和Fragment，用于展示给用户和处理用户交互，会驱动ViewModel从Model中获取数据；  
3、ViewModel层：用于将Model和View进行关联，我们可以在View中通过ViewModel从Model中获取数据；当获取到了数据之后，会通过自动绑定，比如DataBinding，来将结果自动刷新到界面上。

**MVVM的优点**

①低耦合：视图（View）可以独立于Model变化和修改，一个ViewModel可以绑定到不同的View上，当View变化的时候Model可以不变，当Model变化的时候View也可以不变。  
②可重用性：你可以把一些视图逻辑放在一个ViewModel里面，让很多view重用这段视图逻辑。  
③独立开发：开发人员可以专注于业务逻辑和数据的开发（ViewModel），设计人员可以专注于页面设计。  
④可测试：界面素来是比较难于测试的，而现在测试可以针对ViewModel来写。  




> [Android 架构设计：MVC、MVP、MVVM和组件化](https://www.jianshu.com/p/aeb7dad34f05)