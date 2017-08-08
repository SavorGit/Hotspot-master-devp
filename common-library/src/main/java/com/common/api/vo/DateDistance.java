package com.common.api.vo;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/22.
 */
public class DateDistance implements Serializable {
    private static final long serialVersionUID = 1L;

    //1 秒前；2分钟前；3小时前；4天前；5周前
    private int type;
    private long timeLong;
    private String timeLongStr;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(long timeLong) {
        this.timeLong = timeLong;
    }

    public String getTimeLongStr() {
        return timeLongStr;
    }

    public void setTimeLongStr(String timeLongStr) {
        this.timeLongStr = timeLongStr;
    }

    @Override
    public String toString() {
        return "DateDistance{" +
                "type=" + type +
                ", timeLong=" + timeLong +
                ", timeLongStr='" + timeLongStr + '\'' +
                '}';
    }
}
