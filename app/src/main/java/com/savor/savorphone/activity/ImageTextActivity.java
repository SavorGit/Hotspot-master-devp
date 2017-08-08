package com.savor.savorphone.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.api.utils.AppUtils;
import com.common.api.utils.DateUtil;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.adapter.CollectListAdapter;
import com.savor.savorphone.adapter.RecommendListAdapter;
import com.savor.savorphone.bean.AliLogBean;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.CommonListResult;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.interfaces.CopyCallBack;
import com.savor.savorphone.log.AliLogFileUtil;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.STIDUtil;
import com.savor.savorphone.utils.SavorAnimUtil;
import com.savor.savorphone.utils.ScreenOrientationUtil;
import com.savor.savorphone.utils.ShareManager;
import com.savor.savorphone.widget.CustomWebView;
import com.savor.savorphone.widget.MyWebView;
import com.savor.savorphone.widget.ProgressBarView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 图文详情页
 * Created by bushlee on 2017/7/7.
 */

public class ImageTextActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        ProgressBarView.ProgressBarViewClickListener,CopyCallBack {

    private LayoutInflater mInflater;
    private Context context;
    private CustomWebView mCustomWebView;
    //private String webUrl = ConstantValues.H5_BASE_URL;
    private String webUrl = "http://www.sina.com";
    private String artid;
    private int collected;
    private String  title;
    private String  contentURL;
    private String  imageURL;
    private ImageView back;
    private CommonListItem item;
    private ImageView toleft_iv_right;
    private ImageView share;
    private ListView recommend_listview;
    private RecommendListAdapter recommendListAdapter=null;
    private List<CommonListItem> list = new ArrayList<>();
    private ImageView shareWeixinIV;
    private ImageView shareFriendsIV;
    private ImageView shareQqIV;
    private ImageView shareWeiboIV;
    private LinearLayout recommend_layout;
    private ShareManager mShareManager;
    private ShareManager.CustomShareListener mShareListener;
    private String shareUrl;
    private ProgressBarView mProgressLayout;
    private ScrollView info;
    public ProgressBarView allProgressLayuot;
    private String UUID;
    private long mStartTime;
    private boolean isnew = true;
    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_text);
        context = this;
        getIntentData();
        getViews();
        setViews();
        setListeners();
        contentIsOnline();

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        getIntentData();
        //setWeb();
        info.fullScroll(ScrollView.FOCUS_UP);
        contentIsOnline();


    }

    private void contentIsOnline(){
        allProgressLayuot.startLoading();
        AppApi.getContentIsOnline(this,this,item.getArtid());
    }
    private void getIntentData(){
        Intent intent = getIntent();
        if (intent != null) {
            item = (CommonListItem) getIntent().getSerializableExtra("item");
            if (item != null) {
                artid = item.getArtid();
                title = item.getTitle();
                contentURL = item.getContentURL();
                webUrl = ConstantValues.addH5Params(item.getContentURL());
                imageURL = item.getImageURL();
                collected = item.getCollected();
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.details_page),"article");
                RecordUtils.onEvent(ImageTextActivity.this,getString(R.string.details_page),hashMap);

            }

        }


    }

    private void getDataFromServer(){
        if (item!=null){
            AppApi.isCollection(mContext,this,item.getArtid());
        }
    }
    private void getData(){
       AppApi.getRecommendInfo(this,artid,this);
        //AppApi.getRecommendInfo(this,"2702",this);
    }
    @Override
    public void getViews() {
        mInflater = LayoutInflater.from(context);
        mCustomWebView = (CustomWebView) findViewById(R.id.webview_custom);
        back = (ImageView) findViewById(R.id.back);
        toleft_iv_right = (ImageView) findViewById(R.id.toleft_iv_right);
        recommend_listview = (ListView) findViewById(R.id.recommend_listview);
        shareWeixinIV = (ImageView) findViewById(R.id.share_weixin);
        shareFriendsIV = (ImageView) findViewById(R.id.share_friends);
        shareQqIV = (ImageView) findViewById(R.id.share_qq);
        shareWeiboIV = (ImageView) findViewById(R.id.share_weibo);
        recommend_layout = (LinearLayout)findViewById(R.id.recommend_layout);
        share = (ImageView) findViewById(R.id.share);
        mProgressLayout = (ProgressBarView) findViewById(R.id.pbv_loading);
        info = (ScrollView) findViewById(R.id.info);
        mProgressLayout.startLoading();
        allProgressLayuot = (ProgressBarView) findViewById(R.id.all_pbv_loading);

    }

    @Override
    public void setViews() {
        recommend_layout.setVisibility(View.GONE);
        mShareManager = ShareManager.getInstance();
        mShareListener = new ShareManager.CustomShareListener(ImageTextActivity.this);
        share.setVisibility(View.GONE);
        toleft_iv_right.setVisibility(View.GONE);
        mCustomWebView.setOnScrollBottomListener(new MyWebView.OnScrollBottomListener() {
            @Override
            public void onScrollBottom() {
                writeAppLog("complete");
            }
        });
       // recommend_layout.setVisibility(View.GONE);
       // setWeb();


    }

    private void setWeb(){
        mProgressLayout.startLoading();
        if (!TextUtils.isEmpty(webUrl)) {
            mCustomWebView.clearHistory();
            //mProgressLayout.startLoading();
            if (mCustomWebView != null) {
                mCustomWebView.onScrollBottom();
            }
            isnew = mCustomWebView.canGoBack();
            if (isnew) {
                webUrl =  mCustomWebView.getUrl();
            }else {
                webUrl = ConstantValues.addH5Params(item.getContentURL());
            }
            mCustomWebView.loadUrl(webUrl, null, new VideoPlayVODNotHotelActivity.UpdateProgressListener() {
                @Override
                public void loadFinish() {
                   // mProgressLayout.loadSuccess();
                    mProgressLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressLayout.loadSuccess();
                            num++;
                         //   isnew = mCustomWebView.canGoBack();
                        }
                    },700);
                    getData();
                    share.setVisibility(View.VISIBLE);
                    toleft_iv_right.setVisibility(View.VISIBLE);

                }

                @Override
                public void loadHttpError() {
                    mProgressLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressLayout.loadFailure();
                            share.setVisibility(View.GONE);
                            toleft_iv_right.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }
    }
    private void share(SHARE_MEDIA platform){
        mShareManager.setShortcutShare();
        UMWeb umWeb = new UMWeb(ConstantValues.addH5ShareParams(item.getContentURL()));
        umWeb.setThumb(new UMImage(ImageTextActivity.this,R.drawable.ic_launcher));
        umWeb.setTitle(title);
        umWeb.setDescription("投屏神器，进入饭局的才是热点");
        new ShareAction(ImageTextActivity.this)
                .withText("投屏神器，进入饭局的才是热点"+ConstantValues.addH5ShareParams(item.getContentURL()))
                .withMedia(umWeb)
                .setPlatform(platform)
                .setCallback(mShareListener)
                .share();

    }
    private void share() {
        if (!AppUtils.isNetworkAvailable(this)) {
            ShowMessage.showToastSavor(this, getString(R.string.bad_wifi));
            return;
        }
        //暂停，记录播放位置

        String stitle = "小热点| "+ title;
        String text = "小热点| "+title;
       // ShareManager shareManager = ShareManager.getInstance();
        mShareManager.setCategory_id("1");
        mShareManager.setContent_id(artid);
        mShareManager.share(this,text,stitle,imageURL,ConstantValues.addH5ShareParams(item.getContentURL()),this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setListeners() {
        toleft_iv_right.setOnClickListener(this);
        back.setOnClickListener(this);
        shareWeixinIV.setOnClickListener(this);
        shareFriendsIV.setOnClickListener(this);
        shareQqIV.setOnClickListener(this);
        shareWeiboIV.setOnClickListener(this);
        share.setOnClickListener(this);
        recommend_listview.setOnItemClickListener(recommendItemClickListener);
        mProgressLayout.setProgressBarViewClickListener(this);
        allProgressLayuot.setProgressBarViewClickListener(this);
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_CONTENT_IS_ONLINE_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
                    if ("success".equals(str)){
                        allProgressLayuot.loadSuccess();
                        //ScreenOrientationUtil.getInstance().start(this);
                        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                        share.setVisibility(View.VISIBLE);
                        toleft_iv_right.setVisibility(View.VISIBLE);
                        getDataFromServer();
                        setWeb();
                    }
                }
                break;
            case POST_RECOMMEND_LIST_JSON:
                if (obj instanceof List<?>){
                    List<CommonListItem> mList = (List<CommonListItem>) obj;
                    setRecommendData(mList);
                }

                break;
            case GET_ADD_MY_COLLECTION_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
                    if ("success".equals(str)){
                        switch (collected){
                            case 0:
                                collected = 1;
                                toleft_iv_right.setBackgroundResource(R.drawable.yishoucang3x);
                                ShowMessage.showToast(ImageTextActivity.this,"收藏成功");
                                break;
                            case 1:
                                collected = 0;
                                toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
                                ShowMessage.showToast(ImageTextActivity.this,"取消收藏");
                                break;
                        }
                    }
                    toleft_iv_right.setOnClickListener(ImageTextActivity.this);
                }
                break;
            case GET_IS_COLLECTION_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
                    if ("1".equals(str)){
                        collected = 1;
                        toleft_iv_right.setBackgroundResource(R.drawable.yishoucang3x);
                    }else{
                        collected = 0;
                        toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
                    }
                }
                break;


        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {

        switch (method) {
            case POST_RECOMMEND_LIST_JSON:
                recommend_layout.setVisibility(View.GONE);

                break;
            case POST_CONTENT_IS_ONLINE_JSON:
                if (obj instanceof ResponseErrorMessage){
                    ResponseErrorMessage errorMessage = (ResponseErrorMessage)obj;
                    if (errorMessage.getCode()==19002){
                        allProgressLayuot.loadFailure("该内容找不到了~","",R.drawable.kong_wenzhang);
                        share.setVisibility(View.GONE);
                        toleft_iv_right.setVisibility(View.GONE);
                    }else{
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                        ScreenOrientationUtil.getInstance().stop();
                        share.setVisibility(View.GONE);
                        toleft_iv_right.setVisibility(View.GONE);
                        allProgressLayuot.loadFailure();
                    }
                }
                break;
        }
        if(obj instanceof ResponseErrorMessage) {
            ResponseErrorMessage message = (ResponseErrorMessage) obj;
            int code = message.getCode();
            String msg = message.getMessage();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && num>1&&mCustomWebView.canGoBack()) {
            mCustomWebView.goBack();// 返回前一个页面
            return true;
        }else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back :
               // finish();
                if (num>1&& mCustomWebView.canGoBack()) {
                    mCustomWebView.goBack();
                }else {
                    onBackPressed();
                }
                break;
            case R.id.toleft_iv_right:
                toleft_iv_right.setOnClickListener(null);
                handleCollection();
                break;
            case R.id.share_weixin:
                share(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.share_friends:
                share(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.share_qq:
                share(SHARE_MEDIA.QQ);
                break;
            case R.id.share_weibo:
                share(SHARE_MEDIA.SINA);
                break;
            case R.id.share:
                share();
                break;

        }
    }

    private void handleCollection(){
        switch (collected){
            case 0:
                AppApi.handleCollection(mContext,this,artid,"1");
                break;
            case 1:
                AppApi.handleCollection(mContext,this,artid,"0");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void setRecommendData(List<CommonListItem> mList){
        if (mList != null && mList.size() > 0) {
            recommend_layout.setVisibility(View.VISIBLE);
            recommendListAdapter = new RecommendListAdapter(context,mList);
            recommend_listview.setAdapter(recommendListAdapter);
        }else {
            recommend_layout.setVisibility(View.GONE);
        }

    }

    AdapterView.OnItemClickListener recommendItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CommonListItem item = (CommonListItem) parent.getItemAtPosition(position);
            Intent intent = new Intent(mContext,ImageTextActivity.class);
            intent.putExtra("item",item);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            num = 0;
          //  mCustomWebView.clearHistory();
          //  mCustomWebView.destroy();
        //    finish();
        }
    };


    @Override
    public void loadDataEmpty() {

    }

    @Override
    public void loadFailureNoNet() {

    }

    @Override
    public void loadFailure() {
        setViews();
        getDataFromServer();
        setWeb();
    }

    @Override
    public void copyLink() {
        ClipboardManager cmb = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(item.getContentURL());
        ShowMessage.showToast(mContext,"复制完毕");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mShareManager != null) {
            mShareManager.CloseDialog ();
        }
        mStartTime = System.currentTimeMillis();
       // mCustomWebView.resumeTimers();
        UUID = mStartTime +"";
        writeAppLog("start");
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this,this);
        writeAppLog("end");
//        MobclickAgent.onEventValue(this, UmengContact.PLAY_CAST, hashMap, duration);
//        MobclickAgent.onPause(this);
//        mHandler.removeMessages(QUERY_SEEK);
//        if(projectionService!=null)
//            projectionService.stopQuerySeek();
    }
    private void writeAppLog(String action) {
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
        bean.setContent_id(item.getArtid()+"");
        bean.setCategory_id(item.getCategoryId());
        bean.setMobile_id(STIDUtil.getDeviceId(this));
        bean.setMedia_id(item.getMediaId());
        bean.setOs_type("andriod");
        bean.setCustom_volume("pictext");
        String logFilePath = SavorApplication.getInstance().getLogFilePath();
        AliLogFileUtil.getInstance().writeLogToFile(this,bean,logFilePath);
    }
}
