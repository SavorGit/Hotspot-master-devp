package com.savor.savorphone.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

import com.common.api.utils.LogUtils;
import com.savor.savorphone.R;
import com.savor.savorphone.activity.SlidesActivity;
import com.savor.savorphone.bean.ImageProResonse;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.bean.PictureInfo;
import com.savor.savorphone.bean.BaseProReqeust;
import com.savor.savorphone.bean.QuerySeekResponse;
import com.savor.savorphone.bean.QueryRequestVo;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.utils.ActivitiesManager;
import com.savor.savorphone.utils.CompressImage;
import com.savor.savorphone.utils.ConstantsWhat;
import com.savor.savorphone.utils.NetWorkUtil;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.widget.CommonDialog;

import java.util.HashMap;
import java.util.List;

/**
 *  投屏service
 * @author hezd
 */
public class ProjectionService extends Service implements ApiRequestListener {
    /** 参数，幻灯片总页数*/
    public static final String EXTRA_SLIDE_COUNT = "slide_count";
    /**幻灯片列表数据*/
    public static final String EXTRA_SLIDE_LIST = "slide_list";
    /**投屏类型，0视频投屏*/
    public static final String EXTRA_TYPE = "extra_type";
    /**普通点播*/
    public static final int TYPE_VOD_NOMARL = 1;
    /**宣传图点播*/
    public static final int TYPE_VOD_ADVERT = 2;
    /**本地视频投屏*/
    public static final int TYPE_VOD_VIDEO = 3;
    /**幻灯片投屏*/
    public static final int TYPE_VOD_SLIDE = 4;
    public static final int TYPE_PDF_PRO = 5;

    /**查询视频播放进度*/
    private static final int QUERY_SEEK = 0x1;
    /**幻灯片延时切换*/
    private static final int SLIDE = 0x2;
    /**显示图片*/
    private static final int DISPLAY = 0x3;
    private static final int HANDLE_IMAGE = 0x4;
    private static final int FORCE_MSG = 0x5;

    /**点播类型*/
    private int mProjectionType;
    /**视频投屏进度改变监听*/
    private OnProgressChangeListener mOnProgressChangeListener;
    private Session mSession;
    /**幻灯片总页数*/
    private int slideCount ;
    /**
     * 投屏时遇到别人正在投屏，传1代表确认抢投，默认传0
     */
    private int force=0;
    private CommonDialog dialog;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_SEEK:
                    String projectionid = ProjectionManager.getInstance().getProjectId();
                    AppApi.getSeekBySessionId(ProjectionService.this,mSession.getTVBoxUrl(),projectionid,ProjectionService.this);
                    querySeek();
                    break;
                case SLIDE:
                    int position = ProjectionManager.getInstance().getSlidePosition();
                    List<MediaInfo> imageList = ProjectionManager.getInstance().getSlideList();
                    if(position>=0) {
                        position++;

                        if(mOnSlideListener!=null) {
                            mOnSlideListener.onSlide();
                        }
                        boolean foreground = ProjectionManager.getInstance().isForeground();
                        if(imageList == null||imageList.size() ==0 || position>=imageList.size()) {
                            stopProjection();
                            Intent intent = new Intent("play_over");
                            sendBroadcast(intent);
                            switchDelayed = 5*1000;
                            if(mOnSlidePlayOverListener!=null) {
                                mHandler.removeMessages(DISPLAY);
                                mHandler.removeMessages(SLIDE);
                                mOnSlidePlayOverListener.onSlidePlayOver();
                            }
                        }
                        else if(!foreground){
                            LogUtils.d("savor:projection 幻灯片后台投屏");
                            if(position<imageList.size())
                                ProjectionManager.getInstance().setmSlidePosition(position);
                            Class projectionActivity = ProjectionManager.getInstance().getProjectionActivity();
                            if(imageList!=null&&projectionActivity==SlidesActivity.class) {
                                imageList.get(position).setImageId(System.currentTimeMillis()+"");
                                showImageToScreen(1);
                                slide(position);
                            }else {
                                stopSlide();
                            }
                        }
                    }else {
                        stopProjection();
                    }
                    break;
                case DISPLAY:
                    MediaInfo mediaInfo = (MediaInfo) msg.obj;
                    List<MediaInfo> pictureInfos = ProjectionManager.getInstance().getSlideList();
                    if(pictureInfos!=null) {
                        uploadImage(mediaInfo);
                    }
                    break;
                case FORCE_MSG:
                    String message = (String)msg.obj;
                    showConfirm(message);
                    break;
            }
        }
    };

    public void stopSlide() {
        mHandler.removeMessages(DISPLAY);
        mHandler.removeMessages(SLIDE);
//        stopProjection();
    }

    private Handler mImageHandler ;

    private OnProjectoinSuccessListener mOnProjectionSuccessListener;
    private OnProjectionErrorListener mProjectionErrorListener;
    private OnPlayOverListener mOnplayOverListener;
    private OnSlidePlayOverListener mOnSlidePlayOverListener;
    /**幻灯片延时播放时间单位秒*/
    private long switchDelayed = 5 *1000 ;
//    /**幻灯片当前位置*/
//    private int mCurrentSlidePosition;
    /**幻灯片页数*/
    private int mSlideCount;
//    private ArrayList<PictureInfo> mCheckPicList;
    /**幻灯片切换监听*/
    private OnSlideListener mOnSlideListener;
    /**是否是小图投屏*/
    private int small;
    /**幻灯片图片投屏*/
    private OnDisPlayImageListener mOnDisplayImageListener;
//    /**幻灯片播放状态，是否正在播放，暂停*/
//    private boolean isPalying;
//    /**是否运行在前台*/
//    private boolean isForeground;

    public ProjectionService() {
    }
    private Handler.Callback mImageHandleCallBack = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_IMAGE:
                    displayPic();
                    break;
            }
            return true;
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d("savor:projection onBind");
        mProjectionType = intent.getIntExtra(EXTRA_TYPE,-1);
        if(mProjectionType == TYPE_VOD_SLIDE) {
            mSlideCount = intent.getIntExtra(EXTRA_SLIDE_COUNT, 0);
//            mCheckPicList = (ArrayList<PictureInfo>) intent.getSerializableExtra(EXTRA_SLIDE_LIST);
        }
        return new ProjectionBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.d("savor:projection onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSession = Session.get(this);
        HandlerThread imageThread = new HandlerThread("handle_image");
        imageThread.start();
        mImageHandler = new Handler(imageThread.getLooper(),mImageHandleCallBack);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d("savor:projection onStartCommand");
        mProjectionType = intent.getIntExtra(EXTRA_TYPE,-1);
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        LogUtils.d("savor:projection onDestroy");
        super.onDestroy();
    }
    public void setForce(int force){
        this.force = force;
    }
    public void showImageToScreen(int small) {
        this.small = small;
        mImageHandler.sendEmptyMessage(HANDLE_IMAGE);
    }

    private synchronized void displayPic() {
        int position = ProjectionManager.getInstance().getSlidePosition();
        List<MediaInfo> mediaInfos = ProjectionManager.getInstance().getSlideList();
        if(mediaInfos==null||position>=mediaInfos.size())
            return;
        MediaInfo mediaInfo = mediaInfos.get(position);
        try {
            if (mediaInfo==null){
                return;
            }
            Bitmap bitmap = null;
            String copyPath ;
            if (small==1){
                copyPath = CompressImage.compressAndSaveBitmap(this, mediaInfo.getAssetpath(),mediaInfo.getAssetname(),true);
            }else{
                copyPath = CompressImage.compressAndSaveBitmap(this, mediaInfo.getAssetpath(),mediaInfo.getAssetname(),false);
            }

            mediaInfo.setAsseturl(NetWorkUtil.getLocalUrl(this)+copyPath);
            mediaInfo.setCompressPath(copyPath);

            Message message = Message.obtain();
            message.obj = mediaInfo;
            message.what = DISPLAY;
            mHandler.removeMessages(DISPLAY);
            mHandler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置幻灯片状态，播放暂停
     * @param isPlaying
     */
    public void setPlayStatus(boolean isPlaying) {
        ProjectionManager.getInstance().setSlideStatus(isPlaying);
        if(!isPlaying) {
            mHandler.removeMessages(SLIDE);
            mHandler.removeMessages(DISPLAY);
        }
    }

    public int getCurrentSlidePosition() {
        return ProjectionManager.getInstance().getSlidePosition();
    }

    public boolean isPalying() {
        return ProjectionManager.getInstance().isSlideStatus();
    }

    /**
     * 设置幻灯片延时时间
     * @param millins 毫秒
     */
    public void setDelayTime(long millins) {
        this.switchDelayed = millins;
    }

    public long getDelayTime() {
        return this.switchDelayed;
    }

    /**幻灯片延时切换*/
    public void slide(int currentPosition) {
        ProjectionManager.getInstance().setmSlidePosition(currentPosition);
        boolean isPlaying = ProjectionManager.getInstance().isSlideStatus();
        if(isPlaying) {
            LogUtils.d("savor:幻灯片延时切换");
            mHandler.removeMessages(SLIDE);
            mHandler.sendEmptyMessageDelayed(SLIDE, switchDelayed);
        }else {
            LogUtils.d("savor:当前不可切换 isplaying false");
        }
    }

    public void slidePlayOver() {
        if(mOnSlidePlayOverListener!=null)
            mOnSlidePlayOverListener.onSlidePlayOver();
    }

    public void stopProjection() {
        String projectId = ProjectionManager.getInstance().getProjectId();
        AppApi.notifyTvBoxStop(this,mSession.getTVBoxUrl(),projectId,this);

    }

    public void querySeek() {
        QueryRequestVo queryRequestVo = new QueryRequestVo();
        queryRequestVo.setFunction(ConstantsWhat.FunctionsIds.QUERY);
        queryRequestVo.setWhat("pos@" + mSession.getSessionID());
        Message obtain = Message.obtain();
        obtain.what = QUERY_SEEK;
        obtain.obj = queryRequestVo;
        mHandler.removeMessages(QUERY_SEEK);
        mHandler.sendMessageDelayed(obtain,1000);
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case GET_QUERY_SEEK_JSON:
                if(obj instanceof QuerySeekResponse) {
                    QuerySeekResponse queryPosBySessionIdResponseVo = (QuerySeekResponse) obj;
                    int result = queryPosBySessionIdResponseVo.getResult();
                    if(result==1||result==-1) {
                        if(mOnplayOverListener!=null) {
                            mOnplayOverListener.onPlayOver(result);
                        }
                    }
                    if(mOnProgressChangeListener!=null)
                        mOnProgressChangeListener.progressChange(queryPosBySessionIdResponseVo);

                }
                break;
            case POST_IMAGE_PROJECTION_JSON:
//                if(obj instanceof ImageProResonse) {
//                    ImageProResonse resonse = (ImageProResonse) obj;
//                }
                if (small==1){
                    small = 0;
                    showImageToScreen(small);
                    return;
                }
//                slide(mCurrentSlidePosition);
                break;
        }
    }

    public void stopQuerySeek() {
        mHandler.removeMessages(QUERY_SEEK);
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        if(mProjectionErrorListener!=null) {
            mProjectionErrorListener.onProjectoinErrorListener(method,obj);
        }
    }

    @Override
    public void onNetworkFailed(AppApi.Action method) {

    }

    public void uploadImage(final MediaInfo pictureInfo) {
        AppApi.updateScreenProjectionFile(this, mSession.getTVBoxUrl(), getBasePrepareInfo(pictureInfo), pictureInfo.getCompressPath(), small, force,new ApiRequestListener() {
            @Override
            public void onSuccess(AppApi.Action method, Object obj) {
                if(mProjectionType!=TYPE_VOD_SLIDE)
                    return;
                if(mOnProjectionSuccessListener!=null) {
                    mOnProjectionSuccessListener.onProjectionSuccess(method,obj);
                }
                switch(method) {
                    case POST_IMAGE_PROJECTION_JSON:
                        if(obj instanceof ImageProResonse) {
                            ImageProResonse resonse = (ImageProResonse) obj;
                            String projectId = resonse.getProjectId();
                            ProjectionManager.getInstance().setProjectId(projectId);
                        }
                        int position = ProjectionManager.getInstance().getSlidePosition();
                        List<MediaInfo> slideList = ProjectionManager.getInstance().getSlideList();
                        MediaInfo mediaInfo = slideList.get(position);
                        if (small==1&&mediaInfo.getImageId().equals(pictureInfo.getImageId())){
                            small = 0;
                            showImageToScreen(small);
                            return;
                        }
                }
            }

            @Override
            public void onError(AppApi.Action method, Object obj) {
                switch (method) {
                    case POST_IMAGE_PROJECTION_JSON:
                        Activity activity = ActivitiesManager.getInstance().getCurrentActivity();
                        if (activity.getClass()== SlidesActivity.class){
                            if(mProjectionErrorListener!=null) {
                                mProjectionErrorListener.onProjectoinErrorListener(method,obj);
                            }
                        }else{
                            ResponseErrorMessage message = (ResponseErrorMessage) obj;
                            int code = message.getCode();
                            if(code==4){
                                Message msg = Message.obtain();
                                msg.what = FORCE_MSG;
                                msg.obj = message.getMessage();
                                mHandler.sendMessage(msg);
                            }
                        }


                        break;
                }
            }

            @Override
            public void onNetworkFailed(AppApi.Action method) {

            }
        });
    }

    /**
     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
     * @param msg
     */
    private void showConfirm(String msg){
        if (dialog!=null&&dialog.isShowing()){
            return;
        }
        String content = "当前"+msg+"正在投屏,是否继续投屏?";
        Activity activity = ActivitiesManager.getInstance().getCurrentActivity();
        if (activity == null){
            return;
        }
        dialog = new CommonDialog(activity, content,
                new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        HashMap<String,String> params = new HashMap<>();
                        params.put(getString(R.string.to_screen_competition_hint),"ensure");
                        params.put("type","grouppic");
                        RecordUtils.onEvent(ProjectionService.this,getString(R.string.to_screen_competition_hint),params);
                        force = 1;
                        int position = ProjectionManager.getInstance().getSlidePosition();
                        slide(position);
                        dialog.cancel();
                    }
                }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                HashMap<String,String> params = new HashMap<>();
                params.put(getString(R.string.to_screen_competition_hint),"cancel");
                params.put("type","grouppic");
                RecordUtils.onEvent(ProjectionService.this,getString(R.string.to_screen_competition_hint),params);
                dialog.cancel();
            }
        },"继续投屏",true);
        dialog.show();
    }

    private BaseProReqeust getBasePrepareInfo(MediaInfo pictureInfo){
        String imageType = ProjectionManager.getInstance().getImageType();
        String seriesId = ProjectionManager.getInstance().getSeriesId();
        BaseProReqeust prepareInfo = new BaseProReqeust();
        prepareInfo.setAction(pictureInfo.getAction());
        prepareInfo.setAssetname(pictureInfo.getAssetname());
//        prepareInfo.setAsseturl(pictureInfo.getAsseturl());
        prepareInfo.setImageId(pictureInfo.getImageId());
        if("3".equals(imageType)) {
            prepareInfo.setImageType(imageType);
            prepareInfo.setSeriesId(seriesId);
        }
        return prepareInfo;
    }

//    public void stopSlide() {
//        ProjectionManager.getInstance().setmSlidePosition(-1);
//        if (mCheckPicList!=null){
//            mCheckPicList.clear();
//        }
//
//        mHandler.removeMessages(SLIDE);
//        mHandler.removeMessages(DISPLAY);
//        mImageHandler.removeMessages(HANDLE_IMAGE);
//        mImageHandler.removeCallbacksAndMessages(null);
//        mHandler.removeCallbacksAndMessages(null);
//    }

    /**是否运行在前台*/
    public void isForeground(boolean isForeground) {
        ProjectionManager.getInstance().setForegroundStatus(isForeground);
    }

    /**
     * 处理service与activity交互的binder对象
     */
    public class ProjectionBinder extends Binder {
        public ProjectionService getService(){
            return ProjectionService.this;
        }
    }

    public void setOnDisplayImageListener(OnDisPlayImageListener listener) {
        this.mOnDisplayImageListener = listener;

    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        this.mOnProgressChangeListener = listener;
    }

    public void setOnProjectionSuccessListener(OnProjectoinSuccessListener listener) {
        this.mOnProjectionSuccessListener = listener;

    }

    public void setOnProjectionErrorListener(OnProjectionErrorListener listener) {
        this.mProjectionErrorListener = listener;
    }

    public void setOnplayOverListener(OnPlayOverListener onplayOverListener) {
        this.mOnplayOverListener = onplayOverListener;
    }

    public void setOnSlidePlayOverLisetener(OnSlidePlayOverListener lisetener) {
        this.mOnSlidePlayOverListener = lisetener;
    }

    public void setOnSlideListener(OnSlideListener listener) {
        this.mOnSlideListener = listener;
    }

    public interface OnPlayOverListener {
        void onPlayOver(int result);
    }

    /**
     * 视频播放进度监听器
     */
    public interface OnProgressChangeListener {
        void progressChange(QuerySeekResponse progressInfo);
    }

    /**
     * 投屏失败监听
     */
    public interface OnProjectionErrorListener {
        void onProjectoinErrorListener(AppApi.Action method,Object obj);
    }

    /**
     * 投屏成功监听
     */
    public interface OnProjectoinSuccessListener {
        void onProjectionSuccess(AppApi.Action method,Object response);
    }

    /**
     * 当幻灯片播放完
     */
    public interface OnSlidePlayOverListener {
        void onSlidePlayOver();
    }

    /**
     * 幻灯片切换监听
     */
    public interface OnSlideListener {
        void onSlide();
    }

    /**
     * 幻灯片投屏监听
     */
    public interface OnDisPlayImageListener {
        void onDisplayImage(PictureInfo info);
    }
}
