package com.example.jeff.viewpagerdelete.IndividualQuiz.Controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.eftimoff.viewpagertransformers.StackTransformer;
import com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers.GroupWaitingArea;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.IndividualQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Networking.QuizNetworkingService;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.IndividualQuizQuestionFragment;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.QuestionsUnfinishedFragment;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.SubmissionAlertFragment;

import java.util.ArrayList;

/**
 * Author: Jeffery Calhoun
 * Description:
 */
public class IndividualQuizActivity extends AppCompatActivity
        implements IndividualQuizQuestionFragment.PageFragmentListener,
                    SubmissionAlertFragment.SubmissionAlertFragmentListener,
    QuizNetworkingService.IndividualQuizPostListener {

    //Constants used for key/value
    public static final String INTENT_EXTRA_QUIZ = "INTENT_EXTRA_QUIZ";
    public static final String INTENT_EXTRA_SESSION_ID = "INTENT_EXTRA_SESSION_ID";
    public static final String INTENT_EXTRA_COURSE_QUIZ = "INTENT_EXTRA_COURSE_QUIZ";
    public static final String EXTRA_FINISH_BUTTON_TEXT = "EXTRA_FINISH_BUTTON_TEXT";
    public static final String EXTRA_QUIZ_QUESTION = "EXTRA_QUIZ_QUESTION";
    public static final String EXTRA_QUIZ_QUESTION_NUMBER = "EXTRA_QUIZ_QUESTION_NUMBER";
    public static final String EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS = "EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS ";

    public ViewPager mPager;
    private ScreenSlidePagerAdapter mAdapter;

    private Snackbar snackbar;

    private Course course;
    private Quiz quiz;
    private String username = "jcd39";

    private QuizNetworkingService quizNetworkingService;

    private String sessionID;

    private Snackbar submitSnackBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_quiz);

        Intent i = getIntent();
        if(i.hasExtra(INTENT_EXTRA_QUIZ) && i.hasExtra(INTENT_EXTRA_COURSE_QUIZ) && i.hasExtra(INTENT_EXTRA_SESSION_ID)){
            quiz = (Quiz) i.getSerializableExtra(INTENT_EXTRA_QUIZ);
            course = (Course) i.getSerializableExtra(INTENT_EXTRA_COURSE_QUIZ);
            sessionID = i.getStringExtra(INTENT_EXTRA_SESSION_ID);

        }
        else{
            Log.e("TAG", "No 'quizzes' extra found in IndividualQuizActivity.java");
            finish();
        }

        quizNetworkingService = new QuizNetworkingService(this);

        final Context context = this;

        getSupportActionBar().setTitle(quiz.getDescription());

        submitSnackBar = Snackbar.make(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), "Ready to submit?", Snackbar.LENGTH_INDEFINITE);
        submitSnackBar.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBright));
        submitSnackBar.setAction("Submit", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advanceButtonClicked();
            }
        });


        mPager = (ViewPager) findViewById(R.id.question_pager);
        mPager.setOffscreenPageLimit(quiz.getQuestions().size() - 1);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Page was changed; save quiz
                IndividualQuizPersistence.sharedInstance(context).updateQuizInDatabase(quiz);


                if(position == mPager.getChildCount() - 1){
                    submitSnackBar.show();
                }
                else{
                    submitSnackBar.dismiss();
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPager.setPageTransformer(true, new StackTransformer());
        mAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager, true);

//        setTypefaces();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setTitle("Exit Quiz");
        exitDialog.setMessage("Are you sure you wish to exit the quiz? You can resume it later if there is time remaining.");
        exitDialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                IndividualQuizActivity.super.onBackPressed();
            }
        });

        exitDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(exitDialog != null){
                    //dismiss
                }
            }
        });

        exitDialog.create().show();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            IndividualQuizQuestionFragment newFrag = new IndividualQuizQuestionFragment();
            Bundle extras = new Bundle();
            extras.putInt(EXTRA_QUIZ_QUESTION_NUMBER, position + 1);
            extras.putSerializable(EXTRA_QUIZ_QUESTION, quiz.getQuestions().get(position));
            extras.putInt(EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS, this.getCount());
            newFrag.setArguments(extras);
            return newFrag;
        }

        @Override
        public int getCount() {
            return quiz.getQuestions().size();
        }

    }

    @Override
    public void advanceButtonClicked() {

        int currentItem = mPager.getCurrentItem();
        if(currentItem < mPager.getChildCount() - 1){ //there is a next page to go to
            mPager.setCurrentItem(currentItem + 1);
        }
        else{ //there isn't a next page to go to, and the user has clicked the "Finish" button

            IndividualQuizPersistence.sharedInstance(this).updateQuizInDatabase(quiz);

            //TODO: This code passes a list of unanswered questions, and a boolean array for all questions and whether
                //or not they are answered. The plan is to allow the QuestionsUnfinishedFragment to display a list of
                //unanswered questions, and then navigate the user to the first unanswered question. I hav

            ArrayList<QuizQuestion> unansweredQuestions = new ArrayList<>();

            for(QuizQuestion question: quiz.getQuestions()){
                if(question.getPointsRemaining() != 0){
                    unansweredQuestions.add(question);
                }
            }

            if(unansweredQuestions.size() == 0){
                //show finish confirmation alert
                SubmissionAlertFragment submissionAlert = new SubmissionAlertFragment();
                submissionAlert.show(getSupportFragmentManager(), "CONFIRM_SUBMISSION");

            }
            else{
//                //show unanswered questions alert

                submitSnackBar.dismiss();
                int unanswered = unansweredQuestions.size();
                snackbar = Snackbar.make(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), unanswered + " questions unanswered.", Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBright));
                snackbar.setAction("GO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userAcknowledgedUnfinishedQuestions();
                    }
                });

                snackbar.show();

            }

        }
    }


    public void userAcknowledgedUnfinishedQuestions() {

        //Transition to first unanswered question
        int firstUnansweredIndex = 0;

        for(QuizQuestion question: quiz.getQuestions()){
            if(question.getPointsRemaining() != 0){
                mPager.setCurrentItem(firstUnansweredIndex);
                if (mPager.getCurrentItem() == mAdapter.getCount() - 1) {
                    submitSnackBar.show();
                }
                break;
            }
            else{
                ++firstUnansweredIndex;
            }
        }
    }


    //MARK: SubmissionAlertFragmentListener Methods


    @Override
    public void userConfirmedSubmission() {

        quizNetworkingService
            .uploadQuiz(this, course.getCourseID(), username, quiz.getAssociatedSessionID(), quiz);

    }

    @Override
    public void userCanceledSubmission() {
        submitSnackBar.show();
    }


    //MARK: IndividualQuizPostListener Methods

    @Override
    public void onQuizPostSuccess(GradedQuiz gradedQuiz) {
        Intent i = new Intent(this, GroupWaitingArea.class);
        i.putExtra(GroupWaitingArea.EXTRA_COURSE, course);
        i.putExtra(GroupWaitingArea.EXTRA_GRADED_QUIZ, gradedQuiz);
        startActivity(i);
        finish();
    }

    @Override
    public void onQuizPostFailure(VolleyError error) {
        Toast.makeText(this, "Can't submit quiz yet", Toast.LENGTH_LONG).show();
    }
}
