package com.savor.savorphone.interfaces;

/**
 *  根视图控件行为定义
 * 1.布局初始化
 * 2.子布局自定义初始化
 * 3.显示，隐藏加载布局
 * 4.土司展示
 * Created by hezd on 2016/12/14.
 */

public interface IBaseView {
    void getViews();
    void setViews();
    void setListeners();
//    void setContentLayout(int resId);
    void showToast(String msg);
    void showLoadingLayout();
    void hideLoadingLayout();
}
