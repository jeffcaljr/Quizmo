package com.example.jeff.viewpagerdelete.GroupQuiz.Model;

import android.support.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jeff on 4/23/17.
 */


public class GradedGroupQuizQuestion implements Serializable, Comparable<GradedGroupQuizQuestion> {

  private String id;
  private ArrayList<GradedGroupQuizAnswer> gradedAnswers;
  private int numberOfAttempts;
  private boolean isAnsweredCorrectly;

  public GradedGroupQuizQuestion(JSONObject json) {

    try {
      this.id = json.getString("question");
      this.gradedAnswers = new ArrayList<>();

      JSONArray submittedAnswersJSON = json.getJSONArray("submittedAnswers");

      for (int i = 0; i < submittedAnswersJSON.length(); i++) {
        GradedGroupQuizAnswer newAnswer = new GradedGroupQuizAnswer(
            submittedAnswersJSON.getJSONObject(i));
        this.gradedAnswers.add(newAnswer);
      }
      Collections.sort(this.gradedAnswers);

      if (json.has("numberOfAttempts")) {
        this.numberOfAttempts = json.getInt("numberOfAttempts");
        this.isAnsweredCorrectly = json.getBoolean("answeredCorrectly");
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public GradedGroupQuizQuestion() {
  }

  public GradedGroupQuizQuestion(String id, ArrayList<GradedGroupQuizAnswer> gradedAnswers) {
    this.id = id;
    this.gradedAnswers = gradedAnswers;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ArrayList<GradedGroupQuizAnswer> getGradedAnswers() {
    return gradedAnswers;
  }

  public void setGradedAnswers(ArrayList<GradedGroupQuizAnswer> gradedAnswers) {
    this.gradedAnswers = gradedAnswers;
  }

  public int getNumberOfAttempts() {
    return numberOfAttempts;
  }

  public void setNumberOfAttempts(int numberOfAttempts) {
    this.numberOfAttempts = numberOfAttempts;
  }

  public boolean isAnsweredCorrectly() {
    return isAnsweredCorrectly;
  }

  public void setAnsweredCorrectly(boolean answeredCorrectly) {
    isAnsweredCorrectly = answeredCorrectly;
  }


  @Override
  public int compareTo(@NonNull GradedGroupQuizQuestion gradedGroupQuizQuestion) {
    return (this.getId().compareTo(gradedGroupQuizQuestion.getId()));
  }
}
