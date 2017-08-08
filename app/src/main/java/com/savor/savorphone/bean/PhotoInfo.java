package com.savor.savorphone.bean;

/**
 * Created by luminita on 2016/12/12.
 */

public class PhotoInfo {

    /**
     * 相册封面图
     */
    private String topImagePath;
    /**
     * 文件夹名称
     */
    private String folderName;
    /**
     * 文件夹图片数量
     */
    private int imageCounts;

    public String getTopImagePath() {
        return topImagePath;
    }

    public void setTopImagePath(String topImagePath) {
        this.topImagePath = topImagePath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getImageCounts() {
        return imageCounts;
    }

    public void setImageCounts(int imageCounts) {
        this.imageCounts = imageCounts;
    }

    @Override
    public String toString() {
        return "PhotoInfo{" +
                "topImagePath='" + topImagePath + '\'' +
                ", folderName='" + folderName + '\'' +
                ", imageCounts=" + imageCounts +
                '}';
    }
}
