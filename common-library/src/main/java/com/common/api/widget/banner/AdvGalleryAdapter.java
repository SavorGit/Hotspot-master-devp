package com.common.api.widget.banner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.common.api.R;
import com.common.api.bitmap.BitmapDisplayConfig;
import com.common.api.utils.BitmapUtils;
import com.common.api.widget.banner.AdsLooper.AdsEntity;

public class AdvGalleryAdapter extends BaseAdapter {

	private LayoutInflater inflater;
//	private ImageLoader mImageLoader = ImageLoader.getInstance();
//	private DisplayImageOptions mOptions;
	private List<AdsEntity> data;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	private Context mContext;
	private int screenWidth;
	private int itemHeight;

	public AdvGalleryAdapter(Context context, List<AdsEntity> data,
			AutoScrollGallery currGallery,int itemHeight,Drawable bannerDefaultPic) {
		inflater = LayoutInflater.from(context);
		mContext = context;
		this.data = data;
		bitmapUtils = new BitmapUtils(context);
		config = new BitmapDisplayConfig();
		if(bannerDefaultPic!=null){
			config.setLoadingDrawable(bannerDefaultPic);
			config.setLoadFailedDrawable(bannerDefaultPic);
		}else{
			config.setLoadingDrawable(mContext.getResources().getDrawable(R.drawable.kong_mrjz));
			config.setLoadFailedDrawable(mContext.getResources().getDrawable(R.drawable.kong_mrjz));
		}
		screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
		this.itemHeight = itemHeight;
	}

	@Override
	public int getCount() {
		if (data.size() > 1) {
			return Integer.MAX_VALUE;
		} else
			return 1;
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int index = position % data.size();
		// convertView = data.get(index).getView();

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.gallery_item, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) convertView.findViewById(R.id.iv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		//下面这几行是为了保证Item的宽度能够占满屏幕宽度
		LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.gallery_item);
		layout.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.MATCH_PARENT, itemHeight));
		holder.iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		holder.iv.setAdjustViewBounds(true);
		holder.iv.setScaleType(ScaleType.FIT_XY);
//		holder.iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.banner_bg640_110));
		
		bitmapUtils.display(holder.iv, data.get(index).getUri(),config);
		//		bitmapUtils.display(holder.iv, data.get(index).getUri(),config,new BitmapLoadCallBack<View>() {
//
//			@Override
//			public void onLoadCompleted(View container, String url,
//					Bitmap bitmap, BitmapDisplayConfig config,
//					BitmapLoadFrom from) {
//				if(container instanceof ImageView){
//					ImageView iv = (ImageView) container;
//					iv.setImageDrawable(new BitmapDrawable(mContext.getResources(),bitmap));
//				}else{
//					container.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(),bitmap));
//				}
//			}
//
//			@Override
//			public void onLoadFailed(View container, String url,
//					Drawable drawable) {
//				
//			}
//		});
//		mImageLoader
//				.displayImage(data.get(index).getUri(), holder.iv, mOptions);
		return convertView;
	}

	private static class ViewHolder {
		ImageView iv;
	}
	public void setData(List<AdsEntity> data){
		if (data!=null){
			this.data=data;
		}else{
			this.data=new ArrayList<AdsEntity>();
		}
	}
}
