# Activity获取View宽高

**1、Activity/View#onWindowFocusChanged**

```java
@Override
public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
    }
}
```

**2、view.post(runnable)**

通过post可以将一个runnable投递到消息队列的尾部，等待Looper调用次runnable时候，view已经初始化了。

```java
view.post(new Runnable() {
    @Override
    public void run() {
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
    }
});
```

**3、ViewTreeObserver**

```java
view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
    @Override
    public void onGlobalLayout() {
        view.getViewTreeObserver().removeOnGlobalLayoutListener(this::onGlobalLayout);
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
    }
});
```