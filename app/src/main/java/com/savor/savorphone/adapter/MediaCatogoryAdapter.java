package com.savor.savorphone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.MediaInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by Wxcily on 15/11/6.
 */
public class MediaCatogoryAdapter extends BaseAdapter {

    private Context context;
    List<Map<String, List<MediaInfo>>> mAllList;

    private int selected = 0;

    public MediaCatogoryAdapter(Context context) {
        this.context = context;
    }

    public MediaCatogoryAdapter(Context context, List<Map<String, List<MediaInfo>>> AllList) {
        this.context = context;
        this.mAllList = AllList;
    }

    public List<Map<String, List<MediaInfo>>> getmAllList() {
        return mAllList;
    }

    public void setData(List<Map<String, List<MediaInfo>>> allList) {
        mAllList = allList;
        notifyDataSetChanged();
    }

    public void setSelected(int position) {
        this.selected = position;
        this.notifyDataSetChanged();
    }

    public Map<String,List<MediaInfo>> getDataByPosition(int position){
        if(position<mAllList.size()) {
            return mAllList.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return mAllList ==null?0: mAllList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAllList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder holder;
        if (convertView == null) {
            holder = new ViewHoder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_bottom_list_dialog, parent, false);
            holder.countTv = (TextView) convertView.findViewById(R.id.tv_media_count);
            holder.nameTv = (TextView) convertView.findViewById(R.id.tv_media_name);
            holder.placeHolderIv = (ImageView) convertView.findViewById(R.id.iv_place_holder);
            convertView.setTag(holder);
        }else {
            holder = (ViewHoder) convertView.getTag();
        }

        Map<String, List<MediaInfo>> listMap = mAllList.get(position);
        String name = (String) listMap.keySet().toArray()[0];
        List<MediaInfo> imageList = listMap.get(name);
        if(imageList!=null&&imageList.size()>0&&imageList.get(0)!=null) {
            Glide.with(context).
                    load(imageList.get(0).getAssetpath()).
                    centerCrop().
                    placeholder(R.drawable.kong_mrjz).
                    dontAnimate().
                    into(holder.placeHolderIv);
            holder.countTv.setText(imageList.size()+"");
        }else {
            Glide.with(context).
                    load(R.drawable.kong_mrjz).
                    centerCrop().
                    placeholder(R.drawable.kong_mrjz).
                    dontAnimate().
                    into(holder.placeHolderIv);
            holder.countTv.setText("0");
        }
        holder.nameTv.setText(name);

        return convertView;
    }

    public class ViewHoder {
        public ImageView placeHolderIv;
        public TextView nameTv;
        public TextView countTv;
    }
}
