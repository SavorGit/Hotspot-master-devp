package com.savor.savorphone.core;

import java.io.Serializable;


/**
 * @author 朵朵花开
 */
public class ResponseErrorMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private String message;
    private int code;
    private String json;
    private String result;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResponseErrorMessage [message=" + message + ", code=" + code
                + ", json=" + json + ", result=" + result + "]";
    }

}
