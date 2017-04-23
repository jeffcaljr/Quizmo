package com.example.jeff.viewpagerdelete.GroupQuiz.Networking;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.util.Log;
import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.UserGroupStatus;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.UserGroupStatus.Status;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService.GroupStatusDownloadCallback;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupWaitingQueueService.StatusCheckListener;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import java.util.ArrayList;

/**
 * Created by Jeff on 4/23/17.
 */

public class PollingService extends IntentService {

  private static final String TAG = "PollService";
  private static final String EXTRA_GROUP = "EXTRA_GROUP";
  private static final String EXTRA_COURSE = "EXTRA_COURSE";
  private static final String EXTRA_GRADED_QUIZ = "EXTRA_GRADED_QUIZ";
  private long lastTrigger;


  public static Intent newIntent(Context context) {
    Intent i = new Intent(context, PollingService.class);
    return i;
  }


  public PollingService() {
    super(TAG);
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    if (!isNetworkAvailableAndConnected()) {
      return;
    }
    Log.i(TAG, "Received an intent: " + intent);
  }

  private boolean isNetworkAvailableAndConnected() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

    boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
    boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
    return isNetworkConnected;
  }

}
