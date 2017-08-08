package com.savor.savorphone.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.savor.savorphone.R;

import java.io.IOException;

/**
 * Created by Administrator on 2017/3/9.
 */

public class SplashVideoDialog extends Dialog{
    private final String mUrl;
    private SurfaceView surfaceView;
    private TextView scan_tv;
    private Context mContext;
    private MediaPlayer mp = null;
    private RelativeLayout layout;
    private OnCompletionListener mOncompletionListener;

    public SplashVideoDialog(@NonNull Context context,String url) {
        super(context,R.style.Dialog_Fullscreen);
        this.mContext = context;
        mp = new MediaPlayer();
//        mp.setLooping(true);
        this.mUrl = url;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_splash_video);
        initVideo();
    }

    private void initVideo() {

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mOncompletionListener!=null) {
                    mOncompletionListener.oncompletion();
                    mp.setOnCompletionListener(null);
                }
                if(mp!=null&&mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                }
            }
        });
//
//        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                mp.start();
//                return false;
//            }
//        });
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mp.setSurface(holder.getSurface());
                try {
                    mp.setDataSource(mUrl);
                    mp.prepareAsync();
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(mp!=null&&mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                }
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
    public void onDetachedFromWindow() {
        if(mp!=null&&mp.isPlaying()) {
            mp.stop();
            mp.release();
        }
        super.onDetachedFromWindow();
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.mOncompletionListener = listener;
    }

    public interface OnCompletionListener {
        void oncompletion();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            dismiss();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
