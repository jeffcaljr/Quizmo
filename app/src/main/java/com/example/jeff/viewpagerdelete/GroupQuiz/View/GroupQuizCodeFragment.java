package com.example.jeff.viewpagerdelete.GroupQuiz.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers.GroupQuizCodeActivity;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GroupUser;
import com.example.jeff.viewpagerdelete.R;

import java.util.ArrayList;

/**
 * Created by Jeff on 3/24/17.
 */

public class GroupQuizCodeFragment extends Fragment {

    private Group group;

    private EditText quizCodeField;
    private TextView groupNameTextview;
    private ListView groupMemberListView;

    private ArrayAdapter<String> mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.group_quiz_code_fragment, container, false);

        Bundle args = getArguments();

        if (args != null && args.containsKey(GroupQuizCodeActivity.ARG_GROUP)) {
            group = (Group) args.getSerializable(GroupQuizCodeActivity.ARG_GROUP);
            quizCodeField = (EditText) view.findViewById(R.id.group_quiz_code_edittext);
            groupNameTextview = (TextView) view.findViewById(R.id.group_name_textview);
            groupMemberListView = (ListView) view.findViewById(R.id.group_member_list_view);

            ArrayList<String> memberNames = new ArrayList<>();

            for (GroupUser member : group.getMembers()) {
                memberNames.add(member.getFirstName() + " " + member.getLastName());
            }

            groupNameTextview.setText(group.getName());

            mAdapter = new ArrayAdapter<>(getActivity(), R.layout.group_listview_item, R.id.group_listview_item_user_name, memberNames);

            groupMemberListView.setAdapter(mAdapter);
        }


        return view;
    }


}
