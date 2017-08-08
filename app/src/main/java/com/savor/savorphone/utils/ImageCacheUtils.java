package com.savor.savorphone.utils;

import android.os.Looper;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.savor.savorphone.SavorApplication;

import java.io.File;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Glide缓存工具类
 * Created by wmm on 2016/11/29.
 */
public class ImageCacheUtils {

    private static ImageCacheUtils inst;
    private static final String ImageExternalCatchDir = SavorApplication.getInstance().getExternalCacheDir() + "/image_cache";

    public static ImageCacheUtils getInstance() {
        if (inst == null) {
            inst = new ImageCacheUtils();
        }
        return inst;
    }
//
//    /**
//     * 清除图片磁盘缓存
//     */
    private static void clearImageDiskCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(SavorApplication.getInstance()).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(SavorApplication.getInstance()).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    /**
//     * 清除图片内存缓存
//     */
    private static void clearImageMemoryCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(SavorApplication.getInstance()).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//
//    /**
//     * 清除图片所有缓存
//     */
    public static void clearImageAllCache() {
        clearImageDiskCache();
        clearImageMemoryCache();
        deleteFolderFile(ImageExternalCatchDir, true);
    }
//
//    /**
//     * 获取Glide造成的缓存大小
//     *
//     * @return CacheSize
//     */
    public static String getCacheSize() {
        try {
            return getFormatSize(getFolderSize(new File(SavorApplication.getInstance().getCacheDir() + "/image_cache")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
//
//    /**
//     * 获取指定文件夹内所有文件大小的和
//     *
//     * @param file file
//     * @return size
//     * @throws Exception
//     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            if(fileList==null||fileList.length == 0)
                return 0;
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
//
//    /**
//     * 删除指定目录下的文件，这里用于缓存的删除
//     *
//     * @param filePath       filePath
//     * @param deleteThisPath deleteThisPath
//     */
    private static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (File file1 : files) {
                        deleteFolderFile(file1.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//
//    /**
//     * 格式化单位
//     *
//     * @param size size
//     * @return size
//     */
    public static String getFormatSize(double size) {
        if (size == 0) {
            Random rnd = new Random();
            size = rnd.nextInt(2000);
        }
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    public static String getCacheKeyByUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
