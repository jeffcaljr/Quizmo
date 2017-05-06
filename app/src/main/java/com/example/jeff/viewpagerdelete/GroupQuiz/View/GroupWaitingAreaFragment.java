package com.example.jeff.viewpagerdelete.GroupQuiz.View;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GroupMemberStatus;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GroupMember;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService.GroupStatusDownloadCallback;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupStatusPollingService;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

import java.util.ArrayList;

import info.hoang8f.widget.FButton;

/**
 * Created by Jeff on 3/24/17.
 */

public class GroupWaitingAreaFragment extends Fragment {

    public static final String TAG = "GroupWaitingAreaFragment";

    public static final String ARG_GROUP = "ARG_GROUP";
    public static final String ARG_COURSE = "ARG_COURSE";
    public static final String ARG_QUIZ = "ARG_QUIZ";
    public static final String FRAG_TAG_STATUS_CHECKER = "FRAG_TAG_STATUS_CHECKER";

    private Group group;
    private ArrayList<String> memberNames;
    private Course course;
    private Quiz quiz;
    private ArrayList<GroupMemberStatus> statuses;

    private GroupNetworkingService groupNetworkingService;

    private TextView groupNameTextView;
    private FButton startGroupQuizButton;
    private ImageButton refreshButton;

    private Drawable doneDrawable;
    private Drawable inProgressDrawable;
    private Drawable notStartedDrawable;

    private Animation spinAnimation;


    private RecyclerView recyclerView;
    private GroupAdapter adapter;
    private AlertDialog confirmStartDialog;

    private boolean statusLoaded = false;

    private OnGroupQuizStartedListener groupQuizStartListener;

    public interface OnGroupQuizStartedListener {

        void onGroupQuizStarted();

        void onLeaderFound(boolean isCurrentUserLeader);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group_waiting_area, container, false);

        Bundle args = getArguments();

        if (args != null && args.containsKey(ARG_GROUP) && args.containsKey(ARG_COURSE) && args.containsKey(ARG_QUIZ)) {
            group = (Group) args.getSerializable(ARG_GROUP);
            course = (Course) args.getSerializable(ARG_COURSE);
            quiz = (Quiz) args.getSerializable(ARG_QUIZ);

            statuses = new ArrayList<>();

            doneDrawable = ContextCompat.getDrawable(getContext(), R.drawable.group_waiting_area_indicator_icon_done);
            inProgressDrawable = ContextCompat.getDrawable(getContext(), R.drawable.group_waiting_area_indicator_icon_in_progress);
            notStartedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.group_waiting_area_indicator_icon_not_started);

            spinAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.spin);

            startGroupQuizButton = (FButton) view.findViewById(R.id.qroup_waiting_area_start_quiz_button);
            groupNameTextView = (TextView) view.findViewById(R.id.group_name_textview);
            refreshButton = (ImageButton) view.findViewById(R.id.group_waiting_area_refresh_imgbtn);

            groupNameTextView.setText(group.getName());


            recyclerView = (RecyclerView) view.findViewById(R.id.group_waiting_area_member_list);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            adapter = new GroupAdapter();

            recyclerView.setAdapter(adapter);

            confirmStartDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Start Group Quiz")
                    .setMessage("Are you sure you wish to start the quiz without all members being ready?")
                    .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            GroupStatusPollingService.setServiceAlarm(getActivity(), false, group, course, quiz);
                            groupQuizStartListener.onGroupQuizStarted();
                        }
                    }).create();


            memberNames = new ArrayList<>();

            for (GroupMember member : group.getMembers()) {
                memberNames.add(member.getFirstName() + " " + member.getLastName());
            }

//            startGroupQuizButton.setEnabled(false);
            startGroupQuizButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isWholeGroupFinished() == false) {
                        confirmStartDialog.show();
                    } else {
                        //whole group is finished; no need to chicken test

                        GroupStatusPollingService.setServiceAlarm(getActivity(), false, group, course, quiz);
                        groupQuizStartListener.onGroupQuizStarted();
                    }

                }
            });

            groupNetworkingService = new GroupNetworkingService(getContext());



        }


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //perform initial load of group members' status
        groupNetworkingService.getGroupStatus(group, course, quiz, new GroupStatusDownloadCallback() {
            @Override
            public void onGroupStatusSuccess(ArrayList<GroupMemberStatus> statuses) {
                statusLoaded = true;
                GroupWaitingAreaFragment.this.statuses = statuses;


                //after the initial load, if the entire group isn't finished, start polling for status updates

                if (isWholeGroupFinished() == false) {
                    GroupStatusPollingService.setServiceAlarm(getActivity(), true, group, course, quiz);
                } else {
                    checkStatusFinished();
                }


                //determine if the current user is the leader, and notify the listener
                for (GroupMemberStatus memberStatus : statuses) {
                    if (memberStatus.isLeader() == true) {
                        String userID = UserDataSource.getInstance().getUser().getUserID();
                        boolean isThisUserLeader = memberStatus.getUserID().equalsIgnoreCase(userID);
                        groupQuizStartListener.onLeaderFound(isThisUserLeader);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onGroupStatusFailure(VolleyError error) {

            }
        });



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            groupQuizStartListener = (OnGroupQuizStartedListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        GroupStatusPollingService.setServiceAlarm(getActivity(), false, group, course, quiz);
        groupQuizStartListener = null;
    }


    public void onStatusUpdate(ArrayList<GroupMemberStatus> statuses) {
        statusLoaded = true;
        this.statuses = statuses;
        checkStatusFinished();
        if (isWholeGroupFinished() == true) {
            GroupStatusPollingService.setServiceAlarm(getActivity(), false, group, course, quiz);
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * Checks if ALL members in the group  have finished the individual quiz, ands stops checking for status updates if so.
     * NOTE: The group will have the option to proceed if everyone who has started the quiz is finished; even if some members have not started
     * NOTE-continued: If the group of finished members wishes to proceed without members who haven't started; the "Start Group Quiz" button will stop the status update checks
     */
    private void checkStatusFinished() {

        //loop through current users who have started the individual quiz to determine if they are all finished
        boolean groupFinished = true;

        for (GroupMemberStatus status : statuses) {
            if (status.getStatus() != GroupMemberStatus.Status.COMPLETE) {
                groupFinished = false;
            }
        }

        //if all members of the group are finished,
        // stop checking the status and let the group continue

        if (groupFinished) {
            startGroupQuizButton.setEnabled(true);
        } else {
            startGroupQuizButton.setEnabled(false);
        }
    }

    private boolean isWholeGroupFinished() {
        boolean wholeGroupFinished = true;

        //if the number of members who have started the quiz isn't equal to the total number of members; they cant all be finished
        if (statuses.size() != group.getMembers().size()) {
            return false;
        } else {
            //if all members of the group have started, check if they are all finished, and if one isnt; then the whole group cant be finished
            for (GroupMemberStatus userStatus : statuses) {
                if (userStatus.getStatus() != GroupMemberStatus.Status.COMPLETE) {
                    wholeGroupFinished = false;
                    break;
                }
            }
        }

        return wholeGroupFinished;
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

            GroupMemberStatus status = null;
            GroupMember user = group.getMembers().get(position);

            //determine if the user at this position has started the individual quiz (has a status) or not

            for (GroupMemberStatus s : statuses) {
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

        public void bindMember(GroupMember member, GroupMemberStatus memberStatus) {

            memberName.setText(member.getFirstName() + " " + member.getLastName());

            leaderIcon.setVisibility(View.INVISIBLE);

            //check if the given group member finished first; and is the leader
            //find who finished first

            if (statusLoaded) { //dont want this code to run on first recyclerview load

                if (memberStatus == null) {
                    //user hasn't started
                    statusIcon.setImageDrawable(notStartedDrawable);
                } else {

                    //check if user has completed the quiz; and if so; were they the first to start it
                    //if they were the first to start it, denote them as the leader

                    if (memberStatus.getStatus()
                            == GroupMemberStatus.Status.COMPLETE) { //if the user is done, show the green indicators

                        statusIcon.setImageDrawable(doneDrawable);

                        //TODO: Currently indicates that the leader is the first to start the quiz;
                        //TODO: Should indicate the leader based on who was first to finish the quiz

                        //if the user is done and they were the first finished, show the leader indicator
                        if (memberStatus.isLeader()) {
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
