package com.savor.savorphone.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.DateUtil;
import com.common.api.utils.DensityUtil;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.utils.MyBitmapImageViewTarget;

import java.util.List;

/**
 * 点播列表
 * Created by hezd on 2017/8/10.
 */

public class VodListAdapter extends BaseAdapter {
    private static final float IMAGE_SCALE = 1242f/802f;
    private final Context mContext;
    private List<CommonListItem> mData;
    private OnPhoneClickListener mOnPhoneClickListener;
    private OnTvClickListener mOnTvClickListener;

    public VodListAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<CommonListItem> datas ) {
        this.mData = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData == null ?0:mData.size();
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
       Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.coverflow_item_view, null);
            holder.rl_pic_layout = (RelativeLayout) convertView.findViewById(R.id.rl_pic_layout);
            holder.ivChannelImg = (ImageView) convertView.findViewById(R.id.iv_channelImg);
            holder.btn_phone = (ImageView) convertView.findViewById(R.id.btn_phone);
            holder.btn_tv = (ImageView) convertView.findViewById(R.id.btn_tv);
            holder.pageNumberTotal = (TextView) convertView.findViewById(R.id.pageNumberTotal);
            holder.bottomPageNumber = (TextView) convertView.findViewById(R.id.bottomPageNumber);
            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_source = (TextView) convertView.findViewById(R.id.tv_source);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tv_len = (TextView) convertView.findViewById(R.id.tv_len);
            holder.page_num_layout = (RelativeLayout) convertView.findViewById(R.id.page_num_layout);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        ViewGroup.LayoutParams layoutParams = holder.ivChannelImg.getLayoutParams();
        int with = DensityUtil.getScreenWidth(mContext)-DensityUtil.dip2px(mContext,30);
        int height = (int) (with/IMAGE_SCALE);
        holder.rl_pic_layout.getLayoutParams().height = height;
        layoutParams.width = with;
        layoutParams.height = height;

        final CommonListItem item = (CommonListItem) getItem(position);
        String title = item.getTitle();
        String sourceName = item.getSourceName();
        String updateTime = item.getUpdateTime();
        String duration = item.getDuration();
        String time = DateUtil.formatSecondsTime(duration);
        holder.title.setText(title);
        holder.tv_date.setText(updateTime);
        holder.tv_len.setText(time);
        if(!TextUtils.isEmpty(sourceName)) {
            holder.tv_source.setVisibility(View.VISIBLE);
            holder.tv_source.setText(sourceName);
        }else {
            holder.tv_source.setVisibility(View.GONE);
        }
        String imageURL = item.getImageURL();
        Glide.with(mContext).load(imageURL)
                .asBitmap()
                .centerCrop()
                .into(new MyBitmapImageViewTarget(holder.ivChannelImg));
        holder.page_num_layout.setVisibility(View.GONE);

        holder.btn_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnPhoneClickListener!=null) {
                    mOnPhoneClickListener.onPhoneClick(item);
                }
            }
        });
        holder.btn_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnTvClickListener!=null) {
                    mOnTvClickListener.onTvClick(item);
                }
            }
        });
        return convertView;
    }

    public static class Holder {
        public RelativeLayout page_num_layout;
        ImageView ivChannelImg;
        public ImageView btn_phone;
        public ImageView btn_tv;
        public TextView bottomPageNumber;
        public TextView pageNumberTotal;
        public TextView title;
        public TextView tv_source;
        public TextView tv_date;
        public TextView tv_len;
        public RelativeLayout rl_pic_layout;
    }

    public void setOnPhoneClickListener(OnPhoneClickListener listener) {
        this.mOnPhoneClickListener = listener;
    }

    public void setOnTvClickListener(OnTvClickListener listener) {
        this.mOnTvClickListener = listener;
    }

    public interface OnPhoneClickListener {
        void onPhoneClick(CommonListItem item);
    }

    public interface OnTvClickListener {
        void onTvClick(CommonListItem item);
    }
}
