package com.example.jeff.viewpagerdelete.IndividualQuiz.Networking;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by Jeff on 2/11/17.
 */
public class RequestService {

    private RequestQueue queue;

    private static RequestService ourInstance;

    public static RequestService getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new RequestService(context);
        }
        return ourInstance;
    }

    private RequestService(Context context) {
        queue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void addRequest(JsonRequest request){
        this.queue.add(request);
    }
}
