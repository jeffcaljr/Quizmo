package com.example.jeff.viewpagerdelete.Startup.ActivityControllers;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupFetcher;
import com.example.jeff.viewpagerdelete.Homepage.ActivityControllers.HomePageActivity;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Controller.QuizLoaderActivity;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Model.StartModel;
import com.example.jeff.viewpagerdelete.Startup.View.LoginFragment;
import com.example.jeff.viewpagerdelete.Startup.View.NewUserFragment;

import java.util.ArrayList;


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
        Intent quizMe = new Intent(this, HomePageActivity.class);
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


