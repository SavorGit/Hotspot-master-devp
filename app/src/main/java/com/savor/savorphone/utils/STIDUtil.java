package com.savor.savorphone.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.savor.savorphone.bean.UpgradeInfo;
import com.savor.savorphone.core.Session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class STIDUtil {

    private static File file = new File(
            "data/data/com.savor.savorphone/file.xml");

    public static String getDeviceId(Context context) {
        String deviceId = "";
        try {
            deviceId = getIMIEStatus(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (deviceId == null || "".equals(deviceId) || "0".equals(deviceId)) {
            try {
                deviceId = getLocalMac(context).replace(":", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (deviceId == null || "".equals(deviceId) || "0".equals(deviceId)) {
            try {
                deviceId = getAndroidId(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (deviceId == null || "".equals(deviceId) || "0".equals(deviceId)) {
            try {
                deviceId = read();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (deviceId == null || "".equals(deviceId) || "0".equals(deviceId)) {
                UUID uuid = UUID.randomUUID();
                deviceId = uuid.toString().replace("-", "");
                write(deviceId);
            }
        }
        return deviceId;
    }

    public static String getUUID() {
        String read = read();
        if(TextUtils.isEmpty(read)) {
            String uuidStr = "";
            UUID uuid = UUID.randomUUID();
            read = uuid.toString().replace("-", "");
            write(read);
        }
        return read;
    }

    private static String replaceBlank(String mString) {
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        String str = mString;
        Matcher m = p.matcher(str);
        String after = m.replaceAll("");
        return after;
    }

    private static String getMacId(Context context) {
        String mac = "";
        try {
            mac = getLocalMac(context);
        } catch (Exception e) {

        }

        if (TextUtils.isEmpty(mac) || "0".equals(mac)) {
            try {
                mac = getAndroidId(context);
            } catch (Exception e) {

            }
        }

        return mac;
    }

    // IMEI码
    private static String getIMIEStatus(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        return deviceId;
    }

    // Mac地址
    private static String getLocalMac(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    // Mac地址
    private static String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static void write(String str) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Writer out = new OutputStreamWriter(fos, "UTF-8");
            out.write(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String read() {
        StringBuffer buffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            Reader in = new BufferedReader(isr);
            int i;
            while ((i = in.read()) > -1) {
                buffer.append((char) i);
            }
            in.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getChannelIdByChannelName(String channelName) {
        String channelId = "10001";
        if ("tuanche".equals(channelName)) {
            channelId = "10001";
        } else if ("appstore".equals(channelName)) {
            channelId = "10002";
        } else if ("Google".equals(channelName)) {
            channelId = "10003";
        } else if ("baidushouji".equals(channelName)) {
            channelId = "10004";
        } else if ("91shouji".equals(channelName)) {
            channelId = "10005";
        } else if ("androidmark".equals(channelName)) {
            channelId = "10006";
        } else if ("360".equals(channelName)) {
            channelId = "10007";
        } else if ("YingYongBao".equals(channelName)) {
            channelId = "10008";
        } else if ("WanDouJia".equals(channelName)) {
            channelId = "10009";
        } else if ("XiaoMi".equals(channelName)) {
            channelId = "10010";
        } else if ("MeiZu".equals(channelName)) {
            channelId = "10011";
        } else if ("HuaWei".equals(channelName)) {
            channelId = "10012";
        } else if ("Lenovo".equals(channelName)) {
            channelId = "10013";
        } else if ("OPPO".equals(channelName)) {
            channelId = "10014";
        } else if ("vivo".equals(channelName)) {
            channelId = "10015";
        } else if ("JinLi".equals(channelName)) {
            channelId = "10016";
        } else if ("CoolPad".equals(channelName)) {
            channelId = "10017";
        } else if ("Samsung".equals(channelName)) {
            channelId = "10018";
        } else if ("YiDong".equals(channelName)) {
            channelId = "10019";
        } else if ("DianXin".equals(channelName)) {
            channelId = "10020";
        } else if ("LianTong".equals(channelName)) {
            channelId = "10021";
        } else if ("WangYi".equals(channelName)) {
            channelId = "10022";
        } else if ("Sina".equals(channelName)) {
            channelId = "10023";
        } else if ("SoHu".equals(channelName)) {
            channelId = "10024";
        } else if ("sougou".equals(channelName)) {
            channelId = "10025";
        } else if ("UC".equals(channelName)) {
            channelId = "10026";
        } else if ("PP".equals(channelName)) {
            channelId = "10027";
        } else if ("MuMaYi".equals(channelName)) {
            channelId = "10028";
        } else if ("YingYongHui".equals(channelName)) {
            channelId = "10029";
        } else if ("JiFeng".equals(channelName)) {
            channelId = "10030";
        } else if ("AnZhi".equals(channelName)) {
            channelId = "10031";
        } else if ("taobao".equals(channelName)) {
            channelId = "10032";
        } else if ("3Gandroid".equals(channelName)) {
            channelId = "10033";
        } else if ("suning".equals(channelName)) {
            channelId = "10034";
        } else if ("xunlei".equals(channelName)) {
            channelId = "10035";
        } else if ("Nduowan".equals(channelName)) {
            channelId = "10036";
        } else if ("youyi".equals(channelName)) {
            channelId = "10037";
        } else if ("shizimao".equals(channelName)) {
            channelId = "10038";
        } else if ("liqu".equals(channelName)) {
            channelId = "10039";
        } else if ("androidzaixian".equals(channelName)) {
            channelId = "10040";
        } else if ("dangle".equals(channelName)) {
            channelId = "10041";
        } else if ("TencentGuangDianTong".equals(channelName)) {
            channelId = "10042";
        } else if ("WangYiYouDao".equals(channelName)) {
            channelId = "10043";
        } else if ("tianyu".equals(channelName)) {
            channelId = "10044";
        } else if ("feifan".equals(channelName)) {
            channelId = "10045";
        } else if ("shoujizhijia".equals(channelName)) {
            channelId = "10046";
        } else if ("shoujileyuan".equals(channelName)) {
            channelId = "10047";
        } else if ("xiazaiyinhang".equals(channelName)) {
            channelId = "10048";
        } else if ("anqi".equals(channelName)) {
            channelId = "10049";
        } else if ("huajun".equals(channelName)) {
            channelId = "10050";
        } else if ("androidruanjian".equals(channelName)) {
            channelId = "10051";
        } else if ("androidzhijia_ard9".equals(channelName)) {
            channelId = "10052";
        } else if ("androidke".equals(channelName)) {
            channelId = "10053";
        } else if ("tiankong".equals(channelName)) {
            channelId = "10054";
        } else if ("xiazaizhijia".equals(channelName)) {
            channelId = "10055";
        } else if ("OEM".equals(channelName)) {
            channelId = "10056";
        } else if ("kuan".equals(channelName)) {
            channelId = "10057";
        } else if ("lvseruanjian".equals(channelName)) {
            channelId = "10058";
        } else if ("anruan".equals(channelName)) {
            channelId = "10059";
        } else if ("meizumi".equals(channelName)) {
            channelId = "10060";
        } else if ("androidbashi".equals(channelName)) {
            channelId = "10061";
        } else if ("pchome".equals(channelName)) {
            channelId = "10062";
        } else if ("zhuolewang".equals(channelName)) {
            channelId = "10063";
        } else if ("shoujikugou".equals(channelName)) {
            channelId = "10064";
        } else if ("androidzhijia_myapks".equals(channelName)) {
            channelId = "10065";
        } else if ("feiliu".equals(channelName)) {
            channelId = "10066";
        } else if ("jinritoutiao".equals(channelName)) {
            channelId = "20001";
        } else if ("duanxin".equals(channelName)) {
            channelId = "20002";
        } else if ("kanjiafuceng".equals(channelName)) {
            channelId = "20003";
        } else if ("MDownLoadTips".equals(channelName)) {
            channelId = "20004";
        } else if ("MDownLoadTips-ShangHai".equals(channelName)) {
            channelId = "20005";
        } else if ("Old-MDownLoadTips".equals(channelName)) {
            channelId = "20006";
        } else {
            channelId = "10001";
        }
        return channelId;
    }

    // public static String getUserId(Context context) {
    // return
    // Preferences.getUserId(PreferenceManager.getDefaultSharedPreferences(context));
    // }
    //
    // public static String getCityId(Context context) {
    // return
    // Preferences.getCityId(PreferenceManager.getDefaultSharedPreferences(context));
    // }

    public static String getSoftVersion(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "4.3";
        }
    }

    public static String getChannelId(Context context) {
        ApplicationInfo ai;
        try {
            ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            return getChannelIdByChannelName(ai.metaData
                    .getString("UMENG_CHANNEL"));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "10062";
        }
    }

    public static String getSystemVersion() {
        return replaceBlank(android.os.Build.VERSION.RELEASE);
    }

    /**
     * 是否需要版本升级
     *
     * @return
     */
    public static boolean needUpdate(Session appSession, UpgradeInfo versionInfo) {
        if (versionInfo != null) {
            boolean result = false;
            // 如果升级提醒与强制更新有一个打开说明此次需要更新升级

            if (appSession.getVersionCode() == Integer.valueOf(versionInfo.getVersion_code())) {
                result = false;
            } else {
                Double newverCodeNum = 0.0;
                newverCodeNum = Double.valueOf(versionInfo.getVersion_code());
                int currverCodeNum = 0;
                currverCodeNum = appSession.getVersionCode();
                if (newverCodeNum <= currverCodeNum || newverCodeNum == 0) {
                    result = false;
                } else {
                    result = true;
                }
            }

            return result;
        } else {
            return false;
        }
    }

}
