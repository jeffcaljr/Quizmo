package com.example.jeff.viewpagerdelete.IndividualQuiz.Database;

import android.provider.BaseColumns;

/**
 * Created by Jeff on 3/23/17.
 */

public class IndividualQuizSchema {
    public static final String TABLE_NAME = "quizzes";
    public static class QuizEntry implements BaseColumns {
        public static final String COLUMN_NAME_QUIZ_ID = "quiz_id";
        public static final String COLUMN_NAME_QUIZ_JSON = "quiz_json";
    }
}
