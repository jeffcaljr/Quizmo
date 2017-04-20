package com.example.jeff.viewpagerdelete.Homepage.View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
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
  private ArrayList<Course> coursesCopy;

    private QuizListListener mListener;

  private SearchView searchView;
  private RelativeLayout quizzesEmptyView;
    private RecyclerView mRecyclerView;
    private QuizAdapter mAdapter;

    private Animation shake;

    Typeface regularFace;
    Typeface boldFace;
    Typeface boldItalicFace;

  private int colorPrimaryBright;
  private int colorWarningYellow;

    boolean tempDeleteThis = false; //only used to prevent countdowntimer from constantly re-coloring views


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_quizzes_list, container, false);

        Bundle args = getArguments();

        if(args != null && args.containsKey(ARG_COURSES_QUIZ_LIST_FRAGMENT)){
            courses = (ArrayList<Course>) args.get(ARG_COURSES_QUIZ_LIST_FRAGMENT);

          //TODO: Test code proceeding; delete later

          //add two dummy quizzes to the list

          courses.add(courses.get(0));
          courses.add(courses.get(0));
          //TODO: Test code preceeding; delete later


          coursesCopy = new ArrayList<>();
        }

        regularFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/robotoRegular.ttf");
        boldFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/robotoBold.ttf");
        boldItalicFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/robotoBoldItalic.ttf");

      quizzesEmptyView = (RelativeLayout) view.findViewById(R.id.quizzes_list_empty_tv);
      searchView = (SearchView) view.findViewById(R.id.quiz_search_view);

      searchView.setOnQueryTextListener(new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
          mAdapter.filter(query);
          return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
          mAdapter.filter(newText);
          return true;
        }
      });

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

        shake = AnimationUtils.loadAnimation(getContext(), R.anim.shakeanim);
      colorPrimaryBright = ContextCompat.getColor(getContext(), R.color.colorPrimaryBright);
      colorWarningYellow = ContextCompat.getColor(getContext(), R.color.jccolorWarningYellow);

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

      public QuizAdapter() {
        coursesCopy.addAll(courses);
      }

      @Override
        public QuizHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
          View view = inflater.inflate(R.layout.list_item_quiz, parent, false);

            return new QuizHolder(view);
        }


        @Override
        public void onBindViewHolder(QuizHolder holder, int position) {
            //TODO: The following line shows the same quiz for every itemview in the recycler view
            //this is only for test purposes
            holder.bindQuiz(courses.get(0));
//            holder.bindQuiz(courses.get(position));

        }

        @Override
        public int getItemCount() {

            //TODO: The following line is for test purposes
                //because there is currently one quiz in the network, and I want to show a list, I
                //am showing the same quiz multiple times
//            return 3;

          int size = courses.size();

          if (size == 0) {
            quizzesEmptyView.setVisibility(View.VISIBLE);
          } else {
            quizzesEmptyView.setVisibility(View.GONE);
          }
          return size;


        }

      public void filter(String text) {
        courses.clear();
        if (text.isEmpty()) {
          courses.addAll(coursesCopy);
        } else {
          text = text.toLowerCase().trim();
          for (Course course : coursesCopy) {
            String quizName = course.getQuiz().getDescription().toLowerCase().trim();
            if (quizName.contains(text)) {
              courses.add(course);
            }
          }
        }
        notifyDataSetChanged();
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
            availableDate.setTime(new Date());

            if(this.getAdapterPosition() == 0){

                //TODO: should be set to the quizzes available date, not todays date

                availableDate.add(Calendar.MINUTE, q.getTimedLength());

            }
            else if(this.getAdapterPosition() == 1){
                //make quiz expire in 5 mintes and 20 seconds
                itemView.setEnabled(false);
                availableDate.add(Calendar.MINUTE, 5);
                availableDate.add(Calendar.SECOND, 20);
            }
            else{
                availableDate.setTime(q.getAvailableDate());
            }

            long expiryTime = availableDate.getTimeInMillis() - new Date().getTime();



            new CountDownTimer(expiryTime, 1000){
                @Override
                public void onTick(long millisecondsUntilFinished) {
                    timeRemaining = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisecondsUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisecondsUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisecondsUntilFinished)));
                    timeRemainingTextView.setText(timeRemaining);

                    if(millisecondsUntilFinished < 5 * 60 * 1000){

                        if(!tempDeleteThis){
                            itemView.startAnimation(shake);
                          ((CardView) itemView).setCardBackgroundColor(colorWarningYellow);
                            tempDeleteThis = true;
                        }


                    }
                }

                @Override
                public void onFinish() {
                    itemView.clearAnimation();
                    timeRemainingTextView.setText("Expired!");
                  ((CardView) itemView).setCardBackgroundColor(colorPrimaryBright);
                    quizNameTextView.setTextColor(Color.WHITE);
                    timeRemainingTextView.setTextColor(Color.WHITE);
                    itemView.setEnabled(false);
                }
            }.start();
        }

        @Override
        public void onClick(View view) {

            //TODO: The following line sets the click listener for a cell to pass the firs quiz in the list to the listener
                //no matter what; this is only for testing purposes
            mListener.itemClicked((courses.get(0)));
//            mListener.itemClicked(courses.get(this.getAdapterPosition()));
        }
    }
}
