package com.savor.savorphone.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.savor.savorphone.R;
import com.savor.savorphone.adapter.CategoryPagerAdapter;
import com.savor.savorphone.fragment.FeedbackFragment;
import com.savor.savorphone.fragment.QandAFragment;
import com.savor.savorphone.interfaces.IBaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/17.
 */

public class FeedbackMainActivity extends FragmentActivity implements IBaseView,View.OnClickListener {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fd_main);
        mContext = this;
        getViews();
        setViews();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void getViews() {
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    @Override
    public void setViews() {

        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for(int i =0;i<2;i++) {
            TabLayout.Tab tab = mTabLayout.newTab();
            mTabLayout.addTab(tab);
            if (i == 0) {
                titles.add("常见问题");
                QandAFragment fragment = new QandAFragment();
                fragments.add(fragment);
            }else if (i == 1) {
                titles.add("我要反馈");
                FeedbackFragment fragment = new FeedbackFragment();
                fragments.add(fragment);
            }
        }

        CategoryPagerAdapter mPagerAdapter = new CategoryPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setData(fragments,titles);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public void setListeners() {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showLoadingLayout() {

    }

    @Override
    public void hideLoadingLayout() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }
    }
}
