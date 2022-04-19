# View绘制流程

View的绘制流程是从ViewRoot的performTraversals开始的，它经过measure，layout，draw三个过程最终将View绘制出来。performTraversals会依次调用performMeasure，performLayout，performDraw三个方法，他们会依次调用measure，layout，draw方法，然后又调用了onMeasure，onLayout，onDraw。

自定义View绘制流程函数调用链(简化版)

![view](https://img.upyun.zzming.cn/android/zdy_view.jpg)

## measure()
测量视图大小。从顶层父View到子View递归调用measure方法，measure方法又回调OnMeasure。

对于自定义的单一view的测量，只需要根据父 view 传递的MeasureSpec进行计算大小。

对于ViewGroup的测量，一般要重写onMeasure方法，在onMeasure方法中，父容器会对所有的子View进行Measure，子元素又会作为父容器，重复对它自己的子元素进行Measure，这样Measure过程就从DecorView一级一级传递下去了，也就是要遍历所有子View的的尺寸，最终得出出总的viewGroup的尺寸。Layout和Draw方法也是如此。

## layout()
确定View位置，进行页面布局。从顶层父View向子View的递归调用view.layout方法的过程，即父View根据上一步measure子View所得到的布局大小和布局参数，将子View放在合适的位置上。

对于自定义的单一view，计算本身的位置即可。

对于ViewGroup来说，需要重写onlayout方法。除了计算自己View的位置，还需要确定每一个子View在父容器的位置以及子view的宽高（getMeasuredWidth和getMeasuredHeight），最后调用所有子view的layout方法来设定子view的位置。

## draw()
绘制视图。ViewRoot创建一个Canvas对象，然后调用OnDraw()。  

1）drawBackground()，根据在 layout 过程中获取的 View 的位置参数，来设置背景的边界。  
2）onDraw()，绘制View本身的内容，一般自定义单一view会重写这个方法，实现一些绘制逻辑。  
3）dispatchDraw()，绘制子View。   
4）onDrawScrollBars(canvas)，绘制装饰，如 滚动指示器、滚动条、和前景。

六个步骤：  
①、绘制视图的背景；  
②、保存画布的图层（Layer）；  
③、绘制View的内容；  
④、绘制View子视图，如果没有就不用；  
⑤、还原图层（Layer）；  
⑥、绘制滚动条。  

**invalidate()和requestLayout()的不同**

requestLayout()会直接递归调用父窗口的requestLayout()，直到ViewRootImpl,然后触发performTraversals()，**由于mLayoutRequested为true，会导致onMeasure()和onLayout()被调用，不一定会触发OnDraw()。** requestLayout()触发onDraw()可能是因为在在layout过程中发现l,t,r,b和以前不一样，那就会触发一次invalidate()，所以触发了onDraw()，也可能是因为别的原因导致mDirty非空（比如在跑动画）。

view的invalidate()不会导致ViewRootImpl的invalidate()被调用，而是递归调用父view的invalidateChildInParent()，直到ViewRootImpl的invalidateChildInParent()，然后触发performTraversals()，会导致当前view被重绘,**由于mLayoutRequested为false，不会导致onMeasure()和onLayout()被调用，而OnDraw()会被调用**。

postInvalidate是在非UI线程中调用，invalidate则是在UI线程中调用。

## 自定义View方式
 
1、**自定义ViewGroup**：自定义ViewGroup一般是利用现有的组件根据特定的布局方式来组成新的组件，大多继承自ViewGroup或各种Layout，包含有子View。
  
2、**自定义View**：在没有现成的View，需要自己实现的时候，就使用自定义View，一般继承自View，SurfaceView或其他的View，不包含子View。
 
3、**继承原生控件**：继承如TextView，ImageView，Button等原生控件进行拓展。

> [Android自定义View教程目录](https://www.gcssloop.com/category/customview)