package com.huahua.weread.mvp;

import android.content.Context;
import android.support.annotation.AnimRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.huahua.weread.R;
import com.huahua.weread.utils.LogUtils;

import java.util.List;

/**
 *那子类需要重写什么方法 一般来说
 *
 * Created by Administrator on 2017/1/1.
 */

public class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final int TYPE_ITEM = 0;
    protected static final int TYPE_FOOTER = 1;

    protected int mLastPosition = -1;
    protected boolean mIsShowFooter;
    protected List<T> mDataList;   // 其实应该改名为mItems 有意义的名字  这两个差别不是很大
    // 自己写的ItemClickListener，有一个position参数，不然直接用view的ItemClickListener就好了是吧
    protected IOnItemClickListener mOnItemClickListener;

    /**
     * 我自己写的，因为子类需要访问数据库，需要一个context对象
     * 传进来的是activity呀 好像不是 点着看就知道了 是Fragment的onAttach方法获取的
     *
     * 因此需要写多一个构造方法
     */
    protected Context mContext;

    public BaseRecyclerViewAdapter(Context context, List<T> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    /**
     * 构造方法
     * @param dataList
     */
    public BaseRecyclerViewAdapter(List<T> dataList) {
        mDataList = dataList;
    }

    /**
     * 暴露给外界调用的方法
     * 给item添加OnItemClick点击事件  灵活吗
     * @param onItemClickListener
     */
    public void setOnItemClickListener(IOnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * 这个要交给子类重写了
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    /**
     * 创建FooterViewHolder
     * @param parent
     * @return
     */
    protected FooterViewHolder createFooterViewHolder(ViewGroup parent) {
        return new FooterViewHolder(getView(parent, R.layout.footer_load_more));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // 如果是gridlayout，可能需要这个  // 子类相同的代码 所以提到父类
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        /**
         * 这个是说如果是StaggeredGridLayout布局，那么设置LayoutParams参数
         * 使底部加载布局显示在一个独立的行里
         */
        // 真灵活，这里手动去调用 getItemViewType(position)
        if (getItemViewType(position) == TYPE_FOOTER) {
            if (layoutParams != null) {
                if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView
                            .getLayoutParams();
                    params.setFullSpan(true);
                }
            }
        }
    }

    /**
     * 应该是说每个子类都需要这个加载Inflate xml文件渲染成view的方法 所以提到父类来
     *
     * @param parent
     * @param layoutId
     * @return
     */
    protected View getView(ViewGroup parent, int layoutId) {
        // 每个view都持有一个context参数，view的构造函数就是需要传了一个context参数嘛
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    /**
     * 也是说每个子类都可能需要用这个方法，所以提取到公共的类里面
     *
     * 这个也还得mIsShowFooter等于true前提才行，不然你哪来的footer，最后一个也是正常的数据项
     *
     * @param position
     * @return
     */
    protected boolean isLastPosition(int position) {
        return (getItemCount() - 1) == position;
    }

    /**
     * 重要
     * 有根据是否要显示Footer布局而做不同的事情  itemSize是否加1
     * @return
     */
    @Override
    public int getItemCount() {
        if (mDataList == null) {
            return 0;
        }

        int itemSize = mDataList.size();
        if (mIsShowFooter) {
            // ItemCount 和 DataList 是两个概念
            itemSize += 1;
        }
        return itemSize;
    }

    public void showFooter() {
        mIsShowFooter = true;
        // 此时执行getItemCount() 数据数量会加1（误，说得很笼统，不清晰）  footer项
        // ItemCount的值会增1 而DataList.size的值没有变 DataList集合里面的数据还是那么多
        // ItemCount 和 DataList 是两个概念
        // Position of the newly inserted item in the data set.
        notifyItemInserted(getItemCount());

        LogUtils.LOGI("BaseRecyclerViewAdapter", ">>>>>    showFooter      >>>>>");
    }

    public void hideFooter() {
        if (mIsShowFooter) {
            mIsShowFooter = false;
            // Position of the item that has now been removed.
            notifyItemRemoved(getItemCount() + 1);
        }
    }

    /**
     * item进入动画
     * @param holder
     * @param position
     * @param type
     */
    protected void setItemAppearAnimation(RecyclerView.ViewHolder holder, int position, @AnimRes int type) {
        if (position > mLastPosition/* && !isLastPosition(position)*/) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), type);
            holder.itemView.startAnimation(animation);
            mLastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        /**
         * 这里有一个clearAnimation的东西
         * 如果item已经移出屏幕，而假如item还有动画没有执行完，就清除掉动画。
         */
        if (isShowingAnimation(holder)) {
            holder.itemView.clearAnimation();
        }
    }

    /**
     * 判断item是否正在执行动画
     * @param holder
     * @return
     */
    protected boolean isShowingAnimation(RecyclerView.ViewHolder holder) {
        return holder.itemView.getAnimation() != null
                && holder.itemView.getAnimation().hasStarted();
    }

    /**
     * 设置数据源
     * @param dataList
     */
    public void setDataList(List<T> dataList) {
        mDataList = dataList;
    }

    /**
     * 得到整个数据集的数据 返回一个list集合
     * @return
     */
    public List<T> getDataList() {
        return mDataList;
    }

    /**
     * 在某个位置插入单个item数据
     *
     * 不是用notifyDataChange哦！！
     * @param position
     * @param item
     */
    public void add(int position, T item) {
        mDataList.add(position, item);
        notifyItemInserted(position);
    }

    /**
     * 从数据集最后添加一组list数据
     * @param dataList
     */
    public void addMore(List<T> dataList) {
        int startPosition = mDataList.size();
        mDataList.addAll(dataList);
        // 这个是不是搞错啦 mDataList.size() 应该是dataList.size()吧
        // 点进去看源码应该是这个dataList.size() 新插入item的数量
        // notifyItemRangeInserted(startPosition, mDataList.size());
        notifyItemRangeInserted(startPosition, dataList.size());
    }

    /**
     * 删除某个位置的数据
     * @param position
     */
    public void delete(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 清空数据源
     */
    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 底部布局的ViewHolder  相同的代码  所以提取出来到父类
     */
    protected class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}


























