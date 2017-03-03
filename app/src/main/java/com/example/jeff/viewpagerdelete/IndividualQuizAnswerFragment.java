package com.example.jeff.viewpagerdelete;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jeff.viewpagerdelete.Models.QuizAnswer;

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
        View view = inflater.inflate(R.layout.answer_fragment, container, false);

        //bind the views of the IndividualQuizAnswerFragment

        mAnswerValue = (TextView) view.findViewById(R.id.answer_value);
        mAnswerText = (TextView) view.findViewById(R.id.answer_textview);
        mPointsAllocated = (TextView) view.findViewById(R.id.points_allocated_tv);
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
//                mPointsAllocated.setText(0);
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
    public void onAttach(Context context) {
        super.onAttach(context);

        //As this is a nested fragment, what is it being attatched to?
        //I intend to cast the mListener to the parent fragment
        //Currently, this task is done in this.onViewCreated()

//        try{
//            mListener = (AnswerFragmentListener) context;
//        }
//        catch(ClassCastException e){
//            e.printStackTrace();
//        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private class IncrementButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            mListener.incrementButtonClicked(Integer.parseInt(mPointsAllocated.getText().toString()));
        }
    }

    private class DecrementButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            mListener.decrementButtonClicked(Integer.parseInt(mPointsAllocated.getText().toString()));
        }
    }

    public interface AnswerFragmentListener{
        void incrementButtonClicked(int pointsAllocated);
        void decrementButtonClicked(int pointsAllocated);
    }

}
