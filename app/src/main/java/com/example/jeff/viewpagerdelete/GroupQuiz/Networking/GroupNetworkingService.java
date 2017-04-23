package com.example.jeff.viewpagerdelete.GroupQuiz.Networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.GroupQuizAnswer;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.UserGroupStatus;
import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.example.jeff.viewpagerdelete.Miscellaneous.RequestService;
import com.example.jeff.viewpagerdelete.Miscellaneous.ApiURLs;

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


  public void downloadAllGroups(String courseID, final AllGroupFetcherListener callback) {

    if (callback == null) {
            return;
        }

      String urlString = ApiURLs.baseURL + "/groups?course_id=" + courseID;

    JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlString, null,
          new Response.Listener<JSONArray>() {
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
              callback.onDownloadAllGroupsSuccess(groups);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GROUP_FETCHER", "Failed to load groups from server");
              callback.onDownloadAllGroupsFailure(error);
            }
        });

        RequestService.getInstance(context).addRequest(request);
    }

  public void downloadGroup(String groupID, final SingleGroupDownloadCallback callback) {

    if (callback == null) {
            return;
        }

      String urlString = ApiURLs.groupURL + groupID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              callback.onDownloadSingleGroupSuccess(new Group(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              callback.onDownloadSingleGroupFailure(error);
            }
        });

        RequestService.getInstance(context).addRequest(request);
    }

  public void downloadGroupForUser(String userID, String courseID,
      final SingleGroupDownloadCallback callback) {

    if (callback == null) {
            return;
        }

      String urlString = ApiURLs.userGroupURL + "?user_id=" + userID + "&course_id=" + courseID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

              callback.onDownloadSingleGroupSuccess(new Group(response));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              callback.onDownloadSingleGroupFailure(error);
            }
        });

        RequestService.getInstance(context).addRequest(request);
    }

  public void getGroupStatus(Group group, Course course, GradedQuiz quiz,
      final GroupStatusDownloadCallback callback) {

    if (callback == null) {
            return;
        }

        String groupID = group.getId();
        String courseID = course.getCourseID();
        String quizID = quiz.getQuizID();
        String sessionID = quiz.getSessionID();

      String urlString =
          ApiURLs.groupStatusURL + "?group_id=" + groupID + "&course_id=" + courseID + "&quiz_id="
              + quizID + "&session_id=" + sessionID;

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

                  callback.onGroupStatusSuccess(statuses);

                } catch (JSONException e) {
                    e.printStackTrace();
                  callback.onGroupStatusFailure(new VolleyError("Error parsing JSON response"));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              callback.onGroupStatusFailure(error);
            }
        });

        RequestService.getInstance(context).addRequest(request);
    }

  public void getGroupQuizProgress(String quizID, String groupID, String sessionID,
      final GroupQuizProgressDownloadCallback callback) {
    if (callback == null) {
      return;
    }

    String urlString = ApiURLs.groupQuizProgressURL + "?quiz_id=" + quizID + "&group_id=" + groupID
        + "&session_id=" + sessionID;

    JsonObjectRequest request = new JsonObjectRequest(Method.GET, urlString, null,
        new Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {
            callback.onGroupQuizProgressSuccess(response);
          }
        },
        new ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            callback.onGroupQuizProgressFailure(error);
          }
        });

    RequestService.getInstance(context).addRequest(request);

  }

  public void submitGroupQuizAnswer(String quizID,
      String groupID, String sessionID, GroupQuizAnswer groupQuizAnswer,
      final GroupQuizAnswerPostCallback callback) {
    if (callback == null) {
      return;
    }

    String urlString =
        ApiURLs.groupQuizURL + "?quiz_id=" + quizID + "&group_id=" + groupID + "&session_id="
            + sessionID;

    JsonObjectRequest request = new JsonObjectRequest(Method.POST, urlString,
        groupQuizAnswer.toPostJSONFormat(), new Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        callback.onGroupQuizAnswerPostSuccess(response);

      }
    }, new ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        callback.onGroupQuizAnswerPostFailure(error);
      }
    });

    RequestService.getInstance(context).addRequest(request);
  }

    public interface AllGroupFetcherListener {
        void onDownloadAllGroupsSuccess(ArrayList<Group> groups);

        void onDownloadAllGroupsFailure(VolleyError error);

    }

  public interface SingleGroupDownloadCallback {
        void onDownloadSingleGroupSuccess(Group group);

        void onDownloadSingleGroupFailure(VolleyError error);
    }

  public interface GroupStatusDownloadCallback {

      void onGroupStatusSuccess(ArrayList<UserGroupStatus> statuses);

        void onGroupStatusFailure(VolleyError error);
    }

  public interface GroupQuizProgressDownloadCallback {

    void onGroupQuizProgressSuccess(JSONObject response);

    void onGroupQuizProgressFailure(VolleyError error);
  }

  public interface GroupQuizAnswerPostCallback {

    void onGroupQuizAnswerPostSuccess(JSONObject response);

    void onGroupQuizAnswerPostFailure(VolleyError error);
  }
}
