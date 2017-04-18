package com.example.jeff.viewpagerdelete.GroupQuiz.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers.GroupQuizCodeActivity;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GroupStatus;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GroupUser;
import com.example.jeff.viewpagerdelete.R;

import java.util.ArrayList;

/**
 * Created by Jeff on 3/24/17.
 */

public class GroupQuizCodeFragment extends Fragment {

    private Group group;
    private ArrayList<String> memberNames;


    private TextView groupNameTextView;
    private Button startGroupQuizButton;


    private RecyclerView recyclerView;
    private GroupAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.group_quiz_code_fragment, container, false);

        Bundle args = getArguments();

        if (args != null && args.containsKey(GroupQuizCodeActivity.ARG_GROUP)) {
            group = (Group) args.getSerializable(GroupQuizCodeActivity.ARG_GROUP);

            startGroupQuizButton = (Button) view.findViewById(R.id.qroup_waiting_area_start_quiz_button);
            groupNameTextView = (TextView) view.findViewById(R.id.group_name_textview);

            groupNameTextView.setText(group.getName());


            recyclerView = (RecyclerView) view.findViewById(R.id.group_waiting_area_member_list);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            adapter = new GroupAdapter();

            recyclerView.setAdapter(adapter);


            memberNames = new ArrayList<>();

            for (GroupUser member : group.getMembers()) {
                memberNames.add(member.getFirstName() + " " + member.getLastName());
            }

            startGroupQuizButton.setEnabled(false);

        }


        return view;
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupMemberHolder> {
        @Override
        public GroupMemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.group_waiting_area_list_item, parent, false);

            return new GroupMemberHolder(view);
        }

        @Override
        public void onBindViewHolder(GroupMemberHolder holder, int position) {
            holder.bindMember(group.getMembers().get(position), null);
        }

        @Override
        public int getItemCount() {
            return group.getMembers().size();
        }
    }


    private class GroupMemberHolder extends RecyclerView.ViewHolder {

        private ImageView leaderIcon;
        private TextView memberName;
        private ImageView statusIcon;

        public GroupMemberHolder(View itemView) {
            super(itemView);
            leaderIcon = (ImageView) itemView.findViewById(R.id.group_waiting_area_list_item_leader_icon);
            memberName = (TextView) itemView.findViewById(R.id.group_waiting_area_list_item_user_name);
            statusIcon = (ImageView) itemView.findViewById(R.id.group_waiting_area_list_item_status_indicator);
        }

        public void bindMember(GroupUser member, GroupStatus status) {
            if (getAdapterPosition() != 0) {
                leaderIcon.setVisibility(View.INVISIBLE);
            }
            String name = memberNames.get(getAdapterPosition());
            memberName.setText(name);

            if (status == null) {
                if (getAdapterPosition() == 0) {
                    statusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.group_waiting_area_indicator_icon_done));

                } else if (getAdapterPosition() < 3) {
                    statusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.group_waiting_area_indicator_icon_in_progress));

                }

            } else {
                if (status.getStatus() == "complete") {
                    statusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.group_waiting_area_indicator_icon_done));
                } else if (status.getStatus() == "progress" || getAdapterPosition() == 1 || getAdapterPosition() == 2) {
                    statusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.group_waiting_area_indicator_icon_in_progress));

                }
            }

        }
    }


}
