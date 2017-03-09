package com.example.jeff.viewpagerdelete;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.jeff.viewpagerdelete.Models.Quiz;
import com.example.jeff.viewpagerdelete.Models.QuizQuestion;

import java.util.ArrayList;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Author: Jeffery Calhoun
 * Description:
 */
public class IndividualQuizActivity extends AppCompatActivity implements IndividualQuizQuestionFragment.PageFragmentListener, QuestionsUnfinishedFragment.UnfinishedQuestionsInterface {

    //Constants used for key/value
    public static final String EXTRA_FINISH_BUTTON_TEXT = "EXTRA_FINISH_BUTTON_TEXT";
    public static final String EXTRA_QUIZ_QUESTION = "EXTRA_QUIZ_QUESTION";
    public static final String EXTRA_QUIZ_QUESTION_NUMBER = "EXTRA_QUIZ_QUESTION_NUMBER";

    public VerticalViewPager mPager;
    private ScreenSlidePagerAdapter mAdapter;

    private Quiz quiz;

    private IndividualQuizDbHelper dbHelper;
    private SQLiteDatabase dbReadable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_quiz_activity_layout);


        Toast.makeText(this, "At this point, the user has logged in, been presented the quiz instructions, and any other setup", Toast.LENGTH_LONG).show();

        Intent i = getIntent();
        if(i.hasExtra("quiz")){
            quiz = (Quiz) i.getSerializableExtra("quiz");
            dbHelper = new IndividualQuizDbHelper(this);
            dbReadable = dbHelper.getReadableDatabase();

        }
        else{
            Toast.makeText(this, "No 'quizzes' extra", Toast.LENGTH_SHORT).show();
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
                quiz.updateQuizInDatabase(dbReadable);
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
        dbReadable.close();
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
        if(currentItem < quiz.getQuestions().size() - 1){ //there is a next page to go to
            mPager.setCurrentItem(currentItem + 1);
        }
        else{ //there isn't a next page to go to, and the user has clicked the "Finish" button

            quiz.updateQuizInDatabase(dbReadable);

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
            if(position == quiz.getQuestions().size() - 1){
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
}
