package com.example.jeff.viewpagerdelete.IndividualQuiz.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Jeff on 4/19/17.
 */

public class GradedQuizAnswer implements Serializable {

    private String value;
    private int pointsEarned;
    private boolean isCorrect;


    public GradedQuizAnswer(JSONObject json) {

        try {
            this.value = json.getString("value");
            this.pointsEarned = json.getInt("points");
            this.isCorrect = json.getBoolean("isCorrect");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public GradedQuizAnswer() {
    }

    public GradedQuizAnswer(String value, int pointsEarned, boolean isCorrect) {
        this.value = value;
        this.pointsEarned = pointsEarned;
        this.isCorrect = isCorrect;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
