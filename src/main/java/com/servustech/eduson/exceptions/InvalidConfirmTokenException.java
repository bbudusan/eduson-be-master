package com.servustech.eduson.exceptions;

public class InvalidConfirmTokenException extends RuntimeException{

    public InvalidConfirmTokenException(String message) {
        super(message);
    }
}
