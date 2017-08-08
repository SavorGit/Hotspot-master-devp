package com.savor.savorphone.utils.log;

public class LogReqVo {
	public int getContentId() {
		return contentId;
	}

	public int getActionType() {
		return actionType;
	}

	public int getReadTime() {
		return readTime;
	}

	private int contentId;
	private int actionType;
	private int readTime;

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public void setReadTime(int readTime) {
		this.readTime = readTime;
	}

}
