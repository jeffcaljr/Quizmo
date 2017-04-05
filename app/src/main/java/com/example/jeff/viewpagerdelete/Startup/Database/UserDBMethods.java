package com.example.jeff.viewpagerdelete.Startup.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jeff.viewpagerdelete.Startup.Model.User;

/**
 * Created by jamy on 3/29/17.
 */

public class UserDBMethods {

    public static User PullUserInfo(SQLiteDatabase db){
        User new_user = new User();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + UserSchema.Table.NAME, null);
            if (cursor.moveToFirst()){
                new_user.set_id(cursor.getString(cursor.getColumnIndex(UserSchema.Cols.USER_ID)));
                new_user.setFirstName(cursor.getString(cursor.getColumnIndex(UserSchema.Cols.F_NAME)));
                new_user.setLastName(cursor.getString(cursor.getColumnIndex(UserSchema.Cols.L_NAME)));
            }
            else return null;
        }catch (Exception e){
            Log.d("SQL", "PullUserInfo: ");
            return null;
        }

        if (new_user.get_id().equals("")) return null;

        return new_user;
    }

    public static void PushUser(User usr, SQLiteDatabase db) {
        ClearUserDB(db);
        ContentValues values = new ContentValues();
        values.put(UserSchema.Cols.USER_ID, usr.get_id());
        values.put(UserSchema.Cols.F_NAME, usr.getFirstName());
        values.put(UserSchema.Cols.L_NAME, usr.getLastName());
        db.insert(UserSchema.Table.NAME, null, values);
    }

    public static void ClearUserDB(SQLiteDatabase db){
        db.execSQL("DELETE FROM " + UserSchema.Table.NAME + " WHERE _id < 1000");
    }

    public static String getUserID(Context context){
        UserDbHelper mDbHelper = new UserDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + UserSchema.Cols.USER_ID + " FROM " + UserSchema.Table.NAME, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(UserSchema.Cols.USER_ID));
        }
        return null;
    }
}
