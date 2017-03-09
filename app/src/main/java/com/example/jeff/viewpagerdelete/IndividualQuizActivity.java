package com.example.jeff.viewpagerdelete;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.jeff.viewpagerdelete.Models.Quiz;

import org.json.JSONException;
import org.json.JSONObject;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Author: Jeffery Calhoun
 * Description:
 */
public class IndividualQuizActivity extends AppCompatActivity implements IndividualQuizQuestionFragment.PageFragmentListener {

    //Constants used for key/value
    public static final String EXTRA_FINISH_BUTTON_TEXT = "EXTRA_FINISH_BUTTON_TEXT";
    public static final String EXTRA_QUIZ_QUESTION = "EXTRA_QUIZ_QUESTION";

    private VerticalViewPager mPager;
    private ScreenSlidePagerAdapter mAdapter;

    private Quiz quiz;

    private IndividualQuizDbHelper dbHelper;
    private SQLiteDatabase dbReadable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
            SubmissionAlertFragment submissionAlert = new SubmissionAlertFragment();
            submissionAlert.show(getFragmentManager(), "CONFIRM_SUBMISSION");
        }
    }

    @Override
    public void quizStateUpdated() {
        quiz.updateQuizInDatabase(dbReadable);
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
