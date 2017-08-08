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

import com.common.api.utils.DesUtils;
import com.common.api.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.savor.savorphone.bean.BaseProResponse;
import com.savor.savorphone.bean.BottomHotelVodList;
import com.savor.savorphone.bean.CategoryBottomList;
import com.savor.savorphone.bean.CategoryItemVo;
import com.savor.savorphone.bean.CheckApInfo;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.CommonListResult;
import com.savor.savorphone.bean.GameResult;
import com.savor.savorphone.bean.HotelMapBean;
import com.savor.savorphone.bean.PictureSetBean;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.SmashEgg;
import com.savor.savorphone.bean.SpecialNameResult;
import com.savor.savorphone.bean.StartUpSettingsBean;
import com.savor.savorphone.bean.LastTopList;
import com.savor.savorphone.bean.LocalVideoProPesponse;
import com.savor.savorphone.bean.QuerySeekResponse;
import com.savor.savorphone.bean.RotateProResponse;
import com.savor.savorphone.bean.SeekProResponseVo;
import com.savor.savorphone.bean.TopHotelVodList;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.bean.UpgradeInfo;
import com.savor.savorphone.bean.VodListBean;
import com.savor.savorphone.bean.VodProResponse;
import com.savor.savorphone.bean.VolProResponseVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

import static com.savor.savorphone.core.AppApi.Action.GET_NEARLY_HOTEL_JSON;
import static com.savor.savorphone.core.AppApi.Action.POST_CHECKAP_CONNECTION_JSON;
import static com.savor.savorphone.core.AppApi.Action.GET_NOTIFY_PAUSE_JSON;
import static com.savor.savorphone.core.AppApi.Action.POST_NOTIFY_REPLAY_JSON;
import static com.savor.savorphone.core.AppApi.Action.POST_NOTIFY_VOL_DOWN_JSON;
import static com.savor.savorphone.core.AppApi.Action.POST_NOTIFY_VOL_OFF_JSON;
import static com.savor.savorphone.core.AppApi.Action.POST_NOTIFY_VOL_ON_JSON;
import static com.savor.savorphone.core.AppApi.Action.POST_NOTIFY_VOL_UP_JSON;
import static com.savor.savorphone.core.AppApi.Action.POST_SEEK_CHANGE_JSON;

/**
 * API 响应结果解析工厂类，所有的API响应结果解析需要在此完成。
 *
 * @author andrew
 * @date 2011-4-22
 */
public class ApiResponseFactory {
    public final static String TAG = "ApiResponseFactory";
    // 当前服务器时间
    private static String webtime = "";

    public static Object getResponse(Context context, AppApi.Action action,
                                     Response response, String key, boolean isCache, String payType) {
        //转换器

        String requestMethod = "";
        Object result = null;
        boolean isDes = false;
        Session session = Session.get(context);
        String jsonResult = null;
        try {
            jsonResult = (String) response.body().string();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (jsonResult == null) {
            return null;
        }
//		try {
//			int responseCode = Integer.parseInt(jsonResult.trim());
//			return Integer.valueOf(responseCode);
//		} catch (NumberFormatException e1) {
//			e1.printStackTrace();
//		}
        // long start = System.currentTimeMillis();
        String header = response.header("des");
        if (header != null && Boolean.valueOf(header)) {
            isDes = true;
        }
        if (isDes) {
            jsonResult = DesUtils.decrypt(jsonResult);
        }
//		Header[] headers = response.getHeaders("webtime");
//		if (headers != null && headers.length > 0) {
//			webtime = headers[0].getValue();
//		}
        LogUtils.i("action:"+action.toString()+",jsonResult:" + jsonResult);
        JSONObject rSet;
        JSONObject info = null;
        JSONArray infoArray = null;
        String infoJson = "";
        ResponseErrorMessage error;
        try {
            rSet = new JSONObject(jsonResult);
//			infoJson = rSet.toString();
            if(action == POST_CHECKAP_CONNECTION_JSON){
                Gson gson = new Gson();
                return gson.fromJson(jsonResult,CheckApInfo.class);
            }else
            if (action == POST_SEEK_CHANGE_JSON){
                if(!rSet.has("result")) {
                    error = new ResponseErrorMessage();
                    // 没有返回code只返回null
                    error.setCode(-1);
                    error.setMessage(jsonResult);
                    return error;
                }
                Gson gson = new Gson();
                return gson.fromJson(jsonResult,BaseProResponse.class);
            }else if(action == GET_NOTIFY_PAUSE_JSON
                    ||action == POST_NOTIFY_REPLAY_JSON){
                if(!rSet.has("result")) {
                    error = new ResponseErrorMessage();
                    // 没有返回code只返回null
                    error.setCode(-1);
                    error.setMessage(jsonResult);
                    return error;
                }
                Gson gson = new Gson();
                return gson.fromJson(jsonResult,BaseProResponse.class);
            }else if(action == POST_NOTIFY_VOL_UP_JSON
                    ||action == POST_NOTIFY_VOL_DOWN_JSON
                    ||action == POST_NOTIFY_VOL_ON_JSON
                    ||action == POST_NOTIFY_VOL_OFF_JSON){
                if(!rSet.has("result")) {
                    error = new ResponseErrorMessage();
                    // 没有返回code只返回null
                    error.setCode(-1);
                    error.setMessage(jsonResult);
                    return error;
                }
                Gson gson = new Gson();
                return gson.fromJson(jsonResult,VolProResponseVo.class);
            }else if(action == AppApi.Action.GET_QUERY_SEEK_JSON) {
                if(!rSet.has("result")) {
                    error = new ResponseErrorMessage();
                    // 没有返回code只返回null
                    error.setCode(-1);
                    error.setMessage(jsonResult);
                    return error;
                }
                Gson gson = new Gson();
                return gson.fromJson(jsonResult,QuerySeekResponse.class);
            }else  if(action == AppApi.Action.GET_VOD_PRO_JSON) {
                int code = rSet.getInt("result");
                if (AppApi.TVBOX_RESPONSE_STATE_SUCCESS == code) {
                    Gson gson = new Gson();
                   return gson.fromJson(jsonResult,VodProResponse.class);
                } else {
                    String msg = rSet.getString("info");
                    int relt = rSet.getInt("result");
                    Gson gson = new Gson();
                    BaseProResponse prepareResponseVo = gson.fromJson(jsonResult,BaseProResponse.class);
                    error = new ResponseErrorMessage();
                    error.setCode(relt);
                    error.setMessage(prepareResponseVo.getInfo());
                    return error;
                }
            }else  if(action == AppApi.Action.POST_LOCAL_VIDEO_PRO_JSON) {
                int code = rSet.getInt("result");
                if (AppApi.TVBOX_RESPONSE_STATE_SUCCESS == code) {
                    Gson gson = new Gson();
                    return gson.fromJson(jsonResult,LocalVideoProPesponse.class);
                } else {
                    String msg = rSet.getString("info");
                    int relt = rSet.getInt("result");
                    Gson gson = new Gson();
                    BaseProResponse prepareResponseVo = gson.fromJson(jsonResult,BaseProResponse.class);
                    error = new ResponseErrorMessage();
                    error.setCode(relt);
                    error.setMessage(prepareResponseVo.getInfo());
                    return error;
                }
            }
            else  if(action == AppApi.Action.GET_EGG_JSON) {
                int code = rSet.getInt("result");
                if (AppApi.TVBOX_RESPONSE_STATE_SUCCESS == code) {
                    Gson gson = new Gson();
                    return gson.fromJson(jsonResult,VodProResponse.class);
                } else {
                    String msg = rSet.getString("info");
                    int relt = rSet.getInt("result");
                    Gson gson = new Gson();
                    BaseProResponse prepareResponseVo = gson.fromJson(jsonResult,BaseProResponse.class);
                    error = new ResponseErrorMessage();
                    error.setCode(relt);
                    error.setMessage(prepareResponseVo.getInfo());
                    return error;
                }
            }
            else  if(action == AppApi.Action.GET_HIT_EGG_JSON) {
                int code = rSet.getInt("result");
                if (AppApi.TVBOX_RESPONSE_STATE_SUCCESS == code) {
                    Gson gson = new Gson();
                    return gson.fromJson(jsonResult,GameResult.class);
                } else {
                    String msg = rSet.getString("info");
                    int relt = rSet.getInt("result");
                    Gson gson = new Gson();
                    BaseProResponse prepareResponseVo = gson.fromJson(jsonResult,BaseProResponse.class);
                    error = new ResponseErrorMessage();
                    error.setCode(relt);
                    error.setMessage(prepareResponseVo.getInfo());
                    return error;
                }
            }
            else if(action == AppApi.Action.POST_PHOTO_ROTATE_JSON){
                int code = rSet.getInt("result");
                if (AppApi.TVBOX_RESPONSE_STATE_SUCCESS == code) {
                    Gson gson = new Gson();
                    return gson.fromJson(jsonResult,RotateProResponse.class);
                } else {
                    String msg = rSet.getString("info");
                    int relt = rSet.getInt("result");
                    error = new ResponseErrorMessage();
                    error.setCode(relt);
                    error.setMessage(msg);
                    return error;
                }
            }else if (rSet.has("status")) {// cms平台处理
                int code = rSet.getInt("status");
                if (AppApi.CMS_RESPONSE_STATE_SUCCESS == code) {
                    try {
                        if(rSet.has("content")) {
                            info = rSet.getJSONObject("content");
                            infoJson = info.toString();
                        }else if(rSet.has("result")){
                            infoJson = rSet.getString("result");
                        }else {
                            infoJson = "";
                        }

                    } catch (JSONException ex) {
                        try {
                            infoArray = rSet.getJSONArray("content");
                            infoJson = infoArray.toString();
                        } catch (JSONException e) {
                            try {
                                infoJson = rSet.getString("content");
                            } catch (Exception e2) {
//								infoJson = null;
                                infoJson = rSet.toString();
                            }

                        }
                    }

                    /**缓存返回数据包*/
//					if(isCache){
//						String serverKey = response.getFirstHeader("key").getValue();
//						String webtimeKey=response.getFirstHeader("webtime").getValue();
//						HttpCacheManager.getInstance(context).saveCacheData(key, serverKey,webtimeKey, infoJson);
//					}
                } else {
                    try {
//				    	info = rSet.getJSONObject("result");
                        if (rSet.has("result")) {
                            String msg = rSet.getString("result");
                            error = new ResponseErrorMessage();
                            error.setCode(code);
                            error.setMessage(msg);
                            error.setJson(jsonResult);
                            return error;
                        }
//				    	infoJson=info.toString();
                    } catch (JSONException ex) {
                        try {
                            String msg = rSet.getString("result");
                            error = new ResponseErrorMessage();
                            error.setCode(code);
                            error.setMessage(msg);
                            error.setJson(jsonResult);
                            return error;
                        } catch (JSONException e) {
                            try {
                                infoJson = rSet.getString("content");
                            } catch (Exception e2) {
                                LogUtils.d(e.toString());
                            }

                        }
                    }
                }
            }else if(rSet.has("code")) {// 小平台和云平台处理
                int code = rSet.getInt("code");
                if (AppApi.CLOUND_RESPONSE_STATE_SUCCESS == code) {
                    try {

                        info = rSet.getJSONObject("result");
                        infoJson = info.toString();

                    } catch (JSONException ex) {
                        try {
                            infoArray = rSet.getJSONArray("result");
                            infoJson = infoArray.toString();
                        } catch (JSONException e) {
                            try {
                                infoJson = rSet.getString("result");
                            } catch (Exception e2) {
//								infoJson = null;
                                infoJson = rSet.toString();
                            }

                        }
                    }

                    /**缓存返回数据包*/
//					if(isCache){
//						String serverKey = response.getFirstHeader("key").getValue();
//						String webtimeKey=response.getFirstHeader("webtime").getValue();
//						HttpCacheManager.getInstance(context).saveCacheData(key, serverKey,webtimeKey, infoJson);
//					}
                } else {
                    try {
//				    	info = rSet.getJSONObject("result");
                        if (rSet.has("msg")) {
                            String msg = rSet.getString("msg");
                            error = new ResponseErrorMessage();
                            error.setCode(code);
                            error.setMessage(msg);
                            error.setJson(jsonResult);
                            return error;
                        }
//				    	infoJson=info.toString();
                    } catch (JSONException ex) {
                        try {
                            String msg = rSet.getString("msg");
                            error = new ResponseErrorMessage();
                            error.setCode(code);
                            error.setMessage(msg);
                            error.setJson(jsonResult);
                            return error;
                        } catch (JSONException e) {
                            try {
                                infoJson = rSet.getString("msg");
                            } catch (Exception e2) {
                                LogUtils.d(e.toString());
                            }

                        }
                    }
                }
            }
            result = parseResponse(action, infoJson, rSet, payType);
        } catch (Exception e) {
            LogUtils.d(requestMethod + " has other unknown Exception", e);
            e.printStackTrace();
        }finally {
            response.body().close();
        }

        return result;
    }

    public static Object parseResponse(AppApi.Action action, String info, JSONObject ret, String payType) {
        Object result = null;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
//		LogUtils.i("info:-->" + info);
        if (info == null) {
            return result;
        }
        switch (action) {
            case TEST_POST_JSON:
                System.out.println(info);
                break;
            case TEST_GET_JSON:
                System.out.println(info);
                break;
            case POST_GETVODLIST_JSON://热点
                result = gson.fromJson(info, new TypeToken<VodListBean>(){}.getType());
                break;
            case POST_GETVODLIST_LAST_JSON://热点
                result = gson.fromJson(info, new TypeToken<VodListBean>(){}.getType());
                break;

            case GET_CATEGORY_JSON:
                result = gson.fromJson(info, new TypeToken<List<CategoryItemVo>>(){}.getType());
                break;
            case POST_FEEDBACK_JSON:
                result = "SUCCESS";
                break;
            case POST_TOPICLIST_JSON:
                result = gson.fromJson(info, new TypeToken<CategoryBottomList>(){}.getType());
                break;
            case POST_LAST_TOPICLIST_JSON:
                result = gson.fromJson(info, new TypeToken<LastTopList>() {
                }.getType());

                break;
            case POST_TEMP_STATISTICS_JSON:
                result = info;
                break;
            case POST_NOTIFY_TVBOX_SEEK_JSON:
                result = gson.fromJson(info, new TypeToken<SeekProResponseVo>() {
                }.getType());
                break;
            case POST_NOTIFY_REPLAY_JSON:
                result = gson.fromJson(info, new TypeToken<BaseProResponse>() {
                }.getType());
                break;
            case POST_NOTIFY_VOL_UP_JSON:
                result = gson.fromJson(info, new TypeToken<VolProResponseVo>() {
                }.getType());
                break;
            case POST_NOTIFY_VOL_DOWN_JSON:
                result = gson.fromJson(info, new TypeToken<VolProResponseVo>() {
                }.getType());
                break;
            case POST_NOTIFY_VOL_ON_JSON:
                result = gson.fromJson(info, new TypeToken<VolProResponseVo>() {
                }.getType());
                break;
            case POST_NOTIFY_VOL_OFF_JSON:
                result = gson.fromJson(info, new TypeToken<VolProResponseVo>() {
                }.getType());
                break;
            case POST_PHOTO_ROTATE_JSON:
                result = gson.fromJson(info, new TypeToken<RotateProResponse>() {
                }.getType());
                break;
            case GET_SAMLL_PLATFORMURL_JSON:
                result = gson.fromJson(info, new TypeToken<SmallPlatformByGetIp>() {
                }.getType());
                break;
            case GET_CALL_QRCODE_JSON:
                result = info;
                break;
            case POST_STATICS_FIRSTUSE_JSON:
                result = info;
                break;
            case POST_UPGRADE_JSON:
                result = gson.fromJson(info, new TypeToken<UpgradeInfo>() {
                }.getType());
                break;
            case POST_CHECKAP_CONNECTION_JSON:
                result = gson.fromJson(info, new TypeToken<CheckApInfo>() {
                }.getType());
                break;
            case GET_CALL_CODE_BY_BOXIP_JSON:
                result = info;
                break;
            case GET_VERIFY_CODE_BY_BOXIP_JSON:
                result = gson.fromJson(info, TvBoxInfo.class);
                break;
            case POST_LAST_HOTEL_VOD_JSON:
                result = gson.fromJson(info, new TypeToken<TopHotelVodList>() {
                }.getType());
                break;
            case POST_BOTTOM_HOTEL_VOD_JSON:
                result = gson.fromJson(info, new TypeToken<BottomHotelVodList>() {
                }.getType());
                break;
            case POST_NOTIFY_TVBOX_STOP_JSON:
                result = info;
                break;
            case POST_BOX_INFO_JSON:
                result = gson.fromJson(info, TvBoxInfo.class);
                break;
            case GET_CLIENTSTART_JSON:
                result = gson.fromJson(info, new TypeToken<StartUpSettingsBean>() {
                }.getType());
                break;
            case POST_WAITER_EXTENSION_JSON:
                result = info;
                break;
            case GET_NEARLY_HOTEL_JSON:
                result = gson.fromJson(info, new TypeToken<List<HotelMapBean>>() {
                }.getType());
                break;
            case GET_ALL_DISTANCE_JSON:
                result = gson.fromJson(info, new TypeToken<List<HotelMapBean>>() {
                }.getType());
                break;
            case POST_LAST_COLLECTION_JSON:
                result = gson.fromJson(info, new TypeToken<CommonListResult>() {
                }.getType());
                break;
            case POST_UP_COLLECTION_JSON:
                result = gson.fromJson(info, new TypeToken<CommonListResult>() {
                }.getType());
                break;
            case POST_SMASH_EGG_JSON:
                result = gson.fromJson(info, new TypeToken<SmashEgg>() {
                }.getType());
                break;
            case POST_WEALTH_LIFE_LIST_JSON:
                result = gson.fromJson(info, new TypeToken<CommonListResult>() {
                }.getType());
                break;
            case POST_PICTURE_SET_JSON:
                result = gson.fromJson(info,new TypeToken<List<PictureSetBean>>(){
                }.getType());
                break;
            case POST_SPECIAL_LIST_JSON:
                result = gson.fromJson(info, new TypeToken<CommonListResult>() {
                }.getType());
                break;
            case POST_CONTENT_IS_ONLINE_JSON:
                result = "success";
                break;
            case GET_ADD_MY_COLLECTION_JSON:
                result = "success";
                break;
            case GET_IS_COLLECTION_JSON:
                try {
                    JSONObject jsonObject = new JSONObject(info);
                    result = jsonObject.getString("state");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case POST_RECOMMEND_LIST_JSON:
                result = gson.fromJson(info,new TypeToken<List<CommonListItem>>(){
                }.getType());
                break;
            case POST_DEMAND_LIST_JSON:
                result = gson.fromJson(info,new TypeToken<List<CommonListItem>>(){
                }.getType());
                break;
            case POST_AWARD_RECORD_JSON:
                result = gson.fromJson(info,new TypeToken<GameResult>(){
                }.getType());
                break;
            case POST_SPECIAL_NAME_JSON:
                result = gson.fromJson(info,new TypeToken<SpecialNameResult>(){
                }.getType());
                break;
            default:
                break;
        }
        return result;
    }


}