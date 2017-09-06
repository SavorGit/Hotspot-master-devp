package com.savor.savorphone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class SpecialDetailItemAdapter extends BaseAdapter {
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE_TEXT = 1;
    public static final int TYPE_TITLE = 3;
    private static final int VIEW_TYPE_COUNT = 4;
    private final Context mContext;
    private List<SpecialDetail.SpecialDetailTypeBean> data;
    private OnSpecialItemClickListener mOnItemClickListener;

    public SpecialDetailItemAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<SpecialDetail.SpecialDetailTypeBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data==null?0:data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
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
        SpecialDetail.SpecialDetailTypeBean specialDetailTypeBean = data.get(position);
        int type = specialDetailTypeBean.getSgtype();
        int result = -1;
        switch (type) {
            case 3:
                result = TYPE_IMAGE;
                break;
            case 1:
                result = TYPE_TEXT;
                break;
            case 4:
                result = TYPE_TITLE;
                break;
            case 2:
                result = TYPE_IMAGE_TEXT;
                break;
        }
        return result;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageHolder imageHolder = null;
        TextHolder textHolder = null;
        TitleHolder titleHolder = null;
        ImageTextHolder imageTextHolder = null;
        int itemViewType = getItemViewType(position);
        if(convertView == null) {
            switch (itemViewType) {
                case TYPE_IMAGE:
                    imageHolder = new ImageHolder();
                    convertView = View.inflate(mContext, R.layout.item_special_image,null);
                    imageHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_image);
                    imageHolder.dividerView = convertView.findViewById(R.id.divider_line);
                    imageHolder.parentLayout = (LinearLayout) convertView.findViewById(R.id.ll_parent_layout);
                    convertView.setTag(imageHolder);
                    break;
                case TYPE_TEXT:
                    textHolder = new TextHolder();
                    convertView = View.inflate(mContext,R.layout.item_special_text,null);
                    textHolder.parentLayout = (LinearLayout) convertView.findViewById(R.id.ll_parent_layout);
                    textHolder.descTv = (TextView) convertView.findViewById(R.id.tv_text);
                    textHolder.dividerView = convertView.findViewById(R.id.divider_line);
                    convertView.setTag(textHolder);
                    break;
                case TYPE_TITLE:
                    titleHolder = new TitleHolder();
                    convertView = View.inflate(mContext,R.layout.item_special_title,null);
                    titleHolder.titleView = (TextView) convertView.findViewById(R.id.tv_title);
                    titleHolder.divider = convertView.findViewById(R.id.divider_line);
                    titleHolder.parentLayout = (LinearLayout) convertView.findViewById(R.id.ll_parent_layout);
                    convertView.setTag(titleHolder);
                    break;
                case TYPE_IMAGE_TEXT:
                    imageTextHolder = new ImageTextHolder();
                    convertView = View.inflate(mContext,R.layout.item_special_image_text,null);
                    imageTextHolder.content_small_img = (ImageView) convertView.findViewById(R.id.content_small_img);
                    imageTextHolder.content_small_title = (TextView) convertView.findViewById(R.id.content_small_title);
                    imageTextHolder.content_small_source = (TextView) convertView.findViewById(R.id.content_small_source);
                    imageTextHolder.content_small_time = (TextView) convertView.findViewById(R.id.content_small_time);
                    imageTextHolder.dividerView = convertView.findViewById(R.id.divider_line);
                    imageTextHolder.parentLayout = (LinearLayout) convertView.findViewById(R.id.ll_parent_layout);
                    convertView.setTag(imageTextHolder);
                    break;
            }
        }else {
            switch (itemViewType) {
                case TYPE_IMAGE:
                    imageHolder = (ImageHolder) convertView.getTag();
                    break;
                case TYPE_TEXT:
                    textHolder = (TextHolder) convertView.getTag();
                    break;
                case TYPE_TITLE:
                    titleHolder = (TitleHolder) convertView.getTag();
                    break;
                case TYPE_IMAGE_TEXT:
                    imageTextHolder = (ImageTextHolder) convertView.getTag();
                    break;
            }

            final SpecialDetail.SpecialDetailTypeBean specialDetailTypeBean = (SpecialDetail.SpecialDetailTypeBean) getItem(position);
            ViewGroup.LayoutParams layoutParams;
            int itemCount;
            switch (itemViewType) {
                case TYPE_IMAGE:
                    String img_url = specialDetailTypeBean.getImg_url();
                    Glide.with(mContext)
                            .load(img_url)
                            .placeholder(R.drawable.kong_mrjz)
                            .dontAnimate()
                            .into(imageHolder.imageView);

                    layoutParams = imageHolder.dividerView.getLayoutParams();
                    itemCount = getCount();
                    if(itemCount>1&&position<itemCount-1) {
                        handleDividerHeight(position, specialDetailTypeBean, layoutParams);
                    }

                    imageHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mOnItemClickListener!=null) {
                                mOnItemClickListener.onSpecialItemClick(getItemViewType(position),specialDetailTypeBean);
                            }
                        }
                    });
                    break;
                case TYPE_TEXT:
                    String desc = specialDetailTypeBean.getStext();
                    textHolder.descTv.setText(desc);
                    /*文字（简介）
                    1、图片：30px
                    2、文字：50px
                    3、文章：50px
                    4、小标题：50px*/
                    layoutParams = textHolder.dividerView.getLayoutParams();
                    itemCount = getCount();
                    if(itemCount>1&&position<itemCount-1) {
                        handleDividerHeight(position, specialDetailTypeBean, layoutParams);
                    }

                    textHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mOnItemClickListener!=null) {
                                mOnItemClickListener.onSpecialItemClick(getItemViewType(position),specialDetailTypeBean);
                            }
                        }
                    });
                    break;
                case TYPE_TITLE:
                    String stitle = specialDetailTypeBean.getStitle();
                    titleHolder.titleView.setText(stitle);
                    /*小标题
                    1、图片：40px
                    2、文字：40px
                    3、文章：40px
                    4、标题：50px*/
                    layoutParams = titleHolder.divider.getLayoutParams();
                    itemCount = getCount();
                    if(itemCount>1&&position<itemCount-1) {
                        handleDividerHeight(position, specialDetailTypeBean, layoutParams);
                    }

                    titleHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mOnItemClickListener!=null) {
                                mOnItemClickListener.onSpecialItemClick(getItemViewType(position),specialDetailTypeBean);
                            }
                        }
                    });
                    break;
                case TYPE_IMAGE_TEXT:
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
                    layoutParams = imageTextHolder.dividerView.getLayoutParams();
                    itemCount = getCount();
                    if(itemCount>1&&position<itemCount-1) {
                        handleDividerHeight(position, specialDetailTypeBean, layoutParams);
                    }

                    imageTextHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mOnItemClickListener!=null) {
                                mOnItemClickListener.onSpecialItemClick(getItemViewType(position),specialDetailTypeBean);
                            }
                        }
                    });
                    break;
            }

        }
        return convertView;
    }

//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
//        if(viewType == TYPE_IMAGE) {
//            ImageHolder imageHolder = new ImageHolder(View.inflate(mContext, R.layout.item_special_image,null));
//            return imageHolder;
//        }else if(viewType == TYPE_TEXT) {
//            return new TextHolder(View.inflate(mContext,R.layout.item_special_text,null));
//        }else if(viewType == TYPE_IMAGE_TEXT) {
//            return new ImageTextHolder(View.inflate(mContext,R.layout.item_special_image_text,null));
//        }else if(viewType == TYPE_TITLE) {
//            return new TitleHolder(View.inflate(mContext,R.layout.item_special_title,null));
//        }
//        return null;
//    }

//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//        // 在这里进行初始化
//        final SpecialDetail.SpecialDetailTypeBean specialDetailTypeBean = data.get(position);
//        if(holder instanceof ImageHolder){// 图片
//            ImageHolder imageHolder = (ImageHolder) holder;
//            String img_url = specialDetailTypeBean.getImg_url();
//            Glide.with(mContext)
//                    .load(img_url)
//                    .placeholder(R.drawable.kong_mrjz)
//                    .dontAnimate()
//                    .into(imageHolder.imageView);
//
//            ViewGroup.LayoutParams layoutParams = imageHolder.dividerView.getLayoutParams();
//            int itemCount = getItemCount();
//            if(itemCount>1&&position<itemCount-1) {
//                handleDividerHeight(position, specialDetailTypeBean, layoutParams);
//            }
//
//            imageHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(mOnItemClickListener!=null) {
//                        mOnItemClickListener.onSpecialItemClick(getItemViewType(position),specialDetailTypeBean);
//                    }
//                }
//            });
//        }else if(holder instanceof TextHolder) {// 简介
//            TextHolder textHolder = (TextHolder) holder;
//            String desc = specialDetailTypeBean.getStext();
//            textHolder.descTv.setText(desc);
//            /*文字（简介）
//            1、图片：30px
//            2、文字：50px
//            3、文章：50px
//            4、小标题：50px*/
//            ViewGroup.LayoutParams layoutParams = textHolder.dividerView.getLayoutParams();
//            int itemCount = getItemCount();
//            if(itemCount>1&&position<itemCount-1) {
//                handleDividerHeight(position, specialDetailTypeBean, layoutParams);
//            }
//
//            textHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(mOnItemClickListener!=null) {
//                        mOnItemClickListener.onSpecialItemClick(getItemViewType(position),specialDetailTypeBean);
//                    }
//                }
//            });
//
//        }else if(holder instanceof TitleHolder) {// 标题
//            TitleHolder titleHolder = (TitleHolder) holder;
//            String stitle = specialDetailTypeBean.getStitle();
//            titleHolder.titleView.setText(stitle);
//            /*小标题
//            1、图片：40px
//            2、文字：40px
//            3、文章：40px
//            4、标题：50px*/
//            ViewGroup.LayoutParams layoutParams = titleHolder.divider.getLayoutParams();
//            int itemCount = getItemCount();
//            if(itemCount>1&&position<itemCount-1) {
//                handleDividerHeight(position, specialDetailTypeBean, layoutParams);
//            }
//
//            titleHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(mOnItemClickListener!=null) {
//                        mOnItemClickListener.onSpecialItemClick(getItemViewType(position),specialDetailTypeBean);
//                    }
//                }
//            });
//
//        }else if(holder instanceof ImageTextHolder) {// 文章
//            ImageTextHolder imageTextHolder = (ImageTextHolder) holder;
//            String imageURL = specialDetailTypeBean.getImageURL();
//            String title = specialDetailTypeBean.getTitle();
//            String sourceName = specialDetailTypeBean.getSourceName();
//            String updateTime = specialDetailTypeBean.getUpdateTime();
//            Glide.with(mContext).load(imageURL).placeholder(R.drawable.kong_mrjz).dontAnimate().into(imageTextHolder.content_small_img);
//            imageTextHolder.content_small_title.setText(title);
//            imageTextHolder.content_small_source.setText(sourceName);
//            imageTextHolder.content_small_time.setText(updateTime);
//
//            /*文章(图文）
//            1、图片：40px
//            2、文字：50px
//            3、文章：10px
//            4、小标题：50px*/
//            ViewGroup.LayoutParams layoutParams = imageTextHolder.dividerView.getLayoutParams();
//            int itemCount = getItemCount();
//            if(itemCount>1&&position<itemCount-1) {
//                handleDividerHeight(position, specialDetailTypeBean, layoutParams);
//            }
//
//            imageTextHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(mOnItemClickListener!=null) {
//                        mOnItemClickListener.onSpecialItemClick(getItemViewType(position),specialDetailTypeBean);
//                    }
//                }
//            });
//        }
//    }

    private void handleDividerHeight(int position, SpecialDetail.SpecialDetailTypeBean specialDetailTypeBean, ViewGroup.LayoutParams layoutParams) {
        int currentType = specialDetailTypeBean.getSgtype();
        int height = 0;
        SpecialDetail.SpecialDetailTypeBean nextItem = data.get(position + 1);
        int sgtype = nextItem.getSgtype();
        if(currentType == 3) {// 图片
            // sgtype 1:简介 2：文章 3：图片 4：标题
            /* 图片
            1、图片：10px
            2、文字：30px
            3、文章：40px
            4、小标题：50px*/
            switch (sgtype) {
                //sgtype 1:简介 2：文章 3：图片 4：标题
                case 3:
                    height = DensityUtil.dip2px(mContext,5);
                    break;
                case 1:
                    height = DensityUtil.dip2px(mContext,15);
                    break;
                case 2:
                    height = DensityUtil.dip2px(mContext,20);
                    break;
                case 4:
                    height = DensityUtil.dip2px(mContext,25);
                    break;
            }
        }else if(currentType == 1) {// 文本
            // sgtype 1:简介 2：文章 3：图片 4：标题
            switch (sgtype) {
                case 3:
                    height = DensityUtil.dip2px(mContext,15);
                    break;
                case 1:
                    height = DensityUtil.dip2px(mContext,25);
                    break;
                case 2:
                    height = DensityUtil.dip2px(mContext,25);
                    break;
                case 4:
                    height = DensityUtil.dip2px(mContext,25);
                    break;
            }
        }else if(currentType == 4) {// 小标题
            // sgtype 1:简介 2：文章 3：图片 4：标题
            switch (sgtype) {
                case 3:
                    height = DensityUtil.dip2px(mContext,20);
                    break;
                case 1:
                    height = DensityUtil.dip2px(mContext,20);
                    break;
                case 2:
                    height = DensityUtil.dip2px(mContext,20);
                    break;
                case 4:
                    height = DensityUtil.dip2px(mContext,25);
                    break;
            }
        }else if(currentType == 2) {
//            sgtype 1:简介 2：文章 3：图片 4：标题
            switch (sgtype) {
                case 3:
                    height = DensityUtil.dip2px(mContext,20);
                    break;
                case 1:
                    height = DensityUtil.dip2px(mContext,25);
                    break;
                case 2:
                    height = DensityUtil.dip2px(mContext,5);
                    break;
                case 4:
                    height = DensityUtil.dip2px(mContext,25);
                    break;
            }
        }

        layoutParams.height = height;
    }

//    @Override
//    public int getItemCount() {
//        return data == null?0:data.size();
//    }


    /**
     * 图片布局
     */
    public class ImageHolder  {
        public ImageView imageView;
        public View dividerView;
        public LinearLayout parentLayout;
//        public ImageHolder(View itemView) {
//            super(itemView);
//            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
//            dividerView = itemView.findViewById(R.id.divider_line);
//            parentLayout = (LinearLayout) itemView.findViewById(R.id.ll_parent_layout);
//        }
    }

    /**
     * 文字布局
     */
    public class TextHolder  {
        public TextView descTv;
        public View dividerView;
        public LinearLayout parentLayout;
//        public TextHolder(View itemView) {
//            super(itemView);
//            parentLayout = (LinearLayout) itemView.findViewById(R.id.ll_parent_layout);
//            descTv = (TextView) itemView.findViewById(R.id.tv_text);
//            dividerView = itemView.findViewById(R.id.divider_line);
//        }
    }

    /**
     * （文章）文章布局
     */
    public class ImageTextHolder {
        public ImageView content_small_img;
        public TextView content_small_title;
        public TextView content_small_source;
        public TextView content_small_time;
        public View dividerView;
        public LinearLayout parentLayout;
//        public ImageTextHolder(View itemView) {
//            super(itemView);
//            content_small_img = (ImageView) itemView.findViewById(R.id.content_small_img);
//            content_small_title = (TextView) itemView.findViewById(R.id.content_small_title);
//            content_small_source = (TextView) itemView.findViewById(R.id.content_small_source);
//            content_small_time = (TextView) itemView.findViewById(R.id.content_small_time);
//            dividerView = itemView.findViewById(R.id.divider_line);
//            parentLayout = (LinearLayout) itemView.findViewById(R.id.ll_parent_layout);
//        }
    }

    /**
     *  小标题布局
     */
    public class TitleHolder {
        public TextView titleView;
        public View divider;
        public LinearLayout parentLayout;
//        public TitleHolder(View itemView) {
//            super(itemView);
//            titleView = (TextView) itemView.findViewById(R.id.tv_title);
//            divider = itemView.findViewById(R.id.divider_line);
//            parentLayout = (LinearLayout) itemView.findViewById(R.id.ll_parent_layout);
//        }
    }

    public void setOnSpecialItemClickListener(OnSpecialItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public interface OnSpecialItemClickListener {
        void onSpecialItemClick(int viewType, SpecialDetail.SpecialDetailTypeBean bean);
    }
}
