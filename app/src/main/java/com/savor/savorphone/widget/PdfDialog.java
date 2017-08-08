package com.savor.savorphone.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.savor.savorphone.R;

/**
 * Created by bushlee on 2016/12/26.
 */

public class PdfDialog extends Dialog implements View.OnClickListener {

    private OnConfirmListener mConfirmListener;
    private OnCancelListener mCancelListener;
    private TextView btn;


    public PdfDialog(Context context) {
        super(context, R.style.FileProHintDialog);

    }

    public PdfDialog(Context context,OnConfirmListener confirmListener) {
        super(context, R.style.FileProHintDialog);
        this.mConfirmListener = confirmListener;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_dialog_layout);
        getViews();
        setViews();
        setListeners();
    }

    private void getViews() {
        btn = (TextView) findViewById(R.id.btn);
    }



    private void setViews() {
//        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//        layoutParams.gravity= Gravity.CENTER;
//        layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
//
//        getWindow().getDecorView().setPadding(0, 0, 0, 0);
//
//        getWindow().setAttributes(layoutParams);
    }

    private void setListeners() {
        btn.setOnClickListener(this);
//        mConfirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                if(btn!=null) {
                    mConfirmListener.onConfirm();
                }
                dismiss();
                break;
//            case R.id.tv_cancel:
//                if(mCancelListener!=null) {
//                    mCancelListener.onCancel();
//                }
//                dismiss();
//
//                break;
        }
    }

    public interface OnConfirmListener {
        void onConfirm();
    }

    public interface  OnCancelListener {
        void onCancel();
    }
}
