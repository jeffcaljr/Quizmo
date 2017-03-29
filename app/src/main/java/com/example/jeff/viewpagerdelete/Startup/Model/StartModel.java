package com.example.jeff.viewpagerdelete.Startup.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jeff.viewpagerdelete.Startup.Database.UserDbHelper;
import com.example.jeff.viewpagerdelete.Startup.Database.UserSchema;

/**
 * Created by jamy on 3/23/17.
 */

public class StartModel {

    public static class User{
        private static String uid;
        private static String screen_name;

        public static String getScreenName() {
            return screen_name;
        }
        public static void setScreenName(String scrn_name) {
            screen_name = scrn_name;
        }
        public static String getUid() {
            return uid;
        }
        public static void setUid(String id) {
            uid = id;
        }
    }

    private UserDbHelper mDbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    public StartModel(Context context){
        mDbHelper = new UserDbHelper(context);
    }



    public void PushUserToSQL() {
        db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(UserSchema.Cols.USER_ID, User.getUid());
        values.put(UserSchema.Cols.SCREEN_NAME, User.getScreenName());
        db.insert(UserSchema.Table.NAME, null, values);
    }

    public boolean PullUserInfo(){
        db = mDbHelper.getReadableDatabase();

        if (db == null)return false;
        else{
            cursor = db.rawQuery("SELECT * FROM " + UserSchema.Table.NAME, null);
            if (cursor.moveToFirst()){
                User.setUid(cursor.getString(cursor.getColumnIndex(UserSchema.Cols.USER_ID)));
                User.setScreenName(cursor.getString(cursor.getColumnIndex(UserSchema.Cols.SCREEN_NAME)));
            }
            else return false;
        }
        if (User.getUid().equals("")) return false;

        return true;
    }
}
