package com.svalero.enajenarte.exception;

public class InvalidEventDateException extends Exception {
    public InvalidEventDateException() {
        super("La fecha del evento no puede ser anterior a la fecha actual");
    }
}
