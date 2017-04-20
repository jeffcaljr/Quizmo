package com.example.jeff.viewpagerdelete.GroupQuiz.Model;

import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizAnswer;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.QuizQuestion;
import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jeff on 4/20/17.
 */

public class GroupQuizAnswer implements Serializable {

  private String questionID;
  private String answerValue;

  public GroupQuizAnswer(String questionID, String answerValue) {
    this.questionID = questionID;
    this.answerValue = answerValue;
  }

  public GroupQuizAnswer() {
  }

  public String getQuestionID() {
    return questionID;
  }

  public void setQuestionID(String questionID) {
    this.questionID = questionID;
  }

  public String getAnswerValue() {
    return answerValue;
  }

  public void setAnswerValue(String answerValue) {
    this.answerValue = answerValue;
  }

  public JSONObject toPostJSONFormat() {
    JSONObject thisAnswer = new JSONObject();
    try {
      thisAnswer.put("question_id", this.questionID);
      thisAnswer.put("answerValue", this.answerValue);

      return thisAnswer;

    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }

}
