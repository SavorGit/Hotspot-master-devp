package com.savor.savorphone.bean;


import java.io.Serializable;

/**
 * 投屏请求基类
 */
public class BaseProReqeust implements Serializable {

	/**投屏类型，2screen图片投屏、视频投屏，vod点播(vodType 1普通点播2宣传图点播)*/
	private String action;
	private String assetname;
	/**图片旋转角度*/
	private int rotatevalue;
	/**1普通点播2酒楼宣传片*/
	private int vodType;
	/**当前操作的图片id*/
	private String imageId;
	/**
	 * 图片类型，1：普通图片；	2：文件图片,3 幻灯片投屏
	 * */
	private String imageType = "1";

	/**
	 * 本地视频路径
	 * */
	private String mediaPath;

	/**图片或文件投屏时的会话id*/
	private String seriesId;

	@Override
	public String toString() {
		return "BaseProReqeust{" +
				"action='" + action + '\'' +
				", assetname='" + assetname + '\'' +
				", rotatevalue=" + rotatevalue +
				", vodType=" + vodType +
				", imageId='" + imageId + '\'' +
				", imageType='" + imageType + '\'' +
				", mediaPath='" + mediaPath + '\'' +
				", seriesId='" + seriesId + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BaseProReqeust that = (BaseProReqeust) o;

		if (rotatevalue != that.rotatevalue) return false;
		if (vodType != that.vodType) return false;
		if (action != null ? !action.equals(that.action) : that.action != null) return false;
		if (assetname != null ? !assetname.equals(that.assetname) : that.assetname != null)
			return false;
		if (imageId != null ? !imageId.equals(that.imageId) : that.imageId != null) return false;
		if (imageType != null ? !imageType.equals(that.imageType) : that.imageType != null)
			return false;
		if (mediaPath != null ? !mediaPath.equals(that.mediaPath) : that.mediaPath != null)
			return false;
		return seriesId != null ? seriesId.equals(that.seriesId) : that.seriesId == null;

	}

	@Override
	public int hashCode() {
		int result = action != null ? action.hashCode() : 0;
		result = 31 * result + (assetname != null ? assetname.hashCode() : 0);
		result = 31 * result + rotatevalue;
		result = 31 * result + vodType;
		result = 31 * result + (imageId != null ? imageId.hashCode() : 0);
		result = 31 * result + (imageType != null ? imageType.hashCode() : 0);
		result = 31 * result + (mediaPath != null ? mediaPath.hashCode() : 0);
		result = 31 * result + (seriesId != null ? seriesId.hashCode() : 0);
		return result;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAssetname() {
		return assetname;
	}

	public void setAssetname(String assetname) {
		this.assetname = assetname;
	}

	public int getRotatevalue() {
		return rotatevalue;
	}

	public void setRotatevalue(int rotatevalue) {
		this.rotatevalue = rotatevalue;
	}

	public int getVodType() {
		return vodType;
	}

	public void setVodType(int vodType) {
		this.vodType = vodType;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getMediaPath() {
		return mediaPath;
	}

	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}

	public String getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}
}
