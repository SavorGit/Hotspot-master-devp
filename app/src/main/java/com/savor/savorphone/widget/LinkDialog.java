package com.savor.savorphone.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.savor.savorphone.R;

/**
 * Created by hezd on 2016/12/26.
 */

public class LinkDialog extends Dialog {

    private Context mContext;
    private String mContent;
    private ImageView mMaskView;
    private TextView mLinkTv;

    public LinkDialog(Context context) {
        super(context, R.style.loading_dialog);
        this.mContext = context;
    }

    public LinkDialog(Context context,String content) {
        super(context, R.style.loading_dialog);
        this.mContext = context;
        this.mContent = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_link_layout);
        getViews();
        setViews();
        setListeners();
    }

    private void getViews() {
        mMaskView = (ImageView) findViewById(R.id.iv_mask);
        mLinkTv = (TextView) findViewById(R.id.tv_link);
    }

    private void setViews() {
        if(!TextUtils.isEmpty(mContent)) {
            mLinkTv.setText(mContent);
        }
    }

    @Override
    public void show() {
        super.show();
        Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.link_slide_left);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        mMaskView.setAnimation(animation);
    }

    private void setListeners() {

    }

    @Override
    public void dismiss() {
        super.dismiss();
        mMaskView.clearAnimation();
    }


}
