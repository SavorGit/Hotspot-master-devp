package com.savor.savorphone.bean;

/**
 * 图片旋转返回结果
 */
public class RotateProResponse extends BaseProResponse {

	private static final long serialVersionUID = -8167549459162001700L;
	/** The current value after being rotated. **/
	private int rotatevalue;
	public int getRotateValue() {
		return rotatevalue;
	}
	public void setRotateValue(int rotateValue) {
		rotatevalue = rotateValue;
	}
	
}
