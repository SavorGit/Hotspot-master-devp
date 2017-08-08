package com.savor.savorphone.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by hezd on 2017/2/8.
 */

public class NetWorkUtil {
    public static String getLocalUrl(Context context) {
        WifiManager wifiManger = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManger.getConnectionInfo();
         String localIpd = intToIp(wifiInfo.getIpAddress());
        return "http://" + localIpd + ":8080";
    }

    /**
     * 格式化ip
     *
     * @param i
     * @return
     */
    public static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }
}
