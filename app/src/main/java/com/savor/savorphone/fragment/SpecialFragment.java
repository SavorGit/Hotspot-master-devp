package com.savor.savorphone.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.DensityUtil;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshScrollView;
import com.savor.savorphone.R;
import com.savor.savorphone.activity.SpecialListActivity;
import com.savor.savorphone.adapter.SpecialDetailItemAdapter;
import com.savor.savorphone.bean.SpecialDetail;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.utils.SavorCacheUtil;
import com.savor.savorphone.widget.ProgressBarView;

import java.util.List;

/**
 * 专题组详情页
 */
public class SpecialFragment extends BaseFragment implements View.OnClickListener, ProgressBarView.ProgressBarViewClickListener ,PullToRefreshBase.OnRefreshListener{
    public static final float IMAGE_SCALE = 484/750f;
    private Context mContext;
    private static final String TAG = "SpecialFragment";
    private ImageView mSpeicalIv;
    private TextView mSpecialTitleTv;
    private TextView mSpecialDesTv;
    private RecyclerView mSpecialListView;
    private SpecialDetailItemAdapter mSpecialDetailItemAdapter;
    private TextView mSpecialListTv;
    private ProgressBarView mLoadingPb;
    private PullToRefreshScrollView mRefreshScrollView;
    /**是否有专题组详情缓存数据*/
    private boolean isHasCache;

    public SpecialFragment() {
    }

    public static SpecialFragment newInstance() {
        SpecialFragment fragment = new SpecialFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_special, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setViews();
        setListeners();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
    }

    private void getData() {
        if(!isHasCache) {
            mLoadingPb.startLoading();
        }
        AppApi.getSpecialDetail(mContext,this,"");
    }

    public void initViews(View view) {
        mRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pts_special_detail);
        mLoadingPb = (ProgressBarView) view.findViewById(R.id.pbv_loading);
        mSpecialListTv = (TextView) view.findViewById(R.id.tv_look_special_list);
        mSpeicalIv = (ImageView) view.findViewById(R.id.iv_special_pic);
        mSpecialTitleTv = (TextView) view.findViewById(R.id.tv_special_title);
        mSpecialDesTv = (TextView) view.findViewById(R.id.tv_special_desc);
        mSpecialListView = (RecyclerView) view.findViewById(R.id.rlv_special_item);
    }

    @Override
    public void setViews() {
        int screenWidth = DensityUtil.getScreenWidth(mContext);
        float height = screenWidth * IMAGE_SCALE;
        ViewGroup.LayoutParams layoutParams = mSpeicalIv.getLayoutParams();
        layoutParams.height = (int) height;

        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        mSpecialListView.setLayoutManager(manager);
        mSpecialDetailItemAdapter = new SpecialDetailItemAdapter(mContext);
        mSpecialListView.setAdapter(mSpecialDetailItemAdapter);

        SpecialDetail specialDetail = SavorCacheUtil.getInstance().getSpecialDetail(mContext);
        if(specialDetail!=null) {
            initSpecialDetailViews(specialDetail);
            isHasCache = true;
        }else {
            isHasCache = false;
        }
    }

    @Override
    public void setListeners() {
        mRefreshScrollView.setOnRefreshListener(this);
        mLoadingPb.setProgressBarViewClickListener(this);
        mSpecialListTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_look_special_list:
                Intent intent = new Intent(mContext,SpecialListActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void loadDataEmpty() {

    }

    @Override
    public void loadFailureNoNet() {

    }

    @Override
    public void loadFailure() {
        getData();
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_SPECIAL_DETAIL_JSON:
                mRefreshScrollView.onRefreshComplete();
                mLoadingPb.loadSuccess();
                if(obj instanceof SpecialDetail) {
                    SpecialDetail specialDetail = (SpecialDetail) obj;
                    initSpecialDetailViews(specialDetail);
                    SavorCacheUtil.getInstance().cacheSpecialDetail(mContext,specialDetail);
                }
                break;
        }
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
        if(list!=null&&list.size()>0) {
            mSpecialDetailItemAdapter.setData(list);
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
    public void onRefresh(PullToRefreshBase refreshView) {
        SpecialDetail specialDetail = SavorCacheUtil.getInstance().getSpecialDetail(mContext);
        isHasCache = specialDetail == null?false:true;
        getData();
    }
}
