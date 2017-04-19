package com.example.jeff.viewpagerdelete.IndividualQuiz.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jeff on 4/19/17.
 */

public class GradedQuizQuestion implements Serializable {

    private String question;
    private ArrayList<GradedQuizAnswer> submittedAnswers;

    public GradedQuizQuestion(JSONObject json) {

        try {
            question = json.getString("question");

            this.submittedAnswers = new ArrayList<>();

            JSONArray answers = json.getJSONArray("submittedAnswers");

            for (int i = 0; i < answers.length(); i++) {
                this.submittedAnswers.add(new GradedQuizAnswer(answers.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public GradedQuizQuestion() {
    }

    public GradedQuizQuestion(String question, ArrayList<GradedQuizAnswer> submittedAnswers) {
        this.question = question;
        this.submittedAnswers = submittedAnswers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<GradedQuizAnswer> getSubmittedAnswers() {
        return submittedAnswers;
    }

    public void setSubmittedAnswers(ArrayList<GradedQuizAnswer> submittedAnswers) {
        this.submittedAnswers = submittedAnswers;
    }
}
