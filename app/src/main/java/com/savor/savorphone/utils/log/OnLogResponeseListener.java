package com.savor.savorphone.utils.log;

public interface OnLogResponeseListener {
	void onLogNull();

	void onLogSuccess(int actionType, LogRespVo result);

}
