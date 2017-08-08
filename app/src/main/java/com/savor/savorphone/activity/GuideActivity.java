package com.savor.savorphone.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.savor.savorphone.R;
import com.savor.savorphone.utils.RecordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 第一次启动时候引导页
 * 
 * @author savor
 * 
 */
public class GuideActivity extends BaseActivity implements OnPageChangeListener,View.OnClickListener {
	private String TAG = "GuideActivity";
	private ViewPager vp_guide;
	private LinearLayout ll_dots;
	private Context mContext;
	private LayoutInflater mInflater;
	private List<RelativeLayout> contentList;
	private List<ImageView> dotList;
	private final int PICK_CITY = 1;
	// private int pxDotLayout = 16;
	// private int pxDotDiameter = 8;
	private TextView btn_start;
	private RelativeLayout btn_la;
	private ImageView preDot;
	private int mCurrentItem;
	private int[] images = { R.drawable.guides_1, R.drawable.guides_2, R.drawable.guides_3 };
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PICK_CITY:
				// 跳转选择城市
				Intent pickIntent = new Intent(mContext, HotspotMainActivity.class);
				Intent intent = getIntent();
				if(intent!=null&&("application/pdf").equals(intent.getType())) {
					Uri data = getIntent().getData();
					pickIntent.setDataAndType(data,intent.getType());
					pickIntent.setData(data);
				}
				startActivity(pickIntent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_in_left);// 这部分代码是切换Activity时的动画，看起来就不会很生硬
				finish();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		//mSession.setNeedGuide(false);
		mInflater = LayoutInflater.from(mContext);

		setContentView(R.layout.activity_guide);
		vp_guide = (ViewPager) findViewById(R.id.vp_guide);
		ll_dots = (LinearLayout) findViewById(R.id.ll_dots);
		ll_dots.getChildAt(0).setBackgroundResource(R.drawable.view_dot_white);
		btn_start = (TextView) findViewById(R.id.btn_start);
		btn_la = (RelativeLayout) findViewById(R.id.btn_la);
		preDot = (ImageView) ll_dots.getChildAt(0);
		initViewPager();
		mSession.setNeedGuide(false);
	}

	@Override
	public void onBackPressed() {
		Process.killProcess(android.os.Process.myPid());
		super.onBackPressed();
	}

	private void initViewPager() {
		contentList = new ArrayList<RelativeLayout>();
		// dotList = new ArrayList<ImageView>();
		for (int i = 0; i < images.length; i++) {
			RelativeLayout inflate = (RelativeLayout) mInflater.inflate(
					R.layout.view_guide, null);
			((ImageView) inflate.findViewById(R.id.iv_guide))
					.setBackgroundResource(images[i]);


			// if (i == images.length - 1) {
			// inflate.findViewById(R.id.btn_start)
			// .setVisibility(View.VISIBLE);
			// inflate.findViewById(R.id.btn_start).setOnClickListener(this);
			// }
			contentList.add(inflate);
		}
		vp_guide.setAdapter(new GuidePagerAdapter());
		vp_guide.setOnPageChangeListener(this);
		vp_guide.setOnTouchListener(new View.OnTouchListener() {
			float startX;
			float endX;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = event.getX();
					event.getY();
					break;
				case MotionEvent.ACTION_UP:
					endX = event.getX();
					event.getY();
					WindowManager windowManager = (WindowManager) getApplicationContext()
							.getSystemService(Context.WINDOW_SERVICE);

					// 获取屏幕的宽度
					Point size = new Point();
					windowManager.getDefaultDisplay().getSize(size);
					int width = size.x;
					// 首先要确定的是，是否到了最后一页，然后判断是否向左滑动，并且滑动距离是否符合，我这里的判断距离是屏幕宽度的4分之一（这里可以适当控制）
					if (mCurrentItem == (contentList.size() - 1)
							&& startX - endX >= (width / 4)) {
						mHandler.sendEmptyMessage(PICK_CITY);// 进入主页
					}
					break;
				}
				return false;
			}
		});
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	//	LogUtils.d(TAG, "arg0= " + arg0);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

		Log.i(TAG, "第 " + arg0 + " 页");

		if (preDot != null) {
			preDot.setBackgroundResource(R.drawable.view_dot_gray);
		}
		if ( arg0 == images.length-1) {
			btn_start.setVisibility(View.VISIBLE);
			btn_la.setVisibility(View.VISIBLE);
			btn_start.setOnClickListener(this);
		}else {
			btn_start.setVisibility(View.INVISIBLE);
			btn_la.setVisibility(View.INVISIBLE);
		}
		ImageView curDot = (ImageView) ll_dots.getChildAt(arg0);
		curDot.setBackgroundResource(R.drawable.view_dot_white);
		curDot.getBackground().setAlpha(255);
		preDot = curDot;
		preDot.getBackground().setAlpha(255);


//		for (int i = 0; i < ll_dots.getChildCount(); i++) {
//			if (arg0 == 3) {
//				//ll_dots.setVisibility(View.INVISIBLE);
//
//			} else {
//				//ll_dots.setVisibility(View.VISIBLE);
//
//			}
//		}
		mCurrentItem = arg0;
	}

	@Override
	public void getViews() {

	}

	@Override
	public void setViews() {

	}

	@Override
	public void setListeners() {

	}

	@Override
	public void onClick(View view) {
		mHandler.sendEmptyMessage(PICK_CITY);
	}

	private class GuidePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() { // 获得size
			return contentList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View view, int position, Object object) // 销毁Item
		{
			((ViewPager) view).removeView(contentList.get(position));
		}

		@Override
		public Object instantiateItem(View view, int position) // 实例化Item
		{
			((ViewPager) view).addView(contentList.get(position), 0);
			return contentList.get(position);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		RecordUtils.onPageStartAndResume(this,this);
		RecordUtils.onPageStart(this,getString(R.string.guide));
	}

	@Override
	protected void onPause() {
		super.onPause();
		RecordUtils.onPageEndAndPause(this,this);
	}
}
