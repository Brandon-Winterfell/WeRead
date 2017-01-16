package com.huahua.weread.mvp.guoke;

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
import android.widget.Toast;

import com.huahua.weread.R;
import com.huahua.weread.bean.GuokeHotItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/12/7.
 */

public class GuokeFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, IGuokeView {

    @BindView(R.id.recyclerviewCommon)
    RecyclerView mRecyclerviewCommon;
    @BindView(R.id.swiperefreshCommon)
    SwipeRefreshLayout mSwiperefreshCommon;

    private GuokePresenter mPresenter;  // 该类的头脑 灵魂 核心
    private Context mContext;
    private Unbinder mUnbinder;

    private GuokeAdapter mAdapter;
    private ArrayList<GuokeHotItem> mDataList = new ArrayList<GuokeHotItem>();

    private boolean mLoadingMore = false;
    private boolean mIsRefresh = false;
    private int mOffset = 0;
    private LinearLayoutManager mLinearLayoutManager;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        mContext = null;   // 不是静态的引用不用手动置为空吧
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guoke, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initMemberVariable();
        setView();

        // 用户一进来就调用下拉刷新去请求加载数据，不请求的话，哪来的数据给用户看
        onRefresh();
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        mPresenter.unBindSubscriber();
        super.onDestroyView();
    }

    private void initMemberVariable() {
        mPresenter = new GuokePresenter(this);
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
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // 滚动停止后才加载数据
                visibleItemCount = mLinearLayoutManager.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();

                /**
                 * 滑动的时候不加载  停止的时候才加载
                 * 不是正在加载的状态并且滑动到底部 这是触发加载更多的条件
                 *
                 * 已经是正在加载更多，正在跟服务器交互的过程中了，网络慢的话不能一下子完成
                 * 所以设置个变量，不能出现上一次还没有完成，就触发下一次请求
                 */
                if (!mLoadingMore
                        && newState == RecyclerView.SCROLL_STATE_IDLE
                        && (visibleItemCount + pastVisiblesItems) >= totalItemCount
                        && !mIsRefresh) {
                    /**
                     * 显示底部布局
                     */
                    mAdapter.showFooter();
                    if (mDataList.size() > 1) {
                        mRecyclerviewCommon.scrollToPosition(mDataList.size());
                    }

                    // 设置加载更多的标识
                    mLoadingMore = true;
                    // 设置参数
                    // mOffset++;  // 自增1  // 如果前面次失败的话，不能在这里自增1，会漏掉一页，需要在成功加载后才自增
                    // 去执行加载更多的网络请求
                    mPresenter.loadGuokeHot(mOffset);
                }
            }
        });
        // 设置RecycleView的adapter
        mAdapter = new GuokeAdapter(mContext, mDataList);
        mRecyclerviewCommon.setAdapter(mAdapter);
    }

    // SwipeRefreshLayout下拉刷新的回调方法
    @Override
    public void onRefresh() {
        if (mIsRefresh || mLoadingMore) {
            return; // 已经是下拉刷新状态了
        }

        // 将下拉刷新控件显示出来
        mSwiperefreshCommon.setRefreshing(true);
        // 设置下拉刷新状态标识
        mIsRefresh = true;
        // 重置参数
        mOffset = 0;
        // 去请求数据
        mPresenter.loadGuokeHot(mOffset);
    }

    // view层接口的3个实现方法

    @Override
    public void hideSwipeRefreshLayoutOrFooter() {
        // 如果是下拉刷新
        if (mIsRefresh) {
            mIsRefresh = false;
            mSwiperefreshCommon.setRefreshing(false);
        }

        // 如果是上拉加载更多
        if (mLoadingMore) {
            mLoadingMore = false;
            mAdapter.hideFooter();
        }
    }

    @Override
    public void updateSuccessData(List<GuokeHotItem> guokeHotItemslist) {
        if (mIsRefresh) {
            mAdapter.clear();
        }

        mAdapter.addMore(guokeHotItemslist);
        mOffset++;  // 只能在这里自增1   // 失败的情况不能自增
    }

    @Override
    public void showMsgWithLongToast(String errMessage) {
        Toast.makeText(mContext, errMessage, Toast.LENGTH_LONG).show();
    }

}


























