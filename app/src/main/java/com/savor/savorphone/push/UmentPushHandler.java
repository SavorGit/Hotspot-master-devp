//package com.savor.savorphone.push;
//
//import android.content.Context;
//
//import com.common.api.utils.ShowMessage;
//import com.umeng.message.PushAgent;
//import com.umeng.message.UmengNotificationClickHandler;
//import com.umeng.message.entity.UMessage;
//
///**
// * Created by hezd on 2016/12/13.
// */
//
//public class UmentPushHandler implements IPushHandler<UMessage> {
//
//    @Override
//    public void initPush(Context context) {
//        PushAgent pushAgent = PushAgent.getInstance(context);
//        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
//            @Override
//            public void dealWithCustomAction(Context context, UMessage msg) {
//                handlePushMessage(context,msg);
//            }
//        };
//        pushAgent.setNotificationClickHandler(notificationClickHandler);
//    }
//
//    @Override
//    public void handlePushMessage(Context context, UMessage message) {
//        ShowMessage.showToast(context,message.custom);
//    }
//}
