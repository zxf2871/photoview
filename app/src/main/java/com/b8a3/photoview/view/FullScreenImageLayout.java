package com.b8a3.photoview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.b8a3.photoview.R;
import com.b8a3.photoview.zoom.ZoomImageView;

public class FullScreenImageLayout extends FrameLayout implements OnGestureViewListener {


    final FrameLayout mFullScreenContainer = new FrameLayout(getContext());
    final WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

    DrawView scaleHintView;

    private View targetView;

    public FullScreenImageLayout(Context context) {
        this(context, null);
    }

    public FullScreenImageLayout(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public FullScreenImageLayout(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init();
    }

    private void init() {
        FullViewAttacher attacher = new FullViewAttacher(this, this);
    }

    public void showViewFullScreen(final View tView) {

        scaleHintView = new DrawView(getContext());
        scaleHintView.setTarget(tView);

        Rect rectG = new Rect();
        Rect rectL = new Rect();
        tView.getGlobalVisibleRect(rectG);
        tView.getLocalVisibleRect(rectL);

        final FrameLayout.LayoutParams hParams = new FrameLayout.LayoutParams(tView.getWidth(), tView.getHeight() + 200);
        //顶部露出半截
        if (rectL.height() > 0 && rectL.height() < tView.getHeight() && rectL.bottom == tView.getHeight()) {
            hParams.topMargin = rectG.bottom - tView.getHeight();
        } else {
            hParams.topMargin = rectG.top;
        }
        hParams.leftMargin = rectG.left;
        mFullScreenContainer.removeAllViews();
        mFullScreenContainer.addView(scaleHintView, hParams);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags =  WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
//
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
//                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;


        windowManager.addView(mFullScreenContainer, layoutParams);
    }

    private void backToNormal() {
        windowManager.removeView(mFullScreenContainer);
    }

    @Override
    public void onScaleBegin() {
        requestDisallowInterceptTouchEvent(true);
        showViewFullScreen(getChildAt(0));
    }

    @Override
    public void onScaleEnd() {
        requestDisallowInterceptTouchEvent(false);
        backToNormal();
        getChildAt(0).setVisibility(VISIBLE);

    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        Log.e("------>", "scaleFactor:" + scaleFactor + " focusX:" + focusX + " focusY:" + focusY);
        if (scaleHintView != null) {
            scaleHintView.setScaleX(1.5f);
            scaleHintView.setScaleY(1.5f);
        }
    }
}
