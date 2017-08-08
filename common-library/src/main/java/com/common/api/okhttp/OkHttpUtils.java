package com.common.api.okhttp;

import android.annotation.SuppressLint;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import com.common.api.okhttp.callback.Callback;
import com.common.api.okhttp.request.RequestCall;
import com.common.api.okhttp.utils.Platform;
import com.common.api.utils.LogUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by bichao on 16/6/3.
 */
public class OkHttpUtils {
	public static final long DEFAULT_MILLISECONDS = 10_000L;
	private static OkHttpUtils mInstance;
	private OkHttpClient mOkHttpClient;
	private Platform mPlatform;

	public OkHttpUtils(OkHttpClient okHttpClient) {
		if (okHttpClient == null) {
			mOkHttpClient = new OkHttpClient();
		} else {
			mOkHttpClient = okHttpClient;
		}
		OkHttpClient.Builder builder = mOkHttpClient.newBuilder();
		builder.sslSocketFactory(createSSLSocketFactory());
		builder.hostnameVerifier(new TrustAllHostnameVerifier());
		mOkHttpClient = builder.build();


		mPlatform = Platform.get();
	}
	/**
	 * 默认信任所有的证书
	 * TODO 最好加上证书认证，主流App都有自己的证书
	 *
	 * @return
	 */
	@SuppressLint("TrulyRandom")
	private static SSLSocketFactory createSSLSocketFactory() {

		SSLSocketFactory sSLSocketFactory = null;

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[]{new TrustAllManager()},new SecureRandom());
			sSLSocketFactory = sc.getSocketFactory();
		} catch (Exception e) {
		}

		return sSLSocketFactory;
	}
	private static class TrustAllManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType){
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType){
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[]{};
		}
	}

	private static class TrustAllHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	public static OkHttpUtils initClient(OkHttpClient okHttpClient) {
		if (mInstance == null) {
			synchronized (OkHttpUtils.class) {
				if (mInstance == null) {
					mInstance = new OkHttpUtils(okHttpClient);
				}
			}
		}
		return mInstance;
	}

	public static OkHttpUtils getInstance() {
		return initClient(null);
	}

	public Executor getDelivery() {
		return mPlatform.defaultCallbackExecutor();
	}

	public OkHttpClient getOkHttpClient() {

		return mOkHttpClient;
	}

	
	public void execute(final RequestCall call, Callback callback) {
		if (callback == null)
			callback = Callback.CALLBACK_DEFAULT;
		final Callback finalCallback = callback;

		call.getCall().enqueue(new okhttp3.Callback() {
			@Override
			public void onFailure(Call call, final IOException e) {
				sendFailResultCallback(call, e, finalCallback);
			}

			@Override
			public void onResponse(final Call call, final Response response) {
				if (call.isCanceled()) {
					sendFailResultCallback(call, new IOException("Canceled!"),
							finalCallback);
					return;
				}

				if (!finalCallback.validateReponse(response)) {
					sendFailResultCallback(call,
							new IOException(
									"request failed , reponse's code is : "
											+ response.code()), finalCallback);
					return;
				}

				try {
					Object o = finalCallback.parseNetworkResponse(response);
					sendSuccessResultCallback(o, finalCallback);
				} catch (Exception e) {
					sendFailResultCallback(call, e, finalCallback);

				}

			}
		});
	}

	public void sendFailResultCallback(final Call call, final Exception e,
			final Callback callback) {
		if (callback == null)
			return;

		mPlatform.execute(new Runnable() {
			@Override
			public void run() {
				callback.onError(call, e);
				callback.onAfter();
			}
		});
	}

	public void sendSuccessResultCallback(final Object object,
			final Callback callback) {
		if (callback == null)
			return;
		mPlatform.execute(new Runnable() {
			@Override
			public void run() {
				callback.onResponse(object);
				callback.onAfter();
			}
		});
	}

	public void cancelTag(Object tag) {
		for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
			if (tag.equals(call.request().tag())) {
				call.cancel();
				LogUtils.d("queuedCall " + tag + " is canceled");
			}
		}
		for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
			if (tag.equals(call.request().tag())) {
				call.cancel();
				LogUtils.d("runningCall " + tag + " is canceled");
			}
		}
	}

	public static class METHOD {
		public static final String HEAD = "HEAD";
		public static final String DELETE = "DELETE";
		public static final String PUT = "PUT";
		public static final String PATCH = "PATCH";
	}
}
