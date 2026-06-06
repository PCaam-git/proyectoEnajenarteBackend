package com.svalero.enajenarte.exception;

public class UserNotFoundException extends Exception{
    public UserNotFoundException() {
        super("El usuario no existe");
    }
}
