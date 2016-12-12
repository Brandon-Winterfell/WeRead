package com.huahua.weread.mvp.gankio;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huahua.weread.R;
import com.huahua.weread.application.MyApplication;
import com.huahua.weread.bean.GankioFuliItem;
import com.huahua.weread.mvp.gankio.fuliGallery.ActivityImageView;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/8.
 */

public class GankioAdapter extends RecyclerView.Adapter<GankioAdapter.GankioViewHolder> {

    private Context mContext;
    private ArrayList<GankioFuliItem> mDataList;

    public GankioAdapter(Context context, ArrayList<GankioFuliItem> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public GankioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gankio_fuli_item, parent, false);
        GankioViewHolder holder = new GankioViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(GankioViewHolder holder, final int position) {
        // 首先拿到对应位置的单个item数据
        GankioFuliItem fuliItem = mDataList.get(position);
        // 然后就是设置到view控件上
        holder.mItemFuliTv.setText(fuliItem.getPublishedAt());
        //Glide.with(mContext).load(fuliItem.getUrl()).into(holder.mItemFuliIv);
        // 改用Picasso
        MyApplication.getPicasso()
                .load(fuliItem.getUrl())
                .error(R.drawable.empty_photo)
                .into(holder.mItemFuliIv);
        // 整个itemView的点击事件 现在就只有一个ImageView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ActivityImageView.class);
                intent.putExtra("girlist", (Serializable)mDataList);
                intent.putExtra("currentPosition", position);
                mContext.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
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






















