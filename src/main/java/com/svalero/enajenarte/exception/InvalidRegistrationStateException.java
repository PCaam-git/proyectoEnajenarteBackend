package com.svalero.enajenarte.exception;

public class InvalidRegistrationStateException extends Exception{
    public InvalidRegistrationStateException() {
        super("El estado de la inscripción no es válido");
    }
}