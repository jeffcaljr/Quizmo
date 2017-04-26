package com.example.jeff.viewpagerdelete.IndividualQuiz.Database.GradedQuiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Jeff on 4/24/17.
 */

public class GradedQuizDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FrontRowLearningAppGradedQuizzes.db";

    private static final String SQL_QUIZZES =
            "CREATE TABLE " + GradedQuizSchema.TABLE_NAME + " (" +
                    GradedQuizSchema.GradedQuizEntry._ID + " INTEGER PRIMARY KEY," +
                    GradedQuizSchema.GradedQuizEntry.COLUMN_NAME_QUIZ_ID + " TEXT," +
                    GradedQuizSchema.GradedQuizEntry.COLUMN_NAME_USER_ID + " TEXT," +
                    GradedQuizSchema.GradedQuizEntry.COLUMN_NAME_QUIZ_JSON + " TEXT)";

    private static final String SQL_DELETE_QUIZZES =
            "DROP TABLE IF EXISTS " + GradedQuizSchema.TABLE_NAME;


    public GradedQuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_QUIZZES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_QUIZZES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
