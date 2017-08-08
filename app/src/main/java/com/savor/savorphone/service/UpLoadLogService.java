package com.savor.savorphone.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.common.api.utils.LogUtils;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.log.AliLogFileUtil;

import java.io.File;

/**
 * Created by hezd on 2017/4/20.
 */

public class UpLoadLogService extends Service {

    private static final int DELTE_TEMP_FILE = 0x1;
    private static final int ZIP_COMPRESS_LOG = 0x2;
    private static final int UPLOAD_ZIP_LOG = 0x3;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELTE_TEMP_FILE:
                    LogUtils.d("savor:log 删除临时文件");
                    String logTempFilePath = SavorApplication.getInstance().getLogTempFilePath();
                    AliLogFileUtil.getInstance().delteTempLogFile(logTempFilePath, new AliLogFileUtil.OnCompleteListener() {
                        @Override
                        public void onComplete() {
                            mHandler.sendEmptyMessage(ZIP_COMPRESS_LOG);
                        }
                    });
                    break;
                case ZIP_COMPRESS_LOG:
                    String dir = SavorApplication.getInstance().getLogFilePath();
                    String templogDir = SavorApplication.getInstance().getLogTempFilePath();
                    final String temLogPath = templogDir+File.separator+AliLogFileUtil.LOG_TEMP_FILE_NAME;
                    String logPath = dir+File.separator+AliLogFileUtil.LOG_FILE_NAME;
                    File logFile = new File(logPath);
                    if(logFile.exists()) {
                        LogUtils.d("savor:log 开始压缩");
                        AliLogFileUtil.getInstance().logZipToTempFile(getApplicationContext(),dir, templogDir, new AliLogFileUtil.OnCompressListener() {
                            @Override
                            public void onCompress(String tempFilePath) {
                                LogUtils.d("savor:log 压缩完成,开始上传");
                                Message message = Message.obtain();
                                message.what = UPLOAD_ZIP_LOG;
                                message.obj = tempFilePath;
                                mHandler.sendMessage(message);
                            }

                        });
                    }else {
                        LogUtils.d("savor:log 日志文件不存在");
                    }
                    break;
                case UPLOAD_ZIP_LOG:
                    final String logdir = SavorApplication.getInstance().getLogFilePath();
                   String zipLogPath = (String) msg.obj;
                    AliLogFileUtil.getInstance().uploadLogFile(getApplicationContext(), zipLogPath, new AliLogFileUtil.OnCompleteListener() {
                        @Override
                        public void onComplete() {
                            AliLogFileUtil.getInstance().deleteLogFile(logdir, new AliLogFileUtil.OnCompleteListener() {
                                @Override
                                public void onComplete() {
                                    stopSelf();
                                }
                            });
                        }
                    });
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**
         * 规则：
         * 1.删除日志临时文件如果有的话
         * 2.将日志文件压缩为zip保存为临时文件并上传
         *      如果上传成功删除日志文件
         * */
        new Thread(){
            @Override
            public void run() {
                mHandler.sendEmptyMessage(DELTE_TEMP_FILE);
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void uploadLogFile() {
        String logFileDir = SavorApplication.getInstance().getLogFilePath();
        String logFilePath = logFileDir+ File.separator+ AliLogFileUtil.LOG_FILE_NAME;
        String tempDir = SavorApplication.getInstance().getLogTempFilePath();
        String tempFilePath = tempDir+File.separator+AliLogFileUtil.LOG_TEMP_FILE_NAME;
        File tempLogFile = new File(tempFilePath);

        if(tempLogFile.exists()) {
            tempLogFile.delete();
        }


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
