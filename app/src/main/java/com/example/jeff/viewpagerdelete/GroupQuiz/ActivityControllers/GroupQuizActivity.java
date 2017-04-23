package com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.eftimoff.viewpagertransformers.DefaultTransformer;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Controller.IndividualQuizActivity;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Controller.IndividualQuizActivity.DetailOnPageChangeListener;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.IndividualQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.IndividualQuizQuestionFragment;
import com.example.jeff.viewpagerdelete.R;

public class GroupQuizActivity extends AppCompatActivity {

  public static final String TAG = "Group Quiz Activity";

  //Constants used for keys/values
  public static final String INTENT_EXTRA_QUIZ = "INTENT_EXTRA_QUIZ";
  public static final String INTENT_EXTRA_SESSION_ID = "INTENT_EXTRA_SESSION_ID";
  public static final String INTENT_EXTRA_GROUP = "INTENT_EXTRA_GROUP";
  public static final String INTENT_EXTRA_COURSE = "INTENT_EXTRA_COURSE";
  public static final String EXTRA_QUIZ_QUESTION = "EXTRA_QUIZ_QUESTION";
  public static final String EXTRA_QUIZ_QUESTION_NUMBER = "EXTRA_QUIZ_QUESTION_NUMBER";
  public static final String EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS = "EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS ";

  public ViewPager mPager;
  private ScreenSlidePagerAdapter mAdapter;
  private DetailOnPageChangeListener onPageChangeListener;
  private TabLayout tabLayout;

  //  private Course course;
  private Quiz quiz;
  private Group group;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_quiz);

    mPager = (ViewPager) findViewById(R.id.quiz_question_viewpager);

    Bundle extras = getIntent().getExtras();

    if (extras == null || !extras.containsKey(INTENT_EXTRA_QUIZ) || !extras
        .containsKey(INTENT_EXTRA_GROUP)) {
      Log.e(TAG, "Expected Course and Quiz passed via intent");
      finish();
    } else {
      quiz = (Quiz) extras.getSerializable(INTENT_EXTRA_QUIZ);
      group = (Group) extras.getSerializable(INTENT_EXTRA_GROUP);
    }

    mPager.setPageTransformer(false, new DefaultTransformer());
    mAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
    mPager.setAdapter(mAdapter);

    tabLayout = (TabLayout) findViewById(R.id.tabDots);
    tabLayout.setupWithViewPager(mPager, true);
  }

  //ViewPager Helper Classes

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
}
