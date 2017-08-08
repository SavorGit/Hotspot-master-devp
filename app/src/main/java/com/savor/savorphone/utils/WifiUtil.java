package com.savor.savorphone.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by hezd on 2017/2/11.
 */

public class WifiUtil {
    public  static String getWifiName(Context context) {
        if(context == null)
            return null;
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        if(!TextUtils.isEmpty(wifiId)) {
            wifiId = wifiId.replace("\"","");
        }
        return wifiId;
    }

    public static boolean checkWifiState(Context context) {
        WifiManager wifiManger = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManger.getWifiState()==WifiManager.WIFI_STATE_ENABLED;
    }

    public static boolean isHotelNewWork(Context context) {
        WifiManager wifiManger = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            return false;
        }else {
            WifiInfo wifiInfo = wifiManger.getConnectionInfo();
            String localIp = intToIp(wifiInfo.getIpAddress());
            if("192.168.43".equals(localIp))
                return true;
            else
                return false;
        }
    }
    private static String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." ;
    }
}
