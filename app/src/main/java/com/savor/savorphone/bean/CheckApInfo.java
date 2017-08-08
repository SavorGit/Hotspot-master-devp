package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * Created by hezd on 2017/2/14.
 */

public class CheckApInfo implements Serializable {
    private int result;
    private String info;
    private int hotelId;

    @Override
    public String toString() {
        return "CheckApInfo{" +
                "result=" + result +
                ", info='" + info + '\'' +
                ", hotelId=" + hotelId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheckApInfo that = (CheckApInfo) o;

        if (result != that.result) return false;
        if (hotelId != that.hotelId) return false;
        return info != null ? info.equals(that.info) : that.info == null;

    }

    @Override
    public int hashCode() {
        int result1 = result;
        result1 = 31 * result1 + (info != null ? info.hashCode() : 0);
        result1 = 31 * result1 + hotelId;
        return result1;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }
}
