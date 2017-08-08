package com.savor.savorphone.bean.platformvo;

import java.io.Serializable;

/**
 * 小平台请求Vo基类
 * 
 * @author savor
 * 
 */
public class BaseResponesVo implements Serializable {

	private static final long serialVersionUID = -3249467359047024088L;
	private int status;
	private String result;

	public int getStatus() {
		return status;
	}

	public String getResult() {
		return result;
	}

}
