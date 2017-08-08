package com.savor.savorphone.utils;

import android.app.Activity;
import android.app.admin.SystemUpdatePolicy;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.common.api.utils.DensityUtil;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.core.Session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CompressImage {

    private static final int PIC_SIZE_LIMIT = 200;

    public static Bitmap getBitmap(Activity context, String filePath) throws Exception {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        Bitmap bm ;
        BitmapFactory.decodeFile(filePath, opt);

        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        // 获取屏的宽度和高度
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        } else {
            if (picHeight > screenHeight)

                opt.inSampleSize = picHeight / screenHeight;
        }
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        bm = BitmapFactory.decodeFile(filePath, opt);

        return compressImage(bm, PIC_SIZE_LIMIT,opt);

    }

    public static Bitmap getBitmap(Activity context, String filePath,boolean isSmall) throws Exception {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        Bitmap bm ;
        BitmapFactory.decodeFile(filePath, opt);

        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        // 获取屏的宽度和高度
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth*10;
        } else {
            if (picHeight > screenHeight)

                opt.inSampleSize = picHeight / screenHeight*10;
        }
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        bm = BitmapFactory.decodeFile(filePath, opt);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        int options = 100;
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
////        Log.i("图片分辨率压缩后：" + baos.toByteArray().length / 1024 + "KB");
//        while (baos.toByteArray().length>20*1024){
//
//        }
        return bm;

    }

    public static synchronized String compressAndSaveBitmap(Context context, String filePath, String filname, boolean isSmall) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opt);

        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        // 获取屏的宽度和高度
        int screenWidth = DensityUtil.getScreenWidth(context);
        int screenHeight = DensityUtil.getScreenHeight(context);

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        } else {
            if (picHeight > screenHeight)
                opt.inSampleSize = picHeight / screenHeight;
        }
        if(isSmall)
            opt.inSampleSize*=9;
        else {
            opt.inSampleSize*= 2;
        }
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;

      Bitmap  bm = BitmapFactory.decodeFile(filePath, opt);
        try {
            ExifInterface ei = new ExifInterface(filePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileOutputStream fileOutputStream = null;
        Session session = Session.get(context);
        String comppressPath = session.getCompressPath();
        File file = new File(comppressPath);
        if(!file.exists())
            file.mkdirs();
        String copyPath = comppressPath + filname + ".png";
        try {
            fileOutputStream = new FileOutputStream(copyPath);
            bm.compress(Bitmap.CompressFormat.JPEG,80,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            copyPath = filePath;
        }finally {
            if(fileOutputStream!=null) {
                try {
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream!=null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bm!=null&&!bm.isRecycled()) {
                bm.recycle();
            }
            System.gc();
        }

//        return compressImage(bm, PIC_SIZE_LIMIT,opt);
        return copyPath;
    }

    public static synchronized String compressAndSaveBitmapForPdf(Context context, String filePath, String filname, boolean isSmall) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opt);

        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        // 获取屏的宽度和高度
        int screenWidth = DensityUtil.getScreenWidth(context);
        int screenHeight = DensityUtil.getScreenHeight(context);

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        } else {
            if (picHeight > screenHeight)
                opt.inSampleSize = picHeight / screenHeight;
        }
        if(isSmall)
            opt.inSampleSize*=9;
        else {
            opt.inSampleSize = 1;
        }
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap  bm = BitmapFactory.decodeFile(filePath, opt);
        try {
            ExifInterface ei = new ExifInterface(filePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileOutputStream fileOutputStream = null;
        Session session = Session.get(context);
        String comppressPath = session.getCompressPath();
        File file = new File(comppressPath);
        if(!file.exists())
            file.mkdirs();
        String copyPath = comppressPath + filname + ".png";
        try {
            fileOutputStream = new FileOutputStream(copyPath);
            bm.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            copyPath = filePath;
        }finally {
            if(fileOutputStream!=null) {
                try {
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream!=null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bm!=null&&!bm.isRecycled()) {
                bm.recycle();
            }
            System.gc();
        }

//        return compressImage(bm, PIC_SIZE_LIMIT,opt);
        return copyPath;
    }

    public static Bitmap getScaleBitmap(Activity context, String filePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        Bitmap bm;
        BitmapFactory.decodeFile(filePath, opt);

        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        // 获取屏的宽度和高度
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        } else {
            if (picHeight > screenHeight)

                opt.inSampleSize = picHeight / screenHeight;
        }
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(filePath, opt);

        return bm;

    }

    /**
     * 压缩图片
     *
     * @param image
     * @param size
     * @param opt
     * @return
     */
    private static Bitmap compressImage(Bitmap image, int size, BitmapFactory.Options opt) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            if (baos.toByteArray().length / 1024 <= size)
                return image;

            while (baos.toByteArray().length / 1024 > size && options > 0) { // 循环判断如果压缩后图片是否大于50kb,大于继续压缩
                baos.reset();// 重置baos即清空baos
                image.compress(getImgFormat(opt), options, baos);// 这里压缩比options=50，把压缩后的数据存放到baos中
                if (options > 10)
                    options -= 10;// 每次都减少10
                else
                    options -= 1;
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(
                    baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
            if(image!=null&&!image.isRecycled()) {
                image.recycle();
                image = null;
            }

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap.CompressFormat getImgFormat(BitmapFactory.Options options) {
        String type = options.outMimeType;
        if (type != null && type.indexOf("png") > -1)
            return Bitmap.CompressFormat.PNG;
        else
            return Bitmap.CompressFormat.JPEG;
    }

    public static String compressAndSaveBitmap(Context context,Bitmap bitmap) {
        String copyPath = null;
        Matrix matrix = new Matrix();
        Bitmap compressBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        FileOutputStream fileOutputStream = null;
        String compressDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                +File.separator+ "savor" + File.separator + "pic";
        String compressPath = compressDir+File.separator+"pdfScreenshot.jpg";
        File dir = new File(compressDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File compressFile = new File(compressPath);
        if(compressFile.exists()) {
            compressFile.delete();
        }
        try {
            fileOutputStream = new FileOutputStream(compressPath);
            compressBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            copyPath = compressPath;
            Log.d("pdf","pdf compress success!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (compressBitmap != null && !compressBitmap.isRecycled()) {
                compressBitmap.recycle();
            }
            System.gc();
        }

        return copyPath;
    }
}
