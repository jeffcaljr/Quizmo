package com.example.jeff.viewpagerdelete.Startup.ActivityControllers;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.Homepage.ActivityControllers.HomePageActivity;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDbHelper;
import com.example.jeff.viewpagerdelete.Startup.Model.User;
import com.example.jeff.viewpagerdelete.Startup.Networking.UserNetworkingService;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;
import com.example.jeff.viewpagerdelete.Startup.Networking.UserNetworkingService.OnUserDownloadedCallback;

import static com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods.PushUser;

/**
 * Displays splash screen to the user, while application checks to see if there is a currently "authenticated user"
 * If there is a currently authenticated user, the user's data is pulled from the API (refreshed), and the user is taken to homescreen
 * If there is not a currently authenticated user, user is taken to the login screen
 */

public class Splash extends AppCompatActivity {

    /**
     * Splash screen is shown for a minimum amount of time (delayed), to prevent rapid flicker on faster devices
     */
    private static final int SPLASH_MINIMUM_DISPLAY_LENGTH = 2000;

    private User user;
    private UserDbHelper dbHelper;
    private SQLiteDatabase db;
    private UserNetworkingService userNetworkingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        dbHelper = new UserDbHelper(this);
        db = dbHelper.getWritableDatabase();
        userNetworkingService = new UserNetworkingService(this);

    }

    /**
     * Check for authenticated user once the splash screen has appeared on screen
     */
    @Override
    protected void onResume() {
        super.onResume();

        //Display splash screen and animation for specified time, before beginning authentication check

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //check if user is saved locally (this means the user is signed in)
                user = UserDBMethods.PullUserInfo(db);

                //if the user is signed in, fetch their current data from the API
                if (user != null) {
                    userNetworkingService.downloadUser(user.getUserID(), new OnUserDownloadedCallback() {
                        @Override
                        public void userDownloadSuccess(User u) {

                            user = u;

                            /**
                             * Save the user object in the UserDataSource singleton, for use throughout application
                             */
                            UserDataSource.getInstance().setUser(user);

                            /**
                             * save user to database (Sign them in)
                             */
                            PushUser(user, db);

                            //Go to home screen
                            Intent i = new Intent(Splash.this, HomePageActivity.class);
                            startActivity(i);
                            finish();

                        }

                        @Override
                        public void userDownloadFailure(VolleyError error) {
//                            Toast.makeText(Splash.this, "Network error fetching user info",
//                                    Toast.LENGTH_LONG).show();

                            //If network error encountered for user currently "signed in", sign them out and go to login
                            UserDBMethods.ClearUserDB(db);
                            Intent i = new Intent(Splash.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });

                } else {
                    //no user saved in database; go to login
                    Intent intent = new Intent(Splash.this, LoginActivity.class);
                    Splash.this.startActivity(intent);
                    Splash.this.finish();
                }
            }
        }, SPLASH_MINIMUM_DISPLAY_LENGTH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }


}
