package com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import android.widget.Toast;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuiz;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.UserGroupStatus;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService.GroupQuizProgressDownloadCallback;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService.SingleGroupDownloadCallback;
import com.example.jeff.viewpagerdelete.GroupQuiz.View.GroupWaitingAreaFragment;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.ActivityControllers.LoginActivity;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDbHelper;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

import java.util.ArrayList;

public class GroupWaitingAreaActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
    GroupWaitingAreaFragment.OnGroupQuizStartedListener {

    public static final String EXTRA_COURSE = "EXTRA_COURSE";
    public static final String EXTRA_QUIZ = "EXTRA_GRADED_QUIZ";

    private FragmentManager manager;
    private GroupWaitingAreaFragment groupWaitingAreaFragment;

    private Course course;
    private Quiz quiz;
  private Group group;

    private TextView courseNameTextView;


  private GroupNetworkingService groupNetworkingService;

    private UserDbHelper userDbHelper;
    private SQLiteDatabase userDB;

    private LocalBroadcastManager bManager;

    //Your activity will respond to this action String
    public static final String RECEIVE_JSON = "com.your.package.RECEIVE_JSON";

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RECEIVE_JSON)) {
                ArrayList<UserGroupStatus> statuses = (ArrayList<UserGroupStatus>) intent.getSerializableExtra("json");
                if (manager != null && groupWaitingAreaFragment != null) {
                    groupWaitingAreaFragment.onStatusUpdate(statuses);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_group_waiting_area);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey(EXTRA_COURSE) && extras.containsKey(EXTRA_QUIZ)) {
            course = (Course) extras.getSerializable(EXTRA_COURSE);
            quiz = (Quiz) extras.getSerializable(EXTRA_QUIZ);
        } else {
            Log.e("TAG", "expected required extra \"EXTRA_COURSE\" in GroupWaitingAreaActivity");
            finish();
        }

        manager = getSupportFragmentManager();
      groupNetworkingService = new GroupNetworkingService(this);

        userDbHelper = new UserDbHelper(this);
        userDB = userDbHelper.getWritableDatabase();

      groupNetworkingService
          .downloadGroupForUser(UserDataSource.getInstance().getUser().getUserID(),
              course.getCourseID(), new SingleGroupDownloadCallback() {
                @Override
                public void onDownloadSingleGroupSuccess(Group group) {
                  GroupWaitingAreaActivity.this.group = group;
                    groupWaitingAreaFragment = (GroupWaitingAreaFragment) manager
                            .findFragmentByTag(GroupWaitingAreaFragment.TAG);

                    if (groupWaitingAreaFragment == null) {
                        groupWaitingAreaFragment = new GroupWaitingAreaFragment();
                    Bundle args = new Bundle();
                    args.putSerializable(GroupWaitingAreaFragment.ARG_GROUP, group);
                    args.putSerializable(GroupWaitingAreaFragment.ARG_COURSE, course);
                      args.putSerializable(GroupWaitingAreaFragment.ARG_QUIZ, quiz);

                        groupWaitingAreaFragment.setArguments(args);

                    manager.beginTransaction()
                            .replace(R.id.group_code_fragment_container, groupWaitingAreaFragment,
                                    GroupWaitingAreaFragment.TAG)
                        .commit();

                  }
                }

                @Override
                public void onDownloadSingleGroupFailure(VolleyError error) {
                  Snackbar.make(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0),
                      "Failed to load group", Snackbar.LENGTH_LONG).show();

                }
              });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);


        TextView userNameTextView = (TextView) headerView.findViewById(R.id.header_username);
        TextView fullNameTextView = (TextView) headerView.findViewById(R.id.header_fullname);
        TextView emailTextView = (TextView) headerView.findViewById(R.id.header_email);
        courseNameTextView = (TextView) headerView.findViewById(R.id.header_course_name);

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_JSON);
        bManager.registerReceiver(bReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDB.close();
        bManager.unregisterReceiver(bReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_courses) {
            finish();
        } else if (id == R.id.nav_quizzes) {
            finish();
        } else if (id == R.id.nav_logout) {
            //delete user from db to simulate revoking auth token
            UserDBMethods.ClearUserDB(userDB);
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

  //MARK: OnGroupQuizStartedListener Method Implementations


  @Override
  public void onGroupQuizStarted() {

      groupNetworkingService.getGroupQuizProgress(quiz.getId(), group.getId(),
              quiz.getAssociatedSessionID(), new GroupQuizProgressDownloadCallback() {
          @Override
          public void onGroupQuizProgressSuccess(GradedGroupQuiz gradedGroupQuiz) {
            Intent i = new Intent(GroupWaitingAreaActivity.this, GroupQuizActivity.class);
              i.putExtra(GroupQuizActivity.INTENT_EXTRA_QUIZ, quiz);
            i.putExtra(GroupQuizActivity.INTENT_EXTRA_GROUP, group);
            i.putExtra(GroupQuizActivity.INTENT_EXTRA_GROUP_QUIZ_PROGRESS, gradedGroupQuiz);
            startActivity(i);

          }

          @Override
          public void onGroupQuizProgressFailure(VolleyError error) {

              if (error instanceof NoConnectionError) {
                  Toast.makeText(GroupWaitingAreaActivity.this, "No network connection", Toast.LENGTH_LONG).show();
            }
              Intent i = new Intent(GroupWaitingAreaActivity.this, GroupQuizActivity.class);
              i.putExtra(GroupQuizActivity.INTENT_EXTRA_QUIZ, quiz);
              i.putExtra(GroupQuizActivity.INTENT_EXTRA_GROUP, group);
              startActivity(i);

          }
        });
  }


}
