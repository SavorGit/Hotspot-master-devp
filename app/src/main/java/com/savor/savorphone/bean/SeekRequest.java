package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * 修改播放进度
 */
public class SeekRequest implements Serializable {

	private String function;
	/** ID of session being about to play **/
	private int sessionid;
	/** The position seeking to in the asset being played **/
	private int absolutepos;
	/**
	 * The seeking-to position relative to the current one. it can be negative
	 * value.
	 **/
	private int relativepos;

	public int getSessionid() {
		return sessionid;
	}

	public void setSessionid(int sessionid) {
		this.sessionid = sessionid;
	}

	public int getAbsolutepos() {
		return absolutepos;
	}

	public void setAbsolutepos(int absolutepos) {
		this.absolutepos = absolutepos;
	}

	public int getRelativepos() {
		return relativepos;
	}

	public void setRelativepos(int relativepos) {
		this.relativepos = relativepos;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

}
