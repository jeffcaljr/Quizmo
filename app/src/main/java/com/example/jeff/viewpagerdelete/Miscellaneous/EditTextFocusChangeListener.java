package com.example.jeff.viewpagerdelete.Miscellaneous;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * Created by Jeff on 4/17/17.
 */

public class EditTextFocusChangeListener implements View.OnFocusChangeListener {

    private ViewGroup parentView;

    public EditTextFocusChangeListener(ViewGroup parentView) {
        this.parentView = parentView;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(hasFocus) {
            int left = view.getLeft();
            int top = view.getTop();
            int bottom = view.getBottom();
            int keyboardHeight = parentView.getHeight() / 3;

            // if the bottom of edit text is greater than scroll view height divide by 3,
            // it means that the keyboard is visible
            if (bottom > keyboardHeight)  {
                // increase scroll view with padding
                parentView.setPadding(0, 0, 0, keyboardHeight);
                // scroll to the edit text position
                parentView.scrollTo(left, top);
            }
        }
    }
}
