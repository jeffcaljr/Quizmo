package com.example.jeff.viewpagerdelete.Homepage.View;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.Toast;
import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.Miscellaneous.ColorAlternatorUtil;
import com.example.jeff.viewpagerdelete.R;

import com.example.jeff.viewpagerdelete.Startup.Model.User;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;
import com.example.jeff.viewpagerdelete.Startup.Networking.UserNetworkingService;
import com.example.jeff.viewpagerdelete.Startup.Networking.UserNetworkingService.OnUserDownloadedCallback;
import com.example.jeff.viewpagerdelete.Startup.Networking.UserNetworkingService.OnUserDownloadedCallback;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jeff on 4/11/17.
 */

public class CourseListFragment extends Fragment implements OnRefreshListener {

    public static final String ARG_COURSES_COURSE_LIST_FRAGMENT = "ARG_COURSES_COURSE_LIST_FRAGMENT";

    private ArrayList<Course> courses;
  private ArrayList<Course> coursesCopy;

    private CourseListListener mListener;

  private SwipeRefreshLayout swipeRefreshLayout;
  private SearchView searchView;
  private RelativeLayout coursesEmptyView;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses_list, container, false);

        Bundle args = getArguments();

        if(args != null && args.containsKey(ARG_COURSES_COURSE_LIST_FRAGMENT)){

            courses = (ArrayList<Course>) args.getSerializable(ARG_COURSES_COURSE_LIST_FRAGMENT);

          //TODO: Test code; delete proceeding later
          if (courses.size() == 1) {
            Course firstCourse = courses.get(0);
            courses.add(new Course(firstCourse.getId(), firstCourse.getCourseID(),
                firstCourse.getExtendedID(), null, firstCourse.getSemester(),
                firstCourse.getInstructor(), firstCourse.getQuiz()));
            courses.get(1).setName("Ada Programming");
          }
          //TODO: Test code; delete preceeding later

          Collections.sort(courses);


          coursesCopy = new ArrayList<>();
        }
        else{
            courses = new ArrayList<>();
        }


      searchView = (SearchView) view.findViewById(R.id.course_search_view);

      searchView.setOnQueryTextListener(new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
          adapter.filter(query);
          return true;

        }

        @Override
        public boolean onQueryTextChange(String newText) {
          adapter.filter(newText);
          return true;
        }
      });

      swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.course_list_swipe_refresher);

      swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.course_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        adapter = new CourseAdapter();
        recyclerView.setAdapter(adapter);

      coursesEmptyView = (RelativeLayout) view.findViewById(R.id.courses_list_empty_tv);

        try{
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle("Courses");
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

//        public ColorAlternatorUtil colorAlternatorUtil;

      public CourseAdapter() {
        coursesCopy.addAll(courses);
//          colorAlternatorUtil = new ColorAlternatorUtil(getActivity());
      }

      @Override
        public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.list_item_course_list, parent, false);

            return new CourseHolder(view);
        }

        @Override
        public void onBindViewHolder(CourseHolder holder, int position) {

//            holder.bindCourse(courses.get(position), colorAlternatorUtil.getNextDefaultColorSet());
            holder.bindCourse(courses.get(position));

        }

        @Override
        public int getItemCount() {
            int size = courses.size();

            if(size == 0){
              coursesEmptyView.setVisibility(View.VISIBLE);
            }
            else{
              coursesEmptyView.setVisibility(View.INVISIBLE);
            }

            return size;
        }

      public void filter(String text) {
        courses.clear();
        if (text.isEmpty()) {
          courses.addAll(coursesCopy);
        } else {
          text = text.toLowerCase();
          for (Course course : coursesCopy) {
            String courseID = course.getCourseID().toLowerCase();
            String courseName = course.getName().toLowerCase();
            String instructorName = course.getInstructor().toLowerCase();

            if (courseID.contains(text) || courseName.contains(text) || instructorName
                .contains(text)) {
              courses.add(course);
            }
          }
        }
        notifyDataSetChanged();
      }
    }

    private class CourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView courseNameTextView;
        private TextView instructorNameTextView;

        public CourseHolder(View itemView) {
            super(itemView);

            courseNameTextView = (TextView) itemView.findViewById(R.id.course_name_textview);
            instructorNameTextView = (TextView) itemView.findViewById(R.id.course_instructor_textview);


            itemView.setOnClickListener(this);
        }

        //        public void bindCourse(Course course, ColorAlternatorUtil.ColorSet colorSet) {
        public void bindCourse(Course course) {

//        itemView.setBackgroundColor(colorSet.getBackGroundColor());

//            courseNameTextView.setTextColor(colorSet.getForegroundColor());
//            instructorNameTextView.setTextColor(colorSet.getForegroundColor());

            courseNameTextView.setText(course.getExtendedID() + ": " + course.getName());
            instructorNameTextView.setText(course.getInstructor());
        }

        @Override
        public void onClick(View view) {
            mListener.courseItemClicked(courses.get(this.getAdapterPosition()));
        }
    }

  //MARK: OnRefreshListener Methods


  @Override
  public void onRefresh() {

    UserNetworkingService userNetworkingService = new UserNetworkingService(getContext());

    userNetworkingService.downloadUser(UserDataSource.getInstance().getUser().getUserID(),
            new OnUserDownloadedCallback() {
          @Override
          public void userDownloadSuccess(User user) {
            UserDataSource.getInstance().setUser(user);
            courses = user.getEnrolledCourses();
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
          }

          @Override
          public void userDownloadFailure(VolleyError error) {
            courses.clear();
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
          }
        });

  }
}
