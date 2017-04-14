package com.example.jeff.viewpagerdelete.Startup.ActivityControllers;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jeff.viewpagerdelete.Homepage.ActivityControllers.HomeActivity;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDbHelper;
import com.example.jeff.viewpagerdelete.Startup.Model.User;
import com.example.jeff.viewpagerdelete.Startup.View.LoginFragment;
import com.example.jeff.viewpagerdelete.Startup.View.NewUserFragment;
import com.example.jeff.viewpagerdelete.Startup.View.WelcomeCheckFragment;

import static com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods.PullUserInfo;
import static com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods.PushUser;


public class StartUpActivity extends AppCompatActivity implements LoginFragment.LoginClickListener, NewUserFragment.SaveUserClickListener, WelcomeCheckFragment.WelcomeClickListeners {
    Fragment mUIFragment;
    int fragTransaction;
    User mStartModel;
    UserDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        mDbHelper = new UserDbHelper(this);

        if (savedInstanceState == null) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            mStartModel = PullUserInfo(db);
            if (mStartModel != null) {
                mUIFragment = new WelcomeCheckFragment();
                Bundle args = new Bundle();
                args.putString("F_NAME", mStartModel.getFirstName());
                args.putString("L_NAME", mStartModel.getLastName());
                mUIFragment.setArguments(args);
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
        Intent quizMe = new Intent(this, HomeActivity.class);
        startActivity(quizMe);
        finish();
    }

    @Override
    public void onSaveUserClick (String uid, String fName, String lName){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mStartModel = new User();
        mStartModel.set_id(uid);
        mStartModel.setFirstName(fName);
        mStartModel.setLastName(lName);
        PushUser(mStartModel, db);
        Intent quizMe = new Intent(this, HomeActivity.class);
        startActivity(quizMe);
        finish();
    }

    @Override
    public void onNotMeClick() {
        mUIFragment = new NewUserFragment();
        fragTransaction = getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.startup_container, mUIFragment)
                .commit();
    }

    @Override
    public void onIsMeClick() {
        Intent quizMe = new Intent(this, HomeActivity.class);
        startActivity(quizMe);
        finish();
    }
}


