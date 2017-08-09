package com.savor.savorphone.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.widget.TextView;

import com.savor.savorphone.R;

/**
 * 上传投屏图片进度
 * Created by hezd on 2016/12/26.
 */

public class LoadingProgressDialog extends Dialog  {


    private final Activity mContext;
    private TextView mPercentTv;
    private OnBackKeyDownListener mOnBackKeyDownListener;
    public LoadingProgressDialog(Activity context) {
        super(context, R.style.loading_progress_bar);
        this.mContext = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_progress);
        setCancelable(false);
        mPercentTv = (TextView) findViewById(R.id.tv_percent);
    }

    public void updatePercent(String percent) {
        mPercentTv.setText(percent);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(mOnBackKeyDownListener!=null) {
                mOnBackKeyDownListener.onBackKeyDown();
                return true;
            }
        }
        return false;
    }

    public interface OnBackKeyDownListener {
        void onBackKeyDown();
    }

    public void setOnBackKeyDowListener (OnBackKeyDownListener listener){
        this.mOnBackKeyDownListener = listener;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
        mContext.finish();
    }
}