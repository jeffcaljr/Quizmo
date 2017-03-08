package com.example.jeff.viewpagerdelete;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * README README README README README README README README
 * This class simulates loading a quiz from the network
 * In the final app, this behavior may be performed by Ryan's portion after a quiz code has been entered
 */

public class QuizLoaderActivity extends AppCompatActivity {

//    private ArrayList<Quiz> quizzesArray;

    private String urlString = "https://immense-brushlands-50268.herokuapp.com/v1/quiz/8e21fdc6-2a2a-4023-9a32-6313b3e142b1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Context context = this;


//        quizzesArray = new ArrayList<>();

        //Loads one quiz (the mock quiz from the node service
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //If quiz successfully loaded from network, pass it via intent to the real activity

                Intent i = new Intent(getBaseContext(), IndividualQuizActivity.class);
                i.putExtra("quiz", response.toString());

                startActivity(i);
                finish();

                //Right now this Request only loads one quiz I have stored on a server, as all the mock quizzes provided to us are identical
                //The following commented-out code will likely be needed later to load multiple quizzes, so I am leaving it in for now

//                try {
//                    JSONArray quizzes = (JSONArray) response.get("quizzes");
//
//                    for(int i = 0; i < quizzes.length(); i++){
//                        quizzesArray.add(new Quiz((JSONObject) quizzes.get(i)));
//                    }
//
////                    Toast.makeText(context, "Loading worked", Toast.LENGTH_SHORT).show();
//
//
//                    Intent i = new Intent(getBaseContext(), IndividualQuizActivity.class);
//                    i.putExtra("quizzes", quizzesArray);
//
//                    startActivity(i);
//
//
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPONSE", error.toString());

                //TODO: Handle case where network fails to load quiz properly
                //Currently, if the quiz fails to load from the network for whatever reason, this error
                //response will get sample data stored in SampleJson class
                //This behavior is for debugging purposes and should be deleted later

                Intent i = new Intent(getBaseContext(), IndividualQuizActivity.class);
                i.putExtra("quiz", SampleJson.getSampleJSON());
                startActivity(i);
            }
        });


        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

}
