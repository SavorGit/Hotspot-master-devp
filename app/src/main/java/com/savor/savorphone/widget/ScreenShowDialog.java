//package com.savor.savorphone.widget;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.support.v4.view.GravityCompat;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.savor.savorphone.R;
//import com.savor.savorphone.activity.IBindTvView;
//import com.savor.savorphone.activity.PhotoActivity;
//import com.savor.savorphone.activity.SlideActivity;
//import com.savor.savorphone.activity.VideoShareActivity;
//import com.savor.savorphone.core.Session;
//import com.savor.savorphone.presenter.BindTvPresenter;
//import com.savor.savorphone.utils.WifiUtil;
//
///**
// * 投屏菜单
// * Created by hezd on 2017/1/19.
// */
//
//public class ScreenShowDialog implements View.OnClickListener {
//    private final View mDependencyView;
//    private final BindTvPresenter mBindPresenter;
//    private final IBindTvView mBindView;
//    private Activity mContext;
//    private PopupWindow mShowToScreenWindow;
//    private final Session mSession;
//    private boolean mBindTv;
//    private boolean mSessionSmallPlatformEnable;
//    private TextView desTv;
//    private TextView mLineTv;
//
//    public ScreenShowDialog(Activity context, View dependencyView,BindTvPresenter presenter,IBindTvView bindTvView) {
//        this.mContext = context;
//        this.mDependencyView = dependencyView;
//        mSession = Session.get(context);
//        this.mBindView = bindTvView;
//        this.mBindPresenter = presenter;
//        init(context);
//    }
//
//    private void init(Context context) {
//        mShowToScreenWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//        View contentView = View.inflate(context, R.layout.layout_to_screen_window,null);
//        RelativeLayout mBlankLayout = (RelativeLayout) contentView.findViewById(R.id.rl_blank_layout);
//        Button videoBtn = (Button) contentView.findViewById(R.id.btn_video);
//        Button imageBtn = (Button) contentView.findViewById(R.id.btn_image);
//        Button slideBtn =  (Button) contentView.findViewById(R.id.btn_slide);
//        Button fileBtn = (Button) contentView.findViewById(R.id.btn_file);
//        desTv = (TextView) contentView.findViewById(R.id.tv_desc);
//        mLineTv = (TextView) contentView.findViewById(R.id.tv_link);
//        desTv = (TextView) contentView.findViewById(R.id.tv_desc);
//        mLineTv = (TextView) contentView.findViewById(R.id.tv_link);
//        mBindTv = mSession.isBindTv();
//        if(mBindTv) {
//            desTv.setText("您已成功连接"+WifiUtil.getWifiName(mContext)+"的电视\n点击下列分类选择文件即可电视投屏");
//            mLineTv.setText("断开连接");
//        }else {
//            if(WifiUtil.checkWifiState(mContext))  {
//                mSessionSmallPlatformEnable = mSession.isSmallPlatformEnable();
//                if(!mSessionSmallPlatformEnable) {
//                    desTv.setText("您当前连接的网段内没发现可连接设备\n如需投屏请确保wifi开启与需要投屏\n的电视保证在同一WiFi环境下");
//                    mLineTv.setText("去设置");
//                }else {
//                    desTv.setText("已发现可连接的电视，连接后可立即投屏");
//                    mLineTv.setText("扫码连接");
//                }
//            }else {
//                desTv.setText("您当前没有连接wifi，如需投屏请确保wifi开启\n并与需要投屏的电视保证在同一WiFi环境下");
//                mLineTv.setText("去设置");
//            }
//        }
//
//        videoBtn.setOnClickListener(this);
//        imageBtn.setOnClickListener(this);
//        slideBtn.setOnClickListener(this);
//        fileBtn.setOnClickListener(this);
//        mLineTv.setOnClickListener(this);
//        mBlankLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mShowToScreenWindow.dismiss();
//            }
//        });
//        mShowToScreenWindow.setContentView(contentView);
//        mShowToScreenWindow.setAnimationStyle(R.style.PopupSavor);
//    }
//    public void showChangeWifiDialog() {
//        Intent intent = new Intent();
//        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
//        mContext.startActivity(intent);
//    }
//    public void refreshState(Context context) {
//        mBindTv = mSession.isBindTv();
//        if(mBindTv) {
//            desTv.setText("您已成功连接"+WifiUtil.getWifiName(mContext)+"的电视\n点击下列分类选择文件即可电视投屏");
//            mLineTv.setText("断开连接");
//        }else {
//            if (WifiUtil.checkWifiState(mContext)) {
//                mSessionSmallPlatformEnable = mSession.isSmallPlatformEnable();
//                if (!mSessionSmallPlatformEnable) {
//                    desTv.setText("您当前连接的网段内没发现可连接设备\n如需投屏请确保wifi开启并与需要投屏\n电视保证在同一wifi环境下");
//                    mLineTv.setText("去设置");
//                } else {
//                    desTv.setText("已发现可连接的电视，连接后即可投屏");
//                    mLineTv.setText("立即连接");
//                }
//            } else {
//                desTv.setText("您当前没有连接wifi，如需投屏请确保wifi开启\n并与需要投屏的电视保证在同一wifi环境下");
//                mLineTv.setText("去设置");
//            }
//        }
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
//    }
//
//    public boolean isShowing() {
//        return mShowToScreenWindow.isShowing();
//    }
//    @Override
//    public void onClick(View v) {
//        Intent intent;
//        switch (v.getId()) {
//            case R.id.btn_file:
//                mBindView.showToast(mContext.getString(R.string.function_devloping));
//                break;
//            case R.id.tv_link:
//                if(!WifiUtil.checkWifiState(mContext)) {
//                    showChangeWifiDialog();
//                }else {
//                    if(mBindTv) {
//                        mBindView.showUnLinkDialog();
//                    }else {
//                        if(!mSessionSmallPlatformEnable) {
//                            showChangeWifiDialog();
//                        }else {
//                            mBindPresenter.bindTv();
////                        dismiss();
//                        }
//                    }
//                }
//                break;
//            case R.id.btn_video:
//                intent = new Intent(mContext,VideoShareActivity.class);
//                mContext.startActivityForResult(intent,0);
//                dismiss();
//                break;
//            case R.id.btn_image:
//                intent = new Intent(mContext, PhotoActivity.class);
//                mContext.startActivityForResult(intent,0);
////                IntentUtil.openActivity(mContext,PhotoActivity.class);
//                dismiss();
//                break;
//            case R.id.btn_slide:
//                intent = new Intent(mContext,SlideActivity.class);
//                mContext.startActivityForResult(intent,0);
////                IntentUtil.openActivity(mContext,SlideActivity.class);
//                dismiss();
//                break;
//        }
//    }
//}
