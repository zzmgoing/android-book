# Widget、Element、RederObject的区别

Widget 是 Flutter 里对视图的一种结构化描述，你可以把它看作是前端中的“控件”或“组件”。Widget 是控件实现的基本逻辑单位，里面存储的是有关视图渲染的配置信息，包括布局、渲染属性、事件响应信息等。

Element 是 Widget 的一个实例化对象，它承载了视图构建的上下文数据，是连接结构化的配置信息到完成最终渲染的桥梁。  
Flutter 中真正代表屏幕上显示元素的类是Element，Widget 只是描述 Element 的配置数据，并且一个Widget 可以对应多个Element。

RenderObject为应用程序提供真正的渲染。它的主要职责是绘制和布局，是一个真正的渲染对象。

首先，通过 Widget 树生成对应的 Element 树；  
然后，创建相应的 RenderObject 并关联到 Element.renderObject 属性上；  
最后，构建成 RenderObject 树来完成布局的排列和绘制，以完成最终的渲染。  

> [Flutter中Widget 、Element、RenderObject角色深入分析](https://zhuanlan.zhihu.com/p/183645816)  
> [Flutter渲染之Widget、Element 和 RenderObject](https://www.jianshu.com/p/71bb118517b1)