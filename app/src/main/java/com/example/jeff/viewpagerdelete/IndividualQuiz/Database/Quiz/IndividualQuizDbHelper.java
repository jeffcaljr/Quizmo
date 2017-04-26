package com.example.jeff.viewpagerdelete.IndividualQuiz.Database.Quiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Jeff on 3/8/17.
 */

public class IndividualQuizDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FrontRowLearningApp.db";

    private static final String SQL_QUIZZES =
            "CREATE TABLE " + IndividualQuizSchema.TABLE_NAME + " (" +
                    IndividualQuizSchema.QuizEntry._ID + " INTEGER PRIMARY KEY," +
                    IndividualQuizSchema.QuizEntry.COLUMN_NAME_QUIZ_ID + " TEXT," +
                IndividualQuizSchema.QuizEntry.COLUMN_NAME_USER_ID + " TEXT," +
                    IndividualQuizSchema.QuizEntry.COLUMN_NAME_QUIZ_JSON + " TEXT)";

    private static final String SQL_DELETE_QUIZZES =
            "DROP TABLE IF EXISTS " + IndividualQuizSchema.TABLE_NAME;


    public IndividualQuizDbHelper(Context context) {
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
