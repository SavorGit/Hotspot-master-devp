package com.common.api.vo;

import java.io.Serializable;

public class CacheData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4167770912646605408L;

	private String key;

	private String value;

	private String md5;

	private String time;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
