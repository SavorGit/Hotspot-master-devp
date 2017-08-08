package com.savor.savorphone.utils;

/**
 * Handler 状态
 * 
 * @author savor
 * 
 */
public class ConstantsWhat {

	public static final int CT_FINAL = 30 * 1000;
	public static final int PLAY_FAILED = 0x0101;
	public static final int KILL_DIALOG = 0x0102;
	public static final int NULL = 0x0103;

	public class FunctionsIds {
		/** 请求播放媒体的功能id **/
		public static final String PREPARE = "prepare";
		// /** 请求操作视频的功能id(播放,快进,快退) **/
		// public static final String OPERATION = "5002";
		/** 请求seek-to视频的功能id **/
		public static final String SEEK_TO = "seek_to";
		/** 请求控制声音的功能id **/
		public static final String SET_VOLUME = "set_volume";
		/** 请求缩放的功能id **/
		public static final String ZOOM = "zoom";
		/** 请求旋转的功能id **/
		public static final String ROTATE = "rotate";
		/** 请求查询的功能id **/
		public static final String QUERY = "query";
		// /** 请求查询的功能id **/
		// public static final String QUERY_POS = "query_pos";
		// /** 请求查询的功能id **/
		// public static final String QUERY_BUF = "query_buf";
		/** 请求cover的功能id **/
		public static final String SHOW_VOD_COVER = "show_vod_cover";
		/** play功能：包括开始暂停和快进 **/
		public static final String PLAY = "play";
		/** 请求停止播放媒体的功能id **/
		public static final String STOP = "stop";
		/** 心跳 **/
		public static final String HEART_BEAT = "heart_beat";
	}

	public class ResultCode {
		/** 成功 **/
		public static final int SUCCESS = 0;
		/** 失败 **/
		public static final int FAILED = -1;
		/** 机顶盒未授权 **/
		public static final int UNAUTHORIZED = -2;
		/** 未找到此Id对应的session **/
		public static final int SESSION_NOT_FOUND = -3;
		/** 连接到达上限 **/
		public static final int CONNECTION_UPPER_LIMIT = -4;
		/** 活跃的session已经达到上限 **/
		public static final int SESSION_UPPER_LIMIT = -5;
		/** IP地址未找到 **/
		public static final int IP_MISTAKE = -6;
		/** 系统归位 **/
		public static final int HOMING_SYSTEM = -7;
		public static final int SESSION_SUCCESS = -8;
		public static final int SESSION_FAILED = -9;
	}

}
