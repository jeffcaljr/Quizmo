package com.example.jeff.viewpagerdelete.QuizStatistics.View.StatisticsMasterView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuizAnswer;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuizQuestion;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuizAnswer;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuizQuestion;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

/**
 * Created by Jeff on 5/1/17.
 */

public class StatisticsQuestionHolder extends RecyclerView.ViewHolder {

    private TextView questionNumberLabel;
    private TextView userNameLabel;
    private TextView groupNameLabel;
    private TextView individualPointsEarned;
    private TextView groupPointsEarned;

    private StatisticsMasterFragment.OnStatisticsQuestionClickedListener mListener;

    public StatisticsQuestionHolder(View itemView, StatisticsMasterFragment.OnStatisticsQuestionClickedListener listener) {
        super(itemView);

        mListener = listener;

        questionNumberLabel = (TextView) itemView.findViewById(R.id.statistics_question_number_label);
        userNameLabel = (TextView) itemView.findViewById(R.id.statistics_username_label);
        groupNameLabel = (TextView) itemView.findViewById(R.id.statistics_groupname_label);
        individualPointsEarned = (TextView) itemView.findViewById(R.id.statistics_individual_points);
        groupPointsEarned = (TextView) itemView.findViewById(R.id.statistics_group_points);

    }

    public void bindView(final String groupName, final QuizQuestion question, final GradedQuizQuestion gradedQuizQuestion, final GradedGroupQuizQuestion gradedGroupQuizQuestion) {
        questionNumberLabel.setText((getAdapterPosition() + 1) + ".");
        userNameLabel.setText(UserDataSource.getInstance().getUser().getUserID());
        groupNameLabel.setText(groupName);

        GradedQuizAnswer userIndividualAnswer = null;

        //find the correct answer from group mQuiz
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
