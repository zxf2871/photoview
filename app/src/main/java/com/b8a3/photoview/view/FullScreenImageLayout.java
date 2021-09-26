package com.b8a3.photoview.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

public class FullScreenImageLayout extends FrameLayout implements OnGestureViewListener{


    final FrameLayout mFullScreenContainer = new FrameLayout(getContext());
    final WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

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
        setBackgroundColor(Color.argb(100, 255, 255, 0));
        FullViewAttacher attacher = new FullViewAttacher(this, this);
    }

    public void showViewFullScreen(final ImageView targetView1) {

        long l = System.currentTimeMillis();

        final ViewGroup.LayoutParams tarP = targetView1.getLayoutParams();
//        targetView1.setDrawingCacheEnabled(true);

        Rect rectG = new Rect();
        targetView1.getGlobalVisibleRect(rectG);

        mFullScreenContainer.removeAllViews();
        PhotoView targetView = new PhotoView(getContext());


        targetView.setImageDrawable(targetView1.getDrawable());
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        layoutParams.format = PixelFormat.RGBA_8888;//让背景透明，放大过程可以看到当前界面
        windowManager.addView(mFullScreenContainer, layoutParams);

        ((ImageView) targetView).setScaleType(ImageView.ScaleType.FIT_CENTER);

        final FrameLayout.LayoutParams hParams = new FrameLayout.LayoutParams(targetView1.getWidth(), targetView1.getHeight());


        if (rectG.bottom - rectG.top < targetView1.getHeight() && rectG.bottom < windowManager.getDefaultDisplay().getHeight()) {
            hParams.topMargin = rectG.bottom - targetView1.getHeight();
        } else {
            hParams.topMargin = rectG.top;
        }
        hParams.leftMargin = rectG.left;
        mFullScreenContainer.addView(targetView, hParams);
    }

    private void backToNormal() {
        windowManager.removeView(mFullScreenContainer);
    }

    @Override
    public void onScaleBegin() {
        showViewFullScreen((ImageView) getChildAt(0));
    }

    @Override
    public void onScaleEnd() {
        backToNormal();

    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        Log.e("------>", "scaleFactor:" + scaleFactor + " focusX:" + focusX + " focusY:" + focusY);
    }
}
