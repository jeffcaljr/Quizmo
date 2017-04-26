package com.example.jeff.viewpagerdelete.QuizStatistics.ActivityControllers;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.GradedQuiz.GradedQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

import android.view.MenuItem;
import org.json.JSONException;
import org.json.JSONObject;

public class StatisticsActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  public static final String TAG = "StatisticsActivity";

  public static final String INTENT_EXTRA_GROUP_QUIZ = "INTENT_EXTRA_GROUP_QUIZ";
  public static final String INTENT_EXTRA_INDIVIDUAL_QUIZ = "INTENT_EXTRA_INDIVIDUAL_QUIZ";

  private TextView individualQuizStatsTextView;
  private TextView groupQuizStatsTextView;

  private GradedGroupQuiz gradedGroupQuiz;
  private GradedQuiz gradedQuiz;
    private Quiz individualQuiz;

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

    individualQuizStatsTextView = (TextView) findViewById(R.id.stats_individual_textview);
    groupQuizStatsTextView = (TextView) findViewById(R.id.stats_group_tv);

    individualQuizStatsTextView.setMovementMethod(new ScrollingMovementMethod());
    groupQuizStatsTextView.setMovementMethod(new ScrollingMovementMethod());

    Bundle extras = getIntent().getExtras();

    if (extras != null && extras.containsKey(INTENT_EXTRA_INDIVIDUAL_QUIZ) && extras
        .containsKey(INTENT_EXTRA_GROUP_QUIZ)) {
      gradedGroupQuiz = (GradedGroupQuiz) extras.getSerializable(INTENT_EXTRA_GROUP_QUIZ);
        individualQuiz = (Quiz) extras.getSerializable(INTENT_EXTRA_INDIVIDUAL_QUIZ);
        gradedQuiz = GradedQuizPersistence.sharedInstance(this).readGradedQuizFromDatabase(individualQuiz.getId(), UserDataSource.getInstance().getUser().getUserID());
    } else {
      Log.e(TAG, "Expected graded group and individual quizzes");
    }

    try {
      individualQuizStatsTextView.setText(new JSONObject(gradedQuiz.toJSON()).toString(4));
      groupQuizStatsTextView.setText(new JSONObject(gradedGroupQuiz.toJSON()).toString(4));
    } catch (JSONException e) {
      e.printStackTrace();
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
}
