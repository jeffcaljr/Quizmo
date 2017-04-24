package com.example.jeff.viewpagerdelete.GroupQuiz.Model;

import android.support.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jeff on 4/23/17.
 */

//  {"question":"58cf4424507783a09e799a2c","submittedAnswers":[{"value":"A","points":4,"isCorrect":false}]}

public class GradedGroupQuizAnswer implements Comparable<GradedGroupQuizAnswer> {

  private String value;
  private int points;
  private boolean isCorrect;

  public GradedGroupQuizAnswer(JSONObject json) {
    try {
      this.value = json.getString("value");
      this.points = json.getInt("points");
      this.isCorrect = json.getBoolean("isCorrect");
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public GradedGroupQuizAnswer() {
  }

  public GradedGroupQuizAnswer(String value, int points, boolean isCorrect) {
    this.value = value;
    this.points = points;
    this.isCorrect = isCorrect;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  public boolean isCorrect() {
    return isCorrect;
  }

  public void setCorrect(boolean correct) {
    isCorrect = correct;
  }

  @Override
  public int compareTo(@NonNull GradedGroupQuizAnswer gradedGroupQuizAnswer) {
    return (this.value.compareTo(gradedGroupQuizAnswer.getValue()));
  }
}
