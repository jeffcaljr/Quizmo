package com.example.jeff.viewpagerdelete.GroupQuiz.View;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers.GroupQuizCodeActivity2;
import com.example.jeff.viewpagerdelete.GroupQuiz.ActivityControllers.GroupWaitingArea;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GroupStatus;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GroupUser;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupFetcher;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupWaitingQueueService;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Jeff on 3/24/17.
 */

public class GroupWaitingAreaFragment extends Fragment implements GroupFetcher.GroupStatusFetcher, GroupWaitingQueueService.StatusCheckListener {

    public static final String ARG_GROUP = "ARG_GROUP";
    public static final String ARG_COURSE = "ARG_COURSE";
    public static final String ARG_GRADED_QUIZ = "ARG_GRADED_QUIZ";
    public static final String FRAG_TAG_STATUS_CHECKER = "FRAG_TAG_STATUS_CHECKER";

    private Group group;
    private ArrayList<String> memberNames;
    private Course course;
    private GradedQuiz quiz;
    private ArrayList<GroupStatus> statuses;
    private GroupStatus firstFinished;

    private GroupFetcher groupFetcher;

    private TextView groupNameTextView;
    private Button startGroupQuizButton;

    private Drawable doneDrawable;
    private Drawable inProgressDrawable;
    private Drawable notStartedDrawable;


    private RecyclerView recyclerView;
    private GroupAdapter adapter;

    private GroupWaitingQueueService statusChecker;

    private boolean statusLoaded = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.group_waiting_area_fragment, container, false);

        Bundle args = getArguments();

        if (args != null && args.containsKey(ARG_GROUP) && args.containsKey(ARG_COURSE) && args.containsKey(ARG_GRADED_QUIZ)) {
            group = (Group) args.getSerializable(ARG_GROUP);
            course = (Course) args.getSerializable(ARG_COURSE);
            quiz = (GradedQuiz) args.getSerializable(ARG_GRADED_QUIZ);

            statuses = new ArrayList<>();

            doneDrawable = ContextCompat.getDrawable(getContext(), R.drawable.group_waiting_area_indicator_icon_done);
            inProgressDrawable = ContextCompat.getDrawable(getContext(), R.drawable.group_waiting_area_indicator_icon_in_progress);
            notStartedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.group_waiting_area_indicator_icon_not_started);

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
            startGroupQuizButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Should start group quiz", Toast.LENGTH_LONG).show();
                }
            });

            groupFetcher = new GroupFetcher(getContext());

            statusChecker = new GroupWaitingQueueService();
            statusChecker.setStatusCheckListener(this);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(statusChecker, FRAG_TAG_STATUS_CHECKER)
                    .commit();


        }


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statusChecker.startSequence();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        statusChecker.stopSequence();
    }

    /**
     * Checks if all members in the group - who have started the individual quiz - have finished it.
     */
    private void checkStatusFinished() {

        //loop through current users who have started the individual quiz to determine if they are all finished
        boolean groupFinished = true;

        for (GroupStatus status : statuses) {
            if (status.getStatus() != GroupStatus.Status.COMPLETE) {
                groupFinished = false;
            }
        }

        //if all members of the group who started the individual quiz are finished,
        // stop checking the status and let the finished members continue
        if (groupFinished) {
            statusChecker.stopSequence();
            startGroupQuizButton.setEnabled(true);
        }
    }

    //MARK: StatusCheckerListener Methods

    @Override
    public void updateStatus() {
        //send network request to check group status
        groupFetcher.getGroupStatus(this, group, course, quiz);
    }


    //MARK: GroupStatusFetcherListener Methods


    @Override
    public void onGroupStatusSuccess(ArrayList<GroupStatus> statuses) {

        //get an updated list of the statuses of each member
        statusLoaded = true;

        //Sort the statuses by username, to match the order of users in the recyclerview
        this.statuses = statuses;
        Collections.sort(this.statuses);

        //if no user has started the quiz; set the firstFinished user to null
        if (statuses.size() == 0) {
            firstFinished = null;
        } else {
            //loop through the users with a status != "not started" to find who started first
            //they will be the leader

            firstFinished = statuses.get(0);
            for (GroupStatus status : this.statuses) {
                if (status.getTimeStarted().compareTo(firstFinished.getTimeStarted()) < 0) {
                    firstFinished = status;
                }
            }
        }

        //determine if all the members who have started the individual quiz have finisher
        //at this point, the finished members will have the ability to start the group quiz
        checkStatusFinished();

        //refresh the recyclerview
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onGroupStatusFailure(VolleyError error) {
        Toast.makeText(getContext(), "Error fetching group statuses", Toast.LENGTH_LONG).show();
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
            //since the number of users in the group may not match the number of users who started the individual quiz,
            //need to tell viewholder if each member has started (has a status) or not

            GroupStatus status = null;
            GroupUser user = group.getMembers().get(position);

            //determine if the user at this position has started the individual quiz (has a status) or not

            for (GroupStatus s : statuses) {
                if (s.getUserID().toLowerCase().equals(user.getUserID().toLowerCase())) {
                    status = s;
                    break;
                }
            }

            holder.bindMember(user, status);
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

            memberName.setText(member.getFirstName() + " " + member.getLastName());

            leaderIcon.setVisibility(View.INVISIBLE);

            //check if the given group member finished first; and is the leader
            //find who finished first

            if (statusLoaded) { //dont want this code to run on first recyclerview load

                if (status == null) {
                    //user hasn't started
                    statusIcon.setImageDrawable(notStartedDrawable);
                } else {

                    //check if user has completed the quiz; and if so; were they the first to start it
                    //if they were the first to start it, denote them as the leader

                    if (status.getStatus() == GroupStatus.Status.COMPLETE) { //if the user is done, show the green indicators

                        statusIcon.setImageDrawable(doneDrawable);

                        //TODO: Currently indicates that the leader is the first to start the quiz;
                        //TODO: Should indicate the leader based on who was first to finish the quiz

                        //if the user is done and they were the first finished, show the leader indicator
                        if (firstFinished.getUserID().equals(member.getUserID())) {
                            leaderIcon.setVisibility(View.VISIBLE);
                        }

                    } else {
                        //member is in progress of completing the individual quiz
                        statusIcon.setImageDrawable(inProgressDrawable);
                    }


                }
            }

        }
    }


}
