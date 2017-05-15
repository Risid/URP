package com.risid.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.risid.models.TzggModels;
import com.risid.urp.R;
import com.risid.urp.TzggActivity;
import com.risid.urp.TzggDetailActivity;

import java.util.List;

/**
 * Created by Risid on 2017/3/25.
 */

public class TzggAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<TzggModels> lists;

    public TzggAdapter(Context context, List<TzggModels> lists){
        this.mContext = context;
        this.lists = lists;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TzggViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false));
    }

    


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((TzggViewHolder) holder).tv_news_title.setText(lists.get(position).getTitle());
        ((TzggViewHolder) holder).tv_news_time.setText(lists.get(position).getTime());

        ((TzggViewHolder) holder).fl_tzgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("URL", lists.get(holder.getAdapterPosition()).getUrl());
                intent.setClass(mContext, TzggDetailActivity.class);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
    class TzggViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_news_title;
        private TextView tv_news_time;
        private FrameLayout fl_tzgg;



        public TzggViewHolder(View itemView) {
            super(itemView);
            tv_news_time = (TextView) itemView.findViewById(R.id.tv_news_time);
            tv_news_title = (TextView) itemView.findViewById(R.id.tv_news_title);
            fl_tzgg = (FrameLayout) itemView.findViewById(R.id.fl_tzgg);

        }
    }
}
