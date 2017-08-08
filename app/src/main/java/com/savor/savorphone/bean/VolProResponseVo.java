package com.savor.savorphone.bean;

/**
 * Created by Administrator on 2017/2/10.
 */

public class VolProResponseVo extends BaseProResponse {
    private static final long serialVersionUID = -1L;
    private int vol;

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }

    @Override
    public String toString() {
        return "VolProResponseVo{" +
                "vol=" + vol +
                '}';
    }
}
