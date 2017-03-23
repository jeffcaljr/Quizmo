package com.example.jeff.viewpagerdelete;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class StartUpActivity extends AppCompatActivity implements LoginFragment.LoginClickListener{
    Fragment mLoginFragment;
    FragmentTransaction fragTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

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
}


