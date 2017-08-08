package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/9.
 */

public class VodBean implements Serializable {
    private static final long serialVersionUID = 2297653397416862723L;

    private int id;
    private String category;
    private String title;
    private String name;
    private int duration;
    private String imageURL;
    private String contentURL;
    private int canPlay;
    private String videoURL;
    private String shareTitle;
    private String shareContent;
    private long createTime;
    /**0,纯文本，1，图文，2，纯图片3.视频图文.4纯视频*/
    private int type;
    private boolean isMute=false;
    private String mediaId;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getContentURL() {
        return contentURL;
    }

    public void setContentURL(String contentURL) {
        this.contentURL = contentURL;
    }

    public int getCanPlay() {
        return canPlay;
    }

    public void setCanPlay(int canPlay) {
        this.canPlay = canPlay;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VodBean vodBean = (VodBean) o;

        if (id != vodBean.id) return false;
        if (duration != vodBean.duration) return false;
        if (canPlay != vodBean.canPlay) return false;
        if (createTime != vodBean.createTime) return false;
        if (type != vodBean.type) return false;
        if (isMute != vodBean.isMute) return false;
        if (category != null ? !category.equals(vodBean.category) : vodBean.category != null)
            return false;
        if (title != null ? !title.equals(vodBean.title) : vodBean.title != null) return false;
        if (name != null ? !name.equals(vodBean.name) : vodBean.name != null) return false;
        if (imageURL != null ? !imageURL.equals(vodBean.imageURL) : vodBean.imageURL != null)
            return false;
        if (contentURL != null ? !contentURL.equals(vodBean.contentURL) : vodBean.contentURL != null)
            return false;
        if (videoURL != null ? !videoURL.equals(vodBean.videoURL) : vodBean.videoURL != null)
            return false;
        if (shareTitle != null ? !shareTitle.equals(vodBean.shareTitle) : vodBean.shareTitle != null)
            return false;
        if (shareContent != null ? !shareContent.equals(vodBean.shareContent) : vodBean.shareContent != null)
            return false;
        return mediaId != null ? mediaId.equals(vodBean.mediaId) : vodBean.mediaId == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + duration;
        result = 31 * result + (imageURL != null ? imageURL.hashCode() : 0);
        result = 31 * result + (contentURL != null ? contentURL.hashCode() : 0);
        result = 31 * result + canPlay;
        result = 31 * result + (videoURL != null ? videoURL.hashCode() : 0);
        result = 31 * result + (shareTitle != null ? shareTitle.hashCode() : 0);
        result = 31 * result + (shareContent != null ? shareContent.hashCode() : 0);
        result = 31 * result + (int) (createTime ^ (createTime >>> 32));
        result = 31 * result + type;
        result = 31 * result + (isMute ? 1 : 0);
        result = 31 * result + (mediaId != null ? mediaId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VodBean{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", imageURL='" + imageURL + '\'' +
                ", contentURL='" + contentURL + '\'' +
                ", canPlay=" + canPlay +
                ", videoURL='" + videoURL + '\'' +
                ", shareTitle='" + shareTitle + '\'' +
                ", shareContent='" + shareContent + '\'' +
                ", createTime=" + createTime +
                ", type=" + type +
                ", isMute=" + isMute +
                ", mediaId='" + mediaId + '\'' +
                '}';
    }
}
