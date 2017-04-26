package com.example.jeff.viewpagerdelete.IndividualQuiz.Database.GradedQuiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;
import com.google.gson.JsonSyntaxException;

/**
 * Created by Jeff on 4/24/17.
 */

public class GradedQuizPersistence {
    private Context context;
    private SQLiteDatabase db;

    private static GradedQuizPersistence quizPersistence;

    public static GradedQuizPersistence sharedInstance(Context context) {
        if (quizPersistence == null) {
            quizPersistence = new GradedQuizPersistence(context);
        }
        return quizPersistence;
    }

    private GradedQuizPersistence(Context context) {
        this.context = context.getApplicationContext();
        this.db = new GradedQuizDbHelper((context)).getWritableDatabase();
    }

    public GradedQuiz readGradedQuizFromDatabase(String quizID, String userID) {
        GradedQuiz quiz;

        String[] projection = {
                GradedQuizSchema.GradedQuizEntry.COLUMN_NAME_QUIZ_ID,
                GradedQuizSchema.GradedQuizEntry.COLUMN_NAME_QUIZ_JSON
        };

        String selection = GradedQuizSchema.GradedQuizEntry.COLUMN_NAME_QUIZ_ID + " = ? AND "
                + GradedQuizSchema.GradedQuizEntry.COLUMN_NAME_USER_ID + " = ?";
        String[] selectionArgs = {quizID, userID};

        Cursor cursor = db.query(
                GradedQuizSchema.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        GradedQuizCursorWrapper cursorWrapper = new GradedQuizCursorWrapper(cursor);

        if (cursorWrapper.moveToNext()) {
            try {
                quiz = cursorWrapper.getGradedQuiz();
                return quiz;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            return null;
        }
    }

    public boolean writeGradedQuizToDatabase(GradedQuiz quiz) {

        boolean writeSuccessful = false;

        ContentValues values = getContentValues(quiz);

        long newRowID = db.insert(GradedQuizSchema.TABLE_NAME, null, values);

        if (newRowID != -1) {
            writeSuccessful = true;
        }

        return writeSuccessful;

    }

    private ContentValues getContentValues(GradedQuiz quiz) {
        ContentValues values = new ContentValues();
        values.put(GradedQuizSchema.GradedQuizEntry.COLUMN_NAME_QUIZ_ID, quiz.getId());
        values.put(GradedQuizSchema.GradedQuizEntry.COLUMN_NAME_QUIZ_JSON, quiz.toJSON());
        values.put(GradedQuizSchema.GradedQuizEntry.COLUMN_NAME_USER_ID, quiz.getUserID());

        return values;
    }
}
