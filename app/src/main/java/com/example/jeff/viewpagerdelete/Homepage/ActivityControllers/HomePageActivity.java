package com.example.jeff.viewpagerdelete.Homepage.ActivityControllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.jeff.viewpagerdelete.R;


/*
Activity for the Homepage
By: Ryan Davis
 */

/*
Extract string = Alt+Enter when cursor is on the text to be extracted
Import missing library = Alt+Enter when cursor is on the Class that needs to be imported
 */

//HomePageActivity needs to be able to communicate with QuizCode Activity...Intents? (the fragments will communicate with the activities which can communicate with eachother?)

//HomePageActivity needs to be able to communicate with the Model class
//because HomePageActivity is a View-Controller for the ListFragment View-Controllers...

public class HomePageActivity extends AppCompatActivity {

    Button addNewQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Log.e("test", "HomePageOnCreate-tracer");

        addNewQuiz = (Button) findViewById(R.id.new_quiz_button);

        addNewQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //want to launch a new Activity by passing it an Explicit Intent
                Intent launchQuizCodeActivity = new Intent(HomePageActivity.this, QuizCodeActivity.class);
                startActivity(launchQuizCodeActivity);
            }
        });

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
        //quizListFragmentManager.someQuizFragmentInfo(instance_state);
        //groupListFragmentManager.someGroupFragmentInfo(instance_state);
        //Log.e("onCreateTAG", "onCreate method tracer for debugging");
        // }
        //}
        //if you have a background task running on a different thread...
        //implement another FragmentManager manager = getSupportFragmentManager();
        //mWorker_Fragment = (Worker_Fragment) manager.findFragmentByTag(TAG);
        //if (mWorker_Fragment == null) {
        //  mWorker_Fragment = new Worker_Fragment();
        //  manager.beginTransaction()
        //  .add(mWorker_Fragment, TAG);
        //  .commit();
        // }
        //instantiate any mWorker_Fragment.setListenerInterfacesYouMightWant(this);
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
}
