package com.example.jeff.viewpagerdelete.Homepage.ActivityControllers;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.Homepage.View.QuizCodeEntryFragment;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Controller.IndividualQuizActivity;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.IndividualQuizDbHelper;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.IndividualQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Networking.QuizFetcher;
import com.example.jeff.viewpagerdelete.R;

/*
Activity for the QuizCode page
By: Ryan Davis
 */

public class QuizCodeActivity extends AppCompatActivity implements QuizCodeEntryFragment.QuizCodeEntryListener, QuizFetcher.IndividualQuizFetcherListener {
//QuizCodeActivity needs to be able to communicate with HomePageActivity!
    //QuizCodeActivity needs to be able to communicate with the Model class because QuizCodeActivity is a View-Controller for the QuizCode Fragment View-Controller...

    private static final String FRAG_TAG_QUIZ_CODE_ENTRY = "FRAG_TAG_QUIZ_CODE_ENTRY";

    private FragmentManager manager;
    private QuizCodeEntryFragment quizCodeEntryFragment;

    private SQLiteDatabase dbWriteable;
    private IndividualQuizDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_code_page);

        //Model modelObj = new Model();

        //Fragment managers for both my list fragments?

        //if (savedInstanceState != null) {
        //  String instance_state = savedInstanceState.getString("homepage_status");
        //      if (instance_state != null {
        //          //updates the model object after onDestroy
        //modelObj.setSomeInfo(instance_state);
        //updates one List Fragment View-Controller object, both List Fragments View-Controller objects?  (Not really sure)
        //via their respective Fragment Managers
        //ex:
        //quizCodeFragmentManager.someQuizFragmentInfo(instance_state);
        //Log.e("onCreateTAG", "onCreate method tracer for debugging");
        // }
        //}
        //if you have a background task running on a different thread...
        //? would you associate a worker fragment with one activity or another or both ?
        //implement another FragmentManager manager = getSupportFragmentManager();
        //mWorker_Fragment = (Worker_Fragment) manager.findFragmentByTag(TAG);
        //if (mWorker_Fragment == null) {
        //  mWorker_Fragment = new Worker_Fragment();
        //  manager.beginTransaction()
        //  .add(mWorker_Fragment, TAG);
        //  .commit();
        // }
        //instantiate any mWorker_Fragment.setListenerInterfacesYouMightWant(this);

        manager = getSupportFragmentManager();

        quizCodeEntryFragment = (QuizCodeEntryFragment) manager.findFragmentByTag(FRAG_TAG_QUIZ_CODE_ENTRY);

        if(quizCodeEntryFragment == null){
            quizCodeEntryFragment = new QuizCodeEntryFragment();

            manager.beginTransaction()
                    .replace(R.id.quiz_code_container, quizCodeEntryFragment, FRAG_TAG_QUIZ_CODE_ENTRY)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SomeType someVar = modelObj.getSomeInfo();
        //outState.putString("homepage_status", someVar)

        //tracer
        Log.e("onSaveTAG", "onSaveInstanceState method tracer for debugging");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroyTAG", "onDestroy method tracer for debuggign");
    }

    private void onQuizFound(Quiz quiz){
        Intent i = new Intent(this, IndividualQuizActivity.class);
        Log.d("QUIZ", quiz.toJSON());
        i.putExtra(IndividualQuizActivity.INTENT_EXTRA_QUIZ, quiz);

        startActivity(i);
        finish();
    }

    //MARK: QuizCodeEntryListener Methods


    @Override
    public void quizCodeSubmitted(String quizCode) {



        //========================================================
        //try to load quiz from SQLite, and if unsuccessful, try to load quiz from network

        dbHelper = new IndividualQuizDbHelper(this.getApplicationContext());
        dbWriteable = dbHelper.getWritableDatabase();

        //Attempt to load quiz from SQLite
        Quiz quiz = IndividualQuizPersistence.sharedInstance(this).readIndividualQuizFromDatabase(quizCode.trim());
        if(quiz != null){
            //Loaded Quiz From SQLite Successfully
            Log.d("TAG", quiz.toJSON());

            //TODO: Should check if quiz is in sqlite, and if true, display message to user indicating quiz was already downloaded
            //TODO: Currently takes user to quiz from SQLite; should display message to them that quiz was already loaded;
            Toast.makeText(this, "This quiz has already been saved locally", Toast.LENGTH_SHORT).show();
            onQuizFound(quiz);
        }
        else{
            QuizFetcher.sharedInstance(this).submitQuizDownloadRequest(this, quizCode);
        }

    }

    //MARK: IndividualQuizFetcherListener methods


    @Override
    public void onQuizDownloadSuccess(Quiz q) {
        if(quizCodeEntryFragment != null){
            quizCodeEntryFragment.setSubmitCodeButtonEnabled(true);
        }

        onQuizFound(q);
    }

    @Override
    public void onQuizDownloadFailure(VolleyError error) {
        Toast.makeText(this, "Failed to download quiz from network.\n" + error.getMessage(), Toast.LENGTH_LONG).show();
        if(quizCodeEntryFragment != null){
            quizCodeEntryFragment.setSubmitCodeButtonEnabled(true);
        }
    }
}