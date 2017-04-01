package com.example.jeff.viewpagerdelete.Startup.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jeff.viewpagerdelete.Startup.Model.User;

/**
 * Created by jamy on 3/22/17.
 */

public class UserDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    public UserDbHelper(Context context) {

        super(context, UserSchema.DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + UserSchema.Table.NAME +
                " (_id  INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserSchema.Cols.USER_ID + " TEXT, " +
                UserSchema.Cols.F_NAME + " TEXT, " +
                UserSchema.Cols.L_NAME + " TEXT)";
        try{
            db.execSQL(createTable);
        }catch(Exception e){
            Log.d("SQL", createTable);
            throw (e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserSchema.Table.NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserSchema.Table.NAME);
        onCreate(db);
    }


}
