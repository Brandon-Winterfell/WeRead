package com.huahua.weread.mvp.zhihu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.AnimRes;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
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

import com.huahua.weread.R;
import com.huahua.weread.application.MyApplication;
import com.huahua.weread.bean.ZhiHuDailyItem;
import com.huahua.weread.database.DBUtils;
import com.huahua.weread.database.WeReadDataBase;
import com.huahua.weread.utils.ScreenUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/6.
 */

public class ZhiHuAdapter extends RecyclerView.Adapter<ZhiHuAdapter.ZhiHuViewHolder> {

    private ArrayList<ZhiHuDailyItem> mDataList;
    private Context mContext;

    private int mLastPosition = -1;

    public ZhiHuAdapter(Context context, ArrayList<ZhiHuDailyItem> datalist) {
        mContext = context;
        mDataList = datalist;
    }

    @Override
    public ZhiHuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 分开写就很清晰了 view》》holder》》return
        // 需要return 一个ViewHolde
        //      那就new 一个ViewHolder  ViewHolder的构造方法需要一个view
        //          那就inflate一个view出来  要inflate一个view  需要一个xml的layout文件
        View view = LayoutInflater.from(mContext).inflate(R.layout.zhihu_daily_item, parent, false);
        ZhiHuViewHolder holder = new ZhiHuViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ZhiHuViewHolder holder, int position) {
        // 拿到数据集某个位置position的dailyItem
        final  ZhiHuDailyItem dailyItem = mDataList.get(position);
        // 拿到后就好办啦，然后就是各种set数据在对应的控件上
        // 根据是否已经阅读过，设置标题颜色
        if (DBUtils.getDB(mContext).isRead(WeReadDataBase.TABLE_ZHIHURIBAO, dailyItem.getId())) {
            holder.mTvZhihuDaily.setTextColor(Color.GRAY);
        } else {
            holder.mTvZhihuDaily.setTextColor(Color.BLACK);
        }
        holder.mTvZhihuDaily.setText(dailyItem.getTitle());
        holder.mTvTime.setText(dailyItem.getDate());
        if (dailyItem.getImages() != null) {
            //Glide.with(mContext).load(dailyItem.getImages()[0]).into(holder.mIvZhihuDaily);
            // 改用Picasso
            MyApplication.getPicasso()
                    .load(dailyItem.getImages()[0])
                    .error(R.drawable.empty_photo)
                    .into(holder.mIvZhihuDaily);
        }
        // 整个itemView的点击事件 然后itemView里的一个子view（btnZhihu）也有点击事件 弹出PopupMenu
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBUtils.getDB(mContext).insertHasRead(WeReadDataBase.TABLE_ZHIHURIBAO, dailyItem.getId());
                holder.mTvZhihuDaily.setTextColor(Color.GRAY);

                Intent intent = new Intent(mContext, ZhiHuArticleActivity.class);
                intent.putExtra("type", ZhiHuArticleActivity.TYPE_ZHIHU);
                intent.putExtra("id", dailyItem.getId() );
                intent.putExtra("title", dailyItem.getTitle());
                mContext.startActivity(intent);
            }
        });

        holder.mBtnZhihu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        // 这动画有意思 有点不足
        //runEnterAnimation(holder.itemView);

        setItemAppearAnimation(holder, position, R.anim.anim_bottom_in);
    }

    private void setItemAppearAnimation(RecyclerView.ViewHolder holder, int position, @AnimRes int type) {
        if (position > mLastPosition/* && !isFooterPosition(position)*/) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), type);
            holder.itemView.startAnimation(animation);
            mLastPosition = position;
        }
    }

    private void runEnterAnimation(View view) {
        view.setTranslationX(ScreenUtil.getScreenWidth(mContext));
        view.animate()
                .translationX(0)
                .setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


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






























