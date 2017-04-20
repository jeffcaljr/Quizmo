package com.example.jeff.viewpagerdelete.GroupQuiz.Networking;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by Jeff on 4/19/17.
 */

public class GroupWaitingQueueService extends Fragment {

    private StatusCheckListener listener;
    private Handler handler;
  private GroupNetworkingService groupNetworkingService;

    public interface StatusCheckListener {
        void updateStatus();
    }

    public void setStatusCheckListener(StatusCheckListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
      groupNetworkingService = new GroupNetworkingService(getContext());
    }

    public void startSequence() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());

            //delay start by 1 second
            handler.postDelayed(mRunnable, 1000);
        }
    }

    public void stopSequence() {
        if (handler != null) {
            handler.removeCallbacks(mRunnable);
            handler = null;
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (listener != null) {
                listener.updateStatus();
            }
            //check for quiz status updates every 7 seconds
            handler.postDelayed(mRunnable, 7000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
