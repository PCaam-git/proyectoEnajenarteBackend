package com.svalero.enajenarte.exception;

public class UserNotFoundException extends Exception{
    public UserNotFoundException() {
        super("The user does not exist");
    }
}
