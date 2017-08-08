package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hezd on 2017/3/23.
 */

public class CategoryBottomList implements Serializable {
    private List<VodBean> list;
    private long maxTime;

    @Override
    public String toString() {
        return "CategoryBottomList{" +
                "list=" + list +
                ", maxTime=" + maxTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryBottomList that = (CategoryBottomList) o;

        if (maxTime != that.maxTime) return false;
        return list != null ? list.equals(that.list) : that.list == null;

    }

    @Override
    public int hashCode() {
        int result = list != null ? list.hashCode() : 0;
        result = 31 * result + (int) (maxTime ^ (maxTime >>> 32));
        return result;
    }

    public List<VodBean> getList() {
        return list;
    }

    public void setList(List<VodBean> list) {
        this.list = list;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }
}
