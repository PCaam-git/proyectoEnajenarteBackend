package com.svalero.enajenarte.exception;

public class DuplicateRegistrationException extends RuntimeException{

    public DuplicateRegistrationException() {
        super ("El usuario ya está inscrito en este workshop");
    }
}
