package com.example.jeff.viewpagerdelete.Startup;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jeff.viewpagerdelete.QuizLoaderActivity;
import com.example.jeff.viewpagerdelete.R;


public class StartUpActivity extends AppCompatActivity implements LoginFragment.LoginClickListener, NewUserFragment.SaveUserClickListener {
    Fragment mLoginFragment;
    FragmentTransaction fragTransaction;
    StartModel mStartModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        mStartModel = new StartModel(this);

        mLoginFragment = new LoginFragment();
        fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.add(R.id.startup_container, mLoginFragment);
        fragTransaction.commit();

    }


    @Override
    public void onLoginClick() {
        Intent quizMe = new Intent(this, QuizLoaderActivity.class);
        startActivity(quizMe);
        finish();
    }

    @Override
    public void onSaveUserClick(String uid, String scrnName) {
        StartModel.User.setUid(uid);
        StartModel.User.setScreenName(scrnName);
        mStartModel.PushUserToSQL();
    }
}


