package com.example.jeff.viewpagerdelete.IndividualQuiz.Database.Quiz;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;

/**
 * Created by Jeff on 3/26/17.
 */

public class IndividualQuizCursorWrapper extends CursorWrapper {

    public IndividualQuizCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Quiz getQuiz(){
        String json = getString(getColumnIndex(IndividualQuizSchema.QuizEntry.COLUMN_NAME_QUIZ_JSON));

        return Quiz.buildQuizFromJsonString(json);
    }

}
