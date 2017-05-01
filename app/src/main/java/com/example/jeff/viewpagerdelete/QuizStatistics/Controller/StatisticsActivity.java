package com.example.jeff.viewpagerdelete.QuizStatistics.Controller;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.GradedQuiz.GradedQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuizQuestion;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.QuizStatistics.View.StatisticsDetailView.StatisticsDetailFragment;
import com.example.jeff.viewpagerdelete.QuizStatistics.View.StatisticsMasterView.StatisticsMasterFragment;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

import android.view.MenuItem;

public class StatisticsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, StatisticsMasterFragment.OnStatisticsQuestionClickedListener {

    public static final String TAG = "StatisticsActivity";

    public static final String INTENT_EXTRA_GROUP_QUIZ = "INTENT_EXTRA_GROUP_QUIZ";
    public static final String INTENT_EXTRA_INDIVIDUAL_QUIZ = "INTENT_EXTRA_INDIVIDUAL_QUIZ";


    private GradedGroupQuiz gradedGroupQuiz;
    private GradedQuiz gradedQuiz;
    private Quiz individualQuiz;

    private StatisticsMasterFragment statisticsMasterFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey(INTENT_EXTRA_INDIVIDUAL_QUIZ) && extras
                .containsKey(INTENT_EXTRA_GROUP_QUIZ)) {
            gradedGroupQuiz = (GradedGroupQuiz) extras.getSerializable(INTENT_EXTRA_GROUP_QUIZ);
            individualQuiz = (Quiz) extras.getSerializable(INTENT_EXTRA_INDIVIDUAL_QUIZ);
            gradedQuiz = GradedQuizPersistence.sharedInstance(this).readGradedQuizFromDatabase(individualQuiz.getId(), UserDataSource.getInstance().getUser().getUserID());
        } else {
            Log.e(TAG, "Expected graded group and individual quizzes");
        }


        fragmentManager = getSupportFragmentManager();

        statisticsMasterFragment = (StatisticsMasterFragment) fragmentManager.findFragmentByTag(StatisticsMasterFragment.TAG);

        if (statisticsMasterFragment == null) {
            statisticsMasterFragment = new StatisticsMasterFragment();
            Bundle args = new Bundle();
            args.putSerializable(StatisticsMasterFragment.ARG_EXTRA_QUIZ, individualQuiz);
            args.putSerializable(StatisticsMasterFragment.ARG_EXTRA_GRADED_INDIVIDUAL_QUIZ, gradedQuiz);
            args.putSerializable(StatisticsMasterFragment.ARG_EXTRA_GRADED_GROUP_QUIZ, gradedGroupQuiz);
            statisticsMasterFragment.setArguments(args);

            fragmentManager.beginTransaction()
                    .replace(R.id.statistics_container, statisticsMasterFragment, StatisticsMasterFragment.TAG)
                    .commit();

        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
}
