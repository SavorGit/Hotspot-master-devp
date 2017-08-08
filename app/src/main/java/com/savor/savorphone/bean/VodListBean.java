package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/15.
 */

public class VodListBean implements Serializable {
    private Long maxTime;
    private Long  minTime;
    private List<VodBean> list ;
    private int count;
    private String flag;

    @Override
    public String toString() {
        return "VodListBean{" +
                "maxTime=" + maxTime +
                ", minTime=" + minTime +
                ", list=" + list +
                ", count=" + count +
                ", flag='" + flag + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VodListBean that = (VodListBean) o;

        if (count != that.count) return false;
        if (maxTime != null ? !maxTime.equals(that.maxTime) : that.maxTime != null) return false;
        if (minTime != null ? !minTime.equals(that.minTime) : that.minTime != null) return false;
        if (list != null ? !list.equals(that.list) : that.list != null) return false;
        return flag != null ? flag.equals(that.flag) : that.flag == null;

    }

    @Override
    public int hashCode() {
        int result = maxTime != null ? maxTime.hashCode() : 0;
        result = 31 * result + (minTime != null ? minTime.hashCode() : 0);
        result = 31 * result + (list != null ? list.hashCode() : 0);
        result = 31 * result + count;
        result = 31 * result + (flag != null ? flag.hashCode() : 0);
        return result;
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

    public List<VodBean> getList() {
        return list;
    }

    public void setList(List<VodBean> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
