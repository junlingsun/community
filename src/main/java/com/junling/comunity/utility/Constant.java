package com.junling.comunity.utility;

public interface Constant {

    //activation status constant
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAIL = 2;


    //login ticket expired time setting
    int DEFAULT_EXPIRED_SECOND = 3600 * 12;
    int REMEMBER_EXPIRED_SECOND = 3600 * 24 * 100;

    //entity type
    int POST_TYPE = 1;
    int COMMENT_TYPE = 2;
    int USER_TYPE = 3;


    //topics
    String MESSAGE_TOPIC = "message";
    String LIKE_TOPIC = "like";
    String FOLLOW_TOPIC = "follow";
    String POST_TOPIC = "post";
    String DELETE_TOPIC = "delete";

    //SYSTEM
    int SYSTEM_ID = 1;

    //Authority
    String AUTHORITY_USER = "user";
    String AUTHORITY_ADMIN = "admin";
    String AUTHORITY_MODERATOR = "moderator";
}
