package com.svalero.enajenarte.exception;

public class DuplicateProgramRegistrationException extends Exception {

    public DuplicateProgramRegistrationException() {
        super("El usuario ya está inscrito en este programa");
    }
}