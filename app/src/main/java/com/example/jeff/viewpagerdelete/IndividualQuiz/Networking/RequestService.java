package com.example.jeff.viewpagerdelete.IndividualQuiz.Networking;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonRequest;

/**
 * Created by Jeff on 2/11/17.
 */
public class RequestService {
    private static RequestService ourInstance = new RequestService();

    public static RequestService getInstance() {
        return ourInstance;
    }

    private RequestService() {
//        queue = Volley.newRequestQueue()
    }

    private RequestQueue queue;

    public void addRequest(JsonRequest request){
        this.queue.add(request);
    }
}
