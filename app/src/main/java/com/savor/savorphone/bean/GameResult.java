package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * Created by bushlee on 2017/5/9.
 */

public class GameResult implements Serializable {
    private static final long serialVersionUID = -1;
    private int result;
    private String info;
    private int progress;
    private int done;
    private int win;
    private int prize_id;
    private String prize_name;
    private String prize_time;
    private int prize_level;
    private String url;

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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getPrize_id() {
        return prize_id;
    }

    public void setPrize_id(int prize_id) {
        this.prize_id = prize_id;
    }

    public String getPrize_name() {
        return prize_name;
    }

    public void setPrize_name(String prize_name) {
        this.prize_name = prize_name;
    }

    public String getPrize_time() {
        return prize_time;
    }

    public void setPrize_time(String prize_time) {
        this.prize_time = prize_time;
    }

    public int getPrize_level() {
        return prize_level;
    }

    public void setPrize_level(int prize_level) {
        this.prize_level = prize_level;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "result=" + result +
                ", info='" + info + '\'' +
                ", progress=" + progress +
                ", done=" + done +
                ", win=" + win +
                ", prize_id=" + prize_id +
                ", prize_name='" + prize_name + '\'' +
                ", prize_time='" + prize_time + '\'' +
                ", prize_level=" + prize_level +
                ", url='" + url + '\'' +
                '}';
    }
}
