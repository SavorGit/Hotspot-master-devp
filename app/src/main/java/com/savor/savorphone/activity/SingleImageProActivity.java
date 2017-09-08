package com.savor.savorphone.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.FileUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.ImageGalleryAdapter;
import com.savor.savorphone.bean.BaseProReqeust;
import com.savor.savorphone.bean.ImageProResonse;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.bean.RotateRequest;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.utils.CompressImage;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.LinkDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.savor.savorphone.activity.HotspotMainActivity.SCAN_QR;
import static com.savor.savorphone.activity.LinkTvActivity.EXTRA_TV_INFO;

public class SingleImageProActivity extends BaseProActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final String KEY_IS_PROJECTING = "key_is_projecting";
    private static final String KEY_PROJECT_ID = "key_project_id";
    private static final String KEY_POSITION = "key_position";
    private static final int EDIT_PIC = 0x1;
    public static final int SAVE_COMPOUND_FINISH = 100;
    public static final int PRO_IMAGE = 101;
    public static final int SCREEN_SUCESS = 102;
    public static final int ERROR_MSG = 103;
    public static final int FORCE_MSG = 104;
    private static final int START_PRO = 105;
    private boolean isProjecting;
    private String projectId;
    private TextView tv_exit;
    private ImageView mBackBtn;
    private CommonDialog hotsDialog;
    private int mCurrentRotate;
    private int small;
    private int force;
    private LinkDialog mToScreenDialog;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_PRO:
                    MediaInfo info = (MediaInfo) msg.obj;
                    startProjection(info);
                    break;
                case FORCE_MSG:
                    String messag = (String) msg.obj;
                    showConfirm(messag);
                    break;
                case ERROR_MSG:
                    String erromsg = (String) msg.obj;
                    showToast(erromsg);
                    stopPro();
                    break;
                case SCREEN_SUCESS:
                    if(isProjecting)
                        tv_exit.setText("退出投屏");
                    String imid = (String) msg.obj;
                    if (small==1&&isProjecting){
                        MediaInfo currentImageInfo = getCurrentImageInfo();
                        if(imid.equals(currentImageInfo.getImageId())) {
                            small = 0;
                            startProjection(currentImageInfo);
                        }
                        ProjectionManager.getInstance().setImageProjection(SingleImageProActivity.class,picList,projectId,position);
                        return;
                    }
                    break;
                case PRO_IMAGE:
                    final MediaInfo mediaInfo = (MediaInfo) msg.obj;
                    BaseProReqeust basePrepareInfo = getBasePrepareInfo(mediaInfo);
                    if(!TextUtils.isEmpty(mediaInfo.getCompoundPath())) {
                        basePrepareInfo.setRotatevalue(mediaInfo.getComRotateValue());
                    }
                    AppApi.updateScreenProjectionFile(mContext, mSession.getTVBoxUrl(), basePrepareInfo, mediaInfo.getCompressPath(), small, force, new ApiRequestListener() {
                        @Override
                        public void onSuccess(AppApi.Action method, Object obj) {
                            switch (method) {
                                case POST_IMAGE_PROJECTION_JSON:
                                    dismissScreenDialog();
                                    isProjecting = true;
                                    if (obj instanceof ImageProResonse) {
                                        ImageProResonse proResonseInfo = (ImageProResonse) obj;
                                        projectId = proResonseInfo.getProjectId();
                                        ProjectionManager.getInstance().setProjectId(projectId);
                                    }
                                    Message message = Message.obtain();
                                    message.what = SCREEN_SUCESS;
                                    message.obj = mediaInfo.getImageId();
                                    if (mHandler != null)
                                        mHandler.sendMessage(message);
                                    break;
                            }
                        }

                        @Override
                        public void onError(AppApi.Action method, Object obj) {
                            dismissScreenDialog();
                            switch (method) {
                                case POST_IMAGE_PROJECTION_JSON:
                                    isProjecting = false;
                                    break;
                            }
                            if(obj instanceof ResponseErrorMessage) {
                                ResponseErrorMessage message = (ResponseErrorMessage) obj;
                                int code = message.getCode();
                                Message msg = Message.obtain();
                                if (code==4){
                                    String meesg = message.getMessage();
                                    msg.what = FORCE_MSG;
                                    msg.obj = meesg;
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
                case SAVE_COMPOUND_FINISH:
                    if(isProjecting) {
//                        ProjectionManager.getInstance().setImageProjection(SingleImageProActivity.class,mCurrentMediaInfo,projectId,position);
                    }else {
                        ProjectionManager.getInstance().resetProjection();
                    }
                    setResult(HotspotMainActivity.FROM_APP_BACK);
                    finish();
                    break;
            }
        }
    };
    private CommonDialog dialog;
    private CommonDialog mChangeWifiDiallog;
    private ViewPager mImageGalleryVp;
    private List<MediaInfo> picList;
    private int position;
    private ImageGalleryAdapter mGalleryAdapter;


    // region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSession = Session.get(this);
        setContentView(R.layout.activity_local_image_gallery);
        mContext = this;
        handleIntent();
        initPresenter();
        getViews();
        setViews();
        setListeners();
        if(isProjecting) {
            ProjectionManager.getInstance().setImageProjection(SingleImageProActivity.class,picList,projectId,position);
        }
    }


    @Override
    public void getViews() {
        mImageGalleryVp = (ViewPager) findViewById(R.id.viewpager);
//        mImageGalleryVp.setOffscreenPageLimit(5);
        mGalleryAdapter = new ImageGalleryAdapter(this,picList);
        mImageGalleryVp.setAdapter(mGalleryAdapter);
        mImageGalleryVp.setCurrentItem(position);
        tv_exit = (TextView) findViewById(R.id.tv_right);
        tv_exit.setVisibility(View.VISIBLE);
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
    }

    @Override
    public void setViews() {
        if(isProjecting) {
            tv_exit.setText("退出投屏");
        }else {
            tv_exit.setText("投屏");
        }

        if(!mSession.isBindTv()) {
            if(hotsDialog==null) {
                hotsDialog = new CommonDialog(this, getString(R.string.click_link_tv), new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put(getString(R.string.picture_to_screen_link_tv),"link");
                        RecordUtils.onEvent(SingleImageProActivity.this,getString(R.string.picture_to_screen_link_tv),hashMap);
                        mBindTvPresenter.bindTv();
                    }
                }, new CommonDialog.OnCancelListener() {
                    @Override
                    public void onCancel() {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put(getString(R.string.picture_to_screen_link_tv),"link");
                        RecordUtils.onEvent(SingleImageProActivity.this,R.string.picture_to_screen_link_tv,"cancel");
                        RecordUtils.onEvent(SingleImageProActivity.this,getString(R.string.picture_to_screen_link_tv),hashMap);
                    }
                },getString(R.string.link_tv));
            }
            hotsDialog.show();
        }

    }


    public void setListeners() {
        mBackBtn.setOnClickListener(this);
        tv_exit.setOnClickListener(this);
        mImageGalleryVp.addOnPageChangeListener(this);
    }

    private void handleIntent() {
//        mCurrentMediaInfo = (MediaInfo) getIntent().getSerializableExtra(KEY_MEDIA_INFO);
        isProjecting = getIntent().getBooleanExtra(KEY_IS_PROJECTING, false);
        projectId = getIntent().getStringExtra(KEY_PROJECT_ID);
        position = getIntent().getIntExtra(KEY_POSITION, 0);
        picList = ProjectionManager.getInstance().getSingleInfo();
        if(isProjecting) {
            ProjectionManager.getInstance().setImageProjection(SingleImageProActivity.class,picList,projectId,position);
        }
    }

    public static void startImageGallery(Context context, boolean isProjecting, String projectionId,int position) {
        Intent intent = new Intent(context,SingleImageProActivity.class);
        intent.putExtra(KEY_IS_PROJECTING,isProjecting);
        intent.putExtra(KEY_PROJECT_ID,projectionId);
        intent.putExtra(KEY_POSITION,position);
        context.startActivity(intent);
    }


    public void edit(View view) {
        RecordUtils.onEvent(mContext,getString(R.string.picture_to_screen_add_text));
        MediaInfo mediaInfo = getCurrentImageInfo();
        Intent intent = new Intent(this,ImageEditActivity.class);
        intent.putExtra("pic",mediaInfo);
        String path = mediaInfo.getAssetpath();
        intent.putExtra("path",path);

        intent.putExtra("rotate",mediaInfo.getRotatevalue());
        startActivityForResult(intent,EDIT_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImageEditActivity.COMPLETE) {
            if(data!=null) {
                MediaInfo  mediaInfo = (MediaInfo) data.getSerializableExtra("pic");
                MediaInfo currentImageInfo = getCurrentImageInfo();
                currentImageInfo.setCompoundPath(mediaInfo.getCompoundPath());
                currentImageInfo.setComRotateValue(0);
                currentImageInfo.setDateText(mediaInfo.getDateText());
                currentImageInfo.setDesText(mediaInfo.getDesText());
                currentImageInfo.setPrimaryText(mediaInfo.getPrimaryText());
                mediaInfo.setComRotateValue(0);
                small = 1;
                force = 0;
                String imageId = System.currentTimeMillis()+"";
                currentImageInfo.setImageId(imageId);
                if(isProjecting) {
                    handleProDelayed(currentImageInfo);
                }
                mGalleryAdapter.notifyDataSetChanged();
            }

        }else if (resultCode == SCAN_QR) {
            if(data!=null) {
                String scanResult = data.getStringExtra("scan_result");
                mBindTvPresenter.handleQrcodeResult(scanResult);
                LogUtils.d("扫描结果：" + scanResult);
            }
        }else if(resultCode == EXTRA_TV_INFO){
            initBindcodeResult();
//            if(data!=null) {
//                TvBoxInfo boxInfo = (TvBoxInfo) data.getSerializableExtra(EXRA_TV_BOX);
//                mBindTvPresenter.handleBindCodeResult(boxInfo);
//            }
        }
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    // endregion

    public void rotate(View view) {
        RecordUtils.onEvent(mContext,getString(R.string.picture_to_screen_rotating));
        if(isProjecting) {
            RotateRequest rotateRequest = new RotateRequest();
            rotateRequest.setProjectId(projectId);
            AppApi.notifyTvBoxRotate(this,mSession.getTVBoxUrl(), rotateRequest,this);
        }else {
            performRotate();
        }

    }

    private void performRotate() {
        MediaInfo info = getCurrentImageInfo();
        int rotatevalue = info.getRotatevalue();
        rotatevalue+=90;
        String compoundPath = info.getCompoundPath();
        if(!TextUtils.isEmpty(compoundPath)) {
            int rotateV = info.getComRotateValue();
            rotateV+=90;
            info.setComRotateValue(rotateV%360);
        }
        info.setRotatevalue(rotatevalue%360);
        mGalleryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                RecordUtils.onEvent(mContext,getString(R.string.picture_to_screen_back_list));
                onBackPressed();
                break;
            case R.id.tv_right:
                exitProjection();
                break;
        }
    }

    private void exitProjection() {
        if(isProjecting) {
            stopProjection();
        }else {
            RecordUtils.onEvent(mContext,getString(R.string.picture_to_screen_play));
            if(!AppUtils.isWifiNetwork(this)) {
                new CommonDialog(this,"请前往手机设置，连接至电视同一WiFi下").show();
            }else {
                if(mSession.isBindTv()) {
                    showToScreenDialog("正在投屏");
                    small = 1;
                    force = 0;

                    MediaInfo mediaInfo = getCurrentImageInfo();
                    if(mediaInfo!=null) {
                        String currentImageId = System.currentTimeMillis()+"";
                        mediaInfo.setImageId(currentImageId);
                        mediaInfo.setImageId(System.currentTimeMillis()+"");
                        ProjectionManager.getInstance().setSeriesId(System.currentTimeMillis()+"");
                        ProjectionManager.getInstance().setImageType("1");
                        handleProDelayed(mediaInfo);
                    }
                    if(isProjecting) {
                        ProjectionManager.getInstance().setImageProjection(SingleImageProActivity.class,picList,projectId,position);
                    }
                }else {
                    mBindTvPresenter.bindTv();
                }
            }

        }
    }

    private void handleProDelayed(MediaInfo mediaInfo) {
        Message message = Message.obtain();
        message.what = START_PRO;
        message.obj = mediaInfo;
        mHandler.removeMessages(START_PRO);
        mHandler.sendMessageDelayed(message,100);
    }

    private void stopProjection() {
        showToScreenDialog("退出投屏...");

        RecordUtils.onEvent(mContext,getString(R.string.picture_to_screen_exit_screen));
        AppApi.notifyTvBoxStop(this,mSession.getTVBoxUrl(),projectId,this);
    }

    private void showToScreenDialog(String content) {
        mToScreenDialog = new LinkDialog(this,content);
        mToScreenDialog.show();
    }

    @Override
    public void onBackPressed() {
        // 如果当前展示的是合成图将合成图保存到热点文件夹，判断是否是投屏状态，如果是保存投屏状态
        MediaInfo currentImageInfo = getCurrentImageInfo();
        String filePath = currentImageInfo.getCompoundPath();
        if(!TextUtils.isEmpty(filePath)) {
            final File file = new File(filePath);
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            absolutePath += absolutePath.endsWith(File.separator) ? "" : File.separator;
            final String fileDir = absolutePath+"热点儿";
            if(!file.exists())
                file.mkdirs();

            // 保存合成图
            new Thread(){
                @Override
                public void run() {
                    FileUtils.copyFile(file,fileDir,System.currentTimeMillis()+".png",null);
                    mHandler.sendEmptyMessage(SAVE_COMPOUND_FINISH);
                }
            }.start();
        }

        // 判断当前是否是投屏状态,如果是保存
        if(isProjecting) {
            int currentItem = mImageGalleryVp.getCurrentItem();
            ProjectionManager.getInstance().setImageProjection(SingleImageProActivity.class,picList,projectId,currentItem);
        }else {
            ProjectionManager.getInstance().resetProjection();
        }
        super.onBackPressed();
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        dismissScreenDialog();
        switch (method) {
            case POST_NOTIFY_TVBOX_STOP_JSON:
                stopPro();
                break;
            case POST_PHOTO_ROTATE_JSON:
                performRotate();
                break;
        }
    }

    public void stopPro() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                isProjecting = false;
                tv_exit.setText("投屏");
            }
        });
    }

    private void startProjection(final MediaInfo info) {
        new Thread(){
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                LogUtils.d("savor:图片处理开始时间戳 "+startTime);
                String assetpath = info.getAssetpath();
                String compoundPath = info.getCompoundPath();

                if(!TextUtils.isEmpty(compoundPath)) {
                    assetpath = compoundPath;
                }
                String copyPath ;
                if (small==1){
                    copyPath = CompressImage.compressAndSaveBitmap(SingleImageProActivity.this, assetpath,info.getAssetname(),true);
                }else{
                    copyPath =  CompressImage.compressAndSaveBitmap(SingleImageProActivity.this, assetpath,info.getAssetname(),false);
                }
                long endTime = System.currentTimeMillis();
                LogUtils.d("savor:图片处理结束时间戳 "+System.currentTimeMillis()+";共用时 "+(endTime-startTime)/1000+"秒");
                info.setCompressPath(copyPath);
                Message message = Message.obtain();
                message.obj = info;
                message.what = PRO_IMAGE;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    private void dismissScreenDialog() {
        if(mToScreenDialog != null) {
            mToScreenDialog.dismiss();
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        dismissScreenDialog();
        switch (method) {
            case POST_IMAGE_PROJECTION_JSON:
                isProjecting = false;
                break;
        }
        if(obj instanceof ResponseErrorMessage) {
            ResponseErrorMessage message = (ResponseErrorMessage) obj;
            int code = message.getCode();
            Message msg = Message.obtain();
            if (code==4){
                String meesg = message.getMessage();
                msg.what = FORCE_MSG;
                msg.obj = meesg;
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



    @Override
    public void readyForCode() {
        if(mQrcodeDialog==null)
            mQrcodeDialog = new LinkDialog(this,getString(R.string.call_qrcode));
        mQrcodeDialog.show();
    }


    @Override
    public void initBindcodeResult() {
        super.initBindcodeResult();
        isProjecting = true;
//        ProjectionManager.getInstance().setImageProjection(SingleImageProActivity.class,mCurrentMediaInfo,projectId);
        small = 1;
        MediaInfo mediaInfo = getCurrentImageInfo();
        mediaInfo.setImageId(System.currentTimeMillis()+"");
        handleProDelayed(mediaInfo);
    }

    private MediaInfo getCurrentImageInfo() {
        int currentItem = mImageGalleryVp.getCurrentItem();
        return picList.get(currentItem);
    }


    /**
     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
     */
    private void showConfirm(final String messag){
        String content = "当前"+messag+"正在投屏,是否继续投屏?";
        dialog = new CommonDialog(this, content,
                new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        HashMap<String,String> params = new HashMap<>();
                        params.put(getString(R.string.to_screen_competition_hint),"ensure");
                        params.put("type","pic");
                        RecordUtils.onEvent(SingleImageProActivity.this,getString(R.string.to_screen_competition_hint),params);
                        force = 1;
                        startProjection(getCurrentImageInfo());
                        dialog.cancel();
                    }
                }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                HashMap<String,String> params = new HashMap<>();
                params.put(getString(R.string.to_screen_competition_hint),"cancel");
                params.put("type","pic");
                RecordUtils.onEvent(SingleImageProActivity.this,getString(R.string.to_screen_competition_hint),params);
                dialog.cancel();
            }
        },"继续投屏",true);
        dialog.show();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        RecordUtils.onEvent(this,R.string.picture_to_screen_switch);
        small = 1;
        force = 0;
        MediaInfo currentImageInfo = getCurrentImageInfo();
        currentImageInfo.setImageId(System.currentTimeMillis()+"");
        if(isProjecting) {
            handleProDelayed(currentImageInfo);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
