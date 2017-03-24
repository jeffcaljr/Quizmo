package com.example.jeff.viewpagerdelete.Homepage.Model;

import java.util.ArrayList;

/*
Model for the Homepage and QuizCode page
By: Ryan Davis
 */

public class HomePage_and_QuizCode_Model {

    //holds the group data for each list item in the Group List
    public static ArrayList<String> mGroupList;
    //holds the quiz data for each list item in the Quiz List
    public static ArrayList<String> mQuizList;
    //user's quiz code entry
    public static String mUserCodeEntry;
    //should this be Integer type eventually if the code is numeric? Think about how you could support numeric and alpha
    private String mQuizCode;
    private String mNextQuiz;
    private String mNextGroup;
    private String result;

    public HomePage_and_QuizCode_Model() {
        mGroupList = null;
        mQuizList = null;
        mQuizCode = "";
    }

    //need to have JSONArray set this
    public void setQuizCode(String quizCode) {
        this.mQuizCode = quizCode;
    }

    //need to have JSONArray set this
    public void setNextQuiz(String nextQuiz) {
        this.mNextQuiz = nextQuiz;
    }

    //need to have JSONArray set this
    public void setNextGroup(String nextGroup) { this.mNextGroup = nextGroup; }

    public String getReult () { return result; }

    public String getQuizCode() {
        return mQuizCode;
    }

    public String getNextQuiz() {
        return mNextQuiz;
    }

    public String getNextGroup() {
        return mNextGroup;
    }

    public void validate_Code_Entry() {
        if (mQuizCode.equals(mUserCodeEntry)) {
            //mQuizList.add(getNextQuiz());
            //mGroupList.add(getNextGroup());
            result = "CORRECT CODE ENTERED";
        } else {
            result = "INCORRECT CODE";

        }
    }

}
