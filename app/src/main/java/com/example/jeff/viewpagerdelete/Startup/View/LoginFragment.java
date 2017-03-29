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
import android.widget.EditText;

import com.example.jeff.viewpagerdelete.R;

/**
 * Created by jamy on 3/22/17.
 */

public class LoginFragment extends Fragment {

    private Button loginButton;
    private LoginClickListener mLoginClickListener;

    public interface LoginClickListener{
        public void onLoginClick();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_login_fragment, container, false);

        EditText uidField = (EditText) view.findViewById(R.id.loginUserName);
        EditText passwordField = (EditText) view.findViewById(R.id.loginPassword);
        loginButton = (Button) view.findViewById(R.id.loginSubmit);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mLoginClickListener.onLoginClick();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity) {
            a = (Activity) context;
            mLoginClickListener = (LoginClickListener) a;
        }
    }


}
