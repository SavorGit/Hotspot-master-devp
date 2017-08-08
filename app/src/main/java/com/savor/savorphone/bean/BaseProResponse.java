package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * 投屏返回数据基类
 */
public class BaseProResponse implements Serializable {

	private static final long serialVersionUID = -1;
	/** 0表示成功，-1表示失败，1表示视频播放完毕，2表示大小图不匹配，3表示投屏操作与内容不匹配*/
	private int result;
	private String info;
	
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

	@Override
	public String toString() {
		return "BaseProResponse{" +
				"result=" + result +
				", info='" + info + '\'' +
				'}';
	}
}
