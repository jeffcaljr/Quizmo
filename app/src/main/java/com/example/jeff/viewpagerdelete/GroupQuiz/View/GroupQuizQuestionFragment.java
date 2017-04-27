package com.example.jeff.viewpagerdelete.GroupQuiz.View;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuizAnswer;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuizQuestion;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizAnswer;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.IndividualQuizQuestionFragment;
import com.example.jeff.viewpagerdelete.IndividualQuiz.View.IndividualQuizQuestionFragment.PageFragmentListener;
import com.example.jeff.viewpagerdelete.Miscellaneous.ScrollableTextTouchListener;
import com.example.jeff.viewpagerdelete.R;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Jeff on 4/23/17.
 */

public class GroupQuizQuestionFragment extends Fragment {

    public static final String TAG = "GroupQuizQuestionFrag";
    public static final String ANSWER_EXTRA = "ANSWER_EXTRA";
    public static final String ARG_QUIZ_QUESTION_NUMBER = "ARG_QUIZ_QUESTION_NUMBER";
    public static final String ARG_QUIZ_QUESTION = "ARG_QUIZ_QUESTION";
    public static final String ARG_GRADED_QUIZ_QUESTION = "ARG_GRADED_QUIZ_QUESTION";

    private QuizQuestion question;
    private GradedGroupQuizQuestion gradedGroupQuizQuestion;

    private PageFragmentListener mListener;


    private TextView mQuestionTextView;
    private TextView mPointsEarnedTextView;
    private TextView mQuestionLabelTextView;

    private RecyclerView recyclerView;
    private AnswerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private ImageButton toggleAnswersCollapsedButton;
    private boolean toggleExpandedButtonState = true;

    private Drawable collapsedDrawable;
    private Drawable expandedDrawable;
    private Drawable correctDrawable;
    private Drawable incorrectDrawable;
    private int correctMaskColor;
    private int incorrectMaskColor;
    private int unansweredMaskColor;


    private int questionNumber;

    private boolean allAnswersCollapsed = false;
    private boolean allAnswersExpanded = true;

    private boolean[] expandedStates;

    private OnGroupQuizAnswerSelectedListener answerSelectedListener;

    private GroupNetworkingService groupNetworkingService;

    //TODO: Test code; delete proceeding later

    private ArrayList<GradedGroupQuizAnswer> sampleGradedAnswers;

    //TODO: Test code; delete preceeding later

    public interface OnGroupQuizAnswerSelectedListener {

        void answerSelected(QuizQuestion question, QuizAnswer answer,
                            GroupQuizQuestionFragment currentQuestionFragment);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_quiz_question_page, container, false);

        //Get QuizQuestion extra arg
        Bundle args = getArguments();

        if (args != null && args.containsKey(ARG_QUIZ_QUESTION) && args
                .containsKey(ARG_QUIZ_QUESTION_NUMBER)) {
            question = (QuizQuestion) args.getSerializable(ARG_QUIZ_QUESTION);
            questionNumber = args.getInt(ARG_QUIZ_QUESTION_NUMBER);
            Collections.sort(question.getAvailableAnswers());
        } else {
            Log.e(TAG, "Args error");
        }


        //Bind Views

        mQuestionTextView = (TextView) view.findViewById(R.id.quiz_question_text);
        mPointsEarnedTextView = (TextView) view
                .findViewById(R.id.quiz_question_points_earned_label);
        mQuestionLabelTextView = (TextView) view.findViewById(R.id.quiz_question_number_label);

        if (args.containsKey(ARG_GRADED_QUIZ_QUESTION)) {
            gradedGroupQuizQuestion = (GradedGroupQuizQuestion) args.getSerializable(ARG_GRADED_QUIZ_QUESTION);

            //check if the question was answered correctly; and if so, set the "Group Earned Points" textField text
            if (gradedGroupQuizQuestion != null) {
                for (GradedGroupQuizAnswer gradedAnswer : gradedGroupQuizQuestion.getGradedAnswers()) {
                    if (gradedAnswer.isCorrect()) {
                        gradedGroupQuizQuestion.setAnsweredCorrectly(true);
                        mPointsEarnedTextView.setText(gradedAnswer.getPoints() + "");
                    }
                }
            }
        }


        mQuestionLabelTextView.setText(questionNumber + ".");

        mQuestionTextView.setText(question.getText());
        mQuestionTextView.setMovementMethod(new ScrollingMovementMethod());

        recyclerView = (RecyclerView) view.findViewById(R.id.quiz_answer_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        adapter = new AnswerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        collapsedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_collapse);
        expandedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_expand);
        correctMaskColor = ContextCompat.getColor(getActivity(), R.color.jccolorCorrectMask);
        incorrectMaskColor = ContextCompat.getColor(getActivity(), R.color.jccolorIncorrectMask);
        unansweredMaskColor = ContextCompat.getColor(getActivity(), R.color.jccolorUnansweredMask);

        toggleAnswersCollapsedButton = (ImageButton) view
                .findViewById(R.id.quiz_question_toggle_answers_collapse_button);

        toggleAnswersCollapsedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (toggleExpandedButtonState == false) {
                    allAnswersCollapsed = false;
                    allAnswersExpanded = true;
                    Arrays.fill(expandedStates, true);
                    toggleAnswersCollapsedButton.setImageDrawable(expandedDrawable);
                    toggleExpandedButtonState = true;


                } else {
                    allAnswersCollapsed = true;
                    allAnswersExpanded = false;
                    Arrays.fill(expandedStates, false);
                    toggleAnswersCollapsedButton.setImageDrawable(collapsedDrawable);
                    toggleExpandedButtonState = false;
                }

                adapter.notifyDataSetChanged();

            }
        });

        expandedStates = new boolean[question.getAvailableAnswers().size()];
        Arrays.fill(expandedStates, true);


        correctDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_correct_white);
        incorrectDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_incorrect_white);

        groupNetworkingService = new GroupNetworkingService(getActivity());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            answerSelectedListener = (OnGroupQuizAnswerSelectedListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        answerSelectedListener = null;
        mListener = null;
    }

    public void onGradeRecieved(GradedGroupQuizQuestion gradedQuestion) {
        this.gradedGroupQuizQuestion = gradedQuestion;
        //check if the question was answered correctly; and if so, set the "Group Earned Points" textField text
        if (gradedQuestion.isAnsweredCorrectly()) {
            for (GradedGroupQuizAnswer gradedAnswer : gradedQuestion.getGradedAnswers()) {
                if (gradedAnswer.isCorrect()) {
                    gradedGroupQuizQuestion.setAnsweredCorrectly(true);
                    mPointsEarnedTextView.setText(gradedAnswer.getPoints() + "");

                    //hide all currently visible submit buttons
                }
            }
        }

        Handler mainHandler = new Handler(getActivity().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        };
        mainHandler.post(myRunnable);


    }

    private void updateCollapsedState() {
        boolean isAllAnswersCollapsed = true;
        boolean isAllAnswersExpanded = true;

        //check if all
        for (boolean isExpanded : expandedStates) {
            if (isExpanded) {
                isAllAnswersCollapsed = false;
            }
            if (!isExpanded) {
                isAllAnswersExpanded = false;
            }
        }

        //update the expand/collapse button state based on state of answer cards

        if (isAllAnswersCollapsed) {
            allAnswersCollapsed = true;
            allAnswersExpanded = false;
            toggleAnswersCollapsedButton.setImageDrawable(collapsedDrawable);
            toggleExpandedButtonState = false;
        } else if (isAllAnswersExpanded) {
            allAnswersExpanded = true;
            allAnswersCollapsed = false;
            toggleAnswersCollapsedButton.setImageDrawable(expandedDrawable);
            toggleExpandedButtonState = true;
        }
    }

    //RecyclerView Implementation

    private class AnswerAdapter extends RecyclerView.Adapter<AnswerHolder> {

        @Override
        public AnswerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = (View) inflater
                    .inflate(R.layout.item_group_quiz_answer, parent, false);
            return new AnswerHolder(view);
        }

        @Override
        public void onBindViewHolder(AnswerHolder holder, int position) {
            QuizAnswer answer = question.getAvailableAnswers().get(position);
            GradedGroupQuizAnswer thisGradedAnswer = null;

            if (gradedGroupQuizQuestion != null) {
                for (GradedGroupQuizAnswer a : gradedGroupQuizQuestion.getGradedAnswers()) {
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
            return question.getAvailableAnswers().size();
        }

    }


    private class AnswerHolder extends RecyclerView.ViewHolder {

        private TextView mAnswerValue;
        private TextView mAnswerText;
        private ImageView mResultLabel;
        private Button mSubmitAnswerButton;
        private TextView mAnswerTextPreview;

        private RelativeLayout answerCardHeader;
        private ExpandableRelativeLayout answerCardContent;

        private View answerMask;


        public AnswerHolder(final View itemView) {
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

            //Didn't want to disable user interaction with the answer text after they got the question right;
            //still want them to be able to scroll!

            //make the answer mask absorb click/touch events
//            answerMask.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });

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
            mAnswerValue.setText(answer.getValue());
            mAnswerText.setText(answer.getText());
            mAnswerText.setMovementMethod(new ScrollingMovementMethod());
            mAnswerTextPreview.setText(answer.getText());
            mAnswerText.setOnTouchListener(new ScrollableTextTouchListener());

            //if user issued command to expand all answers, and this one is collapsed, expand it
            if (allAnswersExpanded) {
                if (!answerCardContent.isExpanded()) {
                    answerCardContent.expand();
                }
                //else if user issued command to collapse all answers, and this one is expanded, collapse it
            } else if (allAnswersCollapsed) {
                if (answerCardContent.isExpanded()) {
                    answerCardContent.collapse();
                }
            }

            //if answer card header is clicked, toggle collapsed state

            answerCardHeader.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    allAnswersCollapsed = false;
                    allAnswersExpanded = false;
                    answerCardContent.toggle();

                    //update state of answer card expansions, so that if all are expanded or colalpsed, the expand/collapse-all icon is set appropriately
                    expandedStates[getAdapterPosition()] = !expandedStates[getAdapterPosition()];
                    updateCollapsedState();
                }
            });

            //if the question has been answered, and the answer hasn't, it is an unsubmitted answer

            if (gradedGroupQuizQuestion != null && gradedGroupQuizQuestion.isAnsweredCorrectly() == true && gradedAnswer == null) {
                mSubmitAnswerButton.setVisibility(View.GONE);
                answerMask.setBackgroundColor(unansweredMaskColor);
                answerMask.setVisibility(View.VISIBLE);
            }

            //if the question hasn't been answered correctly, and the answer hasn't been tried, views should be as default

            //if the question has been answered, and the answer has as well, determine if it was correct or incorrect
            if (gradedAnswer != null) {
                if (gradedAnswer.isCorrect()) {
                    mSubmitAnswerButton.setVisibility(View.GONE);
                    answerMask.setBackgroundColor(correctMaskColor);
                    answerMask.setVisibility(View.VISIBLE);
                    mResultLabel.setImageDrawable(correctDrawable);
                    mResultLabel.setVisibility(View.VISIBLE);
                    mPointsEarnedTextView.setText(gradedAnswer.getPoints() + "");
                } else {
                    //the answer has been tried, and is incorrect
                    mSubmitAnswerButton.setVisibility(View.GONE);
                    answerMask.setBackgroundColor(incorrectMaskColor);
                    answerMask.setVisibility(View.VISIBLE);
                    mResultLabel.setImageDrawable(incorrectDrawable);
                    mResultLabel.setVisibility(View.VISIBLE);
                }
            }


            mSubmitAnswerButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    answerSelectedListener.answerSelected(question, answer, GroupQuizQuestionFragment.this);
                }
            });


        }

    }

}
