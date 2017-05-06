package com.example.jeff.viewpagerdelete.QuizStatistics.Controller;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuiz;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuizQuestion;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.GradedQuiz.GradedQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuizQuestion;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.QuizStatistics.View.StatisticsDetailView.StatisticsDetailFragment;
import com.example.jeff.viewpagerdelete.QuizStatistics.View.StatisticsGroupVsGroupFragment;
import com.example.jeff.viewpagerdelete.QuizStatistics.View.StatisticsMasterView.StatisticsMasterFragment;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.ActivityControllers.LoginActivity;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDbHelper;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class StatisticsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, StatisticsMasterFragment.OnStatisticsQuestionClickedListener {

    public static final String TAG = "StatisticsActivity";

    public static final String INTENT_EXTRA_GROUP_QUIZ = "INTENT_EXTRA_GROUP_QUIZ";
    public static final String INTENT_EXTRA_INDIVIDUAL_QUIZ = "INTENT_EXTRA_INDIVIDUAL_QUIZ";
    public static final String INTENT_EXTRA_COURSE = "INTENT_EXTRA_COURSE";


    private GradedGroupQuiz gradedGroupQuiz;
    private GradedQuiz gradedQuiz;
    private Quiz individualQuiz;
    private Course course;

    private ViewPager mPager;
    private PagerAdapter mAdapter;

    private StatisticsMasterFragment statisticsMasterFragment;
    private StatisticsGroupVsGroupFragment groupVsGroupFragment;
    private FragmentManager fragmentManager;

    private UserDbHelper userDbHelper;
    private SQLiteDatabase userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey(INTENT_EXTRA_INDIVIDUAL_QUIZ) && extras
                .containsKey(INTENT_EXTRA_GROUP_QUIZ) &&
                extras.containsKey(INTENT_EXTRA_COURSE)) {
            gradedGroupQuiz = (GradedGroupQuiz) extras.getSerializable(INTENT_EXTRA_GROUP_QUIZ);
            individualQuiz = (Quiz) extras.getSerializable(INTENT_EXTRA_INDIVIDUAL_QUIZ);
            course = (Course) extras.getSerializable(INTENT_EXTRA_COURSE);
            gradedQuiz = GradedQuizPersistence.sharedInstance(this).readGradedQuizFromDatabase(individualQuiz.getId(), UserDataSource.getInstance().getUser().getUserID());
        } else {
            Log.e(TAG, "Expected graded group and individual quizzes");
        }

        userDbHelper = new UserDbHelper(this);
        userDB = userDbHelper.getWritableDatabase();


        fragmentManager = getSupportFragmentManager();

        mPager = (ViewPager) findViewById(R.id.pager);

//        statisticsMasterFragment = (StatisticsMasterFragment) fragmentManager.findFragmentByTag(StatisticsMasterFragment.TAG);

//        if (statisticsMasterFragment == null) {
//            statisticsMasterFragment = new StatisticsMasterFragment();
//            Bundle args = new Bundle();
//            args.putSerializable(StatisticsMasterFragment.ARG_EXTRA_QUIZ, individualQuiz);
//            args.putSerializable(StatisticsMasterFragment.ARG_EXTRA_GRADED_INDIVIDUAL_QUIZ, gradedQuiz);
//            args.putSerializable(StatisticsMasterFragment.ARG_EXTRA_GRADED_GROUP_QUIZ, gradedGroupQuiz);
//            statisticsMasterFragment.setArguments(args);
//
//            fragmentManager.beginTransaction()
//                    .replace(R.id.statistics_container, statisticsMasterFragment, StatisticsMasterFragment.TAG)
//                    .commit();
//
//        }

        statisticsMasterFragment = new StatisticsMasterFragment();
            Bundle args = new Bundle();
            args.putSerializable(StatisticsMasterFragment.ARG_EXTRA_QUIZ, individualQuiz);
            args.putSerializable(StatisticsMasterFragment.ARG_EXTRA_GRADED_INDIVIDUAL_QUIZ, gradedQuiz);
            args.putSerializable(StatisticsMasterFragment.ARG_EXTRA_GRADED_GROUP_QUIZ, gradedGroupQuiz);
            statisticsMasterFragment.setArguments(args);


        groupVsGroupFragment = (StatisticsGroupVsGroupFragment) new StatisticsGroupVsGroupFragment();
        Bundle groupVsGroupArgs = new Bundle();
        groupVsGroupArgs.putSerializable(StatisticsGroupVsGroupFragment.ARG_QUIZ, individualQuiz);
        groupVsGroupArgs.putSerializable(StatisticsGroupVsGroupFragment.ARG_COURSE, course);
        groupVsGroupFragment.setArguments(groupVsGroupArgs);


        mPager.setAdapter(new PagerAdapter(fragmentManager));


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
        TextView courseNameTextView = (TextView) headerView.findViewById(R.id.header_course_name);


        userNameTextView.setText("@" + UserDataSource.getInstance().getUser().getUserID());

        fullNameTextView.setText(UserDataSource.getInstance().getUser().getFirstName() + " " + UserDataSource.getInstance().getUser().getLastName());

        emailTextView.setText(UserDataSource.getInstance().getUser().getEmail());
        courseNameTextView.setText(course.getName());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDB.close();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mPager.getCurrentItem() > 0) {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.statistics, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            if (id == R.id.nav_logout) {
                //delete user from db to simulate revoking auth token
                UserDBMethods.ClearUserDB(userDB);
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //MARK: OnStatisticsQuestionClickedListener method implementations


    @Override
    public void onQuestionClicked(int position, QuizQuestion questionClicked, GradedQuizQuestion individualQuizQuestion, GradedGroupQuizQuestion groupQuizQuestion) {

        StatisticsDetailFragment detailFragment = (StatisticsDetailFragment) fragmentManager.findFragmentByTag(StatisticsDetailFragment.TAG);

        if (detailFragment == null) {
            detailFragment = new StatisticsDetailFragment();
            Bundle args = new Bundle();
            args.putInt(StatisticsDetailFragment.ARG_EXTRA_QUESTION_NUMBER, position);
            args.putSerializable(StatisticsDetailFragment.ARG_EXTRA_QUIZ_QUESTION, questionClicked);
            args.putSerializable(StatisticsDetailFragment.ARG_EXTRA_GRADED_QUESTION_INDIVIDUAL, individualQuizQuestion);
            args.putSerializable(StatisticsDetailFragment.ARG_EXTRA_GRADED_QUESTION_GROUP, groupQuizQuestion);
            detailFragment.setArguments(args);

            fragmentManager.beginTransaction()
                    .replace(R.id.statistics_container, detailFragment, StatisticsDetailFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return statisticsMasterFragment;
            } else {
                return groupVsGroupFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Your Group";
            } else {
                return "Your Class";
            }
        }
    }


}
