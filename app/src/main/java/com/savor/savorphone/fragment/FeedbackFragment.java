package com.savor.savorphone.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.core.InitViews;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.activity.FeedbackActivity;
import com.savor.savorphone.activity.HelpActivity;
import com.savor.savorphone.activity.MyCollectActivity;
import com.savor.savorphone.core.ApiRequestListener;
import com.savor.savorphone.core.AppApi;
import com.savor.savorphone.utils.ImageCacheUtils;
import com.savor.savorphone.widget.CustomActionSheetDialog;

/**
 * Created by bushlee on 2016/12/9.
 */

//我的主页
public class FeedbackFragment extends BaseFragment implements InitViews, View.OnClickListener,ApiRequestListener {

    //变量声明
    private Context context;
    private TextView mSend;
    private EditText mFeedback;
    private EditText mContact;
    private String mDeviceID;
    private TextView mContentLength;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_feedback, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        initViews(view);
        setViews();
        setListeners();
    }


    //页面控件变量初始化
    private void initViews(View view) {
        mSend = (TextView)view.findViewById(R.id.send);

        mFeedback = (EditText) view.findViewById(R.id.text_advice);
        //
        mContentLength = (TextView) view.findViewById(R.id.contentLength);
        mContact = (EditText) view.findViewById(R.id.text_contact);

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    //绑定单击事件处理
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.send:
                SendMsg();
                break;
            default:
        }
    }

    private void SendMsg(){
        if (TextUtils.isEmpty(mFeedback.getText())) {
            mFeedback.setError("内容不能为空");
            return;
        }
        if (TextUtils.isEmpty(mContact.getText())) {
            mContact.setError("请留下您的联系方式");
            return;
        }

        AppApi.submitFeedback(context,mDeviceID,mFeedback.getText().toString(),mContact.getText().toString(),this);

    }
    @Override
    public void getViews() {

    }

    @Override
    public void setViews() {
        mDeviceID = mSession.getImei();
    }

    //绑定控件单击事件
    @Override
    public void setListeners() {
        mFeedback.addTextChangedListener(mTextWatcher);
        mSend.setOnClickListener(this);
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
                ShowMessage.showToast(context,"最多只能输入200个字符");
            }

        }
    };

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_FEEDBACK_JSON:
                ShowMessage.showToast(context,"提交成功,感谢您的反馈");
                //finish();
                break;
            default:
                break;
//            case POST_SET_ATTENTION_JSON:
//
//                break;
        }
    }
    @Override
    public void onError(AppApi.Action method, Object obj) {
        super.onError(method, obj);
    }
    @Override
    public String getFragmentName() {
        return "FeedbackFragment";
    }
}
