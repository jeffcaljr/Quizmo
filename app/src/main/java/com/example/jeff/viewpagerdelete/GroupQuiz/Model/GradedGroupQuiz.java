package com.example.jeff.viewpagerdelete.GroupQuiz.Model;

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

public class GradedGroupQuiz implements Serializable {

  private String id;
  private ArrayList<GradedGroupQuizQuestion> gradedQuestions;
  private int totalQuestions;
  private int questionsAnswered;


  public GradedGroupQuiz(JSONObject json) {
    try {
      this.totalQuestions = json.getInt("totalQuestions");
      this.questionsAnswered = json.getInt("questionsAnswered");

      this.gradedQuestions = new ArrayList<>();

      JSONArray answeredQuestionsJSON = json.getJSONArray("givenAnswers");

      for (int i = 0; i < answeredQuestionsJSON.length(); i++) {
        this.gradedQuestions
            .add(new GradedGroupQuizQuestion(answeredQuestionsJSON.getJSONObject(i)));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public GradedGroupQuiz() {
    gradedQuestions = new ArrayList<>();
  }

  public GradedGroupQuiz(String id, ArrayList<GradedGroupQuizQuestion> gradedQuestions) {
    this.id = id;
    this.gradedQuestions = gradedQuestions;

  }

  public void updateQuestion(GradedGroupQuizQuestion gradedQuestion) {

    boolean isQuestionInQuiz = false;

    for (int i = 0; i < this.gradedQuestions.size(); i++) {
      if (this.gradedQuestions.get(i).getId().equals(gradedQuestion.getId())) {
        isQuestionInQuiz = true;
        //the question is already in this quiz's list of questions; so update it
        this.gradedQuestions.set(i, gradedQuestion);
        break;
      }
    }

    //if the question was not found in the quiz, add it to the quiz's list of questions
    if (!isQuestionInQuiz) {
      this.gradedQuestions.add(gradedQuestion);
    }

    //keep the quiz questions sorted by id?
    Collections.sort(this.gradedQuestions);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ArrayList<GradedGroupQuizQuestion> getGradedQuestions() {
    return gradedQuestions;
  }

  public void setGradedQuestions(ArrayList<GradedGroupQuizQuestion> gradedQuestions) {
    this.gradedQuestions = gradedQuestions;
  }

  public int getTotalQuestions() {
    return totalQuestions;
  }

  public void setTotalQuestions(int totalQuestions) {
    this.totalQuestions = totalQuestions;
  }

  public int getQuestionsAnswered() {
    return questionsAnswered;
  }

  public void setQuestionsAnswered(int questionsAnswered) {
    this.questionsAnswered = questionsAnswered;
  }

    public int getTotalPointsScored() {
        int total = 0;

        for (GradedGroupQuizQuestion question : this.gradedQuestions) {
            for (GradedGroupQuizAnswer answer : question.getGradedAnswers()) {
                if (answer.isCorrect()) {
                    total += answer.getPoints();
                }
            }
        }

        return total;
    }

  public String toJSON() {
    return new Gson().toJson(this);

  }

  public static GradedGroupQuiz buildQuizFromJsonString(String quizJsonString)
      throws JsonSyntaxException {
    try {
      return new Gson().fromJson(quizJsonString, new TypeToken<GradedGroupQuiz>() {
      }.getType());
    } catch (JsonSyntaxException e) {
      throw new JsonSyntaxException(
          "Could not parse provided Json string into GradedGroupQuiz object");
    }
  }
}
