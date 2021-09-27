package com.b8a3.photoview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class DrawView extends View {

    public View target;

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTarget(View view) {
        this.target = view;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (target != null) {
            target.draw(canvas);
            post(()->{
                target.setVisibility(INVISIBLE);
            });
            return;
        }
        super.onDraw(canvas);
    }

}
