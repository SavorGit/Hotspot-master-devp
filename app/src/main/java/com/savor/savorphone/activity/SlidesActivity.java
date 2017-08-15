package com.savor.savorphone.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rance.library.ButtonData;
import com.rance.library.ButtonEventListener;
import com.rance.library.SectorMenuButton;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.SlideAdapter;
import com.savor.savorphone.bean.ImageProResonse;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.bean.PictureInfo;
import com.savor.savorphone.bean.RotateProResponse;
import com.savor.savorphone.bean.StopProResponseVo;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.interfaces.IHotspotSenseView;
import com.savor.savorphone.interfaces.OnStopListener;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.service.ProjectionService;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.LinkDialog;
import com.savor.savorphone.widget.LoopViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.savor.savorphone.activity.HotspotMainActivity.SCAN_QR;
import static com.savor.savorphone.activity.LinkTvActivity.EXRA_TV_BOX;
import static com.savor.savorphone.activity.LinkTvActivity.EXTRA_TV_INFO;

/**
 * 幻灯片播放
 *
 * @author savor
 */
public class SlidesActivity extends BaseProActivity implements
        OnPageChangeListener, OnStopListener, View.OnClickListener, IBindTvView, IHotspotSenseView {

    private static final int SLIDE = 1;
    private static final int DISPLAY = 3;
    private static final int ERROR_MSG = 4;
    private static final int SCRENN_SUCESS = 5;
    private static final int FORCE_MSG = 8;
    private int mInterval = 5;
    private int currentPager = 0;
    private Context mContext;
    private List<MediaInfo> mSlideList = new ArrayList<>();
    //    private LinkedList<ImageView> mPhotoList = new LinkedList<ImageView>();
//    private LayoutInflater mInflater;
    private LoopViewPager mViewPager;
    private boolean isfirst = false;
    private LinearLayout finsh;
    /**幻灯片状态，true正在播放，false暂停*/
    private boolean isPlaying = true;
    /**是否正在投屏*/
    private boolean isProjecting = true;
    /**
     * 投屏时遇到别人正在投屏，传1代表确认抢投，默认传0
     */
    private int force=0;
    private CommonDialog dialog;
    /**幻灯片延迟切换时间间隔*/
    private static final List<String> TIME_LIST = new ArrayList<String>(){
        {
            add("5s");
            add("20s");
            add("10s");
            add("3s");
        }
    };
    private Runnable mSlideRunnable = new Runnable() {
        @Override
        public void run() {
            slide();
        }
    };
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCRENN_SUCESS:
                    if(isProjecting)
                        mProjectionBtn.setText("退出投屏");
                    break;
                case ERROR_MSG:
                    String erromsg = (String) msg.obj;
                    ShowMessage.showToast(SlidesActivity.this,erromsg);
                    mHandler.removeMessages(SLIDE);
                    mProjectionService.stopProjection();
                    isProjecting = false;
                    mProjectionService.setPlayStatus(false);
                    mHandler.removeMessages(SLIDE);
                    mPauseBtn.setBackgroundResource(R.drawable.ic_playing);
                    tv_current_type.setText("已暂停");
                    mProjectionService.setPlayStatus(false);
                    mViewPager.setCurrentItem(0,false);
                    mProjectionBtn.setText(R.string.projection);
                    break;
                case SLIDE:
                    if (mSlideList.size()==0||currentPager>= mSlideList.size()-1) {
                        mHandler.removeMessages(SLIDE);
                        mProjectionService.stopProjection();
                        isProjecting = false;
                        setPlayBtnStatusChange();
                        mProjectionService.setPlayStatus(false);
                        mViewPager.setCurrentItem(0,false);
                        mProjectionBtn.setText(R.string.projection);
                        Intent intent = new Intent("play_over");
                        sendBroadcast(intent);
                        ShowMessage.showToast(SlidesActivity.this,getString(R.string.slide_play_over));
                        tv_current_type.setText("幻灯片");

                    }else {
                        mViewPager.setCurrentItem(++currentPager, false);
                        currentPager = mViewPager.getCurrentItem() % mSlideList.size();
                        tv_current_type.setText("正在播放幻灯片");
                    }
                    break;
                case DISPLAY:
                    MediaInfo mediaInfo = (MediaInfo) msg.obj;
                    if(mediaInfo!=null)
                        mProjectionService.uploadImage(mediaInfo);
                    break;
                case FORCE_MSG:
                    String message = (String)msg.obj;
                    showConfirm(message);
                    break;
            }
        }

    };

    /**是否是小图投屏*/
    private int small;
    /**投屏service对象*/
    private ProjectionService mProjectionService;
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mProjectionService = ((ProjectionService.ProjectionBinder) service).getService();
            long delayTime = mProjectionService.getDelayTime();
            if(delayTime == 0) {
                mProjectionService.setDelayTime(5*1000);
            }
            ProjectionManager.getInstance().setForegroundStatus(true);
            LogUtils.d("savor:projectoin service 绑定成功");
            ProjectionManager.getInstance().setSlideStatus(isPlaying);
            mProjectionService.slide(mDefaultPosition);
            mProjectionService.setOnDisplayImageListener(new ProjectionService.OnDisPlayImageListener() {
                @Override
                public void onDisplayImage(PictureInfo info) {
                    Message message = Message.obtain();
                    message.obj = info;
                    message.what = DISPLAY;
                    mHandler.removeMessages(DISPLAY);
                    mHandler.sendMessage(message);
                }
            });
            mProjectionService.setOnSlideListener(new ProjectionService.OnSlideListener() {
                @Override
                public void onSlide() {
                    mHandler.removeMessages(SLIDE);
                    mHandler.sendEmptyMessage(SLIDE);
                }
            });
            mProjectionService.setOnProjectionErrorListener(new ProjectionService.OnProjectionErrorListener() {
                @Override
                public void onProjectoinErrorListener(AppApi.Action method, Object obj) {
                    isProjecting = false;
                    if(obj instanceof ResponseErrorMessage) {
                        ResponseErrorMessage message = (ResponseErrorMessage) obj;
                        int code = message.getCode();
                        Message msg = Message.obtain();
                        if(code==4){
                            msg.what = FORCE_MSG;
                            msg.obj = message.getMessage();
                            mHandler.sendMessage(msg);
                        }else{
                            msg.what = ERROR_MSG;
                            msg.obj = message.getMessage();
                            mHandler.sendMessage(msg);
                        }

                    }else {
                        Message msg = Message.obtain();
                        msg.what = ERROR_MSG;
                        msg.obj = "投屏失败";
                        mHandler.sendMessage(msg);
                    }
                }
            });
            mProjectionService.setOnProjectionSuccessListener(new ProjectionService.OnProjectoinSuccessListener() {
                @Override
                public void onProjectionSuccess(AppApi.Action method, Object response) {
                    switch (method) {
                        case POST_IMAGE_PROJECTION_JSON:
                            mHandler.sendEmptyMessage(SCRENN_SUCESS);
                            break;
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
     * @param msg
     */
    private void showConfirm(String msg){
        if (dialog!=null&&dialog.isShowing()){
            return;
        }
        String content = "当前"+msg+"正在投屏,是否继续投屏?";
        dialog = new CommonDialog(this, content,
                new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        HashMap<String,String> params = new HashMap<>();
                        params.put(getString(R.string.to_screen_competition_hint),"ensure");
                        params.put("type","grouppic");
                        RecordUtils.onEvent(SlidesActivity.this,getString(R.string.to_screen_competition_hint),params);
                        isProjecting = true;
                        isPlaying = true;
                        force = 1;
                        if (mProjectionService!=null){
                            mProjectionService.setForce(force);
                        }
                        slide();
                        ProjectionManager.getInstance().setSlideProjection(SlidesActivity.class,currentPager, mSlideList,isPlaying,seriesId);
                        mProjectionService.setPlayStatus(true);
                        mProjectionService.slide(currentPager);
                        mPauseBtn.setBackgroundResource(R.drawable.ic_pause);
                        tv_current_type.setText("正在播放图片");
                        dialog.cancel();
                    }
                }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                HashMap<String,String> params = new HashMap<>();
                params.put(getString(R.string.to_screen_competition_hint),"cancel");
                params.put("type","grouppic");
                RecordUtils.onEvent(SlidesActivity.this,getString(R.string.to_screen_competition_hint),params);
                isProjecting = false;
                dialog.cancel();
            }
        },"继续投屏",true);
        dialog.show();
    }

    private Intent mProjectionIntent;
    private int mDefaultPosition;
    private LinearLayout mBackBtn;
    private SectorMenuButton mSectorMenu;
    private TextView mCurrentPaeTv;
    private TextView tv_current_type;
    private ImageView mPauseBtn;
    private TextView mProjectionBtn;
    private String currentImageid;
    private CommonDialog hotsDialog;
    private LinkDialog mQrcodeDialog;
    private CommonDialog mChangeWifiDiallog;
    private SlideAdapter mSlideAdapter;
    /**幻灯片投屏时会话id*/
    private String seriesId;


    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.activity_slides);
        mInterval = mSession.getInterval();

        handleIntent();

        bindProjectionService();
        // 在这里将幻灯片投屏状态记录，以便在当前页面接收到机顶盒请求时拿到当前投屏状态
        if(isProjecting) {
            ProjectionManager.getInstance().setSlideProjection(SlidesActivity.class,currentPager, mSlideList,isPlaying,seriesId);
        }
        initPresenter();
        getViews();
        setViews();
        setListeners();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String json = new Gson().toJson(mSlideList);
        outState.putString("piclist",json);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String json = savedInstanceState.getString("piclist");
        mSlideList = new Gson().fromJson(json, new TypeToken<List<MediaInfo>>() {
        }.getType());
    }

    public static void startSlidesActivity(Context context,int position,boolean isPlaying,String seriesId,boolean isProjecting) {
        Intent intent = new Intent(context,SlidesActivity.class);
        intent.putExtra("position",position);
        intent.putExtra("isPlaying",isPlaying);
        intent.putExtra("seriesId",seriesId);
        intent.putExtra("isProjecting",isProjecting);
        context.startActivity(intent);
    }

    private void bindProjectionService() {
        mProjectionIntent = new Intent(this,ProjectionService.class);
        mProjectionIntent.putExtra(ProjectionService.EXTRA_TYPE,ProjectionService.TYPE_VOD_SLIDE);
        mProjectionIntent.putExtra(ProjectionService.EXTRA_SLIDE_COUNT, mSlideList ==null?0: mSlideList.size());
        mProjectionIntent.putExtra(ProjectionService.EXTRA_SLIDE_LIST, (ArrayList)mSlideList);
        bindService(mProjectionIntent,mServiceConn,Context.BIND_AUTO_CREATE);
    }

    private void handleIntent() {
        mSlideList = ProjectionManager.getInstance().getSlideList();
        if(getIntent().hasExtra("position")) {
            mDefaultPosition = getIntent().getIntExtra("position", 0);
        }
        if(getIntent().hasExtra("isPlaying")) {
            isPlaying = getIntent().getBooleanExtra("isPlaying",true);
        }
        seriesId = getIntent().getStringExtra("seriesId");
        isProjecting = getIntent().getBooleanExtra("isProjecting",false);
        isfirst = true;
    }
//    private void displayPic() {
//        PictureInfo pictureInfo = mSlideList.get(currentPager);
//        try {
//            if (pictureInfo==null){
//                return;
//            }
//            Bitmap bitmap = null;
//            String copyPath = "";
//            if (small==1){
//                copyPath = CompressImage.compressAndSaveBitmap(this, pictureInfo.getAssetpath(),pictureInfo.getAssetname(),true);
//            }else{
//                copyPath = CompressImage.compressAndSaveBitmap(this, pictureInfo.getAssetpath(),pictureInfo.getAssetname(),false);
//            }
//
//            pictureInfo.setAsseturl(NetWorkUtil.getLocalUrl(this)+copyPath);
//            pictureInfo.setCompressPath(copyPath);
//
//            Message message = Message.obtain();
//            message.obj = pictureInfo;
//            message.what = DISPLAY;
//            mHandler.removeMessages(DISPLAY);
//            mHandler.sendMessage(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void getViews() {
        mProjectionBtn = (TextView) findViewById(R.id.tv_projectin_btn);
        mBackBtn = (LinearLayout) findViewById(R.id.ll_slide_back);
        mViewPager = (LoopViewPager) findViewById(R.id.viewpager);
        finsh = (LinearLayout) findViewById(R.id.ll_slide_finish);
        mCurrentPaeTv = (TextView) findViewById(R.id.tv_current_page);
        tv_current_type = (TextView) findViewById(R.id.tv_current_type);
        mSectorMenu = (SectorMenuButton) findViewById(R.id.sectorMenuBtn);
        mSlideAdapter = new SlideAdapter(this);
        mSlideAdapter.setData(mSlideList);
        mViewPager.setAdapter(mSlideAdapter);
        mViewPager.setOnPageChangeListener(this);
        currentPager = mDefaultPosition;
//        mViewPager.setCurrentItem(currentPager, false);
        mPauseBtn = (ImageView) findViewById(R.id.iv_pause);
    }

    @Override
    public void setViews() {
//        initItems();
        initSectorMenu();
        mProjectionBtn.setText(R.string.exit_projection);
        if(mSlideList !=null&& mSlideList.size()>0) {
            initCurrentPlayingPageHint(mDefaultPosition+1);
        }
        if(!isProjecting) {
            mProjectionBtn.setText("投屏");
        }else {
            mProjectionBtn.setText("退出投屏");
        }

        if(!isPlaying) {
            mPauseBtn.setBackgroundResource(R.drawable.ic_playing);
            tv_current_type.setText("幻灯片");
        }else {
            mPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            tv_current_type.setText("正在播放图片");
        }

        if(!mSession.isBindTv()) {
            if(hotsDialog==null) {
                hotsDialog = new CommonDialog(this, getString(R.string.click_link_tv), new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        mBindTvPresenter.bindTv();
                    }
                }, new CommonDialog.OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                },getString(R.string.link_tv));
            }
            hotsDialog.show();
        }
        mViewPager.setCurrentItem(mDefaultPosition);
        MediaInfo mediaInfo = mSlideList.get(mDefaultPosition);
        if(mediaInfo == null||TextUtils.isEmpty(mediaInfo.getAssetpath())||!new File(mediaInfo.getAssetpath()).exists()) {
            ShowMessage.showToast(this,getString(R.string.the_pic_deleted));
        }
    }

    private void initCurrentPlayingPageHint(int position) {
        String hint = getString(R.string.photo_playing);
        String formatStr = String.format(hint, position, mSlideList.size());
        mCurrentPaeTv.setText(formatStr);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == EXTRA_TV_INFO){
            initBindcodeResult();
        }else  if (resultCode == SCAN_QR) {
            if(data!=null) {
                String scanResult = data.getStringExtra("scan_result");
                mBindTvPresenter.handleQrcodeResult(scanResult);
                LogUtils.d("扫描结果：" + scanResult);
            }
        }
    }

    @Override
    public void setListeners() {
        mBackBtn.setOnClickListener(this);
        finsh.setOnClickListener(this);
        mPauseBtn.setOnClickListener(this);
        mSectorMenu.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {
                List<ButtonData> buttonDatas = mSectorMenu.getButtonDatas();
                ButtonData buttonData = buttonDatas.get(0);
                String[] mainText = buttonData.getTexts();
                if(index!=0) {
                    ButtonData clickButtonData = buttonDatas.get(index);
                    String[] clickText = clickButtonData.getTexts();

                    // 设置幻灯片切换延时时间
                    String delaytime = clickText[0];
                    delaytime = delaytime.replaceAll("s","");
                    Long dTime = Long.valueOf(delaytime);
                    if (mProjectionService!=null) {
                        mProjectionService.setDelayTime(dTime*1000);
                        mProjectionService.slide(currentPager);
                    }

                    buttonData.setText(clickText);
                    clickButtonData.setText(mainText);
                    List<String> sortList = new ArrayList<>();
                    for(int i =1;i<buttonDatas.size();i++) {
                        sortList.add(buttonDatas.get(i).getTexts()[0]);
                    }
                    Collections.sort(sortList, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            String firstTime = o1.replaceAll("s", "");
                            String secondTime = o2.replaceAll("s","");
                            Integer fTime = Integer.valueOf(firstTime);
                            Integer sTime = Integer.valueOf(secondTime);
                            return fTime-sTime;
                        }
                    });
                    Collections.reverse(sortList);
                    for(int i =1;i<buttonDatas.size();i++) {

                        buttonDatas.get(i).setText(sortList.get(i-1));
                    }
                }
            }

            @Override
            public void onExpand() {

            }

            @Override
            public void onCollapse() {

            }
        });
    }

    private void initSectorMenu() {
        List<ButtonData> slideSectorMenu = ProjectionManager.getInstance().getSlideSectorMenu();
        if(slideSectorMenu == null) {
            List<ButtonData> buttonDatas = new ArrayList<>();
            for(int i = 0;i<TIME_LIST.size();i++) {
                ButtonData buttonData = ButtonData.buildTextButton(TIME_LIST.get(i));
                buttonData.setBackgroundColor(getResources().getColor(R.color.color_B3000000));
                buttonDatas.add(buttonData);
            }
            slideSectorMenu = buttonDatas;
        }
        mSectorMenu.setButtonDatas(slideSectorMenu);
    }

//    private void initItems() {
//        mInflater = LayoutInflater.from(mContext);
//        for (int i = 0; i < mSlideList.size(); i++) {
//            mPhotoList.add((ImageView) mInflater.inflate(R.layout.view_image, null));
//        }
//    }


    @Override
    protected void onRestart() {
        super.onRestart();
        // 判断如果为绑定并且当前ssid与缓存机顶盒ssid相同提示绑定成功
        if(!mSession.isBindTv()) {
            TvBoxInfo tvBoxInfo = mSession.getTvboxInfo();
            if(tvBoxInfo!=null) {
                checkWifiLinked(tvBoxInfo);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(SLIDE);
        mHandler.removeMessages(DISPLAY);
        if(mServiceConn!=null) {
            unbindService(mServiceConn);
        }
        if(mProjectionService!=null) {
            mProjectionService.isForeground(false);
//            projectionService.stopSlide();
            mProjectionService.setOnSlideListener(null);
            mProjectionService.setOnSlidePlayOverLisetener(null);
            mProjectionService.setOnDisplayImageListener(null);
            mProjectionService.setOnProjectionErrorListener(null);
            mProjectionService.setOnProjectionSuccessListener(null);
        }
//        if(mProjectionIntent!=null) {
//            stopService(mProjectionIntent);
//        }
    }

    @Override
    public void onBackPressed() {
        if(!isProjecting) {
            ProjectionManager.getInstance().resetProjection();
            if(mProjectionService!=null) {
                mProjectionService.stopSlide();
            }
        }else {
            boolean isPlaying = mProjectionService.isPalying();
            ProjectionManager.getInstance().setSlideProjection(SlidesActivity.class,currentPager, mSlideList,isPlaying,seriesId);
            List<ButtonData> buttonDatas = mSectorMenu.getButtonDatas();
            ProjectionManager.getInstance().setSlideSectorMenu(buttonDatas);
        }
        super.onBackPressed();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_slide_back:
                RecordUtils.onEvent(this,getString(R.string.slide_to_screen_back));
                onBackPressed();
                break;
            case R.id.ll_slide_finish:
                if(!AppUtils.isWifiNetwork(this)) {
                    new CommonDialog(this,"请前往手机设置，连接至电视同一WiFi下").show();
                }else{
                    if(!mSession.isBindTv()) {
                        RecordUtils.onEvent(this,getString(R.string.slide_to_screen_link_tv));
                        mBindTvPresenter.bindTv();
                    }else if(isProjecting) {
                        RecordUtils.onEvent(this,getString(R.string.slide_to_screen_start));
                        stopSlideProjection();
                    }else {
                        RecordUtils.onEvent(this,getString(R.string.slide_to_screen_exit));
                        MediaInfo mediaInfo = mSlideList.get(currentPager);
                        small = 1;
                        isProjecting = true;
                        force = 0;
                        if(mediaInfo!=null&&!TextUtils.isEmpty(mediaInfo.getAssetpath())&&new File(mediaInfo.getAssetpath()).exists()) {
                            if (mProjectionService!=null){
                                mProjectionService.setForce(force);
                            }
                            ProjectionManager.getInstance().setSeriesId(System.currentTimeMillis()+"");
                            isPlaying = ProjectionManager.getInstance().isSlideStatus();
                            ProjectionManager.getInstance().setSlideProjection(SlidesActivity.class,currentPager, mSlideList,isPlaying,seriesId);
                        }else {
                            ShowMessage.showToast(this,"该图片不存在");
                            mProjectionBtn.setText("退出投屏");
                            isProjecting = true;
                        }
                        setPlayBtnStatusChange();
                        slide();
                    }
                }
//                ShowMessage.showToast(mContext,"播放结束");
                break;
            case R.id.iv_pause:
                setPlayBtnStatusChange();
                break;
        }
    }

    public void stopSlideProjection() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                force = 0;
                isProjecting = false;
                mProjectionBtn.setText(R.string.projection);
                mProjectionBtn.setText(R.string.projection);
                tv_current_type.setText("幻灯片");

                ProjectionManager.getInstance().resetProjection();
                ProjectionManager.getInstance().setmSlidePosition(1);
                ProjectionManager.getInstance().setSlideStatus(true);
                setPlayBtnStatusChange();

                if(mProjectionService!=null) {
                    mProjectionService.stopSlide();
                    String projectId = ProjectionManager.getInstance().getProjectId();
                    AppApi.notifyTvBoxStop(mContext,mSession.getTVBoxUrl(),projectId,SlidesActivity.this);
                }
            }
        });

    }

    private void setPlayBtnStatusChange() {
        boolean isPlaying = mProjectionService.isPalying();
        if(isPlaying) {
            mProjectionService.setPlayStatus(false);
            mHandler.removeMessages(SLIDE);
            mPauseBtn.setBackgroundResource(R.drawable.ic_playing);
            if (isProjecting) {
                tv_current_type.setText("已暂停");
            }else {
                tv_current_type.setText("幻灯片");
            }

            RecordUtils.onEvent(this,getString(R.string.slide_to_screen_pause));
        }else {
            RecordUtils.onEvent(this,getString(R.string.slide_to_screen_play));
            mProjectionService.setPlayStatus(true);
            mProjectionService.slide(currentPager);
            mPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            tv_current_type.setText("正在播放图片");
        }

    }

    @Override
    public void readyForCode() {
        if(mQrcodeDialog==null)
            mQrcodeDialog = new LinkDialog(this,getString(R.string.call_qrcode));
        mQrcodeDialog.show();
    }

    @Override
    public void closeQrcodeDialog() {
        if (mQrcodeDialog != null) {
            mQrcodeDialog.dismiss();
            mQrcodeDialog = null;
        }
    }

    @Override
    public void initBindcodeResult() {
        force = 0;
        small = 1;
        ProjectionManager.getInstance().setSlideProjection(SlidesActivity.class,currentPager, mSlideList,isPlaying,seriesId);
        currentImageid = System.currentTimeMillis()+"";
        mPauseBtn.setBackgroundResource(R.drawable.ic_pause);
        mSlideList.get(currentPager).setImageId(currentImageid);
        ProjectionManager.getInstance().setSlideStatus(true);
        mProjectionBtn.setText(R.string.exit_projection);
        isProjecting = true;
        slide();
    }

    @Override
    public void startLinkTv() {
        Intent intent = new Intent(this,LinkTvActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent,0);
    }

    @Override
    public void showUnLinkDialog() {

    }

    @Override
    public void rotate(RotateProResponse rotateResponse) {

    }

    @Override
    public void reCheckPlatform() {

    }

    @Override
    public void initSenseState() {

    }

    @Override
    public void showProjection(boolean isBind) {

    }

    @Override
    public void hideProjection() {

    }

    @Override
    public void refreshData() {

    }

    @Override
    public void checkSense() {

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        RecordUtils.onEvent(this,getString(R.string.slide_to_screen_switch_item));
        currentPager = position;
        small = 1;
        currentImageid = System.currentTimeMillis()+"";
        mSlideList.get(currentPager).setImageId(currentImageid);
        String assetpath = mSlideList.get(currentPager).getAssetpath();
        if(TextUtils.isEmpty(assetpath)||!new File(assetpath).exists()) {
            if(isProjecting)
                ShowMessage.showToast(this,getString(R.string.the_pic_deleted));
        }
        mHandler.removeCallbacks(mSlideRunnable);
        mHandler.postDelayed(mSlideRunnable,100);

        initCurrentPlayingPageHint(position+1);
    }


    public void slide() {
        if(mProjectionService!=null) {
            mProjectionService.slide(currentPager);
            mProjectionService.setForce(force);
            ProjectionManager.getInstance().setSlideList(mSlideList);
            LogUtils.d("savor:projection 幻灯片切换，前台投屏");
            if(isProjecting)
                mProjectionService.showImageToScreen(small);
        }
    }


    @Override
    public void resultNull() {
    }

    @Override
    public void stopSuccess() {
    }

    @Override
    public void stopFailed(StopProResponseVo responseVo) {

    }


    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_NOTIFY_TVBOX_STOP_JSON:
                ProjectionManager.getInstance().setSlideStatus(false);
                seriesId = null;
                break;
            case POST_IMAGE_PROJECTION_JSON:
                isProjecting = true;
                if(obj instanceof ImageProResonse) {
                    ImageProResonse resonse = (ImageProResonse) obj;
                    String projectId = resonse.getProjectId();
                    ProjectionManager.getInstance().setProjectId(projectId);
                }
                if (small==1&&isProjecting){
                    small = 0;
                    mProjectionService.showImageToScreen(small);
                    return;
                }
                break;
        }
    }

}
