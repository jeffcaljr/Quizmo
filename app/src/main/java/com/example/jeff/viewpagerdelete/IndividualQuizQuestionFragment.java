package com.example.jeff.viewpagerdelete;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jeff.viewpagerdelete.Models.Quiz;
import com.example.jeff.viewpagerdelete.Models.QuizQuestion;

/**
 * Created by Jeff on 2/10/17.
 */

public class IndividualQuizQuestionFragment extends Fragment implements IndividualQuizAnswerFragment.AnswerFragmentListener {

    public static final String ANSWER_EXTRA = "ANSWER_EXTRA";
    public static final String EXTRA_QUIZ_QUESTION_NUMBER = "EXTRA_QUIZ_QUESTION_NUMBER";

    QuizQuestion question;

    private PageFragmentListener mListener;

    private Button mNextButton;
    private TextView mQuestionTextView;
    private TextView mAnswerTextView;
    private TextView mPointsRemainingTextView;
    private TextView mQuestionNumberTextView;

    private ViewPager mPager;
    private PagerAdapter mAdapter;

    private String buttonText = "Next";
    private int questionNumber;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG", "IndividualQuizQuestionFragment onCreateView called");
        View view = inflater.inflate(R.layout.individual_quiz_question_fragment, container, false);

        //Get QuizQuestion extra arg
        Bundle args = getArguments();
        question = (QuizQuestion) args.getSerializable("EXTRA_QUIZ_QUESTION");

        String advanceButtonText = args.getString(IndividualQuizActivity.EXTRA_FINISH_BUTTON_TEXT);

        if(advanceButtonText != null && advanceButtonText != ""){
            this.buttonText = advanceButtonText;
        }

        //Bind Views


        mNextButton = (Button) view.findViewById(R.id.next_btn);
        mNextButton.setText(buttonText);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.advanceButtonClicked();
            }
        });

        mQuestionTextView = (TextView) view.findViewById(R.id.question_text_tv);
        mAnswerTextView = (TextView) view.findViewById(R.id.answer_textview);

        mQuestionNumberTextView = (TextView) view.findViewById(R.id.question_number_tv);
        mQuestionNumberTextView.setText("Q: " + args.getInt(EXTRA_QUIZ_QUESTION_NUMBER));



        mPointsRemainingTextView = (TextView) view.findViewById(R.id.points_remaining_tv);


        mPager = (ViewPager) view.findViewById(R.id.answer_pager);
        mPager.setOffscreenPageLimit(question.getAvailableAnswers().size() - 1);
        mAdapter = new IndividualQuizQuestionFragment.ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(question != null){
            mPointsRemainingTextView.setText(question.getPointsRemaining() + " points remaining");

            mQuestionTextView.setText(question.getText());
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (PageFragmentListener) context;
        }
        catch (ClassCastException e){
            Log.e("PAGE_FRAGMENT", "Class cast exception: couldn't cast activity to PageFragmentListener");
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface PageFragmentListener{
        void advanceButtonClicked();
//        void quizStateUpdated();


    }

    public void configureFromQuiz(Quiz quiz){
        mQuestionTextView.setText(quiz.getQuestionByIndex(0).getText());
        mAnswerTextView.setText(quiz.getQuestionByIndex(0).getAnswerByIndex(0).getText());
    }


    //MARK: AnswerFragmentListenerMethods

    @Override
    public void incrementButtonClicked() {
        mPointsRemainingTextView.setText(question.decrementPointsRemaining() + " points remaining");
//        mListener.quizStateUpdated();
    }

    @Override
    public void decrementButtonClicked() {
        mPointsRemainingTextView.setText(question.incrementPointsRemaining() + " points remaining");
//        mListener.quizStateUpdated();
    }

    @Override
    public int getPointsUnallocated() {
        return question.getPointsRemaining();
    }

    @Override
    public int getPointsPossible() {
        return question.getPointsPossible();
    }

    //MARK: FragmentPager class

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            IndividualQuizAnswerFragment newFrag = new IndividualQuizAnswerFragment();
            Bundle extras = new Bundle();
            if(position == question.getAvailableAnswers().size() - 1){
                //What to do if this is the last answer for the question
            }
            extras.putSerializable(ANSWER_EXTRA, question.getAvailableAnswers().get(position));
            newFrag.setArguments(extras);
            return newFrag;
        }

        @Override
        public int getCount() {
            return question.getAvailableAnswers().size();
        }
    }
}
