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

public class NewUserFragment extends Fragment {

    private Button saveButton;
    private SaveUserClickListener mSaveUserClickListener;

    public interface SaveUserClickListener{
        public void onSaveUserClick();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_user_fragment, container, false);

        EditText uidField = (EditText) view.findViewById(R.id.loginUserName);
        EditText passwordField = (EditText) view.findViewById(R.id.loginPassword);
        saveButton = (Button) view.findViewById(R.id.loginSubmit);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSaveUserClickListener.onSaveUserClick();
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
            mSaveUserClickListener = (SaveUserClickListener) a;
        }
    }


}

