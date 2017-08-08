package com.savor.savorphone.interfaces;


import com.savor.savorphone.bean.StopProResponseVo;

public interface OnStopListener extends OnBaseListenner {
	/**
	 * 视频播放停止成功
	 */
	void stopSuccess();

	/**
	 * 停止失败
	 */
	void stopFailed(StopProResponseVo responseVo);
}
