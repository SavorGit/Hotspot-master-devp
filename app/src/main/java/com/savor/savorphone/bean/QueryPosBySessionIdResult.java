package com.savor.savorphone.bean;

import java.io.Serializable;

public class QueryPosBySessionIdResult implements Serializable {

	private static final long serialVersionUID = -2963538085674375368L;
	private int pos;

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
}
