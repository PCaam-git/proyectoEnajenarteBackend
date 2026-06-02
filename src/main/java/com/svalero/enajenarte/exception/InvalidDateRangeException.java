package com.svalero.enajenarte.exception;

public class InvalidDateRangeException extends Exception {

    public InvalidDateRangeException() {
        super("La fecha de confirmación debe ser anterior a la fecha de inicio");
    }

    public InvalidDateRangeException(String message) {
        super(message);
    }
}