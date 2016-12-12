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
import android.widget.ProgressBar;
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
    @BindView(R.id.progressbarCommon)
    ProgressBar mProgressbarCommon;

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
                    if (mWeixinList.size() > 1) {
                        mRecyclerviewCommon.scrollToPosition(mWeixinList.size() - 1);
                    }
                }
            }
        });
        // 设置RecycleView的adapter
        mAdapter = new WeiXinAdapter(mContext, mWeixinList);
        mRecyclerviewCommon.setAdapter(mAdapter);

        /**
         * 界面显示progressbar  放到后面好点  不应该show这个吧 直接显示下拉刷新那个空间
         */
        showProgressDialog();

        /**
         * presenter要出场了  其实调用下拉刷新控件 的刷新方法更好吧
         */
        //mPresenter.loadData(mCurrentpage);
        // 调用下拉刷新
        onRefresh();
    }

    /**
     * 这四个方法是IWXView接口的实现方法
     */
    @Override
    public void showProgressDialog() {
        if (mProgressbarCommon != null) {
            mProgressbarCommon.setVisibility(View.VISIBLE);
        }
    }

    // 不管成功或者失败都调用这个方法的，那就可以在这里设置一些共有的变量了 mLoadingMore
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
    public void updateSuccessData(List<WeiXinNews> wxNewslist) {
        if (mIsRefresh) { // 下拉刷新需要清空数据
            mIsRefresh = false; // 表示下拉刷新的  // 这个一定要放到这里 不能放到hideProgressDialog里
            mWeixinList.clear();
            mAdapter.notifyDataSetChanged();
        }
        mCurrentpage++;
        mWeixinList.addAll(wxNewslist);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadFailMsg(String errMessage) {
        mIsRefresh = false; // 表示下拉刷新的

        Toast.makeText(mContext, "发生错误：" + errMessage, Toast.LENGTH_SHORT).show();
    }

    /**
     * 下拉刷新控件的回调方法
     */
    @Override
    public void onRefresh() {
        if (mIsRefresh) {
            return;
        }
        // 设置下拉状态
        mSwiperefreshCommon.setRefreshing(true);
        // 重置参数
        mCurrentpage = 1;
        // 不应该在这里清空的，在回调里清空，
        // 不然一下子就没数据，然后网络死慢的话，就一直空白了
        // 设置一个变量 表示下拉刷新的
        mIsRefresh = true;
//        mWeixinList.clear();
//        mAdapter.notifyDataSetChanged();
        // 然后再去请求数据
        mPresenter.loadData(mCurrentpage);
    }

    /**
     * 滑动到底部加载更多监听事件的回调
     */
    private void onLoadMore() {
        // 只有加载更多才showProgress，因为下拉刷新控件本来就是一个旋转进度条
        // 同样这样如果网络慢的话，进度条就一直在界面上显示了
        // 草 应该放到底部的 应该加一项底部布局
        showProgressDialog();
        mPresenter.loadData(mCurrentpage);
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        mPresenter.unBindSubscriber();
        super.onDestroyView();
    }
}



















