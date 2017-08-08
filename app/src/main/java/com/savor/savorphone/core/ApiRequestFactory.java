package com.savor.savorphone.core;
/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.common.api.http.RequestParams;
import com.common.api.utils.DigestUtils;
import com.common.api.utils.LogUtils;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.STIDUtil;
import com.umeng.message.PushAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 这个类是获取API请求内容的工厂方法
 */
public class ApiRequestFactory {
    /**
     * 获取API HTTP 请求内容
     *
     * @param action 请求的API Code
     * @param params 请求参数
     * @throws UnsupportedEncodingException 假如不支持UTF8编码方式会抛出此异常
     */
    public static Object getRequestEntity(AppApi.Action action, Object params,
                                          Session appSession, RequestParams header) throws UnsupportedEncodingException, JSONException {
        String nameSpace = action.name();
        if (nameSpace.contains("XML")) {
            /**暂不实现*/
            return null;

        } else if (nameSpace.contains("JSON")) {
            return getJsonRequest(params, appSession);
        } else {
            // 不需要请求内容
            return null;
        }
    }

    private static JSONObject getJsonRequest(Object params, Session appSession) throws JSONException {
        if (params == null) {
            return new JSONObject();
        }

        HashMap<String, Object> requestParams;
        if (params instanceof HashMap) {
            requestParams = (HashMap<String, Object>) params;
        } else {
            return new JSONObject();
        }

        // add parameter node
        final Iterator<String> keySet = requestParams.keySet().iterator();
        JSONObject jsonParams = new JSONObject();

        try {
            while (keySet.hasNext()) {
                final String key = keySet.next();
                Object val = requestParams.get(key);
                if (val == null) {
                    val = "";
                }
                if (val instanceof String || val instanceof Number) {
                    jsonParams.accumulate(key, val);
                } else if (val instanceof List<?>) {
                    jsonParams.accumulate(key, getJSONArray((List<?>) val).toString());
                } else {
                    jsonParams.accumulate(key, getJSONObject(val).toString());
                }
            }
            LogUtils.i("请求数据包参数:" + jsonParams.toString());
//            return new StringEntity(DesUtils.encrypt(jsonParams.toString()), HTTP.UTF_8);
            return jsonParams;

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private static JSONArray getJSONArray(List<?> list) {
        JSONArray jArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            if (obj instanceof String || obj instanceof Number) {
                jArray.put(list.get(i));
            } else {
                jArray.put(getJSONObject(obj));
            }
        }
        return jArray;
    }

    @NonNull
    private static JSONObject getJSONObject(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        JSONObject jObject = new JSONObject();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(object);
                if (fieldValue instanceof String || fieldValue instanceof Number) {
                    jObject.put(field.getName(), fieldValue);
                } else {
                    jObject.put(field.getName(), getJSONObject(fieldValue));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jObject;
    }

    public static String getUrlRequest(Context context,String requestUrl, AppApi.Action action, Object parameter, Session appSession) {
        requestUrl = getSignParam(context,requestUrl, appSession);
        if (parameter instanceof Map && ((Map) parameter).size() > 0) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) parameter;
            Set<Map.Entry<String, Object>> params = map.entrySet();
            for (Map.Entry<String, Object> param : params) {
                String key = param.getKey();
                Object val = param.getValue();
                requestUrl = requestUrl + "&" + key + "=" + val;
            }
        }

        if (requestUrl.endsWith("/")) {
            requestUrl = requestUrl.substring(0, requestUrl.length() - 1);
        }

        return requestUrl;
//        if (action==null) {
//            return "";
//        }
//        String requestUrl = null;
//        if (!TextUtils.isEmpty(url)) {
//        	requestUrl = url;
//		}else {
//			requestUrl = AppApi.API_URLS.get(action)+"/";
//		}
//        try {
//			return getSign(requestUrl, appSession);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
    }

    /**
     * 获取sign值
     *
     * @param url
     * @return
     */
    public static String getSignParam(Context context,String url, Session appSession) {
        long timestamp = System.currentTimeMillis() / 1000;
        String sign = DigestUtils.md5Hex(timestamp + ConstantValues.APP_KEY);//ConstantValues.APP_KEY);
//        String token = appSession.getToken();
//        token = TextUtils.isEmpty(token) ? "" : token;
        url = url + (url.contains("?")?"&":"?")+ "time=" + timestamp + "&sign=" + sign+"&deviceId="+ STIDUtil.getDeviceId(context)+"&deviceToken="+ PushAgent.getInstance(context).getRegistrationId();

        return url;
    }
}
