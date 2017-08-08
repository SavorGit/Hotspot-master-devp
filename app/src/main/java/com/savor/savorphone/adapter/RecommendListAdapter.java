package com.savor.savorphone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.DateUtil;
import com.common.api.utils.DensityUtil;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.CommonListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐列表适配器
 * Created by bichao on 2017/7/10.
 */

public class RecommendListAdapter extends BaseAdapter{
    private Context context;
    private List<CommonListItem> list = new ArrayList<>();
    public RecommendListAdapter(Context mContext,List<CommonListItem> list){
        this.context = mContext;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<CommonListItem> listRecommend){
        this.list = listRecommend;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView ==null){
            convertView = View.inflate(context, R.layout.item_recommend_list,null);
            holder = new ViewHolder();
            holder.recommend_img = (ImageView) convertView.findViewById(R.id.recommend_img);
            holder.recommend_tip = (TextView) convertView.findViewById(R.id.recommend_tip);
            holder.recommend_title = (TextView) convertView.findViewById(R.id.recommend_title);
            holder.recommend_source = (TextView) convertView.findViewById(R.id.recommend_source);
            holder.recommend_time = (TextView) convertView.findViewById(R.id.recommend_time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        CommonListItem item = list.get(position);

        if (item!=null){
            Glide.with(context)
                    .load(item.getImageURL())
                    .placeholder(R.drawable.kong_mrjz)
                    .error(R.drawable.kong_mrjz)
                    .crossFade()
                    .into(holder.recommend_img);
            if (item.getType()==2){
                holder.recommend_tip.setVisibility(View.VISIBLE);
                int pading = DensityUtil.dip2px(context,5);
                int pading25 = DensityUtil.dip2px(context,2.5f);
                holder.recommend_tip.setBackgroundResource(R.drawable.round_corner_translucent2);
                holder.recommend_tip.setPadding(pading,pading25,pading,pading25);
                holder.recommend_tip.setText(item.getColTuJi()+"图");
            }else if (item.getType()==3||item.getType()==4){
                holder.recommend_tip.setVisibility(View.VISIBLE);
                String time = DateUtil.formatSecondsCh(item.getDuration());
                int pading25 = DensityUtil.dip2px(context,2.5f);
                int pading10 = DensityUtil.dip2px(context,3);
                holder.recommend_tip.setBackgroundResource(R.drawable.round_corner_time);
                holder.recommend_tip.setPadding(pading10,pading25,pading10,pading25);
                holder.recommend_tip.setText(time);
            }else{
                holder.recommend_tip.setVisibility(View.GONE);
            }

            holder.recommend_title.setText(item.getTitle());
            holder.recommend_source.setText(item.getSourceName());
            holder.recommend_time.setText(item.getUpdateTime());
        }
        return convertView;
    }

    private class ViewHolder{
        private ImageView recommend_img;
        private TextView recommend_tip;
        private TextView recommend_title;
        private TextView recommend_source;
        private TextView recommend_time;
    }
}
