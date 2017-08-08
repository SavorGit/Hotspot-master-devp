package com.common.api.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.common.api.codec.binary.Base64;
import com.common.api.vo.NotifyObject;

/**
 * @author 朵朵花开
 *
 *	常用系统工具类
 */
public class AppUtils {
	public static  final String AppFileName="common";
	public static  final String AppFileNameCacheDir="cache";
	public static  final String AppFileNameFileDir="file";
	public static  final String AppFileNameOtherDir="other";
	public static  final String AppThemeImgDir = "theme";
    public static final String appCacheDir = "savor";
	private static long lastClickTime; 
	private static int xuhao=1;
	// UTF-8 encoding
    private static final String ENCODING_UTF8 = "UTF-8";
    private static final byte[] SECRET_KEY_NORMAL;
    
    /** DATE FORMAT 日期格式 例如"yyyy-MM-dd HH:mm:ss"*/
    public static final String DATEFORMAT_YYMMDD_HHMMSS="yyyy-MM-dd HH:mm:ss";
    public static final String DATEFORMAT_YYMMDD="yyyy-MM-dd";
    public static final long CATCHE_EXPIRED_TIME=1*24*60*60*1000;
    
    static {
        SECRET_KEY_NORMAL =  DigestUtils.md5(DigestUtils.md5("7U727ALEWH8".getBytes()));
    }
	public static enum StorageMode {
		/**手机内存 */
		MobileMemory,
		/**存储卡 */
		SDCark;
	}
	public static enum StorageFile {
		/**缓存数据 */
		cache,
		/**保存数据 */
		file,
		/**特殊作用*/
		other,
		/**存放活动主题*/
		theme;
	}
	private static TrustManager[] trustAllCerts;
	private static StorageMode storageMode;
	
	/** SDCard是否可用 **/
	
	/** SDCard的根路径 **/
	private static String SDCARD_PATH;
	
	public static final int NOCONNECTION = 0;
	public static final int WIFI = 1;
	public static final int MOBILE = 2;

	/**
	 * 返回手机连接网络类型
	 * @param context
	 * @return 0： 无连接  1：wifi  2： mobile
	 */
	public static int getNetworkType(Context context)
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		int networkType = NOCONNECTION;
		if (networkInfo != null)
		{
			int type = networkInfo.getType();
			networkType = type == ConnectivityManager.TYPE_WIFI ? WIFI : MOBILE;
		}
		return networkType;
	}
	/**
	 * 防止按钮被连续点击
	 * int seon  秒数 如：1、10
	 * */
	public static boolean isFastDoubleClick(int seon) {  
		
        long time = System.currentTimeMillis();  
        long timeD = time - lastClickTime;  
        seon=seon*1000;
        if ( 0 < timeD && timeD < seon) {     
            return true;     
        }     
        lastClickTime = time;     
        return false;     
    }  
	/**
	 * 取得SD卡路径，以/结尾
	 * @return SD卡路径
	 */
	public static String getSDCardPath(){
		boolean IS_MOUNTED = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		if(!IS_MOUNTED) {
			return null;
		}
		if(null != SDCARD_PATH) {
			return SDCARD_PATH;
		}
		File path = Environment.getExternalStorageDirectory(); 
		String SDCardPath = path.getAbsolutePath();
		SDCardPath += SDCardPath.endsWith(File.separator) ? "" : File.separator;
		SDCARD_PATH = SDCardPath;
		return SDCardPath;
	}

	/**
	 * @param context
	 * @param mode		StorageFile.cache or StorageFile.file
	 * @return
	 */
	public static String getPath(Context context,StorageFile mode){
 		String path;
 		boolean IS_MOUNTED = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		if(IS_MOUNTED) {
			path=getSDCardPath();
		}else {
			final File cacheDir = context.getCacheDir();
			path=cacheDir.getAbsolutePath();
		}
		File target=new File(path,AppFileName);
		if(!target.exists()){
			target.mkdir();
		}
		File targetCache=new File(path+File.separator+AppFileName+File.separator,AppFileNameCacheDir);
		if(!targetCache.exists()){
			targetCache.mkdir();
		}
		File targetFile=new File(path+File.separator+AppFileName+File.separator,AppFileNameFileDir);
		if(!targetFile.exists()){
			targetFile.mkdir();
		}
		File targetotherFile=new File(path+File.separator+AppFileName+File.separator,AppFileNameOtherDir);
		if(!targetotherFile.exists()){
			targetotherFile.mkdir();
		}
		File themeFile=new File(path+File.separator+AppFileName+File.separator,AppThemeImgDir);
		if(!themeFile.exists()){
			themeFile.mkdir();
		}
		if(mode==StorageFile.cache){
			path= targetCache.getAbsolutePath()+File.separator;
		}else if(mode==StorageFile.file){
			path= targetFile.getAbsolutePath()+File.separator;
		}else if (mode==StorageFile.theme){
			path= themeFile.getAbsolutePath()+File.separator;
		}else {
			path= targetotherFile.getAbsolutePath()+File.separator;
		}
		return path;
	}
	public static void trustAllSSLForHttpsURLConnection() {
        // Create a trust manager that does not validate certificate chains
        if (trustAllCerts == null) {
            trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
        }
        // Install the all-trusting trust manager
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (Throwable e) {
            LogUtils.e(e.getMessage(), e);
        }
        HttpsURLConnection.setDefaultHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    } 
	/**
     * Returns whether the network is available
     */
    public static boolean isNetworkAvailable(Context context) {
        
        if (context == null) {
            return false;
        }
        
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            LogUtils.e("couldn't get connectivity manager");
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0, length = info.length; i < length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * Returns whether the network is mobile
     */
    public static boolean isMobileNetwork(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            // Log.w(Constants.TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }
    public static boolean isWifiNetwork(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            // Log.w(Constants.TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }
    
    public static String getCurTime(){
    	SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT_YYMMDD_HHMMSS);//设置日期格式
         return df.format(new Date());// new Date()为获取当前系统时间
    }
    /** 根据日期格式获取当前日期 */
    public static String getCurTime(String format){
    	SimpleDateFormat dfTemp = new SimpleDateFormat(format);//设置日期格式
         return dfTemp.format(new Date());// new Date()为获取当前系统时间
    }
    
    /** 根据日期格式获取当前日期 */
    public static String getStrTime(String time){
    	String mTime = time.replaceAll("-", "");
         return mTime;// new Date()为获取当前系统时间
    }
    public static void clearAllCache(final Context context,final NotifyObject notify){
    	
        Thread clearTask = new Thread() {
            @Override
            public void run() {
            	String path=getPath(context, StorageFile.cache);
                
                File cacheDirectory = new File(path);
                if (cacheDirectory.exists()) {
                    String[] files = cacheDirectory.list();
                    
                    if (files == null || files.length == 0) {
                    	notify.message("亲~，没有需要删除的缓存~");
                        return;
                    }
                    for (String file : files) { 
                         new File(cacheDirectory, file).delete();
                    }
                }
                notify.message("亲~,恭喜,缓存已清除");
            }
        };
        clearTask.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        clearTask.start();
    }
    public static void clearAllFile(final Context context){
    	
        Thread clearTask = new Thread() {
            @Override
            public void run() {
            	String path=getPath(context, StorageFile.file);
                
                File cacheDirectory = new File(path);
                if (cacheDirectory.exists()) {
                    String[] files = cacheDirectory.list();
                    
                    if (files == null || files.length == 0) {
                        return;
                    }
                    for (String file : files) { 
                         new File(cacheDirectory, file).delete();
                    }
                }
            }
        };
        clearTask.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        clearTask.start();
    }
    
    public static String encrypt(String value) {
        if (value == null)
            return null;

        byte[] bytes = getUTF8Bytes(value);
        bytes = new Crypter().encrypt(bytes, SECRET_KEY_NORMAL);
        bytes = Base64.encodeBase64(bytes);

        return getUTF8String(bytes);
    }
    /**
     * <p>
     * Get UTF8 bytes from a string
     * </p>
     * 
     * @param string
     *            String
     * @return UTF8 byte array, or null if failed to get UTF8 byte array
     */
    public static byte[] getUTF8Bytes(String string) {
        if (string == null)
            return new byte[0];

        try {
            return string.getBytes(ENCODING_UTF8);
        } catch (UnsupportedEncodingException e) {
            /*
             * If system doesn't support UTF-8, use another way
             */
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                dos.writeUTF(string);
                byte[] jdata = bos.toByteArray();
                bos.close();
                dos.close();
                byte[] buff = new byte[jdata.length - 2];
                System.arraycopy(jdata, 2, buff, 0, buff.length);
                return buff;
            } catch (IOException ex) {
                return new byte[0];
            }
        }
    }
    /**
     * <p>
     * Get string in UTF-8 encoding
     * </p>
     * 
     * @param b
     *            byte array
     * @return string in utf-8 encoding, or empty if the byte array is not encoded with UTF-8
     */
    public static String getUTF8String(byte[] b) {
        if (b == null)
            return "";
        return getUTF8String(b, 0, b.length);
    }

    /**
     * <p>
     * Get string in UTF-8 encoding
     * </p>
     */
    public static String getUTF8String(byte[] b, int start, int length) {
        if (b == null) {
            return "";
        } else {
            try {
                return new String(b, start, length, ENCODING_UTF8);
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        }
    }
	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec(command);
			if (proc != null) {
				BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));

				String line = null;
				while ((line = is.readLine()) != null) {
				    LogUtils.d("aMarket line:" + line);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 	判断当前应用是否是顶栈
	 * @param context
	 * @return
	 */
	public static boolean isAppOnForeground(Context context){
		PackageInfo info = null;
		try{
		info=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		String curPackage=info.packageName;
		ActivityManager mActivityManager = ((ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE));
		List<RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
		if(tasksInfo!=null && tasksInfo.size()>0){
			if (!TextUtils.isEmpty(curPackage) && curPackage.equals(tasksInfo.get(0).topActivity.getPackageName())) {
				/**当前应用是顶栈*/
				return true;
			} 
			
		}
		}catch(Exception ex){
			LogUtils.e(ex.toString());
		}
		return false;
	}
	public static long getFileSizes(File f){
        long s=0;
        FileInputStream fis = null;
        try{
            if(!f.exists()){
                return s;
            }
            fis = new FileInputStream(f);
            s= fis.available();
        }catch(Exception ex){
            LogUtils.e(ex.toString());
        }finally{
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fis=null;
        }
         
        return s;
    }
	
	 
	/**
	 * <code>getLocalIPAddress</code>
	 * @description: TODO(获得本机的IP地址) 
	 * @return
	 * @throws SocketException
	 * @since   2014-4-16    yourname
	 */
	public static String getLocalIPAddress(){ 
	    try{
    	    for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();en.hasMoreElements();){ 
    	        NetworkInterface intf = en.nextElement();
    	        for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){ 
    	            InetAddress inetAddress = enumIpAddr.nextElement(); 
    	            if(android.os.Build.VERSION.SDK_INT>10){
    	                /**android 4.0以上版本*/
    	                if(!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)){ 
    	                    return inetAddress.getHostAddress().toString(); 
    	                } 
    	            }else {
    	                if(!inetAddress.isLoopbackAddress()){ 
                            return inetAddress.getHostAddress().toString(); 
                        } 
    	            }
    	        } 
    	    } 
	    }catch(Exception ex){
	        ex.toString();
	    }
	    return null; 
	} 
	/**
     * 计算md5值
     */
    public static byte[] getMd5(String str) {
        if (str == null) {
            return null;
        }
        byte[] result = null;
        try {
            result = getMd5(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
        return result;
    }

    /**
     * 计算md5值
     */
    public static byte[] getMd5(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StreamUtil su = new StreamUtil(true);
        try {
            su.copyStreamInner(new ByteArrayInputStream(bytes), null);
        } catch (IOException e) {
        }
        return su.getMD5();
    }
    
    /**
     * Get MD5 Code
     */
    public static String getMD5(String text) {
        try {
            byte[] byteArray = text.getBytes("utf8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteArray, 0, byteArray.length);
            return StringUtils.toHexString(md.digest(), false);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    /**
     * Get MD5 Code
     */
    public static String getMD5(byte[] byteArray) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteArray, 0, byteArray.length);
            return StringUtils.toHexString(md.digest(), false);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * <p>
     * Parse long value from string
     * </p>
     * 
     * @param value
     *            string
     * @return long value
     */
    public static long getLong(String value) {
        if (value == null)
            return 0L;

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
    
    /**
     * 清除过期的缓存文件（FIle里）
     */
    public static void clearExpiredFile(final Context context,final boolean isAllFile) {
        Thread clearTask = new Thread() {
            @Override
            public void run() {
            	try{
	            	String path= AppUtils.getPath(context,StorageFile.file);
	                File cacheDirectory =new File(path);
	                if (cacheDirectory.exists()) {
	                    File[] files = cacheDirectory.listFiles();
	                    
	                    if (files == null || files.length == 0) {
	                        return;
	                    }
	                  
	                    long currentTime = System.currentTimeMillis();
	                    for (File f : files) {
	                    	if(isAllFile){
	                    		f.delete();
	                    		continue;
	                        }
	                    	long lastTime=f.lastModified();
	                    	if(currentTime-lastTime>CATCHE_EXPIRED_TIME){
	                    		f.delete();
	                    	}
	                    }
	                }
            	}catch(Exception ex){
            		LogUtils.e(ex.toString());
            	}
            }
        };
        clearTask.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        clearTask.start();
    }

    /**
     * 清除过期的缓存文件（FIle里）
     */
    public static void clearExpiredCacheFile(final Context context) {
        Thread clearTask = new Thread() {
            @Override
            public void run() {
            	try{
	            	String path= AppUtils.getPath(context,StorageFile.cache);
	                File cacheDirectory =new File(path);
	                if (cacheDirectory.exists()) {
	                    File[] files = cacheDirectory.listFiles();
	                    
	                    if (files == null || files.length == 0) {
	                        return;
	                    }
	                  
	                    long currentTime = System.currentTimeMillis();
	                    for (File f : files) {
	                    	long lastTime=f.lastModified();
	                    	if(currentTime-lastTime>CATCHE_EXPIRED_TIME){
	                    		f.delete();
	                    	}
	                    }
	                }
            	}catch(Exception ex){
            		LogUtils.e(ex.toString());
            	}
            }
        };
        clearTask.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        clearTask.start();
    }
    /**
	 * 隐藏软键盘
	 * 
	 * @param activity
	 */
	public static void hideSoftKeybord(Activity activity) {

        if (null == activity) {
            return;
        }
        try {
            final View v = activity.getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {

        }
    }

    /**
	 * 强制聚焦并打开键盘
	 *
	 * @param activity
	 * @param editText
	 */
    public static void tryFocusEditText(Activity activity, EditText editText) {

        if (editText.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    public static String getSavorCachDir(Context context) {
        String path = getPath(context, StorageFile.cache);
        path+="savor"+File.separator;
        File cacheDir = new File(path);
        if(!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return path;
    }


    public static String getAppVersion(Context context) {
        String version = "1.0";
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }


}