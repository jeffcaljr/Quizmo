package edu.umsl.androidcs4020.frontrow_expressscriptsgroupproject_homepage_and_quizcode_cs4020;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static java.lang.Thread.sleep;


public class QuizCodeEntryFragment extends Fragment {

    //View objects
    Button submitCode;
    EditText enterCode;
    //Model object
    HomePage_and_QuizCode_Model quizCodeModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quiz_code_fragment_layout, container, false);

        submitCode = (Button) view.findViewById(R.id.submit_button);
        enterCode = (EditText) view.findViewById(R.id.code_input);

        if (quizCodeModel == null){
            quizCodeModel = new HomePage_and_QuizCode_Model();
        }

        //just for testing
        quizCodeModel.setQuizCode("123456789");
        //just for testing

        enterCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quizCodeModel.mUserCodeEntry = enterCode.getText().toString();
                quizCodeModel.validate_Code_Entry(); //remember to return getResult
                Log.e("QUIZCODE", "getResult = " + quizCodeModel.getReult());

                if (quizCodeModel.getReult() == "CORRECT CODE ENTERED") {
                    Toast.makeText(getActivity(), "New Quiz Added!", Toast.LENGTH_LONG).show();
                    Intent launchHomePageActivity = new Intent(getActivity(), HomePageActivity.class);
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(launchHomePageActivity);
                    //quizCodeModel.mGroupList.add(quizCodeModel.getNextQuiz()); //do this in model?
                    //need an Interface to update the MyQuizzesListFragment
                }
            }
        });

        return view;
    }

    //Override onAttach(Context context) { } ? ... see questions from project notes ...

    //Override onDestroy() { } ? ... see questions from project notes ...


}
