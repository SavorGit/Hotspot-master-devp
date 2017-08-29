package com.savor.savorphone.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.DensityUtil;
import com.savor.savorphone.R;
import com.savor.savorphone.activity.SpecialListActivity;
import com.savor.savorphone.adapter.SpecialDetailItemAdapter;

/**
 * 专题组详情页
 */
public class SpecialFragment extends BaseFragment implements View.OnClickListener {
    public static final float IMAGE_SCALE = 484/750f;
    private Context mContext;
    private static final String TAG = "SpecialFragment";
    private ImageView mSpeicalIv;
    private TextView mSpecialTitleTv;
    private TextView mSpecialDesTv;
    private RecyclerView mSpecialListView;
    private SpecialDetailItemAdapter mSpecialDetailItemAdapter;
    private TextView mSpecialListTv;

    public SpecialFragment() {
    }

    public static SpecialFragment newInstance() {
        SpecialFragment fragment = new SpecialFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_special, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setViews();
        setListeners();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void initViews(View view) {
        mSpecialListTv = (TextView) view.findViewById(R.id.tv_look_special_list);
        mSpeicalIv = (ImageView) view.findViewById(R.id.iv_special_pic);
        mSpecialTitleTv = (TextView) view.findViewById(R.id.tv_special_title);
        mSpecialDesTv = (TextView) view.findViewById(R.id.tv_special_desc);
        mSpecialListView = (RecyclerView) view.findViewById(R.id.rlv_special_item);
    }

    @Override
    public void setViews() {
        int screenWidth = DensityUtil.getScreenWidth(mContext);
        float height = screenWidth * IMAGE_SCALE;
        ViewGroup.LayoutParams layoutParams = mSpeicalIv.getLayoutParams();
        layoutParams.height = (int) height;

        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        mSpecialListView.setLayoutManager(manager);
        mSpecialDetailItemAdapter = new SpecialDetailItemAdapter(mContext);
        mSpecialListView.setAdapter(mSpecialDetailItemAdapter);
    }

    @Override
    public void setListeners() {
        mSpecialListTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_look_special_list:
                Intent intent = new Intent(mContext,SpecialListActivity.class);
                startActivity(intent);
                break;
        }
    }
}
