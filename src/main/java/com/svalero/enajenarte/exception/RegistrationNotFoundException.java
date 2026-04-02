package com.svalero.enajenarte.exception;

public class RegistrationNotFoundException extends Exception{
    public RegistrationNotFoundException() {
        super("La inscripción no existe");
    }
}
