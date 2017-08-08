package com.common.api.widget.searchView;

/**
 * 内容条目必须实现的接口
 * 当以后内容条目类型发生改变时，修改此接口。
 * @author hezd
 *
 */
public interface IContentItem {
	
	/**
	 * 获取条目要展示的内容
	 * @return
	 */
	String getName();
	
	/**
	 * 获取条目拼音全拼
	 * @return
	 */
	String getPinyin();
	/**
	 * 是否为热门城市
	 * @return
	 */
	boolean isHotCity();

	/**获取国家码*/
	String getRegion_code();

	/**
	 * 设置热门城市
	 * @param isHotCity
	 */
	void setHotCity(boolean isHotCity);

	boolean isUserSwitch();

	void setUserSwitch(boolean selected);
}
