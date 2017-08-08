package com.savor.savorphone.bean;

/**
 * 普通图片投屏返回数据
 * Created by hezd on 2017/3/20.
 */

public class ImageProResonse extends BaseProResponse {
    /**
     * 投屏类操作（点播、投视频、投图片）
     * 成功后会在response里返回一个projectId用以标识某次投屏操作，
     * 控制类操作的request须带上这个projectId，
     * 机顶盒校验request中的projectId与前面返回不一致则返回操作失败result 3
     */
    private String projectId;

    @Override
    public String toString() {
        return "ImageProResonse{" +
                "projectId='" + projectId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageProResonse that = (ImageProResonse) o;

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
