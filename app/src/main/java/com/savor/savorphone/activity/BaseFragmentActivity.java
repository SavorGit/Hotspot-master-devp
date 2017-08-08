package com.savor.savorphone.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.core.InitViews;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.interfaces.IBaseView;
import com.savor.savorphone.utils.ActivitiesManager;


public abstract class BaseFragmentActivity extends FragmentActivity implements IBaseView,ApiRequestListener {
	protected FrameLayout backFL;
	protected ImageView backIV;
	protected TextView backTV;
	protected FrameLayout nextFL;
	protected ImageView nextIV;
	protected TextView nextTV;
	protected TextView titleTV;
	
//	protected PictureUtils pictureUtils;
//	protected BitmapDisplayConfig config;
	
	protected Session mSession;
	protected Context mContext;
	private FrameLayout mParentLayout;
	private ContentLoadingProgressBar mLoadingPb;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mSession = Session.get(getApplicationContext());
		mContext = this;
		ActivitiesManager.getInstance().pushActivity(this);
		setContentView(R.layout.base_layout);
//		EtagoClientApplication.setApplicationContext(this);
	}

//	@Override
//	public void setContentLayout(int resId) {
//		mParentLayout = (FrameLayout) findViewById(R.id.fl_parent);
//		mLoadingPb = (ContentLoadingProgressBar) findViewById(R.id.pb_loading);
//		View childView = View.inflate(this, resId, null);
//		mParentLayout.addView(childView,0);
//	}

	@Override
	public void showLoadingLayout() {
		mLoadingPb.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideLoadingLayout() {
		mLoadingPb.setVisibility(View.GONE);
	}

	public void showToast(String message) {
		ShowMessage.showToastSavor(this,message);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivitiesManager.getInstance().popActivity(this);
	}

	@Override
	public void onSuccess(AppApi.Action method, Object obj) {

	}

	@Override
	public void onError(AppApi.Action method, Object obj) {

		if(obj instanceof ResponseErrorMessage) {
			ResponseErrorMessage message = (ResponseErrorMessage) obj;
			int code = message.getCode();
			String msg = message.getMessage();
			showToast(msg);
//			if(1006==code||12056==code) {
//				ActivityPagerUtils.launchLoginActivity(this);
//				mSession.signOut();
//
//				Intent intent = new Intent(this, RegisterGCMSNSService.class);
//				intent.setAction("com.etago.life.pushgcm");
//				startService(intent);
//			}
		}
	}

	@Override
	public void onNetworkFailed(AppApi.Action method) {

	}

}
