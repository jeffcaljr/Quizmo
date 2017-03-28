package com.example.jeff.viewpagerdelete.IndividualQuiz.View;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizAnswer;
import com.example.jeff.viewpagerdelete.R;

/**
 * Created by Jeff on 3/1/17.
 */

public class IndividualQuizAnswerFragment extends Fragment {

    private AnswerFragmentListener mListener;

    private TextView mAnswerValue;
    private TextView mAnswerText;
    private TextView mPointsAllocated;
    private ImageButton mIncrementPointsAllocatedBtn;
    private ImageButton mDecrementPointsAllocatedBtn;

    private QuizAnswer answer;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_answer_card, container, false);

        //bind the views of the IndividualQuizAnswerFragment

        mAnswerValue = (TextView) view.findViewById(R.id.answer_value);
        mAnswerText = (TextView) view.findViewById(R.id.answer_textview);
        mPointsAllocated = (TextView) view.findViewById(R.id.points_allocated);
        mIncrementPointsAllocatedBtn = (ImageButton) view.findViewById(R.id.increment_points_button);
        mDecrementPointsAllocatedBtn = (ImageButton) view.findViewById(R.id.decrement_points_button);

        //Get answer sent as an argument from parent

        Bundle args = getArguments();

        answer = (QuizAnswer) args.getSerializable(IndividualQuizQuestionFragment.ANSWER_EXTRA);

        //Populate views with answer data

        if(answer != null){
            if(mAnswerValue != null){
                mAnswerValue.setText(answer.getValue());
            }

            if(mAnswerText != null){
                mAnswerText.setText(answer.getText());
            }

            if(mPointsAllocated != null){
                mPointsAllocated.setText(answer.getPointsAllocated() + "");
            }

            if(mIncrementPointsAllocatedBtn != null){
                mIncrementPointsAllocatedBtn.setOnClickListener(new IncrementButtonListener());
            }

            if(mDecrementPointsAllocatedBtn != null){
                mDecrementPointsAllocatedBtn.setOnClickListener(new DecrementButtonListener());
            }

        }
        else{
            Log.e("ANSWER_FRAGMENT", "Answer is null!");
        }


        /* Drawing a mask over an answer view
            CardView root = (CardView) view.findViewById(R.id.answer_card_root);
            View mask = new View(getActivity());
            mask.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mask.setBackgroundColor(Color.GREEN);
            mask.setAlpha(0.5f);

            mask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //absorb click events on the view
                }
            });
            root.addView(mask);
         */




        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IndividualQuizQuestionFragment parentFrag = (IndividualQuizQuestionFragment) getParentFragment();

        if(parentFrag != null && parentFrag instanceof AnswerFragmentListener){
            mListener = (AnswerFragmentListener) parentFrag;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }


    private class IncrementButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {

            if(mListener.getPointsUnallocated() > 0){
                mPointsAllocated.setText(answer.incrementPointsAllocated() + "");
                mListener.incrementButtonClicked();
            }
        }
    }

    private class DecrementButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(answer.getPointsAllocated() > 0){
                mPointsAllocated.setText(answer.decrementPointsAllocated() + "");
                mListener.decrementButtonClicked();
            }
        }
    }

    public interface AnswerFragmentListener{
        int getPointsUnallocated();
        int getPointsPossible();
        void incrementButtonClicked();
        void decrementButtonClicked();

    }

}
