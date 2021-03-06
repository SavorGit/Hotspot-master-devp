package com.savor.savorphone.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hezd on 2017/1/13.
 */

public class CategoryPagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager mFrgmentManager;
    private List<Fragment> mPagerList ;
    private List<String> mTitleList ;
    public CategoryPagerAdapter(FragmentManager fm) {
        super(fm);
        this.mFrgmentManager = fm;
    }
    public CategoryPagerAdapter(FragmentManager fm,List<Fragment> list,List<String> titleList) {
        super(fm);
        this.mPagerList = list;
        this.mTitleList = titleList;
    }

    public void setData(List<Fragment> list,List<String> titleList) {
        this.mPagerList = list;
        this.mTitleList = titleList;
        notifyDataSetChanged();
    }

    public void addPager(Fragment fragment,String title) {
        mPagerList.add(fragment);
        mTitleList.add(title);
        notifyDataSetChanged();
    }

    public void addPager(Fragment fragment,String title,int index) {

        if(!mPagerList.contains(fragment)) {
            mPagerList.add(index,fragment);
            mTitleList.add(index,title);
            notifyDataSetChanged();
        }
    }

    public void removePager(Fragment fragment,String title) {
        if(mPagerList.contains(fragment)) {
            mPagerList.remove(fragment);
            mTitleList.remove(title);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        int i = mPagerList.indexOf(object);
        if(i == -1)
        return PagerAdapter.POSITION_NONE;
        return i;
    }
    @Override
    public int getCount() {
        return mPagerList==null?0:mPagerList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mPagerList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = mPagerList.get(position);
//        if(!fragment.isAdded()) {
//            FragmentTransaction ft = mFrgmentManager.beginTransaction();
//            ft.add(fragment, fragment.hashCode()+"");
//            ft.commit();
//        }
//        return fragment.getView();
//    }
}
