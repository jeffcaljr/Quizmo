package com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupFetcher;
import com.example.jeff.viewpagerdelete.GroupQuiz.View.GroupQuizCodeFragment;
import com.example.jeff.viewpagerdelete.R;

import java.util.ArrayList;

public class GroupQuizCodeActivity extends AppCompatActivity implements GroupFetcher.GroupFetcherListener {

    public static final String ARG_GROUP = "ARG_GROUP";
    public static final String FRAG_TAG_GROUP_QUIZ_CODE_FRAGMENT = "FRAG_TAG_GROUP_QUIZ_CODE_FRAGMENT";

    private FragmentManager manager;
    private GroupQuizCodeFragment groupQuizCodeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_quiz_code);

        manager = getSupportFragmentManager();

        GroupFetcher.sharedInstance(this).downloadGroupForUser(this, "jcd39");


    }

    @Override
    public void onDownloadAllGroupsSuccess(ArrayList<Group> groups) {

    }

    @Override
    public void onDownloadSingleGroupSuccess(Group group) {

        groupQuizCodeFragment = (GroupQuizCodeFragment) manager.findFragmentByTag(FRAG_TAG_GROUP_QUIZ_CODE_FRAGMENT);

        if(groupQuizCodeFragment == null){
            groupQuizCodeFragment = new GroupQuizCodeFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_GROUP, group);

            groupQuizCodeFragment.setArguments(args);

            manager.beginTransaction()
                    .add(R.id.group_code_fragment_container, groupQuizCodeFragment, FRAG_TAG_GROUP_QUIZ_CODE_FRAGMENT)
                    .commit();

        }

    }

    @Override
    public void onDownloadAllGroupsFailure(VolleyError error) {

    }

    @Override
    public void onDownloadSingleGroupFailure(VolleyError error) {

    }
}
