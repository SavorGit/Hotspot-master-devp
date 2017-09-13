package com.savor.savorphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.SpecialListAdapter;
import com.savor.savorphone.bean.SpecialList;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.utils.SavorAnimUtil;
import com.savor.savorphone.utils.SavorCacheUtil;
import com.savor.savorphone.widget.ProgressBarView;

import java.util.List;

public class SpecialListActivity extends BaseActivity implements View.OnClickListener, PullToRefreshListView.NetworkUnavailableOnClick, ProgressBarView.ProgressBarViewClickListener, AdapterView.OnItemClickListener {
    public static final int REFRESH_TYPE_TOP = 1;
    private int currentRefreshState = REFRESH_TYPE_TOP;
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
            String update_time = (String) msg.obj;
            switch (what){
                case HEAD_REFRESH:
                    // listItems.clear();
                case BOTTOM_REFRESH:
                    getData(update_time);
                    break;
            }
        }
    };
    private ProgressBarView mLoadingLayout;
    private TextView refreshDataHintTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_list);

        getViews();
        setViews();
        setListeners();
        getData("");
    }

    private void getData(String update_time) {
        if(mSpecialListAdapter.getCount()==0) {
            mLoadingLayout.startLoading();
        }
        AppApi.getSpecialList(this,update_time,this);
    }

    @Override
    public void getViews() {
        refreshDataHintTV = (TextView) findViewById(R.id.tv_refresh_data_hint);
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
        mSpecialListView.setOnLastItemVisibleListener(onLastItemVisibleListener);
        mSpecialListView.setOnItemClickListener(this);
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
            message.obj = item.getUpdateTime();
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

    private void showRefreshHintAnimation(final String hint) {
        refreshDataHintTV.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshDataHintTV.setText(hint);
                SavorAnimUtil.startListRefreshHintAnimation(refreshDataHintTV);
            }
        },500);
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
                        showRefreshHintAnimation(getString(R.string.refresh_success));
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
                setPtrSuccessComplete();
                if (obj instanceof ResponseErrorMessage){
                    ResponseErrorMessage errorMessage = (ResponseErrorMessage)obj;
                    String statusCode = String.valueOf(errorMessage.getCode());
                    if(currentRefreshState == REFRESH_TYPE_TOP) {
                        if (AppApi.ERROR_TIMEOUT.equals(statusCode)){
                            showRefreshHintAnimation("数据加载超时");
                        }else if (AppApi.ERROR_NETWORK_FAILED.equals(statusCode)){
                            showRefreshHintAnimation("无法连接到网络,请检查网络设置");
                        }
                    }
                }

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SpecialList.SpecialListItem item = (SpecialList.SpecialListItem) parent.getItemAtPosition(position);
        String specialId = item.getId();
        Intent intent = new Intent(this,SpecialDetailActivity.class);
        intent.putExtra("id",specialId);
        startActivity(intent);
    }
}
