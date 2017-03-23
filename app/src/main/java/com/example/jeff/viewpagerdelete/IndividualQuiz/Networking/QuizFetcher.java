package com.example.jeff.viewpagerdelete.IndividualQuiz.Networking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.QuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Misc.SampleJson;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by Jeff on 3/23/17.
 */

public class QuizFetcher {

    private Context context;

    private static WeakReference<IndividualQuizFetcherListener> mListner;

    private static QuizFetcher quizFetcher;


    public static QuizFetcher sharedInstance(Context context){
        try{
            IndividualQuizFetcherListener listener = (IndividualQuizFetcherListener) context;
            mListner = new WeakReference<IndividualQuizFetcherListener>(listener);
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }

        if(quizFetcher == null){
            quizFetcher = new QuizFetcher(context);
        }
        return quizFetcher;
    }

    private QuizFetcher(Context context){
        this.context = context.getApplicationContext();
    }

    public JsonObjectRequest getQuizDownloadRequest(String urlString){
        return new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //If quiz successfully loaded from network, save it to sqlite

                Toast.makeText(context.getApplicationContext(), "Loading quiz from network", Toast.LENGTH_SHORT).show();

                Quiz downloadedQuiz = getQuizFromResponse(response);



                boolean writeSuccess = QuizPersistence.sharedInstance(context).writeIndividualQuizToDatabase(downloadedQuiz);

                if(writeSuccess){
                    mListner.get().quizFecthed(downloadedQuiz);
                }
                else{
                    Toast.makeText(context.getApplicationContext(), "unable to write quiz to sqlite...", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Failed to load quiz from SQLite and from the network, load sample quiz from file
                Log.e("RESPONSE", error.toString());
                Toast.makeText(context.getApplicationContext(), "Loading quiz from sample file", Toast.LENGTH_SHORT).show();

                //TODO: Handle case where network fails to load quiz properly
                //Currently, if the quiz fails to load from the network for whatever reason, this error
                //response will get sample data stored in SampleJson class
                //This behavior is for debugging purposes and should be deleted later

                try {
                    Quiz loadedQuiz = new Quiz(new JSONObject(SampleJson.getSampleJSON()));

                    boolean writeSuccess = QuizPersistence.sharedInstance(context).writeIndividualQuizToDatabase(loadedQuiz);

                    if(writeSuccess){
                        mListner.get().quizFecthed(loadedQuiz);
                    }
                    else{
                        Toast.makeText(context.getApplicationContext(), "unable to write quiz to sqlite...", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
        void quizFecthed(Quiz q);
    }
}
