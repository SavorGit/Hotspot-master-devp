package com.savor.savorphone.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.common.api.utils.DensityUtil;


/**
 * 发现页面的圆形图标遮层
 *
 * @author savor
 */
public class OvalWindowLayout extends RelativeLayout {
    private boolean init = false;
    private int background = 0xFFFFFFFF;
    private Paint paint;
    private Path path;
    private int Margin;

    public OvalWindowLayout(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OvalWindowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OvalWindowLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!init) {
            Margin = DensityUtil.dip2px(getContext(), 40);
            initPaint();
        }
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(background);
        paint.setStyle(Style.FILL);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        path = new Path();
        path.moveTo(0, height);
        path.lineTo(0, 0);
        path.lineTo(width, 0);
        path.lineTo(width, height);
        path.lineTo(0, height);

        path.arcTo(new RectF(Margin, Margin, width - Margin, width - Margin), 180, -359, true);
        path.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }
}
