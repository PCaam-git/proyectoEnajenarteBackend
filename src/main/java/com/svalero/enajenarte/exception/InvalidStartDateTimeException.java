package com.svalero.enajenarte.exception;

public class InvalidStartDateTimeException extends Exception {

    public InvalidStartDateTimeException() {
        super("La fecha de inicio debe ser futura");
    }

    public InvalidStartDateTimeException(String message) {
        super(message);
    }
}