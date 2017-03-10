package edu.umsl.androidcs4020.frontrow_expressscriptsgroupproject_homepage_and_quizcode_cs4020;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/*
Activity for the QuizCode page
By: Ryan Davis
 */


public class QuizCodeActivity extends AppCompatActivity {

    //QuizCodeActivity needs to be able to communicate with HomePageActivity!
    //QuizCodeActivity needs to be able to communicate with the Model class because QuizCodeActivity is a View-Controller for the QuizCode Fragment View-Controller...

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



