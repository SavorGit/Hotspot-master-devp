package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/2/10.
 */

public class UpgradeInfo implements Serializable {
    private static final long serialVersionUID = -1;
    private int id;
    private int device_type;
    private int update_type;
    private String md5;
    private String[] remark;
    private String version_name;
    private String version_code;
    private String oss_addr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDevice_type() {
        return device_type;
    }

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
    }

    public int getUpdate_type() {
        return update_type;
    }

    public void setUpdate_type(int update_type) {
        this.update_type = update_type;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String[] getRemark() {
        return remark;
    }

    public void setRemark(String[] remark) {
        this.remark = remark;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getVersion_code() {
        return version_code;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }

    public String getOss_addr() {
        return oss_addr;
    }

    public void setOss_addr(String oss_addr) {
        this.oss_addr = oss_addr;
    }


    @Override
    public String toString() {
        return "UpgradeInfo{" +
                "id=" + id +
                ", device_type=" + device_type +
                ", update_type=" + update_type +
                ", md5='" + md5 + '\'' +
                ", remark=" + Arrays.toString(remark) +
                ", version_name='" + version_name + '\'' +
                ", version_code='" + version_code + '\'' +
                ", oss_addr='" + oss_addr + '\'' +
                '}';
    }
}
