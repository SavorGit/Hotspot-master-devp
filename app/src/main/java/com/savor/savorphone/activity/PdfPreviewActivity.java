package com.savor.savorphone.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.BaseProReqeust;
import com.savor.savorphone.bean.ImageProResonse;
import com.savor.savorphone.bean.PdfInfo;
import com.savor.savorphone.bean.PictureInfo;
import com.savor.savorphone.bean.RotateProResponse;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.interfaces.IHotspotSenseView;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.service.ProjectionService;
import com.savor.savorphone.utils.CompressImage;
import com.savor.savorphone.utils.NetWorkUtil;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.ScreenOrientationUtil;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.HotsDialog;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.savor.savorphone.activity.HotspotMainActivity.FROM_APP_BACK;
import static com.savor.savorphone.activity.LinkTvActivity.EXRA_TV_BOX;
import static com.savor.savorphone.activity.LinkTvActivity.EXTRA_TV_INFO;
import static com.savor.savorphone.utils.ConstantValues.LOG_FILE_PREFIX;

public class PdfPreviewActivity extends BaseProActivity implements OnPageScrollListener, OnPageChangeListener, OnLoadCompleteListener, IBindTvView, IHotspotSenseView, View.OnClickListener {

    private static final String TAG = "pdf";
    private static final int SCREEN_SHOT_PRO = 0x1;
    private static final int DISPLAY = 0x2;
    private static final int COMPRESS_PIC = 0x5;
    private static final int ERROR_MSG = 0x6;
    private static final int GET_SCREEN_SHOT = 0x7;
    private static final int PRO_SUCCESS = 0x8;
    private static final int FORCE_MSG = 0x9;
    private static final int HIDE_PAGE_HINT = 0x10;
    private PDFView mPdfView;
    private String mPdfPath;
    private int pageNumber;
    private TextView mPdfNameTv;
    private ExecutorService mThreadPool = Executors.newFixedThreadPool(3);
    /**
     * 当投屏时遇到大屏正在投屏中，抢投传1，代表确认抢投，默认传0
     */
    private int force=0;
    private CommonDialog dialog;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE_PAGE_HINT:
                    mPageCountTv.setVisibility(View.GONE);
                    break;
                case PRO_SUCCESS:
                    mExitBtn.setText("退出投屏");
                    break;
                case GET_SCREEN_SHOT:
                    mThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            mPdfView.post(new Runnable() {
                                @Override
                                public void run() {
                                    Bitmap bitmap = getViewBitmap(mPdfView);
                                    String compressPath = CompressImage.compressAndSaveBitmap(PdfPreviewActivity.this, bitmap);
                                    if(!TextUtils.isEmpty(compressPath)) {
                                        Message message = Message.obtain();
                                        message.what = SCREEN_SHOT_PRO;
                                        message.obj = compressPath;
                                        mHandler.removeMessages(SCREEN_SHOT_PRO);
                                        mHandler.sendMessageDelayed(message,50);
                                    }
                                }
                            });

                        }
                    });

                    break;
                case ERROR_MSG:
                    String erromsg = (String) msg.obj;
                    if(!TextUtils.isEmpty(erromsg)) {
                        showToast(erromsg);
                    }
                    stopPdfPro();
                    break;
                case COMPRESS_PIC:
                    getScreenShot();
                    break;
                case SCREEN_SHOT_PRO:
                    small = 1;
                    String path = (String) msg.obj;
                    File file = new File(path);
                    if(file.exists()) {
                        final PictureInfo picInfo = new PictureInfo();
                        String name = file.getName();
                        if(name.contains(".")) {
                            name = name.substring(0,name.lastIndexOf("."));
                        }
                        String imageId = System.currentTimeMillis()+"";
                        picInfo.setImageId(imageId);
                        picInfo.setAssetpath(path);
                        picInfo.setAsseturl(NetWorkUtil.getLocalUrl(PdfPreviewActivity.this)+path);
                        picInfo.setAssetname(name);
                        showScreenShot(picInfo);
                    }
                    break;
                case DISPLAY:
                    final PictureInfo pictureInfo = (PictureInfo) msg.obj;
                    mCurrentPic = pictureInfo.getAssetpath();
                    String imageId = pictureInfo.getImageId();
                    if(small == 0&&!imageId.equals(currentImageId)) {
                        LogUtils.d(LOG_FILE_PREFIX+"大小图不匹配");
                        return;
                    }
                    AppApi.updateScreenProjectionFile(PdfPreviewActivity.this, mSession.getTVBoxUrl(), getBasePrepareInfo(pictureInfo),
                            pictureInfo.getCompressPath(), small, force,new ApiRequestListener() {
                        @Override
                        public void onSuccess(AppApi.Action method, Object obj) {
                            switch (method) {
                                case POST_IMAGE_PROJECTION_JSON:
                                    if(mProjectionService!=null) {
                                        mProjectionService.stopQuerySeek();
                                        mProjectionService.stopSlide();
                                    }
                                    currentImageId = pictureInfo.getImageId();
                                    isProjecting = true;
                                    savePdfProjecting();
                                    if (obj instanceof ImageProResonse) {
                                        ImageProResonse resonse = (ImageProResonse) obj;
                                        String projectId = resonse.getProjectId();
                                        ProjectionManager.getInstance().setProjectId(projectId);
                                    }
                                    mHandler.sendEmptyMessage(PRO_SUCCESS);
                                    if (small == 1) {
                                        small = 0;
                                        showScreenShot(pictureInfo);
                                        return;
                                    }
                                    break;

                            }
                        }

                        @Override
                        public void onError(AppApi.Action method, Object obj) {
                            if(obj instanceof ResponseErrorMessage) {
                                ResponseErrorMessage message = (ResponseErrorMessage) obj;
                                int code = message.getCode();
                                Message msg = Message.obtain();
                                if (code==4){
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

                        @Override
                        public void onNetworkFailed(AppApi.Action method) {

                        }
                    });
                break;
                case FORCE_MSG:
                    String message = (String) msg.obj;
                    showConfirm(message);
                    break;
            }
        }
    };
    private ImageView mLockScreenIv;
    private boolean isScreenLocked;
    private boolean isBackgroud;
    private TextView mPageCountTv;
    private ImageView mBackBtn;
    private TextView mExitBtn;
    private String currentImageId;
    private int scrrenOritation;

    /**
     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
     * @param msg
     */
    private void showConfirm(String msg){
        String content = "当前"+msg+"正在投屏,是否继续投屏?";
        if(dialog == null) {
            dialog = new CommonDialog(this, content,
                    new CommonDialog.OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            HashMap<String,String> params = new HashMap<>();
                            params.put(getString(R.string.to_screen_competition_hint),"ensure");
                            params.put("type","file");
                            RecordUtils.onEvent(PdfPreviewActivity.this,getString(R.string.to_screen_competition_hint),params);
                            force = 1;
                            mPdfView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getScreenShot();
                                }
                            },50);
                            dialog.cancel();
                        }
                    }, new CommonDialog.OnCancelListener() {
                @Override
                public void onCancel() {
                    stopPdfPro();
                    HashMap<String,String> params = new HashMap<>();
                    params.put(getString(R.string.to_screen_competition_hint),"cancel");
                    params.put("type","file");
                    RecordUtils.onEvent(PdfPreviewActivity.this,getString(R.string.to_screen_competition_hint),params);
                    dialog.cancel();
                }
            },"继续投屏",true);
        }
        if(!isBackgroud&&!dialog.isShowing()) {
            dialog.show();
        }
    }

    private int small;
    private CommonDialog hotsDialog;
    private boolean isProjecting;
    private String mCurrentPic;
    private boolean isLoadComPlete;
    /**首次进入进行投屏*/
    private boolean isFirst = true;
    private CommonDialog mChangeWifiDiallog;

    private BaseProReqeust getBasePrepareInfo(PictureInfo pictureInfo){
        String imageType = ProjectionManager.getInstance().getImageType();
        String seriesId = ProjectionManager.getInstance().getSeriesId();
        BaseProReqeust prepareInfo = new BaseProReqeust();
        prepareInfo.setAction(pictureInfo.getAction());
        prepareInfo.setAssetname(pictureInfo.getAssetname());
//        prepareInfo.setAsseturl(pictureInfo.getAsseturl());
        prepareInfo.setImageId(pictureInfo.getImageId());
        prepareInfo.setImageType("2");
        prepareInfo.setSeriesId(seriesId);
        return prepareInfo;
    }
    private void showScreenShot(final PictureInfo pictureInfo) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (pictureInfo==null){
                        return;
                    }
                    String copyPath = "";
                    if (small==1){
                        copyPath = CompressImage.compressAndSaveBitmapForPdf(PdfPreviewActivity.this, pictureInfo.getAssetpath(),pictureInfo.getAssetname(),true);
                    }else{
                        copyPath = CompressImage.compressAndSaveBitmapForPdf(PdfPreviewActivity.this, pictureInfo.getAssetpath(),pictureInfo.getAssetname(),false);
                    }

                    pictureInfo.setAsseturl(NetWorkUtil.getLocalUrl(PdfPreviewActivity.this)+copyPath);
                    pictureInfo.setCompressPath(copyPath);

                    Message message = Message.obtain();
                    message.obj = pictureInfo;
                    message.what = DISPLAY;
                    mHandler.removeMessages(DISPLAY);
                    mHandler.sendMessageDelayed(message,100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private ProjectionService mProjectionService;
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mProjectionService = ((ProjectionService.ProjectionBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_preview);
        ScreenOrientationUtil.getInstance().start(this);
        ProjectionManager.getInstance().setSeriesId(System.currentTimeMillis()+"");
        initPresenter();
        getViews();
        setViews();
        setListeners();
        handleIntent();
        savePdfProjecting();
        // 绑定投屏服务
        bindProService();
    }

    private void bindProService() {
        Intent intent = new Intent(this, ProjectionService.class);
        intent.putExtra(ProjectionService.EXTRA_TYPE,ProjectionService.TYPE_VOD_NOMARL);
        bindService(intent,mServiceConn, Context.BIND_AUTO_CREATE);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        if(intent.hasExtra("isProjecting")) {
            isProjecting = intent.getBooleanExtra("isProjecting",false);
        }

        if(intent.hasExtra("isLockedScrren")) {
            isScreenLocked = intent.getBooleanExtra("isLockedScrren",false);
        }

        if(intent.hasExtra("scrrenOritation")) {
            scrrenOritation = intent.getIntExtra("scrrenOritation",-1);
        }

//        if(isProjecting) {
//            mExitBtn.setText("投屏");
//        }

        if(data!=null) {
            mPdfPath = data.getPath();
            displayPdf(data);
        }else {
            mPdfPath = intent.getStringExtra("path");
            pageNumber = intent.getIntExtra("pageNum",0);
            Uri uri = Uri.parse("file://" + mPdfPath);
            displayPdf(uri);
        }

        if(isScreenLocked) {
            if(scrrenOritation == Surface.ROTATION_90
                    || scrrenOritation == Surface.ROTATION_270
                    ) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }else{
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            mLockScreenIv.setImageResource(R.drawable.ico_yisuoping);
            ScreenOrientationUtil.getInstance().stop();
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            ScreenOrientationUtil.getInstance().start(this);
            mLockScreenIv.setImageResource(R.drawable.ico_suoping);
        }
    }

    public static void startPdfPreviewActivity(Context context,String path,int pageNumber,boolean isProjecting,boolean isLockedScreen,int scrrenOritation) {
        Intent intent = new Intent(context,PdfPreviewActivity.class);
        intent.putExtra("path",path);
        intent.putExtra("pageNum",pageNumber);
        intent.putExtra("isProjecting",isProjecting);
        intent.putExtra("isLockedScrren",isLockedScreen);
        intent.putExtra("scrrenOritation",scrrenOritation);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        getViews();
        setViews();
        setListeners();
        Uri data = intent.getData();
        if(data!=null) {
            mPdfPath = data.getPath();
            displayPdf(data);
        }else {
            if(intent.hasExtra("path")) {
                mPdfPath = intent.getStringExtra("path");
                pageNumber = intent.getIntExtra("pageNum",0);
                Uri uri = Uri.parse("file://" + mPdfPath);
                displayPdf(uri);
            }
        }

        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if(isProjecting) {
            savePdfProjecting();
        }
        setResult(FROM_APP_BACK);
        super.onBackPressed();
    }

    private void savePdfProjecting() {
        float zoom = mPdfView.getZoom();
        int currentPage = mPdfView.getCurrentPage();
        float currentXOffset = mPdfView.getCurrentXOffset();
        float currentYOffset = mPdfView.getCurrentYOffset();
        WindowManager mgr = getWindowManager();
        int orientation = mgr.getDefaultDisplay().getRotation();
        ProjectionManager.getInstance().setPdfProjection(PdfPreviewActivity.class,zoom,currentPage,currentXOffset,currentYOffset,mPdfPath,mCurrentPic,isScreenLocked,orientation);
    }

    @Override
    public void getViews() {
        mPdfView = (PDFView) findViewById(R.id.pdfview);
        mPdfNameTv = (TextView) findViewById(R.id.tv_center);
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mExitBtn = (TextView) findViewById(R.id.tv_right);
        mLockScreenIv = (ImageView) findViewById(R.id.iv_lockscreen);
        mPageCountTv = (TextView) findViewById(R.id.tv_page_count);
    }

    private void displayPdf(Uri uri) {
        mPdfPath = uri.getPath();
        File file = new File(mPdfPath);
        if(file.exists()) {
            savePdfhistory(file);
            String name = file.getName();
            mPdfNameTv.setText(name);
        }
        mPdfView.useBestQuality(true);
        mPdfView.fromUri(uri)
                .defaultPage(pageNumber)
                .onPageScroll(this)
                .onPageChange(this)
                .enableAnnotationRendering(false)
                .onLoad(this)
                .scrollHandle(null)
                .load();
        mPdfView.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirst = false;
                getScreenShot();
            }
        },isFirst?1000:100);
    }

    private void savePdfhistory(File file) {
        List<PdfInfo> pdfList = mSession.getPdfList();
        PdfInfo pdfInfo = new PdfInfo();
        pdfInfo.setName(file.getName());
        pdfInfo.setPath(file.getAbsolutePath());
        if(pdfList==null) {
            pdfList = new ArrayList<>();
        }
        if(!pdfList.contains(pdfInfo)) {
            pdfList.add(0,pdfInfo);
            mSession.setPdfList(pdfList);
        }
    }
    @Override
    public void setViews() {
        mExitBtn.setVisibility(View.VISIBLE);
        mExitBtn.setText("投屏");
        if(!mSession.isBindTv()) {
            mBindTvPresenter.getSmallPlatformUrl(false);
            if (hotsDialog == null) {
                if(hotsDialog==null) {
                    hotsDialog = new CommonDialog(this, getString(R.string.click_link_tv), new CommonDialog.OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put(getString(R.string.file_to_screen_link_tv),"link");
                            RecordUtils.onEvent(PdfPreviewActivity.this,getString(R.string.file_to_screen_link_tv),hashMap);
                            mBindTvPresenter.bindTv();
                        }
                    }, new CommonDialog.OnCancelListener() {
                        @Override
                        public void onCancel() {
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put(getString(R.string.file_to_screen_link_tv),"cancel");
                            RecordUtils.onEvent(PdfPreviewActivity.this,getString(R.string.file_to_screen_link_tv),hashMap);
                        }
                    },getString(R.string.link_tv));
                }
            }
            hotsDialog.show();
        }else {
            isProjecting = true;
//            mExitBtn.setText("退出投屏");
        }
    }

    @Override
    public void setListeners() {
        mBackBtn.setOnClickListener(this);
        mExitBtn.setOnClickListener(this);
        mLockScreenIv.setOnClickListener(this);
    }

    @Override
    public void onPageScrolled(final int page, float positionOffset) {
        LogUtils.d("pdf onPageScrolled page="+page+";positionOffset="+positionOffset);
        RecordUtils.onEvent(this,R.string.file_to_screen_drag);
        int pageCount = mPdfView.getPageCount();
        mPageCountTv.setText((page+1)+"/"+pageCount);
        mPageCountTv.setVisibility(View.VISIBLE);
        mHandler.removeMessages(HIDE_PAGE_HINT);
        mHandler.sendEmptyMessageDelayed(HIDE_PAGE_HINT,3000);
        boolean bindTv = mSession.isBindTv();
        if(bindTv) {
            if(isProjecting && !isFirst) {
                mPdfView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getScreenShot();
                    }
                },50);
            }
        }
    }

    private void getScreenShot() {
        mHandler.removeMessages(GET_SCREEN_SHOT);
        mHandler.sendEmptyMessageDelayed(GET_SCREEN_SHOT,50);
    }

    public static Bitmap getViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        LogUtils.d("pdf onPageChanged page="+page+";pageCount="+pageCount);
        RecordUtils.onEvent(this,R.string.file_to_screen_drag);
    }

    @Override
    public void loadComplete(int nbPages) {
        isLoadComPlete = true;
        PdfDocument.Meta meta = mPdfView.getDocumentMeta();
        LogUtils.d("pdf title = " + meta.getTitle());
        LogUtils.d(TAG+"author = " + meta.getAuthor());
        LogUtils.d(TAG+"subject = " + meta.getSubject());
        LogUtils.d(TAG+"keywords = " + meta.getKeywords());
        LogUtils.d(TAG+"creator = " + meta.getCreator());
        LogUtils.d(TAG+ "producer = " + meta.getProducer());
        LogUtils.d(TAG+"creationDate = " + meta.getCreationDate());
        LogUtils.d(TAG+ "modDate = " + meta.getModDate());
    }

    @Override
    public void showChangeWifiDialog() {
        mChangeWifiDiallog = new CommonDialog(this,
                getString(R.string.tv_bind_wifi) + "" + (TextUtils.isEmpty(mSession.getSsid()) ? "与电视相同的wifi" : mSession.getSsid())
                , new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                Intent intent = new Intent();
                intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                startActivity(intent);
            }
        }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {

            }
        },"去设置");
        mChangeWifiDiallog.show();
    }

    @Override
    public void readyForCode() {

    }

    @Override
    public void closeQrcodeDialog() {

    }

    @Override
    public void initBindcodeResult() {
        ShowMessage.showToast(this,"连接电视成功");
        isProjecting = true;
        mExitBtn.setText("退出投屏");
        small = 1;
//        currentImageId = System.currentTimeMillis()+"";

        ProjectionManager.getInstance().setSeriesId(System.currentTimeMillis()+"");
        Message message = Message.obtain();
        message.what = COMPRESS_PIC;
        mHandler.removeMessages(COMPRESS_PIC);
        mHandler.removeMessages(DISPLAY);
        mHandler.sendMessageDelayed(message,50);
    }

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
    public void startLinkTv() {
        Intent intent = new Intent(this,LinkTvActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == EXTRA_TV_INFO){
            initBindcodeResult();
//            if(data!=null) {
//                TvBoxInfo boxInfo = (TvBoxInfo) data.getSerializableExtra(EXRA_TV_BOX);
//                mBindTvPresenter.handleBindCodeResult(boxInfo);
//            }
        }else if(resultCode == EXTRA_TV_INFO){
            if(data!=null) {
                TvBoxInfo boxInfo = (TvBoxInfo) data.getSerializableExtra(EXRA_TV_BOX);
                mBindTvPresenter.handleBindCodeResult(boxInfo);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_lockscreen:
                changedLockBtnStatus();
                break;
            case R.id.iv_left:
                RecordUtils.onEvent(this,R.string.file_to_screen_back);
                onBackPressed();
                break;
            case R.id.tv_right:
                if(!AppUtils.isWifiNetwork(this)) {
                    new CommonDialog(this,"请前往手机设置，连接至电视同一WiFi下").show();
                }else{
                    if(mSession.isBindTv()) {
                        if(isProjecting) {
                            RecordUtils.onEvent(this,R.string.file_to_screen_exit);
                            String projectId = ProjectionManager.getInstance().getProjectId();
                            AppApi.notifyTvBoxStop(this,mSession.getTVBoxUrl(),projectId,this);
                        }else {
                            RecordUtils.onEvent(PdfPreviewActivity.this,R.string.file_to_screen_details_play);
                            mPdfView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getScreenShot();
                                }
                            },50);
                        }
                    }else {
                        mBindTvPresenter.bindTv();
                    }
                }

                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RecordUtils.onEvent(this,R.string.file_to_screen_rotating);
    }

    private void changedLockBtnStatus() {
        if(isScreenLocked) {
            isScreenLocked = false;
            mLockScreenIv.setImageResource(R.drawable.ico_suoping);
            ScreenOrientationUtil.getInstance().start(this);
        }else {
            RecordUtils.onEvent(this,R.string.file_to_screen_lock);
            isScreenLocked = true;
            mLockScreenIv.setImageResource(R.drawable.ico_yisuoping);
            ScreenOrientationUtil.getInstance().stop();
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_NOTIFY_TVBOX_STOP_JSON:
                stopPdfPro();
                break;
        }
    }

    public void stopPdfPro() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                force = 0;
                isProjecting = false;
                mExitBtn.setText("投屏");
                ProjectionManager.getInstance().resetProjection();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isBackgroud = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBackgroud = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog = null;
        if(mServiceConn!=null) {
            unbindService(mServiceConn);
        }
    }
}
