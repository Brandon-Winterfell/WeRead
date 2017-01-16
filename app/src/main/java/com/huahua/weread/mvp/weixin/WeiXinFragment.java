package com.huahua.weread.mvp.weixin;

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
import com.huahua.weread.bean.WeiXinNews;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/12/1.
 */

public class WeiXinFragment extends Fragment implements IWXView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerviewCommon)
    RecyclerView mRecyclerviewCommon;
    @BindView(R.id.swiperefreshCommon)
    SwipeRefreshLayout mSwiperefreshCommon;

    private Context mContext;
    private Unbinder mUnbinder;  // 用来在destroyView方法里解绑ButterKnife
    private WeiXinPresenter mPresenter;

    private int mCurrentpage = 1;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean mLoadingMore = false;
    private boolean mIsRefresh = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private ArrayList<WeiXinNews> mWeixinList = new ArrayList<WeiXinNews>();
    private WeiXinAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        mContext = null;   // 这里要置空吧
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
        super.onViewCreated(view, savedInstanceState);

        initMemberVariable();
        initView();

        // 调用下拉刷新
        onRefresh();
    }

    /**
     * 初始化成员变量/字段
     */
    private void initMemberVariable() {
        mPresenter = new WeiXinPresenter(this);  // 传入this构造presenter成员变量
    }

    /**
     * 初始化view控件
     * 设置下拉刷新控件及他的监听方法
     * 设置RecycleView控件
     */
    private void initView() {
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

                // 没有正在加载并且滑动到底部并且滑动已经停止
                // 并且目前没有正在执行下拉刷新 要同时满足这4个条件
                // 已经是正在加载更多，正在跟服务器交互的过程中了，网络慢的话不能一下子完成
                // 所以设置个变量，不能出现上一次还没有完成，就触发下一次请求
                if (!mLoadingMore
                        && newState == RecyclerView.SCROLL_STATE_IDLE
                        && (visibleItemCount + pastVisiblesItems) >= totalItemCount
                        && !mIsRefresh) {
                    /**
                     * 显示底部布局
                     */
                    mAdapter.showFooter();
                    if (mWeixinList.size() > 1) {
                        mRecyclerviewCommon.scrollToPosition(mWeixinList.size());
                    }

                    mLoadingMore = true;
                    mPresenter.loadData(mCurrentpage);
                }
            }
        });
        // 设置RecycleView的adapter
        // mAdapter = new WeiXinAdapter(mContext, mWeixinList);
        mAdapter = new WeiXinAdapter(mContext, mWeixinList);
        mRecyclerviewCommon.setAdapter(mAdapter);
    }

    /**
     * 下拉刷新控件的回调方法
     */
    @Override
    public void onRefresh() {
        // 如果有正在执行下拉刷新或者上拉加载更多，就直接return
        if (mIsRefresh || mLoadingMore) {
            return;
        }
        // 设置下拉状态
        mSwiperefreshCommon.setRefreshing(true);
        // 设置一个变量 表示下拉刷新的
        mIsRefresh = true;
        // 重置参数
        mCurrentpage = 1;

        // 然后再去请求数据
        mPresenter.loadData(mCurrentpage);
    }

    /**
     * 这3个方法是IWXView接口的实现方法
     */

    // hideSwipeRefreshLayoutOrFooter是不是更名叫onCompleteOrOnError更好点
    @Override
    public void hideSwipeRefreshLayoutOrFooter() {
        if (mIsRefresh) {
            mIsRefresh = false;
            mSwiperefreshCommon.setRefreshing(false);
        }

        if (mLoadingMore) {
            mLoadingMore = false;
            mAdapter.hideFooter();
        }
    }

    @Override
    public void updateSuccessData(List<WeiXinNews> wxNewslist) {
        if (mIsRefresh) { // 下拉刷新需要清空数据
           mAdapter.clear();
        }

        mAdapter.addMore(wxNewslist);
        mCurrentpage++;
    }

    @Override
    public void showMsgWithLongToast(String message) {

        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        mPresenter.unBindSubscriber();
        super.onDestroyView();
    }
}



















