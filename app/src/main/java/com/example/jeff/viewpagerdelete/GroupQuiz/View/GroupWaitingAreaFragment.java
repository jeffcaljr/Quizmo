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
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.UserGroupStatus;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GroupUser;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupWaitingQueueService;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jeff on 3/24/17.
 */

public class GroupWaitingAreaFragment extends Fragment implements
    GroupNetworkingService.GroupStatusFetcher, GroupWaitingQueueService.StatusCheckListener {

    public static final String ARG_GROUP = "ARG_GROUP";
    public static final String ARG_COURSE = "ARG_COURSE";
    public static final String ARG_GRADED_QUIZ = "ARG_GRADED_QUIZ";
    public static final String FRAG_TAG_STATUS_CHECKER = "FRAG_TAG_STATUS_CHECKER";

    private Group group;
    private ArrayList<String> memberNames;
    private Course course;
    private GradedQuiz quiz;
  private ArrayList<UserGroupStatus> statuses;
  private UserGroupStatus firstFinished;

  private GroupNetworkingService groupNetworkingService;

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

      View view = inflater.inflate(R.layout.fragment_group_waiting_area, container, false);

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
                    statusChecker.stopSequence();
                    Toast.makeText(getContext(), "Should start group quiz", Toast.LENGTH_LONG).show();
                }
            });

          groupNetworkingService = new GroupNetworkingService(getContext());

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
     * Checks if ALL members in the group  have finished the individual quiz, ands stops checking for status updates if so.
     * NOTE: The group will have the option to proceed if everyone who has started the quiz is finished; even if some members have not started
     * NOTE-continued: If the group of finished members wishes to proceed without members who haven't started; the "Start Group Quiz" button will stop the status update checks
     */
    private void checkStatusFinished() {

        //loop through current users who have started the individual quiz to determine if they are all finished
        boolean groupFinished = true;

      for (UserGroupStatus status : statuses) {
        if (status.getStatus() != UserGroupStatus.Status.COMPLETE) {
                groupFinished = false;
            }
        }

        //if all members of the group are finished,
        // stop checking the status and let the group continue

        if (groupFinished) {
            startGroupQuizButton.setEnabled(true);
        }
        if (statuses.size() == group.getMembers().size()) {
            statusChecker.stopSequence();

        }
    }

    //MARK: StatusCheckerListener Methods

    @Override
    public void updateStatus() {
        //send network request to check group status
        Toast.makeText(getContext(), "Refreshing", Toast.LENGTH_SHORT).show();
      groupNetworkingService.getGroupStatus(this, group, course, quiz);
    }


    //MARK: GroupStatusFetcherListener Methods


    @Override
    public void onGroupStatusSuccess(ArrayList<UserGroupStatus> statuses) {

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
          for (UserGroupStatus status : this.statuses) {
                if (status.getTimeStarted().compareTo(firstFinished.getTimeStarted()) < 0) {
                    firstFinished = status;
                }
            }
        }

        //TODO: Remove the proceeding code later
        //Because Ryan and Josh's quizzes are stuck in limbo; this hack will trick the app into thinking they are finished

        for (int i = 0; i < this.statuses.size(); i++) {
            if (statuses.get(i).getUserID().toLowerCase().equals("rpd4g5") || statuses.get(i).getUserID().toLowerCase().equals("jkv2c9")) {
              this.statuses.get(i).setStatus(UserGroupStatus.Status.COMPLETE);
            }
        }


        //TODO: Remove the preceeding code later

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
          View view = inflater.inflate(R.layout.list_item_group_waiting_area, parent, false);

            return new GroupMemberHolder(view);
        }

        @Override
        public void onBindViewHolder(GroupMemberHolder holder, int position) {
            //since the number of users in the group may not match the number of users who started the individual quiz,
            //need to tell viewholder if each member has started (has a status) or not

          UserGroupStatus status = null;
            GroupUser user = group.getMembers().get(position);

            //determine if the user at this position has started the individual quiz (has a status) or not

          for (UserGroupStatus s : statuses) {
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

      public void bindMember(GroupUser member, UserGroupStatus status) {

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

                  if (status.getStatus()
                      == UserGroupStatus.Status.COMPLETE) { //if the user is done, show the green indicators

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
