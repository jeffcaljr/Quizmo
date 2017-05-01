package com.example.jeff.viewpagerdelete.Startup.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jeff.viewpagerdelete.Startup.Model.User;

/**
 * Created by jamy on 3/29/17.
 */

/**
 * SQLite database methods for interacting with User database
 * Used as a mock authentication system
 * Logged-in users have their userID saved locally, and skip login as long as it is saved
 * Upon logout, the userID is removed from local storage, causing the user to have to log in again
 */

public class UserDBMethods {


    /**
     * Read's User information from local storage
     * Simulates retrieving auth token for currently authenticated user
     * Assumes that there will only be one user saved to this database at a time
     *
     * @param db Readable database used to retrieve authenticated user info
     * @return
     */
    public static User PullUserInfo(SQLiteDatabase db){
        User new_user = new User();

        String[] projection = {
                UserSchema.Cols.USER_ID
        };


        Cursor cursor = db.query(
                UserSchema.Table.NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

            if (cursor.moveToFirst()){
                new_user.setUserID(cursor.getString(cursor.getColumnIndex(UserSchema.Cols.USER_ID)));
            }
            else{
                return null;
            }

        return new_user;
    }

    /**
     * Saves User's name to local storage
     * Serves as mock authentication system for remembering logged-in users
     * @param usr User object representing current user
     * @param db SQLite writable database used to store user info
     */
    public static void PushUser(User usr, SQLiteDatabase db) {
        ClearUserDB(db);
        ContentValues values = getContentValues(usr);
        long result = db.insert(UserSchema.Table.NAME, null, values);
    }


//    public static int UpdateUser(User usr, SQLiteDatabase db){
//        ContentValues values = getContentValues(usr);
//
//        String selection = UserSchema.Cols.USER_ID + " LIKE ?";
//        String[] selectionArgs = {usr.getUserID()};
//
//        return db.update(
//                UserSchema.Table.NAME,
//                values,
//                selection,
//                selectionArgs
//        );
//    }


    /**
     * Removes user from database
     * Simulates user logging out, thus requiring sign-in the next time the app is used
     * @param db SQLite writable database used to remove user info
     */
    public static void ClearUserDB(SQLiteDatabase db){
        db.delete(UserSchema.Table.NAME, null, null);
    }


    /**
     * Retrieves values from a User object, to be used for SQLite database access
     * @param usr User object from which to retrieve values
     * @return ContentValues object containing needed user properties for working with SQLite database
     */
    private static ContentValues getContentValues(User usr){
        ContentValues values = new ContentValues();
        values.put(UserSchema.Cols.USER_ID, usr.getUserID());
        return values;
    }
}
