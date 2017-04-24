package com.example.jeff.viewpagerdelete.IndividualQuiz.Model;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jeff on 4/19/17.
 */

public class GradedQuiz implements Serializable {

    public enum SubmitType {
        INDIVIDUAL("individual");

        private final String submitType;

        SubmitType(String submitType) {
            this.submitType = submitType;
        }

        public String getSubmitType() {
            return this.submitType;
        }

        public static SubmitType fromString(String text) {
            for (SubmitType t : SubmitType.values()) {
                if (t.submitType.equalsIgnoreCase(text)) {
                    return t;
                }
            }
            return null;
        }
    }

    private String id;
    private SubmitType submitType;
    private String sessionID;
    private String userID;
    private String courseID;
    private String quizID;
    private ArrayList<GradedQuizQuestion> questions;

    public GradedQuiz(JSONObject json) {

        try {
            this.id = json.getString("_id");
            this.submitType = SubmitType.fromString(json.getString("submitType"));
            this.sessionID = json.getString("sessionId");
            this.userID = json.getString("user");
            this.courseID = json.getString("course");
            this.quizID = json.getString("quizId");

            this.questions = new ArrayList<>();

            JSONArray questionsJSON = json.getJSONArray("answers");

            for (int i = 0; i < questionsJSON.length(); i++) {
                this.questions.add(new GradedQuizQuestion(questionsJSON.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public GradedQuiz() {
    }

    public GradedQuiz(String id, SubmitType submitType, String sessionID, String userID, String courseID, String quizID, ArrayList<GradedQuizQuestion> questions) {
        this.id = id;
        this.submitType = submitType;
        this.sessionID = sessionID;
        this.userID = userID;
        this.courseID = courseID;
        this.quizID = quizID;
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SubmitType getSubmitType() {
        return submitType;
    }

    public void setSubmitType(SubmitType submitType) {
        this.submitType = submitType;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getQuizID() {
        return quizID;
    }

    public void setQuizID(String quizID) {
        this.quizID = quizID;
    }

    public ArrayList<GradedQuizQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<GradedQuizQuestion> questions) {
        this.questions = questions;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }

    public static GradedQuiz buildQuizFromJsonString(String quizJsonString)
        throws JsonSyntaxException {
        try {
            return new Gson().fromJson(quizJsonString, new TypeToken<GradedQuiz>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException("Could not parse provided Json string into Quiz object");
        }
    }
}
