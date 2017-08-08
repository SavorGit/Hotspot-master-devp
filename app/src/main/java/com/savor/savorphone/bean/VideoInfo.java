package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * Created by luminita on 2016/12/13.
 */

public class VideoInfo extends BaseProReqeust implements Serializable {

    /**
     * 视频路径
     */
    private String assetpath;
    /**
     * 视频封面图
     */
    private String assetcover;
    /**
     * 视频长度
     */
    private long assetlength;

    /**本地视频视频url地址*/
    private String asseturl;

    @Override
    public String toString() {
        return "VideoInfo{" +
                "assetpath='" + assetpath + '\'' +
                ", assetcover='" + assetcover + '\'' +
                ", assetlength=" + assetlength +
                ", asseturl='" + asseturl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VideoInfo videoInfo = (VideoInfo) o;

        if (assetlength != videoInfo.assetlength) return false;
        if (assetpath != null ? !assetpath.equals(videoInfo.assetpath) : videoInfo.assetpath != null)
            return false;
        if (assetcover != null ? !assetcover.equals(videoInfo.assetcover) : videoInfo.assetcover != null)
            return false;
        return asseturl != null ? asseturl.equals(videoInfo.asseturl) : videoInfo.asseturl == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (assetpath != null ? assetpath.hashCode() : 0);
        result = 31 * result + (assetcover != null ? assetcover.hashCode() : 0);
        result = 31 * result + (int) (assetlength ^ (assetlength >>> 32));
        result = 31 * result + (asseturl != null ? asseturl.hashCode() : 0);
        return result;
    }

    public String getAssetpath() {
        return assetpath;
    }

    public void setAssetpath(String assetpath) {
        this.assetpath = assetpath;
    }

    public String getAssetcover() {
        return assetcover;
    }

    public void setAssetcover(String assetcover) {
        this.assetcover = assetcover;
    }

    public long getAssetlength() {
        return assetlength;
    }

    public void setAssetlength(long assetlength) {
        this.assetlength = assetlength;
    }

    public String getAsseturl() {
        return asseturl;
    }

    public void setAsseturl(String asseturl) {
        this.asseturl = asseturl;
    }
}
