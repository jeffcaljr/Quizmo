package com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupFetcher;
import com.example.jeff.viewpagerdelete.GroupQuiz.View.GroupQuizCodeFragment;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.UserDataSource;

import java.util.ArrayList;

public class GroupQuizCodeActivity2 extends AppCompatActivity implements GroupFetcher.SingleGroupFetcherListener {

    public static final String EXTRA_COURSE = "EXTRA_COURSE";
    public static final String ARG_GROUP = "ARG_GROUP";
    public static final String FRAG_TAG_GROUP_QUIZ_CODE_FRAGMENT = "FRAG_TAG_GROUP_QUIZ_CODE_FRAGMENT";

    private FragmentManager manager;
    private GroupQuizCodeFragment groupQuizCodeFragment;
    private Course course;

    private GroupFetcher groupFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_quiz_code);

        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey(EXTRA_COURSE)) {
            course = (Course) extras.getSerializable(EXTRA_COURSE);
        } else {
            Log.e("TAG", "expected required extra \"EXTRA_COURSE\" in GroupWaitingAreaActivity");
            finish();
        }

        manager = getSupportFragmentManager();
        groupFetcher = new GroupFetcher(this);

        groupFetcher.downloadGroupForUser(this, UserDataSource.getInstance().getUser().getUserID(), course.getCourseID());


    }


    @Override
    public void onDownloadSingleGroupSuccess(Group group) {

        groupQuizCodeFragment = (GroupQuizCodeFragment) manager.findFragmentByTag(FRAG_TAG_GROUP_QUIZ_CODE_FRAGMENT);

        if (groupQuizCodeFragment == null) {
            groupQuizCodeFragment = new GroupQuizCodeFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_GROUP, group);

            groupQuizCodeFragment.setArguments(args);

            manager.beginTransaction()
                    .replace(R.id.group_code_fragment_container, groupQuizCodeFragment, FRAG_TAG_GROUP_QUIZ_CODE_FRAGMENT)
                    .commit();

        }

    }


    @Override
    public void onDownloadSingleGroupFailure(VolleyError error) {
        Snackbar.make(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), "Failed to load group", Snackbar.LENGTH_LONG).show();
    }
}
