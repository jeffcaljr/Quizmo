package com.example.jeff.viewpagerdelete.Models;

import android.provider.BaseColumns;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Jeff on 2/11/17.
 */

public class QuizAnswer implements Serializable {

    private String id;
    private String value;
    private String text;
    private int sortOrder;

    private int pointsAllocated;




    public QuizAnswer(JSONObject json){
        try {
            String id = json.getString("id");
            String value = json.getString("value");
            String text = json.getString("text");
            int sortOrder = json.getInt("sortOrder");

            this.id = id;
            this.value = value;
            this.text = text;
            this.sortOrder = sortOrder;
            this.pointsAllocated = 0;

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public int getSortOrder() {
        return sortOrder;
    }


    //Setters and no-arg constructor included for seriliazibility

    public QuizAnswer() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    //    public void setId(int id) {
//        this.id = id;
//    }


//    public void setAnswer(String answerString) {
//        this.answer = answerString;
//    }

    public int getPointsAllocated() {
        return pointsAllocated;
    }

//    public void setPointsAllocated(int pointsAllocated) {
//        this.pointsAllocated = pointsAllocated;
//    }

    public int incrementPointsAllocated(){
        ++pointsAllocated;
        return pointsAllocated;
    }

    public int decrementPointsAllocated(){
        --pointsAllocated;
        return pointsAllocated;
    }

    public static final class QuizAnswerDbContract{

        private QuizAnswerDbContract(){}

        public static class QuizAnswerEntry implements BaseColumns {
            public static final String TABLE_NAME = "answers";
            public static final String COLUMN_NAME_ANSWER= "answer";
            public static final String COLUMN_NAME_POINTS_ALLOCATED = "points_allocated";
        }
    }

}
