package com.savor.savorphone.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.common.api.utils.LogUtils;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.HotelMapListAdapter;
import com.savor.savorphone.bean.HotelMapBean;
import com.savor.savorphone.bean.HotelMapListData;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.location.LocationService;
import com.savor.savorphone.utils.BaiduLocationUtil;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.SavorAnimUtil;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.ProgressBarView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bushlee on 2017/5/24.
 */

public class HotelMapListActivity extends BaseActivity implements View.OnClickListener,
        ApiRequestListener,ProgressBarView.ProgressBarViewClickListener{

    private Context context;
    private TextView tv_center;
    private ImageView iv_left;
    private PullToRefreshListView mPullRefreshListView;
    private int pageNum = 1;
    private List<HotelMapBean> maplist= new ArrayList<HotelMapBean>();
    private ProgressBarView mProgressLayout;
    private String hotelId;
    private HotelMapListAdapter mAdapter;
    public static boolean isPerformLocating ;
    private LocationService locationService;
    private static final int LOCATTE_SUCCESS = 0x1;
    private static final int LOCATE_FAILED = 0x2;
    private String currentLng = "";
    private String currentLat = "";
    private boolean isTopPulltoRefreshData = false;
    private boolean isShowDialog = false;
    private CommonDialog dialog;
    private TextView mRefreshDataHinttv;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            BDLocation location;
            switch (msg.what) {
                case LOCATTE_SUCCESS:
                    location = (BDLocation) msg.obj;
                    locateSuccess(location);
                    break;
                case LOCATE_FAILED:
                    location = (BDLocation) msg.obj;
                    locateFailed(location);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);
        context = this;
        getIntentDate();
        getViews();
        setViews();
        setListeners();
        startLocation();
        ShowDialog();
        RecordUtils.onEvent(HotelMapListActivity.this,getString(R.string.hotel_map_list));
    }

    private void getIntentDate(){
        Intent intent = getIntent();
        if (intent != null) {
            isShowDialog = intent.getBooleanExtra("isShowDialog",false);
        }

    }

    private void getDate(){
        if (pageNum == 1 && (maplist!=null&&maplist.size()==0)) {
            mProgressLayout.startLoading();
        }
        AppApi.getAllDistance(this,hotelId,currentLng,currentLat,pageNum+"",this);
    }

    private void ShowDialog(){
        if (isShowDialog) {
            dialog = new CommonDialog(this,"进入餐厅连接包间wifi，精彩内容即可投屏到电视上！");
            dialog.show();
        }
    }
    @Override
    public void getViews() {
        tv_center = (TextView) findViewById(R.id.tv_center);
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.listview);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        mProgressLayout = (ProgressBarView)findViewById(R.id.pbv_loading);
        mRefreshDataHinttv = (TextView) findViewById(R.id.tv_refresh_data_hint);
    }

    private void showRefreshHintAnimation(final String hint) {
        mRefreshDataHinttv.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshDataHinttv.setText(hint);
                SavorAnimUtil.startListRefreshHintAnimation(mRefreshDataHinttv);
            }
        },1000);
    }
    @Override
    public void setViews() {
        tv_center.setText("提供投屏的餐厅");
        iv_left = (ImageView) findViewById(R.id.iv_left);
        hotelId = mSession.getHotelid()<0?"0":(mSession.getHotelid()+"");
        mAdapter = new HotelMapListAdapter(this);
        mPullRefreshListView.setAdapter(mAdapter);
//        HotelMapListData hotelMapList = mSession.getHotelMapList();
//        if(hotelMapList!=null) {
//            List<HotelMapBean> hotelList = hotelMapList.getHotelMapList();
//            mProgressLayout.loadSuccess();
//            mAdapter.setData(hotelList);
//        }
    }

    @Override
    public void setListeners() {
        iv_left.setOnClickListener(this);
        mPullRefreshListView.setOnRefreshListener(onRefreshListener);
        mPullRefreshListView.setOnLastItemVisibleListener(onLastItemVisibleListener);
        mProgressLayout.setProgressBarViewClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                onBackPressed();
                break;
        }

    }

    PullToRefreshBase.OnLastItemVisibleListener onLastItemVisibleListener = new PullToRefreshBase.OnLastItemVisibleListener() {
        @Override
        public void onLastItemVisible() {
            RecordUtils.onEvent(HotelMapListActivity.this,getString(R.string.hotel_map_list_last));
            getDate();
        }
    };

    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            RecordUtils.onEvent(HotelMapListActivity.this,getString(R.string.hotel_map_list_refresh));
            pageNum = 1;
            isTopPulltoRefreshData = true;
            getDate();
        }
    };

    private void startLocation() {
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        isPerformLocating = false;
        if(locationService==null) {
            locationService = new LocationService(this);
            locationService.registerListener(mSectorMenuListener);
        }
        locationService.start();
    }

    private BDLocationListener mSectorMenuListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            String hid = mSession.getHotelid()==0?"":(String.valueOf(mSession.getHotelid()));
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                Message msg = new Message();
                msg.what = LOCATTE_SUCCESS;
                msg.obj = location;
                mHandler.removeMessages(LOCATTE_SUCCESS);
                mHandler.sendMessageDelayed(msg,10);
            }else {
                LogUtils.d("savor:location 定位失败");
                Message msg = new Message();
                msg.what = LOCATE_FAILED;
                msg.obj = location;
                mHandler.removeMessages(LOCATE_FAILED);
                mHandler.sendMessageDelayed(msg,10);

            }
        }

        public void onConnectHotSpotMessage(String s, int i){
            LogUtils.d("savor:location onconnect = "+s);
//            int hotelid = Session.get(mContext).getHotelid();
//            String hotelId = session.getHotelid()==0?"":(session.getHotelid()+"");
//            AppApi.getNearlyHotel(mContext,ProjectionDialog.this,"","",hotelId);
        }
    };

    private void locateFailed(BDLocation location) {

        getDate();
    }

    private void locateSuccess(BDLocation location) {
        if(isPerformLocating) {
            return;
        }
        isPerformLocating = true;
        LogUtils.d("savor:location lat="+location.getLatitude()+"\r\n lng="+location.getLongitude());
        Session session = Session.get(this);
        session.setLatestLat(location.getLatitude());
        session.setLatestLng(location.getLongitude());
        int hid = session.getHotelid();
        hid = hid>0?hid:0;
        String hotelId = String.valueOf(hid);
        String lng = "";
        String lat = "";
        HotelMapListData hotelMapList = mSession.getHotelMapList();
        if(hotelMapList!=null) {
            String cacheLat = hotelMapList.getLat();
            String cacheLng = hotelMapList.getLng();
            double cLat = 0;
            double cLng = 0;
            try {
                cLng = Double.valueOf(cacheLng);
            }catch (Exception e) {

            }
            try {
                cLat = Double.valueOf(cacheLat);
            }catch (Exception e) {

            }

            double distance = BaiduLocationUtil.getLongDistance(cLng, cLat,location.getLongitude(),location.getLatitude());
            if(distance>100) {
                getDate();
            }else {
                isPerformLocating = false;
            }
        }else {
            getDate();
        }


      // AppApi.getNearlyHotel(mContext,ProjectionDialog.this,lng,lat,hotelId);

    }

    private void handleBottomData(List<HotelMapBean> hotelList) {
        if(hotelList!=null&&hotelList.size()>=0) {
            if (isTopPulltoRefreshData) {
                maplist.clear();
                isTopPulltoRefreshData = false;
                showRefreshHintAnimation("更新成功");
            }
            maplist.addAll(hotelList);
            mAdapter.setData(maplist);
            if(hotelList.size()<10) {
                mPullRefreshListView.onLoadComplete(false,true);
            }else{
                mPullRefreshListView.onLoadComplete(true,false);
            }
        }else {
            mPullRefreshListView.onLoadComplete(false,true);
        }
    }
    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        mPullRefreshListView.onRefreshComplete();
        mProgressLayout.loadSuccess();
        switch (method) {
            case GET_ALL_DISTANCE_JSON:
                isPerformLocating = false;
                if(obj instanceof List) {
                    List<HotelMapBean> hotelList = (List<HotelMapBean>) obj;
                    handleBottomData(hotelList);
//                    cacheHotemMapList(hotelList);
                    pageNum++;
                }
                break;
            default:
                break;

        }
    }

//    private void cacheHotemMapList(List<HotelMapBean> hotelList) {
//        if(pageNum == 1) {
//            HotelMapListData hotelMapListData  = new HotelMapListData();
//            hotelMapListData.setLat(currentLat);
//            hotelMapListData.setLng(currentLng);
//            hotelMapListData.setHotelMapList(hotelList);
//            mSession.setHotelMapList(hotelMapListData);
//        }
//    }


    @Override
    public void onError(AppApi.Action method, Object obj) {
//        super.onError(method, obj);
        switch (method) {
            case GET_ALL_DISTANCE_JSON:
                mProgressLayout.loadSuccess();

                if (obj instanceof ResponseErrorMessage){
                    ResponseErrorMessage errorMessage = (ResponseErrorMessage)obj;
                    String statusCode = String.valueOf(errorMessage.getCode());
                    if (AppApi.ERROR_TIMEOUT.equals(statusCode)){
                        mProgressLayout.loadFailure("数据加载超时");
                        showRefreshHintAnimation("数据加载超时");
                    }else if (AppApi.ERROR_NETWORK_FAILED.equals(statusCode)){
                        mProgressLayout.loadFailure("网络异常,点击重试");
                        showRefreshHintAnimation("无法连接到网络,请检查网络设置");
                    }
                }

                break;
            case GET_HIT_EGG_JSON:


                break;



        }
    }

    @Override
    public void loadDataEmpty() {
        mProgressLayout.setVisibility(View.GONE);
        getDate();
    }

    @Override
    public void loadFailureNoNet() {
        mProgressLayout.setVisibility(View.GONE);
        getDate();
    }

    @Override
    public void loadFailure() {
        mProgressLayout.setVisibility(View.GONE);
        getDate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationService!=null) {
            locationService.stop();
        }
    }
}
