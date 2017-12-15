package com.savor.savorphone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.savor.savorphone.service.LocalJettyService;

/**
 * Created by hezd on 2017/12/13.
 */

public class JettyServiceDestroyReceiver extends BroadcastReceiver {
    public static String ACTION_JETTY = "com.savor.savorphone.jetty";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_JETTY)) {
            Intent jettyIntent = new Intent(context, LocalJettyService.class);
            context.startService(jettyIntent);
        }
    }
}
