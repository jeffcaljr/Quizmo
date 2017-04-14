package com.example.jeff.viewpagerdelete.Startup.Database;

import android.content.ContentValues;
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

//        try {
//            Cursor cursor = db.rawQuery("SELECT * FROM " + UserSchema.Table.NAME, null);
            if (cursor.moveToFirst()){
                new_user.setUserID(cursor.getString(cursor.getColumnIndex(UserSchema.Cols.USER_ID)));
//                new_user.setFirstName(cursor.getString(cursor.getColumnIndex(UserSchema.Cols.F_NAME)));
//                new_user.setLastName(cursor.getString(cursor.getColumnIndex(UserSchema.Cols.L_NAME)));
            }
            else{
                return null;
            }
//        }catch (Exception e){
//            Log.d("SQL", "PullUserInfo: ");
//            return null;
//        }

//        if (new_user.get_id().equals("")) return null;

        return new_user;
    }

    public static void PushUser(User usr, SQLiteDatabase db) {
        ClearUserDB(db);
        ContentValues values = getContentValues(usr);
//        values.put(UserSchema.Cols._ID, usr.get_id());
//        values.put(UserSchema.Cols.USER_ID, usr.getUserID());
//        values.put(UserSchema.Cols.F_NAME, usr.getFirstName());
//        values.put(UserSchema.Cols.L_NAME, usr.getLastName());
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

    public static void ClearUserDB(SQLiteDatabase db){
        db.delete(UserSchema.Table.NAME, null, null);
    }

    private static ContentValues getContentValues(User usr){
        ContentValues values = new ContentValues();
        values.put(UserSchema.Cols.USER_ID, usr.getUserID());
        return values;
    }
}
