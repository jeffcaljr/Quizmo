package com.example.jeff.viewpagerdelete.Homepage.Model;

import android.support.annotation.NonNull;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Jeff on 4/11/17.
 */

public class Course implements Serializable, Comparable<Course> {

    private String id;
    private String courseID;
    private String extendedID;
    private String name;
    private String semester;
    private String instructor;
    private Quiz quiz;

    public Course(JSONObject json){
        try{
            if(json.has("_id")){
                this.id = json.getString("_id");
            }
            this.courseID = json.getString("courseId");
            this.extendedID = json.getString("extendedID");
            this.name = json.getString("name");
            this.semester = json.getString("semester");
            this.instructor = json.getString("instructor");

            if(json.has("quiz")){
                this.quiz = new Quiz(json.getJSONObject("quiz"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

  public Course(String id, String courseID, String extendedID, String name, String semester,
      String instructor, Quiz quiz) {
    this.id = id;
    this.courseID = courseID;
    this.extendedID = extendedID;
    this.name = name;
    this.semester = semester;
    this.instructor = instructor;
    this.quiz = quiz;
  }

    public Course() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getExtendedID() {
        return extendedID;
    }

    public void setExtendedID(String extendedID) {
        this.extendedID = extendedID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

  @Override
  public int compareTo(@NonNull Course course) {
    return (this.getName().compareTo(course.getName()));
    }
}
