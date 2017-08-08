package com.savor.savorphone.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.savor.savorphone.R;

/**
 * Created by luminita on 2016/12/12.
 */

public class ToastUtil {

    /**
     * 与app色调一致的自定义Toast
     * @param context
     * @param msg 要展示的信息
     */
    public static void showToastSavor(Context context, String msg) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View inflate = layoutInflater.inflate(R.layout.view_text, null);
        ((TextView) inflate.findViewById(R.id.custom_toast)).setText(msg);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(inflate);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

}
