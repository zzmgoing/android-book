# Animation动画

## 传统动画（帧动画、补间动画）

**帧动画：** 最容易实现的一种动画，将一张张图片连贯起来播放。

xml实现：  
```xml
<?xml version="1.0" encoding="utf-8"?>
<animation-list
xmlns:android="http://schemas.android.com/apk/res/android"
android:oneshot="false">
    <item
         android:drawable="@drawable/anim_1"
         android:duration="200"/>
    <item
         android:drawable="@drawable/anim_2"
         android:duration="200"/>
    <item
         android:drawable="@drawable/anim_3"
         android:duration="200"/>
    <item
         android:drawable="@drawable/anim_4"
         android:duration="200"/>
</animation-list>
```
代码实现：  
```java
  //创建一个AnimationDrawable
  AnimationDrawable animationDrawable1 = new AnimationDrawable();
  //准备好资源图片
  int[] ids = {R.drawable.anim_1,R.drawable.anim_2,R.drawable.anim_3,R.drawable.anim_4};
  //通过for循环添加每一帧动画
  for(int i = 0 ; i < 4 ; i ++){
      Drawable frame = getResources().getDrawable(ids[i]);
  //设定时长
      animationDrawable1.addFrame(frame,200);
  }
  animationDrawable1.setOneShot(false);
  //将动画设置到背景上
  iv.setBackground(animationDrawable1);
  //开启帧动画
  animationDrawable1.start();
```

**补间动画：** 平移动画（Translate），缩放动画（scale），旋转动画（rotate），透明度动画（alpha）。

**1、平移动画（Translate）**

xml实现：  
```xml
<?xml version="1.0" encoding="utf-8"?>
<translate xmlns:android="http://schemas.android.com/apk/res/android">
// 以下参数是4种动画效果的公共属性,即都有的属性
android:duration="3000" // 动画持续时间（ms），必须设置，动画才有效果
android:startOffset ="1000" // 动画延迟开始时间（ms）
android:fillBefore = “true” // 动画播放完后，视图是否会停留在动画开始的状态，默认为true
android:fillAfter = “false” // 动画播放完后，视图是否会停留在动画结束的状态，优先于fillBefore值，默认为false
android:fillEnabled= “true” // 是否应用fillBefore值，对fillAfter值无影响，默认为true
android:repeatMode= “restart” // 选择重复播放动画模式，restart代表正序重放，reverse代表倒序回放，默认为restart|
android:repeatCount = “0” // 重放次数（所以动画的播放次数=重放次数+1），为infinite时无限重复
android:interpolator = @[package:]anim/interpolator_resource // 插值器，即影响动画的播放速度,下面会详细讲

// 以下参数是平移动画特有的属性
android:fromXDelta="0" // 视图在水平方向x 移动的起始值
android:toXDelta="500" // 视图在水平方向x 移动的结束值

android:fromYDelta="0" // 视图在竖直方向y 移动的起始值
android:toYDelta="500" // 视图在竖直方向y 移动的结束值

</translate>
```
代码实现：  
```java
Button mButton = (Button) findViewById(R.id.Button);
Animation translateAnimation = new TranslateAnimation(0，500，0，500);
// 步骤2：创建平移动画的对象：平移动画对应的Animation子类为TranslateAnimation
// 参数分别是：
// 1. fromXDelta ：视图在水平方向x 移动的起始值
// 2. toXDelta ：视图在水平方向x 移动的结束值
// 3. fromYDelta ：视图在竖直方向y 移动的起始值
// 4. toYDelta：视图在竖直方向y 移动的结束值
translateAnimation.setDuration(3000);
// 固定属性的设置都是在其属性前加“set”，如setDuration（）
mButton.startAnimation(translateAnimation);
```

**2、缩放动画（Scale）**

xml实现：
```xml
<?xml version="1.0" encoding="utf-8"?>
<scale  xmlns:android="http://schemas.android.com/apk/res/android">
// 以下参数是4种动画效果的公共属性,即都有的属性
android:duration="3000" // 动画持续时间（ms），必须设置，动画才有效果
android:startOffset ="1000" // 动画延迟开始时间（ms）
android:fillBefore = “true” // 动画播放完后，视图是否会停留在动画开始的状态，默认为true
android:fillAfter = “false” // 动画播放完后，视图是否会停留在动画结束的状态，优先于fillBefore值，默认为false
android:fillEnabled= “true” // 是否应用fillBefore值，对fillAfter值无影响，默认为true
android:repeatMode= “restart” // 选择重复播放动画模式，restart代表正序重放，reverse代表倒序回放，默认为restart|
android:repeatCount = “0” // 重放次数（所以动画的播放次数=重放次数+1），为infinite时无限重复
android:interpolator = @[package:]anim/interpolator_resource // 插值器，即影响动画的播放速度,下面会详细讲

// 以下参数是缩放动画特有的属性
android:fromXScale="0.0" 
// 动画在水平方向X的起始缩放倍数
// 0.0表示收缩到没有；1.0表示正常无伸缩
// 值小于1.0表示收缩；值大于1.0表示放大

android:toXScale="2"  //动画在水平方向X的结束缩放倍数

android:fromYScale="0.0" //动画开始前在竖直方向Y的起始缩放倍数
android:toYScale="2" //动画在竖直方向Y的结束缩放倍数

android:pivotX="50%" // 缩放轴点的x坐标
android:pivotY="50%" // 缩放轴点的y坐标
// 轴点 = 视图缩放的中心点

// pivotX pivotY,可取值为数字，百分比，或者百分比p
// 设置为数字时（如50），轴点为View的左上角的原点在x方向和y方向加上50px的点。在Java代码里面设置这个参数的对应参数是Animation.ABSOLUTE。
// 设置为百分比时（如50%），轴点为View的左上角的原点在x方向加上自身宽度50%和y方向自身高度50%的点。在Java代码里面设置这个参数的对应参数是Animation.RELATIVE_TO_SELF。
// 设置为百分比p时（如50%p），轴点为View的左上角的原点在x方向加上父控件宽度50%和y方向父控件高度50%的点。在Java代码里面设置这个参数的对应参数是Animation.RELATIVE_TO_PARENT

// 两个50%表示动画从自身中间开始，具体如下图

</scale>
```
代码实现：  
```java
Button mButton = (Button) findViewById(R.id.Button);
// 步骤1:创建 需要设置动画的 视图View
  Animation rotateAnimation = new ScaleAnimation(0,2,0,2,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
// 步骤2：创建缩放动画的对象 & 设置动画效果：缩放动画对应的Animation子类为RotateAnimation
// 参数说明:
// 1. fromX ：动画在水平方向X的结束缩放倍数
// 2. toX ：动画在水平方向X的结束缩放倍数
// 3. fromY ：动画开始前在竖直方向Y的起始缩放倍数
// 4. toY：动画在竖直方向Y的结束缩放倍数
// 5. pivotXType:缩放轴点的x坐标的模式
// 6. pivotXValue:缩放轴点x坐标的相对值
// 7. pivotYType:缩放轴点的y坐标的模式
// 8. pivotYValue:缩放轴点y坐标的相对值

// pivotXType = Animation.ABSOLUTE:缩放轴点的x坐标 =  View左上角的原点 在x方向 加上 pivotXValue数值的点(y方向同理)
// pivotXType = Animation.RELATIVE_TO_SELF:缩放轴点的x坐标 = View左上角的原点 在x方向 加上 自身宽度乘上pivotXValue数值的值(y方向同理)
// pivotXType = Animation.RELATIVE_TO_PARENT:缩放轴点的x坐标 = View左上角的原点 在x方向 加上 父控件宽度乘上pivotXValue数值的值 (y方向同理)


scaleAnimation.setDuration(3000);
// 固定属性的设置都是在其属性前加“set”，如setDuration（）

mButton.startAnimation(scaleAnimation);
```

**3、旋转动画（Rotate）**

xml实现：
```xml
<?xml version="1.0" encoding="utf-8"?>
<rotate xmlns:android="http://schemas.android.com/apk/res/android">

// 以下参数是4种动画效果的公共属性,即都有的属性
android:duration="3000" // 动画持续时间（ms），必须设置，动画才有效果
android:startOffset ="1000" // 动画延迟开始时间（ms）
android:fillBefore = “true” // 动画播放完后，视图是否会停留在动画开始的状态，默认为true
android:fillAfter = “false” // 动画播放完后，视图是否会停留在动画结束的状态，优先于fillBefore值，默认为false
android:fillEnabled= “true” // 是否应用fillBefore值，对fillAfter值无影响，默认为true
android:repeatMode= “restart” // 选择重复播放动画模式，restart代表正序重放，reverse代表倒序回放，默认为restart|
android:repeatCount = “0” // 重放次数（所以动画的播放次数=重放次数+1），为infinite时无限重复
android:interpolator = @[package:]anim/interpolator_resource // 插值器，即影响动画的播放速度,下面会详细讲

// 以下参数是旋转动画特有的属性
android:duration="1000"
android:fromDegrees="0" // 动画开始时 视图的旋转角度(正数 = 顺时针，负数 = 逆时针)
android:toDegrees="270" // 动画结束时 视图的旋转角度(正数 = 顺时针，负数 = 逆时针)
android:pivotX="50%" // 旋转轴点的x坐标
android:pivotY="0" // 旋转轴点的y坐标
// 轴点 = 视图缩放的中心点

// pivotX pivotY,可取值为数字，百分比，或者百分比p
// 设置为数字时（如50），轴点为View的左上角的原点在x方向和y方向加上50px的点。在Java代码里面设置这个参数的对应参数是Animation.ABSOLUTE。
// 设置为百分比时（如50%），轴点为View的左上角的原点在x方向加上自身宽度50%和y方向自身高度50%的点。在Java代码里面设置这个参数的对应参数是Animation.RELATIVE_TO_SELF。
// 设置为百分比p时（如50%p），轴点为View的左上角的原点在x方向加上父控件宽度50%和y方向父控件高度50%的点。在Java代码里面设置这个参数的对应参数是Animation.RELATIVE_TO_PARENT
// 两个50%表示动画从自身中间开始，具体如下图

</rotate> 
```
代码实现：  
```java
Button mButton = (Button) findViewById(R.id.Button);
// 步骤1:创建 需要设置动画的 视图View

  Animation rotateAnimation = new RotateAnimation(0,270,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
// 步骤2：创建旋转动画的对象 & 设置动画效果：旋转动画对应的Animation子类为RotateAnimation
// 参数说明:
// 1. fromDegrees ：动画开始时 视图的旋转角度(正数 = 顺时针，负数 = 逆时针)
// 2. toDegrees ：动画结束时 视图的旋转角度(正数 = 顺时针，负数 = 逆时针)
// 3. pivotXType：旋转轴点的x坐标的模式
// 4. pivotXValue：旋转轴点x坐标的相对值
// 5. pivotYType：旋转轴点的y坐标的模式
// 6. pivotYValue：旋转轴点y坐标的相对值

// pivotXType = Animation.ABSOLUTE:旋转轴点的x坐标 =  View左上角的原点 在x方向 加上 pivotXValue数值的点(y方向同理)
// pivotXType = Animation.RELATIVE_TO_SELF:旋转轴点的x坐标 = View左上角的原点 在x方向 加上 自身宽度乘上pivotXValue数值的值(y方向同理)
// pivotXType = Animation.RELATIVE_TO_PARENT:旋转轴点的x坐标 = View左上角的原点 在x方向 加上 父控件宽度乘上pivotXValue数值的值 (y方向同理)

rotateAnimation.setDuration(3000);
// 固定属性的设置都是在其属性前加“set”，如setDuration（）
mButton.startAnimation(rotateAnimation);
```

**4、透明度动画（Alpha）**

xml实现：  
```xml
<?xml version="1.0" encoding="utf-8"?>
<alpha xmlns:android="http://schemas.android.com/apk/res/android">

// 以下参数是4种动画效果的公共属性,即都有的属性
android:duration="3000" // 动画持续时间（ms），必须设置，动画才有效果
android:startOffset ="1000" // 动画延迟开始时间（ms）
android:fillBefore = “true” // 动画播放完后，视图是否会停留在动画开始的状态，默认为true
android:fillAfter = “false” // 动画播放完后，视图是否会停留在动画结束的状态，优先于fillBefore值，默认为false
android:fillEnabled= “true” // 是否应用fillBefore值，对fillAfter值无影响，默认为true
android:repeatMode= “restart” // 选择重复播放动画模式，restart代表正序重放，reverse代表倒序回放，默认为restart|
android:repeatCount = “0” // 重放次数（所以动画的播放次数=重放次数+1），为infinite时无限重复
android:interpolator = @[package:]anim/interpolator_resource // 插值器，即影响动画的播放速度,下面会详细讲

// 以下参数是透明度动画特有的属性
android:fromAlpha="1.0" // 动画开始时视图的透明度(取值范围: -1 ~ 1)
android:toAlpha="0.0"// 动画结束时视图的透明度(取值范围: -1 ~ 1)

</alpha>
```
代码实现：  
```java
Button mButton = (Button) findViewById(R.id.Button);
// 步骤1:创建 需要设置动画的 视图View
Animation alphaAnimation = new AlphaAnimation(1,0);
// 步骤2：创建透明度动画的对象 & 设置动画效果：透明度动画对应的Animation子类为AlphaAnimation
// 参数说明:
// 1. fromAlpha:动画开始时视图的透明度(取值范围: -1 ~ 1)
// 2. toAlpha:动画结束时视图的透明度(取值范围: -1 ~ 1)
alphaAnimation.setDuration(3000);
// 固定属性的设置都是在其属性前加“set”，如setDuration（）
mButton.startAnimation(alphaAnimation);
```
  
## 属性动画（基于对象属性的动画）
<span>所有补间动画都可以用属性动画实现</span>

Android 3.0（API 11）后才提供的一种全新动画模式，出现原因为作用对象局限于View，没有改变View的属性，只是改变视觉效果，动画效果单一。  
ValueAnimator、ObjectAnimator是其重要的两个类。  
ValueAnimator有ofInt、ofFloat、ofObject三个重要的方法。  
方法的作用：  
1、创建动画实例。  
2、将传入的多个参数进行平滑过渡: 此处传入0和1，表示将值从0平滑过渡到1，如果传入了3个Int参数a,b,c ,则是先从a平滑过渡到b,再从b平滑过渡到c，以此类推，其内置了估值器。

**ValueAnimator**：可以设置开始值和结束值来动态改变view的移动位置。  
**ObjectAnimator**：功能更加强大，可以控制位移、透明度、旋转、缩放。

**插值器**（Interpolator）：决定值的变化模式  
**估值器**（TypeEvaluator）：决定值的具体变化数值  

插值器决定值的变化模式，默认的种类有九个：  
AccelerateDecelerateInterpolator ：在动画开始与结束的地方速率改变比较慢，在中间的时候加速  
AccelerateInterpolator：在动画开始的地方速率改变比较慢，然后开始速率变化加快  
**LinearInterpolator**：以常量速率改变  
AnticipateInterpolator：开始的时候向后然后向前甩  
**CycleInterpolator**：动画循环播放特定的次数，速率改变沿着正弦曲线  
**PathInterpolator**：动画执行的效果按贝塞尔曲线  
anticipateOvershootInterpolator：开始的时候向后然后向前甩一定值后返回最后的值  
OvershootInterpolator：向前甩一定值后再回到原来位置  
BounceInterpolator：动画结束的时候有弹起效果  

**自定义插值器：**  
写一个类实现Interpolator接口，Interpolator是一个空的接口继承了TimeInterpolator接口，定义getInterpolation方法即可。

```java
public class Demo{
    private void RotateAnimation() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(myView, "rotation", 0f, 360f);
        anim.setDuration(1000);
        anim.start();
    }
    private void AlpahAnimation() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(myView, "alpha", 1.0f, 0.8f, 0.6f, 0.4f, 0.2f, 0.0f);
        anim.setRepeatCount(-1);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setDuration(2000);
        anim.start();
    }
}
```

**补间动画和属性动画的区别**  

补间动画只是绘制了一个不同的影子，view对象还在原来的位置。  
<span>比如位移后点击原来的位置会响应点击事件，旋转后再次旋转会从头开始重新旋转</span>

而**属性动画则是真正的视图移动**，例如点击移动后的视图会响应点击事件。

>[https://www.cnblogs.com/kross/p/4087780.html](https://www.cnblogs.com/kross/p/4087780.html)
>[https://blog.csdn.net/carson_ho/article/details/72827747](https://blog.csdn.net/carson_ho/article/details/72827747)