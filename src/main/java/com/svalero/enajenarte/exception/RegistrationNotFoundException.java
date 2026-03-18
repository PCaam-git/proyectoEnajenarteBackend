package com.svalero.enajenarte.exception;

public class RegistrationNotFoundException extends Exception{
    public RegistrationNotFoundException() {
        super("The registration does not exist");
    }
}
