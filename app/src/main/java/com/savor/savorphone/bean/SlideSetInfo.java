package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 新建幻灯片信息
 */
public class SlideSetInfo implements Serializable {

    /**
     * 是否是新建幻灯片集
     */
    public boolean isNewCreate;
    //文件夹名（组名）
    public String groupName;
    //文件夹创建/更新时间
    @Override
    public String toString() {
        return "SlideSetInfo{" +
                "isNewCreate=" + isNewCreate +
                ", groupName='" + groupName + '\'' +
                ", updateTime=" + updateTime +
                ", imageList=" + imageList +
                '}';
    }public long updateTime;
    //该组所有图片路径集合
    public List<String> imageList = new ArrayList<String>();


    @Override
    public boolean equals(Object obj) {
        return this.groupName.equals(((SlideSetInfo) obj).groupName);
    }
}
