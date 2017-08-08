package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * Created by hezd on 2017/4/19.
 */

public class AliLogBean implements Serializable {
    /**当前操作唯一标示取时间戳*/
    private String UUID;
    /**酒楼ID*/
    private String hotel_id;
    /**酒楼ID*/
    private String room_id;
    /**时间*/
    private String time;
    /**动作*/
    private String action;
    /**类型*/
    private String type;
    /**内容标识*/
    private String content_id;
    /**分类id,点播-1，热点-2*/
    private String category_id;
    /**手机ID*/
    private String mobile_id;
    /**媒体标识*/
    private String media_id;
    /**通用参数显示*/
    private String custom_volume;
    /**客户端类型*/
    private String os_type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AliLogBean that = (AliLogBean) o;

        if (UUID != null ? !UUID.equals(that.UUID) : that.UUID != null) return false;
        if (hotel_id != null ? !hotel_id.equals(that.hotel_id) : that.hotel_id != null)
            return false;
        if (room_id != null ? !room_id.equals(that.room_id) : that.room_id != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (action != null ? !action.equals(that.action) : that.action != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (content_id != null ? !content_id.equals(that.content_id) : that.content_id != null)
            return false;
        if (category_id != null ? !category_id.equals(that.category_id) : that.category_id != null)
            return false;
        if (mobile_id != null ? !mobile_id.equals(that.mobile_id) : that.mobile_id != null)
            return false;
        if (media_id != null ? !media_id.equals(that.media_id) : that.media_id != null)
            return false;
        if (custom_volume != null ? !custom_volume.equals(that.custom_volume) : that.custom_volume != null)
            return false;
        return os_type != null ? os_type.equals(that.os_type) : that.os_type == null;

    }

    @Override
    public int hashCode() {
        int result = UUID != null ? UUID.hashCode() : 0;
        result = 31 * result + (hotel_id != null ? hotel_id.hashCode() : 0);
        result = 31 * result + (room_id != null ? room_id.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (content_id != null ? content_id.hashCode() : 0);
        result = 31 * result + (category_id != null ? category_id.hashCode() : 0);
        result = 31 * result + (mobile_id != null ? mobile_id.hashCode() : 0);
        result = 31 * result + (media_id != null ? media_id.hashCode() : 0);
        result = 31 * result + (custom_volume != null ? custom_volume.hashCode() : 0);
        result = 31 * result + (os_type != null ? os_type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AliLogBean{" +
                "UUID='" + UUID + '\'' +
                ", hotel_id='" + hotel_id + '\'' +
                ", room_id='" + room_id + '\'' +
                ", time='" + time + '\'' +
                ", action='" + action + '\'' +
                ", type='" + type + '\'' +
                ", content_id='" + content_id + '\'' +
                ", category_id='" + category_id + '\'' +
                ", mobile_id='" + mobile_id + '\'' +
                ", media_id='" + media_id + '\'' +
                ", custom_volume='" + custom_volume + '\'' +
                ", os_type='" + os_type + '\'' +
                '}';
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getMobile_id() {
        return mobile_id;
    }

    public void setMobile_id(String mobile_id) {
        this.mobile_id = mobile_id;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public String getCustom_volume() {
        return custom_volume;
    }

    public void setCustom_volume(String custom_volume) {
        this.custom_volume = custom_volume;
    }

    public String getOs_type() {
        return os_type;
    }

    public void setOs_type(String os_type) {
        this.os_type = os_type;
    }
}
