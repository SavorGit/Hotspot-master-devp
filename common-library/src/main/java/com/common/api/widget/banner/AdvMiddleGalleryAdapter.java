package com.common.api.widget.banner;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class AdvMiddleGalleryAdapter extends BaseAdapter {

	private LayoutInflater inflater;
//	private ImageLoader mImageLoader = ImageLoader.getInstance();
//	private DisplayImageOptions mOptions;
	private List<AdsMiddleLooper.AdsEntity> data;
	private BitmapUtils bitmapUtils;
	private BitmapDisplayConfig config;
	private Context mContext;
	private int screenWidth;

	public AdvMiddleGalleryAdapter(Context context, List<AdsMiddleLooper.AdsEntity> data,
			AutoScrollGallery currGallery) {
		inflater = LayoutInflater.from(context);
		mContext = context;
		this.data = data;
		bitmapUtils = new BitmapUtils(context);
		config = new BitmapDisplayConfig();
		config.setLoadingDrawable(mContext.getResources().getDrawable(R.drawable.movie_bigbanner_def));
		config.setLoadFailedDrawable(mContext.getResources().getDrawable(R.drawable.movie_bigbanner_def));
//		mOptions = new DisplayImageOptions.Builder()
//				.showImageForEmptyUri(R.drawable.banner_bg640_110)
//				.showStubImage(R.drawable.banner_bg640_110)
//				.showImageOnFail(R.drawable.banner_bg640_110).cacheInMemory()
//				.cacheOnDisc().build();
//		mOptions = AdsUtils.getDisplayImageOptions();
		screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;

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
		layout.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.MATCH_PARENT, Gallery.LayoutParams.MATCH_PARENT));
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
	public void setData(List<AdsMiddleLooper.AdsEntity> data){
		if (data!=null){
			this.data=data;
		}else{
			this.data=new ArrayList<AdsMiddleLooper.AdsEntity>();
		}
	}
}
