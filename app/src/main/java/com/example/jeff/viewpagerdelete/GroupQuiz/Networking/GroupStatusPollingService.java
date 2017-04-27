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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers.GroupWaitingAreaActivity;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.UserGroupStatus;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService.GroupStatusDownloadCallback;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;

import java.util.ArrayList;

/**
 * Created by Jeff on 4/23/17.
 */

//TODO: Couldn't get #onHandleIntent to recieve extras after wrapping intent in PendingIntent inside #setServiceAlarm
//TODO: As a temporary workaround, I set the paramaters needed for the network call through static variables. Should revise later

public class GroupStatusPollingService extends IntentService {

    private static final String TAG = "GroupStatusPollingService";

    public static final String EXTRA_GROUP = "EXTRA_GROUP";
    public static final String EXTRA_COURSE = "EXTRA_COURSE";
    public static final String EXTRA_QUIZ = "EXTRA_QUIZ";

    private static final int POLL_INTERVAL = 12000;

    private static Course course;
    private static Group group;
    private static Quiz quiz;

    private GroupNetworkingService groupNetworkingService;


    public GroupStatusPollingService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (!isNetworkAvailableAndConnected()) {
            return;
        }

//        Bundle extras = intent.getExtras();

//        if (extras != null && extras.containsKey(EXTRA_GROUP) && extras.containsKey(EXTRA_COURSE) && extras.containsKey(EXTRA_QUIZ)) {
//            course = (Course) extras.getSerializable(EXTRA_COURSE);
//            group = (Group) extras.getSerializable(EXTRA_GROUP);
//            quiz = (Quiz) extras.getSerializable(EXTRA_QUIZ);
//        } else {
//            return;
//        }

        groupNetworkingService = new GroupNetworkingService(getApplicationContext());

        groupNetworkingService.getGroupStatus(group, course, quiz, new GroupStatusDownloadCallback() {
            @Override
            public void onGroupStatusSuccess(ArrayList<UserGroupStatus> statuses) {
                System.out.println("intent Received");
                Intent RTReturn = new Intent(GroupWaitingAreaActivity.RECEIVE_JSON);
                RTReturn.putExtra("json", statuses);
                LocalBroadcastManager.getInstance(GroupStatusPollingService.this).sendBroadcast(RTReturn);
            }

            @Override
            public void onGroupStatusFailure(VolleyError error) {
                Toast.makeText(GroupStatusPollingService.this.getApplicationContext(), "Error checking status", Toast.LENGTH_LONG).show();
            }
        });

    }

    public static Intent buildIntent(Context context, Group group, Course course, Quiz quiz) {
        Intent i = new Intent(context, GroupStatusPollingService.class);
//    i.putExtra(EXTRA_GROUP, group);
//    i.putExtra(EXTRA_COURSE, course);
//    i.putExtra(EXTRA_QUIZ, quiz);

        return i;
    }

    public static void setServiceAlarm(Context context, boolean isOn, Group group, Course course, Quiz quiz) {

        Intent i = GroupStatusPollingService.buildIntent(context, group, course, quiz);

        //TODO: Couldn't get #onHandleIntent to recieve extras after wrapping intent in PendingIntent inside #setServiceAlarm
        //TODO: As a temporary workaround, I set the paramaters needed for the network call through static variables. Should revise later
//        i.putExtra(com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupStatusPollingService.EXTRA_GROUP, group);
//        i.putExtra(com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupStatusPollingService.EXTRA_COURSE, course);
//        i.putExtra(com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupStatusPollingService.EXTRA_QUIZ, quiz);

        GroupStatusPollingService.group = group;
        GroupStatusPollingService.quiz = quiz;
        GroupStatusPollingService.course = course;
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
