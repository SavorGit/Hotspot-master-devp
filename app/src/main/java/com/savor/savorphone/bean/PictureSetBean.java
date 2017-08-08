package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/6.
 */

public class PictureSetBean implements Serializable{

    private String atext;
    private String pic_url;

    public String getAtext() {
        return atext;
    }

    public void setAtext(String atext) {
        this.atext = atext;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }
}
