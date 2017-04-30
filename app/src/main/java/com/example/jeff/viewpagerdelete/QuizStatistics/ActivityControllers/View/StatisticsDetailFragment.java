package com.example.jeff.viewpagerdelete.QuizStatistics.ActivityControllers.View;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuizAnswer;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuizQuestion;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuizAnswer;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuizQuestion;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizAnswer;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.Miscellaneous.ScrollableTextTouchListener;
import com.example.jeff.viewpagerdelete.R;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.Collections;

/**
 * Created by Jeff on 4/30/17.
 */

public class StatisticsDetailFragment extends Fragment {

    public static final String TAG = "StatsDetailFragment";

    public static final String ARG_EXTRA_QUESTION_NUMBER = "ARG_EXTRA_QUESTION_NUMBER";
    public static final String ARG_EXTRA_QUIZ_QUESTION = "ARG_EXTRA_QUIZ_QUESTION";
    public static final String ARG_EXTRA_GRADED_QUESTION_INDIVIDUAL = "ARG_EXTRA_GRADED_QUESTION_INDIVIDUAL";
    public static final String ARG_EXTRA_GRADED_QUESTION_GROUP = "ARG_EXTRA_GRADED_QUESTION_GROUP";


    private QuizQuestion mQuizQuestion;
    private GradedQuizQuestion mGradedIndividualQuizQuestion;
    private GradedGroupQuizQuestion mGradedGroupQuizQuestion;


    private TextView mQuestionNumberTextView;
    private TextView mCorrectAnswerTextView;
    private TextView mQuestionTextView;

    private TextView mIndividualQuizPointAllocationA;
    private TextView mIndividualQuizPointAllocationB;
    private TextView mIndividualQuizPointAllocationC;
    private TextView mIndividualQuizPointAllocationD;

    private RecyclerView mRecyclerView;

    private int mQuestionNumber;

    private Drawable correctDrawable;
    private Drawable incorrectDrawable;
    private int correctMaskColor;
    private int incorrectMaskColor;
    private int unansweredMaskColor;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_detail_view, container, false);

        Bundle args = getArguments();

        if (args != null
                && args.containsKey(ARG_EXTRA_QUESTION_NUMBER)
                && args.containsKey(ARG_EXTRA_QUIZ_QUESTION) && args.containsKey(ARG_EXTRA_GRADED_QUESTION_INDIVIDUAL)
                && args.containsKey(ARG_EXTRA_GRADED_QUESTION_GROUP)) {


            mQuestionNumber = args.getInt(ARG_EXTRA_QUESTION_NUMBER);
            mQuizQuestion = (QuizQuestion) args.getSerializable(ARG_EXTRA_QUIZ_QUESTION);
            mGradedIndividualQuizQuestion = (GradedQuizQuestion) args.getSerializable(ARG_EXTRA_GRADED_QUESTION_INDIVIDUAL);
            mGradedGroupQuizQuestion = (GradedGroupQuizQuestion) args.getSerializable(ARG_EXTRA_GRADED_QUESTION_GROUP);

            Collections.sort(mQuizQuestion.getAvailableAnswers());
            Collections.sort(mGradedIndividualQuizQuestion.getSubmittedAnswers());
            Collections.sort(mGradedGroupQuizQuestion.getGradedAnswers());

        } else {
            Log.e(TAG, "Arg error");
        }


        mQuestionNumberTextView = (TextView) view.findViewById(R.id.statistics_question_number_label);
        mCorrectAnswerTextView = (TextView) view.findViewById(R.id.statistics_correct_answer_label);
        mQuestionTextView = (TextView) view.findViewById(R.id.statistics_quiz_question_text);

        mIndividualQuizPointAllocationA = (TextView) view.findViewById(R.id.statistics_individual_point_allocation_tv_a);
        mIndividualQuizPointAllocationB = (TextView) view.findViewById(R.id.statistics_individual_point_allocation_tv_b);
        mIndividualQuizPointAllocationC = (TextView) view.findViewById(R.id.statistics_individual_point_allocation_tv_c);
        mIndividualQuizPointAllocationD = (TextView) view.findViewById(R.id.statistics_individual_point_allocation_tv_d);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.statistics_group_answers_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mQuestionNumberTextView.setText(mQuestionNumber + ".");

        //determine the correct answer
        GradedGroupQuizAnswer correctAnswer = null;

        for (GradedGroupQuizAnswer answer : mGradedGroupQuizQuestion.getGradedAnswers()) {
            if (answer.isCorrect()) {
                correctAnswer = answer;
                break;
            }
        }

        mCorrectAnswerTextView.setText(correctAnswer.getValue());
        mQuestionTextView.setText(mQuizQuestion.getText());

        Collections.sort(mGradedIndividualQuizQuestion.getSubmittedAnswers());
        Collections.sort(mGradedGroupQuizQuestion.getGradedAnswers());

        //TODO: The proceeding code is a sloppy implementation; will revise later

        mIndividualQuizPointAllocationA.setText("A: " + mQuizQuestion.getAnswerByValue("A").getPointsAllocated());
        mIndividualQuizPointAllocationB.setText("B: " + mQuizQuestion.getAnswerByValue("B").getPointsAllocated());
        mIndividualQuizPointAllocationC.setText("C: " + mQuizQuestion.getAnswerByValue("C").getPointsAllocated());
        mIndividualQuizPointAllocationD.setText("D: " + mQuizQuestion.getAnswerByValue("D").getPointsAllocated());

        //TODO: The preceeding code is a sloppy implementation; will revise later


        correctMaskColor = ContextCompat.getColor(getActivity(), R.color.jccolorCorrectMask);
        incorrectMaskColor = ContextCompat.getColor(getActivity(), R.color.jccolorIncorrectMask);
        unansweredMaskColor = ContextCompat.getColor(getActivity(), R.color.jccolorUnansweredMask);
        correctDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_correct_white);
        incorrectDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_incorrect_white);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.statistics_group_answers_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.setAdapter(new GroupAnswersAdapter());


        return view;
    }

    private class GroupAnswersAdapter extends RecyclerView.Adapter<GroupAnswerHolder> {

        @Override
        public GroupAnswerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = (View) inflater
                    .inflate(R.layout.item_group_quiz_answer, parent, false);
            return new GroupAnswerHolder(view);
        }

        @Override
        public void onBindViewHolder(GroupAnswerHolder holder, int position) {
            QuizAnswer answer = mQuizQuestion.getAvailableAnswers().get(position);
            GradedGroupQuizAnswer thisGradedAnswer = null;

            if (mGradedGroupQuizQuestion != null) {
                for (GradedGroupQuizAnswer a : mGradedGroupQuizQuestion.getGradedAnswers()) {
                    if (a.getValue().equals(answer.getValue())) {
                        thisGradedAnswer = a;
                        break;
                    }
                }
            }

            holder.bindView(answer, thisGradedAnswer);

        }

        @Override
        public int getItemCount() {
            return mQuizQuestion.getAvailableAnswers().size();
        }
    }

    private class GroupAnswerHolder extends RecyclerView.ViewHolder {

        private TextView mAnswerValue;
        private TextView mAnswerText;
        private ImageView mResultLabel;
        private Button mSubmitAnswerButton;
        private TextView mAnswerTextPreview;

        private RelativeLayout answerCardHeader;
        private ExpandableRelativeLayout answerCardContent;

        private View answerMask;


        public GroupAnswerHolder(View itemView) {
            super(itemView);

            mAnswerValue = (TextView) itemView.findViewById(R.id.quiz_answer_label);
            mAnswerText = (TextView) itemView.findViewById(R.id.quiz_answer_text);
            mResultLabel = (ImageView) itemView.findViewById(R.id.group_quiz_answer_result_icon);
            mSubmitAnswerButton = (Button) itemView.findViewById(R.id.group_quiz_answer_submit_btn);
            mAnswerTextPreview = (TextView) itemView.findViewById(R.id.quiz_answer_text_preview);
            answerCardHeader = (RelativeLayout) itemView.findViewById(R.id.quiz_answer_card_header);
            answerCardContent = (ExpandableRelativeLayout) itemView
                    .findViewById(R.id.quiz_answer_card_content);

            answerMask = (View) itemView.findViewById(R.id.group_quiz_answer_graded_mask);

            answerCardContent.setListener(new ExpandableLayoutListenerAdapter() {
                @Override
                public void onPreOpen() {
                    mAnswerTextPreview.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onClosed() {
                    mAnswerTextPreview.setVisibility(View.VISIBLE);
                }
            });
        }

        public void bindView(final QuizAnswer answer, GradedGroupQuizAnswer gradedAnswer) {
            mAnswerValue.setText(answer.getValue() + ".");
            mAnswerText.setText(answer.getText());
            mAnswerText.setMovementMethod(new ScrollingMovementMethod());
            mAnswerTextPreview.setText(answer.getText());
            mAnswerText.setOnTouchListener(new ScrollableTextTouchListener());


            //if answer card header is clicked, toggle collapsed state

            answerCardHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answerCardContent.toggle();
                }
            });

            mSubmitAnswerButton.setVisibility(View.GONE);

            //if the question has been answered, and the answer hasn't, it is an unsubmitted answer

            if (gradedAnswer == null) {
                mResultLabel.setVisibility(View.INVISIBLE);
                answerMask.setBackgroundColor(unansweredMaskColor);
                answerMask.setVisibility(View.VISIBLE);

            } else if (gradedAnswer.isCorrect()) {
                mResultLabel.setImageDrawable(correctDrawable);
                answerMask.setBackgroundColor(correctMaskColor);
                mResultLabel.setVisibility(View.VISIBLE);
                answerMask.setVisibility(View.VISIBLE);
            } else {
                mResultLabel.setImageDrawable(incorrectDrawable);
                answerMask.setBackgroundColor(incorrectMaskColor);
                mResultLabel.setVisibility(View.VISIBLE);
                answerMask.setVisibility(View.VISIBLE);

            }

        }
    }
}
