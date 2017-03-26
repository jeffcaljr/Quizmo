package com.example.jeff.viewpagerdelete.IndividualQuiz.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Jeff on 2/10/17.
 */

    //TODO: Add functionality to check which questions haven't had all available points allocated, display
    //that info to the user, and prevent quiz submission
    //Note: This may be handled by a different dialog fragment

public class SubmissionAlertFragment extends android.support.v4.app.DialogFragment {

    private SubmissionAlertFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        try{
            mListener = (SubmissionAlertFragmentListener) getActivity();
        } catch(ClassCastException e){
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you're ready to submit the quiz?\nYou cannot go back later.")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(mListener != null){
                            mListener.userConfirmedSubmission();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    public interface SubmissionAlertFragmentListener{
        void userConfirmedSubmission();
    }
}
