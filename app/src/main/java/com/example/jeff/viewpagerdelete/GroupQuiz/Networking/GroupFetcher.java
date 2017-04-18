package com.example.jeff.viewpagerdelete.GroupQuiz.Networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeff.viewpagerdelete.GroupQuiz.Model.Group;
import com.example.jeff.viewpagerdelete.RequestService;
import com.example.jeff.viewpagerdelete.ServerProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Jeff on 3/24/17.
 */

public class GroupFetcher {

    private Context context;


    public GroupFetcher(Context context) {
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

    public interface AllGroupFetcherListener {
        void onDownloadAllGroupsSuccess(ArrayList<Group> groups);

        void onDownloadAllGroupsFailure(VolleyError error);

    }

    public interface SingleGroupFetcherListener {
        void onDownloadSingleGroupSuccess(Group group);

        void onDownloadSingleGroupFailure(VolleyError error);
    }
}
