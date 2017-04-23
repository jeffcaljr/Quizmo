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
import com.example.jeff.viewpagerdelete.Startup.Networking.UserNetworkingService.UserFetcherCallback;

import static com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods.PushUser;

public class Splash extends AppCompatActivity {

    private static final int SPLASH_MINIMUM_DISPLAY_LENGTH = 3000;
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



//       new Handler().postDelayed(new Runnable() {
//           @Override
//           public void run() {
//               //check if user is saved locally (this means the user is signed in)
//               user = UserDBMethods.PullUserInfo(db);
//
//               //if the user is signed in, fetch their current data
//               if (user != null) {
//                   userNetworkingService.downloadUser(listener, user.getUserID());
//               }
//               else{
//                   //no user saved in database; go to login
//                   Intent intent = new Intent(Splash.this, LoginActivity.class);
//                   Splash.this.startActivity(intent);
//                   Splash.this.finish();
//               }
//           }
//       }, SPLASH_MINIMUM_DISPLAY_LENGTH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //check if user is saved locally (this means the user is signed in)
                user = UserDBMethods.PullUserInfo(db);

                //if the user is signed in, fetch their current data
                if (user != null) {
                  userNetworkingService.downloadUser(user.getUserID(), new UserFetcherCallback() {
                    @Override
                    public void userDownloadSuccess(User u) {

                      user = u;

                      //store user info for app-wide use
                      UserDataSource.getInstance().setUser(user);

                      //save user to database (Sign them in)
                      PushUser(user, db);

                      //Go to home screen
                      Intent i = new Intent(Splash.this, HomePageActivity.class);
                      startActivity(i);
                      finish();

                    }

                    @Override
                    public void userDownloadFailure(VolleyError error) {
                      Toast.makeText(Splash.this, "Network error fetching user info",
                          Toast.LENGTH_LONG).show();

                      //If network error encountered for user "signed in", sign them out and go to login
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
