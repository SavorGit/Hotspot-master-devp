package com.savor.savorphone.bean;

import java.io.Serializable;

public class QueryRequestVo implements Serializable {

	private static final long serialVersionUID = 8330152453864561446L;
	private String function;
	/**
	 * Indicating what being query. it can be:</br> ---"all": all info about the
	 * stb</br> ---"pos@session_id": query for the current playing position for
	 * the session_id；</br>sample: pos@2 </br>---"buf@session_id": query for the
	 * buffering status for the session_id;
	 **/
	private String what;

	public String getWhat() {
		return what;
	}

	public void setWhat(String what) {
		this.what = what;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}
}
