package com.b8a3.photoview;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.ortiz.touchview.TouchImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    private FrameLayout mContainer;

    int mScreenWidth;
    int mScreenHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        mContainer = findViewById(R.id.main_container);
        getScreenSizeOfDevice();
//        initImageView();
    }


    private void getScreenSizeOfDevice() {
        DisplayMetrics dm = getResources().getDisplayMetrics();

        WindowManager windowManager = getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);

        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }




}
