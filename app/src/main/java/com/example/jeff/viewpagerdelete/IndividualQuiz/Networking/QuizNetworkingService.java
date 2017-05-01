package com.example.jeff.viewpagerdelete.IndividualQuiz.Networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.Miscellaneous.RequestService;
import com.example.jeff.viewpagerdelete.Miscellaneous.ApiURLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jeff on 3/23/17.
 */

public class QuizNetworkingService {

    private Context context;


  public QuizNetworkingService(Context context) {
        this.context = context.getApplicationContext();
    }


  public void downloadUserQuizzes(String userID, final UserQuizzesDownloadCallback callback) {

    if (callback == null) {
            return;
        }

      String urlString = ApiURLs.quizzesURL + userID;

        JsonArrayRequest quizzesRequest = new JsonArrayRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                ArrayList<Course> courses = new ArrayList<>();

                for(int i = 0; i < response.length(); i++){

                    try {
                        Course course = new Course(response.getJSONObject(i));
                        courses.add(course);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

              callback.onUserQuizzesDownloadSuccess(courses);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              callback.onUserQuizzesDownloadFailure(error);
            }
        });

        RequestService.getInstance(this.context).addRequest(quizzesRequest);

    }


  public void downloadUserQuiz(String userID, String courseID, String quizCode, String token,
      final IndividualQuizDownloadCallback callback) {

    if (callback == null) {
            return;
        }

      String urlString =
          ApiURLs.quizURL + "?user_id=" + userID + "&" + "course_id=" + courseID + "&quiz_id="
              + quizCode + "&token=" + token;

        JsonObjectRequest request = new  JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String sessionID = response.getString("sessionId");
                    Quiz downloadedQuiz = new Quiz(response.getJSONObject("quiz"));
                    downloadedQuiz.setAssociatedSessionID(sessionID);

                    //TODO: quiz start time is set upon download; may not be the customer's wish
                    downloadedQuiz.setStartTime(new Date());
                  callback.onQuizDownloadSuccess(sessionID, downloadedQuiz);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Failed to load quiz from SQLite and from the network, load sample quiz from file

              callback.onQuizDownloadFailure(error);

            }
        });

        RequestService.getInstance(context).addRequest(request);
    }

  public void uploadQuiz(String courseID, String userID, String sessionID, final Quiz quiz,
      final IndividualQuizPostCallback callback) {

    if (callback == null) {
            return;
        }

      String urlString =
          ApiURLs.quizURL + "?course_id=" + courseID + "&user_id=" + userID + "&session_id="
              + sessionID;

        JSONObject quizJSONPost = quiz.toPostJSONFormat();
        Log.d("TAG",quizJSONPost.toString());
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, urlString, quizJSONPost, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              callback.onQuizPostSuccess(new GradedQuiz(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              callback.onQuizPostFailure(error);
            }
        });

        RequestService.getInstance(context).addRequest(postRequest);
    }


  public interface IndividualQuizDownloadCallback {
        void onQuizDownloadSuccess(String sessionID, Quiz q);
        void onQuizDownloadFailure(VolleyError error);
    }

  public interface IndividualQuizPostCallback {
        void onQuizPostSuccess(GradedQuiz quiz);
        void onQuizPostFailure(VolleyError error);
    }


  public interface UserQuizzesDownloadCallback {
        void onUserQuizzesDownloadSuccess(ArrayList<Course> course);
        void onUserQuizzesDownloadFailure(VolleyError error);
    }
}
