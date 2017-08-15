package com.savor.savorphone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.savor.savorphone.R;
import com.savor.savorphone.activity.HotspotMainActivity;
import com.savor.savorphone.activity.LocalVideoProAcitvity;
import com.savor.savorphone.activity.MediaGalleryActivity;
import com.savor.savorphone.activity.PdfListActivity;
import com.savor.savorphone.activity.PdfPreviewActivity;
import com.savor.savorphone.activity.SingleImageProActivity;
import com.savor.savorphone.activity.SlidesActivity;
import com.savor.savorphone.activity.VideoPlayVODInHotelActivity;
import com.savor.savorphone.activity.VideoPlayVODNotHotelActivity;
import com.savor.savorphone.adapter.CoverFlowAdapter;
import com.savor.savorphone.bean.BaseProReqeust;
import com.savor.savorphone.bean.BaseProResponse;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.bean.VodProResponse;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.LinkDialog;
import com.savor.savorphone.widget.ProgressBarView;
import com.savor.savorphone.widget.covorflow.CoverFlowView;

import java.util.HashMap;
import java.util.List;

import static com.savor.savorphone.activity.LinkTvActivity.EXRA_TV_BOX;
import static com.savor.savorphone.activity.LinkTvActivity.EXTRA_TV_INFO;

/**
 * Created by hezd on 2017/07/14.
 */

//投屏页面
public class ProjectionFragment extends BaseFragment implements View.OnClickListener, CoverFlowView.OnTvBtnCLickListener, CoverFlowView.OnPhoneBtnClickListener, ProgressBarView.ProgressBarViewClickListener {

    private CoverFlowView mCoverflowView;
    private TextView mHintTv;
    private TextView mLinkTv;
    /**当前按钮状态，点击退出投屏*/
    private static final int STATE_EXIT_PRO = 0x1;
    /**当前按钮状态，点击连接电视*/
    private static final int STATE_LINK_TV = 0x2;
    /**当前按钮状态，点击断开连接*/
    private static final int STATE_UNLINK_TV = 0x3;
    private int mCurrentState = STATE_LINK_TV;
    public static final int FORCE_MSG = 104;
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

    private ImageView mProGalleryIv;
    private ImageView mProFiles;
    private List<CommonListItem> mDemandList;
    private int force;
    private LinkDialog mProDialog;
    private static final int ERROR_MSG = 105;
    private boolean mPerforming;
    private String projectId;
    private CommonListItem mCurrentProItem;
    private LinkDialog mQrcodeDialog;
    private ProgressBarView mLoadingView;
    private CommonDialog dialog;
    private CommonDialog exitProDialog;
    private LinearLayout mLinkLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecordUtils.onEvent(getContext(),R.string.home_toscreen_state);
        initView(view);
        setViews();
        setListeners();
        initPresenter();
        getData();
    }

    private void getData() {
        mLoadingView.startLoading();
        int hotelid = mSession.getHotelid();
        AppApi.getDemandList(getContext(),this,hotelid+"");
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void initLinkStatus() {
        if(mSession == null)
            return;
        boolean bindTv = mSession.isBindTv();
        if(bindTv) {
            Class projectionActivity = ProjectionManager.getInstance().getProjectionActivity();
            if(projectionActivity!=null) {
                String hint = "";
                if(projectionActivity == LocalVideoProAcitvity.class) {
                    hint = "本地视频";
                    mHintTv.setText("正在投屏"+hint+"，点击进入>>");
                }else if(projectionActivity == SlidesActivity.class
                        || projectionActivity == SingleImageProActivity.class) {
                    hint = "图片";
                    mHintTv.setText("正在投屏"+hint+"，点击进入>>");
                }else if(projectionActivity == PdfPreviewActivity.class) {
                    hint = "文件";
                    mHintTv.setText("正在投屏"+hint+"，点击进入>>");
                }else if(projectionActivity == VideoPlayVODInHotelActivity.class) {
                    hint = "视频";
                    mHintTv.setText("正在点播"+hint+"，点击进入>>");
                }

                mLinkTv.setText("退出投屏");
                mCurrentState = STATE_EXIT_PRO;
            }else {
                mHintTv.setText("已连接--"+mSession.getSsid()+"的电视");
                mLinkTv.setText("断开连接");
                mCurrentState = STATE_UNLINK_TV;
            }
        }else {
            mHintTv.setText("您已进入酒楼，快来体验用电视看手机");
            mLinkTv.setText("连接电视");
            mCurrentState = STATE_LINK_TV;
        }
    }

    /**
     * 关联控件
     */
    private void initView(View view) {
        mCoverflowView = (CoverFlowView) view.findViewById(R.id.coverflow);
        mHintTv = (TextView) view.findViewById(R.id.tv_hint);
        mLinkTv = (TextView) view.findViewById(R.id.tv_link);
        mLinkLayout = (LinearLayout) view.findViewById(R.id.ll_link_layout);
        mProGalleryIv = (ImageView) view.findViewById(R.id.iv_pro_gallery);
        mProFiles = (ImageView) view.findViewById(R.id.iv_pro_files);
        mLoadingView = (ProgressBarView) view.findViewById(R.id.pbv_loading);
    }

    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_hint:
                performProHintBtn();
                break;
            case R.id.ll_link_layout:
                performLinkBtnClik();
                break;
            case R.id.iv_pro_gallery:
                // 打开相册
                RecordUtils.onEvent(getContext(),R.string.home_album_toscreen);
                openGallery();
                break;
            case R.id.iv_pro_files:
                // 打开可投屏的文件
                RecordUtils.onEvent(getContext(),R.string.home_file_toscreen);
                openFiles();
                break;
        }
    }

    private void performProHintBtn() {
        if(mCurrentState == STATE_EXIT_PRO) {
            Class projectionActivity = ProjectionManager.getInstance().getProjectionActivity();
            if(projectionActivity == VideoPlayVODInHotelActivity.class) {
                RecordUtils.onEvent(getContext(),R.string.home_quick_video);
                CommonListItem item = ProjectionManager.getInstance().getmVodBean();
                boolean isPlaying = ProjectionManager.getInstance().getVodPlayStatus();
                VideoPlayVODInHotelActivity.startOnDemandActivity(getActivity(),item,isPlaying);
            }else if(projectionActivity == SingleImageProActivity.class) {
                RecordUtils.onEvent(getContext(),R.string.home_quick_entry);
                int singlePosition = ProjectionManager.getInstance().getSinglePosition();
                String pId = ProjectionManager.getInstance().getProjectId();
                SingleImageProActivity.startImageGallery(getActivity(),true, pId,singlePosition);
            }else if(projectionActivity == SlidesActivity.class) {
                RecordUtils.onEvent(getContext(),R.string.home_quick_entry);
                int position = ProjectionManager.getInstance().getSlidePosition();
                boolean slideStatus = ProjectionManager.getInstance().isSlideStatus();
                String seriesId = ProjectionManager.getInstance().getSeriesId();
                SlidesActivity.startSlidesActivity(getActivity(),position,slideStatus,seriesId,true);
            }else if(projectionActivity == LocalVideoProAcitvity.class) {
                RecordUtils.onEvent(getContext(),R.string.home_quick_entry);
                MediaInfo mediaInfo = ProjectionManager.getInstance().getLocalVideoInfo();
                String projectId = ProjectionManager.getInstance().getProjectId();
                boolean localVideoPlaying = ProjectionManager.getInstance().isLocalVideoPlaying();
                LocalVideoProAcitvity.startLocalVideoProActivity(getActivity(),mediaInfo,localVideoPlaying,projectId);
            }else if(projectionActivity == PdfPreviewActivity.class) {
                RecordUtils.onEvent(getContext(),R.string.home_quick_entry);
                int currentPage = ProjectionManager.getInstance().getCurrentPage();
                String pdfPath = ProjectionManager.getInstance().getPdfPath();
                boolean lockedScrren = ProjectionManager.getInstance().isLockedScrren();
                int pdfOritention = ProjectionManager.getInstance().getPdfOritention();
                PdfPreviewActivity.startPdfPreviewActivity(getActivity(),pdfPath,currentPage,true,lockedScrren,pdfOritention);
            }
        }
    }

    private void openFiles() {
        Intent intent = new Intent(getActivity(),PdfListActivity.class);
        startActivityForResult(intent,0);
    }

    private void openGallery() {
        Intent intent = new Intent(getActivity(), MediaGalleryActivity.class);
        startActivity(intent);
    }

    private void performLinkBtnClik() {
        if(mCurrentState == STATE_LINK_TV) {
            RecordUtils.onEvent(getContext(),R.string.home_connect_tv);
            mCurrentProItem = null;
            mBindTvPresenter.bindTv();
        }else if(mCurrentState == STATE_UNLINK_TV) {
            showUnLinkDialog();
        }else if(mCurrentState == STATE_EXIT_PRO) {
            exitProDialog = new CommonDialog(getActivity(), "是否退出包间" + mSession.getSsid() + "的投屏", new CommonDialog.OnConfirmListener() {
                @Override
                public void onConfirm() {
                    ProjectionManager.getInstance().resetProjection();
                    String projectId = ProjectionManager.getInstance().getProjectId();
                    AppApi.notifyTvBoxStop(getActivity(),mSession.getTVBoxUrl(),projectId,ProjectionFragment.this);
                }
            }, new CommonDialog.OnCancelListener() {
                @Override
                public void onCancel() {
                    exitProDialog.dismiss();
                }
            },"退出投屏");
            exitProDialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == EXTRA_TV_INFO){
            initBindcodeResult();
//            if(data!=null) {
//                TvBoxInfo boxInfo = (TvBoxInfo) data.getSerializableExtra(EXRA_TV_BOX);
//                mBindTvPresenter.handleBindCodeResult(boxInfo);
//            }
        }
    }


    @Override
    public void getViews() {

    }


    /**
     * 初始化控件
     */
    @Override
    public void setViews() {
        initLinkStatus();
    }

    /**
     * 设置监听事件
     */
    @Override
    public void setListeners() {
        mLinkLayout.setOnClickListener(this);
        mCoverflowView.setOnTvBtnClickListener(this);
        mCoverflowView.setOnPhoneBtnClickListener(this);
        mProGalleryIv.setOnClickListener(this);
        mProFiles.setOnClickListener(this);
        mHintTv.setOnClickListener(this);
        mLoadingView.setProgressBarViewClickListener(this);
    }

    @Override
    public String getFragmentName() {
        return "ProjectionFragment";
    }

    public static ProjectionFragment newInstance(int id) {
        ProjectionFragment fragment = new ProjectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onTvBtnClick(int position, View itemView) {
        if(mPerforming)
            return;
        mCurrentProItem = mDemandList.get(position);
        boolean bindTv = mSession.isBindTv();
        if(bindTv) {
            RecordUtils.onEvent(getActivity(),getString(R.string.home_click_bunch_video));
            force = 0;
            startDemandItemPro();
        }else {
            mBindTvPresenter.bindTv();
        }
        RecordUtils.onEvent(getContext(),R.string.home_click_bunch_video);
    }

    /**
     * 点播投屏
     */
    private void startDemandItemPro() {
        // 绑定则跳转内网点播
        Class projectionActivity = ProjectionManager.getInstance().getProjectionActivity();
        if(projectionActivity == SlidesActivity.class) {
            HotspotMainActivity mainActivity = (HotspotMainActivity) getActivity();
            mainActivity.stopSlide();
        }

        // 当点击连接电视按钮时，要清除掉mCurrentProItem对象，否则绑定成功后会自动进行投屏
        if(mCurrentProItem!=null) {
            BaseProReqeust baseProReqeust = new BaseProReqeust();
            baseProReqeust.setVodType(1);
            baseProReqeust.setAssetname(mCurrentProItem.getName());

            // 请求机顶盒投屏，如果成功跳转到播放页面，失败弹出提示接口返回错误信息
            showProLoadingDialog();
            AppApi.vodProection(getActivity(),mSession.getTVBoxUrl(), baseProReqeust,force,this);
        }
    }

    /**
     * 展示请求投屏弹窗
     * */
    private void showProLoadingDialog() {
        if(mProDialog==null) {
            mProDialog = new LinkDialog(getActivity(),"请求投屏...");
        }
        mProDialog.show();
    }

    private void dismissProLoadingDialog() {
        if(mProDialog!=null) {
            mProDialog.dismiss();
        }
    }

    @Override
    public void onPhoneBtnClick(int position, View itemView) {
        RecordUtils.onEvent(getContext(),R.string.home_local_tv);
        CommonListItem item = mDemandList.get(position);
        VideoPlayVODNotHotelActivity.startVODVideoActivity(getActivity(),item);
    }

    @Override
    public void showUnLinkDialog() {
        new CommonDialog(getActivity(), "是否与电视断开，\n断开后将无法投屏？", new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.home_disconnect),"break");
                RecordUtils.onEvent(getActivity(),getString(R.string.home_disconnect),hashMap);
                disconnectTv();
                initLinkStatus();
            }
        }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.home_break_connect),"cancel");
                RecordUtils.onEvent(getActivity(),getString(R.string.home_break_connect),hashMap);
            }
        },"断开连接").show();
//        new CommonDialog(getActivity(),"是否与电视断开，\n断开后将无法投屏？").setTitle("提示").setNegativeButton("取消", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        }).setPositiveButton("断开连接", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        }).show();
    }

    private void disconnectTv() {
        stopProjection();
        ProjectionManager.getInstance().resetProjection();
        mSession.resetPlatform();
        mSession.setTvBoxInfo(null);
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_NOTIFY_TVBOX_STOP_JSON:
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.home_quick_back),"success");
                RecordUtils.onEvent(getContext(),getString(R.string.home_quick_back),hashMap);
                initLinkStatus();
                break;
            case POST_DEMAND_LIST_JSON:
                if(isAdded()) {
                    handleDemandList(obj);
                }else {
                    mLoadingView.loadFailure();
                }
                break;
            case GET_VOD_PRO_JSON:
                mPerforming = false;
                dismissProLoadingDialog();
                // 保存会话id
                if(obj instanceof BaseProResponse) {
                    VodProResponse response = (VodProResponse) obj;
                    projectId = response.getProjectId();
                    ProjectionManager.getInstance().setProjectId(projectId);
                    ProjectionManager.getInstance().setVideoTVProjection(VideoPlayVODInHotelActivity.class,mCurrentProItem,true);
                    boolean isPlaying = ProjectionManager.getInstance().getVodPlayStatus();
                    VideoPlayVODInHotelActivity.startOnDemandActivity(getActivity(),mCurrentProItem,isPlaying);
                }
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object statusCode) {
        switch (method) {
            case POST_NOTIFY_TVBOX_STOP_JSON:
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.home_quick_back),"fail");
                RecordUtils.onEvent(getContext(),getString(R.string.home_quick_back),hashMap);
                break;
            case POST_DEMAND_LIST_JSON:
                mLoadingView.loadFailure();
                break;
            case GET_VOD_PRO_JSON:
                dismissProLoadingDialog();
                mPerforming = false;
                if(statusCode instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) statusCode;
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
            mDemandList = (List<CommonListItem>) obj;
            CoverFlowAdapter coverFlowAdapter = new CoverFlowAdapter(getActivity(), mDemandList);
            mCoverflowView.setAdapter(coverFlowAdapter);
            mCoverflowView.post(new Runnable() {
                @Override
                public void run() {
                    mCoverflowView.gotoPreviousNoAnimator();
                    mLoadingView.loadSuccess();
                }
            });
        }
    }

    @Override
    public void initBindcodeResult() {
        startDemandItemPro();
    }

    @Override
    public void readyForCode() {
        if(mQrcodeDialog==null)
            mQrcodeDialog = new LinkDialog(getActivity(),getString(R.string.call_qrcode));
        mQrcodeDialog.show();
    }

    @Override
    public void startLinkTv() {
        super.startLinkTv();

    }

    /**
     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
     */
    private void showConfirm(final String user){
        String content = "当前"+user+"正在投屏,是否继续投屏?";
        dialog = new CommonDialog(getActivity(), content,
                new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        HashMap<String,String> params = new HashMap<>();
                        params.put(getString(R.string.to_screen_competition_hint),"ensure");
                        params.put("type","pic");
                        RecordUtils.onEvent(getActivity(),getString(R.string.to_screen_competition_hint),params);
                        force = 1;
                        startDemandItemPro();
                        dialog.cancel();
                    }
                }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                HashMap<String,String> params = new HashMap<>();
                params.put(getString(R.string.to_screen_competition_hint),"cancel");
                params.put("type","pic");
                RecordUtils.onEvent(getActivity(),getString(R.string.to_screen_competition_hint),params);
                dialog.cancel();
            }
        },"继续投屏",true);
        dialog.show();
    }

    @Override
    public void closeQrcodeDialog() {
        if (mQrcodeDialog != null) {
            mQrcodeDialog.dismiss();
            mQrcodeDialog = null;
        }
    }

    @Override
    public void loadDataEmpty() {
        getData();
    }

    @Override
    public void loadFailureNoNet() {
        getData();
    }

    @Override
    public void loadFailure() {
        getData();
    }
}