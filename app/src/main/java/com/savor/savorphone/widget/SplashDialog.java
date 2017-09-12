package com.savor.savorphone.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.utils.DensityUtil;
import com.savor.savorphone.R;

import java.io.IOException;

/**
 * Created by Administrator on 2017/3/9.
 */

public class SplashDialog extends Dialog implements View.OnClickListener{
    private SurfaceView surfaceView;
    private Context mContext;
    private MediaPlayer mp = null;
    private OnPlayOverListener mOnPlayOverListener;

    public SplashDialog(@NonNull Context context) {
        super(context,R.style.Dialog_Fullscreen);
        this.mContext = context;
        mp = new MediaPlayer();
        setCancelable(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_splash);
        initVideo();
    }

    private void initVideo() {
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mOnPlayOverListener!=null) {
                    mOnPlayOverListener.onSplashPlayOver();
                }
                mp.release();
            }
        });
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mp.setSurface(holder.getSurface());
                try {
                    AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(R.raw.splash);
                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    afd.close();
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
                int viewWidth = surfaceView.getWidth();
                int viewHeight = surfaceView.getHeight();
                if (((double) viewWidth / width) * height > viewHeight) {
                    layoutParams.width = (int) (((double) viewHeight / height) * width);
                    layoutParams.height = viewHeight;
                } else {
                    layoutParams.width = viewWidth;
                    layoutParams.height = (int) (((double) viewWidth / width) * height);
                }

            }
        });

        surfaceView.setZOrderOnTop(true);
//        surfaceView.getHolder().setFormat(SurfaceView.);
//        btn_start = (Button) findViewById(R.id.btn_start);
//        btn_start.setOnClickListener(this);
//
//        videoview = (CustomVideoView) findViewById(R.id.videoview);
//        //设置播放加载路径
//        videoview.setVideoURI(Uri.parse("android.resource://"+mContext.getPackageName()+"/"+R.raw.scan_guide));
//
//        //播放
//        videoview.start();
//        //循环播放
//        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                videoview.start();
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.btn_scan:
//            case R.id.layout:
//                mp.release();
//                dismiss();
//                break;
        }
    }

    public void setPlayOverListener(OnPlayOverListener listener) {
        this.mOnPlayOverListener = listener;
    }

    public interface OnPlayOverListener {
        void onSplashPlayOver();
    }
}
