package com.savor.savorphone.bean.platformvo;

import java.io.Serializable;

/**
 * @see {@link PlatformContact}
 * @author savor
 * 
 */
public class LocationStaticsRequestVo implements Serializable {

	private static final long serialVersionUID = 5145255058566079784L;
	private int hotelId;
	private int roomId;
	private String deviceId;
	private String deviceModel;
	private int interactType;
	private int interactTime;

	public int getHotelId() {
		return hotelId;
	}

	public void setHotelId(int hotelId) {
		this.hotelId = hotelId;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public int getInteractType() {
		return interactType;
	}

	public void setInteractType(int interactType) {
		this.interactType = interactType;
	}

	public int getInteractTime() {
		return interactTime;
	}

	public void setInteractTime(int interactTime) {
		this.interactTime = interactTime;
	}

}
