package com.example.jeff.viewpagerdelete.IndividualQuiz.View;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jeff.viewpagerdelete.R;

/**
 * Created by Jeff on 3/9/17.
 */

public class QuizStarterFragment extends Fragment {

    private QuizStarterListener mListener;

    private Button mStarQuizButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quiz_starter_fragment_layout, container, false);

        mStarQuizButton = (Button) view.findViewById(R.id.start_quiz_button);

        mStarQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.quizStartInitiated();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (QuizStarterListener) context;
        } catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    public interface QuizStarterListener{
        void quizStartInitiated();
    }
}
