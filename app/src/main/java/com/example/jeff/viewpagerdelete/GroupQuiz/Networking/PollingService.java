package com.example.jeff.viewpagerdelete.GroupQuiz.Networking;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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
  private static final String EXTRA_QUIZ = "EXTRA_QUIZ";

  private static final int POLL_INTERVAL = 5000;

  private Course course;
  private Group group;
  private Quiz quiz;

  private GroupNetworkingService groupNetworkingService;


  public PollingService() {
    super(TAG);
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    Log.d("", "");

    if (!isNetworkAvailableAndConnected()) {
      return;
    }

    Bundle extras = intent.getExtras();

    if (extras != null && extras.containsKey(EXTRA_GROUP) && extras.containsKey(EXTRA_COURSE) && extras.containsKey(EXTRA_QUIZ)) {
      course = (Course) extras.getSerializable(EXTRA_COURSE);
      group = (Group) extras.getSerializable(EXTRA_GROUP);
      quiz = (Quiz) extras.getSerializable(EXTRA_QUIZ);
    } else {
      return;
    }

    groupNetworkingService = new GroupNetworkingService(getApplicationContext());

    groupNetworkingService.getGroupStatus(group, course, quiz, new GroupStatusDownloadCallback() {
      @Override
      public void onGroupStatusSuccess(ArrayList<UserGroupStatus> statuses) {
        Toast.makeText(PollingService.this.getApplicationContext(), statuses.toString(), Toast.LENGTH_LONG).show();
      }

      @Override
      public void onGroupStatusFailure(VolleyError error) {
        Toast.makeText(PollingService.this.getApplicationContext(), "Error checking status", Toast.LENGTH_LONG).show();
      }
    });

    Log.i(TAG, "Received an intent: " + intent);
  }

  public static Intent buildIntent(Context context, Group group, Course course, Quiz quiz) {
    Intent i = new Intent(context, PollingService.class);
    i.putExtra(EXTRA_GROUP, group);
    i.putExtra(EXTRA_COURSE, course);
    i.putExtra(EXTRA_QUIZ, quiz);

    return i;
  }

  public static void setServiceAlarm(Context context, boolean isOn, Group group, Course course, Quiz quiz) {

    Intent i = PollingService.buildIntent(context, group, course, quiz);
    PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);


    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

    if (isOn) {
      alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent);

    } else {
      alarmManager.cancel(pendingIntent);
      pendingIntent.cancel();
    }
  }

  private boolean isNetworkAvailableAndConnected() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

    boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
    boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
    return isNetworkConnected;
  }

}
