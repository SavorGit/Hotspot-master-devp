package com.savor.savorphone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.DateUtil;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.MediaInfo;

import java.util.List;

import jp.wasabeef.glide.transformations.ColorFilterTransformation;

/**
 * 图片适配器
 * Created by hezd on 2017/7/10.
 */

public class MediaAdapter extends BaseAdapter {

    private final Context mContext;
    private List<MediaInfo> mMediaList;
    /**当前是否是编辑状态*/
    private boolean isEditState;

    public MediaAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<MediaInfo> mediaList) {
        this.mMediaList = mediaList;
        notifyDataSetChanged();
    }

    public void setEditState(boolean isEditState) {
        this.isEditState = isEditState;
        notifyDataSetChanged();
    }

    public boolean isEditState() {
        return isEditState;
    }

    public List<MediaInfo> getData() {
        return this.mMediaList;
    }


    @Override
    public int getCount() {
        return mMediaList == null?0:mMediaList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMediaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_media_layout,null);
            holder.image = (ImageView) convertView.findViewById(R.id.iv_pic);
            holder.time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_check);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        MediaInfo mediaInfo = mMediaList.get(position);
        if(mediaInfo!=null) {
            int mediaType = mediaInfo.getMediaType();
            if(mediaType == MediaInfo.MEDIA_TYPE_VIDEO) {
                if(isEditState) {
                    holder.time.setTextColor(mContext.getResources().getColor(R.color.color_888686));
                    Glide.with(mContext).
                            load(mediaInfo.getAssetpath()).
                            placeholder(R.drawable.kong_mrjz).
                            dontAnimate().
                            centerCrop().
                            bitmapTransform(new ColorFilterTransformation((mContext),0x99393536)).
                            into(holder.image);
                }else {
                    holder.time.setTextColor(mContext.getResources().getColor(R.color.color_text_list_item));
                    Glide.with(mContext).
                            load(mediaInfo.getAssetpath()).
                            placeholder(R.drawable.kong_mrjz).
                            dontAnimate().
                            centerCrop().
                            into(holder.image);
                }

                holder.time.setVisibility(View.VISIBLE);
                String time = DateUtil.formatSecondsTime(String.valueOf(mediaInfo.getAssetlength()/1000));
                holder.time.setText(time);
            }else {
                Glide.with(mContext).
                        load(mediaInfo.getAssetpath()).
                        placeholder(R.drawable.kong_mrjz).
                        dontAnimate().
                        centerCrop().
                        into(holder.image);
                holder.time.setVisibility(View.INVISIBLE);
            }
            if(isEditState) {
                holder.checkBox.setVisibility(mediaType == MediaInfo.MEDIA_TYPE_PIC?View.VISIBLE:View.INVISIBLE);
                holder.checkBox.setChecked(mediaInfo.isChecked());
            }else {
                holder.checkBox.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    public static class ViewHolder {
        public ImageView image;
        public TextView time;
        public CheckBox checkBox;
    }
}
