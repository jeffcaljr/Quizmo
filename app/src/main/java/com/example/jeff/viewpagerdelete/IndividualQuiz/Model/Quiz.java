package com.example.jeff.viewpagerdelete.IndividualQuiz.Model;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Jeff on 2/11/17.
 */

public class Quiz implements Serializable {

    private String id;
    private String description;
    private String text;
    private Date availableDate;
    private Date expiryDate;
    private ArrayList<QuizQuestion> questions;


    public Quiz(JSONObject json){


        try {
            String id = json.getString("id");
            String description = json.getString("description");
            String text = json.getString("text");
            String  availableDate = json.getString("availableDate");
            String expiryDate = json.getString("expiryDate");
            JSONArray questionsJSONArray = (JSONArray) json.get("questions");
            ArrayList<QuizQuestion> questions = new ArrayList<>();

            for (int i = 0; i < questionsJSONArray.length(); i++) {
                questions.add(new QuizQuestion(((JSONObject) questionsJSONArray.get(i))));
            }

            Collections.shuffle(questions);

            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss");

            format.setTimeZone(TimeZone.getTimeZone("UTC"));

            this.id = id;
            this.description = description;
            this.text = text;
            this.questions = questions;
             this.availableDate = format.parse(availableDate);
            this.expiryDate = format.parse(expiryDate);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (ParseException e){
            e.printStackTrace();
        }

    }

    public Quiz() {
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public Date getAvailableDate() {
        return availableDate;
    }
    public void setAvailableDate(Date availableDate) {
        this.availableDate = availableDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public ArrayList<QuizQuestion> getQuestions(){
        return questions;
    }
    public void setQuestions(ArrayList<QuizQuestion> questions) {
        this.questions = questions;
    }


    //Convenience Methods

    /**
     * Retrieves a particular QuizQuestion associated with this Quiz from list of QuizQuestions
     * @param index index of QuizQuestion in list
     * @return QuizQuestion from list of QuizQuestions specified by index if possible, null if index is out of bounds
     */
    public QuizQuestion getQuestionByIndex(int index){
        try{
            return questions.get(index);
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
            return null;
        }
    }


    //Helper Functions

    public String toJSON(){
        return new Gson().toJson(this);

    }

    public static Quiz buildQuizFromJsonString(String quizJsonString) throws JsonSyntaxException{
        try{
            return new Gson().fromJson(quizJsonString, new TypeToken<Quiz>() {}.getType());
        } catch(JsonSyntaxException e){
            throw new JsonSyntaxException("Could not parse provided Json string into Quiz object");
        }
    }






}
