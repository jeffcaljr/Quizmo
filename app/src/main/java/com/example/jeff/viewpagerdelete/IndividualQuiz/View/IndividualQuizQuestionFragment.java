package com.example.jeff.viewpagerdelete.IndividualQuiz.View;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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

import com.eftimoff.viewpagertransformers.StackTransformer;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Controller.IndividualQuizActivity;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.R;

/**
 * Created by Jeff on 2/10/17.
 */

public class IndividualQuizQuestionFragment extends Fragment implements IndividualQuizAnswerFragment.AnswerFragmentListener {

    public static final String ANSWER_EXTRA = "ANSWER_EXTRA";
    public static final String EXTRA_QUIZ_QUESTION_NUMBER = "EXTRA_QUIZ_QUESTION_NUMBER";

    QuizQuestion question;

    private PageFragmentListener mListener;

    private TextView mQuestionTextView;
    private TextView mPointsRemainingTextView;

    private TextView mQuestionLabelTextView;
    private TextView mQuestionTotalQuestions;

    private ViewPager mPager;
    private PagerAdapter mAdapter;

    private int questionNumber;
    private int totalQuestions;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_card, container, false);

        //Get QuizQuestion extra arg
        Bundle args = getArguments();
        question = (QuizQuestion) args.getSerializable("EXTRA_QUIZ_QUESTION");
        totalQuestions = args.getInt(IndividualQuizActivity.EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS);
        questionNumber = args.getInt(EXTRA_QUIZ_QUESTION_NUMBER);

        //Bind Views


        mQuestionTextView = (TextView) view.findViewById(R.id.question_text_tv);
        mPointsRemainingTextView = (TextView) view.findViewById(R.id.points_remaining_tv);
        mQuestionLabelTextView = (TextView) view.findViewById(R.id.quiz_question_value);

        mPointsRemainingTextView.setText(question.getPointsRemaining() + "");
        mQuestionLabelTextView.setText(questionNumber + "/" + totalQuestions);



        mQuestionTextView.setText(question.getText());



        mPager = (ViewPager) view.findViewById(R.id.answer_pager);
        mPager.setPageTransformer(true, new StackTransformer());
        mPager.setOffscreenPageLimit(question.getAvailableAnswers().size() - 1);
        mAdapter = new IndividualQuizQuestionFragment.ScreenSlidePagerAdapter(getChildFragmentManager());


        mPager.setAdapter(mAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager, true);


        return view;
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



    //MARK: AnswerFragmentListenerMethods

    @Override
    public void incrementButtonClicked() {
        mPointsRemainingTextView.setText("" + question.decrementPointsRemaining());
//        mListener.quizStateUpdated();
    }

    @Override
    public void decrementButtonClicked() {
        mPointsRemainingTextView.setText("" + question.incrementPointsRemaining());
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
