package com.example.jeff.viewpagerdelete.GroupQuiz.Model;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jeff on 3/23/17.
 */

public class Group implements Serializable{
    private String id;
    private String name;
    private ArrayList<String> courseIDs;
    private ArrayList<GroupUser> members;

    public Group(JSONObject json) {
        try {


            //Json has all expected fields for user, so build a user object

            this.id = json.getString("_id");
            this.name = json.getString("name");

            this.courseIDs = new ArrayList<>();

            JSONArray courseIDs = json.getJSONArray("courseIds");

            for (int i = 0; i < courseIDs.length(); i++) {
                this.courseIDs.add(courseIDs.getString(i));
            }

            JSONArray membersJSONArray = json.getJSONArray("users");

            this.members = new ArrayList<>();

            for (int i = 0; i < membersJSONArray.length(); i++) {
                this.members.add(new GroupUser(membersJSONArray.getJSONObject(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Group() {
    }

    public Group(String id, String name, ArrayList<GroupUser> members) {
        this.id = id;
        this.name = name;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


  public ArrayList<GroupUser> getMembers() {
        Collections.sort(members);
        return members;
    }

    public void setMembers(ArrayList<GroupUser> members) {
        this.members = members;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
