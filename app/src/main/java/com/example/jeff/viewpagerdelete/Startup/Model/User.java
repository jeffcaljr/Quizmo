package com.example.jeff.viewpagerdelete.Startup.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jeff.viewpagerdelete.ServerProperties;
import com.example.jeff.viewpagerdelete.Startup.Database.UserDbHelper;
import com.example.jeff.viewpagerdelete.Startup.Database.UserSchema;
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
    private UserDbHelper mDbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    public User (Context context){
        mDbHelper = new UserDbHelper(context);
    }

    public User(String _id, String userID, String email, String firstName, String lastName, Context context) {
        this._id = _id;
        this.userID = userID;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        mDbHelper = new UserDbHelper(context);
    }
    public void PushUserToSQL() {
        db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserSchema.Cols.USER_ID, get_id());
        values.put(UserSchema.Cols.F_NAME, getFirstName());
        values.put(UserSchema.Cols.L_NAME, getLastName());
        db.insert(UserSchema.Table.NAME, null, values);
    }

    public boolean PullUserInfoFromSQL(){
        db = mDbHelper.getReadableDatabase();

        if (db == null)return false;
        else{
            cursor = db.rawQuery("SELECT * FROM " + UserSchema.Table.NAME, null);
            if (cursor.moveToFirst()){
                set_id(cursor.getString(cursor.getColumnIndex(UserSchema.Cols.USER_ID)));
                setFirstName(cursor.getString(cursor.getColumnIndex(UserSchema.Cols.F_NAME)));
                setLastName(cursor.getString(cursor.getColumnIndex(UserSchema.Cols.L_NAME)));

            }
            else return false;
        }
        if (get_id().equals("")) return false;

        return true;
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
