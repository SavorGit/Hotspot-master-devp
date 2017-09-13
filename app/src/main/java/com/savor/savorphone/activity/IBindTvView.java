package com.savor.savorphone.activity;

import com.savor.savorphone.bean.RotateProResponse;
import com.savor.savorphone.interfaces.IBaseView;

/**
 * Created by hezd on 2016/12/16.
 */

public interface IBindTvView extends IBaseView{

    /**显示修改wifi设置弹窗
     * 1.不在同一网段
     * 2.二维码解析数据通过&符号分割不是4段
     * 3.二维码扫码结果为空
     * */
    void showChangeWifiDialog();

    /**当网络变化wifi可用时*/
//    void updateWifiEnable();

    /**开始呼码*/
    void readyForCode();

    /**关闭二维码提示窗*/
    void closeQrcodeDialog();

    /**绑定成功*/
    void initBindcodeResult();

    /**开始扫连接电视*/
    void startLinkTv();

    /**网络不可用*/
//    void updateWifiDisable();

    /**显示断开连接对话框*/
    void showUnLinkDialog();

    /**计算当前旋转角度*/
    void rotate(RotateProResponse rotateResponse);

    void reCheckPlatform();
    /**绑定失败*/
    void bindError();
}
