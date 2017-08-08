package com.savor.savorphone.log;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.common.api.utils.LogUtils;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.bean.AliLogBean;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.OSSClientUtil;
import com.savor.savorphone.utils.STIDUtil;
import com.savor.savorphone.utils.ZipUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ali日志文件工具类
 * 文件上传逻辑：启动后判断是否有日志文件log，如果有判断是否有临时文件夹如果没有生成，
 * 如果有将文件夹里文件清空，将日志log文件压缩zip
 * 拷贝到临时文件夹temp（在当前文件夹下）
 * 上传到阿里云上传成功后删除
 * Created by hezd on 2017/4/19.
 */

public class AliLogFileUtil {
    public static final String LOG_FILE_NAME = "log.data";
    public static final String LOG_TEMP_FILE_NAME = "tempLog.zip";
    public static volatile AliLogFileUtil sInstance = null;
    private ExecutorService threadPool = Executors.newFixedThreadPool(5);
    private static final int BUFFER = 2048;

    private AliLogFileUtil() {
    }

    public static AliLogFileUtil getInstance() {
        if (sInstance == null) {
            synchronized (AliLogFileUtil.class) {
                if (sInstance == null) {
                    sInstance = new AliLogFileUtil();
                }
            }
        }
        return sInstance;
    }

    /**
     * 写log到日志文件
     *
     * @param logBean
     * @param filepath
     */
    public void writeLogToFile(final Context context, final AliLogBean logBean, final String filepath) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                File fileDir = new File(filepath);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }

                String path = filepath + File.separator + LOG_FILE_NAME;
                File logFile = new File(path);
                if (!logFile.exists()) {
                    try {
                        logFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Session session = Session.get(context);

                StringBuilder sb = new StringBuilder();
                sb.append(formatValue(logBean.getUUID())+",");
                sb.append(formatValue(logBean.getHotel_id()) + ",");
                sb.append(formatValue(logBean.getRoom_id()) + ",");
                sb.append(formatValue(logBean.getTime()) + ",");
                sb.append(formatValue(logBean.getAction()) + ",");
                sb.append(formatValue(logBean.getType()) + ",");
                sb.append(formatValue(logBean.getContent_id()) + ",");
                sb.append(formatValue(logBean.getCategory_id()) + ",");
                sb.append(formatValue(logBean.getMobile_id()) + ",");
                sb.append(formatValue(logBean.getMedia_id()) + ",");
                sb.append(formatValue(logBean.getOs_type()) + ",");
                sb.append(session.getLatestLng()+",");
                sb.append(session.getLatestLat()+",");
                sb.append(""+",");
                sb.append(formatValue(Session.get(SavorApplication.getInstance().getApplicationContext()).getArea_id()) + ",");
                sb.append(formatValue(logBean.getCustom_volume()));
                String logLine = sb.toString();

                try {
                    FileWriter fileWriter = new FileWriter(path, true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(logLine);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 写log到日志文件
     *
     * @param logBean
     * @param filepath
     */
    public void writeLogToFile(final Context context, final AliLogBean logBean, final String filepath, final OnCompleteListener listener) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                File fileDir = new File(filepath);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }

                String path = filepath + File.separator + LOG_FILE_NAME;
                File logFile = new File(path);
                if (!logFile.exists()) {
                    try {
                        logFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Session session = Session.get(context);

                StringBuilder sb = new StringBuilder();
                sb.append(formatValue(logBean.getUUID())+",");
                sb.append(formatValue(logBean.getHotel_id()) + ",");
                sb.append(formatValue(logBean.getRoom_id()) + ",");
                sb.append(formatValue(logBean.getTime()) + ",");
                sb.append(formatValue(logBean.getAction()) + ",");
                sb.append(formatValue(logBean.getType()) + ",");
                sb.append(formatValue(logBean.getContent_id()) + ",");
                sb.append(formatValue(logBean.getCategory_id()) + ",");
                sb.append(formatValue(logBean.getMobile_id()) + ",");
                sb.append(formatValue(logBean.getMedia_id()) + ",");
                sb.append(formatValue(logBean.getOs_type()) + ",");
                sb.append(session.getLatestLng()+",");
                sb.append(session.getLatestLat()+",");
                sb.append(""+",");
                sb.append(formatValue(Session.get(SavorApplication.getInstance().getApplicationContext()).getArea_id()) + ",");
                sb.append(formatValue(logBean.getCustom_volume()));
                String logLine = sb.toString();

                try {
                    FileWriter fileWriter = new FileWriter(path, true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(logLine);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(listener!=null) {
                    listener.onComplete();
                }
            }
        });

    }

    public void deleteLogFile(final String fileDir, final OnCompleteListener listener) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String filePath = fileDir + File.separator + LOG_FILE_NAME;
                File logFile = new File(filePath);
                if (logFile.exists())
                    logFile.delete();
                if(listener!=null)
                    listener.onComplete();
                LogUtils.d("savor:log 删除日志原文件");
            }
        });

    }

    public void delteTempLogFile(final String tempLogDir, final OnCompleteListener listener) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                File tempDir = new File(tempLogDir);
                if(tempDir.exists()&&tempDir.isDirectory()) {
                    File[] files = tempDir.listFiles();
                    for(File file : files) {
                        file.delete();
                    }
                }
                if(listener!=null) {
                    listener.onComplete();
                }
            }
        });

    }

    public void logZipToTempFile(final Context context, final String logDir, final String tempLogDir, final OnCompressListener listener) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String logPath = logDir + File.separator + LOG_FILE_NAME;
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String fileName = STIDUtil.getDeviceId(context)+"_"+simpleDateFormat.format(date)+".zip";
                String tempLogPath = tempLogDir + File.separator + fileName;
                File tempDir = new File(tempLogDir);
                if(!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                File logFile = new File(logPath);
                if (logFile.exists()) {
                    String path = ZipUtil.zip(logPath, tempLogPath, null);
                    if(!TextUtils.isEmpty(path)) {
                        if(listener!=null) {
                            listener.onCompress(tempLogPath);
                        }
                    }else {
                        LogUtils.d("savor:log 压缩失败");
                    }
                }
            }
        });

    }

    public void uploadLogFile(final Context context, final String ziplogfilePath, final OnCompleteListener listener) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                OSSClient ossClient = OSSClientUtil.getInstance().getOSSClient(context);
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                String dateStr = simpleDateFormat.format(date);
                File file = new File(ziplogfilePath);
                Session session = Session.get(context);
                String area_id = session.getArea_id();
                int areaId = 1;
                if(!TextUtils.isEmpty(area_id)) {
                    try {
                        areaId = Integer.valueOf(area_id);
                    }catch (Exception e){}
                }
                // 构造上传请求
                PutObjectRequest put = new PutObjectRequest(ConstantValues.BUCKET_NAME, "log/mobile/android/"+areaId+"/"+dateStr+"/"+file.getName(), ziplogfilePath);
                // 文件元信息的设置是可选的
                // ObjectMetadata metadata = new ObjectMetadata();
                // metadata.setContentType("application/octet-stream"); // 设置content-type
                // metadata.setContentMD5(BinaryUtil.calculateBase64Md5(uploadFilePath)); // 校验MD5
                // put.setMetadata(metadata);
                try {
                    PutObjectResult putResult = ossClient.putObject(put);
                    if(listener!=null) {
                        listener.onComplete();
                    }
                    LogUtils.d("savor:log 上传完成 requestid = "+putResult.getRequestId());
//                    Log.d("PutObject", "UploadSuccess");
//                    Log.d("ETag", putResult.getETag());
//                    Log.d("RequestId", putResult.getRequestId());
                } catch (ClientException e) {
                    // 本地异常如网络异常等
                    e.printStackTrace();
                    LogUtils.d("savor:log 文件上传异常");
                } catch (ServiceException e) {
                    // 服务异常
                    LogUtils.d("savor:log 阿里文件上传服务异常 -"+e.getRawMessage()+"--errorCode:"+e.getErrorCode());
//                    Log.e("RequestId", e.getRequestId());
//                    Log.e("ErrorCode", e.getErrorCode());
//                    Log.e("HostId", e.getHostId());
//                    Log.e("RawMessage", e.getRawMessage());
                }
            }
        });

    }

    private String formatValue(String value) {
        String result = "";
        if (!TextUtils.isEmpty(value)) {
            result = value;
        }
        return result;
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    public interface OnCompressListener {
        void onCompress(String tempFilePath);
    }
}
