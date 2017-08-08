package com.savor.savorphone;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;

import com.savor.savorphone.service.LocalJettyService;
import com.savor.savorphone.utils.ActivitiesManager;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by zhanghq on 2016/12/19.
 */

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Application mContext;
    private final Thread.UncaughtExceptionHandler mDefaultExceptionHandler;

    public ExceptionHandler(Application context) {
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        mContext = context;

    }
    /**
     * 启动jetty服务service
     */
    private void startJettyServer(Context context) {
        Intent intent = new Intent(context, LocalJettyService.class);
        context.startService(intent);
    }
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();

        // 友盟保存统计信息
        MobclickAgent.onKillProcess(mContext);


        showCrashTips();
        startJettyServer(mContext);

        // 退出并重启应用
        exitAndRestart();
    }

    private void showCrashTips() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "亲 ，程序出了点小问题即将重启哦", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();

        try {
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }


    private void exitAndRestart() {
        ActivitiesManager.getInstance().popAllActivities();
        Process.killProcess(Process.myPid());
    }



}
