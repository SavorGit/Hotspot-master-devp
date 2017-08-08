package com.savor.savorphone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.DateUtil;
import com.common.api.utils.DensityUtil;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.VodBean;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.utils.ImageLoaderManager;


import java.util.ArrayList;
import java.util.List;

/**
 * 带有侧滑条目的页面适配器
 * Created by wmm on 2016/11/1.
 */
public class SwipeListAdapter extends BaseAdapter {
    public static final float SCAL = 1.829f;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<VodBean> vodList = new ArrayList<VodBean>();
    private final Session mSession;

    public SwipeListAdapter(Context context) {
        mContext = context;
        mSession = Session.get(mContext);
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<VodBean>  list, boolean refresh) {
        if (refresh) {
            vodList.clear();
        }
        if(list!=null&&list.size()>0)
            vodList.addAll(list);
        notifyDataSetChanged();
    }

    public void addData(List<VodBean>  list) {
        if(list!=null&&list.size()>0) {
            vodList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return vodList==null?0:vodList.size();
//        return 20;
    }

    @Override
    public Object getItem(int position) {
        return vodList.get(position);
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
            convertView = mInflater.inflate(R.layout.item_video_page, null);

            holder.cover = (ImageView) convertView.findViewById(R.id.iv_cover);
            holder.name = (TextView) convertView.findViewById(R.id.video_name);
            holder.length = (TextView) convertView.findViewById(R.id.video_length);
            holder.canPlay = (ImageView) convertView.findViewById(R.id.can_play);
            holder.video_type = (TextView) convertView.findViewById(R.id.video_type);
            ViewGroup.LayoutParams layoutParams = holder.cover.getLayoutParams();
            float widthInPx = DensityUtil.getWidthInPx(mContext);
            float height = widthInPx*SCAL;
            layoutParams.height = (int) height;
            convertView.setTag(R.id.tag_holder, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
        }
        final VodBean itemVo = vodList.get(position);
        String imageURL = itemVo.getImageURL();
        String title = itemVo.getTitle();
        int duration = itemVo.getDuration();
//        if (itemVo.getCanPlay() == 1) {
//            holder.canPlay.setVisibility(View.VISIBLE);
//        }else {
//            holder.canPlay.setVisibility(View.GONE);
//        }

//        String url = "http://cms.rerdian.com:8089/download/cmsDemand/category/img/文化_c8.jpg";
        ImageLoaderManager.getImageLoader().loadImage(mContext,imageURL,holder.cover,R.drawable.kong_mrjz,R.drawable.kong_mrjz);
        holder.name.setText(title);
        holder.length.setText(" "+DateUtil.formatSecondsTime(Integer.toString(duration)));
        holder.video_type.setText("#"+itemVo.getCategory());
        //ImageLoaderManager.getImageLoader().loadRoundImage(mContext, url, holder.cover,R.drawable.ab_solid_custom_blue_inverse_holo);
       // ImageUtils.loadImageView(holder.cover, itemVo.getImageURL());
 //       holder.name.setText(itemVo.getTitle());
       // holder.length.setText(TimeUtils.format1(itemVo.getDuration()));


        return convertView;
    }

    /**
     * 单击收藏事件监听器
     */
    private OnItemStoreClickListener mStoreListener = null;

    public interface OnItemStoreClickListener {
        void onStoreClick(VodBean itemVo);
    }

    public void setOnItemStoreClickListener(OnItemStoreClickListener listener) {
        mStoreListener = listener;
    }

    /**
     * 单击分享事件监听器
     */
    private OnItemShareClickListener mShareListener = null;

    public interface OnItemShareClickListener {
        void onShareClick(VodBean itemVo);
    }

    public void setOnItemShareClickListener(OnItemShareClickListener listener) {
        mShareListener = listener;
    }


    private class ViewHolder {

        public TextView length;
        public TextView name;
        public TextView video_type;
        public ImageView cover;
        public ImageView canPlay;
    }
}

