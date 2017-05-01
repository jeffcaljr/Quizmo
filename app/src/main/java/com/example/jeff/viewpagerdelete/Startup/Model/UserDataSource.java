package com.example.jeff.viewpagerdelete.Startup.Model;

/**
 * Created by Jeff on 4/14/17.
 */

/**
 * Singleton service for easily accessing User properties throughout the application
 * TODO: There are better ways to implement this featuure other than a singleton. Revise later
 */

public class UserDataSource {

    private User user;

    private static UserDataSource mInstance;


    /**
     * Retrieve the instance of the UserDataSource object
     *
     * @return UserDataSource object that holds the current user's information
     */
    public static UserDataSource getInstance(){
        if(mInstance == null){
            mInstance = new UserDataSource();
        }
        return mInstance;
    }

    /**
     * Retrieves current user object
     * @return Current User object
     */
    public User getUser(){
        return user;
    }

    //TODO: This may be considered bad practice! Using singleton as global!

    /**
     * Set's current user object
     * @param user User object
     */
    public void setUser(User user){
        this.user = user;
    }
}
