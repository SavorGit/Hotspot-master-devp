package com.savor.savorphone.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.ImagePagerAdapter;
import com.savor.savorphone.adapter.PictureSetAdapter;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.PictureSetBean;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.interfaces.CopyCallBack;
import com.savor.savorphone.interfaces.SetRecommend;
import com.savor.savorphone.utils.ScreenOrientationUtil;
import com.savor.savorphone.widget.ProgressBarView;
import com.savor.savorphone.widget.imageshow.ImageShowViewPager;



public class ImageShowActivity extends BaseActivity implements ApiRequestListener,
        View.OnClickListener,PictureSetAdapter.PageOnClickListener,ProgressBarView.ProgressBarViewClickListener,CopyCallBack,SetRecommend {

	private ImageShowViewPager image_pager;
	private TextView page_number;

	private ImageView download;

	private ArrayList<String> imgsUrl;
	/** PagerAdapter */
	private ImagePagerAdapter mAdapter;

    private CommonListItem voditem;
    //文章ID
    private String content_id;
    private List<PictureSetBean> pictureSetBeanList = new ArrayList<>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_imageshow);
		initView();
        getIntentData();
		initViewPager();
        getDataFromServer();
	}

    private void getIntentData(){
        content_id = getIntent().getStringExtra("content_id");
        voditem = (CommonListItem) getIntent().getSerializableExtra("voditem");
        voditem.setArtid(content_id);
    }
//	private void initData() {
//		imgsUrl = getIntent().getStringArrayListExtra("infos");
//		page_number.setText("1" + "/" + imgsUrl.size());
//	}

    /**
     * 获取图集数据
     */
    private void getDataFromServer(){
        //mProgressLayout.startLoading();
        AppApi.isCollection(mContext,this,content_id);
        AppApi.getPictureSet(mContext,this,content_id);
        //getData();
    }
	private void initView() {
		image_pager = (ImageShowViewPager) findViewById(R.id.image_pager);
		page_number = (TextView) findViewById(R.id.page_number);
		download = (ImageView) findViewById(R.id.download);
		image_pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				page_number.setText((arg0 + 1) + "/" + pictureSetBeanList.size());
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void initViewPager() {
//		if (imgsUrl != null && imgsUrl.size() != 0) {
			mAdapter = new ImagePagerAdapter(getApplicationContext(), pictureSetBeanList);
			image_pager.setAdapter(mAdapter);
//		}
	}

	@Override
	public void getViews() {

	}

	@Override
	public void setViews() {

	}

	@Override
	public void setListeners() {

	}


    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method){
            case POST_PICTURE_SET_JSON:
               // mProgressLayout.loadSuccess();
                if (obj instanceof List<?>){
                    pictureSetBeanList = (List<PictureSetBean>)obj;
                    handlePictureSet();
                    ScreenOrientationUtil.getInstance().start(this);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
//                    iv_right.setVisibility(View.VISIBLE);
//                    toleft_iv_right.setVisibility(View.VISIBLE);
                }
                break;
            case GET_IS_COLLECTION_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
//                    state = str;
//                    if ("1".equals(state)){
//                        toleft_iv_right.setBackgroundResource(R.drawable.yishoucang3x);
//                    }else{
//                        toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
//                    }
                }
                break;
            case GET_ADD_MY_COLLECTION_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
//                    if ("success".equals(str)){
//                        if ("0".equals(state)){
//                            state = "1";
//                            toleft_iv_right.setBackgroundResource(R.drawable.yishoucang3x);
//                            ShowMessage.showToast(PictureSetActivity.this,"收藏成功");
//                        }else{
//                            state = "0";
//                            toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
//                            ShowMessage.showToast(PictureSetActivity.this,"取消收藏");
//                        }
//                    }
//                    toleft_iv_right.setOnClickListener(PictureSetActivity.this);
                }
                break;
            case POST_RECOMMEND_LIST_JSON:
                if (obj instanceof List<?>){
                    List<CommonListItem> mList = (List<CommonListItem>) obj;
                    //setRecommendData(mList);
                }

                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        switch (method){
            case POST_PICTURE_SET_JSON:
                if (obj instanceof ResponseErrorMessage){
                    ResponseErrorMessage errorMessage = (ResponseErrorMessage)obj;
                    if (errorMessage.getCode()==19002){
//                        mProgressLayout.loadFailure("该内容找不到了~","",R.drawable.kong_wenzhang);
//                        iv_right.setVisibility(View.GONE);
//                        toleft_iv_right.setVisibility(View.GONE);
                    }else{
                        //mProgressLayout.loadFailure();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ScreenOrientationUtil.getInstance().stop();
//                    iv_right.setVisibility(View.GONE);
//                    toleft_iv_right.setVisibility(View.GONE);
                }

                break;
            case GET_ADD_MY_COLLECTION_JSON:
                //toleft_iv_right.setOnClickListener(PictureSetActivity.this);
                if (obj instanceof ResponseErrorMessage){
                    ResponseErrorMessage errorMessage = (ResponseErrorMessage)obj;
                    if (!TextUtils.isEmpty(errorMessage.getMessage())){
                        ShowMessage.showToast(mContext,errorMessage.getMessage());
                    }
                }
                break;
        }
    }

    private void handlePictureSet(){
        if (pictureSetBeanList==null||pictureSetBeanList.size()==0){
            return;
        }
        if (mAdapter!=null){
            mAdapter.setData(pictureSetBeanList);
        }
        //pageNumLayout.setVisibility(View.VISIBLE);
        page_number.setText("1" + "/" + pictureSetBeanList.size());
        //bottomPageTotalTV.setText(pictureSetBeanList.size()+"");
        //describeTV.setText(pictureSetBeanList.get(0).getAtext());
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setData(float startX, float endX, float startY, float endY) {

    }

    @Override
    public void copyLink() {

    }

    @Override
    public void handleBottomText(int position) {

    }

    @Override
    public void loadDataEmpty() {

    }

    @Override
    public void loadFailureNoNet() {

    }

    @Override
    public void loadFailure() {

    }
}
