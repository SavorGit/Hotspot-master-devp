package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 下拉刷新数据
 * Created by Administrator on 2017/2/9.
 */

public class TopHotelVodList implements Serializable {
    private List<AdsBean> adsList = new ArrayList<AdsBean>();
    private List<VodBean> vodList = new ArrayList<VodBean>();
    /**抽奖信息*/
    private AdsBean award;
    private String hotelName;
    private long maxTime;
    private int count;
    private long minTime;
    private String flag;

    @Override
    public String toString() {
        return "TopHotelVodList{" +
                "adsList=" + adsList +
                ", vodList=" + vodList +
                ", award=" + award +
                ", hotelName='" + hotelName + '\'' +
                ", maxTime=" + maxTime +
                ", count=" + count +
                ", minTime=" + minTime +
                ", flag='" + flag + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopHotelVodList that = (TopHotelVodList) o;

        if (maxTime != that.maxTime) return false;
        if (count != that.count) return false;
        if (minTime != that.minTime) return false;
        if (adsList != null ? !adsList.equals(that.adsList) : that.adsList != null) return false;
        if (vodList != null ? !vodList.equals(that.vodList) : that.vodList != null) return false;
        if (award != null ? !award.equals(that.award) : that.award != null) return false;
        if (hotelName != null ? !hotelName.equals(that.hotelName) : that.hotelName != null)
            return false;
        return flag != null ? flag.equals(that.flag) : that.flag == null;

    }

    @Override
    public int hashCode() {
        int result = adsList != null ? adsList.hashCode() : 0;
        result = 31 * result + (vodList != null ? vodList.hashCode() : 0);
        result = 31 * result + (award != null ? award.hashCode() : 0);
        result = 31 * result + (hotelName != null ? hotelName.hashCode() : 0);
        result = 31 * result + (int) (maxTime ^ (maxTime >>> 32));
        result = 31 * result + count;
        result = 31 * result + (int) (minTime ^ (minTime >>> 32));
        result = 31 * result + (flag != null ? flag.hashCode() : 0);
        return result;
    }

    public List<AdsBean> getAdsList() {
        return adsList;
    }

    public void setAdsList(List<AdsBean> adsList) {
        this.adsList = adsList;
    }

    public List<VodBean> getVodList() {
        return vodList;
    }

    public void setVodList(List<VodBean> vodList) {
        this.vodList = vodList;
    }

    public AdsBean getAward() {
        return award;
    }

    public void setAward(AdsBean award) {
        this.award = award;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getMinTime() {
        return minTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
