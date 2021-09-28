package com.b8a3.photoview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.b8a3.photoview.view.ZoomFullScreenLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        root = findViewById(R.id.main_container);
    }

    public void clickBt(View view) {
        Toast.makeText(this, "点击了按钮", Toast.LENGTH_SHORT).show();
        ZoomFullScreenLayout layout = new ZoomFullScreenLayout(getApplicationContext());
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.car_);
        layout.addView(imageView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        root.addView(layout);

        findParent(view.getParent());


    }

    private void findParent(ViewParent parent) {
        if (parent == null) {
            return;
        }

        Log.e("------>", parent.toString() + "  is View:" + (parent instanceof View)
                + " has Activity:" + ((parent instanceof View) ? (((View) parent).getContext() instanceof Activity ? "yes" : "no"):"no"));
        findParent(parent.getParent());
    }
}
