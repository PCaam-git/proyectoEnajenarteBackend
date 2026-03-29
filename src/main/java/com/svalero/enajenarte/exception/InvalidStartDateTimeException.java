package com.svalero.enajenarte.exception;

public class InvalidStartDateTimeException extends Exception {

    public InvalidStartDateTimeException() {
        super("startDate must be in the future");
    }

    public InvalidStartDateTimeException(String message) {
        super(message);
    }
}