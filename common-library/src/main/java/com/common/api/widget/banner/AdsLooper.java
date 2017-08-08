package com.common.api.widget.banner;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.common.api.R;
import com.common.api.widget.indicator.ImageIndicator;

import java.util.List;

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
public class AdsLooper extends LinearLayout {

	private Drawable bannerDefaultPic;
	private AutoScrollGallery autoScrollGallery;
	private AdvGalleryAdapter advGalleryAdapter;
	private ImageView deleteBanner;
	private ImageIndicator laShouIndicator;

	private boolean isAutoScroll = true;
	private OnDelCallBack onDelCallBack;

	private Context mContext;
	
	public OnGalleryClickListener mListener;
	
	private int height;
	private float heightFactor;
	private int space = -1;//设置轮播图的滑动速度
	private IpicCallBack ipicCallBack;
	private OnpageChangeListener mOnPageChangeListener;

	public interface OnGalleryClickListener {
		public void onGalleryItemClick(AdsEntity entity);
	}
	
	public void setOnGalleryClickListener(OnGalleryClickListener listener) {
		this.mListener = listener;
	}

	public AdsLooper(Context context) {
		super(context);
		init(context);
	}

	public void setIpicCallBack(IpicCallBack ipicCallBack){
		this.ipicCallBack = ipicCallBack;
	}
	public AdsLooper(Context context, AttributeSet attrs) {
		super(context, attrs);
		int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		heightFactor = 126.0f / 720;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AdsBanner);
		heightFactor = a.getFloat(R.styleable.AdsBanner_bannerHeightFactor, heightFactor);
		space = a.getInteger(R.styleable.AdsBanner_bannerGallerSlidingSpace, space);
		bannerDefaultPic = a.getDrawable(R.styleable.AdsBanner_bannerDefaultPic);
		height = (int) (1.0f * screenWidth * heightFactor);
		a.recycle();
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		View child = inflater.inflate(R.layout.auto_scroll_gallery, null);
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,(int) height);
		autoScrollGallery = (AutoScrollGallery) child.findViewById(R.id.home_advs_gallery);
		if(space!=-1){
			autoScrollGallery.setSPACE(space*1000);
		}
		autoScrollGallery.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
		autoScrollGallery.setFocusable(true);
		deleteBanner = (ImageView) child.findViewById(R.id.deleteBanner);
		laShouIndicator = (ImageIndicator) child.findViewById(R.id.home_advs_gallery_mark);
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
		if (advGalleryAdapter==null){
			autoScrollGallery.setImagesLength(data.size());
			advGalleryAdapter = new AdvGalleryAdapter(mContext, data,
					autoScrollGallery,height,bannerDefaultPic);
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
							final int arg2, long arg3) {
						if (mOnPageChangeListener!=null) {
							mOnPageChangeListener.onpageSelected(arg3);
						}
						laShouIndicator.check(arg2 % data.size());
						if (arg2 % data.size() == 0) {
							if (ipicCallBack != null) {
								ipicCallBack.ShowName(""+arg2 % data.size()+":"+arg2+":"+data.size());
							}
						}else {
							if (ipicCallBack != null) {
								ipicCallBack.HitName();
							}
						}
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
			laShouIndicator.setCount(data.size());
			laShouIndicator.setVisibility(View.VISIBLE);
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
	 * @author hezd
	 *
	 */
	public static class AdsEntity {
		private int id;
		private String title;
		private String name;
		private int duration;
		private int index;
		private View view;
		private String uri;
		private Object object;
		/**当前item类型 1.抽奖 2.广告*/
		private int type;
		/**抽奖结束时间*/
		private String award_end_time;
		/**抽奖开始时间*/
		private String award_start_time;

		@Override
		public String toString() {
			return "AdsEntity{" +
					"id=" + id +
					", title='" + title + '\'' +
					", name='" + name + '\'' +
					", duration=" + duration +
					", index=" + index +
					", view=" + view +
					", uri='" + uri + '\'' +
					", object=" + object +
					", type=" + type +
					", award_end_time='" + award_end_time + '\'' +
					", award_start_time='" + award_start_time + '\'' +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			AdsEntity adsEntity = (AdsEntity) o;

			if (id != adsEntity.id) return false;
			if (duration != adsEntity.duration) return false;
			if (index != adsEntity.index) return false;
			if (type != adsEntity.type) return false;
			if (title != null ? !title.equals(adsEntity.title) : adsEntity.title != null)
				return false;
			if (name != null ? !name.equals(adsEntity.name) : adsEntity.name != null) return false;
			if (view != null ? !view.equals(adsEntity.view) : adsEntity.view != null) return false;
			if (uri != null ? !uri.equals(adsEntity.uri) : adsEntity.uri != null) return false;
			if (object != null ? !object.equals(adsEntity.object) : adsEntity.object != null)
				return false;
			if (award_end_time != null ? !award_end_time.equals(adsEntity.award_end_time) : adsEntity.award_end_time != null)
				return false;
			return award_start_time != null ? award_start_time.equals(adsEntity.award_start_time) : adsEntity.award_start_time == null;

		}

		@Override
		public int hashCode() {
			int result = id;
			result = 31 * result + (title != null ? title.hashCode() : 0);
			result = 31 * result + (name != null ? name.hashCode() : 0);
			result = 31 * result + duration;
			result = 31 * result + index;
			result = 31 * result + (view != null ? view.hashCode() : 0);
			result = 31 * result + (uri != null ? uri.hashCode() : 0);
			result = 31 * result + (object != null ? object.hashCode() : 0);
			result = 31 * result + type;
			result = 31 * result + (award_end_time != null ? award_end_time.hashCode() : 0);
			result = 31 * result + (award_start_time != null ? award_start_time.hashCode() : 0);
			return result;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

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

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getAward_end_time() {
			return award_end_time;
		}

		public void setAward_end_time(String award_end_time) {
			this.award_end_time = award_end_time;
		}

		public String getAward_start_time() {
			return award_start_time;
		}

		public void setAward_start_time(String award_start_time) {
			this.award_start_time = award_start_time;
		}
	}

	public interface IpicCallBack {
		void ShowName(String name);
		void HitName();
	}
	public void setOnPageSelectedListener(OnpageChangeListener listener) {
		this.mOnPageChangeListener = listener;
	}
	public interface OnpageChangeListener{
		void onpageSelected(long position);
	}
}
