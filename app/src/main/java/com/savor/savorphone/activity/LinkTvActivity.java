package com.savor.savorphone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.TvBoxInfo;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.receiver.NetworkConnectChangedReceiver;
import com.savor.savorphone.service.SSDPService;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.WifiUtil;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.LinkDialog;

import java.util.HashMap;

public class LinkTvActivity extends BaseActivity implements View.OnClickListener{

    public static final String EXRA_TV_BOX = "exra_tv_box";

    private Context context;

    private RelativeLayout relink_la;
    private TextView link_status;
    private TextView relink;
    public static final int EXTRA_TV_INFO = 0x111;
    public static final int EXTRA_BIND_CANCLE = 0x112;
    /**校验三位数字接口错误数*/
    private int verifyCodeErrorCount;
    private String erroMsg;
    private int errorMax = 4;
    /**是否已经校验成功*/
    private boolean isVerify;
    private TextView t1, t2, t3;
    private TextView num_one;
    private TextView num_two;
    private TextView num_three;
    private TextView num_four;
    private TextView num_five;
    private TextView num_six;
    private TextView num_seven;
    private TextView num_eight;
    private TextView num_zero;
    private TextView num_nine;
    private ImageButton num_del;
    private TextView wifi;
    private RelativeLayout finish;
    private ImageView iv_1;
    private ImageView iv_2;
    private ImageView iv_3;
    private ImageView mMaskView;
    private LinkDialog mLinkDialog;
    private NetworkConnectChangedReceiver mChangedReceiver;
    private boolean isWifi = true;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WifiManager.WIFI_STATE_ENABLED:
                    LogUtils.d("savor:网络变为可用");
                    // 为了解决多次重复发送请求利用延时发送方式
                    getSmallPlatformUrl();
                    isWifi = true;
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    wifi.setVisibility(View.INVISIBLE);
                    isWifi = false;
                    break;

            }
        }
    };
    private CommonDialog mChangeWifiDiallog;
    /**是否有三位数字校验时请求成功但ssid为空情况*/
    private boolean isHasSSidEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_tv);
        context = this;
        getViews();
        setViews();
        setListeners();
        registerNetWorkReceiver();
        initPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordUtils.onPageStart(this, getString(R.string.link_tv_enter));

    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandler!=null) {
            mHandler.removeMessages(WifiManager.WIFI_STATE_ENABLED);
            mHandler.removeCallbacksAndMessages(null);
        }
        mContext.unregisterReceiver(mChangedReceiver);
    }

    @Override
    public void getViews() {
        mMaskView = (ImageView) findViewById(R.id.iv_mask);
        link_status = (TextView) findViewById(R.id.link_status);
        relink = (TextView) findViewById(R.id.relink);
        relink_la = (RelativeLayout) findViewById(R.id.relink_la);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        num_one = (TextView) findViewById(R.id.num_one);
        num_two = (TextView) findViewById(R.id.num_two);
        num_three = (TextView) findViewById(R.id.num_three);
        num_four = (TextView) findViewById(R.id.num_four);
        num_five = (TextView) findViewById(R.id.num_five);
        num_six = (TextView) findViewById(R.id.num_six);
        num_seven = (TextView) findViewById(R.id.num_seven);
        num_eight = (TextView) findViewById(R.id.num_eight);
        num_zero = (TextView) findViewById(R.id.num_zero);
        num_nine = (TextView) findViewById(R.id.num_nine);
        num_del = (ImageButton) findViewById(R.id.num_del);
        wifi = (TextView) findViewById(R.id.wifi);
        finish = (RelativeLayout) findViewById(R.id.finish);

        iv_1 = (ImageView) findViewById(R.id.iv_1);
        iv_2 = (ImageView) findViewById(R.id.iv_2);
        iv_3 = (ImageView) findViewById(R.id.iv_3);
    }

    @Override
    public void setViews() {
        String wifiName = WifiUtil.getWifiName(context);
        if (TextUtils.isEmpty(wifiName)) {
            wifi.setVisibility(View.INVISIBLE);
        }else {
            wifi.setVisibility(View.VISIBLE);
            wifi.setText("当前WIFI："+wifiName);
        }

    }

    @Override
    public void setListeners() {

        relink.setOnClickListener(this);
        relink_la.setOnClickListener(this);
        num_one.setOnClickListener(this);
        num_two.setOnClickListener(this);
        num_three.setOnClickListener(this);
        num_four.setOnClickListener(this);
        num_five.setOnClickListener(this);
        num_six.setOnClickListener(this);
        num_seven.setOnClickListener(this);
        num_eight.setOnClickListener(this);
        num_zero.setOnClickListener(this);
        num_nine.setOnClickListener(this);
        num_del.setOnClickListener(this);
        finish.setOnClickListener(this);

    }

    private  void  setKey(String num) {
        Listener();
        String num1 = t1.getText().toString();
        String num2 = t2.getText().toString();
        String num3 = t3.getText().toString();
        if (TextUtils.isEmpty(num1)) {

            t1.setText(num);

        }else {
            if (TextUtils.isEmpty(num2)) {

                t2.setText(num);

            }else if (TextUtils.isEmpty(num3)) {

                t3.setText(num);
                link();
            }

        }
        Listener();
    }

    private  void  delKey() {
        Listener();
        String num1 = t1.getText().toString();
        String num2 = t2.getText().toString();
        String num3 = t3.getText().toString();
        if (!TextUtils.isEmpty(num3)) {
            t3.setText("");
        }else {
            if (!TextUtils.isEmpty(num2)) {
                t2.setText("");
            }else if (!TextUtils.isEmpty(num1)) {
                t1.setText("");
            }

        }
        Listener();
        link_status.setText("请输入电视中的三位数连接电视");

    }

    private void Listener(){
        String num1 = t1.getText().toString();
        String num2 = t2.getText().toString();
        String num3 = t3.getText().toString();
        if (TextUtils.isEmpty(num1)) {
            setKeyBg(1);
        }else if (TextUtils.isEmpty(num2)) {
            setKeyBg(2);
        }else if (TextUtils.isEmpty(num3)) {
            setKeyBg(3);
        }
    }

    private void setKeyBg(int pos){
        switch (pos){
            case 1:
                iv_1.setVisibility(View.VISIBLE);
                iv_2.setVisibility(View.GONE);
                iv_3.setVisibility(View.GONE);
                break;
            case 2:
                iv_1.setVisibility(View.GONE);
                iv_2.setVisibility(View.VISIBLE);
                iv_3.setVisibility(View.GONE);
                break;
            case 3:
                iv_1.setVisibility(View.GONE);
                iv_2.setVisibility(View.GONE);
                iv_3.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        link_status.setVisibility(View.GONE);
        relink_la.setVisibility(View.GONE);
        switch (method) {
            case GET_VERIFY_CODE_BY_BOXIP_JSON:
            case POST_BOX_INFO_JSON:
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put(getString(R.string.link_tv_input_num),"success");
                RecordUtils.onEvent(this,getString(R.string.link_tv_input_num),hashMap);

                handleCodeVerify(obj);
                break;
            case GET_CALL_CODE_BY_BOXIP_JSON:
                break;
        }
    }

    private void handleCodeVerify(Object obj) {
        if(obj instanceof TvBoxInfo) {
            if(!isVerify) {
                TvBoxInfo info = (TvBoxInfo) obj;
                String ssid = info.getSsid();
                if(TextUtils.isEmpty(ssid)) {
                    isHasSSidEmpty = true;
                    verifyCodeErrorCount++;
                    if(verifyCodeErrorCount==errorMax) {
                        link_status.setVisibility(View.VISIBLE);
                        link_status.setText(erroMsg);
                        relink_la.setVisibility(View.INVISIBLE);
                        showChangeWifiDialog();
                    }
                    return;
                }


                dismissLinkDialog();
                isVerify = true;
                link_status.setVisibility(View.INVISIBLE);
                relink_la.setVisibility(View.INVISIBLE);

                mBindTvPresenter.handleBindCodeResult(info);
            }
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
//        super.onError(method, obj);
        HashMap<String,String> hashMap = new HashMap<String, String>();
        hashMap.put(getString(R.string.link_tv_input_num),"fail");
        RecordUtils.onEvent(this,getString(R.string.link_tv_input_num),hashMap);
        link_status.setVisibility(View.INVISIBLE);
        relink_la.setVisibility(View.INVISIBLE);
        switch (method) {
            case GET_VERIFY_CODE_BY_BOXIP_JSON:

            case POST_BOX_INFO_JSON:

                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage msg = (ResponseErrorMessage) obj;
                    erroMsg = msg.getMessage();
                    dismissLinkDialog();
                    if ("绑定失败".equals(erroMsg)) {
                        relink_la.setVisibility(View.VISIBLE);
                        relink.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
                        link_status.setVisibility(View.INVISIBLE);

                    }else {
                        link_status.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(erroMsg)) {
                            erroMsg = "绑定失败";
                            relink_la.setVisibility(View.VISIBLE);
                            relink.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
                            link_status.setVisibility(View.INVISIBLE);
                        }else {
                            link_status.setVisibility(View.VISIBLE);
                            link_status.setText(erroMsg);
                            relink_la.setVisibility(View.INVISIBLE);

                        }
                       // ShowMessage.showToast(LinkTvActivity.this,erroMsg);

                    }

                }else {
                    erroMsg = "绑定失败";
                    link_status.setVisibility(View.INVISIBLE);
                    link_status.setText("");
                    relink_la.setVisibility(View.VISIBLE);
                    relink.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );

                }
                verifyCodeErrorCount++;
                if(verifyCodeErrorCount==errorMax) {
                    if(isHasSSidEmpty) {
                        showChangeWifiDialog();
                    }else {
                        if ("绑定失败".equals(erroMsg)) {
                            relink_la.setVisibility(View.VISIBLE);
                            link_status.setVisibility(View.INVISIBLE);
                            relink.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
                        }else {
                            link_status.setVisibility(View.VISIBLE);
                            link_status.setText(erroMsg);
                            relink_la.setVisibility(View.INVISIBLE);
                        }
                        ShowMessage.showToast(LinkTvActivity.this,erroMsg);
                    }

                }
                break;
            case GET_CALL_CODE_BY_BOXIP_JSON:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(EXTRA_BIND_CANCLE);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.finish:
                RecordUtils.onEvent(this, getString(R.string.link_tv_back));
//                setResult(EXTRA_TV_INFO);
                finish();
                break;
            case R.id.relink_la:
            case R.id.relink:
                link();
                break;
            case R.id.num_one:
                setKey("1");
                break;
            case R.id.num_two:
                setKey("2");
                break;
            case R.id.num_three:
                setKey("3");
                break;
            case R.id.num_four:
                setKey("4");
                break;
            case R.id.num_five:
                setKey("5");
                break;
            case R.id.num_six:
                setKey("6");
                break;
            case R.id.num_seven:
                setKey("7");
                break;
            case R.id.num_eight:
                setKey("8");
                break;
            case R.id.num_nine:
                setKey("9");
                break;
            case R.id.num_zero:
                setKey("0");
                break;
            case R.id.num_del:
                delKey();
                break;


            default:
                break;
        }
    }


    private void link(){
        isHasSSidEmpty = false;
        isVerify = false;
        String firstNum = t1.getText().toString();
        String seconNum = t2.getText().toString();
        String thirdNum = t3.getText().toString();
        link_status.setVisibility(View.INVISIBLE);
        relink_la.setVisibility(View.INVISIBLE);
        verifyCodeErrorCount = 0;
        if(!TextUtils.isEmpty(thirdNum)
                &&!TextUtils.isEmpty(seconNum)
                &&!TextUtils.isEmpty(thirdNum)) {
            showLinkDialog();
            SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
            TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
            SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
            if(smallPlatformByGetIp==null&&tvBoxSSDPInfo==null&&smallPlatInfoBySSDP==null) {
               // erroMsg = "连接失败，请连接包间wifi";
                dismissLinkDialog();
              //  ShowMessage.showToast(this,erroMsg);
                link_status.setVisibility(View.INVISIBLE);
             //   link_status.setText(erroMsg);
                relink_la.setVisibility(View.VISIBLE);
                relink.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
                return;
            }

            showLinkDialog();
            // 1.通过机顶盒ssdp返回的小平台地址进行校验
            String number = firstNum+seconNum+thirdNum;
            if(smallPlatInfoBySSDP !=null) {
                //1.小平台ssdp已经获取到直接进行呼玛，errorcount 为1
                String serverIp = smallPlatInfoBySSDP.getServerIp();
                String type = smallPlatInfoBySSDP.getType();
                if(!TextUtils.isEmpty(serverIp)
                        &&!TextUtils.isEmpty(type)) {
                    AppApi.verifyNumBySpSSDP(LinkTvActivity.this,number,LinkTvActivity.this);
                }else {
                    verifyCodeErrorCount++;
                }
            }else {
                verifyCodeErrorCount++;
            }

            // 2.通过getip返回的小平台进行校验
            if(smallPlatformByGetIp!=null) {
                String localIp = smallPlatformByGetIp.getLocalIp();
                if(!TextUtils.isEmpty(localIp)) {
                    AppApi.verifyNumByClound(mContext,number,this);
                }else {
                    verifyCodeErrorCount++;
                }
            }

            // 3.通过机顶盒地址进行校验
            if(tvBoxSSDPInfo!=null) {
                String boxIp = tvBoxSSDPInfo.getBoxIp();
                String type = tvBoxSSDPInfo.getType();
                if (!TextUtils.isEmpty(boxIp)
                        &&!TextUtils.isEmpty(type)) {
                    String boxUrl = "http://" + boxIp + ":8080";
                    AppApi.verifyNumByBoxIp(LinkTvActivity.this,boxUrl,number,this);
                }else {
                    verifyCodeErrorCount++;
                }
            }else {
                verifyCodeErrorCount++;
            }
            // 4.通过机顶盒返回的小平台地址i进行校验
            if(tvBoxSSDPInfo!=null) {
                String serverIp = tvBoxSSDPInfo.getServerIp();
                String type = tvBoxSSDPInfo.getType();
                if(!TextUtils.isEmpty(serverIp)
                        &&!TextUtils.isEmpty(type)) {
                    AppApi.verifyNumByBoxSSDP(mContext,number,this);
                }else {
                    verifyCodeErrorCount++;
                }
            }else {
                verifyCodeErrorCount++;
            }
        }else {
            link_status.setVisibility(View.VISIBLE);
            link_status.setText("请输入电视中的三位数连接电视");
            relink_la.setVisibility(View.INVISIBLE);
        }
    }

    private void showLinkDialog() {
        if(mLinkDialog == null)
            mLinkDialog = new LinkDialog(this);
        mLinkDialog.show();
    }

    private void dismissLinkDialog() {
        if(mLinkDialog != null)
            mLinkDialog.dismiss();
    }

    public void registerNetWorkReceiver() {
//        initWifiState();
        if(mChangedReceiver==null)
            mChangedReceiver = new NetworkConnectChangedReceiver(mHandler);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mChangedReceiver, filter);
    }


    public void getSmallPlatformUrl() {
        LogUtils.d("getSmallPlatformUrl........");
        if(!AppUtils.isWifiNetwork(mContext)) {
            wifi.setVisibility(View.INVISIBLE);
            return;
        }else {
            setViews();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
    }

    @Override
    public void initBindcodeResult() {
            Intent intent = new Intent();
            setResult(EXTRA_TV_INFO,intent);
            finish();
    }

    @Override
    public void bindError() {
        super.bindError();
        relink_la.setVisibility(View.VISIBLE);
        relink.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        link_status.setVisibility(View.INVISIBLE);
    }

    /**显示修改wifi设置弹窗
     * 1.不在同一网段
     * 2.二维码解析数据通过&符号分割不是4段
     * 3.二维码扫码结果为空
     * */
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
                dismissLinkDialog();
                isVerify = false;
            }
        },"去设置");
        mChangeWifiDiallog.show();
    }

}
