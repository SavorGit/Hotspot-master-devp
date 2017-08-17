package com.savor.savorphone.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.core.InitViews;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.savorphone.R;
import com.savor.savorphone.adapter.CollectListAdapter;
import com.savor.savorphone.bean.CommonListItem;
import com.savor.savorphone.bean.CommonListResult;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.utils.SavorAnimUtil;
import com.savor.savorphone.widget.CommonDialog;
import com.savor.savorphone.widget.ProgressBarView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14.
 */

public class MyCollectActivity extends BaseActivity implements InitViews,View.OnClickListener ,
        CollectListAdapter.OnItemStoreClickListener,
        CollectListAdapter.OnItemLongClickListener,
        ProgressBarView.ProgressBarViewClickListener,
        ApiRequestListener {

    private Context context;
    private TextView tv_center;
    private ImageView iv_left;
    private PullToRefreshListView mPullRefreshListView;
    private CollectListAdapter mAdapter;
    private ProgressBarView mProgressLayout;
    private String ucreateTime;
    private CommonListResult listResult;
    private List<CommonListItem> list = new ArrayList<CommonListItem>();
    private boolean isUp = true;
    private RelativeLayout empty_la;
    private TextView mRefreshDataHinttv;
    private boolean istop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        context = this;
        getViews();
        setViews();
        setListeners();
        getData();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                RecordUtils.onEvent(this,getString(R.string.menu_collection_back));
                finish();
                break;
            case R.id.empty_la:
                getData();
                break;

            default:
                break;
        }
    }

    private void getData(){
        AppApi.getLastCollectoinList(this,ucreateTime,this);

    }
    @Override
    public void getViews() {
        tv_center = (TextView) findViewById(R.id.tv_center);
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.listview);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        mProgressLayout = (ProgressBarView)findViewById(R.id.progressbar);
        empty_la = (RelativeLayout)findViewById(R.id.empty_la);
        mRefreshDataHinttv = (TextView) findViewById(R.id.tv_refresh_data_hint);
    }

    private void showRefreshHintAnimation(final String hint) {
        mRefreshDataHinttv.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshDataHinttv.setText(hint);
                SavorAnimUtil.startListRefreshHintAnimation(mRefreshDataHinttv);
            }
        },1000);
    }
    @Override
    public void setViews() {
        tv_center.setText(context.getString(R.string.my_store));
        mAdapter = new CollectListAdapter(context,this,this);
       // mListView = mPullRefreshListView.getRefreshableView();
        mPullRefreshListView.setAdapter(mAdapter);
    }

    @Override
    public void setListeners() {
        iv_left.setOnClickListener(this);
        empty_la.setOnClickListener(this);
        //mAdapter.setOnItemLongClickListener(this);
        mPullRefreshListView.setOnRefreshListener(onRefreshListener);
        mPullRefreshListView.setOnLastItemVisibleListener(onLastItemVisibleListener);
        mPullRefreshListView.onLoadComplete(true,false);
        mProgressLayout.setProgressBarViewClickListener(this);

    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        mPullRefreshListView.onRefreshComplete();
        switch (method) {
            case POST_LAST_COLLECTION_JSON:
                mProgressLayout.loadSuccess();
                if(obj instanceof CommonListResult) {
                    listResult= (CommonListResult) obj;
                    if (listResult != null) {
                        List<CommonListItem> mList =  listResult.getList();
                        handleVodList(mList);
                    }
                }
                break;
            case GET_ADD_MY_COLLECTION_JSON:
                mProgressLayout.loadSuccess();
                if (obj instanceof String){
                    String str = (String)obj;
                    if ("success".equals(str)){
                        ucreateTime = "";
                        isUp = true;
                        getData();
                    }
                }
                break;


        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {

        switch (method) {
            case POST_LAST_COLLECTION_JSON:
                mProgressLayout.loadSuccess();

                if (obj instanceof ResponseErrorMessage){
                    ResponseErrorMessage errorMessage = (ResponseErrorMessage)obj;
                    String statusCode = String.valueOf(errorMessage.getCode());
                    if (AppApi.ERROR_TIMEOUT.equals(statusCode)){
                        mProgressLayout.loadFailure("数据加载超时");
                        showRefreshHintAnimation("数据加载超时");
                    }else if (AppApi.ERROR_NETWORK_FAILED.equals(statusCode)){
                        mProgressLayout.loadFailure("网络异常，点击重试");
                        showRefreshHintAnimation("无法连接到网络,请检查网络设置");
                    }
                }


                break;
            case GET_HIT_EGG_JSON:


                break;



        }
    }

    private void handleVodList(List<CommonListItem> mList){

                if (mList != null && mList.size() > 0) {
                    empty_la.setVisibility(View.GONE);
                    mPullRefreshListView.setVisibility(View.VISIBLE);
                    if (isUp) {
                        list.clear();
                        mAdapter.clear();
                        mPullRefreshListView.onLoadComplete(true,false);
                        if (istop) {
                            showRefreshHintAnimation("更新成功");
                        }

                    }else {
                        mPullRefreshListView.onLoadComplete(true,false);
                    }
                    ucreateTime = mList.get(mList.size()-1).getUcreateTime()+"";
                    list.addAll(mList);
                    mAdapter.setData(list,isUp);
                    int haveNext = 0;
                    haveNext =  listResult.getNextpage();

                    if (haveNext==0) {
                        mPullRefreshListView.onLoadComplete(false,false);
                    }else {
                        mPullRefreshListView.onLoadComplete(true,false);
                    }
                }else {
                    if (list != null && list.size()>0  ) {
                        mAdapter.clear();
                    }
                    mProgressLayout.loadSuccess();
                    empty_la.setVisibility(View.VISIBLE);
                    mPullRefreshListView.setVisibility(View.GONE);

                    mPullRefreshListView.onLoadComplete(false,true);
                }



    }

    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            ucreateTime = "";
            isUp = true;
            istop = true;
            getData();
        }
    };

    PullToRefreshBase.OnLastItemVisibleListener onLastItemVisibleListener = new PullToRefreshBase.OnLastItemVisibleListener() {
        @Override
        public void onLastItemVisible() {
            isUp = false;
            istop = false;
            getData();
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 根据上面发送过去的请求吗来区别
        switch (requestCode) {
            case 0:

                break;
            case 10:
                ucreateTime = "";
                isUp = true;
                getData();
                break;
            default:
                break;
        }
    }


    @Override
    public void onStoreClick(CommonListItem itemVo) {
        int type = itemVo.getType();
        String artid = itemVo.getArtid();
        int collected = itemVo.getCollected();
        String  title = itemVo.getTitle();
        String  contentURL = itemVo.getContentURL();
        String  imageURL = itemVo.getImageURL();
        String  categoryId =  itemVo.getCategoryId();
        /**文章类型  0:纯文本，1:图文，2:图集,3:视频,4:纯视频*/

        switch (type){
            case 0:
            case 1:
                itemVo.setCollected(1);
                Intent intent = new Intent();
                intent.putExtra("item",itemVo);
                if ("103".equals(categoryId)) {
                    intent.setClass(MyCollectActivity.this,SubjectActivity.class);
                }else {
                    intent.setClass(MyCollectActivity.this,ImageTextActivity.class);
                }

                startActivityForResult(intent,10);
                break;
            case 2:
                Intent intentp = new Intent(context, PictureSetActivity.class);
                intentp.putExtra("content_id",artid);
                intentp.putExtra("collected",collected);
                intentp.putExtra("voditem",itemVo);
                startActivityForResult(intentp,10);
                break;
            case 3:
            case 4:
                Intent  intentv = new Intent(context, VideoPlayVODNotHotelActivity.class);
                intentv.putExtra("voditem",itemVo);
                intentv.putExtra("isBindTv",mSession.isBindTv());
                startActivityForResult(intentv,10);
                break;

        }

//        if ( 4 == type) {
//            Intent videoIntent = new Intent(MyCollectActivity.this, VideoOnlyActivity.class);
//            videoIntent.putExtra("topicItem", itemVo);
//            videoIntent.putExtra("isBindTv", 1);
//            videoIntent.putExtra("vodType",1);
//            startActivityForResult(videoIntent,10);
//        }else {
//            RecordUtils.onEvent(this,getString(R.string.menu_collection_details));
//            Intent videoIntent = new Intent(MyCollectActivity.this, VideoPlayVODNotHotelActivity.class);
//            videoIntent.putExtra("topicItem", itemVo);
//            videoIntent.putExtra("isBindTv", 1);
//            videoIntent.putExtra("vodType",1);
//            startActivityForResult(videoIntent,10);
//        }


    }

    @Override
    public void onItemLongClick( final  CommonListItem itemVo) {


        new CommonDialog(this, "是否删除这条收藏？", new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
//                RecordUtils.onEvent(MyCollectActivity.this,getString(R.string.menu_cancel_collection));
//                if (mSession.contains(itemVo)) {
//                    mSession.remoeCollect(itemVo);
//                    getData();
//                }
                AppApi.handleCollection(context,MyCollectActivity.this,itemVo.getArtid(),"0");

            }
        }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
            }
        },"确定").show();
    }



    @Override
    public void loadDataEmpty() {
        mProgressLayout.loadSuccess();
        getData();
    }

    @Override
    public void loadFailureNoNet() {

    }

    @Override
    public void loadFailure() {
        mProgressLayout.loadSuccess();
        getData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordUtils.onPageStartAndResume(this,this);
    }





}
