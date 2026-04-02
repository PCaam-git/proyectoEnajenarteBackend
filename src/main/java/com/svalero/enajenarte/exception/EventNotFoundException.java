package com.svalero.enajenarte.exception;

public class EventNotFoundException extends Exception {
    public EventNotFoundException() {
        super("El evento no existe");
    }
}
