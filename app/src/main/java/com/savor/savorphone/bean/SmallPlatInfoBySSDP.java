package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * 小平台ssdp获取的小平台信息
 * Created by zhanghq on 2016/12/8.
 */

public class SmallPlatInfoBySSDP implements Serializable {
    /**酒店id*/
    private int hotelId;
    /**
     * command命令比如呼出二维码small
     */
    String type;
    /**
     * 小平台IP
     */
    String serverIp;
    /**
     * 小平台命令IP
     */
    String commandPort;

    public SmallPlatInfoBySSDP() {}
    public SmallPlatInfoBySSDP(String type, String serverIp, String commandPort,int hotelId){
        this.type = type;
        this.serverIp = serverIp;
        this.commandPort = commandPort;
        this.hotelId = hotelId;
    }

    @Override
    public String toString() {
        return "SmallPlatInfoBySSDP{" +
                "hotelId=" + hotelId +
                ", type='" + type + '\'' +
                ", serverIp='" + serverIp + '\'' +
                ", commandPort='" + commandPort + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmallPlatInfoBySSDP that = (SmallPlatInfoBySSDP) o;

        if (hotelId != that.hotelId) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (serverIp != null ? !serverIp.equals(that.serverIp) : that.serverIp != null)
            return false;
        return commandPort != null ? commandPort.equals(that.commandPort) : that.commandPort == null;

    }

    @Override
    public int hashCode() {
        int result = hotelId;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (serverIp != null ? serverIp.hashCode() : 0);
        result = 31 * result + (commandPort != null ? commandPort.hashCode() : 0);
        return result;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getCommandPort() {
        return commandPort;
    }

    public void setCommandPort(String commandPort) {
        this.commandPort = commandPort;
    }
}
