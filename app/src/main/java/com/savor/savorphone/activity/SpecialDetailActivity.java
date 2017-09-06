package com.savor.savorphone.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.ShowMessage;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.SpecialDetailItemAdapter;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.SpecialDetail;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.interfaces.CopyCallBack;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.SavorAnimUtil;
import com.savor.savorphone.utils.SavorCacheUtil;
import com.savor.savorphone.utils.ShareManager;
import com.savor.savorphone.widget.ProgressBarView;

import java.util.List;

/**
 * 专题组详情
 */
public class SpecialDetailActivity extends BaseActivity implements ProgressBarView.ProgressBarViewClickListener, PullToRefreshBase.OnRefreshListener, SpecialDetailItemAdapter.OnSpecialItemClickListener, View.OnClickListener, CopyCallBack {
    public static final float IMAGE_SCALE = 484 / 750f;
    private TextView refreshDataHintTV;
    private ScrollView mScrollView;
    private ProgressBarView mLoadingPb;
    private ImageView mSpeicalIv;
    private TextView mSpecialTitleTv;
    private TextView mSpecialDesTv;
    private SpecialDetailItemAdapter mSpecialDetailItemAdapter;
    private Handler handler = new Handler();
    private SpecialDetail specialDetail;
    private String mSpecialId;
    private ImageView mBackBtn;
    private ImageView mShareBtn;
    private PullToRefreshListView mRefreshListView;
    private View mHeaderView;
    private LinearLayout mShareLayout;

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
        if (mSpecialDetailItemAdapter.getCount() == 0) {
            mLoadingPb.startLoading();
        }
        AppApi.getSpecialDetail(mContext, this, mSpecialId);
    }

    @Override
    public void getViews() {
        mShareLayout = (LinearLayout) findViewById(R.id.ll_share);
        mShareBtn = (ImageView) findViewById(R.id.iv_right);
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        refreshDataHintTV = (TextView) findViewById(R.id.tv_refresh_data_hint);
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.pts_special_detail);
        mLoadingPb = (ProgressBarView) findViewById(R.id.pbv_loading);

        initHeaderView();
    }

    private void initHeaderView() {
        mHeaderView = View.inflate(mContext, R.layout.include_special_detail,null);
        mSpeicalIv = (ImageView) mHeaderView.findViewById(R.id.iv_special_pic);
        mSpecialTitleTv = (TextView) mHeaderView.findViewById(R.id.tv_special_title);
        mSpecialDesTv = (TextView) mHeaderView.findViewById(R.id.tv_special_desc);

    }

    @Override
    public void setViews() {
        int screenWidth = DensityUtil.getScreenWidth(mContext);
        float height = screenWidth * IMAGE_SCALE;
        ViewGroup.LayoutParams layoutParams = mSpeicalIv.getLayoutParams();
        layoutParams.height = (int) height;

        mRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mSpecialDetailItemAdapter = new SpecialDetailItemAdapter(mContext);
        mRefreshListView.setAdapter(mSpecialDetailItemAdapter);
        mRefreshListView.getRefreshableView().addHeaderView(mHeaderView);

        mShareBtn.setVisibility(View.VISIBLE);
        mShareBtn.setImageResource(R.drawable.fenxiang3x);
//        mShareBtn.setOnClickListener(this);
        int padding = DensityUtil.dip2px(this,10);
        mShareBtn.setPadding(padding,padding,padding,padding);
    }

    @Override
    public void setListeners() {
        mShareLayout.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mLoadingPb.setProgressBarViewClickListener(this);
        mSpecialDetailItemAdapter.setOnSpecialItemClickListener(this);
    }

    private void initSpecialDetailViews(SpecialDetail specialDetail) {
        String img_url = specialDetail.getImg_url();
        initHeaderView(specialDetail, img_url);

        List<SpecialDetail.SpecialDetailTypeBean> list = specialDetail.getList();
        if (list != null && list.size() > 0) {
            mSpecialDetailItemAdapter.setData(list);
        }

    }

    private void initHeaderView(SpecialDetail specialDetail, String img_url) {
        Glide.with(mContext)
                .load(img_url)
                .placeholder(R.drawable.kong_mrjz)
                .dontAnimate().into(mSpeicalIv);

        String title = specialDetail.getTitle();
        mSpecialTitleTv.setText(title);

        String desc = specialDetail.getDesc();
        mSpecialDesTv.setText(desc);

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
    public void onSpecialItemClick(int viewType, SpecialDetail.SpecialDetailTypeBean bean) {
        if(viewType == SpecialDetailItemAdapter.TYPE_IMAGE_TEXT) {
            CommonListItem item = new CommonListItem();
            item.setArtid(bean.getArtid());
            item.setImageURL(bean.getImageURL());
            item.setContentURL(bean.getContentURL());
            item.setAcreateTime(bean.getCreateTime());
            item.setId(bean.getArtid());
            item.setType(bean.getType());
            if(item!=null) {
                int type = Integer.valueOf(item.getType());
                switch (type){
                    case 0:
                    case 1:
                        item.setCategoryId(item.getCategoryId());
                        Intent intent = new Intent(mContext, ImageTextActivity.class);
                        intent.putExtra("item",item);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(mContext, PictureSetActivity.class);
                        intent.putExtra("voditem",item);
                        intent.putExtra("content_id",item.getArtid());
                        intent.putExtra("category_id",item.getCategoryId());
                        startActivity(intent);
                        break;
                    case 3:
                    case 4:
                        VideoPlayVODNotHotelActivity.startVODVideoActivity(this,item);
                        break;
                }
            }
        }
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
            case R.id.ll_share:
                if(specialDetail!=null) {
                    ShareManager.getInstance().setCategory_id("1");
                    ShareManager.getInstance().setContent_id(specialDetail.getId());
                    String title = "小热点| "+specialDetail.getTitle();
                    String text = "小热点| "+specialDetail.getTitle();
                    ShareManager.getInstance().share(this,text,title,specialDetail.getImg_url(),ConstantValues.addH5ShareParams(specialDetail.getContentUrl()),this);
                }
                break;
            case R.id.iv_left:
                finish();
                break;
        }
    }

    @Override
    public void copyLink() {
        ClipboardManager cmb = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(ConstantValues.addH5ShareParams(specialDetail.getContentUrl()));
        ShowMessage.showToast(mContext,"复制完毕");
    }
}
