package com.savor.savorphone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.savor.savorphone.R;

/**
 *
 * 当前类型距离下一个元素类型的间距：
     大标题：
     图片
     1、图片：10px
     2、文字：30px
     3、文章：40px
     4、小标题：50px
     文字
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

public class SpecialListAdapter extends RecyclerView.Adapter {
    public static final int TYPE_IMAGE = 0x1;
    public static final int TYPE_TEXT = 0x2;
    public static final int TYPE_IMAGE_TEXT = 0x3;
    public static final int TYPE_TITLE = 0x4;
    private final Context mContext;

    public SpecialListAdapter (Context context) {
        this.mContext = context;
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
//        if(holder instanceof TitleHolder) {
//            TitleHolder titleHolder = (TitleHolder) holder;
//        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0x1;
        if(position == 0) {
            type = TYPE_TITLE;
        }else if(position == 1) {
            type = TYPE_IMAGE_TEXT;
        }else if(position == 2) {
            type = TYPE_IMAGE;
        }else  if(position ==3) {
            type = TYPE_TEXT;
        }
        return type;
    }

    /**
     * 图片布局
     */
    public class ImageHolder extends RecyclerView.ViewHolder {
        public ImageHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 文字布局
     */
    public class TextHolder extends RecyclerView.ViewHolder {
        public TextHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * （文章）文章布局
     */
    public class ImageTextHolder extends RecyclerView.ViewHolder {
        public ImageTextHolder(View itemView) {
            super(itemView);
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
            titleView = (TextView) itemView.findViewById(R.id.tv_special_title);
            divider = itemView.findViewById(R.id.divider_line);
        }
    }
}
