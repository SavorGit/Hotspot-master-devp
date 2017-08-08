package com.savor.savorphone.interfaces;

/**
 * 热点首页ui接口
 * Created by hezd on 2017/1/19.
 */

public interface IHotspotSenseView extends IBaseView {

    /**初始化场景状态*/
    void initSenseState();

    /**
     * 显示右下角的连接按钮
     * @param isBind 是否已绑定，用来更新连接状态的变量
     */
    void showProjection(boolean isBind);

    /**
     * 隐藏绑定按钮，切换到非酒店环境
     */
    void hideProjection();

    /**刷新数据*/
    void refreshData();
    /**检测酒店环境*/
     void checkSense();
}
