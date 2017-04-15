package com.example.jeff.viewpagerdelete.Homepage.View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jeff on 4/4/17.
 */

public class QuizListFragment extends Fragment {

    public static final String ARG_COURSES_QUIZ_LIST_FRAGMENT = "ARG_COURSES_QUIZ_LIST_FRAGMENT";

    private ArrayList<Course> courses;

    private QuizListListener mListener;

    private RecyclerView mRecyclerView;
    private QuizAdapter mAdapter;

    Typeface regularFace;
    Typeface boldFace;
    Typeface boldItalicFace;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quizzes_list_fragment_layout, container, false);

        Bundle args = getArguments();

        if(args != null && args.containsKey(ARG_COURSES_QUIZ_LIST_FRAGMENT)){
            courses = (ArrayList<Course>) args.get(ARG_COURSES_QUIZ_LIST_FRAGMENT);
        }

        regularFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/robotoRegular.ttf");
        boldFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/robotoBold.ttf");
        boldItalicFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/robotoBoldItalic.ttf");


        mRecyclerView = (RecyclerView) view.findViewById(R.id.quizzes_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new QuizAdapter();
        mRecyclerView.setAdapter(mAdapter);

        try{
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle("Quizzes");
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (QuizListListener) context;
        }
        catch(ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public interface QuizListListener{
        void itemClicked(Course course);
    }


    private class QuizAdapter extends RecyclerView.Adapter<QuizHolder>{
        @Override
        public QuizHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.quiz_list_item, parent, false);

            return new QuizHolder(view);
        }


        @Override
        public void onBindViewHolder(QuizHolder holder, int position) {
            holder.bindQuiz(courses.get(position));

        }

        @Override
        public int getItemCount() {
            return courses.size();
        }
    }


    private class QuizHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView quizNameTextView;
        private TextView timeRemainingTextView;
        String timeRemaining;


        public QuizHolder(final View itemView) {
            super(itemView);

            timeRemainingTextView = (TextView) itemView.findViewById(R.id.quiz_time_remaining);
            quizNameTextView = (TextView) itemView.findViewById(R.id.quiz_name_textview);

            quizNameTextView.setTypeface(regularFace);
            timeRemainingTextView.setTypeface(boldItalicFace);

            itemView.setOnClickListener(this);


        }

        public void bindQuiz(Course course){
            quizNameTextView.setText(course.getQuiz().getDescription());

            Quiz q =  course.getQuiz();

            Calendar availableDate = new GregorianCalendar();

            //TODO: should be set to the quizzes available date, not todays date
            availableDate.setTime(new Date());

            availableDate.add(Calendar.MINUTE, q.getTimedLength());

            long expiryTime = availableDate.getTimeInMillis() - new Date().getTime();


            new CountDownTimer(expiryTime, 1000){
                @Override
                public void onTick(long millisecondsUntilFinished) {
                    timeRemaining = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisecondsUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisecondsUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisecondsUntilFinished)));
                    timeRemainingTextView.setText(timeRemaining);

                    if(millisecondsUntilFinished < 5 * 60 * 1000){
                        itemView.setBackgroundColor(Color.YELLOW);
                    }
                }

                @Override
                public void onFinish() {
                    timeRemainingTextView.setText("Expired!");
                    itemView.setBackgroundColor(Color.RED);
                    itemView.setEnabled(false);
                }
            }.start();
        }

        @Override
        public void onClick(View view) {
            mListener.itemClicked(courses.get(this.getAdapterPosition()));
        }
    }
}
