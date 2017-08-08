package com.common.api.widget.banner;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.R;
import com.common.api.utils.ShowMessage;
import com.common.api.widget.indicator.LaShouRedIndicator;

/**
 * 可循环自动滚动的Gallery.<br>
 * 1、控制是否自动滚动<br>
 * 2、删除<br>
 * <pre><strong>使用方法:</strong>
 * <li>在XML布局文件中以使用普通控件的方式引入，在Activity中使用{@link Activity#findViewById(int)}方法完成初始化。
 * <li>调用<strong>{@link #setOnDelCallBack(OnDelCallBack)}</strong>来设置点击删除按钮时的回调.<br>
 * <li>调用<strong>{@link #setData(List)}</strong>来设置广告数据集合。List集合的元素只能是<strong>{@link AdsEntity}</strong>类型
 * </pre>
 * @author Wangzhen
 * 
 */
public class AdsMiddleLooper extends LinearLayout {

	private AutoScrollGallery autoScrollGallery;
	private AdvMiddleGalleryAdapter advGalleryAdapter;
	private ImageView deleteBanner;
	private ImageView ivAdvDesBg;
	private TextView tvMovieDes;
	private LaShouRedIndicator laShouIndicator;

	private boolean isAutoScroll = true;
	private OnDelCallBack onDelCallBack;
	private List<AdsEntity> adsData;
	private Context mContext;
	
	public OnGalleryClickListener mListener;
	
	public interface OnGalleryClickListener {
		public void onGalleryItemClick(AdsEntity entity);
	}
	
	public void setOnGalleryClickListener(OnGalleryClickListener listener) {
		this.mListener = listener;
	}

	public AdsMiddleLooper(Context context) {
		super(context);
		init(context);
	}

	public AdsMiddleLooper(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		View child = inflater.inflate(R.layout.auto_middle_scroll_gallery, null);
		int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		autoScrollGallery = (AutoScrollGallery) child
				.findViewById(R.id.home_advs_gallery);
		
		autoScrollGallery.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
		autoScrollGallery.setFocusable(true);
		deleteBanner = (ImageView) child.findViewById(R.id.deleteBanner);
		ivAdvDesBg = (ImageView) child.findViewById(R.id.iv_adv_des_bg);
		tvMovieDes = (TextView) child.findViewById(R.id.tv_movie_des);
		ivAdvDesBg.getBackground().setAlpha(150);
		laShouIndicator = (LaShouRedIndicator) child.findViewById(R.id.home_advs_gallery_mark);
		addView(child,params);
	}

	public void setOnDelCallBack(OnDelCallBack onDelCallBack) {
		this.onDelCallBack = onDelCallBack;
	}
	
	/**
	 * 填充数据
	 * @param data
	 */
	public void setData(final List<AdsEntity> data) {
		this.adsData=data;
		if (advGalleryAdapter==null){
			autoScrollGallery.setImagesLength(data.size());
			advGalleryAdapter = new AdvMiddleGalleryAdapter(mContext, data,
					autoScrollGallery);
		}else{
			advGalleryAdapter.setData(data);
			advGalleryAdapter.notifyDataSetChanged();
		}
		setCanAutoScroll(isAutoScroll);
		autoScrollGallery.setAdapter(advGalleryAdapter);
		autoScrollGallery.setSelection(0);
		autoScrollGallery
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						laShouIndicator.check(arg2 % data.size()); 
						tvMovieDes.setText(adsData.get(arg2 % data.size()).getDes());
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});
		
		
		autoScrollGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if (null != mListener) {
					mListener.onGalleryItemClick(data.get(position % data.size()));
				}
			}
		});

		if (data.size() > 1) {
			laShouIndicator.setVisibility(View.VISIBLE);
			laShouIndicator.setCount(data.size());
			autoScrollGallery.startAutoScroll();
		} else {
			laShouIndicator.setVisibility(View.GONE);
		}

		deleteBanner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setVisibility(View.GONE);
				cancleAutoScroll();
				if (onDelCallBack != null)
					onDelCallBack.onDel();
			}
		});
	}

	/**
	 * 设置是否可以自动滚动，true可以自动滚动,false不可以。默认为true
	 * 
	 * @param canAutoScroll
	 */
	public void setCanAutoScroll(boolean canAutoScroll) {
		this.isAutoScroll = canAutoScroll;
		autoScrollGallery.setAutoScroll(canAutoScroll);
	}

	/**
	 * 开始自动滚动
	 */
	public void startAutoScroll() {
		this.isAutoScroll = true;
		autoScrollGallery.startAutoScroll();
	}
	
	/**
	 * 取消自动滚动
	 */
	public void cancleAutoScroll() {
		this.isAutoScroll = false;
		autoScrollGallery.cancleAutoScroll();
	}
	
	public static int dpToPx(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}
	
	/**
	 * 点击删除按钮时的回调
	 * @author Wangzhen
	 *
	 */
	public interface OnDelCallBack {
		void onDel();
	}
	
	/**
	 * Ad实体类
	 * @author Wangzhen
	 *
	 */
	public static class AdsEntity {

		private int index;
		private View view;
		private String uri;
		private Object object;
		private String des;

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public View getView() {
			return view;
		}

		public void setView(View view) {
			this.view = view;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public Object getObject() {
			return object;
		}

		public void setObject(Object object) {
			this.object = object;
		}

		public String getDes() {
			return des;
		}

		public void setDes(String des) {
			this.des = des;
		}
		
	}

}
