package com.example.jeff.viewpagerdelete.Homepage.View;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Networking.QuizNetworkingService;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

/**
 * Created by Jeff on 4/12/17.
 */

public class TokenCodeFragment extends Dialog implements
    QuizNetworkingService.IndividualQuizFetcherListener {

    private Context context;
    private Course course;

    private TokenCodeEntryListener mListener;

    private EditText tokenCodeField;
    private Button cancelButton;
    private Button submitButton;
    private TextView titleLabel;
    private TextView errorLabel;
    private LinearLayout errorLayout;

    private ProgressBar spinner;

  private QuizNetworkingService quizNetworkingService;

    public TokenCodeFragment(@NonNull Context context, Course course) {
        super(context);
        this.context = context;
        this.course = course;

      quizNetworkingService = new QuizNetworkingService(context);

        try{
            mListener = (TokenCodeEntryListener) context;
        } catch(ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_fragment_token_code);

      final QuizNetworkingService.IndividualQuizFetcherListener self = this;


        tokenCodeField = (EditText) findViewById(R.id.token_code_entry_field);
        cancelButton = (Button) findViewById(R.id.token_code_cancel_button);
        submitButton = (Button) findViewById(R.id.token_code_submit_button);
        titleLabel = (TextView) findViewById(R.id.token_code_dialog_title);
        errorLabel = (TextView) findViewById(R.id.token_code_error_textfield);
        errorLayout = (LinearLayout) findViewById(R.id.token_code_error_layout);

        setTypefaces();

        spinner = (ProgressBar) findViewById(R.id.token_code_progress_spinner);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                setCanceledOnTouchOutside(false);
                String tokenCode = tokenCodeField.getText().toString().trim();
                errorLayout.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
              quizNetworkingService
                  .downloadUserQuiz(self, UserDataSource.getInstance().getUser().getUserID(),
                      course.getCourseID(), course.getQuiz().getId(), tokenCode);
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mListener = null;
            }
        });


    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    private void setTypefaces(){
        //set type face of views
        Typeface regularFace = Typeface.createFromAsset(this.context.getAssets(),"fonts/robotoRegular.ttf");
        Typeface boldFace = Typeface.createFromAsset(this.context.getAssets(),"fonts/robotoBold.ttf");
        Typeface boldItalicFace = Typeface.createFromAsset(this.context.getAssets(),"fonts/robotoBoldItalic.ttf");

        tokenCodeField.setTypeface(regularFace);
        cancelButton.setTypeface(boldFace);
        submitButton.setTypeface(boldFace);
        titleLabel.setTypeface(boldFace);
        errorLabel.setTypeface(boldItalicFace);
    }


    public interface TokenCodeEntryListener{
        void quizDownloaded(Quiz quiz, Course course);
    }

    @Override
    public void onQuizDownloadSuccess(String sessionID, Quiz q) {

        spinner.setVisibility(View.GONE);
        mListener.quizDownloaded(q, course);
        dismiss();
    }

    @Override
    public void onQuizDownloadFailure(VolleyError error) {
        spinner.setVisibility(View.GONE);
        setCanceledOnTouchOutside(true);
        errorLabel.setText("Invalid token code");
        errorLayout.setVisibility(View.VISIBLE);
    }
}
