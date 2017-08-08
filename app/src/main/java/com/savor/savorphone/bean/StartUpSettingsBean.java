package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * 启动配置
 * Created by Administrator on 2017/3/23.
 */

public class StartUpSettingsBean implements Serializable {
    private static final long serialVersionUID = 2297653397416862723L;
    private String id;
    private String status;
    private String duration;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "StartUpSettingsBean{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", duration='" + duration + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
