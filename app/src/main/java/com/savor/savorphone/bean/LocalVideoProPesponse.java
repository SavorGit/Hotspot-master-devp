package com.savor.savorphone.bean;

/**
 * 点播返回数据
 * Created by hezd on 2017/3/20.
 */

public class LocalVideoProPesponse extends BaseProResponse {
    private String projectId;

    @Override
    public String toString() {
        return "VodProResponse{" +
                "projectId='" + projectId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalVideoProPesponse that = (LocalVideoProPesponse) o;

        return projectId != null ? projectId.equals(that.projectId) : that.projectId == null;

    }

    @Override
    public int hashCode() {
        return projectId != null ? projectId.hashCode() : 0;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
