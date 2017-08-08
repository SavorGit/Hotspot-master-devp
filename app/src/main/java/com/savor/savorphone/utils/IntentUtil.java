package com.savor.savorphone.utils;

import android.app.Activity;
import android.content.Intent;

import com.savor.savorphone.bean.SlideSetInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luminita on 2016/12/13.
 */

public class IntentUtil {

    /**
     * 照片列表界面
     */
    public static final int TYPE_PHOTO = 1;
    /**
     * 第一次创建,从幻灯片列表页进来
     */
    public static final int TYPE_SLIDE_BY_LIST = 2;
    /**
     * 从幻灯片详情页进来
     */
    public static final int TYPE_SLIDE_BY_DETAIL = 3;

    public static final String KEY_PHOTO_PATH = "photoPath";
    public static final String KEY_TYPE = "key_type";
    public static final String KEY_SLIDE = "key_slide";
    public static final String KEY_PHOTO_LIST = "key_photo_list";

    /**
     * activity之间通信
     * @param activity 当前activity
     * @param cls 需要跳转的activity
     * @param value 需要携带的string值
     */
    public static void openActivity(Activity activity, Class<?> cls, String value) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(KEY_PHOTO_PATH, value);
        activity.startActivity(intent);
    }

    /**
     * activity之间通信
     * @param activity 当前activity
     * @param cls 需要跳转的activity
     * @param type 跳转的类型
     * @param slideSetInfo 携带的对象
     */
    public static void openActivity(Activity activity, Class<?> cls, int type, SlideSetInfo slideSetInfo) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_SLIDE, slideSetInfo);
        activity.startActivity(intent);

    }

    /**
     * activity之间通信，携带list
     * @param activity 当前activity
     * @param cls 需要跳转的activity
     * @param type 跳转的类型
     * @param slideSetInfo 携带的对象
     * @param picList 传递的集合
     */
    public static void openActivity(Activity activity, Class<?> cls, int type, SlideSetInfo slideSetInfo, ArrayList<String> picList) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_SLIDE, slideSetInfo);
//        intent.putStringArrayListExtra(KEY_PHOTO_LIST, picList);
        activity.startActivity(intent);
    }

    /**
     * 通用打开activity方法
     * @param activity
     * @param cls
     */
    public static void openActivity(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }
}
