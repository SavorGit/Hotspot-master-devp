package com.savor.savorphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.savor.savorphone.R;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.widget.CustomWebView;
import com.savor.savorphone.widget.DefaultWebView;
import com.savor.savorphone.widget.ProgressBarView;

/**
 * 帮助页面
 * 
 * @author savor
 * 
 */
public class HelpActivity extends BaseActivity implements View.OnClickListener,ProgressBarView.ProgressBarViewClickListener{
	private String TAG = "GuideActivity";
	private ImageView iv_left;
	private TextView tv_center;
	private DefaultWebView mCustomWebView;
	private String webUrl = ConstantValues.H5_BASE_URL;
	private ProgressBarView mProgressLayout;
	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		mContext = this;
		getUrl();
		getViews();
		setViews();
		setListeners();


	}

	private void getUrl(){
		Intent intent = getIntent();
		if (intent != null) {
			String url = intent.getStringExtra("type");
			if ("link".equals(url)) {
				webUrl = webUrl+"helptwo_ang.html";
			}

		}
	}
	public void onClick(View view) {
		RecordUtils.onEvent(this,getString(R.string.menu_help_back));
		//onBackPressed();
		if (mCustomWebView.canGoBack()) {
			mCustomWebView.goBack();
		}else {
			onBackPressed();
		}

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
		mProgressLayout = (ProgressBarView) findViewById(R.id.pbv_loading);

	}

	@Override
	public void setViews() {
		tv_center.setText(mContext.getString(R.string.help));
		mProgressLayout.loadSuccess();

		mCustomWebView.loadUrl(webUrl, null, new VideoPlayVODNotHotelActivity.UpdateProgressListener() {
			@Override
			public void loadFinish() {


			}

			@Override
			public void loadHttpError() {
				mProgressLayout.post(new Runnable() {
					@Override
					public void run() {
						mProgressLayout.loadFailure();
					}
				});
			}
		});

	}

	@Override
	public void setListeners() {
		iv_left.setOnClickListener(this);
		mProgressLayout.setProgressBarViewClickListener(this);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mCustomWebView.canGoBack()) {
			mCustomWebView.goBack();// 返回前一个页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void loadDataEmpty() {

	}

	@Override
	public void loadFailureNoNet() {

	}

	@Override
	public void loadFailure() {
		setViews();
	}
}
