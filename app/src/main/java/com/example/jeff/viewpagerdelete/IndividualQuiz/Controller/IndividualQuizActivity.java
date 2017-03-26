package com.example.jeff.viewpagerdelete.IndividualQuiz.Controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

    private Quiz quiz;

//    private IndividualQuizDbHelper dbHelper;
//    private SQLiteDatabase dbReadable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_quiz_activity_layout);

        final Context context = this;


//        Toast.makeText(this, "At this point, the user has logged in, been presented the quiz instructions, and any other setup", Toast.LENGTH_LONG).show();

        Intent i = getIntent();
        if(i.hasExtra(INTENT_EXTRA_QUIZ)){
            quiz = (Quiz) i.getSerializableExtra(INTENT_EXTRA_QUIZ);
            Log.d("TAG", "found quiz extra in IndividualQuizActivity");
//            dbHelper = new IndividualQuizDbHelper(this);
//            dbReadable = dbHelper.getReadableDatabase();

        }
        else{
            Log.e("TAG", "No 'quizzes' extra");
            finish();
        }

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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        dbReadable.close();
    }

    @Override
    public void onBackPressed() {
        if(mPager.getCurrentItem() == 0){
            super.onBackPressed();
        }
        else{
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
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
