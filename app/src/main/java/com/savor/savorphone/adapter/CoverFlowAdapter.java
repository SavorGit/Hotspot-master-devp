package com.savor.savorphone.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.DateUtil;
import com.common.api.utils.DensityUtil;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.utils.MyBitmapImageViewTarget;
import com.savor.savorphone.widget.covorflow.ICoverFlowAdapter;

import java.util.List;

public class CoverFlowAdapter implements ICoverFlowAdapter {
    private List<CommonListItem> mArray;
    private static final float IMAGE_SCALE = 1242f/802f;
    private Context context;

    public CoverFlowAdapter(Context context, List<CommonListItem> mArray) {
        this.context = context;
        this.mArray = mArray;
    }

    public CoverFlowAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<CommonListItem> mArray) {
        this.mArray = mArray;
    }

    @Override
    public int getCount() {
        return mArray == null ? 0 : mArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mArray.get(position);
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
            convertView = View.inflate(context, R.layout.coverflow_item_view, null);
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
            convertView.setTag(holder);
        }
        holder = (Holder) convertView.getTag();
        ViewGroup.LayoutParams layoutParams = holder.ivChannelImg.getLayoutParams();
        int with = DensityUtil.getScreenWidth(context)-DensityUtil.dip2px(context,30);
        int height = (int) (with/IMAGE_SCALE);
        holder.rl_pic_layout.getLayoutParams().height = height;
        layoutParams.width = with;
        layoutParams.height = height;

        return convertView;
    }

    @Override
    public void getData(View view, int position) {
        if(view!=null) {
            Holder holder = (Holder) view.getTag();
            CommonListItem channelBean = mArray.get(position);
            if(channelBean!=null) {
                String title = channelBean.getTitle();
                String sourceName = channelBean.getSourceName();
                String updateTime = channelBean.getUpdateTime();
                String duration = channelBean.getDuration();
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
                Glide.with(context)
                        .load(channelBean.getImageURL())
                        .asBitmap()
                        .placeholder(R.drawable.kong_mrjz)
                        .dontAnimate()
                        .centerCrop()
                        .into(new MyBitmapImageViewTarget(holder.ivChannelImg));
                holder.bottomPageNumber.setText((position+1)+"");
                holder.pageNumberTotal.setText(getCount()+"");
            }
        }

    }

    public static class Holder {
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

}
