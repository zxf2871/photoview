package com.b8a3.photoview.view;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class FullViewAttacher implements View.OnTouchListener,
        View.OnLayoutChangeListener {

    private FullScreenImageLayout mLayout;

    private final ScaleGestureDetector mDetector;
    private final OnGestureViewListener mOnGestureViewListener;

    public FullViewAttacher(FullScreenImageLayout layout, OnGestureViewListener listener) {
        this.mLayout = layout;
        this.mOnGestureViewListener = listener;
        layout.setOnTouchListener(this);
        layout.addOnLayoutChangeListener(this);
        mDetector = new ScaleGestureDetector(layout.getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();

                if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor))
                    return false;

                mOnGestureViewListener.onScale(scaleFactor,
                        detector.getFocusX(), detector.getFocusY());
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
//                layout.requestDisallowInterceptTouchEvent(true);
//                mOnGestureViewListener.onScaleBegin();

                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
        });
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mOnGestureViewListener.onScaleBegin();
        }

        if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
            mOnGestureViewListener.onScaleEnd();
        }
        return mDetector.onTouchEvent(event);
    }
}
