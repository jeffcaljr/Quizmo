package com.example.jeff.viewpagerdelete.Startup.Database;

/**
 * Created by jamy on 3/22/17.
 */

public class UserSchema {
    public static final String DATABASE_NAME = "user.db";
    public static final class Table{
        public static final String NAME = "quiz_user";
    }
    public static final class Cols{
        public static final String USER_ID = "user_id";
        public static final String F_NAME = "first_name";
        public static final String L_NAME = "last_name";
    }

}
