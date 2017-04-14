package com.example.jeff.viewpagerdelete.IndividualQuiz.Model;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Jeff on 2/11/17.
 */

public class QuizAnswer implements Serializable {

    private String id;
    private String value;
    private String text;
    private int sortOrder;
    private int pointsAllocated;

    /**
     * Constructs QuizAnswer object from json
     * @param json
     */
    public QuizAnswer(JSONObject json){
        try {

            if(json.has("id")){
                String id = json.getString("id");
                this.id = id;
            }
            else if(json.has("_id")){
                String id = json.getString("_id");
                this.id = id;
            }

            String value = json.getString("value");
            String text = json.getString("text");
            int sortOrder = json.getInt("sortOrder");


            this.value = value;
            this.text = text;
            this.sortOrder = sortOrder;
            this.pointsAllocated = 0;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public QuizAnswer() {
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public int getSortOrder() {
        return sortOrder;
    }
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getPointsAllocated() {
        return pointsAllocated;
    }
    public void setPointsAllocated(int pointsAllocated) {
        this.pointsAllocated = pointsAllocated;
    }

    //Convenience Methods

    /**
     * Increments the instance's pointsAllocated by 1 and returns the new value
     * @return
     */
    public int incrementPointsAllocated(){
        ++pointsAllocated;
        return pointsAllocated;
    }

    /**
     * Decrements the instance's pointsAllocated by 1 and returns the new value
     * @return
     */
    public int decrementPointsAllocated(){
        --pointsAllocated;
        return pointsAllocated;
    }



}
