package com.example.jeff.viewpagerdelete.GroupQuiz.View;

import android.content.Context;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.UserGroupStatus;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GroupUser;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService.GroupStatusDownloadCallback;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.PollingService;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.R;

import java.util.ArrayList;
import java.util.Collections;

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
    private ArrayList<UserGroupStatus> statuses;

    private GroupNetworkingService groupNetworkingService;

    private TextView groupNameTextView;
    private Button startGroupQuizButton;
    private ImageButton refreshButton;

    private Drawable doneDrawable;
    private Drawable inProgressDrawable;
    private Drawable notStartedDrawable;

    private Animation spinAnimation;


    private RecyclerView recyclerView;
    private GroupAdapter adapter;


    private boolean statusLoaded = false;

    private OnGroupQuizStartedListener groupQuizStartListener;

    public interface OnGroupQuizStartedListener {

        void onGroupQuizStarted();
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

            startGroupQuizButton = (Button) view.findViewById(R.id.qroup_waiting_area_start_quiz_button);
            groupNameTextView = (TextView) view.findViewById(R.id.group_name_textview);
            refreshButton = (ImageButton) view.findViewById(R.id.group_waiting_area_refresh_imgbtn);

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
                    PollingService.setServiceAlarm(getActivity(), false, group, course, quiz);
                    groupQuizStartListener.onGroupQuizStarted();
                }
            });

            groupNetworkingService = new GroupNetworkingService(getContext());

        }


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PollingService.setServiceAlarm(getActivity(), true, group, course, quiz);
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
        PollingService.setServiceAlarm(getActivity(), false, group, course, quiz);
        groupQuizStartListener = null;
    }


    public void onStatusUpdate(ArrayList<UserGroupStatus> statuses) {
        statusLoaded = true;
        Toast.makeText(getActivity(), statuses.toString(), Toast.LENGTH_LONG).show();
        this.statuses = statuses;
        checkStatusFinished();
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

        for (UserGroupStatus status : statuses) {
            if (status.getStatus() != UserGroupStatus.Status.COMPLETE) {
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
        if (statuses.size() == group.getMembers().size()) {
            PollingService.setServiceAlarm(getActivity(), false, group, course, quiz);

        }
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
                        if (status.isLeader()) {
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
