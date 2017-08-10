package com.savor.savorphone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.VodListAdapter;

public class VodListActivity extends BaseProActivity implements View.OnClickListener {

    private PullToRefreshListView mVodListView;
    private VodListAdapter mVodAdapter;
    private ImageView mBackBtn;

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
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
    }

    @Override
    public void setViews() {
        mVodAdapter = new VodListAdapter(this);
        mVodListView.setAdapter(mVodAdapter);
    }

    @Override
    public void setListeners() {
        mBackBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
        }
    }
}
