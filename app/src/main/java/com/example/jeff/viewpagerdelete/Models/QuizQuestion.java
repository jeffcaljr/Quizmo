package com.example.jeff.viewpagerdelete.Models;

import android.provider.BaseColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Jeff on 2/11/17.
 */

public class QuizQuestion implements Serializable{

    private String id;
    private String title;
    private String text;
    private int pointsPossible;
    private ArrayList<QuizAnswer> availableAnswers;

    private int pointsRemaining;


    public QuizQuestion(JSONObject json){

        try {
            String id = json.getString("id");
            String title = json.getString("title");
            String text = json.getString("text");
            int pointsPossible = json.getInt("pointsPossible");
            JSONArray answersJSONArray = json.getJSONArray("availableAnswers");
            ArrayList<QuizAnswer> availableAnswers = new ArrayList<>();
            for (int i = 0; i < answersJSONArray.length(); i++) {
                availableAnswers.add(new QuizAnswer(((JSONObject) answersJSONArray.get(i))));
            }

            //randomly shuffle answers so they're not in the same order in all quizzes
            Collections.shuffle(availableAnswers);

            this.id = id;
            this.title = title;
            this.text = text;
            this.pointsPossible = pointsPossible;
            this.availableAnswers = availableAnswers;
            this.pointsRemaining = pointsPossible;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public int getPointsPossible() {
        return pointsPossible;
    }

    public ArrayList<QuizAnswer> getAvailableAnswers() {
        return availableAnswers;
    }


    public QuizAnswer getAnswerById(String id){
        for(QuizAnswer a: availableAnswers){
            if(a.getId().equals(id)){
                return a;
            }
        }
        return null;
    }

    public QuizAnswer getAnswerByIndex(int index){
        try{
            return availableAnswers.get(index);
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
            return null;
        }
    }


    public int incrementPointsRemaining(){
        return ++pointsRemaining;
    }

    public int decrementPointsRemaining(){
        return --pointsRemaining;
    }

    //Setters and no-arg constructor included for seriliazibility


    public QuizQuestion() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPointsPossible(int pointsPossible) {
        this.pointsPossible = pointsPossible;
    }

    public void setAvailableAnswers(ArrayList<QuizAnswer> availableAnswers) {
        this.availableAnswers = availableAnswers;
    }

    public int getPointsRemaining() {
        return pointsRemaining;
    }

    public void setPointsRemaining(int pointsRemaining) {
        this.pointsRemaining = pointsRemaining;
    }

    public static final class QuizQuestionDbContract{

        private QuizQuestionDbContract(){}

        public static class QuizQuestionEntry implements BaseColumns {
            public static final String TABLE_NAME = "questions";
            public static final String COLUMN_NAME_CORRECT_ANSWER_ID = "correct_answer_id";
            public static final String COLUMN_NAME_QUESTION = "question";
            public static final String COLUMN_NAME_AVAILABLE_POINTS = "available_points";
        }
    }
}
