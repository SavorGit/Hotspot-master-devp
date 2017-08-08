package com.savor.savorphone.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.core.InitViews;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.activity.FeedbackActivity;
import com.savor.savorphone.activity.HelpActivity;
import com.savor.savorphone.activity.MyCollectActivity;
import com.savor.savorphone.fragment.BaseFragment;
import com.savor.savorphone.utils.ImageCacheUtils;
import com.savor.savorphone.widget.CustomActionSheetDialog;

import java.io.File;

/**
 * Created by bushlee on 2016/12/9.
 */

//我的主页
public class MyFragment extends BaseFragment implements InitViews, View.OnClickListener{

    //变量声明
    private Context context;
    private RelativeLayout my_store;
    private RelativeLayout feedback;
    private RelativeLayout clean;
    private TextView size;
    private RelativeLayout help;
    private TextView tv_appinfo;
    private ImageView iv_left;
    private TextView tv_center;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        initViews(view);
        setViews();
        setListeners();
    }


    //页面控件变量初始化
    private void initViews(View view) {
        my_store = (RelativeLayout) view.findViewById(R.id.my_store);
        feedback = (RelativeLayout) view.findViewById(R.id.feedback);
        clean = (RelativeLayout) view.findViewById(R.id.clean);
        help = (RelativeLayout) view.findViewById(R.id.help);
        size = (TextView) view.findViewById(R.id.size);
        tv_appinfo = (TextView) view.findViewById(R.id.size);
        TextView appInfo = (TextView) view.findViewById(R.id.tv_appinfo);
        tv_center = (TextView) view.findViewById(R.id.tv_center);
        iv_left = (ImageView) view.findViewById(R.id.iv_left);
        //appInfo.setText(getString(R.string.app_info, mSession.getVersionName()));
        //new File(SavorApplication.getInstance().getCacheDir() + "/image_cache");

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    //绑定单击事件处理
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.my_store:
                Intent intentStore = new Intent(getActivity(), MyCollectActivity.class);
                startActivity(intentStore);
                break;
            case R.id.feedback:
                Intent intentAdvice = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intentAdvice);
                break;
            case R.id.clean:
                new CustomActionSheetDialog(getActivity())
                        .builder()
                        .setTitle("本次清除缓存,将清除图片、视频、以及您的文件缓存,请确认您的操作")
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("确定", CustomActionSheetDialog.SheetItemColor.Red,
                                new CustomActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                       // new CleanAsyncTask().execute();
                                        ImageCacheUtils.clearImageAllCache();
                                        showToast("已清除");
                                        size.setText("0.0MB");

                                    }
                                }).show();
                break;
            case R.id.help:
                Intent intentHelp = new Intent(getActivity(), HelpActivity.class);
                startActivity(intentHelp);


                break;
        }
    }

    @Override
    public void getViews() {

    }

    @Override
    public void setViews() {
        size.setText(ImageCacheUtils.getCacheSize());
        tv_center.setText(context.getString(R.string.my));
        iv_left.setVisibility(View.GONE);
    }

    //绑定控件单击事件
    @Override
    public void setListeners() {
        feedback.setOnClickListener(this);
        help.setOnClickListener(this);
        my_store.setOnClickListener(this);
        clean.setOnClickListener(this);
    }

    @Override
    public String getFragmentName() {
        return "MyFragment";
    }
}
