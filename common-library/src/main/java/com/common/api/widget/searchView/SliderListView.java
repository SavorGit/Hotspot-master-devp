package com.common.api.widget.searchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.common.api.R;
import com.common.api.widget.searchView.SliderView.OnItemClickListener;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SliderListView extends LinearLayout{
	// 搜索控件
	private View mRootView;
	// 内容列表
	private ListView mContentListView;
	// 字母导航条
	private SliderView mSliderView;
	// 上下文对象
	private Context mContext;
	// 加载布局
	private LinearLayout mLoadingLayout;
	// 正则表达式
	private static final String FORMAT = "^[a-z,A-Z].$";
	// 内容列表headerview处理器
	private ContentHeaderViewHandler mContentHeaderViewHandler;
	// 内容列表原数据
	private List<IContentItem> mContentList;
	private OnSliderItemSelect mOnSliderItemSelectListener;
	// 内容列表条目点击事件
	private OnContentListItemClickListener mOnContentListItemClickListener;
	private boolean isShowPop = true;
	
	private int mHeaderHeight;
	
	/**
	 * 非常规字符数&$*
	 */
	private int unusualCharCount = 0;
	/**
	 * 内容下标集合
	 */
	private ArrayList<String> mSections;
	
	public ArrayList<String> getSections() {
		return mSections;
	}

	public SliderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SliderListView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化控件
	 * 
	 * @param context
	 *            上下文对象
	 */
	private void init(Context context) {
		this.mContext = context;
		mRootView = View.inflate(context, R.layout.slide_list_view, this);
		mSliderView = (SliderView) mRootView
				.findViewById(R.id.citys_bladeview);
		mContentListView = (ListView) mRootView
				.findViewById(R.id.content_list);
		mLoadingLayout = (LinearLayout) mRootView
				.findViewById(R.id.content_list_empty);

		android.view.ViewGroup.LayoutParams layoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mRootView.setLayoutParams(layoutParams);

	}

	/**设置集合数据
	 * @param allCities 正文列表数据集合
	 * @param hotCities 头数据集合
	 */
	public void setData(List<IContentItem> allCities,List<IContentItem> hotCities) {
		// hideLoadingLayout();
		mSliderView.setVisibility(View.VISIBLE);
		this.mContentList = allCities;
		mSections = new ArrayList<String>();
		HashMap<String, List<IContentItem>> mMap = new HashMap<String, List<IContentItem>>();
		ArrayList<Integer> mPositions = new ArrayList<Integer>();
		final HashMap<String, Integer> mIndexer = new HashMap<String, Integer>();

		// 初始化内容列表数据
		initContentList(allCities, mSections, mMap, mPositions, mIndexer);
		if (hotCities != null && hotCities.size() > 0) {
			// 处理内容列表header view的数据展示
			handleHeaderView();
		}
		// 处理控件事件监听
		handleContentListListener(allCities, mSections, mMap, mPositions,
                mIndexer);

	}
	public void setData(List<IContentItem> allCities,List<IContentItem> nearbyCities,List<IContentItem> hotCities) {
		// hideLoadingLayout();
		mSliderView.setVisibility(View.VISIBLE);
		this.mContentList = allCities;
		mSections = new ArrayList<String>();
		HashMap<String, List<IContentItem>> mMap = new HashMap<String, List<IContentItem>>();
		ArrayList<Integer> mPositions = new ArrayList<Integer>();
		final HashMap<String, Integer> mIndexer = new HashMap<String, Integer>();

		// 初始化内容列表数据
		initContentList(allCities, mSections, mMap, mPositions, mIndexer);
		if ((hotCities != null && hotCities.size() > 0) || (nearbyCities!= null || nearbyCities.size() > 0)) {
			// 处理内容列表headerview的数据展示
			handleHeaderView();
		}
		// 处理控件事件监听
		handleContentListListener(allCities, mSections, mMap, mPositions,
				mIndexer);

	}
	
	public void setHeaderHeight(int height) {
		mHeaderHeight = height;
	}
	
	/**
	 * 隐藏加载布局
	 */
	public void hideLoadingLayout() {
		mLoadingLayout.setVisibility(View.GONE);
	}

	/**
	 * 隐藏加载布局
	 */
	public void showLoadingLayout() {
		mLoadingLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 处理内容列表事件监听
	 * 
	 * @param contentList
	 *            内容列表数据
	 * @param mSections
	 *            导航条标签
	 * @param mMap
	 *            导航条标签和内容列表映射集合
	 * @param mPositions
	 *            导航条标签在内容列表中的位置
	 * @param mIndexer
	 *            导航biaoqia
	 */
	private void handleContentListListener(List<IContentItem> contentList,
			final ArrayList<String> mSections,
			HashMap<String, List<IContentItem>> mMap,
			final ArrayList<Integer> mPositions,
			final HashMap<String, Integer> mIndexer) {
		ContentListAdapter adapter = new ContentListAdapter(mContext,
				contentList, mMap, mSections, mPositions);
		mContentListView.setAdapter(adapter);
		mContentListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (mOnContentListItemClickListener != null) {
							mOnContentListItemClickListener
									.onContentItemClick(parent, view,
											position, id);
						}
					}
				});
		mContentListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
//					mSliderView.dismissPopup();
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Log.d("SlideListView", "firstVisibleItem="+firstVisibleItem);
				if (visibleItemCount > 0 && firstVisibleItem >= 0) {
					int index = 0;
					for (int i = mPositions.size()-1; i >= 0; i--) {
						if (mPositions.get(i) <= firstVisibleItem) {
							index = i;
							break;
						}
					}
					// 划过Header高时才显示Pop
//					if (mHeaderHeight <= mContentListView.getFirstVisiblePosition()) {
//						mSliderView.showPopup(mSections.get(index));
//					}
				}
			}
		});

		List<String> temp = Arrays.asList(SliderView.b);
		if (temp.contains("#")||temp.contains("$")||temp.contains("*")) {
			unusualCharCount++;
		}
		mSliderView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(String s) {
				if (mOnSliderItemSelectListener != null) {
					mOnSliderItemSelectListener.onSliderItemSelect(s);
				}
				
				if (s.equals("#")) {
					mContentListView.setSelection(0);
					return;
				}
				if (mIndexer.get(s) != null) {
					mContentListView.setSelection(mIndexer.get(s) );
				}
			}
		});
	}

	/**
	 * 内容列表点击监听器
	 * 
	 * @author hezd
	 * 
	 */
	public interface OnContentListItemClickListener {
		public void onContentItemClick(AdapterView<?> parent, View view,
				int position, long id);
	}

	public void setOnContentListItemClickListener(
			OnContentListItemClickListener listener) {
		this.mOnContentListItemClickListener = listener;
	}

	/**
	 * 导航条选择监听器
	 * 
	 * @author hezd
	 * 
	 */
	public interface OnSliderItemSelect {
		void onSliderItemSelect(String item);
	}

	/**
	 * 设置item的点击定位listener
	 * 目前只需要处理$和*的定位
	 * @param listener
	 */
	public void setOnSliderItemSelectListener(OnSliderItemSelect listener) {
		this.mOnSliderItemSelectListener = listener;
	}

	/**
	 * 初始化内容列表
	 * 
	 * @param contentList
	 *            原始数据
	 * @param mSections
	 *            导航条标签
	 * @param mMap
	 *            导航条标签和内容列表映射集合
	 * @param mPositions
	 *            导航条标签在内容列表中的位置
	 * @param mIndexer
	 *            导航标签集合
	 */
	private void initContentList(List<IContentItem> contentList,
			ArrayList<String> mSections,
			HashMap<String, List<IContentItem>> mMap,
			ArrayList<Integer> mPositions,
			final HashMap<String, Integer> mIndexer) {
		for (IContentItem city : contentList) {
			String firstName = city.getPinyin().substring(0, 1).toUpperCase();// 第一个字拼音的第一个字母
			if (!firstName.matches(FORMAT)) {
				if (mSections.contains(firstName)) {
					mMap.get(firstName).add(city);
				} else {
					mSections.add(firstName);
					List<IContentItem> list = new ArrayList<IContentItem>();
					list.add(city);
					mMap.put(firstName, list);
				}
			} else {
				if (mSections.contains("#")) {
					mMap.get("#").add(city);
				} else {
					mSections.add("#");
					List<IContentItem> list = new ArrayList<IContentItem>();
					list.add(city);
					mMap.put("#", list);
				}
			}
		}
		Collections.sort(mSections);// 按照字母重新排序
		int position = 0;
		for (int i = 0; i < mSections.size(); i++) {
			mIndexer.put(mSections.get(i), position);// 存入map中，key为首字母字符串，value为首字母在listview中位置
			mPositions.add(position);// 首字母在listview中位置，存入list中
			position += mMap.get(mSections.get(i)).size();// 计算下一个首字母在listview的位置
		}
	}

	public void setContentHeaderViewHandler(ContentHeaderViewHandler handler) {
		this.mContentHeaderViewHandler = handler;
	}

	/**
	 * 处理内容列表listview的headerview数据展示
	 */
	private void handleHeaderView() {
		if (mContentHeaderViewHandler != null) {
			mContentHeaderViewHandler.initContentHeaderView();
		}
	}

	/**
	 * 设置导航条内容
	 */
	public void setSliderData(String[] items) {
		mSliderView.setDataChanged(items);
	}

	public int getHeaderViews() {
		return mContentListView.getHeaderViewsCount();
	}

	public boolean isShowPop() {
		return isShowPop;
	}

	public void setShowPop(boolean isShowPop) {
		this.isShowPop = isShowPop;
		mSliderView.setShowPop(this.isShowPop);
	}

}
