package com.example.jeff.viewpagerdelete.Startup.ActivityControllers;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.Miscellaneous.EditTextFocusChangeListener;
import com.example.jeff.viewpagerdelete.Homepage.ActivityControllers.HomePageActivity;
import com.example.jeff.viewpagerdelete.Miscellaneous.LoadingFragment;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDbHelper;
import com.example.jeff.viewpagerdelete.Startup.Model.User;
import com.example.jeff.viewpagerdelete.Startup.Networking.UserNetworkingService;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

import static com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods.PushUser;

public class LoginActivity extends AppCompatActivity implements
    UserNetworkingService.UserFetcherListener {

    private static final String EXTRA_USERNAME_SAVED_INSTANCE_STATE = "EXTRA_USERNAME_SAVED_INSTANCE_STATE";
    private static final String EXTRA_PASSWORD_SAVED_INSTANCE_STATE = "EXTRA_PASSWORD_SAVED_INSTANCE_STATE";

    private User user;

  private UserNetworkingService userNetworkingService;

    private VideoView videoView;
    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    private RelativeLayout rootLayout;
    private RelativeLayout loginTopLayout;

    private Animation slideFromRight;


    private UserDbHelper dbHelper;
    private SQLiteDatabase db;

    private LoadingFragment authenticatingFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = (EditText) findViewById(R.id.user_id_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        loginButton = (Button) findViewById(R.id.login_button);
        rootLayout = (RelativeLayout) findViewById(R.id.login_root_layout);
        loginTopLayout = (RelativeLayout) findViewById(R.id.login_top_layout);

        slideFromRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);


        loginTopLayout.startAnimation(slideFromRight);


        setTypefaces();


        //handle case where user rotated device after entering data into text fields
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(EXTRA_USERNAME_SAVED_INSTANCE_STATE)){
                usernameField.setText(savedInstanceState.getString(EXTRA_USERNAME_SAVED_INSTANCE_STATE));
            }

            if(savedInstanceState.containsKey(EXTRA_PASSWORD_SAVED_INSTANCE_STATE)){
                passwordField.setText(EXTRA_PASSWORD_SAVED_INSTANCE_STATE);
            }
        }

      userNetworkingService = new UserNetworkingService(this);
      final UserNetworkingService.UserFetcherListener listener = this;

        dbHelper = new UserDbHelper(this);
        db = dbHelper.getWritableDatabase();

        authenticatingFragment = new LoadingFragment(this, "Authenticating");


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usernameField.getText().toString().trim() != ""){
                    String userID = usernameField.getText().toString().trim();

                    authenticatingFragment.show();

                    loginButton.setEnabled(false);
                  userNetworkingService.downloadUser(listener, userID);

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

        if (!videoView.isPlaying()) {
            videoView.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.stopPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
        videoView.suspend();
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

        usernameField.setTypeface(regularFace);
        passwordField.setTypeface(regularFace);
        loginButton.setTypeface(regularFace);

    }

    @Override
    public void userDownloadSuccess(User user) {
        this.user = user;

        UserDataSource.getInstance().setUser(user);


        PushUser(user, db);

      Intent i = new Intent(this, HomePageActivity.class);
        startActivity(i);
        authenticatingFragment.dismiss();
        finish();
    }

    @Override
    public void userDownloadFailure(VolleyError error) {
        loginButton.setEnabled(true);
        authenticatingFragment.dismiss();
//        Snackbar snackbar = Snackbar.make(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), Html.fromHtml("<strong color=\"#ffffff\">Error authenticating user</strong>"), Snackbar.LENGTH_SHORT);
//        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.jccolorPrimary));
        Snackbar snackbar = Snackbar.make(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), "Error authenticating user.", Snackbar.LENGTH_SHORT);

        snackbar.show();
    }
}
