package com.savor.savorphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.VodListAdapter;
import com.savor.savorphone.bean.BaseProReqeust;
import com.savor.savorphone.bean.BaseProResponse;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.VodProResponse;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.utils.ActivitiesManager;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.LinkDialog;
import com.savor.savorphone.widget.ProgressBarView;

import java.util.HashMap;
import java.util.List;

/**
 * 点播投屏列表
 */
public class VodListActivity extends BaseProActivity implements View.OnClickListener ,PullToRefreshBase.OnRefreshListener, ProgressBarView.ProgressBarViewClickListener, VodListAdapter.OnPhoneClickListener, VodListAdapter.OnTvClickListener {

    private PullToRefreshListView mVodListView;
    private VodListAdapter mVodAdapter;
    private ImageView mBackBtn;
    private ProgressBarView mLoadingLayout;
    private LinkDialog mProDialog;
    private int force;
    private CommonListItem currentItem;
    public static final int FORCE_MSG = 104;
    private static final int ERROR_MSG = 105;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FORCE_MSG:
                    String user = (String) msg.obj;
                    showConfirm(user);
                    break;
                case ERROR_MSG:
                    String hint = (String) msg.obj;
                    showToast(hint);
                    break;
            }
        }
    };
    private CommonDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_list);

        getViews();
        setViews();
        setListeners();
        getDemmendList();
    }

    private void getDemmendList() {
        if(mVodAdapter.getCount()==0) {
            mLoadingLayout.startLoading();
        }
        int hotelid = mSession.getHotelid();
        AppApi.getDemandList(this,this,hotelid+"");
    }

    public void getViews() {
        mVodListView = (PullToRefreshListView) findViewById(R.id.ptl_vod_list);
        mLoadingLayout = (ProgressBarView) findViewById(R.id.pbv_loading);
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
    }

    @Override
    public void setViews() {
        mVodAdapter = new VodListAdapter(this);
        mVodListView.setAdapter(mVodAdapter);
    }

    @Override
    public void setListeners() {
        mBackBtn.setOnClickListener(this);
        mVodListView.setOnRefreshListener(this);
        mLoadingLayout.setProgressBarViewClickListener(this);
        mVodAdapter.setOnPhoneClickListener(this);
        mVodAdapter.setOnTvClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        mVodListView.onRefreshComplete();
        switch (method) {
            case POST_DEMAND_LIST_JSON:
                mLoadingLayout.loadSuccess();
                handleDemandList(obj);
                break;
            case GET_VOD_PRO_JSON:
                dismissProLoadingDialog();
                // 保存会话id
                if(obj instanceof BaseProResponse) {
                    VodProResponse response = (VodProResponse) obj;
                    String projectId = response.getProjectId();
                    ProjectionManager.getInstance().setProjectId(projectId);
                    ProjectionManager.getInstance().setVideoTVProjection(VideoPlayVODInHotelActivity.class,currentItem,true);
                    boolean isPlaying = ProjectionManager.getInstance().getVodPlayStatus();
                    Intent vodIntent = new Intent(this, VideoPlayVODInHotelActivity.class);
                    vodIntent.putExtra("voditem", currentItem);
                    vodIntent.putExtra("isPlaying", isPlaying);
                    vodIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(vodIntent);
                }
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_DEMAND_LIST_JSON:
                mLoadingLayout.loadFailure();
                break;
            case GET_VOD_PRO_JSON:
                dismissProLoadingDialog();
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    Message msg = Message.obtain();
                    if (code == 4) {
//                        mCurrentMediaInfo.setMobileUser(message.getMessage());
                        msg.what = FORCE_MSG;
                        msg.obj = message.getMessage();
                        mHandler.sendMessage(msg);
                    } else {
                        msg.what = ERROR_MSG;
                        msg.obj = message.getMessage();
                        mHandler.sendMessage(msg);
                    }
                    break;
                }
        }
    }

    private void handleDemandList(Object obj) {
        if(obj instanceof List<?>) {
            List<CommonListItem> mDemandList = (List<CommonListItem>) obj;
            mVodAdapter.setData(mDemandList);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        getDemmendList();
    }

    @Override
    public void loadDataEmpty() {

    }

    @Override
    public void loadFailureNoNet() {

    }

    @Override
    public void loadFailure() {
        getDemmendList();
    }

    @Override
    public void onPhoneClick(CommonListItem item) {
        VideoPlayVODNotHotelActivity.startVODVideoActivity(this,item);
    }

    @Override
    public void onTvClick(CommonListItem item) {
        currentItem = item;
        startDemandItemPro(item);
    }

    /**
     * 点播投屏
     */
    private void startDemandItemPro(CommonListItem item) {
        // 绑定则跳转内网点播
        Class projectionActivity = ProjectionManager.getInstance().getProjectionActivity();
        if(projectionActivity == SlidesActivity.class) {
            HotspotMainActivity mainActivity = (HotspotMainActivity) ActivitiesManager.getInstance().getSpecialActivity(HotspotMainActivity.class);
            mainActivity.stopSlide();
        }

        // 当点击连接电视按钮时，要清除掉mCurrentProItem对象，否则绑定成功后会自动进行投屏
        if(item!=null) {
            BaseProReqeust baseProReqeust = new BaseProReqeust();
            baseProReqeust.setVodType(1);
            baseProReqeust.setAssetname(item.getName());

            // 请求机顶盒投屏，如果成功跳转到播放页面，失败弹出提示接口返回错误信息
            showProLoadingDialog();
            AppApi.vodProection(this,mSession.getTVBoxUrl(), baseProReqeust,force,this);
        }
    }

    /**
     * 展示请求投屏弹窗
     * */
    private void showProLoadingDialog() {
        if(mProDialog==null) {
            mProDialog = new LinkDialog(this,"请求投屏...");
        }
        mProDialog.show();
    }

    private void dismissProLoadingDialog() {
        if(mProDialog!=null) {
            mProDialog.dismiss();
        }
    }

    /**
     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
     */
    private void showConfirm(final String user){
        String content = "当前"+user+"正在投屏,是否继续投屏?";
        dialog = new CommonDialog(this, content,
                new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        HashMap<String,String> params = new HashMap<>();
                        params.put(getString(R.string.to_screen_competition_hint),"ensure");
                        params.put("type","vod");
                        RecordUtils.onEvent(VodListActivity.this,getString(R.string.to_screen_competition_hint),params);
                        force = 1;
                        startDemandItemPro(currentItem);
                        dialog.cancel();
                    }
                }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                HashMap<String,String> params = new HashMap<>();
                params.put(getString(R.string.to_screen_competition_hint),"cancel");
                params.put("type","vod");
                RecordUtils.onEvent(VodListActivity.this,getString(R.string.to_screen_competition_hint),params);
                dialog.cancel();
            }
        },"继续投屏",true);
        dialog.show();
    }
}
