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

public class GroupMemberStatus implements Serializable, Comparable<GroupMemberStatus> {

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
  private boolean isLeader;

    public GroupMemberStatus(JSONObject statusJSON, JSONObject leaderJSON) {


        try {
          this.status = Status.fromString(statusJSON.getString("status"));
          this.userID = statusJSON.getString("userId");
          this.firstName = statusJSON.getString("firstName");
          this.lastName = statusJSON.getString("lastName");
          this.groupName = statusJSON.getString("groupName");
          this.timeLimit = statusJSON.getInt("timeLimit");
          this.isLeader = leaderJSON.getString("userId").equals(this.userID);

          String timeStartedString = statusJSON.getString("timeStarted");

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

    public GroupMemberStatus() {
    }

    public GroupMemberStatus(Status status, String userID, String firstName, String lastName,
                             String groupName, Date timeStarted, int timeLimit) {
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

  public boolean isLeader() {
    return isLeader;
  }

  public void setLeader(boolean leader) {
    isLeader = leader;
  }

  @Override
  public int compareTo(@NonNull GroupMemberStatus status) {
        return (this.userID.compareTo(status.getUserID()));
    }
}


