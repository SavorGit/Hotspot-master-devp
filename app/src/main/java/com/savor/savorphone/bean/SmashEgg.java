package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * Created by admin on 2017/7/10.
 */

public class SmashEgg implements Serializable {
    private static final long serialVersionUID = -1;
    private Award award;

    public Award getAward() {
        return award;
    }

    public void setAward(Award award) {
        this.award = award;
    }

    @Override
    public String toString() {
        return "SmashEgg{" +
                "award=" + award +
                '}';
    }
}
