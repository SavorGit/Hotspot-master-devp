package com.savor.savorphone.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.adapter.PicRecommendAdapter;
import com.savor.savorphone.bean.AliLogBean;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.PictureSetBean;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.interfaces.CopyCallBack;
import com.savor.savorphone.log.AliLogFileUtil;
import com.savor.savorphone.utils.BarcodeUtil;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.STIDUtil;
import com.savor.savorphone.utils.ScreenOrientationUtil;
import com.savor.savorphone.utils.ShareManager;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PicRecommendActivity extends BaseActivity implements View.OnClickListener,
        ApiRequestListener,
        AdapterView.OnItemClickListener,CopyCallBack {

    private Context context;
    private ImageView back;
    private ImageView toleft_iv_right;
    private ImageView share;
    private ShareManager mShareManager;
    private ShareManager.CustomShareListener mShareListener;
    public List<CommonListItem> rList = new ArrayList<CommonListItem>();
    private CommonListItem voditem;
    private PicRecommendAdapter adapter;
    private GridView gview;
    //收藏状态,1:收藏，0:取消收藏
    private String state="0";
    private final int PICK_CITY = 1;
    private GestureDetector mGestureDetector;



    public static final int EXTRA_FROM_RECOMMEND = 0x112;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case PICK_CITY:
                    // 跳转选择城市
                    Intent pickIntent = new Intent(mContext, HotspotMainActivity.class);
                    Intent intent = getIntent();
                    if(intent!=null&&("application/pdf").equals(intent.getType())) {
                        Uri data = getIntent().getData();
                        pickIntent.setDataAndType(data,intent.getType());
                        pickIntent.setData(data);
                    }
                    Intent mIntent = new Intent();
                    PicRecommendActivity.this.setResult(111, mIntent);
                    //finish();
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_in_left);// 这部分代码是切换Activity时的动画，看起来就不会很生硬
                    finish();
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_recommend);
        context = this;
        getIntentData();
        getViews();
        setViews();
        setListeners();
        getDataFromServer();
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                WindowManager windowManager = (WindowManager) getApplicationContext()
                        .getSystemService(Context.WINDOW_SERVICE);

                // 获取屏幕的宽度
                Point size = new Point();
                windowManager.getDefaultDisplay().getSize(size);
                int width = size.x;
                if (e2.getRawX() - e1.getRawX() > width/5) {
                    // System.out.println("水平方向移动距离过大");
                    mHandler.sendEmptyMessage(PICK_CITY);// 进入主页
                    return true;
                }

                if (Math.abs(e1.getRawX() - e2.getRawX()) > Math.abs(e1.getRawY() - e2.getRawY())) {//左右滑动
                    return true;
                }
                if (Math.abs(velocityY) < 230|| Math.abs(velocityX)<230) {
                    // System.out.println("手指移动的太慢了");
                    return true;
                }

                int x1 = (int) e1.getRawX();
                int x2 = (int) e2.getRawX();
                // 手势向下 down
                if ((e2.getRawY() - e1.getRawY()) >350) {


                    return true;
                }
                // 手势向上 up
                if ((e1.getRawY() - e2.getRawY()) > 350) {

                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    private void getIntentData(){
        rList = (List<CommonListItem>) getIntent().getSerializableExtra("rList");
        voditem = (CommonListItem) getIntent().getSerializableExtra("voditem");
    }


    @Override
    public void getViews() {
        back = (ImageView) findViewById(R.id.back);
        toleft_iv_right = (ImageView) findViewById(R.id.toleft_iv_right);
        share = (ImageView) findViewById(R.id.share);
       // tv_center = (TextView) findViewById(R.id.tv_center);
//        iv_left = (ImageView) findViewById(R.id.iv_left);
//        iv_right = (ImageView) findViewById(R.id.iv_right);
//        code = (ImageView) findViewById(R.id.code);
        gview = (GridView) findViewById(R.id.gview);

    }

    @Override
    public void setViews() {
        adapter = new PicRecommendAdapter(context,rList);
        //tv_center.setText("推荐");
        mShareManager = ShareManager.getInstance();
        mShareListener = new ShareManager.CustomShareListener(PicRecommendActivity.this);
        gview.setAdapter(adapter);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        //  Toast.makeText(PictureSetActivity.this, "touch", Toast.LENGTH_SHORT).show();
        return super.dispatchTouchEvent(event);
    }
    @Override
    public void setListeners() {
        toleft_iv_right.setOnClickListener(this);
        back.setOnClickListener(this);
        gview.setOnItemClickListener(this);
        share.setOnClickListener(this);
//        gview.setOnTouchListener(new View.OnTouchListener() {
//            float startX;
//            float endX;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        startX = event.getX();
//                        event.getY();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        endX = event.getX();
//                        event.getY();
//                        WindowManager windowManager = (WindowManager) getApplicationContext()
//                                .getSystemService(Context.WINDOW_SERVICE);
//
//                        // 获取屏幕的宽度
//                        Point size = new Point();
//                        windowManager.getDefaultDisplay().getSize(size);
//                        int width = size.x;
//                        // 首先要确定的是，是否到了最后一页，然后判断是否向左滑动，并且滑动距离是否符合，我这里的判断距离是屏幕宽度的4分之一（这里可以适当控制）
//                        if ( endX - startX >= (width /5)) {
//                            mHandler.sendEmptyMessage(PICK_CITY);// 进入主页
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
    }

    private void getDataFromServer(){

       AppApi.isCollection(mContext,this,voditem.getArtid());
//        AppApi.getPictureSet(mContext,this,content_id);

    }
    @Override
    public void onBackPressed() {
        setResult(EXTRA_FROM_RECOMMEND);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                Intent mIntent = new Intent();
                this.setResult(222, mIntent);
                finish();

                break;
            case R.id.share:
                share();
                break;
            case R.id.toleft_iv_right:
                toleft_iv_right.setOnClickListener(null);
                handleCollection();
                break;

            default:
        }
    }

    private void handleCollection(){

        if ("0".equals(state)) {//已收藏
            AppApi.handleCollection(mContext,this,voditem.getArtid(),"1");
        }else if ("1".equals(state)) {
            AppApi.handleCollection(mContext,this,voditem.getArtid(),"0");
        }

    }
    private void share() {
        if (!AppUtils.isNetworkAvailable(this)) {
            ShowMessage.showToastSavor(this, getString(R.string.bad_wifi));
            return;
        }
        //暂停，记录播放位置

        String stitle = "小热点- "+ voditem.getTitle();
        String text = "小热点- "+voditem.getTitle();
        // ShareManager shareManager = ShareManager.getInstance();
        mShareManager.setCategory_id("1");
        mShareManager.setContent_id(voditem.getArtid());
        mShareManager.share(this,text,stitle,voditem.getImageURL(),ConstantValues.addH5ShareParams(voditem.getContentURL()),this);
    }


//    private void share(SHARE_MEDIA platform){
//        UMWeb umWeb = new UMWeb(shareUrl);
//        umWeb.setThumb(new UMImage(PicRecommendActivity.this,R.drawable.ic_launcher));
//        umWeb.setTitle(title);
//        umWeb.setDescription("投屏神器，进入饭局的才是热点");
//        new ShareAction(PicRecommendActivity.this)
//                .withText("投屏神器，进入饭局的才是热点"+shareUrl)
//                .withMedia(umWeb)
//                .setPlatform(platform)
//                .setCallback(mShareListener)
//                .share();
//
//    }


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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CommonListItem item = (CommonListItem)parent.getItemAtPosition(position);
        if (item!=null){
            int type = Integer.valueOf(item.getType());
            switch (type){
                case 0:
                case 1:
                    item.setCategoryId(item.getCategoryId());
                    Intent intent = new Intent(context, ImageTextActivity.class);
                    intent.putExtra("item",item);
                    startActivity(intent);
                    Intent mIntent1 = new Intent();
                    this.setResult(222, mIntent1);
                    finish();
                    break;
                case 2:
                    intent = new Intent(context, PictureSetActivity.class);
                    intent.putExtra("voditem",item);
                    intent.putExtra("content_id",item.getArtid());
                    intent.putExtra("category_id",item.getCategoryId());
                    startActivity(intent);
                    Intent mIntent = new Intent();
                    this.setResult(222, mIntent);
                    finish();
                    break;
                case 3:
                case 4:
                    intent = new Intent(context, VideoPlayVODNotHotelActivity.class);
                    item.setCategoryId(item.getCategoryId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("voditem",item);
                    startActivity(intent);
                    Intent mIntent2 = new Intent();
                    this.setResult(222, mIntent2);
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method){

            case GET_IS_COLLECTION_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
                    state = str;
                    if ("1".equals(state)){
                        toleft_iv_right.setBackgroundResource(R.drawable.yishoucang3x);
                    }else{
                        toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
                    }
                }
                break;
            case GET_ADD_MY_COLLECTION_JSON:
                if (obj instanceof String){
                    String str = (String)obj;
                    if ("success".equals(str)){
                        if ("0".equals(state)){
                            state = "1";
                            toleft_iv_right.setBackgroundResource(R.drawable.yishoucang3x);
                            ShowMessage.showToast(PicRecommendActivity.this,"收藏成功");
                        }else{
                            state = "0";
                            toleft_iv_right.setBackgroundResource(R.drawable.shoucang3x);
                            ShowMessage.showToast(PicRecommendActivity.this,"取消收藏");
                        }
                    }
                    toleft_iv_right.setOnClickListener(PicRecommendActivity.this);
                }
                break;

        }
    }

    @Override
    public void copyLink() {
        ClipboardManager cmb = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(voditem.getContentURL());
        ShowMessage.showToast(mContext,"复制完毕");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent mIntent = new Intent();
            this.setResult(222, mIntent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
