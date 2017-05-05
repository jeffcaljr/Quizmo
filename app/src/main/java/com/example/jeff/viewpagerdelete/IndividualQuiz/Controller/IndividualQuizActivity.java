package com.example.jeff.viewpagerdelete.IndividualQuiz.Controller;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.design.widget.Snackbar.Callback;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.eftimoff.viewpagertransformers.DefaultTransformer;
import com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers.GroupWaitingAreaActivity;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.GradedQuiz.GradedQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.Quiz.IndividualQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Networking.QuizNetworkingService;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Networking.QuizNetworkingService.IndividualQuizPostCallback;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.IndividualQuizQuestionFragment;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.Miscellaneous.LoadingFragment;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.SubmissionAlertFragment;

import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Author: Jeffery Calhoun
 * Description:
 */
public class IndividualQuizActivity extends AppCompatActivity
        implements IndividualQuizQuestionFragment.PageFragmentListener,
    SubmissionAlertFragment.SubmissionAlertFragmentListener {

  //Constants used for keys/values
    public static final String INTENT_EXTRA_QUIZ = "INTENT_EXTRA_QUIZ";
    public static final String INTENT_EXTRA_SESSION_ID = "INTENT_EXTRA_SESSION_ID";
    public static final String INTENT_EXTRA_COURSE_QUIZ = "INTENT_EXTRA_COURSE_QUIZ";
    public static final String EXTRA_QUIZ_QUESTION = "EXTRA_QUIZ_QUESTION";
    public static final String EXTRA_QUIZ_QUESTION_NUMBER = "EXTRA_QUIZ_QUESTION_NUMBER";
    public static final String EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS = "EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS ";


    public ViewPager mPager;
    private ScreenSlidePagerAdapter mAdapter;
  private DetailOnPageChangeListener onPageChangeListener;
  private TabLayout tabLayout;
  private ProgressBar quizTimerProgressBar;

    private Snackbar snackbar;

    private Course course;
    private Quiz quiz;

    private QuizNetworkingService quizNetworkingService;

    private String sessionID;

    private Snackbar submitSnackBar;

    private LoadingFragment submittingFragment;

    private CountDownTimer countDownTimer;
    private Drawable halfwayDoneDrawable;
    private Drawable threeQuartersDoneDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_quiz_individual);

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
      submitSnackBar.setActionTextColor(ContextCompat.getColor(this, R.color.jccolorPrimaryBright));
        submitSnackBar.setAction("Submit", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              submitButtonClicked();
            }
        });

      quizTimerProgressBar = (ProgressBar) findViewById(R.id.quiz_timer_progress_bar);

      mPager = (ViewPager) findViewById(R.id.quiz_question_viewpager);
//        mPager.setOffscreenPageLimit(quiz.getQuestions().size() - 1);

      onPageChangeListener = new DetailOnPageChangeListener();

      mPager.addOnPageChangeListener(onPageChangeListener);


      mPager.setPageTransformer(false, new DefaultTransformer());
        mAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

      tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager, true);

      submittingFragment = new LoadingFragment(IndividualQuizActivity.this, "Submitting Quiz");

        halfwayDoneDrawable = ContextCompat.getDrawable(this, R.drawable.progress_bar_timer_halfway);
        threeQuartersDoneDrawable = ContextCompat.getDrawable(this, R.drawable.progress_bar_timer_three_quarters);


    }

  @Override
  protected void onResume() {
    super.onResume();

      Calendar expiryTime = new GregorianCalendar();
      expiryTime.setTime(quiz.getStartTime());
      expiryTime.add(Calendar.MINUTE, quiz.getTimedLength());

      final int timeBeforeExpiry = (int) (expiryTime.getTimeInMillis() - System.currentTimeMillis());


      quizTimerProgressBar.setMax(timeBeforeExpiry);
//      quizTimerProgressBar.setProgress(0);
//
//    //TODO: If using this timer; stop it when the user submits the quiz!!!
//
      countDownTimer = new CountDownTimer(timeBeforeExpiry, 1000) {
          boolean halfwayFlag = false;
          boolean threeQuartersFlag = false;

          @Override
          public void onTick(long l) {
//        quizTimerProgressBar.setProgress(quizTimerProgressBar.getMax() - (int) l);
              ObjectAnimator animation = ObjectAnimator.ofInt(quizTimerProgressBar, "progress", timeBeforeExpiry - (int) l);
              animation.setDuration(250); // 0.5 second
//          animation.setInterpolator(new DecelerateInterpolator());
              animation.start();

              if (halfwayFlag == false) {
                  if (l < timeBeforeExpiry / 2) {
                      halfwayFlag = true;
                      quizTimerProgressBar.setProgressDrawable(halfwayDoneDrawable);
                  }
              } else {
                  if (threeQuartersFlag == false) {
                      if (l < (timeBeforeExpiry / 4)) {
                          threeQuartersFlag = true;
                          quizTimerProgressBar.setProgressDrawable(threeQuartersDoneDrawable);
                      }
                  }
              }


          }

          @Override
          public void onFinish() {
              quizTimerProgressBar.setProgress(quizTimerProgressBar.getMax());
              userConfirmedSubmission();

          }
      }.start();
  }


    @Override
    protected void onStop() {
        super.onStop();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
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



    @Override
    public void submitButtonClicked() {

      //the user has clicked the "Finish" button

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
              snackbar
                  .setActionTextColor(ContextCompat.getColor(this, R.color.jccolorPrimaryBright));
                snackbar.setAction("GO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userAcknowledgedUnfinishedQuestions();
                    }
                });

                snackbar.show();
              snackbar.setCallback(new Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                  super.onDismissed(snackbar, event);
                  if (onPageChangeListener.getCurrentPage() == mAdapter.getCount() - 1) {
                    submitSnackBar.show();
                  }
                }
              });

            }
    }


    public void userAcknowledgedUnfinishedQuestions() {

        //Transition to first unanswered question
        int firstUnansweredIndex = 0;

        for(QuizQuestion question: quiz.getQuestions()){
            if(question.getPointsRemaining() != 0){
                mPager.setCurrentItem(firstUnansweredIndex);
              if (onPageChangeListener.getCurrentPage() == mAdapter.getCount() - 1) {
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

        //Set the quiz as finished; save it to SQLite
        //Then, post it to the server
        //If posting was successsful, and graded quiz is returned, save that graded quiz to SQLite for calculating stats later
        //then, go to group waiting area
        //If posting was unsuccessful, display error to the user

        //TODO: When the quiz expires, if posting is unsuccessful, what to do then? Perhaps post the quiz the next time a user clicks it on the homescreen?

      submittingFragment.show();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }


        //Save the quiz to the database one last time, because this method won't get called on the last page (only gets called on page change)
        IndividualQuizPersistence.sharedInstance(IndividualQuizActivity.this.getApplicationContext())
                .updateQuizInDatabase(quiz);


        quizNetworkingService
            .uploadQuiz(course.getCourseID(),
                UserDataSource.getInstance().getUser().getUserID(), quiz.getAssociatedSessionID(),
                quiz, new IndividualQuizPostCallback() {
                  @Override
                  public void onQuizPostSuccess(GradedQuiz gradedQuiz) {

                      quiz.setFinished(true);

                      //after setting the quiz to finished, save it to the database, so that when the user tries to open it after
                      //restarting the app, the quiz will be marked as finished
                      IndividualQuizPersistence.sharedInstance(IndividualQuizActivity.this.getApplicationContext())
                              .updateQuizInDatabase(quiz);

                      //set the id of the graded quiz to the id of the individual quiz
                      gradedQuiz.setId(quiz.getId());

                      boolean writeSuccess = GradedQuizPersistence.sharedInstance(IndividualQuizActivity.this).writeGradedQuizToDatabase(gradedQuiz);

                      if (writeSuccess == true) {
                          Intent i = new Intent(IndividualQuizActivity.this,
                                  GroupWaitingAreaActivity.class);
                          i.putExtra(GroupWaitingAreaActivity.EXTRA_COURSE, course);
                          i.putExtra(GroupWaitingAreaActivity.EXTRA_QUIZ, quiz);
                          startActivity(i);
                          submittingFragment.dismiss();
                          finish();

                      } else {
                          submittingFragment.dismiss();
                          Toast.makeText(IndividualQuizActivity.this, "Failed to save graded quiz to DB", Toast.LENGTH_LONG).show();

                      }

                  }

                  @Override
                  public void onQuizPostFailure(VolleyError error) {
                    submittingFragment.dismiss();
                    Toast.makeText(IndividualQuizActivity.this, "Can't submit quiz yet",
                        Toast.LENGTH_LONG).show();
                      finish();
                  }
                });

    }

    @Override
    public void userCanceledSubmission() {
        submitSnackBar.show();
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

  public class DetailOnPageChangeListener implements OnPageChangeListener {

    private int currentPage;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
      //Page was changed; save quiz
      currentPage = position;
      IndividualQuizPersistence.sharedInstance(IndividualQuizActivity.this.getApplicationContext())
          .updateQuizInDatabase(quiz);

      if (position == mAdapter.getCount() - 1) {
        submitSnackBar.show();
      } else {
        submitSnackBar.dismiss();
      }


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public final int getCurrentPage() {
      return currentPage;
    }
  }


}
