package com.example.jeff.viewpagerdelete.Startup;

import com.example.jeff.viewpagerdelete.ServerProperties;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Jeff on 3/23/17.
 */

public class User implements Serializable{

    private String _id;
    private String userID;
    private String email;
    private String firstName;
    private String lastName;

    public User(JSONObject json){
        try{
            for(String userField: ServerProperties.getUserFields()){
                if(! json.has(userField)){
                    throw new JSONException("User Json does not contain expected field '" + userField + "'");
                }
            }

            //Json has all expected fields for user, so build a user object

            this._id = json.getString(ServerProperties.UserFields.ID);
            this.userID = json.getString(ServerProperties.UserFields.USER_NAME);
            this.email = json.getString(ServerProperties.UserFields.EMAIL);
            this.firstName = json.getString(ServerProperties.UserFields.FIRST_NAME);
            this.lastName = json.getString(ServerProperties.UserFields.LAST_NAME);

        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public User() {
    }

    public User(String _id, String userID, String email, String firstName, String lastName) {
        this._id = _id;
        this.userID = userID;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String toJSON(){
        return new Gson().toJson(this);
    }
}
