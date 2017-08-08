package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * Created by admin on 2017/7/10.
 */

public class Award implements Serializable {
    private static final long serialVersionUID = -1;
    private String award_start_time;
    private String award_end_time;
    private int  lottery_num;

    public String getAward_start_time() {
        return award_start_time;
    }

    public void setAward_start_time(String award_start_time) {
        this.award_start_time = award_start_time;
    }

    public String getAward_end_time() {
        return award_end_time;
    }

    public void setAward_end_time(String award_end_time) {
        this.award_end_time = award_end_time;
    }

    public int getLottery_num() {
        return lottery_num;
    }

    public void setLottery_num(int lottery_num) {
        this.lottery_num = lottery_num;
    }

    @Override
    public String toString() {
        return "Award{" +
                "award_start_time='" + award_start_time + '\'' +
                ", award_end_time='" + award_end_time + '\'' +
                ", lottery_num=" + lottery_num +
                '}';
    }
}
