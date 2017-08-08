package com.common.api.widget.searchView;

import java.util.List;

/**
 * 查询控件实现的接口
 * @author hezd
 *
 */
public interface ISearchView {
	
	/**
	 * 设置数据
	 * @param contentList 内容列表数据
	 * @param labels 导航条目集合
	 * @param contentHeaderViewLayoutId 内容列表listview的headerview布局id
	 */
	void setData(List<IContentItem> contentList);
	
	/**
	 * 设置搜索框提示文字
	 * @param hint 提示内容
	 * @param color 设置提示文字颜色，如果-1则不设置
	 */
	void setSearchHint(String hint, int color);
	
	/**
	 * 设置内容列表headerview处理器,一定要在setData方法之前调用,否则headerview无法显示
	 * @param handler 处理器
	 */
	void setContentHeaderViewHandler(ContentHeaderViewHandler handler,int contentHeaderViewLayoutId);
	
	/**
	 * 设置导航条内容
	 * @param items
	 */
	void setSliderData(String[] items);
	
	/**隐藏搜索框*/
	void hideSearchEditText();
}
