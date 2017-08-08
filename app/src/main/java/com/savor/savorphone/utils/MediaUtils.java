package com.savor.savorphone.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import com.common.api.utils.LogUtils;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.bean.PhotoInfo;
import com.savor.savorphone.bean.PictureInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MediaUtils {
    private static final String TAG = "MediaUtils";
    public static final int INIT_SUCCESS = 0;

    public static void getMediaPhoto(final Context context,
                                     final List<MediaInfo> datas,Map<String, List<MediaInfo>> map) {
        final String[] columns = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATE_TAKEN};
        ContentResolver mContentResolver = context.getContentResolver();
        // 只查询jpeg和png的图片
        Cursor cursor = mContentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_TAKEN + " DESC");
        while (cursor.moveToNext()) {
            // 获取图片的路径
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
            int createTimeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
            String title = cursor.getString(titleIndex);
            String filename = cursor.getString(dataColumnIndex);
            long createTime = cursor.getLong(createTimeIndex);

            MediaInfo model = new MediaInfo();
            model.setAction("2screen");
            model.setAssetname(title);
            model.setAssetpath(filename);
            model.setAsseturl(NetWorkUtil.getLocalUrl(context) + filename);
            model.setMediaType(MediaInfo.MEDIA_TYPE_PIC);
            model.setCreateTime(createTime);
            datas.add(model);

            //获取图片父路径
            String parentPath = new File(filename).getParentFile().getName();
            if (!map.containsKey(parentPath)) {
                ArrayList<MediaInfo> childList = new ArrayList<>();
                childList.add(model);
                map.put(parentPath, childList);
            } else {
                map.get(parentPath).add(model);
            }
        }
        cursor.close();
    }

    public static void getMediaVideo(final Context context,
                                     final List<MediaInfo> datas) {
        final String[] columns = {MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATE_TAKEN,MediaStore.Video.Media.DURATION};
        ContentResolver mContentResolver = context.getContentResolver();
        // 只查询jpeg和png的图片
        Cursor cursor = mContentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns,null,null
                , MediaStore.Video.Media.DATE_TAKEN + " DESC");
        while (cursor.moveToNext()) {
            // 获取图片的路径
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
            int createTimeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
            long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            String title = cursor.getString(titleIndex);
            String filename = cursor.getString(dataColumnIndex);
            long createTime = cursor.getLong(createTimeIndex);

            MediaInfo model = new MediaInfo();
            model.setAction("2screen");
            model.setAssetname(title);
            model.setAssetcover(filename);
            model.setAssetpath(filename);
            model.setMediaType(MediaInfo.MEDIA_TYPE_VIDEO);
            model.setAsseturl(NetWorkUtil.getLocalUrl(context) + filename);
            model.setCreateTime(createTime);
            model.setAssetlength(duration);
            datas.add(model);
        }
        cursor.close();
    }

    /**
     * 获取本地图片信息
     * @param context
     * @param map
     */
    public static void getImgInfo(Context context, HashMap<String, ArrayList<String>> map) {
        //获取图片信息表
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
        Cursor mCursor = mContentResolver.query(imageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_TAKEN + " DESC");
        while (mCursor.moveToNext()) {
            String imgPath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //获取图片父路径
            String parentPath = new File(imgPath).getParentFile().getName();
            if (!map.containsKey(parentPath)) {
                ArrayList<String> childList = new ArrayList<String>();
                childList.add(imgPath);
                map.put(parentPath, childList);
            } else {
                map.get(parentPath).add(imgPath);
            }
        }
        mCursor.close();
    }

    /**
     * 组装分组界面ListView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成List
     *
     * @param
     * @return
     */
    public static List<PhotoInfo> subGroupOfImage(HashMap<String, ArrayList<String>> hashMap) {
        if (hashMap == null || hashMap.size() == 0) {
            LogUtils.i("hashMap为空");
            return null;
        }
        List<PhotoInfo> list = new ArrayList<PhotoInfo>();
        Iterator<Map.Entry<String, ArrayList<String>>> it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = it.next();
            PhotoInfo mImageBean = new PhotoInfo();
            String key = entry.getKey();
            List<String> value = entry.getValue();
            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片
            list.add(mImageBean);
        }
        LogUtils.i("LIST:" + list.size());
        return list;
    }

    /**
     * 获取当前相册下面所有照片的信息
     * @param context
     * @param datas 用来保存图片信息集合
     * @param childList 当前相册下所有图片的路径
     */
    public static void getFolderAllImg(Context context, List<MediaInfo> datas, List<String> childList) {
        for (int i = 0; i < childList.size(); i++) {
            int startTitle = childList.get(i).lastIndexOf("/") + 1;
            int endTitle = childList.get(i).lastIndexOf(".");
            String title = (String) childList.get(i).subSequence(startTitle, endTitle);
            String filename = childList.get(i);

            MediaInfo model = new MediaInfo();
//            model.setFunction(ConstantsWhat.FunctionsIds.PREPARE);
            model.setAction("2screen");
            model.setMediaType(MediaInfo.MEDIA_TYPE_PIC);
//            model.setAssettype("pic");
            model.setAssetname(title);
            model.setAssetpath(filename);
//            if (contains(context, title)) {
//                if (application == null) {
//                    application = (SavorApplication) context.getApplicationContext();
//                }
//                model.setAsseturl(application.GalleyPath + title + ".jpg");
//            } else {
            model.setAsseturl(NetWorkUtil.getLocalUrl(context) + filename);
//            }
            datas.add(model);
        }
    }

    public static void getFolderAllImg(Context context, List<PictureInfo> datas, List<String> childList,final Handler handler) {
        for (int i = 0; i < childList.size(); i++) {
            int startTitle = childList.get(i).lastIndexOf("/") + 1;
            int endTitle = childList.get(i).lastIndexOf(".");
            String title = (String) childList.get(i).subSequence(startTitle, endTitle);
            String filename = childList.get(i);

            PictureInfo model = new PictureInfo();
            model.setFunction(ConstantsWhat.FunctionsIds.PREPARE);
            model.setAction("2screen");
            model.setAssettype("pic");
            model.setAssetname(title);
            model.setAssetpath(filename);
//            if (contains(context, title)) {
//                if (application == null) {
//                    application = (SavorApplication) context.getApplicationContext();
//                }
//                model.setAsseturl(application.GalleyPath + title + ".jpg");
//            } else {
            model.setAsseturl(NetWorkUtil.getLocalUrl(context) + filename);
//            }
            datas.add(model);
        }

        handler.sendEmptyMessage(INIT_SUCCESS);
        // calback.setData();
    }

    public static ArrayList<String> getSpecialFolderImg(Context context,String folder) {
//        Cursor mCursor = mContentResolver.query(imageUri, null,
//                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
//                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_TAKEN + " DESC");
        //selection: 指定查询条件
        String selection = MediaStore.Images.Media.DATA + " like ? and ("+MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?)";
        //定义selectionArgs：
        String[] selectionArgs = {"%"+folder+"%","image/jpeg","image/png"};
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,
                selection, selectionArgs, null);
        ArrayList<String> photolist = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String imgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            photolist.add(imgPath);
        }
        cursor.close();
        return photolist;
    }
//    private static boolean contains(Context context, String title) {
////        if (app == null) {
////            app = (SavorApplication) context.getApplicationContext();
////        }
////        return new File(app.GalleyPath + title + ".jpg").exists();
//    }

//    public static void getMediaGroupPhotoList(Context context, Handler handler,
//                                              List<MediaInfo> datas, List<String> childList) {
//        for (int i = 0; i < childList.size(); i++) {
//            int startTitle = childList.get(i).lastIndexOf("/") + 1;
//            int endTitle = childList.get(i).lastIndexOf(".");
//            String title = (String) childList.get(i).subSequence(startTitle, endTitle);
//            String filename = childList.get(i);
//            MediaInfo model = new MediaInfo();
//            model.setAction("2screen");
//            model.setAssetname(title);
//            model.setAssetpath(filename);
//            LogUtils.d("是否包含压缩图：" + contains(context, title));
//            LogUtils.d(app.GalleyPath + title + ".jpg");
////            if (contains(context, title)) {
////                if (app == null) {
////                    app = (SavorApplication) context.getApplicationContext();
////                }
////                model.setAsseturl(app.GalleyPath + title + ".jpg");
////            } else {
//                model.setAsseturl(NetWorkUtil.getLocalUrl(context) + filename);
////            }
//            datas.add(model);
//            Log.i("yxc", model.toString() + "title=" + title + "-----filename=" + filename);
//        }
//        handler.sendEmptyMessage(INIT_SUCCESS);
//    }
}
