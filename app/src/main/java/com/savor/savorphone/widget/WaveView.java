package com.savor.savorphone.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class WaveView extends View {

    private Paint mPaint;
    private float[] cycles = new float[2];
    private float mWw;

    public WaveView(Context context) {
        super(context);
        init(null, 0);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setBackgroundColor(Color.TRANSPARENT);

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
//        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() > 0 && getMeasuredWidth() > 0) {

            mWw = Math.min(getMeasuredWidth() /2f, getMeasuredHeight()/2f) / (cycles.length+1);
            for (int i = 0; i< cycles.length; i++) {
                cycles[i] = mWw * (i+1);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

            for (int i = 0; i < cycles.length; i++) {
                int alpha =  (int) ((Math.min(getMeasuredWidth() /2f, getMeasuredHeight()/2f)-cycles[i]) / cycles[i] * 100);
                mPaint.setAlpha(alpha);
                canvas.drawCircle(getMeasuredWidth() / 2f, getMeasuredHeight() / 2f, cycles[i], mPaint);

                cycles[i] += 0.5;
                if (cycles[i] > Math.min(getMeasuredWidth() /2f, getMeasuredHeight()/2f)) {
                    cycles[i] = mWw;
                }
            }

            postInvalidateDelayed(20);


    }
}
