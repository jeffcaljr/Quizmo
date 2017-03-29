package com.example.jeff.viewpagerdelete.Startup.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jamy on 3/22/17.
 */

public class UserDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "user.db";

    public UserDbHelper(Context context) {

        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + UserSchema.Table.NAME +
                "(_id  INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserSchema.Cols.USER_ID + " TEXT, " +
                UserSchema.Cols.F_NAME + " TEXT)" +
                UserSchema.Cols.L_NAME + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserSchema.Table.NAME);

    }

}
