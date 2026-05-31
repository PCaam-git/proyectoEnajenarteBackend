package com.svalero.enajenarte.exception;

public class DuplicateEventException extends Exception {

    public DuplicateEventException() {
        super("Ya existe un evento con el mismo nombre en la misma fecha");
    }
}
