package com.savor.savorphone.utils;

import android.content.Context;

import com.common.api.utils.FileUtils;
import com.google.gson.reflect.TypeToken;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.SpecialDetail;
import com.savor.savorphone.bean.SpecialList;

import java.io.File;
import java.util.List;

/**
 * 缓存工具类
 * Created by hezd on 2017/7/22.
 */

public class SavorCacheUtil {
    private static volatile SavorCacheUtil instance;
    private SavorCacheUtil() {
    }

    public static SavorCacheUtil getInstance() {
        if(instance == null) {
            synchronized (SavorCacheUtil.class) {
                if(instance==null) {
                    instance = new SavorCacheUtil();
                }
            }
        }
        return instance;
    }

    public String getListCachePath(Context context) {
        String listCachePath = SavorApplication.getInstance().getListCachePath();
        File file = new File(listCachePath);
        if(!file.exists()) {
            file.mkdirs();
        }
        return listCachePath;
    }

    /**
     * 缓存创富数据
     * @param context
     * @param data
     */
    public void cacheChuangFuData(Context context,List<CommonListItem> data) {
        String chuangfuPath = getListCachePath(context)+File.separator+"chuangfu.data";
        if(data!=null&&data.size()>0) {
            FileUtils.saveObject(context,chuangfuPath,data);
        }
    }

    /**
     * 获取创富缓存数据
     * @param context
     * @return
     */
    public List<CommonListItem> getChuangFData(Context context) {
        String chuangfuPath = getListCachePath(context)+File.separator+"chuangfu.data";
        File file = new File(chuangfuPath);
        if(!file.exists()) {
            return null;
        }
        List<CommonListItem> data = (List<CommonListItem>) FileUtils.readObject(context,chuangfuPath,new TypeToken<List<CommonListItem>>(){}.getType().getClass());
        return data;
    }

    /**
     * 缓存生活数据
     * @param context
     * @param data
     */
    public void cacheLifeData(Context context,List<CommonListItem> data) {
        String lifePath = getListCachePath(context)+File.separator+"life.data";
        if(data!=null&&data.size()>0) {
            FileUtils.saveObject(context,lifePath,data);
        }
    }

    /**
     * 获取生活缓存数据
     * @param context
     * @return
     */
    public List<CommonListItem> getLifeData(Context context) {
        String lifePath = getListCachePath(context)+File.separator+"life.data";
        File file = new File(lifePath);
        if(!file.exists()) {
            return null;
        }
        List<CommonListItem> data = (List<CommonListItem>) FileUtils.readObject(context,lifePath,new TypeToken<List<CommonListItem>>(){}.getType().getClass());
        return data;
    }

    /**
     * 缓存专题数据（已过时，请使用cacheSpecialDetail替代）
     * @param context
     * @param data
     */
    @Deprecated
    public void cacheSpecialData(Context context,List<CommonListItem> data) {
        String lifePath = getListCachePath(context)+File.separator+"special.data";
        if(data!=null&&data.size()>0) {
            FileUtils.saveObject(context,lifePath,data);
        }
    }

    /**
     * 获取专题缓存数据(已过时，最新版本请使用getSpecialDetail替代
     * @param context
     * @return
     */
    @Deprecated
    public List<CommonListItem> getSpecialData(Context context) {
        String lifePath = getListCachePath(context)+File.separator+"special.data";
        File file = new File(lifePath);
        if(!file.exists()) {
            return null;
        }
        List<CommonListItem> data = (List<CommonListItem>) FileUtils.readObject(context,lifePath,new TypeToken<List<CommonListItem>>(){}.getType().getClass());
        return data;
    }

    /**
     * 缓存专题组详情数据
     * @param context
     * @param specialDetail
     */
    public void cacheSpecialDetail(Context context, SpecialDetail specialDetail) {
        String lifePath = getListCachePath(context)+File.separator+"special.data";
        File file = new File(lifePath);
        if(file.exists())
            file.delete();
        if(specialDetail!=null) {
            FileUtils.saveObject(context,lifePath,specialDetail);
        }
    }

    /**
     * 获取专题组详情缓存
     * @param context
     * @return
     */
    public SpecialDetail getSpecialDetail(Context context) {
        String lifePath = getListCachePath(context)+File.separator+"special.data";
        File file = new File(lifePath);
        if(!file.exists()) {
            return null;
        }
        try {
            return FileUtils.readObject(context,lifePath,SpecialDetail.class);
        }catch (Exception e) {
            file.delete();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 缓存专题组详情数据
     * @param context
     */
    public void cacheSpecialList(Context context, SpecialList specialList) {
        String lifePath = getListCachePath(context)+File.separator+"specialList.data";
        File file = new File(lifePath);
        if(file.exists())
            file.delete();
        if(specialList!=null) {
            FileUtils.saveObject(context,lifePath,specialList);
        }
    }

    /**
     * 获取专题组详情缓存
     * @param context
     * @return
     */
    public SpecialList getSpecialList(Context context) {
        String lifePath = getListCachePath(context)+File.separator+"specialList.data";
        File file = new File(lifePath);
        if(!file.exists()) {
            return null;
        }
        try {
            return FileUtils.readObject(context,lifePath,SpecialList.class);
        }catch (Exception e) {
            file.delete();
            e.printStackTrace();
        }
        return null;
    }
}
