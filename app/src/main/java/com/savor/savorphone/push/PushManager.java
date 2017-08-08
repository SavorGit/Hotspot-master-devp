//package com.savor.savorphone.push;
//
///**
// * Created by hezd on 2016/12/13.
// */
//
//public class PushManager {
//    private static volatile PushManager mInstance = null;
//    IPushHandler mPushHandler ;
//
//    private PushManager() {
//    }
//
//    public static PushManager getInstance() {
//        if(mInstance==null) {
//            synchronized (PushManager.class) {
//                if(mInstance==null)
//                    mInstance = new PushManager();
//            }
//        }
//        return mInstance;
//    }
//
//    public IPushHandler getPushHandler() {
//        if(mPushHandler == null) {
//            mPushHandler = new UmentPushHandler();
//        }
//        return mPushHandler;
//    }
//}
