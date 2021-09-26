package com.b8a3.photoview.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

public class FullScreenImageLayout extends FrameLayout {


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
    }

    public void showViewFullScreen(final ImageView targetView1) {

        long l = System.currentTimeMillis();

        final ViewGroup.LayoutParams tarP = targetView1.getLayoutParams();
//        targetView1.setDrawingCacheEnabled(true);

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
        hParams.leftMargin = 100;
        hParams.topMargin = 300;
        mFullScreenContainer.addView(targetView, hParams);

        Log.e("------>", "添加时间:" + (System.currentTimeMillis()-l));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showViewFullScreen((ImageView) getChildAt(0));
                return true;
        }


        if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
            backToNormal();
        }
        return super.onTouchEvent(event);
    }

    private void backToNormal() {
        windowManager.removeView(mFullScreenContainer);
    }
}
