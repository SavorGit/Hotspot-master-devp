package com.savor.savorphone.bean;

import java.io.Serializable;

public class ModelPic extends BaseProReqeust implements Serializable {
    private String assetpath;
    private String assetcover;
    private String asseturl;
    /**是否是小图*/
    private int small = 1;
    private String compressPath;
    private boolean isChecked = false;

    private int rotate;
    /**合成图相对于原图旋转角度*/
    private int comRotateValue;
    private String primaryText;
    private String desText;
    private String dateText;
    private String compoundPath;
    private String mobileUser;
    @Override
    public String toString() {
        return "ModelPic{" +
                "assetpath='" + assetpath + '\'' +
                ", assetcover='" + assetcover + '\'' +
                ", asseturl='" + asseturl + '\'' +
                ", small=" + small +
                ", compressPath='" + compressPath + '\'' +
                ", isChecked=" + isChecked +
                ", rotate=" + rotate +
                ", comRotateValue=" + comRotateValue +
                ", primaryText='" + primaryText + '\'' +
                ", desText='" + desText + '\'' +
                ", dateText='" + dateText + '\'' +
                ", compoundPath='" + compoundPath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ModelPic modelPic = (ModelPic) o;

        if (small != modelPic.small) return false;
        if (isChecked != modelPic.isChecked) return false;
        if (rotate != modelPic.rotate) return false;
        if (comRotateValue != modelPic.comRotateValue) return false;
        if (assetpath != null ? !assetpath.equals(modelPic.assetpath) : modelPic.assetpath != null)
            return false;
        if (assetcover != null ? !assetcover.equals(modelPic.assetcover) : modelPic.assetcover != null)
            return false;
        if (asseturl != null ? !asseturl.equals(modelPic.asseturl) : modelPic.asseturl != null)
            return false;
        if (compressPath != null ? !compressPath.equals(modelPic.compressPath) : modelPic.compressPath != null)
            return false;
        if (primaryText != null ? !primaryText.equals(modelPic.primaryText) : modelPic.primaryText != null)
            return false;
        if (desText != null ? !desText.equals(modelPic.desText) : modelPic.desText != null)
            return false;
        if (dateText != null ? !dateText.equals(modelPic.dateText) : modelPic.dateText != null)
            return false;
        return compoundPath != null ? compoundPath.equals(modelPic.compoundPath) : modelPic.compoundPath == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (assetpath != null ? assetpath.hashCode() : 0);
        result = 31 * result + (assetcover != null ? assetcover.hashCode() : 0);
        result = 31 * result + (asseturl != null ? asseturl.hashCode() : 0);
        result = 31 * result + small;
        result = 31 * result + (compressPath != null ? compressPath.hashCode() : 0);
        result = 31 * result + (isChecked ? 1 : 0);
        result = 31 * result + rotate;
        result = 31 * result + comRotateValue;
        result = 31 * result + (primaryText != null ? primaryText.hashCode() : 0);
        result = 31 * result + (desText != null ? desText.hashCode() : 0);
        result = 31 * result + (dateText != null ? dateText.hashCode() : 0);
        result = 31 * result + (compoundPath != null ? compoundPath.hashCode() : 0);
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

    public String getAsseturl() {
        return asseturl;
    }

    public void setAsseturl(String asseturl) {
        this.asseturl = asseturl;
    }

    public int getSmall() {
        return small;
    }

    public void setSmall(int small) {
        this.small = small;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public int getComRotateValue() {
        return comRotateValue;
    }

    public void setComRotateValue(int comRotateValue) {
        this.comRotateValue = comRotateValue;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
    }

    public String getDesText() {
        return desText;
    }

    public void setDesText(String desText) {
        this.desText = desText;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }

    public String getCompoundPath() {
        return compoundPath;
    }

    public void setCompoundPath(String compoundPath) {
        this.compoundPath = compoundPath;
    }

    public String getMobileUser() {
        return mobileUser;
    }

    public void setMobileUser(String mobileUser) {
        this.mobileUser = mobileUser;
    }
}
