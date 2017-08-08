package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 下拉刷新数据
 * Created by Administrator on 2017/2/9.
 */

public class BottomHotelVodList implements Serializable {

    private List<VodBean> vodList = new ArrayList<VodBean>();
    private String hotelName;
    private long maxTime;

    @Override
    public String toString() {
        return "BottomHotelVodList{" +
                "vodList=" + vodList +
                ", hotelName='" + hotelName + '\'' +
                ", maxTime=" + maxTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BottomHotelVodList that = (BottomHotelVodList) o;

        if (maxTime != that.maxTime) return false;
        if (vodList != null ? !vodList.equals(that.vodList) : that.vodList != null) return false;
        return hotelName != null ? hotelName.equals(that.hotelName) : that.hotelName == null;

    }

    @Override
    public int hashCode() {
        int result = vodList != null ? vodList.hashCode() : 0;
        result = 31 * result + (hotelName != null ? hotelName.hashCode() : 0);
        result = 31 * result + (int) (maxTime ^ (maxTime >>> 32));
        return result;
    }

    public List<VodBean> getVodList() {
        return vodList;
    }

    public void setVodList(List<VodBean> vodList) {
        this.vodList = vodList;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }
}


