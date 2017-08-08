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
package com.common.api.vo;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.Pair;

/**
 * @author  Andrew
 * @date    2011-7-12
 *
 */
public class LogEntity implements Serializable {

    public static final int LOG_LEVEL_V = 1;
    public static final int LOG_LEVEL_D = 2;
    public static final int LOG_LEVEL_I = 3;
    public static final int LOG_LEVEL_W = 4;
    public static final int LOG_LEVEL_E = 5;
    
    public static final String LOG_LEVEL_V_STR = "V";
    public static final String LOG_LEVEL_D_STR = "D";
    public static final String LOG_LEVEL_I_STR = "I";
    public static final String LOG_LEVEL_W_STR = "W";
    public static final String LOG_LEVEL_E_STR = "E";
    
    /** Serializable ID */
    private static final long serialVersionUID = -1825000203436679450L;

    public long createTime;
    public int level;
    public String module;
    public String network;
    private String log;
    private JSONObject logObject;
    
    public LogEntity(Context context, String module, int level) {
        this.module = module;
        this.level = level;
        this.createTime = System.currentTimeMillis() / 1000;
        boolean isNetworkdOk = AppUtils.isNetworkAvailable(context);
        boolean isMobileNetwork = AppUtils.isMobileNetwork(context);
        String netType = isMobileNetwork ? "Mobile Network " : "Other Network ";
        this.network = netType + (isNetworkdOk ? "OK" : "ERROR");
        this.logObject = new JSONObject();
    }
    
    public void addLogContent(Pair<String, String> node) {
        try {
            logObject.put(node.first, node.second);
        } catch (JSONException e) {
            LogUtils.e("add log content meet json exception", e);
        }
    }
    
    public String getLogContent() {

        if (!TextUtils.isEmpty(log)) {
            return log;
        } else if (logObject != null) {
            return logObject.toString();
        }
        return "";
    }
    
    public void setLogContent(String log) {
        this.log = log;
    }
}
