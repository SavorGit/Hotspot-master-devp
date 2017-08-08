package com.savor.savorphone.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.savor.savorphone.R;

/**
 * Created by hezd on 2016/12/26.
 */

public class WebDialog extends Dialog {

    private Context mContext;
    private String mContent;
    private ProgressBar pb_loading;

    public WebDialog(Context context) {
        super(context, R.style.loading_dialog);
        this.mContext = context;
    }

    public WebDialog(Context context, String content) {
        super(context, R.style.loading_dialog);
        this.mContext = context;
        this.mContent = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_web_layout);
        getViews();
        setViews();
        setListeners();
    }

    private void getViews() {
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
       // mLinkTv = (TextView) findViewById(R.id.tv_link);
    }

    private void setViews() {
        if(!TextUtils.isEmpty(mContent)) {
            //mLinkTv.setText(mContent);
        }
    }

    @Override
    public void show() {
        super.show();
//        Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.link_slide_left);
//        animation.setRepeatCount(Animation.INFINITE);
//        animation.setRepeatMode(Animation.RESTART);
//        mMaskView.setAnimation(animation);
    }

    private void setListeners() {

    }

    @Override
    public void dismiss() {
        super.dismiss();
       // mMaskView.clearAnimation();
    }


}
