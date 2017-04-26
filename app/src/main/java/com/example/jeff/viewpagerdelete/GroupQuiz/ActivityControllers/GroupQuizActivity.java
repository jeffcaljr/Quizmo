package com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.example.jeff.viewpagerdelete.GroupQuiz.View.GroupQuizQuestionFragment;
import com.example.jeff.viewpagerdelete.GroupQuiz.View.GroupQuizQuestionFragment.OnGroupQuizAnswerSelectedListener;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Controller.IndividualQuizActivity.DetailOnPageChangeListener;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.GradedQuiz.GradedQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.Quiz.IndividualQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizAnswer;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.QuizStatistics.ActivityControllers.StatisticsActivity;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

public class GroupQuizActivity extends AppCompatActivity implements
    OnGroupQuizAnswerSelectedListener {

  public static final String TAG = "Group Quiz Activity";

  //Constants used for keys/values
  public static final String INTENT_EXTRA_GROUP_QUIZ_PROGRESS = "INTENT_EXTRA_GROUP_QUIZ_PROGRESS";
  public static final String INTENT_EXTRA_QUIZ = "INTENT_EXTRA_QUIZ";
  public static final String INTENT_EXTRA_SESSION_ID = "INTENT_EXTRA_SESSION_ID";
  public static final String INTENT_EXTRA_GROUP = "INTENT_EXTRA_GROUP";
  public static final String INTENT_EXTRA_COURSE = "INTENT_EXTRA_COURSE";
  public static final String EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS = "EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS ";

  public ViewPager mPager;
  private ScreenSlidePagerAdapter mAdapter;
  private DetailOnPageChangeListener onPageChangeListener;
  private TabLayout tabLayout;

  private GradedQuiz gradedQuiz;
  private Quiz quiz;
  private Group group;
  private GradedGroupQuiz gradedGroupQuiz;

  private GroupNetworkingService groupNetworkingService;



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

//    quiz = IndividualQuizPersistence.sharedInstance(this)
//        .readIndividualQuizFromDatabase(gradedQuiz.getQuizID(),
//            UserDataSource.getInstance().getUser().getUserID());

    if (extras.containsKey(INTENT_EXTRA_GROUP_QUIZ_PROGRESS)) {
      gradedGroupQuiz = (GradedGroupQuiz) extras.getSerializable(INTENT_EXTRA_GROUP_QUIZ_PROGRESS);
      gradedGroupQuiz.setId(quiz.getId());
    }

    mPager = (ViewPager) findViewById(R.id.quiz_question_viewpager);
    mPager.setPageTransformer(false, new DefaultTransformer());
    mAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
    mPager.setAdapter(mAdapter);

    tabLayout = (TabLayout) findViewById(R.id.tabDots);
    tabLayout.setupWithViewPager(mPager, true);

    groupNetworkingService = new GroupNetworkingService(this);

    updateQuizProgress();
  }

  private void updateQuizProgress() {
    groupNetworkingService.getGroupQuizProgress(quiz.getId(), group.getId(),
        quiz.getAssociatedSessionID(), new GroupQuizProgressDownloadCallback() {
          @Override
          public void onGroupQuizProgressSuccess(final GradedGroupQuiz gradedGroupQuiz) {
            GroupQuizActivity.this.gradedGroupQuiz = gradedGroupQuiz;
            if (gradedGroupQuiz.getQuestionsAnswered() == gradedGroupQuiz.getTotalQuestions()) {
              // the quiz is done

              gradedQuiz = GradedQuizPersistence.sharedInstance(GroupQuizActivity.this).readGradedQuizFromDatabase(quiz.getId(), UserDataSource.getInstance().getUser().getUserID());

              Snackbar snackbar = Snackbar
                  .make(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0),
                      "Group quiz complete!", Snackbar.LENGTH_INDEFINITE)
                  .setAction("View Stats", new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      Intent i = new Intent(GroupQuizActivity.this, StatisticsActivity.class);
                      i.putExtra(StatisticsActivity.INTENT_EXTRA_GROUP_QUIZ, gradedGroupQuiz);
                      i.putExtra(StatisticsActivity.INTENT_EXTRA_INDIVIDUAL_QUIZ, quiz);
                      startActivity(i);
                    }
                  });

              snackbar.show();
            } else {
              Toast.makeText(GroupQuizActivity.this,
                  (gradedGroupQuiz.getTotalQuestions() - gradedGroupQuiz.getQuestionsAnswered())
                      + "questions left", Toast.LENGTH_SHORT).show();
            }
          }

          @Override
          public void onGroupQuizProgressFailure(VolleyError error) {
            Toast
                .makeText(GroupQuizActivity.this, "Error updating quiz progress", Toast.LENGTH_LONG)
                .show();
            gradedGroupQuiz = new GradedGroupQuiz();

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
            Toast.makeText(GroupQuizActivity.this, "Error submitting group quiz answer",
                Toast.LENGTH_LONG).show();
          }
        });

  }

  //ViewPager Helper Classes

  private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    public ScreenSlidePagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      GroupQuizQuestionFragment newFrag = new GroupQuizQuestionFragment();
      Bundle extras = new Bundle();
      extras.putInt(GroupQuizQuestionFragment.ARG_QUIZ_QUESTION_NUMBER, position + 1);
      extras.putSerializable(GroupQuizQuestionFragment.ARG_QUIZ_QUESTION,
          quiz.getQuestions().get(position));
      extras.putInt(EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS, this.getCount());

      if (GroupQuizActivity.this.gradedGroupQuiz != null) {
        String thisQuestionID = quiz.getQuestions().get(position).getId();
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
