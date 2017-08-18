package com.savor.savorphone.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.activity.ImageTextActivity;
import com.savor.savorphone.activity.PictureSetActivity;
import com.savor.savorphone.activity.VideoPlayVODNotHotelActivity;
import com.savor.savorphone.adapter.WealthLifeAdapter;
import com.savor.savorphone.bean.AliLogBean;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.CommonListResult;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.log.AliLogFileUtil;
import com.savor.savorphone.utils.STIDUtil;
import com.savor.savorphone.utils.SavorAnimUtil;
import com.savor.savorphone.utils.SavorCacheUtil;
import com.savor.savorphone.widget.ProgressBarView;
import com.savor.savorphone.widget.ProgressBarView.ProgressBarViewClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 创富生活fragment
 * Created by Administrator on 2017/7/4.
 */

public class WealthLifeFragment extends BaseFragment implements ApiRequestListener,ProgressBarViewClickListener,
        PullToRefreshListView.NetworkUnavailableOnClick ,
        AbsListView.OnScrollListener{
    private static final String TAG = "WealthLifeFragment";
    private static final int HEAD_REFRESH = 10101;
    private static final int BOTTOM_REFRESH = 10102;
    private Context context;
    private PullToRefreshListView pullToRefreshListView;
    private TextView refreshDataHintTV;
    private ProgressBarView mProgressLayout;
    private CommonListResult listResult;
    private WealthLifeAdapter wealthLifeAdapter=null;
    private int currentRefreshState = REFRESH_TYPE_NONE;
    public static final int REFRESH_TYPE_NONE = 0;
    public static final int REFRESH_TYPE_TOP = 1;
    public static final int REFRESH_TYPE_BOTTOM = 2;
    private String UUID;
    /**只有头部刷新的时候才会显示*/
    private boolean refreshData=false;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            String sort_num = (String) msg.obj;
            switch (what){
                case HEAD_REFRESH:
                   // listItems.clear();
                case BOTTOM_REFRESH:
                    getDataFromServer(sort_num);
                    break;
            }
        }
    };

    /**
     * 101:创富，102:生活
     */
    private int category_id=0;
    private List<CommonListItem> listItems = new ArrayList<>();
    private List<CommonListItem> list;
    /**是否有创富缓存数据*/
    private boolean isHasChuangFCache;
    /**是否有生活缓存数据*/
    private boolean isHasLifeCache;

    @Override
    public String getFragmentName() {
        return TAG;
    }
    public static WealthLifeFragment getInstance(int type) {
        WealthLifeFragment wealthLifeFragment = new WealthLifeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        wealthLifeFragment.setArguments(bundle);
        return  wealthLifeFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wealth_life,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        Bundle argument = getArguments();
        category_id = argument.getInt("type");
        initViews(view);
        setViews();
        UUID = System.currentTimeMillis()+"";
        writeAppLogPv();
    }

    private void initViews(View view){

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.wl_listview);
        wealthLifeAdapter = new WealthLifeAdapter(context,listItems);
        pullToRefreshListView.setAdapter(wealthLifeAdapter);
        pullToRefreshListView.setOnItemClickListener(itemClickListener);
        pullToRefreshListView.setOnRefreshListener(onRefreshListener);
        pullToRefreshListView.setOnLastItemVisibleListener(onLastItemVisibleListener);
        mProgressLayout = (ProgressBarView) view.findViewById(R.id.pbv_loading);
        mProgressLayout.setProgressBarViewClickListener(this);
        refreshDataHintTV = (TextView) view.findViewById(R.id.tv_refresh_data_hint);
        pullToRefreshListView.setNetworkUnavailableOnClick(this);
    }

    @Override
    public void setViews() {
        if(101 == category_id) {// 创富
            List<CommonListItem> chuangFData = SavorCacheUtil.getInstance().getChuangFData(getActivity());
            if(chuangFData!=null&&chuangFData.size()>0) {
                isHasChuangFCache = true;
                wealthLifeAdapter.setData(chuangFData);
            }else {
                isHasChuangFCache = false;
            }
        }else if(102 == category_id) {// 生活
            List<CommonListItem> lifeData = SavorCacheUtil.getInstance().getLifeData(getActivity());
            if(lifeData!=null&&lifeData.size()>0) {
                isHasLifeCache = true;
                wealthLifeAdapter.setData(lifeData);
            }else {
                isHasLifeCache = false;
            }

        }
        pullToRefreshListView.setOnScrollListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(listItems!=null) {
            listItems.clear();
        }
        initDisplay();
    }

    private void initDisplay(){
        getDataFromServer("0");
    }

    private void getDataFromServer(String sort_num){
        List<CommonListItem> chuangFData = SavorCacheUtil.getInstance().getChuangFData(getActivity());
        List<CommonListItem> lifeData = SavorCacheUtil.getInstance().getLifeData(getActivity());
        if ((101 == category_id&&chuangFData==null)||(102 == category_id&&lifeData==null)){
            mProgressLayout.startLoading();
        }

        AppApi.getWealthLifeList(context,this,category_id+"",sort_num);
    }

    OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            CommonListItem item = (CommonListItem)parent.getItemAtPosition(position);
            writeAppLog(item,"click");
            if (item!=null){
                int type = Integer.valueOf(item.getType());
                switch (type){
                    case 0:
                    case 1:
                        item.setCategoryId(category_id+"");
                        Intent intent = new Intent(context, ImageTextActivity.class);
                        intent.putExtra("item",item);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(context, PictureSetActivity.class);
                        intent.putExtra("voditem",item);
                        intent.putExtra("content_id",item.getArtid());
                        intent.putExtra("category_id",category_id);
                        startActivity(intent);
                        break;
                    case 3:
                    case 4:
                        intent = new Intent(context, VideoPlayVODNotHotelActivity.class);
                        item.setCategoryId(category_id+"");
                        intent.putExtra("voditem",item);
                        startActivity(intent);
                        break;
                }
            }
        }
    };

    private boolean isTopRefresh;
    OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            currentRefreshState = REFRESH_TYPE_TOP;
            refreshData = true;
            Message message = Message.obtain();
            message.what = HEAD_REFRESH;
            message.obj = "0";
            handler.sendMessage(message);
        }
    };

    OnLastItemVisibleListener onLastItemVisibleListener = new OnLastItemVisibleListener() {
        @Override
        public void onLastItemVisible() {
            currentRefreshState = REFRESH_TYPE_BOTTOM;
            bottomRefresh();
        }
    };

    private void bottomRefresh() {
        List<CommonListItem> data = wealthLifeAdapter.getData();
        if (data!=null&&data.size()>0){
            refreshData = false;
            CommonListItem item = data.get(data.size()-1);
            Message message = Message.obtain();
            message.what = BOTTOM_REFRESH;
            message.obj = item.getSort_num();
            handler.sendMessage(message);
        }
    }

    private void showRefreshHintAnimation(final String hint) {
        refreshDataHintTV.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshDataHintTV.setText(hint);
                SavorAnimUtil.startListRefreshHintAnimation(refreshDataHintTV);
            }
        },500);
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        setPtrSuccessComplete();
        mProgressLayout.loadSuccess();
        switch (method){
            case POST_WEALTH_LIFE_LIST_JSON:
//                if (obj instanceof List<?>){
//                    list = (List<CommonListItem>) obj;
//                    handleWealthData(list);
//                    if(listItems.size()<=20) {
//                        // 缓存第一页数据
//                        if(101 == category_id) {
//                            SavorCacheUtil.getInstance().cacheChuangFuData(getActivity(),list);
//                        }else if(102 == category_id) {
//                            SavorCacheUtil.getInstance().cacheLifeData(getActivity(),list);
//                        }
//                    }
//                }

                if(obj instanceof CommonListResult) {
                    listResult= (CommonListResult) obj;
                    if (listResult != null) {
                        list =  listResult.getList();
                        handleWealthData(list);
                       // handleVodList(mList);
                        if(listItems.size()<=20) {
                            // 缓存第一页数据
                            if(101 == category_id) {
                                SavorCacheUtil.getInstance().cacheChuangFuData(getActivity(),list);
                            }else if(102 == category_id) {
                                SavorCacheUtil.getInstance().cacheLifeData(getActivity(),list);
                            }
                        }


                    }
                }
                break;
        }

    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        setPtrSuccessComplete();
        switch (method){
            case POST_WEALTH_LIFE_LIST_JSON:
                if(isAdded()&&refreshData){
                    if (obj instanceof ResponseErrorMessage){
                        ResponseErrorMessage errorMessage = (ResponseErrorMessage)obj;
                        String statusCode = String.valueOf(errorMessage.getCode());
                        if (AppApi.ERROR_TIMEOUT.equals(statusCode)){
                            showRefreshHintAnimation("数据加载超时");
                        }else if (AppApi.ERROR_NETWORK_FAILED.equals(statusCode)){
                            showRefreshHintAnimation("无法连接到网络,请检查网络设置");
                        }
                    }

                }
                int count = wealthLifeAdapter.getCount();
                switch (currentRefreshState) {
                    case REFRESH_TYPE_BOTTOM:
                        pullToRefreshListView.onLoadCompleteNetworkUnavailable(true);
                        break;
                    default:
                        if(count == 0) {
                            mProgressLayout.loadFailure();
                        }else {
                            pullToRefreshListView.onLoadComplete(true);
                            mProgressLayout.loadSuccess();
                        }
                        break;
                }

                break;
        }
    }
    private void setPtrSuccessComplete() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(ptrRunnable, 1000);
    }

//    private void setPtrFailedComplete() {
//        if (handler == null) {
//            handler = new Handler();
//        }
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                pullToRefreshListView.onLoadCompleteNetworkUnavailable(true);
//            }
//        }, 1000);
//    }

    private Runnable ptrRunnable = new Runnable() {

        @Override
        public void run() {
            pullToRefreshListView.onRefreshComplete();
        }
    };
    /**
     * 处理返回的创富数据
     */
    private void handleWealthData(List<CommonListItem> list){
        if(isAdded()&&refreshData){
            showRefreshHintAnimation("更新成功");
        }

        if (list!=null&&list.size()>0){
            if (currentRefreshState == REFRESH_TYPE_TOP){
                listItems.clear();
            }
            listItems.addAll(list);
            if (wealthLifeAdapter!=null){
                wealthLifeAdapter.setData(listItems);
            }
            int haveNext = 0;
            haveNext =  listResult.getNextpage();
            pullToRefreshListView.onLoadComplete(haveNext==1);
        }
    }

    @Override
    public void loadDataEmpty() {

    }

    @Override
    public void loadFailureNoNet() {
        getDataFromServer("0");
    }

    @Override
    public void loadFailure() {
        getDataFromServer("0");
    }

    @Override
    public void bottomOnClick() {
        pullToRefreshListView.onLoadComplete(true);
        bottomRefresh();
    }

    private void writeAppLogPv() {
        AliLogBean bean = new AliLogBean();
        bean.setUUID(UUID);
        int hotelid = mSession.getHotelid();
        int roomid = mSession.getRoomid();
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
        SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
        if(smallPlatInfoBySSDP!=null&&smallPlatInfoBySSDP.getHotelId()>0) {
            hotelid = smallPlatInfoBySSDP.getHotelId();
        }else if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getHotelId())) {
            try {
                hotelid = Integer.valueOf(tvBoxSSDPInfo.getHotelId());
            }catch (Exception e){}
        }else if(smallPlatformByGetIp!=null&&!TextUtils.isEmpty(smallPlatformByGetIp.getHotelId())) {
            try {
                hotelid = Integer.valueOf(smallPlatformByGetIp.getHotelId());
            }catch (Exception e){}
        }
        bean.setHotel_id(hotelid>0?String.valueOf(hotelid):"");
        bean.setRoom_id(roomid>0?String.valueOf(roomid):"");
        bean.setTime(System.currentTimeMillis()+"");
        bean.setAction("show");
        bean.setType("page");
        bean.setContent_id("");
        bean.setCategory_id(category_id+"");
        bean.setMobile_id(STIDUtil.getDeviceId(context));
        bean.setMedia_id("");
        bean.setOs_type("andriod");
        bean.setCustom_volume("category");

        String logFilePath = SavorApplication.getInstance().getLogFilePath();
        AliLogFileUtil.getInstance().writeLogToFile(getActivity(),bean,logFilePath);
    }
    private void writeAppLog(CommonListItem crrentItemVo,String action) {
        AliLogBean bean = new AliLogBean();
        bean.setUUID(UUID);
        int hotelid = mSession.getHotelid();
        int roomid = mSession.getRoomid();
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
        SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
        if(smallPlatInfoBySSDP!=null&&smallPlatInfoBySSDP.getHotelId()>0) {
            hotelid = smallPlatInfoBySSDP.getHotelId();
        }else if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getHotelId())) {
            try {
                hotelid = Integer.valueOf(tvBoxSSDPInfo.getHotelId());
            }catch (Exception e){}
        }else if(smallPlatformByGetIp!=null&&!TextUtils.isEmpty(smallPlatformByGetIp.getHotelId())) {
            try {
                hotelid = Integer.valueOf(smallPlatformByGetIp.getHotelId());
            }catch (Exception e){}
        }
        bean.setHotel_id(hotelid>0?String.valueOf(hotelid):"");
        bean.setRoom_id(roomid>0?String.valueOf(roomid):"");
        bean.setTime(System.currentTimeMillis()+"");
        bean.setAction(action);
        bean.setType("content");
        bean.setContent_id(crrentItemVo.getArtid()+"");
        bean.setCategory_id(category_id+"");
        bean.setMobile_id(STIDUtil.getDeviceId(context));
        bean.setMedia_id(crrentItemVo.getMediaId());
        bean.setOs_type("andriod");
        switch (crrentItemVo.getType()){
            case 0://文本
                bean.setCustom_volume("text");
                break;
            case 1://图文
                bean.setCustom_volume("pictext");
                break;
            case 2://图集
                bean.setCustom_volume("pic");
                break;
            case 3://视频
                bean.setCustom_volume( "video");
            case 4://视频
                bean.setCustom_volume( "video");
                break;
        }
        //bean.setCustom_volume("pictext");

        String logFilePath = SavorApplication.getInstance().getLogFilePath();
        AliLogFileUtil.getInstance().writeLogToFile(getActivity(),bean,logFilePath);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (listItems != null && listItems.size() >0 &&  listItems.size()>=firstVisibleItem) {
            CommonListItem ItemVo = (CommonListItem) listItems.get(firstVisibleItem);
            UUID = System.currentTimeMillis()+"";
            writeAppLog(ItemVo,"show");
//
        }
    }
}
