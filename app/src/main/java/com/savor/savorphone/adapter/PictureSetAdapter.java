package com.savor.savorphone.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.PictureSetBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bichao on 2017/7/6.
 */

public class PictureSetAdapter extends PagerAdapter{
    private Context mContext;
    private List<PictureSetBean> list = new ArrayList<>();
    PageOnClickListener pageOnClickListener = null;
    public PictureSetAdapter(Context context,List<PictureSetBean> listPhoto,PageOnClickListener listener){
        this.mContext = context;
        this.list = listPhoto;
        this.pageOnClickListener = listener;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setData(List<PictureSetBean> list){
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = View.inflate(mContext, R.layout.item_picture_set,null);
        PictureSetBean pictureSet = list.get(position);
        ImageView photoImg = (ImageView) view.findViewById(R.id.picture);
        Glide.with(mContext)
                .load(pictureSet.getPic_url())
                .placeholder(R.drawable.kong_mrjz)
                .error(R.drawable.kong_mrjz)
                .crossFade()
                .into(photoImg);
        container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageOnClickListener.handleBottomText(position);
            }
        });

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    public interface PageOnClickListener{
        void handleBottomText(int position);
    }
}
