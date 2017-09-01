package com.savor.savorphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.SpecialListAdapter;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.SpecialList;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.utils.SavorCacheUtil;
import com.savor.savorphone.widget.ProgressBarView;

import java.util.List;

public class SpecialListActivity extends BaseActivity implements View.OnClickListener, PullToRefreshListView.NetworkUnavailableOnClick, ProgressBarView.ProgressBarViewClickListener {
    private int currentRefreshState;
    public static final int REFRESH_TYPE_TOP = 1;
    public static final int REFRESH_TYPE_BOTTOM = 2;
    private static final int HEAD_REFRESH = 10101;
    private static final int BOTTOM_REFRESH = 10102;
    private ImageView mBackBtn;
    private PullToRefreshListView mSpecialListView;
    private SpecialListAdapter mSpecialListAdapter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            String id = (String) msg.obj;
            switch (what){
                case HEAD_REFRESH:
                    // listItems.clear();
                case BOTTOM_REFRESH:
                    getData(id);
                    break;
            }
        }
    };
    private ProgressBarView mLoadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_list);

        getViews();
        setViews();
        setListeners();

        getData("");
    }

    private void getData(String id) {
        currentRefreshState = REFRESH_TYPE_TOP;
        mLoadingLayout.startLoading();
        AppApi.getSpecialList(this,id,this);
    }

    @Override
    public void getViews() {
        mSpecialListView = (PullToRefreshListView) findViewById(R.id.ptl_special_list);
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mLoadingLayout = (ProgressBarView) findViewById(R.id.pbv_loading);
    }

    @Override
    public void setViews() {
        mSpecialListAdapter = new SpecialListAdapter(this);
        mSpecialListView.setAdapter(mSpecialListAdapter);
        SpecialList specialList = SavorCacheUtil.getInstance().getSpecialList(this);
        if(specialList!=null) {
            List<SpecialList.SpecialListItem> list = specialList.getList();
            if(list!=null&&list.size()>0) {
                mSpecialListAdapter.setData(list);
            }
        }
    }

    @Override
    public void setListeners() {
        mBackBtn.setOnClickListener(this);
        mSpecialListView.setOnRefreshListener(onRefreshListener);
        mSpecialListView.setNetworkUnavailableOnClick(this);
        mLoadingLayout.setProgressBarViewClickListener(this);
    }


    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            currentRefreshState = REFRESH_TYPE_TOP;
            Message message = Message.obtain();
            message.what = HEAD_REFRESH;
            message.obj = "";
            handler.sendMessage(message);
        }
    };

    PullToRefreshBase.OnLastItemVisibleListener onLastItemVisibleListener = new PullToRefreshBase.OnLastItemVisibleListener() {
        @Override
        public void onLastItemVisible() {
            currentRefreshState = REFRESH_TYPE_BOTTOM;
            bottomRefresh();
        }
    };

    private void bottomRefresh() {
        List<SpecialList.SpecialListItem> data = mSpecialListAdapter.getData();
        if (data!=null&&data.size()>0){
            SpecialList.SpecialListItem item = data.get(data.size()-1);
            Message message = Message.obtain();
            message.what = BOTTOM_REFRESH;
            message.obj = item.getId();
            handler.sendMessage(message);
        }
    }

    private Runnable ptrRunnable = new Runnable() {

        @Override
        public void run() {
            mSpecialListView.onRefreshComplete();
        }
    };

    private void setPtrSuccessComplete() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(ptrRunnable, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_SPECIAL_LIST_JSON:
                setPtrSuccessComplete();
                mLoadingLayout.loadSuccess();
                if(obj instanceof SpecialList) {
                    SpecialList specialList = (SpecialList) obj;
                    List<SpecialList.SpecialListItem> list = specialList.getList();
                    int nextpage = specialList.getNextpage();
                    if(currentRefreshState == REFRESH_TYPE_TOP) {
                        mSpecialListAdapter.setData(list);
                        SavorCacheUtil.getInstance().cacheSpecialList(this,specialList);
                    }else {
                        mSpecialListAdapter.addData(list);
                    }
                    mSpecialListView.onLoadComplete(nextpage==1,nextpage==0);
                }
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_SPECIAL_LIST_JSON:
                if(currentRefreshState == REFRESH_TYPE_BOTTOM) {
                    mSpecialListView.onLoadCompleteNetworkUnavailable(true);
                }else {
                    int count = mSpecialListAdapter.getCount();
                    switch (currentRefreshState) {
                        case REFRESH_TYPE_BOTTOM:
                            mSpecialListView.onLoadCompleteNetworkUnavailable(true);
                            break;
                        default:
                            if(count == 0) {
                                mLoadingLayout.loadFailure();
                            }else {
                                mSpecialListView.onLoadComplete(true);
                                mLoadingLayout.loadSuccess();
                            }
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void bottomOnClick() {
        mSpecialListView.onLoadComplete(true);
        bottomRefresh();
    }

    @Override
    public void loadDataEmpty() {

    }

    @Override
    public void loadFailureNoNet() {

    }

    @Override
    public void loadFailure() {
        getData("");
    }
}
