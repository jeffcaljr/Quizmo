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
import com.example.jeff.viewpagerdelete.IndividualQuiz.Networking.QuizFetcher;
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
            JsonObjectRequest request = QuizFetcher.sharedInstance(getActivity()).getQuizDownloadRequest(urlString);

            Volley.newRequestQueue(getActivity().getApplicationContext()).add(request);
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
