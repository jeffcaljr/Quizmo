package com.example.jeff.viewpagerdelete.IndividualQuiz.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.google.gson.JsonSyntaxException;

/**
 * Created by Jeff on 3/23/17.
 */

public class QuizPersistence {
    private Context context;
    private SQLiteDatabase db;

    private static QuizPersistence quizPersistence;
    public static QuizPersistence sharedInstance(Context context){
        if(quizPersistence == null){
            quizPersistence = new QuizPersistence(context);
        }
        return quizPersistence;
    }

    private QuizPersistence(Context context){
        this.context = context.getApplicationContext();
        this.db = new IndividualQuizDbHelper((context)).getWritableDatabase();
    }


    public Quiz readIndividualQuizFromDatabase(String quizID){
        Quiz quiz = null;

        String[] projection = {
                QuizSchema.QuizEntry.COLUMN_NAME_QUIZ_ID,
                QuizSchema.QuizEntry.COLUMN_NAME_QUIZ_JSON
        };

        String selection = QuizSchema.QuizEntry.COLUMN_NAME_QUIZ_ID + " = ?";
        String[] selectionArgs = {quizID};

        Cursor cursor = db.query(
                QuizSchema.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if(cursor.moveToNext()){
            try{
                String quizData = cursor.getString(cursor.getColumnIndexOrThrow(QuizSchema.QuizEntry.COLUMN_NAME_QUIZ_JSON));

                quiz = Quiz.buildQuizFromJsonString(quizData);
            } catch (IllegalArgumentException e){
                e.printStackTrace();
                return null;
            } catch(JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }
        }

        return quiz;

    }


    public boolean writeIndividualQuizToDatabase(Quiz quiz){

        boolean writeSuccessful = false;

        ContentValues values = getContentValues(quiz);

        long newRowID = db.insert(QuizSchema.TABLE_NAME, null, values);

        if(newRowID != -1){
            writeSuccessful = true;
        }

        return writeSuccessful;

    }


    public void updateQuizInDatabase(Quiz quiz){
        boolean writeSuccessful = false;

        ContentValues values = getContentValues(quiz);

        String selection = QuizSchema.QuizEntry.COLUMN_NAME_QUIZ_ID + " LIKE ?";
        String[] selectionArgs = {quiz.getId()};

        db.update(
                QuizSchema.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );


    }

    private ContentValues getContentValues(Quiz quiz){
        ContentValues values = new ContentValues();
        values.put(QuizSchema.QuizEntry.COLUMN_NAME_QUIZ_ID, quiz.getId());
        values.put(QuizSchema.QuizEntry.COLUMN_NAME_QUIZ_JSON, quiz.convertToJsonString());

        return values;
    }
}
