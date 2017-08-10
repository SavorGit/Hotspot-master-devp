package com.savor.savorphone.activity;

import android.os.Bundle;

import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.VodListAdapter;

public class VodListActivity extends BaseProActivity {

    private PullToRefreshListView mVodListView;
    private VodListAdapter mVodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_list);

        getViews();
        setViews();
        setListeners();
    }

    public void getViews() {
        mVodListView = (PullToRefreshListView) findViewById(R.id.ptl_vod_list);

    }

    @Override
    public void setViews() {
        mVodAdapter = new VodListAdapter(this);
        mVodListView.setAdapter(mVodAdapter);
    }
}
