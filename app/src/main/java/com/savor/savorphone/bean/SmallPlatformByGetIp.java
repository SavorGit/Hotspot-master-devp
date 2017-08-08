package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * 通过云平台getIp接口获取小平台信息
 * Created by hezd on 2016/12/22.
 */

public class SmallPlatformByGetIp implements Serializable{
    /**小平台地址和酒店id组合字符串，已经弃用，可用localip和hotelid获得*/
    private String ip;
    /**命令类型比如small表示小平台*/
    private String type;
    /**命令端口号*/
    private String command_port;
    /**小平台外网ip，没用*/
    private String hotelIp;
    /**酒店id*/
    private String hotelId;
    /**小平台内网ip*/
    private String localIp;
    /**区域id*/
    private String area_id;



    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommand_port() {
        return command_port;
    }

    public void setCommand_port(String command_port) {
        this.command_port = command_port;
    }

    public String getHotelIp() {
        return hotelIp;
    }

    public void setHotelIp(String hotelIp) {
        this.hotelIp = hotelIp;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmallPlatformByGetIp that = (SmallPlatformByGetIp) o;

        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (command_port != null ? !command_port.equals(that.command_port) : that.command_port != null)
            return false;
        if (hotelIp != null ? !hotelIp.equals(that.hotelIp) : that.hotelIp != null) return false;
        if (hotelId != null ? !hotelId.equals(that.hotelId) : that.hotelId != null) return false;
        if (localIp != null ? !localIp.equals(that.localIp) : that.localIp != null) return false;
        return area_id != null ? area_id.equals(that.area_id) : that.area_id == null;

    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (command_port != null ? command_port.hashCode() : 0);
        result = 31 * result + (hotelIp != null ? hotelIp.hashCode() : 0);
        result = 31 * result + (hotelId != null ? hotelId.hashCode() : 0);
        result = 31 * result + (localIp != null ? localIp.hashCode() : 0);
        result = 31 * result + (area_id != null ? area_id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SmallPlatformByGetIp{" +
                "ip='" + ip + '\'' +
                ", type='" + type + '\'' +
                ", command_port='" + command_port + '\'' +
                ", hotelIp='" + hotelIp + '\'' +
                ", hotelId='" + hotelId + '\'' +
                ", localIp='" + localIp + '\'' +
                ", area_id='" + area_id + '\'' +
                '}';
    }
}
