package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/9.
 */

public class AdsBean implements Serializable {
    private  int id;
    private String title;
    /**抽奖次数*/
    private String lottery_num;
    private String name;
    private int duration;
    private String imageURL;
    private String award_start_time;
    private String award_end_time;
    /**当前item类型 1.广告 2.抽奖*/
    private int type;

    @Override
    public String toString() {
        return "AdsBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", lottery_num='" + lottery_num + '\'' +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", imageURL='" + imageURL + '\'' +
                ", award_start_time='" + award_start_time + '\'' +
                ", award_end_time='" + award_end_time + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdsBean adsBean = (AdsBean) o;

        if (id != adsBean.id) return false;
        if (duration != adsBean.duration) return false;
        if (type != adsBean.type) return false;
        if (title != null ? !title.equals(adsBean.title) : adsBean.title != null) return false;
        if (lottery_num != null ? !lottery_num.equals(adsBean.lottery_num) : adsBean.lottery_num != null)
            return false;
        if (name != null ? !name.equals(adsBean.name) : adsBean.name != null) return false;
        if (imageURL != null ? !imageURL.equals(adsBean.imageURL) : adsBean.imageURL != null)
            return false;
        if (award_start_time != null ? !award_start_time.equals(adsBean.award_start_time) : adsBean.award_start_time != null)
            return false;
        return award_end_time != null ? award_end_time.equals(adsBean.award_end_time) : adsBean.award_end_time == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (lottery_num != null ? lottery_num.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + duration;
        result = 31 * result + (imageURL != null ? imageURL.hashCode() : 0);
        result = 31 * result + (award_start_time != null ? award_start_time.hashCode() : 0);
        result = 31 * result + (award_end_time != null ? award_end_time.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLottery_num() {
        return lottery_num;
    }

    public void setLottery_num(String lottery_num) {
        this.lottery_num = lottery_num;
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

    public String getAward_start_time() {
        return award_start_time;
    }

    public void setAward_start_time(String award_start_time) {
        this.award_start_time = award_start_time;
    }

    public String getAward_end_time() {
        return award_end_time;
    }

    public void setAward_end_time(String award_end_time) {
        this.award_end_time = award_end_time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
