package com.savor.savorphone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.common.api.bitmap.BitmapCommonUtils;
import com.common.api.utils.AppUtils;
import com.common.api.utils.ShowMessage;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.core.Session;
import com.savor.savorphone.utils.blur.RotateTransformation;
import com.savor.savorphone.widget.LinkDialog;

import java.io.File;
import java.io.IOException;

public class ImageEditActivity extends BaseActivity implements View.OnClickListener {

    public static final int COMPLETE = 0x1;
    private ImageView mImageView;
    private RelativeLayout mContainerLayout;
    private MediaInfo mPhotoBean;
    private TextView mPrimaryTv;
    private TextView mDesTv;
    private TextView mDateTv;
    private RelativeLayout mEditTextlayout;
    private EditText mEditTextEt;
    public int mCurrentEditItem = EDIT_NONE;
    public static final int EDIT_NONE = 0x0;
    public static final int EDIT_PRIMARY = 0x1;
    public static final int EDIT_DESC = 0x2;
    public static final int EDIT_DATE = 0x3;
    private TextView mCompleteInputBtn;
    private String mCurrentEditText;
    private TextView mInputShowTv;
    private TextView mCompleteEditTv;
    private boolean ishasInput;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COMPLETE:
                    // 完成
                    Intent intent = new Intent();
                    intent.putExtra("pic",mPhotoBean);
                    setResult(COMPLETE,intent);
                    finish();
                    break;
            }
        }
    };
    private int mRotate;
    private String mPath;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);
        mSession = Session.get(this);
        handleInante();
        getViews();
        setViews();
        setListeners();

    }
    public void getViews() {
        // 预编辑组件初始化
        mImageView = (ImageView) findViewById(R.id.iv_pic);
        mContainerLayout = (RelativeLayout) findViewById(R.id.rl_container);
        mPrimaryTv = (TextView) findViewById(R.id.tv_primary);
        mDesTv = (TextView) findViewById(R.id.tv_desc);
        mDateTv = (TextView) findViewById(R.id.tv_date);
        mCompleteEditTv = (TextView) findViewById(R.id.tv_complete);

        // 编辑布局初始化
        mEditTextlayout = (RelativeLayout) findViewById(R.id.rl_text_edit);
        mEditTextEt = (EditText) findViewById(R.id.et_input);
        mCompleteInputBtn = (TextView) findViewById(R.id.tv_input_complete);
        mInputShowTv = (TextView) findViewById(R.id.tv_text_show);
    }

    public void setViews() {
        initEditTextHint();
        Glide.with(this).
                load(mPath).
                centerCrop()
                .transform(new RotateTransformation(this,mRotate%360)).
                into(mImageView);
    }


    public void setListeners() {
        mPrimaryTv.setOnClickListener(this);
        mDesTv.setOnClickListener(this);
        mDateTv.setOnClickListener(this);
        mCompleteInputBtn.setOnClickListener(this);
        mCompleteEditTv.setOnClickListener(this);

        setEditInputListener();
    }

    private void setEditInputListener() {
        mEditTextEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if(!TextUtils.isEmpty(content)) {
                    mInputShowTv.setText(content);
                    switch (mCurrentEditItem) {
                        case EDIT_DATE:
                            if(content.length()>20) {
                                mEditTextEt.setText(content.substring(0,20));
                                mEditTextEt.setSelection(20);
                                ShowMessage.showToast(ImageEditActivity.this,"最多输入20个字符");
                            }
                            break;
                        case EDIT_DESC:
                            if(content.length()>20) {
                                mEditTextEt.setText(content.substring(0,20));
                                mEditTextEt.setSelection(20);
                                ShowMessage.showToast(ImageEditActivity.this,"最多输入20个字符");
                            }
                            break;
                        case EDIT_PRIMARY:
                            if(content.length()>16) {
                                mEditTextEt.setText(content.substring(0,16));
                                mEditTextEt.setSelection(16);
                                ShowMessage.showToast(ImageEditActivity.this,"最多输入16个字符");
                            }
                            break;
                    }
                }else {
                    mInputShowTv.setText(R.string.click_input_text);
                }
            }
        });
    }

    private void handleInante() {
        mPhotoBean = (MediaInfo) getIntent().getSerializableExtra("pic");
        mCurrentEditItem = getIntent().getIntExtra("edit_item", EDIT_NONE);
        mCurrentEditText = getIntent().getStringExtra("hint");
        mRotate = getIntent().getIntExtra("rotate", -1);
        mPath = getIntent().getStringExtra("path");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_primary:
                // 主标题
                mCurrentEditItem = EDIT_PRIMARY;
                showEditLayout();
                break;
            case R.id.tv_desc:
                // 副标题
                mCurrentEditItem = EDIT_DESC;
                showEditLayout();
                break;
            case R.id.tv_date:
                // 时间
                mCurrentEditItem = EDIT_DATE;
                showEditLayout();
                break;
            case R.id.tv_complete:
                // 完成编辑
                completeEdit();
                break;
            case R.id.tv_input_complete:
                // 完成输入
                completeInput();
                break;
        }
    }

    /**
     * 完成输入，判断当前对那个文本框进行操作显示输入文本
     * 对文本编辑布局状态重置
     */
    private void completeInput() {
        mEditTextlayout.setVisibility(View.GONE);
        AppUtils.hideSoftKeybord(this);

        mCurrentEditText = mEditTextEt.getText().toString();
        mEditTextEt.setText("");
        mInputShowTv.setText(getString(R.string.click_input_text));

        setEditTextToBean();
    }

    private void setEditTextToBean() {
        switch (mCurrentEditItem) {
            case EDIT_PRIMARY:
                if(!TextUtils.isEmpty(mCurrentEditText)) {
                    mPrimaryTv.setText(mCurrentEditText);
                    mPhotoBean.setPrimaryText(mCurrentEditText);
                }else {
                    mPhotoBean.setPrimaryText(null);
                }
                break;
            case EDIT_DESC:
                if(!TextUtils.isEmpty(mCurrentEditText)) {
                    mDesTv.setText(mCurrentEditText);
                    mPhotoBean.setDesText(mCurrentEditText);
                }else {
                    mPhotoBean.setDesText(null);
                }
                break;
            case EDIT_DATE:
                if(!TextUtils.isEmpty(mCurrentEditText)) {
                    mDateTv.setText(mCurrentEditText);
                    mPhotoBean.setDateText(mCurrentEditText);
                }else {
                    mPhotoBean.setDateText(null);
                }
                break;
        }
    }

    /**设置编辑文字提示信息*/
    private void initEditTextHint() {
        if(mPhotoBean!=null) {
            String primaryText = mPhotoBean.getPrimaryText();
            String desText = mPhotoBean.getDesText();
            String dateText = mPhotoBean.getDateText();
            if(!TextUtils.isEmpty(primaryText)) {
                mPrimaryTv.setText(primaryText);
            }
            if(!TextUtils.isEmpty(desText)) {
                mDesTv.setText(desText);
            }
            if(!TextUtils.isEmpty(dateText)) {
                mDateTv.setText(dateText);
            }
        }
    }

    private void completeEdit() {
        String primarytv = mPrimaryTv.getText().toString();
        String desTv = mDesTv.getText().toString();
        String dateText = mDateTv.getText().toString();
        String defHint = getString(R.string.input_text_here);
        boolean primmaryNoInput = defHint.equals(primarytv);
        boolean desNoInput = defHint.equals(desTv);
        boolean dateNoinput = defHint.equals(dateText);

//        if(primmaryNoInput) {
            mPrimaryTv.setVisibility(primmaryNoInput?View.INVISIBLE:View.VISIBLE);
//        }

//        if(desNoInput) {
            mDesTv.setVisibility(desNoInput?View.INVISIBLE:View.VISIBLE);
//        }

//        if(dateNoinput) {
            mDateTv.setVisibility(dateNoinput?View.INVISIBLE:View.VISIBLE);
//        }
        if(mPhotoBean!=null) {
            mPhotoBean.setPrimaryText(primarytv);
            mPhotoBean.setDateText(dateText);
            mPhotoBean.setDesText(desTv);
        }

        // 去掉边框
        initTextBorder(false);

        // 输入内容都为空的时候直接关闭编辑
        if(primmaryNoInput&&desNoInput&&dateNoinput) {
            finish();
            return;
        }

        // 保存到gallery缓存目录
        saveLayoutShot();

    }

    private void saveLayoutShot() {
        Toast.makeText(this,"正在合成图片...",Toast.LENGTH_LONG).show();
        final Bitmap viewBitmap = BitmapCommonUtils.getViewBitmap(mContainerLayout, mContainerLayout.getWidth(), mContainerLayout.getHeight());
        new Thread(){
            @Override
            public void run() {
                String dir = mSession.getCompressPath();
                File fileDir = new File(dir);
                if(!fileDir.exists())
                    fileDir.mkdirs();

                String path = dir+mPhotoBean.getAssetname()+"coumpund.png";
                mPhotoBean.setCompoundPath(path);
                File file = new File(path);
                if(!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                BitmapCommonUtils.saveBitmap2SdCard(viewBitmap,path);
                mHandler.sendEmptyMessage(COMPLETE);
            }
        }.start();

    }

    private void initTextBorder(boolean isShowBorder) {
        mPrimaryTv.setBackgroundResource(0);
        mDesTv.setBackgroundResource(0);
        mDateTv.setBackgroundResource(0);
    }

    /**
     * 显示文字编辑布局
     * */
    private void showEditLayout() {
        mEditTextlayout.setVisibility(View.VISIBLE);
        AppUtils.tryFocusEditText(this,mEditTextEt);

        // 数据回显
        String primaryText = mPrimaryTv.getText().toString();
        String desText = mDesTv.getText().toString();
        String dateText = mDateTv.getText().toString();
        String initText = "";
        switch (mCurrentEditItem) {
            case EDIT_PRIMARY:
                if(!getString(R.string.input_text_here).equals(primaryText)) {
                    initText = primaryText;
                }
                break;
            case EDIT_DESC:
                if(!getString(R.string.input_text_here).equals(desText)) {
                    initText = desText;
                }
                break;
            case EDIT_DATE:
                if(!getString(R.string.input_text_here).equals(dateText)) {
                    initText = dateText;
                }
                break;
        }

        mInputShowTv.setText(initText);
        mEditTextEt.setText(initText);
        mEditTextEt.setSelection(initText.length());
    }
}
