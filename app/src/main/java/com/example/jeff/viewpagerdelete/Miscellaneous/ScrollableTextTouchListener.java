package com.example.jeff.viewpagerdelete.Miscellaneous;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

/**
 * Created by Jeff on 4/23/17.
 */

public class ScrollableTextTouchListener implements OnTouchListener {

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    boolean isLarger;

    isLarger = ((TextView) v).getLineCount()
        * ((TextView) v).getLineHeight() > v.getHeight();
    if (event.getAction() == MotionEvent.ACTION_MOVE
        && isLarger) {
      v.getParent().requestDisallowInterceptTouchEvent(true);

    } else {
      v.getParent().requestDisallowInterceptTouchEvent(false);

    }
    return false;
  }
}
