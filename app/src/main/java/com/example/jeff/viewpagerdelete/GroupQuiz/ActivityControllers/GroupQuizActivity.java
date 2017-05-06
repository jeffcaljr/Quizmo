package com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.eftimoff.viewpagertransformers.DefaultTransformer;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuiz;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuizQuestion;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GroupQuizAnswer;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService.GroupQuizAnswerPostCallback;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService.GroupQuizProgressDownloadCallback;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupQuizProgressPollingService;
import com.example.jeff.viewpagerdelete.GroupQuiz.View.GroupQuizQuestionFragment;
import com.example.jeff.viewpagerdelete.GroupQuiz.View.GroupQuizQuestionFragment.OnGroupQuizAnswerSelectedListener;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Controller.IndividualQuizActivity.DetailOnPageChangeListener;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.GradedQuiz.GradedQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizAnswer;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.QuizStatistics.Controller.StatisticsActivity;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

import java.util.Collections;

public class GroupQuizActivity extends AppCompatActivity implements
        OnGroupQuizAnswerSelectedListener {

    public static final String TAG = "Group Quiz Activity";

    //Constants used for keys/values
    public static final String INTENT_EXTRA_GROUP_QUIZ_PROGRESS = "INTENT_EXTRA_GROUP_QUIZ_PROGRESS";
    public static final String INTENT_EXTRA_QUIZ = "INTENT_EXTRA_QUIZ";
    public static final String INTENT_EXTRA_SESSION_ID = "INTENT_EXTRA_SESSION_ID";
    public static final String INTENT_EXTRA_GROUP = "INTENT_EXTRA_GROUP";
    public static final String INTENT_EXTRA_IS_LEADER = "INTENT_EXTRA_IS_LEADER";
    public static final String INTENT_EXTRA_COURSE = "INTENT_EXTRA_COURSE";
    public static final String EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS = "EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS ";

    public ViewPager mPager;
    private ScreenSlidePagerAdapter mAdapter;
    private DetailOnPageChangeListener onPageChangeListener;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar goToStatisticsSnackbar;

    private GradedQuiz gradedQuiz;
    private Quiz quiz;
    private Group group;
    private Course course;
    private GradedGroupQuiz gradedGroupQuiz;
    private boolean isGroupLeader;

    private GroupNetworkingService groupNetworkingService;

    private LocalBroadcastManager bManager;

    //This activity will listen for Group Status Updates
    public static final String RECEIVE_GROUP_PROGRESS = "com.example.RECEIVE_GROUP_PROGRESS";

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RECEIVE_GROUP_PROGRESS)) {
                GroupQuizActivity.this.gradedGroupQuiz = (GradedGroupQuiz) intent.getSerializableExtra("gradedGroupQuiz");
                mAdapter.notifyDataSetChanged();

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_quiz);

        mPager = (ViewPager) findViewById(R.id.quiz_question_viewpager);

        Bundle extras = getIntent().getExtras();

        if (extras == null || !extras.containsKey(INTENT_EXTRA_QUIZ) || !extras
                .containsKey(INTENT_EXTRA_GROUP) || !extras.containsKey(INTENT_EXTRA_IS_LEADER)) {
            Log.e(TAG, "Expected Course and Quiz passed via intent");
            finish();
        } else {
            quiz = (Quiz) extras.getSerializable(INTENT_EXTRA_QUIZ);
            group = (Group) extras.getSerializable(INTENT_EXTRA_GROUP);
            isGroupLeader = extras.getBoolean(INTENT_EXTRA_IS_LEADER);
            course = (Course) extras.getSerializable(INTENT_EXTRA_COURSE);
        }


        if (extras.containsKey(INTENT_EXTRA_GROUP_QUIZ_PROGRESS)) {
            gradedGroupQuiz = (GradedGroupQuiz) extras.getSerializable(INTENT_EXTRA_GROUP_QUIZ_PROGRESS);
            gradedGroupQuiz.setId(quiz.getId());
        }

        //sort the quiz questions, so that they will be displayed in the same order on each member's device

        Collections.sort(quiz.getQuestions());

        mPager = (ViewPager) findViewById(R.id.quiz_question_viewpager);
        mPager.setPageTransformer(false, new DefaultTransformer());
        mAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager, true);

        goToStatisticsSnackbar = Snackbar
                .make(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0),
                        "Group quiz complete!", Snackbar.LENGTH_INDEFINITE)
                .setAction("View Stats", new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(GroupQuizActivity.this, StatisticsActivity.class);
                        i.putExtra(StatisticsActivity.INTENT_EXTRA_GROUP_QUIZ, gradedGroupQuiz);
                        i.putExtra(StatisticsActivity.INTENT_EXTRA_INDIVIDUAL_QUIZ, quiz);
                        i.putExtra(StatisticsActivity.INTENT_EXTRA_COURSE, course);
                        startActivity(i);
                        finish();
                    }
                });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.group_quiz_swipe_refresher);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateQuizProgress();
            }
        });

        //The leader won't need to use the refreshing feature, as they are in control of the quiz
        if (isGroupLeader == true) {
            swipeRefreshLayout.setEnabled(false);
        }

        groupNetworkingService = new GroupNetworkingService(this);


        //listen for group status updates
        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_GROUP_PROGRESS);
        bManager.registerReceiver(bReceiver, intentFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateQuizProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Start listening for group quiz progress, if the user is not the group leader

        GroupQuizProgressPollingService.setServiceAlarm(this, false, group, quiz);
    }

    private void updateQuizProgress() {
        groupNetworkingService.getGroupQuizProgress(quiz, group, new GroupQuizProgressDownloadCallback() {
            @Override
            public void onGroupQuizProgressSuccess(final GradedGroupQuiz gradedGroupQuiz) {
                GroupQuizActivity.this.gradedGroupQuiz = gradedGroupQuiz;
                mAdapter.notifyDataSetChanged();
                if (gradedGroupQuiz.getQuestionsAnswered() == gradedGroupQuiz.getTotalQuestions()) {
                    // the quiz is done


                    //Retrieve the graded individual quiz from local storage; to be passed to statistics activity

                    gradedQuiz = GradedQuizPersistence.sharedInstance(GroupQuizActivity.this).readGradedQuizFromDatabase(quiz.getId(), UserDataSource.getInstance().getUser().getUserID());

                    //Prompt the user to see their statistics when the group quiz is done

                    goToStatisticsSnackbar.show();

                    swipeRefreshLayout.setRefreshing(false);
                    GroupQuizProgressPollingService.setServiceAlarm(GroupQuizActivity.this, false, group, quiz);

                } else {

                    //Start listening for group quiz progress, if the user is not the group leader

                    if (isGroupLeader == false) {
                    GroupQuizProgressPollingService.setServiceAlarm(GroupQuizActivity.this, true, group, quiz);
                    }

                    swipeRefreshLayout.setRefreshing(false);

//                    Toast.makeText(GroupQuizActivity.this,
//                            (gradedGroupQuiz.getTotalQuestions() - gradedGroupQuiz.getQuestionsAnswered())
//                                    + "questions left", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onGroupQuizProgressFailure(VolleyError error) {
//                Toast
//                        .makeText(GroupQuizActivity.this, "Error updating quiz progress", Toast.LENGTH_LONG)
//                        .show();
                gradedGroupQuiz = new GradedGroupQuiz();


                //Start listening for group quiz progress, if the user is not the group leader

                if (isGroupLeader == false) {
                GroupQuizProgressPollingService.setServiceAlarm(GroupQuizActivity.this, true, group, quiz);
                }

                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    //OnGroupQuizAnswerSelectedListener method implementations

    @Override
    public void answerSelected(QuizQuestion question, QuizAnswer answer,
                               final GroupQuizQuestionFragment currentQuestionFragment) {

        GroupQuizAnswer groupQuizAnswer = new GroupQuizAnswer(question.getId(), answer.getValue());

        groupNetworkingService.submitGroupQuizAnswer(quiz.getId(), group.getId(),
                quiz.getAssociatedSessionID(), groupQuizAnswer, new GroupQuizAnswerPostCallback() {
                    @Override
                    public void onGroupQuizAnswerPostSuccess(
                            GradedGroupQuizQuestion gradedGroupQuizQuestion) {

                        currentQuestionFragment.onGradeRecieved(gradedGroupQuizQuestion);
                        gradedGroupQuiz.updateQuestion(gradedGroupQuizQuestion);
                        updateQuizProgress();
                    }

                    @Override
                    public void onGroupQuizAnswerPostFailure(VolleyError error) {
//                        Toast.makeText(GroupQuizActivity.this, "Error submitting group quiz answer",
//                                Toast.LENGTH_LONG).show();
                    }
                });

    }

    //ViewPager Helper Classes

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            GroupQuizQuestionFragment newFrag = new GroupQuizQuestionFragment();
            Bundle extras = new Bundle();
            extras.putInt(GroupQuizQuestionFragment.ARG_QUIZ_QUESTION_NUMBER, position + 1);
            extras.putSerializable(GroupQuizQuestionFragment.ARG_QUIZ_QUESTION,
                    quiz.getQuestions().get(position));
            extras.putInt(EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS, this.getCount());
            extras.putBoolean(GroupQuizQuestionFragment.ARG_IS_USER_GROUP_LEADER, isGroupLeader);

            if (GroupQuizActivity.this.gradedGroupQuiz != null) {
                String thisQuestionID = quiz.getQuestions().get(position).getId();

                //if any of the group quiz has been graded, pass this info to the appropriate fragment
                //appropriate fragment found by matching quizQuestionID with graded question id

                for (GradedGroupQuizQuestion gradedQuestion : GroupQuizActivity.this.gradedGroupQuiz
                        .getGradedQuestions()) {
                    if (gradedQuestion.getId().equals(thisQuestionID)) {
                        extras.putSerializable(GroupQuizQuestionFragment.ARG_GRADED_QUIZ_QUESTION,
                                gradedQuestion);
                        break;
                    }
                }
            }
            newFrag.setArguments(extras);
            return newFrag;
        }

        @Override
        public int getCount() {
            return quiz.getQuestions().size();
        }


    }
}
