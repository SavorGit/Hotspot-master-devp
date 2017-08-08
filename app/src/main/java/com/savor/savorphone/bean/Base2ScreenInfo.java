package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * Created by luminita on 2016/12/13.
 */

public class Base2ScreenInfo implements Serializable{

    private static final long serialVersionUID = 5521010298786852067L;
    /**
     * 投频操作类型，如暂停
     */
    private String function;
    /**
     * 互动类型，2screen为投屏
     */
    private String action;
    /**
     * 文件类型
     */
    private String assettype;
    /**
     * 投屏url
     */
    private String asseturl;
    /**
     * 文件id
     */
    private String assetid;
    /**
     * 文件名称
     */
    private String assetname;
    private int play;

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAssettype() {
        return assettype;
    }

    public void setAssettype(String assettype) {
        this.assettype = assettype;
    }

    public String getAsseturl() {
        return asseturl;
    }

    public void setAsseturl(String asseturl) {
        this.asseturl = asseturl;
    }

    public String getAssetid() {
        return assetid;
    }

    public void setAssetid(String assetid) {
        this.assetid = assetid;
    }

    public String getAssetname() {
        return assetname;
    }

    public void setAssetname(String assetname) {
        this.assetname = assetname;
    }

    public int getPlay() {
        return play;
    }

    public void setPlay(int play) {
        this.play = play;
    }

}
