package com.huahua.weread.mvp.gankio;

import android.app.Activity;
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

import com.huahua.weread.MainActivity;
import com.huahua.weread.R;
import com.huahua.weread.bean.GankioFuliItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/12/8.
 */

public class GankioFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, IGankioView {

    @BindView(R.id.progressbarCommon)
    ProgressBar mProgressbarCommon;
    @BindView(R.id.recyclerviewCommon)
    RecyclerView mRecyclerviewCommon;
    @BindView(R.id.swiperefreshCommon)
    SwipeRefreshLayout mSwiperefreshCommon;

    private Context mContext;
    private Unbinder mUnbinder; // 在onDestroyview里解绑

    private GankioPresenter mPresenter;  // presenter  大哥 大脑 控制枢纽

    private GankioAdapter mAdapter; // RecycleView的适配器
    private ArrayList<GankioFuliItem> mDataList = new ArrayList<GankioFuliItem>(); // RecycleView的数据源

    private boolean mLoadingMore = false; // 正在上拉加载更多的标识
    private boolean mIsRefresh = false; // 正在下拉刷新的标识
    private int mPageIndex = 1;
    private LinearLayoutManager mLinearLayoutManager;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        // 一进来白屏时间比较长 看不到下拉刷新旋转那个圆圈
//        initMemberVariable();
//        setView();

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
        mPresenter.unBindSubscription();
        super.onDestroyView();
    }

    private void initMemberVariable() {
        mPresenter = new GankioPresenter(this);
    }

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
        mAdapter = new GankioAdapter(mContext, mDataList);
        mRecyclerviewCommon.setAdapter(mAdapter);

        // 调用下拉刷新
        onRefresh();
    }

    // SwipeRefreshLayout下拉刷新的回调方法
    @Override
    public void onRefresh() {
        if (mIsRefresh) {
            return;
        }

        // 置下拉刷新标识
        mIsRefresh = true;
        // 下拉刷新状态 UI
        mSwiperefreshCommon.setRefreshing(true);
        // 重置参数
        mPageIndex = 1;

        // 去向服务器请求数据
        mPresenter.loadGankioFuli(mPageIndex);
    }

    /**
     * 上拉加载更多请求数据
     */
    private void onLoadMore() {
        mPresenter.loadGankioFuli(mPageIndex);
    }

    // IGankioView的四个回调方法
    @Override
    public void showProgressDialog() {
        if (mProgressbarCommon != null) {
            mProgressbarCommon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgressDialog() {
        // 请求数据成功或者失败都会调用这个方法，所以做一些共同的操作
        mLoadingMore = false; // 上拉加载更多的

        if (mSwiperefreshCommon != null) {
            mSwiperefreshCommon.setRefreshing(false);
        }
        if (mProgressbarCommon != null) {
            mProgressbarCommon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void updateSuccessData(List<GankioFuliItem> gankioFuliItemList) {
        // 如果是下拉刷新请求的数据，需要清空datalist
        if (mIsRefresh) {
            mIsRefresh = false; // 表示下拉刷新的  // 这个一定要放到这里 不能放到hideProgressDialog里
            mDataList.clear();
            mAdapter.notifyDataSetChanged();
        }

        mDataList.addAll(gankioFuliItemList);
        mAdapter.notifyDataSetChanged();

        mPageIndex++; // 页码加1
    }

    @Override
    public void showLoadFailMsg(String errMessage) {
        mIsRefresh = false; // 表示下拉刷新的

        Toast.makeText(mContext, "出错了" + errMessage, Toast.LENGTH_LONG).show();
    }
}














