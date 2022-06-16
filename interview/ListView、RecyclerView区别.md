# ListView、RecyclerView区别

- ListView中ViewHolder是需要自定义的，在RecyclerView中ViewHolder是谷歌已经封装好的。   
- ListView和RecyclerView<span class="font-red">最大的区别在于数据源改变时的缓存策略</span>，listView缓存Item，RecyclerView缓存Item和ViewHolder。  
- ListView中的Item是只能垂直滑动的，RecyclerView可以水平滑动或者垂直滑动，针对多种类型条目的展示效果，如瀑布流 网格 支持多种类型。  
- ListView中删除或添加item时，item是无法产生动画效果的，在RecyclerView中添加、删除或移动item时有两种默认的效果可以选择SimpleItemAnimator（简单条目动画） 和 DefaultItemAnimator（原样的条目动画）。

**结论：**  
列表页展示界面，需要支持动画，或者频繁更新，局部刷新，建议使用RecyclerView，更加强大完善，易扩展；其它情况(如微信卡包列表页)两者都OK，但ListView在使用上会更加方便，快捷。

## ListView二级缓存机制
ListView的缓存主要是通过AbsListView的一个内部类**RecycleBin**来实现的。  

- **一级缓存**：屏幕内的item缓存到mActiveViews中，便于快速重用。
- **二级缓存**：移除屏幕的item缓存到mScrapViews中，便于新的item进入屏幕时重用。  
(ListView每当一项子view滑出界面时，RecycleBin会调用addScrapView()方法将这个废弃的子view进行缓存。每当子view滑入界面时，RecycleBin会调用getScrapView()方法获取一个废弃已缓存的view。)

## RecyclerView四级缓存机制
RecycleView的四级缓存是由三个类共同作用完成的：**Recycler、RecycledViewPool和ViewCacheExtension**。  

<details><summary>Recycler、RecycledViewPool和ViewCacheExtension</summary>

**Recycler**  
用于管理已经废弃或者与RecyclerView分离的ViewHolder，这里面有两个重要的成员：  
- 屏幕内缓存  
屏幕内缓存指在屏幕中显示的ViewHolder，这些ViewHolder会缓存在**mAttachedScrap、mChangedScrap**中;
mChangedScrap表示数据已经改变的viewHolder列表，mAttachedScrap表示未与RecyclerView分离的ViewHolder列表。
- 屏幕外缓存  
当列表滑动出了屏幕时，ViewHolder会被缓存在**mCachedViews**其大小由mViewCacheMax决定，默认DEFAULT_CACHE_SIZE为2，可通过Recyclerview.setItemViewCacheSize()动态设置。

**RecycledViewPool**  
RecycledViewPool类是用来缓存ViewHolder用，如果多个RecyclerView之间用setRecycledViewPool(RecycledViewPool)设置同一个RecycledViewPool，他们就可以共享ViewHolder。

**ViewCacheExtension**  
开发者可自定义的一层缓存，是抽象类ViewCacheExtension的一个实例，开发者可实现方法getViewForPositionAndType(Recycler recycler, int position, int type)来实现自己的缓存。

```java
public final class Recycler {
    //一级缓存中用来存储屏幕中显示的ViewHolder
    final ArrayList<ViewHolder> mAttachedScrap = new ArrayList<ViewHolder>();
    private ArrayList<ViewHolder> mChangedScrap = null;
   //二级缓存中用来存储屏幕外的缓存
    final ArrayList<ViewHolder> mCachedViews = new ArrayList<ViewHolder>();
   //暂可忽略 mAttachedScrap的不可变视图
    private final List<ViewHolder>
        mUnmodifiableAttachedScrap = Collections.unmodifiableList(mAttachedScrap);
    //当前屏幕外缓存大小，数量为2，即本代码片最后一个DEFAULT_CACHE_SIZE 成员的值，可变。
    private int mViewCacheMax = DEFAULT_CACHE_SIZE;
    //四级缓存当屏幕外缓存的大小大于2，便放入mRecyclerPool中缓存。
    private RecycledViewPool mRecyclerPool;
    //三级缓存自定义缓存，根据coder自己定义的缓存规则。
    private ViewCacheExtension mViewCacheExtension;
    //默认屏幕外缓存大小。
    private static final int DEFAULT_CACHE_SIZE = 2;
}

public abstract static class ViewCacheExtension {
    public abstract View getViewForPositionAndType(Recycler recycler, int position, int type);
}

public static class RecycledViewPool {
    //默认pool大小为5，只能存储5个，这个值可以更改的，有提供set函数
    private static final int DEFAULT_MAX_SCRAP = 5;
    //其他一些成员不必深究，mScrapHeap 是咱们存储的这一类viewholder
    static class ScrapData {
        final ArrayList<ViewHolder> mScrapHeap = new ArrayList<>();
        int mMaxScrap = DEFAULT_MAX_SCRAP;
        long mCreateRunningAverageNs = 0;
        long mBindRunningAverageNs = 0;
    }

    SparseArray<ScrapData> mScrap = new SparseArray<>();

    private int mAttachCount = 0;
}

```

</details>

缓存流程：屏幕中--屏幕外--用户自定义--pool池

- **一级缓存**：先判断了一下ViewHolder有没有发生改变，如果ViewHolder没有改变就放入mAttachedScrap中，改变了就存入mChangedScrap中。
- **二级缓存**：item被移出屏幕后，先缓存进了mCachedViews，如果mCachedViews满了就加入RecycledViewPool中。（因ViewHolder相关的position,flag等标志都一并被缓存了，所以从mCachedViews中取出的ViewHolder不需要再进行绑定操作就可以直接使用）  
- **三级缓存**：自定义缓存，ViewCacheExtension是一个抽象的静态内部类，用户实现getViewForPositionAndType(Recycler recycler, int position, int type)方法就可以实现这级缓存。
- **四级缓存**：根据ViewHolder的getItemViewType()方法返回的type进行缓存，如果没重写该方法默认一种类型返回-1。（保存时会清除ViewHolder的position,flag等，所以从pool中取出的ViewHolder需要重新绑定数据）

> [结合源码理解RecyclerView的四级缓存机制](https://blog.csdn.net/HJsir/article/details/81485653)