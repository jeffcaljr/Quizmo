package com.example.jeff.viewpagerdelete.IndividualQuiz.Controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Networking.QuizFetcher;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.QuizLoadingFragment;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.QuizStarterFragment;
import com.example.jeff.viewpagerdelete.R;

/**
 * Author: Jeffery Calhoun
 */

/**
 * README README README README README README README README
 * This class simulates loading a quiz from the network
 * In the final app, this behavior may be performed by Ryan's portion after a quiz code has been entered
 */

public class QuizLoaderActivity extends AppCompatActivity implements QuizLoadingFragment.QuizLoaderListener, QuizStarterFragment.QuizStarterListener, QuizFetcher.IndividualQuizFetcherListener {

    private Quiz quiz = null;

    private String quizID;

    public static final String LOADING_FRAGMENT_TAG = "LOADING_FRAGMENT_TAG";
    public static final String STARTER_FRAGMENT_TAG = "STARTER_FRAGMENT_TAG";



    FragmentManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_loader_activity_layout);

        manager = getSupportFragmentManager();
        final Context context = this;

        Bundle extras = getIntent().getExtras();

        if(extras != null && extras.containsKey("quizID")){
            quizID = extras.getString("quizID");

            QuizLoadingFragment loadingFragment = (QuizLoadingFragment) manager.findFragmentByTag(LOADING_FRAGMENT_TAG);

            if(loadingFragment == null){
                loadingFragment = new QuizLoadingFragment();
                Bundle quizLoadingExtras = new Bundle();
                quizLoadingExtras.putString("quizID", quizID);
                manager.beginTransaction()
                        .replace(R.id.quiz_loader_container_frame, loadingFragment)
                        .commit();

            }

        }
        else{
            //NOTE!: To link this app with Ryan's portion, comment out the following lines between the =====s,
            //and uncomment the last 2 lines of this block

            //===============================================================
//            quizID = "8e21fdc6-2a2a-4023-9a32-6313b3e142b1";
//            QuizLoadingFragment loadingFragment = (QuizLoadingFragment) manager.findFragmentByTag(LOADING_FRAGMENT_TAG);

//            if(loadingFragment == null){
//                loadingFragment = new QuizLoadingFragment();
//                Bundle quizLoadingExtras = new Bundle();
//                quizLoadingExtras.putString("quizID", quizID);
//                loadingFragment.setArguments(quizLoadingExtras);
//                manager.beginTransaction()
//                        .replace(R.id.quiz_loader_container_frame, loadingFragment)
//                        .commit();
                //===============================================================

            //The below code will likely be the behavior left in, the above code is for debugging purposes

            //UNCOMMENT THE FOLLOWING TWO LINES
            Log.e("QuizLoader", "QuizLoaderActivity expects to find an Intent extra named 'quizID' of type String - none was found!");
            finish();
//            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //QuizLoaderListener Methods


    @Override
    public void quizLoaded(Quiz quiz) {
        this.quiz = quiz;

        //switch to QuizStarter Fragment

        QuizStarterFragment starterFragment = (QuizStarterFragment) manager.findFragmentByTag(LOADING_FRAGMENT_TAG);

        if(starterFragment == null) {
            starterFragment = new QuizStarterFragment();
            manager.beginTransaction()
                    .replace(R.id.quiz_loader_container_frame, starterFragment)
                    .commit();
        }
    }

    @Override
    public void quizStartInitiated() {
        Intent i = new Intent(this, IndividualQuizActivity.class);
        i.putExtra("quiz", quiz);

        startActivity(i);
        finish();
    }

    @Override
    public void quizFecthed(Quiz q) {
        quizLoaded(q);
    }
}
