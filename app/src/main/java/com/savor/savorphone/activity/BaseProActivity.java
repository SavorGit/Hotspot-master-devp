package com.savor.savorphone.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.common.api.utils.LogUtils;
import com.savor.savorphone.service.LocalJettyService;
import com.savor.savorphone.service.ProjectionService;
import com.savor.savorphone.utils.ConstantValues;

import org.eclipse.jetty.server.Server;

public class BaseProActivity extends BaseActivity {
    protected ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalJettyService.JettyBinder jettyBinder = (LocalJettyService.JettyBinder) service;
            jettyService = jettyBinder.getService();
            Server server = jettyService.getServer();
            if(server==null||!server.getState().equals("STARTED")) {
                LogUtils.d(ConstantValues.LOG_PREFIX+"jetty 未启动，开始启动...");
                jettyService.startJetty();
            }else {
                LogUtils.d(ConstantValues.LOG_PREFIX+"jetty 已启动");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private LocalJettyService jettyService;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindJettyService();
    }

    private void bindJettyService() {
        intent = new Intent(this, LocalJettyService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    private void unbindJettyService() {
        if(connection!=null) {
            unbindService(connection);
            stopService(intent);
        }
    }

    @Override
    public void getViews() {

    }

    @Override
    public void setViews() {

    }

    @Override
    public void setListeners() {

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindJettyService();
    }
}
