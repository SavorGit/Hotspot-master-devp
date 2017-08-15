package com.savor.savorphone.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.adapter.MediaAdapter;
import com.savor.savorphone.adapter.MediaCatogoryAdapter;
import com.savor.savorphone.bean.BaseProReqeust;
import com.savor.savorphone.bean.ImageProResonse;
import com.savor.savorphone.bean.LocalVideoProPesponse;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.utils.CompressImage;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.MediaUtils;
import com.savor.savorphone.utils.NetWorkUtil;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.LinkDialog;
import com.savor.savorphone.widget.LoadingProgressDialog;

import net.ypresto.androidtranscoder.MediaTranscoder;
import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.savor.savorphone.activity.LinkTvActivity.EXRA_TV_BOX;
import static com.savor.savorphone.activity.LinkTvActivity.EXTRA_TV_INFO;

/**
 * 媒体文件（图片和视频）列表
 */
public class MediaGalleryActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    /**当前状态普通*/
    public static final int STATE_NORMAL = 0x1;
    /**当前状态选择图片*/
    public static final int STATE_SELECT_IMAGE = 0x2;
    /**选择分类（弹窗）*/
    public static final int STATE_SELECT_CATEGORY = 0x3;
    private static final int SINGLE_PRO = 0x10;

    private int mCurrentState = STATE_NORMAL;
    /**当前投屏类型,幻灯片投屏*/
    public static final int TYPE_PRO_SLIDES = 0x4;
    /**当前投屏类型，单张投屏*/
    public static final int TYPE_PRO_SINGLE = 0x5;
    /**当前投屏类型，视频投屏*/
    public static final int TYPE_PRO_VIDEO = 0x6;
    private static final int SHOW_ERROR_MSG = 0x7;
    private static final int FORCE_MSG = 0x8;
    private static final int SLIDES_PRO = 0x9;
    /**当前投屏类型，默认单张投屏*/
    private int mCurrentProType = TYPE_PRO_SINGLE;
    private GridView mMediasGv;
    private MediaAdapter mMediaListAdapter;
    private TextView mTitleTv;
    private MediaCatogoryAdapter mMediaCategoryApdapter;
    private LinearLayout mMediaListLayout;
    private ImageView mBackIv;
    private TextView mLeftTv;
    private TextView mRightTv;
    private ListView mMediaLv;
    private RelativeLayout mProBtn;
    private LoadingProgressDialog mLoadingDialog;
    private MediaInfo mCurrentVideoInfo;
    private String mCacheVideoPath;
    /**是否已取消视频处理*/
    private boolean isCancelVideoHandle;
    private String projectId;
    private ImageView toscreen;
    /**
     * 首先进来投屏先显示小图，在显示大图
     */
    private int small;
    private List<MediaInfo> mImageList = new ArrayList<>();
    private List<MediaInfo> mVideoList = new ArrayList<>();
    private List<MediaInfo> mMediaList = new ArrayList<>();
    private List<MediaInfo> mSelectedList = new ArrayList<>();
    private Map<String, List<MediaInfo>> photoMap = new HashMap<>();
    /**加载本地图片完成*/
    public static final int LOAD_COMPLETE = 0x1;
    /**图片投屏*/
    public static final int PRO_IMAGE = 0x2;

    /**图片视频*/
    private static final int PROCESS_VIDEO = 0x11;

    private static final int HANDLE_OVER = 0x12;

    private static final int UPDATE_PROGRESS = 0x13;
    /**
     * 当投屏时遇到大屏正在投屏中，抢投传1，代表确认抢投，默认传0
     */
    private int force=0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SINGLE_PRO:
                    startSingleProjection();
                    break;
                case SLIDES_PRO:
                    startSlidesProjection();
                    break;
                case SHOW_ERROR_MSG:
                   String mess = (String) msg.obj;
                    if(TextUtils.isEmpty(mess)) {
                        mess = "投屏失败";
                        ShowMessage.showToast(MediaGalleryActivity.this,mess);
                    }else {
                        showToast(mess);
                    }
                    dismissProLoadingDialog();
                    break;
                case LOAD_COMPLETE:
                    mProgressBar.setVisibility(View.GONE);
                    showMedia();
                    initMediaList();
                    break;
                case PRO_IMAGE:
                    MediaInfo mediaInfo = (MediaInfo) msg.obj;
                    BaseProReqeust basePrepareInfo = getBasePrepareInfo(mediaInfo);
                    AppApi.updateScreenProjectionFile(mContext,mSession.getTVBoxUrl(), basePrepareInfo,mediaInfo.getCompressPath(),small,force,MediaGalleryActivity.this);
                    break;
                case FORCE_MSG:
                   String message = (String)msg.obj;
                    showConfirm(message);
                    break;
                case PROCESS_VIDEO:
                    MediaInfo mVideoInfo = (MediaInfo) msg.obj;
                    handleVideo(mVideoInfo);
                    break;
                case HANDLE_OVER:
                    if(isCancelVideoHandle)
                        return;
                    if(mSession.isBindTv()) {
                        MediaInfo VideoInfo = (MediaInfo) msg.obj;
                        AppApi.localVideoPro(MediaGalleryActivity.this,mSession.getTVBoxUrl(),VideoInfo,force,MediaGalleryActivity.this);
                    }else {
                        dismissLoadingDialog();
                        Intent intent = new Intent(MediaGalleryActivity.this,LocalVideoProAcitvity.class);
                        intent.putExtra("ModelVideo", mCurrentVideoInfo);
                        intent.putExtra("isPlaying",false);
                        startActivity(intent);
                        resetNormalState();
                    }
                    break;
                case UPDATE_PROGRESS:
                    String progress = (String) msg.obj;
                    if(mLoadingDialog!=null) {
                        mLoadingDialog.updatePercent(progress);
                    }
                    break;



            }
        }
    };
    private LinkDialog mProDialog;
    private String mProjectId;
    /**呼码提示窗*/
    private LinkDialog mQrcodeDialog;
    /**抢投提示对话框*/
    private CommonDialog forceDialog;
    /**单张投屏*/
    private MediaInfo mSingleInfo;
    /**视频投屏*/
    private MediaInfo mVideoInfo;
    private CommonDialog mChangeWifiDiallog;
    /**是否是全选状态*/
    private boolean isSelectAll;

    private void showMedia() {
        mMediaListAdapter.setData(mMediaList);
    }

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        RecordUtils.onEvent(this,R.string.album_toscreen_enter);

        initProjectionService();
        initPresenter();
        getViews();
        setViews();
        setListeners();
    }

    public void getViews() {
        mTitleTv = (TextView) findViewById(R.id.tv_center);
        mBackIv = (ImageView) findViewById(R.id.iv_left);
        mLeftTv = (TextView) findViewById(R.id.tv_left);
        mRightTv = (TextView) findViewById(R.id.tv_right);
        mMediasGv = (GridView) findViewById(R.id.gv_medias);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mMediaListLayout = (LinearLayout) findViewById(R.id.ll_media_list);
        mMediaLv = (ListView) findViewById(R.id.listview);
        mProBtn = (RelativeLayout) findViewById(R.id.rl_toscreen);
        toscreen = (ImageView) findViewById(R.id.toscreen);
    }

    public void setViews() {
        mTitleTv.setText("图片和视频");
        mTitleTv.setCompoundDrawablesWithIntrinsicBounds(null,null, getResources().getDrawable(R.drawable.ico_arraw_down),null);
        mRightTv.setVisibility(View.VISIBLE);
        mRightTv.setText("选择");
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(mImageList!=null) {
            for(MediaInfo mediaInfo : mImageList) {
                mediaInfo.setRotatevalue(0);
                mediaInfo.setComRotateValue(0);
                mediaInfo.setCompoundPath(null);
            }
        }

        if(!mSession.isBindTv()) {
            TvBoxInfo tvBoxInfo = mSession.getTvboxInfo();
            if(tvBoxInfo!=null) {
                LogUtils.d(ConstantValues.LOG_CHECKWIFI_PREFIX+" 有缓存机顶盒信息 检查是否已连接指定wifi");
                checkWifiLinked(tvBoxInfo);
            }
        }
    }

    private void initData() {
        mProgressBar.setVisibility(View.VISIBLE);
        mMediaListAdapter = new MediaAdapter(this);
        mMediasGv.setAdapter(mMediaListAdapter);

        mMediaCategoryApdapter = new MediaCatogoryAdapter(this);
        mMediaLv.setAdapter(mMediaCategoryApdapter);

        loadMediaFiles();
    }

    private void handleVideo(MediaInfo mVideoInfo) {
        mCurrentVideoInfo = mVideoInfo;
        String assetpath = mVideoInfo.getAssetcover();
        File srcFile = new File(assetpath);
        int dotindex = assetpath.lastIndexOf(".");
        if (dotindex != -1) {
            String type = assetpath.substring(dotindex, assetpath.length());
            String compressPath = mSession.getCompressPath();
            if (Build.VERSION.SDK_INT >= 18) {
                FileOutputStream fos = null;
                try {
                    mCacheVideoPath = compressPath + (compressPath.endsWith(File.separator) ? "" : File.separator) + "savorVideo" + type;
                    Uri uriForFile = FileProvider.getUriForFile(this, "com.savor.savorphone.fileprovider", srcFile);
                    ContentResolver contentResolver = getContentResolver();
                    ParcelFileDescriptor pFileDesCripter = contentResolver.openFileDescriptor(uriForFile, "r");
                    FileDescriptor fileDesCripter = pFileDesCripter.getFileDescriptor();
//                    MediaTranscoder.getInstance().transcodeVideo(fileDesCripter, mCacheVideoPath,
//                            MediaFormatStrategyPresets.createExportPreset960x540Strategy(), listener);
                    MediaTranscoder.getInstance().transcodeVideo(fileDesCripter, mCacheVideoPath,
                            MediaFormatStrategyPresets.createAndroid720pStrategy(200 * 1000, 128 * 1000, 1), listener);
                } catch (Exception e) {
                    e.printStackTrace();
                    handleFileCopy(mVideoInfo, srcFile, type, compressPath);
                }
            } else {
                handleFileCopy(mVideoInfo, srcFile, type, compressPath);
            }
        }
    }

    MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
        @Override
        public void onTranscodeProgress(double progress) {
            BigDecimal db = new BigDecimal(progress);
            String ii = db.toPlainString();
            Double d = Double.valueOf(ii);
            int pro = (int) (d*100);
            Message message = Message.obtain();
            message.what = UPDATE_PROGRESS;
            message.obj = String.valueOf(pro)+"%";
            mHandler.sendMessage(message);
        }

        @Override
        public void onTranscodeCompleted() {
            startProLocalVideo();
        }

        @Override
        public void onTranscodeCanceled() {
            LogUtils.d("savor:video trans cacel");
        }

        @Override
        public void onTranscodeFailed(Exception exception) {
            dismissLoadingDialog();
            String assetpath = mCurrentVideoInfo.getAssetpath();
            File srcFile = new File(assetpath);
            int dotindex = assetpath.lastIndexOf(".");
            String type = ".mp4";
            if(dotindex!=-1) {
                type = assetpath.substring(dotindex, assetpath.length());
            }
            handleFileCopy(mCurrentVideoInfo, srcFile, type, mSession.getCompressPath());
//            showToast("暂不支持该视频格式");
            LogUtils.d("savor:video trans failed");
        }
    };

    private void startProLocalVideo() {
        Message message = Message.obtain();
        message.what = HANDLE_OVER;
        mCurrentVideoInfo.setAsseturl(NetWorkUtil.getLocalUrl(MediaGalleryActivity.this)+mCacheVideoPath);
        message.obj = mCurrentVideoInfo;
        mHandler.sendMessage(message);
    }

    /**文件拷贝到压缩目录下*/
    private void handleFileCopy(MediaInfo mVideoInfo, File srcFile, String type, String compressPath) {
//        copyFile(srcFile, type, compressPath);
        Message message = Message.obtain();
        message.what = HANDLE_OVER;
        message.obj = mVideoInfo;
        mHandler.sendMessage(message);
    }
    private void initMediaList() {
        List<Map<String,List<MediaInfo>>> listMap = new ArrayList<>();
        if(photoMap!=null&&photoMap.size()>0) {
            Set<Map.Entry<String, List<MediaInfo>>> entries = photoMap.entrySet();
            for(Map.Entry<String, List<MediaInfo>> entry : entries) {
                String key = entry.getKey();
                List<MediaInfo> value = entry.getValue();
                Map<String,List<MediaInfo>> tempMap = new HashMap<>();
                tempMap.put(key,value);
                listMap.add(tempMap);
            }

            addAllVideo(listMap);

            addPicAndVideo(listMap);
            mMediaCategoryApdapter.setData(listMap);
        }else {
            ShowMessage.showToast(this,"没有发现图片");
        }
    }

    private void loadMediaFiles() {
        new Thread(){
            @Override
            public void run() {
                MediaUtils.getMediaPhoto(MediaGalleryActivity.this,mImageList,photoMap);
                MediaUtils.getMediaVideo(MediaGalleryActivity.this,mVideoList);
                mMediaList.clear();
                mMediaList.addAll(mImageList);
                mMediaList.addAll(mVideoList);
                Collections.sort(mMediaList, new Comparator<MediaInfo>() {
                    @Override
                    public int compare(MediaInfo o1, MediaInfo o2) {
                        return new Long(o1.getCreateTime()).compareTo(new Long(o2.getCreateTime()));
                    }
                });
                mHandler.sendEmptyMessage(LOAD_COMPLETE);
            }
        }.start();

    }

    public void setListeners() {
        mTitleTv.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
        mLeftTv.setOnClickListener(this);
        mBackIv.setOnClickListener(this);
        mProBtn.setOnClickListener(this);
        toscreen.setOnClickListener(this);
        mMediaLv.setOnItemClickListener(this);
        mMediasGv.setOnItemClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_toscreen:
            case R.id.toscreen:
                RecordUtils.onEvent(this,R.string.album_toscreen_cancel);
                mCurrentProType = TYPE_PRO_SLIDES;
                // 过滤选中列表
                boolean isDeleteAll = isSelectListDeleteAll();
                if(isDeleteAll) {
                    ShowMessage.showToast(this,"照片已删除");
                }else if(mSelectedList.size()==0) {
                    ShowMessage.showToast(this,"请选择图片");
                }else {
                    // 判断当前第一张是否存在
                    MediaInfo info = mSelectedList.get(0);
                    String assetpath = info.getAssetpath();
                    File file = new File(assetpath);
                    if(file.exists()) {
                        // 幻灯片投屏，首先投屏第一张，投屏成功后跳转到幻灯片页面
                        boolean bindTv = mSession.isBindTv();
                        if(bindTv) {
                            small = 1;
                            force = 0;
                            startSlidesProjection();
                        }else {
                            mBindTvPresenter.bindTv();
                        }
                    }else {
                        ProjectionManager.getInstance().setSeriesId(System.currentTimeMillis()+"");
                        startSlides();
                    }
                }
                break;
            case R.id.tv_left:
                if(isSelectAll) {
                    RecordUtils.onEvent(this,R.string.album_toscreen_screen);
                    isSelectAll = false;
                    mLeftTv.setText("全选");
                    List<MediaInfo> data = mMediaListAdapter.getData();
                    if(data!=null&&data.size()>0) {
                        for(MediaInfo info : data) {
                            info.setChecked(false);
                        }
                        mSelectedList.clear();
                        mMediaListAdapter.notifyDataSetChanged();
                    }
                    return;
                }
                RecordUtils.onEvent(this,R.string.album_toscreen_chooseall);
                List<MediaInfo> datas = mMediaListAdapter.getData();
                if(datas!=null&&datas.size()>0) {
                    mSelectedList.clear();
                    for(MediaInfo info : datas) {
                       if(info.getMediaType()==MediaInfo.MEDIA_TYPE_PIC) {
                           if(mSelectedList.size()<50) {
                               info.setChecked(true);
                               mSelectedList.add(info);
                           }else {
                               break;
                           }
                       }
                    }
                    mMediaListAdapter.notifyDataSetChanged();
                }
                isSelectAll = true;
                mLeftTv.setText("取消全选");
                break;
            case R.id.iv_left:
                RecordUtils.onEvent(this,R.string.album_toscreen_back);
                finish();
                break;
            case R.id.tv_center:
                if(mCurrentState == STATE_NORMAL) {
                    mCurrentState = STATE_SELECT_CATEGORY;
                    mTitleTv.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ico_arraw_up),null);
                    RecordUtils.onEvent(this,R.string.album_toscreen_open);
                    showMediaList();
                }else if(mCurrentState == STATE_SELECT_CATEGORY) {
                    // 关闭选择
                    mCurrentState = STATE_NORMAL;
                    mTitleTv.setCompoundDrawablesWithIntrinsicBounds(null,null,
                            getResources().getDrawable(R.drawable.ico_arraw_down),null);
                    mRightTv.setVisibility(View.VISIBLE);
                    dismissMediaListLayout();
                }
                break;
            case R.id.tv_right:
                if(mCurrentState == STATE_NORMAL) {
                    mCurrentState = STATE_SELECT_IMAGE;
                    mTitleTv.setCompoundDrawablesWithIntrinsicBounds(null,null,
                            null,null);
                    mRightTv.setText("取消");
                    mBackIv.setVisibility(View.GONE);
                    mLeftTv.setVisibility(View.VISIBLE);
                    mLeftTv.setText("全选");
                    mMediaListAdapter.setEditState(true);
                    mProBtn.setVisibility(View.VISIBLE);
                    toscreen.setVisibility(View.VISIBLE);
                }else if(mCurrentState == STATE_SELECT_IMAGE) {
                    mCurrentState = STATE_NORMAL;
                    isSelectAll = false;
                    resetNormalState();
                }
                break;
        }
    }

    private void resetNormalState() {
        mCurrentState = STATE_NORMAL;
        mTitleTv.setCompoundDrawablesWithIntrinsicBounds(null,null,
                getResources().getDrawable(R.drawable.ico_arraw_down),null);
        List<MediaInfo> data = mMediaListAdapter.getData();
        if(data!=null&&data.size()>0) {
            for(MediaInfo info : data) {
                info.setChecked(false);
            }
            mSelectedList.clear();
        }
        mMediaListAdapter.setEditState(false);
        mLeftTv.setVisibility(View.GONE);
        mBackIv.setVisibility(View.VISIBLE);
        mRightTv.setText("选择");
        mProBtn.setVisibility(View.GONE);
        toscreen.setVisibility(View.GONE);
    }

    private boolean isSelectListDeleteAll() {
        if(mSelectedList!=null&&mSelectedList.size()>0) {
            for(MediaInfo info : mSelectedList) {
                String assetpath = info.getAssetpath();
                if(!TextUtils.isEmpty(assetpath)) {
                    File file = new File(assetpath);
                    if(file.exists()) {
                        return false;
                    }
                }
            }
            return true;
        }else {
            return false;
        }
    }

    private void startSlidesProjection() {
        mCurrentProType = TYPE_PRO_SLIDES;
        MediaInfo info = mSelectedList.get(0);
        info.setImageId(System.currentTimeMillis()+"");
        ProjectionManager.getInstance().setSeriesId(System.currentTimeMillis()+"");
        ProjectionManager.getInstance().setImageType("3");
        showProLoadingDialog();
        compressFirstImage(info);
    }

    /**
     * 单张投屏
     */
    private void startSingleProjection() {
        mCurrentProType = TYPE_PRO_SINGLE;
        mSingleInfo.setImageId(System.currentTimeMillis()+"");
        ProjectionManager.getInstance().setSeriesId(System.currentTimeMillis()+"");
        ProjectionManager.getInstance().setImageType("1");
        showProLoadingDialog();
        compressFirstImage(mSingleInfo);
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

    /**
     * 压缩幻灯片第一张图片，并投屏
     * @param info
     */
    private void compressFirstImage(final MediaInfo info) {
        new Thread(){
            @Override
            public void run() {
                String copyPath ;
                if (small==1){
                    copyPath = CompressImage.compressAndSaveBitmap(MediaGalleryActivity.this, info.getAssetpath(),info.getAssetname(),true);
                }else{
                    copyPath = CompressImage.compressAndSaveBitmap(MediaGalleryActivity.this, info.getAssetpath(),info.getAssetname(),false);
                }

                info.setAsseturl(NetWorkUtil.getLocalUrl(MediaGalleryActivity.this)+copyPath);
                info.setCompressPath(copyPath);

                // 压缩成功，开始投屏
                Message message = Message.obtain();
                message.what = PRO_IMAGE;
                message.obj = info;
                mHandler.removeMessages(PRO_IMAGE);
                mHandler.sendMessage(message);
            }
        }.start();
    }

    private void dismissMediaListLayout() {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.actionsheet_dialog_out);
        mMediaListLayout.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMediaListLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showMediaList() {
        mRightTv.setVisibility(View.GONE);
        mMediaListLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.actionsheet_dialog_in);
        mMediaListLayout.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mMediaListLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                mMediaListLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 添加所有视频分类
     * @param listMap
     */
    private void addAllVideo(List<Map<String, List<MediaInfo>>> listMap) {
        HashMap<String,List<MediaInfo>> allImagesMap = new HashMap<>();
        allImagesMap.put("所有视频",mVideoList);
        listMap.add(0,allImagesMap);
    }

    /**
     * 添加图片和视频分类
     * @param listMap
     */
    private void addPicAndVideo(List<Map<String, List<MediaInfo>>> listMap) {
        HashMap<String,List<MediaInfo>> allImagesMap = new HashMap<>();
        allImagesMap.put("图片和视频",mMediaList);
        listMap.add(0,allImagesMap);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        switch (parent.getId()) {
            case R.id.listview:
                RecordUtils.onEvent(MediaGalleryActivity.this,R.string.album_toscreen_choose);
                Animation animation = AnimationUtils.loadAnimation(this,R.anim.actionsheet_dialog_out);
                mMediaListLayout.startAnimation(animation);
                mMediaListLayout.setVisibility(View.GONE);
                Map<String, List<MediaInfo>> clickData = mMediaCategoryApdapter.getDataByPosition(position);
                String name = "图片和视频";
                if(clickData!=null&&clickData.size()>0) {
                    name = (String) clickData.keySet().toArray()[0];
                }
                mTitleTv.setText(name);
                if (position == 0) {
                    mRightTv.setVisibility(View.VISIBLE);
                    mMediaListAdapter.setData(mMediaList);
                } else if (position == 1) {
                    mRightTv.setVisibility(View.GONE);
                    mMediaListAdapter.setData(mVideoList);
                } else {
                    mRightTv.setVisibility(View.VISIBLE);
                    if (clickData != null) {
                        List<MediaInfo> newData = clickData.get(name);
                        mMediaListAdapter.setData(newData);
                    }
                }
                dismissMediaListLayout();
                mMediasGv.post(new Runnable() {
                    @Override
                    public void run() {
                        mMediasGv.setSelection(0);
                    }
                });
                mTitleTv.setCompoundDrawablesWithIntrinsicBounds(null,null,
                        getResources().getDrawable(R.drawable.ico_arraw_down),null);
                mCurrentState = STATE_NORMAL;
                break;
            case R.id.gv_medias:
                boolean editState = mMediaListAdapter.isEditState();
                MediaInfo info = (MediaInfo) parent.getItemAtPosition(position);
                if(editState) {
                    boolean checked = info.isChecked();
                    if(checked) {
                        info.setChecked(false);
                        mSelectedList.remove(info);
                        mLeftTv.setText("全选");
                        isSelectAll = false;
                    }else {
                        if(mSelectedList.size()<50) {
                            info.setChecked(true);
                            mSelectedList.add(info);
                        }else {
                            ShowMessage.showToast(this,"最多只能选择50张");
                        }
                    }
                    mMediaListAdapter.notifyDataSetChanged();
                }else {
                    // 直接投屏
                    boolean bindTv = mSession.isBindTv();

                    int mediaType = info.getMediaType();
                    switch (mediaType) {
                        case MediaInfo.MEDIA_TYPE_PIC:
                            RecordUtils.onEvent(MediaGalleryActivity.this,R.string.picture_to_screen_click_item);
                            mCurrentProType = TYPE_PRO_SINGLE;
                            mSingleInfo = info;
                            String assetpath = mSingleInfo.getAssetpath();
                            File file = new File(assetpath);
                            if(file.exists()) {
                                if(bindTv) {
                                    small = 1;
                                    force = 0;
                                    startSingleProjection();
                                }else {
                                    Collections.sort(mImageList, new Comparator<MediaInfo>() {
                                        @Override
                                        public int compare(MediaInfo o1, MediaInfo o2) {
                                            return new Long(o1.getCreateTime()).compareTo(new Long(o2.getCreateTime()));
                                        }
                                    });
                                    int index = mImageList.indexOf(mSingleInfo);
                                    ProjectionManager.getInstance().setSingleInfo(mImageList);
                                    SingleImageProActivity.startImageGallery(this,false,mProjectId,index);
                                }
                            }else {
                                ShowMessage.showToast(this,"该图片不存在");
                            }

                            break;
                        case MediaInfo.MEDIA_TYPE_VIDEO:
                            RecordUtils.onEvent(MediaGalleryActivity.this,R.string.album_toscreen_video);
                            mCurrentProType = TYPE_PRO_VIDEO;
                            mVideoInfo = info;
                            force = 0;
                            //////////////////////////
                            if(!AppUtils.isNetworkAvailable(this)) {
                                showToast(getString(R.string.network_error));
                                return;
                            }

                           // VideoInfo modelVideo = (VideoInfo) parent.getItemAtPosition(position);

                            mLoadingDialog = new LoadingProgressDialog(this);
                            mLoadingDialog.setOnBackKeyDowListener(new LoadingProgressDialog.OnBackKeyDownListener() {
                                @Override
                                public void onBackKeyDown() {
                                    isCancelVideoHandle = true;
                                    finish();
                                    mHandler.removeMessages(PROCESS_VIDEO);
                                    mHandler.removeCallbacksAndMessages(null);
                                }
                            });
                            mLoadingDialog.show();
                            Message message = Message.obtain();
                            message.what = PROCESS_VIDEO;
                            message.obj = mVideoInfo;
                            mHandler.sendMessage(message);

                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_IMAGE_PROJECTION_JSON:
                dismissProLoadingDialog();

                if(obj instanceof ImageProResonse) {
                    ImageProResonse proResonseInfo = (ImageProResonse) obj;
                    mProjectId = proResonseInfo.getProjectId();
                    ProjectionManager.getInstance().setProjectId(mProjectId);
                }
                if (small==1){
                    small = 0;
                    switch (mCurrentProType) {
                        case TYPE_PRO_SINGLE:
                            ProjectionManager.getInstance().setSlideStatus(false);
                            if(mProjectionService!=null) {
                                mProjectionService.stopQuerySeek();
                                mProjectionService.stopSlide();
                            }
                            compressFirstImage(mSingleInfo);
                            break;
                        case TYPE_PRO_SLIDES:
                            if(mProjectionService!=null) {
                                mProjectionService.stopQuerySeek();
                                mProjectionService.stopSlide();
                            }
                            if(mSelectedList.size()>0) {
                                compressFirstImage(mSelectedList.get(0));
                            }
                            break;

                    }
                    handleProSuccess();
                    return;
                }
                break;
            case POST_LOCAL_VIDEO_PRO_JSON:
                dismissProLoadingDialog();
                mLoadingDialog.dismiss();
                if(obj instanceof LocalVideoProPesponse) {
                    LocalVideoProPesponse response = (LocalVideoProPesponse) obj;
                    projectId = response.getProjectId();
                    ProjectionManager.getInstance().setProjectId(projectId);
//                    prepareSuccess(prepareResponseVo);
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(SavorApplication.getInstance(), notification);
                    r.play();

                    dismissLoadingDialog();

                    LocalVideoProAcitvity.startLocalVideoProActivity(this,mCurrentVideoInfo,true,projectId);
                    resetNormalState();
                    if(mProjectionService!=null) {
                        mProjectionService.stopSlide();
                    }
                    ProjectionManager.getInstance().setSlideStatus(false);
                }

                break;

        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        if(obj == AppApi.ERROR_TIMEOUT) {
            ShowMessage.showToast(this,"网络超时");
            dismissProLoadingDialog();
            return;
        }
        switch (method) {
            case POST_IMAGE_PROJECTION_JSON:
                dismissProLoadingDialog();
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage responseErrorMessage = (ResponseErrorMessage) obj;
                    String msg = responseErrorMessage.getMessage();
                    Message message = Message.obtain();
                    if (responseErrorMessage.getCode()==4){
                        message.what = FORCE_MSG;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }else{
                        message.what = SHOW_ERROR_MSG;
                        message.obj = msg;
                        mHandler.removeMessages(SHOW_ERROR_MSG);
                        mHandler.sendMessage(message);
                    }

                }
//                mHandler.sendEmptyMessage(SHOW_ERROR_MSG);
                break;
            case POST_LOCAL_VIDEO_PRO_JSON:
                if(mLoadingDialog!=null&&mLoadingDialog.isShowing())
                    mLoadingDialog.dismiss();
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage responseErrorMessage = (ResponseErrorMessage) obj;
                    String msg = responseErrorMessage.getMessage();
                    Message message = Message.obtain();
                    if (responseErrorMessage.getCode()==4){
                        message.what = FORCE_MSG;
                        message.obj = msg;
                        mHandler.sendMessage(message);
                    }else{
                        message.what = SHOW_ERROR_MSG;
                        message.obj = msg;
                        mHandler.removeMessages(SHOW_ERROR_MSG);
                        mHandler.sendMessage(message);
                    }

                }
                break;
        }
    }

    /**
     * 处理投屏成功
     */
    private void handleProSuccess() {
        switch (mCurrentProType) {
            case TYPE_PRO_SLIDES:
                startSlides();
                break;
            case TYPE_PRO_SINGLE:
                Collections.sort(mImageList, new Comparator<MediaInfo>() {
                    @Override
                    public int compare(MediaInfo o1, MediaInfo o2) {
                        return new Long(o1.getCreateTime()).compareTo(new Long(o2.getCreateTime()));
                    }
                });
                int index = mImageList.indexOf(mSingleInfo);
                ProjectionManager.getInstance().setSingleInfo(mImageList);
                SingleImageProActivity.startImageGallery(MediaGalleryActivity.this,true,mProjectId,index);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resetNormalState();
                    }
                });
                break;
        }
    }

    /**
     * 开启幻灯片
     */
    private void startSlides() {
        List<MediaInfo> mediaInfoList = new ArrayList<>();
        mediaInfoList.addAll(mSelectedList);
        ProjectionManager.getInstance().setSlideList(mediaInfoList);
        SlidesActivity.startSlidesActivity(this,0,true,ProjectionManager.getInstance().getSeriesId(),true);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                resetNormalState();
            }
        });
    }

    @Override
    public void readyForCode() {
        if(mQrcodeDialog==null)
            mQrcodeDialog = new LinkDialog(this,getString(R.string.call_qrcode));
        mQrcodeDialog.show();
    }

    @Override
    public void startLinkTv() {
        super.startLinkTv();
        if (mQrcodeDialog != null) {
            mQrcodeDialog.dismiss();
            mQrcodeDialog = null;
        }
    }

    private void dismissProLoadingDialog() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mProDialog!=null) {
                    mProDialog.dismiss();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == EXTRA_TV_INFO){
            if(data!=null) {
                TvBoxInfo boxInfo = (TvBoxInfo) data.getSerializableExtra(EXRA_TV_BOX);
                mBindTvPresenter.handleBindCodeResult(boxInfo);
            }
//            mBackFromInternal = true;
        }
    }

    @Override
    public void initBindcodeResult() {
//        if(AppUtils.isFastDoubleClick(1)) {
//            showToast("连接电视成功");
//        }

        if(mCurrentProType == TYPE_PRO_SLIDES) {
            small = 1;
            force = 0;
            startSlidesProjection();
        }else if(mCurrentProType == TYPE_PRO_SINGLE) {
            small = 1;
            force = 0;
            startSingleProjection();
        }
    }

    /**
     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
     * @param msg
     */
    private void showConfirm(String msg){
        String content = "当前"+msg+"正在投屏,是否继续投屏?";
        if(forceDialog!=null&&forceDialog.isShowing()) {
            forceDialog.dismiss();
        }
        forceDialog = new CommonDialog(this, content,
                new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        HashMap<String,String> params = new HashMap<>();
                        params.put(getString(R.string.to_screen_competition_hint),"ensure");
                        params.put("type","pic");
                        RecordUtils.onEvent(MediaGalleryActivity.this,getString(R.string.to_screen_competition_hint),params);
                        if(mCurrentProType == TYPE_PRO_SLIDES) {
                            force = 1;
                            small = 1;
                            mHandler.sendEmptyMessage(SLIDES_PRO);
                        }else if(mCurrentProType == TYPE_PRO_SINGLE){
                            force = 1;
                            small = 1;
                            mHandler.sendEmptyMessage(SINGLE_PRO);
                        }else if(mCurrentProType == TYPE_PRO_VIDEO) {
                            force = 1;
                            showProLoadingDialog();
                            startProLocalVideo();
                        }
                        forceDialog.cancel();
                    }
                }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                HashMap<String,String> params = new HashMap<>();
                params.put(getString(R.string.to_screen_competition_hint),"cancel");
                params.put("type","pic");
                RecordUtils.onEvent(MediaGalleryActivity.this,getString(R.string.to_screen_competition_hint),params);
                forceDialog.cancel();
            }
        },"继续投屏",true);
        if(forceDialog!=null&&!forceDialog.isShowing()) {
            forceDialog.show();
        }
    }

    private void dismissLoadingDialog() {
        if(mLoadingDialog!=null)
            mLoadingDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mServiceConn!=null) {
            unbindService(mServiceConn);
        }
        mHandler.removeMessages(HANDLE_OVER);
        mHandler.removeCallbacksAndMessages(null);
        if(mLoadingDialog!=null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
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
}
