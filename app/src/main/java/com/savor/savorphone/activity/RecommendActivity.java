package com.savor.savorphone.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.bean.AliLogBean;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.log.AliLogFileUtil;
import com.savor.savorphone.utils.BarcodeUtil;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.STIDUtil;
import com.savor.savorphone.utils.ShareManager;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.HashMap;

public class RecommendActivity extends BaseActivity implements View.OnClickListener,ApiRequestListener {

    private Context context;
    private TextView tv_center;
    private ImageView iv_left;
    private ImageView iv_right;
    private ImageView code;
    private RelativeLayout we_chat_la;
    private RelativeLayout friends_la;
    private RelativeLayout qq_la;
    private RelativeLayout weibo_la;
    private RelativeLayout copy_link_la;
    private ShareManager mShareManager;
    private ShareManager.CustomShareListener mShareListener;
    private String shareUrl;
    private String qrcodeUrl;
    private String UUID;
    private int type = 0;
    private String title;

    public static final int EXTRA_FROM_RECOMMEND = 0x112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        RecordUtils.onEvent(RecommendActivity.this,getString(R.string.menu_recommend_page));
        context = this;
        getType();
        getViews();
        setViews();
        setListeners();
        setCode();
        UUID = System.currentTimeMillis()+"";
        //BarcodeUtil.createQRImage("",200,200,null,"");
    }


    private void getType(){

        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getIntExtra("flag",0);
            if(type == 6){
                title = "快点儿来参加抽奖活动哦～";
            }else {
                title = "我觉得“小热点”很好用，推荐给您～";
            }
        }

    }

    @Override
    public void getViews() {
        tv_center = (TextView) findViewById(R.id.tv_center);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        we_chat_la = (RelativeLayout)findViewById(R.id.we_chat_la);
        friends_la = (RelativeLayout)findViewById(R.id.friends_la);
        qq_la = (RelativeLayout)findViewById(R.id.qq_la);
        weibo_la = (RelativeLayout)findViewById(R.id.weibo_la);
        copy_link_la = (RelativeLayout)findViewById(R.id.copy_link_la);
        code = (ImageView) findViewById(R.id.code);

    }

    @Override
    public void setViews() {
        tv_center.setText("推荐");
        mShareManager = ShareManager.getInstance();
        mShareListener = new ShareManager.CustomShareListener(RecommendActivity.this);
    }

    @Override
    public void setListeners() {
        iv_left.setOnClickListener(this);
        we_chat_la.setOnClickListener(this);
        friends_la.setOnClickListener(this);
        qq_la.setOnClickListener(this);
        weibo_la.setOnClickListener(this);
        copy_link_la.setOnClickListener(this);
    }

    private void setCode(){
        Resources res=getResources();

        Bitmap bmp= BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
        qrcodeUrl = ConstantValues.HOST_URL+"d?st=qrcode&clientname=android&deviceid="+ STIDUtil.getDeviceId(context);
        shareUrl = ConstantValues.HOST_URL+"d?st=usershare&clientname=android&deviceid="+ STIDUtil.getDeviceId(context);
        BarcodeUtil.createCodeImage(qrcodeUrl,850,850,bmp,code);
    }


    @Override
    public void onBackPressed() {
        setResult(EXTRA_FROM_RECOMMEND);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                RecordUtils.onEvent(this,getString(R.string.menu_feedback_back));
                onBackPressed();
                break;
            case R.id.we_chat_la:
              share(SHARE_MEDIA.WEIXIN);
              writeAppLogPv("weixin");
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.menu_recommend_share_weixin),"success");
                RecordUtils.onEvent(RecommendActivity.this,"menu_recommend_share_weixin",hashMap);
                break;
            case R.id.friends_la:
                share(SHARE_MEDIA.WEIXIN_CIRCLE);
                writeAppLogPv("weixin_circle");
                HashMap<String,String> hashMap1 = new HashMap<>();
                hashMap1.put(getString(R.string.menu_recommend_share_weixin_friends),"success");
                RecordUtils.onEvent(RecommendActivity.this,"menu_recommend_share_weixin_friends",hashMap1);
                break;
            case R.id.qq_la:
                share(SHARE_MEDIA.QQ);
                writeAppLogPv("qq");
                HashMap<String,String> hashMap2 = new HashMap<>();
                hashMap2.put(getString(R.string.menu_recommend_share_qq),"success");
                RecordUtils.onEvent(RecommendActivity.this,"menu_recommend_share_qq",hashMap2);
                break;
            case R.id.weibo_la:
                share(SHARE_MEDIA.SINA);
                writeAppLogPv("weibo");
                HashMap<String,String> hashMap3 = new HashMap<>();
                hashMap3.put(getString(R.string.menu_recommend_sina),"success");
                RecordUtils.onEvent(RecommendActivity.this,"menu_recommend_sina",hashMap3);
                UMImage umImage = new UMImage(RecommendActivity.this,R.drawable.ic_launcher);
                new ShareAction(RecommendActivity.this)
                        .withText("投屏神器，进入饭局的才是热点"+shareUrl)
                        .withMedia(umImage)
                        .setPlatform(SHARE_MEDIA.SINA)
                        .setCallback(mShareListener)
                        .share();
                break;
            case R.id.copy_link_la:
                RecordUtils.onEvent(RecommendActivity.this,getString(R.string.menu_recommend_copy_link));
                ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(shareUrl);
                ShowMessage.showToast(context,"复制完毕");
                break;

            default:
        }
    }


    private void share(SHARE_MEDIA platform){
        UMWeb umWeb = new UMWeb(shareUrl);
        umWeb.setThumb(new UMImage(RecommendActivity.this,R.drawable.ic_launcher));
        umWeb.setTitle(title);
        umWeb.setDescription("投屏神器，进入饭局的才是热点");
        new ShareAction(RecommendActivity.this)
                .withText("投屏神器，进入饭局的才是热点"+shareUrl)
                .withMedia(umWeb)
                .setPlatform(platform)
                .setCallback(mShareListener)
                .share();

    }

    private void writeAppLogPv(String custom_volume) {
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
        bean.setAction("share");
        bean.setType("content");
        bean.setContent_id("");
        bean.setCategory_id("");
        bean.setMobile_id(STIDUtil.getDeviceId(context));
        bean.setMedia_id("");
        bean.setOs_type("andriod");
        bean.setCustom_volume(custom_volume);

        String logFilePath = SavorApplication.getInstance().getLogFilePath();
        AliLogFileUtil.getInstance().writeLogToFile(this,bean,logFilePath);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // querySeek();
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }
}
