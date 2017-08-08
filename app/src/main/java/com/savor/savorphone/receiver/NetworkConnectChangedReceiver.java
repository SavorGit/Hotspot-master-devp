package com.savor.savorphone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.common.api.utils.LogUtils;

import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;


public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private Handler mHandler;

    private NetworkConnectChangedReceiver() {
    }

    public NetworkConnectChangedReceiver(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info != null && info.isAvailable()) {
                String name = info.getTypeName();
                mHandler.removeMessages(WIFI_STATE_ENABLED);
                mHandler.sendEmptyMessage(WIFI_STATE_ENABLED);
                LogUtils.d("savor:connectivity 网络可用，当前网络类型：" + name);
            } else {
                LogUtils.d("savor:connectivity 没有可用网络");
                mHandler.removeMessages(WIFI_STATE_DISABLED);
                mHandler.sendEmptyMessage(WIFI_STATE_DISABLED);
            }
        }
    }
}
