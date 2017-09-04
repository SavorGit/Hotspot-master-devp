package com.savor.savorphone.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.Polygon;
import com.bumptech.glide.Glide;
import com.common.api.utils.DateUtil;
import com.common.api.utils.DensityUtil;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.CommonListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/4.
 */

public class WealthLifeAdapter extends BaseAdapter{
    private Context context;
    private List<CommonListItem> list = new ArrayList<>();
    //为三种布局定义一个标识
    // item view的类型总数。
    private final int VIEW_TYPE_COUNT = 3;
    /**图文*/
    private final int TYPE_TEXT_IMAGE = 0;
    /**图文*/
    private final int TYPE_TEXT_IMAGE_BIG = 2;
    /**视频*/
    private final int TYPE_VIDEO = 1;
    public WealthLifeAdapter(Context mcontext,List<CommonListItem> listItems){
        this.context = mcontext;
        this.list = listItems;
    }

    public List<CommonListItem> getData() {
        return list;
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

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = -1;
        if (position==0){
            return  viewType;
        }
        CommonListItem item = list.get(position);
        int type = Integer.valueOf(item.getType());
        String imgStyle = item.getImgStyle();
        switch (type){
            case 0:
            case 1:
                if ("2".equals(imgStyle)) {
                    viewType = TYPE_TEXT_IMAGE_BIG;
                }else {
                    viewType = TYPE_TEXT_IMAGE;
                }

                break;
            case 2:
            case 3:
            case 4:
                viewType = TYPE_VIDEO;
                break;
        }
        return viewType;
    }

    public void setData(List<CommonListItem> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position==0){
            View view = handleHeaderData();
            return  view;
        }
        ViewHolderSmall holderSmall = null;
        ViewHolderBig holderBig = null;
        CommonListItem item = (CommonListItem) getItem(position);
        int type = getItemViewType(position);
        switch (type){
            case TYPE_TEXT_IMAGE:
                if (convertView==null){
                    holderSmall = new ViewHolderSmall();
                    convertView = View.inflate(context, R.layout.item_wealth_life_small,null);
                    holderSmall.contentSmallImgIV = (ImageView) convertView.findViewById(R.id.content_small_img);
                    holderSmall.contentSmallTitleTV = (TextView) convertView.findViewById(R.id.content_small_title);
                    holderSmall.contentSmallSourceTV = (TextView) convertView.findViewById(R.id.content_small_source);
                    holderSmall.contentSmallTimeTV = (TextView) convertView.findViewById(R.id.content_small_time);
                    convertView.setTag(holderSmall);
                }else{
                    holderSmall = (ViewHolderSmall) convertView.getTag();
                }
                Glide.with(context)
                        .load(item.getImageURL())
                        .placeholder(R.drawable.kong_mrjz)
                        .error(R.drawable.kong_mrjz)
                        .centerCrop()
                        .crossFade()
                        .into(holderSmall.contentSmallImgIV);
                holderSmall.contentSmallTitleTV.setText(item.getTitle());
                String sourceName = item.getSourceName();
                if(TextUtils.isEmpty(sourceName)) {
                    holderSmall.contentSmallSourceTV.setVisibility(View.GONE);
                }else{
                    holderSmall.contentSmallSourceTV.setVisibility(View.VISIBLE);
                    holderSmall.contentSmallSourceTV.setText(item.getSourceName());
                }

                holderSmall.contentSmallTimeTV.setText(item.getUpdateTime());
                break;
            case TYPE_VIDEO:
                if (convertView==null){
                    holderBig = new ViewHolderBig();
                    convertView = View.inflate(context,R.layout.item_wealth_life_big,null);
                    holderBig.contentBigTitleTV = (TextView) convertView.findViewById(R.id.content_big_title);
                    holderBig.contentBigImgIV = (ImageView) convertView.findViewById(R.id.content_big_img);
                    holderBig.contentBigStartTV = (ImageView) convertView.findViewById(R.id.content_big_start);
                    holderBig.contentBigCornerTV = (TextView) convertView.findViewById(R.id.content_big_corner);
                    holderBig.contentBigSourceIconTV = (ImageView) convertView.findViewById(R.id.content_big_source_icon);
                    holderBig.contentBigSourceNameTV = (TextView) convertView.findViewById(R.id.content_big_source_name);
                    holderBig.contentBigSourceTimeTV = (TextView) convertView.findViewById(R.id.content_big_source_time);
                    convertView.setTag(holderBig);
                }else{
                    holderBig = (ViewHolderBig) convertView.getTag();
                }
                holderBig.contentBigTitleTV.setText(item.getTitle());
                ViewGroup.LayoutParams layoutParams = holderBig.contentBigImgIV.getLayoutParams();
                int width = DensityUtil.getScreenWidth(context)-DensityUtil.dip2px(context,30f);
                int height = (int)(width/(1242f/802f));
                layoutParams.width = width;
                layoutParams.height = height;
                Glide.with(context)
                        .load(item.getImageURL())
                        .placeholder(R.drawable.kong_mrjz)
                        .error(R.drawable.kong_mrjz)
                        .centerCrop()
                        .crossFade()
                        .into(holderBig.contentBigImgIV);
                if (item.getType()==2){
                    int pading = DensityUtil.dip2px(context,5);
                    int pading25 = DensityUtil.dip2px(context,2.5f);
                    holderBig.contentBigStartTV.setVisibility(View.GONE);
                    holderBig.contentBigCornerTV.setBackgroundResource(R.drawable.round_corner_translucent2);
                    holderBig.contentBigCornerTV.setPadding(pading,pading25,pading,pading25);
                    holderBig.contentBigCornerTV.setText(item.getColTuJi()+"图");
                }else{
                    holderBig.contentBigStartTV.setVisibility(View.VISIBLE);
                    String time = DateUtil.formatSecondsCh(item.getDuration());
                    int pading25 = DensityUtil.dip2px(context,2.5f);
                    int pading10 = DensityUtil.dip2px(context,10);
                    holderBig.contentBigCornerTV.setBackgroundResource(R.drawable.round_corner_time);
                    holderBig.contentBigCornerTV.setPadding(pading10,pading25,pading10,pading25);
                    holderBig.contentBigCornerTV.setText(time);
                }
                Glide.with(context)
                        .load(item.getLogo())
                        .placeholder(R.drawable.kong_mrjz)
                        .error(R.drawable.kong_mrjz)
                        .centerCrop()
                        .crossFade()
                        .into(holderBig.contentBigSourceIconTV);
                holderBig.contentBigSourceNameTV.setText(item.getSourceName());
                holderBig.contentBigSourceTimeTV.setText(item.getUpdateTime()+"");
                break;
            case TYPE_TEXT_IMAGE_BIG:
                if (convertView==null){
                    holderBig = new ViewHolderBig();
                    convertView = View.inflate(context,R.layout.item_wealth_life_big,null);
                    holderBig.contentBigTitleTV = (TextView) convertView.findViewById(R.id.content_big_title);
                    holderBig.contentBigImgIV = (ImageView) convertView.findViewById(R.id.content_big_img);
                    holderBig.contentBigStartTV = (ImageView) convertView.findViewById(R.id.content_big_start);
                    holderBig.contentBigCornerTV = (TextView) convertView.findViewById(R.id.content_big_corner);
                    holderBig.contentBigSourceIconTV = (ImageView) convertView.findViewById(R.id.content_big_source_icon);
                    holderBig.contentBigSourceNameTV = (TextView) convertView.findViewById(R.id.content_big_source_name);
                    holderBig.contentBigSourceTimeTV = (TextView) convertView.findViewById(R.id.content_big_source_time);
                    convertView.setTag(holderBig);
                }else{
                    holderBig = (ViewHolderBig) convertView.getTag();
                }
                holderBig.contentBigTitleTV.setText(item.getTitle());
                ViewGroup.LayoutParams layoutParamsB = holderBig.contentBigImgIV.getLayoutParams();
                int widthb = DensityUtil.getScreenWidth(context)-DensityUtil.dip2px(context,30f);
                int heightb = (int)(widthb/(1242f/802f));
                layoutParamsB.width = widthb;
                layoutParamsB.height = heightb;
                Glide.with(context)
                        .load(item.getImageURL())
                        .placeholder(R.drawable.kong_mrjz)
                        .error(R.drawable.kong_mrjz)
                        .centerCrop()
                        .crossFade()
                        .into(holderBig.contentBigImgIV);

                    int pading = DensityUtil.dip2px(context,5);
                    int pading25 = DensityUtil.dip2px(context,2.5f);
                    holderBig.contentBigStartTV.setVisibility(View.GONE);
                    holderBig.contentBigCornerTV.setVisibility(View.GONE);

                Glide.with(context)
                        .load(item.getLogo())
                        .placeholder(R.drawable.kong_mrjz)
                        .error(R.drawable.kong_mrjz)
                        .centerCrop()
                        .crossFade()
                        .into(holderBig.contentBigSourceIconTV);
                holderBig.contentBigSourceNameTV.setText(item.getSourceName());
                holderBig.contentBigSourceTimeTV.setText(item.getUpdateTime()+"");
                break;
            default:
                break;
        }

        return convertView;
    }

    private View handleHeaderData(){
        View view = View.inflate(context,R.layout.header_wealth_life,null);
        ImageView headerBigImageIV = (ImageView) view.findViewById(R.id.header_big_img);
        TextView headerTitleTV = (TextView) view.findViewById(R.id.header_title);
        ImageView headerSourceIconIV = (ImageView) view.findViewById(R.id.hesder_source_icon);
        TextView headerSourceNameTV = (TextView) view.findViewById(R.id.hesder_source_name);
        TextView headerSourceTimeTV = (TextView) view.findViewById(R.id.hesder_source_time);
        TextView headerlengthTV = (TextView) view.findViewById(R.id.length);
        CommonListItem item = list.get(0);
        ViewGroup.LayoutParams layoutParams = headerBigImageIV.getLayoutParams();
        int width = DensityUtil.getScreenWidth(context)-DensityUtil.dip2px(context,20f);
        int height = (int) (width/(1142f/844f));
        layoutParams.width = width;
        layoutParams.height = height;
        Glide.with(context)
                .load(item.getIndexImageUrl())
                .placeholder(R.drawable.kong_mrjz)
                .error(R.drawable.kong_mrjz)
                .crossFade()
                .into(headerBigImageIV);
        headerTitleTV.setText(item.getTitle());
        Glide.with(context)
                .load(item.getLogo())
                .placeholder(R.drawable.kong_mrjz)
                .error(R.drawable.kong_mrjz)
                .crossFade()
                .into(headerSourceIconIV);
        headerSourceNameTV.setText(item.getSourceName());
        headerSourceTimeTV.setText(item.getUpdateTime());
        int tpye = item.getType();
        if (tpye == 2) {
            headerlengthTV.setVisibility(View.VISIBLE);
            headerlengthTV.setText(item.getColTuJi()+"图");
        }else if (tpye == 3 ||tpye == 4) {
            headerlengthTV.setVisibility(View.VISIBLE);
            String time = DateUtil.formatSecondsCh(item.getDuration());
            headerlengthTV.setText(time);
        }else {
            headerlengthTV.setVisibility(View.GONE);
        }
        return view;
    }



    public class ViewHolderSmall{
        public ImageView contentSmallImgIV;
        public TextView contentSmallTitleTV;
        public TextView contentSmallSourceTV;
        public TextView contentSmallTimeTV;
    }

    public class ViewHolderBig{
        public TextView contentBigTitleTV;
        public ImageView contentBigImgIV;
        public ImageView contentBigStartTV;
        public TextView contentBigCornerTV;
        public ImageView contentBigSourceIconTV;
        public TextView contentBigSourceNameTV;
        public TextView contentBigSourceTimeTV;
    }

}
