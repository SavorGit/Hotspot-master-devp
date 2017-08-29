package com.savor.savorphone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;

public class SpecialListActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBackBtn;
    private PullToRefreshListView mSpecialListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_list);

        getViews();
        setViews();
        setListeners();
    }

    @Override
    public void getViews() {
        mSpecialListView = (PullToRefreshListView) findViewById(R.id.ptl_special_list);
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
    }

    @Override
    public void setViews() {

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
