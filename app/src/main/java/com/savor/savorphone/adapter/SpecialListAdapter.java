package com.savor.savorphone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.DensityUtil;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.SpecialList;

import java.util.ArrayList;
import java.util.List;

/**
 * 专题组历史记录列表
 * Created by hezd on 2017/8/29.
 */

public class SpecialListAdapter extends BaseAdapter {
    public static final float IMAGE_SCALE = 432/670f;
    private final Context mContext;
    private List<SpecialList.SpecialListItem> mData = new ArrayList<>();

    public SpecialListAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<SpecialList.SpecialListItem> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void addData(List<SpecialList.SpecialListItem> data) {
        if(data!=null&&data.size()>0) {
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    public List<SpecialList.SpecialListItem> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData == null?0:mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
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
            holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        ViewGroup.LayoutParams layoutParams = holder.iv_image.getLayoutParams();
        int width = DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 20);
        layoutParams.width = width;
        layoutParams.height = (int) (width*IMAGE_SCALE);

        SpecialList.SpecialListItem item = (SpecialList.SpecialListItem) getItem(position);
        Glide.with(mContext).load(item.getImg_url()).placeholder(R.drawable.kong_mrjz).dontAnimate().into(holder.iv_image);

        holder.tv_special_title.setText(item.getName());

        holder.tv_little_title.setText(item.getTitle());

        holder.tv_special_desc.setText(item.getDesc());
        return convertView;
    }

    public class ViewHolder {
        public TextView tv_special_title;
        public ImageView iv_image;
        public TextView tv_little_title;
        public TextView tv_special_desc;
    }
}
