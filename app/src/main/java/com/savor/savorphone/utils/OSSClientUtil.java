package com.savor.savorphone.utils;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;

/**
 * oss工具类
 * Created by hezd on 2017/4/20.
 */

public class OSSClientUtil {
    private static volatile OSSClientUtil sInstance = null;

    private OSSClientUtil(){}

    public static OSSClientUtil getInstance() {
        if(sInstance==null) {
            synchronized (OSSClientUtil.class) {
                if(sInstance == null) {
                    sInstance = new OSSClientUtil();
                }
            }
        }
        return sInstance;
    }

    public static OSSClient getOSSClient(Context context) {
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(3); // 失败后最大重试次数，默认2次

        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(ConstantValues.ACCESS_KEY_ID, ConstantValues.ACCESS_KEY_SECRET);
        OSSClient oss = new OSSClient(context, ConstantValues.ENDPOINT, credentialProvider,conf);
        return oss;
    }
}
