package com.savor.savorphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.DensityUtil;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.SpecialDetailItemAdapter;
import com.savor.savorphone.bean.SpecialDetail;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.utils.SavorAnimUtil;
import com.savor.savorphone.utils.SavorCacheUtil;
import com.savor.savorphone.widget.ProgressBarView;

import java.util.List;

/**
 * 专题组详情
 */
public class SpecialDetailActivity extends BaseActivity implements ProgressBarView.ProgressBarViewClickListener, PullToRefreshBase.OnRefreshListener, SpecialDetailItemAdapter.OnSpecialItemClickListener, View.OnClickListener {
    public static final float IMAGE_SCALE = 484 / 750f;
    private TextView refreshDataHintTV;
    private ScrollView mScrollView;
    private ProgressBarView mLoadingPb;
    private ImageView mSpeicalIv;
    private TextView mSpecialTitleTv;
    private TextView mSpecialDesTv;
    private RecyclerView mSpecialListView;
    private SpecialDetailItemAdapter mSpecialDetailItemAdapter;
    private Handler handler = new Handler();
    private SpecialDetail specialDetail;
    private String mSpecialId;
    private ImageView mBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_detail);
        handleIntent();
        getViews();
        setViews();
        setListeners();
        getData();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        mSpecialId = intent.getStringExtra("id");
    }

    private void getData() {
        if (mSpecialListView.getChildCount() == 0) {
            mLoadingPb.startLoading();
        }
        AppApi.getSpecialDetail(mContext, this, mSpecialId);
    }

    @Override
    public void getViews() {
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        refreshDataHintTV = (TextView) findViewById(R.id.tv_refresh_data_hint);
        mScrollView = (ScrollView) findViewById(R.id.sv_content);
        mLoadingPb = (ProgressBarView) findViewById(R.id.pbv_loading);
        mSpeicalIv = (ImageView) findViewById(R.id.iv_special_pic);
        mSpecialTitleTv = (TextView) findViewById(R.id.tv_special_title);
        mSpecialDesTv = (TextView) findViewById(R.id.tv_special_desc);
        mSpecialListView = (RecyclerView) findViewById(R.id.rlv_special_item);
    }

    @Override
    public void setViews() {
        int screenWidth = DensityUtil.getScreenWidth(mContext);
        float height = screenWidth * IMAGE_SCALE;
        ViewGroup.LayoutParams layoutParams = mSpeicalIv.getLayoutParams();
        layoutParams.height = (int) height;

        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mSpecialListView.setLayoutManager(manager);
        mSpecialDetailItemAdapter = new SpecialDetailItemAdapter(mContext);
        mSpecialListView.setAdapter(mSpecialDetailItemAdapter);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSpecialListView.getLayoutParams();
        params.setMargins(DensityUtil.dip2px(this,15),0,DensityUtil.dip2px(this,15),0);

    }

    @Override
    public void setListeners() {
        mBackBtn.setOnClickListener(this);
        mLoadingPb.setProgressBarViewClickListener(this);
        mSpecialDetailItemAdapter.setOnSpecialItemClickListener(this);
    }

    private void initSpecialDetailViews(SpecialDetail specialDetail) {
        String img_url = specialDetail.getImg_url();
        Glide.with(mContext)
                .load(img_url)
                .placeholder(R.drawable.kong_mrjz)
                .dontAnimate().into(mSpeicalIv);

        String title = specialDetail.getTitle();
        mSpecialTitleTv.setText(title);

        String desc = specialDetail.getDesc();
        mSpecialDesTv.setText(desc);

        List<SpecialDetail.SpecialDetailTypeBean> list = specialDetail.getList();
        if (list != null && list.size() > 0) {
            mSpecialDetailItemAdapter.setData(list);
        }
        mSpecialListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSpecialListView.setFocusable(false);
                mSpecialListView.setFocusableInTouchMode(false);
                mScrollView.requestFocus();
                mScrollView.setFocusable(true);
                mScrollView.scrollTo(0, 0);
            }
        }, 50);
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

    @Override
    public void onSpecialItemClick(int viewType, SpecialDetail.SpecialDetailTypeBean bean) {

    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_SPECIAL_DETAIL_JSON:
                mLoadingPb.loadSuccess();
                if (obj instanceof SpecialDetail) {
                    specialDetail = (SpecialDetail) obj;
                    initSpecialDetailViews(specialDetail);
                }
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object statusCode) {
        switch (method) {
            case POST_SPECIAL_DETAIL_JSON:
                mLoadingPb.loadFailure();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
        }
    }
}
