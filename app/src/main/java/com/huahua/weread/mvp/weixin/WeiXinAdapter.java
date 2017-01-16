package com.huahua.weread.mvp.weixin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huahua.weread.ActivityWebView;
import com.huahua.weread.R;
import com.huahua.weread.application.MyApplication;
import com.huahua.weread.bean.WeiXinNews;
import com.huahua.weread.database.DBUtils;
import com.huahua.weread.database.WeReadDataBase;
import com.huahua.weread.mvp.BaseRecyclerViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/13.
 */

public class WeiXinAdapter extends BaseRecyclerViewAdapter<WeiXinNews> {

    public WeiXinAdapter(Context context, List<WeiXinNews> dataList) {
        super(context, dataList);
    }

    // getItemCount == >> getItemViewType == >> onCreateViewHolder == >> onBindViewHolder
    @Override
    public int getItemViewType(int position) {
        if (mIsShowFooter && isLastPosition(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_FOOTER:
                return createFooterViewHolder(parent);
            case TYPE_ITEM:
                return createWeiXinViewHolder(parent);
            default:
                throw new RuntimeException("there is no type that matches the type: "
                        + viewType
                        + ", make sure your using types correctly ");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_FOOTER:
                // 不用给footer的view绑定什么数据
                break;
            case TYPE_ITEM:
                bindNormalItem((WeiXinViewHolder)holder, mDataList.get(position));
                break;
        }
    }

    /**
     * 创建WeiXinViewHolder 普通的item数据视图
     * @param parent
     * @return
     */
    private WeiXinViewHolder createWeiXinViewHolder(ViewGroup parent) {
        View itemView = getView(parent, R.layout.weixin_item);
        final WeiXinViewHolder holder = new WeiXinViewHolder(itemView);

        /**
         * 整个item的点击事件
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;
                WeiXinNews newsItem = mDataList.get(position);

                DBUtils.getDB(mContext).insertHasRead(WeReadDataBase.TABLE_WEIXINJINGXUAN, newsItem.getUrl());
                holder.tvTitle.setTextColor(Color.GRAY);

                Intent intent = new Intent(mContext, ActivityWebView.class);
                intent.putExtra("url", newsItem.getUrl());
                intent.putExtra("title", newsItem.getTitle());
                mContext.startActivity(intent);
            }
        });

        holder.btnWeixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;
                final WeiXinNews newsItem = mDataList.get(position);

                // 创建PopupMenu，最后要调用show方法
                PopupMenu popupMenu = new PopupMenu(mContext, holder.btnWeixin);
                popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                popupMenu.getMenu().removeItem(R.id.pop_fav);
                final boolean isRead = DBUtils.getDB(mContext)
                        .isRead(WeReadDataBase.TABLE_WEIXINJINGXUAN, newsItem.getUrl());
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
                                            WeReadDataBase.TABLE_WEIXINJINGXUAN, newsItem.getUrl()
                                    );
                                    holder.tvTitle.setTextColor(Color.BLACK);
                                } else {
                                    DBUtils.getDB(mContext).insertHasRead(
                                            WeReadDataBase.TABLE_WEIXINJINGXUAN, newsItem.getUrl()
                                    );
                                    holder.tvTitle.setTextColor(Color.GRAY);
                                }
                                break;
                            case R.id.pop_share:
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, newsItem.getTitle() + " " + newsItem.getUrl() + mContext.getString(R.string.share_tail));
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

        return holder;
    }


    private void bindNormalItem(WeiXinViewHolder holder, WeiXinNews item) {
        // 然后给控件填上相应的信息
        if (DBUtils.getDB(mContext).isRead(WeReadDataBase.TABLE_WEIXINJINGXUAN, item.getUrl())) {
            holder.tvTitle.setTextColor(Color.GRAY);
        } else {
            holder.tvTitle.setTextColor(Color.BLACK);
        }

        holder.tvDescription.setText(item.getDescription());
        holder.tvTitle.setText(item.getTitle());
        holder.tvTime.setText(item.getCtime());
        // 对于图片 要判断图片链接是否为空 分两种情况
        if (!TextUtils.isEmpty(item.getPicUrl())) {
            // 图片加载不再封装一个统一入口了，感觉麻烦，这个项目又不大，没必要
            //Glide.with(mContext).load(weixinNews.getPicUrl()).into(holder.ivWeixin);
            // 改用Picasso
            MyApplication.getPicasso()
                    .load(item.getPicUrl())
                    .placeholder(R.color.image_place_holder)
                    .error(R.drawable.empty_photo)
                    .into(holder.ivWeixin);
        } else {
            holder.ivWeixin.setImageResource(R.drawable.bg);
        }

        /**
         * 进入加载动画
         */
        // 这个动画 快速滑动的时候会造成item漂浮起来 有问题
        // 重写onViewDetachedFromWindow解决 是动画的问题
        setItemAppearAnimation(holder, holder.getAdapterPosition(), R.anim.anim_bottom_in);
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
