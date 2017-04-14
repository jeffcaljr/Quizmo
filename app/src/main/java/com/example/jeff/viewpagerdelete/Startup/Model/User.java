package com.example.jeff.viewpagerdelete.Startup.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.ServerProperties;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDbHelper;
import com.example.jeff.viewpagerdelete.Startup.Database.UserSchema;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.deleteDatabase;

/**
 * Created by Jeff on 3/23/17.
 */

public class User implements Serializable{

    private String _id;
    private String userID;
    private String email;
    private String firstName;
    private String lastName;
    private ArrayList<Course> enrolledCourses;

    public User(JSONObject json){
        try{

            //Json has all expected fields for user, so build a user object

            this._id = json.getString("_id");
            this.userID = json.getString("userID");
            this.email = json.getString("email");
            this.firstName = json.getString("first");
            this.lastName = json.getString("last");

            this.enrolledCourses = new ArrayList<>();

            JSONArray coursesArray = json.getJSONArray("enrolledCourses");

            for(int i = 0; i < coursesArray.length(); i++){
                this.enrolledCourses.add(new Course(coursesArray.getJSONObject(i)));
            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }


    public User(){ }

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

    public ArrayList<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(ArrayList<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }
}
