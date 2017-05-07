package com.example.jeff.viewpagerdelete.QuizStatistics.View;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GradedGroupQuiz;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Networking.GroupNetworkingService;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.Miscellaneous.ColorAlternatorUtil;
import com.example.jeff.viewpagerdelete.Miscellaneous.LoadingFragment;
import com.example.jeff.viewpagerdelete.R;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.util.ArrayList;

/**
 * Created by Jeff on 5/5/17.
 */

public class StatisticsGroupVsGroupFragment extends Fragment {
    public static final String TAG = "StatsGroupVGroupFrag";

    public static final String ARG_QUIZ = "ARG_QUIZ";
    public static final String ARG_COURSE = "ARG_COURSE";


    private Quiz quiz;
    private Course course;

    private BarChart mBarChart;
    private TextView noGroupStatsTextView;

    private LoadingFragment loadingFragment;

    private ColorAlternatorUtil colorAlternatorUtil;

    private GroupNetworkingService groupNetworkingService;

    private int groupProgressResponses = 0;
    private ArrayList<GradedGroupQuiz> gradedGroupQuizzes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_group_group_stats, container, false);

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_QUIZ) && args.containsKey(ARG_COURSE)) {
            quiz = (Quiz) args.get(ARG_QUIZ);
            course = (Course) args.getSerializable(ARG_COURSE);
        } else {
            Log.e(TAG, "Arg error");
        }

        gradedGroupQuizzes = new ArrayList<>();
        mBarChart = (BarChart) view.findViewById(R.id.statistics_graph_view);

        noGroupStatsTextView = (TextView) view.findViewById(R.id.group_stats_not_found_label);


        groupNetworkingService = new GroupNetworkingService(getActivity());


        colorAlternatorUtil = new ColorAlternatorUtil(getActivity());

        loadingFragment = new LoadingFragment(getActivity(), "Loading data for Class");
        loadingFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (mBarChart != null) {
                    mBarChart.startAnimation();
                }
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadingFragment.show();

        groupNetworkingService.downloadAllGroups(course.getId(), new GroupNetworkingService.AllGroupFetcherListener() {
            @Override
            public void onDownloadAllGroupsSuccess(final ArrayList<Group> groups) {

                for (final Group group : groups) {
                    groupNetworkingService.getGroupQuizProgress(quiz, group, new GroupNetworkingService.GroupQuizProgressDownloadCallback() {
                        @Override
                        public void onGroupQuizProgressSuccess(GradedGroupQuiz gradedGroupQuiz) {
                            groupProgressResponses++;
                            gradedGroupQuizzes.add(gradedGroupQuiz);

                            BarModel newBarModel = new BarModel(group.getName().substring(0, (group.getName().length() > 8 ? 8 : 5)), gradedGroupQuiz.getTotalPointsScored(), colorAlternatorUtil.getNextDefaultColorSet().getBackGroundColor());
                            newBarModel.setShowLabel(true);
                            mBarChart.addBar(newBarModel);

                            if (groupProgressResponses == groups.size()) {
                                loadingFragment.dismissWithDelay(500);
//                                mBarChart.startAnimation();

                            }
                        }

                        @Override
                        public void onGroupQuizProgressFailure(VolleyError error) {
                            groupProgressResponses++;
                            //TODO: Score for groups not found is test code; delete later
                            BarModel newBarModel = new BarModel(group.getName().substring(0, (group.getName().length() > 8 ? 8 : 5)), 14, colorAlternatorUtil.getNextDefaultColorSet().getBackGroundColor());
                            newBarModel.setShowLabel(true);
                            mBarChart.addBar(newBarModel);

                            if (groupProgressResponses == groups.size()) {
                                loadingFragment.dismissWithDelay(500);
//                                mBarChart.startAnimation();
                            }

                        }
                    });
                }
            }

            @Override
            public void onDownloadAllGroupsFailure(VolleyError error) {
                loadingFragment.dismissWithDelay(500);
                Toast.makeText(getActivity(), "Failed to load group stats", Toast.LENGTH_SHORT).show();
                mBarChart.setVisibility(View.GONE);
                noGroupStatsTextView.setVisibility(View.VISIBLE);
            }
        });

    }


}
