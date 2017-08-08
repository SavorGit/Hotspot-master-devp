package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 缓存最近可投屏酒店信息
 * Created by hezd on 2017/5/25.
 */

public class HotelMapCache implements Serializable {
    private String lng;
    private String lat;
    private List<HotelMapBean> hotelMapList;

    @Override
    public String toString() {
        return "HotelMapCache{" +
                "lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                ", hotelMapList=" + hotelMapList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HotelMapCache cache = (HotelMapCache) o;

        if (lng != null ? !lng.equals(cache.lng) : cache.lng != null) return false;
        if (lat != null ? !lat.equals(cache.lat) : cache.lat != null) return false;
        return hotelMapList != null ? hotelMapList.equals(cache.hotelMapList) : cache.hotelMapList == null;

    }

    @Override
    public int hashCode() {
        int result = lng != null ? lng.hashCode() : 0;
        result = 31 * result + (lat != null ? lat.hashCode() : 0);
        result = 31 * result + (hotelMapList != null ? hotelMapList.hashCode() : 0);
        return result;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public List<HotelMapBean> getHotelMapList() {
        return hotelMapList;
    }

    public void setHotelMapList(List<HotelMapBean> hotelMapList) {
        this.hotelMapList = hotelMapList;
    }
}
