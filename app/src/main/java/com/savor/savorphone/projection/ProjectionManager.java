package com.savor.savorphone.projection;

import com.rance.library.ButtonData;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.bean.VideoInfo;

import java.util.List;

/**
 * 投屏管理器
 * Created by hezd on 2017/3/6.
 */

public class ProjectionManager {
    private Class mProjectionActivity;
    private VideoInfo mVideoInfo;
    private static volatile ProjectionManager instance = null;
    private int mImagePosition;
    /**单张投屏图片集合*/
    private CommonListItem mVodBean;
    private MediaInfo mLocalVideoInfo;
    /**幻灯片当前位置*/
    private int mSlidePosition;
    private List<MediaInfo> mSlideList;
    /**幻灯片播放状态*/
    private boolean mSlideStatus;
    /**是否运行在前台*/
    private boolean isForeground;
    /**视频点播当前是暂停或播放状态*/
    private boolean isPlaying;
    /**本地视频是否正在播放*/
    private boolean isLocalVideoPlaying;
    /**投屏操作比如暂停播放需要的id投屏成功返回*/
    private String mProjectId;
    private int mVodType;
    /**图片投屏类型1.普通图片投屏 2.文件投屏 3.幻灯片投屏*/
    private String imageType;
    /**幻灯片和文件投屏会话id*/
    private String seriesId;
    /**幻灯片扇形菜单填充数据*/
    private List<ButtonData> mButtonDatas;
    /**pdf放大倍数*/
    private float mPdfZoom;
    /**当前展示的页数*/
    private int mCurrentPage;
    /**当前x坐标偏移量*/
    private float mCurrentXoffset;
    /**当前y坐标偏移量*/
    private float mCurrentYOffset;
    private String mPdfPath;
    /**当前文件投屏的图片路径*/
    private String mCurrentPic;
    private boolean isLockedScrren;
    /**单张投屏*/
    private List<MediaInfo> mSingleInfos;
    /**pdf投屏屏幕方向*/
    private int pdfOritention;
    /**单张投屏当前位置*/
    private int singlePosition;

    private ProjectionManager(){}

    public static ProjectionManager getInstance() {
        if(instance==null) {
            synchronized (ProjectionManager.class) {
                if(instance == null)
                    instance = new ProjectionManager();
            }
        }
        return instance;
    }

    public int getSinglePosition() {
        return singlePosition;
    }

    public void setSinglePosition(int singlePosition) {
        this.singlePosition = singlePosition;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getProjectId() {
        return mProjectId;
    }

    public void setProjectId(String mProjectId) {
        this.mProjectId = mProjectId;
    }

    public void setmSlidePosition(int mSlidePosition) {
        this.mSlidePosition = mSlidePosition;
    }

    public void setSlideList(List<MediaInfo> mSlideList) {
        this.mSlideList = mSlideList;
        setImageType("3");
    }

    public void setProjectionFrame(Class activity) {
        this.mProjectionActivity = activity;
    }

    public Class getProjectionActivity() {
        return this.mProjectionActivity;
    }

    public void setVideoTVProjection(Class activity, CommonListItem vodBean, boolean isPlaying) {
        this.mProjectionActivity = activity;
        this.mVodBean = vodBean;
        this.isPlaying = isPlaying;
    }

    public boolean getVodPlayStatus() {
        return this.isPlaying;
    }

    public void setVideoLocalProjection(Class activity,MediaInfo videoInfo,boolean isLocalVideoPlaying) {
        this.mProjectionActivity = activity;
        this.mLocalVideoInfo = videoInfo;
        this.isLocalVideoPlaying = isLocalVideoPlaying;
    }

    public boolean isLocalVideoPlaying() {
        return isLocalVideoPlaying;
    }

    public void setLocalVideoPlaying(boolean localVideoPlaying) {
        isLocalVideoPlaying = localVideoPlaying;
    }

    public MediaInfo getLocalVideoInfo() {
        return mLocalVideoInfo;
    }

    public void setImageProjection(Class activity, List<MediaInfo> mediaInfo,String projectId,int singlePosition) {
        this.mProjectionActivity = activity;
        this.mProjectId = projectId;
        this.mSingleInfos = mediaInfo;
        this.singlePosition = singlePosition;
    }

    public List<MediaInfo> getSingleInfo() {
        return mSingleInfos;
    }

    public void setSingleInfo(List<MediaInfo> mSingleInfo) {
        this.mSingleInfos = mSingleInfo;
    }

    /**
     * 退出页面保存幻灯片播放状态
     * @param activity
     * @param slidePosition
     * @param slideList
     * @param isPlaying
     */
    public void setSlideProjection(Class activity, int slidePosition, List<MediaInfo> slideList,boolean isPlaying,String seriesId) {
        this.mProjectionActivity = activity;
        this.mSlidePosition = slidePosition;
        this.mSlideList = slideList;
        this.mSlideStatus = isPlaying;
        this.seriesId = seriesId;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public boolean isSlideStatus() {
        return mSlideStatus;
    }

    public void setSlideStatus(boolean isPlaying) {
        this.mSlideStatus = isPlaying;
    }

    public int getSlidePosition() {
        return mSlidePosition;
    }

    public List<MediaInfo> getSlideList() {
        return mSlideList;
    }

    public VideoInfo getmVideoInfo() {
        return mVideoInfo;
    }

    public int getmImagePosition() {
        return mImagePosition;
    }

    public CommonListItem getmVodBean() {
        return mVodBean;
    }

    public void resetProjection() {
        this.isLockedScrren = false;
        this.mProjectionActivity = null;
        this.mImagePosition = 0;
        this.mSingleInfos = null;
        this.imageType = null;
        this.mButtonDatas = null;
    }

    public void setForegroundStatus(boolean isForeground) {
        this.isForeground = isForeground;
    }

    public boolean isForeground() {
        return this.isForeground;
    }

    public void setVodType(int vodType) {
        this.mVodType = vodType;
    }

    public int getVodType() {
        return this.mVodType;
    }

    public void setSlideSectorMenu(List<ButtonData> buttonDatas) {
        this.mButtonDatas = buttonDatas;
    }

    public List<ButtonData> getSlideSectorMenu() {
        return this.mButtonDatas;
    }

    public void setPdfProjection(Class clazz,float zoom, int currentPage, float currentXOffset, float currentYOffset,String pdfPath,String currentPic,boolean isLockedScreen,int orientation) {
        this.mProjectionActivity = clazz;
        this.mPdfZoom = zoom;
        this.mCurrentPage = currentPage;
        this.mCurrentXoffset = currentXOffset;
        this.mCurrentYOffset = currentYOffset;
        this.mPdfPath = pdfPath;
        this.mCurrentPic = currentPic;
        this.isLockedScrren = isLockedScreen;
        this.pdfOritention = orientation;
    }

    public int getPdfOritention() {
        return pdfOritention;
    }

    public void setPdfOritention(int pdfOritention) {
        this.pdfOritention = pdfOritention;
    }

    public boolean isLockedScrren() {
        return isLockedScrren;
    }

    public String getmCurrentPic() {
        return mCurrentPic;
    }

    public void setmCurrentPic(String mCurrentPic) {
        this.mCurrentPic = mCurrentPic;
    }

    public String getPdfPath() {
        return mPdfPath;
    }

    public void setmPdfPath(String mPdfPath) {
        this.mPdfPath = mPdfPath;
    }

    public float getmPdfZoom() {
        return mPdfZoom;
    }

    public void setmPdfZoom(float mPdfZoom) {
        this.mPdfZoom = mPdfZoom;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setmCurrentPage(int mCurrentPage) {
        this.mCurrentPage = mCurrentPage;
    }

    public float getmCurrentXoffset() {
        return mCurrentXoffset;
    }

    public void setmCurrentXoffset(float mCurrentXoffset) {
        this.mCurrentXoffset = mCurrentXoffset;
    }

    public float getmCurrentYOffset() {
        return mCurrentYOffset;
    }

    public void setmCurrentYOffset(float mCurrentYOffset) {
        this.mCurrentYOffset = mCurrentYOffset;
    }
}
