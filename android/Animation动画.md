# Animation动画

## 传统动画

### 帧动画

> 最容易实现的一种动画（AnimationDrawable），将一张张图片连贯起来播放。

### 补间动画

- 平移动画（TranslateAnimation）
- 缩放动画（ScaleAnimation）
- 旋转动画（RotateAnimation）
- 透明度动画（AlphaAnimation）
  
## 属性动画（基于对象属性的动画）

所有补间动画都可以用属性动画实现。

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

- AccelerateDecelerateInterpolator ：在动画开始与结束的地方速率改变比较慢，在中间的时候加速  
- AccelerateInterpolator：在动画开始的地方速率改变比较慢，然后开始速率变化加快  
- **LinearInterpolator**：以常量速率改变  
- AnticipateInterpolator：开始的时候向后然后向前甩  
- **CycleInterpolator**：动画循环播放特定的次数，速率改变沿着正弦曲线  
- **PathInterpolator**：动画执行的效果按贝塞尔曲线  
- anticipateOvershootInterpolator：开始的时候向后然后向前甩一定值后返回最后的值  
- OvershootInterpolator：向前甩一定值后再回到原来位置  
- BounceInterpolator：动画结束的时候有弹起效果  

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

### 补间动画和属性动画的区别

- 补间动画只是绘制了一个不同的影子，view对象还在原来的位置。  
比如位移后点击原来的位置会响应点击事件，旋转后再次旋转会从头开始重新旋转。

- 而**属性动画则是真正的视图移动**，例如点击移动后的视图会响应点击事件。

>[https://www.cnblogs.com/kross/p/4087780.html](https://www.cnblogs.com/kross/p/4087780.html)
>[https://blog.csdn.net/carson_ho/article/details/72827747](https://blog.csdn.net/carson_ho/article/details/72827747)