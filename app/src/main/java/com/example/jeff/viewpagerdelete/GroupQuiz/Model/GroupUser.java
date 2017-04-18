package com.example.jeff.viewpagerdelete.GroupQuiz.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Jeff on 4/18/17.
 */

public class GroupUser implements Serializable {
    private String userID;
    private String email;
    private String firstName;
    private String lastName;

    public GroupUser(JSONObject json) {
        try {

            this.userID = json.getString("userID");
            this.firstName = json.getString("first");
            this.lastName = json.getString("last");

            if (json.has("email")) {
                this.email = json.getString("email");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public GroupUser() {
    }

    public GroupUser(String userID, String email, String firstName, String lastName) {
        this.userID = userID;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
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
}