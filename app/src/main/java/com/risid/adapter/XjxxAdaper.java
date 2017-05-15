package com.risid.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.risid.models.XjxxModels;
import com.risid.urp.R;
import com.risid.util.ImageUtils;
import com.risid.util.Sp;
import com.risid.util.urlUtil;

import java.util.List;

import me.grantland.widget.AutofitTextView;

/**
 * Created by Risid on 2017/3/25.
 */

public class XjxxAdaper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<XjxxModels> lists;
    private static final int IMAGE = 0x00;
    private static final int TEXT = 0x01;
    private Sp sp;


    public XjxxAdaper(Context context, List<XjxxModels> lists){
        this.lists = lists;
        this.mContext = context;
        sp = new Sp(mContext);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == IMAGE){
            return new XjxxImageHolder(LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false));
        }else {
            return new XjxxHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof XjxxImageHolder){
            GlideUrl glideUrl = new GlideUrl(urlUtil.URL + urlUtil.URL_ZP, new LazyHeaders.Builder().addHeader("Cookie",sp.getCookie()).build());
            Glide.with(mContext).load(glideUrl).transform(new ImageUtils(mContext)).into(((XjxxImageHolder) holder).imageView);

        }else if (holder instanceof XjxxHolder){
            ((XjxxHolder) holder).tv_attr.setText(lists.get(position).getAtrr());
            ((XjxxHolder) holder).tv_value.setText(lists.get(position).getValue());
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return IMAGE;
        }else {
            return TEXT;
        }

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class XjxxHolder extends RecyclerView.ViewHolder{
        TextView tv_attr;
        AutofitTextView tv_value;

        public XjxxHolder(View itemView) {
            super(itemView);
            tv_attr = (TextView) itemView.findViewById(R.id.tv_attr);
            tv_value = (AutofitTextView) itemView.findViewById(R.id.tv_value);

        }
    }
    class XjxxImageHolder extends RecyclerView.ViewHolder{
        ImageView imageView;


        public XjxxImageHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_item);
        }
    }
}
