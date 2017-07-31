package com.jaychang.widget.spp;

import android.content.Context;

final class Utils {

  public static int dpToPx(Context context, int dp) {
    float density = context.getApplicationContext().getResources().getDisplayMetrics().density;
    return (int) (dp * density);
  }

  public static int getScreenWidthPixels(Context context) {
    return context.getResources().getDisplayMetrics().widthPixels;
  }

}
