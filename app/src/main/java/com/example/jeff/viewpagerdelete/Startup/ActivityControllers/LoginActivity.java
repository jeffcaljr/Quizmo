package com.example.jeff.viewpagerdelete.Startup.ActivityControllers;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.EditTextFocusChangeListener;
import com.example.jeff.viewpagerdelete.Homepage.ActivityControllers.HomeActivity;
import com.example.jeff.viewpagerdelete.LoadingFragment;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDbHelper;
import com.example.jeff.viewpagerdelete.Startup.Model.User;
import com.example.jeff.viewpagerdelete.Startup.Networking.UserFetcher;
import com.example.jeff.viewpagerdelete.Startup.UserDataSource;
import com.example.jeff.viewpagerdelete.Startup.View.NewUserFragment;
import com.example.jeff.viewpagerdelete.Startup.View.WelcomeCheckFragment;

import static com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods.PullUserInfo;
import static com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods.PushUser;
//import static com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods.UpdateUser;

public class LoginActivity extends AppCompatActivity implements UserFetcher.UserFetcherListener {

    private static final String EXTRA_USERNAME_SAVED_INSTANCE_STATE = "EXTRA_USERNAME_SAVED_INSTANCE_STATE";
    private static final String EXTRA_PASSWORD_SAVED_INSTANCE_STATE = "EXTRA_PASSWORD_SAVED_INSTANCE_STATE";

    private User user;

    private UserFetcher userFetcher;

    private VideoView videoView;
    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    private RelativeLayout rootLayout;

    private UserDbHelper dbHelper;
    private SQLiteDatabase db;

    private LoadingFragment authenticatingFragment;
    private LoadingFragment loadingFragment;

    private boolean isAutoAuthenticating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = (EditText) findViewById(R.id.user_id_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        loginButton = (Button) findViewById(R.id.login_button);
        rootLayout = (RelativeLayout) findViewById(R.id.login_root_layout);

        setTypefaces();

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(EXTRA_USERNAME_SAVED_INSTANCE_STATE)){
                usernameField.setText(savedInstanceState.getString(EXTRA_USERNAME_SAVED_INSTANCE_STATE));
            }

            if(savedInstanceState.containsKey(EXTRA_PASSWORD_SAVED_INSTANCE_STATE)){
                passwordField.setText(EXTRA_PASSWORD_SAVED_INSTANCE_STATE);
            }
        }

        userFetcher = new UserFetcher(this);
        final UserFetcher.UserFetcherListener listener = this;

        dbHelper = new UserDbHelper(this);
        db = dbHelper.getWritableDatabase();

        authenticatingFragment = new LoadingFragment(this, "Authenticating");
        loadingFragment =  new LoadingFragment(this, "Loading");


        //check if user is already saved to the database, and if so, re-download their info

        isAutoAuthenticating = true;
        loadingFragment.show();
        user = PullUserInfo(db);
        if (user != null) {
            userFetcher.downloadUser(listener, user.getUserID());
        } else {
            loadingFragment.dismiss();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usernameField.getText().toString().trim() != ""){
                    String userID = usernameField.getText().toString();

                    authenticatingFragment.show();

                    loginButton.setEnabled(false);
                    userFetcher.downloadUser(listener, userID);

                }
            }
        });

        videoView = (VideoView) findViewById(R.id.login_background_video_view);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sample);
        videoView.setVideoURI(uri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoView.start();

        usernameField.setOnFocusChangeListener(new EditTextFocusChangeListener(rootLayout));


    }


    @Override
    protected void onStart() {
        super.onStart();
        videoView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.stopPlayback();
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

        UserDataSource.getInstance().setUser(user);

        //try to update the user in the database (in the case that they already logged in)
        //if update affects 0 rows, then the user wasnt in the database, and needs to be added to it

//        int updateResult = UpdateUser(user, db);

//        if(updateResult < 1){
            PushUser(user, db);
//        }



        Intent i = new Intent(this, HomeActivity.class);
//        i.putExtra(HomeActivity.EXTRA_USER, this.user);
        startActivity(i);
        loadingFragment.dismiss();
        authenticatingFragment.dismiss();
        finish();
    }

    @Override
    public void userDownloadFailure(VolleyError error) {
        loginButton.setEnabled(true);
        loadingFragment.dismiss();
        authenticatingFragment.dismiss();

        if(isAutoAuthenticating == false){
            Snackbar.make(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), "Error authenticating user", Snackbar.LENGTH_SHORT).show();
        }
        isAutoAuthenticating = false;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db != null){
            db.close();
        }
    }
}
