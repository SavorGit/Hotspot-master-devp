package com.common.api.widget.searchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.R;
import com.common.api.widget.searchView.SliderView.OnItemClickListener;


/**
 * 通过拼音首字母查询控件
 * 
 * @author hezd
 * 
 */
public class SearchView extends LinearLayout implements ISearchView{
	// 搜索控件
	private View mSearchView;
	// 内容列表
	private ListView mContentListView;
	// 搜索输入框
//	private EditText mSearchEt;
	// 字母导航条
	private SliderView mSliderView;
	// 上下文对象
	private Context mContext;
	// 加载布局 
	private LinearLayout mLoadingLayout;
	// 正则表达式
	private static final String FORMAT = "^[a-z,A-Z].*$";
	// 模糊查询输入框
	private EditText mSearchEdit;
	// 内容列表headerview处理器
	private ContentHeaderViewHandler mContentHeaderViewHandler;
	// 内容列表listview的headerview布局资源id
	private int ContentHeaderViewLaoyoutId;
	// 内容列表原数据 
	private List<IContentItem> mContentList;
	// 搜索结果
	private List<IContentItem> mSearchResults;
	private OnSliderItemSelect mOnSliderItemSelectListener;
	private ListView mSearchResultsLv;
	//城市列表布局 
	private RelativeLayout mCityContentLayout;
	// 搜索结果布局
	private FrameLayout mSearchResultLayout;
	// 清楚输入内容
	private ImageButton mClearBtn;
	// 内容列表条目点击事件 
	private OnContentListItemClickListener mOnContentListItemClickListener;
	private OnSearchResultItemClickListener mOnSearchResultItemClickListener;
	private boolean isShowPop = true;
	private RelativeLayout msearchEditLayout;
	private OnEditTextInputListener mOnEditTextInputListener;
	private TextView mSearchEmptyTv;
	private List<IContentItem> mHistoryResults = new ArrayList<IContentItem>();
	/**搜索城市（城市名）*/
	private static final String M_SELECT_CITY_SEARCH = "M_Select_City_Search";
	
	public SearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SearchView(Context context) {
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
		mSearchView = View.inflate(context, R.layout.search_view_layout, this);
		msearchEditLayout = (RelativeLayout) mSearchView.findViewById(R.id.search_container);
		mSliderView = (SliderView) mSearchView.findViewById(R.id.citys_bladeview);
		mContentListView = (ListView) mSearchView.findViewById(R.id.content_list);
//		mSearchEt = (EditText) mSearchView.findViewById(R.id.search_edit);
		mLoadingLayout = (LinearLayout) mSearchView.findViewById(R.id.content_list_empty);
		
		mCityContentLayout = (RelativeLayout) mSearchView.findViewById(R.id.city_content_container);
		mSearchEdit = (EditText) mSearchView.findViewById(R.id.search_edit);
		mSearchResultsLv = (ListView) findViewById(R.id.search_list);
		mSearchResultLayout = (FrameLayout) mSearchView.findViewById(R.id.search_content_container);
		mClearBtn = (ImageButton) mSearchView.findViewById(R.id.ib_clear_text);
		mSearchEmptyTv = (TextView) findViewById(R.id.search_empty);
		setClearListener(mClearBtn);
		setResultListrener();
		android.view.ViewGroup.LayoutParams layoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mSearchView.setLayoutParams(layoutParams);
		
		mSearchEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mHistoryResults!=null&&!TextUtils.isEmpty(mSearchEdit.getText())) {
					mHistoryResults.clear();
				}
			}
		});
		
		// 默认显示城市列表界面
		hideSearchResultLayout();
		
		// 模糊查询监听处理
		handleSearchEditText(mSearchEdit);
	}
	
	private void setResultListrener() {
		mSearchResultsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mOnSearchResultItemClickListener!=null) {
					mOnSearchResultItemClickListener.setOnSearchResultItemClickListener(parent, view, position, id);
				}
			}

		});
	}
	
	/**
	 * 搜索结果条目点击监听
	 * @author hezd
	 *
	 */
	public interface OnSearchResultItemClickListener {
		public void setOnSearchResultItemClickListener(AdapterView<?> parent, View view,
				int position, long id);
	}
	
	public void setOnSearchResultItemClickListener(OnSearchResultItemClickListener listener) {
		this.mOnSearchResultItemClickListener = listener;
	}
	
	private void setClearListener(ImageButton clearBtn) {
		clearBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(mSearchEdit.getText().toString())) {
					mSearchEdit.setText("");
				}
			}
		});
	}

	/**
	 * 处理模糊查询
	 * @param editText
	 */
	private void handleSearchEditText(EditText editText) {
		// 注册内容变化监听器
		editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String content = s.toString().trim();
				if(!TextUtils.isEmpty(content)) {
					mClearBtn.setVisibility(View.VISIBLE);
					if(mOnEditTextInputListener!=null) {
						mOnEditTextInputListener.OnEditTextInput(content);
					}
					// 模糊查询并显示查询结果
					fuzzySearch(content);
				}else {
					mClearBtn.setVisibility(View.INVISIBLE);
					mHistoryResults.clear();
					if(mOnEditTextInputListener!=null) {
						mOnEditTextInputListener.onEditTextInputEmpty();
					}
					hideSearchResultLayout();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	public interface OnEditTextInputListener {
		void OnEditTextInput(String s);
		void onEditTextInputEmpty();
	}
	
	public void setOnEditTextInputListener(OnEditTextInputListener listener) {
		this.mOnEditTextInputListener = listener;
	}
	
	/**
	 * 执行模糊查询 
	 * @param content 查询输入框内容
	 */
	protected void fuzzySearch(String content) {
//		List<IContentItem> tempList = new ArrayList<IContentItem>();
		if(mContentList!=null) {
			if(mSearchResults==null)
				mSearchResults = new ArrayList<IContentItem>();
			mSearchResults.clear();
			for(IContentItem item : mContentList) {
				if(item.getName().startsWith(content)||item.getPinyin().startsWith(content)) {
//					tempList.add(item);
					mSearchResults.add(item);
				}
			}
//			if(tempList.size()>0) {
//				mSearchResults.clear();
//				mSearchResults.addAll(tempList);
//			}
			
			if(mSearchResults!=null&&mSearchResults.size()>0) {
				Map<String,String> map = new HashMap<String, String>();
				map.put(M_SELECT_CITY_SEARCH, content);
			}
			// 显示搜索结果
			showSearchResults(mSearchResults);
		}
	}

	@Override
	public void setSearchHint(String hint, int color) {
		mSearchEdit.setHint(hint);
		if (color != -1) {
			mSearchEdit.setHintTextColor(color);
		}
	}
	
	/**
	 * 显示查询结果
	 * @param results
	 */
	private void showSearchResults(List<IContentItem> results) {
		showSearchResultLayout();
		if((results==null||results.size()==0)) {
			// 显示无结果结果布局
//			if(mHistoryResults.size()==0)
				mSearchEmptyTv.setVisibility(View.VISIBLE);
			return;
		}
		mSearchEmptyTv.setVisibility(View.GONE);
		SearchResultAdapter adapter = new SearchResultAdapter(mContext);
		adapter.setData(results);
		mSearchResultsLv.setAdapter(adapter);
		mHistoryResults.addAll(results);
	}

	private void showSearchResultLayout() {
		mSearchResultLayout.setVisibility(View.VISIBLE);
		mSearchResultsLv.setVisibility(View.VISIBLE);
		mCityContentLayout.setVisibility(View.GONE);
	}
	
	private void hideSearchResultLayout() {
		mSearchResultLayout.setVisibility(View.GONE);
		mCityContentLayout.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void setData(List<IContentItem> contentList) {
//		hideLoadingLayout();
		mSliderView.setVisibility(View.VISIBLE);
		this.mContentList = contentList;
		List<String> results = new ArrayList<String>();
		for(IContentItem item : contentList) {
			results.add(item.getName());
		}
		ArrayList<String> mSections = new ArrayList<String>();
		HashMap<String, List<IContentItem>> mMap = new HashMap<String, List<IContentItem>>();
		ArrayList<Integer> mPositions = new ArrayList<Integer>();
		final HashMap<String, Integer> mIndexer = new HashMap<String, Integer>();

		// 初始化内容列表数据
		initContentList(contentList, mSections, mMap, mPositions,
				mIndexer);
		// 处理内容列表headerview的数据展示
		handleHeaderView();
		// 处理控件事件监听
		handleContentListListener(contentList, mSections, mMap, mPositions,
				mIndexer);
		
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
	 * @param contentList 内容列表数据
	 * @param mSections 导航条标签
	 * @param mMap 导航条标签和内容列表映射集合
	 * @param mPositions 导航条标签在内容列表中的位置
	 * @param mIndexer 导航biaoqia
	 */
	private void handleContentListListener(List<IContentItem> contentList,ArrayList<String> mSections,HashMap<String, List<IContentItem>> mMap,ArrayList<Integer> mPositions,final HashMap<String, Integer> mIndexer) {
		ContentListAdapter adapter = new ContentListAdapter(mContext,contentList, mMap, mSections, mPositions);
		mContentListView.setAdapter(adapter);
		mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mOnContentListItemClickListener!=null) {
					mOnContentListItemClickListener.setOnContentItemClick(parent, view, position, id);
				}
			}
		});
		
		mSliderView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(String s) {
				if(mOnSliderItemSelectListener!=null) {
					mOnSliderItemSelectListener.setOnSliderItemSelect(s);
				}
				if(s.equals("#")) {
					mContentListView.setSelection(0);
					return;
				}
				if(s.equals("A")) {
					mContentListView.setSelection(1);
					return;
				}
				if (mIndexer.get(s) != null&&!s.equals("*")&&!s.equals("$")) {
					mContentListView.setSelection(mIndexer.get(s)+1);
				}
			}
		});
	}
	
	/**
	 * 内容列表点击监听器 
	 * @author hezd
	 *
	 */
	public interface OnContentListItemClickListener {
		public void setOnContentItemClick(AdapterView<?> parent, View view,
				int position, long id);
	}
	
	public void setOnContentListItemClickListener(OnContentListItemClickListener listener) {
		this.mOnContentListItemClickListener = listener;
	}
	
	/**
	 * 导航条选择监听器
	 * @author hezd
	 *
	 */
	public interface OnSliderItemSelect {
		void setOnSliderItemSelect(String item);
	}
	
	public void setOnSliderItemSelectListener(OnSliderItemSelect listener) {
		this.mOnSliderItemSelectListener = listener;
	}
	
	/**
	 * 初始化内容列表
	 * @param contentList 原始数据
	 * @param mSections 导航条标签
	 * @param mMap 导航条标签和内容列表映射集合
	 * @param mPositions 导航条标签在内容列表中的位置
	 * @param mIndexer 导航标签集合
	 */
	private void initContentList(List<IContentItem> contentList,ArrayList<String> mSections,HashMap<String, List<IContentItem>> mMap,ArrayList<Integer> mPositions,final HashMap<String, Integer> mIndexer) {
		for (IContentItem city : contentList) {
			String firstName = city.getPinyin().substring(0, 1).toUpperCase();// 第一个字拼音的第一个字母
			if (firstName.matches(FORMAT)) {
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
	
	
	@Override
	public void setContentHeaderViewHandler(ContentHeaderViewHandler handler,int contentHeaderViewLayoutId) {
		this.mContentHeaderViewHandler = handler;
		this.ContentHeaderViewLaoyoutId = contentHeaderViewLayoutId;
	}
	
	/**
	 * 处理内容列表listview的headerview数据展示
	 */
	private void handleHeaderView() {
		if(mContentHeaderViewHandler!=null) {
			mContentHeaderViewHandler.initContentHeaderView();
		}
	}
	
	/**
	 * 设置导航条内容
	 */
	@Override
	public void setSliderData(String[] items) {
		mSliderView.setDataChanged(items);
	}
		
	/**
	 * 查询结果列表适配器
	 * @author hezd
	 *
	 */
	private class SearchResultAdapter extends BaseAdapter {
		// 上下文对象
		private Context mContext;
		// 查询结果
		private List<IContentItem> mResults;
		
		/**
		 * 构造函数
		 * @param context 上下文对象
		 */
		public SearchResultAdapter(Context context) {
			this.mContext = context;
		}
		
		public void setData(List<IContentItem> results) {
			this.mResults = results;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return mResults.size();
		}

		@Override
		public Object getItem(int position) {
			return mResults.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder viewHolder = null;
			if(convertView!=null) {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}else {
				viewHolder = new ViewHolder();
				view = View.inflate(mContext, R.layout.search_result_item, null);
				viewHolder.contentText = (TextView) view.findViewById(R.id.tv_content);
				view.setTag(viewHolder);
			}
			String content = mResults.get(position).getName();
			viewHolder.contentText.setText(content);
			return view;
		}
		
		class ViewHolder {
			TextView contentText;
		}
		
	}
	
	public int getHeaderViews() {
		return mContentListView.getHeaderViewsCount();
	}

	@Override
	public void hideSearchEditText() {
		if(msearchEditLayout.getVisibility()==View.VISIBLE)
			msearchEditLayout.setVisibility(View.GONE);
	}

	public boolean isShowPop() {
		return isShowPop;
	}

	public void setShowPop(boolean isShowPop) {
		this.isShowPop = isShowPop;
		mSliderView.setShowPop(this.isShowPop);
	}

}
