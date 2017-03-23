package com.example.jeff.viewpagerdelete.IndividualQuiz.View;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.IndividualQuizDbHelper;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.QuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Misc.SampleJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jeff on 3/9/17.
 */

public class QuizLoadingFragment extends Fragment {

    private Quiz quiz;
    private String quizID;
    private String urlString = "https://immense-brushlands-50268.herokuapp.com/v1/quiz/";

    private ProgressBar spinner;
    private TextView loadingText;

    private QuizLoaderListener mListener;

    private IndividualQuizDbHelper dbHelper;
    private SQLiteDatabase dbWriteable;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quiz_loader_loading_fragment, container, false);



        spinner = (ProgressBar) view.findViewById(R.id.progressBar);
        loadingText = (TextView) view.findViewById(R.id.loading_text);

        quizID = getArguments().getString("quizID");
        urlString = urlString + quizID;


        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (QuizLoaderListener) context;
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        spinner.setVisibility(View.VISIBLE);

        dbHelper = new IndividualQuizDbHelper(getContext().getApplicationContext());
        dbWriteable = dbHelper.getWritableDatabase();

        //Attempt to load quiz from SQLite
        quiz = QuizPersistence.sharedInstance(getActivity()).readIndividualQuizFromDatabase(quizID);

        if(quiz != null){
            //Loaded Quiz From SQLite Successfully
            mListener.quizLoaded(quiz);
        }
        else{
            //Quiz not found in SQLite; attempt to load from network

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    //If quiz successfully loaded from network, save it to sqlite

                    Toast.makeText(getActivity().getApplicationContext(), "Loading quiz from network", Toast.LENGTH_SHORT).show();

                    try {
                        Quiz loadedQuiz = new Quiz(new JSONObject(response.toString()));

                        boolean writeSuccess = QuizPersistence.sharedInstance(getActivity()).writeIndividualQuizToDatabase(loadedQuiz);

                        if(writeSuccess){
                            quiz = loadedQuiz;
                            mListener.quizLoaded(quiz);
                        }
                        else{
                            Toast.makeText(getActivity().getApplicationContext(), "unable to write quiz to sqlite...", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Failed to load quiz from SQLite and from the network, load sample quiz from file
                    Log.e("RESPONSE", error.toString());
                    Toast.makeText(getContext().getApplicationContext(), "Loading quiz from sample file", Toast.LENGTH_SHORT).show();

                    //TODO: Handle case where network fails to load quiz properly
                    //Currently, if the quiz fails to load from the network for whatever reason, this error
                    //response will get sample data stored in SampleJson class
                    //This behavior is for debugging purposes and should be deleted later

                    try {
                        Quiz loadedQuiz = new Quiz(new JSONObject(SampleJson.getSampleJSON()));

                        boolean writeSuccess = QuizPersistence.sharedInstance(getActivity()).writeIndividualQuizToDatabase(loadedQuiz);

                        if(writeSuccess){
                            quiz = loadedQuiz;
                            mListener.quizLoaded(quiz);
                        }
                        else{
                            Toast.makeText(getActivity().getApplicationContext(), "unable to write quiz to sqlite...", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            Volley.newRequestQueue(getActivity().getApplicationContext()).add(jsonObjectRequest);
        }


    }

    public interface QuizLoaderListener{
        void quizLoaded(Quiz quiz);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
        dbWriteable = null;
    }
}
