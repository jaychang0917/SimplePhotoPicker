package com.jaychang.widget.spp;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;

public class AnimUtils {

  public static void scaleIn(View view) {
    view.animate().scaleX(0f).scaleY(0f).setDuration(0).start();
    view.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
  }

  public static ObjectAnimator getReboundAnimation(View view) {
    PropertyValuesHolder cellScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.95f);
    PropertyValuesHolder cellScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.95f);
    ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, cellScaleX, cellScaleY);
    anim.setRepeatCount(1);
    anim.setRepeatMode(ValueAnimator.REVERSE);
    anim.setDuration(100);
    return anim;
  }
}
