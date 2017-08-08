package com.savor.savorphone.bean;

import java.io.Serializable;


public class StopRequestVo implements Serializable {

	private static final long serialVersionUID = -7224605505218630259L;
	private String function;
	/** ID of session being about to play **/
	private int sessionid;
	/**
	 * O: normal</br> 1: app going to background</br> 2:...
	 **/
	private int reason;

	public int getSessionid() {
		return sessionid;
	}

	public void setSessionid(int sessionid) {
		this.sessionid = sessionid;
	}

	public int getReason() {
		return reason;
	}

	public void setReason(int reason) {
		this.reason = reason;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

}
