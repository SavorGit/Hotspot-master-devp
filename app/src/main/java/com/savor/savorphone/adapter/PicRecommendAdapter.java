package com.savor.savorphone.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.DateUtil;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.CommonListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 带有侧滑条目的页面适配器
 * Created by wmm on 2016/11/1.
 */
public class PicRecommendAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<CommonListItem> commonList  = new ArrayList<CommonListItem>();
    public static final float SCAL = 1.829f;

    public PicRecommendAdapter(Context context,List<CommonListItem> common) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        commonList.addAll(common);
        notifyDataSetChanged();
    }

    public void setData(List<CommonListItem> common , boolean refresh) {
        commonList.clear();
        commonList.addAll(common);
        notifyDataSetChanged();
    }

    public void clear() {
        commonList.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return commonList.size();
    }

    @Override
    public CommonListItem getItem(int position) {
        return commonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_pic_recommed, null);

            holder.contentSmallImgIV = (ImageView) convertView.findViewById(R.id.content_small_img);
            holder.contentSmallTitleTV = (TextView) convertView.findViewById(R.id.content_title);
            holder.contentLengthTV = (TextView) convertView.findViewById(R.id.length);
//            ViewGroup.LayoutParams layoutParams = holder.contentSmallImgIV.getLayoutParams();
//            float widthInPx = DensityUtil.getWidthInPx(mContext);
//            float height = widthInPx*SCAL;
//            layoutParams.height = (int) height;
            convertView.setTag(R.id.tag_holder, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
        }
        final CommonListItem itemVo = getItem(position);
        Glide.with(mContext)
                .load(itemVo.getImageURL())
                .placeholder(R.drawable.kong_mrjz)
                .error(R.drawable.kong_mrjz)
                .crossFade()
                .into(holder.contentSmallImgIV);
        holder.contentSmallTitleTV.setText(itemVo.getTitle());

        /**文章类型  0:纯文本，1:图文，2:图集,3:视频,4:纯视频*/
        int  type = itemVo.getType();
//        switch (type){
//            case 0:
//            case 1:
//                holder.contentLengthTV.setVisibility(View.GONE);
//                break;
//            case 2:
//                int colTuJi = itemVo.getColTuJi();
//                if (colTuJi > 0) {
//                    holder.contentLengthTV.setText(colTuJi+"图");
//                }
//                break;
//            case 3:
//            case 4:
//                String  duration = itemVo.getDuration();
//                if (!TextUtils.isEmpty(duration)) {
//                    holder.contentLengthTV.setText(DateUtil.formatSecondsTime(duration));
//                }
//                break;
//        }

        if (type == 2) {
            holder.contentLengthTV.setVisibility(View.VISIBLE);
            holder.contentLengthTV.setText(itemVo.getColTuJi()+"图");
        }else if (type == 3 ||type == 4) {
            holder.contentLengthTV.setVisibility(View.VISIBLE);
            String time = DateUtil.formatSecondsCh(itemVo.getDuration());
            holder.contentLengthTV.setText(time);
        }else {
            holder.contentLengthTV.setVisibility(View.GONE);
        }
//        convertView.setOnClickListener(new mStoreListener(itemVo) );
//        convertView.setOnLongClickListener(new mLongItemListener(itemVo));
        return convertView;
    }


//    /**
//     * 单击收藏事件监听器
//     */
//    public class mStoreListener implements OnClickListener{
//        private CommonListItem itemVo;
//
//        public mStoreListener(CommonListItem vodAndTopicItemVo) {
//            this.itemVo = vodAndTopicItemVo;
//        }
//
//        @Override
//        public void onClick(View view) {
//            callback.onStoreClick(itemVo);
//        }
//    }
//
//
//    public interface OnItemStoreClickListener {
//        void onStoreClick(CommonListItem itemVo);
//    }


//    public class mLongItemListener implements View.OnLongClickListener{
//        private CommonListItem itemVo;
//
//        public mLongItemListener(CommonListItem vodAndTopicItemVo) {
//            this.itemVo = vodAndTopicItemVo;
//        }
//        @Override
//        public boolean onLongClick(View v) {
//            mOnItemLongClickListener.onItemLongClick(itemVo);
//            return false;
//        }
//    }
//    /**
//     * 单击分享事件监听器
//     */
//
//
//
//    public interface OnItemLongClickListener {
//        void onItemLongClick(CommonListItem itemVo);
//    }
//
//    public void setOnItemLongClickListener(PicRecommendAdapter.OnItemLongClickListener listener) {
//        this.mOnItemLongClickListener = listener;
//    }


    private class ViewHolder {

        private ImageView contentSmallImgIV;
        private TextView contentSmallTitleTV;
        private TextView contentLengthTV;
    }
}

