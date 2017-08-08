package com.savor.savorphone.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.savor.savorphone.R;

import java.io.IOException;

/**
 * Created by Administrator on 2017/3/9.
 */

public class ScanGuideDialog extends Dialog implements View.OnClickListener{
    @Override
    public void onClick(View view) {

    }
    private SurfaceView surfaceView;
    private TextView scan_tv;
    private Context mContext;
    private MediaPlayer mp = null;
    private RelativeLayout layout;
    public ScanGuideDialog(@NonNull Context context) {
        super(context,R.style.Dialog_Fullscreen);
        this.mContext = context;
        mp = new MediaPlayer();
        mp.setLooping(true);
    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.dialog_scan_guide);
//        initVideo();
//    }
//
//    private void initVideo() {
//
//
////        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
////            @Override
////            public void onCompletion(MediaPlayer mp) {
////                mp.start();
////            }
////        });
//        layout =  (RelativeLayout) findViewById(R.id.layout);
//        scan_tv = (TextView) findViewById(R.id.btn_scan);
//        scan_tv.setOnClickListener(this);
//        layout.setOnClickListener(this);
//        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
//        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                mp.setSurface(holder.getSurface());
//                try {
//                    AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(R.raw.scan_guide);
//                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//                    afd.close();
//                    mp.prepare();
//                    mp.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });
//        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//            @Override
//            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//                ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
//                int viewWidth = surfaceView.getWidth();
//                int viewHeight = surfaceView.getHeight();
//                if (((double) viewWidth / width) * height > viewHeight) {
//                    layoutParams.width = (int) (((double) viewHeight / height) * width);
//                    layoutParams.height = viewHeight;
//                } else {
//                    layoutParams.width = viewWidth;
//                    layoutParams.height = (int) (((double) viewWidth / width) * height);
//                }
//            }
//        });
////        btn_start = (Button) findViewById(R.id.btn_start);
////        btn_start.setOnClickListener(this);
////
////        videoview = (CustomVideoView) findViewById(R.id.videoview);
////        //设置播放加载路径
////        videoview.setVideoURI(Uri.parse("android.resource://"+mContext.getPackageName()+"/"+R.raw.scan_guide));
////
////        //播放
////        videoview.start();
////        //循环播放
////        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
////            @Override
////            public void onCompletion(MediaPlayer mediaPlayer) {
////                videoview.start();
////            }
////        });
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn_scan:
//            case R.id.layout:
//                mp.release();
//                dismiss();
//                break;
//        }
//    }
}
