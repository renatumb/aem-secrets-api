package com.renatobonfim.aemblogbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    public static final String SUBSCRIBER_EMAIL_MISSING = "020. Subscriber 'EMAIL'  is missing";
    public static final String SUBSCRIBER_NAME_MISSING = "022. Subscriber 'NAME'  is missing ";
    public static final String SUBSCRIBER_EMAIL_INVALID = "024. Subscriber 'EMAIL' invalid";
    public static final String SUBSCRIBER_NAME_INVALID = "026. Subscriber 'NAME' invalid";
    public static final String SUBSCRIBER_EMAIL_ALREADY_EXIST = "028. Subscriber 'EMAIL' already being used";
    public static final String USER_NOT_FOUND_ID = "030. User not found for given id:%s";
    public static final String USER_NAME_IS_EMPTY = "032. User NAME is empty";
    public static final String USER_EMAIL_IS_EMPTY = "034. User EMAIL is empty";
    public static final String USER_PASSWORD_IS_EMPTY = "036. User PASSWORD is empty";
    public static final String USER_ABOUT_IS_EMPTY = "038. User ABOUT is empty";


    public static String PROFILE_PICTURE_STORAGE;

    @Value("${AppConfig.profilePictureStorage}")
    public void setProfilePictureDirectory(String value){
        Constants.PROFILE_PICTURE_STORAGE = value;
    }

    public static String CURRENT_HOST;
    @Value("${AppConfig.currentHost}")
    public void setCurrentHost(String value){
        Constants.CURRENT_HOST = value;
    }
}
