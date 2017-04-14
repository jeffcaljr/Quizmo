package com.example.jeff.viewpagerdelete.Homepage.View;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.example.jeff.viewpagerdelete.R;

import java.util.ArrayList;

/**
 * Created by Jeff on 4/11/17.
 */

public class CourseListFragment extends Fragment {

    public static final String ARG_COURSES_COURSE_LIST_FRAGMENT = "ARG_COURSES_COURSE_LIST_FRAGMENT";

    private ArrayList<Course> courses;

    private CourseListListener mListener;

    private TextView coursesEmptyTextView;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;

    Typeface regularFace;
    Typeface boldFace;
    Typeface boldItalicFace;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses_list, container, false);

        Bundle args = getArguments();

        if(args != null && args.containsKey(ARG_COURSES_COURSE_LIST_FRAGMENT)){
            courses = (ArrayList<Course>) args.getSerializable(ARG_COURSES_COURSE_LIST_FRAGMENT);
        }
        else{
            courses = new ArrayList<>();
        }

        regularFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/robotoRegular.ttf");
        boldFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/robotoBold.ttf");
        boldItalicFace = Typeface.createFromAsset(getContext(). getAssets(),"fonts/robotoBoldItalic.ttf");

        recyclerView = (RecyclerView) view.findViewById(R.id.course_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        adapter = new CourseAdapter();
        recyclerView.setAdapter(adapter);

        coursesEmptyTextView = (TextView) view.findViewById(R.id.courses_list_empty_tv);

        try{
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle("Courses");
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }


        coursesEmptyTextView.setTypeface(boldFace);


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (CourseListListener) context;
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    public interface CourseListListener{
        void courseItemClicked(Course course);
    }


    private class CourseAdapter extends RecyclerView.Adapter<CourseHolder>{
        @Override
        public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_listitem, parent, false);

            return new CourseHolder(view);
        }

        @Override
        public void onBindViewHolder(CourseHolder holder, int position) {

            holder.bindCourse(courses.get(position));

        }

        @Override
        public int getItemCount() {
            int size = courses.size();

            if(size == 0){
                coursesEmptyTextView.setVisibility(View.VISIBLE);
            }
            else{
                coursesEmptyTextView.setVisibility(View.GONE);
            }
            return size;
        }
    }

    private class CourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView courseNameTextView;
        private TextView instructorNameTextView;

        public CourseHolder(View itemView) {
            super(itemView);

            courseNameTextView = (TextView) itemView.findViewById(R.id.course_name_textview);
            instructorNameTextView = (TextView) itemView.findViewById(R.id.course_instructor_textview);

            courseNameTextView.setTypeface(regularFace);
            instructorNameTextView.setTypeface(regularFace);

            itemView.setOnClickListener(this);
        }

        public void bindCourse(Course course){
            courseNameTextView.setText(course.getExtendedID() + ": " + course.getName());
            instructorNameTextView.setText(course.getInstructor());
        }

        @Override
        public void onClick(View view) {
            mListener.courseItemClicked(courses.get(this.getAdapterPosition()));
        }
    }
}