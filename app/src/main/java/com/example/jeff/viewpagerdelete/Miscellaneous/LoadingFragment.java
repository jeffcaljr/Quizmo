package com.example.jeff.viewpagerdelete.Miscellaneous;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jeff.viewpagerdelete.R;

/**
 * Created by Jeff on 4/12/17.
 */

public class LoadingFragment extends Dialog implements View.OnClickListener {

    private String loadingText;
    private ProgressBar spinner;
    private TextView dialogText;

    public LoadingFragment(@NonNull Context context, String loadingText) {
        super(context);
        this.loadingText = loadingText;
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_fragment_loading);

        spinner = (ProgressBar) findViewById(R.id.progress_spinner);
        dialogText = (TextView) findViewById(R.id.loading_text);

        dialogText.setText(loadingText);

        setTypefaces();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onClick(View view) {

    }

    private void setTypefaces(){
        //set type face of views
//        Typeface regularFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/robotoRegular.ttf");
        Typeface boldFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/robotoBold.ttf");
//        Typeface boldItalicFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/robotoBoldItalic.ttf");

        dialogText.setTypeface(boldFace);

    }
}
