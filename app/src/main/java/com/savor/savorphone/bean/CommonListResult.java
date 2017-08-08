package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017/7/10.
 */

public class CommonListResult implements Serializable {

    private static final long serialVersionUID = -1;
    private long minTime;
    private long maxTime;
    private String flag;
    private List<CommonListItem> list;
    private int nextpage;

    public long getMinTime() {
        return minTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public List<CommonListItem> getList() {
        return list;
    }

    public void setList(List<CommonListItem> list) {
        this.list = list;
    }

    public int getNextpage() {
        return nextpage;
    }

    public void setNextpage(int nextpage) {
        this.nextpage = nextpage;
    }

    @Override
    public String toString() {
        return "CommonListResult{" +
                "minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", flag='" + flag + '\'' +
                ", list=" + list +
                ", nextpage=" + nextpage +
                '}';
    }
}
