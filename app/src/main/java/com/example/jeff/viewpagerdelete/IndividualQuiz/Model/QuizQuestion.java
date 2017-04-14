package com.example.jeff.viewpagerdelete.IndividualQuiz.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jeff on 2/11/17.
 */

public class QuizQuestion implements Serializable{

    private String id;
    private String title;
    private String text;
    private int pointsPossible;
    private int pointsRemaining;
    private ArrayList<QuizAnswer> availableAnswers;


    public QuizQuestion(JSONObject json){

        try {
            String id = json.getString("_id");
            String title = json.getString("title");
            String text = json.getString("text");
            int pointsPossible = json.getInt("pointsPossible");
            JSONArray answersJSONArray = json.getJSONArray("availableAnswers");
            ArrayList<QuizAnswer> availableAnswers = new ArrayList<>();
            for (int i = 0; i < answersJSONArray.length(); i++) {
                availableAnswers.add(new QuizAnswer(((JSONObject) answersJSONArray.get(i))));
            }

            //randomly shuffle answers so they're not in the same order in all quizzes
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

    public QuizQuestion() {
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public int getPointsPossible() {
        return pointsPossible;
    }
    public void setPointsPossible(int pointsPossible) {
        this.pointsPossible = pointsPossible;
    }

    public ArrayList<QuizAnswer> getAvailableAnswers() {
        return availableAnswers;
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

    //Convenience Methods

    /**
     * Increments the points number of unallocated confidence points available for this question by 1
     * @return
     */
    public int incrementPointsRemaining(){
        return ++pointsRemaining;
    }

    /**
     * Decrements the points number of unallocated confidence points available for this question by 1
     * @return
     */
    public int decrementPointsRemaining(){
        return --pointsRemaining;
    }

    /**
     * Retrieves a particular QuizAnswer associated with this QuizQuestion from list of QuizAnswers
     * @param index index of QuizAnswer in list
     * @return QuizAnswer from list of QuizAnswers specified by index if possible, null if index is out of bounds
     */
    public QuizAnswer getAnswerByIndex(int index){
        try{
            return availableAnswers.get(index);
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a particular QuizAnswer associated with this QuizQuestion from list of QuizAnswers
     * @param value value of 'value' property of QuizAnswer you wish to retrieve
     * @return first QuizAnswer from list of QuizAnswers with 'value' property matching the id argument, null if no QuizAnswer with provided 'value' found
     */
    public QuizAnswer getAnswerByValue(String value){
        for(QuizAnswer a: availableAnswers){
            if(a.getValue().equals(value)){
                return a;
            }
        }
        return null;
    }



}
