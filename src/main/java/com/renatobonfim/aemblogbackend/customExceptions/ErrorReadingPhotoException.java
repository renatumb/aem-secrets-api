package com.renatobonfim.aemblogbackend.customExceptions;

import java.io.IOException;

public class ErrorReadingPhotoException extends RuntimeException {
    public ErrorReadingPhotoException(String errorReadingProfilePhoto) {
        super(errorReadingProfilePhoto);
    }
}
