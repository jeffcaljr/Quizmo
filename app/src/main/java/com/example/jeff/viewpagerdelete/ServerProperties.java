package com.example.jeff.viewpagerdelete;

import java.util.ArrayList;

/**
 * Created by Jeff on 3/23/17.
 */

public class ServerProperties {

    public static final String baseURL = "https://immense-brushlands-50268.herokuapp.com/v1/";
    public static final String quizzesURL = "https://immense-brushlands-50268.herokuapp.com/v1/quizes/";
    public static final String quizURL = "https://immense-brushlands-50268.herokuapp.com/v1/quiz/";
    public static final String groupURL = "https://immense-brushlands-50268.herokuapp.com/v1/groups/";
    public static final String userGroupURL = "https://immense-brushlands-50268.herokuapp.com/v1/groupForUser/";


    public static ArrayList<String> getUserFields(){
        ArrayList<String> fields = new ArrayList<>();

        fields.add(UserFields.ID);
        fields.add(UserFields.USER_NAME);
        fields.add(UserFields.EMAIL);
        fields.add(UserFields.FIRST_NAME);
        fields.add(UserFields.LAST_NAME);

        return fields;
    }

    public static ArrayList<String> getGroupFields(){
        ArrayList<String> fields = new ArrayList<>();

        fields.add(GroupFields.ID);
        fields.add(GroupFields.NAME);
        fields.add(GroupFields.USERS);

        return fields;
    }

    public class GroupFields{
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String USERS = "users";
    }

    public class UserFields{
        public static final String ID = "_id";
        public static final String USER_NAME = "userId";
        public static final String EMAIL = "email";
        public static final String FIRST_NAME = "first";
        public static final String LAST_NAME = "last";

    }
}
