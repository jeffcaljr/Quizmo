package com.example.jeff.viewpagerdelete.GroupQuiz.Networking;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Jeff on 4/23/17.
 */

public class PollingService extends IntentService {

  private static final String TAG = "PollService";

  public static Intent newIntent(Context context) {
    return new Intent(context, PollingService.class);
  }


  public PollingService() {
    super(TAG);
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    if (!isNetworkAvailableAndConnected()) {
      return;
    }

//   new GroupNetworkingService(this).getGroupStatus(this, );
    Log.i(TAG, "Received an intent: " + intent);
  }

  private boolean isNetworkAvailableAndConnected() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

    boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
    boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
    return isNetworkConnected;
  }
}
