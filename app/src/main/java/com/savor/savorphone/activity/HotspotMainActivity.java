package com.savor.savorphone.activity;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.Process;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.common.api.http.callback.FileDownProgress;
import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.common.api.utils.StringUtils;
import com.gxz.PagerSlidingTabStrip;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.adapter.CategoryPagerAdapter;
import com.savor.savorphone.bean.CategoryItemVo;
import com.savor.savorphone.bean.RotateProResponse;
import com.savor.savorphone.bean.SpecialNameResult;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.bean.UpgradeInfo;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.fragment.ProjectionFragment;
import com.savor.savorphone.fragment.SubjectFragment;
import com.savor.savorphone.fragment.WealthLifeFragment;
import com.savor.savorphone.interfaces.IHotspotSenseView;
import com.savor.savorphone.presenter.BindTvPresenter;
import com.savor.savorphone.presenter.SensePresenter;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.service.ClearImageCacheService;
import com.savor.savorphone.service.LocalJettyService;
import com.savor.savorphone.service.ProjectionService;
import com.savor.savorphone.service.SSDPService;
import com.savor.savorphone.service.StopProjectionService;
import com.savor.savorphone.utils.ActivitiesManager;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.DialogUtil;
import com.savor.savorphone.utils.GlideImageLoader;
import com.savor.savorphone.utils.ImageCacheUtils;
import com.savor.savorphone.utils.IntentUtil;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.STIDUtil;
import com.savor.savorphone.utils.WifiUtil;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.LinkDialog;
import com.savor.savorphone.widget.ProgressBarView;
import com.savor.savorphone.widget.UpgradeDialog;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.savor.savorphone.activity.LinkTvActivity.EXTRA_TV_INFO;
import static com.savor.savorphone.activity.RecommendActivity.EXTRA_FROM_RECOMMEND;

public class HotspotMainActivity extends AppCompatActivity
        implements View.OnClickListener, IHotspotSenseView,IBindTvView,ApiRequestListener,ProgressBarView.ProgressBarViewClickListener {
    public static final int SCAN_QR = 11;
    public static final int FROM_APP_BACK = 0x100;
    private static final int CHECK_PLATFORM_URL = 0x101;
    private static final String CAGEGORY_FILE_NAME = "category";
    private static final int CHECK_WIFI_LINKED = 0x102;
    /**取消检测wifi*/
    private static final int CANCEL_CHECK_WIFI = 0x103;
    private static final int CHECK_PDF_DATA = 0x104;
    public static int count ;
    private Toolbar mToolBar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private PagerSlidingTabStrip mTabLayout;
    private ViewPager mViewPager;
    private ImageView mMenuBtn;
    private RelativeLayout mCollectionLayout;
    private RelativeLayout mFeedbackLayout;
    private RelativeLayout mClearCacheLayout;
    private RelativeLayout mHelpLayout;
    private RelativeLayout mCheckUpLayout;
    private RelativeLayout  recommend_la;
    private RelativeLayout  rl_map;
    private RelativeLayout  rl_favourable;
    private RelativeLayout  logo_la;
    private RelativeLayout  la;
    private Context mContext;
    private TextView size;
    private TextView version_code;
    private CoordinatorLayout mParentLayout;
    /**场景主导器*/
    private SensePresenter mSensePresenter;
    /**绑定电视主导器*/
    private BindTvPresenter mBindTvPresenter;
    private CategoryPagerAdapter mPagerAdapter;
    private List<Fragment> mFragmentList;
    private List<String> mTitleList;
    private Session mSession;
    private LinkDialog mQrcodeDialog;
    private long exitTime;
    private List<CategoryItemVo> mContents = new ArrayList<CategoryItemVo>();
    private UpgradeInfo upGradeInfo;
    private UpgradeDialog mUpgradeDialog;
    private NotificationManager manager;
    private Notification notif;
    private final int NOTIFY_DOWNLOAD_FILE=10001;
    private boolean mBackFromInternal;
    /**是否是自动检查更新，如果不是那就是手动检查提示有版本更新否则不提示*/
    private boolean ismuteUp = false;
    /**如果没发现小平台地址，每隔两秒检测一次，三次没检测到就认为是非酒店环境*/
    private int checkcount;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_PDF_DATA:
                    Intent intent = getIntent();
                    if(intent!=null&&("application/pdf").equals(intent.getType())) {
                        Intent intentPdf = new Intent(HotspotMainActivity.this,PdfPreviewActivity.class);
                        Uri data = intent.getData();
                        intentPdf.setType(getIntent().getType());
                        intentPdf.setData(data);
                        startActivityForResult(intentPdf,0);
                    }
                    break;
                case CANCEL_CHECK_WIFI:
                    mSession.setTvBoxUrl(null);
                    mHandler.removeMessages(CHECK_WIFI_LINKED);
                    break;
                case CHECK_PLATFORM_URL:
                    if(!mSensePresenter.isHotelEnvironment()&&AppUtils.isWifiNetwork(HotspotMainActivity.this)) {
                        if(checkcount<3) {
                            LogUtils.d("savor:checkSense 当前为非酒店环境,并且检测次数小于3继续检测");
                            mBindTvPresenter.getSmallPlatformUrl(true);
                            reCheckPlatform();
                        }else {
                            LogUtils.d("savor:checkSense 三次都没获取到小平台并且ap不可用当前为非酒店环境");
                            mHandler.removeMessages(CHECK_PLATFORM_URL);
                        }
                    }
                    checkcount++;
                    break;
                case CHECK_WIFI_LINKED:
                    if(!mSession.isBindTv()) {
                        TvBoxInfo tvboxInfo = mSession.getTvboxInfo();
                        if(tvboxInfo!=null) {
                            checkWifiLinked(tvboxInfo);
                        }
                    }else {
                        mHandler.removeMessages(CANCEL_CHECK_WIFI);
                    }
                    break;
            }
        }
    };
    private CommonDialog mChangeWifiDiallog;
    /**是否正在执行动画*/
    private boolean isAnimationPlaying;
    /**分类接口是否正在请求*/
    private boolean isRequesting;
//    private ImageView mProjectionImage;
    private Intent mProjectionIntent;
    /**视频监听*/
    private ServiceConnection mVideoServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            projectionService = ((ProjectionService.ProjectionBinder) service).getService();
            projectionService.setOnplayOverListener(new ProjectionService.OnPlayOverListener() {
                @Override
                public void onPlayOver(int result) {
//                    SavorAnimUtil.hideProjectionImage(HotspotMainActivity.this,mBackgroudProjectLayout);
                    ProjectionManager.getInstance().resetProjection();
                    projectionService.stopQuerySeek();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ServiceConnection mSlideServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            projectionService = ((ProjectionService.ProjectionBinder) service).getService();
//            SavorApplication.getInstance().setProjectionService(projectionService);
            projectionService.setOnSlidePlayOverLisetener(new ProjectionService.OnSlidePlayOverListener() {
                @Override
                public void onSlidePlayOver() {
                    initBackgroundProjectionHint();
                    projectionService.stopQuerySeek();
                    projectionService.stopProjection();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private ProjectionService projectionService;
//    private RelativeLayout mExitProBtn;
    /**是否需要展示发现可连接设备提示*/
    private boolean isNeedShowFoundHint = true;
    private boolean isShowingAnimation;
    private LinearLayout mProjectonLayout;
    private ProjectionFragment mProjectionFragment;
    private TextView mCategoryNameLabel;
    private PlayOverReceiver mPlayOverReceiver;
    private int prePosition;
    private ViewPager mShadeViewPager;
    private PagerSlidingTabStrip mShadeTabContainer;
    private RelativeLayout mShadeLayout;
    private CategoryPagerAdapter mShadePagerAdapter;
    private RelativeLayout mContentLayout;
    private View mShadeLayer;

    /**
     * 退出背景投屏更新提示语
     */
    public void initBackgroundProjectionHint() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ProjectionManager.getInstance().resetProjection();
                refreshLinkStatus();
            }
        });

    }

    public void reCheckPlatform() {
        Message message = Message.obtain();
        message.obj = checkcount;
        message.what = CHECK_PLATFORM_URL;
        mHandler.sendMessageDelayed(message,2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(ConstantValues.LOG_PREFIX+"打开首页...");
        setContentView(R.layout.activity_hotspot_main);
        RecordUtils.onEvent(getApplicationContext(),getString(R.string.home_start));
        ActivitiesManager.getInstance().pushActivity(this);
        mSession = Session.get(this);
        mContext = this;
        getViews();
        setViews();
        setListeners();
        initPresenter();
        initSenseState();
        getData();
        registerProjection();
        registerPlayOverReceiver();
        registerNetWorkReceiver();
        upgrade();
        // 检查
        checkIsNeedDeleteStartUp();
        ismuteUp = true;
        // 检查是否是pdf打开
        checkIntentData();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    private void checkIntentData() {
        mHandler.sendEmptyMessageDelayed(CHECK_PDF_DATA,500);
    }

    private void checkIsNeedDeleteStartUp() {
        boolean deleteStartUp = mSession.isDeleteStartUp();
        if(deleteStartUp) {
            LogUtils.d("savor:splash 删除启动图");
            String cachePath = SavorApplication.getInstance().getSplashCachePath();
            String splashTempPath = SavorApplication.getInstance().getSplashTempPath();
            com.common.api.utils.FileUtils.deleteFileAndFoder(cachePath);
            com.common.api.utils.FileUtils.deleteFileAndFoder(splashTempPath);
        }
    }

    private void registerProjection() {
        bindProjectionService(ProjectionService.TYPE_VOD_SLIDE,mSlideServiceConn);
        bindProjectionService(ProjectionService.TYPE_VOD_VIDEO,mVideoServiceConn);
    }

    private void getData() {
        AppApi.getSpecialName(this,this);
//        boolean hasCacheData = isHasCacheData();
//        if(hasCacheData) {
//            initCategoryCache();
//        }else {
//            mProgressLayout.startLoading();
//        }
//
//        getCategoryListTab(true);
    }

    private void initPresenter() {
        mSensePresenter = new SensePresenter(this,this,this);
        mBindTvPresenter = new BindTvPresenter(this,this,this,this);
    }

    @Override
    public void getViews() {
        mShadeLayer = findViewById(R.id.shade_layer);
        mContentLayout = (RelativeLayout) findViewById(R.id.include);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTabLayout = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mMenuBtn = (ImageView) findViewById(R.id.iv_menu);
        mCollectionLayout = (RelativeLayout) findViewById(R.id.rl_my_collection);
        mFeedbackLayout = (RelativeLayout) findViewById(R.id.rl_feedback);
        mClearCacheLayout = (RelativeLayout) findViewById(R.id.rl_clear_cache);
        rl_map = (RelativeLayout) findViewById(R.id.rl_map);
        rl_favourable = (RelativeLayout) findViewById(R.id.rl_favourable);
        size = (TextView)findViewById(R.id.size);
        version_code = (TextView)findViewById(R.id.version_code);
        mHelpLayout = (RelativeLayout) findViewById(R.id.rl_help);
        mCheckUpLayout = (RelativeLayout) findViewById(R.id.rl_checkup);
        recommend_la = (RelativeLayout) findViewById(R.id.recommend_la);
        logo_la = (RelativeLayout) findViewById(R.id.logo_la);
        la = (RelativeLayout) findViewById(R.id.la);
        mParentLayout = (CoordinatorLayout) findViewById(R.id.parent_layout);
        mCategoryNameLabel = (TextView) findViewById(R.id.tv_category_name);

        mShadeLayout = (RelativeLayout) findViewById(R.id.shade);
        mShadeViewPager = (ViewPager) findViewById(R.id.pager_shade);
        mShadeTabContainer = (PagerSlidingTabStrip) findViewById(R.id.tabs_shade);
    }

    @Override
    public void setViews() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_home_menu);
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        mActionBarDrawerToggle.syncState();
        size.setText(ImageCacheUtils.getCacheSize());
        version_code.setText("V"+mSession.getVersionName());
        //getVersionCode()
        mFragmentList = new ArrayList<>();
        mTitleList = new ArrayList<>();

        initIndicator();
    }

    private void initIndicator() {
        mProjectionFragment = ProjectionFragment.newInstance(0);
        mTitleList.add("投屏");
        mTitleList.add("创富");
        mTitleList.add("生活");
        mTitleList.add("专题");
        mFragmentList.add(mProjectionFragment);
        mFragmentList.add(WealthLifeFragment.getInstance(101));
        mFragmentList.add(WealthLifeFragment.getInstance(102));
        mFragmentList.add(SubjectFragment.getInstance());

        Bundle bundle = new Bundle();
        bundle.putString("title", "专题");
        mPagerAdapter = new CategoryPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setData(mFragmentList,mTitleList);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(3);
//        mViewPager.post(new Runnable() {
//            @Override
//            public void run() {
//                hideProjection();
//            }
//        });

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        titleList.add("创富");
        titleList.add("生活");
        titleList.add("专题");
        fragmentList.add(WealthLifeFragment.getInstance(101));
        fragmentList.add(WealthLifeFragment.getInstance(102));
        fragmentList.add(SubjectFragment.getInstance());
        mShadePagerAdapter = new CategoryPagerAdapter(getSupportFragmentManager());
        mShadePagerAdapter.setData(fragmentList,titleList);
        mShadeViewPager.setAdapter(mShadePagerAdapter);
        mShadeTabContainer.setViewPager(mShadeViewPager);
        mShadeViewPager.setOffscreenPageLimit(0);
    }

    @Override
    public void setListeners() {
        mShadeLayer.setOnClickListener(this);
//        mBackgroudProjectLayout.setOnClickListener(this);
        mMenuBtn.setOnClickListener(this);
        mCollectionLayout.setOnClickListener(this);
        mFeedbackLayout.setOnClickListener(this);
        mClearCacheLayout.setOnClickListener(this);
        mHelpLayout.setOnClickListener(this);
        rl_map.setOnClickListener(this);
        rl_favourable.setOnClickListener(this);
        mCheckUpLayout.setOnClickListener(this);
        recommend_la.setOnClickListener(this);
        logo_la.setOnClickListener(this);
        la.setOnClickListener(this);
//        mProgressLayout.setProgressBarViewClickListener(this);
        mTabLayout.setOnPagerTitleItemClickListener(new PagerSlidingTabStrip.OnPagerTitleItemClickListener() {
            @Override
            public void onSingleClickItem(int position) {
                if(mTitleList !=null&& mTitleList.size()>position) {
                    String cname = mTitleList.get(position);
                    HashMap<String,String> hashMap = new HashMap<String, String>();
                    hashMap.put(getString(R.string.home_click_category),cname);
                    RecordUtils.onEvent(HotspotMainActivity.this,getString(R.string.home_click_category),hashMap);
                }
            }

            @Override
            public void onDoubleClickItem(int position) {

            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(mTitleList.contains("投屏")) {
                    prePosition = position;
                }
                if(mTitleList !=null&& mTitleList.size()>position) {
                    String cname = mTitleList.get(position);
                    HashMap<String,String> hashMap = new HashMap<String, String>();
                    hashMap.put(getString(R.string.home_sliding_category),cname);
                    RecordUtils.onEvent(HotspotMainActivity.this,getString(R.string.home_sliding_category),hashMap);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                RecordUtils.onEvent(HotspotMainActivity.this,getString(R.string.home_slide_menu));
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                RecordUtils.onEvent(HotspotMainActivity.this,getString(R.string.menu_expand));
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    RecordUtils.onEvent(HotspotMainActivity.this,R.string.home_menu);
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    @Override
    public void checkSense() {
        // 因为默认无投屏切换到 有投屏会报错fragment add，默认有投屏，在判断非酒店去掉投屏
        // 这样间接解决了那个问题
        mShadeViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSensePresenter.checkSense();
            }
        },100);
    }

    private void getCategoryListTab(boolean isneedLoading){
        if(isRequesting)
            return;
        isRequesting = true;
       AppApi.getCategoryList(mContext,this);
    }

    private void initCategoryCache() {
        String savorCachDir = AppUtils.getSavorCachDir(this);
        String path = savorCachDir + CAGEGORY_FILE_NAME;
        List<CategoryItemVo> list = com.common.api.utils.FileUtils.readObject(this, path, List.class);
        handleCategoryData(list);
    }

    private boolean isHasCacheData() {
        String categoryCachePath = AppUtils.getSavorCachDir(this)+CAGEGORY_FILE_NAME;
        File file = new File(categoryCachePath);
        return file.exists();
    }

    @Override
    public void initSenseState() {
        mSensePresenter.regitsterSmallPlatformReciever();
        checkSense();
        if(!mSensePresenter.isHotelEnvironment()&&AppUtils.isWifiNetwork(this)) {
            LogUtils.d("savor:checkSense 当前为非酒店环境，延迟获取小平台地址");
            Message message = Message.obtain();
            message.what = CHECK_PLATFORM_URL;
            message.obj = checkcount;
            mHandler.sendMessageDelayed(message,2000);
        }
    }

    @Override
    public void showToast(String msg) {
        ShowMessage.showToastSavor(this,msg);
    }

    @Override
    public void showLoadingLayout() {

    }

    @Override
    public void hideLoadingLayout() {

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawerLayout();
        } else {
                super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                closeDrawerLayout();
                break;
            case R.id.rl_my_collection:
               // showToast(view,"我的收藏");
                RecordUtils.onEvent(this,getString(R.string.menu_collection));
                IntentUtil.openActivity(this,MyCollectActivity.class);
               // closeDrawerLayout();
                break;
            case R.id.rl_feedback:
               // showToast(view,"意见反馈");
                RecordUtils.onEvent(this,getString(R.string.menu_feedback));
                IntentUtil.openActivity(this,FeedbackActivity.class);
              //  closeDrawerLayout();
                break;
            case R.id.rl_clear_cache:
              //  showToast(view,"清除缓存");
                RecordUtils.onEvent(this,getString(R.string.menu_clear_cache));
                DialogUtil.showClearCacheDialog(this,size);
                //closeDrawerLayout();
                break;
            case R.id.rl_help:
              //  showToast(view,"帮助");
                RecordUtils.onEvent(this,getString(R.string.menu_help));
                IntentUtil.openActivity(this,HelpActivity.class);
               // closeDrawerLayout();
                break;
            case R.id.rl_checkup:
                //showToast(view,"检查更新");
                ismuteUp = false;
                upgrade();
               // closeDrawerLayout();
                break;
            case R.id.recommend_la:
                RecordUtils.onEvent(this,getString(R.string.menu_recommend));
                Intent recoIntent = new Intent(this,RecommendActivity.class);
                startActivityForResult(recoIntent,0);
//                IntentUtil.openActivity(this,RecommendActivity.class);
                break;

            case R.id.rl_map:
                RecordUtils.onEvent(this,getString(R.string.menu_hotel_map_list));
                Intent mapIntent = new Intent(this,HotelMapListActivity.class);
                startActivity(mapIntent);
//                IntentUtil.openActivity(this,RecommendActivity.class);
                break;

            case R.id.rl_favourable:
                RecordUtils.onEvent(this,getString(R.string.menu_game));
                if (mSession.getHotelid()>0) {
                    Intent favourableIntent = new Intent(this,GameActivity.class);
                    startActivity(favourableIntent);
                }else {
                    ShowMessage.showToast(HotspotMainActivity.this,"请在酒店中使用此功能");
                }

//                IntentUtil.openActivity(this,RecommendActivity.class);
                break;


        }
    }

    private void closeDrawerLayout() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * 注册监控网络状态改变广播
     */
    private void registerNetWorkReceiver() {
        mBindTvPresenter.registerNetWorkReceiver();
    }
    @Override
    public void showProjection(boolean isLinked) {
        LogUtils.d("savor:hotel 当前为酒店环境");
        mContentLayout.setVisibility(View.VISIBLE);
        mShadeLayout.setVisibility(View.GONE);
        mShadeLayout.removeAllViews();
        if(!mTitleList.contains("投屏")) {
            RecordUtils.onEvent(getApplicationContext(),R.string.home_find_tv);
            mPagerAdapter.addPager(mProjectionFragment,"投屏",0);
            mViewPager.setCurrentItem(0);
            mViewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTabLayout.refresh();
                }
            },100);
        }
    }

    @Override
    public void hideProjection() {
        // 切换到非酒店环境 1.隐藏绑定按钮 2.隐藏点播列表
        LogUtils.d("savor:hotel 切换为非酒店环境");
        mContentLayout.setVisibility(View.VISIBLE);
        mShadeLayout.setVisibility(View.GONE);
        mShadeLayout.removeAllViews();
        if(mTitleList.contains("投屏")) {
            mPagerAdapter.removePager(mProjectionFragment,"投屏");
            mTabLayout.refresh();
            if(prePosition>0) {
                mViewPager.setCurrentItem(prePosition-1);
            }else{
                mViewPager.setCurrentItem(0);
            }
        }
    }

    @Override
    public void refreshData() {
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
        if(mQrcodeDialog==null)
            mQrcodeDialog = new LinkDialog(this,"");
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
        if(AppUtils.isFastDoubleClick(1)) {
            showToast("连接电视成功");
        }
    }

    @Override
    public void startLinkTv() {
        Intent intent = new Intent(this,LinkTvActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent,0);
    }

    @Override
    public void showUnLinkDialog() {
        new CommonDialog(this, "是否与电视断开，\n断开后将无法投屏？"
                , new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.home_break_connect),"break");
                RecordUtils.onEvent(HotspotMainActivity.this,getString(R.string.home_break_connect),hashMap);
                disconnectTv();
            }
        }, new CommonDialog.OnCancelListener() {
                @Override
                public void onCancel() {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(getString(R.string.home_break_connect), "cancel");
                    RecordUtils.onEvent(HotspotMainActivity.this, getString(R.string.home_break_connect), hashMap);
                }

        },"断开连接").show();
    }

    public void disconnectTv() {
        ProjectionManager.getInstance().resetProjection();
        mBindTvPresenter.stopProjection();
        mSession.resetPlatform();
        mSession.setTvBoxIp(null);
    }

    @Override
    public void rotate(RotateProResponse rotateResponse) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == EXTRA_FROM_RECOMMEND) {
            mBackFromInternal = true;
        }else if(resultCode == EXTRA_TV_INFO){
//            if(data!=null) {
//                TvBoxInfo boxInfo = (TvBoxInfo) data.getSerializableExtra(EXRA_TV_BOX);
//                mBindTvPresenter.handleBindCodeResult(boxInfo);
//            }
            mBackFromInternal = true;
        } else if (resultCode == SCAN_QR) {
            if(data!=null) {
                String scanResult = data.getStringExtra("scan_result");
                mBindTvPresenter.handleQrcodeResult(scanResult);
                LogUtils.d("扫描结果：" + scanResult);
            }
            mBackFromInternal = true;
        }else if(resultCode == FROM_APP_BACK) {
            mBackFromInternal = true;
        }
    }

    @Override
    protected void onStop() {
        mBackFromInternal = false;
        ismuteUp = true;
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("savor:pro onresume");
        RecordUtils.onPageStartAndResume(this,this);
        ismuteUp = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this,this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ismuteUp = true;
//        isNeedShowFoundHint = true;
        // 判断如果为绑定并且当前ssid与缓存机顶盒ssid相同提示绑定成功
        if(!mSession.isBindTv()) {
            TvBoxInfo tvBoxInfo = mSession.getTvboxInfo();
            if(tvBoxInfo!=null) {
                LogUtils.d(ConstantValues.LOG_PREFIX+"有缓存的盒子信息，检测三分钟内是否连接到指定wifi");
                checkWifiLinked(tvBoxInfo);
            }else {
                LogUtils.d(ConstantValues.LOG_PREFIX+"无缓存的盒子信息");
            }
        }else {
            if(mChangeWifiDiallog!=null&&mChangeWifiDiallog.isShowing()) {
                mChangeWifiDiallog.dismiss();
            }
        }

        /**应用从后台切换到前台*/
        if(!mBackFromInternal) {
            if(mBindTvPresenter==null) {
                mBindTvPresenter = new BindTvPresenter(this,this,this,this);
            }
            if(!mSession.isBindTv()) {
                mHandler.removeMessages(CHECK_PLATFORM_URL);
                mBindTvPresenter.getSmallPlatformUrl(true);
            }
        }

        refreshLinkStatus();
    }

    public void refreshLinkStatus() {
        if(mProjectionFragment!=null) {
            mProjectionFragment.initLinkStatus();
        }
    }

    /**
     * 检查是否在同一wifi，如果三分钟内连接到同一wifi提示连接成功
     */
    private void checkWifiLinked(TvBoxInfo tvBoxInfo) {
        String ssid = tvBoxInfo.getSsid();
        String localSSid = WifiUtil.getWifiName(this);
        if(!TextUtils.isEmpty(ssid)) {
            if(ssid.endsWith(localSSid)) {
                mSession.setWifiSsid(ssid);
                mSession.setTvBoxUrl(tvBoxInfo);
                refreshLinkStatus();
                initBindcodeResult();
                mHandler.removeMessages(CANCEL_CHECK_WIFI);
            }
        }else {
            // 每隔一秒检测是否已连接同一wifi
            startCheckWifiLinkedTimer();
        }
    }

    /**开启检查是否是同一wifi定时器每隔一秒检查一次*/
    private void startCheckWifiLinkedTimer() {
        mHandler.removeMessages(CHECK_WIFI_LINKED);
        mHandler.sendEmptyMessageDelayed(CHECK_WIFI_LINKED,1000);
    }

    private void bindProjectionService(int type,ServiceConnection connon) {
        mProjectionIntent = new Intent(this, ProjectionService.class);
        mProjectionIntent.putExtra(ProjectionService.EXTRA_TYPE,type);
        bindService(mProjectionIntent, connon, Context.BIND_AUTO_CREATE);
    }

    private void registerPlayOverReceiver() {
        IntentFilter filter = new IntentFilter("play_over");
        mPlayOverReceiver = new PlayOverReceiver();
        registerReceiver(mPlayOverReceiver,filter);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        if(mBindTvPresenter==null) {
            mBindTvPresenter = new BindTvPresenter(this,this,this,this);
        }
        mBindTvPresenter.getSmallPlatformUrl(true);
        LogUtils.d("savor:onRestore");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                closeDrawerLayout();
            }else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    showToast(getString(R.string.confirm_exit_app));
                    exitTime = System.currentTimeMillis();
                } else {
                    exitApp();
                }
            }
        }
        return true;
    }

    private void exitApp() {
        // 清楚图片内存缓存
        GlideImageLoader.getInstance().clearMemory(getApplicationContext());
        // 清楚activity任务栈
        ActivitiesManager.getInstance().popAllActivities();

        // 关闭jetty服务
        Intent stopIntent = new Intent(this,LocalJettyService.class);
        stopService(stopIntent);

        // 关闭发现小平台的service
        Intent stopDescoveryIntent = new Intent(this,SSDPService.class);
        stopService(stopDescoveryIntent);

        Intent intent = new Intent(this, ClearImageCacheService.class);
        intent.putExtra("path",mSession.getCompressPath());
        startService(intent);

//        AppUtils.clearSpecificCacheFile(this,mSession.getCompressPath());

        Intent stopProjectoinIntent = new Intent(this,StopProjectionService.class);
        getApplicationContext().startService(stopProjectoinIntent);

        Process.killProcess(android.os.Process.myPid());
//        finish();

    }

    @Override
    protected void onDestroy() {
        ActivitiesManager.getInstance().popActivity(this);
        if(mPlayOverReceiver!=null) {
            unregisterReceiver(mPlayOverReceiver);
        }
        if(mChangeWifiDiallog!=null) {
            mChangeWifiDiallog.dismiss();
            mChangeWifiDiallog = null;
        }
        if(mHandler!=null) {
            mHandler.removeMessages(CHECK_PLATFORM_URL);
            mHandler.removeCallbacksAndMessages(null);
        }
        if(mSensePresenter!=null) {
            mSensePresenter.onDestroy();
            mSensePresenter = null;
        }
        if(mBindTvPresenter!=null) {
            mBindTvPresenter.onDestroy();
            mBindTvPresenter = null;
        }
        if(mVideoServiceConn!=null)
            unbindService(mVideoServiceConn);
        if(mSlideServiceConn!=null)
            unbindService(mSlideServiceConn);
        stopService(mProjectionIntent);
        LogUtils.d("savor:main destory");
        super.onDestroy();
    }

    int msg = 0;
    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
//        mProgressLayout.loadSuccess();
        switch (method) {
            case POST_SPECIAL_NAME_JSON:
                if( obj instanceof SpecialNameResult) {
                    SpecialNameResult result = (SpecialNameResult) obj;
                    String specialName = result.getSpecialName();
                    if(!TextUtils.isEmpty(specialName)) {
                        mTitleList.remove("专题");
                        String formatSpecialName = StringUtils.formatStrByBytes(specialName);
                        mTitleList.add(formatSpecialName);
                        mTabLayout.refresh();
                        mCategoryNameLabel.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case POST_NOTIFY_TVBOX_STOP_JSON:
//                if(mBackgroudProjectLayout.getVisibility()==View.VISIBLE)
//                    SavorAnimUtil.hideProjectionImage(HotspotMainActivity.this,mBackgroudProjectLayout);
                ProjectionManager.getInstance().resetProjection();
                break;
            case GET_CATEGORY_JSON:
//                isRequesting = false;
//                //               hideLoadingLayout();
////                mProgressLayout.loadSuccess();
//                mFragmentList.clear();
//                mTitleList.clear();
//                mViewPager.removeAllViews();
//                    if (obj instanceof List) {
//                        mContents = (List)obj;
//                        if(mContents!=null&&mContents.size()>0)
//                            cachCategoryData(mContents);
//                        handleCategoryData(mContents);
//                    }

                break;
            case POST_UPGRADE_JSON:
                if (obj instanceof UpgradeInfo) {
                    upGradeInfo = (UpgradeInfo) obj;
                    if (upGradeInfo != null) {
                        setUpGrade();
                    }
                }
                break;
            case TEST_DOWNLOAD_JSON:
                if (obj instanceof FileDownProgress){
                    FileDownProgress fs = (FileDownProgress) obj;
                    long now = fs.getNow();
                    long total = fs.getTotal();
                    if ((int)(((float)now / (float)total)* 100)-msg>=5) {
                        msg = (int) (((float)now / (float)total)* 100);
                        notif.contentView.setTextViewText(R.id.content_view_text1,
                                (Integer) msg + "%");
                        notif.contentView.setProgressBar(R.id.content_view_progress,
                                100, (Integer) msg, false);
                        manager.notify(NOTIFY_DOWNLOAD_FILE, notif);
                    }

                }else if (obj instanceof File) {
                    mSession.setApkDownloading(false);
                    File f = (File) obj;
                    byte[] fRead;
                    String md5Value=null;
                    try {
                        fRead = FileUtils.readFileToByteArray(f);
                        md5Value= AppUtils.getMD5(fRead);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //比较本地文件版本是否与服务器文件一致，如果一致则启动安装
                    if (md5Value!=null&&md5Value.equals(upGradeInfo.getMd5())){
                        //ShowMessage.showToast(this, f.getAbsolutePath());
                        if (manager!=null){
                            manager.cancel(NOTIFY_DOWNLOAD_FILE);
                        }
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setDataAndType(Uri.parse("file://" + f.getAbsolutePath()),
                                "application/vnd.android.package-archive");
                        startActivity(i);
                    }else {
                        if (manager!=null){
                            manager.cancel(NOTIFY_DOWNLOAD_FILE);
                        }
                        ShowMessage.showToast(mContext,"下载失败");
                        setUpGrade();
                    }

                }
                break;
        }
    }

    private void handleCategoryData(List<CategoryItemVo> mContents) {
//        if (mContents != null && mContents.size()>0) {
//            //mAdapter.setData(mContents,true);
//            if(mSession.getHotelid()>0) {
////                if(!mFragmentList.contains(mHotSpotFragment)&&!mHotSpotFragment.isAdded()) {
//                mFragmentList.add(mHotSpotFragment);
//                mTitleList.add("点播");
////                }
//            }
//            mTitleList.add("热点");
//
//            mFragmentList.add(mRedianFragment);
//            for(int i = 0; i< mContents.size(); i++) {
//                mTitleList.add(mContents.get(i).getName());
//                mFragmentList.add(CategoryFragment.getInstance(mContents.get(i).getId()));
//            }
//            mPagerAdapter.setData(mFragmentList,mTitleList);
//            mTabLayout.setViewPager(mViewPager);
//        }
    }

    private void cachCategoryData(List<CategoryItemVo> mContents) {
        String categoryfile = AppUtils.getSavorCachDir(this)+CAGEGORY_FILE_NAME;
        com.common.api.utils.FileUtils.saveObject(this,categoryfile,mContents);
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {

        switch (method) {
            case POST_SPECIAL_NAME_JSON:
                mCategoryNameLabel.setVisibility(View.INVISIBLE);
                break;
            case POST_UPGRADE_JSON:
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    String msg = message.getMessage();
                    if (!ismuteUp){
                        if (!TextUtils.isEmpty(msg)){
                            showToast(msg);
                        }

                    }

                }
                break;
            case GET_CATEGORY_JSON:
                isRequesting = false;
                if(mTitleList !=null&& mTitleList.size()>0) {
//                    mProgressLayout.loadSuccess();
                }else {
//                    mProgressLayout.loadFailure(getString(R.string.network_disable_click));
                }
                break;

        }
    }

    @Override
    public void onNetworkFailed(AppApi.Action method) {
        switch (method) {
            case GET_CATEGORY_JSON:
                isRequesting = false;
                break;
        }
    }
    private void upgrade(){
        AppApi.Upgrade(mContext,this,mSession.getVersionCode());
    }

    private void setUpGrade(){
        String upgradeUrl = upGradeInfo.getOss_addr();
        //String upgradeUrl = "http://a5.pc6.com/pc6_soure/2016-2/com.huiche360.huiche_8.apk";

        if (!TextUtils.isEmpty(upgradeUrl)) {
            if (STIDUtil.needUpdate(mSession, upGradeInfo)) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.home_update),"ensure");
                RecordUtils.onEvent(this,getString(R.string.home_update),hashMap);
                String[] content = upGradeInfo.getRemark();
//                if (content != null && !"".equals(content)) {
//                    content = upGradeInfo.getRemark()
//                            .replace("|", "\n");
//                }
                if (upGradeInfo.getUpdate_type() == 1) {
                    mUpgradeDialog = new UpgradeDialog(
                            mContext,
                            TextUtils.isEmpty(upGradeInfo.getVersion_code()+"")?"":"新版本：V"+upGradeInfo.getVersion_code(),
                            content,
                            this.getString(R.string.confirm),
                            forUpdateListener
                    );
                    mUpgradeDialog.show();
                }else {
                    mUpgradeDialog = new UpgradeDialog(
                            mContext,
                            TextUtils.isEmpty(upGradeInfo.getVersion_code()+"")?"":"新版本：V"+upGradeInfo.getVersion_code(),
                            content,
                            this.getString(R.string.cancel),
                            this.getString(R.string.confirm),
                            cancelListener,
                            forUpdateListener
                    );
                    mUpgradeDialog.show();
                }


            }else{
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.home_update),"cancel");
                RecordUtils.onEvent(this,getString(R.string.home_update),hashMap);
                if (!ismuteUp){
                    ShowMessage.showToast(mContext, "当前为最新版本");
                }

            }
        }else {
            if (!ismuteUp){
                ShowMessage.showToast(mContext, "当前为最新版本");
            }
        }


    }

    private View.OnClickListener forUpdateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mUpgradeDialog.dismiss();
           downLoadApk(upGradeInfo.getOss_addr());
           // downLoadApk("http://download.savorx.cn/app-xiaomi-debug.apk");
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//			mMDialog.dismiss();
            mUpgradeDialog.dismiss();
        }
    };
    protected void downLoadApk(String apkUrl) {
        //TODO 测试，记得去掉
//		apkUrl = "http://test.ailemy.com/mobile/download/aileBuy.apk";
        if (!mSession.isApkDownloading()){
            mSession.setApkDownloading(true);
            // 下载apk包
            initNotification();
            AppApi.downApp(mContext,apkUrl, HotspotMainActivity.this);
        }else{
            ShowMessage.showToast(mContext,"下载中,请稍候");
        }
    }

    /**
     * 初始化Notification
     */
    private void initNotification() {
        manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notif = new Notification();
        notif.icon = R.drawable.ic_launcher;
        notif.tickerText = "下载通知";
        // 通知栏显示所用到的布局文件
        notif.contentView = new RemoteViews(mContext.getPackageName(),
                R.layout.download_content_view);
        notif.contentIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(
                mContext.getPackageName()+".debug"), PendingIntent.FLAG_CANCEL_CURRENT);
        // notif.defaults = Notification.DEFAULT_ALL;
        manager.notify(NOTIFY_DOWNLOAD_FILE, notif);
    }

    @Override
    public void loadDataEmpty() {
//        mProgressLayout.startLoading();
        getCategoryListTab(true);
    }

    @Override
    public void loadFailureNoNet() {
//        mProgressLayout.setVisibility(View.GONE);
        getCategoryListTab(true);
    }

    @Override
    public void loadFailure() {
//        mProgressLayout.startLoading();
        getCategoryListTab(true);
    }

    private String getTopActivityName(Context context) {
        ActivityManager manager = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE));
        //getRunningTasks() is deprecated since API Level 21 (Android 5.0)
        List localList = manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo)localList.get(0);
        return localRunningTaskInfo.topActivity.getClassName();
    }

    public void stopSlide() {
        if(projectionService!=null) {
            projectionService.stopSlide();
        }
    }

    public class PlayOverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("savor:projection slideplayover" );
            if(projectionService!=null) {
                initBackgroundProjectionHint();
                projectionService.stopQuerySeek();
            }
        }
    }
}
