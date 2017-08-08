package com.savor.savorphone.core;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.common.api.http.callback.FileDownProgress;
import com.common.api.okhttp.OkHttpUtils;
import com.common.api.okhttp.callback.Callback;
import com.common.api.okhttp.coreProgress.ProgressHelper;
import com.common.api.okhttp.coreProgress.download.UIProgressResponseListener;
import com.common.api.okhttp.coreProgress.upload.UIProgressRequestListener;
import com.common.api.okhttp.request.GetRequest;
import com.common.api.okhttp.request.PostStringRequest;
import com.common.api.okhttp.request.RequestCall;
import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.google.gson.Gson;
import com.savor.savorphone.bean.ImageProResonse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppServiceOk {
    private Context mContext;
    private AppApi.Action action;
    private ApiRequestListener handler;
    private Object mParameter;
    private OkHttpUtils okHttpUtils;
    private OkHttpClient client;
    private Handler mDelivery = new Handler();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private boolean isNeedUpdateUI = true;
    /**
     * 应用Session
     */
    protected Session appSession;

    // 支付方式id,默认值为余额支付
    private String mPayType = "";
    private static long cacheSize = 1024 * 1024 * 5;
    private static String cachedirectory = Environment.getExternalStorageDirectory() + "/etago/caches";
    private static Cache cache = new Cache(new File(cachedirectory), cacheSize);

    public AppServiceOk(Context context, AppApi.Action action) {
        this.mContext = context;
        this.action = action;
        this.appSession = Session.get(context);
        this.okHttpUtils = OkHttpUtils.getInstance();
        this.client = okHttpUtils.getOkHttpClient();
    }

    public AppServiceOk(Context context, AppApi.Action action, ApiRequestListener handler, Object params) {
        this.mContext = context;
        this.action = action;
        this.handler = handler;
        this.mParameter = params;
        this.appSession = Session.get(context);
        this.okHttpUtils = OkHttpUtils.getInstance();
        this.client = okHttpUtils.getOkHttpClient();
    }

    public AppServiceOk(Context context, String url,AppApi.Action action, ApiRequestListener handler, Object params) {
        this.mContext = context;
        this.action = action;
        this.handler = handler;
        this.mParameter = params;
        this.appSession = Session.get(context);
        this.okHttpUtils = OkHttpUtils.getInstance();
        this.client = okHttpUtils.getOkHttpClient();
        if(!TextUtils.isEmpty(url)) {
            AppApi.API_URLS.put(action,url);
        }
    }

    public AppServiceOk(Context context, AppApi.Action action, ApiRequestListener handler, Object params, String payType) {
        this.mContext = context;
        this.action = action;
        this.handler = handler;
        this.mParameter = params;
        appSession = Session.get(context);
        this.mPayType = payType;
        this.okHttpUtils = OkHttpUtils.getInstance();
        this.client = okHttpUtils.getOkHttpClient();
    }

    public void cancelTag(Object tag) {
        okHttpUtils.cancelTag(tag);

    }

    public void cancelByAction() {
        okHttpUtils.cancelTag(this.action);

    }

    public synchronized boolean isNeedUpdateUI() {
        return isNeedUpdateUI;
    }

    public synchronized void setNeedUpdateUI(boolean isNeedUpdateUI) {
        this.isNeedUpdateUI = isNeedUpdateUI;
    }

    public void post() {
        post(false, false, false, false);
    }

    /**
     * @param isCache         是否需要缓存
     * @param isGzip          是否需要服务端返回的数据Gzip
     * @param isDes           是否需要返回的数据进行Des加密
     * @param isNeedUserAgent 是否需要设置User-Agent请求头
     */
    public void post(final boolean isCache, boolean isGzip, boolean isDes, boolean isNeedUserAgent) {
        final String requestUrl;
        try {
            final JSONObject obj;
            /** 序列化请求包体json */
            obj = (JSONObject) ApiRequestFactory.getRequestEntity(action, mParameter, appSession, null);
            requestUrl = AppApi.API_URLS.get(action);
            String url = ApiRequestFactory.getSignParam(mContext,requestUrl, appSession);
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("traceinfo", appSession.getDeviceInfo());
            headers.put("version","1.0");
            LogUtils.d("url-->" + url);
            LogUtils.d("traceinfo-->" + appSession.getDeviceInfo());
            if (isGzip) {
                headers.put("Accept-Encoding", "gzip");
            }
            if (isNeedUserAgent) {
                headers.put("User-Agent", "tcphone");// 添加请求头
            }
            if (isDes) {
                headers.put("des", "true");
            }

            if (!AppUtils.isNetworkAvailable(mContext)) {
                ResponseErrorMessage error = new ResponseErrorMessage();
                // 没有返回code只返回null
                error.setCode(Integer.valueOf(AppApi.ERROR_NETWORK_FAILED));
                handler.onError(action, error);
                return;
            }

            /**
             * 1.通过一个requrest构造方法将参数传入
             * 2.
             */
            Callback<Object> callback = new Callback<Object>() {

                @Override
                public Object parseNetworkResponse(Response response)
                        throws Exception {
//					try {
                    try {
                        System.err.println(response.cacheResponse().body().string());
                    } catch (Exception e) {
                    }

                    Object object = ApiResponseFactory.getResponse(mContext, action, response, "", isCache, mPayType);

                    LogUtils.d(object.toString() + "");
                    response.body().close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
                    return object;
                }

                @Override
                public void onError(Call call, Exception e) {
                    ResponseErrorMessage error = new ResponseErrorMessage();
                    // 没有返回code只返回null
                    error.setCode(Integer.valueOf(AppApi.ERROR_TIMEOUT));
                    handler.onError(action, error);
                }

                @Override
                public void onResponse(Object response) {
                    if (response instanceof ResponseErrorMessage) {
                        handler.onError(action, response);
                    } else {
                        handler.onSuccess(action, response);
                    }
                }

            };
//		    PostFormRequest formRequest = new PostFormRequest(requestUrl, action, requestParams, headers, null);
            PostStringRequest stringRequest = new PostStringRequest(url, action, null, headers, obj.toString(), MediaType.parse("application/json;charset=utf-8"));
            RequestCall requestCall = new RequestCall(stringRequest);
            requestCall.execute(callback);


        } catch (Exception e) {
            LogUtils.d(e.toString());
        }
    }

    /**
     * get请求，一般是需要baseURL的
     */

    public void get() {
        get(false, false, false, false);
    }

    /**
     * @param isCache         是否需要缓存
     * @param isGzip          是否需要服务端返回的数据Gzip
     * @param isDes           是否需要返回的数据进行Des加密
     * @param isNeedUserAgent 是否需要设置User-Agent请求头
     */
    public void get(final boolean isCache, boolean isGzip, boolean isDes, boolean isNeedUserAgent) {
        String requestUrl = AppApi.API_URLS.get(action);
        Map<String, String> requestParams = null;
        if (mParameter instanceof HashMap) {
            requestParams = (HashMap<String, String>) mParameter;
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("traceinfo", appSession.getDeviceInfo());
        headers.put("version","1.0");
        LogUtils.d("traceinfo-->" + appSession.getDeviceInfo());
        if (!AppUtils.isNetworkAvailable(mContext)) {
            ResponseErrorMessage error = new ResponseErrorMessage();
            // 没有返回code只返回null
            error.setCode(Integer.valueOf(AppApi.ERROR_NETWORK_FAILED));
            handler.onError(action, error);
            return;
        }
        Callback<Object> callback = new Callback<Object>() {

            @Override
            public Object parseNetworkResponse(Response response)
                    throws Exception {
//					try {
//						System.err.println(response2.body().string());
                Object object = ApiResponseFactory.getResponse(mContext, action, response, "", isCache, mPayType);

                LogUtils.d(object.toString() + "");
                response.body().close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
                return object;
            }

            @Override
            public void onError(Call call, Exception e) {
                // TODO Auto-generated method stub
                ResponseErrorMessage error = new ResponseErrorMessage();
                // 没有返回code只返回null
                error.setCode(Integer.valueOf(AppApi.ERROR_TIMEOUT));
                handler.onError(action, error);
            }

            @Override
            public void onResponse(Object response) {
                if (response instanceof ResponseErrorMessage) {
                    handler.onError(action, response);
                } else {
                    handler.onSuccess(action, response);
                }


            }

            @Override
            public void inProgress(float progress) {
                // TODO Auto-generated method stub
                super.inProgress(progress);
            }

        };

        requestUrl = ApiRequestFactory.getUrlRequest(mContext,requestUrl, action, mParameter, appSession);
        LogUtils.d("requestUrl-->" + requestUrl);
        GetRequest getRequest = new GetRequest(requestUrl, action, requestParams, headers);
        RequestCall requestCall = new RequestCall(getRequest);
        requestCall.execute(callback);
    }
    public void get(String url) {
        String requestUrl = url;

        Callback<Object> callback = new Callback<Object>() {

            @Override
            public Object parseNetworkResponse(Response response)
                    throws Exception {
//					try {
//						System.err.println(response2.body().string());
                Object object = ApiResponseFactory.getResponse(mContext, action, response, "", false, mPayType);

                LogUtils.d(object.toString() + "");
                response.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
                return object;
            }

            @Override
            public void onError(Call call, Exception e) {
                // TODO Auto-generated method stub
                handler.onError(action, AppApi.ERROR_TIMEOUT);
            }

            @Override
            public void onResponse(Object response) {
                if (response instanceof ResponseErrorMessage) {
                    handler.onError(action, response);
                } else {
                    handler.onSuccess(action, response);
                }


            }

            @Override
            public void inProgress(float progress) {
                // TODO Auto-generated method stub
                super.inProgress(progress);
            }

        };

        requestUrl = ApiRequestFactory.getUrlRequest(mContext,requestUrl, action, mParameter, appSession);
        LogUtils.d("requestUrl-->" + requestUrl);
        GetRequest getRequest = new GetRequest(requestUrl, action, null, null);
        RequestCall requestCall = new RequestCall(getRequest);
        requestCall.execute(callback);
    }
    /**
     * 下载文件
     *
     * @param url
     * @param targetFile
     */
    public void downLoad(String url, final String targetFile) {
        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, String> headers = new HashMap<String, String>();
        int read = 0;
        //这个是ui线程回调，可直接操作UI
        final UIProgressResponseListener uiProgressResponseListener = new UIProgressResponseListener() {
            @Override
            public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                Log.e("TAG123", "bytesRead:" + bytesRead);
                Log.e("TAG123", "contentLength:" + contentLength);
                Log.e("TAG123", "done:" + done);
                System.out.format("%d%% done\n", (100 * bytesRead) / contentLength);
                if (contentLength != -1) {
                    //长度未知的情况下回返回-1
                    Log.e("TAG123", (100 * bytesRead) / contentLength + "% done");
                }
                Log.e("TAG123", "================================");
                //ui层回调

                FileDownProgress fileDownProgress = new FileDownProgress();
                fileDownProgress.setTotal(contentLength);
                fileDownProgress.setNow(bytesRead);
                fileDownProgress.setLoading(done);
                handler.onSuccess(action, fileDownProgress);
//				downloadProgeress.setProgress((int) ((100 * bytesRead) / contentLength));
                //Toast.makeText(getApplicationContext(), bytesRead + " " + contentLength + " " + done, Toast.LENGTH_LONG).show();
            }
        };


        okhttp3.Callback callback2 = new okhttp3.Callback() {
            @Override
            public void onFailure(Call var1, IOException e) {
                LogUtils.e("下载失败", e);
            }

            @Override
            public void onResponse(Call var1, Response response) throws IOException {
                //将返回结果转化为流，并写入文件
                LogUtils.d("TAG--onResponse ================================");
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    int len;
                    byte[] buf = new byte[2048];
                    inputStream = response.body().byteStream();
                    //可以在这里自定义路径
                    final File file1 = new File(targetFile);
                    fileOutputStream = new FileOutputStream(file1);

                    while ((len = inputStream.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, len);
                    }

					mDelivery.post(new Runnable(){
						@Override
						public void run()
						{
                            LogUtils.d("下载进度完成关闭进度条");
                            handler.onSuccess(action, file1);
						}
					});
                } catch (Exception e) {
                    LogUtils.e("下载文件写入异常", e);
                } finally {
                    if(fileOutputStream!=null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();

                    }
                    if(inputStream!=null)
                        inputStream.close();
                    response.close();
                }

            }
        };
        //封装请求
//        try {
            Request request = new Request.Builder()
                    //下载地址
                    .url(url)
                    .build();
            ProgressHelper.addProgressResponseListener(client, uiProgressResponseListener).newCall(request).enqueue(callback2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 上传文件
     *
     * @param filePath 文件本地路径
     * @param isGzip
     */
    public void uploadFile(final String filePath,boolean isGzip, boolean isNeedUserAgent) {
        final File srcFile = new File(filePath);
        if (!srcFile.exists()) {
            return;
        }
        //这个是ui线程回调，可直接操作UI
        final UIProgressRequestListener uiProgressRequestListener = new UIProgressRequestListener() {
            @Override
            public void onUIRequestProgress(long bytesWrite, long contentLength, boolean done) {
//                Log.e("TAG", "bytesWrite:" + bytesWrite);
//                Log.e("TAG", "contentLength" + contentLength);
//                Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
//                Log.e("TAG", "done:" + done);
//                Log.e("TAG", "======================");
//                //ui层回调
//                FileDownProgress fileDownProgress = new FileDownProgress();
//                fileDownProgress.setTotal(contentLength);
//                fileDownProgress.setNow(bytesWrite);
//                fileDownProgress.setLoading(done);
                if (done){
                    LogUtils.d("action111:"+ filePath +"savor:图片上传完成");
//                    handler.onSuccess(action, fileDownProgress);
                }
//				uploadProgress.setProgress((int) ((100 * bytesWrite) / contentLength));
            }
        };
        final String requestUrl = AppApi.API_URLS.get(action);
        try {

//            uri = ApiRequestFactory.getUrlRequest(uri, action, mParameter, appSession);
//            requestUrl = uri;

            Map<String, String> requestParams = null;
            if (mParameter instanceof HashMap) {
                requestParams = (HashMap<String, String>) mParameter;
            }
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("traceinfo", appSession.getDeviceInfo());
            LogUtils.d("traceinfo-->" + appSession.getDeviceInfo());
            if (isGzip) {
                headers.put("Accept-Encoding", "gzip");
            }
            if (isNeedUserAgent) {
                headers.put("User-Agent", "tcphone");// 添加请求头
            }

            if (!AppUtils.isNetworkAvailable(mContext)) {
                handler.onError(action, AppApi.ERROR_NETWORK_FAILED);
                return;
            }

            okhttp3.Callback callback = new okhttp3.Callback() {

                @Override
                public void onFailure(Call var1, IOException e) {
                    LogUtils.e("下载失败", e);
//                    ResponseErrorMessage responseErrorMessage = new ResponseErrorMessage();
//                    responseErrorMessage.setCode(-1);
//                    responseErrorMessage.setMessage("投屏失败");
//                    handler.onError(action,responseErrorMessage);
                }

                @Override
                public void onResponse(Call var1, Response response) throws IOException {
                    String result = response.body().string();
                    LogUtils.d("savor:pro "+action+",jsonResult:"+result);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        if(!jsonObject.has("result")) {
                            ResponseErrorMessage responseErrorMessage = new ResponseErrorMessage();
                            responseErrorMessage.setCode(-1);
                            responseErrorMessage.setMessage(result);
                            handler.onError(action,responseErrorMessage);
                        }else {
                            int resultcode = jsonObject.getInt("result");
                            LogUtils.d("savor:pro resultcode="+result);
                            Gson gson = new Gson();
                            ImageProResonse prepareResponseVo =  gson.fromJson(result,ImageProResonse.class);
                            if(AppApi.TVBOX_RESPONSE_STATE_SUCCESS == resultcode) {
                                handler.onSuccess(action,prepareResponseVo);
                            }else if(AppApi.TVBOX_RESPONSE_STATE_ERROR == resultcode||
                                    AppApi.TVBOX_RESPONSE_STATE_FORCE == resultcode){
                                int relt = jsonObject.getInt("result");
                                ResponseErrorMessage error = new ResponseErrorMessage();
                                error.setCode(relt);
                                error.setMessage(prepareResponseVo.getInfo());
                                handler.onError(action,error);
                            }else {
                                ResponseErrorMessage responseErrorMessage = new ResponseErrorMessage();
                                responseErrorMessage.setCode(-1);
                                responseErrorMessage.setMessage("投屏失败");
                                handler.onError(action,responseErrorMessage);
                            }
                        }
                    } catch (JSONException e) {
                        LogUtils.d("action111:报错啦"+action+",jsonResult:"+result);
                        e.printStackTrace();
                    }finally {
                        response.body().close();
                    }

//                    LogUtils.d("jsonresult:图片上传结果 "+result);
                }
            };
            //构造上传请求，类似web表单

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                    .addFormDataPart("action", requestParams.get("action"))
//                    .addFormDataPart("assettype", requestParams.get("assettype"))
////                    .addFormDataPart("asseturl", requestParams.get("asseturl"))
//                    .addFormDataPart("assetid", requestParams.get("assetid"))
//                    .addFormDataPart("assetname", requestParams.get("assetname"))
////                    .addFormDataPart("play", requestParams.get("play"))
//                    .addFormDataPart("vodType", requestParams.get("vodType"))
//                    .addFormDataPart("function", requestParams.get("function"))
//                    .addFormDataPart("deviceId", requestParams.get("deviceId"))
//                    .addFormDataPart("deviceName", requestParams.get("deviceName"))
//                    .addFormDataPart("isThumbnail", requestParams.get("isThumbnail"))
//                    .addFormDataPart("imageId", requestParams.get("imageId"))
                    .addFormDataPart("fileUpload", srcFile.getName(), RequestBody.create(null, srcFile))
//                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""), RequestBody.create(MediaType.parse("application/octet-stream"), srcFile))
                    .build();
            LogUtils.d("json 请求参数："+new Gson().toJson(mParameter));
            //进行包装，使其支持进度回调
            final Request request = new Request.Builder()
                    .header("version","1.0")
                    .tag(action).url(requestUrl)
                    .post(ProgressHelper
                    .addProgressRequestListener(requestBody, uiProgressRequestListener)).build();
            //开始请求
            client.newCall(request).enqueue(callback);
//			PostFileRequest postFileRequest = new PostFileRequest(requestUrl,action,requestParams,headers,srcFile,null);
//
//			RequestCall requestCall = new RequestCall(postFileRequest);
//			requestCall.execute(callback);

        } catch (Exception e) {
            LogUtils.d(e.toString());
        }
    }

}
