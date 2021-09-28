package com.b8a3.photoview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.b8a3.photoview.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 支持内部放置view进行全屏放大
 */
public class ZoomFullScreenLayout extends FrameLayout {

    //响应自定义手势的几个状态, 0 默认状态, 系统处理, 1 准备状态  2拦截掉自己处理图片的状态 3回复到原位置的动画状态
    public static final int STATE_DEFAULT = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_ZOOM = 2;
    public static final int STATE_RESTORE = 3;

    private ViewGroup mDecorView;//用来承载view的最上层界面
    private View mOriView; //从原布局拿出来的view
    private ViewGroup.LayoutParams mOriLp; //原view的参数lp
    private View mPlaceHolderView;//用来放到原来的位置, 占位用的view

    private final int[] mOriTopLeft = new int[2];

    private float mDonwX, mDownY; //手指按下的点
    private float moveX, moveY; //手指移动的点

    private Activity mActivity;
    private int mState = STATE_DEFAULT;
    private final PointF mLastCenter = new PointF(); //双指中心点
    private double mLastDistance; //双指距离

    public ZoomFullScreenLayout(@NonNull Context context) {
        super(context);
        init((Activity) context);
    }

    public ZoomFullScreenLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init((Activity) context);
    }

    public ZoomFullScreenLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init((Activity) context);
    }

    public void init(Activity activity) {
        mActivity = activity;
        mPlaceHolderView = new View(activity);
        mPlaceHolderView.setBackgroundResource(R.color.green);
    }

    private void backToOri() {
        if (mState == STATE_RESTORE) {
            return;
        }
        mState = STATE_RESTORE;
        final float translationX = mOriView.getTranslationX();
        final float translationY = mOriView.getTranslationY();
        final float scaleX = mOriView.getScaleX();
        final float scaleY = mOriView.getScaleY();

        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0f);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                //从初始位置到0的过程
                mOriView.setTranslationX(translationX * f);
                mOriView.setTranslationY(translationY * f);
                //从初始位置到1的过程
                mOriView.setScaleX(1.0f + (scaleX - 1.0f) * f);
                mOriView.setScaleY(1.0f + (scaleY - 1.0f) * f);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //mImageView.setVisibility(GONE);
                //mOriImageView.setVisibility(VISIBLE);
                //把占位view拿出来, 把原来的view放回去
                mDecorView.removeView(mOriView);
                removeView(mPlaceHolderView);
                addView(mOriView, mOriLp);
                mState = STATE_DEFAULT;
            }
        });
        animator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mActivity != null) {
            int action = event.getActionMasked();
            if (mState == STATE_READY) {//准备
                int pointerCount = event.getPointerCount();
                if (pointerCount <= 1 ||
                        (pointerCount == 2 && (action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL))) {
                    mState = STATE_DEFAULT;
                } else {
                    if (action == MotionEvent.ACTION_MOVE && mOriView != null) {
                        float x1 = event.getX(0);
                        float y1 = event.getY(0);
                        float x2 = event.getX(1);
                        float y2 = event.getY(1);

                        double distance = getDistance(x1, y1, x2, y2);
                        if (Math.abs(distance - mLastDistance) >= 10) {
                            mDecorView = (ViewGroup) mActivity.getWindow().getDecorView();
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(mOriView.getWidth(), mOriView.getHeight());
                            lp.topMargin = mOriTopLeft[1];
                            lp.leftMargin = mOriTopLeft[0];
                            lp.gravity = Gravity.LEFT | Gravity.TOP;

                            //把原来的view拿出来, 把占位view放进去
                            mOriLp = mOriView.getLayoutParams();
                            removeView(mOriView);
                            mPlaceHolderView.setId(mOriView.getId());
                            addView(mPlaceHolderView, new LinearLayout.LayoutParams(mOriView.getWidth(), mOriView.getHeight()));
                            mDecorView.addView(mOriView, lp);

                            //修复因为阈值引起的第一帧跳变
                            mDonwX = x1;
                            mDownY = y1;
                            mLastCenter.x = (x1 + x2) * 0.5f;
                            mLastCenter.y = (y1 + y2) * 0.5f;
                            mLastDistance = getDistance(x1, y1, x2, y2);

                            mState = STATE_ZOOM;//进入拖动状态
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    return true;
                }
            } else if (mState == STATE_ZOOM) { //自己处理手势

                int pointerCount = event.getPointerCount();
                if (pointerCount <= 1 ||
                        (pointerCount == 2 && (action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL))) {
                    backToOri();
                } else {
                    float x1 = event.getX(0);
                    float y1 = event.getY(0);
                    float x2 = event.getX(1);
                    float y2 = event.getY(1);

                    //处理图片的移动
                    float halfX = (x1 + x2) * 0.5f - mLastCenter.x;
                    float halfY = (y1 + y2) * 0.5f - mLastCenter.y;
                    mOriView.setTranslationX(halfX);
                    mOriView.setTranslationY(halfY);

                    //处理图片的放大缩小
                    double distence = getDistance(x1, y1, x2, y2);
                    double scaleFactor = distence / mLastDistance;
                    mOriView.setScaleX((float) scaleFactor);
                    mOriView.setScaleY((float) scaleFactor);
                }
                return true;
            } else if (mState == STATE_RESTORE) {//返回原位置中
                //nothing
                return true;
            } else {
                if (action == MotionEvent.ACTION_DOWN) {
                    moveX = mDonwX = event.getX();
                    moveY = mDownY = event.getY();
                    requestDisallowInterceptTouchEvent(true);
                    return true;
                } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
                    float x1 = event.getX();
                    float y1 = event.getY();
                    if (Math.abs(x1 - mDonwX) < 8 && Math.abs(y1 - mDownY) < 8) { //手指抖动的阈值

                        if (mOriView == null) {
                            mOriView = getTargetView();
                        }

                        mOriView.getLocationOnScreen(mOriTopLeft);
                        mState = STATE_READY; //双指按下, 并且双指按下的条目中有imageview, 并且双指的点都在imageview的区域中
                    }
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float x1 = event.getX();
                    float y1 = event.getY();
                    Log.e("------>", "x1 - moveX:" + (x1 - moveX) + " y1 - moveY:" + (y1 - moveY));
                    if (mState == STATE_DEFAULT) {
                        if (Math.abs(x1 - moveX) < 2 && Math.abs(y1 - moveY) < 2 || Math.abs(y1 - moveY) > 15) {
                            requestDisallowInterceptTouchEvent(false);
                        }
                    }
                    moveX = x1;
                    moveY = y1;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public View getTargetView() {
        if (mOriView == null) {
            mOriView = getChildAt(0);
        }
        return mOriView;
    }


    private double getDistance(float x1, float y1, float x2, float y2) {
        float deltaX = Math.abs(x1 - x2);
        float deltaY = Math.abs(y1 - y2);
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }


    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ZoomFullScreenLayout can host only one direct child");
        }

        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ZoomFullScreenLayout can host only one direct child");
        }

        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ZoomFullScreenLayout can host only one direct child");
        }

        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ZoomFullScreenLayout can host only one direct child");
        }

        super.addView(child, index, params);
    }
}
