package com.example.jeff.viewpagerdelete.Startup.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jeff.viewpagerdelete.R;

/**
 * Created by jamy on 3/28/17.
 */

public class WelcomeCheckFragment extends Fragment {

//    public interface WelcomeClickListeners{
//        public void onNotMeClick();
//        public void onIsMeClick();
//    }
//
//    Button isMeButton;
//    Button notMeButton;
//    WelcomeClickListeners mWelcomeClickListeners;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.start_welcome_fragment, container, false);
//
//        final TextView welcomeText = (TextView) view.findViewById(R.id.welcome_back);
//
//        welcomeText.setText("Welcome Back\n" + getArguments().getString("F_NAME") +
//                " " + getArguments().getString("L_NAME"));
//        isMeButton = (Button) view.findViewById(R.id.isMeButton);
//        notMeButton = (Button) view.findViewById(R.id.notMeButton);
//
//        isMeButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                mWelcomeClickListeners.onIsMeClick();
//            }
//        });
//        notMeButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                mWelcomeClickListeners.onNotMeClick();
//            }
//        });
//        return view;
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        Activity a;
//
//        if (context instanceof Activity) {
//            a = (Activity) context;
//            mWelcomeClickListeners = (WelcomeClickListeners) a;
//        }
//    }
}
