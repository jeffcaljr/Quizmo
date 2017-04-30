package com.example.jeff.viewpagerdelete.QuizStatistics.ActivityControllers.View;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuiz;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuizAnswer;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuizQuestion;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuizAnswer;
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

public class StatisticsMasterFragment extends Fragment {

    public static final String TAG = "StatisticsFragment";

    public static final String ARG_EXTRA_QUIZ = "ARG_EXTRA_QUIZ";
    public static final String ARG_EXTRA_GRADED_GROUP_QUIZ = "ARG_EXTRA_GRADED_GROUP_QUIZ";
    public static final String ARG_EXTRA_GRADED_INDIVIDUAL_QUIZ = "ARG_EXTRA_GRADED_INDIVIDUAL_QUIZ";

    private BarChart mBarChart;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private StatisticsQuestionAdapter adapter;

    private Quiz quiz;
    private GradedGroupQuiz gradedGroupQuiz;
    private GradedQuiz gradedIndividualQuiz;

    private OnStatisticsQuestionClickedListener mListener;

    public interface OnStatisticsQuestionClickedListener {
        void onQuestionClicked(int position, QuizQuestion questionClicked, GradedQuizQuestion individualQuizQuestion, GradedGroupQuizQuestion groupQuizQuestion);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_master_view, container, false);

        Bundle args = getArguments();

        if (args != null && args.containsKey(ARG_EXTRA_QUIZ) && args.containsKey(ARG_EXTRA_GRADED_INDIVIDUAL_QUIZ) && args.containsKey(ARG_EXTRA_GRADED_GROUP_QUIZ)) {
            quiz = (Quiz) args.getSerializable(ARG_EXTRA_QUIZ);
            gradedGroupQuiz = (GradedGroupQuiz) args.getSerializable(ARG_EXTRA_GRADED_GROUP_QUIZ);
            gradedIndividualQuiz = (GradedQuiz) args.getSerializable(ARG_EXTRA_GRADED_INDIVIDUAL_QUIZ);

            Collections.sort(quiz.getQuestions());
            Collections.sort(gradedGroupQuiz.getGradedQuestions());
        } else {
            Log.e(TAG, "Missing arguments");
        }


        recyclerView = (RecyclerView) view.findViewById(R.id.statistics_questions_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        adapter = new StatisticsQuestionAdapter();

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        mBarChart = (BarChart) view.findViewById(R.id.statistics_graph_view);



        //TODO: Test code proceeding, delete later
        float totalPointsPossible = 40.0f; //MAY NOT BE TRUE FOR OTHER QUIZZES; FIND WAY TO CALCULATE THIS BASED ON QUIZ
        float individualPercentageGrade = gradedIndividualQuiz.getTotalPointsScored() / totalPointsPossible;
        float groupPercentageGrade = gradedGroupQuiz.getTotalPointsScored() / totalPointsPossible;

        BarModel individualBarModel = new BarModel(UserDataSource.getInstance().getUser().getUserID(), individualPercentageGrade, ContextCompat.getColor(getActivity(), R.color.jccolorPrimaryDark));
        BarModel groupBarModel = new BarModel(gradedGroupQuiz.getGroup().getName(), groupPercentageGrade, ContextCompat.getColor(getActivity(), R.color.jccolorAccent));


        mBarChart.setShowValues(false);

        mBarChart.addBar(individualBarModel);
        mBarChart.addBar(groupBarModel);


        //TODO: Test code preceeding, delete later

//        mBarChart.addBar(new BarModel("", 2.3f, 0xFF123456));


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
        mBarChart.startAnimation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    private class StatisticsQuestionAdapter extends RecyclerView.Adapter<StatisticsQuestionHolder> {
        @Override
        public StatisticsQuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.item_statistics_question, parent, false);

            return new StatisticsQuestionHolder(view);
        }

        @Override
        public void onBindViewHolder(StatisticsQuestionHolder holder, int position) {
            holder.bindView(quiz.getQuestions().get(position), gradedIndividualQuiz.getQuestions().get(position), gradedGroupQuiz.getGradedQuestions().get(position));

        }

        @Override
        public int getItemCount() {
            return quiz.getQuestions().size();
        }
    }


    private class StatisticsQuestionHolder extends RecyclerView.ViewHolder {

        private TextView questionNumberLabel;
        private TextView userNameLabel;
        private TextView groupNameLabel;
        private TextView individualPointsEarned;
        private TextView groupPointsEarned;

        public StatisticsQuestionHolder(View itemView) {
            super(itemView);

            questionNumberLabel = (TextView) itemView.findViewById(R.id.statistics_question_number_label);
            userNameLabel = (TextView) itemView.findViewById(R.id.statistics_username_label);
            groupNameLabel = (TextView) itemView.findViewById(R.id.statistics_groupname_label);
            individualPointsEarned = (TextView) itemView.findViewById(R.id.statistics_individual_points);
            groupPointsEarned = (TextView) itemView.findViewById(R.id.statistics_group_points);

        }

        public void bindView(final QuizQuestion question, final GradedQuizQuestion gradedQuizQuestion, final GradedGroupQuizQuestion gradedGroupQuizQuestion) {
            questionNumberLabel.setText((getAdapterPosition() + 1) + ".");
            userNameLabel.setText(UserDataSource.getInstance().getUser().getUserID());
            groupNameLabel.setText(gradedGroupQuiz.getGroup().getName());

            GradedQuizAnswer userIndividualAnswer = null;

            //find the correct answer from group quiz
            for (GradedGroupQuizAnswer answer : gradedGroupQuizQuestion.getGradedAnswers()) {
                if (answer.isCorrect()) {
                    //use the value attribute of the correct answer to find the points earned for the user
                    userIndividualAnswer = gradedQuizQuestion.getAnswerByValue(answer.getValue());
                    break;
                }
            }

            individualPointsEarned.setText((userIndividualAnswer == null ? "0" : userIndividualAnswer.getPointsEarned() + ""));


            for (GradedGroupQuizAnswer answer : gradedGroupQuizQuestion.getGradedAnswers()) {
                if (answer.isCorrect()) {
                    groupPointsEarned.setText(answer.getPoints() + "");
                    break;
                }
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onQuestionClicked(getAdapterPosition() + 1, question, gradedQuizQuestion, gradedGroupQuizQuestion);
                }
            });
        }

    }
}
