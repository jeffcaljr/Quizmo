package com.example.jeff.viewpagerdelete.Startup.ActivityControllers;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jeff.viewpagerdelete.Homepage.ActivityControllers.HomePageActivity;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Model.User;
import com.example.jeff.viewpagerdelete.Startup.View.LoginFragment;
import com.example.jeff.viewpagerdelete.Startup.View.NewUserFragment;
import com.example.jeff.viewpagerdelete.Startup.View.WelcomeCheckFragment;


public class StartUpActivity extends AppCompatActivity implements LoginFragment.LoginClickListener, NewUserFragment.SaveUserClickListener, WelcomeCheckFragment.WelcomeClickListeners {
    Fragment mUIFragment;
    int fragTransaction;
    User mStartModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        mStartModel = new User(this);

        if (savedInstanceState == null) {
            if (mStartModel.PullUserInfoFromSQL()) {
                mUIFragment = new WelcomeCheckFragment();
            } else {
                mUIFragment = new NewUserFragment();
            }
            fragTransaction = getSupportFragmentManager().beginTransaction()
                    .add(R.id.startup_container, mUIFragment)
                    .commit();
        }
    }


    @Override
    public void onLoginClick() {
        Intent quizMe = new Intent(this, HomePageActivity.class);
        startActivity(quizMe);
        finish();
    }

    @Override
    public void onSaveUserClick(String uid, String scrnName) {
        mStartModel.set_id(uid);
        mStartModel.setFirstName(scrnName);
        mStartModel.PushUserToSQL();
        Intent quizMe = new Intent(this, HomePageActivity.class);
        startActivity(quizMe);
        finish();
    }

    @Override
    public void onNotMeClick() {
        mUIFragment = new NewUserFragment();
        fragTransaction = getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .add(R.id.startup_container, mUIFragment)
                .commit();
    }

    @Override
    public void onIsMeClick() {
        Intent quizMe = new Intent(this, HomePageActivity.class);
        startActivity(quizMe);
        finish();
    }
}


