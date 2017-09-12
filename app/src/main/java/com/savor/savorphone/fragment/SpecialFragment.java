package com.savor.savorphone.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.ShowMessage;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.activity.ImageTextActivity;
import com.savor.savorphone.activity.PictureSetActivity;
import com.savor.savorphone.activity.SpecialListActivity;
import com.savor.savorphone.activity.SubjectActivity;
import com.savor.savorphone.activity.VideoPlayVODNotHotelActivity;
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
 * 专题组详情页
 */
public class SpecialFragment extends BaseFragment implements View.OnClickListener, ProgressBarView.ProgressBarViewClickListener ,PullToRefreshBase.OnRefreshListener, SpecialDetailItemAdapter.OnSpecialItemClickListener, CopyCallBack {
    public static final float IMAGE_SCALE = 484/750f;
    private Context mContext;
    private ImageView mSpeicalIv;
    private TextView mSpecialTitleTv;
    private TextView mSpecialDesTv;
//    private RecyclerView mSpecialListView;
    private SpecialDetailItemAdapter mSpecialDetailItemAdapter;
    private TextView mSpecialListTv;
    private ProgressBarView mLoadingPb;
    private PullToRefreshListView mRefreshListView;
    /**是否有专题组详情缓存数据*/
    private boolean isHasCache;
    private TextView refreshDataHintTV;

    private Handler handler = new Handler();
    private SpecialDetail specialDetail;
    private View mHeaderView;
    private View mFooterView;
    private ShareManager mShareManager;

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
        refreshDataHintTV = (TextView) view.findViewById(R.id.tv_refresh_data_hint);
        mRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pts_special_detail);
        mLoadingPb = (ProgressBarView) view.findViewById(R.id.pbv_loading);

        initHeaderView();
        initFooterView();

        mShareManager = ShareManager.getInstance();
//        mSpecialListView = (RecyclerView) view.findViewById(R.id.rlv_special_item);
    }

    private void initFooterView() {
        mFooterView = View.inflate(mContext, R.layout.footer_view_special_detail,null);
        mSpecialListTv = (TextView) mFooterView.findViewById(R.id.tv_look_special_list);
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


        mSpecialDetailItemAdapter = new SpecialDetailItemAdapter(mContext);
        mRefreshListView.setAdapter(mSpecialDetailItemAdapter);
        mRefreshListView.getRefreshableView().addHeaderView(mHeaderView);
        mRefreshListView.getRefreshableView().addFooterView(mFooterView);
//        mSpecialListView.setAdapter(mSpecialDetailItemAdapter);

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
        mRefreshListView.setOnRefreshListener(this);
        mLoadingPb.setProgressBarViewClickListener(this);
        mSpecialListTv.setOnClickListener(this);
        mSpecialDetailItemAdapter.setOnSpecialItemClickListener(this);
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

    private void showRefreshHintAnimation(final String hint) {
        refreshDataHintTV.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshDataHintTV.setText(hint);
                SavorAnimUtil.startListRefreshHintAnimation(refreshDataHintTV);
            }
        },500);
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_SPECIAL_DETAIL_JSON:
                setPtrSuccessComplete();
                mLoadingPb.loadSuccess();
                if(obj instanceof SpecialDetail) {
                    specialDetail = (SpecialDetail) obj;
                    initSpecialDetailViews(specialDetail);
                    if(isAdded()){
                        showRefreshHintAnimation(getString(R.string.refresh_success));
                    }
                    SavorCacheUtil.getInstance().cacheSpecialDetail(mContext, specialDetail);
                }
                break;
        }
    }

    private void initSpecialDetailViews(SpecialDetail specialDetail) {
        initHeaderView(specialDetail);

        List<SpecialDetail.SpecialDetailTypeBean> list = specialDetail.getList();
        if(list!=null&&list.size()>0) {
            mSpecialDetailItemAdapter.setData(list);
        }
    }

    private void initHeaderView(SpecialDetail specialDetail) {
        String img_url = specialDetail.getImg_url();
        Glide.with(mContext)
                .load(img_url)
                .placeholder(R.drawable.kong_mrjz)
                .dontAnimate().into(mSpeicalIv);

        String title = specialDetail.getTitle();
        mSpecialTitleTv.setText(title);

        String desc = specialDetail.getDesc();
        mSpecialDesTv.setText(desc);
    }

    private Runnable ptrRunnable = new Runnable() {

        @Override
        public void run() {
            mRefreshListView.onRefreshComplete();
        }
    };

    private void setPtrSuccessComplete() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(ptrRunnable, 1000);
    }

    @Override
    public void onError(AppApi.Action method, Object statusCode) {
        switch (method) {
            case POST_SPECIAL_DETAIL_JSON:
                mRefreshListView.onRefreshComplete();
                SpecialDetail specialDetail = SavorCacheUtil.getInstance().getSpecialDetail(mContext);
                if(specialDetail == null) {
                    mLoadingPb.loadFailure();
                }
                if(isAdded()){
                    if (statusCode instanceof ResponseErrorMessage){
                        ResponseErrorMessage errorMessage = (ResponseErrorMessage)statusCode;
                        String code = String.valueOf(errorMessage.getCode());
                        if (AppApi.ERROR_TIMEOUT.equals(code)){
                            showRefreshHintAnimation("数据加载超时");
                        }else if (AppApi.ERROR_NETWORK_FAILED.equals(code)){
                            showRefreshHintAnimation("无法连接到网络,请检查网络设置");
                        }
                    }

                }
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        SpecialDetail specialDetail = SavorCacheUtil.getInstance().getSpecialDetail(mContext);
        isHasCache = specialDetail != null;
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
            item.setVideoURL(bean.getVideoURL());
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
                        VideoPlayVODNotHotelActivity.startVODVideoActivity(getActivity(),item);
                        break;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mShareManager != null) {
            mShareManager.CloseDialog ();
        }
    }

    public void share() {
        if(isAdded()) {
            if(specialDetail!=null) {
                mShareManager.setCategory_id("1");
                mShareManager.setContent_id(specialDetail.getId());
                String title = "小热点- "+specialDetail.getTitle();
                String text = specialDetail.getDesc();
                mShareManager.share(getActivity(),text,title,specialDetail.getImg_url(),ConstantValues.addH5ShareParams(specialDetail.getContentUrl()),this);
            }else {
                ShowMessage.showToast(getActivity(),"无法获取专题组信息");
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void copyLink() {
        ClipboardManager cmb = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(ConstantValues.addH5ShareParams(specialDetail.getContentUrl()));
        ShowMessage.showToast(mContext,"复制完毕");
    }
}
