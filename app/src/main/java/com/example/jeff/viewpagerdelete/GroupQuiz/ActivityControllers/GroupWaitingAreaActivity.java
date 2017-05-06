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
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GroupMemberStatus;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService.GroupQuizProgressDownloadCallback;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService.SingleGroupDownloadCallback;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupStatusPollingService;
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
    private boolean isGroupLeader;

    private TextView courseNameTextView;


    private GroupNetworkingService groupNetworkingService;

    private UserDbHelper userDbHelper;
    private SQLiteDatabase userDB;

    private LocalBroadcastManager bManager;

    //This activity will listen for Group Status Updates
    public static final String RECEIVE_GROUP_STATUS = "com.example.jeff.viewpagerdelete.RECEIVE_GROUP_STATUS";

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RECEIVE_GROUP_STATUS)) {
                ArrayList<GroupMemberStatus> statuses = (ArrayList<GroupMemberStatus>) intent.getSerializableExtra("json");
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

        getSupportActionBar().setTitle("Group Waiting Queue");


        manager = getSupportFragmentManager();
        groupNetworkingService = new GroupNetworkingService(this);

        userDbHelper = new UserDbHelper(this);
        userDB = userDbHelper.getWritableDatabase();

        //Download the group the user belongs to, so that the member statuses can be listed in waiting room fragment

        loadGroupForUser();


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

        userNameTextView.setText("@" + UserDataSource.getInstance().getUser().getUserID());

        fullNameTextView.setText(UserDataSource.getInstance().getUser().getFirstName() + " " + UserDataSource.getInstance().getUser().getLastName());

        emailTextView.setText(UserDataSource.getInstance().getUser().getEmail());
        courseNameTextView.setText(course.getName());


        //listen for group status updates
        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_GROUP_STATUS);
        bManager.registerReceiver(bReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //free FB
        userDB.close();

        //stop listening for group status updates
        bManager.unregisterReceiver(bReceiver);

        GroupStatusPollingService.setServiceAlarm(this, false, group, course, quiz);
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

        if (id == R.id.nav_logout) {
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

    private void loadGroupForUser() {
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
                                        "Failed to load group", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Retry", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                loadGroupForUser();
                                            }
                                        }).show();

                            }
                        });
    }

    //MARK: OnGroupQuizStartedListener Method Implementations


    @Override
    public void onGroupQuizStarted() {

        //when group quiz start button is pressed (in fragment): load the group's quiz progress (if any), then go to group quiz activity


        groupNetworkingService.getGroupQuizProgress(quiz, group, new GroupQuizProgressDownloadCallback() {
                    @Override
                    public void onGroupQuizProgressSuccess(GradedGroupQuiz gradedGroupQuiz) {
                        Intent i = new Intent(GroupWaitingAreaActivity.this, GroupQuizActivity.class);
                        i.putExtra(GroupQuizActivity.INTENT_EXTRA_QUIZ, quiz);
                        i.putExtra(GroupQuizActivity.INTENT_EXTRA_GROUP, group);
                        i.putExtra(GroupQuizActivity.INTENT_EXTRA_GROUP_QUIZ_PROGRESS, gradedGroupQuiz);
                        i.putExtra(GroupQuizActivity.INTENT_EXTRA_IS_LEADER, isGroupLeader);
                        i.putExtra(GroupQuizActivity.INTENT_EXTRA_COURSE, course);
                        startActivity(i);

                        finish();

                    }

                    @Override
                    public void onGroupQuizProgressFailure(VolleyError error) {

                        if (error instanceof NoConnectionError) {
                            Toast.makeText(GroupWaitingAreaActivity.this, "No network connection", Toast.LENGTH_LONG).show();
                            Snackbar.make(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0),
                                    "No network connection", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            onGroupQuizStarted();
                                        }
                                    }).show();
                        }
                        Intent i = new Intent(GroupWaitingAreaActivity.this, GroupQuizActivity.class);
                        i.putExtra(GroupQuizActivity.INTENT_EXTRA_QUIZ, quiz);
                        i.putExtra(GroupQuizActivity.INTENT_EXTRA_GROUP, group);
                        i.putExtra(GroupQuizActivity.INTENT_EXTRA_IS_LEADER, isGroupLeader);
                        i.putExtra(GroupQuizActivity.INTENT_EXTRA_COURSE, course);
                        startActivity(i);

                        finish();

                    }
                });
    }

    @Override
    public void onLeaderFound(boolean isCurrentUserLeader) {

        this.isGroupLeader = isCurrentUserLeader;
    }
}
