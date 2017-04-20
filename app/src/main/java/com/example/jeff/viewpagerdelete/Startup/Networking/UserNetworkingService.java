package com.example.jeff.viewpagerdelete.Startup.Networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeff.viewpagerdelete.Miscellaneous.RequestService;
import com.example.jeff.viewpagerdelete.Miscellaneous.ServerProperties;
import com.example.jeff.viewpagerdelete.Startup.Model.User;

import org.json.JSONObject;

/**
 * Created by Jeff on 4/11/17.
 */

public class UserNetworkingService {

    private Context mContext;

    public UserNetworkingService(Context mContext) {
        this.mContext = mContext.getApplicationContext();
    }

    public void downloadUser(final UserFetcherListener listener, String userID){

        if(listener == null){
            return;
        }

        String urlString = ServerProperties.usersURL + userID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if(response.isNull("_id")){
                    listener.userDownloadFailure(null);
                }
                else{
                    listener.userDownloadSuccess(new User(response));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.userDownloadFailure(error);
            }
        });


        RequestService.getInstance(mContext).addRequest(request);
    }

    public interface UserFetcherListener{
        void userDownloadSuccess(User user);
        void userDownloadFailure(VolleyError error);
    }
}
