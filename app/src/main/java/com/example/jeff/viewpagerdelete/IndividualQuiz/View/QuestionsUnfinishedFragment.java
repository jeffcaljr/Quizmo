package com.example.jeff.viewpagerdelete.IndividualQuiz.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.R;

import java.util.ArrayList;

/**
 * Created by Jeff on 3/8/17.
 */

public class QuestionsUnfinishedFragment extends DialogFragment {

    ArrayList<QuizQuestion> unansweredQuestions = new ArrayList<>();
    UnfinishedQuestionsInterface mListener = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.IndividualQuizDialogStyle);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        try{
            mListener = (UnfinishedQuestionsInterface) getActivity();
        } catch(ClassCastException e){
            e.printStackTrace();
        }

        dialogBuilder.setTitle("Uh-oh");

        StringBuilder questionsStringBuilder = new StringBuilder();
        questionsStringBuilder.append("Oops, you haven't fully completed the quiz:\n");

        Bundle args = getArguments();

        if(args != null && args.containsKey("unansweredQuestions")){

            unansweredQuestions = (ArrayList<QuizQuestion>) args.getSerializable("unansweredQuestions");
            dialogBuilder.setMessage(unansweredQuestions.size() + " unfinished questions");
            //TODO: Do something more ineteresting with the unanswered questions data, rather than just show a count
            // (potentially show question titles or question numbers)
        }
        else{
            dialogBuilder.setMessage("Error, cant parce unanswered questions from serializable to arraylist");
        }

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User cleared unanswered questions alert
//                //TODO: Is this creating a strong reference? Consider revising so this isnt the case
//                //TODO: Currently, this takes the user back to first quiz question when there are unanswered questions
//                    //It should take the to the first UNANSWERED QUESTION
//                ((IndividualQuizActivity) getActivity()).mPager.setCurrentItem(0);
            }
        });


        return dialogBuilder.create();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mListener != null){
            mListener.userAcknowledgedUnfinishedQuestions();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener = null;
    }

    public interface UnfinishedQuestionsInterface{
        void userAcknowledgedUnfinishedQuestions();
    }
}
