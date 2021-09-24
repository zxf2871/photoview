package com.b8a3.photoview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.chrisbanes.photoview.PhotoView;

public class FullScreenImageLayout extends PhotoView {


    final private ImageView mImage = new ImageView(getContext());

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
        this.setOnClickListener(v -> showViewFullScreen(v));
    }


    public void showViewFullScreen(View view) {

        final WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        final RelativeLayout relativeLayout = new RelativeLayout(getContext());
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        layoutParams.format = PixelFormat.RGBA_8888;//让背景透明，放大过程可以看到当前界面
        layoutParams.verticalMargin = 0;
        windowManager.addView(relativeLayout, layoutParams);

        final PhotoView animationIV = new PhotoView(getContext());
        animationIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.addView(animationIV, params);
        relativeLayout.setFocusableInTouchMode(true);

        animationIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(relativeLayout);
            }
        });

        relativeLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (null != windowManager && null != relativeLayout) {
                        windowManager.removeView(relativeLayout);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
            backToNormal();
        }
        return super.onTouchEvent(event);
    }

    private void backToNormal() {

    }
}
