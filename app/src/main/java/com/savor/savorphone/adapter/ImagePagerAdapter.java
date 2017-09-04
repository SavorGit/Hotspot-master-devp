package com.savor.savorphone.adapter;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.savor.savorphone.bean.PictureSetBean;
import com.savor.savorphone.interfaces.ImageLoader;
import com.savor.savorphone.utils.Options;
import com.savor.savorphone.widget.PhotoView;
import com.savor.savorphone.widget.imageshow.ImageShowViewPager;
import com.savor.savorphone.widget.imageshow.TouchImageView;
import com.savor.savorphone.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;



public class ImagePagerAdapter extends PagerAdapter {
	Context context;
	private List<PictureSetBean> list = new ArrayList<>();
	PictureSetAdapter.PageOnClickListener pageOnClickListener = null;
	LayoutInflater inflater = null;
	TouchImageView full_image;
	TextView progress_text;
	ProgressBar progress;
	TextView retry;
	DisplayImageOptions options;
	com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
	
	public ImagePagerAdapter(Context context, List<PictureSetBean> listPhoto) {
		this.context = context;
		this.list = listPhoto;
		inflater = LayoutInflater.from(context);
		options = Options.getListOptions();
	}

	public void setData(List<PictureSetBean> list){
		this.list = list;
		this.notifyDataSetChanged();
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		((ImageShowViewPager) container).mCurrentView = ((TouchImageView) ((View)object).findViewById(R.id.full_image));
	}
	
	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container,final int position) {


		View view = inflater.from(context).inflate(R.layout.details_imageshow_item, null);
		full_image = (TouchImageView)view.findViewById(R.id.full_image);
		PictureSetBean pictureSet = list.get(position);
//		ViewGroup parent = (ViewGroup) full_image.getParent();
//		if (null != parent) {
//			parent.removeAllViews();
//		}
		Glide.with(context)
				.load(pictureSet.getPic_url())
				.placeholder(R.drawable.kong_mrjz)
				.error(R.drawable.kong_mrjz)
				.crossFade()
				.into(full_image);
		full_image.setBackgroundResource(R.drawable.beidishipinbg);
		((ViewPager) container).addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);  
	}
}
