package com.example.jeff.viewpagerdelete.IndividualQuiz.View;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jeff.viewpagerdelete.IndividualQuiz.Controller.IndividualQuizActivity;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizAnswer;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.Miscellaneous.ScrollableTextTouchListener;
import com.example.jeff.viewpagerdelete.R;
import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Jeff on 2/10/17.
 */

public class IndividualQuizQuestionFragment extends Fragment {

    public static final String ANSWER_EXTRA = "ANSWER_EXTRA";
    public static final String EXTRA_QUIZ_QUESTION_NUMBER = "EXTRA_QUIZ_QUESTION_NUMBER";

    QuizQuestion question;

    private PageFragmentListener mListener;


    private TextView mQuestionTextView;
    private TextView mPointsRemainingTextView;
    private TextView mQuestionLabelTextView;

    private RecyclerView recyclerView;
    private AnswerAdapter adapter;

    private ImageButton toggleAnswersCollapsedButton;
    private boolean toggleExpandedButtonState = true;

    private Drawable collapsedDrawable;
    private Drawable expandedDrawable;

    private Animation rotateUp;
    private Animation rotateDown;

//    private ViewPager mPager;
//    private PagerAdapter mAdapter;

    private int questionNumber;

    private boolean allAnswersCollapsed = false;
    private boolean allAnswersExpanded = true;

    private boolean[] expandedStates;

    public interface PageFragmentListener {

        void submitButtonClicked();
//        void quizStateUpdated();

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater
            .inflate(R.layout.fragment_individual_quiz_question_page, container, false);

        //Get QuizQuestion extra arg
        Bundle args = getArguments();
        question = (QuizQuestion) args.getSerializable("EXTRA_QUIZ_QUESTION");
        questionNumber = args.getInt(EXTRA_QUIZ_QUESTION_NUMBER);

        Collections.sort(question.getAvailableAnswers());

        //Bind Views

        mQuestionTextView = (TextView) view.findViewById(R.id.quiz_question_text);
        mPointsRemainingTextView = (TextView) view
            .findViewById(R.id.quiz_question_points_remaining_label);
        mQuestionLabelTextView = (TextView) view.findViewById(R.id.quiz_question_number_label);

        mPointsRemainingTextView.setText(question.getPointsRemaining() + "");

        mQuestionLabelTextView.setText(questionNumber + ".");

        mQuestionTextView.setText(question.getText());
        mQuestionTextView.setMovementMethod(new ScrollingMovementMethod());

        recyclerView = (RecyclerView) view.findViewById(R.id.quiz_answer_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AnswerAdapter();
        recyclerView.setAdapter(adapter);


        collapsedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_collapse);
        expandedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_expand);
        rotateUp = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_up_180_degrees);
        rotateDown = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_down_180_degrees);

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
            View view = inflater
                .inflate(R.layout.item_individual_quiz_answer, parent, false);
            return new AnswerHolder(view);
        }

        @Override
        public void onBindViewHolder(AnswerHolder holder, int position) {
            holder.bindView(question.getAvailableAnswers().get(position));

        }

        @Override
        public int getItemCount() {
            return question.getAvailableAnswers().size();
        }

    }


    private class AnswerHolder extends RecyclerView.ViewHolder {

        private TextView mAnswerValue;
        private TextView mAnswerText;
        private TextView mPointsAllocated;
        private ImageButton mIncrementPointsAllocatedBtn;
        private ImageButton mDecrementPointsAllocatedBtn;
        private TextView answerTextPreview;

        private RelativeLayout answerCardHeader;
        private ExpandableRelativeLayout answerCardContent;


        public AnswerHolder(View itemView) {
            super(itemView);

            mAnswerValue = (TextView) itemView.findViewById(R.id.quiz_answer_label);
            mAnswerText = (TextView) itemView.findViewById(R.id.quiz_answer_text);
            mPointsAllocated = (TextView) itemView.findViewById(R.id.quiz_points_allocated);
            mIncrementPointsAllocatedBtn = (ImageButton) itemView
                .findViewById(R.id.quiz_increase_points_allocated);
            mDecrementPointsAllocatedBtn = (ImageButton) itemView
                .findViewById(R.id.quiz_reduce_points_allocated);
            answerTextPreview = (TextView) itemView.findViewById(R.id.quiz_answer_text_preview);
            answerCardHeader = (RelativeLayout) itemView.findViewById(R.id.quiz_answer_card_header);
            answerCardContent = (ExpandableRelativeLayout) itemView
                .findViewById(R.id.quiz_answer_card_content);

            answerCardContent.setListener(new ExpandableLayoutListenerAdapter() {
                @Override
                public void onPreOpen() {
                    answerTextPreview.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onClosed() {
                    answerTextPreview.setVisibility(View.VISIBLE);
                }
            });

        }


        public void bindView(final QuizAnswer answer) {
            mAnswerValue.setText(answer.getValue() + ".");
            mAnswerText.setText(answer.getText());
            mAnswerText.setMovementMethod(new ScrollingMovementMethod());
            mPointsAllocated.setText(answer.getPointsAllocated() + "");
            answerTextPreview.setText(answer.getText());

            mAnswerText.setOnTouchListener(new ScrollableTextTouchListener());

            if (allAnswersExpanded) {
                if (!answerCardContent.isExpanded()) {
                    answerCardContent.expand();
                }
            } else if (allAnswersCollapsed) {
                if (answerCardContent.isExpanded()) {
                    answerCardContent.collapse();
                }
            }

            answerCardHeader.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    allAnswersCollapsed = false;
                    allAnswersExpanded = false;
                    answerCardContent.toggle();
                    expandedStates[getAdapterPosition()] = !expandedStates[getAdapterPosition()];
                    updateCollapsedState();
                }
            });


            mIncrementPointsAllocatedBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (question.getPointsRemaining() > 0) {
                        mPointsAllocated.setText(answer.incrementPointsAllocated() + "");
                        incrementButtonClicked();
                    }
                }
            });

            mDecrementPointsAllocatedBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answer.getPointsAllocated() > 0) {
                        mPointsAllocated.setText(answer.decrementPointsAllocated() + "");
                        decrementButtonClicked();
                    }
                }
            });


        }


        public void incrementButtonClicked() {
            mPointsRemainingTextView.setText("" + question.decrementPointsRemaining());
//        mListener.quizStateUpdated();
        }

        public void decrementButtonClicked() {
            mPointsRemainingTextView.setText("" + question.incrementPointsRemaining());
//        mListener.quizStateUpdated();
        }
    }


}
