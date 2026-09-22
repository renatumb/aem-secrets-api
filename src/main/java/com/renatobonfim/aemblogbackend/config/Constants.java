package com.renatobonfim.aemblogbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {


    public static final String SUBSCRIBER_EMAIL_ALREADY_EXIST = "028. Subscriber 'EMAIL' already being used";
    public static final String USER_NOT_FOUND_ID = "030. User not found for given id: %s";
    public static final String USER_NAME_IS_EMPTY = "032. User NAME is empty";
    public static final String USER_EMAIL_IS_EMPTY = "034. User EMAIL is empty";
    public static final String USER_PASSWORD_IS_EMPTY = "036. User PASSWORD is empty";
    public static final String USER_ABOUT_IS_EMPTY = "038. User ABOUT is empty";
    public static final String ERROR_READING_PROFILE_PHOTO = "040. Error while reading file: ";
    public static final String POST_NOT_FOUND_ID = "042. Post not found for given id: %s";
    public static final String POST_NOT_FOUND = "044. Post not found for given id/permalink: %s";
    public static final String COMMENT_NOT_FOUND = "046. Comment not found for given id: %s";
    public static final String INVALID_FIELD = "048. Invalid field";
    public static final String SUBSCRIBER_UNSUBSCRIBE_TOKEN_NOT_FOUND = "050. Unsubscribe token not found";
    public static final String RECAPTCHA_TOKEN_MISSING = "052. Missing X-Recaptcha-Token header";
    public static final String RECAPTCHA_VERIFICATION_FAILED = "054. reCAPTCHA verification failed";

    public static String USER_PROFILE_PICTURE_PATH;

    @Value("${app.config.user-profile-picture}")
    public void setProfilePictureDirectory(String value){
        Constants.USER_PROFILE_PICTURE_PATH = value;
    }

    /** */
    public static String IMAGES_ROOT_PATH;

    @Value("${app.config.images-post-root-path}")
    public void setImagesUploadRootPath(String value) {
        Constants.IMAGES_ROOT_PATH = value;
    }
}
