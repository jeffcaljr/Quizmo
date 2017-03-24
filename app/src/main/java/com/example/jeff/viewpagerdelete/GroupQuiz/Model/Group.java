package com.example.jeff.viewpagerdelete.GroupQuiz.Model;

import com.example.jeff.viewpagerdelete.ServerProperties;
import com.example.jeff.viewpagerdelete.Startup.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jeff on 3/23/17.
 */

public class Group {
    private String id;
    private String name;
    private ArrayList<User> members;

    public Group(JSONObject json){
        try{
            for(String groupField: ServerProperties.getGroupFields()){
                if(! json.has(groupField)){
                    throw new JSONException("User Json does not contain expected field '" + groupField + "'");
                }
            }

            //Json has all expected fields for user, so build a user object

            this.id = json.getString(ServerProperties.GroupFields.ID);
            this.name = json.getString(ServerProperties.GroupFields.NAME);

            JSONArray membersJSONArray = json.getJSONArray(ServerProperties.GroupFields.USERS);

            this.members = new ArrayList<>();

            for(int i = 0; i < membersJSONArray.length(); i++){
                this.members.add(new User(membersJSONArray.getJSONObject(i)));
            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public Group() {
    }

    public Group(String id, String name, ArrayList<User> members) {
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

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }

    public String toJSON(){
        return new Gson().toJson(this);
    }
}
