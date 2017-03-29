package com.example.jeff.viewpagerdelete.GroupQuiz.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.jeff.viewpagerdelete.R;

/**
 * Created by Joshua on 3/24/2017.
 */

public class GroupQuizCodeFragment extends Fragment {
    private static final String ARG_SSO_ID = "sso_id";

    private EditText mCodeEditText;
    private Button mEnterButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_code, container, false);

//        mCodeEditText = (EditText) view.findViewById(R.id.);
//        mCodeEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        mEnterButton = (Button) view.findViewById(R.id.enter_code);
        mEnterButton.setOnClickListener(enterButtonPressed);

        return view;
    }

    View.OnClickListener enterButtonPressed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
}
