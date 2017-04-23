package com.example.jeff.viewpagerdelete.Startup.Networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeff.viewpagerdelete.Miscellaneous.RequestService;
import com.example.jeff.viewpagerdelete.Miscellaneous.ApiURLs;
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

    public void downloadUser(String userID, final UserFetcherCallback callback) {

        if (callback == null) {
            return;
        }

      String urlString = ApiURLs.usersURL + userID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if(response.isNull("_id")){
                    callback.userDownloadFailure(null);
                }
                else{
                    callback.userDownloadSuccess(new User(response));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.userDownloadFailure(error);
            }
        });


        RequestService.getInstance(mContext).addRequest(request);
    }

    public interface UserFetcherCallback {
        void userDownloadSuccess(User user);
        void userDownloadFailure(VolleyError error);
    }
}
