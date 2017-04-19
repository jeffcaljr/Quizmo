package com.example.jeff.viewpagerdelete.GroupQuiz.Model;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Jeff on 4/18/17.
 */

public class GroupStatus implements Serializable, Comparable<GroupStatus> {

    public enum Status {
        COMPLETE("complete"),
        IN_PROGRESS("inProgress");

        private final String statusString;

        Status(String statusString) {
            this.statusString = statusString;
        }

        String getStatusString() {
            return this.statusString;
        }

        public static Status fromString(String text) {
            for (Status s : Status.values()) {
                if (s.statusString.equalsIgnoreCase(text)) {
                    return s;
                }
            }
            return null;
        }

    }

    private Status status;
    private String userID;
    private String firstName;
    private String lastName;
    private String groupName;
    private Date timeStarted;
    private int timeLimit;

    public GroupStatus(JSONObject json) {


        try {
            this.status = Status.fromString(json.getString("status"));
            this.userID = json.getString("userId");
            this.firstName = json.getString("firstName");
            this.lastName = json.getString("lastName");
            this.groupName = json.getString("groupName");
            this.timeLimit = json.getInt("timeLimit");

            String timeStartedString = json.getString("timeStarted");

            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss");

            format.setTimeZone(TimeZone.getTimeZone("UTC"));

            this.timeStarted = format.parse(timeStartedString);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public GroupStatus() {
    }

    public GroupStatus(Status status, String userID, String firstName, String lastName, String groupName, Date timeStarted, int timeLimit) {
        this.status = status;
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupName = groupName;
        this.timeStarted = timeStarted;
        this.timeLimit = timeLimit;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(Date timeStarted) {
        this.timeStarted = timeStarted;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    @Override
    public int compareTo(@NonNull GroupStatus status) {
        return (this.userID.compareTo(status.getUserID()));
    }
}


