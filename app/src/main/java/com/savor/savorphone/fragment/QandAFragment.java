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
import com.savor.savorphone.R;
import com.savor.savorphone.activity.FeedbackActivity;
import com.savor.savorphone.activity.HelpActivity;
import com.savor.savorphone.activity.MyCollectActivity;
import com.savor.savorphone.utils.ImageCacheUtils;
import com.savor.savorphone.widget.CustomActionSheetDialog;

/**
 * Created by bushlee on 2016/12/9.
 */

//我的主页
public class QandAFragment extends BaseFragment implements InitViews, View.OnClickListener{

    //变量声明
    private Context context;
    private RelativeLayout rl_q1;
    private RelativeLayout rl_q2;
    private RelativeLayout rl_q3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qa, container, false);
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
        rl_q1 = (RelativeLayout) view.findViewById(R.id.rl_q1);
        rl_q2 = (RelativeLayout) view.findViewById(R.id.rl_q2);
        rl_q3 = (RelativeLayout) view.findViewById(R.id.rl_q3);


    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    //绑定单击事件处理
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.rl_q1:
                Intent intentStore = new Intent(getActivity(), HelpActivity.class);
                startActivity(intentStore);
                break;
            case R.id.rl_q2:
                Intent intentAdvice = new Intent(getActivity(), HelpActivity.class);
                startActivity(intentAdvice);
                break;
            case R.id.rl_q3:
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
//        size.setText(ImageCacheUtils.getCacheSize());
//        tv_center.setText(context.getString(R.string.my));
//        iv_left.setVisibility(View.GONE);
    }

    //绑定控件单击事件
    @Override
    public void setListeners() {
        rl_q1.setOnClickListener(this);
        rl_q2.setOnClickListener(this);
        rl_q3.setOnClickListener(this);
    }

    @Override
    public String getFragmentName() {
        return "MyFragment";
    }
}
