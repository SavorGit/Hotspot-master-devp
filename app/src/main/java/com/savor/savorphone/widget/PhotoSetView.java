package com.savor.savorphone.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.savor.savorphone.widget.imageshow.TouchImageView;
import com.savor.savorphone.widget.imageshow.WrapMotionEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin on 2017/8/10.
 */

public class PhotoSetView extends ImageView {
    private PhotoViewAttacher attacher;
    private ScaleType pendingScaleType;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    static final long DOUBLE_PRESS_INTERVAL = 600;
    static final float FRICTION = 0.9f;

    // We can be in one of these 4 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int CLICK = 10;
    int mode = NONE;

    float redundantXSpace, redundantYSpace;
    float right, bottom, origWidth, origHeight, bmWidth, bmHeight;
    float width, height;
    PointF last = new PointF();
    PointF mid = new PointF();
    PointF start = new PointF();
    float[] m;
    float matrixX, matrixY;

    float saveScale = 1f;
    float minScale = 1f;
    float maxScale = 3f;
    float oldDist = 1f;

    PointF lastDelta = new PointF(0, 0);
    float velocity = 0;

    long lastPressTime = 0, lastDragTime = 0;
    boolean allowInert = false;

    private Context mContext;
    private Timer mClickTimer;
    private OnClickListener mOnClickListener;
    private Object mScaleDetector;
    private Handler mTimerHandler = null;

    // Scale mode on DoubleTap
    private boolean zoomToOriginalSize = false;

    public boolean isZoomToOriginalSize() {
        return this.zoomToOriginalSize;
    }

    public void setZoomToOriginalSize(boolean zoomToOriginalSize) {
        this.zoomToOriginalSize = zoomToOriginalSize;
    }

    public boolean onLeftSide = false, onTopSide = false, onRightSide = false,
            onBottomSide = false;

    public PhotoSetView(Context context) {
        this(context, null);
    }

    public PhotoSetView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PhotoSetView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init();
    }

    @TargetApi(21)
    public PhotoSetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        attacher = new PhotoViewAttacher(this);
        //We always pose as a Matrix scale type, though we can change to another scale type
        //via the attacher
        super.setScaleType(ScaleType.MATRIX);
        //apply the previously applied scale type
        if (pendingScaleType != null) {
            setScaleType(pendingScaleType);
            pendingScaleType = null;
        }

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent rawEvent) {
                WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);
                if (mScaleDetector != null) {
                    ((ScaleGestureDetector) mScaleDetector)
                            .onTouchEvent(rawEvent);
                }
                fillMatrixXY();
                PointF curr = new PointF(event.getX(), event.getY());

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        allowInert = false;
                        savedMatrix.set(matrix);
                        last.set(event.getX(), event.getY());
                        start.set(last);
                        mode = DRAG;

                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        // Log.d(TAG, "oldDist=" + oldDist);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                            // Log.d(TAG, "mode=ZOOM");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        allowInert = true;
                        mode = NONE;
                        int xDiff = (int) Math.abs(event.getX() - start.x);
                        int yDiff = (int) Math.abs(event.getY() - start.y);

                        if (xDiff < CLICK && yDiff < CLICK) {

                            // Perform scale on double click
                            long pressTime = System.currentTimeMillis();
                            if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {
                                if (mClickTimer != null)
                                    mClickTimer.cancel();
                                if (saveScale == 1) {
                                    final float targetScale = maxScale / saveScale;
                                    matrix.postScale(targetScale, targetScale,
                                            start.x, start.y);
                                    saveScale = maxScale;
                                } else {
                                    matrix.postScale(minScale / saveScale, minScale
                                            / saveScale, width / 2, height / 2);
                                    saveScale = minScale;
                                }
                                calcPadding();
                                checkAndSetTranslate(0, 0);
                                lastPressTime = 0;
                            } else {
                                lastPressTime = pressTime;
                                mClickTimer = new Timer();
                                mClickTimer.schedule(new PhotoSetView.Task(), 300);
                            }
                            if (saveScale == minScale) {
                                scaleMatrixToBounds();
                            }
                        }

                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        velocity = 0;
                        savedMatrix.set(matrix);
                        oldDist = spacing(event);
                        // Log.d(TAG, "mode=NONE");
                        break;

                    case MotionEvent.ACTION_MOVE:
                        allowInert = false;
                        if (mode == DRAG) {
                            float deltaX = curr.x - last.x;
                            float deltaY = curr.y - last.y;

                            long dragTime = System.currentTimeMillis();

                            velocity = (float) distanceBetween(curr, last)
                                    / (dragTime - lastDragTime) * FRICTION;
                            lastDragTime = dragTime;

                            checkAndSetTranslate(deltaX, deltaY);
                            lastDelta.set(deltaX, deltaY);
                            last.set(curr.x, curr.y);
                        } else if (mScaleDetector == null && mode == ZOOM) {
                            float newDist = spacing(event);
                            if (rawEvent.getPointerCount() < 2)
                                break;
                            // There is one serious trouble: when you scaling with
                            // two fingers, then pick up first finger of gesture,
                            // ACTION_MOVE being called.
                            // Magic number 50 for this case
                            if (10 > Math.abs(oldDist - newDist)
                                    || Math.abs(oldDist - newDist) > 50)
                                break;
                            float mScaleFactor = newDist / oldDist;
                            oldDist = newDist;

                            float origScale = saveScale;
                            saveScale *= mScaleFactor;
                            if (saveScale > maxScale) {
                                saveScale = maxScale;
                                mScaleFactor = maxScale / origScale;
                            } else if (saveScale < minScale) {
                                saveScale = minScale;
                                mScaleFactor = minScale / origScale;
                            }

                            calcPadding();
                            if (origWidth * saveScale <= width
                                    || origHeight * saveScale <= height) {
                                matrix.postScale(mScaleFactor, mScaleFactor,
                                        width / 2, height / 2);
                                if (mScaleFactor < 1) {
                                    fillMatrixXY();
                                    if (mScaleFactor < 1) {
                                        scaleMatrixToBounds();
                                    }
                                }
                            } else {
                                PointF mid = midPointF(event);
                                matrix.postScale(mScaleFactor, mScaleFactor, mid.x,
                                        mid.y);
                                fillMatrixXY();
                                if (mScaleFactor < 1) {
                                    if (matrixX < -right)
                                        matrix.postTranslate(-(matrixX + right), 0);
                                    else if (matrixX > 0)
                                        matrix.postTranslate(-matrixX, 0);
                                    if (matrixY < -bottom)
                                        matrix.postTranslate(0, -(matrixY + bottom));
                                    else if (matrixY > 0)
                                        matrix.postTranslate(0, -matrixY);
                                }
                            }
                            checkSiding();
                        }
                        break;
                }

                setImageMatrix(matrix);
                invalidate();
                return false;
            }
        });
    }

    /** Determine the space between the first two fingers */
    private float spacing(WrapMotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, WrapMotionEvent event) {
        // ...
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private PointF midPointF(WrapMotionEvent event) {
        // ...
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }

    private void calcPadding() {
        right = width * saveScale - width - (2 * redundantXSpace * saveScale);
        bottom = height * saveScale - height
                - (2 * redundantYSpace * saveScale);
    }

    private void fillMatrixXY() {
        matrix.getValues(m);
        matrixX = m[Matrix.MTRANS_X];
        matrixY = m[Matrix.MTRANS_Y];
    }

    private void scaleMatrixToBounds() {
        if (Math.abs(matrixX + right / 2) > 0.5f)
            matrix.postTranslate(-(matrixX + right / 2), 0);
        if (Math.abs(matrixY + bottom / 2) > 0.5f)
            matrix.postTranslate(0, -(matrixY + bottom / 2));
    }

    private void checkSiding() {
        fillMatrixXY();
        // Log.d(TAG, "x: " + matrixX + " y: " + matrixY + " left: " + right / 2
        // + " top:" + bottom / 2);
        float scaleWidth = Math.round(origWidth * saveScale);
        float scaleHeight = Math.round(origHeight * saveScale);
        onLeftSide = onRightSide = onTopSide = onBottomSide = false;
        if (-matrixX < 10.0f)
            onLeftSide = true;
        // Log.d("GalleryViewPager",
        // String.format("ScaleW: %f; W: %f, MatrixX: %f", scaleWidth, width,
        // matrixX));
        if ((scaleWidth >= width && (matrixX + scaleWidth - width) < 10)
                || (scaleWidth <= width && -matrixX + scaleWidth <= width))
            onRightSide = true;
        if (-matrixY < 10.0f)
            onTopSide = true;
        if (Math.abs(-matrixY + height - scaleHeight) < 10.0f)
            onBottomSide = true;
    }

    private void checkAndSetTranslate(float deltaX, float deltaY) {
        float scaleWidth = Math.round(origWidth * saveScale);
        float scaleHeight = Math.round(origHeight * saveScale);
        fillMatrixXY();
        if (scaleWidth < width) {
            deltaX = 0;
            if (matrixY + deltaY > 0)
                deltaY = -matrixY;
            else if (matrixY + deltaY < -bottom)
                deltaY = -(matrixY + bottom);
        } else if (scaleHeight < height) {
            deltaY = 0;
            if (matrixX + deltaX > 0)
                deltaX = -matrixX;
            else if (matrixX + deltaX < -right)
                deltaX = -(matrixX + right);
        } else {
            if (matrixX + deltaX > 0)
                deltaX = -matrixX;
            else if (matrixX + deltaX < -right)
                deltaX = -(matrixX + right);

            if (matrixY + deltaY > 0)
                deltaY = -matrixY;
            else if (matrixY + deltaY < -bottom)
                deltaY = -(matrixY + bottom);
        }
        matrix.postTranslate(deltaX, deltaY);
        checkSiding();
    }

    private double distanceBetween(PointF left, PointF right) {
        return Math.sqrt(Math.pow(left.x - right.x, 2)
                + Math.pow(left.y - right.y, 2));
    }
    private class Task extends TimerTask {
        public void run() {
            mTimerHandler.sendEmptyMessage(0);
        }
    }
    /**
     * Get the current {@link PhotoViewAttacher} for this view. Be wary of holding on to references
     * to this attacher, as it has a reference to this view, which, if a reference is held in the
     * wrong place, can cause memory leaks.
     *
     * @return the attacher.
     */
    public PhotoViewAttacher getAttacher() {
        return attacher;
    }

    @Override
    public ScaleType getScaleType() {
        return attacher.getScaleType();
    }

    @Override
    public Matrix getImageMatrix() {
        return attacher.getImageMatrix();
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        attacher.setOnLongClickListener(l);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        attacher.setOnClickListener(l);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (attacher == null) {
            pendingScaleType = scaleType;
        } else {
            attacher.setScaleType(scaleType);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // setImageBitmap calls through to this method
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            attacher.update();
        }
        return changed;
    }

    public void setRotationTo(float rotationDegree) {
        attacher.setRotationTo(rotationDegree);
    }

    public void setRotationBy(float rotationDegree) {
        attacher.setRotationBy(rotationDegree);
    }

    @Deprecated
    public boolean isZoomEnabled() {
        return attacher.isZoomEnabled();
    }

    public boolean isZoomable() {
        return attacher.isZoomable();
    }

    public void setZoomable(boolean zoomable) {
        attacher.setZoomable(zoomable);
    }

    public RectF getDisplayRect() {
        return attacher.getDisplayRect();
    }

    public void getDisplayMatrix(Matrix matrix) {
        attacher.getDisplayMatrix(matrix);
    }

    public boolean setDisplayMatrix(Matrix finalRectangle) {
        return attacher.setDisplayMatrix(finalRectangle);
    }

    public void getSuppMatrix(Matrix matrix) {
        attacher.getSuppMatrix(matrix);
    }

    public boolean setSuppMatrix(Matrix matrix) {
        return attacher.setDisplayMatrix(matrix);
    }

    public float getMinimumScale() {
        return attacher.getMinimumScale();
    }

    public float getMediumScale() {
        return attacher.getMediumScale();
    }

    public float getMaximumScale() {
        return attacher.getMaximumScale();
    }

    public float getScale() {
        return attacher.getScale();
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        attacher.setAllowParentInterceptOnEdge(allow);
    }

    public void setMinimumScale(float minimumScale) {
        attacher.setMinimumScale(minimumScale);
    }

    public void setMediumScale(float mediumScale) {
        attacher.setMediumScale(mediumScale);
    }

    public void setMaximumScale(float maximumScale) {
        attacher.setMaximumScale(maximumScale);
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        attacher.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        attacher.setOnMatrixChangeListener(listener);
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        attacher.setOnPhotoTapListener(listener);
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener listener) {
        attacher.setOnOutsidePhotoTapListener(listener);
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        attacher.setOnViewTapListener(listener);
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        attacher.setOnViewDragListener(listener);
    }

    public void setScale(float scale) {
        attacher.setScale(scale);
    }

    public void setScale(float scale, boolean animate) {
        attacher.setScale(scale, animate);
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        attacher.setScale(scale, focalX, focalY, animate);
    }

    public void setZoomTransitionDuration(int milliseconds) {
        attacher.setZoomTransitionDuration(milliseconds);
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        attacher.setOnDoubleTapListener(onDoubleTapListener);
    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
        attacher.setOnScaleChangeListener(onScaleChangedListener);
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        attacher.setOnSingleFlingListener(onSingleFlingListener);
    }

}
