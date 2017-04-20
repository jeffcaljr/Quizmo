package com.example.jeff.viewpagerdelete.GroupQuiz.Networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.UserGroupStatus;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.Miscellaneous.RequestService;
import com.example.jeff.viewpagerdelete.Miscellaneous.ServerProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jeff on 3/24/17.
 */

public class GroupNetworkingService {

    private Context context;


  public GroupNetworkingService(Context context) {
        this.context = context.getApplicationContext();
    }


    public void downloadAllGroups(final AllGroupFetcherListener listener, String courseID) {

        if (listener == null) {
            return;
        }

        String urlString = ServerProperties.baseURL + "/groups?course_id=" + courseID;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, ServerProperties.groupURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                ArrayList<Group> groups = new ArrayList<>();


                for (int i = 0; i < response.length(); i++) {
                    try {
                        groups.add(new Group(response.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                listener.onDownloadAllGroupsSuccess(groups);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GROUP_FETCHER", "Failed to load groups from server");
                listener.onDownloadAllGroupsFailure(error);
            }
        });

        RequestService.getInstance(context).addRequest(request);
    }

    public void downloadGroup(final SingleGroupFetcherListener listener, String groupID) {

        if (listener == null) {
            return;
        }

        String urlString = ServerProperties.groupURL + groupID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onDownloadSingleGroupSuccess(new Group(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onDownloadSingleGroupFailure(error);
            }
        });

        RequestService.getInstance(context).addRequest(request);
    }

    public void downloadGroupForUser(final SingleGroupFetcherListener listener, String userID, String courseID) {

        if (listener == null) {
            return;
        }

        String urlString = ServerProperties.userGroupURL + "?user_id=" + userID + "&course_id=" + courseID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                listener.onDownloadSingleGroupSuccess(new Group(response));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onDownloadSingleGroupFailure(error);
            }
        });

        RequestService.getInstance(context).addRequest(request);
    }

    public void getGroupStatus(final GroupStatusFetcher listener, Group group, Course course, GradedQuiz quiz) {

        if (listener == null) {
            return;
        }

        String groupID = group.getId();
        String courseID = course.getCourseID();
        String quizID = quiz.getQuizID();
        String sessionID = quiz.getSessionID();

        String urlString = ServerProperties.groupStatusURL + "?group_id=" + groupID + "&course_id=" + courseID + "&quiz_id=" + quizID + "&session_id=" + sessionID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray status = response.getJSONArray("status");
                  ArrayList<UserGroupStatus> statuses = new ArrayList<>();

                    for (int i = 0; i < status.length(); i++) {
                      UserGroupStatus userGroupStatus = new UserGroupStatus(
                          status.getJSONObject(i));
                      statuses.add(userGroupStatus);

                    }

                    listener.onGroupStatusSuccess(statuses);

                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onGroupStatusFailure(new VolleyError("Error parsing JSON response"));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onGroupStatusFailure(error);
            }
        });

        RequestService.getInstance(context).addRequest(request);
    }

    public interface AllGroupFetcherListener {
        void onDownloadAllGroupsSuccess(ArrayList<Group> groups);

        void onDownloadAllGroupsFailure(VolleyError error);

    }

    public interface SingleGroupFetcherListener {
        void onDownloadSingleGroupSuccess(Group group);

        void onDownloadSingleGroupFailure(VolleyError error);
    }

    public interface GroupStatusFetcher {

      void onGroupStatusSuccess(ArrayList<UserGroupStatus> statuses);

        void onGroupStatusFailure(VolleyError error);

    }
}
