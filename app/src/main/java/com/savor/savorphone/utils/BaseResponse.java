package com.savor.savorphone.utils;

import java.io.Serializable;

/**
 * Created by zhanghq on 2016/12/22.
 */

public class BaseResponse implements Serializable {
    private static final long serialVersionUID = 2461816488039795423L;
    private int result;
    private String info;

    public BaseResponse() {
    }

    public int getResult() {
        return this.result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
