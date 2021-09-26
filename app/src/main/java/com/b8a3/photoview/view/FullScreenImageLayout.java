package com.b8a3.photoview.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class FullScreenImageLayout extends FrameLayout implements OnGestureViewListener {


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

    public void showViewFullScreen(final View tView) {

        long l = System.currentTimeMillis();

        final ViewGroup.LayoutParams tarP = tView.getLayoutParams();
        tView.setDrawingCacheEnabled(true);

        Rect rectG = new Rect();
        Rect rectL = new Rect();
        tView.getGlobalVisibleRect(rectG);
        tView.getLocalVisibleRect(rectL);

        mFullScreenContainer.removeAllViews();
        ImageView scaleView = new ImageView(getContext());


        scaleView.setImageBitmap(tView.getDrawingCache());
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        layoutParams.format = PixelFormat.RGBA_8888;//让背景透明，放大过程可以看到当前界面
        windowManager.addView(mFullScreenContainer, layoutParams);

        final FrameLayout.LayoutParams hParams = new FrameLayout.LayoutParams(tView.getWidth(), tView.getHeight());


        //顶部露出半截
        if (rectL.height() > 0 && rectL.height() < tView.getHeight() && rectL.bottom == tView.getHeight()) {
            hParams.topMargin = rectG.bottom - tView.getHeight();
        } else {
            hParams.topMargin = rectG.top;
        }
        hParams.leftMargin = rectG.left;
        mFullScreenContainer.addView(scaleView, hParams);
    }

    private void backToNormal() {
        windowManager.removeView(mFullScreenContainer);
    }

    @Override
    public void onScaleBegin() {
        showViewFullScreen(getChildAt(0));
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
