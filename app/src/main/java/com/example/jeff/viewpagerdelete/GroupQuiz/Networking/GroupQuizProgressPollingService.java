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
import com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers.GroupWaitingAreaActivity;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;

/**
 * Created by Jeff on 4/26/17.
 */

public class GroupQuizProgressPollingService extends IntentService {

    private static final String TAG = "GroupQuizProgressPollingService";

    public static final String EXTRA_GROUP_ID = "EXTRA_GROUP_ID";
    public static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    public static final String EXTRA_QUIZ_ID = "EXTRA_QUIZ_ID";

    private static final int POLL_INTERVAL = 12000;

    private static GroupNetworkingService groupNetworkingService;
    private static String quizID;
    private static String groupID;
    private static String sessionID;

    public GroupQuizProgressPollingService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        groupNetworkingService = new GroupNetworkingService(getApplicationContext());

        groupNetworkingService.getGroupQuizProgress(quizID, groupID, sessionID, new GroupNetworkingService.GroupQuizProgressDownloadCallback() {
            @Override
            public void onGroupQuizProgressSuccess(GradedGroupQuiz gradedGroupQuiz) {
                System.out.println("intent Received");
                Intent RTReturn = new Intent(GroupWaitingAreaActivity.RECEIVE_JSON);
                RTReturn.putExtra("json", gradedGroupQuiz);
                LocalBroadcastManager.getInstance(GroupQuizProgressPollingService.this).sendBroadcast(RTReturn);
            }

            @Override
            public void onGroupQuizProgressFailure(VolleyError error) {
                Log.d("TAG", "eror retrieving group quiz");

            }
        });

    }

    public static Intent buildIntent(Context context, String groupID, String quizID, String sessionID) {
        Intent i = new Intent(context, GroupQuizProgressPollingService.class);
//        i.putExtra(EXTRA_QUIZ_ID, quizID);
//        i.putExtra(EXTRA_SESSION_ID, sessionID);
//        i.putExtra(EXTRA_GROUP_ID, groupID);
        return i;

    }

    public static void setServiceAlarm(Context context, boolean isOn, String groupID, String quizID, String sessionID) {

        Intent i = buildIntent(context, groupID, quizID, sessionID);

        //TODO: Couldn't get #onHandleIntent to recieve extras after wrapping intent in PendingIntent inside #setServiceAlarm
        //TODO: As a temporary workaround, I set the paramaters needed for the network call through static variables. Should revise later


        GroupQuizProgressPollingService.groupID = groupID;
        GroupQuizProgressPollingService.quizID = quizID;
        GroupQuizProgressPollingService.sessionID = sessionID;
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