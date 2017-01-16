package com.huahua.weread.mvp.zhihu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huahua.weread.R;
import com.huahua.weread.application.MyApplication;
import com.huahua.weread.bean.ZhiHuDailyItem;
import com.huahua.weread.database.DBUtils;
import com.huahua.weread.database.WeReadDataBase;
import com.huahua.weread.mvp.BaseRecyclerViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/13.
 */

public class ZhiHuAdapter extends BaseRecyclerViewAdapter<ZhiHuDailyItem> {

    public ZhiHuAdapter(Context context, List<ZhiHuDailyItem> dataList) {
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
                return createZhiHuViewHolder(parent);
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
                bindNormalItem((ZhiHuViewHolder)holder, mDataList.get(position));
                break;
        }
    }

    /**
     * 创建ZhiHuViewHolder 普通的item数据视图
     * @param parent
     * @return
     */
    private ZhiHuViewHolder createZhiHuViewHolder(ViewGroup parent) {
        View itemView = getView(parent, R.layout.zhihu_daily_item);
        final ZhiHuViewHolder holder = new ZhiHuViewHolder(itemView);

        // 整个itemView的点击事件 然后itemView里的一个子view（btnZhihu）也有点击事件 弹出PopupMenu
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;
                ZhiHuDailyItem item = mDataList.get(position);

                DBUtils.getDB(mContext).insertHasRead(WeReadDataBase.TABLE_ZHIHURIBAO, item.getId());
                holder.mTvZhihuDaily.setTextColor(Color.GRAY);

                Intent intent = new Intent(mContext, ZhiHuArticleActivity.class);
                intent.putExtra("type", ZhiHuArticleActivity.TYPE_ZHIHU);
                intent.putExtra("id", item.getId() );
                intent.putExtra("title", item.getTitle());
                mContext.startActivity(intent);
            }
        });

        holder.mBtnZhihu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;
                final ZhiHuDailyItem dailyItem = mDataList.get(position);

                // 创建PopupMenu，最后要调用show方法
                PopupMenu popupMenu = new PopupMenu(mContext, holder.mBtnZhihu);
                popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                popupMenu.getMenu().removeItem(R.id.pop_fav);
                final boolean isRead = DBUtils.getDB(mContext)
                        .isRead(WeReadDataBase.TABLE_ZHIHURIBAO, dailyItem.getId());
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
                                            WeReadDataBase.TABLE_ZHIHURIBAO, dailyItem.getId()
                                    );
                                    holder.mTvZhihuDaily.setTextColor(Color.BLACK);
                                } else {
                                    DBUtils.getDB(mContext).insertHasRead(
                                            WeReadDataBase.TABLE_ZHIHURIBAO, dailyItem.getId()
                                    );
                                    holder.mTvZhihuDaily.setTextColor(Color.GRAY);
                                }
                                break;
                            case R.id.pop_share:
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, dailyItem.getTitle() + " " + dailyItem.getId() + mContext.getString(R.string.share_tail));
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


    private void bindNormalItem(ZhiHuViewHolder holder, ZhiHuDailyItem item) {
        // 拿到后就好办啦，然后就是各种set数据在对应的控件上
        // 根据是否已经阅读过，设置标题颜色
        if (DBUtils.getDB(mContext).isRead(WeReadDataBase.TABLE_ZHIHURIBAO, item.getId())) {
            holder.mTvZhihuDaily.setTextColor(Color.GRAY);
        } else {
            holder.mTvZhihuDaily.setTextColor(Color.BLACK);
        }
        holder.mTvZhihuDaily.setText(item.getTitle());
        holder.mTvTime.setText(item.getDate());
        if (item.getImages() != null) {
            //Glide.with(mContext).load(dailyItem.getImages()[0]).into(holder.mIvZhihuDaily);
            // 改用Picasso
            MyApplication.getPicasso()
                    .load(item.getImages()[0])
                    .placeholder(R.color.image_place_holder)
                    .error(R.drawable.empty_photo)
                    .into(holder.mIvZhihuDaily);
        } else {
            holder.mIvZhihuDaily.setImageResource(R.drawable.bg);
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
    class ZhiHuViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_zhihu_daily)
        ImageView mIvZhihuDaily;
        @BindView(R.id.tv_zhihu_daily)
        TextView mTvZhihuDaily;
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.btn_zhihu)
        Button mBtnZhihu;

        public ZhiHuViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
