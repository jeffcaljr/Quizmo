package com.example.jeff.viewpagerdelete.IndividualQuiz.Database.GradedQuiz;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.GradedQuiz;

/**
 * Created by Jeff on 4/24/17.
 */

public class GradedQuizCursorWrapper extends CursorWrapper {

    public GradedQuizCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public GradedQuiz getGradedQuiz() {
        String json = getString(getColumnIndex(GradedQuizSchema.GradedQuizEntry.COLUMN_NAME_QUIZ_JSON));

        return GradedQuiz.buildQuizFromJsonString(json);
    }
}
