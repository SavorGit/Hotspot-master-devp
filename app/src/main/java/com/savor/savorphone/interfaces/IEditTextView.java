package com.savor.savorphone.interfaces;

import com.savor.savorphone.presenter.EditPicPresenter;

/**
 * 文字编辑视图接口
 * Created by hezd on 2017/2/7.
 */

public interface IEditTextView {
    /**
     * 展示初始化ui
     */
    void showNormalUi();
    /**展示预编辑状态ui*/
    void showPreEditUI();
    /**展示编辑状态ui*/
    void showEditTextUI();
    /**重置UI,隐藏所有组件*/
    void resetUI();
}
