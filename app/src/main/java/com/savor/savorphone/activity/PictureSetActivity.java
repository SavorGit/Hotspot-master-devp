package com.savor.savorphone.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.api.utils.AppUtils;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.PictureSetAdapter;
import com.savor.savorphone.adapter.PictureSetAdapter.PageOnClickListener;
import com.savor.savorphone.adapter.RecommendListAdapter;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.PictureSetBean;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.interfaces.CopyCallBack;
import com.savor.savorphone.interfaces.SetRecommend;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.ScreenOrientationUtil;
import com.savor.savorphone.utils.ShareManager;
import com.savor.savorphone.widget.AlignTextView;
import com.savor.savorphone.widget.ProgressBarView;
import com.savor.savorphone.widget.ProgressBarView.ProgressBarViewClickListener;
import com.savor.savorphone.widget.SolveViewPager;
import com.umeng.socialize.UMShareAPI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 图集类
 * Created by bichao on 2017/7/6.
 */

public class PictureSetActivity extends BaseActivity implements ApiRequestListener,
        View.OnClickListener,PageOnClickListener,ProgressBarViewClickListener,CopyCallBack,SetRecommend {
    private Context context;
    private LinearLayout headLayout;
    private final int PICK_CITY = 1;
    private ImageView iv_left;
    private ImageView iv_right;
    private ImageView toleft_iv_right;
    private SolveViewPager photoViewpager;
    private LinearLayout bottomTextLayout;
    private RelativeLayout pageNumLayout;
    private RelativeLayout main_al;
    private TextView bottomPageNumberTV;
    private TextView bottomPageTotalTV;
    private TextView describeTV;
    private int screenState;//横竖屏状态，0：竖屏，1：横屏
    private PictureSetAdapter pictureSetAdapter;
    private List<PictureSetBean> pictureSetBeanList = new ArrayList<>();
    private CommonListItem voditem;
    public List<CommonListItem> rList = new ArrayList<CommonListItem>();
    private int mCurrentItem;
    //文章ID
    private String content_id;
    //收藏状态,1:收藏，0:取消收藏
    private String state="0";
    Handler handler = new Handler();
    private ShareManager shareManager;
    private ProgressBarView mProgressLayout;
    private GestureDetector mGestureDetector;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case PICK_CITY:
                    // 跳转选择城市
                    if (rList != null && rList.size()>0) {
                        Intent pickIntent = new Intent(mContext, PicRecommendActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("rList", (Serializable) rList);
                        pickIntent.putExtras(bundle);
                        pickIntent.putExtra("voditem",voditem);
                        Intent intent = getIntent();
                        if(intent!=null&&("application/pdf").equals(intent.getType())) {
                            Uri data = getIntent().getData();
                            pickIntent.setDataAndType(data,intent.getType());
                            pickIntent.setData(data);
                        }

                        startActivity(pickIntent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_in_left);
                    }
                   // 这部分代码是切换Activity时的动画，看起来就不会很生硬
                    //finish();
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_set);
        context = this;
        getIntentData();
        getViews();
        setViews();
        setListeners();
        getDataFromServer();
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // if (Math.abs(e1.getRawX() - e2.getRawX()) > 250) {
                // // System.out.println("水平方向移动距离过大");
                // return true;
                // }
                if (Math.abs(velocityY) < 180|| Math.abs(velocityX)<180) {
                    // System.out.println("手指移动的太慢了");
                    return true;
                }

                // 手势向下 down
                if ((e2.getRawY() - e1.getRawY()) >450) {
                    Animation animation = AnimationUtils.loadAnimation(PictureSetActivity.this, R.anim.slide_up_out);
                    animation.setAnimationListener(new PictureSetActivity.AnimationImp() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            super.onAnimationEnd(animation);
                            main_al.setVisibility(View.GONE);
                            finish();
                        }
                    });
                    main_al.startAnimation(animation);

                    return true;
                }
                // 手势向上 up
                if ((e1.getRawY() - e2.getRawY()) > 450) {
                    Animation animation1 = AnimationUtils.loadAnimation(PictureSetActivity.this, R.anim.slide_down_out);
                    animation1.setAnimationListener(new PictureSetActivity.AnimationImp() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            super.onAnimationEnd(animation);
                            main_al.setVisibility(View.GONE);
                            finish();
                        }
                    });
                    main_al.startAnimation(animation1);

                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }
    private void getIntentData(){
        content_id = getIntent().getStringExtra("content_id");
        voditem = (CommonListItem) getIntent().getSerializableExtra("voditem");
    }
    private void getData(){
        AppApi.getRecommendInfo(this,voditem.getArtid(),this);
        //AppApi.getRecommendInfo(this,"2702",this);
    }
    @Override
    public void getViews() {
        headLayout = (LinearLayout) findViewById(R.id.head_layout);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        toleft_iv_right = (ImageView) findViewById(R.id.toleft_iv_right);
        photoViewpager = (SolveViewPager) findViewById(R.id.photoViewpager);
        bottomTextLayout = (LinearLayout) findViewById(R.id.bottomTextLayout);
        pageNumLayout = (RelativeLayout) findViewById(R.id.page_num_layout);
        bottomPageNumberTV = (TextView) findViewById(R.id.bottomPageNumber);
        bottomPageTotalTV = (TextView) findViewById(R.id.pageNumberTotal);
        describeTV = (TextView) findViewById(R.id.describe);
        main_al = (RelativeLayout) findViewById(R.id.main_al);
        mProgressLayout = (ProgressBarView) findViewById(R.id.pbv_loading);

    }

    @Override
    public void setViews() {
        iv_right.setVisibility(View.VISIBLE);
        toleft_iv_right.setVisibility(View.VISIBLE);
        iv_right.setImageResource(R.drawable.fenxiang3x);


        pictureSetAdapter = new PictureSetAdapter(context,pictureSetBeanList,this,photoViewpager);
        photoViewpager.setAdapter(pictureSetAdapter);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        { // 竖屏
            screenState = 0;
        } else {
            // 横屏
            screenState = 1;
        }

        shareManager = ShareManager.getInstance();
        photoViewpager.setRecommend(this);
    }

    @Override
    public void setListeners() {
        headLayout.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        toleft_iv_right.setOnClickListener(this);
        photoViewpager.setOnPageChangeListener(onPageChangeListener);
        mProgressLayout.setProgressBarViewClickListener(this);
    }

    /**
     * 获取图集数据
     */
    private void getDataFromServer(){
        mProgressLayout.startLoading();
        AppApi.isCollection(mContext,this,content_id);
        AppApi.getPictureSet(mContext,this,content_id);
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                    finish();
                break;
            case R.id.iv_right:
                shareMethod();
                break;
            case R.id.toleft_iv_right:
                toleft_iv_right.setOnClickListener(null);
                handleCollection();
                break;
        }
    }

    /**
     * 分享方法
     */
    private void shareMethod(){
        if (!AppUtils.isNetworkAvailable(this)) {
            ShowMessage.showToastSavor(this, getString(R.string.bad_wifi));
            return;
        }
        String title = "小热点| "+voditem.getTitle();
        String text = "小热点| "+voditem.getTitle();
        String imageURL = voditem.getImageURL();
        String contentURL = voditem.getContentURL();
        shareManager.setCategory_id("0");
        shareManager.setContent_id(voditem.getArtid()+"");
        shareManager.share(this,text,title,imageURL, ConstantValues.addH5ShareParams(contentURL),this);

    }

    private void handleCollection(){
        if ("1".equals(state)){
            AppApi.handleCollection(mContext,this,content_id,"0");
        }else{
            AppApi.handleCollection(mContext,this,content_id,"1");
        }
    }

    OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(final int position) {
            mCurrentItem = position;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottomPageNumberTV.setText((position+1)+"");
                    bottomPageTotalTV.setText(pictureSetBeanList.size()+"");
                    describeTV.setText(pictureSetBeanList.get(position).getAtext());
                }
            },500);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void handleBottomText(int position) {
        if (bottomTextLayout.getVisibility()== View.VISIBLE){
            ObjectAnimator animator = ObjectAnimator.ofFloat(bottomTextLayout, "alpha", 1f, 0f);
            animator.setDuration(0);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (screenState == 0) {
                        RecordUtils.onEvent(PictureSetActivity.this,getString(R.string.page_pic_vertical_hide));
                    }else if (screenState == 1) {

                        RecordUtils.onEvent(PictureSetActivity.this,getString(R.string.page_pic_landscape_hide));
                    }
                    bottomTextLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            animator.start();
        }else{
            bottomTextLayout.setVisibility(View.VISIBLE);
            if (screenState == 0) {
                RecordUtils.onEvent(PictureSetActivity.this,getString(R.string.page_pic_vertical_show));
            }else if (screenState == 1) {
                RecordUtils.onEvent(PictureSetActivity.this,getString(R.string.page_pic_landscape_show));
            }
            ObjectAnimator animator = ObjectAnimator.ofFloat(bottomTextLayout, "alpha", 0f, 1f);
            animator.setDuration(0);
            animator.start();

        }
        if (headLayout.getVisibility()==View.VISIBLE){
            ObjectAnimator animator = ObjectAnimator.ofFloat(headLayout, "alpha", 1f, 0f);
            animator.setDuration(0);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    headLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }else{
            headLayout.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(headLayout, "alpha", 0f, 1f);
            animator.setDuration(0);
            animator.start();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        { // 竖屏
            screenState = 0;
            headLayout.setBackgroundColor(getResources().getColor(R.color.color_main));
            RecordUtils.onEvent(PictureSetActivity.this,getString(R.string.page_pic_landscape_rotate));
        } else {
            // 横屏
            screenState = 1;
            headLayout.setBackgroundResource(R.drawable.quanpingmc);
            RecordUtils.onEvent(PictureSetActivity.this,getString(R.string.page_pic_vertical_rotate));
        }

        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method){
            case POST_PICTURE_SET_JSON:
                mProgressLayout.loadSuccess();
                if (obj instanceof List<?>){
                    pictureSetBeanList = (List<PictureSetBean>)obj;
                    handlePictureSet();
                    ScreenOrientationUtil.getInstance().start(this);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                    iv_right.setVisibility(View.VISIBLE);
                    toleft_iv_right.setVisibility(View.VISIBLE);
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
                            ShowMessage.showToast(PictureSetActivity.this,"收藏成功");
                        }else{
                            state = "0";
                            toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
                            ShowMessage.showToast(PictureSetActivity.this,"取消收藏");
                        }
                    }
                    toleft_iv_right.setOnClickListener(PictureSetActivity.this);
                }
                break;
            case POST_RECOMMEND_LIST_JSON:
                if (obj instanceof List<?>){
                    List<CommonListItem> mList = (List<CommonListItem>) obj;
                    setRecommendData(mList);
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
                        mProgressLayout.loadFailure("该内容找不到了~","",R.drawable.kong_wenzhang);
                        iv_right.setVisibility(View.GONE);
                        toleft_iv_right.setVisibility(View.GONE);
                    }else{
                        mProgressLayout.loadFailure();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ScreenOrientationUtil.getInstance().stop();
                    iv_right.setVisibility(View.GONE);
                    toleft_iv_right.setVisibility(View.GONE);
                }

                break;
            case GET_ADD_MY_COLLECTION_JSON:
                toleft_iv_right.setOnClickListener(PictureSetActivity.this);
                if (obj instanceof ResponseErrorMessage){
                    ResponseErrorMessage errorMessage = (ResponseErrorMessage)obj;
                    if (!TextUtils.isEmpty(errorMessage.getMessage())){
                        ShowMessage.showToast(mContext,errorMessage.getMessage());
                    }
                }
                break;
        }
    }


    private void setRecommendData(List<CommonListItem> mList){
        if (mList != null && mList.size()>0) {
            if (rList != null && rList.size()>0) {
                rList.clear();
                rList = mList;
            }else {
                rList = mList;
            }

        }


    }
    private void handlePictureSet(){
        if (pictureSetBeanList==null||pictureSetBeanList.size()==0){
            return;
        }
        if (pictureSetAdapter!=null){
            pictureSetAdapter.setData(pictureSetBeanList);
        }
        pageNumLayout.setVisibility(View.VISIBLE);
        bottomPageNumberTV.setText(1+"");
        bottomPageTotalTV.setText(pictureSetBeanList.size()+"");
        describeTV.setText(pictureSetBeanList.get(0).getAtext());
    }

    @Override
    public void loadDataEmpty() {

    }

    @Override
    public void loadFailureNoNet() {
        getDataFromServer();
    }

    @Override
    public void loadFailure() {
        getDataFromServer();
    }

    @Override
    public void copyLink() {
        ClipboardManager cmb = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(voditem.getContentURL());
        ShowMessage.showToast(mContext,"复制完毕");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shareManager != null) {
            shareManager.CloseDialog ();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
      //  Toast.makeText(PictureSetActivity.this, "touch", Toast.LENGTH_SHORT).show();
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void setData(float startX, float endX) {
        WindowManager windowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        // 获取屏幕的宽度
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        int width = size.x;
        // 首先要确定的是，是否到了最后一页，然后判断是否向左滑动，并且滑动距离是否符合，我这里的判断距离是屏幕宽度的4分之一（这里可以适当控制）
        if (mCurrentItem == (pictureSetBeanList.size() - 1)) {
           // && endX - startX >= (width / 6)
            mHandler.sendEmptyMessage(PICK_CITY);// 进入主页
        }
    }


    private class AnimationImp implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }
}
