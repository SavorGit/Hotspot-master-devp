package com.savor.savorphone.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.activity.ImageTextActivity;
import com.savor.savorphone.activity.PictureSetActivity;
import com.savor.savorphone.activity.VideoPlayVODNotHotelActivity;
import com.savor.savorphone.adapter.PictureSetAdapter;
import com.savor.savorphone.adapter.WealthLifeAdapter;
import com.savor.savorphone.bean.AliLogBean;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.CommonListResult;
import com.savor.savorphone.bean.PictureSetBean;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.interfaces.CopyCallBack;
import com.savor.savorphone.log.AliLogFileUtil;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.STIDUtil;
import com.savor.savorphone.utils.SavorAnimUtil;
import com.savor.savorphone.utils.SavorCacheUtil;
import com.savor.savorphone.widget.ProgressBarView;
import com.savor.savorphone.widget.ProgressBarView.ProgressBarViewClickListener;
import com.savor.savorphone.widget.SolveViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 创富生活fragment
 * Created by Administrator on 2017/7/4.
 */

public class PicSetFragment extends BaseFragment implements ApiRequestListener,
        View.OnClickListener,PictureSetAdapter.PageOnClickListener,ProgressBarViewClickListener,CopyCallBack {


    private Context context;
    private static final String TAG = "WealthLifeFragment";
    private SolveViewPager photoViewpager;
    private LinearLayout bottomTextLayout;
    private RelativeLayout pageNumLayout;
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
    private ProgressBarView mProgressLayout;
    Handler handler = new Handler();
    private LinearLayout headLayout;

    @Override
    public String getFragmentName() {
        return TAG;
    }
    public static PicSetFragment getInstance(int type) {
        PicSetFragment wealthLifeFragment = new PicSetFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        wealthLifeFragment.setArguments(bundle);
        return  wealthLifeFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wealth_life,container,false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        initViews(view);
        setViews();
    }

    private void initViews(View view){
        headLayout = (LinearLayout) view.findViewById(R.id.head_layout);
        photoViewpager = (SolveViewPager) view.findViewById(R.id.photoViewpager);
        bottomTextLayout = (LinearLayout)view.findViewById(R.id.bottomTextLayout);
        pageNumLayout = (RelativeLayout) view.findViewById(R.id.page_num_layout);
        bottomPageNumberTV = (TextView) view.findViewById(R.id.bottomPageNumber);
        bottomPageTotalTV = (TextView) view.findViewById(R.id.pageNumberTotal);
        describeTV = (TextView) view.findViewById(R.id.describe);
        mProgressLayout = (ProgressBarView) view.findViewById(R.id.pbv_loading);

    }

    @Override
    public void setViews() {
        pictureSetAdapter = new PictureSetAdapter(context,pictureSetBeanList,this,photoViewpager);
        photoViewpager.setAdapter(pictureSetAdapter);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        { // 竖屏
            screenState = 0;
        } else {
            // 横屏
            screenState = 1;
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initDisplay();
    }

    private void initDisplay(){
        getDataFromServer("0");
    }

    private void getDataFromServer(String sort_num){

    }

//    private void getIntentData(){
//        content_id = getIntent().getStringExtra("content_id");
//        voditem = (CommonListItem) getIntent().getSerializableExtra("voditem");
//        voditem.setArtid(content_id);
//    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
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
    public void onClick(View v) {

    }

    @Override
    public void copyLink() {

    }

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
                        RecordUtils.onEvent(context,getString(R.string.page_pic_vertical_hide));
                    }else if (screenState == 1) {

                        RecordUtils.onEvent(context,getString(R.string.page_pic_landscape_hide));
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
                RecordUtils.onEvent(context,getString(R.string.page_pic_vertical_show));
            }else if (screenState == 1) {
                RecordUtils.onEvent(context,getString(R.string.page_pic_landscape_show));
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

    }

    @Override
    public void loadFailure() {

    }
}
