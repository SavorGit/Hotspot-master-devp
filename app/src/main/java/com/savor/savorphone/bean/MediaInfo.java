package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * 媒体文件（图片或视频）
 */
public class MediaInfo extends BaseProReqeust implements Serializable {
    /**
     * 媒体类型图片
     */
    public static final int MEDIA_TYPE_PIC = 1;
    /**
     * 媒体类型视频
     */
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * 媒体文件（图片或视频）路径
     */
    private String assetpath;
    /**
     * 视频长度
     */
    private long assetlength;
    /**
     * 媒体文件缩略图
     */
    private String assetcover;
    /**
     * 媒体文件访问路径
     */
    private String asseturl;
    /**是否是小图*/
    private int small = 1;
    /**
     * 压缩图（仅适用图片）路径
     */
    private String compressPath;
    /**
     * 是否被选中
     */
    private boolean isChecked = false;
    /**
     * 旋转角度（仅适用图片）
     */
    private int rotate;
    /**
     * 图片合成图相对于原图旋转角度*/
    private int comRotateValue;
    /**
     * 图片合成图主标题
     */
    private String primaryText;
    /**
     * 图片合成图的副标题
     */
    private String desText;
    /**
     * 图片合成图第二副标题 例如日期
     */
    private String dateText;
    /**
     * 合成图路径
     */
    private String compoundPath;
    /**
     * 媒体文件类型，1图片，2视频
     */
    private int mediaType;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 抢投提示，当前xxx正在投屏
     * */
    private String mobileUser;

    @Override
    public String toString() {
        return "MediaInfo{" +
                "assetpath='" + assetpath + '\'' +
                ", assetlength=" + assetlength +
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
                ", mediaType=" + mediaType +
                ", createTime=" + createTime +
                ", mobileUser='" + mobileUser + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MediaInfo mediaInfo = (MediaInfo) o;

        if (assetlength != mediaInfo.assetlength) return false;
        if (small != mediaInfo.small) return false;
        if (isChecked != mediaInfo.isChecked) return false;
        if (rotate != mediaInfo.rotate) return false;
        if (comRotateValue != mediaInfo.comRotateValue) return false;
        if (mediaType != mediaInfo.mediaType) return false;
        if (createTime != mediaInfo.createTime) return false;
        if (assetpath != null ? !assetpath.equals(mediaInfo.assetpath) : mediaInfo.assetpath != null)
            return false;
        if (assetcover != null ? !assetcover.equals(mediaInfo.assetcover) : mediaInfo.assetcover != null)
            return false;
        if (asseturl != null ? !asseturl.equals(mediaInfo.asseturl) : mediaInfo.asseturl != null)
            return false;
        if (compressPath != null ? !compressPath.equals(mediaInfo.compressPath) : mediaInfo.compressPath != null)
            return false;
        if (primaryText != null ? !primaryText.equals(mediaInfo.primaryText) : mediaInfo.primaryText != null)
            return false;
        if (desText != null ? !desText.equals(mediaInfo.desText) : mediaInfo.desText != null)
            return false;
        if (dateText != null ? !dateText.equals(mediaInfo.dateText) : mediaInfo.dateText != null)
            return false;
        if (compoundPath != null ? !compoundPath.equals(mediaInfo.compoundPath) : mediaInfo.compoundPath != null)
            return false;
        return mobileUser != null ? mobileUser.equals(mediaInfo.mobileUser) : mediaInfo.mobileUser == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (assetpath != null ? assetpath.hashCode() : 0);
        result = 31 * result + (int) (assetlength ^ (assetlength >>> 32));
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
        result = 31 * result + mediaType;
        result = 31 * result + (int) (createTime ^ (createTime >>> 32));
        result = 31 * result + (mobileUser != null ? mobileUser.hashCode() : 0);
        return result;
    }

    public String getAssetpath() {
        return assetpath;
    }

    public void setAssetpath(String assetpath) {
        this.assetpath = assetpath;
    }

    public long getAssetlength() {
        return assetlength;
    }

    public void setAssetlength(long assetlength) {
        this.assetlength = assetlength;
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

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getMobileUser() {
        return mobileUser;
    }

    public void setMobileUser(String mobileUser) {
        this.mobileUser = mobileUser;
    }
}
