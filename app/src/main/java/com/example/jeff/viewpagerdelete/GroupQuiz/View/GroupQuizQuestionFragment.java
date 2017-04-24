package com.example.jeff.viewpagerdelete.GroupQuiz.View;

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

  public static final String ANSWER_EXTRA = "ANSWER_EXTRA";
  public static final String EXTRA_QUIZ_QUESTION_NUMBER = "EXTRA_QUIZ_QUESTION_NUMBER";

  private QuizQuestion question;
  private GradedGroupQuizQuestion gradedGroupQuizQuestion;

  private PageFragmentListener mListener;


  private TextView mQuestionTextView;
  private TextView mPointsEarnedTextView;
  private TextView mQuestionLabelTextView;

  private RecyclerView recyclerView;
  private AnswerAdapter adapter;

  private ImageButton toggleAnswersCollapsedButton;
  private boolean toggleExpandedButtonState = true;

  private Drawable collapsedDrawable;
  private Drawable expandedDrawable;
  private Drawable correctDrawable;
  private Drawable incorrectDrawable;
  private int correctMaskColor;
  private int incorrectMaskColor;
  private int unansweredMaskColor;

  private Animation rotateUp;
  private Animation rotateDown;

  private int questionNumber;

  private boolean allAnswersCollapsed = false;
  private boolean allAnswersExpanded = true;

  private boolean[] expandedStates;

  private OnGroupQuizAnswerSelectedListener answerSelectedListener;

  //TODO: Test code; delete proceeding later

  private ArrayList<GradedGroupQuizAnswer> sampleGradedAnswers;

  //TODO: Test code; delete preceeding later

  public interface OnGroupQuizAnswerSelectedListener {

    void answerSelected(QuizQuestion question, QuizAnswer answer);

  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_group_quiz_question_page, container, false);

    //Get QuizQuestion extra arg
    Bundle args = getArguments();
    question = (QuizQuestion) args.getSerializable("EXTRA_QUIZ_QUESTION");
    questionNumber = args.getInt(EXTRA_QUIZ_QUESTION_NUMBER);
    Collections.sort(question.getAvailableAnswers());

    //Bind Views

    mQuestionTextView = (TextView) view.findViewById(R.id.quiz_question_text);
    mPointsEarnedTextView = (TextView) view
        .findViewById(R.id.quiz_question_points_earned_label);
    mQuestionLabelTextView = (TextView) view.findViewById(R.id.quiz_question_number_label);

    mPointsEarnedTextView.setText("?");

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



    //TODO: Test code; delete proceeding later

    sampleGradedAnswers = new ArrayList<>();
    sampleGradedAnswers.add(new GradedGroupQuizAnswer("A", 4, false));
    sampleGradedAnswers.add(new GradedGroupQuizAnswer("C", 1, true));
    sampleGradedAnswers.add(new GradedGroupQuizAnswer("D", 2, false));

    //TODO: Test code; delete preceeding later

    correctDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_correct_white);
    incorrectDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_incorrect_white);

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

  public void onGradeRecieved(GradedGroupQuizQuestion gradedQuestion) {

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
      for (GradedGroupQuizAnswer a : sampleGradedAnswers) {
        if (a.getValue().equals(answer.getValue())) {
          thisGradedAnswer = a;
          break;
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

      //make the answer mask absorb click/touch events
      answerMask.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {

        }
      });

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

      if (gradedAnswer != null) {

        mSubmitAnswerButton.setEnabled(false);
        mSubmitAnswerButton.setVisibility(View.GONE);

        if (!gradedAnswer.isCorrect()) {
          mResultLabel.setImageDrawable(incorrectDrawable);
          mResultLabel.setVisibility(View.VISIBLE);
          answerMask.setBackgroundColor(incorrectMaskColor);
          answerMask.setVisibility(View.VISIBLE);
        } else if (gradedAnswer.isCorrect()) {
          //answer is the correct one
          mResultLabel.setImageDrawable(correctDrawable);
          mPointsEarnedTextView.setText(gradedAnswer.getPoints() + "");
          mResultLabel.setVisibility(View.VISIBLE);
          answerMask.setBackgroundColor(correctMaskColor);
          answerMask.setVisibility(View.VISIBLE);
        }
      } else {
        mResultLabel.setVisibility(View.INVISIBLE);
      }

      mSubmitAnswerButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {

          answerSelectedListener.answerSelected(question, answer);
        }
      });


    }

  }

}
