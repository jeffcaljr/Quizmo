package com.example.jeff.viewpagerdelete.Homepage.ActivityControllers;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers.GroupWaitingAreaActivity;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuiz;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.Homepage.Database.QuizLoadTask;
import com.example.jeff.viewpagerdelete.Homepage.View.CourseListFragment;
import com.example.jeff.viewpagerdelete.Homepage.View.QuizListFragment;
import com.example.jeff.viewpagerdelete.Homepage.View.StartQuizFragment;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Controller.IndividualQuizActivity;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.Quiz.IndividualQuizDbHelper;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.Quiz.IndividualQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Networking.QuizNetworkingService;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Networking.QuizNetworkingService.UserQuizzesDownloadCallback;
import com.example.jeff.viewpagerdelete.Miscellaneous.LoadingFragment;
import com.example.jeff.viewpagerdelete.QuizStatistics.Controller.StatisticsActivity;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.ActivityControllers.LoginActivity;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDBMethods;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDbHelper;
import com.example.jeff.viewpagerdelete.Homepage.View.TokenCodeFragment;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CourseListFragment.CourseListListener,
        TokenCodeFragment.TokenCodeEntryListener,
        QuizLoadTask.QuizLoadTaskListener, StartQuizFragment.OnQuizStartListener {

    //    public static final String EXTRA_USER = "EXTRA_USER";
    public static final String FRAG_TAG_QUIZ_LIST = "FRAG_TAG_QUIZ_LIST";
    public static final String FRAG_TAG_COURSE_LIST = "FRAG_TAG_COURSE_LIST";

    private ArrayList<Course> courses;

    private FragmentManager manager;
    //    private QuizListFragment quizListFragment;
    private CourseListFragment courseListFragment;
    private LoadingFragment loadingFragment;
    private StartQuizFragment startQuizFragment;

    private TextView courseNameTextView;

    private IndividualQuizDbHelper quizDbHelper;
    private SQLiteDatabase quizDB;

    private String sessionID;


    private QuizNetworkingService quizNetworkingService;
    private GroupNetworkingService groupNetworkingService;

    private UserDbHelper userDbHelper;
    private SQLiteDatabase userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = getSupportFragmentManager();

        userDbHelper = new UserDbHelper(this);
        userDB = userDbHelper.getWritableDatabase();


        quizDbHelper = new IndividualQuizDbHelper(this.getApplicationContext());
        quizDB = quizDbHelper.getWritableDatabase();

        quizNetworkingService = new QuizNetworkingService(this);
        groupNetworkingService = new GroupNetworkingService(this);


        courseListFragment = (CourseListFragment) manager.findFragmentByTag(FRAG_TAG_COURSE_LIST);
        if (courseListFragment == null) {
            courseListFragment = new CourseListFragment();
            Bundle args = new Bundle();
            args.putSerializable(CourseListFragment.ARG_COURSES_COURSE_LIST_FRAGMENT, UserDataSource.getInstance().getUser().getEnrolledCourses());
            courseListFragment.setArguments(args);

            showCoursesFragment();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.setBackgroundColor(ContextCompat.getColor(this, R.color.jccolorAccentBright));
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

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        HomePageActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(
                        HomePageActivity.this.getCurrentFocus().getWindowToken(),
                        0
                );
            }
        });


        userNameTextView.setText("@" + UserDataSource.getInstance().getUser().getUserID());

        fullNameTextView.setText(UserDataSource.getInstance().getUser().getFirstName() + " " + UserDataSource.getInstance().getUser().getLastName());

        emailTextView.setText(UserDataSource.getInstance().getUser().getEmail());

        loadingFragment = new LoadingFragment(this, "Loading");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        quizDB.close();
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


//    private void showQuizzesFragment() {
//        manager.beginTransaction()
//                .addToBackStack(FRAG_TAG_QUIZ_LIST)
//                .replace(R.id.list_container, quizListFragment, FRAG_TAG_QUIZ_LIST)
//                .commit();
//
//    }

    private void showCoursesFragment() {
        manager.beginTransaction()
                .addToBackStack(CourseListFragment.TAG)
                .replace(R.id.list_container, courseListFragment, FRAG_TAG_COURSE_LIST)
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .commit();
    }

    private void onQuizFound(Quiz quiz, Course course) {
        loadingFragment.show();
        Intent i = new Intent(this, IndividualQuizActivity.class);
        Log.d("QUIZ", quiz.toJSON());
        i.putExtra(IndividualQuizActivity.INTENT_EXTRA_QUIZ, quiz);
        i.putExtra(IndividualQuizActivity.INTENT_EXTRA_COURSE_QUIZ, course);
        i.putExtra(IndividualQuizActivity.INTENT_EXTRA_SESSION_ID, sessionID);
        startActivity(i);
        getSupportFragmentManager().popBackStack(StartQuizFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        loadingFragment.dismissWithDelay(500);
    }

    @Override
    public void onUserInitiatedQuizStart(Course course) {
        //try to load quiz from SQLite, and if unsuccessful, try to load quiz from network

        TokenCodeFragment tokenCodeFragment = new TokenCodeFragment(this, course);

        loadingFragment.dismissWithDelay(500);

        tokenCodeFragment.show();

//        loadingFragment.show();
//
//        //Attempt to load quiz from SQLite
//        QuizLoadTask quizLoadTask = new QuizLoadTask(this, course);
//        quizLoadTask.listener = this;
//        quizLoadTask.execute();

    }

//    @Override
//    public void itemClicked(Course course) {
//
//
//        //try to load quiz from SQLite, and if unsuccessful, try to load quiz from network
//
//        loadingFragment.show();
//
//        //Attempt to load quiz from SQLite
//        QuizLoadTask quizLoadTask = new QuizLoadTask(this, course);
//        quizLoadTask.listener = this;
//        quizLoadTask.execute();
//
//    }


    @Override
    public void courseItemClicked(final Course course) {
        courseNameTextView.setText(course.getName());
        quizNetworkingService.downloadUserQuizzes(UserDataSource.getInstance().getUser().getUserID(),
                new UserQuizzesDownloadCallback() {
                    @Override
                    public void onUserQuizzesDownloadSuccess(ArrayList<Course> courses) {
                        HomePageActivity.this.courses = courses;

//                        quizListFragment = (QuizListFragment) manager.findFragmentByTag(FRAG_TAG_QUIZ_LIST);

                        //find the quiz associated with the course you clicked
                        Course selectedCourse = null;

                        for (Course returnedCourse : courses) {
                            if (course.getCourseID().equals(returnedCourse.getCourseID())) {
                                selectedCourse = returnedCourse;
                                break;
                            }
                        }

                        selectedCourse.setId(course.getId());

                        loadingFragment.show();

                        //Attempt to load quiz from SQLite
                        QuizLoadTask quizLoadTask = new QuizLoadTask(HomePageActivity.this, selectedCourse);
                        quizLoadTask.listener = HomePageActivity.this;
                        quizLoadTask.execute();


                    }

                    @Override
                    public void onUserQuizzesDownloadFailure(VolleyError error) {
                        Toast.makeText(HomePageActivity.this, "Failed to load quizzes!", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    @Override
    public void quizDownloaded(Quiz quiz, Course course) {

        quiz.setUserID(UserDataSource.getInstance().getUser().getUserID());
        quiz.setStartTime(new Date());

        Calendar endTime = new GregorianCalendar();
        endTime.setTime(quiz.getStartTime());
        endTime.add(Calendar.MINUTE, quiz.getTimedLength());

        quiz.setEndTime(endTime.getTime());

        boolean writeSuccess = IndividualQuizPersistence.sharedInstance(this).writeIndividualQuizToDatabase(quiz);

        if (writeSuccess) {
            onQuizFound(quiz, course);
        } else {
            Toast.makeText(this, "Error saving quiz to local storage.", Toast.LENGTH_LONG).show();
        }
    }

    //QuizLoadTaskListener Methods


    @Override
    public void onQuizLoadResponse(final Course course, final Quiz quiz) {
        if (quiz != null) {
            //Loaded Quiz From SQLite Successfully

            if (quiz.isFinished()) {
//                Toast.makeText(HomePageActivity.this, "Quiz is finished!", Toast.LENGTH_SHORT).show();

                //Maybe check group progress for the given quiz, and move to next activity accordingly?

                //get user group id

                groupNetworkingService.downloadGroupForUser(UserDataSource.getInstance().getUser().getUserID(), course.getCourseID(), new GroupNetworkingService.SingleGroupDownloadCallback() {
                    @Override
                    public void onDownloadSingleGroupSuccess(Group group) {
                        String groupID = group.getId();
                        groupNetworkingService.getGroupQuizProgress(quiz, group, new GroupNetworkingService.GroupQuizProgressDownloadCallback() {
                            @Override
                            public void onGroupQuizProgressSuccess(GradedGroupQuiz gradedGroupQuiz) {
                                if (gradedGroupQuiz.getTotalQuestions() == gradedGroupQuiz.getQuestionsAnswered()) {
                                    //the group quiz is complete, go to stats page
                                    Intent i = new Intent(HomePageActivity.this, StatisticsActivity.class);
                                    i.putExtra(StatisticsActivity.INTENT_EXTRA_INDIVIDUAL_QUIZ, quiz);
                                    i.putExtra(StatisticsActivity.INTENT_EXTRA_GROUP_QUIZ, gradedGroupQuiz);
                                    i.putExtra(StatisticsActivity.INTENT_EXTRA_COURSE, course);
                                    startActivity(i);
                                    loadingFragment.dismissWithDelay(500);
                                    finish();
                                } else {
                                    //the group quiz is incomplete, go to waiting area page
                                    //TODO: should this go straight to the group quiz page?
                                    Intent i = new Intent(HomePageActivity.this, GroupWaitingAreaActivity.class);
                                    i.putExtra(GroupWaitingAreaActivity.EXTRA_COURSE, course);
                                    i.putExtra(GroupWaitingAreaActivity.EXTRA_QUIZ, quiz);
                                    startActivity(i);
                                    loadingFragment.dismiss();
                                    finish();
                                }
                            }

                            @Override
                            public void onGroupQuizProgressFailure(VolleyError error) {

                                //the individual quiz is finished, but the group quiz hasn't been started?
                                //also go to waiting area page
                                Intent i = new Intent(HomePageActivity.this, GroupWaitingAreaActivity.class);
                                i.putExtra(GroupWaitingAreaActivity.EXTRA_COURSE, course);
                                i.putExtra(GroupWaitingAreaActivity.EXTRA_QUIZ, quiz);
                                startActivity(i);
                                loadingFragment.dismiss();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onDownloadSingleGroupFailure(VolleyError error) {

                        //the quiz has been finished, but there was an error determining what group the user belongs to
                        loadingFragment.dismissWithDelay(500);
                        Toast.makeText(HomePageActivity.this, "Failed to load group!", Toast.LENGTH_LONG).show();

                    }
                });


            } else {
                //the quiz has been started, but hasn't been finished
                //go back to quiz
                Intent i = new Intent(this, IndividualQuizActivity.class);
                i.putExtra(IndividualQuizActivity.INTENT_EXTRA_QUIZ, quiz);
                i.putExtra(IndividualQuizActivity.INTENT_EXTRA_COURSE_QUIZ, course);
                i.putExtra(IndividualQuizActivity.INTENT_EXTRA_SESSION_ID, sessionID);
                startActivity(i);
                loadingFragment.dismiss();
            }
        } else {
            //the quiz isn't in the SQLite database, so it hasn't been started; request token code
//            TokenCodeFragment tokenCodeFragment = new TokenCodeFragment(this, course);
//
//            loadingFragment.dismissWithDelay(500);
//
//            tokenCodeFragment.show();

            startQuizFragment = (StartQuizFragment) manager.findFragmentByTag(StartQuizFragment.TAG);

            if (startQuizFragment == null) {
                Bundle args = new Bundle();


                args.putSerializable(StartQuizFragment.ARG_COURSE,
                        course);
                startQuizFragment = new StartQuizFragment();
                startQuizFragment.setArguments(args);

            }

//                        showQuizzesFragment();
            manager.beginTransaction()
                    .addToBackStack(StartQuizFragment.TAG)
                    .replace(R.id.list_container, startQuizFragment, StartQuizFragment.TAG)
                    .commit();

            loadingFragment.dismissWithDelay(500);
        }
    }
}
