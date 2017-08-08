package com.savor.savorphone.utils;

import android.content.Context;
import android.widget.TextView;

import com.common.api.utils.ShowMessage;
import com.savor.savorphone.widget.CustomActionSheetDialog;

/**
 * 对话框工具类
 * Created by hezd on 2017/1/19.
 */

public class DialogUtil {
    /**
     * 显示清楚缓存的对话框
     * @param context
     * @param size 显示当前缓存大小的文本
     */
    public static void showClearCacheDialog(final Context context, final TextView size) {
        new CustomActionSheetDialog(context)
                .builder()
                .setTitle("本次清除缓存,将清除图片、视频、以及您的文件缓存,请确认您的操作")
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("确定", CustomActionSheetDialog.SheetItemColor.Red,
                        new CustomActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                // new CleanAsyncTask().execute();
                                ImageCacheUtils.clearImageAllCache();
                                //showToast(view,"已清除");
                                size.setText("0.0MB");
                                ShowMessage.showToast(context,"清除缓存成功");
                            }
                        }).show();
    }
}
