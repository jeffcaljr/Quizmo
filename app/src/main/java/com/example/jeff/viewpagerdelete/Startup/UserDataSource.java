package com.example.jeff.viewpagerdelete.Startup;

import com.example.jeff.viewpagerdelete.Startup.Model.User;

/**
 * Created by Jeff on 4/14/17.
 */

public class UserDataSource {

    private User user;

    private static UserDataSource mInstance;

    private UserDataSource(User user){
        this.user = user;
    }

    public static UserDataSource getInstance(User user){
        if(mInstance == null){
            mInstance = new UserDataSource(user);
        }

        return mInstance;
    }

    public User getUser(){
        return user;
    }
}
