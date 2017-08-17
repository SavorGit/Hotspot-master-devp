package com.savor.savorphone.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.savor.savorphone.R;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.widget.DefaultWebView;

/**
 * 帮助页面
 * 
 * @author savor
 * 
 */
public class FileProHelpActivity extends BaseActivity implements View.OnClickListener {
	private String TAG = "GuideActivity";
//	private ViewPager vp_guide;
//	private LinearLayout ll_dots;
//	private Context mContext;
	private LayoutInflater mInflater;
//	private List<RelativeLayout> contentList;
//	// private int pxDotLayout = 16;
//	// private int pxDotDiameter = 8;
//	private ImageView preDot;
//	private int[] images = { R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3 };
	private ImageView iv_left;
	private TextView tv_center;
	private RelativeLayout btn_la;
	private DefaultWebView mCustomWebView;
	private String mUrl = "http://h5.rerdian.com/Public/html/help/helpone_ang.html";
	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		mContext = this;
		mInflater = LayoutInflater.from(mContext);
		getUrl();
		getViews();
		setViews();
		setListeners();


	}

	private void getUrl(){
	}
	public void onClick(View view) {
		RecordUtils.onEvent(this,getString(R.string.menu_help_back));
		onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		RecordUtils.onPageEndAndPause(this,this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		RecordUtils.onPageStartAndResume(this,this);
	}

	@Override
	public void getViews() {

		tv_center = (TextView)findViewById(R.id.tv_center);
		iv_left = (ImageView)findViewById(R.id.iv_left);
		mCustomWebView = (DefaultWebView) findViewById(R.id.webview_custom);

	}

	@Override
	public void setViews() {
		tv_center.setText(mContext.getString(R.string.file_pro_step));
//		String Url = "http://h5.rerdian.com/Public/html/help/helpone_ang.html";
//		//String Url = "www.baidu.com";
//		if (!TextUtils.isEmpty(Url)) {
			mCustomWebView.loadUrl(ConstantValues.H5_FILE_PRO_HELP,null);
//		}
	}

	@Override
	public void setListeners() {
		iv_left.setOnClickListener(this);
	}



}
