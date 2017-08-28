package com.savor.savorphone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.DensityUtil;
import com.common.api.widget.ScrollListView;
import com.savor.savorphone.R;

/**
 * 专题组详情页
 */
public class SpecialFragment extends BaseFragment {
    public static final float IMAGE_SCALE = 484/750f;
    private Context mContext;
    private static final String TAG = "SpecialFragment";
    private ImageView mSpeicalIv;
    private TextView mSpecialTitleTv;
    private TextView mSpecialDesTv;
    private ScrollListView mSpecialListView;

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
        mSpeicalIv = (ImageView) view.findViewById(R.id.iv_special_pic);
        mSpecialTitleTv = (TextView) view.findViewById(R.id.tv_special_title);
        mSpecialDesTv = (TextView) view.findViewById(R.id.tv_special_desc);
        mSpecialListView = (ScrollListView) view.findViewById(R.id.slv_special_item);
    }

    @Override
    public void setViews() {
        int screenWidth = DensityUtil.getScreenWidth(mContext);
        float height = screenWidth * IMAGE_SCALE;
        ViewGroup.LayoutParams layoutParams = mSpeicalIv.getLayoutParams();
        layoutParams.height = (int) height;
    }

    @Override
    public void setListeners() {

    }
}
