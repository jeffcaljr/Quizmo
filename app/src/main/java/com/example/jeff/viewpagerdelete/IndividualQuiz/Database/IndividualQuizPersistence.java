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

public class IndividualQuizPersistence {
    private Context context;
    private SQLiteDatabase db;

    private static IndividualQuizPersistence quizPersistence;
    public static IndividualQuizPersistence sharedInstance(Context context){
        if(quizPersistence == null){
            quizPersistence = new IndividualQuizPersistence(context);
        }
        return quizPersistence;
    }

    private IndividualQuizPersistence(Context context){
        this.context = context.getApplicationContext();
        this.db = new IndividualQuizDbHelper((context)).getWritableDatabase();
    }


    public Quiz readIndividualQuizFromDatabase(String quizID){
        Quiz quiz;

        String[] projection = {
                IndividualQuizSchema.QuizEntry.COLUMN_NAME_QUIZ_ID,
                IndividualQuizSchema.QuizEntry.COLUMN_NAME_QUIZ_JSON
        };

        String selection = IndividualQuizSchema.QuizEntry.COLUMN_NAME_QUIZ_ID + " = ?";
        String[] selectionArgs = {quizID};

        Cursor cursor = db.query(
                IndividualQuizSchema.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        IndividualQuizCursorWrapper cursorWrapper = new IndividualQuizCursorWrapper(cursor);

        if(cursorWrapper.moveToNext()){
            try{
                quiz = cursorWrapper.getQuiz();
                return quiz;
            } catch (IllegalArgumentException e){
                e.printStackTrace();
                return null;
            } catch(JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }

        }
        else{
            return null;
        }
    }


    public boolean writeIndividualQuizToDatabase(Quiz quiz){

        boolean writeSuccessful = false;

        ContentValues values = getContentValues(quiz);

        long newRowID = db.insert(IndividualQuizSchema.TABLE_NAME, null, values);

        if(newRowID != -1){
            writeSuccessful = true;
        }

        return writeSuccessful;

    }


    public void updateQuizInDatabase(Quiz quiz){

        ContentValues values = getContentValues(quiz);

        String selection = IndividualQuizSchema.QuizEntry.COLUMN_NAME_QUIZ_ID + " LIKE ?";
        String[] selectionArgs = {quiz.getId()};

        db.update(
                IndividualQuizSchema.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );


    }

    private ContentValues getContentValues(Quiz quiz){
        ContentValues values = new ContentValues();
        values.put(IndividualQuizSchema.QuizEntry.COLUMN_NAME_QUIZ_ID, quiz.getId());
        values.put(IndividualQuizSchema.QuizEntry.COLUMN_NAME_QUIZ_JSON, quiz.toJSON());

        return values;
    }
}
