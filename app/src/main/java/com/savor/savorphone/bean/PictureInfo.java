package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * Created by luminita on 2016/12/13.
 */

public class PictureInfo extends Base2ScreenInfo implements Serializable{

    private static final long serialVersionUID = 4149588719590875756L;
    /**
     * 图片路径
     */
    private String assetpath;
    /**图片压缩路径*/
    private String compressPath;
    private String assetcover;
    private boolean isChecked = false;
    private String imageId=null;

    @Override
    public String toString() {
        return "PictureInfo{" +
                "assetpath='" + assetpath + '\'' +
                ", compressPath='" + compressPath + '\'' +
                ", assetcover='" + assetcover + '\'' +
                ", isChecked=" + isChecked +
                ", imageId='" + imageId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PictureInfo that = (PictureInfo) o;

        if (isChecked != that.isChecked) return false;
        if (assetpath != null ? !assetpath.equals(that.assetpath) : that.assetpath != null)
            return false;
        if (compressPath != null ? !compressPath.equals(that.compressPath) : that.compressPath != null)
            return false;
        if (assetcover != null ? !assetcover.equals(that.assetcover) : that.assetcover != null)
            return false;
        return imageId != null ? imageId.equals(that.imageId) : that.imageId == null;

    }

    @Override
    public int hashCode() {
        int result = assetpath != null ? assetpath.hashCode() : 0;
        result = 31 * result + (compressPath != null ? compressPath.hashCode() : 0);
        result = 31 * result + (assetcover != null ? assetcover.hashCode() : 0);
        result = 31 * result + (isChecked ? 1 : 0);
        result = 31 * result + (imageId != null ? imageId.hashCode() : 0);
        return result;
    }

    public String getAssetpath() {
        return assetpath;
    }

    public void setAssetpath(String assetpath) {
        this.assetpath = assetpath;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public String getAssetcover() {
        return assetcover;
    }

    public void setAssetcover(String assetcover) {
        this.assetcover = assetcover;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
