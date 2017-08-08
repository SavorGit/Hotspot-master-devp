package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * 机顶盒ssdp数据
 * Created by hezd on 2016/12/8.
 */

public class TvBoxSSDPInfo implements Serializable {
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

    /**机顶盒ip*/
    String boxIp;
    /**酒店id*/
    String hotelId;

//    public TvBoxSSDPInfo() {}
    public TvBoxSSDPInfo(String type, String serverIp, String commandPort,String boxIp,String hotelId){
        this.type = type;
        this.serverIp = serverIp;
        this.commandPort = commandPort;
        this.boxIp = boxIp;
        this.hotelId = hotelId;
    }

    @Override
    public String toString() {
        return "TvBoxSSDPInfo{" +
                "type='" + type + '\'' +
                ", serverIp='" + serverIp + '\'' +
                ", commandPort='" + commandPort + '\'' +
                ", boxIp='" + boxIp + '\'' +
                ", hotelId='" + hotelId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TvBoxSSDPInfo that = (TvBoxSSDPInfo) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (serverIp != null ? !serverIp.equals(that.serverIp) : that.serverIp != null)
            return false;
        if (commandPort != null ? !commandPort.equals(that.commandPort) : that.commandPort != null)
            return false;
        if (boxIp != null ? !boxIp.equals(that.boxIp) : that.boxIp != null) return false;
        return hotelId != null ? hotelId.equals(that.hotelId) : that.hotelId == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (serverIp != null ? serverIp.hashCode() : 0);
        result = 31 * result + (commandPort != null ? commandPort.hashCode() : 0);
        result = 31 * result + (boxIp != null ? boxIp.hashCode() : 0);
        result = 31 * result + (hotelId != null ? hotelId.hashCode() : 0);
        return result;
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

    public String getBoxIp() {
        return boxIp;
    }

    public void setBoxIp(String boxIp) {
        this.boxIp = boxIp;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }
}
