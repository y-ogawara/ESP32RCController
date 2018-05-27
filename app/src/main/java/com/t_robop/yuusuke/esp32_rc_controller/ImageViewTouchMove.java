package com.t_robop.yuusuke.esp32_rc_controller;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;




public class ImageViewTouchMove implements View.OnTouchListener  {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        return true;
    }

}
