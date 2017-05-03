package com.example.jeff.viewpagerdelete.Homepage.View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.Toast;

import com.example.jeff.viewpagerdelete.Homepage.ActivityControllers.HomePageActivity;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Networking.QuizNetworkingService;
import com.example.jeff.viewpagerdelete.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jeff on 4/4/17.
 */

public class QuizListFragment extends Fragment implements OnRefreshListener {

    public static final String ARG_COURSE_QUIZ_LIST_FRAGMENT = "ARG_COURSE_QUIZ_LIST_FRAGMENT";

    private Course course;
    private ArrayList<Course> coursesCopy;

    private QuizNetworkingService quizNetworkingService;

    private QuizListListener mListener;

    private SwipeRefreshLayout swipeRefreshLayout;
    //  private SearchView searchView;
    private RelativeLayout quizzesEmptyView;
    private RecyclerView mRecyclerView;
    private QuizAdapter mAdapter;

    private Animation shake;


    Typeface regularFace;
    Typeface boldFace;
    Typeface boldItalicFace;

    private int colorPrimaryBright;
    private int colorWarningYellow;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quizzes_list, container, false);

        Bundle args = getArguments();

        if (args != null && args.containsKey(ARG_COURSE_QUIZ_LIST_FRAGMENT)) {
            course = (Course) args.get(ARG_COURSE_QUIZ_LIST_FRAGMENT);

            //Does this work?
            //try to sort the quizzes by available date (most recent first)
//          Collections.sort(courses, new Comparator<Course>() {
//            @Override
//            public int compare(Course course, Course t1) {
//              return (t1.getQuiz().getAvailableDate()
//                  .compareTo(course.getQuiz().getAvailableDate()));
//            }
//          });


            coursesCopy = new ArrayList<>();
        }

        regularFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/robotoRegular.ttf");
        boldFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/robotoBold.ttf");
        boldItalicFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/robotoBoldItalic.ttf");

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.quiz_list_swipe_refresher);

        swipeRefreshLayout.setOnRefreshListener(this);

        quizzesEmptyView = (RelativeLayout) view.findViewById(R.id.quizzes_list_empty_tv);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.quizzes_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new QuizAdapter();


        try {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle("Quizzes");
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        AdapterDataObserver emptyObserver = new AdapterDataObserver() {


            @Override
            public void onChanged() {
                Adapter<?> adapter = mAdapter;
                if (adapter != null && quizzesEmptyView != null) {
                    if (adapter.getItemCount() == 0) {
                        quizzesEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        quizzesEmptyView.setVisibility(View.GONE);
                    }
                }

            }
        };

        mAdapter.registerAdapterDataObserver(emptyObserver);
        mRecyclerView.setAdapter(mAdapter);

        shake = AnimationUtils.loadAnimation(getContext(), R.anim.shakeanim);
        colorPrimaryBright = ContextCompat.getColor(getContext(), R.color.jccolorPrimaryBright);
        colorWarningYellow = ContextCompat.getColor(getContext(), R.color.jccolorWarningYellow);


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (QuizListListener) context;
        } catch (ClassCastException e) {
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

    public interface QuizListListener {
        void itemClicked(Course course);
    }

    //MARK: OnRefreshListener Methods


    @Override
    public void onRefresh() {

        Toast.makeText(getContext(), "Refreshing", Toast.LENGTH_LONG).show();

        swipeRefreshLayout.setRefreshing(false);
    }

    private class QuizAdapter extends RecyclerView.Adapter<QuizHolder> {

//      public QuizAdapter() {
//        coursesCopy.addAll(courses);
//      }

        @Override
        public QuizHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.list_item_quiz, parent, false);

            return new QuizHolder(view);
        }


        @Override
        public void onBindViewHolder(QuizHolder holder, int position) {
            //there should only be one quiz per course;
            holder.bindQuiz(course.getQuiz());
//            holder.bindQuiz(courses.get(position));

        }

        @Override
        public int getItemCount() {

            //TODO: The following line is for test purposes
            //because there is currently one quiz in the network, and I want to show a list, I
            //am showing the same quiz multiple times

            //there should only be one quiz per course;
            return (course.getQuiz() != null ? 1 : 0);


        }

//      public void filter(String text) {
//        courses.clear();
//        if (text.isEmpty()) {
//          courses.addAll(coursesCopy);
//        } else {
//          text = text.toLowerCase().trim();
//          for (Course course : coursesCopy) {
//            String quizName = course.getQuiz().getDescription().toLowerCase().trim();
//            if (quizName.contains(text)) {
//              courses.add(course);
//            }
//          }
//        }
//        notifyDataSetChanged();
//      }


    }


    private class QuizHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView quizNameTextView;
        //        private TextView timeRemainingTextView;
        String timeRemaining;


        public QuizHolder(final View itemView) {
            super(itemView);

//            timeRemainingTextView = (TextView) itemView.findViewById(R.id.quiz_time_remaining);
            quizNameTextView = (TextView) itemView.findViewById(R.id.quiz_name_textview);

//            quizNameTextView.setTypeface(regularFace);
//            timeRemainingTextView.setTypeface(boldItalicFace);

            itemView.setOnClickListener(this);


        }


        public void bindQuiz(Quiz quiz) {

            Quiz q = course.getQuiz();

            quizNameTextView.setText(q.getDescription());


//            CountDownTimer quizTimer;

//            if(q.getStartTime() != null){
//
//                Calendar expiryDate = new GregorianCalendar();
//                expiryDate.setTime(q.getStartTime());
//                expiryDate.add(Calendar.MINUTE, q.getTimedLength());
//                final int timeBeforeExpiry = (int) (expiryDate.getTimeInMillis() - System.currentTimeMillis());
//
//                if(timeBeforeExpiry < 1000){
//                    itemView.clearAnimation();
//                    timeRemainingTextView.setText("Expired!");
//                    ((CardView) itemView).setCardBackgroundColor(Color.WHITE);
//                    quizNameTextView.setTextColor(colorPrimaryBright);
//                    timeRemainingTextView.setTextColor(Color.WHITE);
//                    itemView.setEnabled(false);
//                }
//                else{
//
//                    quizTimer = new CountDownTimer(timeBeforeExpiry, 1000){
//                        @Override
//                        public void onTick(long millisecondsUntilFinished) {
//                            timeRemaining = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisecondsUntilFinished),
//                                    TimeUnit.MILLISECONDS.toSeconds(millisecondsUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisecondsUntilFinished)));
//                            timeRemainingTextView.setText(timeRemaining);
//
////                    if(millisecondsUntilFinished < 5 * 60 * 1000){
////
////                        if(!tempDeleteThis){
////                            itemView.startAnimation(shake);
////                          ((CardView) itemView).setCardBackgroundColor(colorWarningYellow);
////                            tempDeleteThis = true;
////                        }
////
////
////                    }
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            itemView.clearAnimation();
//                            timeRemainingTextView.setText("Expired!");
//                            ((CardView) itemView).setCardBackgroundColor(Color.WHITE);
//                            quizNameTextView.setTextColor(colorPrimaryBright);
//                            timeRemainingTextView.setTextColor(Color.WHITE);
//                            itemView.setEnabled(false);
//                        }
//                    }.start();
//                }
//
//            }
        }

        @Override
        public void onClick(View view) {

            mListener.itemClicked(course);
        }
    }


}
