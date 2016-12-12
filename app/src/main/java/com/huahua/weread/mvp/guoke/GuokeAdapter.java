package com.huahua.weread.mvp.guoke;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.AnimRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huahua.weread.R;
import com.huahua.weread.application.MyApplication;
import com.huahua.weread.bean.GuokeHotItem;
import com.huahua.weread.database.DBUtils;
import com.huahua.weread.database.WeReadDataBase;
import com.huahua.weread.mvp.zhihu.ZhiHuArticleActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/7.
 */

public class GuokeAdapter extends RecyclerView.Adapter<GuokeAdapter.GuokeViewHolder> {

    //解决item状态混乱问题
    private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();

    private Context mContext;
    private ArrayList<GuokeHotItem> mDataList = new ArrayList<GuokeHotItem>();

    private int mLastPosition = -1;

    public GuokeAdapter(Context context, ArrayList<GuokeHotItem> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public GuokeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.guokehot_item, parent, false);
        GuokeViewHolder holder = new GuokeViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GuokeViewHolder holder, int position) {
        // 第一步 拿到对应位置的item数据
        final GuokeHotItem hotItem = mDataList.get(position);
        // 然后对应控件填数据

        if (DBUtils.getDB(mContext).isRead(WeReadDataBase.TABLE_GUOKERIMEN, hotItem.getId())) {
            holder.mTvTitle.setTextColor(Color.GRAY);
        } else {
            holder.mTvTitle.setTextColor(Color.BLACK);
        }

        holder.mTvTitle.setText(hotItem.getTitle());
        holder.mTvTime.setText(hotItem.getDate_published());
        holder.mTvDescription.setText(hotItem.getSummary());

        //Glide.with(mContext).load(hotItem.getSmall_image()).into(holder.mIvIthome);
        // 改用Picasso
        MyApplication.getPicasso()
                .load(hotItem.getSmall_image())
                .error(R.drawable.empty_photo)
                .into(holder.mIvIthome);

        // 整个itemView的点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBUtils.getDB(mContext).insertHasRead(WeReadDataBase.TABLE_GUOKERIMEN, hotItem.getId());
                holder.mTvTitle.setTextColor(Color.GRAY);

                Intent intent = new Intent(mContext, ZhiHuArticleActivity.class);
                intent.putExtra("type", ZhiHuArticleActivity.TYPE_GUOKR);
                intent.putExtra("id", hotItem.getId());
                intent.putExtra("title", hotItem.getTitle());
                mContext.startActivity(intent);
            }
        });

        if (mSparseBooleanArray.get(Integer.parseInt(hotItem.getId()))){
            holder.mBtnDetail.setBackgroundResource(R.drawable.ic_expand_less_black_24px);
            holder.mTvDescription.setVisibility(View.VISIBLE);
        }else{
            holder.mBtnDetail.setBackgroundResource(R.drawable.ic_expand_more_black_24px);
            holder.mTvDescription.setVisibility(View.GONE);
        }
        /**
         * 是否显示detail内容
         */
        holder.mBtnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mTvDescription.getVisibility() == View.GONE) {
                    holder.mBtnDetail.setBackgroundResource(R.drawable.ic_expand_less_black_24px);
                    holder.mTvDescription.setVisibility(View.VISIBLE);
                    mSparseBooleanArray.put(Integer.parseInt(hotItem.getId()), true);
                } else {
                    holder.mBtnDetail.setBackgroundResource(R.drawable.ic_expand_more_black_24px);
                    holder.mTvDescription.setVisibility(View.GONE);
                    mSparseBooleanArray.put(Integer.parseInt(hotItem.getId()), false);
                }
            }
        });

        // item的进入动画 从底部进入
        setItemAppearAnimation(holder, position, R.anim.anim_bottom_in);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    private void setItemAppearAnimation(RecyclerView.ViewHolder holder, int position, @AnimRes int type) {
        if (position > mLastPosition/* && !isFooterPosition(position)*/) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), type);
            holder.itemView.startAnimation(animation);
            mLastPosition = position;
        }
    }

    /**
     * 内部类的ViewHolder
     */
    class GuokeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_ithome)
        ImageView mIvIthome;
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.btn_detail)
        Button mBtnDetail;
        @BindView(R.id.tv_description)
        TextView mTvDescription;

        public GuokeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}





















