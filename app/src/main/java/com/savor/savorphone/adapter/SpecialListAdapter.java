package com.savor.savorphone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.savor.savorphone.R;

/**
 * 专题组历史记录列表
 * Created by hezd on 2017/8/29.
 */

public class SpecialListAdapter extends BaseAdapter {
    private final Context mContext;

    public SpecialListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_special_list,null);
            holder = new ViewHolder();
            holder.tv_little_title = (TextView) convertView.findViewById(R.id.tv_little_title);
            holder.tv_special_desc = (TextView) convertView.findViewById(R.id.tv_special_desc);
            holder.tv_special_title = (TextView) convertView.findViewById(R.id.tv_special_title);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    public class ViewHolder {
        public TextView tv_special_title;
        public ImageView iv_image;
        public TextView tv_little_title;
        public TextView tv_special_desc;
    }
}
