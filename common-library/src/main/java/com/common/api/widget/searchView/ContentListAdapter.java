package com.common.api.widget.searchView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.common.api.R;


/**
 * 内容列表数据适配器
 * 
 * @author hezd
 */
public class ContentListAdapter extends BaseAdapter implements SectionIndexer {
	/** 内容列表数据*/
	private List<IContentItem> mContentList;
	/** 对应每个导航条目key的value集合组成的map集合*/
	private Map<String, List<IContentItem>> mCategoryMap;
	/** 导航标签集合 */
	private List<String> mNavigationLabels;
	/** 导航标签在listview中的位置*/
	private List<Integer> mPositions;
	// 布局填充器
	private LayoutInflater inflater;
	private Context mContext;
	
	/**
	 * 构造函数
	 * @param context 上下文对象
	 * @param contentList 内容列表集合
	 * @param map 导航标签与内容列表映射集合
	 * @param labels 导航标签集合
	 * @param positions 导航标签在listview中的位置
	 */
	public ContentListAdapter(Context context, List<IContentItem> contentList,
			Map<String, List<IContentItem>> map, List<String> labels,
			List<Integer> positions) {
		inflater = LayoutInflater.from(context);
		mContentList = contentList;
		mCategoryMap = map;
		mNavigationLabels = labels;
		mPositions = positions;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mContentList.size();
	}

	@Override
	public Object getItem(int position) {
		int section = getSectionForPosition(position);
		return mCategoryMap.get(mNavigationLabels.get(section)).get(
				position - getPositionForSection(section));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int section = getSectionForPosition(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.select_city_item, null);
			// 分组标题栏
			holder.group = (TextView) convertView.findViewById(R.id.group_title);
			// 分组条目
			holder.city = (TextView) convertView.findViewById(R.id.column_title);
			holder.itemSelected = (ImageView) convertView.findViewById(R.id.iv_select_icon);
			holder.region = (TextView) convertView.findViewById(R.id.tv_region);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (getPositionForSection(section) == position) {
			holder.group.setVisibility(View.VISIBLE);
			holder.group.setText(mNavigationLabels.get(section));
		} else {
			holder.group.setVisibility(View.GONE);
		}
		IContentItem item = mCategoryMap.get(mNavigationLabels.get(section)).get(
				position - getPositionForSection(section));
		// 如果当前城市是用户选择城市，打锚点。
		if(item.isUserSwitch()) {
//			holder.itemSelected.setVisibility(View.VISIBLE);
			holder.city.setTextColor(mContext.getResources().getColor(R.color.location_city_hint));
		}else {
//			holder.itemSelected.setVisibility(View.GONE);
			holder.city.setTextColor(Color.BLACK);
		}
			
		holder.city.setText(item.getName());
		holder.region.setText(item.getRegion_code());
		return convertView;
	}


	@Override
	public Object[] getSections() {
		return mNavigationLabels.toArray();
	}

	@Override
	public int getPositionForSection(int section) {
		if (section < 0 || section >= mPositions.size()) {
			return -1;
		}
		return mPositions.get(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position < 0 || position >= getCount()) {
			return -1;
		}
		int index = Arrays.binarySearch(mPositions.toArray(), position);
		return index >= 0 ? index : -index - 2;
	}
	
	static class  ViewHolder {
		TextView group;
		TextView city;
		TextView region;
		ImageView itemSelected;
	}


}
