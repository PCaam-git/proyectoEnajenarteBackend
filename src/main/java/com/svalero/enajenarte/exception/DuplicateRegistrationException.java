package com.svalero.enajenarte.exception;

public class DuplicateRegistrationException extends Exception{

    public DuplicateRegistrationException() {
        super ("El usuario ya está inscrito en este workshop");
    }
}
