package com.huahua.weread.mvp.weixin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.AnimRes;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huahua.weread.ActivityWebView;
import com.huahua.weread.MainActivity;
import com.huahua.weread.R;
import com.huahua.weread.application.MyApplication;
import com.huahua.weread.bean.WeiXinNews;
import com.huahua.weread.bean.WeiXinResponse;
import com.huahua.weread.database.DBUtils;
import com.huahua.weread.database.WeReadDataBase;
import com.huahua.weread.utils.ScreenUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/2.
 */

public class WeiXinAdapter extends RecyclerView.Adapter<WeiXinAdapter.WeiXinViewHolder> {

    private ArrayList<WeiXinNews> mWeixinNewsList;
    private Context mContext;

    private int mLastPosition = -1;

    /**
     * 构造方法需要两个参数
     * @param context
     * @param list
     */
    public WeiXinAdapter(Context context, ArrayList<WeiXinNews> list) {
        mContext = context;
        mWeixinNewsList = list;
    }

    @Override
    public WeiXinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /**
         * onCreateViewHolder
         * 见名知意  就是创建ViewHolder并返回
         *      下一步 onBindViewHolder 在view控件上填充数据
         */
        return new WeiXinViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.weixin_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(final WeiXinViewHolder holder, int position) {
        // 拿到某个位置新闻条目的数据
        final WeiXinNews weixinNews = mWeixinNewsList.get(position);
        // 然后给控件填上相应的信息
        if (DBUtils.getDB(mContext).isRead(WeReadDataBase.TABLE_WEIXINJINGXUAN, weixinNews.getUrl())) {
            holder.tvTitle.setTextColor(Color.GRAY);
        } else {
            holder.tvTitle.setTextColor(Color.BLACK);
        }

        holder.tvDescription.setText(weixinNews.getDescription());
        holder.tvTitle.setText(weixinNews.getTitle());
        holder.tvTime.setText(weixinNews.getCtime());
        // 对于图片 要判断图片链接是否为空 分两种情况
        if (!TextUtils.isEmpty(weixinNews.getPicUrl())) {
            // 图片加载不再封装一个统一入口了，感觉麻烦，这个项目又不大，没必要
            //Glide.with(mContext).load(weixinNews.getPicUrl()).into(holder.ivWeixin);
            // 改用Picasso
            MyApplication.getPicasso()
                    .load(weixinNews.getPicUrl())
                    .error(R.drawable.empty_photo)
                    .into(holder.ivWeixin);
        } else {
            holder.ivWeixin.setImageResource(R.drawable.bg);
        }

        /**
         * 进入加载动画
         */
        setItemAppearAnimation(holder, position, R.anim.anim_bottom_in);
        // runEnterAnimation(holder.itemView, position);

        holder.btnWeixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建PopupMenu，最后要调用show方法
                PopupMenu popupMenu = new PopupMenu(mContext, holder.btnWeixin);
                popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                popupMenu.getMenu().removeItem(R.id.pop_fav);
                final boolean isRead = DBUtils.getDB(mContext)
                        .isRead(WeReadDataBase.TABLE_WEIXINJINGXUAN, weixinNews.getUrl());
                /**
                 * 设置menu的title
                 */
                if (!isRead) { // 如果没有阅读过
                    // pop menu 的 item title 就是 '标记为已读'
                    popupMenu.getMenu().findItem(R.id.pop_unread).setTitle(R.string.common_set_read);
                } else {
                    popupMenu.getMenu().findItem(R.id.pop_unread).setTitle(R.string.common_set_unread);
                }
                /**
                 * 设置menu 的点击事件
                 */
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.pop_unread:
                                if (isRead) {
                                    DBUtils.getDB(mContext).deleteHasRead(
                                            WeReadDataBase.TABLE_WEIXINJINGXUAN, weixinNews.getUrl()
                                    );
                                    holder.tvTitle.setTextColor(Color.BLACK);
                                } else {
                                    DBUtils.getDB(mContext).insertHasRead(
                                            WeReadDataBase.TABLE_WEIXINJINGXUAN, weixinNews.getUrl()
                                    );
                                    holder.tvTitle.setTextColor(Color.GRAY);
                                }
                                break;
                            case R.id.pop_share:
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, weixinNews.getTitle() + " " + weixinNews.getUrl() + mContext.getString(R.string.share_tail));
                                shareIntent.setType("text/plain");
                                //设置分享列表的标题，并且每次都显示分享列表
                                mContext.startActivity(Intent.createChooser(shareIntent, mContext.getString(R.string.share)));
                                break;

                        }

                        return true;
                    }
                });
                // show出来
                popupMenu.show();
            }
        });

        /**
         * 整个item的点击事件
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBUtils.getDB(mContext).insertHasRead(WeReadDataBase.TABLE_WEIXINJINGXUAN, weixinNews.getUrl());
                holder.tvTitle.setTextColor(Color.GRAY);

                Intent intent = new Intent(mContext, ActivityWebView.class);
                intent.putExtra("url", weixinNews.getUrl());
                intent.putExtra("title", weixinNews.getTitle());
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mWeixinNewsList.size();
    }

    private void setItemAppearAnimation(RecyclerView.ViewHolder holder, int position, @AnimRes int type) {
        if (position > mLastPosition/* && !isFooterPosition(position)*/) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), type);
            holder.itemView.startAnimation(animation);
            mLastPosition = position;
        }
    }

    private void runEnterAnimation(View view, int position) {
        view.setTranslationY(ScreenUtil.getScreenHight(mContext));
        view.animate()
                .translationY(0)
                .setStartDelay(100 * (position % 5))
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }


    /**
     * 内部类的ViewHolder
     */
    class WeiXinViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_weixin)
        ImageView ivWeixin;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.btn_weixin)
        Button btnWeixin;

        /**
         * 构造方法需要传入一个View  (将layout文件inflate后)
         * @param itemView
         */
        public WeiXinViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}




























