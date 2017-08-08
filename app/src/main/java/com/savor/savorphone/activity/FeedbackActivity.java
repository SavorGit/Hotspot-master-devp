package com.savor.savorphone.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.api.core.InitViews;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.core.ResponseErrorMessage;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.widget.CommonDialog;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/12/12.
 */

public class FeedbackActivity extends Activity implements View.OnClickListener,ApiRequestListener {

    private Context context;
    private View mSend;
    private EditText mFeedback;
    private EditText mContact;
    private String mDeviceID;
    private TextView mContentLength;
    private ImageView iv_left;
   // private LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        context = this;
        getViews();
        setListeners();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                RecordUtils.onEvent(this,getString(R.string.menu_feedback_back));
                onBackPressed();
                break;
            case R.id.send:
                mSend.setClickable(false);
                SendMsg();
                break;
            default:
        }

    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_FEEDBACK_JSON:
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.menu_feedback_back_submit),"success");
                RecordUtils.onEvent(this,getString(R.string.menu_feedback_back_submit),hashMap);
                ShowMessage.showToast(FeedbackActivity.this,"提交成功,感谢您的反馈");
                finish();
                break;
            default:
                break;
//            case POST_SET_ATTENTION_JSON:
//
//                break;
        }
    }

    private void SendMsg(){
        mSend.setClickable(false);
        if (TextUtils.isEmpty(mFeedback.getText().toString())) {
            //mFeedback.setError("内容不能为空");
           // ShowMessage.showToast(FeedbackActivity.this,"内容不能为空");
            UnLinkDialog();
            return;
        }
        AppApi.submitFeedback(context,mDeviceID,mFeedback.getText().toString(),mContact.getText().toString(),this);

    }

    private void UnLinkDialog() {
        new CommonDialog(context, "内容不能为空", new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {

            }
        }).show();
        mSend.setClickable(true);
    }
    @Override
    public void onError(AppApi.Action method, Object obj) {

        switch (method) {
            case POST_FEEDBACK_JSON:
                String msg = "";
                mSend.setClickable(true);
                if (obj instanceof ResponseErrorMessage){
                    ResponseErrorMessage errorMessage = (ResponseErrorMessage)obj;
                    String statusCode = String.valueOf(errorMessage.getCode());
                    if (AppApi.ERROR_TIMEOUT.equals(statusCode)){
                         msg = "提交失败，请重试";
                       // mProgressLayout.loadFailure("数据加载超时");
                    }else if (AppApi.ERROR_NETWORK_FAILED.equals(statusCode)){
                        msg = "无法连接到网络，请检查网络设置";
                       // mProgressLayout.loadFailure("网络异常，点击重试");
                    }
                }
                new CommonDialog(context, msg, new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {

                    }
                }).show();
                break;
        }
    }

    @Override
    public void onNetworkFailed(AppApi.Action method) {

    }


    public void getViews() {
        mSend = findViewById(R.id.send);

        mFeedback = (EditText) findViewById(R.id.text_advice);
        //
        mContentLength = (TextView) findViewById(R.id.contentLength);
        mContact = (EditText) findViewById(R.id.text_contact);
        iv_left = (ImageView) findViewById(R.id.iv_left);

    }



    public void setListeners() {
        mFeedback.addTextChangedListener(mTextWatcher);
       // back.setOnClickListener(this);
        mSend.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        mContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null&&!TextUtils.isEmpty(s.toString())) {
                    RecordUtils.onEvent(FeedbackActivity.this,getString(R.string.menu_feedback_information));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            int size = s.length();
            if (size<201){
                mContentLength.setText(size+ "/200");
            }else {
                ShowMessage.showToast(FeedbackActivity.this,"最多只能输入200个字符");
            }
            RecordUtils.onEvent(FeedbackActivity.this,getString(R.string.menu_feedback_input));
        }
    };

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
