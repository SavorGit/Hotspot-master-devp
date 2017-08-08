//package com.savor.savorphone.widget;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.view.GravityCompat;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//
//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.common.api.utils.AppUtils;
//import com.common.api.utils.LogUtils;
//import com.savor.savorphone.R;
//import com.savor.savorphone.SavorApplication;
//import com.savor.savorphone.activity.HotelMapListActivity;
//import com.savor.savorphone.activity.PdfListActivity;
//import com.savor.savorphone.activity.PhotoActivity;
//import com.savor.savorphone.activity.SlideActivity;
//import com.savor.savorphone.activity.VideoShareActivity;
//import com.savor.savorphone.adapter.ResturantAdapter;
//import com.savor.savorphone.bean.HotelMapBean;
//import com.savor.savorphone.bean.HotelMapCache;
//import com.savor.savorphone.core.ApiRequestListener;
//import com.savor.savorphone.core.AppApi;
//import com.savor.savorphone.core.Session;
//import com.savor.savorphone.location.LocationService;
//import com.savor.savorphone.utils.BaiduLocationUtil;
//import com.savor.savorphone.utils.RecordUtils;
//import com.savor.savorphone.utils.SavorAnimUtil;
//
//import java.util.List;
//
///**
// * 投屏菜单
// * Created by hezd on 2017/1/19.
// */
//
//public class ProjectionDialog implements View.OnClickListener, ApiRequestListener {
//    private static final int LOCATTE_SUCCESS = 0x1;
//    private static final int LOCATE_FAILED = 0x2;
//    public static boolean isPerformLocating ;
//    private final View mDependencyView;
//    private Activity mContext;
//    private PopupWindow mShowToScreenWindow;
//    private final Session session;
//    private ImageView mCloseBtn;
//    private ImageView mPhotoProBtn;
//    private ImageView mVideoProBtn;
//    private ImageView mFileProBtn;
//    private RecyclerView mRestuarentLv;
//    private LinearLayout mLoadingLayout;
//    private ResturantAdapter mResturantAdapter;
//    private ImageView mSlidesBtn;
//    private LinearLayout mResturantLayout;
//    private RelativeLayout mLoadErrorLayout;
//    private LocationService locationService;
//    private List<HotelMapBean> hotelList;
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            BDLocation location;
//            switch (msg.what) {
//                case LOCATTE_SUCCESS:
//                    location = (BDLocation) msg.obj;
//                    locateSuccess(location);
//                    break;
//                case LOCATE_FAILED:
//                    location = (BDLocation) msg.obj;
//                    locateFailed(location);
//                    break;
//            }
//        }
//    };
//    private ImageView mLookMoreBtn;
//
//    public ProjectionDialog(Activity context, View dependencyView) {
//        this.mContext = context;
//        this.mDependencyView = dependencyView;
//        session = Session.get(context);
//        init(context);
//    }
//
//    private void init(Activity context) {
//        mShowToScreenWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//        View contentView = View.inflate(context, R.layout.dialog_projection,null);
//        mResturantLayout = (LinearLayout) contentView.findViewById(R.id.ll_resturant_list);
//        mLookMoreBtn = (ImageView) contentView.findViewById(R.id.iv_look_more);
//        mRestuarentLv = (RecyclerView) contentView.findViewById(R.id.lv_restuarent);
//        mLoadingLayout = (LinearLayout) contentView.findViewById(R.id.ll_loading);
//        mCloseBtn = (ImageView) contentView.findViewById(R.id.iv_close);
//        mPhotoProBtn = (ImageView) contentView.findViewById(R.id.iv_tupian);
//        mVideoProBtn = (ImageView) contentView.findViewById(R.id.iv_shipin);
//        mFileProBtn = (ImageView) contentView.findViewById(R.id.iv_wenjian);
//        mSlidesBtn = (ImageView) contentView.findViewById(R.id.iv_huandengpian);
//        mLoadErrorLayout = (RelativeLayout) contentView.findViewById(R.id.rl_load_error);
//        mShowToScreenWindow.setContentView(contentView);
//        mPhotoProBtn.setOnClickListener(this);
//        mLookMoreBtn.setOnClickListener(this);
//        mVideoProBtn.setOnClickListener(this);
//        mFileProBtn.setOnClickListener(this);
//        mCloseBtn.setOnClickListener(this);
//        mSlidesBtn.setOnClickListener(this);
//        mLoadErrorLayout.setOnClickListener(this);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRestuarentLv.setLayoutManager(linearLayoutManager);
//        mResturantAdapter = new ResturantAdapter(mContext);
//        mRestuarentLv.setAdapter(mResturantAdapter);
//        HotelMapCache cache = session.getmHotelMapCache();
//        if(cache!=null) {
//            List<HotelMapBean> hotelMapList = cache.getHotelMapList();
//            mResturantAdapter.setData(hotelMapList);
//            mLoadingLayout.setVisibility(View.GONE);
//        }
//
//        startLocation();
//        startShowProAnim();
//    }
//
//    private void startShowProAnim() {
//        SavorAnimUtil.showProBtnAnim(mContext, mPhotoProBtn);
//        mVideoProBtn.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SavorAnimUtil.showProBtnAnim(mContext,mVideoProBtn);
//            }
//        },50);
//        mSlidesBtn.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SavorAnimUtil.showProBtnAnim(mContext,mSlidesBtn);
//            }
//        },100);
//        mFileProBtn.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SavorAnimUtil.showProBtnAnim(mContext,mFileProBtn);
//            }
//        },150);
//    }
//
//    private void startLocation() {
//        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
//        isPerformLocating = false;
//        if(locationService==null) {
//            locationService = new LocationService(mContext);
//            locationService.registerListener(mSectorMenuListener);
//        }
//        locationService.start();
//    }
//
//
//    private String currentLng = "";
//    private String currentLat = "";
//    /*****
//     *
//     * 点击首页底部悬浮窗进行定位结果回调
//     *
//     */
//    private BDLocationListener mSectorMenuListener = new BDLocationListener() {
//
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            String hid = session.getHotelid()==0?"":(String.valueOf(session.getHotelid()));
//            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
//                Message msg = new Message();
//                msg.what = LOCATTE_SUCCESS;
//                msg.obj = location;
//                mHandler.removeMessages(LOCATTE_SUCCESS);
//                mHandler.sendMessageDelayed(msg,10);
//            }else {
//                LogUtils.d("savor:location 定位失败");
//                Message msg = new Message();
//                msg.what = LOCATE_FAILED;
//                msg.obj = location;
//                mHandler.removeMessages(LOCATE_FAILED);
//                mHandler.sendMessageDelayed(msg,10);
//
//            }
//        }
//
//        public void onConnectHotSpotMessage(String s, int i){
//            LogUtils.d("savor:location onconnect = "+s);
////            int hotelid = Session.get(mContext).getHotelid();
////            String hotelId = session.getHotelid()==0?"":(session.getHotelid()+"");
////            AppApi.getNearlyHotel(mContext,ProjectionDialog.this,"","",hotelId);
//        }
//    };
//
//    private void locateFailed(BDLocation location) {
//        String hid = String.valueOf(session.getHotelid());
//        HotelMapCache cache = session.getmHotelMapCache();
//        if(cache!=null) {
//            List<HotelMapBean> hotelMapList = cache.getHotelMapList();
//            String cacheLat = cache.getLat();
//            String cacheLng = cache.getLng();
//            double cLat = 0;
//            double cLng = 0;
//            try {
//                cLng = Double.valueOf(cacheLng);
//            }catch (Exception e) {
//
//            }
//            try {
//                cLat = Double.valueOf(cacheLat);
//            }catch (Exception e) {
//
//            }
//            double distance = BaiduLocationUtil.getLongDistance(cLng, cLat,location.getLongitude(),location.getLatitude());
//            LogUtils.d("savor:location 与上次缓存酒店距离 distance="+distance);
//            if(distance>100) {
//                AppApi.getNearlyHotel(mContext,ProjectionDialog.this,"","",hid);
//            }else {
//                mResturantAdapter.setData(hotelMapList);
//                showResturantList();
//                isPerformLocating = false;
//            }
//        }else {
//            AppApi.getNearlyHotel(mContext,ProjectionDialog.this,"","",hid);
//        }
//    }
//
//    private void locateSuccess(BDLocation location) {
//        if(isPerformLocating) {
//            return;
//        }
//        isPerformLocating = true;
//        LogUtils.d("savor:location lat="+location.getLatitude()+"\r\n lng="+location.getLongitude());
//        Session session = Session.get(mContext);
//        session.setLatestLat(location.getLatitude());
//        session.setLatestLng(location.getLongitude());
//        String hotelId = session.getHotelid()==0?"":(session.getHotelid()+"");
//        String lng = "";
//        String lat = "";
//        try {
//            lng = String.valueOf(location.getLongitude());
//        }catch (Exception e) {
//
//        }
//        try {
//            lat = String.valueOf(location.getLatitude());
//        }catch (Exception e) {
//
//        }
//        currentLng = lng;
//        currentLat = lat;
//        HotelMapCache cache = session.getmHotelMapCache();
//        if(cache!=null) {
//            String cacheLat = cache.getLat();
//            String cacheLng = cache.getLng();
//            double cLat = 0;
//            double cLng = 0;
//            try {
//                cLng = Double.valueOf(cacheLng);
//            }catch (Exception e) {
//
//            }
//            try {
//                cLat = Double.valueOf(cacheLat);
//            }catch (Exception e) {
//
//            }
//
//            double distance = BaiduLocationUtil.getLongDistance(cLng, cLat,location.getLongitude(),location.getLatitude());
//            LogUtils.d("savor:location 与上次缓存酒店距离 distance="+distance);
//            if(distance>100) {
//                AppApi.getNearlyHotel(mContext,ProjectionDialog.this,lng,lat,hotelId);
//            }else {
//                isPerformLocating = false;
//            }
//        }else {
//            AppApi.getNearlyHotel(mContext,ProjectionDialog.this,lng,lat,hotelId);
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.iv_look_more:
//                hideDialog(new OnAnimEndListener() {
//                    @Override
//                    public void onEnd() {
//                        if(!AppUtils.isFastDoubleClick(1)) {
//                            Intent intent = new Intent(mContext, HotelMapListActivity.class);
//                            mContext.startActivity(intent);
//                        }
//                    }
//                });
//                break;
//            case R.id.rl_load_error:
//                showLoadingLayout();
//                startLocation();
//                break;
//            case R.id.iv_close:
//                if(locationService!=null) {
//                    locationService.stop();
//                }
//                hideDialog(null);
//                break;
//            case R.id.iv_tupian:
//                hideDialog(new OnAnimEndListener() {
//                    @Override
//                    public void onEnd() {
//                        Intent intent = new Intent(mContext,PhotoActivity.class);
//                        mContext.startActivityForResult(intent,0);
//                    }
//                });
//
//                RecordUtils.onEvent(mContext,mContext.getString(R.string.home_pic));
//                break;
//            case R.id.iv_shipin:
//                hideDialog(new OnAnimEndListener() {
//                    @Override
//                    public void onEnd() {
//                        Intent intent = new Intent(mContext,VideoShareActivity.class);
//                        mContext.startActivityForResult(intent,0);
//                    }
//                });
//
//                RecordUtils.onEvent(mContext,mContext.getString(R.string.home_video));
//                break;
//            case R.id.iv_huandengpian:
//                hideDialog(new OnAnimEndListener() {
//                    @Override
//                    public void onEnd() {
//                        Intent intent = new Intent(mContext,SlideActivity.class);
//                        mContext.startActivityForResult(intent,0);
//                    }
//                });
//
//                RecordUtils.onEvent(mContext,mContext.getString(R.string.home_slide));
//                break;
//            case R.id.iv_wenjian:
//                hideDialog(new OnAnimEndListener() {
//                    @Override
//                    public void onEnd() {
//                        Intent intent = new Intent(mContext,PdfListActivity.class);
//                        mContext.startActivityForResult(intent,0);
//                    }
//                });
//                RecordUtils.onEvent(mContext,mContext.getString(R.string.file_to_screen_list));
//                break;
//        }
//    }
//
//    private void showLoadingLayout() {
//        mLoadErrorLayout.setVisibility(View.GONE);
//        mLoadingLayout.setVisibility(View.VISIBLE);
//    }
//
//    public void hideDialog(final OnAnimEndListener listener) {
//        isPerformLocating = false;
//        mPhotoProBtn.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SavorAnimUtil.hideProBtnAnim(mContext, mPhotoProBtn, new SavorAnimUtil.OnAnimationEndListener() {
//                    @Override
//                    public void onEnd() {
//                        if(listener!=null) {
//                            listener.onEnd();
//                        }
//                        dismiss();
//                    }
//                });
//            }
//        },150);
//        mVideoProBtn.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SavorAnimUtil.hideProBtnAnim(mContext,mVideoProBtn,null);
//            }
//        },100);
//        mSlidesBtn.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SavorAnimUtil.hideProBtnAnim(mContext,mSlidesBtn,null);
//            }
//        },50);
//        mFileProBtn.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SavorAnimUtil.hideProBtnAnim(mContext,mFileProBtn,null);
//            }
//        },0);
//    }
//
//    public void show() {
//        if(mShowToScreenWindow!=null)
//            mShowToScreenWindow.showAtLocation(mDependencyView, GravityCompat.START,0,0);
//    }
//
//    public void dismiss() {
//        if(mShowToScreenWindow!=null&&isShowing())
//            mShowToScreenWindow.dismiss();
//        LocationService locationService = SavorApplication.getInstance().locationService;
//        if(locationService!=null) {
//            SavorApplication.getInstance().locationService.stop();
//        }
//    }
//
//    public boolean isShowing() {
//        return mShowToScreenWindow.isShowing();
//    }
//
//    @Override
//    public void onSuccess(AppApi.Action method, Object obj) {
//        switch (method) {
//            case GET_NEARLY_HOTEL_JSON:
//                if(obj instanceof List) {
//                    hotelList = (List<HotelMapBean>) obj;
//                    mResturantAdapter.setData(hotelList);
//                    showResturantList();
//                    HotelMapCache cache = new HotelMapCache();
//                    cache.setLat(currentLat);
//                    cache.setLng(currentLng);
//                    cache.setHotelMapList(hotelList);
//                    session.setHotelMapCache(cache);
//                    isPerformLocating = false;
//                }
//                break;
//        }
//    }
//
//    private void showResturantList() {
//        LogUtils.d("savor:location 显示本地缓存，关闭loading布局");
//        mLoadingLayout.setVisibility(View.GONE);
//        mLoadErrorLayout.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onError(AppApi.Action method, Object obj) {
//        switch (method) {
//            case GET_NEARLY_HOTEL_JSON:
//                if(locationService!=null) {
//                    locationService.stop();
//                }
//                showLoadingErrorLayout();
//                isPerformLocating = false;
//                break;
//        }
//    }
//
//    private void showLoadingErrorLayout() {
//        mLoadErrorLayout.setVisibility(View.VISIBLE);
//        mLoadingLayout.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onNetworkFailed(AppApi.Action method) {
//
//    }
//
//    public interface OnAnimEndListener {
//        void onEnd();
//    }
//}
