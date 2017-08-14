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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.adapter.PicRecommendAdapter;
import com.savor.savorphone.bean.AliLogBean;
import com.savor.savorphone.bean.CommonListItem;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PicRecommendActivity extends BaseActivity implements View.OnClickListener,
        ApiRequestListener,
        AdapterView.OnItemClickListener{

    private Context context;
    private TextView tv_center;
    private ImageView iv_left;
    private ImageView iv_right;
    private ImageView code;
    private ShareManager mShareManager;
    private ShareManager.CustomShareListener mShareListener;
    public List<CommonListItem> rList = new ArrayList<CommonListItem>();
    private CommonListItem voditem;
    private PicRecommendAdapter adapter;
    private GridView gview;



    public static final int EXTRA_FROM_RECOMMEND = 0x112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_recommend);
        context = this;
        getIntentData();
        getViews();
        setViews();
        setListeners();

    }

    private void getIntentData(){
        rList = (List<CommonListItem>) getIntent().getSerializableExtra("rList");
        voditem = (CommonListItem) getIntent().getSerializableExtra("voditem");
    }


    @Override
    public void getViews() {
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
    public void setListeners() {
        //iv_left.setOnClickListener(this);
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

            default:
        }
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

    }
}
