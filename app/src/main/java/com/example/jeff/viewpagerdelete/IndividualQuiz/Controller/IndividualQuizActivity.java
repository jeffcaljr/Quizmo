package com.example.jeff.viewpagerdelete.IndividualQuiz.Controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.eftimoff.viewpagertransformers.AccordionTransformer;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.eftimoff.viewpagertransformers.DrawFromBackTransformer;
import com.eftimoff.viewpagertransformers.FlipVerticalTransformer;
import com.eftimoff.viewpagertransformers.StackTransformer;
import com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers.GroupQuizCodeActivity;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.IndividualQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.IndividualQuizQuestionFragment;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.QuestionsUnfinishedFragment;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.SubmissionAlertFragment;

import java.util.ArrayList;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Author: Jeffery Calhoun
 * Description:
 */
public class IndividualQuizActivity extends AppCompatActivity
        implements IndividualQuizQuestionFragment.PageFragmentListener,
                    QuestionsUnfinishedFragment.UnfinishedQuestionsInterface,
                    SubmissionAlertFragment.SubmissionAlertFragmentListener{

    //Constants used for key/value
    public static final String INTENT_EXTRA_QUIZ = "QUIZ";
    public static final String EXTRA_FINISH_BUTTON_TEXT = "EXTRA_FINISH_BUTTON_TEXT";
    public static final String EXTRA_QUIZ_QUESTION = "EXTRA_QUIZ_QUESTION";
    public static final String EXTRA_QUIZ_QUESTION_NUMBER = "EXTRA_QUIZ_QUESTION_NUMBER";

    public VerticalViewPager mPager;
    private ScreenSlidePagerAdapter mAdapter;

    private Button previousQuestionButton;
    private Button nextQuestionButton;
    private Button submitButton;

    private Quiz quiz;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_quiz);

        Intent i = getIntent();
        if(i.hasExtra(INTENT_EXTRA_QUIZ)){
            quiz = (Quiz) i.getSerializableExtra(INTENT_EXTRA_QUIZ);

        }
        else{
            Log.e("TAG", "No 'quizzes' extra found in IndividualQuizActivity.java");
            finish();
        }

        final Context context = this;

        getSupportActionBar().setTitle(quiz.getDescription());


        previousQuestionButton = (Button) findViewById(R.id.previous_question_available_indicator);
        nextQuestionButton = (Button) findViewById(R.id.next_question_available_indicator);
        submitButton = (Button) findViewById(R.id.submit_button);

        previousQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = mPager.getCurrentItem();
                if(currentItem - 1 >= 0){ //there is a previous page to go to
                    mPager.setCurrentItem(currentItem - 1);
                }
            }
        });

        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = mPager.getCurrentItem();
                if(currentItem < mPager.getChildCount() - 1){ //there is a next page to go to
                    mPager.setCurrentItem(currentItem + 1);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advanceButtonClicked();
            }
        });


        mPager = (VerticalViewPager) findViewById(R.id.question_pager);
        mPager.setOffscreenPageLimit(quiz.getQuestions().size() - 1);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Page was changed; save quiz
                IndividualQuizPersistence.sharedInstance(context).updateQuizInDatabase(quiz);

                if(position == 0){
                    previousQuestionButton.setEnabled(false);
                }
                else {
                    previousQuestionButton.setEnabled(true);
                }

                if(position == mPager.getChildCount() - 1){
                    nextQuestionButton.setVisibility(View.INVISIBLE);
                    submitButton.setVisibility(View.VISIBLE);
                }
                else{
                    nextQuestionButton.setVisibility(View.VISIBLE);
                    submitButton.setVisibility(View.INVISIBLE);
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPager.setPageTransformer(true, new DrawFromBackTransformer());
        mAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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

    //MARK: IndividualQuizQuestionFragmentListener Methods

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
                //show unanswered questions alert
                QuestionsUnfinishedFragment unfinishedFragment = new QuestionsUnfinishedFragment();
                Bundle args = new Bundle();
                args.putSerializable("unansweredQuestions", unansweredQuestions);
                unfinishedFragment.setArguments(args);
                unfinishedFragment.show(getSupportFragmentManager(), "SHOW_UNANSWERED_QUESTIONS");

            }

        }
    }


    //UnfinishedQuestionsInterface Methods


    @Override
    public void userAcknowledgedUnfinishedQuestions() {

        //Transition to first unanswered question
        int firstUnansweredIndex = 0;

        for(QuizQuestion question: quiz.getQuestions()){
            if(question.getPointsRemaining() != 0){
                mPager.setCurrentItem(firstUnansweredIndex);
                break;
            }
            else{
                ++firstUnansweredIndex;
            }
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter{

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            IndividualQuizQuestionFragment newFrag = new IndividualQuizQuestionFragment();
            Bundle extras = new Bundle();
            if(position == getCount() - 1){
                extras.putString(EXTRA_FINISH_BUTTON_TEXT, "Finish");
            }
            extras.putInt(EXTRA_QUIZ_QUESTION_NUMBER, position + 1);
            extras.putSerializable(EXTRA_QUIZ_QUESTION, quiz.getQuestions().get(position));
            newFrag.setArguments(extras);
            return newFrag;
        }

        @Override
        public int getCount() {
            return quiz.getQuestions().size();
        }

    }

    //MARK: SubmissionAlertFragmentListener Methods


    @Override
    public void userConfirmedSubmission() {
        Intent i = new Intent(this, GroupQuizCodeActivity.class);
        startActivity(i);
        finish();
    }
}
