package com.example.jeff.viewpagerdelete.QuizStatistics.View.StatisticsMasterView;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuiz;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuizQuestion;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuizQuestion;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.util.Collections;

/**
 * Created by Jeff on 4/26/17.
 */

/**
 * Displays graph of scores for individual and group(s)
 */

public class StatisticsMasterFragment extends Fragment {

    public static final String TAG = "StatisticsFragment";
    public static final String ARG_EXTRA_QUIZ = "ARG_EXTRA_QUIZ";
    public static final String ARG_EXTRA_GRADED_GROUP_QUIZ = "ARG_EXTRA_GRADED_GROUP_QUIZ";
    public static final String ARG_EXTRA_GRADED_INDIVIDUAL_QUIZ = "ARG_EXTRA_GRADED_INDIVIDUAL_QUIZ";


    private BarChart mBarChart;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private StatisticsQuestionAdapter mAdapter;

    private Quiz mQuiz;
    private GradedGroupQuiz mGradedGroupQuiz;
    private GradedQuiz mGradedIndividualQuiz;

    private OnStatisticsQuestionClickedListener mListener;

    /**
     * Listener for question clicked events
     */
    public interface OnStatisticsQuestionClickedListener {
        void onQuestionClicked(int position, QuizQuestion questionClicked, GradedQuizQuestion individualQuizQuestion, GradedGroupQuizQuestion groupQuizQuestion);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_master_view, container, false);

        Bundle args = getArguments();

        if (args != null && args.containsKey(ARG_EXTRA_QUIZ) && args.containsKey(ARG_EXTRA_GRADED_INDIVIDUAL_QUIZ) && args.containsKey(ARG_EXTRA_GRADED_GROUP_QUIZ)) {
            mQuiz = (Quiz) args.getSerializable(ARG_EXTRA_QUIZ);
            mGradedGroupQuiz = (GradedGroupQuiz) args.getSerializable(ARG_EXTRA_GRADED_GROUP_QUIZ);
            mGradedIndividualQuiz = (GradedQuiz) args.getSerializable(ARG_EXTRA_GRADED_INDIVIDUAL_QUIZ);

            //Sort the questions by id
            Collections.sort(mQuiz.getQuestions());
            Collections.sort(mGradedGroupQuiz.getGradedQuestions());
        } else {
            Log.e(TAG, "Missing arguments");
        }


        mRecyclerView = (RecyclerView) view.findViewById(R.id.statistics_questions_recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new StatisticsQuestionAdapter(getActivity(), mListener, mQuiz, mGradedIndividualQuiz, mGradedGroupQuiz);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mBarChart = (BarChart) view.findViewById(R.id.statistics_graph_view);


        /**
         * Draw a bar graph showing the user's vs group's statistics
         */
        //TODO: Test code proceeding, revise later
        float totalPointsPossible = 40.0f; //MAY NOT BE TRUE FOR OTHER QUIZZES; FIND WAY TO CALCULATE THIS BASED ON QUIZ
        float individualPercentageGrade = mGradedIndividualQuiz.getTotalPointsScored() / totalPointsPossible;
        float groupPercentageGrade = mGradedGroupQuiz.getTotalPointsScored() / totalPointsPossible;

        BarModel individualBarModel = new BarModel(UserDataSource.getInstance().getUser().getUserID(), mGradedIndividualQuiz.getTotalPointsScored(), ContextCompat.getColor(getActivity(), R.color.jccolorPrimaryDark));
        BarModel groupBarModel = new BarModel(mGradedGroupQuiz.getGroup().getName(), mGradedGroupQuiz.getTotalPointsScored(), ContextCompat.getColor(getActivity(), R.color.jccolorAccent));


//        mBarChart.setShowValues(false);

        mBarChart.addBar(individualBarModel);
        mBarChart.addBar(groupBarModel);


        //TODO: Test code preceeding, revise later


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnStatisticsQuestionClickedListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Animates the bar graph each time the view appears
        mBarChart.startAnimation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }


//    private class StatisticsQuestionAdapter extends RecyclerView.Adapter<StatisticsQuestionHolder> {
//        @Override
//        public StatisticsQuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            View view = inflater.inflate(R.layout.item_statistics_question, parent, false);
//
//            return new StatisticsQuestionHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(StatisticsQuestionHolder holder, int position) {
//            //TODO: Currently doesn't seem to be a better way to match mGradedGroupQuiz question to mGradedIndividualQuiz question
//            //There is no id property for mGradedIndividualQuiz question; must search for correct question by  question text
//
//            holder.bindView(mQuiz.getQuestions().get(position), mGradedIndividualQuiz.getQuestions().get(position), mGradedGroupQuiz.getGradedQuestions().get(position));
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return mQuiz.getQuestions().size();
//        }
//    }
//
//
//    private class StatisticsQuestionHolder extends RecyclerView.ViewHolder {
//
//        private TextView questionNumberLabel;
//        private TextView userNameLabel;
//        private TextView groupNameLabel;
//        private TextView individualPointsEarned;
//        private TextView groupPointsEarned;
//
//        public StatisticsQuestionHolder(View itemView) {
//            super(itemView);
//
//            questionNumberLabel = (TextView) itemView.findViewById(R.id.statistics_question_number_label);
//            userNameLabel = (TextView) itemView.findViewById(R.id.statistics_username_label);
//            groupNameLabel = (TextView) itemView.findViewById(R.id.statistics_groupname_label);
//            individualPointsEarned = (TextView) itemView.findViewById(R.id.statistics_individual_points);
//            groupPointsEarned = (TextView) itemView.findViewById(R.id.statistics_group_points);
//
//        }
//
//        public void bindView(final QuizQuestion question, final GradedQuizQuestion gradedQuizQuestion, final GradedGroupQuizQuestion gradedGroupQuizQuestion) {
//            questionNumberLabel.setText((getAdapterPosition() + 1) + ".");
//            userNameLabel.setText(UserDataSource.getInstance().getUser().getUserID());
//            groupNameLabel.setText(mGradedGroupQuiz.getGroup().getName());
//
//            GradedQuizAnswer userIndividualAnswer = null;
//
//            //find the correct answer from group mQuiz
//            for (GradedGroupQuizAnswer answer : gradedGroupQuizQuestion.getGradedAnswers()) {
//                if (answer.isCorrect()) {
//                    //use the value attribute of the correct answer to find the points earned for the user
//                    userIndividualAnswer = gradedQuizQuestion.getAnswerByValue(answer.getValue());
//                    break;
//                }
//            }
//
//            individualPointsEarned.setText((userIndividualAnswer == null ? "0" : userIndividualAnswer.getPointsEarned() + ""));
//
//
//            for (GradedGroupQuizAnswer answer : gradedGroupQuizQuestion.getGradedAnswers()) {
//                if (answer.isCorrect()) {
//                    groupPointsEarned.setText(answer.getPoints() + "");
//                    break;
//                }
//            }
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mListener.onQuestionClicked(getAdapterPosition() + 1, question, gradedQuizQuestion, gradedGroupQuizQuestion);
//                }
//            });
//        }
//    }
}
