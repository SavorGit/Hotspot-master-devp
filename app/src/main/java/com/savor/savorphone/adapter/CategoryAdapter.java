package com.savor.savorphone.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.savor.savorphone.R;
import com.savor.savorphone.bean.CategoryItemVo;
import com.savor.savorphone.utils.ImageLoaderManager;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by savor on 2016/11/10.
 */
public class CategoryAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<CategoryItemVo> mList = new ArrayList<CategoryItemVo>();

    public CategoryAdapter(Context context,List<CategoryItemVo> mContents) {
        this.mList = mContents;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<CategoryItemVo> list, boolean refresh) {
        if (refresh) {
            mList.clear();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
        //return 15;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_find_grid, null);
            holder.iv_cover = (ImageView) convertView.findViewById(R.id.iv_cover);
            holder.tv_category_name = (TextView) convertView.findViewById(R.id.tv_category_name);
            convertView.setTag(R.id.tag_holder, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
        }
        CategoryItemVo categoryItemVo = mList.get(position);
       // ImageUtils.loadImageView(holder.iv_cover, categoryItemVo.getImageUrl(), R.drawable.ic_loadding2);
        if (categoryItemVo != null) {
            String url = categoryItemVo.getImageURL();
            if (!TextUtils.isEmpty(url)) {
                ImageLoaderManager.getImageLoader().loadImage(mContext, url, holder.iv_cover);
            }

            String name = categoryItemVo.getName();
            if (!TextUtils.isEmpty(name)) {
                holder.tv_category_name.setText(name);
            }

        }


        //
        return convertView;
    }

    private class ViewHolder {
        public ImageView iv_cover;
        public TextView tv_category_name;
    }
}
