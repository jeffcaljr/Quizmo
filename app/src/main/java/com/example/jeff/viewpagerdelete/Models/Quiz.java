package com.example.jeff.viewpagerdelete.Models;

import android.provider.BaseColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jeff on 2/11/17.
 */

public class Quiz implements Serializable {

    private String id;
    private String description;
    private String text;
    private String availableDate;
    private String expiryDate;
    private ArrayList<QuizQuestion> questions;


    public Quiz(JSONObject json){
//        Quiz quiz;

//        if(!json.has("id") || !json.has("description") || !json.has("text")
//                || !json.has("availableDate") || !json.has("expiryDate") || !json.has("questions")){
//            quiz = null;
//        }
//        else{
            try {
                String id = json.getString("id");
                String description = json.getString("description");
                String text = json.getString("text");
                String availableDate = json.getString("availableDate");
                String expiryDate = json.getString("expiryDate");
                JSONArray questionsJSONArray = (JSONArray) json.get("questions");
                ArrayList<QuizQuestion> questions = new ArrayList<>();

                for(int i = 0; i < questionsJSONArray.length(); i++){
                    questions.add(new QuizQuestion(((JSONObject) questionsJSONArray.get(i))));
                }
                this.id = id;
                this.description = description;
                this.text = text;
                this.availableDate = availableDate;
                this.expiryDate = expiryDate;
                this.questions = questions;

            } catch (JSONException e) {
                e.printStackTrace();
            }
//        }

//        return quiz;
    }


    public String getId() {
        return id;
    }

//    public void setId(int id) {
//        this.id = id;
//    }

    public String getDescription() {
        return description;
    }

//    public void setDescription(String description) {
//        this.description = description;
//    }

    public String getText() {
        return text;
    }

//    public void setText(String text) {
//        this.text = text;
//    }

    public String getAvailableDate() {
        return availableDate;
    }

//    public void setAvailableDate(String availableDate) {
//        this.availableDate = availableDate;
//    }

    public String getExpiryDate() {
        return expiryDate;
    }

//    public void setExpiryDate(String expiryDate) {
//        this.expiryDate = expiryDate;
//    }
//
//    public void addQuestion(QuizQuestion newQuestion){
//        questions.add(newQuestion);
//    }
//
    public QuizQuestion getQuestionById(String id){
        for(QuizQuestion q: questions){
            if(q.getId().equals(id)){
                return q;
            }
        }
        return null;
    }

    public QuizQuestion getQuestionByIndex(int index){
        try{
            return questions.get(index);
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
            return null;
        }
    }

    //Setters and no-arg constructor included for seriliazibility


    public Quiz() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public ArrayList<QuizQuestion> getQuestions(){
        return questions;
    }

    public void setQuestions(ArrayList<QuizQuestion> questions) {
        this.questions = questions;
    }

    public static final class QuizDbContract{

        private QuizDbContract(){}

        public static class QuizEntry implements BaseColumns{
            public static final String TABLE_NAME = "quizzes";
            public static final String COLUMN_NAME_QUIZ_NAME = "quiz_name";
            public static final String COLUMN_NAME_DATE_ADDED = "date_added";

        }
    }
}
