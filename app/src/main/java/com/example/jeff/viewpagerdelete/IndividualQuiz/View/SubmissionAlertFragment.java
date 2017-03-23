package com.example.jeff.viewpagerdelete.IndividualQuiz.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you're ready to submit the quiz?\nYou cannot go back later.")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity().getApplicationContext(), "At this point, the application would check that all confidence points have been allocated and move to the group quiz portion.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        return builder.create();
    }
}
