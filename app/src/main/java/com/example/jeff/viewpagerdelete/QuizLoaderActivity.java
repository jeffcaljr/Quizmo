package com.example.jeff.viewpagerdelete;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jeff.viewpagerdelete.Models.Quiz;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author: Jeffery Calhoun
 */

/**
 * README README README README README README README README
 * This class simulates loading a quiz from the network
 * In the final app, this behavior may be performed by Ryan's portion after a quiz code has been entered
 */

public class QuizLoaderActivity extends AppCompatActivity {

    private Quiz quiz = null;

    private String quizID;
    private String urlString = "https://immense-brushlands-50268.herokuapp.com/v1/quiz/";

    private IndividualQuizDbHelper dbHelper;
    private SQLiteDatabase dbReadable;
    private SQLiteDatabase dbWritable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Context context = this;

//        final Intent receivedIntent = getIntent();

        Bundle extras = getIntent().getExtras();

        if(extras != null && extras.containsKey("quizID")){
            quizID = extras.getString("quizID");
            urlString = urlString + quizID;

            dbHelper = new IndividualQuizDbHelper(this);
            dbReadable = dbHelper.getReadableDatabase();
            dbWritable = dbHelper.getWritableDatabase();

        }
        else{
            //NOTE!: To link this app with Ryan's portion, comment out the following lines between the =====s,
                //and uncomment the last 2 lines of this block

            //===============================================================
            quizID = "8e21fdc6-2a2a-4023-9a32-6313b3e142b1";
            urlString = urlString + quizID;

            dbHelper = new IndividualQuizDbHelper(this);
            dbReadable = dbHelper.getReadableDatabase();
            dbWritable = dbHelper.getWritableDatabase();


            //The below code will likely be the behavior left in, the above code is for debugging purposes
            
            //UNCOMMENT THE FOLLOWING TWO LINES
//            Log.e("QuizLoader", "QuizLoaderActivity expects to find an Intent extra named 'quizID' of type String - none was found!");
//            finish();
        }

        //First, attempt to read quiz from sqlite database

        quiz = Quiz.readQuizFromDatabase(dbReadable, quizID);

        if (quiz != null) {
            Log.d("QUIZ_LOADER", "Loading quiz from sql file");
            Toast.makeText(context, "Loading quiz from sqlite", Toast.LENGTH_SHORT).show();


            Intent i = new Intent(context, IndividualQuizActivity.class);
            i.putExtra("quiz", quiz);

            startActivity(i);
            finish();

        } else {

            //if quiz not stored in sqlite, try to download quiz from network

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    //If quiz successfully loaded from network, save it to sqlite

                    Log.d("QUIZ_LOADER", "Loading quiz from network");
                    Toast.makeText(context, "Loading quiz from network", Toast.LENGTH_SHORT).show();

                    try {
                        Quiz loadedQuiz = new Quiz(new JSONObject(response.toString()));

                        boolean writeSuccess = loadedQuiz.writeQuizToDatabase(dbWritable);

                        if(writeSuccess){

                            quiz = loadedQuiz;

                            Intent i = new Intent(context, IndividualQuizActivity.class);
                            i.putExtra("quiz", quiz);

                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast.makeText(context, "unable to write quiz to sqlite...", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("RESPONSE", error.toString());
                    Log.d("QUIZ_LOADER", "Loading quiz from sample file");
                    Toast.makeText(context, "Loading quiz from sample file", Toast.LENGTH_SHORT).show();

                    //TODO: Handle case where network fails to load quiz properly
                    //Currently, if the quiz fails to load from the network for whatever reason, this error
                    //response will get sample data stored in SampleJson class
                    //This behavior is for debugging purposes and should be deleted later

                    try {
                        Quiz loadedQuiz = new Quiz(new JSONObject(SampleJson.getSampleJSON()));

                        boolean writeSuccess = loadedQuiz.writeQuizToDatabase(dbWritable);

                        if(writeSuccess){
                            quiz = loadedQuiz;

                            Intent i = new Intent(context, IndividualQuizActivity.class);
                            i.putExtra("quiz", quiz);

                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast.makeText(context, "unable to write quiz to sqlite...", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            Volley.newRequestQueue(this).add(jsonObjectRequest);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbReadable.close();
        dbWritable.close();
    }
}
