package com.example.jeff.viewpagerdelete.IndividualQuiz.Networking;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.IndividualQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.RequestService;
import com.example.jeff.viewpagerdelete.ServerProperties;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jeff on 3/23/17.
 */

public class QuizFetcher {

    private Context context;


    private static QuizFetcher quizFetcher;

    public static QuizFetcher sharedInstance(Context context){

        if(quizFetcher == null){
            quizFetcher = new QuizFetcher(context);
        }
        return quizFetcher;
    }

    private QuizFetcher(Context context){
        this.context = context.getApplicationContext();
    }


    public void submitQuizDownloadRequest(final IndividualQuizFetcherListener listener, String quizCode){

        if(listener == null){
            return;
        }

        String urlString = ServerProperties.quizURL + quizCode;

        JsonObjectRequest request = new  JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //If quiz successfully loaded from network, save it to sqlite

                Toast.makeText(context.getApplicationContext(), "Loading quiz from network", Toast.LENGTH_SHORT).show();

                Quiz downloadedQuiz = getQuizFromResponse(response);


                boolean writeSuccess = IndividualQuizPersistence.sharedInstance(context).writeIndividualQuizToDatabase(downloadedQuiz);

                if(writeSuccess){
                    listener.onQuizDownloadSuccess(downloadedQuiz);
                }
                else{
                    Toast.makeText(context.getApplicationContext(), "unable to write quiz to sqlite...", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Failed to load quiz from SQLite and from the network, load sample quiz from file

               listener.onQuizDownloadFailure(error);

            }
        });

        RequestService.getInstance(context).addRequest(request);
    }


    private Quiz getQuizFromResponse(JSONObject response){
        try {
            return new Quiz(new JSONObject(response.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface IndividualQuizFetcherListener{
        void onQuizDownloadSuccess(Quiz q);
        void onQuizDownloadFailure(VolleyError error);
    }
}
