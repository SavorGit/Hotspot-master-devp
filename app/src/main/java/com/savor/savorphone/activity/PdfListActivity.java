package com.savor.savorphone.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.savor.savorphone.R;
import com.savor.savorphone.adapter.PdfListAdapter;
import com.savor.savorphone.bean.PdfInfo;
import com.savor.savorphone.utils.ActivitiesManager;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.utils.ConstantValues;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.widget.CustomWebView;
import com.savor.savorphone.widget.DefaultWebView;
import com.savor.savorphone.widget.PdfDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfListActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, PdfDialog.OnConfirmListener {

    private ListView mPdfListView;
    private PdfListAdapter mPdfListAdapter;
    private TextView mTitleTv;
    private ImageView mRightIv;
    private DefaultWebView mWebView;
    private ImageView mBackBtn;
    private PdfDialog pdfDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list);

        getViews();
        setViews();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final List<PdfInfo> pdfList = mSession.getPdfList();
        List<PdfInfo> tempList = new ArrayList<>();
        if(pdfList!=null) {
            for (PdfInfo pdfInfo : pdfList) {
                String path = pdfInfo.getPath();
                File file = new File(path);
                if (file.exists()) {
                    tempList.add(pdfInfo);
                }
            }
        }
        if (tempList != null && tempList.size() > 0) {
            mPdfListAdapter.setData(pdfList);
            mWebView.setVisibility(View.GONE);
        } else {
            mWebView.setVisibility(View.VISIBLE);
            mWebView.loadUrl(ConstantValues.H5_FILE_PRO_HELP,null);
        }

        RecordUtils.onEvent(this,R.string.file_to_screen_list);
    }

    @Override
    public void getViews() {
        mTitleTv = (TextView) findViewById(R.id.tv_center);
        mRightIv = (ImageView) findViewById(R.id.iv_right);
        mPdfListView = (ListView) findViewById(R.id.lv_files);
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mWebView = (DefaultWebView) findViewById(R.id.webview_custom);
    }

    @Override
    public void setViews() {
        mTitleTv.setText("我的文件");
        mRightIv.setImageResource(R.drawable.bangzhupdf);
        mRightIv.setVisibility(View.VISIBLE);
        mPdfListAdapter = new PdfListAdapter(this);
        mPdfListView.setAdapter(mPdfListAdapter);
        final List<PdfInfo> pdfList = mSession.getPdfList();
        List<PdfInfo> tempList = new ArrayList<>();
        if(pdfList!=null) {
            for (PdfInfo pdfInfo : pdfList) {
                String path = pdfInfo.getPath();
                File file = new File(path);
                if (file.exists()) {
                    tempList.add(pdfInfo);
                }
            }
        }
        if (tempList != null && tempList.size() > 0) {
            mPdfListAdapter.setData(pdfList);
            mWebView.setVisibility(View.GONE);
            mRightIv.setVisibility(View.VISIBLE);
        } else {
            mRightIv.setVisibility(View.GONE);
            pdfDialog = new PdfDialog(this,this);
            pdfDialog.show();
            mWebView.setVisibility(View.VISIBLE);
            mWebView.loadUrl(ConstantValues.H5_FILE_PRO_HELP,null);
        }
    }

    @Override
    public void setListeners() {
        mPdfListView.setOnItemClickListener(this);
        mRightIv.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RecordUtils.onEvent(this,R.string.file_to_screen_play);
        PdfInfo info = (PdfInfo) parent.getItemAtPosition(position);
        String path = info.getPath();
        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this,PdfPreviewActivity.class);
            Uri data = Uri.parse("file://"+path);
            boolean lockedScrren = ProjectionManager.getInstance().isLockedScrren();
            intent.putExtra("isLockedScrren",lockedScrren);
            intent.setDataAndType(data,"application/pdf");
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if(ActivitiesManager.getInstance().contains(HotspotMainActivity.class)) {
            setResult(HotspotMainActivity.FROM_APP_BACK);
        }else {
            Intent intent = new Intent(this,HotspotMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_right:
                RecordUtils.onEvent(this,R.string.file_to_screen_help);
                Intent intent = new Intent(this,FileProHelpActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_left:
                finish();
                break;
        }
    }

    @Override
    public void onConfirm() {
        pdfDialog.dismiss();
    }
}
