package com.example.jeff.viewpagerdelete.Miscellaneous;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Display;

/**
 * Created by Jeff on 4/20/17.
 */

public class DrawableScaler {

  private Context context;

  public DrawableScaler(Context context) {

  }

  public Drawable getResizedDrawable(Drawable image) {
    Bitmap b = ((BitmapDrawable) image).getBitmap();
    Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 50, 50, false);
    return new BitmapDrawable(context.getResources(), bitmapResized);
  }

  public Drawable getResizedDrawable(int resId) {
    Drawable image = ContextCompat.getDrawable(context, resId);
    Bitmap b = ((BitmapDrawable) image).getBitmap();
    Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 50, 50, false);
    return new BitmapDrawable(context.getResources(), bitmapResized);
  }

}
