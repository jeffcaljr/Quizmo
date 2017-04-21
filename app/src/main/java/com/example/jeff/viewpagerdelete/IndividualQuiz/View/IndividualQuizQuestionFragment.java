package com.example.jeff.viewpagerdelete.IndividualQuiz.View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import android.support.v7.widget.RecyclerView.RecyclerListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.method.ScrollingMovementMethod;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.Toast;
import com.eftimoff.viewpagertransformers.StackTransformer;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Controller.IndividualQuizActivity;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizAnswer;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.Miscellaneous.CollapsibleCardView;
import com.example.jeff.viewpagerdelete.R;
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

    private Drawable collapsedDrawable;
    private Drawable expandedDrawable;

    private Animation rotateUp;
    private Animation rotateDown;

//    private ViewPager mPager;
//    private PagerAdapter mAdapter;

    private int questionNumber;
    private int totalQuestions;

    private boolean allAnswersCollapsed = false;
    private boolean allAnswersExpanded = true;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_question_page, container, false);

        //Get QuizQuestion extra arg
        Bundle args = getArguments();
        question = (QuizQuestion) args.getSerializable("EXTRA_QUIZ_QUESTION");
        totalQuestions = args.getInt(IndividualQuizActivity.EXTRA_QUIZ_QUESTION_TOTAL_QUESTIONS);
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

        recyclerView.addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (allAnswersCollapsed) {
                    ((CollapsibleCardView) view).setCollapsed(true);

                } else if (allAnswersExpanded) {
                    ((CollapsibleCardView) view).setCollapsed(false);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });


        collapsedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_collapse);
        expandedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_expand);

        toggleAnswersCollapsedButton = (ImageButton) view
            .findViewById(R.id.quiz_question_toggle_answers_collapse_button);

        toggleAnswersCollapsedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (allAnswersCollapsed) {
                    allAnswersCollapsed = false;
                    allAnswersExpanded = true;
                    toggleAnswersCollapsedButton.startAnimation(rotateUp);

                    for (int i = 0; i < recyclerView.getLayoutManager().getChildCount(); i++) {
                        CollapsibleCardView cardView = (CollapsibleCardView) recyclerView
                            .getLayoutManager().getChildAt(i);
                        cardView.expandContent();
                    }

                } else {
                    allAnswersCollapsed = true;
                    allAnswersExpanded = false;
                    toggleAnswersCollapsedButton.startAnimation(rotateDown);

                    for (int i = 0; i < recyclerView.getLayoutManager().getChildCount(); i++) {
                        CollapsibleCardView cardView = (CollapsibleCardView) recyclerView
                            .getLayoutManager().getChildAt(i);
                        cardView.collapseContent();
                    }
                }

            }
        });

        rotateUp = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_up_180_degrees);
        rotateUp.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.setFillAfter(true);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rotateDown = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_down_180_degrees);
        rotateDown.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.setFillAfter(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


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

    //RecyclerView Implementation

    private class AnswerAdapter extends RecyclerView.Adapter<AnswerHolder> {

        @Override
        public AnswerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            CollapsibleCardView view = (CollapsibleCardView) inflater
                .inflate(R.layout.item_quiz_answer, parent, false);
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
        private RelativeLayout answerCardContent;


        public AnswerHolder(final CollapsibleCardView itemView) {
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
            answerCardContent = (RelativeLayout) itemView
                .findViewById(R.id.quiz_answer_card_content);

            itemView.setCardHeader(answerCardHeader);
            itemView.setCardContent(answerCardContent);
            itemView.setAnswerTextPreview(answerTextPreview);

        }


        public void bindView(final QuizAnswer answer) {
            mAnswerValue.setText(answer.getValue());
            mAnswerText.setText(answer.getText());
            mAnswerText.setMovementMethod(new ScrollingMovementMethod());
            mPointsAllocated.setText(answer.getPointsAllocated() + "");
            answerTextPreview.setText(answer.getText());

            CollapsibleCardView view = ((CollapsibleCardView) itemView);

            if (allAnswersCollapsed) {
                view.setCollapsed(true);

            } else if (allAnswersExpanded) {
                view.setCollapsed(false);
            }

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
