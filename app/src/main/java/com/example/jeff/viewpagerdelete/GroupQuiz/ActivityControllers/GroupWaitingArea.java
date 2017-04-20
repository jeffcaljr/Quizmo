package com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
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

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService;
import com.example.jeff.viewpagerdelete.GroupQuiz.View.GroupWaitingAreaFragment;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.ActivityControllers.LoginActivity;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDbHelper;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

public class GroupWaitingArea extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
    GroupNetworkingService.SingleGroupFetcherListener {

    public static final String EXTRA_COURSE = "EXTRA_COURSE";
    public static final String EXTRA_GRADED_QUIZ = "EXTRA_GRADED_QUIZ";
    public static final String FRAG_TAG_GROUP_QUIZ_CODE_FRAGMENT = "FRAG_TAG_GROUP_QUIZ_CODE_FRAGMENT";

    private FragmentManager manager;
    private GroupWaitingAreaFragment groupQuizCodeFragment;

    private Course course;
    private GradedQuiz quiz;

    private TextView courseNameTextView;


  private GroupNetworkingService groupNetworkingService;

    private UserDbHelper userDbHelper;
    private SQLiteDatabase userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_group_waiting_area);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey(EXTRA_COURSE) && extras.containsKey(EXTRA_GRADED_QUIZ)) {
            course = (Course) extras.getSerializable(EXTRA_COURSE);
            quiz = (GradedQuiz) extras.getSerializable(EXTRA_GRADED_QUIZ);
        } else {
            Log.e("TAG", "expected required extra \"EXTRA_COURSE\" in GroupWaitingAreaActivity");
            finish();
        }

        manager = getSupportFragmentManager();
      groupNetworkingService = new GroupNetworkingService(this);

        userDbHelper = new UserDbHelper(this);
        userDB = userDbHelper.getWritableDatabase();

      groupNetworkingService
          .downloadGroupForUser(this, UserDataSource.getInstance().getUser().getUserID(),
              course.getCourseID());


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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDB.close();
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

    @Override
    public void onDownloadSingleGroupSuccess(Group group) {

        groupQuizCodeFragment = (GroupWaitingAreaFragment) manager.findFragmentByTag(FRAG_TAG_GROUP_QUIZ_CODE_FRAGMENT);

        if (groupQuizCodeFragment == null) {
            groupQuizCodeFragment = new GroupWaitingAreaFragment();
            Bundle args = new Bundle();
            args.putSerializable(GroupWaitingAreaFragment.ARG_GROUP, group);
            args.putSerializable(GroupWaitingAreaFragment.ARG_COURSE, course);
            args.putSerializable(GroupWaitingAreaFragment.ARG_GRADED_QUIZ, quiz);

            groupQuizCodeFragment.setArguments(args);

            manager.beginTransaction()
                    .replace(R.id.group_code_fragment_container, groupQuizCodeFragment, FRAG_TAG_GROUP_QUIZ_CODE_FRAGMENT)
                    .commit();

        }

    }


    @Override
    public void onDownloadSingleGroupFailure(VolleyError error) {
        Snackbar.make(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), "Failed to load group", Snackbar.LENGTH_LONG).show();
    }

}
