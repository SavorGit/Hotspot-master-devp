package com.savor.savorphone.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.DensityUtil;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.CommonListItem;

import java.util.ArrayList;
import java.util.List;

import static com.taobao.accs.ACCSManager.mContext;

/**
 * Created by Administrator on 2017/7/4.
 */

public class SubjectAdapter extends BaseAdapter{
    private Context context;
    private List<CommonListItem> commonList  = new ArrayList<CommonListItem>();
    //为三种布局定义一个标识
    private final int TYPE_SMALL = 0;
    private final int TYPE_BIG = 1;
    public SubjectAdapter(Context mcontext){
        this.context = mcontext;
    }
    @Override
    public int getCount() {
        return commonList.size();
    }

    @Override
    public Object getItem(int position) {
        return commonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public void setData(List<CommonListItem> common){
        commonList.clear();
        commonList.addAll(common);
        notifyDataSetChanged();
    }

    public void clear() {
        commonList.clear();
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolderSmall holderSmall;
        if (convertView == null) {
            holderSmall = new ViewHolderSmall();
            convertView = View.inflate(context, R.layout.item_subject,null);
            holderSmall.contentSmallImgIV = (ImageView) convertView.findViewById(R.id.content_small_img);
            holderSmall.contentSmallTitleTV = (TextView) convertView.findViewById(R.id.content_small_title);
            holderSmall.contentSmallTimeTV = (TextView) convertView.findViewById(R.id.content_small_time);
            convertView.setTag(R.id.tag_holder, holderSmall);
        } else {
            holderSmall = (SubjectAdapter.ViewHolderSmall) convertView.getTag(R.id.tag_holder);
        }

        final CommonListItem itemVo = commonList.get(position);
        Glide.with(context)
                .load(itemVo.getImageURL())
                .placeholder(R.drawable.kong_mrjz)
                .error(R.drawable.kong_mrjz)
                .crossFade()
                .into(holderSmall.contentSmallImgIV);
        holderSmall.contentSmallTitleTV.setText(itemVo.getTitle());

        holderSmall.contentSmallTimeTV.setText(itemVo.getUpdateTime());



        return convertView;
    }

    public class ViewHolderSmall{
        private ImageView contentSmallImgIV;
        private TextView contentSmallTitleTV;
        private TextView contentSmallTimeTV;
    }


}
