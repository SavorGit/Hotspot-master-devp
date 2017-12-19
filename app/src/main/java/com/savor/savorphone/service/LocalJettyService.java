package com.savor.savorphone.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.common.api.utils.LogUtils;
import com.savor.savorphone.Servlet.StopProjectionServlet;
import com.savor.savorphone.location.LocationService;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * jar包进行了瘦身，如果有问题，对比之前项目是否缺少jar包
 */
public class LocalJettyService extends Service {
	private Server server;
//	public LocalJettyService() {
//		super("LocalJettyService");
//	}
//	public LocalJettyService(String name) {
//		super(name);
//	}

		@Override
	public void onCreate() {
		super.onCreate();
		if(server==null) {
			server = new Server(8080);
		}
		ServletContextHandler svtcontext = new ServletContextHandler(null, "/", 0);// ServletContextHandler.SESSIONS);
		svtcontext.setResourceBase("/");
		server.setHandler(svtcontext);
		svtcontext.addServlet(new ServletHolder(new DefaultServlet()), "/*");

		svtcontext.addServlet(new ServletHolder(new StopProjectionServlet()), "/stopProjection");
		LogUtils.d("LocalJettyService：onCreate()");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.d("LocalJettyService：onStartCommand()");
		flags = START_STICKY;
		startJetty();
		return super.onStartCommand(intent, flags, startId);
	}

	public void startJetty() {
		new JettyThread().start();
	}

//	@Override
//	protected void onHandleIntent(Intent intent) {
//		LogUtils.d("LocalJettyService：onStartCommand()");
//		try {
//			server.start();
//			server.join();
//		} catch (Exception e) {
//			e.printStackTrace();
//			LogUtils.e("LocalJettyService:"+e.getMessage());
//		}
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			server.stop();
			server = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtils.d("LocalJettyService:onDestroy()");
	}

	public class JettyBinder extends Binder {
		public LocalJettyService getService() {
			return LocalJettyService.this;
		}
	}

	public Server getServer() {
		return server;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return new JettyBinder();
	}

	class JettyThread extends Thread {
		@Override
		public void run() {
			try {
				server.start();
				server.join();
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.e("LocalJettyService:"+e.getMessage());
			}
		}


	}
}
