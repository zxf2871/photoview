package com.b8a3.photoview.view;

public interface OnGestureViewListener {
    void onScaleBegin();

    void onScaleEnd();

    void onScale(float scaleFactor, float focusX, float focusY);

}
