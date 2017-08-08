package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 机顶盒信息
 * Created by hezd on 2017/3/21.
 */

public class TvBoxInfo implements Serializable{

    /**
     * command : CLIENT_HEART_REQ
     * message_contents : ["I am a Heart Pakage..."]
     * serial_number : 6ae0d8d01490064801349
     * box_ip : 192.168.88.100
     * box_mac : FCD5D900B74C
     * hotel_id : 7
     * room_id : 15
     * ssid : redian-john.lee
     * random_code : 564
     */

    private String command;
    private String serial_number;
    private String box_ip;
    private String box_mac;
    private String hotel_id;
    private String room_id;
    private String ssid;
    private String random_code;
    private List<String> message_contents;

    @Override
    public String toString() {
        return "TvBoxInfo{" +
                "command='" + command + '\'' +
                ", serial_number='" + serial_number + '\'' +
                ", box_ip='" + box_ip + '\'' +
                ", box_mac='" + box_mac + '\'' +
                ", hotel_id='" + hotel_id + '\'' +
                ", room_id='" + room_id + '\'' +
                ", ssid='" + ssid + '\'' +
                ", random_code='" + random_code + '\'' +
                ", message_contents=" + message_contents +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TvBoxInfo tvBoxInfo = (TvBoxInfo) o;

        if (command != null ? !command.equals(tvBoxInfo.command) : tvBoxInfo.command != null)
            return false;
        if (serial_number != null ? !serial_number.equals(tvBoxInfo.serial_number) : tvBoxInfo.serial_number != null)
            return false;
        if (box_ip != null ? !box_ip.equals(tvBoxInfo.box_ip) : tvBoxInfo.box_ip != null)
            return false;
        if (box_mac != null ? !box_mac.equals(tvBoxInfo.box_mac) : tvBoxInfo.box_mac != null)
            return false;
        if (hotel_id != null ? !hotel_id.equals(tvBoxInfo.hotel_id) : tvBoxInfo.hotel_id != null)
            return false;
        if (room_id != null ? !room_id.equals(tvBoxInfo.room_id) : tvBoxInfo.room_id != null)
            return false;
        if (ssid != null ? !ssid.equals(tvBoxInfo.ssid) : tvBoxInfo.ssid != null) return false;
        if (random_code != null ? !random_code.equals(tvBoxInfo.random_code) : tvBoxInfo.random_code != null)
            return false;
        return message_contents != null ? message_contents.equals(tvBoxInfo.message_contents) : tvBoxInfo.message_contents == null;

    }

    @Override
    public int hashCode() {
        int result = command != null ? command.hashCode() : 0;
        result = 31 * result + (serial_number != null ? serial_number.hashCode() : 0);
        result = 31 * result + (box_ip != null ? box_ip.hashCode() : 0);
        result = 31 * result + (box_mac != null ? box_mac.hashCode() : 0);
        result = 31 * result + (hotel_id != null ? hotel_id.hashCode() : 0);
        result = 31 * result + (room_id != null ? room_id.hashCode() : 0);
        result = 31 * result + (ssid != null ? ssid.hashCode() : 0);
        result = 31 * result + (random_code != null ? random_code.hashCode() : 0);
        result = 31 * result + (message_contents != null ? message_contents.hashCode() : 0);
        return result;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getBox_ip() {
        return box_ip;
    }

    public void setBox_ip(String box_ip) {
        this.box_ip = box_ip;
    }

    public String getBox_mac() {
        return box_mac;
    }

    public void setBox_mac(String box_mac) {
        this.box_mac = box_mac;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getRandom_code() {
        return random_code;
    }

    public void setRandom_code(String random_code) {
        this.random_code = random_code;
    }

    public List<String> getMessage_contents() {
        return message_contents;
    }

    public void setMessage_contents(List<String> message_contents) {
        this.message_contents = message_contents;
    }
}
