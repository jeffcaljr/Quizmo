package com.example.jeff.viewpagerdelete.QuizStatistics.View.StatisticsMasterView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.R;

/**
 * Created by Jeff on 5/1/17.
 */

public class StatisticsQuestionAdapter extends RecyclerView.Adapter<StatisticsQuestionHolder> {

    private Quiz mQuiz;
    private GradedQuiz mGradedIndividualQuiz;
    private GradedGroupQuiz mGradedGroupQuiz;

    private Context mContext;
    private StatisticsMasterFragment.OnStatisticsQuestionClickedListener mListener;

    public StatisticsQuestionAdapter(Context context, StatisticsMasterFragment.OnStatisticsQuestionClickedListener listener, Quiz mQuiz, GradedQuiz gradedQuiz, GradedGroupQuiz gradedGroupQuiz) {
        this.mQuiz = mQuiz;
        this.mGradedIndividualQuiz = gradedQuiz;
        this.mGradedGroupQuiz = gradedGroupQuiz;
        this.mContext = context;
        this.mListener = listener;

    }

    @Override
    public StatisticsQuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_statistics_question, parent, false);

        return new StatisticsQuestionHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(StatisticsQuestionHolder holder, int position) {
        //TODO: Currently doesn't seem to be a better way to match mGradedGroupQuiz question to mGradedIndividualQuiz question
        //There is no id property for mGradedIndividualQuiz question; must search for correct question by  question text

        holder.bindView(mGradedGroupQuiz.getGroup().getName(), mQuiz.getQuestions().get(position), mGradedIndividualQuiz.getQuestions().get(position), mGradedGroupQuiz.getGradedQuestions().get(position));

    }

    @Override
    public int getItemCount() {
        return mQuiz.getQuestions().size();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mListener = null;
    }
}
