package com.risid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.risid.models.MainModels;
import com.risid.urp.R;

import java.util.List;

/**
 * Created by risid on 2015/9/20.
 */
public class MianAdapter extends BaseAdapter {
    private List<MainModels> lists;
    private Context context;


    public MianAdapter(List<MainModels> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.layout_main, parent,false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_main);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_main);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageResource(Integer.parseInt(lists.get(position).getImg()));
        viewHolder.textView.setText(lists.get(position).getName());

        return convertView;
    }


    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
