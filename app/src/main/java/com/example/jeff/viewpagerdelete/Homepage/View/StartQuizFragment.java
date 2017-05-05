package com.example.jeff.viewpagerdelete.Homepage.View;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import info.hoang8f.widget.FButton;

/**
 * Created by Jeff on 5/4/17.
 */

public class StartQuizFragment extends Fragment {

    public static final String TAG = "StartQuizFragment";

    public static final String ARG_COURSE = "ARG_COURSE";

    private Course course;
    private Quiz quiz;

    private TextView quizNameTextView;
    private TextView quizTimeInstructionTextView;

    private FButton startQuizButton;

    private OnQuizStartListener mOnQuizStartListener;


    public interface OnQuizStartListener {
        void onUserInitiatedQuizStart(Course thisCourse);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_quiz_start, container, false);

        Bundle args = getArguments();

        if (args != null
                && args.containsKey(ARG_COURSE)) {
            this.course = (Course) args.getSerializable(ARG_COURSE);
            this.quiz = course.getQuiz();
        } else {
            Log.e(TAG, "Args error");
        }

        quizNameTextView = (TextView) view.findViewById(R.id.start_quiz_quiz_name_label);
        quizTimeInstructionTextView = (TextView) view.findViewById(R.id.start_quiz_instruction_quiz_time);

        startQuizButton = (FButton) view.findViewById(R.id.start_quiz_button);

        quizNameTextView.setText(quiz.getDescription());
        quizTimeInstructionTextView.setText(getString(R.string.individual_quiz_instructions_2, quiz.getTimedLength()));

        startQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (quiz.getStartTime() != null) {

                    Calendar expiryTime = new GregorianCalendar();
                    expiryTime.setTime(quiz.getStartTime());
                    expiryTime.add(Calendar.MINUTE, quiz.getTimedLength());


                    if (expiryTime.getTime().before(new Date())) {
                        Toast.makeText(getActivity(), "No time remaining in quiz", Toast.LENGTH_SHORT).show();
                    } else {
                        mOnQuizStartListener.onUserInitiatedQuizStart(course);
                    }
                } else {
                    mOnQuizStartListener.onUserInitiatedQuizStart(course);
                }


            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnQuizStartListener = (OnQuizStartListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOnQuizStartListener = null;
    }
}
