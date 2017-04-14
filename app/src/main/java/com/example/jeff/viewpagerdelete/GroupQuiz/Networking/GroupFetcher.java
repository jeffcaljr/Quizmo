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

//    private Context context;
//
//    private static GroupFetcher thisInstance;
//
//    public static GroupFetcher sharedInstance(Context context){
//
//        if(thisInstance == null){
//            thisInstance = new GroupFetcher(context.getApplicationContext());
//        }
//        return thisInstance;
//    }
//
//    private GroupFetcher(Context context){
//        this.context = context;
//    }
//
//
//    public void downloadAllGroups(final GroupFetcherListener listener){
//
//        if(listener == null){
//            return;
//        }
//
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, ServerProperties.groupURL, null, new Response.Listener<JSONArray>(){
//            @Override
//            public void onResponse(JSONArray response) {
//
//                ArrayList<Group> groups = new ArrayList<>();
//
//
//                for(int i = 0; i < response.length(); i++){
//                    try {
//                        groups.add(new Group(response.getJSONObject(i)));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                listener.onDownloadAllGroupsSuccess(groups);
//
//            }
//        }, new Response.ErrorListener(){
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("GROUP_FETCHER", "Failed to load groups from server");
//                listener.onDownloadAllGroupsFailure(error);
//            }
//        });
//
//        RequestService.getInstance(context).addRequest(request);
//    }
//
//    public void downloadGroup(final GroupFetcherListener listener, String groupID){
//
//        if(listener == null){
//            return;
//        }
//
//        String urlString = ServerProperties.groupURL + groupID;
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                listener.onDownloadSingleGroupSuccess(new Group(response));
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                listener.onDownloadSingleGroupFailure(error);
//            }
//        });
//
//        RequestService.getInstance(context).addRequest(request);
//    }
//
//    public void downloadGroupForUser(final GroupFetcherListener listener, String userName){
//
//        if(listener == null){
//            return;
//        }
//
//        String urlString = ServerProperties.userGroupURL + userName;
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                listener.onDownloadSingleGroupSuccess(new Group(response));
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                listener.onDownloadSingleGroupFailure(error);
//            }
//        });
//
//        RequestService.getInstance(context).addRequest(request);
//    }
//
//    public interface GroupFetcherListener{
//        void onDownloadAllGroupsSuccess(ArrayList<Group> groups);
//        void onDownloadSingleGroupSuccess(Group group);
//        void onDownloadAllGroupsFailure(VolleyError error);
//        void onDownloadSingleGroupFailure(VolleyError error);
//    }
}
