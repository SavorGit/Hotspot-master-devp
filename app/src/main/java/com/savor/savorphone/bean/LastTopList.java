package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/9.
 */

public class LastTopList implements Serializable{
    private String flag;
    private int count;
    private List<VodBean> list = new ArrayList<VodBean>();
    private Long maxTime;
    private Long  minTime;

    @Override
    public String toString() {
        return "LastTopList{" +
                "flag='" + flag + '\'' +
                ", count=" + count +
                ", list=" + list +
                ", maxTime=" + maxTime +
                ", minTime=" + minTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LastTopList that = (LastTopList) o;

        if (count != that.count) return false;
        if (flag != null ? !flag.equals(that.flag) : that.flag != null) return false;
        if (list != null ? !list.equals(that.list) : that.list != null) return false;
        if (maxTime != null ? !maxTime.equals(that.maxTime) : that.maxTime != null) return false;
        return minTime != null ? minTime.equals(that.minTime) : that.minTime == null;

    }

    @Override
    public int hashCode() {
        int result = flag != null ? flag.hashCode() : 0;
        result = 31 * result + count;
        result = 31 * result + (list != null ? list.hashCode() : 0);
        result = 31 * result + (maxTime != null ? maxTime.hashCode() : 0);
        result = 31 * result + (minTime != null ? minTime.hashCode() : 0);
        return result;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<VodBean> getList() {
        return list;
    }

    public void setList(List<VodBean> list) {
        this.list = list;
    }

    public Long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Long maxTime) {
        this.maxTime = maxTime;
    }

    public Long getMinTime() {
        return minTime;
    }

    public void setMinTime(Long minTime) {
        this.minTime = minTime;
    }
}
