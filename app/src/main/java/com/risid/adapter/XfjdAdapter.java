package com.risid.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.risid.models.XfjdModels;
import com.risid.urp.R;

import java.util.List;

import me.grantland.widget.AutofitTextView;


/**
 * Created by Risid on 2017/3/25.
 */

public class XfjdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> lists;
    private String arr[] = XfjdModels.xfjdArray;

    public XfjdAdapter(Context context, List<String> lists){
        this.mContext = context;
        this.lists = lists;


    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new XfjdHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((XfjdHolder) holder).tv_attr.setText(arr[position]);
        ((XfjdHolder) holder).tv_value.setText(lists.get(position));

    }

    @Override
    public int getItemCount() {
        return arr.length;
    }
    class XfjdHolder extends RecyclerView.ViewHolder{
        TextView tv_attr;
        AutofitTextView tv_value;

        public XfjdHolder(View itemView) {
            super(itemView);
            tv_attr = (TextView) itemView.findViewById(R.id.tv_attr);
            tv_value = (AutofitTextView) itemView.findViewById(R.id.tv_value);

        }
    }
}
