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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.activity.ImageTextActivity;
import com.savor.savorphone.activity.SubjectActivity;
import com.savor.savorphone.adapter.SubjectAdapter;
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

import java.util.ArrayList;
import java.util.List;



/**
 * 创富生活fragment
 * Created by Administrator on 2017/7/4.
 */

public class SubjectFragment extends BaseFragment implements ApiRequestListener,
        AdapterView.OnItemClickListener,
        ProgressBarView.ProgressBarViewClickListener,
        PullToRefreshListView.NetworkUnavailableOnClick ,
        AbsListView.OnScrollListener{
    private static final String TAG = "SubjectFragment";
    private Context context;
    private PullToRefreshListView pullToRefreshListView;
    /**头部大图**/
    private ImageView headerBigImageIV;
    /**头部标题**/
    private TextView headerTitleTV;
    /**头部来源ICON**/
    private ImageView  headerSourceIconIV;
    /**头部来源名称**/
    private TextView headerNameTV;
    /**头部来源时间**/
    private TextView headerSourceTimeTV;
    private TextView mRefreshDataHinttv;
    private SubjectAdapter subjectAdapter=null;
    private String sort_num = "";
    private CommonListResult listResult;
    private List<CommonListItem> list = new ArrayList<CommonListItem>();
    /**是否是头部刷新*/
    private boolean isUp = true;
    private CommonListItem first;
    private ProgressBarView mProgressLayout;
    private static final int HEAD_REFRESH = 10101;
    private static final int BOTTOM_REFRESH = 10102;
    private boolean isfrist = true;
    private String UUID;
    private long mStartTime;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what){
                case HEAD_REFRESH:
                    sort_num = "";
                    isUp = true;
                    getData();
                    break;
                case BOTTOM_REFRESH:
                    isUp = false;
                    getData();
                    break;
            }
        }
    };
    @Override
    public String getFragmentName() {
        return TAG;
    }
    public static SubjectFragment getInstance() {
        SubjectFragment subjectFragment = new SubjectFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("type",type);
//        wealthLifeFragment.setArguments(bundle);
        return  subjectFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wealth_life,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        initViews(view);
        setViews();
        setListeners();
        UUID = System.currentTimeMillis()+"";
        writeAppLogPv();
        //getData();
    }

    private void initViews(View view){
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.wl_listview);
        subjectAdapter = new SubjectAdapter(context);
        pullToRefreshListView.getRefreshableView().addHeaderView(createHeaderView());
        pullToRefreshListView.setAdapter(subjectAdapter);
        //pullToRefreshListView.setOnItemClickListener(itemClickListener);
        pullToRefreshListView.setOnRefreshListener(onRefreshListener);
        pullToRefreshListView.setOnLastItemVisibleListener(onLastItemVisibleListener);
        mProgressLayout  = (ProgressBarView) view.findViewById(R.id.pbv_loading);
        mProgressLayout.setProgressBarViewClickListener(this);
        mRefreshDataHinttv = (TextView) view.findViewById(R.id.tv_refresh_data_hint);
    }

    @Override
    public void setViews() {
        List<CommonListItem> specialData = SavorCacheUtil.getInstance().getSpecialData(getActivity());
        if(specialData!=null&&specialData.size()>0) {
            CommonListItem item = specialData.get(0);
            setHeaderData(item);
            specialData.remove(0);
            subjectAdapter.setData(specialData);
        }
    }

    private void getData(){
        List<CommonListItem> specialData = SavorCacheUtil.getInstance().getSpecialData(getActivity());
        if(specialData==null||specialData.size()==0) {
            mProgressLayout.startLoading();
        }

        AppApi.getSpecialList(context,sort_num,this);

    }
    private View createHeaderView(){
        View view = View.inflate(getActivity(),R.layout.header_subject,null);
        headerBigImageIV = (ImageView) view.findViewById(R.id.header_big_img);
        headerTitleTV = (TextView) view.findViewById(R.id.header_title);
        headerNameTV = (TextView) view.findViewById(R.id.header_info);
        headerSourceTimeTV = (TextView) view.findViewById(R.id.hesder_source_time);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isfrist = true;
        initDisplay();
    }

    private void initDisplay(){
        sort_num = "";
        isUp = true;
        getData();
    }



    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        setPtrComplete(1000);
        mProgressLayout.loadSuccess();
        switch (method) {
            case POST_SPECIAL_LIST_JSON:
//                if (obj instanceof List<?>){
//                    List<CommonListItem> mList = (List<CommonListItem>) obj;
//                    handleVodList(mList);
//                }
                if(obj instanceof CommonListResult) {
                    listResult= (CommonListResult) obj;
                    if (listResult != null) {
                        List<CommonListItem> mList =  listResult.getList();
                        handleVodList(mList);
                    }
                }

                break;
            case POST_GETVODLIST_JSON:

                break;


        }
    }

    private void setPtrComplete(long delay) {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(ptrRunnable, delay);
    }
    private Runnable ptrRunnable = new Runnable() {

        @Override
        public void run() {
            pullToRefreshListView.onRefreshComplete();
        }
    };

    @Override
    public void onError(AppApi.Action method, Object obj) {
        String msg = "";
        if(obj instanceof ResponseErrorMessage) {
            ResponseErrorMessage message = (ResponseErrorMessage) obj;
            int code = message.getCode();
            msg = message.getMessage();
        }
        switch (method) {
            case POST_SPECIAL_LIST_JSON:
                if(!isUp) {
                    pullToRefreshListView.onLoadCompleteNetworkUnavailable(true);
                }else {
                    if(subjectAdapter.getCount()==0) {
                        mProgressLayout.loadFailure();
                    }else {
                        mProgressLayout.loadSuccess();
                    }
                }
                setPtrComplete(0);
                if(isAdded() ) {
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

                break;
            case POST_GETVODLIST_JSON:

                break;


        }

    }
    private void handleVodList(List<CommonListItem> mList){

        if (mList != null && mList.size() > 0) {
            if (isUp) {
                first = mList.get(0);
                SavorCacheUtil.getInstance().cacheSpecialData(getActivity(),mList);
                setHeaderData(first);
                mList.remove(0);
                list.clear();
                pullToRefreshListView.onLoadComplete(true,false);
                if(isAdded() && isfrist == false) {
                    showRefreshHintAnimation("更新成功");
                }
            }else {
                pullToRefreshListView.onLoadComplete(true,false);
            }
            int haveNext = 0;
            haveNext =  listResult.getNextpage();
            if (haveNext>0) {
                sort_num = mList.get(mList.size()-1).getSort_num()+"";
            }else {
                sort_num = first.getSort_num()+"";
            }

            list.addAll(mList);
            subjectAdapter.setData(list);
            if (haveNext == 0) {
                pullToRefreshListView.onLoadComplete(false,true);
            }else {
                pullToRefreshListView.onLoadComplete(true,false);
            }
        }else {
            pullToRefreshListView.onLoadComplete(false,true);
        }
        isfrist = false;
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
    private void setHeaderData(CommonListItem first){
        Glide.with(context)
                .load(first.getImageURL())
                .placeholder(R.drawable.kong_mrjz)
                .error(R.drawable.kong_mrjz)
                .crossFade()
                .into(headerBigImageIV);
        headerTitleTV.setText(first.getTitle());
        headerNameTV.setText(first.getShareTitle());
        headerSourceTimeTV.setText(first.getUpdateTime());
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        CommonListItem item;
        if (list != null&&list.size()>=position-1) {
            if (position == 1) {
                item = first;
            }else {
                item = list.get(position-2);
            }
            writeAppLog(item,"click");
            Intent intent = new Intent();
            intent.putExtra("type","subject");
            intent.putExtra("item",item);
            intent.setClass(context,SubjectActivity.class);
            //intent.setClass(context,LocalVideoProAcitvity.class);

            startActivity(intent);
        }


    }

    @Override
    public void setListeners() {
        pullToRefreshListView.setNetworkUnavailableOnClick(this);
        pullToRefreshListView.setOnItemClickListener(this);
        pullToRefreshListView.setOnRefreshListener(onRefreshListener);
        pullToRefreshListView.setOnLastItemVisibleListener(onLastItemVisibleListener);
        pullToRefreshListView.setOnScrollListener(this);
        pullToRefreshListView.onLoadComplete(true,true);
    }

    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            Message message = Message.obtain();
            message.what = HEAD_REFRESH;
            message.obj = "0";
            handler.sendMessage(message);
        }
    };

    PullToRefreshBase.OnLastItemVisibleListener onLastItemVisibleListener = new PullToRefreshBase.OnLastItemVisibleListener() {
        @Override
        public void onLastItemVisible() {

            Message message = Message.obtain();
            message.what = BOTTOM_REFRESH;
            handler.sendMessage(message);
        }
    };

    @Override
    public void loadDataEmpty() {

    }

    @Override
    public void loadFailureNoNet() {
        sort_num = "";
        isUp = true;
        getData();
    }

    @Override
    public void loadFailure() {
        sort_num = "";
        isUp = true;
        getData();
    }

    @Override
    public void bottomOnClick() {
        Message message = Message.obtain();
        message.what = BOTTOM_REFRESH;
        handler.sendMessage(message);
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
        bean.setCategory_id("103");
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
        bean.setCategory_id("103");
        bean.setMobile_id(STIDUtil.getDeviceId(context));
        bean.setMedia_id(crrentItemVo.getMediaId());
        bean.setOs_type("andriod");
        bean.setCustom_volume("pictext");

        String logFilePath = SavorApplication.getInstance().getLogFilePath();
        AliLogFileUtil.getInstance().writeLogToFile(getActivity(),bean,logFilePath);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (list != null && list.size() >0 &&  list.size()>=firstVisibleItem) {
            CommonListItem ItemVo = (CommonListItem) list.get(firstVisibleItem);
            UUID = System.currentTimeMillis()+"";
            writeAppLog(ItemVo,"show");
//
       }

    }
}
