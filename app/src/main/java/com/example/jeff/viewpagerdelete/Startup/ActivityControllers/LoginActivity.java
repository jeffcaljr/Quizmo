package com.example.jeff.viewpagerdelete.Startup.ActivityControllers;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.Homepage.ActivityControllers.HomeActivity;
import com.example.jeff.viewpagerdelete.LoadingFragment;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Model.User;
import com.example.jeff.viewpagerdelete.Startup.Networking.UserFetcher;
import com.example.jeff.viewpagerdelete.Startup.UserDataSource;

public class LoginActivity extends AppCompatActivity implements UserFetcher.UserFetcherListener {

    private static final String EXTRA_USERNAME_SAVED_INSTANCE_STATE = "EXTRA_USERNAME_SAVED_INSTANCE_STATE";
    private static final String EXTRA_PASSWORD_SAVED_INSTANCE_STATE = "EXTRA_PASSWORD_SAVED_INSTANCE_STATE";

    private User user;

    private UserFetcher userFetcher;

    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    private RelativeLayout rootLayout;

    private LoadingFragment loadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userFetcher = new UserFetcher(this);

        final UserFetcher.UserFetcherListener listener = this;

        usernameField = (EditText) findViewById(R.id.user_id_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        loginButton = (Button) findViewById(R.id.login_button);

        rootLayout = (RelativeLayout) findViewById(R.id.login_root_layout);

        loadingFragment = new LoadingFragment(this, "Authenticating");

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(EXTRA_USERNAME_SAVED_INSTANCE_STATE)){
                usernameField.setText(savedInstanceState.getString(EXTRA_USERNAME_SAVED_INSTANCE_STATE));
            }

            if(savedInstanceState.containsKey(EXTRA_PASSWORD_SAVED_INSTANCE_STATE)){
                passwordField.setText(EXTRA_PASSWORD_SAVED_INSTANCE_STATE);
            }
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usernameField.getText().toString().trim() != ""){
                    String userID = usernameField.getText().toString();

                    loadingFragment.show();

                    loginButton.setEnabled(false);
                    userFetcher.downloadUser(listener, userID);

                }
            }
        });

        setTypefaces();

//        TokenCodeFragment tokenCodeFragment = new TokenCodeFragment(this);
//        tokenCodeFragment.show();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        if(username != null && !username.isEmpty()){
            outState.putString(EXTRA_USERNAME_SAVED_INSTANCE_STATE, username);
        }
        if(password != null && !password.isEmpty()){
            outState.putString(EXTRA_PASSWORD_SAVED_INSTANCE_STATE, password);
        }

    }

    private void setTypefaces(){
        //set type face of views
        Typeface regularFace = Typeface.createFromAsset(getAssets(),"fonts/robotoRegular.ttf");
        Typeface boldFace = Typeface.createFromAsset(getAssets(),"fonts/robotoBold.ttf");
        Typeface italicFace = Typeface.createFromAsset(getAssets(),"fonts/robotoItalic.ttf");


        usernameField.setTypeface(regularFace);
        passwordField.setTypeface(regularFace);
        loginButton.setTypeface(regularFace);

    }

    @Override
    public void userDownloadSuccess(User user) {
        this.user = user;

        UserDataSource.getInstance(user);

        Intent i = new Intent(this, HomeActivity.class);
//        i.putExtra(HomeActivity.EXTRA_USER, this.user);
        startActivity(i);
        loadingFragment.dismiss();
        finish();
    }

    @Override
    public void userDownloadFailure(VolleyError error) {
        loginButton.setEnabled(true);
        loadingFragment.dismiss();
        Snackbar.make(rootLayout, "Error authenticating user", Snackbar.LENGTH_SHORT).show();

    }
}
