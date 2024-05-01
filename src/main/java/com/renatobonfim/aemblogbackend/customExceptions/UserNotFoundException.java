package com.renatobonfim.aemblogbackend.customExceptions;

import com.renatobonfim.aemblogbackend.config.Constants;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String userId){
             super( String.format(Constants.USER_NOT_FOUND_ID, userId));
    }
}
