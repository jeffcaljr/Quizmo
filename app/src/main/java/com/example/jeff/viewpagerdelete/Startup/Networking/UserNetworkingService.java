package com.example.jeff.viewpagerdelete.Startup.Networking;

import android.content.Context;
import android.net.Uri;

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


/**
 * Provides methods to access /users API
 */

public class UserNetworkingService {


    private Context mContext;


    /**
     * Listener for results of downloadUser API call
     */
    public interface OnUserDownloadedCallback {

        /**
         * Callback method for when a user's data was successfully loaded
         *
         * @param user: Object containing user data that was downloaded
         */
        void userDownloadSuccess(User user);

        /**
         * Callback method for when a user's data was unable to be loaded due to error
         *
         * @param error: Volley error for the failed request
         */
        void userDownloadFailure(VolleyError error);
    }


    /**
     * Constructor: initializes context used for network requests.
     *
     * @param mContext: used to retrieve application context for sending network requests
     */
    public UserNetworkingService(Context mContext) {
        this.mContext = mContext.getApplicationContext();
    }


    /**
     * Loads user data from the API
     *
     * @param userID:   userID of the user whose data is to be downloaded
     * @param callback: listener for results or error of API call
     */
    public void downloadUser(String userID, final OnUserDownloadedCallback callback) {


        //Require a listener for results of the API call; otherwise don't make the call
        if (callback == null) {
            return;
        }

        //Build the uri for interacting with the API

        final Uri endpoint = Uri
                .parse(ApiURLs.usersURL)
                .buildUpon()
                .appendEncodedPath(userID)
                .build();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, endpoint.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //check if the response is successful - but empty - by checking if there is an "_id" property in the response
                //Assumes that if there is a user in the response, the "_id" property will not be null
                if (response.isNull("_id")) {
                    callback.userDownloadFailure(new VolleyError("Empty response"));
                } else {
                    //Successfully downloaded user; notify listener
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
}
