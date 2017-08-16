package com.savor.savorphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.savor.savorphone.R;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.STIDUtil;
import com.savor.savorphone.widget.CustomWebView;
import com.savor.savorphone.widget.DefaultWebView;

import java.util.List;

/**
 * H5页面
 * 
 * @author bushlee
 * 
 */
public class WebViewActivity extends BaseActivity implements View.OnClickListener{
	private String TAG = "GuideActivity";
	private ImageView iv_left;
	private TextView tv_center;
	private DefaultWebView mCustomWebView;
	private String webUrl = ConstantValues.H5_BASE_URL;
	//private String webUrl = "http://www.baidu.com";

	public static final int RECORD = 10;
	public static final int SUBJECT = 20;
	private int currentH5 = 0;
	private ImageView iv_right;
	private ImageView iv_left_b;

	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		mContext = this;
		getUrl();
		getViews();
		setViews();
		setListeners();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	private void getUrl(){
		Intent intent = getIntent();
		if (intent != null) {
			String type = intent.getStringExtra("type");
			String url =  intent.getStringExtra("content");
			if ("record".equals(type)) {
				webUrl = url+"?deviceid="+ STIDUtil.getDeviceId(mContext);
				currentH5 = RECORD;
			}
		}
	}
	public void onClick(View view) {

		switch (view.getId()){
			case R.id.iv_left :
			case R.id.iv_left_b :
				RecordUtils.onEvent(this,getString(R.string.menu_help_back));
				onBackPressed();
				break;

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
		iv_left = (ImageView) findViewById(R.id.iv_left);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		iv_left_b = (ImageView) findViewById(R.id.iv_left_b);
		tv_center = (TextView)findViewById(R.id.tv_center);
		iv_left = (ImageView)findViewById(R.id.iv_left);
		mCustomWebView = (DefaultWebView) findViewById(R.id.webview_custom);


	}

	@Override
	public void setViews() {
		//tv_center.setText(mContext.getString(R.string.help));
		//webUrl = "http://devp.admin.littlehotspot.com/Display/geteggAwardRecord?deviceid=B54EBA80-2FA2-4C01-BC0C-587565CE8C07";
		mCustomWebView.loadUrl(webUrl,null);
	}


	@Override
	public void setListeners() {
		iv_left.setOnClickListener(this);
		iv_left_b.setOnClickListener(this);

	}

	@Override
	public void onSuccess(AppApi.Action method, Object obj) {
		switch (method) {
			case GET_ADD_MY_COLLECTION_JSON:

				break;


		}
	}

	@Override
	public void onError(AppApi.Action method, Object obj) {

		if(obj instanceof ResponseErrorMessage) {
			ResponseErrorMessage message = (ResponseErrorMessage) obj;
			int code = message.getCode();
			String msg = message.getMessage();
		}
	}


}
