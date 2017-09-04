package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 列表页条目公共bean
 * Created by admin on 2017/7/10.
 */

public class CommonListItem implements Serializable {

    private static final long serialVersionUID = -1;
    private String id;
    /**文章ID*/
    private String artid;
    /**更新时间*/
    private String  updateTime;
    private String  state;
    private String  category;
    private String  categoryId;
    /**文章标题*/
    private String  title;
    /**文章类型  0:纯文本，1:图文，2:图集,3:视频,4:纯视频*/
    private int  type;
    private String  acreateTime;
    /**文章封面图*/
    private String  imageURL;
    /**文章详情页H5*/
    private String  contentURL;
    /**头条封面图*/
    private String  indexImageUrl;
    /**视频地址*/
    private String  videoURL;
    private String  mediaId;
    private String  duration;
    private int canplay;
    /**来源*/
    private String sourceName;
    /**视频文件名*/
    private String name;
    /**来源logo*/
    private String logo;
    /**排序序号*/
    private String sort_num;

    private String colid;
    private long ucreateTime;
    /**图集图片个数*/
    private int colTuJi;
    private String order_tag;
    /**是否收藏，1：已收藏,0:未收藏**/
    private int collected;

    private String shareTitle;

    private boolean isMute=false;

    private String imgStyle;

    public String getImgStyle() {
        return imgStyle;
    }

    public void setImgStyle(String imgStyle) {
        this.imgStyle = imgStyle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtid() {
        return artid;
    }

    public void setArtid(String artid) {
        this.artid = artid;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAcreateTime() {
        return acreateTime;
    }

    public void setAcreateTime(String acreateTime) {
        this.acreateTime = acreateTime;
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

    public String getIndexImageUrl() {
        return indexImageUrl;
    }

    public void setIndexImageUrl(String indexImageUrl) {
        this.indexImageUrl = indexImageUrl;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getCanplay() {
        return canplay;
    }

    public void setCanplay(int canplay) {
        this.canplay = canplay;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSort_num() {
        return sort_num;
    }

    public void setSort_num(String sort_num) {
        this.sort_num = sort_num;
    }

    public String getColid() {
        return colid;
    }

    public void setColid(String colid) {
        this.colid = colid;
    }

    public long getUcreateTime() {
        return ucreateTime;
    }

    public void setUcreateTime(long ucreateTime) {
        this.ucreateTime = ucreateTime;
    }

    public int getColTuJi() {
        return colTuJi;
    }

    public void setColTuJi(int colTuJi) {
        this.colTuJi = colTuJi;
    }

    public String getOrder_tag() {
        return order_tag;
    }

    public void setOrder_tag(String order_tag) {
        this.order_tag = order_tag;
    }

    public int getCollected() {
        return collected;
    }

    public void setCollected(int collected) {
        this.collected = collected;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    @Override
    public String toString() {
        return "CommonListItem{" +
                "id='" + id + '\'' +
                ", artid='" + artid + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", state='" + state + '\'' +
                ", category='" + category + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", acreateTime='" + acreateTime + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", contentURL='" + contentURL + '\'' +
                ", indexImageUrl='" + indexImageUrl + '\'' +
                ", videoURL='" + videoURL + '\'' +
                ", mediaId='" + mediaId + '\'' +
                ", duration='" + duration + '\'' +
                ", canplay=" + canplay +
                ", sourceName='" + sourceName + '\'' +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", sort_num='" + sort_num + '\'' +
                ", colid='" + colid + '\'' +
                ", ucreateTime=" + ucreateTime +
                ", colTuJi=" + colTuJi +
                ", order_tag='" + order_tag + '\'' +
                ", collected=" + collected +
                ", shareTitle='" + shareTitle + '\'' +
                ", isMute=" + isMute +
                ", imgStyle='" + imgStyle + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonListItem)) return false;

        CommonListItem that = (CommonListItem) o;

        if (type != that.type) return false;
        if (canplay != that.canplay) return false;
        if (ucreateTime != that.ucreateTime) return false;
        if (colTuJi != that.colTuJi) return false;
        if (collected != that.collected) return false;
        if (isMute != that.isMute) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (artid != null ? !artid.equals(that.artid) : that.artid != null) return false;
        if (updateTime != null ? !updateTime.equals(that.updateTime) : that.updateTime != null)
            return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (category != null ? !category.equals(that.category) : that.category != null)
            return false;
        if (categoryId != null ? !categoryId.equals(that.categoryId) : that.categoryId != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (acreateTime != null ? !acreateTime.equals(that.acreateTime) : that.acreateTime != null)
            return false;
        if (imageURL != null ? !imageURL.equals(that.imageURL) : that.imageURL != null)
            return false;
        if (contentURL != null ? !contentURL.equals(that.contentURL) : that.contentURL != null)
            return false;
        if (indexImageUrl != null ? !indexImageUrl.equals(that.indexImageUrl) : that.indexImageUrl != null)
            return false;
        if (videoURL != null ? !videoURL.equals(that.videoURL) : that.videoURL != null)
            return false;
        if (mediaId != null ? !mediaId.equals(that.mediaId) : that.mediaId != null) return false;
        if (duration != null ? !duration.equals(that.duration) : that.duration != null)
            return false;
        if (sourceName != null ? !sourceName.equals(that.sourceName) : that.sourceName != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (logo != null ? !logo.equals(that.logo) : that.logo != null) return false;
        if (sort_num != null ? !sort_num.equals(that.sort_num) : that.sort_num != null)
            return false;
        if (colid != null ? !colid.equals(that.colid) : that.colid != null) return false;
        if (order_tag != null ? !order_tag.equals(that.order_tag) : that.order_tag != null)
            return false;
        if (shareTitle != null ? !shareTitle.equals(that.shareTitle) : that.shareTitle != null)
            return false;
        return imgStyle != null ? imgStyle.equals(that.imgStyle) : that.imgStyle == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (artid != null ? artid.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + (acreateTime != null ? acreateTime.hashCode() : 0);
        result = 31 * result + (imageURL != null ? imageURL.hashCode() : 0);
        result = 31 * result + (contentURL != null ? contentURL.hashCode() : 0);
        result = 31 * result + (indexImageUrl != null ? indexImageUrl.hashCode() : 0);
        result = 31 * result + (videoURL != null ? videoURL.hashCode() : 0);
        result = 31 * result + (mediaId != null ? mediaId.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + canplay;
        result = 31 * result + (sourceName != null ? sourceName.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (logo != null ? logo.hashCode() : 0);
        result = 31 * result + (sort_num != null ? sort_num.hashCode() : 0);
        result = 31 * result + (colid != null ? colid.hashCode() : 0);
        result = 31 * result + (int) (ucreateTime ^ (ucreateTime >>> 32));
        result = 31 * result + colTuJi;
        result = 31 * result + (order_tag != null ? order_tag.hashCode() : 0);
        result = 31 * result + collected;
        result = 31 * result + (shareTitle != null ? shareTitle.hashCode() : 0);
        result = 31 * result + (isMute ? 1 : 0);
        result = 31 * result + (imgStyle != null ? imgStyle.hashCode() : 0);
        return result;
    }
}
