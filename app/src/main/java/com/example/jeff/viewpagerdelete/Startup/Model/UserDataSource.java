package com.example.jeff.viewpagerdelete.Startup.Model;

/**
 * Created by Jeff on 4/14/17.
 */

public class UserDataSource {

    private User user;

    private static UserDataSource mInstance;


    public static UserDataSource getInstance(){
        if(mInstance == null){
            mInstance = new UserDataSource();
        }

        return mInstance;
    }

    public User getUser(){
        return user;
    }

    //TODO: This may be considered bad practice! Using singleton as global
    public void setUser(User user){
        this.user = user;
    }
}
