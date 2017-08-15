package com.savor.savorphone.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.PictureSetAdapter;
import com.savor.savorphone.adapter.PictureSetAdapter.PageOnClickListener;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.PictureSetBean;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.interfaces.CopyCallBack;
import com.savor.savorphone.utils.ActivitiesManager;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.ScreenOrientationUtil;
import com.savor.savorphone.utils.ShareManager;
import com.savor.savorphone.widget.ProgressBarView;
import com.savor.savorphone.widget.ProgressBarView.ProgressBarViewClickListener;
import com.savor.savorphone.widget.SolveViewPager;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * 图集类
 * Created by bichao on 2017/7/6.
 */

public class ImageCollectionActivity extends BaseFragmentActivity implements View.OnClickListener{

    private ImageView back;
    private ImageView toleft_iv_right;
    private ImageView share;
    private ShareManager mShareManager;
    private ShareManager.CustomShareListener mShareListener;
    private ViewPager mViewPager;
    private String content_id;
    private CommonListItem voditem;
    //收藏状态,1:收藏，0:取消收藏
    private String state="0";
    private List<PictureSetBean> pictureSetBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot_main);
        mContext = this;
        getIntentData();
        getViews();
        setViews();
        setListeners();
        getDataFromServer();

    }

    @Override
    public void getViews() {
        back = (ImageView) findViewById(R.id.back);
        toleft_iv_right = (ImageView) findViewById(R.id.toleft_iv_right);
        share = (ImageView) findViewById(R.id.share);
        mViewPager = (ViewPager) findViewById(R.id.pager);
    }

    @Override
    public void setViews() {
        mShareManager = ShareManager.getInstance();
        mShareListener = new ShareManager.CustomShareListener(ImageCollectionActivity.this);
    }

    @Override
    public void setListeners() {
        toleft_iv_right.setOnClickListener(this);
        back.setOnClickListener(this);
        share.setOnClickListener(this);
    }

    private void getIntentData(){
        content_id = getIntent().getStringExtra("content_id");
        voditem = (CommonListItem) getIntent().getSerializableExtra("voditem");
        voditem.setArtid(content_id);
    }
    @Override
    public void onClick(View v) {

    }
    private void getDataFromServer(){
        AppApi.isCollection(mContext,this,content_id);
        AppApi.getPictureSet(mContext,this,content_id);
        getData();
    }

    private void getData(){
        AppApi.getRecommendInfo(this,voditem.getArtid(),this);
        //AppApi.getRecommendInfo(this,"2702",this);
    }
    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method){
            case POST_PICTURE_SET_JSON:
              //  mProgressLayout.loadSuccess();
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
                    state = str;
                    if ("1".equals(state)){
                        toleft_iv_right.setBackgroundResource(R.drawable.yishoucang3x);
                    }else{
                        toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
                    }
                }
                break;
            case GET_ADD_MY_COLLECTION_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
                    if ("success".equals(str)){
                        if ("0".equals(state)){
                            state = "1";
                            toleft_iv_right.setBackgroundResource(R.drawable.yishoucang3x);
                            ShowMessage.showToast(ImageCollectionActivity.this,"收藏成功");
                        }else{
                            state = "0";
                            toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
                            ShowMessage.showToast(ImageCollectionActivity.this,"取消收藏");
                        }
                    }
                    toleft_iv_right.setOnClickListener(ImageCollectionActivity.this);
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


    private void handlePictureSet(){
        if (pictureSetBeanList==null||pictureSetBeanList.size()==0){
            return;
        }

    }
}
