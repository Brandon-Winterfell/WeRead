package com.huahua.weread.mvp.guoke;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huahua.weread.R;
import com.huahua.weread.application.MyApplication;
import com.huahua.weread.bean.GuokeHotItem;
import com.huahua.weread.database.DBUtils;
import com.huahua.weread.database.WeReadDataBase;
import com.huahua.weread.mvp.BaseRecyclerViewAdapter;
import com.huahua.weread.mvp.zhihu.ZhiHuArticleActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/4.
 */

public class GuokeAdapter extends BaseRecyclerViewAdapter<GuokeHotItem> {

    //解决item状态混乱问题
    private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();

    /**
     * 构造方法
     * @param context
     * @param dataList
     */
    GuokeAdapter(Context context, List<GuokeHotItem> dataList) {
        super(context, dataList);
    }

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
                return createGuokeViewHolder(parent);
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
                bindNormalItem((GuokeViewHolder)holder, mDataList.get(position));
                break;
        }
    }

    /**
     * 为item项绑定数据、   为View(ViewHolder)设置数据
     * @param holder
     * @param hotItem
     */
    private void bindNormalItem(GuokeViewHolder holder, GuokeHotItem hotItem) {
        // guokeViewHolder.itemView.getContext() 那要看itemView是怎么初始化的了
        // 每个view初始化都需要一个context的，就算传进来context也没什么坏处吧
        /**
         * 人家google io 也是传context进来的 他传进来了一个activity --->> mHost
         */
        if (DBUtils.getDB(mContext).isRead(WeReadDataBase.TABLE_GUOKERIMEN, hotItem.getId())) {
            holder.mTvTitle.setTextColor(Color.GRAY);
        } else {
            holder.mTvTitle.setTextColor(Color.BLACK);
        }

        holder.mTvTitle.setText(hotItem.getTitle());
        holder.mTvTime.setText(hotItem.getDate_published());
        holder.mTvDescription.setText(hotItem.getSummary());

        // 改用Picasso
        MyApplication.getPicasso()
                .load(hotItem.getSmall_image())
                .placeholder(R.color.image_place_holder)
                .error(R.drawable.empty_photo)
                .into(holder.mIvIthome);

        if (mSparseBooleanArray.get(Integer.parseInt(hotItem.getId()))){
            holder.mBtnDetail.setBackgroundResource(R.drawable.ic_expand_less_black_24px);
            holder.mTvDescription.setVisibility(View.VISIBLE);
        }else{
            holder.mBtnDetail.setBackgroundResource(R.drawable.ic_expand_more_black_24px);
            holder.mTvDescription.setVisibility(View.GONE);
        }

        /**
         * item的进入动画  从右边进
         * 动画会造成问题 重写onViewDetachedFromWindow解决问题 已在父类重写
         */
        setItemAppearAnimation(holder, holder.getAdapterPosition(), R.anim.anim_right_in);
    }

    /**
     * 创建GuokeViewHolder
     * 给holder中的控件添加监听事件
     * @param parent
     * @return
     */
    private GuokeViewHolder createGuokeViewHolder(ViewGroup parent) {
        View view = getView(parent, R.layout.guokehot_item);
        final GuokeViewHolder holder = new GuokeViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    GuokeHotItem item = mDataList.get(position);
                    DBUtils.getDB(mContext).insertHasRead(WeReadDataBase.TABLE_GUOKERIMEN, item.getId());
                    holder.mTvTitle.setTextColor(Color.GRAY);

                    Intent intent = new Intent(mContext, ZhiHuArticleActivity.class);
                    intent.putExtra("type", ZhiHuArticleActivity.TYPE_GUOKR);
                    intent.putExtra("id", item.getId());
                    intent.putExtra("title", item.getTitle());
                    mContext.startActivity(intent);
                }
            });
        /**
         * 是否显示detail内容
         */
        holder.mBtnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;
                GuokeHotItem item = mDataList.get(position);
                if (holder.mTvDescription.getVisibility() == View.GONE) {
                    holder.mBtnDetail.setBackgroundResource(R.drawable.ic_expand_less_black_24px);
                    holder.mTvDescription.setVisibility(View.VISIBLE);
                    mSparseBooleanArray.put(Integer.parseInt(item.getId()), true);
                } else {
                    holder.mBtnDetail.setBackgroundResource(R.drawable.ic_expand_more_black_24px);
                    holder.mTvDescription.setVisibility(View.GONE);
                    mSparseBooleanArray.put(Integer.parseInt(item.getId()), false);
                }
            }
        });

        /**
         * item的进入动画  等下改下变成从右进
         * 动画会造成问题 滑动得很快的话，一些item项会浮起来
         * 重写onViewDetachedFromWindow解决问题 已在父类重写
         */
        // 写在这里的话，动画就不生效了
        // setItemAppearAnimation(holder, holder.getAdapterPosition(), R.anim.anim_right_in);

        return holder;
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

        GuokeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}



















