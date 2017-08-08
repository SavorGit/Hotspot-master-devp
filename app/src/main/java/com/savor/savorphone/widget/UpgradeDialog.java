package com.savor.savorphone.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.savor.savorphone.R;

import java.util.zip.Inflater;

/**
 * 自定义AlertDialog
 * Created by luminita on 16/11/21.
 */
public class UpgradeDialog {
    private Context context;
    private Dialog dialog;
    private RelativeLayout lLayout_bg;
    private TextView txt_title;
    //private TextView txt_msg;

    private Button btn_neg;
    private Button btn_mid;
    private Display display;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showInput = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private boolean showMidBtn = false;
    private View view;
    private OnClickListener leftListener;
    private OnClickListener rightListener;
    private String leftButtonText;
    private String rightButtonText;
    private int id;
    private int position;
    private int type = 0;
    private String title;
    private String[] message;
    private LayoutInflater mInflater;
    private LinearLayout msg_la;

    public UpgradeDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        mInflater = LayoutInflater.from(context);
        //inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public UpgradeDialog(Context context, String title, String[] message, String leftText , String rightText , OnClickListener  leftButtonListenner, OnClickListener rightButtonListener){
        this.context = context;
        this.title = title;
        this.message = message;
        this.leftListener = leftButtonListenner;
        this.rightListener = rightButtonListener;
        this.leftButtonText = leftText;
        this.rightButtonText = rightText;
        mInflater = LayoutInflater.from(context);
        builder();
    }

    public UpgradeDialog(Context context, String title, String[] message,String rightText,OnClickListener rightButtonListener){
        this.context = context;
        this.title = title;
        this.message = message;
        this.rightListener = rightButtonListener;
        this.rightButtonText = rightText;
        type = 1;
        mInflater = LayoutInflater.from(context);
        builder();
    }

//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        (new Handler()).postDelayed(new Runnable() {
//            public void run() {
//
//                InputMethodManager inManager = (InputMethodManager)et_input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        },500);
//    }
    public UpgradeDialog builder() {
        view = LayoutInflater.from(context).inflate(R.layout.view_hotsdialog, null);
        msg_la = (LinearLayout) view.findViewById(R.id.msg_la);
        lLayout_bg = (RelativeLayout) view.findViewById(R.id.layout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setVisibility(View.INVISIBLE);
        //txt_msg = (TextView) view.findViewById(R.id.txt_msg);
        //txt_msg.setVisibility(View.VISIBLE);

        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.VISIBLE);

        btn_mid = (Button) view.findViewById(R.id.btn_mid);
        btn_mid.setVisibility(View.VISIBLE);
        if (dialog == null) {
            dialog = new Dialog(context, R.style.AlertDialogStyle);
        }
        dialog.setContentView(view);
        if(title.equals("")){
            txt_title.setVisibility(View.INVISIBLE);
            //txt_title.setGravity(Gravity.GONE);
            txt_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
            view.setVisibility(View.INVISIBLE);
        }else{
            txt_title.setVisibility(View.INVISIBLE);
            txt_title.setText(title);
        }

        if (message!= null && message.length>0) {
            addView();
        }
        //txt_msg.setText(message);

        if(leftListener == null){
            btn_mid.setVisibility(View.GONE);
        }
        if(rightListener == null){
            btn_neg.setVisibility(View.GONE);
        }

        if(leftButtonText != null){
            btn_mid.setText(leftButtonText);
        }
        if(rightButtonText != null){
            btn_neg.setText(rightButtonText);
        }

        if (leftListener == null) {
            btn_mid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } else {
            btn_mid.setOnClickListener(leftListener);
        }

        if (rightListener == null) {
            btn_neg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } else {
            btn_neg.setOnClickListener(rightListener);
        }

        if (type == 1) {
            btn_mid.setVisibility(View.GONE);
        }
        dialog.setCancelable(false);// 不可以用“返回键”取消
//        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (
//                display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));

        return this;
    }

    private void addView(){
        for (int i = 0; i < message.length; i++) {
            View v = mInflater.inflate(R.layout.upgrade_info_item_layout, null);
            TextView info = (TextView)v.findViewById(R.id.info);
            String msg = message[i];
            if (!TextUtils.isEmpty(msg)) {
                info.setText(msg);
            }
            //convertView = mInflater.inflate(R.layout.item_video, null);
            msg_la.addView(v);
        }
    }

    public void dismiss(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

    public void show(){
        if(dialog != null){
            dialog.show();
        }
    }

}
