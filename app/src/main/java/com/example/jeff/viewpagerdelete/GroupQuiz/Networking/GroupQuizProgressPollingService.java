package com.example.jeff.viewpagerdelete.GroupQuiz.Networking;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers.GroupQuizActivity;
import com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers.GroupWaitingAreaActivity;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuiz;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;

/**
 * Created by Jeff on 4/26/17.
 */

public class GroupQuizProgressPollingService extends IntentService {

    private static final String TAG = "GroupQuizProgressPollingService";

    public static final String EXTRA_GROUP_ID = "EXTRA_GROUP_ID";
    public static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    public static final String EXTRA_QUIZ_ID = "EXTRA_QUIZ_ID";

    private static final int REQUEST_CODE = 1;

    private static final int POLL_INTERVAL = 7000;

    private static GroupNetworkingService groupNetworkingService;
    private static Quiz quiz;
    private static Group group;

    public GroupQuizProgressPollingService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        groupNetworkingService = new GroupNetworkingService(getApplicationContext());

        groupNetworkingService.getGroupQuizProgress(quiz, group, new GroupNetworkingService.GroupQuizProgressDownloadCallback() {
            @Override
            public void onGroupQuizProgressSuccess(GradedGroupQuiz gradedGroupQuiz) {
                Intent RTReturn = new Intent(GroupQuizActivity.RECEIVE_GROUP_PROGRESS);
                RTReturn.putExtra("gradedGroupQuiz", gradedGroupQuiz);
                LocalBroadcastManager.getInstance(GroupQuizProgressPollingService.this).sendBroadcast(RTReturn);
            }

            @Override
            public void onGroupQuizProgressFailure(VolleyError error) {
                Log.d("TAG", "eror retrieving group quiz");

            }
        });

    }

    public static Intent buildIntent(Context context, Group group, Quiz quiz) {
        Intent i = new Intent(context, GroupQuizProgressPollingService.class);
//        i.putExtra(EXTRA_QUIZ_ID, quizID);
//        i.putExtra(EXTRA_SESSION_ID, sessionID);
//        i.putExtra(EXTRA_GROUP_ID, groupID);
        return i;

    }

    public static void setServiceAlarm(Context context, boolean isOn, Group group, Quiz quiz) {

        Intent i = GroupQuizProgressPollingService.buildIntent(context, group, quiz);

        //TODO: Couldn't get #onHandleIntent to recieve extras after wrapping intent in PendingIntent inside #setServiceAlarm
        //TODO: As a temporary workaround, I set the paramaters needed for the network call through static variables. Should revise later


        GroupQuizProgressPollingService.group = group;
        GroupQuizProgressPollingService.quiz = quiz;
        PendingIntent pendingIntent = PendingIntent.getService(context, REQUEST_CODE, i, 0);


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
