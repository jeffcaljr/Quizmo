package com.example.jeff.viewpagerdelete.IndividualQuiz.Database.GradedQuiz;

import android.provider.BaseColumns;

/**
 * Created by Jeff on 4/24/17.
 */

public class GradedQuizSchema {
    public static final String TABLE_NAME = "graded_quizzes";

    public static class GradedQuizEntry implements BaseColumns {
        public static final String COLUMN_NAME_QUIZ_ID = "quiz_id";
        public static final String COLUMN_NAME_USER_ID = "userID";
        public static final String COLUMN_NAME_QUIZ_JSON = "quiz_json";
    }
}
