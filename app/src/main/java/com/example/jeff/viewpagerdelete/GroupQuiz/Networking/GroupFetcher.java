package com.example.jeff.viewpagerdelete.GroupQuiz.Networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
    private static WeakReference<GroupFetcherListener> mListener;

    private static GroupFetcher thisInstance;

    public static GroupFetcher sharedInstance(Context context){

        try{
            GroupFetcherListener listener = (GroupFetcherListener) context;
            mListener = new WeakReference<GroupFetcherListener>(listener);
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }

        if(thisInstance == null){
            thisInstance = new GroupFetcher(context.getApplicationContext());
        }
        return thisInstance;
    }

    private GroupFetcher(Context context){
        this.context = context;
    }


    public void downloadAllGroups(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, ServerProperties.groupURL, null, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {

                ArrayList<Group> groups = new ArrayList<>();


                for(int i = 0; i < response.length(); i++){
                    try {
                        groups.add(new Group(response.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mListener.get().groupsDownloaded(groups);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GROUP_FETCHER", "Failed to load groups from server");
                mListener.get().failedgroupsDownload();
            }
        });

        RequestService.getInstance(context).addRequest(request);
    }

    public void downloadGroup(String groupID){
        String urlString = ServerProperties.groupURL + groupID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mListener.get().singleGroupDownloaded(new Group(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mListener.get().failedSingleGroupDownload();
            }
        });

        RequestService.getInstance(context).addRequest(request);
    }

    public void downloadGroupForUser(String userName){
        String urlString = ServerProperties.userGroupURL + userName;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                mListener.get().singleGroupDownloaded(new Group(response));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mListener.get().failedSingleGroupDownload();
            }
        });

        RequestService.getInstance(context).addRequest(request);
    }

    public interface GroupFetcherListener{
        void groupsDownloaded(ArrayList<Group> groups);
        void singleGroupDownloaded(Group group);
        void failedgroupsDownload();
        void failedSingleGroupDownload();
    }
}
