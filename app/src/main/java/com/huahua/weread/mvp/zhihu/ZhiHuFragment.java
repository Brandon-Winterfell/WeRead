package com.huahua.weread.mvp.zhihu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.huahua.weread.R;
import com.huahua.weread.bean.ZhiHuDailyItem;
import com.huahua.weread.bean.ZhiHuResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/12/6.
 */

public class ZhiHuFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, IZHView {

    @BindView(R.id.progressbarCommon)
    ProgressBar mProgressbarCommon;
    @BindView(R.id.recyclerviewCommon)
    RecyclerView mRecyclerviewCommon;
    @BindView(R.id.swiperefreshCommon)
    SwipeRefreshLayout mSwiperefreshCommon;

    private Context mContext;
    private Unbinder mUnbinder;  // 用来在destroyView方法里解绑ButterKnife

    private ZhiHuPresenter mPresenter;

    private String mCurrentLoadedDate;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean mLoadingMore = false;
    private boolean mIsRefresh = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private ZhiHuAdapter mAdapter;
    private ArrayList<ZhiHuDailyItem> mDataList = new ArrayList<ZhiHuDailyItem>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        mContext = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initMemberVariable();
        setView();
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        mPresenter.unBindSubscriber();
        super.onDestroyView();
    }

    private void initMemberVariable() {
        mPresenter = new ZhiHuPresenter(this); // 实例化presenter
    }

    /**
     * 这个方法是否要放到onCreateView里面去
     * 初始化view控件
     * 设置下拉刷新控件及他的监听方法
     * 设置RecycleView控件
     */
    private void setView() {
        /**
         * 设置下拉刷新控件
         */
        mSwiperefreshCommon.setOnRefreshListener(this);
        // 设置颜色
        mSwiperefreshCommon.setColorSchemeResources(
                android.R.color.holo_blue_light,android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light
        );
        // 设置偏移量  不设置这个的话一开始不会显示出来
        mSwiperefreshCommon.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                        getResources().getDisplayMetrics()));

        /**
         * 设置RecycleView
         */
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerviewCommon.setLayoutManager(mLinearLayoutManager);
        mRecyclerviewCommon.setHasFixedSize(true);
        // 默认的动画效果
        mRecyclerviewCommon.setItemAnimator(new DefaultItemAnimator());
        mRecyclerviewCommon.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) {  // 向下滚动
//                    visibleItemCount = mLinearLayoutManager.getChildCount();
//                    totalItemCount = mLinearLayoutManager.getItemCount();
//                    pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();
//
//                    /**
//                     * TODO 还有个判断的  就是滑动的时候不加载  停止的时候才加载
//                     */
//                    // 没有正在加载并且滑动到底部
//                    // 已经是正在加载更多，正在跟服务器交互的过程中了，网络慢的话不能一下子完成
//                    // 所以设置个变量，不能出现上一次还没有完成，就触发下一次请求
//                    if (!mLoadingMore && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                        mLoadingMore = true;
//                        onLoadMore();
//                    }
//                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // 滚动停止后才加载数据
                visibleItemCount = mLinearLayoutManager.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();

                /**
                 * TODO 还有个判断的  就是滑动的时候不加载  停止的时候才加载
                 */
                // 没有正在加载并且滑动到底部
                // 已经是正在加载更多，正在跟服务器交互的过程中了，网络慢的话不能一下子完成
                // 所以设置个变量，不能出现上一次还没有完成，就触发下一次请求
                if (!mLoadingMore
                        && newState == RecyclerView.SCROLL_STATE_IDLE
                        && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    mLoadingMore = true;
                    onLoadMore();
                    if (mDataList.size() > 1) {
                        mRecyclerviewCommon.scrollToPosition(mDataList.size() - 1);
                    }
                }
            }
        });

        // 设置RecycleView的adapter
        mAdapter = new ZhiHuAdapter(mContext, mDataList);
        mRecyclerviewCommon.setAdapter(mAdapter);

        // 调用下拉刷新
        onRefresh();
    }

    // SwipeRefreshLayout的下拉刷新回调方法
    @Override
    public void onRefresh() {
        if (mIsRefresh) {
            return;
            // 其实这里可以不用这个判断return，用presenter.unBindSubscriber，只是这样比较费流量吧
            // 是数据返回后才发现解绑，这时候把数据丢弃吧
        }
        // 设置下拉刷新状态
        mSwiperefreshCommon.setRefreshing(true);
        // 重置参数
        mCurrentLoadedDate = "0";
        // 表示下拉刷新的变量
        mIsRefresh = true;
        // 请求数据
        mPresenter.loadZhiHuDailyLast();
    }

    /**
     * 上拉加载更多 传入今天的日期 获取昨天的知乎日报
     */
    private void onLoadMore() {
        mPresenter.loadTheDaily(mCurrentLoadedDate);
    }

    // 4个IZHView的具体实现方法
    @Override
    public void showProgressDialog() {
        if (mProgressbarCommon != null) {
            mProgressbarCommon.setVisibility(View.VISIBLE);
        }
    }

    // 不管成功或者失败都会调用这个方法的，那就可以在这里设置一些共有的变量了 mLoadingMore
    @Override
    public void hideProgressDialog() {
        mLoadingMore = false; // 上拉加载更多的

        if (mSwiperefreshCommon != null) {
            mSwiperefreshCommon.setRefreshing(false);
        }
        if (mProgressbarCommon != null) {
            mProgressbarCommon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void updateSuccessData(ZhiHuResponse zhiHuResponse) {
        if (mIsRefresh) {
            mIsRefresh = false; // 表示下拉刷新的 // 这个一定要放到这里 不能放到hideProgressDialog里
            mDataList.clear();  // 下拉刷新才清空数据
            mAdapter.notifyDataSetChanged();
        }
        // 更新日期
        mCurrentLoadedDate = zhiHuResponse.getDate();
        mDataList.addAll(zhiHuResponse.getStories());
        mAdapter.notifyDataSetChanged();
        // 怪了 为什么一直在加载  而他的却不会
        // 如果没有填满屏幕 加了这个是作死的节奏呀  是一直在加载 不断的执行onLoadMore
//        if (!mRecyclerviewCommon.canScrollVertically(View.SCROLL_INDICATOR_BOTTOM)) {
//            onLoadMore();
//            Log.i("caonima", "我要看看是不是一直在加载" + mCurrentLoadedDate);
//        }
    }

    @Override
    public void showLoadFailMsg(String errMessage) {
        mIsRefresh = false; // 表示下拉刷新的

        Toast.makeText(mContext, "发生错误：" + errMessage, Toast.LENGTH_SHORT).show();
    }


}

































