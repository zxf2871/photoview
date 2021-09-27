package com.b8a3.photoview;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mContainer;

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


    public void openUC(View view) {

    }
}
