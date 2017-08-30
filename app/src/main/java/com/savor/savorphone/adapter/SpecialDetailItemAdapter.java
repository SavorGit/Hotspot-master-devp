package com.savor.savorphone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.DensityUtil;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.SpecialDetail;

import java.util.List;

/**
 * 专题组详情页 内容列表适配器
 * 当前类型距离下一个元素类型的间距：
     大标题：
     图片
     1、图片：10px
     2、文字：30px
     3、文章：40px
     4、小标题：50px
     文字（简介）
     1、图片：30px
     2、文字：50px
     3、文章：50px
     4、小标题：50px
     文章(图文）
     1、图片：40px
     2、文字：50px
     3、文章：10px
     4、小标题：50px
     小标题
     1、图片：40px
     2、文字：40px
     3、文章：40px
     4、标题：50px
 * Created by hezd on 2017/8/28.
 */

public class SpecialDetailItemAdapter extends RecyclerView.Adapter {
    public static final int TYPE_IMAGE = 3;
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE_TEXT = 2;
    public static final int TYPE_TITLE = 4;
    private final Context mContext;
    private List<SpecialDetail.SpecialDetailTypeBean> data;

    public SpecialDetailItemAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<SpecialDetail.SpecialDetailTypeBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_IMAGE) {
            ImageHolder imageHolder = new ImageHolder(View.inflate(mContext, R.layout.item_special_image,null));
            return new ImageHolder(View.inflate(mContext, R.layout.item_special_image,null));
        }else if(viewType == TYPE_TEXT) {
            return new TextHolder(View.inflate(mContext,R.layout.item_special_text,null));
        }else if(viewType == TYPE_IMAGE_TEXT) {
            return new ImageTextHolder(View.inflate(mContext,R.layout.item_special_image_text,null));
        }else if(viewType == TYPE_TITLE) {
            return new TitleHolder(View.inflate(mContext,R.layout.item_special_title,null));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // 在这里进行初始化
        SpecialDetail.SpecialDetailTypeBean specialDetailTypeBean = data.get(position);
        if(holder instanceof ImageHolder){// 图片
            ImageHolder imageHolder = (ImageHolder) holder;
            String img_url = specialDetailTypeBean.getImg_url();
            Glide.with(mContext)
                    .load(img_url)
                    .placeholder(R.drawable.kong_mrjz)
                    .dontAnimate()
                    .into(imageHolder.imageView);

            ViewGroup.LayoutParams layoutParams = imageHolder.dividerView.getLayoutParams();
            int itemCount = getItemCount();
            if(itemCount>1&&position<itemCount-1) {
                int height = 0;
                SpecialDetail.SpecialDetailTypeBean nextItem = data.get(position + 1);
                int sgtype = nextItem.getSgtype();
               /* 图片
                1、图片：10px
                2、文字：30px
                3、文章：40px
                4、小标题：50px*/
               switch (sgtype) {
                   case TYPE_IMAGE:
                       height = DensityUtil.dip2px(mContext,5);
                       break;
                   case TYPE_TEXT:
                       height = DensityUtil.dip2px(mContext,15);
                       break;
                   case TYPE_IMAGE_TEXT:
                       height = DensityUtil.dip2px(mContext,20);
                       break;
                   case TYPE_TITLE:
                       height = DensityUtil.dip2px(mContext,25);
                       break;
               }
               layoutParams.height = height;
            }
        }else if(holder instanceof TextHolder) {// 简介
            TextHolder textHolder = (TextHolder) holder;
            String desc = specialDetailTypeBean.getStext();
            textHolder.descTv.setText(desc);
            /*文字（简介）
            1、图片：30px
            2、文字：50px
            3、文章：50px
            4、小标题：50px*/
            ViewGroup.LayoutParams layoutParams = textHolder.dividerView.getLayoutParams();
            int itemCount = getItemCount();
            if(itemCount>1&&position<itemCount-1) {
                int height = 0;
                SpecialDetail.SpecialDetailTypeBean nextItem = data.get(position + 1);
                int sgtype = nextItem.getSgtype();
                switch (sgtype) {
                    case TYPE_IMAGE:
                        height = DensityUtil.dip2px(mContext,15);
                        break;
                    case TYPE_TEXT:
                        height = DensityUtil.dip2px(mContext,25);
                        break;
                    case TYPE_IMAGE_TEXT:
                        height = DensityUtil.dip2px(mContext,25);
                        break;
                    case TYPE_TITLE:
                        height = DensityUtil.dip2px(mContext,25);
                        break;
                }
                layoutParams.height = height;
            }

        }else if(holder instanceof TitleHolder) {// 标题
            TitleHolder titleHolder = (TitleHolder) holder;
            String stitle = specialDetailTypeBean.getStitle();
            titleHolder.titleView.setText(stitle);
            /*小标题
            1、图片：40px
            2、文字：40px
            3、文章：40px
            4、标题：50px*/
            ViewGroup.LayoutParams layoutParams = titleHolder.divider.getLayoutParams();
            int itemCount = getItemCount();
            if(itemCount>1&&position<itemCount-1) {
                int height = 0;
                SpecialDetail.SpecialDetailTypeBean nextItem = data.get(position + 1);
                int sgtype = nextItem.getSgtype();
                switch (sgtype) {
                    case TYPE_IMAGE:
                        height = DensityUtil.dip2px(mContext,20);
                        break;
                    case TYPE_TEXT:
                        height = DensityUtil.dip2px(mContext,20);
                        break;
                    case TYPE_IMAGE_TEXT:
                        height = DensityUtil.dip2px(mContext,20);
                        break;
                    case TYPE_TITLE:
                        height = DensityUtil.dip2px(mContext,25);
                        break;
                }
                layoutParams.height = height;
            }

        }else if(holder instanceof ImageTextHolder) {// 文章
            ImageTextHolder imageTextHolder = (ImageTextHolder) holder;
            String imageURL = specialDetailTypeBean.getImageURL();
            String title = specialDetailTypeBean.getTitle();
            String sourceName = specialDetailTypeBean.getSourceName();
            String updateTime = specialDetailTypeBean.getUpdateTime();
            Glide.with(mContext).load(imageURL).placeholder(R.drawable.kong_mrjz).dontAnimate().into(imageTextHolder.content_small_img);
            imageTextHolder.content_small_title.setText(title);
            imageTextHolder.content_small_source.setText(sourceName);
            imageTextHolder.content_small_time.setText(updateTime);

            /*文章(图文）
            1、图片：40px
            2、文字：50px
            3、文章：10px
            4、小标题：50px*/
            ViewGroup.LayoutParams layoutParams = imageTextHolder.dividerView.getLayoutParams();
            int itemCount = getItemCount();
            if(itemCount>1&&position<itemCount-1) {
                int height = 0;
                SpecialDetail.SpecialDetailTypeBean nextItem = data.get(position + 1);
                int sgtype = nextItem.getSgtype();
                switch (sgtype) {
                    case TYPE_IMAGE:
                        height = DensityUtil.dip2px(mContext,20);
                        break;
                    case TYPE_TEXT:
                        height = DensityUtil.dip2px(mContext,25);
                        break;
                    case TYPE_IMAGE_TEXT:
                        height = DensityUtil.dip2px(mContext,5);
                        break;
                    case TYPE_TITLE:
                        height = DensityUtil.dip2px(mContext,25);
                        break;
                }
                layoutParams.height = height;
            }

        }

    }

    @Override
    public int getItemCount() {
        return data == null?0:data.size();
    }

    @Override
    public int getItemViewType(int position) {
        SpecialDetail.SpecialDetailTypeBean specialDetailTypeBean = data.get(position);
        return specialDetailTypeBean.getSgtype();
    }

    /**
     * 图片布局
     */
    public class ImageHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public View dividerView;
        public ImageHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            dividerView = itemView.findViewById(R.id.divider_line);
        }
    }

    /**
     * 文字布局
     */
    public class TextHolder extends RecyclerView.ViewHolder {
        public TextView descTv;
        public View dividerView;
        public TextHolder(View itemView) {
            super(itemView);
            descTv = (TextView) itemView.findViewById(R.id.tv_text);
            dividerView = itemView.findViewById(R.id.divider_line);
        }
    }

    /**
     * （文章）文章布局
     */
    public class ImageTextHolder extends RecyclerView.ViewHolder {
        public ImageView content_small_img;
        public TextView content_small_title;
        public TextView content_small_source;
        public TextView content_small_time;
        public View dividerView;
        public ImageTextHolder(View itemView) {
            super(itemView);
            content_small_img = (ImageView) itemView.findViewById(R.id.content_small_img);
            content_small_title = (TextView) itemView.findViewById(R.id.content_small_title);
            content_small_source = (TextView) itemView.findViewById(R.id.content_small_source);
            content_small_time = (TextView) itemView.findViewById(R.id.content_small_time);
            dividerView = itemView.findViewById(R.id.divider_line);
        }
    }

    /**
     *  小标题布局
     */
    public class TitleHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public View divider;
        public TitleHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.tv_title);
            divider = itemView.findViewById(R.id.divider_line);
        }
    }
}
