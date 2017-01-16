package com.huahua.weread.mvp.gankio;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huahua.weread.R;
import com.huahua.weread.application.MyApplication;
import com.huahua.weread.bean.GankioFuliItem;
import com.huahua.weread.mvp.BaseRecyclerViewAdapter;
import com.huahua.weread.mvp.gankio.fuliGallery.ActivityImageView;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/14.
 */

public class GankioAdapter extends BaseRecyclerViewAdapter<GankioFuliItem> {

    public GankioAdapter(Context context, List<GankioFuliItem> dataList) {
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
                return createGankioViewHolder(parent);
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
                bindNormalItem((GankioViewHolder)holder, mDataList.get(position));
                break;
        }
    }

    private GankioViewHolder createGankioViewHolder(ViewGroup parent) {
        View view = getView(parent, R.layout.gankio_fuli_item);
        final GankioViewHolder holder = new GankioViewHolder(view);

        // 整个itemView的点击事件 现在就只有一个ImageView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                Intent intent = new Intent(mContext, ActivityImageView.class);
                intent.putExtra("girlist", (Serializable)mDataList);
                intent.putExtra("currentPosition", position);
                mContext.startActivity(intent);

            }
        });

        return holder;
    }

    private void bindNormalItem(GankioViewHolder holder, GankioFuliItem item) {
        // 给itemView的子控件设置数据
        holder.mItemFuliTv.setText(item.getPublishedAt());
        //Glide.with(mContext).load(fuliItem.getUrl()).into(holder.mItemFuliIv);
        // 改用Picasso
        MyApplication.getPicasso()
                .load(item.getUrl())
                .error(R.drawable.empty_photo)
                .into(holder.mItemFuliIv);
    }

    /**
     * 内部类存在的ViewHolder
     */
    class GankioViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_view)
        CardView mCardView;
        @BindView(R.id.item_fuli_iv)
        ImageView mItemFuliIv;
        @BindView(R.id.item_fuli_tv)
        TextView mItemFuliTv;

        private GankioViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}





















