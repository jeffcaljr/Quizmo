package com.example.jeff.viewpagerdelete.Startup.ActivityControllers;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.VideoView;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.Homepage.ActivityControllers.HomePageActivity;
import com.example.jeff.viewpagerdelete.Miscellaneous.LoadingFragment;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDbHelper;
import com.example.jeff.viewpagerdelete.Startup.Model.User;
import com.example.jeff.viewpagerdelete.Startup.Networking.UserNetworkingService;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;
import com.example.jeff.viewpagerdelete.Startup.Networking.UserNetworkingService.OnUserDownloadedCallback;

import static com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods.PushUser;


/**
 * Activity for allowing user to login with credentials
 * Displays background video relevant to application's core purpose
 * TODO: Login video sometimes stutters due to various user interaction
 * TODO: Login video is watermarked and in un-optimal orientation
 */
public class LoginActivity extends AppCompatActivity {

    private static final String EXTRA_USERNAME_SAVED_INSTANCE_STATE = "EXTRA_USERNAME_SAVED_INSTANCE_STATE";
    private static final String EXTRA_PASSWORD_SAVED_INSTANCE_STATE = "EXTRA_PASSWORD_SAVED_INSTANCE_STATE";

    private User user;

    private UserNetworkingService userNetworkingService;

    private VideoView videoView;
    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    private RelativeLayout loginTopLayout;
    private ScrollView videoScrollView;

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
        loginTopLayout = (RelativeLayout) findViewById(R.id.login_top_layout);
        videoScrollView = (ScrollView) findViewById(R.id.video_scrollview);

        //handle case where user rotated device after entering data into text fields
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(EXTRA_USERNAME_SAVED_INSTANCE_STATE)) {
                usernameField.setText(savedInstanceState.getString(EXTRA_USERNAME_SAVED_INSTANCE_STATE));
            }

            if (savedInstanceState.containsKey(EXTRA_PASSWORD_SAVED_INSTANCE_STATE)) {
                passwordField.setText(EXTRA_PASSWORD_SAVED_INSTANCE_STATE);
            }
        }


        //disable scrolling of background video view
        videoScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });


        //have the content for this activity animate into view by sliding in
        slideFromRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
        loginTopLayout.startAnimation(slideFromRight);


        userNetworkingService = new UserNetworkingService(this);

        dbHelper = new UserDbHelper(this);
        db = dbHelper.getWritableDatabase();

        authenticatingFragment = new LoadingFragment(this, "Authenticating");


        //When login button is clicked, send network request to fetch user information
        //If the user successfully authenticated, save their name to local storage (simulates auth token)
        //Then go to the next activity
        //Else, if the authentication failed, display an error message to the user and allow them to retry

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (usernameField.getText().toString().trim() != "") {

                    String userID = usernameField.getText().toString();

                    authenticatingFragment.show();

                    loginButton.setEnabled(false);
                    userNetworkingService.downloadUser(userID, new OnUserDownloadedCallback() {
                        @Override
                        public void userDownloadSuccess(User u) {
                            user = u;

                            /**
                             * Save the user object in the UserDataSource singleton, for use throughout application
                             */
                            UserDataSource.getInstance().setUser(user);

                            PushUser(user, db);

                            Intent i = new Intent(LoginActivity.this, HomePageActivity.class);
                            startActivity(i);
                            authenticatingFragment.dismissWithDelay(500);
                            finish();

                        }

                        @Override
                        public void userDownloadFailure(VolleyError error) {

                            loginButton.setEnabled(true);
                            authenticatingFragment.dismissWithDelay(500);

                            Snackbar snackbar = Snackbar
                                    .make(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0),
                                            "Error authenticating user.", Snackbar.LENGTH_SHORT);

                            snackbar.show();

                        }
                    });

                }
            }
        });


        //Play background video in a loop while user is viewing this activity
        videoView = (VideoView) findViewById(R.id.login_background_video_view);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample);
        videoView.setVideoURI(uri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        videoView.start();

        usernameField.requestFocus();


    }


    /**
     * Start video playback if it is not currently playing
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (!videoView.isPlaying()) {
            videoView.start();
        }
    }


    /**
     * Stop video playback when application is not in the foreground
     * Assumed that this helps conserve memory and keep app from lagging
     */
    @Override
    protected void onStop() {
        super.onStop();
        videoView.stopPlayback();
    }


    /**
     * Stop video loading and playback when activity is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
        videoView.suspend();
    }


    /**
     * If activity will allow for orientation changes; store text currently in credential fields and repopulate those fields on orientation change
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        if (username != null && !username.isEmpty()) {
            outState.putString(EXTRA_USERNAME_SAVED_INSTANCE_STATE, username);
        }
        if (password != null && !password.isEmpty()) {
            outState.putString(EXTRA_PASSWORD_SAVED_INSTANCE_STATE, password);
        }

    }

}
