package com.savor.savorphone.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.savor.savorphone.R;

/**
 * 自定义AlertDialog
 * Created by luminita on 16/11/21.
 */
public class CustomAlertDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_title;
    private TextView txt_msg;
    private EditText et_input;
    private Button btn_neg;
    private Button btn_pos;
    private Button btn_mid;
    private ImageView line1, line2;
    private Display display;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showInput = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private boolean showMidBtn = false;
    private View view;

    public CustomAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }


    public CustomAlertDialog builder() {
        view = LayoutInflater.from(context).inflate(R.layout.view_alertdialog, null);

        lLayout_bg = (LinearLayout) view.findViewById(R.id.layout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setVisibility(View.GONE);
        txt_msg = (TextView) view.findViewById(R.id.txt_msg);
        txt_msg.setVisibility(View.GONE);
        et_input = (EditText) view.findViewById(R.id.et_input);
        et_input.setVisibility(View.GONE);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);
        btn_mid = (Button) view.findViewById(R.id.btn_mid);
        btn_mid.setVisibility(View.GONE);
        line1 = (ImageView) view.findViewById(R.id.line1);
        line1.setVisibility(View.GONE);
        line2 = (ImageView) view.findViewById(R.id.line2);
        line2.setVisibility(View.GONE);

        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (
                display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));

        return this;
    }

    public CustomAlertDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    public CustomAlertDialog setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    public CustomAlertDialog setInputHint(String hint) {
        showInput = true;
        if ("".equals(hint)) {
            et_input.setHint("请输入");
        } else {
            et_input.setHint(hint);
        }
        return this;
    }

    public String getInputText() {
        return et_input.getText().toString();
    }

    public CustomAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public CustomAlertDialog setPositiveButton(String text, final OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public CustomAlertDialog setNegativeButton(String text, final OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("取消");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public CustomAlertDialog setMidButton(String text, final OnClickListener listener) {
        showMidBtn = true;
        if ("".equals(text)) {
            btn_mid.setText("取消");
        } else {
            btn_mid.setText(text);
        }
        btn_mid.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText("提示");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (showInput) {
            et_input.setVisibility(View.VISIBLE);

        }

        //中间按钮
        if (showMidBtn) {
            btn_mid.setText("取消");
            btn_mid.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
            btn_mid.setBackgroundResource(R.drawable.alertdialog_single_selector);
            btn_mid.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (!showPosBtn && !showNegBtn) {
            btn_pos.setText("确定");
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            btn_pos.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
            line1.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }
    }

    public void show() {
        setLayout();
        dialog.show();
//         et_input.setFocusable(true);
//            et_input.setFocusableInTouchMode(true);
//            et_input.requestFocus();
        et_input.post(new Runnable() {
            @Override
            public void run() {
                AppUtils.tryFocusEditText((Activity) context,et_input);
                et_input.setClickable(true);
            }
        });
    }
}
