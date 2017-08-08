package com.common.api.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.common.api.R;

public class ShowMessage {
    /**
     * Show Warning to user.
     */

    public static final int MSG_LOCATION_CODE_CENTER = 1;//居中显示标记位
    public static final int MSG_LOCATION_CODE_TOP = 2;  //居顶部显示标记位
    public static final int MSG_LOCATION_CODE_BUTTOM = 3;//居底部显示标记位


    public static void showToast(Activity activity, String str) {
        Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();
    }
    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
    public static void showToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    //toast 居上中下toast
    public static void showToast(Context context, String str,int locStyle) {
        Toast toast=Toast.makeText(context, str, Toast.LENGTH_SHORT);
        //第一个参数：设置toast在屏幕中显示的位置。我现在的设置是居中靠顶
        //第二个参数：相对于第一个参数设置toast位置的横向X轴的偏移量，正数向右偏移，负数向左偏移
        //第三个参数：同的第二个参数道理一样
        //如果你设置的偏移量超过了屏幕的范围，toast将在屏幕内靠近超出的那个边界显示
        switch (locStyle){
            case MSG_LOCATION_CODE_CENTER:
                toast.setGravity(Gravity.CENTER, 0, 0);
                break;
            case MSG_LOCATION_CODE_TOP:
                toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 50);
                break;
            case MSG_LOCATION_CODE_BUTTOM:
                toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 50);
                break;
        }
        toast.show();
    }

    //toast 居上中下toast
    public static void showToast(Activity activity, String str,int locStyle) {
        Toast toast=Toast.makeText(activity, str, Toast.LENGTH_SHORT);
        //第一个参数：设置toast在屏幕中显示的位置。我现在的设置是居中靠顶
        //第二个参数：相对于第一个参数设置toast位置的横向X轴的偏移量，正数向右偏移，负数向左偏移
        //第三个参数：同的第二个参数道理一样
        //如果你设置的偏移量超过了屏幕的范围，toast将在屏幕内靠近超出的那个边界显示
        switch (locStyle){
            case MSG_LOCATION_CODE_CENTER:
                toast.setGravity(Gravity.CENTER, 0, 0);
                break;
            case MSG_LOCATION_CODE_TOP:
                toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 50);
                break;
            case MSG_LOCATION_CODE_BUTTOM:
                toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 50);
                break;
        }
        toast.show();

    }

    public static void showToastSavor(Context context, String msg) {
        if(context==null)
            return;
        TextView tv = new TextView(context);
        tv.setBackgroundColor(context.getResources().getColor(R.color.black));
        tv.setTextColor(context.getResources().getColor(R.color.white));
       // tv.setBackgroundResource(R.drawable.bg_toast);
        tv.setTextSize(18);
        int px = DensityUtil.dip2px(context,10);
        tv.setPadding(px,px,px,px);
        tv.setText(msg);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(tv);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
