package com.example.jeff.viewpagerdelete.Startup.Database;

import android.provider.BaseColumns;

/**
 * Created by jamy on 3/22/17.
 */

/**
 * Holds the SQLite information for storing user
 * Used as a mock authentication system
 * Logged-in users have their userID saved locally, and skip login as long as it is saved
 * Upon logout, the userID is removed from local storage, causing the user to have to log in again
 */

public class UserSchema {
    public static final String DATABASE_NAME = "user.db";
    public static final class Table{
        public static final String NAME = "quiz_user";
    }
    public static final class Cols implements BaseColumns{
        public static final String USER_ID = "userID";

    }

}
