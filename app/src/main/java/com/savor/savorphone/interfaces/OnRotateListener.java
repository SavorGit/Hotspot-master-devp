package com.savor.savorphone.interfaces;


import com.savor.savorphone.bean.RotateProResponse;

public interface OnRotateListener extends OnBaseListenner {
	/**
	 * 图片旋转
	 * 
	 * @param responseVo
	 */
	void rotate(RotateProResponse responseVo);

	/**
	 * 旋转失败
	 */
	void rotateFailed(RotateProResponse responseVo);
}
