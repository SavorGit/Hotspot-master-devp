package com.savor.savorphone.core;

import org.json.JSONObject;

/**
 * @author 朵朵花开
 */
public class ResponseMessage {
    private String message;
    private String code;
    private JSONObject info;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JSONObject getInfo() {
        return info;
    }

    public void setInfo(JSONObject info) {
        this.info = info;
    }

}
